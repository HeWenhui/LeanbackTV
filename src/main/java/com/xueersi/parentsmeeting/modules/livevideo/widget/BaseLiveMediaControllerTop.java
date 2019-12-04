package com.xueersi.parentsmeeting.modules.livevideo.widget;

/**
 * Created by Zhang Yuansun on 2018/1/23.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.ControllerTopInter;
import com.xueersi.parentsmeeting.module.videoplayer.media.FractionalTouchDelegate;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 直播播放器控制栏顶部区域
 */
public class BaseLiveMediaControllerTop extends FrameLayout implements ControllerTopInter {
    String TAG = "BaseLiveMediaControllerTop";
    /** 播放器的控制监听 */
    protected LiveMediaController.MediaPlayerControl mPlayer;
    private LiveMediaController mMediaController;
    protected Context mContext;

    private Animation mAnimSlideInBottom;
    private Animation mAnimSlideOutBottom;

    /** 上方信息栏布局 */
    protected View mSystemInfoLayout;
    /** 顶部信息栏左边的回退按钮 */
    protected ImageView mBack;
    /** 上方信息栏的播放文件名显示控件 */
    protected TextView tvFileName;
    /** 底部控制栏右边的横竖屏切换按钮 */
    protected ImageView mAllView;
    /** 标题栏右侧按钮 */
    protected View vTitleRight;
    private AnimatorSet mAnimatorsetIn;
    private ObjectAnimator mTransOut;
    private AnimatorSet mAnimatorSetOut;

    public BaseLiveMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl mPlayer) {
        super(context);
        mContext = context;
        this.mPlayer = mPlayer;
        this.mMediaController = controller;
        initResources();
    }

    protected void initResources() {
        inflateLayout();
        findViewItems();
        // 初始化上下控制栏的动画
        mAnimSlideOutBottom = AnimationUtils.loadAnimation(mContext, com.xueersi.parentsmeeting.base.R.anim
                .anim_mediactrl_slide_out_bottom);
        mAnimSlideInBottom = AnimationUtils.loadAnimation(mContext, com.xueersi.parentsmeeting.base.R.anim
                .anim_mediactrl_slide_in_bottom);
        mAnimSlideOutBottom.setFillAfter(true);
        mAnimSlideInBottom.setFillAfter(true);
        mAnimSlideOutBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMediaController.hiderl_video_mediacontroller(); // 隐藏控制栏
                mMediaController.showButtons(false); // 隐藏系统按钮
                mMediaController.removeMessages(LiveMediaController.MSG_HIDE_SYSTEM_UI);
                mMediaController.sendEmptyMessage(LiveMediaController.MSG_HIDE_SYSTEM_UI); // 隐藏状态栏
                if(mSystemInfoLayout != null){
                    mSystemInfoLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });



    }

    /**
     * 初始化 属性动画
     */
    private void initAnim() {
        int screenHight = ScreenUtils.getScreenHeight();
        int height = 0;
        mSystemInfoLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        height = mSystemInfoLayout.getMeasuredHeight();
        if(height == 0){
            height  = SizeUtils.Dp2Px(mContext,35f);
        }
        ObjectAnimator  mTransIn = ObjectAnimator.ofFloat(mSystemInfoLayout,"translationY",-height,0);
        ObjectAnimator  alphaIn =  ObjectAnimator.ofFloat(mSystemInfoLayout,"alpha",0.0f,1.0f);
        mAnimatorsetIn = new AnimatorSet();
        mAnimatorsetIn.setDuration(300);
        mAnimatorsetIn.playTogether(mTransIn,alphaIn);

        mTransOut = ObjectAnimator.ofFloat(mSystemInfoLayout,"translationY",0,-height);
        ObjectAnimator  alphaOut =  ObjectAnimator.ofFloat(mSystemInfoLayout,"alpha",1.0f,0.0f);
        mAnimatorSetOut = new AnimatorSet();
        mAnimatorSetOut.setDuration(300);
        mAnimatorSetOut.playTogether(mTransOut,alphaOut);

        mTransOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMediaController.hiderl_video_mediacontroller(); // 隐藏控制栏
                mMediaController.showButtons(false); // 隐藏系统按钮
                mMediaController.removeMessages(LiveMediaController.MSG_HIDE_SYSTEM_UI);
                mMediaController.sendEmptyMessage(LiveMediaController.MSG_HIDE_SYSTEM_UI); // 隐藏状态栏
            }
        });

    }

    /** 播放器的布局界面 */
    protected View inflateLayout() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top, this);
    }

    /** 初始化控制界面上的控制部件 */
    protected void findViewItems() {
        mSystemInfoLayout = findViewById(R.id.rl_video_mediacontroller_info_panel); // 上方信息栏
        tvFileName = (TextView) findViewById(R.id.tv_video_mediacontroller_filename); // 当前视频的名称
        mBack = (ImageView) findViewById(R.id.iv_video_mediacontroller_back);
        mAllView = (ImageView) findViewById(R.id.iv_video_mediacontroller_controls_allview);
        mBack.setOnClickListener(mBackClickListener);
        mAllView.setOnClickListener(mAllViewClickListener);
        vTitleRight = findViewById(R.id.iv_video_mark_points);
        FractionalTouchDelegate.setupDelegate(mSystemInfoLayout, mBack, new RectF(1.0f, 1f, 1.2f, 1.2f));
        // 初始化上下控制栏的动画
       // initAnim();
    }

    /** 回退监听 */
    private OnClickListener mBackClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mPlayer.stop(); // 回退操作
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                    .livevideo_quit_1712001));

        }
    };


    /** 横竖屏切换按钮点击监听 */
    protected OnClickListener mAllViewClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mPlayer.changeLOrP(); // 切换横竖屏
        }
    };

    @Override
    public void setFileName(String name) {
        tvFileName.setText(name);
    }

    @Override
    public void onShow() {
        mSystemInfoLayout.setVisibility(View.VISIBLE);
        //mAnimatorsetIn.start();
        mSystemInfoLayout.startAnimation(mAnimSlideInBottom);
    }

    @Override
    public void onHide() {
       // mAnimatorSetOut.start();
        mSystemInfoLayout.startAnimation(mAnimSlideOutBottom);
    }

    /** 设置横竖屏切换按钮是否显示 */
    public void setAutoOrientation(boolean autoOrientation) {
        if (autoOrientation) {
            mAllView.setVisibility(View.VISIBLE);
        } else {
            mAllView.setVisibility(View.INVISIBLE);
        }
    }

    /** 标记点按钮是否显示及监听 */
    public void setMarkPointsOp(boolean isShow, OnClickListener listener) {
        if (isShow) {
            vTitleRight.setVisibility(VISIBLE);
            vTitleRight.setOnClickListener(listener);
        } else {
            vTitleRight.setVisibility(GONE);
        }
    }
}
