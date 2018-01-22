package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/1/22.
 * 语音答题
 */
public class VoiceAnswerLog {
    private static String eventId = LiveVideoConfig.LIVE_TEST_VOICE;

    /** 语音答题第二步，收到普通互动题 */
    public static void sno2H5test(LiveAndBackDebug liveAndBackDebug, String testtype, String testid, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("showAnswerDialog");
        logHashMap.put("testtype", "" + testtype);
        logHashMap.put("testid", "" + testid);
        logHashMap.put("sourcetype", "h5test");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2").addNonce("" + nonce);
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebug3(eventId, logHashMap.getData());
    }

    /** 语音答题第二步，收到课件互动题 */
    public static void sno2H5Ware(LiveAndBackDebug liveAndBackDebug, String testtype, String testid, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("showAnswerDialog");
        logHashMap.put("testtype", "" + testtype);
        logHashMap.put("testid", "" + testid);
        logHashMap.put("sourcetype", "h5ware");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2").addNonce("" + nonce);
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebug3(eventId, logHashMap.getData());
    }
}
