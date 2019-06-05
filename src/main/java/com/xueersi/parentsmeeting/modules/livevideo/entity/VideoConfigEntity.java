package com.xueersi.parentsmeeting.modules.livevideo.entity;

import org.json.JSONException;
import org.json.JSONObject;

/** PSIJK使用的参数，播放视频需要的参数 */
public class VideoConfigEntity {
    private long waterMark;
    private long duration;

    private String streamId;

    private int protocol;

    private String fileUrl;

    private String userName;
    private String userId;

    public long getWaterMark() {
        return waterMark;
    }

    public void setWaterMark(long waterMark) {
        this.waterMark = waterMark;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("waterMark", waterMark);
            jsonObject.put("duration", duration);
            jsonObject.put("userName", userName);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }
}
