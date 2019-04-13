package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

/**
*直播间 直播UI 变化监听
*@author chekun
*created  at 2019/3/21 11:23
*/
public interface LiveUIStateListener {
    /**
     * UI 模式切换  （三分屏切换为 全面屏）
     * @param baseLiveMediaControllerBottom
     */
    void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom);
}
