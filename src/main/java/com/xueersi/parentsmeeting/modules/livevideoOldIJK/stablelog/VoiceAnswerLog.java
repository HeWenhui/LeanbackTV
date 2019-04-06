package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/1/22.
 * 语音答题
 */
public class VoiceAnswerLog {
    private static String eventId = LiveVideoConfig.LIVE_TEST_VOICE;

    /** 语音答题第二步，收到互动题-回放 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, VideoQuestionEntity videoQuestionLiveEntity) {
        StableLogHashMap logHashMap = new StableLogHashMap("showAnswerDialog");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.getvQuestionID());
        if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionLiveEntity.getvCategory()) {
            logHashMap.put("sourcetype", "h5ware");
            logHashMap.put("testtype", "" + videoQuestionLiveEntity.getVoiceQuestiontype());
        } else {
            logHashMap.put("sourcetype", "h5test");
            logHashMap.put("testtype", "" + videoQuestionLiveEntity.getvQuestionType());
        }
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音答题第二步，收到普通互动题 */
    public static void sno2H5test(LiveAndBackDebug liveAndBackDebug, String testtype, String testid, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("showAnswerDialog");
        logHashMap.put("testtype", "" + testtype);
        logHashMap.put("testid", "" + testid);
        logHashMap.put("sourcetype", "h5test");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2").addNonce("" + nonce);
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音答题第二步，收到课件互动题 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testtype, String testid, String nonce, String sourcetype) {
        StableLogHashMap logHashMap = new StableLogHashMap("showAnswerDialog");
        logHashMap.put("testtype", "" + testtype);
        logHashMap.put("testid", "" + testid);
        logHashMap.put("sourcetype", sourcetype + "");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2").addNonce("" + nonce);
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    public static void sno5(LiveAndBackDebug liveAndBackDebug, String sourcetype, String testid, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("showResultDialog");
        logHashMap.put("testid", "" + testid);
        logHashMap.put("sourcetype", sourcetype).addNonce(nonce);
        logHashMap.addExY().addExpect("0").addSno("5").addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
