package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.app.Fragment;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.ActivityUtils;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * @author linyuqiang
 * @date 2018/6/22
 */
public class VideoFragment extends BaseVideoFragment implements VideoView.SurfaceCallback, LiveMediaController.MediaPlayerControl {
    /** 视频的名称，用于显示在播放器上面的信息栏 */
    private String mDisplayName;
    /** 是否从头开始播放 */
    private boolean mFromStart = true;
    /** 开始播放的起始点位 */
    private long mStartPos;
    /** 当前视频是否播放到了结尾 */
    protected boolean mIsEnd = false;

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
        logger.d("onResume");
        super.onResume();
        mIsPlayerEnable = true;
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
    public void onPause() {
        logger.d("onPause");
        mIsPlayerEnable = false;
        super.onPause();
    }

    /** 解锁广播 */
    private static final IntentFilter USER_PRESENT_FILTER = new IntentFilter(Intent.ACTION_USER_PRESENT);
    /** 屏幕点亮 */
    private static final IntentFilter SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_ON);

    static {
        // 同时监听屏幕被灭掉的监听
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }

    private ScreenReceiver mScreenReceiver;
    private UserPresentReceiver mUserPresentReceiver;

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
            mReceiverRegistered = true;
        } else {
            try {
                if (mScreenReceiver != null)
                    activity.unregisterReceiver(mScreenReceiver);
                if (mUserPresentReceiver != null)
                    activity.unregisterReceiver(mUserPresentReceiver);
            } catch (IllegalArgumentException e) {
            }
            mReceiverRegistered = false;
        }
    }

    /** 在所有资源初始化完毕后，调用开始播放 */
    protected void startPlayer() {
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
    protected void setFileName() {
        if (mUri != null) {
            String name = null;
            if (mUri.getScheme() == null || mUri.getScheme().equals("file"))
                name = FileUtils.getFileName(mUri);
            else
                name = mUri.getLastPathSegment();
            if (name == null)
                name = "null";
            if (mDisplayName == null)
                mDisplayName = name;
            mMediaController.setFileName(mDisplayName);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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
    public void showLongMediaController() {
        if (!mIsLand) {
            // 竖屏时长时间显示
            mMediaController.showLong();
        } else {
            // 横屏时短时间显示
            mMediaController.show();
        }
    }

    protected void playNewVideo() {
        if (mUri != null && mDisplayName != null) {
            playNewVideo(mUri, mDisplayName);
        }
    }

    public void playNewVideo(Uri uri, String displayName) {
        if (isInitialized()) {
            vPlayer.release();
            vPlayer.releaseContext();
        }
        mDisplayName = "";
        mIsHWCodec = false;
        mFromStart = false;
        mStartPos = 0;
        mIsEnd = false;

        mUri = uri;
        mDisplayName = displayName;

        if (viewRoot != null) {
            viewRoot.invalidate();
        }
        if (mOpened != null) {
            mOpened.set(false);
        }

        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }

    public void playNewVideo(Uri uri, String displayName, String shareKey) {
        if (isInitialized()) {
            vPlayer.release();
            vPlayer.releaseContext();
        }
        mDisplayName = "";
        mIsHWCodec = false;
        mFromStart = false;
        mStartPos = 0;
        mIsEnd = false;

        mUri = uri;
        mDisplayName = displayName;

        if (viewRoot != null)
            viewRoot.invalidate();
        if (mOpened != null)
            mOpened.set(false);

        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }


    /** 视频非正常播放完毕，有可能是断网了，也有可能一开始打开失败了 */
    protected void resultFailed(int arg1, int arg2) {
        showRefresyLayout(arg1, arg2);
    }

    /** 加载视频异常时出现可重新刷新的背景界面 TODO */
    protected void showRefresyLayout(int arg1, int arg2) {
        if (videoBackgroundRefresh == null) {
            return;
        }
        videoBackgroundRefresh.setVisibility(View.VISIBLE);
        updateRefreshImage();
        TextView errorInfo = (TextView) videoBackgroundRefresh.findViewById(R.id.tv_course_video_errorinfo);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errorInfo.setVisibility(View.VISIBLE);
            errorInfo.setText(error.getNum() + " (" + error.getTag() + ")");
        } else {
            errorInfo.setVisibility(View.GONE);
        }
        videoBackgroundRefresh.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    /** 当前视频播放完毕 */
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

    /** 控制开始播放视频 */
    @Override
    public void start() {
        if (isInitialized())
            vPlayer.start();
    }

    /** 控制视频暂停 */
    @Override
    public void pause() {
        if (isInitialized())
            vPlayer.pause();
    }

    /** 停止（按了返回键） */
    @Override
    public void stop() {
        onBackPressed();
    }

    @Override
    public void seekTo(long pos) {
        if (isInitialized())
            // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));
            vPlayer.seekTo(pos);
        mShareDataManager.put(mUri + VP.SESSION_LAST_POSITION_SUFIX, (long) 0, ShareDataManager.SHAREDATA_USER);//重置播放进度
    }

    @Override
    public void next() {
        startPlayNextVideo();
    }

    @Override
    public boolean isPlaying() {
        if (isInitialized())
            return vPlayer.isPlaying();
        return false;
    }

    @Override
    public long getCurrentPosition() {
        if (isInitialized())
            return vPlayer.getCurrentPosition();
        // return (long) (getStartPosition() * vPlayer.getDuration());
        return 0;
    }

    @Override
    public long getDuration() {
        if (isInitialized())
            return vPlayer.getDuration();
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        if (isInitialized())
            return (int) (vPlayer.getBufferProgress() * 100);
        return 0;
    }

    @Override
    public float scale(float scaleFactor) {
        float userRatio = VP.DEFAULT_ASPECT_RATIO;
        int videoWidth = vPlayer.getVideoWidth();
        int videoHeight = vPlayer.getVideoHeight();
        float videoRatio = vPlayer.getVideoAspectRatio();
        float currentRatio = videoView.mVideoHeight / (float) videoHeight;

        currentRatio += (scaleFactor - 1);
        if (videoWidth * currentRatio >= LiveVideoConfig.VIDEO_MAXIMUM_WIDTH)
            currentRatio = LiveVideoConfig.VIDEO_MAXIMUM_WIDTH / (float) videoWidth;

        if (videoHeight * currentRatio >= LiveVideoConfig.VIDEO_MAXIMUM_HEIGHT)
            currentRatio = LiveVideoConfig.VIDEO_MAXIMUM_HEIGHT / (float) videoHeight;

        if (currentRatio < 0.5f)
            currentRatio = 0.5f;

        videoView.mVideoHeight = (int) (videoHeight * currentRatio);
        videoView.setVideoLayout(mVideoMode, userRatio, videoWidth, videoHeight, videoRatio);
        return currentRatio;
    }

    @Override
    public void onTitleShow(boolean show) {

    }

    protected void updateRefreshImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        ImageView ivRefresh = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id.iv_course_video_refresh_bg);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivRefresh);
        }
    }


}
