package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.tal.speech.language.TalLanguage;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * Created by lyqyuqiang on 2017/11/8.
 * 英语能量条
 */
public interface EnglishSpeekAction {
    void onDBStart();

    void onDBStop();

    TalLanguage getTalLanguage();

    /**
     * @param answer
     */
    void praise(int answer);

    void remind(int answer);

    void onModeChange(String mode, boolean audioRequest);

    void start();

    void stop(AudioRequest.OnAudioRequest onAudioRequest);

    void setSpeakerRecognitioner(SpeakerRecognitioner speakerRecognitioner);

    void destory();
}
