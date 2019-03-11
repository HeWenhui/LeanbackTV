package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.text.TextUtils;

import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownLoader;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.common.network.download.DownloadPool;
import com.xueersi.lib.framework.are.RunningEnvironment;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreLoadDownLoaderManager {
    private static Logger logger = LoggerFactory.getLogger("PreLoadDownLoaderManager");
    /**
     * 当前正在下载的文件url
     */
    private static String sDownUrl = null;
    /**
     * 自动下载池
     */
    private static Map<String, DownLoadInfoListener> sAutoDownloaderPool =
            new HashMap<>();

    private static AtomicBoolean isUrgent = new AtomicBoolean(false);
    private static List<DownLoadInfoListener> downLoadInfoListeners = new CopyOnWriteArrayList<>();
    /**
     * 同步锁
     */
    private static Object sLockObject = new Object();

    public static class DownLoadInfoListener {
        private DownLoadInfo downLoadInfo;
        private DownloadListener listener;

        public DownLoadInfoListener(DownLoadInfo downLoadInfo, DownloadListener listener) {
            this.downLoadInfo = downLoadInfo;
            this.listener = listener;
        }

        public DownLoadInfo getDownLoadInfo() {
            return downLoadInfo;
        }

        public void setDownLoadInfo(DownLoadInfo downLoadInfo) {
            this.downLoadInfo = downLoadInfo;
        }

        public DownloadListener getListener() {
            return listener;
        }

        public void setListener(DownloadListener listener) {
            this.listener = listener;
        }
    }

    public static void addUrgentInfo(DownLoadInfoListener downLoadInfo) {
        if (!isUrgent.get()) {
            isUrgent.set(true);
        }

        downLoadInfoListeners.add(downLoadInfo);
        startAutoDownload();
    }

    /**
     * 启动后台自动下载-Wifi连接时调用
     */
    public static void startAutoDownload() {

        //非Wi-Fi 不能自动下载
        if (!NetWorkHelper.isWifiDataEnable(RunningEnvironment.sAppContext)) {
            logger.i("非wifi环境，不能下载");
            sDownUrl = null;
            return;
        }
        synchronized (sLockObject) {


            // 如果有已经在下载的任务则不下载
            if (!TextUtils.isEmpty(sDownUrl)) {
//                logger.i("如果有已经在下载的任务则不下载");
                return;
            }
            DownLoadInfo info;
            final DownloadListener realDownLoadListener;
            if (isUrgent.get() && downLoadInfoListeners.size() > 0) {
                info = downLoadInfoListeners.get(0).getDownLoadInfo();
                realDownLoadListener = downLoadInfoListeners.get(0).getListener();
                downLoadInfoListeners.remove(0);
            } else {
                Iterator<Map.Entry<String, DownLoadInfoListener>> iterator = sAutoDownloaderPool.entrySet().iterator();
                if (!iterator.hasNext()) {
                    logger.i("no next iterator");
                    return;
                }
                Map.Entry<String, DownLoadInfoListener> entry = iterator.next();
                DownLoadInfoListener downLoadInfoListener = entry.getValue();
                info = downLoadInfoListener.getDownLoadInfo();
                realDownLoadListener = downLoadInfoListener.getListener();
            }

            if (info == null || TextUtils.isEmpty(info.getUrl())) {
                logger.i("info or url is null");
                return;
            }

            if (DownLoadInfo.DownloadType.FILE.equals(info.getDownloadType())) {
                // 下载文件
                DownloadListener downloadListener = new DownloadListener() {

                    @Override
                    public void onStart(String url) {
                        synchronized (sLockObject) {
                            sDownUrl = url;
                        }
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
                        removeDownloaderFromPool(sDownUrl);
                        synchronized (sLockObject) {
                            sDownUrl = null;
                        }
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
                };
                DownloadPool.getDownLoader(info).start(downloadListener);
            } else if (DownLoadInfo.DownloadType.IMG.equals(info.getDownloadType())) {
                // 下载图片
                // @Fixme
                //sImageManager.loadImage(info.getUrl(), null);
                sDownUrl = null;
                removeDownloaderFromPool(info.getUrl());
                startAutoDownload();
            }
        }
    }

    /**
     * 添加到自动下载池，并启动下载
     */
    public static void addToAutoDownloadPool(final DownLoadInfoListener info) {
        if (info == null || TextUtils.isEmpty(info.getDownLoadInfo().getUrl())) {
            return;
        }
        // 下载的是文件
        if (DownLoadInfo.DownloadType.FILE.equals(info.getDownLoadInfo().getDownloadType())) {
            // 文件名或文件夹为空，返回
            if (TextUtils.isEmpty(info.getDownLoadInfo().getFolder())
                    || TextUtils.isEmpty(info.getDownLoadInfo().getFileName())) {
                return;
            }
            // 将下载器添加到下载池
            DownloadPool.addDownloader(info.getDownLoadInfo().getUrl(), new DownLoader(info.getDownLoadInfo()));
        }
        addDownloaderToPool(info.getDownLoadInfo().getUrl(), info);
        startAutoDownload();
    }

//    private static DownloadListener listener;

//    public static void setDownLoadListener(DownloadListener listener) {
//        PreLoadDownLoaderManager.listener = listener;
//    }

//    /**
//     * 添加到自动下载池，并启动下载
//     */
//    public static void addToAutoDownloadPool(final DownLoadInfoListener info) {
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
//            DownloadPool.addDownloader(info.getDownLoadInfo().getUrl(), new DownLoader(info.getDownLoadInfo()));
//        }
//        addDownloaderToPool(info.getDownLoadInfo().getUrl(), info);
//        startAutoDownload();
//    }


    /**
     * 从自动下载池中删除任务
     */
    private static void removeDownloaderFromPool(String key) {
        synchronized (sLockObject) {
            if (sAutoDownloaderPool.containsKey(key)) {
                sAutoDownloaderPool.remove(key);
            }
        }
    }

    /**
     * 添加任务到自动下载池
     */
    private static void addDownloaderToPool(String key,
                                           DownLoadInfoListener downLoadInfo) {
        synchronized (sLockObject) {
            if (!sAutoDownloaderPool.containsKey(key)) {
                sAutoDownloaderPool.put(key, downLoadInfo);
            }
        }
    }

    public interface SuccessCallBack {
        void onSuccess();

    }

    private SuccessCallBack callBack;

    public void setCallBack(SuccessCallBack callBack) {
        this.callBack = callBack;
    }
}
