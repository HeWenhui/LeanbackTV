package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseCacheData;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.CenterLayout;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.VPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.XESVideoView;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassLiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodyAuditClassBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.AuditLiveEnvironment;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 直播旁听课堂
 *
 * @author linyuqiang
 */
public class AuditClassLiveActivity extends LiveVideoActivityBase implements AuditVideoAction, ActivityStatic {

    private String TAG = "AcLiveVideoActivityLog";
    private RelativeLayout businessRootView;

    {
        mLayoutVideo = R.layout.activity_video_audit_live;
    }

    /** 播放器同步 */
    private static final Object mIjkLock = new Object();
    private static final Object mIjkLock2 = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    /** 缓冲超时 */
//    private final long mBufferTimeout = 5000;
    /** 打开超时 */
//    private final long mOpenTimeOut = 15000;
    private AuditClassLiveBll mLiveBll;
    private AuditClassAction auditClassBll;
    /** 直播缓存打开统计 */
    private VPlayerListener mPlayStatistics;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    /** 老师不在直播间 */
    private ImageView ivTeacherNotpresent;
    /** 缓冲提示 */
    private ImageView ivLoading;
    private TextView tvLoadingHint;
    private LiveGetInfo mGetInfo;
    /** 直播服务器 */
    private PlayServerEntity mServer;
    private ArrayList<PlayserverEntity> failPlayserverEntity = new ArrayList<>();
    private ArrayList<PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
    /** 直播服务器-学生 */
    private PlayServerEntity mStudentServer;
    /** 直播服务器选择 */
    private PlayserverEntity lastPlayserverEntity;
    private int lastIndex;
    /** 直播服务器选择-学生 */
    private PlayserverEntity lastStudentPlayserverEntity;
    private int lastStudentIndex;
    private LiveTopic mLiveTopic;
    private String mVSectionID;
    private String stuCouId;
    private long createTime;
    /** Activity暂停过，执行onStop */
    private boolean mHaveStop = false;
    /** Activity在onResume */
    private boolean mIsResume = false;
    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public int isArts;
    private long resumeTime;
    private LogToFile mLogtf;
    boolean isBigLive;
    public static final String ENTER_ROOM_FROM = "from";
    /** 直播类型 */
    private int liveType;
    /** 连接老师加载-主讲 */
    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
    /** 连接老师加载-辅导 */
    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
    /** 正在播放 */
    private boolean isPlay = false;
    /** video缓存时间 */
    private long videoCachedDuration;
    /** 视频宽度 */
    public static final float VIDEO_WIDTH = 1280f;
    /** 视频高度 */
    public static final float VIDEO_HEIGHT = 720f;
    /** 视频宽高比 */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
    /** 头像宽度 */
    public static final float VIDEO_HEAD_WIDTH = 320f;
    /** 头像高度 */
    public static final float VIDEO_HEAD_HEIGHT = 240f;
    long openStartTime;
    /** 接麦已经连接老师 */
    private AtomicBoolean startRemote = new AtomicBoolean(false);
    int from = 0;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    RelativeLayout rlLivevideoStudentVideo;
    XESVideoView xv_livevideo_student;
    String playUrl;
    RelativeLayout rl_livevideo_student;
    /** 学生视频加载提示 */
    TextView tv_livevideo_student_load_tip;
    /** 学生视频加载进度 */
    ProgressBar pb_livevideo_student_load;
    /** 学生摄像头 */
    ImageView iv_livevideo_student_camera;
    /** 学生摄像头文字 */
    TextView tv_livevideo_student_camera;
    /** 学生是不是离开 */
    boolean leave = true;
    /** 学生是不是错误 */
    AtomicBoolean studentError = new AtomicBoolean(false);
    /** 学生是不是流畅模式 */
    AtomicBoolean fluentMode = new AtomicBoolean(false);
    static int times = -1;
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    protected LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
    private AuditLiveEnvironment liveEnvironment;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        times++;
        createTime = System.currentTimeMillis();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        liveType = getIntent().getIntExtra("type", 0);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LiveAppBll.getInstance().registerAppEvent(this);
        boolean init = initData();
        if (!init) {
            onUserBackPressed();
            return false;
        }
        mLogtf = new LogToFile(this, TAG);
        initView();
        return true;
    }

    @Override
    protected void createPlayer() {
        MediaPlayer.setIsNewIJK(true);
        super.createPlayer();
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    private void initView() {
        // 加载直播基本布局
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        businessRootView = findViewById(R.id.rcl_livevideo_auditclass_business_container);

        RelativeLayout bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        tvLoadingHint.setText("获取课程信息");
        //先让播放器按照默认模式设置
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH, (int) VIDEO_HEIGHT, VIDEO_RATIO);
    }

    //
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent event) {
//        if (event.getClass() == AppEvent.class) {
//            logger.i("onEvent:netWorkType=" + event.netWorkType);
////            mLiveVideoBll.onNetWorkChange(event.netWorkType);
//        }
//    }
    private void initListener() {
        AtomicBoolean mIsLand = new AtomicBoolean(false);
        xv_livevideo_student.setIsLand(mIsLand);
        xv_livevideo_student.onCreate();
        xv_livevideo_student.setZOrderOnTop(true);
        xv_livevideo_student.setVPlayerListener(new VPlayerListener() {

            @Override
            public void getPSServerList(int cur, int total, boolean modeChange) {

            }

//            @Override
//            public void getPServerListFail() {
//
//            }

            @Override
            public void onHWRenderFailed() {

            }

            @Override
            public void onVideoSizeChanged(int width, int height) {

            }

            @Override
            public void onOpenStart() {
                if (!studentError.get()) {
                    rl_livevideo_student.setVisibility(View.VISIBLE);
                    iv_livevideo_student_camera.setVisibility(View.GONE);
                    tv_livevideo_student_camera.setVisibility(View.GONE);
                    pb_livevideo_student_load.setVisibility(View.VISIBLE);
                    tv_livevideo_student_load_tip.setVisibility(View.VISIBLE);
                    tv_livevideo_student_load_tip.setText("加载学生视频");
                }
            }

            @Override
            public void onOpenSuccess() {
                xv_livevideo_student.setVisibility(View.VISIBLE);
                rl_livevideo_student.setVisibility(View.GONE);
                MediaController2 mMediaController = new MediaController2(AuditClassLiveActivity.this,
                        xv_livevideo_student);
                xv_livevideo_student.setMediaController(mMediaController);
            }

            @Override
            public void onOpenFailed(int arg1, int arg2) {

            }

            @Override
            public void onBufferStart() {

            }

            @Override
            public void onBufferComplete() {

            }

            @Override
            public void onSeekComplete() {

            }

            @Override
            public void onDownloadRateChanged(int kbPerSec) {

            }

            @Override
            public void onPlaybackComplete() {

            }

            @Override
            public void onCloseStart() {

            }

            @Override
            public void onCloseComplete() {

            }

            @Override
            public void onPlaying(long currentPosition, long duration) {

            }

            @Override
            public void onPlayError() {

            }
        });
        xv_livevideo_student.setPlayerListener(new PlayerListener() {

            @Override
            public void onPlayOpenSuccess() {

            }

            @Override
            public void onPlayOpenStart() {

            }

            @Override
            public void onSeekTo(long pos) {

            }

            @Override
            public void changeLOrP() {

            }

            @Override
            public void onBufferProgress() {

            }

            @Override
            public void onCloseStart() {

            }

            @Override
            public void removeLoadingView() {

            }

            @Override
            public void resultFailed(int arg1, int arg2) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            if (leave) {
                                mLiveBll.startVideo();
                            }
                            mLogtf.d("resultFailed:studentError=" + studentError);
                            if (!studentError.get()) {
                                synchronized (mIjkLock2) {
                                    xv_livevideo_student.playNewVideo(Uri.parse(playUrl), mGetInfo.getName());
                                }
                            }
                        }
                    }
                }, 10000);
            }

            @Override
            public void onUserBackPressed() {

            }

            @Override
            public void onShare() {

            }
        });
    }


    protected boolean initData() {
        Intent intent = getIntent();
        mVSectionID = intent.getStringExtra("vSectionID");
        stuCouId = intent.getStringExtra("stuCouId");
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(this, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        BaseCacheData.addUmsData("liveId", mVSectionID);
        UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_AUDIO_LIVE,
                "times=" + times + ",mVSectionID=" + mVSectionID);
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        //XesMobAgent.enterLiveRoomFrom(from);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            mLiveBll = new AuditClassLiveBll(this, stuCouId, "", mVSectionID, from);
        } else {
            Toast.makeText(this, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        mPlayStatistics = mLiveBll.getVideoListener();
        liveEnvironment = new AuditLiveEnvironment(this);
        isBigLive = getIntent().getBooleanExtra("isBigLive", false);
        liveEnvironment.setBigLive(isBigLive);
        auditClassBll = new AuditClassBll(liveEnvironment);
        mLiveBll.setVideoAction(this);
        mLiveBll.setAuditClassAction(auditClassBll);
        mLiveBll.setLiveEnvironment(liveEnvironment);
        return true;
    }

    @Override
    protected void onVideoCreateEnd() {
        mLiveBll.getInfo();
    }

    /** 设置蓝屏界面 */
    private void setFirstParam(ViewGroup.LayoutParams lp) {
        final View contentView = findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = (int) (VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
        }
        //家长头像
        RelativeLayout.LayoutParams xvlp = (RelativeLayout.LayoutParams) rlLivevideoStudentVideo.getLayoutParams();
        int screenHeight = ScreenUtils.getScreenHeight();
        int wradio = (int) (VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
        int hradio = (int) ((VIDEO_HEIGHT - VIDEO_HEAD_HEIGHT) * screenHeight / VIDEO_HEIGHT);
        if (xvlp.width != wradio || xvlp.height != screenHeight - hradio) {
            xvlp.width = wradio;
            xvlp.height = xvlp.topMargin = screenHeight - hradio;
            rlLivevideoStudentVideo.setLayoutParams(xvlp);
        }
        xvlp = (RelativeLayout.LayoutParams) rl_livevideo_student.getLayoutParams();
        if (xvlp.height != screenHeight - hradio) {
            xvlp.height = screenHeight - hradio;
            rl_livevideo_student.setLayoutParams(xvlp);
        }
        //logger.e( "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
    }


    @Override
    protected void onPlayOpenStart() {
        setFirstBackgroundVisible(View.VISIBLE);
        View view = findViewById(R.id.probar_course_video_loading_tip_progress);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPlayOpenSuccess() {
        TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
        if (tvFail != null) {
            tvFail.setVisibility(View.INVISIBLE);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFirstBackgroundVisible(View.GONE);
            }
        }, 100);

    }

    @Override
    public void onResume() {
        super.onResume();
        resumeTime = System.currentTimeMillis();
        mIsResume = true;
        if (mHaveStop) {
            mHaveStop = false;
            if (startRemote.get()) {
                return;
            }
            if (fluentMode.get()) {
                return;
            }
            setFirstBackgroundVisible(View.VISIBLE);
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                rePlay(false);
                            }
                        });
                    }
                    mLogtf.d("onResume:studentError=" + studentError);
                    if (!studentError.get()) {
                        synchronized (mIjkLock2) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (playUrl != null) {
                                        xv_livevideo_student.playNewVideo(Uri.parse(playUrl), mGetInfo.getName());
                                    } else {
                                        if (leave) {
                                            mLiveBll.startVideo();
                                        }
//                                rePlayStudent();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_AUDIO_LIVE,
                "times=" + times + ",mVSectionID=" + mVSectionID + ",resumeTime=" + (System.currentTimeMillis() - resumeTime));
        mIsResume = false;
        mHaveStop = true;
        if (startRemote.get()) {
            return;
        }
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mIjkLock) {
                    if (isInitialized()) {
                        vPlayer.releaseSurface();
                        if (MediaPlayer.getIsNewIJK()) {
                            transferStop();
                        } else {
                            vPlayer.stop();
                        }
                    }
                    isPlay = false;
                }
                synchronized (mIjkLock2) {
                    if (xv_livevideo_student != null) {
                        xv_livevideo_student.stop2();
                    }
                }
            }
        });
    }

    @Override
    public boolean isResume() {
        return mIsResume;
    }

    @Override
    protected void resultFailed(final int arg1, final int arg2) {
        if (MediaPlayer.getIsNewIJK()) {
            judgeTeacherIsPresent();
        }
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(arg1, arg2);
                        }
                    }
                });
            }
        }, 1200);
    }

    /** 判断老师是否在直播间 */
    private void judgeTeacherIsPresent() {
        boolean isPresent = mLiveBll.isPresent();
        if (!isPresent) {//如果不在，设置当前页面为老师不在直播间
            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
            }
        }
    }

    @Override
    protected void playComplete() {
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(0, MediaErrorInfo.PLAY_COMPLETE);
                        }
                    }
                });
            }
        }, 200);
    }

    @Override
    protected void onPlayError() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                tvLoadingHint.setText("您的手机暂时不支持播放直播");
                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onTitleShow(boolean show) {

    }

    @Override
    protected VPlayerListener getWrapListener() {
        return mPlayListener;
    }

    private VPlayerListener mPlayListener = new SimpleVPlayerListener() {

        @Override
        public void onPlaying(long currentPosition, long duration) {
            if (startRemote.get()) {
                mLogtf.d("onPlaying:startRemote");
                stopPlay();
            }
        }

        @Override
        public void onPlaybackComplete() {
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onPlaybackComplete();
            mLogtf.d("onPlaybackComplete");
        }

        @Override
        public void onPlayError() {
            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onPlayError();
        }

        @Override
        public void onOpenSuccess() {
            isPlay = true;
            mLogtf.d("onOpenSuccess:startRemote=" + startRemote.get() + ",fluentMode=" + fluentMode.get());
            if (startRemote.get() || fluentMode.get()) {
                stopPlay();
                return;
            }
//            mHandler.removeCallbacks(mOpenTimeOutRun);
            mPlayStatistics.onOpenSuccess();
            mHandler.removeCallbacks(getVideoCachedDurationRun);
            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
        }

        @Override
        public void onOpenStart() {
            mLogtf.d("onOpenStart");
            openStartTime = System.currentTimeMillis();
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
            mPlayStatistics.onOpenStart();
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onOpenFailed(arg1, arg2);
            mLogtf.d("onOpenFailed");
        }

        @Override
        public void onBufferStart() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
            mPlayStatistics.onBufferStart();
            mLogtf.d("onBufferStart");
        }

        @Override
        public void onBufferComplete() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onBufferComplete();
            mLogtf.d("onBufferComplete");
        }
    };


    /** 得到Video缓存时间 */
    private Runnable getVideoCachedDurationRun = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isPlay && !isFinishing()) {
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        videoCachedDuration = vPlayer.getVideoCachedDuration();
                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
                        mLogtf.d("videoCachedDuration=" + videoCachedDuration);
                    }
                });
                //logger.i( "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
            }
        }
    };

    /**
     * 缓冲超时
     */
