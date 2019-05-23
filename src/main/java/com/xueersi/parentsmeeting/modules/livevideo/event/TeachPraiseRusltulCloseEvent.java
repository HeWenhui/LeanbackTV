package com.xueersi.parentsmeeting.modules.livevideo.event;


/**
 * 直播间内 教师表扬关闭本地 结果展示UI 事件
 *
 * @author linyuqiang
 */
public class TeachPraiseRusltulCloseEvent {
    /** 语音id */
    String voiceId;

    public TeachPraiseRusltulCloseEvent(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoiceId() {
        return voiceId;
    }

}
