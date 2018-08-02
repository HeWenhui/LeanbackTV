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
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * @author lyqai
 * @date 2018/6/22
 */
public class LiveBackPlayVideoFragment extends Fragment implements VideoView.SurfaceCallback, MediaPlayerControl {
    Logger logger = LoggerFactory.getLogger("LiveBackPlayVideoFragment");
    BaseActivity activity;
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 所在的Activity是否已经onCreated */
    private boolean mCreated = false;
    /** 视频的名称，用于显示在播放器上面的信息栏 */
    private String mDisplayName;
    /** 是否使用硬解码，如当是本地采集的视频 */
    private boolean mIsHWCodec = false;
    /** 是否从头开始播放 */
    private boolean mFromStart = true;
    /** 是否可以播放视频 */
    protected boolean mIsPlayerEnable = true;
    /** 开始播放的起始点位 */
    private long mStartPos;
    /** 当前视频是否播放到了结尾 */
    protected boolean mIsEnd = false;
    /** 是否完成了当前视频的播放 */
    private boolean mCloseComplete = false;
    /** 当前播放的视频地址 */
    protected Uri mUri;
    /** 播放器界面的模式 */
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    /** 放播放器的 io.vov.vitamio.widget.CenterLayout */
    protected ViewGroup viewRoot;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    /** 加载中动画的加载文字 */
    private TextView tvVideoLoadingText;
    /** 播放器播放失败时的提供可刷新操作的背景 */
    protected View videoBackgroundRefresh;
    /** 加载中动画Loading */
    private View videoLoadingLayout;

    /** 当前播放进度 */
    protected long mCurrentPosition;
    /** 视频总时长 */
    protected long mDuration;
    /** 播放器统计时长 */
    private double mUMPlayVideoTime;

    // region 播放业务Handler
    private AtomicBoolean mOpened = new AtomicBoolean(Boolean.FALSE); // 线程安全的Boolean值
    /** 播放器的Surface是否创建 */
    private boolean mSurfaceCreated = false;
    /** 播放服务是否已连接 */
    private boolean mServiceConnected = false;
    /** 播放器的控制对象 */
    protected MediaController2 mMediaController;
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    /** 当前界面方向 */
    protected int mDirection = VideoOrientationEventListener.DIRECTION_UP;
    /** 是否可以自动横竖屏转换 */
    protected boolean mIsAutoOrientation = true;

    /** 是否点击了横竖屏切换按钮 */
    private boolean mClick = false;

    /** 点击进入横屏 */
    private boolean mClickLand = true;
    /** 点击进入竖屏 */
    private boolean mClickPort = true;
    /** 监听手机当前旋转角度 */
    private VideoOrientationEventListener mOrientationEventListener;
    /** 是否完成了一系列的系统广播 */
    private boolean mReceiverRegistered = false;
    /** 播放器的屏幕高 */
    private int mPortVideoHeight = 0;
    /** 是否显示控制栏 */
    protected boolean mIsShowMediaController = true;
    ShareDataManager mShareDataManager;

