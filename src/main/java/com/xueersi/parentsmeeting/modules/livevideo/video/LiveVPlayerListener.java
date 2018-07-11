package com.xueersi.parentsmeeting.modules.livevideo.video;

import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;

/**
 * Created by lyqai on 2018/7/11.
 */

public interface LiveVPlayerListener extends PlayerService.VPlayerListener {
    void onBufferTimeOutRun();
}
