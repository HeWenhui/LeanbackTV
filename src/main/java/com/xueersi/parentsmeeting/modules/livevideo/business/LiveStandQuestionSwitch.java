package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;

/**
 * Created by lyqai on 2018/4/4.
 */

public interface LiveStandQuestionSwitch extends QuestionSwitch {
    void getQuestionTeamRank(BaseVideoQuestionEntity videoQuestionLiveEntity, AbstractBusinessDataCallBack callBack);
}
