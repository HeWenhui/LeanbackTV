package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * Created by linyuqiang on 2018/4/24.
 * 语音答题-站立直播
 */
public class VoiceAnswerStandLog {
    private static String eventId = LiveVideoConfig.LIVE_STAND_TEST_VOICE;

    /** 语音答题第二步，收到互动题 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, VideoQuestionEntity videoQuestionLiveEntity) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceAnswerReceive");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.getvQuestionID());
        logHashMap.put("testtype", "H5");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 语音答题第二步，收到互动题 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, VideoQuestionLiveEntity videoQuestionLiveEntity) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceAnswerReceive");
        logHashMap.put("testid", "" + videoQuestionLiveEntity.getvQuestionID());
        logHashMap.put("testtype", "H5");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addSno("2").addNonce(videoQuestionLiveEntity.nonce);
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 语音答题第3步，麦克风弹出 */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid, boolean micrun) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceAnswerMicUp");
        logHashMap.put("testid", "" + testid);
        logHashMap.put("micrun", "" + micrun);
        logHashMap.addExY().addSno("3");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音答题第4步，组内战况弹出 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceAnswerTeamStates");
        logHashMap.put("testid", "" + testid);
        logHashMap.addExY().addSno("4");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音答题第5步，学生端结果框弹出 */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, String state, int goldnum, boolean isright, double speaktime) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceAnswerResult");
        logHashMap.put("testid", testid);
        logHashMap.put("state", state);
        logHashMap.put("goldnum", "" + goldnum);
        logHashMap.put("isright", "" + (isright ? "1" : "0"));
        logHashMap.put("speaktime", "" + speaktime);
        logHashMap.addExY().addSno("5");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /**
     * 语音答题第7步，学员切换手动时间点
     *
     * @param liveAndBackDebug
     * @param testid
     * @param clicktime        出现互动题和点击切换的时间差
     */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String testid, String clicktime) {
        StableLogHashMap logHashMap = new StableLogHashMap("switchClickAnswer");
        logHashMap.put("testid", "" + testid);
        logHashMap.put("clicktime", clicktime);
        logHashMap.addExY().addSno("7");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }
}
