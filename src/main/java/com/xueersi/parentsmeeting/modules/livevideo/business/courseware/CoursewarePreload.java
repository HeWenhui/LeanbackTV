package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.content.Context;
import android.text.TextUtils;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.xutils.common.util.MD5;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;


/**
 * Created by: WangDe on 2019/2/27
 */
public class CoursewarePreload {

    String TAG = getClass().getSimpleName();
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context mContext;
    //    private String liveId;
    private int mSubject = -1;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser liveHttpResponseParser;
    File cacheFile;
    private File todayCacheDir;
    public static String mPublicCacheoutName = "publicRes";
    public static String FZY3JW_TTF = "FZY3JW.ttf";
//    public static int mDownloadThreadCount = 1;

    /** 所有需要下载文件的总量 */
    private AtomicInteger documentNum = new AtomicInteger(0);
    /** 下载的科目总数 */
    private AtomicInteger subjectNum = new AtomicInteger(0);

    public CoursewarePreload(Context context, int subject) {
        mContext = context;
//        this.liveId = liveId;
        mSubject = subject;
        liveHttpResponseParser = new LiveHttpResponseParser(context);
        cacheFile = LiveCacheFile.geCacheFile(mContext, "webviewCache");
        logger.d("cache path :" + cacheFile.getAbsolutePath());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    public void setmHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    List<CoursewareInfoEntity> courseWareInfos = new CopyOnWriteArrayList<>();

    AtomicBoolean isPrecise = new AtomicBoolean(false);

    AtomicInteger ipPos, cdnPos, ipLength, cdnLength;

    /**
     * 删除旧的Dir
     */
    private void deleteOldDir(final File file, final String today) {
//        LiveThreadPoolExecutor executor = LiveThreadPoolExecutor.getInstance();
        executos.execute(new Runnable() {
            @Override
            public void run() {
                logger.i("开始删除文件");
                for (File itemFile : file.listFiles()) {
                    if (isCoursewareDir(itemFile.getName()) && !itemFile.getName().equals(today)) {
                        if (!itemFile.isDirectory()) {
                            itemFile.delete();
                        } else {
                            deleteFor(itemFile);
                            itemFile.delete();
                        }
                    }
                }
                logger.i("文件删除成功");
                StableLogHashMap hashMap = new StableLogHashMap();
                hashMap.put("logtype", " deleteCourseware");
                hashMap.put("dir", file.getAbsolutePath());
                hashMap.put("sno", "5");
                hashMap.put("status", "true");
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());

            }
        });
    }

    private void deleteFor(final File file) {
        for (File itemFile : file.listFiles()) {
            if (!itemFile.isDirectory()) {
                itemFile.delete();
            } else {
                deleteFor(itemFile);
                itemFile.delete();
            }
        }
    }

