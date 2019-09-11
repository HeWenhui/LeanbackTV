package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.utils;

import android.app.DownloadManager;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreLoadDownLoaderManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.FileDownLoadManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.LiveVideoDownLoadUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;

import static com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.NewCourseWarePreload.mPublicCacheoutName;

public class CoursewareHelper {
    private String TAG = "CoursewareHelper";
    private Logger logger = LoggerFactory.getLogger(TAG);
    File cacheFile;

    public void handleCourseWare(List<CoursewareInfoEntity> courseWareInfos, File cacheFile) {
        this.cacheFile = cacheFile;
        execDownLoad(
                courseWareInfos,
                getMergeList(courseWareInfos, CDNS),
                getMergeList(courseWareInfos, IPS),
                getMergeList(courseWareInfos, RESOURCES),
                getMergeList(courseWareInfos, NB_PRELOAD));
    }

    private final static int CDNS = 1;
    private final static int IPS = 2;
    private final static int RESOURCES = 3;
    private final static int NB_PRELOAD = 4;

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

    private void execDownLoad(List<CoursewareInfoEntity> courseWareInfos, List<String> cdns, List<String> ips,
                              List<String> resources) {
        List<CoursewareInfoEntity.LiveCourseware> liveCoursewares = getSortArrays(courseWareInfos);
        //直播资源列表
        //cdns列表
        if (cdns == null || cdns.size() == 0) {
            return;
        }
        List<String> newIPs = new LinkedList<>();
        newIPs.addAll(cdns);
        newIPs.addAll(ips);

        logger.i("" + cdns.size() + " " + newIPs.size());
//        cdnLength.set(cdns.size());
//        ipLength.set(newIPs.size());

        downloadResources(resources, cdns, newIPs);

        exeDownLoadCourseware(liveCoursewares, cdns, newIPs);
        //下载Nb 预加载资源
        if (mNbCoursewareInfo != null) {
            downLoadNbResource(mNbCoursewareInfo, cdns, newIPs);
        }
    }

    /** 将所有的列表合并成为一个 */
    private List<String> getMergeList(List<CoursewareInfoEntity> coursewareInfoEntities, int type) {
        List<String> ansList = new LinkedList<>();
//        HashSet<String> ansSet = new HashSet<>();
        for (CoursewareInfoEntity coursewareInfoEntity : coursewareInfoEntities) {
            if (type == CDNS) {
                ansList = appendList(ansList, coursewareInfoEntity.getCdns());
            } else if (type == IPS) {
                ansList = appendList(ansList, coursewareInfoEntity.getIps());
            } else if (type == RESOURCES) {
                ansList = appendList(ansList, coursewareInfoEntity.getResources());
            } else if (type == NB_PRELOAD) {

            }
        }
        return ansList;
    }

