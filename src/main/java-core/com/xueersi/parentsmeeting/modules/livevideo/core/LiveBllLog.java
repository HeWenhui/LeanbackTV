package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.app.Activity;
import android.content.Intent;

import com.xueersi.lib.analytics.umsagent.UmsAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LiveBllLog {
    static String eventid = LogConfig.LIVE_BUSINESS_TIME;
    static Logger logger = LoggerFactory.getLogger("LiveBllLog");
    static int creatindex = 0;
    static int index = 0;

    public static void onCreateEnd(Activity activity, ArrayList<BusinessTime> businessTimes) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("oncreat");
            Intent intent = activity.getIntent();
            int isArts = intent.getIntExtra("isArts", -1);
            String vStuCourseID = intent.getStringExtra("vStuCourseID");
            logHashMap.put("liveid", "" + vStuCourseID);
            logHashMap.put("isarts", "" + isArts);
            logHashMap.put("businesssize", "" + businessTimes.size());
            logHashMap.put("logindex", "" + creatindex);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < businessTimes.size(); i++) {
                BusinessTime businessTime = businessTimes.get(i);
                JSONObject busObj = new JSONObject();
                busObj.put("businessname", "" + businessTime.businessName);
                busObj.put("businesstime", "" + businessTime.time);
                jsonArray.put(busObj);
                logger.d("onCreateEnd:businessBll=" + businessTime.businessName + "\ttime2=" + businessTime.time);
            }
            logHashMap.put("businessarr", "" + jsonArray);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventid, logHashMap.getData());
            creatindex++;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("LiveBllLog", e);
        }
    }

    public static void onGetInfoEnd(LiveGetInfo liveGetInfo, ArrayList<BusinessTime> businessTimes) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("getinfo");
            logHashMap.put("liveid", "" + liveGetInfo.getId());
            logHashMap.put("isarts", "" + liveGetInfo.getIsArts());
            logHashMap.put("businesssize", "" + businessTimes.size());
            logHashMap.put("logindex", "" + index);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < businessTimes.size(); i++) {
                BusinessTime businessTime = businessTimes.get(i);
                JSONObject busObj = new JSONObject();
                busObj.put("businessname", "" + businessTime.businessName);
                busObj.put("businesstime", "" + businessTime.time);
                jsonArray.put(busObj);
                logger.d("onGetInfoEnd:businessBll=" + businessTime.businessName + "\ttime2=" + businessTime.time);
            }
            logHashMap.put("businessarr", "" + jsonArray);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventid, logHashMap.getData());
            index++;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("LiveBllLog", e);
        }
    }

    public static class BusinessTime {
        String businessName;
        long time;

        BusinessTime(String businessName, long time) {
            this.businessName = businessName;
            this.time = time;
        }
    }
}
