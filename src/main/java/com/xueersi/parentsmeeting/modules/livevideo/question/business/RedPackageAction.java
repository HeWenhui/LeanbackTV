package com.xueersi.parentsmeeting.modules.livevideo.question.business;

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
}
