package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;

/**
 * Created by lyqai on 2018/3/21.
 */

public class LiveBackStandMediaControllerBottom extends MediaControllerBottom2 {
    public LiveBackStandMediaControllerBottom(Context context, MediaController2 controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    @Override
    protected View inflateLayout() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_livestand_mediacontroller_bottom, this);
        return view;
    }
}
