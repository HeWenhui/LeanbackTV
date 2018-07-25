package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by lyqai on 2018/7/25.
 */

public interface VideoPlayAction {
    void seekTo(long pos);

    void start();

    void pause();
}
