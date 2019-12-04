package com.xueersi.parentsmeeting.modules.livevideo.weight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import java.util.ArrayList;


/**
 * 半身直播 体验课 底部播放控制栏
 *
 * @author linyuqiang
 * created 2019/9/26 下午7:25
 * version 1.0
 */

public class LiveHalfBodyExpMediaCtrlBottom extends LiveMediaControllerBottom implements LiveUIStateReg {

    private int mode = ExperConfig.COURSE_STATE_0;
    private static final String TAG = "LiveHalfBodyExpMediaCtrBottom";
    ArrayList<LiveUIStateListener> liveUIStateListeners = new ArrayList<>();
    View tranLiveView;
    View mainLiveView;

    public LiveHalfBodyExpMediaCtrlBottom(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    public View inflateLayout() {

        View view;
        if (mode == ExperConfig.COURSE_STATE_2) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_exp_mediactr_bottom,
                        this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                if (LiveVideoConfig.isPrimary) {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom, this, false);
                } else if (LiveVideoConfig.isSmallChinese) {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_bottom, this, false);
                } else {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this, false);
                }
            }
            view = tranLiveView;
            addView(view);
        }
        return view;
    }

    /**
     * 根据不同直播流切换不同 底部控制栏
     *
     * @param mode
     */
    public void onModeChange(int mode) {
        this.mode = mode;
        logger.d("onModeChange:mode=" + mode);
        // removeAllViews();
        removeAllViewsInLayout();
        inflateLayout();
        findViewItems();
        //通知相关 UI 底部 控制栏改变
        noticeUIChange();
    }


    /**
     * 通知UI 状态改变
     */
    protected void noticeUIChange() {
        for (LiveUIStateListener listener : liveUIStateListeners) {
            listener.onViewChange(this);
        }
    }

    public void addLiveUIStateListener(LiveUIStateListener listener) {
        if (!liveUIStateListeners.contains(listener)) {
            liveUIStateListeners.add(listener);
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        if (controllerStateListener != null) {
            controllerStateListener.onSHow();
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        if (controllerStateListener != null) {
            controllerStateListener.onHide();
        }
    }

    ControllerStateListener controllerStateListener;


    public void setControllerStateListener(ControllerStateListener controllerStateListener) {
        this.controllerStateListener = controllerStateListener;
    }

    public interface ControllerStateListener {
        /**
         * 状态栏显示
         */
        void onSHow();

        /**
         * 状态栏隐藏
         */
        void onHide();
    }

}