package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

public interface SpeechEvalHttp {
    public void getSpeechEvalAnswerTeamRank(boolean isNewArt,String id, final AbstractBusinessDataCallBack callBack);

    public String getRequestTime();

    public void getSpeechEvalAnswerTeamStatus(boolean isNewArt,String testId, AbstractBusinessDataCallBack callBack);

    void getRolePlayAnswerTeamRank(boolean isNewArt,String num, AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}
