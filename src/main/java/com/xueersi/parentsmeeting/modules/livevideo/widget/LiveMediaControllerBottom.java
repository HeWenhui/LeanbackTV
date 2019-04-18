package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import java.util.HashMap;


/**
 * 直播播放器控制栏底部区域
 */
public class LiveMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";
    String id = "";

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
        isExperience = paramIntent.getBooleanExtra("isExperience", false);
        if (LiveVideoConfig.isPrimary) {
            id = "layout_livemediacontroller_psbottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_ps_switch_flow_bottom, this);
        } else if (LiveVideoConfig.isSmallChinese) {
            id = "layout_livemediacontroller_chs_bottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_switch_flow_bottom, this);
//            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_switch_flow_bottom, this);
        } else if (isSmallEnglish) {
            id = "layout_livemediacontroller_english_switch_flow_bottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_english_switch_flow_bottom, this);
        } else {
            if (pattern == 1 && !isExperience) {
                id = "layout_livemediacontroller_normal_bottom";
                return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_normal_bottom, this);
            } else {
                id = "layout_livemediacontroller_bottom";
                return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
            }
//            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_english_switch_flow_bottom, this);
        }
    }

    @Override
    public void onHide() {
        View view = null;
        String findid = "";
        if (LiveVideoConfig.isPrimary) {
            findid = "rl_livevideo_common_wordps";
            view = findViewById(R.id.rl_livevideo_common_word);
        } else if (LiveVideoConfig.isSmallChinese) {
            findid = "rl_livevideo_common_wordsc";
            view = findViewById(R.id.rl_livevideo_common_word);
        } else if (isSmallEnglish) {
            findid = "rl_livevideo_common_wordse";
            view = findViewById(R.id.rl_livevideo_common_word);
        } else if (pattern == 1) {
            findid = "rl_livevideo_common_wordpa";
            view = findViewById(R.id.rl_livevideo_common_word);
        } else {
            findid = "rl_livevideo_common_word4";
            view = findViewById(R.id.rl_livevideo_common_word);
        }
        if (view != null) {
            view.setVisibility(GONE);
        } else {
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("myid", "" + id);
                hashMap.put("findid", "" + findid);
                UmsAgentManager.umsAgentDebug(mContext, TAG + "_onhide", hashMap);
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
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
