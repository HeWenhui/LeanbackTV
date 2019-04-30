package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;


import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;

public class GroupGameLog {
    static String eventId = LogConfig.LIVE_VOICE_CANNON;

    /**
     * 学生收到题目信息（SNO 2）   ---交互日志
     */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voicecannonReceive");
        logHashMap.put("testid", testid);
        logHashMap.put("sno", "2");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * 开始作答时间---展示日志
     */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voicecannonAnswer");
        logHashMap.put("testid", testid);
        logHashMap.put("sno", "3");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * 翻页时间---交互
     */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid) {
        StableLogHashMap logHashMap = new StableLogHashMap("voicecannonNextPage");
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("sno", "4");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * 游戏结束时间（SNO 5）---交互日志
     */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, String state,
                            String hasvoicetime) {
        StableLogHashMap logHashMap = new StableLogHashMap("voicecannonSubmit");
        logHashMap.put("testid", testid);
        logHashMap.put("state", state);
        logHashMap.put("hasvoicetime", hasvoicetime);
        logHashMap.put("sno", "5");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno6显示结果页 */
    public static void sno6(LiveAndBackDebug liveAndBackDebug, String testid, String mvpnum) {
        StableLogHashMap logHashMap = new StableLogHashMap("voicecannonShowMvp");
        logHashMap.put("testid", testid);
        logHashMap.put("mvpnum", "" + mvpnum);
        logHashMap.put("sno", "6");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

}