    /**
     * 对数组进行排序
     *
     * @return
     */
    private List<CoursewareInfoEntity.LiveCourseware> getSortArrays(List<CoursewareInfoEntity> courseWareInfos) {
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

    public static class ResourcesDownFactory {
        public LiveVideoDownLoadUtils.LiveVideoDownLoadFile create(File cacheFile, List<String> resourseInfos, List<String> cdns,
                                                                   final List<String> ips) {
            final File mPublicCacheout = new File(cacheFile, mPublicCacheoutName);
            if (!mPublicCacheout.exists()) {
                mPublicCacheout.mkdirs();
            }
//            logger.i("download common resources");
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
            final String fileName;
            final File save;
            if (url.endsWith(".zip")) {
                int index = url.lastIndexOf("/");
                if (index != -1) {
                    fileName = url.substring(index + 1);
                } else {
                    fileName = MD5Utils.getMD5(url);
                }
                save = new File(mPublicCacheout, fileName);
                if (!fileIsExists(save.getAbsolutePath())) {
//                if (!fileIsExists(save.getAbsolutePath())) {
                    logger.d("resource zip url path:  " + ip + url + "   file name:" + fileName + ".zip");
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder =
                            new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder().
                                    setInFileName("fileName + \".temp\"").
                                    setUrl(ip + url).
                                    setInDirPath(mPublicCacheout.getAbsolutePath()).
                                    setDownloadListener(new ZipDownloadListener(
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
//                    DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(
//                            ip + url,
//                            mPublicCacheout.getAbsolutePath(),
//                            fileName + ".temp",
//                            "");
                    if (isIp) {
                        builder.setmHost(cdn);
                    }
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile liveVideoDownLoadFile = builder.build();
//                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener =
//                            new PreLoadDownLoaderManager.DownLoadInfoAndListener(
//                                    downLoadInfo,
//                                    new CoursewarePreload.ZipDownloadListener(
//                                            mPublicCacheout,
//                                            mPublicCacheout,
//                                            fileName,
//                                            ips,
//                                            cdns,
//                                            url,
//                                            fileName,
//                                            new AtomicInteger(0),
//                                            "",
//                                            "2"), "");
                    if (!isPrecise.get()) {
                        FileDownLoadManager.addToAutoDownloadPool(liveVideoDownLoadFile);
                    } else {
                        DownloadManager.addUrgentInfo(liveVideoDownLoadFile);
                    }
                    documentNum.getAndIncrement();
                }
            } else {
                if (url.endsWith(FZY3JW_TTF)) {
                    fileName = FZY3JW_TTF;
                } else {
                    fileName = MD5Utils.getMD5(url);
                }
                save = new File(mPublicCacheout, fileName);
                if (!fileIsExists(save.getPath())) {
                    logger.d("resource ttf url path:  " + ip + url + "   file name:" + fileName + ".nozip");
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile downLoadFile =
                            new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder().
                                    setUrl(ip + url).
                                    setInDirPath(mPublicCacheout.getAbsolutePath()).
                                    setInFileName(fileName + ".temp").build();
//                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile liveVideoDownLoadFile = builder.build();

//                    DownLoadInfo downLoadInfo =
//                            DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
//                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
//                            downLoadInfo,
//                            new CoursewarePreload.NoZipDownloadListener(
//                                    mPublicCacheout,
//                                    mPublicCacheout,
//                                    fileName,
//                                    ips,
//                                    cdns,
//                                    url,
//                                    fileName,
//                                    new AtomicInteger(0),
//                                    "3"),
//                            "");
                    if (!isPrecise.get()) {
                        FileDownLoadManager.addToAutoDownloadPool(downLoadFile);
                    } else {
                        FileDownLoadManager.addUrgentInfo(infoListener);
                    }
                    documentNum.getAndIncrement();
                }
            }
        }
    }

    public File getStorageFile() {

    }


    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
//        logger.i(strFile);
        try {
            File f = new File(strFile);
//            logger.i(strFile + "" + f.getName() + " " + f.isFile() + " " + f.exists());
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }

        return true;
    }

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
            StringBuilder sb = new StringBuilder(ips.get(0));
            for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                sb.append("," + ips.get(i) + url);
            }

            String tempIP = ips.get(downTryCount.get() % ipLength.get());
            boolean isIP;
            //拼接ip
            if (tempIP.contains("http") || tempIP.contains("https")) {
                isIP = false;
            } else {
                isIP = true;
            }
            sendUms(LogConfig.PRE_LOAD_START,
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
            CoursewarePreload.PreZipExtractorTask task = new CoursewarePreload.PreZipExtractorTask(file, mMorecacheout, true, new CoursewarePreload.Progresses(), md5, itemLiveId, resourcetype);
            task.setProgressUpdate(false);
            task.executeOnExecutor(executos);
//                isUnZip.set(true);
//            }
        }

        @Override
        public void onFail(int errorCode) {
            String oldIP = ips.get(downTryCount.get() % ipLength.get());
            logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".zip path in:" + mMorecachein.getAbsolutePath() + " out:" + mMorecacheout.getAbsolutePath());
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
                logger.i("onFail:ips=" + ips.size() + "");
                int index = cdns.get(tryCount % cdnLength.get()).indexOf("/") + 2;
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".zip");
                if (isIP) {
                    downLoadInfo.setHost(cdns.get(tryCount % cdnLength.get()).substring(index));
                }
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
                CoursewarePreload.ZipDownloadListener mZipDownloadListener = new CoursewarePreload.ZipDownloadListener(
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
                StringBuilder sb = new StringBuilder(ips.get(0));
                for (int i = 0; i < downTryCount.get() && i < ips.size(); i++) {
                    sb.append("," + ips.get(i) + url);
                }
                long downLoadTime = System.currentTimeMillis() - startDownLoadTime;
                sendUms(LogConfig.PRE_LOAD_START,
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
            StringBuilder sb = new StringBuilder(ips.get(0));
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
                String tempIP = ips.get(downTryCount.get() % ipLength.get());
                boolean isIP;
                //拼接ip
                if (tempIP.contains("http") || tempIP.contains("https")) {
                    isIP = false;
                } else {
                    isIP = true;
                }
                sendUms(LogConfig.PRE_LOAD_START,
                        "startPreload",
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
                PreLoadDownLoaderManager.DownLoadInfoAndListener downLoadInfoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                        downLoadInfo,
                        new CoursewarePreload.NoZipDownloadListener(
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
                    sendUms(LogConfig.PRE_LOAD_START,
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

}
