package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;


/**
 * 大班灰度
 *
 */
public class PublicLiveGrayEntity {
    /** 灰度状态 */
    private int status = -1;
    /** 是否立即跳转 */
    private boolean isIntentTo;
    /** 是否直播 */
    private boolean isLive;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isIntentTo() {
        return isIntentTo;
    }

    public void setIntentTo(boolean intentTo) {
        isIntentTo = intentTo;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
