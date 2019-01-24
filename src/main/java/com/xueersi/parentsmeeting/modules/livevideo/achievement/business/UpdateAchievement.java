package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

/**
 * Created by linyuqiang on 2018/7/13.
 * 更新本场成就金币
 */
public interface UpdateAchievement {
    int GET_TYPE_RED = 1;
    int GET_TYPE_QUE = 2;

    void getStuGoldCount(int type);
}
