package com.xueersi.parentsmeeting.modules.livevideo.videochat;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/6/23.
 */

public interface VideoChatEvent {
    void setVolume(float i, float i1);

    void showLongMediaController();

    AtomicBoolean getStartRemote();

    void stopPlay();

    void rePlay(boolean b);
}
