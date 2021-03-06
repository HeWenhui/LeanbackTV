package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.BaseVideoQuestionEntity;

/**
 * Created by linyuqiang on 2018/4/4.
 * 站立直播语音答题切换h5课件
 */
public interface LiveStandQuestionSwitch extends QuestionSwitch {
    void getTestAnswerTeamStatus(BaseVideoQuestionEntity videoQuestionLiveEntity, AbstractBusinessDataCallBack callBack);

    long getRequestTime();
}
