package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.event.AppEvent;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobEnumUtil;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.H5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.PraiseOrEncourageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RedPackageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.VPlayerListener;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VP;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LectureLiveVideoActivity extends LiveVideoActivityBase implements VideoAction, ActivityStatic, BaseLiveMessagePager.OnMsgUrlClick {

    private String TAG = "LecLiveVideoActivityLog";
    /**
     * 播放器同步
     */
    private static final Object mIjkLock = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    /** 缓冲超时 */
    private final long mBufferTimeout = 5000;
    /** 打开超时 */
    private final long mOpenTimeOut = 15000;
    /** 播放时长定时任务 */
    private final long mPlayDurTime = 420000;
    private LiveBll mLiveBll;
    /**
     * 直播缓存打开统计
     */
    private VPlayerListener mPlayStatistics;
    /**
     * 初始进入播放器时的预加载界面
     */
    private RelativeLayout rlFirstBackgroundView;
    /**
     * 老师不在直播间
     */
    private ImageView ivTeacherNotpresent;
    /**
     * 缓冲提示
     */
    private TextView tvLoadingHint;
    private LiveGetInfo mGetInfo;
    /** 直播服务器 */
    private PlayServerEntity mServer;
    private ArrayList<PlayserverEntity> failPlayserverEntity = new ArrayList<>();
    private ArrayList<PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
    /**
     * 直播服务器选择
     */
    private PlayserverEntity lastPlayserverEntity;
    private int lastIndex;
    private LiveTopic mLiveTopic;
    private String mVSectionID;
    /**
     * Activity暂停过，执行onStop
     */
    private boolean mHaveStop = false;
    /**
     * Activity在onResume
     */
    private boolean mIsResume = false;
    private LogToFile mLogtf;
    /**
     * 一些用户错误
     */
    public static final int LIVE_USER_ERROR = 102;

    public static final String ENTER_ROOM_FROM = "from";
    RelativeLayout questionContent;
    /**
     * 直播类型
     */
    private int liveType;
    /**
     * 连接老师加载-主讲
     */
    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
    /**
     * 连接老师加载-辅导
     */
    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
    /**
     * 正在播放
     */
    private boolean isPlay = false;
    /**
     * video缓存时间
     */
    private long videoCachedDuration;
    LiveMessageBll liveMessageBll;
    QuestionBll questionBll;
    RollCallBll rollCallBll;
    RedPackageBll redPackageBll;
    PraiseOrEncourageBll praiseOrEncourageBll;
    LearnReportBll learnReportBll;
    H5CoursewareBll h5CoursewareBll;
//    StarInteractBll starBll;
    /**
     * 视频宽度
     */
    public static final float VIDEO_WIDTH = 1280f;
    /**
     * 视频高度
     */
    public static final float VIDEO_HEIGHT = 720f;
    /**
     * 视频宽高比
     */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
    long openStartTime;
    /** onPause状态不暂停视频 */
    boolean onPauseNotStopVideo = false;

    protected boolean onVideoCreate(Bundle savedInstanceState) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        liveType = getIntent().getIntExtra("type", 0);
//        LectureMediaControllerBottom liveMediaControllerBottom = new LectureMediaControllerBottom(this,
// mMediaController, this);
        LiveMediaControllerBottom liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController,
                this);
        liveMessageBll = new LiveMessageBll(this, liveType);
        liveMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
        questionBll = new QuestionBll(this);
        liveMessageBll.setQuestionBll(questionBll);
        rollCallBll = new RollCallBll(this);
        praiseOrEncourageBll = new PraiseOrEncourageBll(this);
        redPackageBll = new RedPackageBll(this);
        learnReportBll = new LearnReportBll(this);
        h5CoursewareBll = new H5CoursewareBll(this);
        questionBll.setShareDataManager(mShareDataManager);
        questionBll.setPraiseOrEncourageBll(praiseOrEncourageBll);
        AppBll.getInstance().registerAppEvent(this);
        boolean init = initData();
        if (!init) {
            onUserBackPressed();
            return false;
        }
        liveMessageBll.setLiveBll(mLiveBll);
        rollCallBll.setLiveBll(mLiveBll);
        redPackageBll.setLiveBll(mLiveBll);
        learnReportBll.setLiveBll(mLiveBll);
        questionBll.setLiveBll(mLiveBll);
        questionBll.setVSectionID(mVSectionID);
        redPackageBll.setVSectionID(mVSectionID);
        questionBll.setLiveType(liveType);
        questionBll.initData();
        initView();
        changeLandAndPort();
        return true;
    }

    @Override
    protected void showLongMediaController() {
        mMediaController.show();
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        questionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        questionContent.setVisibility(View.VISIBLE);
        //聊天
        liveMessageBll.initView(questionContent, mIsLand);
        //聊天
        //if (liveType != LiveBll.LIVE_TYPE_LECTURE) {
        //}
        //公开表扬,只有直播有
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
            praiseOrEncourageBll.initView(questionContent);
        }
        //点名
        rollCallBll.initView(questionContent);
        //互动题和懂了吗
        questionBll.initView(questionContent, mIsLand);
        //红包
        redPackageBll.initView(questionContent);
        //学习报告
        learnReportBll.initView(questionContent);
        h5CoursewareBll.initView(questionContent);
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        //先让播放器按照默认模式设置
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
                (int) VIDEO_HEIGHT, VIDEO_RATIO);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        liveMessageBll.setVideoLayout(lp.width, lp.height);
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
                setFirstParamLand(lp);
                liveMessageBll.setVideoLayout(lp.width, lp.height);
                setMediaControllerBottomParam(lp);
