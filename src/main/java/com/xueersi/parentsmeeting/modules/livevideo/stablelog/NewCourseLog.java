package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.content.Context;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

public class NewCourseLog {
    static String eventId = LogConfig.LIVE_H5PLAT;
    /** 区分文理appid */
    static String appID = UmsConstants.LIVE_APP_ID;

    /**
     * sno2 学生端接收发题指令
     * testid 理科+ 语文：
     * <p>
     * 英语： [testid+testd]
     */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testid) {
        StableLogHashMap logHashMap = new StableLogHashMap("receiveH5Plat");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /**
     * sno3学生加载页面loading
     * pageid 页面id(英语无pageid)
     */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid, String loadurl, boolean ispreload, String pageid) {
        StableLogHashMap logHashMap = new StableLogHashMap("showLoading");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        logHashMap.put("pageid", "" + pageid);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /** sno4课件加载完成/打开页面 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid, String loadurl, boolean ispreload, String pageid, long loadtime) {
        StableLogHashMap logHashMap = new StableLogHashMap("showH5Plat");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        logHashMap.put("pageid", "" + pageid);
        logHashMap.put("loadtime", "" + loadtime);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /** sno5学生提交(包括强制提交) */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, boolean isforce, String loadurl, boolean ispreload) {
        StableLogHashMap logHashMap = new StableLogHashMap("startSubmit");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        logHashMap.put("isforce", "" + isforce);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /** sno6提交成功(包括强制提交) */
    public static void sno6(LiveAndBackDebug liveAndBackDebug, String testid, boolean status, boolean isforce, boolean ispreload, long submittime, String errmsg) {
        StableLogHashMap logHashMap = new StableLogHashMap("submitResult");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        logHashMap.put("status", "" + status);
        logHashMap.put("submittime", "" + submittime);
        logHashMap.put("isforce", "" + isforce);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("errmsg", "" + errmsg);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }

    /** sno7加载结果页 */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String testid, boolean ispreload) {
        StableLogHashMap logHashMap = new StableLogHashMap("showResult");
        logHashMap.put("appid", appID);
        logHashMap.put("testid", testid);
        logHashMap.put("ispreload", "" + ispreload);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
    }
}
