package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.VPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoteBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RankBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.UserOnline;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity2 extends LiveFragmentBase implements VideoAction, BaseLiveMessagePager.OnMsgUrlClick {
    private String TAG = "LiveVideoActivity2Log";
    Logger logger = LoggerFactory.getLogger(TAG);

    public LiveVideoActivity2() {
        mLayoutVideo = R.layout.activity_video_live_new;
    }

    /** 播放器同步 */
    private static final Object mIjkLock = new Object();
    protected WeakHandler mHandler = new WeakHandler(null);
    /** 上次播放统计开始时间 */
    long lastPlayTime;
    /** 是否播放成功 */
    boolean openSuccess = false;
    /** 播放时长定时任务 */
    private final long mPlayDurTime = 420000;
    RelativeLayout bottomContent;
    protected LiveGetInfo mGetInfo;
    protected String vStuCourseID;
    protected String courseId;
    protected String mVSectionID;
    /** Activity暂停过，执行onStop */
    private boolean mHaveStop = false;

    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public boolean IS_SCIENCE = true;
    public static final String ENTER_ROOM_FROM = "from";
    /** 直播类型 */
    private int liveType;
    /** 是不是文科 */
    private int isArts;
    /** 正在播放 */
    private boolean isPlay = false;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;
    boolean audioRequest = false;
    long openStartTime;
    int from = 0;
    long startTime = System.currentTimeMillis();
    /** onPause状态不暂停视频 */
    boolean onPauseNotStopVideo = false;
    protected LiveBll2 mLiveBll;
    private LiveIRCMessageBll liveIRCMessageBll;
    protected String mode = LiveTopic.MODE_TRANING;
    private LiveVideoBll mLiveVideoBll;
    private UserOnline userOnline;
    private static String Tag = "LiveVideoActivity2";
    protected LogToFile mLogtf;
    protected LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
    protected VideoChatIRCBll videoChatIRCBll;
    protected ArrayList<LiveMediaController.MediaPlayerControl> mediaPlayerControls = new ArrayList<>();
    protected LiveVideoAction liveVideoAction;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        logger.d("==========>onVideoCreate:");
        long before = System.currentTimeMillis();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        liveType = activity.getIntent().getIntExtra("type", 0);
        isArts = activity.getIntent().getIntExtra("isArts", -1);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        AppBll.getInstance().registerAppEvent(this);
        boolean init = initData();
        if (!init) {
            onUserBackPressed();
            return false;
        }
        mLogtf = new LogToFile(mLiveBll, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        //先让播放器按照默认模式设置
        videoView = mContentView.findViewById(R.id.vv_course_video_video);
        logger.d("onVideoCreate:videoView=" + (videoView == null));
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
        setFirstParam();
        logger.d("onVideoCreate:time1=" + (System.currentTimeMillis() - startTime) + "," + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        initAllBll();
        logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        addBusiness(activity, bottomContent);
        logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
        return true;
    }

    @Override
    protected void onVideoCreateEnd() {
        mLiveBll.onCreate();
        mLiveVideoBll.setvPlayer(vPlayer);
        addOnGlobalLayoutListener();
        startGetInfo();
    }

    protected void startGetInfo() {
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
        if (mGetInfo != null) {
            mode = mGetInfo.getMode();
        }
        mLiveBll.getInfo(mGetInfo);
    }

    protected void addOnGlobalLayoutListener() {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        boolean change = LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
                        setFirstParam();
                        setMediaControllerBottomParam(lp);
                        long before = System.currentTimeMillis();
                        if (change) {
                            List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
                            for (LiveBaseBll businessBll : businessBlls) {
                                businessBll.setVideoLayoutF(liveVideoPoint);
                            }
                        }
                        logger.d("onGlobalLayout:change=" + change + ",time=" + (System.currentTimeMillis() - before));
                    }
                });
            }
        }, 10);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return mContentView;
    }

    /**
     * 添加 直播间内 所需的功能模块
     *
     * @param activity
     * @param bottomContent
     */
    private void addBusiness(Activity activity, RelativeLayout bottomContent) {
        //是文科
        if (isArts == 1) {
            //理科功能
//            mLiveBll.addBusinessBll(new TeamPkBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll, bottomContent));
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll, bottomContent);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            mLiveBll.addBusinessBll(liveIRCMessageBll);
            mLiveBll.addBusinessBll(new LiveAchievementIRCBll(activity, mLiveBll, bottomContent));
            RankBll rankBll = new RankBll(activity, mLiveBll, bottomContent);
            rankBll.setLiveMediaController(mMediaController, liveMediaControllerBottom);
            mLiveBll.addBusinessBll(rankBll);
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll, bottomContent));
            //理科功能
