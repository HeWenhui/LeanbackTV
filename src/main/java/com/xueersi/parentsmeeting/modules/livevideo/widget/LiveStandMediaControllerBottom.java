package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;

/**
 * 直播播放器控制栏底部区域
 */
public class LiveStandMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";

    public LiveStandMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
    }

    @Override
    public void onHide() {
        super.onHide();
    }

}