//    private Runnable mBufferTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
//            mLiveBll.repair(true);
////            mLiveBll.liveGetPlayServer(false);
//            changeNextLine();
//        }
//    };
    protected void changeNextLine() {
        this.nowPos++;
        if (nowProtol == MediaPlayer.VIDEO_PROTOCOL_NO_PROTOL) {
            //初始化
            nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
            mLiveBll.liveGetPlayServer(false);
            return;
        }
        //当前线路小于总线路数
        if (this.nowPos < totalRouteNum) {
            changePlayLive(this.nowPos, nowProtol);
        } else {
            nowProtol = changeProtol(nowProtol);
            mLiveBll.liveGetPlayServer(false);
        }
    }

    /**
     * 打开超时
     */
//    private Runnable mOpenTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            long openTimeOut = System.currentTimeMillis() - openStartTime;
//            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
//            mLiveBll.repair(false);
////            mLiveBll.liveGetPlayServer(false);
//            changeNextLine();
//        }
//    };
    @Override
    public void onTeacherNotPresent(final boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                        setFirstBackgroundVisible(View.VISIBLE);
                        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                        ivTeacherNotpresent.setVisibility(View.GONE);
                        return;
                    }
                }
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        mGetInfo = getInfo;
        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
        isArts = mGetInfo.getIsArts();
        mMediaController.setFileName(getInfo.getName());
        mLiveBll.setHalfBodyLive(isHalfBodyLive());
        liveEnvironment.setLiveGetInfo(getInfo);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                mLiveBll.getStudentLiveInfo();
                long delayMillis;
                //大班直播一分钟
                if (isBigLive) {
                    delayMillis = 60000;
                } else {
                    delayMillis = 300000;
                }
                mHandler.postDelayed(this, delayMillis);
            }
        });
        initBussinessUI();
    }

    private void initBussinessUI() {
        if (isHalfBodyLive()) {
            initHalfBodyUI();
        } else {
            initNormalUI();
        }
    }

    private void initNormalUI() {
        View view = View.inflate(this, R.layout.layout_auditclass_studentinfo, null);
        businessRootView.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //业务UI
        rl_livevideo_student = (RelativeLayout) findViewById(R.id.rl_livevideo_student_load);
        rlLivevideoStudentVideo = (RelativeLayout) findViewById(R.id.rl_livevideo_student_video);
        xv_livevideo_student = (XESVideoView) findViewById(R.id.xv_livevideo_student_video);
        tv_livevideo_student_load_tip = (TextView) findViewById(R.id.tv_livevideo_student_load_tip);
        pb_livevideo_student_load = (ProgressBar) findViewById(R.id.pb_livevideo_student_load);
        iv_livevideo_student_camera = (ImageView) findViewById(R.id.iv_livevideo_student_camera);
        tv_livevideo_student_camera = (TextView) findViewById(R.id.tv_livevideo_student_camera);

        findViewById(R.id.rl_livevideo_student_liveinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGetInfo == null) {
                    XESToastUtils.showToast(AuditClassLiveActivity.this, "请稍等");
                    return;
                }
                UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_AUDIO_LIVE,
                        "times=" + times + ",mVSectionID=" + mVSectionID + ",roomClick");
                Bundle bundle = new Bundle();
                bundle.putInt("isArts", isArts);
                bundle.putBoolean("isBigLive", isBigLive);
                if (isBigLive) {
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    bundle.putInt("classId", Integer.parseInt(studentLiveInfo.getClassId()));
                    bundle.putInt("teamId", Integer.parseInt(studentLiveInfo.getTeamId()));
                }
                OtherModulesEnter.intentToAuditClassActivity(AuditClassLiveActivity.this, mVSectionID, stuCouId, bundle);
            }
        });

        auditClassBll = new AuditClassBll(liveEnvironment);
        mLiveBll.setAuditClassAction(auditClassBll);


        //重新 计算 业务UI 和视频 窗口的位置关系
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        setFirstParam(lp);
        final View contentView = findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (videoView.getWidth() <= 0) {
                    return;
                }
                boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                if (!isLand) {
                    return;
                }
                videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
                        (int) VIDEO_HEIGHT, VIDEO_RATIO);
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                setFirstParam(lp);
            }
        });
        initListener();
    }

    private void initHalfBodyUI() {
        View view = View.inflate(this, R.layout.layout_auditclass_halfbody_studentinfo, null);
        businessRootView.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //业务UI
        rl_livevideo_student = (RelativeLayout) findViewById(R.id.rl_livevideo_student_load);
        rlLivevideoStudentVideo = (RelativeLayout) findViewById(R.id.rl_livevideo_student_video);
        xv_livevideo_student = (XESVideoView) findViewById(R.id.xv_livevideo_student_video);
        tv_livevideo_student_load_tip = (TextView) findViewById(R.id.tv_livevideo_student_load_tip);
        pb_livevideo_student_load = (ProgressBar) findViewById(R.id.pb_livevideo_student_load);
        iv_livevideo_student_camera = (ImageView) findViewById(R.id.iv_livevideo_student_camera);
        tv_livevideo_student_camera = (TextView) findViewById(R.id.tv_livevideo_student_camera);

        auditClassBll = new HalfBodyAuditClassBll(this);
        mLiveBll.setAuditClassAction(auditClassBll);

        //重新 计算 业务UI 和视频 窗口的位置关系
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        setHalfBodyUiPrama(lp);
        final View contentView = findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (videoView.getWidth() <= 0) {
                    return;
                }
                boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                if (!isLand) {
                    return;
                }
                CenterLayout.LayoutParams params = calculateLayoutParam(videoView);
                if (params.width != videoView.getWidth()) {
                    videoView.setLayoutParams(params);
                    ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                    setHalfBodyUiPrama(lp);
                }
            }
        });
        initListener();
    }

    /**
     * 根据尺寸信息 计算viewView 的布局参数
     *
     * @param videoView
     * @return
     */
    private CenterLayout.LayoutParams calculateLayoutParam(com.xueersi.parentsmeeting.module.videoplayer.media.VideoView videoView) {
        // 获取屏幕 尺寸，比例信息
        View contentView = findViewById(android.R.id.content);
        View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int windowWidth = (r.right - r.left);
        int windowHeight = ScreenUtils.getScreenHeight();
        float windowRatio = windowWidth / (float) windowHeight;
        int videoViewWidth = videoView.getWidth();
        int videoViewHeight = videoView.getHeight();
        // videoViewWidth = (int) (windowWidth * (windowRatio / VIDEO_RATIO)*
        videoViewWidth = (int) (windowWidth * 0.75f);
        videoViewHeight = (int) (videoViewWidth * 1 / VIDEO_RATIO);
        //Log.e(TAG,"=======>calculateLayoutParam:"+videoViewWidth+":"+videoViewHeight);
        return new CenterLayout.LayoutParams(videoViewWidth, videoViewHeight, 0, 0);
    }

    /**
     * 设置全是直播 UI 位置，尺寸 信息
     *
     * @param lp
     */
    private void setHalfBodyUiPrama(ViewGroup.LayoutParams lp) {
        final View contentView = findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = screenWidth - lp.width;
        int topMargin = 0;
        if (params.rightMargin != rightMargin) {
            params.rightMargin = rightMargin;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            // Log.e(TAG,"====>setHalfBodyUiPrama:"+params.rightMargin);
            viewRoot.setLayoutParams(params);
        }

        RelativeLayout.LayoutParams xvlp = (RelativeLayout.LayoutParams) rlLivevideoStudentVideo.getLayoutParams();
        int screenHeight = ScreenUtils.getScreenHeight();
        int wradio = rightMargin;
        int hradio = (int) (rightMargin * VIDEO_HEAD_HEIGHT / VIDEO_HEAD_WIDTH);
        if (xvlp.width != wradio || xvlp.height != hradio) {
            xvlp.width = wradio;
            xvlp.height = hradio;
            //Log.e(TAG,"=====>setHalfBodyUiPrama:"+wradio+":"+hradio+":"+xvlp.height);
            xvlp.topMargin = 0;
            rlLivevideoStudentVideo.setLayoutParams(xvlp);
        }
        xvlp = (RelativeLayout.LayoutParams) rl_livevideo_student.getLayoutParams();
        if (xvlp.height != hradio) {
            xvlp.height = hradio;
            xvlp.topMargin = 0;
            xvlp.width = wradio;
            rl_livevideo_student.setLayoutParams(xvlp);
        }
    }

    /**
     * 是否是半身直播
     *
     * @return
     */
    private boolean isHalfBodyLive() {
        return mGetInfo != null && mGetInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY;
    }

    /***
     * PSIJK不在走这个方法
     * @param server
     * @param cacheData
     * @param modechange
     */
    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
