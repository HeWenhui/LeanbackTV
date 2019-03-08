package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.content.Context;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

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
    private String liveId;
    private int mSubject = -1;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser liveHttpResponseParser;
    File cacheFile;
    private File todayCacheDir;
    public static String mPublicCacheoutName = "publicRes";
    public static int mDownloadThreadCount = 1;

    private AtomicInteger subjectNum = new AtomicInteger(0);

    public CoursewarePreload(Context context, String liveId, int subject) {
        mContext = context;
        this.liveId = liveId;
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
        LiveThreadPoolExecutor executor = LiveThreadPoolExecutor.getInstance();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (File itemFile : file.listFiles()) {
                    if (!itemFile.isDirectory()) {
                        if (isCoursewareDir(itemFile.getName()) && !itemFile.getName().equals(today)) {
                            itemFile.delete();
                        }
                    }
                }
            }
        });

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
    public void getCoursewareInfo() {
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
        if (liveId != null && !"".equals(liveId)) {
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
//        if (BuildConfig.DEBUG) {
//            newIPs.add("https://icourse.xesimg.com");
//        }
        newIPs.addAll(cdns);
        newIPs.addAll(ips);

        logger.i("" + cdns.size() + " " + newIPs.size());
        cdnLength.set(cdns.size());
        ipLength.set(newIPs.size());

        resources.add("/courseware_pages/74989b568bfceaab053a8b6b297ac007/katex@0.10.1.zip");

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
    private void exeDownLoadCourseware(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, List<String> cdns, List<String> ips) {

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
            downloadCourseware(todayLiveCacheDir, coursewareInfos, ips, cdns);
        }
        ShareDataManager shareDataManager = ShareDataManager.getInstance();

        shareDataManager.put(ShareBusinessConfig.SP_PRELOAD_COURSEWARE, liveIds.toString(), ShareDataManager.SHAREDATA_USER);

    }

    /**
     * 下载每一个单独的课件资源
     *
     * @param path
     * @param coursewareInfos
     * @param ips
     * @param cdns
     */
    private void downloadCourseware(File path, List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos, List<String> ips, List<String> cdns) {

        final File mMorecachein = new File(path, liveId);
        if (!mMorecachein.exists()) {
            mMorecachein.mkdirs();
        }
        final File mMorecacheout = new File(path, liveId + "child");
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
                equals = coursewareInfo.getMd5().equalsIgnoreCase(filemd5);
            }
            if (!fileIsExists(resourceSave.getAbsolutePath()) || (fileIsExists(resourceSave.getAbsolutePath()) && !equals)) {
                DownLoadInfo resourceDownLoadInfo = DownLoadInfo.createFileInfo(ip + coursewareInfo.getResourceUrl(), mMorecachein.getAbsolutePath(), resourceName + ".temp", coursewareInfo.getMd5());
//                resourceSave.mkdirs();
//                try {
//                    resourceSave.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                if (isIP) {
                    resourceDownLoadInfo.setHost(cdn);
                }
//                DownLoader resourceDownLoader = new DownLoader(mContext, resourceDownLoadInfo);
//                resourceDownLoader.setDownloadThreadCount(mDownloadThreadCount);
                logger.d("courseware url path:  " + ip + coursewareInfo.getResourceUrl() + "   file name:" + resourceName + ".zip");
//                resourceDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(resourceDownLoadInfo,
                        new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger(0)));

                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                }
            }
            //下载模板资源
            final String templateName = MD5.md5(coursewareInfo.getTemplateUrl()) + ".zip";
            File templateSave = new File(mMorecachein, resourceName);
            if (!fileIsExists(templateSave.getAbsolutePath())) {
//                templateSave.mkdirs();
                DownLoadInfo templateDownLoadInfo = DownLoadInfo.createFileInfo(ip + coursewareInfo.getTemplateUrl(), mMorecachein.getAbsolutePath(), templateName + ".temp", "");
                logger.d("template url path:  " + ip + coursewareInfo.getTemplateUrl() + "   file name:" + templateName + ".zip");
                if (isIP) {
                    templateDownLoadInfo.setHost(cdn);
                }
//                DownLoader templateDownLoader = new DownLoader(mContext, templateDownLoadInfo);
//                templateDownLoader.setDownloadThreadCount(mDownloadThreadCount);
//                templateDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, templateName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(templateDownLoadInfo,
                        new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger(0)));
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                }
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
    private void downloadResources(List<String> resourseInfos, List<String> cdns, List<String> ips) {
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
//        if (resourseInfos.size() > 0) {
//            for (int i = 0; i < resourseInfos.size(); i++) {
        for (String url : resourseInfos) {
//                String url = resourseInfos.get(i);
            final AtomicInteger downTryCount = new AtomicInteger();
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
//                    save.mkdirs();
//                    try {
//                        save.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (!fileIsExists(save.getAbsolutePath())) {
                    logger.d("resource zip url path:  " + ip + url + "   file name:" + fileName + ".zip");
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
                    if (isIp) {
                        downLoadInfo.setHost(cdn);
                    }
//                        final DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                        downLoader.start(new ZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, "", downTryCount));
                    PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(
                            downLoadInfo,
                            new ZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, fileName, new AtomicInteger(0)));
                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
                }
            } else {
                String fileName = MD5Utils.getMD5(url);
                final File save = new File(mPublicCacheout, fileName);
                if (!fileIsExists(save.getPath())) {
//                    save.mkdirs();
//                    try {
//                        save.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                        downLoader.start(new NoZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, "", downTryCount));
//                }
                    logger.d("resource ttf url path:  " + ip + url + "   file name:" + fileName + ".nozip");
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
//                        final DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
                    PreLoadDownLoaderManager.DownLoadInfoListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(
                            downLoadInfo,
                            new NoZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, fileName, new AtomicInteger(0)));
                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
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

        public ZipDownloadListener(File mMorecachein, File mMorecacheout, String fileName, List<String> ips, List<String> cdns, String url, String md5, AtomicInteger downTryCount) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download zip success");
            File tempFile = new File(folderPath, fileName);
            File file = new File(folderPath, mFileName);
            boolean rename = tempFile.renameTo(file);
//            if (!isUnZip.get()) {
            new ZipExtractorTask(file, mMorecacheout, true, new Progresses()).executeOnExecutor(executos);

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
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(new PreLoadDownLoaderManager.DownLoadInfoListener(downLoadInfo, this));
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(new PreLoadDownLoaderManager.DownLoadInfoListener(downLoadInfo, this));
                }
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

        public NoZipDownloadListener(File mMorecachein, File mMorecacheout, String fileName, List<String> ips, List<String> cdns, String url, String md5, AtomicInteger downTryCount) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download ttf success");
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
                PreLoadDownLoaderManager.DownLoadInfoListener downLoadInfoListener = new PreLoadDownLoaderManager.DownLoadInfoListener(downLoadInfo, this);
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(downLoadInfoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(downLoadInfoListener);
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new NoZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
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


}
