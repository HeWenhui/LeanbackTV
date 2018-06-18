package com.xueersi.parentsmeeting.modules.livevideo.widget;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;

/**
 * 直播播放器控制栏底部区域
 */
public class LectureMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LectureMediaControllerBottom";

    public LectureMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_lecture_mediacontroller_bottom, this);
    }
}
