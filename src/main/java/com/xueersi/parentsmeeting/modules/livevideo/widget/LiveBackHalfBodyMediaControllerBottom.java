package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom2;

/**
*半身直播 底部播放控制栏
*@author chekun
*created  at 2018/11/15 16:03
*/
public class LiveBackHalfBodyMediaControllerBottom extends MediaControllerBottom2 {
    public LiveBackHalfBodyMediaControllerBottom(Context context, MediaController2 controller, BackMediaPlayerControl
            player) {
        super(context, controller, player);
    }

    @Override
    protected View inflateLayout() {
        // TODO: 2018/11/15  返回对应的 UI
        return  null;
    }
}
