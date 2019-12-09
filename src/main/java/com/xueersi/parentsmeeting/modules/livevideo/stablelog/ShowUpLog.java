package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * 上台日志
 */
public class ShowUpLog {
    private static final String EVENT_ID = "cometostage";

    /**
     * 学生端收到发题指令时间点
     */
    public static void reciveQuestion(LiveAndBackDebug liveAndBackDebug, boolean isplayback,
                                      String interactionid) {
        StableLogHashMap logHashMap = new StableLogHashMap("receiveComeToStage");
        logHashMap.put("isplayback", isplayback ? "1" : "0");
        logHashMap.put("interactionid", interactionid);
        logHashMap.put("sno", "1");
        liveAndBackDebug.umsAgentDebugInter(EVENT_ID, logHashMap.getData());
    }

    /**
     * 是否成功加载课件
     */
    public static void loadCoursewareStatus(LiveAndBackDebug liveAndBackDebug, boolean isplayback
            , boolean success, String interactionid) {
        StableLogHashMap logHashMap = new StableLogHashMap("loadingsucceedComeToStage");
        logHashMap.put("loadingsucceed", success ? "1" : "0");
        logHashMap.put("isplayback", isplayback ? "1" : "0");
        logHashMap.put("interactionid", interactionid);
        logHashMap.put("sno", "2");
        liveAndBackDebug.umsAgentDebugPv(EVENT_ID, logHashMap.getData());
    }

    /**
     * 测评大于 0 分的单词文本+分数（多条）
     */
    public static void voiceTestResult(LiveAndBackDebug liveAndBackDebug, String word
            , int score, boolean isplayback, String interactionid) {
        StableLogHashMap logHashMap = new StableLogHashMap("submitComeToStage");
        logHashMap.put("word", word);
        logHashMap.put("score", String.valueOf(score));
        logHashMap.put("isplayback", isplayback ? "1" : "0");
        logHashMap.put("interactionid", interactionid);
        logHashMap.put("sno", "3");
        liveAndBackDebug.umsAgentDebugInter(EVENT_ID, logHashMap.getData());
    }

    /**
     * 结果页弹出时间
     */
    public static void popResultPage(LiveAndBackDebug liveAndBackDebug, boolean isplayback,
                                     String interactionid) {
        StableLogHashMap logHashMap = new StableLogHashMap("displayResults");
        logHashMap.put("isplayback", isplayback ? "1" : "0");
        logHashMap.put("interactionid", interactionid);
        logHashMap.put("sno", "4");
        liveAndBackDebug.umsAgentDebugPv(EVENT_ID, logHashMap.getData());
    }


}
