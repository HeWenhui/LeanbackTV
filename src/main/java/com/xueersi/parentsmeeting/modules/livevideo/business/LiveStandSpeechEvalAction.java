package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/4/7.
 * 站立直播语音评测一些事件
 */
public interface LiveStandSpeechEvalAction extends SpeechEvalAction {
    /**
     * 得到小组战况
     *
     * @param testId
     * @param callBack
     */
    void getSpeechEvalAnswerTeamStatus(String testId, AbstractBusinessDataCallBack callBack);

    long getRequestTime();
}
