package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveLog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * 学研中心直播及回放心跳日志规范
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=17699185
 */
public class UserOnlineLog {
    static String eventId = LogConfig.LIVE_HEART_BEAT;
    private static String TAG = "UserOnlineLog";

//    public static void sno1(long oldTime, LiveAndBackDebug liveAndBackDebug) {
//        StableLogHashMap logHashMap = new StableLogHashMap("joinLiveRoom");
//        logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
//        logHashMap.put("oldTime", "" + oldTime);
//        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//    }

    public static void sno2(long oldTime, LiveAndBackDebug liveAndBackDebug) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("heartbeatTimerStart");
            logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
            logHashMap.put("inittime", "" + LiveVideoConfig.LIVE_HB_TIME);
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    public static void sno3(String params, String status, String errmsg, int heartTimes, int mHbCount, LiveAndBackDebug liveAndBackDebug) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("uploadHeartbeatTime");
            logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
            logHashMap.put("params", "" + params);
            logHashMap.put("status", "" + status);
            logHashMap.put("errmsg", "" + errmsg);
            logHashMap.put("heartTimes", "" + heartTimes);
            logHashMap.put("mHbCount", "" + mHbCount);
            logHashMap.put("inittime", "" + LiveVideoConfig.LIVE_HB_TIME);
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    public static void sno4(boolean send, int mLiveType, int heartTimes, int mHbCount, LiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("heartbeatTimerPost");
        logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
        logHashMap.put("inittime", "" + LiveVideoConfig.LIVE_HB_TIME);
        logHashMap.put("send", "" + send);
        logHashMap.put("onlinetype", "" + mLiveType);
        logHashMap.put("heartTimes", "" + heartTimes);
        logHashMap.put("mHbCount", "" + mHbCount);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    public static void sno5(long oldTime, int heartTimes, int mHbCount, LiveAndBackDebug liveAndBackDebug) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("heartbeatTimerStop");
            logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
            logHashMap.put("oldTime", "" + oldTime);
            logHashMap.put("heartTimes", "" + heartTimes);
            logHashMap.put("mHbCount", "" + mHbCount);
            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

//    public static void sno6(long oldTime, LiveAndBackDebug liveAndBackDebug) {
//        StableLogHashMap logHashMap = new StableLogHashMap("quitLiveRoom");
//        logHashMap.put("livetimes", "" + LiveLog.LIVE_TIME);
//        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//    }
}
