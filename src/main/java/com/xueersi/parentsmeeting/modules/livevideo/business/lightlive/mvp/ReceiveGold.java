package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.mvp;

public interface ReceiveGold {
    /**
     * 请求获得红包
     *
     * @param operateId
     * @param onRedPackageSend
     */
    void sendReceiveGold(int operateId, OnRedPackageSend onRedPackageSend);

    public interface OnRedPackageSend {
        void onReceiveGold(int gold);

        void onReceiveFail();

        void onReceiveError(int errStatus, String failMsg, int code);

        void onHaveReceiveGold();
    }
}
