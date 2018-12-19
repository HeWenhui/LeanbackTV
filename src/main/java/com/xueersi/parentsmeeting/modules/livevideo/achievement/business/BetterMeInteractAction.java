package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;

/**
 * 英语小目标 互动
 *
 * @author zhangyuansun
 * created  at 2018/12/18
 */
public interface BetterMeInteractAction {
    void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity);
    void onReceiveBetterMe(AimRealTimeValEntity aimRealTimeValEntity);
}
