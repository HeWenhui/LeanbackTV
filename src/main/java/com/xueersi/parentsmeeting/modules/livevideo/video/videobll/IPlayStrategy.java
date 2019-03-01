package com.xueersi.parentsmeeting.modules.livevideo.video.videobll;

/**
 * 播放策略
 */
public interface IPlayStrategy {
    void playNewVideo(String url, int protocol);
}
