package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;

public interface EnTeamPkHttp {
    @Deprecated
    void getSelfTeamInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void reportStuInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void updataEnglishPkGroup(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void getEnglishPkGroup(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void reportStuLike(String testId, ArrayList<TeamMemberEntity> myTeamEntitys, AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}
