package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.XesActivity;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ActivityUtils;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.parentsmeeting.module.audio.AudioPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.VPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LivePlayLog;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;


/***
 *
 * 旁听三分屏体验课Activity
 * 视频播放主界面
 *
 * @author 林玉强
 */
public class LiveVideoActivityBase extends XesActivity implements LiveMediaController.MediaPlayerControl,
        VideoView.SurfaceCallback {
    private String TAG = "LiveVideoActivityBaseLog";
    /** 布局默认资源 */
    protected int mLayoutVideo = R.layout.activity_video_live;
    /** 播放器可刷新布局 */
    protected int mLayoutBackgroundRefresh = R.layout.layout_video_resfresh;
    /** 视频加载失败的标识码 */
    public static final int RESULT_FAILED = -7;
    /** 所在的Activity是否已经onCreated */
    private boolean mCreated = false;
    /** 视频的名称，用于显示在播放器上面的信息栏 */
    private String mDisplayName;
    /** 是否从头开始播放 */
    private boolean mFromStart = true;
    /** 是否使用硬解码，如当是本地采集的视频 */
    private boolean mIsHWCodec = false;
    /** 播放服务是否已连接 */
    private boolean mServiceConnected = false;
    /** 播放器的Surface是否创建 */
    private boolean mSurfaceCreated = false;
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    /** 当前界面方向-上方 */
    protected static final int DIRECTION_UP = 0;
    /** 当前界面方向-手机左侧抬起 */
    protected static final int DIRECTION_LEFT = 1;
    /** 当前界面方向-手机右侧抬起 */
    protected static final int DIRECTION_RIGHT = 2;
    /** 当前界面方向-下方-暂时没有 */
    protected static final int DIRECTION_DOWN = 3;
    /** 当前界面方向 */
    protected int mDirection = DIRECTION_UP;
    /** 是否显示控制栏 */
    protected boolean mIsShowMediaController = true;

    /** 是否点击了横竖屏切换按钮 */
    private boolean mClick = false;

    /** 点击进入横屏 */
    private boolean mClickLand = true;

    /** 点击进入竖屏 */
    private boolean mClickPort = true;

    /** 当前视频是否播放到了结尾 */
    protected boolean mIsEnd = false;

    /** 系统状态栏高度 */
    private int mStatusBarHeight = 0;

    /** 播放器的屏幕高 */
    private int mPortVideoHeight = 0;

    /** 当前播放进度 */
    protected long mCurrentPosition;
    /** 视频总时长 */
    protected long mDuration;

    /** 监听手机当前旋转角度 */
    private OrientationEventListener mOrientationEventListener;

    /** 开始播放的起始点位 */
    private long mStartPos;
    /** 播放器界面的模式 */
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;

    /** 放播放器的 io.vov.vitamio.widget.CenterLayout */
    protected ViewGroup viewRoot;

    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;

    /** 播放器父布局 */
    protected RelativeLayout rlContent;

    /** 加载中动画Loading */
    private View videoLoadingLayout;

    /** 播放器播放失败时的提供可刷新操作的背景 */
    protected View videoBackgroundRefresh;

    /** 重新刷新 */
    private Button btnVideoRefresh;

    /** 刷新页面的回退按钮 */
    private ImageView ivBack;

    /** 加载中动画的加载文字 */
    private TextView tvVideoLoadingText;

    /** 当前播放的视频地址 */
    protected Uri mUri;

    /** 播放器的控制对象 */
    protected LiveMediaController mMediaController;

    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 直播帧数统计 */
    LivePlayLog livePlayLog;
    /** 是否可以自动横竖屏转换 */
    protected boolean mIsAutoOrientation = true;

    /** 是否可以播放视频 */
    protected boolean mIsPlayerEnable = true;

    /** 播放器统计时长 */
    private double mUMPlayVideoTime;

    /** live_report_play_duration 开始时间 */
    protected long reportPlayStarTime;
    /** 视频类型 */
    protected String mVideoType = MobEnumUtil.VIDEO_RECORDED;

    /** 是否可以播放当前视频 */
    protected boolean mIsEnalbePlayer = true;

    // region 解锁、屏幕点亮、耳麦拔插广播
    /** 解锁广播 */
    private static final IntentFilter USER_PRESENT_FILTER = new IntentFilter(Intent.ACTION_USER_PRESENT);
    /** 屏幕点亮 */
    private static final IntentFilter SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_ON);

    static {
        // 同时监听屏幕被灭掉的监听
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }

    /** 是否完成了一系列的系统广播 */
    private boolean mReceiverRegistered = false;
    /** 是否是正在播放时插拔耳机 */
    private boolean mHeadsetPlaying = false;
    /** 是否完成了当前视频的播放 */
    private boolean mCloseComplete = false;

    private ScreenReceiver mScreenReceiver;
    private UserPresentReceiver mUserPresentReceiver;

    /** 播放器请求 */
    public static final int VIDEO_REQUEST = 210;
    /** 播放器用户返回 */
    public static final int VIDEO_CANCLE = 211;
    /** 播放器java崩溃 */
    public static final int VIDEO_CRASH = 1200;

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

    /** 管理广播（屏幕点亮、解锁、耳麦）的注册和释放 */
    private void manageReceivers() {
        if (!mReceiverRegistered) {
            // 屏幕点亮广播
            mScreenReceiver = new ScreenReceiver();
            registerReceiver(mScreenReceiver, SCREEN_FILTER);
            // 解锁广播
            mUserPresentReceiver = new UserPresentReceiver();
            registerReceiver(mUserPresentReceiver, USER_PRESENT_FILTER);
            mReceiverRegistered = true;
        } else {
            try {
                if (mScreenReceiver != null)
                    unregisterReceiver(mScreenReceiver);
                if (mUserPresentReceiver != null)
                    unregisterReceiver(mUserPresentReceiver);
            } catch (IllegalArgumentException e) {
            }
            mReceiverRegistered = false;
        }
    }

    // endregion

    // region 播放业务Handler
    private AtomicBoolean mOpened = new AtomicBoolean(Boolean.FALSE); // 线程安全的Boolean值
    /** 同步锁 */
    private final Object mOpenLock = new Object();
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
    String video;
    float leftVolume = VP.DEFAULT_STEREO_VOLUME, rightVolume = VP.DEFAULT_STEREO_VOLUME;

    private Handler vPlayerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_FILE:
                    // 打开新的视频时长统计初始化
                    // 准备开始播放指定视频
                    synchronized (mOpenLock) {
                        if (!mOpened.get() && vPlayer != null) {
                            if (!MediaPlayer.getIsNewIJK()) {
                                mOpened.set(true);
                                vPlayer.setVPlayerListener(vPlayerServiceListener);
                                if (vPlayer.isInitialized()) {
                                    //这个地方可能会播放错误的地址，参照TripleScreenBasePlayerFragment
                                    Uri olduri = vPlayer.getUri();
                                    logger.d("playNewVideo:olduri=" + olduri);
                                    vPlayer.release();
                                    vPlayer.releaseContext();
                                }
                                if (videoView != null) {
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                if (mUri != null) {
                                    vPlayer.initialize(mUri, video, 0, vPlayerServiceListener, mIsHWCodec);
                                }
                            } else {
                                mOpened.set(true);
                                vPlayer.setVPlayerListener(vPlayerServiceListener);
                                if (videoView != null) {
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                vPlayer.psInit(MediaPlayer.VIDEO_PLAYER_NAME, 0, vPlayerServiceListener, mIsHWCodec);
                                if (isChangeLine) {
                                    try {
                                        vPlayer.changeLine(changeLinePos, protocol);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    isChangeLine = false;
                                } else {
                                    String userName = AppBll.getInstance().getAppInfoEntity().getChildName();
                                    String userId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                                    try {
                                        if (vPlayer.getPlayer() instanceof PSIJK) {
                                            vPlayer.getPlayer().setUserInfo(userName, userId);
                                        }
                                        vPlayer.playPSVideo(videoPath, protocol);
                                    } catch (IOException e) {
                                        vPlayerHandler.sendEmptyMessage(OPEN_FAILED);
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        StableLogHashMap map = new StableLogHashMap();
                                        map.put("userName", userName).
                                                put("userId", userId).
                                                put("videoPath", videoPath).
                                                put("protocol", String.valueOf(protocol));
                                        UmsAgentManager.umsAgentDebug(LiveVideoActivityBase.this, LiveLogUtils.DISPATCH_REQEUSTING, map.getData());
                                        CrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
                                        e.printStackTrace();
                                    }
                                }
                            }
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
                    // attachMediaController();
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

//                        stopPlayer(); 缓冲过程不暂停
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
                        videoView.initialize(LiveVideoActivityBase.this, LiveVideoActivityBase.this, false);
                    }
                    break;
                case LOAD_PREFS:
                    // 初始化一些播放器的配置参数
                    loadVPlayerPrefs();
                    break;
            }
        }
    };

    private String videoPath;
    /** 切换线路使用位置 */
    protected int changeLinePos;
    /** 当前使用的协议 */
    protected int protocol;
    /**
     * 使用切换线路，
     * true代表切换线路，走
     * {@link com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK#changePlayLine(int)}
     * 和{@link PSIJK#tryPlayLive()}
     * false代表不切换线路,直接走{@link com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK#playLive(String, int)}
     */
    protected boolean isChangeLine = false;
    /**
     * @param streamId 直播的话为channel_name
     * @param protocol 回放的话为videoPath
     */
    /** 直播类型 */
    public int liveType = 0;
    /** 直播，使用{@link PSIJK#playLive(String, int)} */
    public final static int PLAY_LIVE = 0;
    /** 回放，使用{@link PSIJK#playVod(String, int)} */
    public final static int PLAY_BACK = 1;
    /** 录播，使用{@link PSIJK#playFile(String, int)} */
    public final static int PLAY_TUTORIAL = 2;

    /**
     * PSIJK切换线路使用
     *
     * @param pos
     * @param protocol
     */
    public void changePlayLive(int pos, int protocol) {
        isChangeLine = true;
        this.changeLinePos = pos;
        this.protocol = protocol;
        if (protocol == MediaPlayer.VIDEO_PROTOCOL_RTMP || protocol == MediaPlayer.VIDEO_PROTOCOL_FLV || protocol == MediaPlayer.VIDEO_PROTOCOL_HLS) {
            this.liveType = PLAY_LIVE;
        } else if (protocol == MediaPlayer.VIDEO_PROTOCOL_MP4 || protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
            this.liveType = PLAY_BACK;
        }
        if (mCreated && vPlayer != null) {
//        if (vPlayer != null) {
            vPlayer.release();
            vPlayer.psStop();
        }
//        }
        //初始化

        mDisplayName = "";
        mIsHWCodec = false;
        mFromStart = false;
        mStartPos = 0;
        mIsEnd = false;
//        mUri = uri;
//        mDisplayName = displayName;

//        vPlayerHandler.post(new Runnable() {
//            @Override
//            public void run() {
        if (viewRoot != null) {
            viewRoot.postInvalidate();
        }
//            }
//        });
        if (mOpened != null) {
            mOpened.set(false);
        }
        vPlayerHandler.sendEmptyMessage(OPEN_FILE);

    }

    protected void playPSVideo(String videoPath, int protocol) {
        isChangeLine = false;
        logger.i("videoPath = " + videoPath);
        this.videoPath = videoPath;
        this.protocol = protocol;
        if (mCreated && vPlayer != null) {
            vPlayer.release();
            vPlayer.psStop();
        }
        mDisplayName = "";
        mIsHWCodec = false;
        mFromStart = false;
        mStartPos = 0;
        mIsEnd = false;
//        mUri = uri;
//        mDisplayName = displayName;
        if (viewRoot != null) {
            viewRoot.postInvalidate();
        }
        if (mOpened != null) {
            mOpened.set(false);
        }

        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }

    /** 赋值视频名称 */
    public void setmDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }
    // endregion

    // region 生命周期及系统调用
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
//        final Thread.UncaughtExceptionHandler defaultUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                finish(VIDEO_CRASH);
//                if (defaultUncaughtHandler != null) {
//                    defaultUncaughtHandler.uncaughtException(thread, ex);
//                } else {
//                    MobclickAgent.reportError(LiveVideoActivityBase.this, ex);
//                }
//            }
//        });
        FileLogger.runActivity = this;
        super.onCreate(savedInstanceState);
        // 统计视频点击某个视频
        XesMobAgent.userMarkVideoClick();
        // 注册事件
        EventBus.getDefault().register(this);
        sendPlayVideoHandler.sendEmptyMessageDelayed(1, 1000);
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mIsLand) {
            mDirection = DIRECTION_RIGHT;
        }
        mClick = false;
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(this);
        BaseApplication baseApplication = (BaseApplication) getApplication();
        baseApplication.addActivty(this);
        //showDialog(savedInstanceState);
        video = "ijk";
        onSelect(savedInstanceState, video);
    }

    private void showDialog(final Bundle savedInstanceState) {
        setContentView(R.layout.layout_select_video);
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.ll_layout_select_video_ijk) {
                    video = "ijk";
                } else if (id == R.id.ll_layout_select_video_android) {
                    video = "android";
                } else {
                    return;
                }
                onSelect(savedInstanceState, video);
            }
        };
        findViewById(R.id.ll_layout_select_video_ijk).setOnClickListener(listener);
        findViewById(R.id.ll_layout_select_video_android).setOnClickListener(listener);
    }

    private void onSelect(Bundle savedInstanceState, String video) {
        mOrientationEventListener = new OrientationEventListener(this) {

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
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            mIsLand = true;
                            mClick = false;
                        }
                    }
                }
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置在该页面音量控制键的音频流为媒体音量
        loadView(mLayoutVideo); // 初始化界面
        manageReceivers(); // 注册广播
        mCreated = true; // 界面onCreate完毕
        if (onVideoCreate(savedInstanceState)) {
            createPlayer();
            onVideoCreateEnd();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
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

    protected void onVideoCreateEnd() {

    }

    public void enableAutoSpeedPlay(VideoConfigEntity videoConfigEntity) {
        if (vPlayer != null && videoConfigEntity != null) {
            vPlayer.enableAutoSpeedPlay(videoConfigEntity.getWaterMark(), videoConfigEntity.getDuration());
        }

    }

    protected void createPlayer() {
        vPlayer = new PlayerService(this);
        vPlayer.onCreate();
        mServiceConnected = true;
        if (mSurfaceCreated)
            // 链接成功后尝试开始播放
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        // 设置当前是否为横屏
        setFileName(); // 设置视频显示名称
        showLongMediaController();
        livePlayLog = new LivePlayLog(this, true);
        livePlayLog.setvPlayer(vPlayer);
    }

    protected boolean onVideoCreate(Bundle savedInstanceState) {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mCreated)
            return;
    }


    @Override
    public void onResume() {
        super.onResume();
        FileLogger.runActivity = this;
        //关闭系统后台声音
        AudioPlayer.requestAudioFocus(this);

        // 设置视频可播放
        mIsPlayerEnable = true;
        if (!mCreated)
            return;
        if (isInitialized()) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!keyguardManager.inKeyguardRestrictedInputMode()) {
                // 如果当前并不是锁屏状态，则开始播放
                if (mIsShowMediaController) {
                    startPlayer();
                }
            }
        } else {
            if (mCloseComplete) {
                // 如果当前没有初始化，并且是已经播放完毕的状态则重新打开播放
                if (!MediaPlayer.getIsNewIJK()) {
                    playNewVideo();
                } else {
//                    String videoPath;
//
//                    if (url.contains("http") || url.contains("https")) {
//                        videoPath = DoPSVideoHandle.getPSVideoPath(url);
//                    } else {
//                        videoPath = url;
//                    }
//                    playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        AudioPlayer.abandAudioFocus(this);
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONPAUSE);
        // 设置视频不可播放
        mIsPlayerEnable = false;
        if (!mCreated)
            return;
        if (isInitialized()) {
            if (livePlayLog != null) {
                livePlayLog.onPause(0);
            }
            if (vPlayer != null && vPlayer.isPlaying()) {
                // 暂停播放
                stopPlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mCreated)
            return;
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONSTOP);
        // 友盟统计
        umPlayVideoTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCreated)
            return;
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
        if (livePlayLog != null) {
            livePlayLog.destory();
        }
        // 注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE; //
        // 设置当前屏幕是否横屏
        loadLandOrPortView(); // 重新加载界面
        if (isInitialized()) {
            setVideoLayout(); // 设置播放器VideoView的布局样式

            if (mIsLand) {
                if (mMediaController != null) {
                    mMediaController.showSystemUi(false);
                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
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

    /**
     * 用户点击返回，判断是不是程序崩溃
     */
    protected void onUserBackPressed() {
        if (vPlayer != null) {
            vPlayer.psExit();
        }
        finish(VIDEO_CANCLE);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        logger.i( "onKeyDown:keyCode=" + event.getKeyCode());
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        logger.i( "dispatchKeyEvent:keyCode=" + event.getKeyCode());
//        return super.dispatchKeyEvent(event);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            // 据查阅资料是用来解决某个GOOGLE自带的BUG的
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            super.onSaveInstanceState(outState);
        }
    }

    // endregion

    // region 播放管理业务

    /** 加载界面 */
    protected void loadView(int id) {
        setContentView(id);
        getWindow().setBackgroundDrawable(null);
        viewRoot = (ViewGroup) findViewById(R.id.cl_course_video_root);// 播放器所在的io.vov.vitamio.widget.CenterLayout
        videoView = (VideoView) findViewById(R.id.vv_course_video_video); // 播放器的videoView
        videoView.initialize(this, this, mIsHWCodec); // 初始化播放器所在的画布
        rlContent = (RelativeLayout) findViewById(R.id.rl_course_video_content);
        videoBackgroundRefresh = getLayoutInflater().inflate(mLayoutBackgroundRefresh, rlContent, false); // 失败时播放器显示的背景
        videoBackgroundRefresh.setVisibility(View.GONE);
        rlContent.addView(videoBackgroundRefresh);
        tvVideoLoadingText = (TextView) findViewById(R.id.tv_course_video_loading_tip); // 加载进度文字框
        videoLoadingLayout = findViewById(R.id.rl_course_video_loading); // 加载进度动画
        btnVideoRefresh = (Button) videoBackgroundRefresh.findViewById(R.id.btn_layout_video_resfresh); // 刷新按钮
        btnVideoRefresh.setOnClickListener(btnRefreshListener); // 刷新事件
        ivBack = (ImageView) videoBackgroundRefresh.findViewById(R.id.iv_layout_video_resfresh_back);
        ivBack.setOnClickListener(ivRefreshBackListener); // 刷新页面的回退
        mMediaController = new LiveMediaController(LiveVideoActivityBase.this, LiveVideoActivityBase.this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ((ViewGroup) findViewById(R.id.rl_course_video_live_controller_content)).addView(mMediaController, params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 使屏幕保持长亮
        loadLandOrPortView();
    }

    /** 加载旋转屏时相关布局 */
    protected void loadLandOrPortView() {
        LayoutParams lpr = rlContent.getLayoutParams();
        if (lpr == null) {
            return;
        }
        if (mIsLand) {
            lpr.height = LayoutParams.MATCH_PARENT;
        } else {
            lpr.height = mPortVideoHeight;
            // lpr.height = VP.DEFAULT_PORT_HEIGHT;
        }

        if (viewRoot != null) {
            LayoutParams lp = viewRoot.getLayoutParams();
            if (mIsLand) {
                lp.height = LayoutParams.MATCH_PARENT;
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

    /** 加载缓冲进度动画 */
    private void setVideoLoadingLayoutVisibility(int visibility) {
        if (videoLoadingLayout != null) {
            videoLoadingLayout.setVisibility(visibility);
        }
    }

    /** 加载视频异常时出现可重新刷新的背景界面 */
    protected void showRefresyLayout(int arg1, int arg2) {
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
        videoBackgroundRefresh.getLayoutParams().height = LayoutParams.MATCH_PARENT;

    }

    /** 加载播放器的默认设置参数 */
    private void loadVPlayerPrefs() {
        if (!isInitialized())
            return;
        // 初始化播放器的参数
        vPlayer.setBuffer(VP.DEFAULT_BUF_SIZE);
        if (AppUtils.getAvailMemory(LiveVideoActivityBase.this) > (long) (1024 * 1024 * 500)) {
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

    /** 播放一个新的视频 */
    @Deprecated
    protected void playNewVideo(Uri uri, String displayName) {
        //
        if (!MediaPlayer.getIsNewIJK()) {
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
                viewRoot.postInvalidate();
            }
            if (mOpened != null) {
                mOpened.set(false);
            }

            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        } else {
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
                viewRoot.postInvalidate();
            }
            if (mOpened != null) {
                mOpened.set(false);
            }

            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        }
    }

    public void setVolume(float left, float right) {
        leftVolume = left;
        rightVolume = right;
        if (isInitialized()) {
            vPlayer.setVolume(left, right);
        }
    }

    //    /**
//     * 播放一个新的视频
//     *
//     * @param uri
//     * @param displayName
//     * @param shareKey    用于标识当前视频的唯一值
//     * @author zouhao
//     * @Create at: 2015-9-23 下午7:45:41
//     */
//    protected void playNewVideo(Uri uri, String displayName, String shareKey) {
//        if (isInitialized()) {
//            vPlayer.release();
//            vPlayer.releaseContext();
//        }
//        mDisplayName = "";
//        mIsHWCodec = false;
//        mFromStart = false;
//        mStartPos = 0;
//        mIsEnd = false;
//
//        mUri = uri;
//        mDisplayName = displayName;
//
//        if (viewRoot != null)
//            viewRoot.postInvalidate();
//        if (mOpened != null)
//            mOpened.set(false);
//
//        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
//    }
    @Deprecated
    protected void playNewVideo() {
        if (mUri != null && mDisplayName != null) {
            playNewVideo(mUri, mDisplayName);
        }
    }

    /** 播放下一个视频 */
    protected void startPlayNextVideo() {

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

    /** 暂停播放 */
    protected void stopPlayer() {
        if (isInitialized()) {
            vPlayer.pause();
        }
    }

    /** seek完成 */
    protected void onSeekComplete() {

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

    /** 刷新界面重新加载视频 */
    protected void onRefresh() {
        if (mIsEnalbePlayer) {
            loadView(mLayoutVideo);
        }
    }

    /**
     * 是否允许视频界面自动横竖屏
     *
     * @param isAutoOrientation
     */
    public void setAutoOrientation(boolean isAutoOrientation) {
        mIsAutoOrientation = isAutoOrientation;
    }

    /** 视频正常播放完毕退出时调用，非加载失败 */
    protected void resultComplete() {
        startPlayNextVideo();
    }

    /** 视频非正常播放完毕，有可能是断网了，也有可能一开始打开失败了 */
    protected void resultFailed(int arg1, int arg2) {
        showRefresyLayout(arg1, arg2);
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

    protected void resetMediaController() {
        setFileName(); // 设置视频显示名称
        showLongMediaController();
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

    /** 设置视频名称 */
    protected void setFileName() {
        if (!MediaPlayer.getIsNewIJK()) {
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
        } else {
            if (mDisplayName != null) {
                mMediaController.setFileName(mDisplayName);
            }
        }
    }

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
    }

    /** 视频是否正在前台播放(用于解锁后判断界面是否在最上层是的话就开始播放) */
    private boolean isRootActivity() {
        return ActivityUtils.isForceShowActivity(getApplicationContext(), getClass().getName());
    }

    /** 播放异常刷新界面 */
    OnClickListener btnRefreshListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    /** 刷新界面上返回按钮 */
    OnClickListener ivRefreshBackListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    // endregion

    // region 播放器核心服务监听

    /** 播放器核心服务监听 */
    protected VPlayerListener vPlayerServiceListener = new VPlayerListener() {

        @Override
        public void getPSServerList(int cur, int total, boolean modeChange) {

        }

//        @Override
//        public void getPServerListFail() {
//
//        }

        /** 硬解码失败 */
        @Override
        public void onHWRenderFailed() {
            if (Build.VERSION.SDK_INT < 11 && mIsHWCodec) {
                vPlayerHandler.sendEmptyMessage(HW_FAILED);
                vPlayerHandler.sendEmptyMessageDelayed(HW_FAILED, 200); // 确保使用软解码初始化成功？？？
            }
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onHWRenderFailed();
            }
        }

        ;

        /** 开始准备播放 */
        @Override
        public void onOpenStart() {
            vPlayerHandler.sendEmptyMessage(OPEN_START);
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenStart();
            }
            if (livePlayLog != null) {
                livePlayLog.onOpenStart();
            }
        }

        /** 视频预处理完毕可以随时播放了 */
        @Override
        public void onOpenSuccess() {
            if (mIsPlayerEnable) {
                reportPlayStarTime = System.currentTimeMillis();
                mUMPlayVideoTime = 0;
                vPlayerHandler.sendEmptyMessage(OPEN_SUCCESS);
            } else {
                release();
            }
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenSuccess();
            }
            if (isInitialized()) {
                vPlayer.setVolume(leftVolume, rightVolume);
            }
            if (livePlayLog != null) {
                livePlayLog.onOpenSuccess();
            }
        }

        /** 视频打开失败 */
        @Override
        public void onOpenFailed(int arg1, int arg2) {
            vPlayerHandler.sendMessage(vPlayerHandler.obtainMessage(OPEN_FAILED, arg1, arg2));
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenFailed(arg1, arg2);
            }
            if (livePlayLog != null) {
                livePlayLog.onOpenFailed(arg1, arg2);
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
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferStart();
            }
            if (livePlayLog != null) {
                livePlayLog.onBufferStart();
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
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferComplete();
            }
            if (livePlayLog != null) {
                livePlayLog.onBufferComplete();
            }
        }

        @Override
        public void onSeekComplete() {
            vPlayerHandler.sendEmptyMessage(SEEK_COMPLETE);
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onSeekComplete();
            }
        }

        /** 播放完毕 */
        @Override
        public void onPlaybackComplete() {
            playComplete();
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaybackComplete();
            }
            if (livePlayLog != null) {
                livePlayLog.onPlaybackComplete();
            }
        }

        /** 关闭开始 */
        @Override
        public void onCloseStart() {
            vPlayerHandler.sendEmptyMessage(CLOSE_START);
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onCloseStart();
            }
        }

        /** 关闭完成 */
        @Override
        public void onCloseComplete() {
            vPlayerHandler.sendEmptyMessage(CLOSE_COMPLETE);
            VPlayerListener wrapListener = getWrapListener();
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
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onVideoSizeChanged(width, height);
            }
        }

        /** 下载进度 */
        @Override
        public void onDownloadRateChanged(int kbPerSec) {
            VPlayerListener wrapListener = getWrapListener();
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
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaying(currentPosition, duration);
            }
        }

        /** 播放出现错误 */
        @Override
        public void onPlayError() {
            LiveVideoActivityBase.this.onPlayError();
            VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlayError();
            }
            if (livePlayLog != null) {
                livePlayLog.onPlayError();
            }
        }
    };

    protected void onPlayError() {
        vPlayerHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(LiveVideoActivityBase.this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected VPlayerListener getWrapListener() {
        return null;
    }

    // endregion

    // region 播放器的控制界面间接调用的事件

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

    /** 控制视频跳转到指定点位 */
    @Override
    public void seekTo(long pos) {
        if (isInitialized())
            // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));
            vPlayer.seekTo(pos);
        mShareDataManager.put(mUri + VP.SESSION_LAST_POSITION_SUFIX, (long) 0, ShareDataManager.SHAREDATA_USER);//重置播放进度
    }

    /** 是否正在播放 */
    @Override
    public boolean isPlaying() {
        if (isInitialized())
            return vPlayer.isPlaying();
        return false;
    }

    /** 返回视频的总时长(毫秒) */
    @Override
    public long getDuration() {
        if (isInitialized())
            return vPlayer.getDuration();
        return 0;
    }

    /** 按钮切换横竖屏 */
    @Override
    public void changeLOrP() {
        mClick = true;
        if (!mIsLand) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mIsLand = true;
            mClickLand = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mIsLand = false;
            mClickPort = false;
        }
    }

    /** 返回当前播放进度点位(毫秒) */
    @Override
    public long getCurrentPosition() {
        if (isInitialized())
            return vPlayer.getCurrentPosition();
        // return (long) (getStartPosition() * vPlayer.getDuration());
        return 0;
    }

    /** 返回当前缓冲进度 */
    @Override
    public int getBufferPercentage() {
        if (isInitialized())
            return (int) (vPlayer.getBufferProgress() * 100);
        return 0;
    }

    /** 播放下一个视频 */
    @Override
    public void next() {
        startPlayNextVideo();
    }

    /** 视频预加载成功 */
    protected void onPlayOpenSuccess() {

    }

    /** 准备加载新视频 */
    protected void onPlayOpenStart() {

    }

    /** 播放器最大的高 */
    private static final int VIDEO_MAXIMUM_HEIGHT = 2048;
    /** 播放器最大的宽 */
    private static final int VIDEO_MAXIMUM_WIDTH = 2048;

    /** 双击切换当前界面显示比例 */
    @Override
    public float scale(float scaleFactor) {
        float userRatio = VP.DEFAULT_ASPECT_RATIO;
        int videoWidth = vPlayer.getVideoWidth();
        int videoHeight = vPlayer.getVideoHeight();
        float videoRatio = vPlayer.getVideoAspectRatio();
        float currentRatio = videoView.mVideoHeight / (float) videoHeight;

        currentRatio += (scaleFactor - 1);
        if (videoWidth * currentRatio >= VIDEO_MAXIMUM_WIDTH)
            currentRatio = VIDEO_MAXIMUM_WIDTH / (float) videoWidth;

        if (videoHeight * currentRatio >= VIDEO_MAXIMUM_HEIGHT)
            currentRatio = VIDEO_MAXIMUM_HEIGHT / (float) videoHeight;

        if (currentRatio < 0.5f)
            currentRatio = 0.5f;

        videoView.mVideoHeight = (int) (videoHeight * currentRatio);
        videoView.setVideoLayout(mVideoMode, userRatio, videoWidth, videoHeight, videoRatio);
        return currentRatio;
    }

    /** 关闭加载动画 */
    @Override
    public void removeLoadingView() {
        videoLoadingLayout.setVisibility(View.GONE);
    }

    /** 当前是否是横屏 */
    @Override
    public boolean isLandSpace() {
        return mIsLand;
    }

    /** 返回竖屏播放器的高度 */
    @Override
    public int getVideoHeight() {
        if (mIsLand) {
            return videoView.mVideoHeight;
        } else {
            if (mStatusBarHeight == 0) {
                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                mStatusBarHeight = frame.top;
            }
            return mPortVideoHeight;
            /* return VP.DEFAULT_PORT_HEIGHT; */
        }
    }

    @Override
    public void onTitleShow(boolean show) {

    }

    // endregion

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

    /** 统计观看视频时长 */
    @SuppressLint("HandlerLeak")
    private Handler sendPlayVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // 视频初始化完成，视频正在播放,统计观看时长
            if (isInitialized() && vPlayer != null && vPlayer.isPlaying()) {
                mUMPlayVideoTime++;
            }
            if (!isFinishing()) {
                sendPlayVideoHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    private void umPlayVideoTime() {
        double videoPlayTime = 1;

        if (mUMPlayVideoTime > 10) {
            if (MobEnumUtil.VIDEO_RECORDED.equals(mVideoType)) {
                videoPlayTime = Math.floor(mUMPlayVideoTime / 10);
            } else {
                videoPlayTime = Math.floor(mUMPlayVideoTime / 60);
            }
        }
        mUMPlayVideoTime = 0;
        String timeKey = (int) videoPlayTime + "～" + (int) (videoPlayTime + 1);
        if (MobEnumUtil.VIDEO_RECORDED.equals(mVideoType)) {
            timeKey = timeKey + "(10秒)";
        } else {
            timeKey = timeKey + "(分)";
        }

        XesMobAgent.userPlayVideoTime(mVideoType, timeKey);
    }

    /**
     * 改变界面加载数据状态
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataLoadEvent(AppEvent.OnDataLoadingEvent event) {
        if (event.dataLoadEntity != null) {
            DataLoadManager.newInstance().loadDataStyle(this, event.dataLoadEntity);
        }
    }

    protected void updateRefreshImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        ImageView ivRefresh = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id.iv_course_video_refresh_bg);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivRefresh);
        }
    }
}
