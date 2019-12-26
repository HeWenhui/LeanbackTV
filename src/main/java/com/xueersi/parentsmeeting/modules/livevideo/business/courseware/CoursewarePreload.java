package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
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
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Context mContext;
    //    private String liveId;
    private int mSubject = -1;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser liveHttpResponseParser;
    File cacheFile;
    private File todayCacheDir;
    public static String mPublicCacheoutName = "publicRes";
    public static String FZY3JW_TTF = "FZY3JW.ttf";

    List<CoursewareInfoEntity> courseWareInfos = new CopyOnWriteArrayList<>();
    public static final String NB_EXPERIMENT = "NBExperiment";
//    public static int mDownloadThreadCount = 1;

    /**
     * 所有需要下载文件的总量
     */
    private AtomicInteger documentNum = new AtomicInteger(0);
    /**
     * 下载的科目总数
     */
    private AtomicInteger subjectNum = new AtomicInteger(0);

    private int isBig;

    private String newBigUrl;

    /**
     * nb 加试实验 预加载资源信息
     **/
//    private CoursewareInfoEntity.NbCoursewareInfo mNbCoursewareInfo;
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

    public static long getFreeSize() {
        try {
            File cacheFile = LiveCacheFile.geCacheFile(ContextManager.getContext(), "webviewCache");
            long nSDFreeSize = cacheFile.getFreeSpace() / 1024L / 1024L;
            Log.d("CoursewarePreload", "getFreeSize:nSDFreeSize=" + nSDFreeSize);
            return nSDFreeSize;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("CoursewarePreload", e);
        }
        return -1234;
    }

    public void setBig(int big, String url) {
        this.isBig = big;
        this.newBigUrl = url;
    }

    public void setmHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    /**
     * 是否紧急下载
     */
    AtomicBoolean isPrecise = new AtomicBoolean(false);

    AtomicInteger ipPos, cdnPos, ipLength, cdnLength;

    /**
     * 删除旧的Dir
     */
    private void deleteOldDirAsync(final File file, final String today) {
        synchronized (CoursewarePreload.class) {
            executos.execute(new Runnable() {
                @Override
                public void run() {
                    logger.i("start delete file");
                    if (file == null || file.listFiles() == null) {
                        return;
                    }
                    //buglys上面有报Attempt to get length of null array,加上try,catch
                    try {
                        File[] files = file.listFiles();
                        if (files == null) return;
                        for (File itemFile : files) {
                            //文件夹是日期格式并且不是今天才删除
                            if (!itemFile.getName().equals(today) && !itemFile.getName().equals(mPublicCacheoutName) && isCoursewareDir(itemFile.getName())) {
                                if (!itemFile.isDirectory()) {
                                    itemFile.delete();
                                } else {
                                    deleteFor(itemFile);
                                    itemFile.delete();
                                }
                            }
                        }
                        logger.i("delete file success");
                        StableLogHashMap hashMap = new StableLogHashMap();
                        hashMap.put("logtype", " deleteCourseware");
                        hashMap.put("dir", file.getAbsolutePath());
                        hashMap.put("sno", "5");
                        hashMap.put("status", "true");
                        hashMap.put("ip", IpAddressUtil.USER_IP);
                        hashMap.put("freeSize", "" + getFreeSize());
                        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
                    } catch (Exception e) {
                        logger.e(e);
                        LiveCrashReport.postCatchedException(TAG, e);
                    }
                }
            });
        }
    }

    /**
     * 循环删除子文件夹
     *
     * @param file
     */
    private synchronized static void deleteFor(final File file) {
        if (file == null || file.listFiles() == null) {
            return;
        }

        File[] files = file.listFiles();
        if (files == null) return;
        for (File itemFile : files) {
            if (!itemFile.isDirectory()) {
                itemFile.delete();
            } else {
                deleteFor(itemFile);
                itemFile.delete();
            }
        }
    }

    /**
     * 是否是课件的文件夹(课件文件夹由日期构成)
     *
     * @return
     */
    private boolean isCoursewareDir(String fileName) {
        try {
            Integer.parseInt(fileName);
            return true;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }

    }

    /**
     * 获取课件信息
     */
    public void getCoursewareInfo(String liveId) {
        executos.allowCoreThreadTimeOut(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        todayCacheDir = new File(cacheFile, today);

        deleteOldDirAsync(cacheFile, today);

        //根据传liveid来判断 不为空或者不是""则为直播进入下载资源，否则为学习中心进入下载资源
        ipPos = new AtomicInteger(0);
        ipLength = new AtomicInteger();
        cdnLength = new AtomicInteger();
        cdnPos = new AtomicInteger(0);
        if (!TextUtils.isEmpty(liveId)) {
            isPrecise.set(true);
            if (isBig == 1) {
                if (!TextUtils.isEmpty(newBigUrl)) {
                    subjectNum.incrementAndGet();
                    mHttpManager.getBigLiveCourewareInfo(newBigUrl, liveId, new CoursewareHttpCallBack(false, "biglive", liveId));
                }
            } else {
                if (0 == mSubject) {//理科
                    logger.i("donwload science");
                    subjectNum.getAndIncrement();
                    mHttpManager.getScienceCourewareInfo(liveId, new CoursewareHttpCallBack(false, "science", liveId));
                } else if (1 == mSubject) {//英语
                    logger.i("download english");
                    subjectNum.getAndIncrement();
                    mHttpManager.getEnglishCourewareInfo(liveId, new CoursewareHttpCallBack(false, "english", liveId));
                } else if (2 == mSubject) {//语文
                    logger.i("download chs");
                    subjectNum.getAndIncrement();
                    mHttpManager.getArtsCourewareInfo(liveId, new CoursewareHttpCallBack(false, "chs", liveId));
                }
            }
        } else {//下载当天所有课件资源
            logger.i("donwload all subjects");
            subjectNum.getAndIncrement();
            mHttpManager.getScienceCourewareInfo("", new CoursewareHttpCallBack(false, "science", ""));
            subjectNum.getAndIncrement();
            mHttpManager.getEnglishCourewareInfo("", new CoursewareHttpCallBack(false, "english", ""));
            subjectNum.getAndIncrement();
            mHttpManager.getArtsCourewareInfo("", new CoursewareHttpCallBack(false, "chs", ""));
//            mHttpManager.getArtsCourewareInfo("", new CoursewareHttpCallBack(false, "chs", ""));
//            if (AppConfig.DEBUG) {
//                subjectNum.getAndIncrement();
//                mHttpManager.getTestIntelligentRecognitionInfo(new CoursewareHttpCallBack(false, "intelligent_recg"));
//            }
        }
    }


    public class CoursewareHttpCallBack extends HttpCallBack {

        private String arts;
        private String liveId;

        public CoursewareHttpCallBack(boolean isShow, String arts, String liveId) {
            super(isShow);
            this.arts = arts;
            this.liveId = liveId;
        }


        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            CoursewareInfoEntity coursewareInfoEntity = liveHttpResponseParser.parseCoursewareInfo(responseEntity);
            logger.i(responseEntity.getJsonObject().toString());
            courseWareInfos.add(coursewareInfoEntity);
            logger.i(arts + " pmSuccess");
            // 加试实验 只从理科资源预加载接口返回
//            if ("science".equals(arts) && coursewareInfoEntity != null) {
//                mNbCoursewareInfo = coursewareInfoEntity.getNbCoursewareInfo();
//            }
            boolean perform = performDownLoad();
            try {
                StableLogHashMap hashMap = new StableLogHashMap();
                hashMap.put("logtype", "onPmSuccess");
                if (coursewareInfoEntity != null) {
                    hashMap.put("size", "" + coursewareInfoEntity.getCoursewaresList().size());
                } else {
                    hashMap.put("size", "-10");
                }
                hashMap.put("arts", "" + arts);
                hashMap.put("subjectnum", "" + subjectNum.get());
                hashMap.put("perform", "" + perform);
                hashMap.put("liveId", "" + liveId);
                hashMap.put("ip", IpAddressUtil.USER_IP);
                hashMap.put("freeSize", "" + CoursewarePreload.getFreeSize());
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID,
                        LogConfig.PRE_LOAD_START, hashMap.getData());
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(TAG, e);
            }
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

    private boolean performDownLoad() {
        logger.i("performDownLoad:size=" + courseWareInfos.size() + " " + subjectNum.get());
        if (courseWareInfos.size() == subjectNum.get()) {
            logger.i("perform download ");
            LiveAppBll.getInstance().registerAppEvent(CoursewarePreload.this);
//            storageLiveId();
            execDownLoad(
                    sortArrays(),
                    mergeList(courseWareInfos, 1),
                    mergeList(courseWareInfos, 2),
                    mergeList(courseWareInfos, 3));
//            List<CoursewareInfoEntity.NbCoursewareInfo> ansList = InfoUtils.mergeNbList(courseWareInfos);
            return true;
        } else {
            return false;
        }
    }

    private void execDownLoadNb(final List<CoursewareInfoEntity.NbCoursewareInfo> list, final List<String> cdns, final List<String> allIPS) {
        executos.execute(new Runnable() {
            @Override
            public void run() {
                if (allIPS == null || allIPS.size() == 0) {
                    return;
                }
                downloadNbCoursewareAsync(list, cdns, allIPS);
            }
        });
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

//    private List<CoursewareInfoEntity.ItemCoursewareInfo> mergeList(List<CoursewareInfoEntity.LiveCourseware> coursewares) {
//        List<CoursewareInfoEntity.ItemCoursewareInfo> itemCourseware = new LinkedList<>();
//        for (CoursewareInfoEntity.LiveCourseware liveCourseware : coursewares) {
//            itemCourseware.addAll(liveCourseware.getCoursewareInfos());
//        }
//        return itemCourseware;
//    }

    private List<String> appendList(List<String> totalList, List<String> list) {
        if (totalList != null && list != null) {
            for (String item : list) {
                if (!totalList.contains(item)) {
                    totalList.add(item);
                }
            }
        }
        return totalList;
    }

    private void execDownLoad(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares,
                              List<String> cdns, List<String> ips, List<String> resources) {
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

        execDownLoadNb(InfoUtils.mergeNbList(courseWareInfos), cdns, newIPs);
        //下载Nb 预加载资源
//        if (mNbCoursewareInfo != null) {
//            downLoadNbResource(mNbCoursewareInfo, cdns, newIPs);
//        }


    }

    private String getFileName(String url) {
        final String fileName;
        int index = url.lastIndexOf("/");
        if (index != -1) {
            fileName = url.substring(index + 1);
        } else {
            fileName = MD5Utils.getMD5(url);
        }
        return fileName;
    }

    private void downloadNbCoursewareAsync(List<CoursewareInfoEntity.NbCoursewareInfo> addExps,
                                           final List<String> cdns,
                                           final List<String> allIPS) {

//        File cacheDir = LiveCacheFile.geCacheFile(mContext,
//                NbCourseWareConfig.NB_RESOURSE_CACHE_DIR);
        final File cacheDir = InfoUtils.getNbFilePath(mContext);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        executos.execute(new Runnable() {
            @Override
            public void run() {
                InfoUtils.deleteNotTodayOldDirSync(cacheDir.getParent());
            }
        });
        for (int i = 0; i < addExps.size(); i++) {
            CoursewareInfoEntity.NbCoursewareInfo addExp = addExps.get(i);
            String remoteCourseware = addExp.getResourceUrl();
            String remoteMD5 = addExp.getResourceMd5();
            if (remoteCourseware.endsWith(".zip")) {
                //每个预加载文件的子文件夹
                String outDirName = addExp.getId();
                File outDir = new File(cacheDir, outDirName);
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }
                String tempIP = allIPS.get(0), cdn = "";
                String ip;
                boolean isIP;
                //拼接ip
                if (tempIP.contains("http") || tempIP.contains("https")) {
                    isIP = false;
                    ip = tempIP;
                } else {
                    isIP = true;
                    ip = "http://" + tempIP;
                    //截取域名，去掉前面 http://
                    int index = cdns.get(0).indexOf("/") + 2;
                    cdn = cdns.get(0).substring(index);
                }
                String fileNameSuffix = addExp.getId() + ".zip";
                if (TextUtils.isEmpty(fileNameSuffix)) {
                    fileNameSuffix = getFileName(remoteCourseware);
                }
                final File save = new File(cacheDir, fileNameSuffix);
//                boolean equals = false;
//                if (fileIsExists(save.getAbsolutePath())) {
//                    String filemd5 = FileUtils.getFileMD5ToString(save);
//                    equals = remoteMD5.equalsIgnoreCase(filemd5);
//                if (!equals) {
//                    InfoUtils.deleteDirSync(save);
//                }
//                }
                boolean equals = InfoUtils.eqaulsMd5(remoteMD5, save);
                if (!equals) {
                    InfoUtils.deleteDirSync(save);
                }
                if (!InfoUtils.fileIsExists(save.getAbsolutePath()) || !equals) {
//                if (!fileIsExists(save.getAbsolutePath())) {
                    String remoteUrl = ip + remoteCourseware;
                    logger.i("nb resource zip url path:  " + remoteUrl + "   file name:" + fileNameSuffix);
                    String inDownLoadFileName = addExp.getId() + ".zip.temp";
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(
                            remoteUrl,
                            cacheDir.getAbsolutePath(),
                            inDownLoadFileName,
                            remoteMD5);
                    if (isIP) {
                        downLoadInfo.setHost(cdn);
                    }
                    PreLoadDownLoaderManager.DownLoadInfoAndListener
                            infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                            downLoadInfo,
                            new ZipDownloadListener(
                                    cacheDir,
                                    outDir,
                                    fileNameSuffix,
                                    allIPS,
                                    cdns,
                                    remoteCourseware,
                                    remoteMD5,
                                    new AtomicInteger(0),
                                    "",
                                    NbCourseWareConfig.RESOURSE_TYPE_NB),
                            "");
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

    /**
     * //下载同一场直播的课件资源
     *
     * @param liveCoursewares
     * @param cdns
     * @param ips
     */
    private void exeDownLoadCourseware(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, final List<String> cdns, final List<String> ips) {

        StringBuilder liveIds = new StringBuilder("");
        long before = System.currentTimeMillis();
        for (final CoursewareInfoEntity.LiveCourseware liveCourseware : liveCoursewares) {
            //课件列表
            liveIds.append(liveCourseware.getLiveId() + ",");

            PreloadStaticStorage.preloadLiveId.add(liveCourseware.getLiveId());
            executos.execute(new Runnable() {
                @Override
                public void run() {
                    List<CoursewareInfoEntity.ItemCoursewareInfo> itemCoursewareInfos = liveCourseware.getCoursewareInfos();
                    File todayLiveCacheDir = new File(todayCacheDir, liveCourseware.getLiveId());
                    boolean exists = todayLiveCacheDir.exists();
                    boolean mkdirs = false;
                    if (!todayLiveCacheDir.exists()) {
                        mkdirs = todayLiveCacheDir.mkdirs();
                    }
//            logger.d("exeDownLoadCourseware:exists=" + exists + ",mkdirs=" + mkdirs);
                    String itemLiveId = liveCourseware.getLiveId();
                    downloadCourseware(todayLiveCacheDir, itemCoursewareInfos, ips, cdns, itemLiveId);
//                    String liveCoursewareNowDown = liveCourseware.getLiveId();

                    downLiveIdGroupVideo(itemLiveId);
                    //如果没有课件预加载，需要直接走视频预加载,也就是再把视频url添加一般，底层会过滤

                }
            });
        }
        doExeAllGroupVideo();
        logger.d("exeDownLoadCourseware:size=" + liveCoursewares.size() + ",time=" + (System.currentTimeMillis() - before));
        ShareDataManager shareDataManager = ShareDataManager.getInstance();
        shareDataManager.put(ShareDataConfig.SP_PRELOAD_COURSEWARE, liveIds.toString(), ShareDataManager.SHAREDATA_USER);
    }

    /**
     * 下载指定liveId的视频
     *
     * @param itemLiveId
     */
    private void downLiveIdGroupVideo(String itemLiveId) {
        for (int i = 0; i < courseWareInfos.size(); i++) {
            List<CoursewareInfoEntity.GroupClassVideoInfo> groupClassVideoInfos = courseWareInfos.get(i).getCourses();
            if (groupClassVideoInfos == null) {
                continue;
            }
            for (CoursewareInfoEntity.GroupClassVideoInfo groupClassVideoInfo : groupClassVideoInfos) {
                if (itemLiveId != null && itemLiveId.equals(groupClassVideoInfo.getLiveId())) {
                    exeDownGroupClassVideoPath(groupClassVideoInfo);
                    groupClassVideoInfo.setDown(true);
                    break;
                }
            }
        }
    }

    /**
     * 下载所有没有下载过的视频
     */
    private void doExeAllGroupVideo() {

        for (int i = 0; i < courseWareInfos.size(); i++) {
            List<CoursewareInfoEntity.GroupClassVideoInfo> groupClassVideoInfos = courseWareInfos.get(i).getCourses();
            if (groupClassVideoInfos == null) {
                continue;
            }
            for (CoursewareInfoEntity.GroupClassVideoInfo groupClassVideoInfo : groupClassVideoInfos) {
                if (!groupClassVideoInfo.isDown()) {
                    exeDownGroupClassVideoPath(groupClassVideoInfo);
                    groupClassVideoInfo.setDown(true);
                }
            }
        }
    }

    private void exeDownGroupClassVideoPath(CoursewareInfoEntity.GroupClassVideoInfo videoInfo) {
        if (videoInfo == null) {
            return;
        }
        List<String> hostList = videoInfo.getHost();
        List<CoursewareInfoEntity.GroupClassVideoInfo.GroupClassPath>
                groupClassPathList = videoInfo.getGroupClassPaths();
        if (InfoUtils.checkoutNone(hostList) || InfoUtils.checkoutNone(groupClassPathList)) {
            return;
        }
        String host = hostList.get(0);
        CoursewareInfoEntity.GroupClassVideoInfo.GroupClassPath groupClassPath = groupClassPathList.get(0);
        if (groupClassPath == null) {
            return;
        }
        String path = groupClassPath.getPath();

        String fileName = groupClassPath.getFileName();
        File todayLiveCacheDir = new File(todayCacheDir, videoInfo.getLiveId());
        File mMorecacheout = new File(todayLiveCacheDir, videoInfo.getLiveId() + "child");
        mMorecacheout = LiveCacheFile.getGroupClassFile(mContext, videoInfo.getLiveId());
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
//        final File mMorecachein = new File(path, videoInfo.getLiveId());
        DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(host + path,
                mMorecacheout.getAbsolutePath(), fileName, null);
        PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener =
                new PreLoadDownLoaderManager.DownLoadInfoAndListener(downLoadInfo,
                        new NoZipDownloadListener(
                                mMorecacheout,
                                mMorecacheout,
                                fileName,
                                videoInfo.getHost(),
                                null,
                                path,
                                "",
                                new AtomicInteger(1),
                                "4"),
                        videoInfo.getLiveId());
        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
    }

    /**
     * 下载每一个单独的课件资源
     *
     * @param path
     * @param itemCoursewareInfos
     * @param ips
     * @param cdns
     */
    private void downloadCourseware(File path,
                                    List<CoursewareInfoEntity.ItemCoursewareInfo> itemCoursewareInfos,
                                    final List<String> ips,
                                    List<String> cdns, String itemLiveId) {

        final File mMorecachein = new File(path, itemLiveId);
        if (!mMorecachein.exists()) {
            mMorecachein.mkdirs();
        }
        File mMorecacheout = new File(path, itemLiveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        logger.i("BBB in:" + mMorecachein.getAbsolutePath() + " out:" + mMorecacheout.getAbsolutePath());
//        if (mMorecachein.getAbsolutePath().equals(debugString)) {
//            logger.i(debugLog);
//        }
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
        long before = System.currentTimeMillis();
        for (CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo : itemCoursewareInfos) {
            {
                //下载课件资源
                final String resourceName = MD5.md5(coursewareInfo.getResourceUrl()) + ".zip";
                File resourceSave = new File(mMorecachein, resourceName);
                boolean equals = false;
                if (InfoUtils.fileIsExists(resourceSave.getAbsolutePath())) {
                    String filemd5 = FileUtils.getFileMD5ToString(resourceSave);
                    equals = coursewareInfo.getResourceMd5().equalsIgnoreCase(filemd5);
                }
                if (!InfoUtils.fileIsExists(resourceSave.getAbsolutePath()) || (InfoUtils.fileIsExists(resourceSave.getAbsolutePath()) && !equals)) {
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
//                    logger.d("courseware url path:  " + ip + coursewareInfo.getResourceUrl() + "   file name:" + resourceName + ".zip");
//                resourceDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener =
                            new PreLoadDownLoaderManager.DownLoadInfoAndListener(resourceDownLoadInfo,
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
                                            "1"),
                                    itemLiveId);

                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
                    documentNum.getAndIncrement();
                }
            }
            {
                //下载模板资源
                final String templateName = MD5.md5(coursewareInfo.getTemplateUrl()) + ".zip";
                File templateSave = new File(mMorecachein, templateName);
                if (!InfoUtils.fileIsExists(templateSave.getAbsolutePath())) {
//                templateSave.mkdirs();
                    DownLoadInfo templateDownLoadInfo = DownLoadInfo.createFileInfo(
                            ip + coursewareInfo.getTemplateUrl(),
                            mMorecachein.getAbsolutePath(),
                            templateName + ".temp",
                            coursewareInfo.getTemplateMd5());
//                    logger.d("template url path:  " + ip + coursewareInfo.getTemplateUrl() + "   file name:" + templateName + ".zip");
                    if (isIP) {
                        templateDownLoadInfo.setHost(cdn);
                    }
//                DownLoader templateDownLoader = new DownLoader(mContext, templateDownLoadInfo);
//                templateDownLoader.setDownloadThreadCount(mDownloadThreadCount);
//                templateDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, templateName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                            templateDownLoadInfo,
                            new ZipDownloadListener(
                                    mMorecachein,
                                    mMorecacheout,
                                    templateName,
                                    ips,
                                    cdns,
                                    coursewareInfo.getTemplateUrl(),
                                    coursewareInfo.getTemplateMd5(),
                                    new AtomicInteger(0),
                                    itemLiveId,
                                    "1"),
                            itemLiveId);
                    if (!isPrecise.get()) {
                        PreLoadDownLoaderManager.addToAutoDownloadPool(infoListener);
                    } else {
                        PreLoadDownLoaderManager.addUrgentInfo(infoListener);
                    }
                    documentNum.getAndIncrement();
                }
            }
            //英语智能测评预加载
            if (coursewareInfo.getIntelligentEntity() != null) {
                downLoadIntelligentResourse(coursewareInfo, mMorecachein, mMorecacheout, isIP, ip, cdn, ips, cdns, itemLiveId);
            }
        }
    }

    /**
     * 下载英语智能测评的资源
     *
     * @param coursewareInfo
     * @param cacheIn
     * @param cacheOut
     * @param isIP
     * @param ip
     * @param cdn
     * @param ips
     * @param cdns
     * @param itemLiveId
     */
    private void downLoadIntelligentResourse(
            CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo,
            File cacheIn, File cacheOut,
            boolean isIP, String ip, String cdn, List<String> ips, List<String> cdns, String itemLiveId) {
        {
            //与其他预加载不同，还需要使用sourceId作文文件路径
            File mMorecachein = new File(cacheIn, coursewareInfo.getSourceId());
            if (!mMorecachein.exists()) {
                mMorecachein.mkdirs();
            }
            File mMorecacheout = new File(cacheOut, coursewareInfo.getSourceId());
            if (!mMorecacheout.exists()) {
                mMorecacheout.mkdirs();
            }
            final String resourceName = coursewareInfo.getSourceId() + ".zip";

            File resourceSave = new File(mMorecachein, resourceName);
            if (!InfoUtils.fileIsExists(resourceSave.getAbsolutePath())) {
                logger.i("T_T" + ip + coursewareInfo.getIntelligentEntity().getResource());
                DownLoadInfo resourceDownLoadInfo = DownLoadInfo.createFileInfo(
                        ip + coursewareInfo.getIntelligentEntity().getResource(),
                        mMorecachein.getAbsolutePath(),
                        resourceName + ".temp",
                        null);

                if (isIP) {
                    resourceDownLoadInfo.setHost(cdn);
                }
                logger.d("courseware url path:  " + ip + coursewareInfo.getIntelligentEntity().getResource()
                        + "   file name:" + resourceName + ".zip");
                PreLoadDownLoaderManager.DownLoadInfoAndListener
                        infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(resourceDownLoadInfo,
                        new ZipDownloadListener(
                                mMorecachein,
                                mMorecacheout,
                                resourceName,
                                ips,
                                cdns,
                                coursewareInfo.getIntelligentEntity().getResource(),
                                null,
                                new AtomicInteger(0),
                                itemLiveId,
                                "1"),
                        itemLiveId);

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
    private void downloadResources(List<String> resourseInfos, List<String> cdns,
                                   final List<String> ips) {
        final File mPublicCacheout = new File(cacheFile, mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        logger.i("download common resources");
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
                final String fileName = getFileName(url);
//                int index = url.lastIndexOf("/");
//                if (index != -1) {
//                    fileName = url.substring(index + 1);
//                } else {
//                    fileName = MD5Utils.getMD5(url);
//                }
                final File save = new File(mPublicCacheout, fileName);
                if (!InfoUtils.fileIsExists(save.getAbsolutePath())) {
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
                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                            downLoadInfo,
                            new ZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    "",
                                    new AtomicInteger(0),
                                    "",
                                    "2"), "");
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
                if (!InfoUtils.fileIsExists(save.getPath())) {
                    logger.d("resource ttf url path:  " + ip + url + "   file name:" + fileName + ".nozip");
                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                            downLoadInfo,
                            new NoZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    "",
                                    new AtomicInteger(0),
                                    "3"),
                            "");
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

    static ThreadPoolExecutor executos = new ThreadPoolExecutor(1, 1,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    class ZipDownloadListener implements DownloadListener {

        public final File mMorecacheout;
        public final File mMorecachein;
        private String mFileName;
        private String url;
        private String md5;
        AtomicInteger downTryCount;
        List<String> cdns;
        List<String> ips;
        String itemLiveId;
        String resourcetype;

        private long startDownLoadTime;

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
            hashMap.put("isresume", "");
            hashMap.put("isresume", "false");
            hashMap.put("sno", "1");
            hashMap.put("liveid", itemLiveId);
            hashMap.put("resourcetype", resourcetype);
            hashMap.put("ip", IpAddressUtil.USER_IP);
            hashMap.put("freeSize", "" + getFreeSize());
            startDownLoadTime = System.currentTimeMillis();
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID,
                    LogConfig.PRE_LOAD_START, hashMap.getData());
        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        //日志系统
        @Override
        public void onSuccess(String folderPath, String fileName) {
            long downLoadTime = System.currentTimeMillis() - startDownLoadTime;
            StringBuilder sb = new StringBuilder();
            if (ips.size() > 0) {
                sb = new StringBuilder(ips.get(0));
            }
            for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                sb.append("," + ips.get(i) + url);
            }
            int successIpPos = downTryCount.get() % ipLength.get();
            String tempIP = "";
            if (successIpPos < ips.size()) {
                tempIP = ips.get(successIpPos);
            }
            boolean isIP;
            //拼接ip
            if (tempIP.contains("http") || tempIP.contains("https")) {
                isIP = false;
            } else {
                isIP = true;
            }
            InfoUtils.sendUms(LogConfig.PRE_LOAD_START,
                    "endPreload",
                    md5,
                    isIP ? "true" : "false",
                    url,
                    "",
                    String.valueOf(downLoadTime),
                    "2",
                    "true",
                    "",
                    resourcetype,
                    sb.toString(),
                    itemLiveId);
            decrementDocument();

            File tempFile = new File(folderPath, fileName);
            File file = new File(folderPath, mFileName);
            boolean rename = tempFile.renameTo(file);
//            if (!isUnZip.get()) {
            PreZipExtractorTask task = new PreZipExtractorTask(file, mMorecacheout, true, new InfoUtils.Progresses(), md5, itemLiveId, resourcetype);
            task.setProgressUpdate(false);
            task.executeOnExecutor(executos);
//                isUnZip.set(true);
//            }
        }

        @Override
        public void onFail(int errorCode) {
            logger.e("zip download error:" + errorCode);
            int oldPos = downTryCount.get() % ipLength.get();
            String oldIP = "";
            if (oldPos < ips.size()) {
                oldIP = ips.get(oldPos);
            }
            logger.i("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".zip path in:" + mMorecachein.getAbsolutePath() + " out:" + mMorecacheout.getAbsolutePath());
            downTryCount.getAndIncrement();
            int newPos = downTryCount.get() % ipLength.get();
            String tempIP = "";
            if (newPos < ips.size()) {
                tempIP = ips.get(newPos);
            }
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
                logger.i("onFail:ips.size()=" + ips.size() + " url:" + url);
                int failCdnPos = tryCount % cdnLength.get(), index = 0;
                if (failCdnPos < cdns.size()) {
                    index = cdns.get(failCdnPos).indexOf("/") + 2;
                }
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".zip");
                if (isIP) {
                    downLoadInfo.setHost(cdns.get(tryCount % cdnLength.get()).substring(index));
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
                ZipDownloadListener mZipDownloadListener = new ZipDownloadListener(
                        mMorecachein,
                        mMorecacheout,
                        mFileName,
                        ips,
                        cdns,
                        url,
                        md5,
                        downTryCount,
                        itemLiveId,
                        resourcetype);
                PreLoadDownLoaderManager.DownLoadInfoAndListener preLoadDownLoaderManager = new PreLoadDownLoaderManager.DownLoadInfoAndListener(downLoadInfo, mZipDownloadListener, itemLiveId);

                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(preLoadDownLoaderManager);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(preLoadDownLoaderManager);
                }
            } else {
                decrementDocument();
                StringBuilder sb = new StringBuilder();
                if (ips.size() > 0) {
                    sb = new StringBuilder(ips.get(0));
                }
                for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                    sb.append("," + ips.get(i) + url);
                }
                long downLoadTime = System.currentTimeMillis() - startDownLoadTime;
                InfoUtils.sendUms(LogConfig.PRE_LOAD_START,
                        "endPreload",
                        md5,
                        isIP ? "true" : "false",
                        "",
                        "",
                        String.valueOf(downLoadTime),
                        "2",
                        "false",
                        String.valueOf(errorCode),
                        resourcetype,
                        sb.toString(),
                        itemLiveId);

            }

        }

        @Override
        public void onFinish() {
//            logger.i("zip download finish");
        }
    }

    static class PreZipExtractorTask extends ZipExtractorTask {
        String md5;
        File mMorecacheout;
        String itemLiveId;
        String resourcetype;

        public PreZipExtractorTask(File in, File out, boolean replaceAll, ZipProg zipProg, String md5, String itemLiveId, String resourcetype) {
            super(in, out, replaceAll, zipProg);
            this.md5 = md5;
            this.mMorecacheout = out;
            this.itemLiveId = itemLiveId;
            this.resourcetype = resourcetype;
        }

        @Override
        protected Exception doInBackground(Void... params) {

            StableLogHashMap unZipMap = new StableLogHashMap();
            unZipMap.put("logtype", "startUnzip");
            unZipMap.put("preloadid", md5);
            if (mInput != null) {
                unZipMap.put("extrainfo", mInput.getAbsolutePath());
            } else {
                unZipMap.put("extrainfo", "null");
            }
            unZipMap.put("sno", "3");
            unZipMap.put("liveid", itemLiveId);
            unZipMap.put("resourcetype", resourcetype);
            unZipMap.put("ip", IpAddressUtil.USER_IP);
            unZipMap.put("freeSize", "" + getFreeSize());
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
            unZipMap.put("inputfile", "" + mInput);
            unZipMap.put("sno", "4");
            unZipMap.put("liveid", itemLiveId);
            unZipMap.put("resourcetype", resourcetype);
            unZipMap.put("ip", IpAddressUtil.USER_IP);
            if (exception != null && mInput != null) {
                boolean delete = mInput.delete();
                unZipMap.put("delete", "" + delete);
            }
            unZipMap.put("freeSize", "" + getFreeSize());
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, unZipMap.getData());
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
        long startDonwLoadTime;

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
            startDonwLoadTime = System.currentTimeMillis();
        }

        @Override
        public void onStart(String url) {
            if (!NbCourseWareConfig.RESOURSE_TYPE_NB.equals(resourcetype)) {
                StableLogHashMap hashMap = new StableLogHashMap();
//            hashMap.put("eventid", LogConfig.PRE_LOAD_START);
                hashMap.put("logtype", "startPreload");
                hashMap.put("preloadid", md5);
                hashMap.put("loadurl", url);
                hashMap.put("isresume", "false");
                hashMap.put("sno", "1");
                hashMap.put("liveid", "");
                hashMap.put("resourcetype", resourcetype);
                hashMap.put("ip", IpAddressUtil.USER_IP);
                hashMap.put("freeSize", "" + getFreeSize());
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID,
                        LogConfig.PRE_LOAD_START, hashMap.getData());
            }

        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download ttf success");

//            StableLogHashMap hashMap = new StableLogHashMap();
//            hashMap.put("logtype", "endPreload");
//            hashMap.put("preloadid", md5);
//            hashMap.put("loadurl", url);
//            hashMap.put("sno", "2");
//            hashMap.put("status", "true");
            StringBuilder sb = new StringBuilder();
            if (ips.size() > 0) {
                sb = new StringBuilder(ips.get(0));
            }
            for (int i = 1; i < downTryCount.get() && i < ips.size(); i++) {
                sb.append("," + ips.get(i));
            }

//            hashMap.put("failurl", downTryCount.get() != 0 ? sb.toString() : "");

//            hashMap.put("liveid", "");
//            hashMap.put("resourcetype", resourcetype);
//            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig
// .PRE_LOAD_START, hashMap.getData());

            if (!NbCourseWareConfig.RESOURSE_TYPE_NB.equals(resourcetype)) {


                long downLoadTime = System.currentTimeMillis() - startDonwLoadTime;
                int tempPos = downTryCount.get() % ipLength.get();
                String tempIP = "";
                if (tempPos < ips.size()) {
                    tempIP = ips.get(tempPos);
                }
                boolean isIP;
                //拼接ip
                if (tempIP.contains("http") || tempIP.contains("https")) {
                    isIP = false;
                } else {
                    isIP = true;
                }
                InfoUtils.sendUms(LogConfig.PRE_LOAD_START,
                        "endPreload",
                        md5,
                        isIP ? "true" : "false",
                        url,
                        "",
                        String.valueOf(downLoadTime),
                        "2",
                        "true",
                        "",
                        resourcetype,
                        downTryCount.get() != 0 ? sb.toString() : "",
                        "");
            }

            decrementDocument();

            File file = new File(folderPath, fileName);
            file.renameTo(new File(folderPath, mFileName));
        }

        @Override
        public void onFail(int errorCode) {
//            String ip = "http://" + ips.get((cdnPos.getAndIncrement()) % cdnLength.get());
            int oldPos = downTryCount.get() % ipLength.get();
            String oldIP = "";
            if (oldPos < ips.size()) {
                oldIP = ips.get(oldPos);
            }
            logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".nozip");
            downTryCount.getAndIncrement();
            int newPos = downTryCount.get() % ipLength.get();
            String tempIP = "";
            if (newPos < ips.size()) {
                tempIP = ips.get(newPos);
            }
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
                PreLoadDownLoaderManager.DownLoadInfoAndListener downLoadInfoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                        downLoadInfo,
                        new NoZipDownloadListener(
                                mMorecachein,
                                mMorecacheout,
                                mFileName,
                                ips,
                                cdns,
                                url,
                                md5,
                                downTryCount,
                                resourcetype),
                        "");
                if (!isPrecise.get()) {
                    PreLoadDownLoaderManager.addToAutoDownloadPool(downLoadInfoListener);
                } else {
                    PreLoadDownLoaderManager.addUrgentInfo(downLoadInfoListener);
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new NoZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
            } else {
                decrementDocument();
//                StableLogHashMap hashMap = new StableLogHashMap();
//                hashMap.put("logtype", "endPreload");
//                hashMap.put("preloadid", md5);
//                hashMap.put("loadurl", url);

//                hashMap.put("sno", "2");
//                hashMap.put("status", "false");
//                hashMap.put("errorcode", String.valueOf(errorCode));
                StringBuilder sb = new StringBuilder(ips.get(0));
                for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                    sb.append("," + ips.get(i));
                }
//                hashMap.put("failurl", sb.toString());
//                hashMap.put("liveid", "");
//                hashMap.put("resourcetype", resourcetype);
//                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());

                if (!NbCourseWareConfig.RESOURSE_TYPE_NB.equals(resourcetype)) {
                    long downLoadTime = System.currentTimeMillis() - startDonwLoadTime;
                    InfoUtils.sendUms(LogConfig.PRE_LOAD_START,
                            "endPreload",
                            md5,
                            isIP ? "true" : "false",
                            url,
                            "",
                            String.valueOf(downLoadTime),
                            "2",
                            "false",
                            String.valueOf(errorCode),
                            resourcetype,
                            sb.toString(),
                            "");
                }
            }
        }

        @Override
        public void onFinish() {
//            logger.i("no zip download finish");
        }

    }


    /**
     * 统计所有文件是否下载完
     */
    private void decrementDocument() {
        documentNum.getAndDecrement();
//        logger.i("remaining " + documentNum.get());
        //下载完成，则注销
        if (documentNum.get() == 0) {
            LiveAppBll.getInstance().unRegisterAppEvent(this);
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
