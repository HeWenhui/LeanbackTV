package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;

/**
 * 英语小目标 互动
 *
 * @author zhangyuansun
 * created  at 2018/12/18
 */
public interface BetterMeInteractAction {
    void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity);
    void onReceiveBetterMe(BetterMeEntity betterMeEntity,boolean isNotce);
    void onBetterMeLate();
}