    /**
     * 是否是课件的文件夹
     *
     * @return
     */
    private boolean isCoursewareDir(String fileName) {
        try {
            Integer.parseInt(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 获取课件信息
     */
    public void getCoursewareInfo(String liveId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        todayCacheDir = new File(cacheFile, today);

        deleteOldDir(cacheFile, today);

        //根据传liveid来判断 不为空或者不是""则为直播进入下载资源，否则为学习中心进入下载资源
        ipPos = new AtomicInteger(0);
        ipLength = new AtomicInteger();
        cdnLength = new AtomicInteger();
        cdnPos = new AtomicInteger(0);
        if (!TextUtils.isEmpty(liveId)) {
            isPrecise.set(true);
            if (0 == mSubject) {//理科
                logger.i("下载理科");
                subjectNum.getAndIncrement();
                mHttpManager.getScienceCourewareInfo(liveId, new CoursewareHttpCallBack(false, "science"));
            } else if (1 == mSubject) {//英语
                logger.i("下载英语");
                subjectNum.getAndIncrement();
                mHttpManager.getEnglishCourewareInfo(liveId, new CoursewareHttpCallBack(false, "english"));
            } else if (2 == mSubject) {//语文
                logger.i("下载语文");
                subjectNum.getAndIncrement();
                mHttpManager.getArtsCourewareInfo(liveId, new CoursewareHttpCallBack(false, "chs"));
            }
        } else {//下载当天所有课件资源
            logger.i("下载当天所有课件资源");
            subjectNum.getAndIncrement();
            mHttpManager.getScienceCourewareInfo("", new CoursewareHttpCallBack(false, "science"));
            subjectNum.getAndIncrement();
            mHttpManager.getEnglishCourewareInfo("", new CoursewareHttpCallBack(false, "english"));
            subjectNum.getAndIncrement();
            mHttpManager.getArtsCourewareInfo("", new CoursewareHttpCallBack(false, "chs"));
        }
    }


    public class CoursewareHttpCallBack extends HttpCallBack {

        private String arts;

        public CoursewareHttpCallBack(boolean isShow, String arts) {
            super(isShow);
            this.arts = arts;
        }

        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            CoursewareInfoEntity coursewareInfoEntity = liveHttpResponseParser.parseCoursewareInfo(responseEntity);
            logger.i(responseEntity.getJsonObject().toString());
            courseWareInfos.add(coursewareInfoEntity);
            logger.i("接收到了数据");
            performDownLoad();
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
            super.onPmFailure(error, msg);
            subjectNum.getAndDecrement();
            logger.i("paFailure" + arts);
            performDownLoad();
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
            super.onPmError(responseEntity);
            subjectNum.getAndDecrement();
            performDownLoad();
            if (responseEntity != null) {
                logger.i("onPmError:" + arts + " " + responseEntity.getJsonObject() + "  " + responseEntity.getErrorMsg());
//                if (responseEntity.getJsonObject() != null) {
//                    logger.i("onPmError:" + responseEntity.getJsonObject().toString());
//                }
            }
        }
    }

    private void performDownLoad() {
        logger.i("" + courseWareInfos.size() + " " + subjectNum.get());
        if (courseWareInfos.size() == subjectNum.get()) {
            logger.i("数据返回成功");
            AppBll.getInstance().registerAppEvent(CoursewarePreload.this);
//            storageLiveId();
            execDownLoad(
                    sortArrays(),
                    mergeList(courseWareInfos, 1),
                    mergeList(courseWareInfos, 2),
                    mergeList(courseWareInfos, 3));
        }
    }

    private List<String> mergeList(List<CoursewareInfoEntity> coursewareInfoEntities, int type) {
        List<String> ansList = new LinkedList<>();
//        HashSet<String> ansSet = new HashSet<>();
        for (CoursewareInfoEntity coursewareInfoEntity : coursewareInfoEntities) {
            if (type == 1) {
                ansList = appendList(ansList, coursewareInfoEntity.getCdns());
            } else if (type == 2) {
                ansList = appendList(ansList, coursewareInfoEntity.getIps());
            } else if (type == 3) {
                ansList = appendList(ansList, coursewareInfoEntity.getResources());
            }
        }
        return ansList;
    }

    /**
     * 对数组进行排序
     *
     * @return
     */
    private List<CoursewareInfoEntity.LiveCourseware> sortArrays() {
        List<CoursewareInfoEntity.LiveCourseware> liveCoursewares = new LinkedList<>();

        for (CoursewareInfoEntity coursewareInfoEntity : courseWareInfos) {
            liveCoursewares.addAll(coursewareInfoEntity.getCoursewaresList());
        }
        Collections.sort(liveCoursewares, new Comparator<CoursewareInfoEntity.LiveCourseware>() {
            @Override
            public int compare(CoursewareInfoEntity.LiveCourseware liveCourseware, CoursewareInfoEntity.LiveCourseware t1) {
                if (liveCourseware.getStime() < t1.getStime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return liveCoursewares;
    }

    private List<CoursewareInfoEntity.ItemCoursewareInfo> mergeList(List<CoursewareInfoEntity.LiveCourseware> coursewares) {
        List<CoursewareInfoEntity.ItemCoursewareInfo> itemCourseware = new LinkedList<>();
        for (CoursewareInfoEntity.LiveCourseware liveCourseware : coursewares) {
            itemCourseware.addAll(liveCourseware.getCoursewareInfos());
        }
        return itemCourseware;
    }

    private List<String> appendList(List<String> totalList, List<String> list) {
        for (String item : list) {
            if (!totalList.contains(item)) {
                totalList.add(item);
            }
        }
        return totalList;
    }

    private void execDownLoad(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, List<String> cdns, List<String> ips, List<String> resources) {
        //直播资源列表
        //cdns列表
        if (cdns == null || cdns.size() == 0) {
            return;
        }
        List<String> newIPs = new LinkedList<>();
        newIPs.addAll(cdns);
        newIPs.addAll(ips);

        logger.i("" + cdns.size() + " " + newIPs.size());
        cdnLength.set(cdns.size());
        ipLength.set(newIPs.size());

        downloadResources(resources, cdns, newIPs);
        exeDownLoadCourseware(liveCoursewares, cdns, newIPs);

    }

    /**
     * //下载同一场直播的课件资源
     *
     * @param liveCoursewares
     * @param cdns
     * @param ips
     */
    private void exeDownLoadCourseware(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, List<String> cdns, final List<String> ips) {

        StringBuilder liveIds = new StringBuilder("");
        for (CoursewareInfoEntity.LiveCourseware liveCourseware : liveCoursewares) {
            //课件列表
            liveIds.append(liveCourseware.getLiveId() + ",");

            PreloadStaticStorage.preloadLiveId.add(liveCourseware.getLiveId());

            List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos = liveCourseware.getCoursewareInfos();
            File todayLiveCacheDir = new File(todayCacheDir, liveCourseware.getLiveId());
            boolean exists = todayLiveCacheDir.exists();
            boolean mkdirs = false;
            if (!todayLiveCacheDir.exists()) {
                mkdirs = todayLiveCacheDir.mkdirs();
            }
            logger.d("getCourseWareUrl:exists=" + exists + ",mkdirs=" + mkdirs);
            String itemLiveId = liveCourseware.getLiveId();
            downloadCourseware(todayLiveCacheDir, coursewareInfos, ips, cdns, itemLiveId);
        }
        ShareDataManager shareDataManager = ShareDataManager.getInstance();

        shareDataManager.put(ShareDataConfig.SP_PRELOAD_COURSEWARE, liveIds.toString(), ShareDataManager.SHAREDATA_USER);

    }

    /**
     * 下载每一个单独的课件资源
     *
     * @param path
     * @param coursewareInfos
     * @param ips
     * @param cdns
     */
    private void downloadCourseware(File path, List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos, final List<String> ips, List<String> cdns, String itemLiveId) {

        final File mMorecachein = new File(path, itemLiveId);
        if (!mMorecachein.exists()) {
            mMorecachein.mkdirs();
        }
        final File mMorecacheout = new File(path, itemLiveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        String tempIP = ips.get(0);
        String ip;
        boolean isIP = false;
        //拼接ip
        if (tempIP.contains("http") || tempIP.contains("https")) {
            isIP = false;
            ip = tempIP;
        } else {
            isIP = true;
            ip = "http://" + tempIP;
        }
        //截取host
        int index = cdns.get(0).indexOf("/") + 2;
        String cdn = cdns.get(0).substring(index);
        for (CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo : coursewareInfos) {
            //下载课件资源
            final String resourceName = MD5.md5(coursewareInfo.getResourceUrl()) + ".zip";
            File resourceSave = new File(mMorecachein, resourceName);
            boolean equals = false;
            if (fileIsExists(resourceSave.getAbsolutePath())) {
                String filemd5 = FileUtils.getFileMD5ToString(resourceSave);
                equals = coursewareInfo.getResourceMd5().equalsIgnoreCase(filemd5);
            }
            if (!fileIsExists(resourceSave.getAbsolutePath()) || (fileIsExists(resourceSave.getAbsolutePath()) && !equals)) {
                DownLoadInfo resourceDownLoadInfo = DownLoadInfo.createFileInfo(
                        ip + coursewareInfo.getResourceUrl(),
                        mMorecachein.getAbsolutePath(),
                        resourceName + ".temp",
                        coursewareInfo.getResourceMd5());

                if (isIP) {
                    resourceDownLoadInfo.setHost(cdn);
                }
//                DownLoader resourceDownLoader = new DownLoader(mContext, resourceDownLoadInfo);
//                resourceDownLoader.setDownloadThreadCount(mDownloadThreadCount);
                logger.d("courseware url path:  " + ip + coursewareInfo.getResourceUrl() + "   file name:" + resourceName + ".zip");
//                resourceDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(resourceDownLoadInfo,
                        new ZipDownloadListener(
                                mMorecachein,
                                mMorecacheout,
                                resourceName,
                                ips,
                                cdns,
                                coursewareInfo.getResourceUrl(),
                                coursewareInfo.getResourceMd5(),
                                new AtomicInteger(0),
                                itemLiveId,
                                "1"));

                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                }
                documentNum.getAndIncrement();
            }
            //下载模板资源
            final String templateName = MD5.md5(coursewareInfo.getTemplateUrl()) + ".zip";
            File templateSave = new File(mMorecachein, resourceName);
            if (!fileIsExists(templateSave.getAbsolutePath())) {
//                templateSave.mkdirs();
                DownLoadInfo templateDownLoadInfo = DownLoadInfo.createFileInfo(
                        ip + coursewareInfo.getTemplateUrl(),
                        mMorecachein.getAbsolutePath(),
                        templateName + ".temp",
                        coursewareInfo.getTemplateMd5());
                logger.d("template url path:  " + ip + coursewareInfo.getTemplateUrl() + "   file name:" + templateName + ".zip");
                if (isIP) {
                    templateDownLoadInfo.setHost(cdn);
                }
//                DownLoader templateDownLoader = new DownLoader(mContext, templateDownLoadInfo);
//                templateDownLoader.setDownloadThreadCount(mDownloadThreadCount);
//                templateDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, templateName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(
                        templateDownLoadInfo,
                        new ZipDownloadListener(
                                mMorecachein,
                                mMorecacheout,
                                resourceName,
                                ips,
                                cdns,
                                coursewareInfo.getTemplateUrl(),
                                coursewareInfo.getTemplateMd5(),
                                new AtomicInteger(0),
                                itemLiveId,
                                "1"));
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                }
                documentNum.getAndIncrement();
            }
        }
    }

    /**
     * 下载公共资源(字体)
     *
     * @param resourseInfos
     * @param ips
     * @param cdns
     */
    private void downloadResources(List<String> resourseInfos, List<String> cdns, final List<String> ips) {
        final File mPublicCacheout = new File(cacheFile, mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        logger.i("下载公共资源");
        String tempIP = ips.get(0);
        String ip;
        boolean isIp = false;
        //拼接ip
        if (tempIP.contains("http") || tempIP.contains("https")) {
            ip = tempIP;
            isIp = false;
        } else {
            ip = "http://" + tempIP;
            isIp = true;
        }
        int cdnIndex = 0;
        int subIndex = cdns.get(cdnIndex).indexOf("/") + 2;
        String cdn = cdns.get(cdnIndex).substring(subIndex);
        for (String url : resourseInfos) {
            if (url.endsWith(".zip")) {
                final String fileName;
                int index = url.lastIndexOf("/");
                if (index != -1) {
                    fileName = url.substring(index + 1);
                } else {
                    fileName = MD5Utils.getMD5(url);
                }
                final File save = new File(mPublicCacheout, fileName);
                if (!fileIsExists(save.getAbsolutePath())) {
//                if (!fileIsExists(save.getAbsolutePath())) {
                    logger.d("resource zip url path:  " + ip + url + "   file name:" + fileName + ".zip");
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(
                            ip + url,
                            mPublicCacheout.getAbsolutePath(),
                            fileName + ".temp",
                            "");
                    if (isIp) {
                        downLoadInfo.setHost(cdn);
                    }
                    PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(
                            downLoadInfo,
                            new ZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    fileName,
                                    new AtomicInteger(0),
                                    "",
                                    "2"));
                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
                    documentNum.getAndIncrement();
                }
            } else {
                String fileName;
                if (url.endsWith(FZY3JW_TTF)) {
                    fileName = FZY3JW_TTF;
                } else {
                    fileName = MD5Utils.getMD5(url);
                }
                final File save = new File(mPublicCacheout, fileName);
                if (!fileIsExists(save.getPath())) {
                    logger.d("resource ttf url path:  " + ip + url + "   file name:" + fileName + ".nozip");
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
                    PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(
                            downLoadInfo,
                            new NoZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    fileName,
                                    new AtomicInteger(0),
                                    "3"));
                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
                    documentNum.getAndIncrement();
                }
            }
        }
    }

    private class Progresses implements ZipProg {
        @Override
        public void onProgressUpdate(Integer... values) {

        }

        @Override
        public void onPostExecute(Exception exception) {

        }

        @Override
        public void setMax(int max) {

        }
    }

//    AtomicBoolean isUnZip = new AtomicBoolean(false);

//    private ZipExtractorTask zipExtractorTask;

    Executor executos = new ThreadPoolExecutor(3, 3,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    class ZipDownloadListener implements DownloadListener {

        private File mMorecacheout;
        private File mMorecachein;
        private String mFileName;
        private String url;
        private String md5;
        AtomicInteger downTryCount;
        List<String> cdns;
        List<String> ips;
        String itemLiveId;
        String resourcetype;

        public ZipDownloadListener(
                File mMorecachein,
                File mMorecacheout,
                String fileName,
                final List<String> ips,
                List<String> cdns,
                String url,
                String md5,
                AtomicInteger downTryCount,
                String itemLiveId,
                String resourcetype) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
            this.itemLiveId = itemLiveId;
            this.resourcetype = resourcetype;
        }

        @Override
        public void onStart(String url) {
            StableLogHashMap hashMap = new StableLogHashMap();
//            hashMap.put("eventid", LogConfig.PRE_LOAD_START);
            hashMap.put("logtype", "startPreload");
            hashMap.put("preloadid", md5);
            hashMap.put("loadurl", url);
            hashMap.put("isresume", "false");
            hashMap.put("sno", "1");
            hashMap.put("liveid", itemLiveId);
            hashMap.put("resourcetype", resourcetype);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download zip success");
            StableLogHashMap hashMap = new StableLogHashMap();
            hashMap.put("logtype", "endPreload");
            hashMap.put("preloadid", md5);
            hashMap.put("loadurl", url);
            hashMap.put("sno", "2");
            hashMap.put("status", "true");
            StringBuilder sb = new StringBuilder(ips.get(0));
            for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                sb.append("," + ips.get(i));
            }
            hashMap.put("failurl", sb.toString());
            hashMap.put("liveid", itemLiveId);
            hashMap.put("resourcetype", resourcetype);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());

            decrementDocument();

            File tempFile = new File(folderPath, fileName);
            File file = new File(folderPath, mFileName);
            boolean rename = tempFile.renameTo(file);
//            if (!isUnZip.get()) {
            new ZipExtractorTask(file, mMorecacheout, true, new Progresses()) {
                @Override
                protected Exception doInBackground(Void... params) {

                    StableLogHashMap unZipMap = new StableLogHashMap();
                    unZipMap.put("logtype", "startUnzip");
                    unZipMap.put("preloadid", md5);
                    unZipMap.put("extrainfo", mMorecacheout.getAbsolutePath());
                    unZipMap.put("sno", "3");
                    unZipMap.put("liveid", itemLiveId);
                    unZipMap.put("resourcetype", resourcetype);
                    UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, unZipMap.getData());
                    return super.doInBackground(params);

                }

                @Override
                protected void onPostExecute(Exception exception) {
                    super.onPostExecute(exception);
                    StableLogHashMap unZipMap = new StableLogHashMap();
                    unZipMap.put("logtype", "endUnzip");
                    unZipMap.put("preloadid", md5);
//                    if(exception==null){
                    unZipMap.put("status", exception == null ? "true" : "false");
//                    }
                    unZipMap.put("extrainfo", mMorecacheout.getAbsolutePath());
                    unZipMap.put("sno", "3");
                    unZipMap.put("liveid", itemLiveId);
                    unZipMap.put("resourcetype", resourcetype);
                    UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, unZipMap.getData());
                }
            }.executeOnExecutor(executos);

//                isUnZip.set(true);
//            }
        }

        @Override
        public void onFail(int errorCode) {
            String oldIP = ips.get(downTryCount.get() % ipLength.get());
            logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".zip");
            downTryCount.getAndIncrement();
            String tempIP = ips.get(downTryCount.get() % ipLength.get());
            String ip;
            boolean isIP;
            //拼接ip
            if (tempIP.contains("http") || tempIP.contains("https")) {
                isIP = false;
                ip = tempIP;
            } else {
                ip = "http://" + tempIP;
                isIP = true;
            }
            int tryCount = downTryCount.get();
            logger.d("download zip fail trycount" + tryCount);
            if (tryCount < ips.size()) {
                logger.i(ips.size() + "");
                int index = cdns.get(tryCount % cdnLength.get()).indexOf("/") + 2;
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".zip");
                if (isIP) {
                    downLoadInfo.setHost(cdns.get(tryCount % cdnLength.get()).substring(index));
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
                ZipDownloadListener mZipDownloadListener = new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount, itemLiveId, resourcetype);
                PreLoadDownLoaderManager.DownLoadInfoListener preLoadDownLoaderManager = new PreLoadDownLoaderManager.DownLoadInfoListener(downLoadInfo, mZipDownloadListener);

                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(preLoadDownLoaderManager);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(preLoadDownLoaderManager);
                }
            } else {
                StableLogHashMap hashMap = new StableLogHashMap();
                hashMap.put("logtype", "endPreload");
                hashMap.put("preloadid", md5);
                hashMap.put("loadurl", url);

                hashMap.put("sno", "2");
                hashMap.put("status", "false");
                hashMap.put("errorcode", String.valueOf(errorCode));
                StringBuilder sb = new StringBuilder(ips.get(0));
                for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                    sb.append("," + ips.get(i));
                }
                hashMap.put("failurl", sb.toString());
                hashMap.put("liveid", itemLiveId);
                hashMap.put("resourcetype", resourcetype);
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());

            }

        }

        @Override
        public void onFinish() {
//            logger.i("zip download finish");
        }
    }

    class NoZipDownloadListener implements DownloadListener {
        private File mMorecacheout;
        private File mMorecachein;
        private String mFileName;
        private String url;
        private String md5;
        AtomicInteger downTryCount;
        List<String> cdns;
        List<String> ips;
        String resourcetype;

        public NoZipDownloadListener(
                File mMorecachein,
                File mMorecacheout,
                String fileName,
                final List<String> ips,
                List<String> cdns,
                String url,
                String md5,
                AtomicInteger downTryCount,
                String resourcetype) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
            this.resourcetype = resourcetype;
        }

        @Override
        public void onStart(String url) {
            StableLogHashMap hashMap = new StableLogHashMap();
//            hashMap.put("eventid", LogConfig.PRE_LOAD_START);
            hashMap.put("logtype", "startPreload");
            hashMap.put("preloadid", md5);
            hashMap.put("loadurl", url);
            hashMap.put("isresume", "false");
            hashMap.put("sno", "1");
            hashMap.put("liveid", "");
            hashMap.put("resourcetype", resourcetype);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download ttf success");

            StableLogHashMap hashMap = new StableLogHashMap();
            hashMap.put("logtype", "endPreload");
            hashMap.put("preloadid", md5);
            hashMap.put("loadurl", url);
            hashMap.put("sno", "2");
            hashMap.put("status", "true");
            StringBuilder sb = new StringBuilder(ips.get(0));
            for (int i = 1; i < downTryCount.get() && i < ips.size(); i++) {
                sb.append("," + ips.get(i));
            }

            hashMap.put("failurl", downTryCount.get() != 0 ? sb.toString() : "");

            hashMap.put("liveid", "");
            hashMap.put("resourcetype", resourcetype);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());

            decrementDocument();

            File file = new File(folderPath, fileName);
            file.renameTo(new File(folderPath, mFileName));
        }

        @Override
        public void onFail(int errorCode) {
//            String ip = "http://" + ips.get((cdnPos.getAndIncrement()) % cdnLength.get());
            String oldIP = ips.get(downTryCount.get() % ipLength.get());
            logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".nozip");
            downTryCount.getAndIncrement();
            String tempIP = ips.get(downTryCount.get() % ipLength.get());
            String ip;
            boolean isIP;
            //拼接ip
            if (tempIP.contains("http") || tempIP.contains("https")) {
                ip = tempIP;
                isIP = false;
            } else {
                ip = "http://" + tempIP;
                isIP = true;
            }
            int tryCount = downTryCount.get();
            logger.d("download fail trycount" + tryCount);


            if (tryCount < ips.size()) {
                int index = cdns.get(tryCount % cdnLength.get()).indexOf("/") + 2;
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".nozip");
                if (isIP) {
                    downLoadInfo.setHost(cdns.get(cdnPos.get() % cdnLength.get()).substring(index));
                }
                PreLoadDownLoaderManager.DownLoadInfoListener downLoadInfoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(downLoadInfo,
                        new NoZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount, resourcetype));
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(downLoadInfoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(downLoadInfoListener);
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new NoZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
            } else {
                StableLogHashMap hashMap = new StableLogHashMap();
                hashMap.put("logtype", "endPreload");
                hashMap.put("preloadid", md5);
                hashMap.put("loadurl", url);

                hashMap.put("sno", "2");
                hashMap.put("status", "false");
                hashMap.put("errorcode", String.valueOf(errorCode));
                StringBuilder sb = new StringBuilder(ips.get(0));
                for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                    sb.append("," + ips.get(i));
                }
                hashMap.put("failurl", sb.toString());
                hashMap.put("liveid", "");
                hashMap.put("resourcetype", resourcetype);
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
            }
        }

        @Override
        public void onFinish() {
//            logger.i("no zip download finish");
        }
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
//        logger.i(strFile);
        try {
            File f = new File(strFile);
            logger.i(strFile + "" + f.getName() + " " + f.isFile() + " " + f.exists());
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 统计所有文件是否下载完
     */
    private void decrementDocument() {
        documentNum.getAndDecrement();
//        logger.i("remaining " + documentNum.get());
        //下载完成，则注销
        if (documentNum.get() == 0) {
            AppBll.getInstance().unRegisterAppEvent(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (event.getClass() == AppEvent.class) {
            //只有处于wifi情况下才会开启自动下载
            if (event.netWorkType == NetWorkHelper.WIFI_STATE) {
                PreLoadDownLoaderManager.startAutoDownload();
            }
        }
    }

}
