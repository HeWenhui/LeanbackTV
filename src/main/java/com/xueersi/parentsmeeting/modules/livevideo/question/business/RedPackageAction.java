package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

/**
 * @author linyuqiang
 * @date 2016/9/23
 * 红包事件
 */
public interface RedPackageAction {
    /**
     * 红包消息
     *
     * @param operateId
     * @param onReceivePackage
     */
    void onReadPackage(int operateId, OnReceivePackage onReceivePackage);

    /**
     * 请求到金币
     */
    interface OnReceivePackage {
        /**
         * 请求到金币
         *
         * @param operateId
         */
        void onReceivePackage(int operateId);
    }

    public interface ReceiveGold {
        /**
         * 请求获得红包
         *
         * @param operateId
         * @param liveId
         * @param callBack
         */
        void sendReceiveGold(final int operateId, String liveId, AbstractBusinessDataCallBack callBack);

    }

    public interface ReceiveGoldStand extends ReceiveGold{

        /**
         * 请求小组成员得到红包
         *
         * @param operateId
         * @param callBack
         */
        void getReceiveGoldTeamStatus(int operateId, AbstractBusinessDataCallBack callBack);

        /**
         * 请求小组成员得到红包的top3
         *
         * @param operateId
         * @param callBack
         */
        void getReceiveGoldTeamRank(int operateId, AbstractBusinessDataCallBack callBack);

        /**
         * 当请求到红包
         */
        void onReceiveGold();
    }
}
