package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2019/8/6.
 * 语音测评-三分屏直播
 */
public class TsSpeechLog {
    private static String eventId = LiveVideoConfig.LIVE_SPEECH_TEST2;

    public static void receiveVoiceTest(LiveAndBackDebug liveAndBackDebug, boolean isLive, String id, String content, int time) {
        StableLogHashMap logHashMap = new StableLogHashMap("receiveVoiceTest");
        logHashMap.put("live", "" + isLive);
        logHashMap.put("testtype", "4");
        logHashMap.put("testid", id);
        logHashMap.put("answer", content);
        logHashMap.put("answertime", "" + time);
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    public static void startRecord(LiveAndBackDebug liveAndBackDebug, boolean isLive, String id) {
        StableLogHashMap logHashMap = new StableLogHashMap("startRecord");
        logHashMap.put("testid", id);
        logHashMap.put("islive", "" + isLive);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    public static void voiceTestClose(LiveAndBackDebug liveAndBackDebug, boolean isLive, String id) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestClose");
        logHashMap.put("islive", "" + isLive);
        logHashMap.put("testid", "" + id);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 语音测评第5步，学生端结果框弹出 */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, boolean isLive, String state, int goldnum, int starnum, int totalscore, double speaktime) {
        StableLogHashMap logHashMap = new StableLogHashMap("voiceTestResult");
        logHashMap.put("testid", testid);
        logHashMap.put("islive", "" + isLive);
        logHashMap.put("state", state);
        logHashMap.put("totalscore", "" + totalscore);
        logHashMap.put("speaktime", "" + speaktime);
        logHashMap.addExY().addSno("5");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

}
