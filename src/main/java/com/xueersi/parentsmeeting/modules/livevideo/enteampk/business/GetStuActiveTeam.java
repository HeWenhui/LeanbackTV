package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;

public interface GetStuActiveTeam {
    void getStuActiveTeam(final AbstractBusinessDataCallBack callBack);

    PkTeamEntity getPkTeamEntity();
}
