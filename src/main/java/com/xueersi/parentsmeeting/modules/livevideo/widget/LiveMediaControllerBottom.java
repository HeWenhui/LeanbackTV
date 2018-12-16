package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
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

    public LiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        if (LiveVideoConfig.isPrimary) {
            id = "layout_livemediacontroller_psbottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom, this);
        } else if (LiveVideoConfig.isSmallChinese) {
            id = "layout_livemediacontroller_chs_bottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_bottom, this);
        } else {
            id = "layout_livemediacontroller_bottom";
            return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
        }
    }

    @Override
    public void onHide() {
        View view;
        String findid = "";
        if (LiveVideoConfig.isPrimary) {
            findid = "rl_livevideo_common_wordps";
            view = findViewById(R.id.rl_livevideo_common_wordps);
        } else if (LiveVideoConfig.isSmallChinese) {
            findid = "rl_livevideo_common_word";
            view = findViewById(R.id.rl_livevideo_common_word);
        } else {
            findid = "rl_livevideo_common_word2";
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
        super.onHide();
    }

    public void experience() {
        findViewById(R.id.bt_livevideo_message_flowers).setVisibility(INVISIBLE);
        findViewById(R.id.bt_livevideo_mark).setVisibility(INVISIBLE);
    }

}
