package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoOnAudioFocusChangeListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoOnAudioGain;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoPhoneState;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.LiveLogBill;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.utils.PlayerLogUtils;
import com.xueersi.parentsmeeting.widget.LiveNetCheckTip;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;

/**
 * Created by linyuqiang on 2018/8/3.
 * 直播和回放的基础控制
 */
public class BasePlayerFragment extends Fragment implements VideoView.SurfaceCallback, LiveProvide, VideoOnAudioGain {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    BaseActivity activity;
    /** 视频的名称，用于显示在播放器上面的信息栏 */
    protected String mDisplayName;
    /** 是否从头开始播放 */
    private boolean mFromStart = true;
    /** 开始播放的起始点位 */
    protected long mStartPos;
    /** 当前视频是否播放到了结尾 */
    protected boolean mIsEnd = false;
    public static final Object mIjkLock = new Object();
    /** 所在的Activity是否已经onCreated */
    private boolean mCreated = false;
    /** 播放器核心服务 */
    protected volatile PlayerService vPlayer;
    /** 播放服务是否已连接 */
    protected boolean mServiceConnected = false;
    /** 播放器的Surface是否创建 */
    private boolean mSurfaceCreated = false;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    // region 播放业务Handler
    protected AtomicBoolean mOpened = new AtomicBoolean(Boolean.FALSE); // 线程安全的Boolean值
    /** 播放器统计时长 */
    private double mUMPlayVideoTime;
    /** 播放器界面的模式 */
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    /** 是否可以播放视频 */
    protected boolean mIsPlayerEnable = true;
    /** 是否使用硬解码，如当是本地采集的视频 */
    protected boolean mIsHWCodec = false;
    /** 是否完成了当前视频的播放 */
    protected boolean mCloseComplete = false;
    protected ShareDataManager mShareDataManager;
    /** 当前播放进度 */
    protected long mCurrentPosition;
    /** 视频总时长 */
    protected long mDuration;

    /** 当前播放的视频地址 */
    protected Uri mUri;
    /** 同步锁 */
    protected final Object mOpenLock = new Object();
    /** 准备打开播放文件 */
    protected static final int OPEN_FILE = 0;
    /** 初始化完播放器准备加载播放文件 */
    protected static final int OPEN_START = 1;
    /** 缓冲完毕可以播放 */
    protected static final int OPEN_SUCCESS = 2;
    /** 打开失败 */
    protected static final int OPEN_FAILED = 3;
    /** 硬解码失败 */
    protected static final int HW_FAILED = 4;
    /** 初始化播放器的默认参数 */
    protected static final int LOAD_PREFS = 5;
    /** 缓冲开始 */
    protected static final int BUFFER_START = 11;
    /** 正在缓冲 */
    protected static final int BUFFER_PROGRESS = 12;
    /** 缓冲结束 */
    protected static final int BUFFER_COMPLETE = 13;
    /** 播放时的实时进度 */
    protected static final int ON_PLAYING_POSITION = 14;
    /** 暂停播放 */
    protected static final int STOP_PLAYER = 15;
    /** seek完成 */
    protected static final int SEEK_COMPLETE = 16;
    /** 开始关闭播放器 */
    protected static final int CLOSE_START = 21;
    /** 已退出播放器 */
    protected static final int CLOSE_COMPLETE = 22;
    /** 是否可以自动横竖屏转换 */
    protected boolean mIsAutoOrientation = true;
    /** 当前界面方向 */
    protected int mDirection = VideoOrientationEventListener.DIRECTION_UP;

    /** 是否点击了横竖屏切换按钮 */
    private boolean mClick = false;
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    /** 点击进入横屏 */
    private boolean mClickLand = true;

    /** 点击进入竖屏 */
    private boolean mClickPort = true;
    /** 监听手机当前旋转角度 */
    private VideoOrientationEventListener mOrientationEventListener;
    String video = "ijk";
    /** 系统状态栏高度 */
    private int mStatusBarHeight = 0;
    /** 播放器的屏幕高 */
    protected int mPortVideoHeight = 0;
    protected float leftVolume = VP.DEFAULT_STEREO_VOLUME, rightVolume = VP.DEFAULT_STEREO_VOLUME;

