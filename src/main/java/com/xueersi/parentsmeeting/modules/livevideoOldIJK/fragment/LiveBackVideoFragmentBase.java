package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.audio.AudioPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/***
 * 视频播放主界面
 *
 * @author 林玉强
 */
public class LiveBackVideoFragmentBase extends Fragment {
    private String TAG = "LiveBackVideoFragmentBase";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    protected BaseActivity activity;
    /** 布局默认资源 */
    protected int mLayoutVideo = R.layout.liveback_video_fragment;
    /** 播放器可刷新布局 */
    protected int mLayoutBackgroundRefresh = R.layout.layout_video_resfresh;
    protected LiveBackPlayerFragment liveBackPlayVideoFragment;
    /** 所在的Activity是否已经onCreated */
    protected boolean mCreated = false;
    /** 视频的名称，用于显示在播放器上面的信息栏 */
    protected String mDisplayName;
    /** 是否从头开始播放 */
    protected boolean mFromStart = false;
    protected boolean pausePlay = false;
    /** 当前界面是否横屏 */
    protected AtomicBoolean mIsLand = new AtomicBoolean(false);
    /** 是否显示控制栏 */
    protected boolean mIsShowMediaController = true;
    /** 当前视频是否播放到了结尾 */
    protected boolean mIsEnd = false;
    /** 播放器的屏幕高 */
    protected int mPortVideoHeight = 0;
    /** 进度缓存的追加KEY值 */
    protected String mShareKey = "";
    /** 当前播放进度 */
    protected long mCurrentPosition;
    /** 视频总时长 */
    protected long mDuration;
    /** 播放器界面的模式 */
    private int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    /** 放播放器的 io.vov.vitamio.widget.CenterLayout */
    private View viewRoot;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    protected LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
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
    protected MediaController2 mMediaController;
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** onPause状态不暂停视频 */
    protected AtomicBoolean onPauseNotStopVideo = new AtomicBoolean(false);
    /** 是否可以自动横竖屏转换 */
    private boolean mIsAutoOrientation = true;
    /** 业务层 */
    protected VideoBll bllVideo;
    /** 播放器统计时长 */
    protected long mPlayVideoTime = 0;
    /** 播放器统计时长 */
    private double mUMPlayVideoTime;
    /** 播放器统计时长发送间隔 */
    protected int mSendPlayVideoTime = 180;
    /** 视频进度 */
    private String mLastVideoPositionKey;
    /** 统计视频播放key */
    protected String mVisitTimeKey;
    /** 购课id */
    protected String stuCourId;
    /** 视频类型 */
    protected String mVideoType = MobEnumUtil.VIDEO_RECORDED;
    /** 是否完成了一系列的系统广播 */
    private boolean mReceiverRegistered = false;
    /** 是否是正在播放时插拔耳机 */
    private boolean mHeadsetPlaying = false;
    /** 是否完成了当前视频的播放 */
    private boolean mCloseComplete = false;
    /** 播放器请求 */
    public static final int VIDEO_REQUEST = 210;
    /** 播放器用户返回 */
    public static final int VIDEO_CANCLE = 211;
    /** 播放器java崩溃 */
    public static final int VIDEO_CRASH = 1200;
    protected float mySpeed = 1.0f;
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
    String video;
    // endregion

    // region 生命周期及系统调用
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.setLogMethod(false);
        activity = (BaseActivity) getActivity();
        FileLogger.runActivity = activity;
        // 统计视频点击某个视频
        XesMobAgent.userMarkVideoClick();
        // 注册事件
        EventBus.getDefault().register(this);
        bllVideo = new VideoBll(activity);
        sendPlayVideoHandler.sendEmptyMessageDelayed(1, 1000);
        mIsLand.set(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        BaseApplication baseApplication = (BaseApplication) activity.getApplication();
        baseApplication.addActivty(activity);
        //showDialog(savedInstanceState);
        video = "ijk";
    }

