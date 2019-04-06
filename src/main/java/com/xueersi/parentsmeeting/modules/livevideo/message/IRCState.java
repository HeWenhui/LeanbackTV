package com.xueersi.parentsmeeting.modules.livevideo.message;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.ui.dataload.PageDataLoadEntity;

/**
 * Created by lyqai on 2018/6/26.
 */

public interface IRCState {
    public String getMode();

    boolean isOpenbarrage();

    boolean openchat();

    boolean sendMessage(String msg, String s);

    void praiseTeacher(String formWhichTeacher, String s, String s1, HttpCallBack gold);

    boolean isDisable();

    boolean isHaveTeam();

    boolean isSeniorOfHighSchool();

    void getMoreChoice(PageDataLoadEntity mPageDataLoadEntity, AbstractBusinessDataCallBack getDataCallBack);

    boolean isOpenZJLKbarrage();

    boolean isOpenFDLKbarrage();

    String getLKNoticeMode();
}
