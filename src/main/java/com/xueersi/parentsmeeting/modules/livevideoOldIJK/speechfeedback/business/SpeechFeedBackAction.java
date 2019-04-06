package com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechfeedback.business;

/**
 * Created by linyuqiang on 2018/1/11.
 * 语音反馈
 */
public interface SpeechFeedBackAction {
    void start(String roomId);

    void stop();

    void setVideoLayout(int width, int height);
    void setNonce(String s);
}
