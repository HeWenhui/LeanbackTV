package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
*直播间 直播UI 变化监听注册
*@author linyuqiang
*created  at 2019/6/7
*/
public interface LiveUIStateReg {
    /**
     * UI 模式切换  （三分屏切换为 全面屏）
     * @param listener
     */
    void addLiveUIStateListener(LiveUIStateListener listener);
}
