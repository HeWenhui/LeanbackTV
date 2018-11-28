package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;

/**
 * Created by linyuqiang on 2017/2/28.
 * 语言评测得到试题和停止
 */
public interface SpeechEvalAction {

    /**
     * @param id           试题id
     * @param onSpeechEval 回调
     */
    void getSpeechEval(String id, OnSpeechEval onSpeechEval);

    /**
     * 关闭试题
     */
    void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, String num);

    void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval);

    /**
     * 新课件{@linkplain LiveHttpManager#sendSpeechEvalResultNewArts}
     * 旧课件{@linkplain LiveHttpManager#sendSpeechEvalResult2}
     *
     * @param id
     * @param stuAnswer
     * @param onSpeechEval
     */
    void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval);

    void onSpeechSuccess(String num);

    void speechIsAnswered(String num, SpeechIsAnswered isAnswered);

    interface SpeechIsAnswered {
        void isAnswer(boolean answer);
    }
}
