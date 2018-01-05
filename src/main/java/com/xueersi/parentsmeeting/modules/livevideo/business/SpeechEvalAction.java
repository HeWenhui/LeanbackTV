package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;

import java.util.Map;

/**
 * Created by linyuqiang on 2017/2/28.
 * 语言评测得到试题和停止
 */
public interface SpeechEvalAction extends LiveAndBackDebug {

    /**
     * @param id           试题id
     * @param onSpeechEval 回调
     */
    void getSpeechEval(String id, OnSpeechEval onSpeechEval);

    /**
     * 关闭试题
     */
    void stopSpeech(BaseSpeechAssessmentPager pager, String num);

    void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval);

    void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval);

    void onSpeechSuccess(String num);

    void speechIsAnswered(String num, SpeechIsAnswered isAnswered);

    interface SpeechIsAnswered {
        void isAnswer(boolean answer);
    }
}
