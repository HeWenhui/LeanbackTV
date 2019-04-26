package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/4/20.
 * 站立直播红包日志
 */
public class RedPackageStandLog {
    private static String eventId = LiveVideoConfig.STAND_LIVE_GRANT;

    /** 红包第一步，学生端接收到发送红包指令 */
    public static void sno1(LiveAndBackDebug liveAndBackDebug, String operateId) {
        StableLogHashMap logHashMap = new StableLogHashMap("start_grant");
        logHashMap.put("grantid", operateId);
        logHashMap.addSno("2");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 红包第二步，学员点击领取红包时间点，点击领取大红包（即在10s内领取） */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String operateId, String granturl, String result) {
        StableLogHashMap logHashMap = new StableLogHashMap("receive_big_grant");
        logHashMap.put("grantid", operateId);
        logHashMap.put("granturl", granturl);
        logHashMap.put("result", result);
        logHashMap.addSno("3");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 红包第二步，学员点击领取红包时间点，点击领取小红包（即屏幕右端领取） */
    public static void sno3_2(LiveAndBackDebug liveAndBackDebug, String operateId, String granturl, String result) {
        StableLogHashMap logHashMap = new StableLogHashMap("receive_small_grant");
        logHashMap.put("grantid", operateId);
        logHashMap.put("granturl", granturl);
        logHashMap.put("result", result);
        logHashMap.addSno("3");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 红包第三步，红包榜弹出时间点 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String operateId, String granturl, String result) {
        StableLogHashMap logHashMap = new StableLogHashMap("grant_gold_list");
        logHashMap.put("grantid", operateId);
        logHashMap.put("granturl", granturl);
        logHashMap.put("result", result);
        logHashMap.addSno("4");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /** 红包第四步，幸运儿弹出时间点 */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String operateId, String granturl, String result) {
        StableLogHashMap logHashMap = new StableLogHashMap("grant_top3");
        logHashMap.put("grantid", operateId);
        logHashMap.put("granturl", granturl);
        logHashMap.put("result", result);
        logHashMap.addSno("5");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
