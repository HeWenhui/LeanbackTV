package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import java.util.HashMap;
import java.util.Map;

/** 直播-星星互动稳定性日志 */
public class StartInteractLog {
    static String eventId = LiveVideoConfig.LIVE_STAR_INTERACT;

    public static void starOpen(LiveAndBackDebug liveAndBackDebug, String answer, String mStarid, String nonce) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "starOpen");
        mData.put("starAnswer", "" + answer);
        mData.put("statue", "true");
        mData.put("starid", mStarid);
        if (!StringUtils.isEmpty(nonce)) {
            mData.put("ex", "Y");
            mData.put("sno", "2");
            mData.put("stable", "1");
        }
        liveAndBackDebug.umsAgentDebugPv(eventId, mData);
    }

    public static void starClose(LiveAndBackDebug liveAndBackDebug, String id, int receive, String myAnswer, String mStarid, int starCount) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "starClose");
        mData.put("star_id", id);
        mData.put("status", "" + (receive > -1 ? 1 : 0));
        mData.put("answer", myAnswer);
        mData.put("starid", mStarid);
        mData.put("star_num", "" + (starCount));
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    public static void setStuStarCount(LiveAndBackDebug liveAndBackDebug, String id, String myAnswer, String mStarid, int starCount) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "setStuStarCount");
        mData.put("answer", myAnswer);
        mData.put("star_id", id);
        mData.put("status", "success");
        mData.put("starnum", "" + (starCount));
        mData.put("starid", mStarid);
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    public static void setStuStarCount(LiveAndBackDebug liveAndBackDebug, String id, String myAnswer, int starCount, int errStatus, String failMsg) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "setStuStarCount");
        mData.put("answer", myAnswer);
        mData.put("star_id", id);
        mData.put("starnum", "" + (starCount));
        if (errStatus == 1) {
            mData.put("status", "failure");
        } else {
            mData.put("status", "error");
        }
        mData.put("msg", failMsg);
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    public static void sendStarAnswer(LiveAndBackDebug liveAndBackDebug, String msg, String mStarid) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "sendStarAnswer");
        mData.put("answer", msg);
        mData.put("status", "true");
        mData.put("starid", mStarid);
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }
}
