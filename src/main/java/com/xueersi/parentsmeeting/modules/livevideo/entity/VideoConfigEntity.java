package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/** PSIJK使用的参数,包括打日志使用的参数 */
public class VideoConfigEntity {
    private long waterMark;
    private long duration;

    private String streamId;

    private int protocol;

    private String fileUrl;

    private String userName;

    private String userId;

    private int changeLinePos;

    public int getChangeLinePos() {
        return changeLinePos;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setChangeLinePos(int changeLinePos) {
        this.changeLinePos = changeLinePos;
    }

    public long getWaterMark() {
        return waterMark;
    }

    public VideoConfigEntity setWaterMark(long waterMark) {
        this.waterMark = waterMark;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public VideoConfigEntity setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public VideoConfigEntity setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public VideoConfigEntity setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("waterMark", waterMark);
            jsonObject.put("duration", duration);
            jsonObject.put("streamId", streamId);
            jsonObject.put("protocol", protocol);
            jsonObject.put("fileUrl", fileUrl);
            jsonObject.put("fileUrl", fileUrl);
            jsonObject.put("userName", userName);
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /** 添加播放异常的信息 */
    public JSONObject addPlayException() {
        try {
            return toJSONObject().put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toJSONObject();
    }

    public JSONObject addJSON(String key, String value) {
        try {
            return toJSONObject().put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toJSONObject();
    }
}