    /** 放播放器的 io.vov.vitamio.widget.CenterLayout */
    protected ViewGroup viewRoot;
    /** 加载中动画的加载文字 */
    protected TextView tvVideoLoadingText;
    /** 播放器播放失败时的提供可刷新操作的背景 */
    protected View videoBackgroundRefresh;
    /** 加载中动画Loading */
    private View videoLoadingLayout;
    /** 直播类型 */
    public int liveType = 0;
    /** 直播，使用{@link PSIJK#playLive(String, int)} */
    public final static int PLAY_LIVE = 0;
    /** 回放，使用{@link PSIJK#playVod(String, int)} */
    public final static int PLAY_BACK = 1;
    /** 录播，使用{@link PSIJK#playFile(String, int)} */
    public final static int PLAY_TUTORIAL = 2;

    //自检提醒
    private LiveNetCheckTip mLiveNetCheckTip;

    public void playNewVideo() {
        if (mUri != null && mDisplayName != null) {
            playNewVideo(mUri, mDisplayName);
        }
    }

    public void playNewVideo(Uri uri, String displayName) {
        logger.d("playNewVideo:uri=" + uri);
        if (isInitialized()) {
            playerReleaseAndStopSync();
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

//    public void playNewVideo(Uri uri, String displayName, String shareKey) {
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
//
//        if (viewRoot != null) {
//            viewRoot.postInvalidate();
//        }
//
//        if (mOpened != null) {
//            mOpened.set(false);
//        }
//
//        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
//    }

    private AudioManager audioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

    /** 失去焦点 */
    protected boolean hasloss = false;

    public final void onAudioGain(boolean gain) {
        boolean oldhasloss = hasloss;
        hasloss = !gain;
        if (oldhasloss != hasloss) {
            onRealAudioGain(gain);
        }
    }

    public void onRealAudioGain(boolean gain) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        mShareDataManager = ShareDataManager.getInstance();
        audioFocusChangeListener = new VideoOnAudioFocusChangeListener(this);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            request();
        }
    }

