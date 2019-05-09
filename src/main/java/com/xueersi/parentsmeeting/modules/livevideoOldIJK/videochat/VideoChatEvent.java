package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/6/23.
 * 接麦的一些事件
 */
public interface VideoChatEvent {
    void setVolume(float i, float i1);

    void showLongMediaController();

    AtomicBoolean getStartRemote();

    void stopPlay();

    void rePlay(boolean b);
}
