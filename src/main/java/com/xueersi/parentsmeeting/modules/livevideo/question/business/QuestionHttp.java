package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * Created by lyqai on 2018/7/5.
 */

public interface QuestionHttp {
    void getStuGoldCount();

    void understand(boolean isUnderstand, String nonce);

    void sendRankMessage(int rankStuReconnectMessage);

    void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void liveSubmitTestAnswer(VideoQuestionLiveEntity videoQuestionLiveEntity1, String mVSectionID, String testAnswer, boolean isVoice, boolean isRight, QuestionSwitch.OnAnswerReslut answerReslut);

    void getSpeechEval(String id, OnSpeechEval onSpeechEval);

    void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval);

    void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval);

    void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack callBack);

    void speechEval42IsAnswered(String mVSectionID, String num, SpeechEvalAction.SpeechIsAnswered isAnswered);
}
