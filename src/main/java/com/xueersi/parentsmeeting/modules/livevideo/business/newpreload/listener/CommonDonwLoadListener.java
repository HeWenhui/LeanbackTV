package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.listener;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CommonDonwLoadListener implements DownloadListener {
    String mFileName;
    String url;
    String md5;
    List<String> cdns;
    List<String> ips;
    File mMorecacheout;
    File mMorecachein;
    AtomicInteger downTryCount;
    String itemLiveId;
    String resourcetype;
    long startDownLoadTime;
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    boolean isPrecise;

    public CommonDonwLoadListener(
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
            boolean isPrecise) {
//        this.coursewareHelper = coursewareHelper;

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
        this.isPrecise = isPrecise;
    }

    protected abstract void decrementDocument();


    public static class Progresses implements ZipProg {
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

    public static class PreZipExtractorTask extends ZipExtractorTask {
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
            unZipMap.put("extrainfo", mMorecacheout.getAbsolutePath());
            unZipMap.put("sno", "3");
            unZipMap.put("liveid", itemLiveId);
            unZipMap.put("resourcetype", resourcetype);
            unZipMap.put("ip", IpAddressUtil.USER_IP);
            unZipMap.put("freeSize", "" + CoursewarePreload.getFreeSize());
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
            unZipMap.put("sno", "4");
            unZipMap.put("liveid", itemLiveId);
            unZipMap.put("resourcetype", resourcetype);
            unZipMap.put("ip", IpAddressUtil.USER_IP);
            unZipMap.put("freeSize", "" + CoursewarePreload.getFreeSize());
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, unZipMap.getData());
        }
    }
}
