package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;

/**
 * Created by linyuqiang on 2018/7/13.
 * 更新本场成就金币
 */
public interface UpdateAchievement {
    //红包
    int GET_TYPE_RED = 1;
    //互动题
    int GET_TYPE_QUE = 2;
    //战队pk
    int GET_TYPE_TEAM = 3;
    //智能测评
    int GET_TYPE_INTELLIGENT_RECOGNITION = 4;

    void getStuGoldCount(Object method, int type);

    void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity);

    void updateGoldCount(Object method, int type, int goldCount, int starCount);
}
