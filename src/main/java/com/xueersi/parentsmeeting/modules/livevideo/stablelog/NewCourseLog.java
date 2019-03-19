package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class NewCourseLog {
    static String eventId = LogConfig.LIVE_H5PLAT;

    public static String getNewCourseTestIdSec(VideoQuestionLiveEntity detailInfo, int arts) {
        if (arts == LiveVideoSAConfig.ART_EN) {
            detailInfo.setNewCourseTestIdSec(detailInfo.id);
            return detailInfo.id;
        }
        String newCourseTestIdSec = detailInfo.getNewCourseTestIdSec();
        if (newCourseTestIdSec != null) {
            return newCourseTestIdSec;
        }
        newCourseTestIdSec = "";
        try {
            EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
//            String packageId = englishH5Entity.getPackageId();
            JSONArray array = new JSONArray(englishH5Entity.getReleasedPageInfos());
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONArray value = jsonObject.getJSONArray(key);
                    String srcTypes = value.getString(0);
                    String testIds = value.getString(1);
                    newCourseTestIdSec += key + "_" + srcTypes + "_" + testIds + ",";
                }
            }
        } catch (JSONException e) {
            Logger logger = LiveLoggerFactory.getLogger("NewCourseLog");
            logger.e("getCourseWareTests", e);
        }
        detailInfo.setNewCourseTestIdSec(newCourseTestIdSec);
        return newCourseTestIdSec;
    }

    /**
     * sno2 学生端接收发题指令
     * testid 理科+ 语文：
     * <p>
     * 英语： [testid+testd]
     */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String testid, int noticecode) {
        StableLogHashMap logHashMap = new StableLogHashMap("receiveH5Plat");
        logHashMap.put("testid", testid);
        logHashMap.put("noticecode", "" + noticecode);
        logHashMap.put("sno", "2");
        logHashMap.put("status", "true");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * sno3学生加载页面loading
     * pageid 页面id(英语无pageid)
     */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid, String loadurl, boolean ispreload, String pageid) {
        StableLogHashMap logHashMap = new StableLogHashMap("showLoading");
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        logHashMap.put("pageid", "" + pageid);
        logHashMap.put("sno", "3");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno4课件加载完成/打开页面 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid, String loadurl, boolean ispreload, String pageid, long loadtime, int isfresh) {
        StableLogHashMap logHashMap = new StableLogHashMap("showH5Plat");
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        logHashMap.put("pageid", "" + pageid);
        logHashMap.put("loadtime", "" + loadtime);
        logHashMap.put("isfresh", "" + isfresh);
        logHashMap.put("sno", "4");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno5学生提交(包括强制提交) */
    public static void sno5(LiveAndBackDebug liveAndBackDebug, String testid, boolean isforce, String loadurl, boolean ispreload) {
        StableLogHashMap logHashMap = new StableLogHashMap("startSubmit");
        logHashMap.put("testid", testid);
        logHashMap.put("isforce", "" + isforce);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("loadurl", "" + loadurl);
        logHashMap.put("sno", "5");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno6提交成功(包括强制提交) */
    public static void sno6(LiveAndBackDebug liveAndBackDebug, String testid, boolean status, boolean isforce, boolean ispreload, long submittime, String errmsg) {
        StableLogHashMap logHashMap = new StableLogHashMap("submitResult");
        logHashMap.put("testid", testid);
        logHashMap.put("status", "" + status);
        logHashMap.put("submittime", "" + submittime);
        logHashMap.put("isforce", "" + isforce);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("errmsg", "" + errmsg);
        logHashMap.put("sno", "6");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno7加载结果页 */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String testid, boolean ispreload) {
        StableLogHashMap logHashMap = new StableLogHashMap("showResultLoading");
        logHashMap.put("testid", testid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("sno", "7");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** sno8显示结果页 */
    public static void sno8(LiveAndBackDebug liveAndBackDebug, String testid, boolean ispreload, long showtime) {
        StableLogHashMap logHashMap = new StableLogHashMap("showResult");
        logHashMap.put("testid", testid);
        logHashMap.put("ispreload", "" + ispreload);
        logHashMap.put("showtime", "" + showtime);
        logHashMap.put("sno", "8");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

}
