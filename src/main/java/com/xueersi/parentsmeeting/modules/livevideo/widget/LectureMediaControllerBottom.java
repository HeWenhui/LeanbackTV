package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 直播播放器控制栏底部区域
 */
public class LectureMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LectureMediaControllerBottom";

    public LectureMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_lecture_mediacontroller_bottom, this);
    }
}
