package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by lyqai on 2018/6/12.
 */

public class RolePlayStandLog {
    private static String eventId = LiveVideoConfig.LIVE_STAND_ROLEPLAY;

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
