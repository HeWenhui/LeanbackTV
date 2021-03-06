package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import java.util.ArrayList;


/**
*半身直播 体验课 底部播放控制栏
*@author chenkun
*created 2019/4/22 上午10:38
*version 1.0
*/

public class LiveHalfBodyExpMediaCtrBottom extends BaseLiveMediaControllerBottom implements LiveUIStateReg {

    /**半身直播体验课只有主讲*/
    private String mode = LiveTopic.MODE_TRANING;
    private static final String TAG = "LiveHalfBodyExpMediaCtrBottom";
    ArrayList<LiveUIStateListener> liveUIStateListeners = new ArrayList<>();
    View tranLiveView;
    View mainLiveView;

    public LiveHalfBodyExpMediaCtrBottom(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    public View inflateLayout() {

        View view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_exp_mediactr_bottom,
                        this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if(tranLiveView == null){
                if (LiveVideoConfig.isPrimary) {
                    tranLiveView  = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_psbottom, this,false);
                }else if(LiveVideoConfig.isSmallChinese) {
                    tranLiveView  = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_chs_bottom, this,false);
                }
                else {
                    tranLiveView  = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this,false);
                }
            }
            view = tranLiveView;
            addView(view);
        }
        return  view;
    }


    /**
     * 根据不同直播流切换不同 底部控制栏
     * @param mode
     */
    public void onModeChange(String mode){
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
        super.onShow();
        if(controllerStateListener != null){
            controllerStateListener.onSHow();
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        if(controllerStateListener != null){
            controllerStateListener.onHide();
        }
    }

    ControllerStateListener controllerStateListener;


    public void setControllerStateListener(ControllerStateListener controllerStateListener) {
        this.controllerStateListener = controllerStateListener;
    }

    public interface ControllerStateListener{
        /**
         *  状态栏显示
         */
        void onSHow();

        /**
         * 状态栏隐藏
         */
        void onHide();
    }

}