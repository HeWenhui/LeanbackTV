package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.listener;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.FileDownLoadManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.LiveVideoDownLoadUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.utils.LogHelper.sendUms;

public abstract class ZipDownloadListener extends CommonDonwLoadListener {

//    private CoursewareHelper coursewareHelper;
//    public final File mMorecacheout;
//    public final File mMorecachein;

    Executor executor;

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
            String resourcetype,
            boolean isPrecise,
            Executor executor) {
//        this.coursewareHelper = coursewareHelper;
        super(mMorecachein, mMorecacheout, fileName, ips, cdns, url, md5,
                downTryCount, itemLiveId, resourcetype, isPrecise);
        this.executor = executor;
//        this.mMorecachein = mMorecachein;
//        this.mMorecacheout = mMorecacheout;
//        this.mFileName = fileName;
//        this.url = url;
//        this.md5 = md5;
//        this.ips = ips;
//        this.cdns = cdns;
//        this.downTryCount = downTryCount;
//        this.itemLiveId = itemLiveId;
//        this.resourcetype = resourcetype;
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

        String tempIP = ips.get(downTryCount.get() % ips.size());
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
        PreZipExtractorTask task = new PreZipExtractorTask(file, mMorecacheout, true,
                new Progresses(), md5, itemLiveId, resourcetype);
        task.setProgressUpdate(false);
        task.executeOnExecutor(executor);
//                isUnZip.set(true);
//            }
    }

    @Override
    public void onFail(int errorCode) {
        String oldIP = ips.get(downTryCount.get() % ips.size());
        logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".zip path in:" + mMorecachein.getAbsolutePath() + " out:" + mMorecacheout.getAbsolutePath());
        downTryCount.getAndIncrement();
        String tempIP = ips.get(downTryCount.get() % ips.size());
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
            int index = cdns.get(tryCount % cdns.size()).indexOf("/") + 2;
            LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder =
                    new LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder();
            builder.setUrl(ip + url)
                    .setInFilePath(mMorecachein.getAbsolutePath())
                    .setInFileName(mFileName + ".temp")
                    .setMd5(md5);
//            DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
            logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".zip");
            if (isIP) {
                builder.setmHost(cdns.get(tryCount % cdns.size()).substring(index));
//                downLoadInfo.setHost(cdns.get(tryCount % cdns.size()).substring(index));
            }
            LiveVideoDownLoadUtils.LiveVideoDownLoadFile downLoadFile = builder.build();
//                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                downLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
//            CoursewarePreload.ZipDownloadListener mZipDownloadListener = new CoursewarePreload.ZipDownloadListener(
//                    mMorecachein,
//                    mMorecacheout,
//                    mFileName,
//                    ips,
//                    cdns,
//                    url,
//                    md5,
//                    downTryCount,
//                    itemLiveId,
//                    resourcetype);
//            PreLoadDownLoaderManager.DownLoadInfoAndListener preLoadDownLoaderManager = new PreLoadDownLoaderManager.DownLoadInfoAndListener(downLoadInfo, mZipDownloadListener, itemLiveId);

            if (!isPrecise) {
                FileDownLoadManager.addToAutoDownloadPool(downLoadFile);
            } else {
                FileDownLoadManager.addUrgentInfo(downLoadFile);
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
