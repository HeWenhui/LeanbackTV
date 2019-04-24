package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import java.util.ArrayList;

/**
 * 半身直播 底部播放控制栏
 *
 * @author chenkun
 * @version 1.0, 2018/10/22 下午3:59
 */

public class LiveHalfBodyMediaControllerBottom extends BaseLiveMediaControllerBottom {

    private String mode = LiveTopic.MODE_TRANING;
    /**
     * UI 模式切换监听器
     */
    ArrayList<LiveUIStateListener> liveUIStateListeners = new ArrayList<>();
    View tranLiveView;
    View mainLiveView;

    /**
     * 直播间 初始化参数
     */
    private LiveGetInfo mRoomInintData;


    /**
     * 显示隐藏回调监听器
     */
    ControllerStateListener controllerStateListener;

    /**
     * 是否拦截 顶部控制栏自动隐藏
     */
    boolean interceptBtmMediaCtrHide;

    public LiveHalfBodyMediaControllerBottom(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    public View inflateLayout() {
        //在得到 详细的直播间初始化参数之前 返回默认布局信息
        if (mRoomInintData == null) {
            return super.inflateLayout();
        }

        View view;
        //语文半身直播 沿用 一期 交互方式
        if (isChHalfBodyLive()) {
            view = initChMediaCtr();
        } else {
            view = initScienceMediaCtr();
        }
        return view;
    }


    /**
     * 初始化 理科半身直播 底部媒体控制栏
     *
     * @return
     */
    private View initScienceMediaCtr() {
        View view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            mainLiveView =
                    LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_mediacontroller_bottom,
                            this, false);
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {

                if (LiveVideoConfig.isSmallChinese) {
                    tranLiveView =
                            LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_bottom,
                                    this, false);
                }else if (LiveVideoConfig.isPrimary) {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom
                            , this, false);
                } else{
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom,
                            this, false);
                }
            }
            view = tranLiveView;
            addView(view);
        }
        return view;
    }

    /**
     * 初始化 语文底部媒体控制栏
     *
     * @return
     */
    private View initChMediaCtr() {
        View view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView =
                        LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_mediacontroller_bottom_ch,
                                this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                if (LiveVideoConfig.isSmallChinese) {
                    tranLiveView =
                            LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_bottom,
                                    this, false);
                } else if (LiveVideoConfig.isPrimary) {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom
                            , this, false);
                } else {
                    tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom,
                            this, false);
                }
            }
            view = tranLiveView;
            addView(view);
        }
        return view;
    }


    /**
     * 是否是语文半身直播
      * @return
     */
    private boolean isChHalfBodyLive() {
        return mRoomInintData != null && mRoomInintData.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH;
    }


    /**
     * 根据不同直播流切换不同 底部控制栏
     *
     * @param mode
     * @param getInfo
     */
    public void onModeChange(String mode, LiveGetInfo getInfo) {
        mRoomInintData = getInfo;
        this.mode = mode;
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
        if (!interceptBtmMediaCtrHide) {
            super.onShow();
        }

        if (controllerStateListener != null) {
            controllerStateListener.onSHow();
        }
    }

    @Override
    public void onHide() {
        if (!interceptBtmMediaCtrHide) {
            super.onHide();
        }

        if (controllerStateListener != null) {
            controllerStateListener.onHide();
        }
    }


    /**
     * 拦截 显示隐藏 动画
     * @param intercept
     */
    public void interceptHideBtmMediaCtr(boolean intercept) {
        interceptBtmMediaCtrHide = intercept;
    }


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