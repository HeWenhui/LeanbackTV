package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;

/**
 * 直播播放器控制栏底部区域
 */
public class LiveMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";

    public LiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
    }

    @Override
    public void onHide() {
        findViewById(R.id.rl_livevideo_common_word).setVisibility(INVISIBLE);
        super.onHide();
    }

    public void experience(){
        findViewById(R.id. bt_livevideo_message_flowers).setVisibility(INVISIBLE);
    }

}
