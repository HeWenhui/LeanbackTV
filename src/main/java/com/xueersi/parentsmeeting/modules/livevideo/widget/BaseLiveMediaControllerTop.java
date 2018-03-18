package com.xueersi.parentsmeeting.modules.livevideo.widget;

/**
 * Created by Zhang Yuansun on 2018/1/23.
 */

import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.videoplayer.media.ControllerTopInter;
import com.xueersi.parentsmeeting.modules.videoplayer.media.FractionalTouchDelegate;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;

/**
 * 直播播放器控制栏顶部区域
 */
public class BaseLiveMediaControllerTop extends FrameLayout implements ControllerTopInter {
    String TAG = "BaseLiveMediaControllerTop";
    /** 播放器的控制监听 */
    protected LiveMediaController.MediaPlayerControl mPlayer;
    private LiveMediaController mMediaController;
    protected Context mContext;

    /** 底部动画向上出现 */
    private Animation mAnimSlideInBottom;
    /** 底部动画向下隐藏 */
    private Animation mAnimSlideOutBottom;

    /** 上方信息栏布局 */
    protected View mSystemInfoLayout;
    /** 顶部信息栏左边的回退按钮 */
    private ImageView mBack;
    /** 上方信息栏的播放文件名显示控件 */
    protected TextView tvFileName;
    /** 底部控制栏右边的横竖屏切换按钮 */
    private ImageView mAllView;
    /**标题栏右侧按钮*/
    private View vTitleRight;

    public BaseLiveMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl mPlayer) {
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
        mAnimSlideOutBottom = AnimationUtils.loadAnimation(mContext, com.xueersi.parentsmeeting.base.R.anim.anim_mediactrl_slide_out_bottom);
        mAnimSlideInBottom = AnimationUtils.loadAnimation(mContext, com.xueersi.parentsmeeting.base.R.anim.anim_mediactrl_slide_in_bottom);
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
                mMediaController.removeMessages(mMediaController.MSG_HIDE_SYSTEM_UI);
                mMediaController.sendEmptyMessage(mMediaController.MSG_HIDE_SYSTEM_UI); // 隐藏状态栏
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
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
        vTitleRight=findViewById(R.id.iv_video_mark_points);
        FractionalTouchDelegate.setupDelegate(mSystemInfoLayout, mBack, new RectF(1.0f, 1f, 1.2f, 1.2f));
    }

    /** 回退监听 */
    private OnClickListener mBackClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mPlayer.stop(); // 回退操作
        }
    };


    /** 横竖屏切换按钮点击监听 */
    private OnClickListener mAllViewClickListener = new OnClickListener() {

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
        mSystemInfoLayout.startAnimation(mAnimSlideInBottom);
    }

    @Override
    public void onHide() {
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
    /**标记点按钮是否显示及监听*/
    public void setMarkPointsOp(boolean isShow,OnClickListener listener){
        if(isShow){
            vTitleRight.setVisibility(VISIBLE);
            vTitleRight.setOnClickListener(listener);
        }else{
            vTitleRight.setVisibility(GONE);
        }
    }
}
