package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/4/7.
 */

public interface LiveStandSpeechEvalAction extends SpeechEvalAction {
    void getSpeechEvalAnswerTeamStatus(String testId, AbstractBusinessDataCallBack callBack);
}
