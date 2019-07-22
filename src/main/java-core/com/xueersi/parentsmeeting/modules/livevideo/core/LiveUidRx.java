package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/8/18.
 * 直播流量统计
 */
public class LiveUidRx {
    private Context mContext;
    private boolean isLive;
    /** 时间和流量，用于计算下载速度 */
    private long timeAndUidRxBytes[] = new long[]{0, 0};
    private LiveGetInfo liveGetInfo;

    public LiveUidRx(Context context, boolean isLive) {
        this.mContext = context;
        this.isLive = isLive;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void onCreate() {
        int sdk = Build.VERSION.SDK_INT;
        boolean isJellyBean = sdk >= Build.VERSION_CODES.JELLY_BEAN_MR2;
        long curTime = System.currentTimeMillis();
        timeAndUidRxBytes[0] = curTime;
        if (isJellyBean) {
            timeAndUidRxBytes[1] = TrafficStats.getUidRxBytes(Process.myUid());
        }
    }

    public void onDestroy() {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap();
            logHashMap.put("time", "" + (System.currentTimeMillis() - timeAndUidRxBytes[0]));
            logHashMap.put("islive", "" + isLive);
            logHashMap.put("network", "" + NetWorkHelper.getNetWorkState(BaseApplication.getContext()));
            int sdk = Build.VERSION.SDK_INT;
            boolean isJellyBean = sdk >= Build.VERSION_CODES.JELLY_BEAN_MR2;
            if (isJellyBean) {
                long rxbytes = TrafficStats.getUidRxBytes(Process.myUid()) - timeAndUidRxBytes[1];
                logHashMap.put("rxbytes", "" + rxbytes);
                logHashMap.put("rxsize", "" + FileUtils.byte2FitMemorySize(rxbytes));
            } else {
                logHashMap.put("rxbytes", "-1");
            }
            if (liveGetInfo != null) {
                logHashMap.put("liveid", "" + liveGetInfo.getId());
                logHashMap.put("livetype", "" + liveGetInfo.getLiveType());
                logHashMap.put("isarts", "" + liveGetInfo.getIsArts());
                logHashMap.put("isenglish", "" + liveGetInfo.getIsEnglish());
                logHashMap.put("pattern", "" + liveGetInfo.getPattern());
            }
            UmsAgentManager.umsAgentDebug(mContext, LiveVideoConfig.LIVE_VIDEO_UID_RX, logHashMap.getData());
        } catch (Exception e) {

        }
    }
}
