package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

public interface ILocalVideoController {
    /**
     * 设置总体时间
     *
     * @param totalTime
     */
    void setTotalTime(String totalTime);

    /**
     * 设置当前时间
     *
     * @param currentTime
     */
    void setCurrentTime(String currentTime);

    void pause();

    void start();

    void stop();

    void release();

    void seekTo(long pos);

    /**
     * 开始播放视频
     *
     * @param path
     * @param time
     */
    void startPlayVideo(String path, int time);
}
