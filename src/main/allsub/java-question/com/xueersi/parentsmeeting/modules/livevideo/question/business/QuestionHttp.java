package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by linyuqiang on 2018/7/5.
 */

public interface QuestionHttp {
    void getStuGoldCount(String method);

    void sendRankMessage(int rankStuReconnectMessage);

    void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void liveSubmitTestAnswer(LiveBasePager liveBasePager, VideoQuestionLiveEntity videoQuestionLiveEntity1, String mVSectionID, String testAnswer, boolean isVoice, boolean isRight, QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit);

    void sendSpeechEvalResult2(boolean isNewArt,String id, String stuAnswer, String isSubmit, AbstractBusinessDataCallBack callBack);

    void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack callBack);

    void speechEval42IsAnswered(boolean isNewArt,String mVSectionID, String num, AbstractBusinessDataCallBack callBack);
}
