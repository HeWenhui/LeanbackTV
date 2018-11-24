package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;


/**
 * 直播播放器控制栏底部区域
 */
public class LiveMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";

    public LiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        if (LiveVideoConfig.isPrimary) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom, this);
        } else {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
        }
    }

    @Override
    public void onHide() {
        if(LiveVideoConfig.isPrimary){
            findViewById(R.id.rl_livevideo_common_wordps).setVisibility(GONE);
        }else{
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        }
        super.onHide();
    }

    public void experience() {
        findViewById(R.id.bt_livevideo_message_flowers).setVisibility(INVISIBLE);
        findViewById(R.id.bt_livevideo_mark).setVisibility(INVISIBLE);
    }

}
