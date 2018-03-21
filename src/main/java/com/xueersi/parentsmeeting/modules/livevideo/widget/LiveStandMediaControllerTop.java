package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;

/**
 * Created by lyqai on 2018/3/21.
 */
public class LiveStandMediaControllerTop extends BaseLiveMediaControllerTop {
    public LiveStandMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }

    /** 播放器的布局界面 */
    protected View inflateLayout() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_top, this);
        return view;
    }
}
