package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

/**
 * Created by linyuqiang on 2018/9/14.
 * 缓冲的实体
 */
public class PlayBufferEntity {
    private String tip = "";
    private long startTime;
    private long endTime;

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