//        if (!MediaPlayer.getIsNewIJK()) {
        mServer = server;
//        } else {

//        }
        // 直播状态是不是变化
        final AtomicBoolean change = new AtomicBoolean(modechange);
        mLogtf.d("onLiveStart:change=" + change.get() + ",fluentMode=" + fluentMode.get());
        mLiveTopic = cacheData;
        if (fluentMode.get()) {
            return;
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
        rePlay(change.get());

    }

//    @Override
//    public void getPSServerList(int cur, int total) {
//
//    }

    @Override
    public void onLiveTimeOut() {
//        final Button bt = findViewById(R.id.bt_course_video_livetimeout);
//        if (bt != null) {
//            bt.setVisibility(View.VISIBLE);
//            bt.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt.getLayoutParams();
////                    lp.leftMargin = LiveVideoPoint.getInstance().x3 / 2 - bt.getWidth() / 2;
////                    if (tvLoadingHint != null) {
////                        int[] outLocation = new int[2];
////                        tvLoadingHint.getLocationInWindow(outLocation);
////                        lp.topMargin = outLocation[1] + tvLoadingHint.getHeight() + 20;
////                    } else {
////                        lp.topMargin = LiveVideoPoint.getInstance().screenHeight * 2 / 3 - 40;
////                    }
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//                    bt.setLayoutParams(lp);
//                    bt.getViewTreeObserver().removeOnPreDrawListener(this);
//                    return false;
//                }
//            });
//            bt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLiveBll.liveGetPlayServer(false);
//                    v.setVisibility(View.GONE);
//                }
//            });
//        } else {
//            XESToastUtils.showToast(this, "请退出直播间重试");
//        }
    }

    @Override
    public void onStudentLeave(final boolean leave, final String stuPushStatus) {
        this.leave = leave;
        if (leave && !xv_livevideo_student.isPlaying()) {
            mLiveBll.startVideo();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (leave) {
//                    pb_livevideo_student_load.setVisibility(View.INVISIBLE);
//                    tv_livevideo_student_load_tip.setText(mGetInfo.getStuName() + "同学未在直播间");
                    pb_livevideo_student_load.setVisibility(View.GONE);
                    tv_livevideo_student_load_tip.setVisibility(View.GONE);
                    iv_livevideo_student_camera.setVisibility(View.VISIBLE);
                    tv_livevideo_student_camera.setVisibility(View.VISIBLE);
                    //半身直播单行显示
                    if (isHalfBodyLive()) {
                        tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学未在直播间");
                    } else {
                        tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学\n未在直播间");
                    }
                } else {
                    iv_livevideo_student_camera.setVisibility(View.GONE);
                    tv_livevideo_student_camera.setVisibility(View.GONE);
                    pb_livevideo_student_load.setVisibility(View.VISIBLE);
                    tv_livevideo_student_load_tip.setVisibility(View.VISIBLE);
                    tv_livevideo_student_load_tip.setText("加载学生视频");
                }
            }
        });
    }

    @Override
    public void onStudentError(final String status, final String msg) {
        if (fluentMode.get()) {
            return;
        }
        studentError.set(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                xv_livevideo_student.setVisibility(View.GONE);
                rl_livevideo_student.setVisibility(View.VISIBLE);
                pb_livevideo_student_load.setVisibility(View.GONE);
                tv_livevideo_student_load_tip.setVisibility(View.GONE);
                iv_livevideo_student_camera.setVisibility(View.VISIBLE);
                tv_livevideo_student_camera.setVisibility(View.VISIBLE);
                if ("fluentMode".equals(status)) {
                    if (vPlayer.isInitialized()) {
                        vPlayer.onDestroy();
                    }
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setErrorMsg("流畅模式不支持该功能，如您需要，可在电脑客户端右上角修改为标准模式");
                    onLiveError(responseEntity);
                    tv_livevideo_student_camera.setText("");
                    fluentMode.set(true);
                    setFirstBackgroundVisible(View.VISIBLE);
                } else {
                    tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学\n" + msg);
                }
            }
        });
    }

    @Override
    public void onStudentLiveStart(PlayServerEntity server) {
        mStudentServer = server;
        synchronized (mIjkLock2) {
            rePlayStudent();
        }
    }

    @Override
    public void onStudentLiveUrl(String playUrl) {
        studentError.set(false);
        if (playUrl.equals(this.playUrl) && xv_livevideo_student.isPlaying()) {
            return;
        }
        this.playUrl = playUrl;
        synchronized (mIjkLock2) {
            xv_livevideo_student.playNewVideo(Uri.parse(playUrl), mGetInfo.getName());
        }
    }

    @Override
    public void onKick() {
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        mLogtf.i("onModeChange:mode=" + mode);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mLogtf.d("onModeChange:isInitialized=" + isInitialized());
                if (isInitialized()) {
                    vPlayer.releaseSurface();
                    transferStop();
                }
                isPlay = false;
                setFirstBackgroundVisible(View.VISIBLE);
                if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mode)) {
                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                        return;
                    }
                }
                if (isPresent) {
                    if (tvLoadingHint != null) {
                        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onClassTimoOut() {
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "你来晚了，下课了，等着看回放吧";
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onLiveDontAllow(final String msg) {
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
        XESToastUtils.showToast(this, "将在3秒内退出");
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
                finish();
            }
        }, 3000);
    }

    @Override
    public void onLiveError(final ResponseEntity responseEntity) {
        mLogtf.d("onLiveError");
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "" + responseEntity.getErrorMsg();
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    public AtomicBoolean getStartRemote() {
        return startRemote;
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     * <p>
     * 左边区域采用PlayLive来播放，右边采用playFile，
     * 这里是播放左边区域，采用playLive
     *
     * @param modechange
     */
    public void rePlay(boolean modechange) {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        if (startRemote.get()) {
            return;
        }
        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (LiveTopic.MODE_TRANING.endsWith(mGetInfo.getLiveTopic().getMode()) && mGetInfo.getStudentLiveInfo().isExpe()) {
                tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                setFirstBackgroundVisible(View.VISIBLE);
                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                ivTeacherNotpresent.setVisibility(View.GONE);
                return;
            }
        }
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean isPresent = mLiveBll.isPresent();
                if (isPresent) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (tvLoadingHint != null) {
                                if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                                    tvLoadingHint.setText(mainTeacherLoad);
                                } else {
                                    tvLoadingHint.setText(coachTeacherLoad);
                                }
                            }
                        }
                    });
                }
            }
        });


        String url;
        String msg = "rePlay:";
        if (mServer == null) {
            String rtmpUrl = null;
            String[] rtmpUrls = mGetInfo.getRtmpUrls();
            if (rtmpUrls != null) {
                rtmpUrl = rtmpUrls[(lastIndex++) % rtmpUrls.length];
            }
            if (rtmpUrl == null) {
                rtmpUrl = mGetInfo.getRtmpUrl();
            }
            url = rtmpUrl + "/" + mGetInfo.getChannelname();
            msg += "mServer=null";
            mLiveBll.setPlayserverEntity(null);
        } else {
            List<PlayserverEntity> playservers = mServer.getPlayserver();
            msg += "playservers=" + playservers.size();
            PlayserverEntity entity = null;
            boolean useFlv = false;
            if (lastPlayserverEntity == null) {
                msg += ",lastPlayserverEntity=null";
                entity = playservers.get(0);
            } else {
                msg += ",failPlayserverEntity=" + failPlayserverEntity.size();
                if (!failPlayserverEntity.isEmpty()) {
                    boolean allRtmpFail = true;
                    boolean allFlvFail = true;
                    List<PlayserverEntity> flvPlayservers = new ArrayList<>();
                    for (int i = 0; i < playservers.size(); i++) {
                        PlayserverEntity playserverEntity = playservers.get(i);
                        if (!StringUtils.isEmpty(playserverEntity.getFlvpostfix())) {
                            flvPlayservers.add(playserverEntity);
                            if (!failFlvPlayserverEntity.contains(playserverEntity)) {
                                allFlvFail = false;
                            }
                        }
                        if (!failPlayserverEntity.contains(playserverEntity)) {
                            allRtmpFail = false;
                        }
                    }
                    if (allFlvFail) {
                        msg += ",allFlvFail";
                        failPlayserverEntity.clear();
                        failFlvPlayserverEntity.clear();
                    } else {
                        if (allRtmpFail) {
                            if (flvPlayservers.isEmpty()) {
                                failPlayserverEntity.clear();
                            } else {
                                if (!lastPlayserverEntity.isUseFlv()) {
                                    entity = flvPlayservers.get(0);
                                    entity.setUseFlv(true);
                                    useFlv = true;
                                    msg += ",setUseFlv1";
                                } else {
                                    for (int i = 0; i < flvPlayservers.size(); i++) {
                                        PlayserverEntity playserverEntity = flvPlayservers.get(i);
                                        if (lastPlayserverEntity.getAddress().equals(playserverEntity.getAddress())) {
                                            if (modechange) {
                                                entity = flvPlayservers.get(i % flvPlayservers.size());
                                            } else {
                                                entity = flvPlayservers.get((i + 1) % flvPlayservers.size());
                                            }
                                            entity.setUseFlv(true);
                                            useFlv = true;
                                            msg += ",setUseFlv2,modechange=" + modechange;
                                            break;
                                        }
                                    }
                                    if (entity == null) {
                                        msg += ",entity=null1";
                                        entity = flvPlayservers.get(0);
                                    }
                                }
                            }
                        }
                    }
                }
                if (entity == null) {
                    for (int i = 0; i < playservers.size(); i++) {
                        PlayserverEntity playserverEntity = playservers.get(i);
                        if (lastPlayserverEntity.equals(playserverEntity)) {
                            if (modechange) {
                                entity = playservers.get(i % playservers.size());
                            } else {
                                entity = playservers.get((i + 1) % playservers.size());
                            }
                            msg += ",entity=null2,modechange=" + modechange;
                            break;
                        }
                    }
                }
                if (entity == null) {
                    msg += ",entity=null3";
                    entity = playservers.get(0);
                }
            }
            lastPlayserverEntity = entity;
            mLiveBll.setPlayserverEntity(entity);
            if (useFlv) {
                url = "http://" + entity.getAddress() + ":" + entity.getHttpport() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname() + entity.getFlvpostfix();
            } else {
                if (StringUtils.isEmpty(entity.getIp_gslb_addr())) {
                    url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                } else {
                    final PlayserverEntity finalEntity = entity;
                    mLiveBll.dns_resolve_stream(entity, mServer, mGetInfo.getChannelname(), new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            if (finalEntity != lastPlayserverEntity) {
                                return;
                            }
                            String provide = (String) objData[0];
                            String url;
                            if ("wangsu".equals(provide)) {
                                url = objData[1] + "&username=" + mGetInfo.getUname() + "&cfrom=android";
                                playNewVideo(Uri.parse(url), mGetInfo.getName());
                            } else if ("ali".equals(provide)) {
                                url = (String) objData[1];
                                StringBuilder stringBuilder = new StringBuilder(url);
                                addBody("Sucess", stringBuilder);
                                url = stringBuilder + "&username=" + mGetInfo.getUname();
                                playNewVideo(Uri.parse(url), mGetInfo.getName());
                            } else {
                                return;
                            }
                            StableLogHashMap stableLogHashMap = new StableLogHashMap("glsb3rdDnsReply");
                            stableLogHashMap.put("message", "" + url);
                            stableLogHashMap.put("activity", mContext.getClass().getSimpleName());
                            UmsAgentManager.umsAgentDebug(mContext, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData());
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            if (finalEntity != lastPlayserverEntity) {
                                return;
                            }
                            String url = "rtmp://" + finalEntity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                            StringBuilder stringBuilder = new StringBuilder(url);
                            addBody("Fail", stringBuilder);
                            playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
                        }
                    });
                    return;
                }
            }
            msg += ",entity=" + entity.getIcode();
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        msg += addBody("rePlay", stringBuilder);
        msg += ",url=" + stringBuilder;
        mLogtf.d(msg);
        logger.i("url = " + url);
        if (!MediaPlayer.getIsNewIJK()) {
            playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
        } else {
            if (nowProtol == MediaPlayer.VIDEO_PROTOCOL_RTMP || nowProtol == MediaPlayer.VIDEO_PROTOCOL_FLV) {
            } else {
                nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
            }
            playPSVideo(mGetInfo.getChannelname(), nowProtol);
            if (mGetInfo.getVideoConfigEntity() != null) {
                enableAutoSpeedPlay(mGetInfo.getVideoConfigEntity());
            }
            setmDisplayName(mGetInfo.getName());
        }
    }


    /** 得到转化的协议 */
    public int changeProtol(int now) {
        int tempProtol;
        if (now == MediaPlayer.VIDEO_PROTOCOL_RTMP) {
            tempProtol = MediaPlayer.VIDEO_PROTOCOL_FLV;
        } else {
            tempProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
        }
        return tempProtol;
    }

    protected String addBody(String method, StringBuilder url) {
        String msg = "";
        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
                msg += ",t1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
                    url.append("?" + mGetInfo.getSkeyPlayT() + "&cfrom=android");
                    msg += ",t2";
                } else {
                    url.append("?cfrom=android");
                    msg += ",t3";
                }
            }
        } else {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
                msg += ",f1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayF())) {
                    url.append("?" + mGetInfo.getSkeyPlayF() + "&cfrom=android");
                    msg += ",f2";
                } else {
                    url.append("?cfrom=android");
                    msg += ",f3";
                }
            }
        }
        logger.d("addBody:method=" + method + ",url=" + url);
        return msg;
    }

    public void rePlayStudent() {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        if (leave) {
            pb_livevideo_student_load.setVisibility(View.GONE);
            tv_livevideo_student_load_tip.setVisibility(View.GONE);
            iv_livevideo_student_camera.setVisibility(View.VISIBLE);
            tv_livevideo_student_camera.setVisibility(View.VISIBLE);
            tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学未在直播间");
        } else {
            iv_livevideo_student_camera.setVisibility(View.GONE);
            tv_livevideo_student_camera.setVisibility(View.GONE);
            pb_livevideo_student_load.setVisibility(View.VISIBLE);
            tv_livevideo_student_load_tip.setVisibility(View.VISIBLE);
            tv_livevideo_student_load_tip.setText("加载学生视频");
        }
        String url;
        String msg = "rePlayStudent:";
        if (mStudentServer == null) {
            String rtmpUrl = null;
            String[] rtmpUrls = mGetInfo.getRtmpUrls();
            if (rtmpUrls != null) {
                rtmpUrl = rtmpUrls[(lastStudentIndex++) % rtmpUrls.length];
            }
            if (rtmpUrl == null) {
                rtmpUrl = mGetInfo.getRtmpUrl();
            }
            url = rtmpUrl + "/" + mGetInfo.getStudentChannelname();
            msg += "mServer=null";
//            mLiveBll.setPlayserverEntity(null);
        } else {
            List<PlayserverEntity> playservers = mStudentServer.getPlayserver();
            PlayserverEntity entity = null;
            if (lastStudentPlayserverEntity == null) {
                msg += ",lastPlayserverEntity=null";
                entity = playservers.get(0);
            } else {
                for (int i = 0; i < playservers.size(); i++) {
                    PlayserverEntity playserverEntity = playservers.get(i);
                    if (lastStudentPlayserverEntity.getAddress().equals(playserverEntity.getAddress())) {
                        entity = playservers.get((i + 1) % playservers.size());
                        msg += ",equals";
                        break;
                    }
                }
                if (entity == null) {
                    msg += ",entity=null";
                    entity = playservers.get(0);
                }
            }
            lastStudentPlayserverEntity = entity;
//            mLiveBll.setPlayserverEntity(entity);
            url = "rtmp://" + entity.getAddress() + "/" + mStudentServer.getAppname() + "/" + mGetInfo.getStudentChannelname();
            msg += ",entity=" + entity.getIcode();
        }
        if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
            url += "?" + mGetInfo.getSkeyPlayT() + "&cfrom=android";
        } else {
            url += "?cfrom=android";
        }
        msg += ",t";
        msg += ",url=" + url;
        mLogtf.d(msg);
        xv_livevideo_student.playNewVideo(Uri.parse(url), mGetInfo.getName());
