package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.listener;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.FileDownLoadManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.LiveVideoDownLoadUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.utils.LogHelper.sendUms;

public abstract class NoZipDownloadListener extends CommonDonwLoadListener {
    //    private CoursewareHelper coursewareHelper;

    public NoZipDownloadListener(
            File mMorecachein,
            File mMorecacheout,
            String fileName,
            final List<String> ips,
            List<String> cdns,
            String url,
            String md5,
            AtomicInteger downTryCount,
            String resourcetype,
            boolean isPrecise) {
//        this.mMorecachein = mMorecachein;
//        this.mMorecacheout = mMorecacheout;
//        this.mFileName = fileName;
//        this.url = url;
//        this.md5 = md5;
//        this.ips = ips;
//        this.cdns = cdns;
//        this.downTryCount = downTryCount;
//        this.resourcetype = resourcetype;
//        this.isPrecise = isPrecise;
//        startDownLoadTime = System.currentTimeMillis();
        super(mMorecachein, mMorecacheout, fileName, ips, cdns, url, md5,
                downTryCount, "", resourcetype, isPrecise);
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
            hashMap.put("freeSize", "" + CoursewarePreload.getFreeSize());
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


            long downLoadTime = System.currentTimeMillis() - startDownLoadTime;
            String tempIP = ips.get(downTryCount.get() % ips.size());
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
        String oldIP = ips.get(downTryCount.get() % ips.size());
        logger.d("fail url path:  " + oldIP + url + "   file name:" + mFileName + ".nozip");
        downTryCount.getAndIncrement();
        String tempIP = ips.get(downTryCount.get() % ips.size());
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
            int index = cdns.get(tryCount % cdns.size()).indexOf("/") + 2;
            LiveVideoDownLoadUtils.LiveVideoDownLoadFile.Builder builder = new
                    LiveVideoDownLoadUtils
                            .LiveVideoDownLoadFile
                            .Builder()
                    .setUrl(ip + url)
                    .setInDirPath(mMorecachein.getAbsolutePath())
                    .setMd5(md5)
                    .setDownloadListener(this)
                    .setInFileName(mFileName + ".temp");
//            DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url,
//                    mMorecachein.getAbsolutePath(),
//                    mFileName + ".temp", md5);
            logger.d("now url path:  " + ip + url + "   file name:" + mFileName + ".nozip");
            if (isIP) {
                builder.setmHost(cdns.get(0));
//                downLoadInfo.setHost(cdns.get(cdnPos.get() % cdns.size()).substring(index));
            }
            LiveVideoDownLoadUtils.LiveVideoDownLoadFile downLoadFile = builder.build();
//            PreLoadDownLoaderManager.DownLoadInfoAndListener downLoadInfoListener = new PreLoadDownLoaderManager.DownLoadInfoAndListener(
//                    downLoadInfo,
//                    new CoursewarePreload.NoZipDownloadListener(
//                            mMorecachein,
//                            mMorecacheout,
//                            mFileName,
//                            ips,
//                            cdns,
//                            url,
//                            md5,
//                            downTryCount,
//                            resourcetype),
//                    "");
            if (!isPrecise) {
                FileDownLoadManager.addToAutoDownloadPool(downLoadFile);
            } else {
                FileDownLoadManager.addUrgentInfo(downLoadFile);
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
                long downLoadTime = System.currentTimeMillis() - startDownLoadTime;
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
