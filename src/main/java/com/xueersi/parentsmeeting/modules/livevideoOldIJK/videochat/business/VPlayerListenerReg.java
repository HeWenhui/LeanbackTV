package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business;

import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;

/**
 * Created by lyqai on 2018/7/11.
 */

public interface VPlayerListenerReg {
    void addVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener);

    void removeVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener);
}
