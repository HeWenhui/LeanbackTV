package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public interface LiveVPlayerListener extends VPlayerCallBack.VPlayerListener {
    void onBufferTimeOutRun();
}
