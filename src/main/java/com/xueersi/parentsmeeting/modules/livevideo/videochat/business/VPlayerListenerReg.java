package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface VPlayerListenerReg extends LiveProvide {
    void addVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener);

    void removeVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener);

    void releaseWithViewGone();

    void playVideoWithViewVisible();
}