//                if (starBll != null) {
//                    starBll.setLayoutParams(lp);
//                }
            }
        });
    }

    protected boolean initData() {
        Intent intent = getIntent();
        mVSectionID = intent.getStringExtra("vSectionID");
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(this, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        int from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        if (liveType == LiveBll.LIVE_TYPE_LIVE || liveType == LiveBll.LIVE_TYPE_LECTURE) {// 直播
            mLiveBll = new LiveBll(this, mVSectionID, liveType, from);
        } else if (liveType == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            mLiveBll = new LiveBll(this, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
        } else {
            Toast.makeText(this, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        LogToFile.liveBll = mLiveBll;
        mPlayStatistics = mLiveBll.getVideoListener();
        mLiveBll.setQuestionAction(questionBll);
        mLiveBll.setRollCallAction(rollCallBll);
        mLiveBll.setPraiseOrEncourageAction(praiseOrEncourageBll);
        mLiveBll.setReadPackageBll(redPackageBll);
        mLiveBll.setLearnReportAction(learnReportBll);
        mLiveBll.setVideoAction(this);
        mLiveBll.setRoomAction(liveMessageBll);
        mLiveBll.setH5CoursewareAction(h5CoursewareBll);
        mLiveBll.getInfo();
        mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom());
        setMediaControllerBottomParam(videoView.getLayoutParams());
        return true;
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    private void setMediaControllerBottomParam(ViewGroup.LayoutParams lp) {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMessageBll.getLiveMediaControllerBottom();
        int topGap = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        //Loger.e(TAG, "setMediaControllerBottomParam:paddingBottom=" + paddingBottom + "," +
        // liveMediaControllerBottom.getPaddingBottom());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeLandAndPort();
//        if (starBll != null) {
//            starBll.initView(questionContent);
//            starBll.onConfigurationChanged(mIsLand);
//        }
    }

    /**
     * 切换试题区位置
     */
    private void changeLandAndPort() {
        ViewGroup group = (ViewGroup) questionContent.getParent();
        if (mIsLand) {
            if (group != rlContent) {
                //设置控制
                ViewGroup controllerContent = (ViewGroup) findViewById(R.id.rl_course_video_live_controller_content);
                controllerContent.removeAllViews();
                mMediaController = new LiveMediaController(LectureLiveVideoActivity.this, LectureLiveVideoActivity
                        .this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                controllerContent.addView(mMediaController, params);
                mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom());
                mMediaController.setAutoOrientation(true);
                liveMessageBll.getLiveMediaControllerBottom().setController(mMediaController);
                if (mGetInfo != null) {
                    mMediaController.setFileName(mGetInfo.getName());
                }
                setMediaControllerBottomParam(videoView.getLayoutParams());
                // 换位置
                group.removeView(questionContent);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlContent.addView(questionContent, lp);
                questionContent.removeAllViews();
                liveMessageBll.initView(questionContent, mIsLand);
                //公开表扬,只有直播有
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
                    praiseOrEncourageBll.initView(questionContent);
                }
                //点名
                rollCallBll.initView(questionContent);
                //互动题和懂了吗
                questionBll.initView(questionContent, mIsLand);
                //红包
                redPackageBll.initView(questionContent);
                //学习报告
                learnReportBll.initView(questionContent);
                h5CoursewareBll.initView(questionContent);
            }
            group.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                    setFirstParamLand(lp);
                }
            });
        } else {
            ViewGroup content = (ViewGroup) findViewById(R.id.rl_course_video_contentview);
            if (group != content) {
                //设置控制
                ViewGroup controllerContent = (ViewGroup) findViewById(R.id.rl_course_video_live_controller_content);
                controllerContent.removeAllViews();
                mMediaController = new LiveMediaController(LectureLiveVideoActivity.this, LectureLiveVideoActivity
                        .this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                controllerContent.addView(mMediaController, params);
                mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom());
                mMediaController.setAutoOrientation(true);
                liveMessageBll.getLiveMediaControllerBottom().setController(mMediaController);
                if (mGetInfo != null) {
                    mMediaController.setFileName(mGetInfo.getName());
                }
                setMediaControllerBottomParam(videoView.getLayoutParams());
                // 换位置
                group.removeView(questionContent);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
                content.addView(questionContent, lp);
                questionContent.removeAllViews();
                liveMessageBll.initView(questionContent, mIsLand);
                //公开表扬,只有直播有
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
                    praiseOrEncourageBll.initView(questionContent);
                }
                //点名
                rollCallBll.initView(questionContent);
                //互动题和懂了吗
                questionBll.initView(questionContent, mIsLand);
                //红包
                redPackageBll.initView(questionContent);
                //学习报告
                learnReportBll.initView(questionContent);
                h5CoursewareBll.initView(questionContent);
            }
            group.post(new Runnable() {
                @Override
                public void run() {
                    setFirstParamPort();
                }
            });
        }
    }

    /**
     * 设置蓝屏界面
     */
    private void setFirstParamLand(ViewGroup.LayoutParams lp) {
        final View contentView = findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            ivTeacherNotpresent.setBackgroundResource(R.drawable.bg_course_video_teacher_notpresent_land2);
        }
        //Loger.e(TAG, "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
    }

    private void setFirstParamPort() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.rightMargin != RelativeLayout.LayoutParams.MATCH_PARENT || params.bottomMargin != RelativeLayout.LayoutParams.MATCH_PARENT) {
            params.rightMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.bottomMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            ivTeacherNotpresent.setBackgroundResource(R.drawable.bg_course_video_teacher_notpresent_port);
        }
        //Loger.e(TAG, "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
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
        rollCallBll.onPlayOpenSuccess(videoView.getLayoutParams());
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResume = true;
        if (mHaveStop) {
            mHaveStop = false;
            if (!onPauseNotStopVideo) {
                setFirstBackgroundVisible(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    rePlay();
                                }
                            });
                        }
                    }
                }.start();
            }
            onPauseNotStopVideo = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsResume = false;
        mHaveStop = true;
        if (!onPauseNotStopVideo) {
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            mHandler.removeCallbacks(mPlayDuration);
                            vPlayer.releaseSurface();
                            vPlayer.stop();
                        }
                        isPlay = false;
                    }
                }
            }.start();
        }
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
        liveMessageBll.onTitleShow(show);
    }

    protected VPlayerListener getWrapListener() {
        return mPlayListener;
    }

    private VPlayerListener mPlayListener = new SimpleVPlayerListener() {

        @Override
        public void onPlaybackComplete() {
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            mPlayStatistics.onPlaybackComplete();
            mLogtf.d("onPlaybackComplete");
        }

        @Override
        public void onPlayError() {
            isPlay = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            mPlayStatistics.onPlayError();
        }

        @Override
        public void onOpenSuccess() {
            isPlay = true;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mPlayStatistics.onOpenSuccess();
            mHandler.removeCallbacks(mPlayDuration);
            mHandler.postDelayed(mPlayDuration, mPlayDurTime);
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
            if (lastPlayserverEntity != null) {
                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "fail reconnect");
                reportPlayStarTime = System.currentTimeMillis();
            }
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


    /**
     * 得到Video缓存时间
     */
    private Runnable getVideoCachedDurationRun = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isPlay && !isFinishing()) {
                new Thread() {
                    @Override
                    public void run() {
                        videoCachedDuration = vPlayer.getVideoCachedDuration();
                        questionBll.setVideoCachedDuration(videoCachedDuration);
                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
                        mLiveBll.getOnloadLogs("videoCachedDuration=" + videoCachedDuration);
                        if (videoCachedDuration > 10000) {
                            mLiveBll.streamReport(LiveBll.MegId.MEGID_12130, mGetInfo.getChannelname(), -1);
                            if (lastPlayserverEntity != null) {
                                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "play delay reconnect");
                                reportPlayStarTime = System.currentTimeMillis();
                            }
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
            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
            long openTime = System.currentTimeMillis() - openStartTime;
            if (openTime > 40000) {
                mLiveBll.streamReport(LiveBll.MegId.MEGID_12107, mGetInfo.getChannelname(), openTime);
            } else {
                mLiveBll.streamReport(LiveBll.MegId.MEGID_12137, mGetInfo.getChannelname(), openTime);
            }
            if (lastPlayserverEntity != null) {
                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "buffer empty reconnect");
                reportPlayStarTime = System.currentTimeMillis();
            }
            mLiveBll.repair(true);
            mLiveBll.liveGetPlayServer();
        }
    };

    /** 播放时长，7分钟统计 */
    private Runnable mPlayDuration = new Runnable() {
        @Override
        public void run() {
            if (lastPlayserverEntity != null) {
                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "normal");
                reportPlayStarTime = System.currentTimeMillis();
            }
            if (isPlay && !isFinishing()) {
                mHandler.postDelayed(this, mPlayDurTime);
            }
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
            mLiveBll.liveGetPlayServer();
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
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                if (isLandSpace()) {
                    ivTeacherNotpresent.setBackgroundResource(R.drawable.bg_course_video_teacher_notpresent_land2);
                } else {
                    ivTeacherNotpresent.setBackgroundResource(R.drawable.bg_course_video_teacher_notpresent_port);
                }
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
        if (getInfo.isCloseChat()) {
            liveMessageBll.closeChat(true);
        }
        liveMessageBll.setLiveGetInfo(getInfo);
        rollCallBll.onLiveInit(getInfo);
        praiseOrEncourageBll.onLiveInit(getInfo);
        questionBll.setUserName(getInfo);
//        if (1 == getInfo.getIsArts()) {
//            starBll = new StarInteractBll(this, liveType, getInfo.getStarCount(), mIsLand);
//            starBll.onConfigurationChanged(mIsLand);
//            starBll.setLiveBll(mLiveBll);
//            starBll.initView(questionContent);
//            mLiveBll.setStarAction(starBll);
//        }
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData) {
        mServer = server;
        final AtomicBoolean change = new AtomicBoolean(false);// 直播状态是不是变化
        if (mLiveTopic != null) {
            change.set(!mLiveTopic.getMode().equals(cacheData.getMode()));
        }
        mLogtf.d("onLiveStart:change=" + change.get());
        mLiveTopic = cacheData;
        questionBll.setLiveTopic(cacheData);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic()
                            .getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
        rePlay();
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        mLogtf.i("onModeChange:mode=" + mode);
        try {
            liveMessageBll.onModeChange(mode, isPresent);
            rollCallBll.onModeChange(mode, isPresent);
        } catch (Exception e) {
            mLogtf.e("onModeChange:mode=" + mode, e);
        }
        mLogtf.i("onModeChange:mode=" + mode);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mLogtf.d("onModeChange:isInitialized=" + isInitialized());
                if (isInitialized()) {
                    mHandler.removeCallbacks(mPlayDuration);
                    vPlayer.releaseSurface();
                    vPlayer.stop();
                }
                isPlay = false;
                setFirstBackgroundVisible(View.VISIBLE);
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
                setResult(LIVE_USER_ERROR, intent);
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

    /**
     * 第一次播放，或者播放失败，重新播放
     */
    private void rePlay() {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
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
                                if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
                                        .getLiveTopic().getMode())) {
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
                                            entity = flvPlayservers.get((i + 1) % flvPlayservers.size());
                                            entity.setUseFlv(true);
                                            useFlv = true;
                                            msg += ",setUseFlv2";
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
                            entity = playservers.get((i + 1) % playservers.size());
                            msg += ",entity=null2";
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
                        if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic
                                ().getMode())) {
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
        mLiveBll.liveGetPlayServer();
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

    /**
     * 是否显示移动网络提示
     */
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
        liveMessageBll.onGetMyGoldDataEvent(event.goldNum);
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
        if (questionBll.onBack()) {

        } else {
            if (liveMessageBll.onBack()) {

            } else if (h5CoursewareBll.onBack()) {

            } else {
                super.onUserBackPressed();
            }
        }
    }

    @Override
    public void onDestroy() {
        isPlay = false;
        if (mLogtf != null) {
            mLogtf.d("onDestroy");
        }
        liveMessageBll.onDestroy();
        new Thread() {
            @Override
            public void run() {
                if (mLiveBll != null) {
                    mLiveBll.onDestroy();
                }
            }
        }.start();
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, LectureLiveVideoActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }
}