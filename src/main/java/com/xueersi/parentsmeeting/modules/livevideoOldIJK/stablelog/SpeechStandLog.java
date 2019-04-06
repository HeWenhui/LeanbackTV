package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/4/24.
 * 语音测评-站立直播
 */
public class SpeechStandLog {
    private static String eventId = LiveVideoConfig.LIVE_STAND_SPEECH_TEST;

    /** 语音测评第二步，收到互动题 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testId, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestReceive");
        logHashMap.put("testid", "" + testId);
        logHashMap.put("testtype", "TeacherResearch");
        logHashMap.put("answertype", "voice");
        logHashMap.addExY().addNonce(nonce).addSno("2");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 语音测评第3步，麦克风弹出 */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid, boolean micrun) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestMicUp");
        logHashMap.put("testid", "" + testid);
        logHashMap.put("micrun", "" + micrun);
        logHashMap.addExY().addSno("3");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音测评第4步，组内战况弹出 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestTeamStates");
        logHashMap.put("testid", "" + testid);
        logHashMap.addExY().addSno("4");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 语音测评第5步，学生端结果框弹出 */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, String state, int goldnum, int starnum, int totalscore, double speaktime) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestResult");
        logHashMap.put("testid", testid);
        logHashMap.put("state", state);
        logHashMap.put("goldnum", "" + goldnum);
        logHashMap.put("starnum", "" + starnum);
        logHashMap.put("totalscore", "" + totalscore);
        logHashMap.put("speaktime", "" + speaktime);
        logHashMap.addExY().addSno("5");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /**
     * 语音测评第7步，学生端结果榜单弹出
     *
     * @param liveAndBackDebug
     * @param testid
     */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestTopThree");
        logHashMap.put("testid", "" + testid);
        logHashMap.addExY().addSno("7");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