//            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll, bottomContent));
//            mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll, bottomContent));
//            mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll, bottomContent));
//            mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll, bottomContent));
        } else {
            mLiveBll.addBusinessBll(new TeamPkBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll, bottomContent));
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll, bottomContent);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            mLiveBll.addBusinessBll(liveIRCMessageBll);
            //文科功能
//            mLiveBll.addBusinessBll(new LiveAchievementIRCBll(activity, mLiveBll, bottomContent));
            RankBll rankBll = new RankBll(activity, mLiveBll, bottomContent);
            rankBll.setLiveMediaController(mMediaController, liveMediaControllerBottom);
            mLiveBll.addBusinessBll(rankBll);
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll, bottomContent));
            mLiveBll.addBusinessBll(new SpeechFeedBackIRCBll(activity, mLiveBll, bottomContent));
            if (liveMediaControllerBottom instanceof LiveMediaControllerBottom) {
                LiveRemarkIRCBll liveRemarkIRCBll = new LiveRemarkIRCBll(activity, mLiveBll, bottomContent);
                liveRemarkIRCBll.setvPlayer(vPlayer);
                liveRemarkIRCBll.setVideoView(videoView);
                LiveMediaControllerBottom controllerBottom = (LiveMediaControllerBottom) liveMediaControllerBottom;
                liveRemarkIRCBll.setLiveMediaControllerBottom(controllerBottom);
                mLiveBll.addBusinessBll(liveRemarkIRCBll);
            }
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll, bottomContent));
        }
        videoChatIRCBll = new VideoChatIRCBll(activity, mLiveBll, bottomContent);
        videoChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
        videoChatIRCBll.setLiveFragmentBase(this);
        mLiveVideoBll.setVideoChatEvent(videoChatIRCBll);
        mLiveBll.addBusinessBll(videoChatIRCBll);
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
        createMediaControllerBottom();
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
    }

    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

    protected boolean initData() {
        Intent intent = activity.getIntent();
        courseId = intent.getStringExtra("courseId");
        vStuCourseID = intent.getStringExtra("vStuCourseID");
        mVSectionID = intent.getStringExtra("vSectionID");
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
            LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
            if (mGetInfo != null) {
                mode = mGetInfo.getMode();
            }
            mLiveBll = new LiveBll2(activity, vStuCourseID, courseId, mVSectionID, from, mGetInfo);
        } else if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            mLiveBll = new LiveBll2(activity, mVSectionID, liveType, from);
        } else if (liveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            mLiveBll = new LiveBll2(activity, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
        } else {
            Toast.makeText(activity, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    private void initAllBll() {
        mLiveBll.setmIsLand(mIsLand);
        createLiveVideoAction();
        ProxUtil.getProxUtil().put(activity, RegMediaPlayerControl.class, new RegMediaPlayerControl() {

            @Override
            public void addMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl) {
                mediaPlayerControls.add(mediaPlayerControl);
            }

            @Override
            public void removeMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl) {
                mediaPlayerControls.remove(mediaPlayerControl);
            }
        });
        LiveVideoBll liveVideoBll = new LiveVideoBll(activity, mLiveBll, liveType);
        mLiveBll.setVideoAction(this);
        mLiveBll.setLiveVideoBll(liveVideoBll);
        userOnline = new UserOnline(activity, liveType, mVSectionID);
        userOnline.setHttpManager(mLiveBll.getHttpManager());
        android.util.Log.e("LiveVideoActivity", "====>initAllBll:" + bottomContent);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam(videoView.getLayoutParams());
        videoFragment.setIsAutoOrientation(false);
        liveVideoBll.setHttpManager(mLiveBll.getHttpManager());
        liveVideoBll.setHttpResponseParser(mLiveBll.getHttpResponseParser());
        liveVideoBll.setVideoFragment(videoFragment);
        liveVideoBll.setVideoAction(this);
        mLiveVideoBll = liveVideoBll;
    }

    protected void createLiveVideoAction() {
        liveVideoAction = new LiveVideoAction(activity, mLiveBll, mContentView);
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam(ViewGroup.LayoutParams lp) {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = liveVideoPoint.y2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    /**
     * 设置蓝屏界面
     */
    protected void setFirstParam() {
        if (liveVideoAction != null) {
            liveVideoAction.setFirstParam(liveVideoPoint);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLand.get()) {
            mMediaController.setControllerBottom(liveMediaControllerBottom, false);
            setMediaControllerBottomParam(videoView.getLayoutParams());
        }
    }

    @Override
    protected VideoFragment getFragment() {
        return new LiveVideoPlayFragment();
    }

    protected class LiveVideoPlayFragment extends VideoFragment {

        @Override
        protected void onPlayOpenStart() {
            setFirstBackgroundVisible(View.VISIBLE);
            mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
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
        protected void onPlayOpenSuccess() {
            TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
            if (tvFail != null) {
                tvFail.setVisibility(View.INVISIBLE);
            }
            setFirstBackgroundVisible(View.GONE);
//            if (mGetInfo != null && mGetInfo.getIsShowMarkPoint().equals("1")) {
//                if (liveRemarkBll == null) {
//                    liveRemarkBll = new LiveRemarkBll(activity, vPlayer);
//                    if (videoChatBll != null) {
//                        videoChatBll.setLiveRemarkBll(liveRemarkBll);
//                    }
//                    if (mLiveBll != null && liveMediaControllerBottom != null) {
//                        if (liveTextureView == null) {
//                            ViewStub viewStub = (ViewStub) mContentView.findViewById(R.id.vs_course_video_video_texture);
//                            liveTextureView = (LiveTextureView) viewStub.inflate();
//                            liveTextureView.vPlayer = vPlayer;
//                            liveTextureView.setLayoutParams(videoView.getLayoutParams());
//                        }
//                        liveRemarkBll.showBtMark();
//                        liveRemarkBll.setTextureView(liveTextureView);
//                        liveRemarkBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//                        liveRemarkBll.setVideoView(videoView);
//                        mLiveBll.setLiveRemarkBll(liveRemarkBll);
//                        liveRemarkBll.setLiveAndBackDebug(mLiveBll);
//                    }
//                } else {
//                    liveRemarkBll.initData();
//                }
//            }
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
            liveVideoAction.onPlayError();
        }

        @Override
        public void onTitleShow(boolean show) {
            for (LiveMediaController.MediaPlayerControl control : mediaPlayerControls) {
                control.onTitleShow(show);
            }
        }

        @Override
        protected VPlayerListener getWrapListener() {
            return mLiveVideoBll.getPlayListener();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveStop) {
            mHaveStop = false;
            if (videoChatIRCBll.isChat()) {
                return;
            }
            if (!onPauseNotStopVideo) {
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
                    }
                }.start();
            }
            onPauseNotStopVideo = false;
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
        if (videoChatIRCBll.isChat()) {
            return;
        }
        if (!onPauseNotStopVideo) {
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            if (openSuccess) {
                                mLiveVideoBll.stopPlayDuration();
                                Loger.d(TAG, "onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                            }
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
    public void onStop() {
        super.onStop();
        if (mLiveBll != null) {
            mLiveBll.onStop();
        }
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
    public void onTeacherNotPresent(final boolean isBefore) {
        liveVideoAction.onTeacherNotPresent(isBefore);
    }

    @Override
    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用

    }

    int count = 0;

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        userOnline.setGetInfo(getInfo);
        userOnline.start();
        mGetInfo = getInfo;
        mode = mGetInfo.getMode();
        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        liveVideoAction.onLiveInit(getInfo);
        Loger.d(TAG, "onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        liveVideoAction.onLiveStart(server, cacheData, modechange);
        mLiveVideoBll.onLiveStart(server, cacheData, modechange);
        AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        rePlay(change.get());
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        LiveVideoActivity2.this.mode = mGetInfo.getMode();
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (isInitialized()) {
                    mLiveVideoBll.stopPlayDuration();
                    vPlayer.releaseSurface();
                    vPlayer.stop();
                }
                isPlay = false;
                liveVideoAction.onModeChange(mode, isPresent);
            }
        });

    }

    @Override
    public void onClassTimoOut() {
        liveVideoAction.onClassTimoOut();
    }

    @Override
    public void onLiveDontAllow(final String msg) {
        liveVideoAction.onLiveDontAllow(msg);
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        liveVideoAction.onLiveError(responseEntity);
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     *
     * @param modechange
     */
    @Override
    public void rePlay(boolean modechange) {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        if (videoChatIRCBll.isChat()) {
            return;
        }
        liveVideoAction.rePlay(modechange);
        mLiveVideoBll.rePlay(modechange);
    }

    /**
     * 播放失败，或者完成时调用
     */
    private void onFail(int arg1, final int arg2) {
        liveVideoAction.onFail(arg1, arg2);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void setFirstBackgroundVisible(int visible) {
        liveVideoAction.setFirstBackgroundVisible(visible);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        Loger.i(TAG, "onEvent:netWorkType=" + event.netWorkType);
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
        Toast.makeText(activity, "没有wifi", Toast.LENGTH_SHORT).show();
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
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
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

    @Override
    protected void onUserBackPressed() {
        super.onUserBackPressed();
    }

    @Override
    public void onDestroy() {
        isPlay = false;
        if (mLiveBll != null) {
            mLiveBll.onDestory();
        }
        ProxUtil.getProxUtil().clear();
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        userOnline.stop();
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }


    public void updateIcon() {
        if (liveVideoAction != null) {
            liveVideoAction.updateLoadingImage();
        }
        updateRefreshImage();
    }

}
