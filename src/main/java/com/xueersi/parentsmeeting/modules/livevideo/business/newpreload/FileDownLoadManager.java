package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload;

import android.text.TextUtils;

import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownLoader;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.common.network.download.DownloadPool;
import com.xueersi.lib.framework.are.RunningEnvironment;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.LiveVideoDownLoadUtils.LiveVideoDownLoadFile;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileDownLoadManager {

    private static Logger logger = LoggerFactory.getLogger("PreLoadDownLoaderManager");
    /**
     * 当前正在下载的文件url
     */
//    private static String sDownUrl = null;
    /**
     * 自动下载池
     */
    private static Map<String, LiveVideoDownLoadFile> sAutoDownloaderPool =
            new ConcurrentHashMap<>();

    private static AtomicBoolean isUrgent = new AtomicBoolean(false);
    private static List<LiveVideoDownLoadFile> downLoadInfoListeners = new CopyOnWriteArrayList<>();

    /**
     * 同步锁
     */
//    private static Object sLockObject = new Object();

//    public static class DownLoadInfoAndListener {
//        private DownLoadInfo downLoadInfo;
//        private DownloadListener listener;
//        private String liveId;
//
//        public DownLoadInfoAndListener(DownLoadInfo downLoadInfo, DownloadListener listener, String liveId) {
//            this.downLoadInfo = downLoadInfo;
//            this.listener = listener;
//            this.liveId = liveId;
//        }
//
//        public DownLoadInfo getDownLoadInfo() {
//            return downLoadInfo;
//        }
//
//        public DownloadListener getListener() {
//            return listener;
//        }
//
//        public String getLiveId() {
//            return liveId;
//        }
//    }
    public static void addUrgentInfo(LiveVideoDownLoadFile downLoadInfo) {
        if (!isUrgent.get()) {
            isUrgent.set(true);
        }

        downLoadInfoListeners.add(downLoadInfo);
        startAutoDownload();
    }

    private static Semaphore semaphore = new Semaphore(1);

    /**
     * 启动后台自动下载-Wifi连接时调用
     */
    public static void startAutoDownload() {

        //非Wi-Fi 不能自动下载
        if (!NetWorkHelper.isWifiDataEnable(RunningEnvironment.sAppContext)) {
            logger.i("非wifi环境，不能下载");
//            sDownUrl = null;
            return;
        }
        if (!semaphore.tryAcquire()) {
            return;
        }
//        synchronized (sLockObject) {
        // 如果有已经在下载的任务则不下载
//            if (!TextUtils.isEmpty(sDownUrl)) {
//                logger.i("如果有已经在下载的任务则不下载");
//                return;
//            }
        LiveVideoDownLoadFile liveVideoDownLoadFile;
        DownLoadInfo info;
//            DownloadListener realDownLoadListener;
        if (isUrgent.get() && downLoadInfoListeners.size() > 0) {
            liveVideoDownLoadFile = downLoadInfoListeners.get(0);
            info = factory.create(liveVideoDownLoadFile);
//                realDownLoadListener = downLoadInfoAndListener.getDownloadListener();
            downLoadInfoListeners.remove(0);
        } else {
            Iterator<Map.Entry<String, LiveVideoDownLoadFile>> iterator = sAutoDownloaderPool.entrySet().iterator();
            if (!iterator.hasNext()) {
                logger.i("no next iterator Thread:" + Thread.currentThread().getName());
                return;
            }
            Map.Entry<String, LiveVideoDownLoadFile> entry = iterator.next();
            liveVideoDownLoadFile = entry.getValue();
            info = factory.create(liveVideoDownLoadFile);
//                realDownLoadListener = downLoadInfoAndListener.getDownloadListener();
        }

        if (info == null || TextUtils.isEmpty(info.getUrl())) {
            logger.i("info or url is null");
            return;
        }

        if (DownLoadInfo.DownloadType.FILE.equals(info.getDownloadType())) {
            // 下载文件
//                CoursewarePreload.ZipDownloadListener zipDownloadListener = (CoursewarePreload.ZipDownloadListener) realDownLoadListener;
//                logger.i("in:" + zipDownloadListener.mMorecachein.getAbsolutePath() + " out:" + zipDownloadListener.mMorecacheout.getAbsolutePath());
//                if (zipDownloadListener.mMorecachein.getAbsolutePath().equals(debugString)) {
//                    logger.i(debugLog);
//                }
            DownLoadListenerProxy downloadListener = new DownLoadListenerProxy(liveVideoDownLoadFile);
            DownloadPool.getDownLoader(info).start(downloadListener);
        } else if (DownLoadInfo.DownloadType.IMG.equals(info.getDownloadType())) {
            // 下载图片
            // @Fixme
            //sImageManager.loadImage(info.getUrl(), null);
//            sDownUrl = null;
            removeDownloaderFromPool(liveVideoDownLoadFile.onlyKey);
            startAutoDownload();
        }
    }
//    }

    public static DownLoadListenerFactory listenerFactory = new DownLoadListenerFactory();

    public static class DownLoadListenerFactory implements DownLoadEntityUtils.Factory<LiveVideoDownLoadFile, DownloadListener> {

        @Override
        public DownloadListener create(LiveVideoDownLoadFile file) {
            return file.getDownloadListener();
        }
    }

    public static class DownLoadListenerProxy implements DownloadListener {

        private LiveVideoDownLoadFile liveVideoDownLoadFile;

        private DownloadListener realDownLoadListener;

        public DownLoadListenerProxy(LiveVideoDownLoadFile liveVideoDownLoadFile) {

            this.liveVideoDownLoadFile = liveVideoDownLoadFile;
            this.realDownLoadListener = liveVideoDownLoadFile.getDownloadListener();
        }

        @Override
        public void onStart(String url) {
//            synchronized (sLockObject) {
//                sDownUrl = url;
//            }
            if (realDownLoadListener != null) {
                realDownLoadListener.onStart(url);

            }
        }

        /**
         * 下载完成
         */
        @Override
        public void onFinish() {
            //从下载队列中移除
            removeDownloaderFromPool(liveVideoDownLoadFile.onlyKey);
//            synchronized (sLockObject) {
//                sDownUrl = null;
//            }
            semaphore.release();
//            CoursewarePreload.ZipDownloadListener zipDownloadListener = (CoursewarePreload.ZipDownloadListener) realDownLoadListener;
//            if (zipDownloadListener.mMorecachein.getAbsolutePath().equals(debugString)) {
//                logger.i(debugLog);
//            }
            if (realDownLoadListener != null) {
                realDownLoadListener.onFinish();
            }
//                        logger.i("next auto download");
            startAutoDownload();
        }

        /**
         *
         */
        @Override
        public void onSuccess(String folderPath, String fileName) {
            if (realDownLoadListener != null) {
                realDownLoadListener.onSuccess(folderPath, fileName);
            }
        }

        /**
         *
         */
        @Override
        public void onFail(int errorCode) {

//            CoursewarePreload.ZipDownloadListener zipDownloadListener = (CoursewarePreload.ZipDownloadListener) realDownLoadListener;
//            if (zipDownloadListener.mMorecachein.getAbsolutePath().equals(debugString)) {
//                logger.i(debugLog);
//            }
            logger.i(errorCode + "");
            if (realDownLoadListener != null) {
                realDownLoadListener.onFail(errorCode);
            }
        }

        @Override
        public void onProgressChange(long currentLength,
                                     long fileLength) {
            if (realDownLoadListener != null) {
//                            logger.i(currentLength + " " + fileLength);
                realDownLoadListener.onProgressChange(currentLength, fileLength);
            }
        }
    }

    /**
     * 添加到自动下载池，并启动下载
     */
//    public static void addToAutoDownloadPool(final DownLoadInfoAndListener info) {
//        if (info == null || TextUtils.isEmpty(info.getDownLoadInfo().getUrl())) {
//            return;
//        }
//        // 下载的是文件
//        if (DownLoadInfo.DownloadType.FILE.equals(info.getDownLoadInfo().getDownloadType())) {
//            // 文件名或文件夹为空，返回
//            if (TextUtils.isEmpty(info.getDownLoadInfo().getFolder())
//                    || TextUtils.isEmpty(info.getDownLoadInfo().getFileName())) {
//                return;
//            }
//            // 将下载器添加到下载池
//            DownloadPool.addDownloader(info.getDownLoadInfo().getUrl() + info.liveId, new DownLoader(info.getDownLoadInfo()));
//        }
//        addDownloaderToPool(info.getDownLoadInfo().getUrl() + info.liveId, info);
//        if (info.listener instanceof CoursewarePreload.ZipDownloadListener &&
//                ((CoursewarePreload.ZipDownloadListener) info.listener).mMorecachein.getAbsolutePath().equals(debugString)) {
//            logger.i("url:" + info.getDownLoadInfo().getUrl() + " " + debugLog);
//        }
//        startAutoDownload();
//    }

    private static class DownLoadInfoFactory implements DownLoadEntityUtils.Factory<LiveVideoDownLoadFile, DownLoadInfo> {

        @Override
        public DownLoadInfo create(LiveVideoDownLoadFile entity) {
            DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(entity.url, entity.inDirPath, entity.getInFileName(), entity.md5);
            return downLoadInfo;
        }
    }

    private static DownLoadInfoFactory factory = new DownLoadInfoFactory();

    public synchronized static void addToAutoDownloadPool(LiveVideoDownLoadFile info) {
        if (info == null || TextUtils.isEmpty(info.getUrl())) {
            return;
        }
        // 下载的是文件
        if (DownLoadInfo.DownloadType.FILE.equals(info.getmDownloadType())) {
            // 文件名或文件夹为空，返回
            DownLoadInfo downLoadInfo = factory.create(info);
            if (TextUtils.isEmpty(downLoadInfo.getFolder())
                    || TextUtils.isEmpty(downLoadInfo.getFileName())) {
                return;
            }
            // 将下载器添加到下载池
            DownloadPool.addDownloader(info.onlyKey, new DownLoader(downLoadInfo));
        }
        addDownloaderToPool(info.onlyKey, info);
//        if (info.listener instanceof CoursewarePreload.ZipDownloadListener &&
//                ((CoursewarePreload.ZipDownloadListener) info.listener).mMorecachein.getAbsolutePath().equals(debugString)) {
//            logger.i("url:" + info.getDownLoadInfo().getUrl() + " " + debugLog);
//        }
        startAutoDownload();
    }

    /**
     * 从自动下载池中删除任务
     */
    private static void removeDownloaderFromPool(String key) {
//        synchronized (sLockObject) {
        if (sAutoDownloaderPool.containsKey(key)) {
            sAutoDownloaderPool.remove(key);
        }
//        }
    }

    /**
     * 添加任务到自动下载池
     */
    private static void addDownloaderToPool(String key,
                                            LiveVideoDownLoadFile loadFile) {
//        synchronized (sLockObject) {
//            if (downLoadInfo.listener instanceof CoursewarePreload.ZipDownloadListener &&
//                    ((CoursewarePreload.ZipDownloadListener) downLoadInfo.listener).mMorecachein.getAbsolutePath().equals(debugString)) {
//                logger.i("key:" + key + " " + debugLog);
//            }
        if (!sAutoDownloaderPool.containsKey(key)) {
            sAutoDownloaderPool.put(key, loadFile);
//            }
        }
    }
}
