package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 直播播放器控制栏底部区域
 */
public class LiveGroupClassMediaControllerBottom extends LiveStandMediaControllerBottom {

    public LiveGroupClassMediaControllerBottom(Context context, LiveMediaController controller,
                                               MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        Intent paramIntent = ((Activity) mContext).getIntent();
        isSmallEnglish = paramIntent.getBooleanExtra("isSmallEnglish", false);
        View view;
//        return LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
        if (mainLiveView == null) {
            mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this, false);
        }
        view = mainLiveView;
        addView(view);
        logger.d("inflateLayout:mode=" + mode + ",mainLiveView=" + mainLiveView + ",tranLiveView=" + tranLiveView);
        return view;
    }
}