    private void onSelect(Bundle savedInstanceState, String video) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置在该页面音量控制键的音频流为媒体音量
        mCreated = true; // 界面onCreate完毕
        videoView = (VideoView) mContentView.findViewById(R.id.vv_course_video_video); // 播放器的videoView
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
        createPlayer();
        onVideoCreate(savedInstanceState);
    }

    public void setRequestedOrientation(int requestedOrientation) {
        liveBackPlayVideoFragment.setRequestedOrientation(requestedOrientation);
    }

    protected void createPlayer() {
        vPlayer = liveBackPlayVideoFragment.createPlayer();
        // 设置当前是否为横屏
        setFileName(); // 设置视频显示名称
        showLongMediaController();
    }

    protected void onVideoCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mCreated) {
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FileLogger.runActivity = activity;
        //关闭系统后台声音
        AudioPlayer.requestAudioFocus(activity);

        // 设置视频可播放
        liveBackPlayVideoFragment.setIsPlayerEnable(true);
        if (!mCreated) {
            return;
        }
        if (isInitialized()) {
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
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
        super.onPause();
        AudioPlayer.abandAudioFocus(activity);
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONPAUSE);
        liveBackPlayVideoFragment.setIsPlayerEnable(false);
        if (!mCreated) {
            return;
        }
        if (isInitialized()) {
            // 存储当前视频播放的进度
            if (mIsEnd) {
                savePosition(0);
            } else {
                savePosition();
            }
            if (vPlayer != null && vPlayer.isPlaying() && !onPauseNotStopVideo.get()) {
                liveBackPlayVideoFragment.setIsPlayerEnable(false);
                // 暂停播放
                stopPlayer();
            } else {
                liveBackPlayVideoFragment.setIsPlayerEnable(true);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mCreated) {
            return;
        }
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONSTOP);
        // 友盟统计
        umPlayVideoTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mCreated) {
            return;
        }
        // 统计退出
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONDESTROY);

        if (mMediaController != null) {// 释放控制器
            mMediaController.release();
        }
        // 注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand.set(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        // 设置当前屏幕是否横屏
        loadLandOrPortView(); // 重新加载界面
        if (isInitialized()) {
            setVideoLayout(); // 设置播放器VideoView的布局样式
            if (mIsShowMediaController) {
                attachMediaController(); // 在最上面加上控制器界面
            }

            if (mIsLand.get()) {
                if (mMediaController != null) {
                    mMediaController.showSystemUi(false);
                }
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                // 换总实现方法，防止5.0系统自动进入了沉浸式视图
                // mMediaController.showSystemUi(true);
                activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                /*
                 * getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                 * ) ; getWindow().clearFlags(WindowManager.LayoutParams.
                 * FLAG_LAYOUT_IN_SCREEN);
                 */
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    public final void onBackPressed() {
        // 这里需要写代码，如果是横屏则转换竖屏
//        setBackgroundAlpha(0.4f);
//        showPopupwinResult();
//        XESToastUtils.showToast(activity, "onBackPressed");
        if (mIsLand.get()) {
            // 如果是横屏则切换为竖屏
            if (mIsAutoOrientation) {
                liveBackPlayVideoFragment.changeLOrP();
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
        activity.finish(VIDEO_CANCLE);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            // 据查阅资料是用来解决某个GOOGLE自带的BUG的
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            super.onSaveInstanceState(outState);
        }
    }

    // region 播放管理业务
    protected RelativeLayout mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) inflater.inflate(R.layout.frag_livevideo_content, container, false);
        logger.d("onCreateView");
        loadView(mLayoutVideo);
        return mContentView;
    }

    public RelativeLayout getContentView() {
        return mContentView;
    }

    /** 加载界面 */
    protected void loadView(int id) {
        activity.getWindow().setBackgroundDrawable(null);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(id, mContentView, false);
        mContentView.removeAllViews();
        mContentView.addView(view);
        viewRoot = view.findViewById(com.xueersi.parentsmeeting.module.player.R.id.cl_course_video_root);// 播放器所在的io
        // .vov.vitamio.widget.CenterLayout
        rlContent = (RelativeLayout) view.findViewById(com.xueersi.parentsmeeting.module.player.R.id
                .rl_course_video_content);
        videoBackgroundRefresh = LayoutInflater.from(activity).inflate(mLayoutBackgroundRefresh, rlContent, false);
        // 失败时播放器显示的背景
        videoBackgroundRefresh.setVisibility(View.GONE);
        rlContent.addView(videoBackgroundRefresh);
        tvVideoLoadingText = (TextView) view.findViewById(com.xueersi.parentsmeeting.module.player.R.id
                .tv_course_video_loading_tip); // 加载进度文字框
        videoLoadingLayout = view.findViewById(com.xueersi.parentsmeeting.module.player.R.id.rl_course_video_loading)
        ; // 加载进度动画
        btnVideoRefresh = (Button) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.module.player.R.id
                .btn_layout_video_resfresh); // 刷新按钮
        btnVideoRefresh.setOnClickListener(btnRefreshListener); // 刷新事件
        ivBack = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.module.player.R.id
                .iv_layout_video_resfresh_back);
        ivBack.setOnClickListener(ivRefreshBackListener); // 刷新页面的回退

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        LiveBackPlayerFragment fragment = (LiveBackPlayerFragment) getChildFragmentManager().findFragmentByTag
                ("LivePlayerFragment");
        if (fragment == null) {
            fragment = getFragment();
            transaction.add(R.id.rl_live_video_frag, fragment, "LivePlayerFragment");
        } else {
            restoreFragment(fragment);
        }
        liveBackPlayVideoFragment = fragment;
        fragment.setOnVideoCreate(videoCreate);
        transaction.commit();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 使屏幕保持长亮
        loadLandOrPortView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoCreate.savedInstanceState = savedInstanceState;
    }

    // region 播放管理业务


    protected LiveBackPlayerFragment getFragment() {
        LiveVideoFragmentBase liveVideoPlayFragment = new LiveVideoFragmentBase();
        liveVideoPlayFragment.liveBackVideoFragment = this;
        return liveVideoPlayFragment;
    }

    protected void restoreFragment(LiveBackPlayerFragment videoFragment) {
        LiveVideoFragmentBase liveVideoPlayFragment = (LiveVideoFragmentBase) videoFragment;
        liveVideoPlayFragment.liveBackVideoFragment = this;
    }

    public static class LiveVideoFragmentBase extends LiveBackPlayerFragment {
        LiveBackVideoFragmentBase liveBackVideoFragment;

        @Override
        public void pause() {
            super.pause();
            liveBackVideoFragment.pausePlay = true;
            liveBackVideoFragment.onPausePlayer();
        }

        @Override
        public void start() {
            super.start();
            liveBackVideoFragment.onStartPlayer();
        }

        @Override
        protected void onPlayOpenStart() {
            super.onPlayOpenStart();
            liveBackVideoFragment.onPlayOpenStart();
        }

        @Override
        public void setSpeed(float speed) {
            super.setSpeed(speed);
            liveBackVideoFragment.setSpeed(speed);
        }

        @Override
        public int onVideoStatusChange(int code, int status) {
            liveBackVideoFragment.onVideoStatusChange(code,status);
            return super.onVideoStatusChange(code, status);

        }

        @Override
        public void seekTo(long pos) {
            super.seekTo(pos);
            liveBackVideoFragment.seekTo(pos);
        }

        @Override
        public void onPlayOpenSuccess() {
            super.onPlayOpenSuccess();
            liveBackVideoFragment.onPlayOpenSuccess();
        }

        @Override
        public void resultFailed(int arg1, int arg2) {
            liveBackVideoFragment.resultFailed(arg1, arg2);
        }

        @Override
        protected void playingPosition(long currentPosition, long duration) {
            super.playingPosition(currentPosition, duration);
            liveBackVideoFragment.playingPosition(currentPosition, duration);
        }

        @Override
        protected VPlayerCallBack.VPlayerListener getWrapListener() {
            return liveBackVideoFragment.getWrapListener();
        }

        @Override
        protected void resultComplete() {
            super.resultComplete();
            liveBackVideoFragment.resultComplete();
            mIsEnd = true;
        }

        @Override
        protected long getStartPosition() {
            return liveBackVideoFragment.getStartPosition();
        }


    }

    protected void onStartPlayer() {
    }

    protected void onPausePlayer() {
    }


    LiveOnVideoCreate videoCreate = new LiveOnVideoCreate();

    class LiveOnVideoCreate implements LiveBackPlayerFragment.OnVideoCreate {
        Bundle savedInstanceState;

        @Override
        public void onVideoCreate() {
            video = "ijk";
            logger.d("onActivityCreated:frag=" + ((ViewGroup) mContentView.findViewById(R.id.rl_live_video_frag))
                    .getChildCount());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSelect(savedInstanceState, video);
                }
            });
        }
    }

    /** 加载旋转屏时相关布局 */
    protected void loadLandOrPortView() {
        RelativeLayout.LayoutParams lpr = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
        if (mIsLand.get()) {
            lpr.height = LayoutParams.MATCH_PARENT;
        } else {
            lpr.height = mPortVideoHeight;
            // lpr.height = VP.DEFAULT_PORT_HEIGHT;
        }
        if (viewRoot != null) {
            LayoutParams lp = viewRoot.getLayoutParams();
            if (mIsLand.get()) {
                lp.height = LayoutParams.MATCH_PARENT;
            } else {
                lp.height = mPortVideoHeight;
                /* lp.height = VP.DEFAULT_PORT_HEIGHT; */
            }
            viewRoot.setLayoutParams(lp);
        }
        liveBackPlayVideoFragment.loadLandOrPortView(mIsLand.get());
    }

    /** 设置播放器的界面布局 */
    protected void setVideoLayout() {
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, vPlayer.getVideoWidth(),
                vPlayer.getVideoHeight(), vPlayer.getVideoAspectRatio());
        if (mIsLand.get()) {
            ViewGroup.LayoutParams lp = videoView.getLayoutParams();
            LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
        }
    }

    /** 加载缓冲进度动画 */
    private void setVideoLoadingLayoutVisibility(int visibility) {
        if (videoLoadingLayout != null) {
            videoLoadingLayout.setVisibility(visibility);
        }
    }

    /** 加载视频异常时出现可重新刷新的背景界面 */
    protected void showRefresyLayout(int arg1, int arg2) {
        if (mMediaController != null) {
            mMediaController.release();
        }
        videoBackgroundRefresh.setVisibility(View.VISIBLE);
        updateRefreshImage();
        TextView errorInfo = (TextView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.module.player
                .R.id.tv_course_video_errorinfo);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errorInfo.setVisibility(View.VISIBLE);
            String videoKey = getVideoKey();
            if (StringUtils.isSpace(videoKey)) {
                errorInfo.setText(error.getNum() + " (" + error.getTag() + ")");
            } else {
                errorInfo.setText("(" + videoKey + ")" + error.getNum() + " (" + error.getTag() + ")");
            }
        } else {
            errorInfo.setVisibility(View.GONE);
        }
        videoBackgroundRefresh.getLayoutParams().height = LayoutParams.MATCH_PARENT;
    }

    /** 播放错误视频id */
    protected String getVideoKey() {
        return "";
    }

    private void hideMediaController() {
        if (mMediaController != null) {
            mMediaController.release();
        }
    }

    protected void stopShowRefresyLayout() {
        if (isInitialized()) {
            vPlayer.stop();
        }
        showRefresyLayout(0, 0);
    }

    protected void playNewVideo() {
        liveBackPlayVideoFragment.playNewVideo();
    }

    public void playNewVideo(Uri uri, String displayName) {
        mUri = uri;
        mDisplayName = displayName;
        liveBackPlayVideoFragment.playNewVideo(uri, displayName);
    }

    /** 播放下一个视频 */
    protected void startPlayNextVideo() {

    }

    /** 在所有资源初始化完毕后，调用开始播放 */
    protected void startPlayer() {
        liveBackPlayVideoFragment.startPlayer();
    }

    /** 暂停播放 */
    protected void stopPlayer() {
        if (isInitialized()) {
            vPlayer.pause();
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
        loadView(mLayoutVideo);
    }

    /** 横竖屏进行切换 */
    public void changeLandOrPort() {
        if (!mIsLand.get()) {
            // 切换为横屏
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            // 切换为竖屏
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 是否允许视频界面自动横竖屏
     *
     * @param isAutoOrientation
     */
    protected void setAutoOrientation(boolean isAutoOrientation) {
        mIsAutoOrientation = isAutoOrientation;
        if (liveBackPlayVideoFragment != null) {
            liveBackPlayVideoFragment.setIsAutoOrientation(isAutoOrientation);
        }
    }

    protected void setSpeed(float speed) {
        mySpeed = speed;
    }

    protected void seekTo(long pos) {

    }

    protected void onPlayOpenStart() {

    }

    protected void onPlayOpenSuccess() {

    }
    protected int onVideoStatusChange(int code,int status){
        return code;

    }


    /** 视频正常播放完毕退出时调用，非加载失败 */
    protected void resultComplete() {
        startPlayNextVideo();
    }

    /** 视频非正常播放完毕，有可能是断网了，也有可能一开始打开失败了 */
    protected void resultFailed(int arg1, int arg2) {
        if (vPlayer != null) {
            vPlayer.stopListenPlaying();
        }
        savePosition();
        showRefresyLayout(arg1, arg2);
    }

    /** 将当前播放视频的进度缓存 */
    private void savePosition() {
        // savePosition((float) (vPlayer.getCurrentPosition() / (double)
        // vPlayer.getDuration()));
        // savePosition(vPlayer.getCurrentPosition());
        savePosition(mCurrentPosition);
    }

    protected void savePosition(long fromStart) {
        if (vPlayer != null && mUri != null) {
            ShareDataManager.getInstance().put(mUri + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, fromStart,
                    ShareDataManager.SHAREDATA_USER);
        }
    }

    /** 取出当前播放视频上次播放的点位 */
    protected long getStartPosition() {
        if (mFromStart) {
            return 0;
        }
        // if (mStartPos <= 0.0f || mStartPos >= 1.0f)
        try {
            return ShareDataManager.getInstance().getLong(mUri + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, 0,
                    ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            // 有一定不知明原因造成取出的播放点位int转long型失败,故加上这个值确保可以正常观看
            e.printStackTrace();
            return 0L;
        }
        // return mStartPos;
    }

    /** 初始化控制器界面 */
    protected void attachMediaController() {
        if (mMediaController != null) {
            mMediaController.release();
        }

        // 设置当前是否为横屏
        mMediaController = new MediaController2(activity, liveBackPlayVideoFragment);
        setFileName(); // 设置视频显示名称
        showLongMediaController();
    }

    protected void resetMediaController() {
        setFileName(); // 设置视频显示名称
        showLongMediaController();
    }

    /** 判断当前为竖屏并且处于播放状态时，显示控制栏 */
    protected void showLongMediaController() {
        if (!mIsLand.get() && vPlayer.isPlaying()) {
            // 竖屏时长时间显示
            mMediaController.showLong();
        } else if (mIsLand.get() && vPlayer.isPlaying()) {
            // 横屏时短时间显示
            mMediaController.show();
        }
    }

    /** 设置视频名称 */
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

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
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

    protected VPlayerCallBack.VPlayerListener getWrapListener() {
        return null;
    }

    /** 播放器最大的高 */
    private static final int VIDEO_MAXIMUM_HEIGHT = 2048;
    /** 播放器最大的宽 */
    private static final int VIDEO_MAXIMUM_WIDTH = 2048;

    // endregion

    // region 播放器Surface界面回

    // endregion

    /** 发送统计时长Key */
    protected void setmLastVideoPositionKey(String mLastVideoPositionKey) {
        this.mLastVideoPositionKey = mLastVideoPositionKey;
    }

    /** 播放器统计时长发送间隔 */
    protected void setmSendPlayVideoTime(int mSendPlayVideoTime) {
        this.mSendPlayVideoTime = mSendPlayVideoTime;
    }

    /** 统计观看视频时长 */
    @SuppressLint("HandlerLeak")
    protected Handler sendPlayVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // 视频初始化完成，视频正在播放,统计观看时长
            if (isInitialized() && vPlayer != null && shouldSendPlayVideo()) {
                mPlayVideoTime++;
                mUMPlayVideoTime++;
                sendPlayVideo();
            }
            if (!activity.isFinishing()) {
                long delayMillis = (long) (1000 / mySpeed);
                sendPlayVideoHandler.sendEmptyMessageDelayed(1, delayMillis);
            }
        }
    };

    protected boolean shouldSendPlayVideo() {
        return vPlayer.isPlaying();
    }

    /** 发送统计观看视频时长 */
    protected void sendPlayVideo() {
        if (TextUtils.isEmpty(mVisitTimeKey)) {
            return;
        }
        // 如果观看视频时间等于或大于统计数则发送
        if (mPlayVideoTime == mSendPlayVideoTime || mPlayVideoTime > mSendPlayVideoTime) {
            String tradeId = mVisitTimeKey + "-" + mPlayVideoTime;
            // 发送观看视频时间
            bllVideo.sendVisitTime(stuCourId, tradeId, sendPlayVideoHandler, 1000);
            // 时长初始化
            mPlayVideoTime = 0;
        }
    }

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
            DataLoadManager.newInstance().loadDataStyle(activity, event.dataLoadEntity);
        }
    }

    protected void updateRefreshImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false,
                ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        ImageView ivRefresh = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.module
                .player.R.id.iv_course_video_refresh_bg);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(com.xueersi.parentsmeeting.module
                        .player.R.drawable.livevideo_cy_moren_logo_normal)
                        .error(com.xueersi.parentsmeeting.module.player.R.drawable.livevideo_cy_moren_logo_normal)
                        .into(ivRefresh);
            }
        }
    }

    public VideoView getVideoView() {
        return videoView;
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }
    //    PopupWindow mWindow;
