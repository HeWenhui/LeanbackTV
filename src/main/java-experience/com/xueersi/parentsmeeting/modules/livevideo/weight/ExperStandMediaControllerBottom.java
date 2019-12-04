package com.xueersi.parentsmeeting.modules.livevideo.weight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import java.util.ArrayList;

/**
 * 直播播放器控制栏底部区域
 */
public class ExperStandMediaControllerBottom extends LiveMediaControllerBottom implements LiveUIStateReg {
    String TAG = "ExperStandMediaControllerBottom";
    View tranLiveView;
    View mainLiveView;
    int mode = ExperConfig.COURSE_STATE_0;
    ArrayList<LiveUIStateListener> onViewChanges = new ArrayList<>();

    public ExperStandMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    @Override
    protected void initResources() {
        super.initResources();
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        View view;
//        return LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
        if (ExperConfig.COURSE_STATE_2 == mode) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this, false);
                View bt_livevideo_message_flowers = tranLiveView.findViewById(R.id.bt_livevideo_message_flowers);
                bt_livevideo_message_flowers.setVisibility(GONE);
            }
            view = tranLiveView;
            addView(view);
        }
        logger.d("inflateLayout:mode=" + mode + ",mainLiveView=" + mainLiveView + ",tranLiveView=" + tranLiveView);
        return view;
    }

    public void onModeChange(int mode) {
        this.mode = mode;
        removeAllViews();
        inflateLayout();
        findViewItems();
        noticeUIChange();
    }

    /**
     * 通知UI 状态改变
     */
    protected void noticeUIChange() {
        for (LiveUIStateListener listener : onViewChanges) {
            listener.onViewChange(this);
        }
    }

    @Override
    public void addLiveUIStateListener(LiveUIStateListener listener) {
        if (!onViewChanges.contains(listener)) {
            onViewChanges.add(listener);
        }
    }
    /*public interface OnViewChange {
        void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom);
    }*/
}
