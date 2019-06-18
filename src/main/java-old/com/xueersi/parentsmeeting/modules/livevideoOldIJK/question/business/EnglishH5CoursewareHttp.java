package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * Created by lyqai on 2018/7/6.
 */

public interface EnglishH5CoursewareHttp {
    void getStuGoldCount(String method);

    void sendRankMessage(int rankStuReconnectMessage);

    void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, AbstractBusinessDataCallBack callBack);

    void liveSubmitTestH5Answer(VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, String courseware_type, String isSubmit, double voiceTime, boolean isRight, QuestionSwitch.OnAnswerReslut onAnswerReslut);
}
