package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by linyuqiang on 2018/3/21.
 */
public class LiveStandMediaControllerTop extends BaseLiveMediaControllerTop {
    TextView tv_live_stand_title_time;

    public LiveStandMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }

    /** 播放器的布局界面 */
    @Override
    protected View inflateLayout() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_top, this);
        return view;
    }

    @Override
    protected void initResources() {
        super.initResources();
        tv_live_stand_title_time = findViewById(R.id.tv_live_stand_title_time);
    }
}
