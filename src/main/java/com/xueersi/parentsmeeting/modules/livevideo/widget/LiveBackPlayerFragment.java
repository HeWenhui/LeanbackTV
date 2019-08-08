package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ActivityUtils;
import com.xueersi.lib.framework.utils.ThreadMap;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.config.LogConfig;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.util.HashMap;

/**
 * @author linyuqiang
 * @date 2018/6/22
 */
public class LiveBackPlayerFragment extends BasePlayerFragment implements VideoView.SurfaceCallback,
        BackMediaPlayerControl {

    /** 播放器的控制对象 */
    protected MediaController2 mMediaController;
    /** 是否完成了一系列的系统广播 */
    private boolean mReceiverRegistered = false;
    /** 是否显示控制栏 */
    protected boolean mIsShowMediaController = true;
    protected float mySpeed = 1.0f;
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

    /**
     * 播放PS视频
     */
//    public void playPSVod() {
//        if (vPlayer != null) {
//            vPlayer.release();
//            vPlayer.psStop();
//        }
//        mDisplayName = "";
//        mIsHWCodec = false;
////        mFromStart = false;
//        mStartPos = 0;
//        mIsEnd = false;
////        mUri = uri;
////        mDisplayName = displayName;
//        if (viewRoot != null) {
//            viewRoot.invalidate();
//        }
//        if (mOpened != null) {
//            mOpened.set(false);
//        }
//
//        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
//    }

    /**
     * 切换播放地址
     */
//    public void changLine() {

//    }
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
        logger.d("onActivityCreated:Parent=" + videoView.getParent());
        if (videoView.getParent() != null) {
            if (onVideoCreate != null) {
                onVideoCreate.onVideoCreate();
            }
        } else {
            final long before = System.currentTimeMillis();
            videoView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap();
                    stableLogHashMap.put("time", "" + (System.currentTimeMillis() - before));
                    UmsAgentManager.umsAgentDebug(activity, "LiveBackPlayerFragment_onActivityCreated", stableLogHashMap.getData());
                    if (onVideoCreate != null) {
                        onVideoCreate.onVideoCreate();
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View view) {

                }
            });
        }
    }

    public void setMediaController(MediaController2 mediaController) {
        this.mMediaController = mediaController;
        mMediaController.setFileName(mDisplayName);
    }

    @Override
    public void onResume() {
        logger.d("onResume");
        super.onResume();
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
    /** 耳麦拔插广播 */
    private static final IntentFilter HEADSET_FILTER = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

    static {
        // 同时监听屏幕被灭掉的监听
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }

    private ScreenReceiver mScreenReceiver;
    private UserPresentReceiver mUserPresentReceiver;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;

    private class ScreenReceiver extends BroadcastReceiver {
        private boolean screenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenOn = false;
                stopPlayer();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenOn = true;
            }
        }
    }

    private class UserPresentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRootActivity()) {
                startPlayer();
            }
        }
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        /** 是否是正在播放时插拔耳机 */
        private boolean mHeadsetPlaying = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("state")) {
                int state = intent.getIntExtra("state", -1);
                if (state == 0) {
                    mHeadsetPlaying = isPlaying();
                    stopPlayer();
                } else if (state == 1) {
                    if (mHeadsetPlaying) {
                        startPlayer();
                    }
                }
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
            mScreenReceiver = new ScreenReceiver();
            activity.registerReceiver(mScreenReceiver, SCREEN_FILTER);
            // 解锁广播
            mUserPresentReceiver = new UserPresentReceiver();
            activity.registerReceiver(mUserPresentReceiver, USER_PRESENT_FILTER);
            // 耳麦广播
            mHeadsetPlugReceiver = new HeadsetPlugReceiver();
            activity.registerReceiver(mHeadsetPlugReceiver, HEADSET_FILTER);
            mReceiverRegistered = true;
        } else {
            try {
                if (mScreenReceiver != null) {
                    activity.unregisterReceiver(mScreenReceiver);
                }
                if (mUserPresentReceiver != null) {
                    activity.unregisterReceiver(mUserPresentReceiver);
                }
                if (mHeadsetPlugReceiver != null) {
                    activity.unregisterReceiver(mHeadsetPlugReceiver);
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
            // 解绑播放的Service
            vPlayer.onDestroy();
            // 链接置空
            mServiceConnected = false;
        }

        if (isInitialized() && !vPlayer.isPlaying()) {
            // 释放播放器资源
            release();
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
        if (mMediaController == null) {
            logger.d("showLongMediaController:mMediaController==null");
            return;
        }
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
    protected void playComplete() {
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

    /** 视频预加载成功 */
    protected void onPlayOpenSuccess() {
        if (mySpeed != 1.0f) {
            setSpeed(mySpeed);
        }
    }

    @Override
    public void setSpeed(float speed) {
        mySpeed = speed;
        String uuid = "" + ThreadMap.getInstance().getKey("play_setspeed_uid");
        HashMap<String, String> hmParams = new HashMap<>();
        hmParams.put("logtype", "backsetspeed");
        hmParams.put("speed", "" + speed);
        hmParams.put("muri", "" + mUri);
        hmParams.put("uuid", "" + uuid);
        if (isInitialized()) {
            vPlayer.setSpeed(speed);
            hmParams.put("mInitialized", "true");
        } else {
            hmParams.put("mInitialized", "false");
        }
        UmsAgentManager.umsAgentDebug(activity, LogConfig.PLAY_SET_SPEED, hmParams);
    }

    @Override
    public float getSpeed() {
        if (isInitialized())
        // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));
        {
            return vPlayer.getSpeed();
        }
        return 1.0f;
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
    public boolean isPlayInitialized() {
        return isInitialized();
    }


    @Override
    public void toggleVideoMode(int mode) {

    }


    @Override
    public void onShare() {

    }

    @Override
    public void startPlayVideo() {
        playPSVideo(streamId, protocol);
    }

    @Override
    public void setVideoStatus(int code, int status, String values) {

    }

    @Override
    public int onVideoStatusChange(int code, int status) {
        return 0;
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

    public VideoView getVideoView() {
        return videoView;
    }
}
