package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

public class EnglishSpeekLog {
    static String eventId = LiveVideoConfig.LIVE_ENGLISH_SPEEK;

    /**
     * 语音互动表扬
     */
    public static void sendPraise(LiveAndBackDebug liveAndBackDebug, String answer, String sendDbDuration) {
        StableLogHashMap logHashMap = new StableLogHashMap("sendPraise");
        logHashMap.put("answer", "" + answer);
        logHashMap.put("duration", "" + sendDbDuration);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /**
     * 语音互动表扬
     */
    public static void sendRemind(LiveAndBackDebug liveAndBackDebug, String answer, String sendDbDuration) {
        StableLogHashMap logHashMap = new StableLogHashMap("sendPraise");
        logHashMap.put("answer", "" + answer);
        logHashMap.put("duration", "" + sendDbDuration);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

}
