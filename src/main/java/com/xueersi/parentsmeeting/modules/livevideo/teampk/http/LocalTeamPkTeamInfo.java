package com.xueersi.parentsmeeting.modules.livevideo.teampk.http;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalTeamPkTeamInfo {
    private static String TAG = "LocalTeamPkTeamInfo";

    public static ResponseEntity getTeamPkTeamInfo(ShareDataManager mShareDataManager, String liveId) {
        try {
            String string = mShareDataManager.getString(ShareDataConfig.LIVE_TEAMPK_INFO, "{}", ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = new JSONObject(string);
            if (jsonObject.has("liveId")) {
                String templiveId = jsonObject.optString("liveId");
                if (liveId.equals(templiveId)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setJsonObject(data);
                    return responseEntity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mShareDataManager.put(ShareDataConfig.LIVE_TEAMPK_INFO, "{}", ShareDataManager.SHAREDATA_USER);
            CrashReport.postCatchedException(new LiveException("LocalTeamPkTeamInfo", e));
        }
        return null;
    }

    public static void saveTeamPkTeamInfo(ShareDataManager mShareDataManager, ResponseEntity responseEntity, String liveId) {
        try {
            if (responseEntity == null) {
                return;
            }
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("liveId", liveId);
            jsonObject.put("data", data);
            mShareDataManager.put(ShareDataConfig.LIVE_TEAMPK_INFO, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }
}
