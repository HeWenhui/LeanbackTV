package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by lyqai on 2018/6/12.
 */

public class RolePlayStandLog {
    private static String eventId = LiveVideoConfig.LIVE_STAND_ROLEPLAY;

    /**
     * roleplay第2步，收到发题指令时间点
     *
     * @param liveAndBackDebug
     * @param testid
     */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("receiveRoleplay");
        logHashMap.put("testid", "" + testid);
        logHashMap.addSno("2");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * roleplay第3步，题目弹出时间点
     *
     * @param liveAndBackDebug
     * @param testid
     */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("showRoleplay");
        logHashMap.put("testid", "" + testid);
        logHashMap.addSno("3");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /**
     * roleplay第6步，学生端结果榜单弹出
     *
     * @param liveAndBackDebug
     * @param testid
     */
    public static void sno6(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("roleplayTop3");
        logHashMap.put("testid", "" + testid);
        logHashMap.addExY().addSno("6");
        logHashMap.addStable("1");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
