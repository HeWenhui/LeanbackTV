package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

public interface SpeechEvalHttp {
    public void getSpeechEvalAnswerTeamRank(String id, final AbstractBusinessDataCallBack callBack);

    public String getRequestTime();

    public void getSpeechEvalAnswerTeamStatus(String testId, AbstractBusinessDataCallBack callBack);

    void getRolePlayAnswerTeamRank(String num, AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}