    private int request() {
        if (Build.VERSION.SDK_INT <= 26) {
            int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            logger.d("request:requestAudioFocus:result1=" + result);
            return result;
        } else {//API26 废弃了原来的获取方法
            //下面两个常量参数试过很多 都无效，最终反编译了其他app才搞定，汗~
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                            .build())
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build();
            int result = audioManager.requestAudioFocus(mAudioFocusRequest);
            logger.d("request:requestAudioFocus:result2=" + result);
            return result;
        }
    }

    boolean pause = false;

    @Override
    public void onResume() {
        super.onResume();
        if (pause && audioManager != null) {
            resumeRequest();
        }
        pause = false;
    }

    protected void resumeRequest() {
        int result = request();
        hasloss = result != AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onPause() {
        super.onPause();
        pause = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsLand = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mIsLand) {
            mDirection = VideoOrientationEventListener.DIRECTION_RIGHT;
        }
        logger.d("onActivityCreated:mIsLand=" + mIsLand + ",mDirection=" + mDirection);
        mCreated = true;
    }

    public void setIsAutoOrientation(boolean mIsAutoOrientation) {
        logger.d("setIsAutoOrientation:mIsAutoOrientation=" + mIsAutoOrientation);
        this.mIsAutoOrientation = mIsAutoOrientation;
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new VideoOrientationEventListener(activity);
            if (mOrientationEventListener.canDetectOrientation()) {
                mOrientationEventListener.enable();
            }
        }
    }

    public boolean isLandSpace() {
        return mIsLand;
    }

    public void changeLOrP() {
        logger.d("changeLOrP:mIsLand=" + mIsLand);
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

    /**
     * 用户点击返回，判断是不是程序崩溃
     */
    protected void onUserBackPressed() {
        activity.onBackPressed();
//        activity.finish(LiveVideoConfig.VIDEO_CANCLE);
    }

    public PlayerService createPlayer() {
        vPlayer = new PlayerService(activity, true);
        vPlayer.onCreate();
        mServiceConnected = true;
        if (mSurfaceCreated) {// 链接成功后尝试开始播放
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        }
        // 设置当前是否为横屏
        setFileName(); // 设置视频显示名称
        showLongMediaController();
        vPlayer.setVideoPhoneState(new VideoPhoneState() {
            @Override
            public void state(boolean start) {
                onAudioGain(!start);
            }
        });
        return vPlayer;
    }

    protected void setFileName() {

    }

    public void showLongMediaController() {

    }

    /**
     * Activity 设置了横竖
     *
     * @param requestedOrientation
     */
    public void setRequestedOrientation(int requestedOrientation) {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.setRequestedOrientation(requestedOrientation, true);
        }
    }

    public void setVolume(float left, float right) {
        leftVolume = left;
        rightVolume = right;
        if (isInitialized()) {
            vPlayer.setVolume(left, right);
        }
    }

    SetVolumeListener setVolumeListener;

    public boolean setVolume(float left, float right, SetVolumeListener setVolumeListener) {
        leftVolume = left;
        rightVolume = right;
        this.setVolumeListener = setVolumeListener;
        if (isInitialized()) {
            vPlayer.setVolume(left, right);
            return true;
        }
        return false;
    }

    /**
     * 是否静音模式
     */
    private boolean muteMode;

    public void setMuteMode(boolean muteMode) {
        this.muteMode = muteMode;
        if (muteMode) {
            setVolume(0, 0);
        } else {
            setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
        }
    }

    public boolean isMuteMode() {
        return muteMode;
    }

    protected boolean handleMessage(Message msg) {
        return false;
    }

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (BasePlayerFragment.this.handleMessage(msg)) {
                return true;
            }
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
                                    Uri olduri = vPlayer.getUri();
                                    logger.d("playNewVideo:olduri=" + olduri);
                                    playerReleaseAndStopLock();
                                }

                                if (videoView != null) {
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                if (mUri != null) {
                                    vPlayer.initialize(mUri, video, getStartPosition(), vPlayerServiceListener, mIsHWCodec);
                                }
                            } else {
                                mOpened.set(true);
                                vPlayer.setVPlayerListener(vPlayerServiceListener);
                                if (videoView != null) {
                                    logger.i("setDisplay  ");
                                    vPlayer.setDisplay(videoView.getHolder());
                                }
                                if (isChangeLine) {
                                    try {
                                        vPlayer.changeLine(changeLinePos, protocol);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        videoConfigEntity.setProtocol(protocol);
                                        videoConfigEntity.setChangeLinePos(changeLinePos);
                                        PlayerLogUtils.changPlayLineLog(videoConfigEntity, getActivity(), e);
                                    }
                                    isChangeLine = false;
                                } else {
                                    boolean isPlayerCreated = vPlayer.psInit(MediaPlayer.VIDEO_PLAYER_NAME, getStartPosition(), vPlayerServiceListener, mIsHWCodec);
                                    setVideoConfig();
                                    String userName = "", userId = null;
                                    try {
                                        userName = LiveAppUserInfo.getInstance().getChildName();
                                        userId = LiveAppUserInfo.getInstance().getStuId();
                                        if (TextUtils.isEmpty(userName)) {
                                            userName = "";
                                        }
                                        if (videoConfigEntity != null) {
                                            videoConfigEntity.setUserId(userId);
                                            videoConfigEntity.setUserName(userName);
                                        }
                                        if (vPlayer.getPlayer() instanceof PSIJK) {
                                            vPlayer.getPlayer().setUserInfo(userName, userId);
                                        }
                                        if (liveType == PLAY_LIVE || liveType == PLAY_BACK) {
                                            vPlayer.playPSVideo(streamId, protocol);
                                        } else if (liveType == PLAY_TUTORIAL) {
                                            vPlayer.playFile(url, (int) mStartPos);
                                        }
                                    } catch (IOException e) {
                                        vPlayerHandler.sendEmptyMessage(OPEN_FAILED);
//                                        StableLogHashMap map = new StableLogHashMap();
//                                        map.put("userName", userName).
//                                                put("userId", userId + "").
//                                                put("streamId", streamId).
//                                                put("protocol", String.valueOf(protocol)).
//                                                put("isPlayerCreated", String.valueOf(isPlayerCreated)).
//                                                put("initPlayer", String.valueOf(vPlayer.checkNotNull())).
//                                                put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION).
//                                                put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
//                                        if (getActivity() != null) {
//                                            UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map.getData());
//                                        }
                                        if (videoConfigEntity != null) {
                                            videoConfigEntity.setStreamId(streamId);
                                            videoConfigEntity.setProtocol(protocol);
                                            videoConfigEntity.setUserName(userName);
                                            videoConfigEntity.setUserId(userId);
                                        }
                                        PlayerLogUtils.playerError(videoConfigEntity, isPlayerCreated, vPlayer.checkNotNull(), e, getActivity());
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (videoConfigEntity != null) {
                                            recordFailData(videoConfigEntity.addPlayException().toString());
                                        } else {
                                            if (videoConfigEntity != null) {
                                                videoConfigEntity.setStreamId(streamId);
                                                videoConfigEntity.setProtocol(protocol);
                                                videoConfigEntity.setUserName(userName);
                                                videoConfigEntity.setUserId(userId);
                                            }
                                            PlayerLogUtils.playerError(videoConfigEntity, isPlayerCreated, vPlayer.checkNotNull(), e, getActivity());
//                                            StableLogHashMap map = new StableLogHashMap();
//                                            map.put("userName", userName).
//                                                    put("userId", userId).
//                                                    put("streamId", streamId).
//                                                    put("protocol", String.valueOf(protocol)).
//                                                    put("isPlayerCreated", String.valueOf(isPlayerCreated)).
//                                                    put("initPlayer", String.valueOf(vPlayer.checkNotNull())).
//                                                    put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION).
//                                                    put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
//                                            if (getActivity() != null) {
//                                                UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map.getData());
//                                            }

                                        }
                                        LiveCrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
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
                    vPlayer.start();
                    showLongMediaController();
                    break;
                case OPEN_FAILED:
                    // 视频打开失败
                    int arg1 = msg.arg1, arg2 = msg.arg2;
                    //通知LiveVideoFragment
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
                    LiveLogBill.getInstance().liveANRLog();
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    mLiveNetCheckTip.showTips(activity);
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
                        videoView.initialize(activity, BasePlayerFragment.this, false);
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

    protected WeakHandler vPlayerHandler = new WeakHandler(callback);

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
        mLiveNetCheckTip = new LiveNetCheckTip();
        return viewRoot;
    }

    /** 记录播放失败日志日志 */
    protected void recordFailData(String jsonString) {
        if (getActivity() != null) {
            UmsAgentManager.umsAgentDebug(getActivity(), LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, jsonString);
        }
    }

    /** 加载缓冲进度动画 */
    protected void setVideoLoadingLayoutVisibility(int visibility) {
        if (videoLoadingLayout != null) {
            videoLoadingLayout.setVisibility(visibility);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void removeLoadingView() {
        videoLoadingLayout.setVisibility(View.GONE);
    }

    protected long getStartPosition() {
        return 0L;
    }

    protected void setVideoConfig() {
        if (videoConfigEntity != null) {
            vPlayer.enableAutoSpeedPlay(videoConfigEntity.getPsIjkParameter());
        }
    }

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
    protected String streamId;
    /**
     * 回放时使用的url
     */
    protected String url;


    /**
     * url
     *
     * @param url      文件路径
     * @param startPos 启播时间，暂不支持； V1.2会支持该功能
     */
    public void playPSFile(String url, int startPos) {
        this.url = url;
        mStartPos = startPos;
        liveType = PLAY_TUTORIAL;
        psPlayerReleaseAndStopSync();

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

    /**
     *
     */
//    public void enableAutoSpeedPlay(long waterMark, long duration) {
//        if (vPlayer != null) {
//            vPlayer.enableAutoSpeedPlay(waterMark, duration);
//        }
////        if (mediaPlayer != null) {
////            mediaPlayer.enableAutoSpeedPlay(waterMark, duration);
////        }
//    }
    protected VideoConfigEntity videoConfigEntity;

    public void enableAutoSpeedPlay(VideoConfigEntity videoConfigEntity) {
        if (vPlayer != null && videoConfigEntity != null) {
            this.videoConfigEntity = videoConfigEntity;
            vPlayer.enableAutoSpeedPlay(videoConfigEntity.getPsIjkParameter());
        }
    }

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

    /**
     * PSIJK专用，
     *
     * @param streamId 直播的话为channel_name
     * @param protocol 回放的话为videoPath
     */
    public void playPSVideo(String streamId, int protocol) {
        isChangeLine = false;
        this.streamId = streamId;
        this.protocol = protocol;
        if (protocol == MediaPlayer.VIDEO_PROTOCOL_RTMP || protocol == MediaPlayer.VIDEO_PROTOCOL_FLV || protocol == MediaPlayer.VIDEO_PROTOCOL_HLS) {
            this.liveType = PLAY_LIVE;
        } else if (protocol == MediaPlayer.VIDEO_PROTOCOL_MP4 || protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
            this.liveType = PLAY_BACK;
        }
        psPlayerReleaseAndStopSync();
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

    /** 设置视频名称 */
    public void setmDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    private void playerReleaseAndStopSync() {
        synchronized (mOpenLock) {
            playerReleaseAndStopLock();
        }
    }

    private void playerReleaseAndStopLock() {
        vPlayer.release();
        vPlayer.releaseContext();
    }

    /**
     * psijk使用的释放资源
     */
    private void psPlayerReleaseAndStopSync() {
        if (mCreated && vPlayer != null) {
            synchronized (mOpenLock) {
                psPlayerReleaseAndStopLock();
            }
        }
    }

    private void psPlayerReleaseAndStopLock() {
        vPlayer.release();
        vPlayer.psStop();
    }

    protected VPlayerCallBack.VPlayerListener vPlayerServiceListener = new VPlayerCallBack.VPlayerListener() {

        @Override
        public void getPSServerList(int cur, int total, boolean modeChange) {
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.getPSServerList(cur, total, modeChange);
//                BasePlayerFragment.this.getPSServerList(cur, total);
            }
        }

//        @Override
//        public void getPServerListFail() {
//            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
//            if (wrapListener != null) {
//                wrapListener.getPServerListFail();
////                BasePlayerFragment.this.getPSServerList(cur, total);
//            }
//        }

        /** 硬解码失败 */
        @Override
        public void onHWRenderFailed() {
            if (Build.VERSION.SDK_INT < 11 && mIsHWCodec) {
                vPlayerHandler.sendEmptyMessage(HW_FAILED);
                vPlayerHandler.sendEmptyMessageDelayed(HW_FAILED, 200); // 确保使用软解码初始化成功？？？
            }
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onHWRenderFailed();
            }
        }

        /** 开始准备播放 */
        @Override
        public void onOpenStart() {
            vPlayerHandler.sendEmptyMessage(OPEN_START);
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
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
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onOpenSuccess();
            }
            if (isInitialized()) {
                vPlayer.setVolume(leftVolume, rightVolume);
                try {
                    if (setVolumeListener != null) {
                        setVolumeListener.onSuccess(true);
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
                }
            } else {
                try {
                    if (setVolumeListener != null) {
                        setVolumeListener.onSuccess(false);
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
                }
            }
            if (activity != null) {
                VideoPlayDebugUtils.umsIfVideoViewIsNotVisible(activity, activity.findViewById(R.id.vv_course_video_video));
            }
        }

        /** 视频打开失败 */
        @Override
        public void onOpenFailed(int arg1, int arg2) {
            vPlayerHandler.sendMessage(vPlayerHandler.obtainMessage(OPEN_FAILED, arg1, arg2));
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                //通知
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
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferStart();
            }
        }

        /** 缓冲结束 */
        @Override
        public void onBufferComplete() {
            String s = "onBufferComplete";
            if (mIsPlayerEnable && vPlayer != null) {
                vPlayerHandler.sendEmptyMessage(BUFFER_COMPLETE);
            }
            if (vPlayer != null) {
                vPlayer.startListenPlaying();
            }
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onBufferComplete();
            }
        }

        @Override
        public void onSeekComplete() {
            vPlayerHandler.sendEmptyMessage(SEEK_COMPLETE);
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onSeekComplete();
            }
        }

        /** 播放完毕 */
        @Override
        public void onPlaybackComplete() {
            playComplete();
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaybackComplete();
            }
        }

        /** 关闭开始 */
        @Override
        public void onCloseStart() {
            vPlayerHandler.sendEmptyMessage(CLOSE_START);
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onCloseStart();
            }
        }

        /** 关闭完成 */
        @Override
        public void onCloseComplete() {
            vPlayerHandler.sendEmptyMessage(CLOSE_COMPLETE);
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
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
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onVideoSizeChanged(width, height);
            }
        }

        /** 下载进度 */
        @Override
        public void onDownloadRateChanged(int kbPerSec) {
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
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
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlaying(currentPosition, duration);
            }
        }

        /** 播放出现错误 */
        @Override
        public void onPlayError() {
            BasePlayerFragment.this.onPlayError();
            VPlayerCallBack.VPlayerListener wrapListener = getWrapListener();
            if (wrapListener != null) {
                wrapListener.onPlayError();
            }
        }
    };

    /** 加载播放器的默认设置参数 */
    protected void loadVPlayerPrefs() {
        if (!isInitialized()) {
            return;
        }
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
        if (videoView != null && isInitialized()) {
            setVideoLayout();
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

    /** 当前视频播放完毕 */
    public void playComplete() {

    }

    /** 视频非正常播放完毕，有可能是断网了，也有可能一开始打开失败了 */
    public void resultFailed(int arg1, int arg2) {
        showRefresyLayout(arg1, arg2);
    }

    /** 加载视频异常时出现可重新刷新的背景界面 TODO */
    protected void showRefresyLayout(int arg1, int arg2) {
        if (videoBackgroundRefresh == null) {
            return;
        }
        videoBackgroundRefresh.setVisibility(View.VISIBLE);
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

    /** 控制开始播放视频 */
    public void start() {
        if (isInitialized()) {
            vPlayer.start();
        }
    }

    /** 控制视频暂停 */
    public void pause() {
        if (isInitialized()) {
            vPlayer.pause();
        }
    }

    /** 停止（按了返回键） */
    public void stop() {
        onBackPressed();
    }

    public void seekTo(long pos) {
        if (isInitialized()) {
            // vPlayer.seekTo((float) ((double) pos / vPlayer.getDuration()));

            vPlayer.seekTo(pos);
        }
        //即使视频没有播放，也会存储这个位置
        mShareDataManager.put(streamId + VP.SESSION_LAST_POSITION_SUFIX, (long) 0, ShareDataManager.SHAREDATA_USER);//重置播放进度
    }

    /** 设置播放器的界面布局 */
    protected void setVideoLayout() {
        logger.d("setVideoLayout:VideoWidth=" + vPlayer.getVideoWidth() + ",VideoHeight=" + vPlayer.getVideoHeight());
        if (vPlayer.getVideoWidth() == 0 || vPlayer.getVideoHeight() == 0) {
            return;
        }
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, vPlayer.getVideoWidth(),
                vPlayer.getVideoHeight(), vPlayer.getVideoAspectRatio());
    }

    /** 准备加载新视频 */
    protected void onPlayOpenStart() {

    }

    /** 视频预加载成功 */
    protected void onPlayOpenSuccess() {

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

    /** 释放播放器资源 */
    public void release() {
        if (vPlayer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                playerReleaseAndStopSync();
            } else {
                playerReleaseAndStopSync();
            }
        }
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


    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        logger.d("onSurfaceCreated:vPlayer=null?" + (vPlayer == null) + ",mServiceConnected=" + mServiceConnected);
        mSurfaceCreated = true;
//        if (mServiceConnected) {
//            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
//        }
        if (vPlayer != null) {
            vPlayer.setDisplay(holder);
        }
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        logger.d("onSurfaceChanged:width=" + width + ",height=" + height);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        logger.d("onSurfaceDestroyed:hasloss=" + hasloss);
        if (vPlayer != null && vPlayer.isInitialized()) {
            if (vPlayer.isPlaying()) {
                vPlayer.pause();
                vPlayer.setState(PlayerService.STATE_NEED_RESUME);
            }
            vPlayer.releaseSurface();
            //TODO 这个会影响暂停视频，返回后台继续播放。但是悬浮窗还需要
            PauseNotStopVideoInter onPauseNotStopVideo = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter.class);
            //onPauseNotStopVideo 应该不会空
            if ((onPauseNotStopVideo == null || onPauseNotStopVideo.getPause()) && !hasloss) {
                if (mIsPlayerEnable && vPlayer.needResume()) {
                    vPlayer.start();
                }
            }
        }
    }

    public long getCurrentPosition() {
        if (isInitialized()) {
            return vPlayer.getCurrentPosition();
        }
        // return (long) (getStartPosition() * vPlayer.getDuration());
        return 0;
    }

    public long getDuration() {
        if (isInitialized()) {
            return vPlayer.getDuration();
        }
        return 0;
    }

    public int getBufferPercentage() {
        if (isInitialized()) {
            return (int) (vPlayer.getBufferProgress() * 100);
        }
        return 0;
    }

    public float scale(float scaleFactor) {
        float userRatio = VP.DEFAULT_ASPECT_RATIO;
        int videoWidth = vPlayer.getVideoWidth();
        int videoHeight = vPlayer.getVideoHeight();
        float videoRatio = vPlayer.getVideoAspectRatio();
        float currentRatio = videoView.mVideoHeight / (float) videoHeight;

        currentRatio += (scaleFactor - 1);
        if (videoWidth * currentRatio >= LiveVideoConfig.VIDEO_MAXIMUM_WIDTH) {
            currentRatio = LiveVideoConfig.VIDEO_MAXIMUM_WIDTH / (float) videoWidth;
        }

        if (videoHeight * currentRatio >= LiveVideoConfig.VIDEO_MAXIMUM_HEIGHT) {
            currentRatio = LiveVideoConfig.VIDEO_MAXIMUM_HEIGHT / (float) videoHeight;
        }

        if (currentRatio < 0.5f) {
            currentRatio = 0.5f;
        }

        videoView.mVideoHeight = (int) (videoHeight * currentRatio);
        videoView.setVideoLayout(mVideoMode, userRatio, videoWidth, videoHeight, videoRatio);
        return currentRatio;
    }

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

//    public void changLine() {
//
//    }

    protected VPlayerCallBack.VPlayerListener getWrapListener() {
        return null;
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

    public MediaErrorInfo getMediaErrorInfo() {
//        if (mPlayer instanceof PSIJK) {
        return vPlayer != null ? vPlayer.getMediaErrorInfo() : MediaErrorInfo.getInstance();
//        }
//        return null;
    }

    /** 网络发生变化 */
    public void onNetWorkChange(int netWorkType) {

        vPlayer.onNetWorkChange(netWorkType);
//        if (liveGetPlayServer != null) {
//            liveGetPlayServer.onNetWorkChange(netWorkType);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vPlayer != null) {
            vPlayer.psExit();
        }
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT <= 26) {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            } else {
                if (Build.VERSION.SDK_INT > 26 && mAudioFocusRequest != null) {
                    audioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                }
            }
        }
    }
}
