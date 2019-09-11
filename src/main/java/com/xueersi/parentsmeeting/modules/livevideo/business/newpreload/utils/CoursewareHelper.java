package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.utils;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreLoadDownLoaderManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreloadStaticStorage;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.FileDownLoadManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.LiveVideoDownLoadUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.listener.NoZipDownloadListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.listener.ZipDownloadListener;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;

import org.xutils.xutils.common.util.MD5;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;

import static com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.CoursewareConstants.FZY3JW_TTF;
import static com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.NewCourseWarePreload.mPublicCacheoutName;

public class CoursewareHelper {
    private String TAG = "CoursewareHelper";
    private Logger logger = LoggerFactory.getLogger(TAG);
    File cacheFile;
    boolean isPrecise;

    /**
     * 所有需要下载文件的总量
     */
    private AtomicInteger documentNum;

    public void handleCourseWare(List<CoursewareInfoEntity> courseWareInfos, File cacheFile, boolean isPrecise, AtomicInteger documentNum) {
        this.cacheFile = cacheFile;
        this.isPrecise = isPrecise;
        this.documentNum = documentNum;
        execDownLoad(
                courseWareInfos,
                getMergeList(courseWareInfos, CDNS),
                getMergeList(courseWareInfos, IPS),
                getMergeList(courseWareInfos, RESOURCES));
    }

    private final static int CDNS = 1;
    private final static int IPS = 2;
    private final static int RESOURCES = 3;
    private final static int NB_ADD_PRELOAD = 4;
    private final static int NB_FREE_PRELOAD = 5;

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

    static ThreadPoolExecutor executos = new ThreadPoolExecutor(1, 1,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

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
            }
        }
        return ansList;
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
                    List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos = liveCourseware.getCoursewareInfos();
                    File todayLiveCacheDir = new File(todayCacheDir, liveCourseware.getLiveId());
                    boolean exists = todayLiveCacheDir.exists();
                    boolean mkdirs = false;
                    if (!todayLiveCacheDir.exists()) {
                        mkdirs = todayLiveCacheDir.mkdirs();
                    }
