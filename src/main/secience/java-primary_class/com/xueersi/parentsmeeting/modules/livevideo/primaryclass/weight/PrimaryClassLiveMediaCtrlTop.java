package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;

/**
 * 半身直播 顶部控制栏
 *
 * @author linyuqiang
 * created  at 2019/5/16 16:28
 */
public class PrimaryClassLiveMediaCtrlTop extends BaseLiveMediaControllerTop {

    String mode = LiveTopic.MODE_TRANING;
    private View mainLiveView;
    private View tranLiveView;
    /** 直播间初始化参数 **/
    private LiveGetInfo mRoomInitData;
    private String mVideoName;

    public PrimaryClassLiveMediaCtrlTop(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }

    @Override
    protected View inflateLayout() {
        View view;

        if (mRoomInitData == null) {
            return super.inflateLayout();
        }

        if (isChHalfBodyLive()) {
            view = initChMediaCtr();
        } else {
            view = initScienceMediaCtr();
        }

        return view;
    }


    /**
     * 初始化理科半身直播顶部控制栏
     */
    private View initScienceMediaCtr() {
        View view = null;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_primary_class_mediacontroller_top,
                        this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top, this, false);
            }
            view = tranLiveView;
            addView(view);
        }
        return view;
    }


    /**
     * 初始化语文半身直播顶部控制栏
     */
    private View initChMediaCtr() {
        View view = null;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_primary_class_mediacontroller_top,
                        this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top, this, false);
            }
            view = tranLiveView;
            addView(view);
        }
        return view;
    }


    /**
     * 是否是语文半身直播
     *
     * @return
     */
    private boolean isChHalfBodyLive() {

        return mRoomInitData != null && mRoomInitData.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH;
    }


    @Override
    public void setAutoOrientation(boolean autoOrientation) {
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            super.setAutoOrientation(autoOrientation);
        }
    }

    @Override
    public void setFileName(String name) {
        // 保存视频名称
        mVideoName = name;
    }

    @Override
    public void setMarkPointsOp(boolean isShow, OnClickListener listener) {
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            super.setMarkPointsOp(isShow, listener);
        }
    }

    /**
     * 主辅导且切换
     *
     * @param mode
     * @param getInfo
     */
    public void onModeChange(String mode, LiveGetInfo getInfo) {
        mRoomInitData = getInfo;
        this.mode = mode;
        removeAllViewsInLayout();
        inflateLayout();
        findViewItems();
        showVideoName();
    }

    @Override
    protected void findViewItems() {
        super.findViewItems();
    }

    /**
     * 显示视频名称
     */
    private void showVideoName() {
//        if(LiveTopic.MODE_TRANING.equals(mode) || isChHalfBodyLive()){
//            super.setFileName(mVideoName);
//        }
        super.setFileName(mVideoName);
    }
}
