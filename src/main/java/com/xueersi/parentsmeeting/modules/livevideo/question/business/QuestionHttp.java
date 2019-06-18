package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by lyqai on 2018/7/5.
 */

public interface QuestionHttp {
    void getStuGoldCount(String method);

    void sendRankMessage(int rankStuReconnectMessage);

    void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void liveSubmitTestAnswer(LiveBasePager liveBasePager, VideoQuestionLiveEntity videoQuestionLiveEntity1, String mVSectionID, String testAnswer, boolean isVoice, boolean isRight, QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit);

    void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval);

    void sendSpeechEvalResult2(String id, String stuAnswer, String isSubmit, OnSpeechEval onSpeechEval);

    void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack callBack);

    void speechEval42IsAnswered(String mVSectionID, String num, AbstractBusinessDataCallBack callBack);
}
