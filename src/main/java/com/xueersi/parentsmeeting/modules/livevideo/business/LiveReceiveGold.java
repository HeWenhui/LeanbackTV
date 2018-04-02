package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;

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
    }

    @Override
    public void getReceiveGoldTeamStatus(int operateId, AbstractBusinessDataCallBack callBack) {
        mLiveBll.getReceiveGoldTeamStatus(operateId, callBack);
    }
}
