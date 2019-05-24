package com.xueersi.parentsmeeting.modules.livevideo.entity;

/** PSIJK使用的参数 */
public class VideoConfigEntity {
    private long waterMark;
    private long duration;

    private String streamId;

    private int protocol;

    private String fileUrl;

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
}
