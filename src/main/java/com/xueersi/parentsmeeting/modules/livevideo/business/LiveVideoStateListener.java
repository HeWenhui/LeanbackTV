package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2019-08-27 13:13
 */

public interface LiveVideoStateListener extends VideoAction {

    /**
     * 设置位置信息
     * @param liveVideoPoint
     */
    void setFirstParam(LiveVideoPoint liveVideoPoint);


    /**
     *
     * @param visibility
     */
    void setFirstBackgroundVisible(int visibility);

    /**
     * 播放完成
     */
    void playComplete();


    void onFail(int agr1,int agr2);


    void onFail(MediaErrorInfo info);



    void updateLoadingImage();

    void onPlayError();


    void onDestroy();


    void onPlaySuccess();


    void rePlay(boolean modeChange);

    void setVideoSwitchFlowStatus(int status,int pos);
}