//            logger.d("exeDownLoadCourseware:exists=" + exists + ",mkdirs=" + mkdirs);
                    String itemLiveId = liveCourseware.getLiveId();
                    downloadCourseware(todayLiveCacheDir, coursewareInfos, ips, cdns, itemLiveId);
                }
            });
        }
        logger.d("exeDownLoadCourseware:size=" + liveCoursewares.size() + ",time=" + (System.currentTimeMillis() - before));
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
        for (CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo : coursewareInfos) {
            {
                //下载课件资源
                final String resourceName = MD5.md5(coursewareInfo.getResourceUrl()) + ".zip";
                File resourceSave = new File(mMorecachein, resourceName);
                boolean equals = false;
                if (fileIsExists(resourceSave.getAbsolutePath())) {
                    String filemd5 = FileUtils.getFileMD5ToString(resourceSave);
                    equals = coursewareInfo.getResourceMd5().equalsIgnoreCase(filemd5);
                }
                if (!fileIsExists(resourceSave.getAbsolutePath()) || (fileIsExists(resourceSave.getAbsolutePath()) && !equals)) {
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder = new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder();
                    builder.setUrl(ip + coursewareInfo.getResourceUrl())
                            .setMd5(coursewareInfo.getResourceMd5())
                            .setInFileName(resourceName + ".temp")
                            .setInDirPath(mMorecachein.getAbsolutePath());
//                    DownLoadInfo resourceDownLoadInfo = DownLoadInfo.createFileInfo(
//                            ip + coursewareInfo.getResourceUrl(),
//                            mMorecachein.getAbsolutePath(),
//                            resourceName + ".temp",
//                            coursewareInfo.getResourceMd5());

                    if (isIP) {
                        builder.setmHost(cdn);
//                        resourceDownLoadInfo.setHost(cdn);
                    }
                    builder.setDownloadListener(new ZipDownloadListener(mMorecachein,
                            mMorecacheout,
                            resourceName,
                            ips,
                            cdns,
                            coursewareInfo.getResourceUrl(),
                            coursewareInfo.getResourceMd5(),
                            new AtomicInteger(0),
                            itemLiveId,
                            "1", isPrecise, executos) {
                        @Override
                        protected void decrementDocument() {
                            CoursewareHelper.this.decrementDocument();
                        }
                    });
//                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(resourceDownLoadInfo,
//                            new CoursewarePreload.ZipDownloadListener(
//                                    mMorecachein,
//                                    mMorecacheout,
//                                    resourceName,
//                                    ips,
//                                    cdns,
//                                    coursewareInfo.getResourceUrl(),
//                                    coursewareInfo.getResourceMd5(),
//                                    new AtomicInteger(0),
//                                    itemLiveId,
//                                    "1"),
//                            itemLiveId);

                    if (!isPrecise) {
                        FileDownLoadManager.addToAutoDownloadPool(builder.build());
                    } else {
                        FileDownLoadManager.addUrgentInfo(builder.build());
                    }
                    documentNum.getAndIncrement();
                }
            }
            {
                //下载模板资源
                final String templateName = MD5.md5(coursewareInfo.getTemplateUrl()) + ".zip";
                File templateSave = new File(mMorecachein, templateName);
                if (!fileIsExists(templateSave.getAbsolutePath())) {
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder = new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder();
                    builder.setUrl(ip + coursewareInfo.getTemplateUrl())
                            .setInDirPath(mMorecachein.getAbsolutePath())
                            .setInFileName(templateName + ".temp")
                            .setMd5(coursewareInfo.getTemplateMd5());
//                    DownLoadInfo templateDownLoadInfo = DownLoadInfo.createFileInfo(
//                            ip + coursewareInfo.getTemplateUrl(),
//                            mMorecachein.getAbsolutePath(),
//                            templateName + ".temp",
//                            coursewareInfo.getTemplateMd5());
//                    logger.d("template url path:  " + ip + coursewareInfo.getTemplateUrl() + "   file name:" + templateName + ".zip");
                    if (isIP) {
                        builder.setmHost(cdn);
//                        templateDownLoadInfo.setHost(cdn);
                    }
//                DownLoader templateDownLoader = new DownLoader(mContext, templateDownLoadInfo);
//                templateDownLoader.setDownloadThreadCount(mDownloadThreadCount);
//                templateDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, templateName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                    PreLoadDownLoaderManager.DownLoadInfoAndListener infoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
                            templateDownLoadInfo,
                            new CoursewarePreload.ZipDownloadListener(
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
            return null;
        }
    }

    private void decrementDocument() {

        documentNum.getAndDecrement();
//        logger.i("remaining " + documentNum.get());
        //下载完成，则注销
        if (documentNum.get() == 0) {
            LiveAppBll.getInstance().unRegisterAppEvent(this);
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
                            new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder();
                    builder.setInFileName("fileName + \".temp\"")
                            .setUrl(ip + url).
                            setInDirPath(mPublicCacheout.getAbsolutePath())
                            .setDownloadListener(new ZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    fileName,
                                    new AtomicInteger(0),
                                    "",
                                    "2", isPrecise, executos) {
                                @Override
                                protected void decrementDocument() {
                                    CoursewareHelper.this.decrementDocument();
                                }
                            });
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
                    if (!isPrecise) {
                        FileDownLoadManager.addToAutoDownloadPool(liveVideoDownLoadFile);
                    } else {
                        FileDownLoadManager.addUrgentInfo(liveVideoDownLoadFile);
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
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder =
                            new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder();
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile
                            downLoadFile = builder.setUrl(ip + url)
                            .setInDirPath(mPublicCacheout.getAbsolutePath())
                            .setInFileName(fileName + ".temp")
                            .setDownloadListener(new NoZipDownloadListener(
                                    mPublicCacheout,
                                    mPublicCacheout,
                                    fileName,
                                    ips,
                                    cdns,
                                    url,
                                    fileName,
                                    new AtomicInteger(0),
                                    "3", isPrecise) {
                                @Override
                                protected void decrementDocument() {
                                    CoursewareHelper.this.decrementDocument();
                                }
                            }).build();
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
                    if (!isPrecise) {
                        FileDownLoadManager.addToAutoDownloadPool(downLoadFile);
                    } else {
                        FileDownLoadManager.addUrgentInfo(downLoadFile);
                    }
                    documentNum.getAndIncrement();
                }
            }
        }

    }

//    public File getStorageFile() {
//
//    }


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

}