//    PopupWindow mFeedbackWindow;
//    String mDifficulty = "-1";
//    String mSatisficing = "-1";
//    String mSuggess = "";
//
//    public void showPopupwinResult() {
//        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View result = inflater.inflate(R.layout.pop_experience_livevideo_result, null);
//        result.setFocusable(true);
//        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        mWindow = new PopupWindow(result, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams
//                .MATCH_PARENT, false);
//        mWindow.setOutsideTouchable(false);
//        mWindow.setFocusable(false);
//        mWindow.setTouchable(true);
//        mWindow.showAtLocation(result, Gravity.CENTER, 0, 0);
//        TextView recommand = (TextView) result.findViewById(R.id.tv_detail_result);
//        TextView beat = (TextView) result.findViewById(R.id.tv_result);
//        TextView totalscore = (TextView) result.findViewById(R.id.tv_total_score);
//        ImageButton shut = (ImageButton) result.findViewById(R.id.ib_shut);
//        result.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    mWindow.dismiss();
//                    mWindow = null;
//                    showPopupwinFeedback();
//                }
//                return true;
//            }
//        });
//        shut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWindow.dismiss();
//                showPopupwinFeedback();
////                setBackgroundAlpha(1f);
//            }
//        });
//
//
//    }
//
//    private void showPopupwinFeedback() {
//        VideoLivePlayBackEntity mVideoEntity = new VideoLivePlayBackEntity();
//        mVideoEntity.setChapterId("1");
//        mVideoEntity.setStuCourseId("22");
//        mVideoEntity.setLiveId("333");
//        LectureLivePlayBackBll lectureLivePlayBackBll = new LectureLivePlayBackBll(getActivity(), "");
//        ExperienceLearnFeedbackPager expFeedbackPager = new ExperienceLearnFeedbackPager(getActivity(),
// mVideoEntity, getActivity().getWindow(), lectureLivePlayBackBll);
//        mFeedbackWindow = new PopupWindow(expFeedbackPager.getRootView(), RelativeLayout.LayoutParams.MATCH_PARENT,
// RelativeLayout
//                .LayoutParams.MATCH_PARENT, false);
//        mFeedbackWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.color.transparent));
//        mFeedbackWindow.setOutsideTouchable(true);
//        mFeedbackWindow.setFocusable(true);
//        mFeedbackWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        mFeedbackWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        mFeedbackWindow.showAtLocation(expFeedbackPager.getRootView(), Gravity.CENTER, 0, 0);
//        expFeedbackPager.setCloseAction(new ExperienceLearnFeedbackPager.CloseAction() {
//            @Override
//            public void onClose() {
//                mFeedbackWindow.dismiss();
//            }
//        });
//    }
//    public void setBackgroundAlpha(float bgAlpha) {
//        WindowManager.LayoutParams lp = getActivity().getWindow()
//                .getAttributes();
//        lp.alpha = bgAlpha;
//        getActivity().getWindow().setAttributes(lp);
//    }
//
//    public static int dp2px(Context context, int dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
//                .getDisplayMetrics());
//    }

}