//        xv_livevideo_student.playNewVideo(Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks/"), mGetInfo.getName());
    }

    public void stopPlay() {
        if (isInitialized()) {
            vPlayer.releaseSurface();
            transferStop();
        }
    }

    /**
     * 调用底层播放器的停止播放
     */
    private void transferStop() {
        if (!MediaPlayer.getIsNewIJK()) {
            vPlayer.stop();
        } else {
            vPlayer.psStop();
        }
    }

    /**
     * 播放失败，或者完成时调用
     */
    private void onFail(int arg1, final int arg2) {
        if (lastPlayserverEntity != null) {
            if (lastPlayserverEntity.isUseFlv()) {
                if (!failFlvPlayserverEntity.contains(lastPlayserverEntity)) {
                    failFlvPlayserverEntity.add(lastPlayserverEntity);
                }
            } else {
                if (!failPlayserverEntity.contains(lastPlayserverEntity)) {
                    failPlayserverEntity.add(lastPlayserverEntity);
                }
            }
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (tvLoadingHint != null) {
                    String errorMsg = null;
                    AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
                    if (error != null) {
                        errorMsg = error.getNum() + " (" + error.getTag() + ")";
                    }
                    TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
                    if (errorMsg != null) {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.VISIBLE);
                            tvFail.setText(errorMsg);
                        }
                    } else {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.INVISIBLE);
                        }
                    }
                    mLogtf.d("onFail:arg2=" + arg2 + ",errorMsg=" + errorMsg + ",isPresent=" + mLiveBll.isPresent());
                    if (fluentMode.get()) {
                        if (vPlayer != null) {
                            vPlayer.onDestroy();
                        }
                    } else {
                        if (!fluentMode.get() && mLiveBll.isPresent()) {
                            if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                                tvLoadingHint.setText(mainTeacherLoad);
                            } else {
                                tvLoadingHint.setText(coachTeacherLoad);
                            }
                        }
                    }
                    RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                    if (status != null) {
                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
                    }
                }
            }
        });

        switch (arg2) {
            case MediaErrorInfo.PSPlayerError: {
                //播放器错误
                break;
            }
            case MediaErrorInfo.PSDispatchFailed: {
                //调度失败，建议重新访问playLive或者playVod频道不存在
                //调度失败，延迟1s再次访问调度
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
                        mLiveBll.liveGetPlayServer(false);
                    }
                }, 1000);
            }
            break;
            case MediaErrorInfo.PSChannelNotExist: {
                //提示用户等待,交给上层来处理

                break;
            }
            case MediaErrorInfo.PSServer403: {
                //防盗链鉴权失败，需要重新访问playLive或者playVod
//                mLiveBll.liveGetPlayServer(false);
//                playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
                mLiveBll.liveGetPlayServer(false);
            }
            break;
            case MediaErrorInfo.PLAY_COMPLETE: {
//                mLiveBll.liveGetPlayServer(false);
//                playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
                mLiveBll.liveGetPlayServer(false);
            }
            break;
            default:
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
                        mLiveBll.liveGetPlayServer(false);
                    }
                }, 1000);
                //除了这四种情况，还有播放完成的情况
                break;
        }

    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void setFirstBackgroundVisible(int visible) {
        if (fluentMode.get()) {
            rlFirstBackgroundView.setVisibility(View.VISIBLE);
            ivTeacherNotpresent.setVisibility(View.GONE);
        } else {
            rlFirstBackgroundView.setVisibility(visible);
            if (visible == View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }
        }
    }

    private boolean lastNowNetWork = true;

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        logger.i("onEvent:netWorkType=" + event.netWorkType);
        mLiveBll.onNetWorkChange(event.netWorkType);
        if (event.netWorkType != NetWorkHelper.NO_NETWORK) {
            if (xv_livevideo_student != null && playUrl != null && mGetInfo.getName() != null && lastNowNetWork) {
                xv_livevideo_student.playNewVideo(Uri.parse(playUrl), mGetInfo.getName());
                lastNowNetWork = false;
            }
        } else {
            lastNowNetWork = true;
        }
    }

    /**
     * 只在WIFI下使用激活
     *
     * @param onlyWIFIEvent
     * @author zouhao
     * @Create at: 2015-9-24 下午1:57:04
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {
        Toast.makeText(this, "没有wifi", Toast.LENGTH_SHORT).show();
        onUserBackPressed();
    }

    /** 是否显示移动网络提示 */
    private boolean mIsShowMobileAlert = true;

    /**
     * 开启了3G/4G提醒
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-10-12 下午1:49:22
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.NowMobileEvent event) {
        if (mIsShowMobileAlert) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(this, ContextManager.getApplication(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserBackPressed();
                }
            });
            cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？")
                    .showDialog();
            mIsShowMobileAlert = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onUserBackPressed() {
        super.onUserBackPressed();
    }

    @Override
    public void onDestroy() {
        isPlay = false;
        UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_AUDIO_LIVE,
                "times=" + times + ",mVSectionID=" + mVSectionID + ",createTime=" + (System.currentTimeMillis() - createTime));
        if (mLogtf != null) {
            mLogtf.d("onDestroy");
        }
        new Thread() {
            @Override
            public void run() {
                if (mLiveBll != null) {
                    mLiveBll.onDestroy();
                }
                ProxUtil.getProxUtil().clear(AuditClassLiveActivity.this);
            }
        }.start();
        LiveAppBll.getInstance().unRegisterAppEvent(this);

        if (xv_livevideo_student != null) {
            xv_livevideo_student.onDestroy();
        }
        super.onDestroy();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LiveThreadPoolExecutor.destory();
            }
        });
    }

    /**
     * 进入旁听课堂
     *
     * @param context
     * @param liveId
     */
    public static void intentTo(Context context, String stuCouId, String liveId, boolean isBigLive) {
        Intent intent = new Intent(context, AuditClassLiveActivity.class);
        intent.putExtra("stuCouId", stuCouId);
        intent.putExtra("isBigLive", isBigLive);
        intent.putExtra("vSectionID", liveId);
        intent.putExtra("type", LiveVideoConfig.LIVE_TYPE_LIVE);
        context.startActivity(intent);
    }

    @Override
    protected void updateIcon() {
        updateLoadingImage();
        updateRefreshImage();
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {

    }

    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
            }
        }
    }

    /**
     * 当前处于哪条线路
     */
    private int nowPos;
    /** 当前使用的协议,初始值为-1 */
    private int nowProtol = MediaPlayer.VIDEO_PROTOCOL_NO_PROTOL;
    /**
     *
     */
    private int totalRouteNum;

    @Override
    public void getPSServerList(int cur, int total, boolean modeChange) {
        this.nowPos = cur;
        this.totalRouteNum = total;
//        mServer = server;
        // 直播状态是不是变化
        final AtomicBoolean change = new AtomicBoolean(modeChange);
        mLogtf.d("onLiveStart:change=" + change.get() + ",fluentMode=" + fluentMode.get());
//        mLiveTopic = cacheData;
        if (fluentMode.get()) {
            return;
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
//        rePlay(change.get());
    }

//    @Override
//    public void getPServerListFail() {
//
//    }
}
