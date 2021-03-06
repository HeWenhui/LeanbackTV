package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
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
    //投票
    int GET_TYPE_VOTE = 5;
    void getStuGoldCount(Object method, int type);

    void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity);

    void onUpdateBetterMe(AimRealTimeValEntity aimRealTimeValEntity,boolean isShowBubble);

    void onReceiveBetterMe(BetterMeEntity betterMeEntity, boolean isShowBubble);
}