    /** 系统状态栏高度 */
    private int mStatusBarHeight = 0;
    float leftVolume = VP.DEFAULT_STEREO_VOLUME, rightVolume = VP.DEFAULT_STEREO_VOLUME;
    String video = "ijk";
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
        activity = (BaseActivity) getActivity();
        logger.d("onCreate:activity=" + activity);
        mShareDataManager = ShareDataManager.getInstance();
//        mPortVideoHeight = (int) LiveVideoConfig.VIDEO_HEIGHT;
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mIsLand) {
            mDirection = VideoOrientationEventListener.DIRECTION_RIGHT;
        }
        mOrientationEventListener = new VideoOrientationEventListener(activity);
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
        mCreated = true;
        manageReceivers();
        logger.d("onActivityCreated");
        if (onVideoCreate != null) {
            onVideoCreate.onVideoCreate();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        logger.d("onCreateView");
        // 播放器所在的io.vov.vitamio.widget.CenterLayout
        viewRoot = (ViewGroup) inflater.inflate(R.layout.live_video_center, container, false);
        videoView = viewRoot.findViewById(R.id.vv_course_video_video); // 播放器的videoView
        videoView.initialize(activity, this, mIsHWCodec); // 初始化播放器所在的画布
        tvVideoLoadingText = viewRoot.findViewById(R.id.tv_course_video_loading_tip); // 加载进度文字框
        videoLoadingLayout = viewRoot.findViewById(R.id.rl_course_video_loading); // 加载进度动画
        return viewRoot;
    }

    public void setMediaController(MediaController2 mediaController) {
        this.mMediaController = mediaController;
    }

    public PlayerService createPlayer() {
        vPlayer = new PlayerService(activity);
        vPlayer.onCreate();
        mServiceConnected = true;
        if (mSurfaceCreated)
            // 链接成功后尝试开始播放
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        // 设置当前是否为横屏
        setFileName(); // 设置视频显示名称
        showLongMediaController();
        return vPlayer;
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
                    if (mHeadsetPlaying)
                        startPlayer();
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
                if (mScreenReceiver != null)
                    activity.unregisterReceiver(mScreenReceiver);
                if (mUserPresentReceiver != null)
                    activity.unregisterReceiver(mUserPresentReceiver);
                if (mHeadsetPlugReceiver != null)
                    activity.unregisterReceiver(mHeadsetPlugReceiver);
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
        super.onDestroy();
    }

    /** 释放播放器资源 */
    private void release() {
        if (vPlayer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                vPlayer.release();
                vPlayer.releaseContext();
            } else {
                vPlayer.release();
                vPlayer.releaseContext();
            }
        }
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

    /** 设置播放器的界面布局 */
    protected void setVideoLayout() {
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, vPlayer.getVideoWidth(),
                vPlayer.getVideoHeight(), vPlayer.getVideoAspectRatio());
    }

    /** 判断当前为竖屏并且处于播放状态时，显示控制栏 */
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

    public void playNewVideo() {
        if (mUri != null && mDisplayName != null) {
            playNewVideo(mUri, mDisplayName);
        }
    }

    public void playNewVideo(Uri uri, String displayName) {
        logger.d("playNewVideo:uri=" + uri);
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

    /** 同步锁 */
    private Object mOpenLock = new Object();
    /** 准备打开播放文件 */
    private static final int OPEN_FILE = 0;
    /** 初始化完播放器准备加载播放文件 */
    private static final int OPEN_START = 1;
    /** 缓冲完毕可以播放 */
    private static final int OPEN_SUCCESS = 2;
    /** 打开失败 */
    private static final int OPEN_FAILED = 3;
    /** 硬解码失败 */
    private static final int HW_FAILED = 4;
    /** 初始化播放器的默认参数 */
    private static final int LOAD_PREFS = 5;
    /** 缓冲开始 */
    private static final int BUFFER_START = 11;
    /** 正在缓冲 */
    private static final int BUFFER_PROGRESS = 12;
    /** 缓冲结束 */
    private static final int BUFFER_COMPLETE = 13;
    /** 播放时的实时进度 */
    private static final int ON_PLAYING_POSITION = 14;
    /** 暂停播放 */
    private static final int STOP_PLAYER = 15;
    /** seek完成 */
    private static final int SEEK_COMPLETE = 16;
    /** 开始关闭播放器 */
    private static final int CLOSE_START = 21;
    /** 已退出播放器 */
    private static final int CLOSE_COMPLETE = 22;

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_FILE:
                    // 打开新的视频时长统计初始化
                    // 准备开始播放指定视频
                    synchronized (mOpenLock) {
                        if (!mOpened.get() && vPlayer != null) {
                            mOpened.set(true);
                            vPlayer.setVPlayerListener(vPlayerServiceListener);
                            if (vPlayer.isInitialized())
                                mUri = vPlayer.getUri();

                            if (videoView != null)
                                vPlayer.setDisplay(videoView.getHolder());
                            if (mUri != null)
                                vPlayer.initialize(mUri, video, 0, vPlayerServiceListener, mIsHWCodec);
                        }
                    }
                    break;
                case OPEN_START:
                    // 统计播放器初始化成功
                    XesMobAgent.userMarkVideoInit();
                    // 播放器初始化完毕准备开始加载指定视频
                    tvVideoLoadingText.setText(R.string.video_layout_loading);
                    onPlayOpenStart();
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case OPEN_SUCCESS:
                    // 统计播放开始
                    XesMobAgent.userMarkVideoPlay();
                    // 视频加载成功开始初始化一些播放参数，并开始播放和加载控制栏
                    loadVPlayerPrefs();
                    onPlayOpenSuccess();
                    setVideoLoadingLayoutVisibility(View.GONE);
                    setVideoLayout();
                    if (!mIsLand) {
                        mMediaController.showLong();
                    }
                    vPlayer.start();
                    showLongMediaController();
                    break;
                case OPEN_FAILED:
                    // 视频打开失败
                    int arg1 = msg.arg1, arg2 = msg.arg2;
                    resultFailed(arg1, arg2);
                    break;
                case STOP_PLAYER:
                    // 暂停播放
                    stopPlayer();
                    break;
                case SEEK_COMPLETE:
                    // seek完成
                    onSeekComplete();
                    break;
                case BUFFER_START:
                    // 网络视频缓冲开始
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    break;
                case BUFFER_PROGRESS:
                    // 视频缓冲中进行进度更新
                    if (!vPlayer.isBuffering() || vPlayer.getBufferProgress() >= 100) {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    } else {
                        // 视频缓冲中进行进度更新,tvVideoLoadingText.getVisibility()==View.GONE
//                        tvVideoLoadingText.setText(getString(R.string.video_layout_buffering_progress,
//                                vPlayer.getBufferProgress()));
                        vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    }
                    break;
                case BUFFER_COMPLETE:
                    // 缓冲完毕
                    setVideoLoadingLayoutVisibility(View.GONE);
                    vPlayerHandler.removeMessages(BUFFER_PROGRESS);
                    break;
                case CLOSE_START:
                    // 开始退出播放
                    tvVideoLoadingText.setText(R.string.closing_file);
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    break;
                case CLOSE_COMPLETE:
                    // 播放器退出完毕，设置相应Boolean值
                    mCloseComplete = true;
                    break;
                case ON_PLAYING_POSITION:
                    // 播放中获取实时的进度
                    long[] arrPosition = (long[]) msg.obj;
                    if (arrPosition != null && arrPosition.length == 2) {
                        playingPosition(arrPosition[0], arrPosition[1]);
                    }
                    break;
                case HW_FAILED:
                    // 硬解码失败,尝试使用软解码初始化播放器
                    if (videoView != null) {
                        videoView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.initialize(activity, LiveBackPlayVideoFragment.this, false);
                    }
                    break;
                case LOAD_PREFS:
                    // 初始化一些播放器的配置参数
                    loadVPlayerPrefs();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private WeakHandler vPlayerHandler = new WeakHandler(callback);

    /** 加载播放器的默认设置参数 */
    private void loadVPlayerPrefs() {
        if (!isInitialized())
            return;
        // 初始化播放器的参数
        vPlayer.setBuffer(VP.DEFAULT_BUF_SIZE);
        if (AppUtils.getAvailMemory(activity) > (long) (1024 * 1024 * 500)) {
            // 如果当前手机可用运行时内存大于500MB就使用普通清晰度视频，否则使用低清晰度
            vPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
        } else {
            vPlayer.setVideoQuality(VP.DEFAULT_VIDEO_QUALITY);
        }

        vPlayer.setDeinterlace(VP.DEFAULT_DEINTERLACE);
        vPlayer.setVolume(leftVolume, rightVolume);
        if (videoView != null && isInitialized())
            setVideoLayout();
    }

    public void setVolume(float left, float right) {
        leftVolume = left;
        rightVolume = right;
        if (isInitialized()) {
            vPlayer.setVolume(left, right);
        }
    }

    public void setIsAutoOrientation(boolean mIsAutoOrientation) {
        this.mIsAutoOrientation = mIsAutoOrientation;
    }

    /** 准备加载新视频 */
    protected void onPlayOpenStart() {
        logger.d("onPlayOpenStart");
    }

    /** 视频预加载成功 */
    protected void onPlayOpenSuccess() {
        logger.d("onPlayOpenSuccess");
    }

    /** seek完成 */
    protected void onSeekComplete() {

    }

    /** 暂停播放 */
    protected void stopPlayer() {
        if (isInitialized()) {
            vPlayer.pause();
        }
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

    /** 加载缓冲进度动画 */
    private void setVideoLoadingLayoutVisibility(int visibility) {
        if (videoLoadingLayout != null) {
            videoLoadingLayout.setVisibility(visibility);
        }
    }

    /***
     * 正在播放中的实时进度回调
     *
     * @param currentPosition 当前播放的进度
     * @param duration        视频总时长(毫秒)
     * @author zouhao
     * @Create at: 2015-6-5 上午11:33:26
     */
    protected void playingPosition(long currentPosition, long duration) {
        this.mCurrentPosition = currentPosition;
        this.mDuration = duration;
    }

    /** 播放器核心服务监听 */
    protected PlayerService.VPlayerListener vPlayerServiceListener = new PlayerService.VPlayerListener() {

        /** 硬解码失败 */
        @Override
        public void onHWRenderFailed() {
            if (Build.VERSION.SDK_INT < 11 && mIsHWCodec) {
                vPlayerHandler.sendEmptyMessage(HW_FAILED);
                vPlayerHandler.sendEmptyMessageDelayed(HW_FAILED, 200); // 确保使用软解码初始化成功？？？
            }
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onHWRenderFailed();
            }
        }

        /** 开始准备播放 */
        @Override
        public void onOpenStart() {
            vPlayerHandler.sendEmptyMessage(OPEN_START);
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenStart();
            }
        }

        /** 视频预处理完毕可以随时播放了 */
        @Override
        public void onOpenSuccess() {
            if (mIsPlayerEnable) {
                mUMPlayVideoTime = 0;
                vPlayerHandler.sendEmptyMessage(OPEN_SUCCESS);
            } else {
                release();
            }
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenSuccess();
            }
            if (isInitialized()) {
                vPlayer.setVolume(leftVolume, rightVolume);
            }
        }

        /** 视频打开失败 */
        @Override
        public void onOpenFailed(int arg1, int arg2) {
            vPlayerHandler.sendMessage(vPlayerHandler.obtainMessage(OPEN_FAILED, arg1, arg2));
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenFailed(arg1, arg2);
            }
        }

        /** 缓冲开始 */
        @Override
        public void onBufferStart() {
            String s = "onBufferStart";
            vPlayerHandler.sendEmptyMessage(BUFFER_START);
            if (vPlayer != null) {
                vPlayer.stopListenPlaying();
            }
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferStart();
            }
        }

        /** 缓冲结束 */
        @Override
        public void onBufferComplete() {
            String s = "onBufferComplete";
            if (mIsPlayerEnable && vPlayer != null)
                vPlayerHandler.sendEmptyMessage(BUFFER_COMPLETE);
            if (vPlayer != null) {
                vPlayer.startListenPlaying();
            }
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferComplete();
            }
        }

        @Override
        public void onSeekComplete() {
            vPlayerHandler.sendEmptyMessage(SEEK_COMPLETE);
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onSeekComplete();
            }
        }

        /** 播放完毕 */
        @Override
        public void onPlaybackComplete() {
            playComplete();
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaybackComplete();
            }
        }

        /** 关闭开始 */
        @Override
        public void onCloseStart() {
            vPlayerHandler.sendEmptyMessage(CLOSE_START);
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onCloseStart();
            }
        }

        /** 关闭完成 */
        @Override
        public void onCloseComplete() {
            vPlayerHandler.sendEmptyMessage(CLOSE_COMPLETE);
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onCloseComplete();
            }
        }

        /** 设置VideoView */
        @Override
        public void onVideoSizeChanged(int width, int height) {
            if (videoView != null) {
                setVideoLayout();
            }
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onVideoSizeChanged(width, height);
            }
        }

        /** 下载进度 */
        @Override
        public void onDownloadRateChanged(int kbPerSec) {
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onDownloadRateChanged(kbPerSec);
            }
        }

        /** 实时播放进度 */
        @Override
        public void onPlaying(long currentPosition, long duration) {
            Message msg = Message.obtain();
            long[] arrLong = {currentPosition, duration};
            msg.obj = arrLong;
            msg.what = ON_PLAYING_POSITION;
            vPlayerHandler.sendMessage(msg);
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaying(currentPosition, duration);
            }
        }

        /** 播放出现错误 */
        @Override
        public void onPlayError() {
            LiveBackPlayVideoFragment.this.onPlayError();
            PlayerService.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlayError();
            }
        }
    };

    protected PlayerService.VPlayerListener getWrapListener() {
        return null;
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

    protected void onPlayError() {
        vPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(activity, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // region 播放器Surface界面回

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
        if (mServiceConnected)
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        if (vPlayer != null)
            vPlayer.setDisplay(holder);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        if (vPlayer != null && vPlayer.isInitialized()) {
            if (vPlayer.isPlaying()) {
                vPlayer.pause();
                vPlayer.setState(PlayerService.STATE_NEED_RESUME);
            }
            vPlayer.releaseSurface();
            if (mIsPlayerEnable && vPlayer.needResume())
                vPlayer.start();
        }
    }

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
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
    public void setSpeed(float speed) {
        if (isInitialized())
            // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));
            vPlayer.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        if (isInitialized())
            // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));
            vPlayer.getSpeed();
        return 1.0f;
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
    public boolean isLandSpace() {
        return mIsLand;
    }

    @Override
    public boolean isPlayInitialized() {
        return isInitialized();
    }

    @Override
    public void changeLOrP() {
        mClick = true;
        if (!mIsLand) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mIsLand = true;
            mClickLand = false;
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mIsLand = false;
            mClickPort = false;
        }
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
    public void toggleVideoMode(int mode) {

    }

    @Override
    public void removeLoadingView() {
        videoLoadingLayout.setVisibility(View.GONE);
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
    public int getVideoHeight() {
        if (mIsLand) {
            return videoView.mVideoHeight;
        } else {
            if (mStatusBarHeight == 0) {
                Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                mStatusBarHeight = frame.top;
            }
            return mPortVideoHeight;
            /* return VP.DEFAULT_PORT_HEIGHT; */
        }
    }

    @Override
    public void onShare() {

    }

    public final void onBackPressed() {
        // 这里需要写代码，如果是横屏则转换竖屏
        if (mIsLand) {
            // 如果是横屏则切换为竖屏
            if (mIsAutoOrientation) {
                changeLOrP();
            } else {
                onUserBackPressed();
//                super.onBackPressed();
            }
        } else {
            onUserBackPressed();
//            super.onBackPressed();
        }
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

    /**
     * 用户点击返回，判断是不是程序崩溃
     */
    protected void onUserBackPressed() {
        activity.onBackPressed();
//        activity.finish(LiveVideoConfig.VIDEO_CANCLE);
    }

    /**
     * Activity 设置了横竖
     *
     * @param requestedOrientation
     */
    public void setRequestedOrientation(int requestedOrientation) {
        mOrientationEventListener.setRequestedOrientation(requestedOrientation, true);
    }

    class VideoOrientationEventListener extends OrientationEventListener {
        /** 当前界面方向-上方 */
        public static final int DIRECTION_UP = 0;
        /** 当前界面方向-手机左侧抬起 */
        public static final int DIRECTION_LEFT = 1;
        /** 当前界面方向-手机右侧抬起 */
        public static final int DIRECTION_RIGHT = 2;
        /** 当前界面方向-下方-暂时没有 */
        public static final int DIRECTION_DOWN = 3;

        public VideoOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (((orientation >= 0) && (orientation <= 30)) || ((orientation <= 360) && (orientation >= 330))) {
                if (!mIsAutoOrientation) {
                    // 不自动旋转屏幕时退出
                    return;
                }
                if (mClick) {
                    if (mIsLand && !mClickLand) {
                        return;
                    } else {
                        mClickPort = true;
                        mClick = false;
                        mIsLand = false;
                    }
                } else {
                    if (mIsLand) {
                        mDirection = DIRECTION_UP;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, false);
                        mIsLand = false;
                        mClick = false;
                    }
                }
            } else if (((orientation >= 230) && (orientation <= 310))) {
                if (!mIsAutoOrientation && mDirection == DIRECTION_UP) {
                    // 不自动旋转屏幕,竖屏不能转横屏，但是横屏左右可切换
                    return;
                }
                if (mClick) {
                    if (!mIsLand && !mClickPort) {
                        return;
                    } else {
                        mClickLand = true;
                        mClick = false;
                        mIsLand = true;
                    }
                } else {
                    if (mDirection != DIRECTION_RIGHT) {
                        mDirection = DIRECTION_RIGHT;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, false);
                        mIsLand = true;
                        mClick = false;
                    }
                }
            } else if (((orientation >= 50) && (orientation <= 130))) {
                if (!mIsAutoOrientation && mDirection == DIRECTION_UP) {
                    // 不自动旋转屏幕,竖屏不能转横屏，但是横屏左右可切换
                    return;
                }
                if (mClick) {
                    if (!mIsLand && !mClickPort) {
                        return;
                    } else {
                        mClickLand = true;
                        mClick = false;
                        mIsLand = true;
                    }
                } else {
                    if (mDirection != DIRECTION_LEFT) {
                        mDirection = DIRECTION_LEFT;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, false);
                        mIsLand = true;
                        mClick = false;
                    }
                }
            }
        }

        public void setRequestedOrientation(int requestedOrientation, boolean fromActivity) {
            if (!fromActivity) {
                activity.setRequestedOrientation(requestedOrientation);
            }
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mDirection = DIRECTION_UP;
            } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mDirection = DIRECTION_RIGHT;
            } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mDirection = DIRECTION_LEFT;
            } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                mDirection = DIRECTION_DOWN;
            }
        }
    }
}
