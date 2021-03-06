package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.ActivityUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoScreenReceiver;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

/**
 * @author linyuqiang
 * @date 2018/6/22
 */
public class LivePlayerFragment extends TripleScreenBasePlayerFragment implements VideoView.SurfaceCallback, LiveMediaController
        .MediaPlayerControl {

    /** 播放器的控制对象 */
    protected LiveMediaController mMediaController;

    /** 是否完成了一系列的系统广播 */
    private boolean mReceiverRegistered = false;

    /** 是否显示控制栏 */
    protected boolean mIsShowMediaController = true;

    private OnVideoCreate onVideoCreate;

    /**
     * 在VideoFragment的onActivityCreated创建完成以后
     */
    public interface OnVideoCreate {
        void onVideoCreate();
    }

    public void setOnVideoCreate(OnVideoCreate onVideoCreate) {
        this.onVideoCreate = onVideoCreate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.setLogMethod(false);
        logger.d("onCreate:activity=" + activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        manageReceivers();
        logger.d("onActivityCreated");
        if (onVideoCreate != null) {
            onVideoCreate.onVideoCreate();
        }
    }

    public void setMediaController(LiveMediaController mediaController) {
        this.mMediaController = mediaController;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInitialized()) {
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Activity.KEYGUARD_SERVICE);
            if (!keyguardManager.inKeyguardRestrictedInputMode()) {
                // 如果当前并不是锁屏状态，则开始播放
                if (mIsShowMediaController) {
                    startPlayer();
                }
            }
        } else {
            if (mCloseComplete) {
                // 如果当前没有初始化，并且是已经播放完毕的状态则重新打开播放
                playNewVideo();
            }
        }
    }

    @Override
    protected void resumeRequest() {
        logger.d("resumeRequest:hasloss=" + hasloss);
        if (hasloss) {
            leftVolume = oldleftVolume;
            rightVolume = oldrightVolume;
            setVolume(leftVolume, rightVolume);
        }
        super.resumeRequest();
    }

    @Override
    public void onPause() {
        logger.d("onPause");
        super.onPause();
    }

    public void setIsPlayerEnable(boolean mIsPlayerEnable) {
        this.mIsPlayerEnable = mIsPlayerEnable;
    }

    /** 解锁广播 */
    private static final IntentFilter USER_PRESENT_FILTER = new IntentFilter(Intent.ACTION_USER_PRESENT);
    /** 屏幕点亮 */
    private static final IntentFilter SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_ON);

    static {
        // 同时监听屏幕被灭掉的监听
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }

    private VideoScreenReceiver mScreenReceiver;
    private UserPresentReceiver mUserPresentReceiver;

    private class UserPresentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRootActivity()) {
                startPlayer();
            }
        }
    }

    /** 视频是否正在前台播放(用于解锁后判断界面是否在最上层是的话就开始播放) */
    private boolean isRootActivity() {
        return ActivityUtils.isForceShowActivity(activity.getApplicationContext(), getClass().getName());
    }

    /** 管理广播（屏幕点亮、解锁、耳麦）的注册和释放 */
    private void manageReceivers() {
        if (!mReceiverRegistered) {
            // 屏幕点亮广播
            mScreenReceiver = new VideoScreenReceiver();
            activity.registerReceiver(mScreenReceiver, SCREEN_FILTER);
            // 解锁广播
            mUserPresentReceiver = new UserPresentReceiver();
            activity.registerReceiver(mUserPresentReceiver, USER_PRESENT_FILTER);
            mReceiverRegistered = true;
        } else {
            try {
                if (mScreenReceiver != null) {
                    activity.unregisterReceiver(mScreenReceiver);
                }
                if (mUserPresentReceiver != null) {
                    activity.unregisterReceiver(mUserPresentReceiver);
                }
            } catch (IllegalArgumentException e) {
            }
            mReceiverRegistered = false;
        }
    }

    /** 在所有资源初始化完毕后，调用开始播放 */
    public void startPlayer() {
        if (mIsPlayerEnable && isInitialized() && mScreenReceiver.screenOn && !vPlayer.isBuffering()) {
            // 播放器初始化完毕，屏幕点亮，没有缓冲
            if (!vPlayer.isPlaying()) {
                // 开始播放
                vPlayer.start();
            }
            vPlayer.startListenPlaying();
        }
    }

    @Override
    public void onDestroy() {
        logger.d("onDestroy");
        // 统计退出
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONDESTROY);
        // 注销广播
        manageReceivers();
        if (isInitialized()) {
            // 释放界面资源
            vPlayer.releaseSurface();
        }
        if (mServiceConnected) {
            // 链接置空
            mServiceConnected = false;
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        // 解绑播放的Service
                        vPlayer.onDestroy();
                        logger.d("onDestroy:vPlayer.onDestroy");
                    }
                }
            }.start();
        }
        if (isInitialized()) {
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized() && !vPlayer.isPlaying()) {
                            // 释放播放器资源
                            release();
                            logger.d("onDestroy:release");
                        } else {
                            logger.d("onDestroy:isInitialized,isPlaying=false");
                        }
                    }
                }
            }.start();
        } else {
            logger.d("onDestroy:isInitialized=false");
        }
        super.onDestroy();
    }

    /** 设置视频名称 */
    @Override
    protected void setFileName() {
        if (mUri != null) {
            String name = null;
            if (mUri.getScheme() == null || mUri.getScheme().equals("file")) {
                name = FileUtils.getFileName(mUri);
            } else {
                name = mUri.getLastPathSegment();
            }
            if (name == null) {
                name = "null";
            }
            if (mDisplayName == null) {
                mDisplayName = name;
            }
            mMediaController.setFileName(mDisplayName);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isInitialized()) {
            setVideoLayout(); // 设置播放器VideoView的布局样式
            if (mIsLand) {
                if (mMediaController != null) {
                    mMediaController.showSystemUi(false);
                }
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    /** 加载旋转屏时相关布局 */
    public void loadLandOrPortView(boolean isLand) {
        logger.d("loadLandOrPortView:isLand=" + isLand);
        mIsLand = isLand;
        if (viewRoot != null) {
            ViewGroup.LayoutParams lp = viewRoot.getLayoutParams();
            if (mIsLand) {
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.height = mPortVideoHeight;
                /* lp.height = VP.DEFAULT_PORT_HEIGHT; */
            }
//            viewRoot.setLayoutParams(lp);
            LayoutParamsUtil.setViewLayoutParams(viewRoot, lp);
        }
    }

    /** 判断当前为竖屏并且处于播放状态时，显示控制栏 */
    @Override
    public void showLongMediaController() {
        if (!mIsLand) {
            // 竖屏时长时间显示
            mMediaController.showLong();
        } else {
            // 横屏时短时间显示
            mMediaController.show();
        }
    }

    /** 加载视频异常时出现可重新刷新的背景界面 TODO */
    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
        updateRefreshImage();
    }

    /** 当前视频播放完毕 */
    @Override
    public void playComplete() {
        if (mDuration == 0 || mCurrentPosition < (mDuration - 5000)) {
            // 异常中断退出
            resultFailed(0, 0);
        } else {
            // 播放正常完成
            mIsEnd = true;
            resultComplete();
        }
    }

    /** 视频正常播放完毕退出时调用，非加载失败 */
    protected void resultComplete() {
        startPlayNextVideo();
    }

    /** 播放下一个视频 */
    protected void startPlayNextVideo() {

    }


    @Override
    public void next() {
        startPlayNextVideo();
    }

    @Override
    public boolean isPlaying() {
        return isInitialized() && vPlayer.isPlaying();
    }

    @Override
    public void onTitleShow(boolean show) {

    }

    protected void updateRefreshImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false,
                ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        ImageView ivRefresh = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id
                .iv_course_video_refresh_bg);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable
                        .livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into
                        (ivRefresh);
            }
        }
    }

    float oldleftVolume = 1.0f;
    float oldrightVolume = 1.0f;

    @Override
    public void onRealAudioGain(boolean gain) {
        logger.d("onAudioGain:gain=" + gain + ",oldleftVolume=" + oldleftVolume);
        if (gain) {
            setVolume(oldleftVolume, oldrightVolume);
        } else {
            // 会长时间的失去AudioFoucs,就不在监听远程播放
            oldleftVolume = leftVolume;
            oldrightVolume = rightVolume;
            setVolume(0.0f, 0.0f);
        }
    }
}
