package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
 * 半身直播 底部播放控制栏
 * @author chenkun
 * @version 1.0, 2018/10/22 下午3:59
 */

public class LiveHalfBodyMediaControllerBottom extends LiveStandMediaControllerBottom {

    private String mode = LiveTopic.MODE_TRANING;
    private static final String TAG = "LiveHalfBodyMediaControllerBottom";

    View tranLiveView;
    View mainLiveView;

    public LiveHalfBodyMediaControllerBottom(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    public View inflateLayout() {
        Log.e(TAG,"=====>inflateLayout called 000000:"+mode);

        View view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            Log.e(TAG,"=====>inflateLayout called 11111:"+ mainLiveView);
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_mediacontroller_bottom,
                        this, false);
            }
            view = mainLiveView;
            addView(view);
            Log.e(TAG,"=====>inflateLayout called 2222222:");
        } else {
            Log.e(TAG,"=====>inflateLayout called 33333333:"+tranLiveView);
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom,
                        this, false);
            }
            view = tranLiveView;
            addView(view);
            Log.e(TAG,"=====>inflateLayout called 444444:");
        }
        Log.e(TAG,"=====>inflateLayout called 55555:"+view);
        return  view;
    }


    /**
     * 根据不同直播流切换不同 底部控制栏
     * @param mode
     * @param getInfo
     */
    @Override
    public void onModeChange(String mode,LiveGetInfo getInfo){
        Log.e(TAG,"=======>onModeChange called:"+mode);
        this.mode = mode;
        removeAllViews();
        inflateLayout();
        findViewItems();
        //通知相关 UI 底部 控制栏改变
        noticeUIChange();
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

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        //super.onVisibilityChanged(changedView, visibility);
        if(changedView == this && controllerStateListener != null){
            controllerStateListener.onHide();
        }
        Log.e(TAG,"=====>onVisibilityChanged:"+visibility);
    }
}