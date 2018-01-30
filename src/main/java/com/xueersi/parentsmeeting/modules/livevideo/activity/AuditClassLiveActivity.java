package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
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

import com.xueersi.parentsmeeting.base.BaseCacheData;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.entity.FooterIconEntity;
import com.xueersi.parentsmeeting.event.AppEvent;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobEnumUtil;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassLiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerListener;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.VPlayerListener;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.videoplayer.media.XESVideoView;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.xesalib.umsagent.UmsAgentManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * 直播旁听课堂
 *
 * @author linyuqiang
 */
public class AuditClassLiveActivity extends LiveVideoActivityBase implements AuditVideoAction, ActivityStatic {

    private String TAG = "LiveVideoActivityLog";

    {
        mLayoutVideo = R.layout.activity_video_audit_live;
    }

    /** 播放器同步 */
    private static final Object mIjkLock = new Object();
    private static final Object mIjkLock2 = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    /** 缓冲超时 */
    private final long mBufferTimeout = 5000;
    /** 打开超时 */
    private final long mOpenTimeOut = 15000;
    private AuditClassLiveBll mLiveBll;
    private AuditClassBll auditClassBll;
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
    private long resumeTime;
    private LogToFile mLogtf;

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
    static int times = -1;

    protected boolean onVideoCreate(Bundle savedInstanceState) {
        times++;
        createTime = System.currentTimeMillis();
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        liveType = getIntent().getIntExtra("type", 0);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        AppBll.getInstance().registerAppEvent(this);
        boolean init = initData();
        if (!init) {
            onUserBackPressed();
            return false;
        }
        initView();
        findViewById(R.id.rl_livevideo_student_liveinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_AUDIO_LIVE,
                        "times=" + times + ",mVSectionID=" + mVSectionID + ",roomClick");
                OtherModulesEnter.intentToAuditClassActivity(AuditClassLiveActivity.this, mVSectionID);
            }
        });
        AtomicBoolean mIsLand = new AtomicBoolean(false);
        xv_livevideo_student.setIsLand(mIsLand);
        xv_livevideo_student.onCreate();
        xv_livevideo_student.setVPlayerListener(new VPlayerListener() {

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
                rl_livevideo_student.setVisibility(View.GONE);
                MediaController2 mMediaController = new MediaController2(AuditClassLiveActivity.this, xv_livevideo_student);
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
//                mLiveBll.startVideo();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            if (leave) {
                                mLiveBll.startVideo();
                            }
                            mLogtf.d("resultFailed:studentError=" + studentError);
                            if (!studentError.get()) {
                                if (StringUtils.isEmpty(playUrl)) {
                                    mLiveBll.liveGetStudentPlayServer();
                                } else {
                                    synchronized (mIjkLock2) {
                                        xv_livevideo_student.playNewVideo(Uri.parse(playUrl), mGetInfo.getName());
                                    }
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
        return true;
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
        // 预加载布局
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        rl_livevideo_student = (RelativeLayout) findViewById(R.id.rl_livevideo_student_load);
        rlLivevideoStudentVideo = (RelativeLayout) findViewById(R.id.rl_livevideo_student_video);
        xv_livevideo_student = (XESVideoView) findViewById(R.id.xv_livevideo_student_video);
        tv_livevideo_student_load_tip = (TextView) findViewById(R.id.tv_livevideo_student_load_tip);
        pb_livevideo_student_load = (ProgressBar) findViewById(R.id.pb_livevideo_student_load);
        iv_livevideo_student_camera = (ImageView) findViewById(R.id.iv_livevideo_student_camera);
        tv_livevideo_student_camera =
                (TextView) findViewById(R.id.tv_livevideo_student_camera);
        RelativeLayout bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //聊天
        //if (liveType != LiveBll.LIVE_TYPE_LECTURE) {
        //}
        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        //先让播放器按照默认模式设置
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
                (int) VIDEO_HEIGHT, VIDEO_RATIO);
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
                //Loger.i(TAG, "setVideoWidthAndHeight:isLand=" + isLand);
                if (!isLand) {
                    return;
                }
                videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
                        (int) VIDEO_HEIGHT, VIDEO_RATIO);
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                setFirstParam(lp);
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
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {// 直播
            mLiveBll = new AuditClassLiveBll(this, stuCouId, "", mVSectionID, from);
        } else {
            Toast.makeText(this, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        LogToFile.auditClassLiveBll = mLiveBll;
        mPlayStatistics = mLiveBll.getVideoListener();
        auditClassBll = new AuditClassBll(this);
        mLiveBll.setVideoAction(this);
        mLiveBll.getInfo();
        mLiveBll.setAuditClassAction(auditClassBll);
        return true;
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
        //Loger.e(TAG, "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
    }

    @Override
    protected void onPlayOpenStart() {
        setFirstBackgroundVisible(View.VISIBLE);
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPlayOpenSuccess() {
        TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
        if (tvFail != null) {
            tvFail.setVisibility(View.INVISIBLE);
        }
        setFirstBackgroundVisible(View.GONE);
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
            setFirstBackgroundVisible(View.VISIBLE);
            new Thread() {
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
            }.start();
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
        new Thread() {
            @Override
            public void run() {
                synchronized (mIjkLock) {
                    if (isInitialized()) {
                        vPlayer.releaseSurface();
                        vPlayer.stop();
                    }
                    isPlay = false;
                }
                synchronized (mIjkLock2) {
                    xv_livevideo_student.stop2();
                }
            }
        }.start();
    }

    @Override
    public boolean isResume() {
        return mIsResume;
    }

    @Override
    protected void resultFailed(final int arg1, final int arg2) {
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(arg1, arg2);
                        }
                    }
                }.start();
            }
        }, 1200);
    }

    @Override
    protected void playComplete() {
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(0, 0);
                        }
                    }
                }.start();
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
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onPlaybackComplete();
            mLogtf.d("onPlaybackComplete");
        }

        @Override
        public void onPlayError() {
            isPlay = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onPlayError();
        }

        @Override
        public void onOpenSuccess() {
            isPlay = true;
            mLogtf.d("onOpenSuccess:startRemote=" + startRemote.get());
            if (startRemote.get()) {
                stopPlay();
                return;
            }
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mPlayStatistics.onOpenSuccess();
            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
        }

        @Override
        public void onOpenStart() {
            mLogtf.d("onOpenStart");
            openStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(mOpenTimeOutRun);
            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
            mPlayStatistics.onOpenStart();
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            isPlay = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mPlayStatistics.onOpenFailed(arg1, arg2);
            mLogtf.d("onOpenFailed");
        }

        @Override
        public void onBufferStart() {
            mHandler.removeCallbacks(mBufferTimeOutRun);
            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
            mPlayStatistics.onBufferStart();
            mLogtf.d("onBufferStart");
        }

        @Override
        public void onBufferComplete() {
            mHandler.removeCallbacks(mBufferTimeOutRun);
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
                new Thread() {
                    @Override
                    public void run() {
                        videoCachedDuration = vPlayer.getVideoCachedDuration();
                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
                        mLiveBll.getOnloadLogs(TAG, "videoCachedDuration=" + videoCachedDuration);
                        if (videoCachedDuration > 10000) {
                            mLiveBll.streamReport(AuditClassLiveBll.MegId.MEGID_12130, mGetInfo.getChannelname(), -1);
                        }
                    }
                }.start();
                //Loger.i(TAG, "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
            }
        }
    };

    /**
     * 缓冲超时
     */
    private Runnable mBufferTimeOutRun = new Runnable() {

        @Override
        public void run() {
            long openTime = System.currentTimeMillis() - openStartTime;
            if (openTime > 40000) {
                mLiveBll.streamReport(AuditClassLiveBll.MegId.MEGID_12107, mGetInfo.getChannelname(), openTime);
            } else {
                mLiveBll.streamReport(AuditClassLiveBll.MegId.MEGID_12137, mGetInfo.getChannelname(), openTime);
            }
            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
            mLiveBll.repair(true);
            mLiveBll.liveGetPlayServer(false);
        }
    };

    /**
     * 打开超时
     */
    private Runnable mOpenTimeOutRun = new Runnable() {

        @Override
        public void run() {
            long openTimeOut = System.currentTimeMillis() - openStartTime;
            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
            mLiveBll.repair(false);
            mLiveBll.liveGetPlayServer(false);
        }
    };

    @Override
    public void onTeacherNotPresent(final boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                setFirstBackgroundVisible(View.VISIBLE);
//                String text;
//                if (isBefore) {
//                    text = "老师还未进入直播间，请稍后再来";
//                } else {
//                    text = "你来晚了，下课了，等着看回放吧";
//                }
//                final String msg = text;
//                if (tvLoadingHint != null) {
//                    tvLoadingHint.setText(msg);
//                }
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                        setFirstBackgroundVisible(View.VISIBLE);
                        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                        ivTeacherNotpresent.setVisibility(View.GONE);
                        return;
                    }
                }
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdai_bg_normal);
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
        mMediaController.setFileName(getInfo.getName());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                mLiveBll.getStudentLiveInfo();
                mHandler.postDelayed(this, 300000);
            }
        });
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        mServer = server;
        final AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        mLogtf.d("onLiveStart:change=" + change.get());
        mLiveTopic = cacheData;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
        rePlay(change.get());
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
                    tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学\n未在直播间");
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
    public void onStudentError(final String msg) {
        studentError.set(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                rl_livevideo_student.setVisibility(View.VISIBLE);
                pb_livevideo_student_load.setVisibility(View.GONE);
                tv_livevideo_student_load_tip.setVisibility(View.GONE);
                iv_livevideo_student_camera.setVisibility(View.VISIBLE);
                tv_livevideo_student_camera.setVisibility(View.VISIBLE);
                tv_livevideo_student_camera.setText(mGetInfo.getStuName() + "同学\n" + msg);
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
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                XESToastUtils.showToast(AuditClassLiveActivity.this, "您的帐号已在其他设备登录，请重新进入直播间");
//                finish();
//            }
//        });
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
                    vPlayer.stop();
                }
                isPlay = false;
                setFirstBackgroundVisible(View.VISIBLE);
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
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
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
            if (LiveTopic.MODE_TRANING.endsWith(mGetInfo.getLiveTopic().getMode()) && mGetInfo.getStudentLiveInfo().isExpe()) {
                tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                setFirstBackgroundVisible(View.VISIBLE);
                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                ivTeacherNotpresent.setVisibility(View.GONE);
                return;
            }
        }
        new Thread() {
            @Override
            public void run() {
                boolean isPresent = mLiveBll.isPresent();
                if (isPresent) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (tvLoadingHint != null) {
                                if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                                    tvLoadingHint.setText(mainTeacherLoad);
                                } else {
                                    tvLoadingHint.setText(coachTeacherLoad);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
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
                url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
            }
            msg += ",entity=" + entity.getIcode();
        }
        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url += "?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android";
                msg += ",t1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
                    url += "?" + mGetInfo.getSkeyPlayT() + "&cfrom=android";
                    msg += ",t2";
                } else {
                    url += "?cfrom=android";
                    msg += ",t3";
                }
            }
        } else {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url += "?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android";
                msg += ",f1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayF())) {
                    url += "?" + mGetInfo.getSkeyPlayF() + "&cfrom=android";
                    msg += ",f2";
                } else {
                    url += "?cfrom=android";
                    msg += ",f3";
                }
            }
        }
        msg += ",url=" + url;
        mLogtf.d(msg);
        playNewVideo(Uri.parse(url), mGetInfo.getName());
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
            vPlayer.stop();
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
                    if (mLiveBll.isPresent()) {
                        if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                    RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                    if (status != null) {
                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
                    }
                }
            }
        });
        mLiveBll.liveGetPlayServer(false);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        Loger.i(TAG, "onEvent:netWorkType=" + event.netWorkType);
        mLiveBll.onNetWorkChange(event.netWorkType);
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
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(this, mBaseApplication, false,
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
                    LogToFile.auditClassLiveBll = null;
                }
            }
        }.start();
        AppBll.getInstance().unRegisterAppEvent(this);
        xv_livevideo_student.onDestroy();
        super.onDestroy();
    }

    /**
     * 进入旁听课堂
     *
     * @param context
     * @param liveId
     */
    public static void intentTo(Context context, String stuCouId, String liveId) {
        Intent intent = new Intent(context, AuditClassLiveActivity.class);
        intent.putExtra("stuCouId", stuCouId);
        intent.putExtra("vSectionID", liveId);
        intent.putExtra("type", LiveBll.LIVE_TYPE_LIVE);
        context.startActivity(intent);
    }

    @Override
    protected void updateIcon() {
        updateLoadingImage();
        updateRefreshImage();
    }

    protected void updateLoadingImage() {
        Log.d("zhang", TAG + ":updateLoadingImage()");
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(this).load(loadingNoClickUrl).into(ivLoading);
        }
    }
}
