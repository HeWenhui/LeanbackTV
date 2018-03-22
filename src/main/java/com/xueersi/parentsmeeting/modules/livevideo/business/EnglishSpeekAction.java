package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by lyqyuqiang on 2017/11/8.
 * 英语能量条
 */
public interface EnglishSpeekAction {
    void onDBStart();

    void onDBStop();

    void praise(int answer);

    void remind(int answer);

    void onModeChange(String mode, boolean audioRequest);

    void start();

    void stop(AudioRequest.OnAudioRequest onAudioRequest);

    void destory();
}
