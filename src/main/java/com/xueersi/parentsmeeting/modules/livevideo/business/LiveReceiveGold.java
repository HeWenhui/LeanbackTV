package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.entity.UpdateAchievementEvent;

/**
 * Created by linyuqiang on 2018/4/2.
 * 站立直播金币接口连接
 */
public class LiveReceiveGold implements RedPackageStandBll.ReceiveGold {
    LiveBll mLiveBll;

    public LiveReceiveGold(LiveBll liveBll) {
        this.mLiveBll = liveBll;
    }

    @Override
    public void sendReceiveGold(int operateId, String liveId, AbstractBusinessDataCallBack callBack) {
        mLiveBll.sendReceiveGold(operateId, liveId, callBack);
    }

    @Override
    public void onReceiveGold() {
        mLiveBll.getStuGoldCount();
        // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
        //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));
    }

    @Override
    public void getReceiveGoldTeamStatus(int operateId, AbstractBusinessDataCallBack callBack) {
        mLiveBll.getReceiveGoldTeamStatus(operateId, callBack);
    }

    @Override
    public void getReceiveGoldTeamRank(int operateId, AbstractBusinessDataCallBack callBack) {
        mLiveBll.getReceiveGoldTeamRank(operateId, callBack);
    }
}
