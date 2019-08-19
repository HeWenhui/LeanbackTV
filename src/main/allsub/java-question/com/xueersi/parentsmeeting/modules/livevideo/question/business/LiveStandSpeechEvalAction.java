package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

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
    void getSpeechEvalAnswerTeamStatus(boolean isNewArt,String testId, AbstractBusinessDataCallBack callBack);

    long getRequestTime();
}
