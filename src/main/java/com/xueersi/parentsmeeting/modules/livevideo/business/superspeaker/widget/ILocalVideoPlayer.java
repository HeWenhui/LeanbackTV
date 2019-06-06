package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.view.SurfaceView;

import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;

public interface ILocalVideoPlayer {
    /**
     * 设置视频播放的View
     *
     * @param surfaceView
     */
    void setVideoView(SurfaceView surfaceView);

    /**
     * 开始播放视频
     *
     * @param path
     * @param time
     */
    void startPlayVideo(String path, int time);

    void pause();

    void start();

    void stop();

    void release();

    void seekTo(long pos);

    void setListener(VPlayerCallBack.VPlayerListener listener);
}
