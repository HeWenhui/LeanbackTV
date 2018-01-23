package com.xueersi.parentsmeeting.modules.livevideo.widget;

/**
 * Created by Zhang Yuansun on 2018/1/23.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.videoplayer.media.ControllerBottomInter;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;

/**
 * 直播播放器控制栏顶部区域
 */
public class BaseLiveMediaControllerTop extends FrameLayout implements ControllerBottomInter{
    String TAG = "BaseLiveMediaControllerTop";
    /** 播放器的控制监听 */
    protected LiveMediaController.MediaPlayerControl mPlayer;
    private LiveMediaController controller;
    protected Context mContext;
    /** 顶部动画向下出现 */
    private Animation mAnimSlideInTop;
    /** 顶部动画向上隐藏 */
    private Animation mAnimSlideOutTop;

    /** 顶部信息栏左边的回退按钮 */
    private ImageView ivBack;
    /** 上方信息栏的播放文件名显示控件 */
    protected TextView tvFileName;
    /** 底部控制栏右边的横竖屏切换按钮 */
    private ImageView ivAllView;

    public BaseLiveMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl player) {
        super(context);
        mContext = context;
        mPlayer = player;
        this.controller = controller;
        initResources();
    }
    protected void initResources() {
        inflateLayout();
        findViewItems();
        mAnimSlideInTop = AnimationUtils.loadAnimation(mContext, R.anim.anim_mediactrl_slide_in_top);
        mAnimSlideOutTop = AnimationUtils.loadAnimation(mContext, R.anim.anim_mediactrl_slide_out_top);
        mAnimSlideInTop.setFillAfter(true);
        mAnimSlideOutTop.setFillAfter(true);
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top, this);
    }

    /** 初始化控制界面上的控制部件 */
    protected void findViewItems() {
        tvFileName = (TextView) findViewById(com.xueersi.parentsmeeting.base.R.id.tv_video_mediacontroller_filename); // 当前视频的名称
        ivBack = (ImageView) findViewById(com.xueersi.parentsmeeting.base.R.id.iv_video_mediacontroller_back);
        ivAllView = (ImageView) findViewById(com.xueersi.parentsmeeting.base.R.id.iv_video_mediacontroller_controls_allview);
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public TextView getTvFileName() {
        return tvFileName;
    }

    public ImageView getIvAllView() {
        return ivAllView;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void setProgress(long mDuration, long position) {

    }

    @Override
    public void updatePausePlay(boolean isPlaying) {

    }

    @Override
    public void setAutoOrientation(boolean autoOrientation) {

    }

    @Override
    public void setPlayNextVisable(boolean playNextVisable) {

    }

    @Override
    public void setSetSpeedVisable(boolean setSpeedVisable) {

    }
}
