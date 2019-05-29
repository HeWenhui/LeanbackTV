package com.xueersi.parentsmeeting.modules.livevideo.event;


/**
 * 直播间内 教师表扬关闭本地 结果展示UI 事件
 *
 * @author linyuqiang
 */
public class TeachPraiseRusltulCloseEvent {
    /** 语音id */
    private String voiceId;
    private boolean addBack = true;
    int[] startPosition = new int[2];

    public TeachPraiseRusltulCloseEvent(String voiceId) {
        this.voiceId = voiceId;
    }

    public TeachPraiseRusltulCloseEvent(String voiceId, boolean addBack) {
        this.voiceId = voiceId;
        this.addBack = addBack;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public boolean isAddBack() {
        return addBack;
    }

    public void setAddBack(boolean addBack) {
        this.addBack = addBack;
    }

    public int[] getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int[] startPosition) {
        this.startPosition = startPosition;
    }
}
