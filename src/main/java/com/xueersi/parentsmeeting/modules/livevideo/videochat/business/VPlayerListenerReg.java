package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;

/**
 * Created by lyqai on 2018/7/11.
 */

public interface VPlayerListenerReg {
    void addVPlayerListener(PlayerService.VPlayerListener vPlayerListener);

    void removeVPlayerListener(PlayerService.VPlayerListener vPlayerListener);
}
