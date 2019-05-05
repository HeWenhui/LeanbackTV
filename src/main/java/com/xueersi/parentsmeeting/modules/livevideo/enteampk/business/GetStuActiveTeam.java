package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;

/**
 * 战队和小组
 */
public interface GetStuActiveTeam {
    /**
     * 获得小组
     *
     * @param forseGet 强制刷新
     * @param callBack
     * @return
     */
    InteractiveTeam getStuActiveTeam(boolean forseGet, final AbstractBusinessDataCallBack callBack);

    /**
     * 获得战队信息
     *
     * @return
     */
    PkTeamEntity getPkTeamEntity();
}
