package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;

/**
 * Created by linyuqiang on 2018/7/13.
 * 更新本场成就金币
 */
public interface UpdateAchievement {
    int GET_TYPE_RED = 1;
    int GET_TYPE_QUE = 2;
    int GET_TYPE_TEAM = 3;

    void getStuGoldCount(Object method, int type);

    void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity);
}