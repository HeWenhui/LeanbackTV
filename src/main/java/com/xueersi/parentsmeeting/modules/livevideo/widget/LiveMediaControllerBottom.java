package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    private int mArts = 0;

    public LiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        Intent paramIntent = ((Activity) mContext).getIntent();
        mArts = paramIntent.getIntExtra("isArts", -1);
        pattern = paramIntent.getIntExtra("pattern", 0);
        isSmallEnglish = paramIntent.getBooleanExtra("isSmallEnglish", false);

        if (LiveVideoConfig.isPrimary) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_ps_switch_flow_bottom, this);
        } else if (LiveVideoConfig.isSmallChinese) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_switch_flow_bottom, this);
        } else if (isSmallEnglish) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_english_switch_flow_bottom, this);
        } else {
            if (pattern == 2) {
                return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
            } else {
                return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_normal_bottom, this);
            }
        }

    }

    @Override
    public void onHide() {
        if (LiveVideoConfig.isPrimary) {
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        } else if (LiveVideoConfig.isSmallChinese) {
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        } else if (isSmallEnglish) {
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        } else if (pattern == 1) {
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        } else {
            findViewById(R.id.rl_livevideo_common_word).setVisibility(GONE);
        }
        if (switchFlowView != null) {
            switchFlowView.setSwitchFlowPopWindowVisible(false);
        }
        super.onHide();
    }

    public void experience() {
        findViewById(R.id.bt_livevideo_message_flowers).setVisibility(INVISIBLE);
        findViewById(R.id.bt_livevideo_mark).setVisibility(INVISIBLE);
    }

}
