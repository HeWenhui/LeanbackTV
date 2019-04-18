//package com.xueersi.parentsmeeting.modules.livevideo.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewStub;
//import android.view.ViewTreeObserver;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.tal.speech.speechrecognizer.Constants;
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.entity.FooterIconEntity;
//import com.xueersi.common.event.AppEvent;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.logerhelper.MobEnumUtil;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
//import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
//import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
//import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
//import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CacheAction;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
//import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.EnglishSpeekBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.ExpeBll;
//import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.H5CoursewareBll;
//import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportBll;
//import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveLazyBllCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
//import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.LiveVoiceAnswerCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.RankBll;
//import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
//import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackAction;
//import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
//import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
//import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
//import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionWebCache;
//import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
//import com.xueersi.common.business.UserBll;
//import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.SimpleVPlayerListener;
//import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.VPlayerListener;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.common.speech.SpeechEvaluatorUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
//
///**
// * 直播
// *
// * @author linyuqiang
// */
//public class LiveVideoActivity extends LiveActivityBase implements VideoAction, ActivityStatic, BaseLiveMessagePager
//        .OnMsgUrlClick, BaseLiveMediaControllerBottom.MediaChildViewClick,
//        AudioRequest, WebViewRequest, VideoChatEvent {
//
//    private String TAG = "LiveVideoActivityLog";
//    /** 播放器同步 */
//    private static final Object mIjkLock = new Object();
//    private WeakHandler mHandler = new WeakHandler(null);
//    /** 缓冲超时 */
//    private final long mBufferTimeout = 5000;
//    /** 打开超时 */
//    private final long mOpenTimeOut = 15000;
//    /** 播放时长 */
//    long playTime = 0;
//    /** 上次播放统计开始时间 */
//    long lastPlayTime;
//    /** 是否播放成功 */
//    boolean openSuccess = false;
//    /** 播放时长定时任务 */
//    private final long mPlayDurTime = 420000;
//    private LiveBll mLiveBll;
//    /** 直播缓存打开统计 */
//    private VPlayerListener mPlayStatistics;
//    /** 初始进入播放器时的预加载界面 */
//    private RelativeLayout rlFirstBackgroundView;
//    /** 老师不在直播间 */
//    private ImageView ivTeacherNotpresent;
//    RelativeLayout bottomContent;
//    RelativeLayout praiselistContent;
//    /** 缓冲提示 */
//    private ImageView ivLoading;
//    private TextView tvLoadingHint;
//    private LiveGetInfo mGetInfo;
//    /** 直播服务器 */
//    private PlayServerEntity mServer;
//    private ArrayList<PlayserverEntity> failPlayserverEntity = new ArrayList<>();
//    private ArrayList<PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
//    /** 直播服务器选择 */
//    private PlayserverEntity lastPlayserverEntity;
//    private int lastIndex;
//    private LiveTopic mLiveTopic;
//    private String vStuCourseID;
//    private String courseId;
//    private String mVSectionID;
//    /** Activity暂停过，执行onStop */
//    private boolean mHaveStop = false;
//    /** Activity在onResume */
//    private boolean mIsResume = false;
//    private LogToFile mLogtf;
//    private LiveVideoSAConfig liveVideoSAConfig;
//    /** 是不是文理 */
//    public boolean IS_SCIENCE = true;
//    public static final String ENTER_ROOM_FROM = "from";
//    /** 直播类型 */
//    private int liveType;
//    /** 连接老师加载-主讲 */
//    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
//    /** 连接老师加载-辅导 */
//    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
//    /** 正在播放 */
//    private boolean isPlay = false;
//    /** video缓存时间 */
//    private long videoCachedDuration;
//    LiveLazyBllCreat liveLazyBllCreat;
//    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
//    LiveMediaControllerBottom liveMediaControllerBottom;
//    ExpeBll expeBll;
//    LiveMessageBll liveMessageBll;
//    VideoChatBll videoChatBll;
//    QuestionBll questionBll;
//    RollCallBll rollCallBll;
//    //RedPackageBll redPackageBll;
//    LearnReportBll learnReportBll;
//    H5CoursewareBll h5CoursewareBll;
//    EnglishH5CoursewareBll englishH5CoursewareBll;
//    LiveAchievementBll starBll;
//    EnglishSpeekBll englishSpeekBll;
//    SpeechFeedBackAction speechFeedBackAction;
//    boolean audioRequest = false;
//    SpeechEvaluatorUtils mIse;
//    RankBll rankBll;
//    EnglishH5CacheAction englishH5Cache;
//    private LiveRemarkBll liveRemarkBll;
//    /** 视频宽度 */
//    public static final float VIDEO_WIDTH = 1280f;
//    /** 视频高度 */
//    public static final float VIDEO_HEIGHT = 720f;
//    /** 视频宽高比 */
//    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
//    /** 头像宽度 */
//    public static final float VIDEO_HEAD_WIDTH = 320f;
//    /** 头像高度 */
//    public static final float VIDEO_HEAD_HEIGHT = 240f;
//    long openStartTime;
//    /** 接麦已经连接老师 */
//    private AtomicBoolean startRemote = new AtomicBoolean(false);
//    int from = 0;
//    long startTime = System.currentTimeMillis();
//    /** onPause状态不暂停视频 */
//    boolean onPauseNotStopVideo = false;
//    LiveTextureView liveTextureView;
//    private TeacherPraiseBll teacherPraiseBll;
//
//    @Override
//    protected boolean onVideoCreate(Bundle savedInstanceState) {
//        long before = System.currentTimeMillis();
//        mLogtf = new LogToFile(TAG);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        liveType = getIntent().getIntExtra("type", 0);
//        // 设置不可自动横竖屏
//        setAutoOrientation(false);
//        AppBll.getInstance().registerAppEvent(this);
//        boolean init = initData();
//        if (!init) {
//            onUserBackPressed();
//            return false;
//        }
//        mLogtf.setLiveOnLineLogs(mLiveBll);
//        logger.d( "onVideoCreate:time1=" + (System.currentTimeMillis() - startTime) + "," + (System
//                .currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
//        LiveGetInfo mGetInfo = LiveVideoEnter.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
//        initAllBll();
//        logger.d( "onVideoCreate:time2=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        initView();
//        logger.d( "onVideoCreate:time3=" + (System.currentTimeMillis() - before));
////        SpeechAssessmentWebPager pager=new SpeechAssessmentWebPager(mContext,"","","",true,"",null);
////        ((RelativeLayout)findViewById(R.id.rl_speech_test)).addView(pager.getRootView());
//        return true;
//    }
//
//    @Override
//    protected void onVideoCreateEnd() {
//        mLiveBll.setLivePlayLog(livePlayLog);
//        mLiveBll.getInfo(mGetInfo);
//    }
//
//    @Override
//    protected void showRefresyLayout(int arg1, int arg2) {
//        super.showRefresyLayout(arg1, arg2);
//    }
//
//    @Override
//    public void showLongMediaController() {
//        super.showLongMediaController();
//    }
//
//    private void initView() {
//        // 预加载布局
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
//        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
//        bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
//        bottomContent.setVisibility(View.VISIBLE);
//        praiselistContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_praiselist_content);
//        praiselistContent.setVisibility(View.VISIBLE);
//        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
//        updateLoadingImage();
//        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
//        // 预加载布局中退出事件
//        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
//        tvLoadingHint.setText("获取课程信息");
//        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
//                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        bottomContent.addView(liveMediaControllerBottom);
//        //聊天
//        long before = System.currentTimeMillis();
//        liveLazyBllCreat.setBottomContent(bottomContent);
//        liveLazyBllCreat.setPraiselistContent(praiselistContent);
//        liveMessageBll.initViewLive(bottomContent);
//        logger.d( "initView:time1=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//
//        //先让播放器按照默认模式设置
//        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
//                (int) VIDEO_HEIGHT, VIDEO_RATIO);
//        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//
//        //公开表扬,只有直播有
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//            rankBll.initView(bottomContent);
//        }
//        videoChatBll.initView(bottomContent);
//        //点名
//        rollCallBll.initView(bottomContent);
//        //互动题和懂了吗
//        questionBll.initView(bottomContent, true);
//        //红包
//        // redPackageBll.initView(bottomContent);
//        //学习报告
//        learnReportBll.initView(bottomContent);
//        h5CoursewareBll.initView(bottomContent);
//        englishH5CoursewareBll.initView(bottomContent);
//
//        setFirstParam(lp);
//        liveMessageBll.setVideoLayout(lp.width, lp.height);
//        logger.d( "initView:time2=" + (System.currentTimeMillis() - before));
//        final View contentView = findViewById(android.R.id.content);
//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
//                        .OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        if (videoView.getWidth() <= 0) {
//                            return;
//                        }
//                        boolean isLand = getResources().getConfiguration().orientation == Configuration
//                                .ORIENTATION_LANDSCAPE;
//                        //logger.i( "setVideoWidthAndHeight:isLand=" + isLand);
//                        if (!isLand) {
//                            return;
//                        }
//                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
//                                (int) VIDEO_HEIGHT, VIDEO_RATIO);
//                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//                        setFirstParam(lp);
//                        liveMessageBll.setVideoLayout(lp.width, lp.height);
//                        questionBll.setVideoLayout(lp.width, lp.height);
//                        if (rankBll != null) {
//                            rankBll.setVideoLayout();
//                        }
////                        if (expeBll != null) {
////                            expeBll.setVideoLayout(lp.width, lp.height);
////                        }
//                        setMediaControllerBottomParam(lp);
//                        if (englishSpeekBll != null) {
//                            englishSpeekBll.setVideoWidthAndHeight(lp.width, lp.height);
//                        }
//                        if (englishH5CoursewareBll != null) {
//                            englishH5CoursewareBll.setVideoLayout(lp.width, lp.height);
//                        }
//                        if (mLiveBll != null && mLiveBll.getAnswerRankBll() != null) {
//                            mLiveBll.getAnswerRankBll().setVideoLayout(lp.width, lp.height);
//                        }
//                        if (mLiveBll != null && mLiveBll.getLiveAutoNoticeBll() != null) {
//                            mLiveBll.getLiveAutoNoticeBll().setLayout(lp.width, lp.height);
//                        }
//                        if (mLiveBll != null && mLiveBll.getLiveRemarkBll() != null) {
//                            mLiveBll.getLiveRemarkBll().setLayout(lp.width, lp.height);
//                        }
//                        if (speechFeedBackAction != null) {
//                            speechFeedBackAction.setVideoLayout(lp.width, lp.height);
//                        }
//                        if (mLiveBll != null && mLiveBll.getPraiseListAction() != null) {
//                            mLiveBll.getPraiseListAction().setVideoLayout(lp.width, lp.height);
//                        }
//                    }
//                });
//            }
//        }, 10);
//        /*answerRankBll = new AnswerRankBll(mContext, bottomContent,mLiveBll);
//        mLiveBll.setAnswerRankBll(answerRankBll);
//        questionBll.setAnswerRankBll(answerRankBll);
//        englishH5CoursewareBll.setAnswerRankBll(answerRankBll);
//        final ArrayList<FullMarkListEntity> lst=new ArrayList<>();
//        for(int i=0;i<16;i++){
//            FullMarkListEntity entity=new FullMarkListEntity();
//            entity.setAnswer_time(""+(60+i));
//            entity.setStuName("李亚龙啊");
//            entity.setId(UserBll.getInstance().getMyUserInfoEntity().getStuId()+(i==5?"":"abc"));
//            lst.add(entity);
//        }
//        final ArrayList<RankUserEntity> lst1=new ArrayList<>();
//        RankUserEntity entity=new RankUserEntity();
//        entity.setName("李亚龙啊");
//        entity.setId("abc");
//        lst1.add(entity);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                answerRankBll.showFullMarkList(lst,1);
//                answerRankBll.showRankList(lst1);
//            }
//        },3000);*/
//    }
//
//    protected boolean initData() {
//        Intent intent = getIntent();
//        courseId = intent.getStringExtra("courseId");
//        vStuCourseID = intent.getStringExtra("vStuCourseID");
//        mVSectionID = intent.getStringExtra("vSectionID");
//        mVideoType = MobEnumUtil.VIDEO_LIVE;
//        if (TextUtils.isEmpty(mVSectionID)) {
//            Toast.makeText(this, "直播场次不存在", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
//        XesMobAgent.enterLiveRoomFrom(from);
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
//            mLiveBll = new LiveBll(this, vStuCourseID, courseId, mVSectionID, from, null);
//        } else if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
//            mLiveBll = new LiveBll(this, mVSectionID, liveType, from);
//        } else if (liveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
//            mLiveBll = new LiveBll(this, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
//        } else {
//            Toast.makeText(this, "直播类型不支持", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private void initAllBll() {
//        liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController, this);
//        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
//        liveMessageBll = new LiveMessageBll(this, liveType);
//        liveMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//        questionBll = new QuestionBll(this, vStuCourseID);
//        liveMessageBll.setQuestionBll(questionBll);
//        videoChatBll = new VideoChatBll(this, this);
//        rollCallBll = new RollCallBll(this);
//        // redPackageBll = new RedPackageBll(this);
//        learnReportBll = new LearnReportBll(this);
//        h5CoursewareBll = new H5CoursewareBll(this);
//        englishH5CoursewareBll = new EnglishH5CoursewareBll(this);
//        questionBll.setShareDataManager(mShareDataManager);
//
//        LogToFile.liveBll = mLiveBll;
//        mPlayStatistics = mLiveBll.getVideoListener();
//        mLiveBll.setQuestionAction(questionBll);
//        mLiveBll.setRollCallAction(rollCallBll);
//        //  mLiveBll.setReadPackageBll(redPackageBll);
//        mLiveBll.setLearnReportAction(learnReportBll);
//        mLiveBll.setVideoAction(this);
//        mLiveBll.setRoomAction(liveMessageBll);
//        mLiveBll.setH5CoursewareAction(h5CoursewareBll);
//        mLiveBll.setEnglishH5CoursewareAction(englishH5CoursewareBll);
//        mLiveBll.setVideoChatAction(videoChatBll);
//        videoChatBll.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom());
//        mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), false);
//        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
//        mMediaController.setControllerTop(baseLiveMediaControllerTop);
//        setMediaControllerBottomParam(videoView.getLayoutParams());
//
//        liveMessageBll.setLiveBll(mLiveBll);
//        //rollCallBll.setLiveBll(mLiveBll);
//        //  redPackageBll.setLiveBll(mLiveBll);
//        learnReportBll.setLiveBll(mLiveBll);
//        questionBll.setLiveBll(mLiveBll);
//        questionBll.setVSectionID(mVSectionID);
//        //  redPackageBll.setVSectionID(mVSectionID);
//        questionBll.setLiveType(liveType);
//        questionBll.initData();
//        questionBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(questionBll.new LiveQuestionSwitchImpl(), null));
//        questionBll.setBaseSpeechCreat(new LiveSpeechCreat(null));
//        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
//        englishH5CoursewareBll.setLiveType(liveType);
//        englishH5CoursewareBll.setVSectionID(mVSectionID);
//        englishH5CoursewareBll.setLiveBll(mLiveBll);
//        englishH5CoursewareBll.initData();
//        englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(englishH5CoursewareBll.new
//                LiveQuestionSwitchImpl(), null));
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
////            rankBll = new RankBll(this);
////            rankBll.setLiveMediaController(mMediaController, liveMediaControllerBottom);
////            rankBll.setLiveBll(mLiveBll);
//            englishH5Cache = new EnglishH5Cache(this, mLiveBll, mVSectionID);
////            englishH5Cache = new EnglishH5CacheZip(this, mLiveBll, mVSectionID);
//        }
//        videoChatBll.setLiveBll(mLiveBll);
//
//        liveLazyBllCreat = new LiveLazyBllCreat(this, mLiveBll);
//        liveLazyBllCreat.setQuestionBll(questionBll);
//        mLiveBll.setLiveLazyBllCreat(liveLazyBllCreat);
//        ProxUtil.getProxUtil().put(this, AudioRequest.class, this);
//        // 初始战队pk
//        //teamPKBll = new TeamPkBll(this);
//        //setTeamPkBll(teamPKBll);
//        //老师点赞
//        // teacherPraiseBll = new TeacherPraiseBll(this);
//        // mLiveBll.setTeacherPriaseBll(teacherPraiseBll);
//    }
//
//    /**
//     * 控制栏下面距离视频底部的尺寸
//     */
//    public void setMediaControllerBottomParam(ViewGroup.LayoutParams lp) {
//        //控制栏下面距离视频底部的尺寸
//        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMessageBll.getLiveMediaControllerBottom();
//        int topGap = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
//        //logger.e( "setMediaControllerBottomParam:paddingBottom=" + paddingBottom + "," +
//        // liveMediaControllerBottom.getPaddingBottom());
//        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
//            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
//        }
//    }
//
//    /**
//     * 设置蓝屏界面
//     */
//    private void setFirstParam(ViewGroup.LayoutParams lp) {
//        final View contentView = findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
//        int rightMargin = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp
//                .width) / 2);
//        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
//            params.rightMargin = rightMargin;
//            params.bottomMargin = params.topMargin = topMargin;
//            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
////            rlFirstBackgroundView.setLayoutParams(params);
//            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
////            ivTeacherNotpresent.setLayoutParams(params);
//        }
//        //logger.e( "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," +
//        // rightMargin);
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (mIsLand) {
//            mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), false);
//            setMediaControllerBottomParam(videoView.getLayoutParams());
//        }
//    }
//
//    @Override
//    protected void onPlayOpenStart() {
//        setFirstBackgroundVisible(View.VISIBLE);
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected void onPlayOpenSuccess() {
//        TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
//        if (tvFail != null) {
//            tvFail.setVisibility(View.INVISIBLE);
//        }
//        setFirstBackgroundVisible(View.GONE);
//        rollCallBll.onPlayOpenSuccess(videoView.getLayoutParams());
//        if (mGetInfo != null && mGetInfo.getIsShowMarkPoint().equals("1")) {
//            if (liveRemarkBll == null) {
//                liveRemarkBll = new LiveRemarkBll(mContext, vPlayer);
//                if (videoChatBll != null) {
//                    videoChatBll.setLiveRemarkBll(liveRemarkBll);
//                }
//                if (mLiveBll != null && liveMediaControllerBottom != null) {
//                    if (liveTextureView == null) {
//                        ViewStub viewStub = (ViewStub) findViewById(R.id.vs_course_video_video_texture);
//                        liveTextureView = (LiveTextureView) viewStub.inflate();
//                        liveTextureView.vPlayer = vPlayer;
//                        liveTextureView.setLayoutParams(videoView.getLayoutParams());
//                    }
//                    liveRemarkBll.showBtMark();
//                    liveRemarkBll.setTextureView(liveTextureView);
//                    liveRemarkBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//                    mLiveBll.setLiveRemarkBll(liveRemarkBll);
//                    liveRemarkBll.setLiveAndBackDebug(mLiveBll);
//                }
//            } else {
//                liveRemarkBll.initData();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mIsResume = true;
//        if (mHaveStop) {
//            mHaveStop = false;
////            if (expeBll != null) {
////                boolean onResume = expeBll.onResume();
////                if (!onResume) {
////                    setFirstBackgroundVisible(View.VISIBLE);
////                    return;
////                }
////            }
//            if (startRemote.get()) {
//                return;
//            }
//            if (!onPauseNotStopVideo) {
//                setFirstBackgroundVisible(View.VISIBLE);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    rePlay(false);
//                                }
//                            });
//                        }
//                    }
//                }.start();
//            }
//            onPauseNotStopVideo = false;
//        }
//
//        if (mLiveBll != null) {
//            mLiveBll.onResume();
//        }
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mIsResume = false;
//        mHaveStop = true;
////        if (expeBll != null) {
////            expeBll.onPause();
////        }
//        if (questionBll != null) {
//            questionBll.onPause();
//        }
//        if (startRemote.get()) {
//            return;
//        }
//        if (!onPauseNotStopVideo) {
//            new Thread() {
//                @Override
//                public void run() {
//                    synchronized (mIjkLock) {
//                        if (isInitialized()) {
//                            if (openSuccess) {
//                                mHandler.removeCallbacks(mPlayDuration);
//                                playTime += (System.currentTimeMillis() - lastPlayTime);
//                                logger.d( "onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
//                            }
//                            vPlayer.releaseSurface();
//                            vPlayer.stop();
//                        }
//                        isPlay = false;
//                    }
//                }
//            }.start();
//        }
//        if (liveRemarkBll != null) {
//            liveRemarkBll.onPause();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mLiveBll != null) {
//            mLiveBll.onStop();
//        }
//    }
//
//    @Override
//    public boolean isResume() {
//        return mIsResume;
//    }
//
//    @Override
//    protected void resultFailed(final int arg1, final int arg2) {
//        postDelayedIfNotFinish(new Runnable() {
//
//            @Override
//            public void run() {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            onFail(arg1, arg2);
//                        }
//                    }
//                }.start();
//            }
//        }, 1200);
//    }
//
//    @Override
//    protected void playComplete() {
//        postDelayedIfNotFinish(new Runnable() {
//
//            @Override
//            public void run() {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            onFail(0, 0);
//                        }
//                    }
//                }.start();
//            }
//        }, 200);
//    }
//
//    @Override
//    protected void onPlayError() {
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                tvLoadingHint.setText("您的手机暂时不支持播放直播");
//                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//
//    @Override
//    public void onTitleShow(boolean show) {
//        liveMessageBll.onTitleShow(show);
//        if (rankBll != null) {
//            rankBll.onTitleShow(show);
//        }
//    }
//
//    @Override
//    protected VPlayerListener getWrapListener() {
//        return mPlayListener;
//    }
//
//    private VPlayerListener mPlayListener = new SimpleVPlayerListener() {
//
//        @Override
//        public void onPlaying(long currentPosition, long duration) {
//            if (startRemote.get()) {
//                mLogtf.d("onPlaying:startRemote");
//                stopPlay();
//            }
//        }
//
//        @Override
//        public void onPlaybackComplete() {
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mHandler.removeCallbacks(mPlayDuration);
//            mPlayStatistics.onPlaybackComplete();
//            mLogtf.d("onPlaybackComplete");
//            if (openSuccess) {
//                playTime += (System.currentTimeMillis() - lastPlayTime);
//            }
//            openSuccess = false;
//        }
//
//        @Override
//        public void onPlayError() {
//            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mHandler.removeCallbacks(mPlayDuration);
//            mPlayStatistics.onPlayError();
//            if (openSuccess) {
//                playTime += (System.currentTimeMillis() - lastPlayTime);
//            }
//            openSuccess = false;
//        }
//
//        @Override
//        public void onOpenSuccess() {
//            isPlay = true;
//            if (startRemote.get()) {
//                mLogtf.d("onOpenSuccess:startRemote=true");
//                stopPlay();
//                return;
//            }
//            openSuccess = true;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mPlayStatistics.onOpenSuccess();
//            mHandler.removeCallbacks(mPlayDuration);
//            mLogtf.d("onOpenSuccess:playTime=" + playTime);
//            mHandler.postDelayed(mPlayDuration, mPlayDurTime);
//            mHandler.removeCallbacks(getVideoCachedDurationRun);
//            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
//        }
//
//        @Override
//        public void onOpenStart() {
//            mLogtf.d("onOpenStart");
//            openStartTime = System.currentTimeMillis();
//            openSuccess = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
//            mPlayStatistics.onOpenStart();
//        }
//
//        @Override
//        public void onOpenFailed(int arg1, int arg2) {
//            isPlay = false;
//            if (openSuccess) {
//                playTime += (System.currentTimeMillis() - lastPlayTime);
//            }
//            openSuccess = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mHandler.removeCallbacks(mPlayDuration);
//            mPlayStatistics.onOpenFailed(arg1, arg2);
//            mLogtf.d("onOpenFailed");
//            if (lastPlayserverEntity != null) {
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() -
//                        reportPlayStarTime, lastPlayserverEntity, "fail reconnect");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//        }
//
//        @Override
//        public void onBufferStart() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
//            mPlayStatistics.onBufferStart();
//            mLogtf.d("onBufferStart");
//        }
//
//        @Override
//        public void onBufferComplete() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mPlayStatistics.onBufferComplete();
//            mLogtf.d("onBufferComplete");
//        }
//    };
//
//    /**
//     * 得到Video缓存时间
//     */
//    private Runnable getVideoCachedDurationRun = new Runnable() {
//        @Override
//        public void run() {
//            mHandler.removeCallbacks(this);
//            if (isPlay && !isFinishing()) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        videoCachedDuration = vPlayer.getVideoCachedDuration();
//                        questionBll.setVideoCachedDuration(videoCachedDuration);
//                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
//                        mLiveBll.getOnloadLogs(TAG, "videoCachedDuration=" + videoCachedDuration);
//                        if (videoCachedDuration > 10000) {
//                            mLiveBll.streamReport(LiveBll.MegId.MEGID_12130, mGetInfo.getChannelname(), -1);
//                            if (lastPlayserverEntity != null) {
//                                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System
//                                        .currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "play delay " +
//                                        "reconnect");
//                                reportPlayStarTime = System.currentTimeMillis();
//                            }
//                        }
//                    }
//                }.start();
//                //logger.i( "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
//            }
//        }
//    };
//
//    /**
//     * 缓冲超时
//     */
//    private Runnable mBufferTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            long openTime = System.currentTimeMillis() - openStartTime;
//            if (openTime > 40000) {
//                mLiveBll.streamReport(LiveBll.MegId.MEGID_12107, mGetInfo.getChannelname(), openTime);
//            } else {
//                mLiveBll.streamReport(LiveBll.MegId.MEGID_12137, mGetInfo.getChannelname(), openTime);
//            }
//            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
//            if (lastPlayserverEntity != null) {
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() -
//                        reportPlayStarTime, lastPlayserverEntity, "buffer empty reconnect");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//            mLiveBll.repair(true);
//            if (liveRemarkBll != null) {
//                liveRemarkBll.setVideoReady(false);
//            }
//            mLiveBll.liveGetPlayServer(false);
//        }
//    };
//
//    /** 播放时长，7分钟统计 */
//    private Runnable mPlayDuration = new Runnable() {
//        @Override
//        public void run() {
//            if (lastPlayserverEntity != null) {
//                lastPlayTime = System.currentTimeMillis();
//                playTime += mPlayDurTime;
//                logger.d( "mPlayDuration:playTime=" + playTime / 1000);
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() -
//                        reportPlayStarTime, lastPlayserverEntity, "normal");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//            if (isPlay && !isFinishing()) {
//                mHandler.postDelayed(this, mPlayDurTime);
//            }
//        }
//    };
//
//    /**
//     * 打开超时
//     */
//    private Runnable mOpenTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            long openTimeOut = System.currentTimeMillis() - openStartTime;
//            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
//            mLiveBll.repair(false);
//            mLiveBll.liveGetPlayServer(false);
//        }
//    };
//
//    @Override
//    public void onTeacherNotPresent(final boolean isBefore) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
////                setFirstBackgroundVisible(View.VISIBLE);
////                String text;
////                if (isBefore) {
////                    text = "老师还未进入直播间，请稍后再来";
////                } else {
////                    text = "你来晚了，下课了，等着看回放吧";
////                }
////                final String msg = text;
////                if (tvLoadingHint != null) {
////                    tvLoadingHint.setText(msg);
////                }
//                if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
//                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
//                        setFirstBackgroundVisible(View.VISIBLE);
//                        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
//                        ivTeacherNotpresent.setVisibility(View.GONE);
//                        return;
//                    }
//                }
//                mLogtf.d("onTeacherNotPresent:First=" + rlFirstBackgroundView.getVisibility());
//                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
//                    ivTeacherNotpresent.setVisibility(View.GONE);
//                } else {
//                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
//                    ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//                    findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用
//
//    }
//
//    int count = 0;
//
//    @Override
//    public void onLiveInit(LiveGetInfo getInfo) {
//        mGetInfo = getInfo;
//        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
//        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
//        questionBll.setLiveVideoSAConfig(liveVideoSAConfig);
//        englishH5CoursewareBll.setLiveVideoSAConfig(liveVideoSAConfig);
//        liveMediaControllerBottom.setVisibility(View.VISIBLE);
//        if ("1".equals(mGetInfo.getIsShowMarkPoint())) {
//            liveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
//        }
//        long before = System.currentTimeMillis();
//        if (liveLazyBllCreat != null) {
//            liveLazyBllCreat.setGetInfo(getInfo);
//        }
//        if (mGetInfo.getStudentLiveInfo() != null
//                && "1".equals(mGetInfo.getIsShowCounselorWhisper())) {
//
//            LiveAutoNoticeBll liveAutoNoticeBll = liveLazyBllCreat.createAutoNoticeBll();
//            liveAutoNoticeBll.setGrade(mGetInfo.getGrade());
//            liveAutoNoticeBll.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
//            liveAutoNoticeBll.setTeacherImg(mGetInfo.getTeacherIMG());
//            liveAutoNoticeBll.setTeacherName(mGetInfo.getTeacherName());
//            liveAutoNoticeBll.setLiveBll(mLiveBll);
//            mLiveBll.setLiveAutoNoticeBll(liveAutoNoticeBll);
//            //if (mQuestionAction instanceof QuestionBll) {
//            questionBll.setLiveAutoNoticeBll(liveAutoNoticeBll);
//            //}
//            //if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
//            englishH5CoursewareBll.setLiveAutoNoticeBll(liveAutoNoticeBll);
//            //}
//        }
//
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
//            if (studentLiveInfo != null && studentLiveInfo.isExpe()) {
//                expeBll = new ExpeBll(this, mLiveBll);
//                expeBll.setVSectionID(mVSectionID);
//                expeBll.setShareDataManager(getInfo, studentLiveInfo, mShareDataManager);
//                RelativeLayout bottomContent = (RelativeLayout) findViewById(R.id
//                        .rl_course_video_live_question_content);
//                expeBll.initView(bottomContent);
//            }
//            if (rankBll != null) {
//                rankBll.setGetInfo(mGetInfo);
//            }
//            if (englishH5Cache != null) {
//                englishH5Cache.getCourseWareUrl();
//            }
//            if (IS_SCIENCE) {
//                SpeechFeedBackBll speechFeedBackBll = new SpeechFeedBackBll(this, mLiveBll);
//                speechFeedBackBll.setGetInfo(getInfo);
////                SpeechCollectiveBll speechFeedBackBll = new SpeechCollectiveBll(this, mLiveBll);
//                speechFeedBackBll.setBottomContent(bottomContent);
//                speechFeedBackAction = speechFeedBackBll;
//                mLiveBll.setSpeechFeedBackAction(speechFeedBackBll);
//                QuestionWebCache questionWebCache = new QuestionWebCache(this);
//                questionWebCache.startCache();
//            }
//        }
//        logger.d( "onLiveInit:time=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        //本场成就
//        if (1 == getInfo.getIsAllowStar()) {
////            starBll = new StarInteractBll(this, liveType, getInfo.getStarCount(), mIsLand);
//            starBll = new LiveAchievementBll(this, liveType, getInfo, mIsLand);
//            starBll.setLiveBll(mLiveBll);
//            starBll.initView(bottomContent);
//            mLiveBll.setStarAction(starBll);
//            //能量条
//            EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(this, mGetInfo);
//            boolean initView = englishSpeekBll.initView(bottomContent, mGetInfo.getMode(), null);
//            if (initView) {
//                englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
//                englishSpeekBll.setLiveBll(mLiveBll);
//                englishSpeekBll.setmShareDataManager(mShareDataManager);
//                mLiveBll.setEnglishSpeekAction(englishSpeekBll);
//                LiveVideoActivity.this.englishSpeekBll = englishSpeekBll;
//            }
//        }
//        logger.d( "onLiveInit:time2=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        if (1 == getInfo.getIsEnglish()) {
//            mIse = new SpeechEvaluatorUtils(true);
//            questionBll.setIse(mIse);
//            englishH5CoursewareBll.setIse(mIse);
//            //记录当前正在走的模型，留给界面更新使用
//            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
//                    RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
//        } else {
//            if (!IS_SCIENCE) {
//                String[] subjectIds = getInfo.getSubjectIds();
//                if (subjectIds != null) {
//                    for (int i = 0; i < subjectIds.length; i++) {
//                        String subjectId = subjectIds[i];
//                        if (LiveVideoConfig.SubjectIds.SUBJECT_ID_CH.equals(subjectId)) {
//                            mIse = new SpeechEvaluatorUtils(true, Constants.ASSESS_PARAM_LANGUAGE_CH);
//                            //记录当前正在走的模型，留给界面更新使用
//                            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
//                                    RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        mMediaController.setFileName(getInfo.getName());
//        if (getInfo.isCloseChat()) {
//            liveMessageBll.closeChat(true);
//        }
//        liveMessageBll.setLiveGetInfo(getInfo);
//        //rollCallBll.onLiveInit(liveType, getInfo);
//        questionBll.setLiveGetInfo(getInfo);
//        videoChatBll.onLiveInit(getInfo);
//        logger.d( "onLiveInit:time3=" + (System.currentTimeMillis() - before));
//    }
//
//    @Override
//    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
//        mServer = server;
//        final AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
//        mLogtf.d("onLiveStart:change=" + change.get());
//        mLiveTopic = cacheData;
//        questionBll.setLiveTopic(cacheData);
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                if (change.get()) {
//                    setFirstBackgroundVisible(View.VISIBLE);
//                }
//                if (tvLoadingHint != null) {
//                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
//                            .getLiveTopic().getMode())) {
//                        tvLoadingHint.setText(mainTeacherLoad);
//                    } else {
//                        tvLoadingHint.setText(coachTeacherLoad);
//                    }
//                }
//            }
//        });
//        rePlay(change.get());
//    }
//
//    @Override
//    public void onModeChange(final String mode, final boolean isPresent) {
//        mLogtf.i("onModeChange:mode=" + mode);
//        try {
//            liveMessageBll.onModeChange(mode, isPresent);
//            rollCallBll.onModeChange(mode, isPresent);
//        } catch (Exception e) {
//            mLogtf.e("onModeChange:mode=" + mode, e);
//        }
//        mLogtf.i("onModeChange:mode=" + mode);
////        if (expeBll != null) {
////            expeBll.onModeChange(mode);
////        }
//        if (englishSpeekBll != null) {
//            englishSpeekBll.onModeChange(mode, audioRequest);
//        }
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                if (liveRemarkBll != null) {
//                    liveRemarkBll.setVideoReady(false);
//                }
//                mLogtf.d("onModeChange:isInitialized=" + isInitialized());
//                if (isInitialized()) {
//                    mHandler.removeCallbacks(mPlayDuration);
//                    vPlayer.releaseSurface();
//                    vPlayer.stop();
//                }
//                isPlay = false;
//                setFirstBackgroundVisible(View.VISIBLE);
//                if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mode)) {
//                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
//                        return;
//                    }
//                }
//                if (isPresent) {
//                    if (tvLoadingHint != null) {
//                        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
//                            tvLoadingHint.setText(mainTeacherLoad);
//                        } else {
//                            tvLoadingHint.setText(coachTeacherLoad);
//                        }
//                    }
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onClassTimoOut() {
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "你来晚了，下课了，等着看回放吧";
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
//    @Override
//    public void onLiveDontAllow(final String msg) {
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//        XESToastUtils.showToast(this, "将在3秒内退出");
//        postDelayedIfNotFinish(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent();
//                intent.putExtra("msg", msg);
//                setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
//                finish();
//            }
//        }, 3000);
//    }
//
//    @Override
//    public void onLiveError(final ResponseEntity responseEntity) {
//        mLogtf.d("onLiveError");
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "" + responseEntity.getErrorMsg();
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
//    @Override
//    public AtomicBoolean getStartRemote() {
//        return startRemote;
//    }
//
//    /**
//     * 第一次播放，或者播放失败，重新播放
//     *
//     * @param modechange
//     */
//    @Override
//    public void rePlay(boolean modechange) {
//        if (mGetInfo == null) {//上次初始化尚未完成
//            return;
//        }
//        if (startRemote.get()) {
//            return;
//        }
//        livePlayLog.onReplay();
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//            if (LiveTopic.MODE_TRANING.endsWith(mGetInfo.getLiveTopic().getMode()) && mGetInfo.getStudentLiveInfo()
//                    .isExpe()) {
//                tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
//                setFirstBackgroundVisible(View.VISIBLE);
//                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
//                ivTeacherNotpresent.setVisibility(View.GONE);
//                return;
//            }
//        }
//        new Thread() {
//            @Override
//            public void run() {
//                boolean isPresent = mLiveBll.isPresent();
//                if (isPresent) {
//                    mHandler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (tvLoadingHint != null) {
//                                if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith
//                                        (mGetInfo.getLiveTopic().getMode())) {
//                                    tvLoadingHint.setText(mainTeacherLoad);
//                                } else {
//                                    tvLoadingHint.setText(coachTeacherLoad);
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        }.start();
//        String url;
//        String msg = "rePlay:";
//        if (mServer == null) {
//            livePlayLog.setLastPlayserverEntity(null);
//            String rtmpUrl = null;
//            String[] rtmpUrls = mGetInfo.getRtmpUrls();
//            if (rtmpUrls != null) {
//                rtmpUrl = rtmpUrls[(lastIndex++) % rtmpUrls.length];
//            }
//            if (rtmpUrl == null) {
//                rtmpUrl = mGetInfo.getRtmpUrl();
//            }
//            url = rtmpUrl + "/" + mGetInfo.getChannelname();
//            msg += "mServer=null";
//            mLiveBll.setPlayserverEntity(null);
//        } else {
//            List<PlayserverEntity> playservers = mServer.getPlayserver();
////            for (int i = 0; i < playservers.size(); i++) {
////                final PlayserverEntity playserverEntity = playservers.get(i);
////                mLiveBll.dns_resolve_stream(playserverEntity, mGetInfo.getChannelname(), mServer.getAppname(), new
//// AbstractBusinessDataCallBack() {
////                    @Override
////                    public void onDataSucess(Object... objData) {
////                        String ip = (String) objData[0];
////                        mLogtf.d("dns_resolve_stream:ip=" + ip);
////                    }
////
////                    @Override
////                    public void onDataFail(int errStatus, String failMsg) {
////                        mLogtf.d("dns_resolve_stream:onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
////                        super.onDataFail(errStatus, failMsg);
////                    }
////                });
////            }
//            msg += "playservers=" + playservers.size();
//            PlayserverEntity entity = null;
//            boolean useFlv = false;
//            if (lastPlayserverEntity == null) {
//                msg += ",lastPlayserverEntity=null";
//                entity = playservers.get(0);
//            } else {
//                msg += ",failPlayserverEntity=" + failPlayserverEntity.size();
//                if (!failPlayserverEntity.isEmpty()) {
//                    boolean allRtmpFail = true;
//                    boolean allFlvFail = true;
//                    List<PlayserverEntity> flvPlayservers = new ArrayList<>();
//                    for (int i = 0; i < playservers.size(); i++) {
//                        PlayserverEntity playserverEntity = playservers.get(i);
//                        if (!StringUtils.isEmpty(playserverEntity.getFlvpostfix())) {
//                            flvPlayservers.add(playserverEntity);
//                            if (!failFlvPlayserverEntity.contains(playserverEntity)) {
//                                allFlvFail = false;
//                            }
//                        }
//                        if (!failPlayserverEntity.contains(playserverEntity)) {
//                            allRtmpFail = false;
//                        }
//                    }
//                    if (allFlvFail) {
//                        msg += ",allFlvFail";
//                        failPlayserverEntity.clear();
//                        failFlvPlayserverEntity.clear();
//                    } else {
//                        if (allRtmpFail) {
//                            if (flvPlayservers.isEmpty()) {
//                                failPlayserverEntity.clear();
//                            } else {
//                                if (!lastPlayserverEntity.isUseFlv()) {
//                                    entity = flvPlayservers.get(0);
//                                    entity.setUseFlv(true);
//                                    useFlv = true;
//                                    msg += ",setUseFlv1";
//                                } else {
//                                    for (int i = 0; i < flvPlayservers.size(); i++) {
//                                        PlayserverEntity playserverEntity = flvPlayservers.get(i);
//                                        if (lastPlayserverEntity.getAddress().equals(playserverEntity.getAddress())) {
//                                            if (modechange) {
//                                                entity = flvPlayservers.get(i % flvPlayservers.size());
//                                            } else {
//                                                entity = flvPlayservers.get((i + 1) % flvPlayservers.size());
//                                            }
//                                            entity.setUseFlv(true);
//                                            useFlv = true;
//                                            msg += ",setUseFlv2,modechange=" + modechange;
//                                            break;
//                                        }
//                                    }
//                                    if (entity == null) {
//                                        msg += ",entity=null1";
//                                        entity = flvPlayservers.get(0);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                if (entity == null) {
//                    for (int i = 0; i < playservers.size(); i++) {
//                        PlayserverEntity playserverEntity = playservers.get(i);
//                        if (lastPlayserverEntity.equals(playserverEntity)) {
//                            if (modechange) {
//                                entity = playservers.get(i % playservers.size());
//                            } else {
//                                entity = playservers.get((i + 1) % playservers.size());
//                            }
//                            msg += ",entity=null2,modechange=" + modechange;
//                            break;
//                        }
//                    }
//                }
//                if (entity == null) {
//                    msg += ",entity=null3";
//                    entity = playservers.get(0);
//                }
//            }
//            lastPlayserverEntity = entity;
//            mLiveBll.setPlayserverEntity(entity);
//            livePlayLog.setLastPlayserverEntity(entity);
//            if (useFlv) {
//                url = "http://" + entity.getAddress() + ":" + entity.getHttpport() + "/" + mServer.getAppname() + "/"
//                        + mGetInfo.getChannelname() + entity.getFlvpostfix();
//            } else {
//                if (StringUtils.isEmpty(entity.getIp_gslb_addr())) {
//                    url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo
//                            .getChannelname();
//                } else {
//                    final PlayserverEntity finalEntity = entity;
//                    mLiveBll.dns_resolve_stream(entity, mServer, mGetInfo.getChannelname(), new
//                            AbstractBusinessDataCallBack() {
//                                @Override
//                                public void onDataSucess(Object... objData) {
//                                    if (finalEntity != lastPlayserverEntity) {
//                                        return;
//                                    }
//                                    String provide = (String) objData[0];
//                                    String url;
//                                    if ("wangsu".equals(provide)) {
//                                        url = objData[1] + "&username=" + mGetInfo.getUname() + "&cfrom=android";
//                                        playNewVideo(Uri.parse(url), mGetInfo.getName());
//                                    } else if ("ali".equals(provide)) {
//                                        url = (String) objData[1];
//                                        StringBuilder stringBuilder = new StringBuilder(url);
//                                        addBody("Sucess", stringBuilder);
//                                        url = stringBuilder + "&username=" + mGetInfo.getUname();
//                                        playNewVideo(Uri.parse(url), mGetInfo.getName());
//                                    } else {
//                                        return;
//                                    }
//                                    StableLogHashMap stableLogHashMap = new StableLogHashMap("glsb3rdDnsReply");
//                                    stableLogHashMap.put("message", "" + url);
//                                    stableLogHashMap.put("activity", mContext.getClass().getSimpleName());
//                                    Loger.e(mContext, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData(), true);
//                                }
//
//                                @Override
//                                public void onDataFail(int errStatus, String failMsg) {
//                                    if (finalEntity != lastPlayserverEntity) {
//                                        return;
//                                    }
//                                    String url = "rtmp://" + finalEntity.getAddress() + "/" + mServer.getAppname() +
//                                            "/" +
//                                            mGetInfo.getChannelname();
//                                    StringBuilder stringBuilder = new StringBuilder(url);
//                                    addBody("Fail", stringBuilder);
//                                    playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
//                                }
//                            });
//                    return;
//                }
//            }
//            msg += ",entity=" + entity.getIcode();
//        }
//        StringBuilder stringBuilder = new StringBuilder(url);
//        msg += addBody("rePlay", stringBuilder);
//        msg += ",url=" + stringBuilder;
//        mLogtf.d(msg);
//        playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
//    }
//
//    /**
//     * 直播地址的一些通用参数
//     *
//     * @param method
//     * @param url
//     * @return
//     */
//    protected String addBody(String method, StringBuilder url) {
//        String msg = "";
//        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
//            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
//                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
//                msg += ",t1";
//            } else {
//                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
//                    url.append("?" + mGetInfo.getSkeyPlayT() + "&cfrom=android");
//                    msg += ",t2";
//                } else {
//                    url.append("?cfrom=android");
//                    msg += ",t3";
//                }
//            }
//        } else {
//            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
//                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
//                msg += ",f1";
//            } else {
//                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayF())) {
//                    url.append("?" + mGetInfo.getSkeyPlayF() + "&cfrom=android");
//                    msg += ",f2";
//                } else {
//                    url.append("?cfrom=android");
//                    msg += ",f3";
//                }
//            }
//        }
//        logger.d( "addBody:method=" + method + ",url=" + url);
//        return msg;
//    }
//
//    @Override
//    public void stopPlay() {
//        if (isInitialized()) {
//            vPlayer.releaseSurface();
//            vPlayer.stop();
//        }
//    }
//
//    /**
//     * 播放失败，或者完成时调用
//     */
//    private void onFail(int arg1, final int arg2) {
//        if (lastPlayserverEntity != null) {
//            if (lastPlayserverEntity.isUseFlv()) {
//                if (!failFlvPlayserverEntity.contains(lastPlayserverEntity)) {
//                    failFlvPlayserverEntity.add(lastPlayserverEntity);
//                }
//            } else {
//                if (!failPlayserverEntity.contains(lastPlayserverEntity)) {
//                    failPlayserverEntity.add(lastPlayserverEntity);
//                }
//            }
//        }
//        if (liveRemarkBll != null) {
//            Loger.i("liveremarkbll", "video fail");
//            liveRemarkBll.setVideoReady(false);
//        }
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                if (tvLoadingHint != null) {
//                    String errorMsg = null;
//                    AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
//                    if (error != null) {
//                        errorMsg = error.getNum() + " (" + error.getTag() + ")";
//                    }
//                    TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
//                    if (errorMsg != null) {
//                        if (tvFail != null) {
//                            tvFail.setVisibility(View.VISIBLE);
//                            tvFail.setText(errorMsg);
//                        }
//                    } else {
//                        if (tvFail != null) {
//                            tvFail.setVisibility(View.INVISIBLE);
//                        }
//                    }
//                    mLogtf.d("onFail:arg2=" + arg2 + ",errorMsg=" + errorMsg + ",isPresent=" + mLiveBll.isPresent());
//                    if (mLiveBll.isPresent()) {
//                        if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
//                                .getLiveTopic().getMode())) {
//                            tvLoadingHint.setText(mainTeacherLoad);
//                        } else {
//                            tvLoadingHint.setText(coachTeacherLoad);
//                        }
//                    }
//                    RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
//                    if (status != null) {
//                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
//                    }
//                }
//            }
//        });
//        mLiveBll.liveGetPlayServer(false);
//    }
//
//    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
//        if (isFinishing()) {
//            return;
//        }
//        mHandler.postDelayed(r, delayMillis);
//    }
//
//    public void setFirstBackgroundVisible(int visible) {
//        rlFirstBackgroundView.setVisibility(visible);
//        if (visible == View.GONE) {
//            ivTeacherNotpresent.setVisibility(View.GONE);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent event) {
//        logger.i( "onEvent:netWorkType=" + event.netWorkType);
//        videoChatBll.onNetWorkChange(event.netWorkType);
//        mLiveBll.onNetWorkChange(event.netWorkType);
//        if (englishH5Cache != null) {
//            englishH5Cache.onNetWorkChange(event.netWorkType);
//        }
//    }
//
//    /**
//     * 只在WIFI下使用激活
//     *
//     * @param onlyWIFIEvent
//     * @author zouhao
//     * @Create at: 2015-9-24 下午1:57:04
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {
//        Toast.makeText(this, "没有wifi", Toast.LENGTH_SHORT).show();
//        onUserBackPressed();
//    }
//
//    /**
//     * 是否显示移动网络提示
//     */
//    private boolean mIsShowMobileAlert = true;
//
//    /**
//     * 开启了3G/4G提醒
//     *
//     * @param event
//     * @author zouhao
//     * @Create at: 2015-10-12 下午1:49:22
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.NowMobileEvent event) {
//        if (mIsShowMobileAlert) {
//            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(this, mBaseApplication, false,
//                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
//            cancelDialog.setCancelBtnListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onUserBackPressed();
//                }
//            });
//            cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？")
//                    .showDialog();
//            mIsShowMobileAlert = false;
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
//        liveMessageBll.onGetMyGoldDataEvent(event.goldNum);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (null != this.getCurrentFocus()) {
//            /** 点击空白位置 隐藏软键盘 */
//            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onUserBackPressed() {
//        if (questionBll.onBack()) {
//
//        } else {
//            if (englishH5CoursewareBll.onBack()) {
//
//            } else if (h5CoursewareBll.onBack()) {
//
//            } else {
//                if (rankBll != null && rankBll.onBack()) {
//                    return;
//                }
//                super.onUserBackPressed();
//            }
//        }
//    }
//
//    /**
//     * 结束聊天
//     */
//    public void stopIRC() {
//        if (mLiveBll != null) {
//            mLiveBll.stopIRC();
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        isPlay = false;
//        if (mLogtf != null) {
//            mLogtf.d("onDestroy");
//        }
//        liveMessageBll.onDestroy();
//        videoChatBll.onDestroy();
//        new Thread() {
//            @Override
//            public void run() {
//                if (mLiveBll != null) {
//                    mLiveBll.onDestroy();
//                    LogToFile.liveBll = null;
//                }
//                ProxUtil.getProxUtil().clear(LiveVideoActivity.this);
//            }
//        }.start();
//        AppBll.getInstance().unRegisterAppEvent(this);
//        englishH5CoursewareBll.destroy();
//        if (englishSpeekBll != null) {
//            englishSpeekBll.destory();
//        }
//        if (englishH5Cache != null) {
//            englishH5Cache.stop();
//        }
//        if (speechFeedBackAction != null) {
//            speechFeedBackAction.stop();
//        }
//
//        /*if (teacherPraiseBll != null) {
//            teacherPraiseBll.onDestroy();
//        }
//*/
//        super.onDestroy();
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     * @param requestCode
//     */
//    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
//        Intent intent = new Intent(context, LiveVideoActivity.class);
//        intent.putExtras(bundle);
//        context.startActivityForResult(intent, requestCode);
//    }
//
//    @Override
//    public void onMsgUrlClick(String url) {
////        onPauseNotStopVideo = true;
//    }
//
//    @Override
//    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
//
//    }
//
//    @Override
//    public void onMediaViewClick(View child) {
//        if (rankBll != null) {
//            rankBll.onTitleShow(true);
//        }
//    }
//
//    //语音请求和释放
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                englishSpeekBll.start();
//                logger.d( "start:englishSpeekBll.start");
//            }
//        }
//    };
//
//    @Override
//    public void request(OnAudioRequest onAudioRequest) {
//        audioRequest = true;
//        logger.d( "request:englishSpeekBll=" + (englishSpeekBll == null));
//        if (englishSpeekBll != null) {
//            handler.removeMessages(1);
//            englishSpeekBll.stop(onAudioRequest);
//        } else {
//            if (onAudioRequest != null) {
//                onAudioRequest.requestSuccess();
//            }
//        }
//    }
//
//    @Override
//    public void release() {
//        audioRequest = false;
//        logger.d( "release:englishSpeekBll=" + (englishSpeekBll == null));
//        if (englishSpeekBll != null) {
//            handler.sendEmptyMessageDelayed(1, 2000);
//        }
//    }
//
//    @Override
//    public void requestWebView() {
//        logger.d( "requestWebView:englishH5Cache=" + (englishH5Cache == null));
//        if (englishH5Cache != null) {
//            englishH5Cache.stop();
//        }
//    }
//
//    @Override
//    public void releaseWebView() {
//        logger.d( "releaseWebView:englishH5Cache=" + (englishH5Cache == null));
//        if (englishH5Cache != null) {
//            englishH5Cache.start();
//        }
//    }
//
//    @Override
//    protected void updateIcon() {
//        updateLoadingImage();
//        updateRefreshImage();
//    }
//
//    protected void updateLoadingImage() {
//        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false,
//                ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
//        if (footerIconEntity != null) {
//            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
//            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
//                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal)
//                        .error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
//            }
//        }
//    }
//}
