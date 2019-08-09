//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
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
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.business.UserBll;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.entity.FooterIconEntity;
//import com.xueersi.common.event.AppEvent;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.logerhelper.MobEnumUtil;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.common.speech.SpeechEvaluatorUtils;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.VPlayerListener;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
//import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveActivityBase;
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
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAchievementBll;
//import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBllL;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveLazyBllCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
//import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.LiveVoiceAnswerCreat;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.RankBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.RedPackageBll;
//import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackAction;
//import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
//import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
//import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionWebCache;
//import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
//import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
//import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
//import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import tv.danmaku.ijk.media.player.AvformatOpenInputError;
//
///**
// * 直播
// *
// * @author linyuqiang
// */
//public class LiveVideoFragment extends LiveFragmentBase implements VideoAction, ActivityStatic, BaseLiveMessagePager.OnMsgUrlClick, BaseLiveMediaControllerBottom.MediaChildViewClick, AudioRequest, WebViewRequest, VideoChatEvent {
//
//    private String TAG = "LiveVideoActivityLog";
//    /** 播放器同步 */
//    private static final Object mIjkLock = new Object();
//    private WeakHandler mHandler = new WeakHandler(null);
//    /** 直播服务器 */
//    private PlayServerEntity mServer;
//
//    /** 是否播放成功 */
//    boolean openSuccess = false;
//    private LiveBll2 mLiveBll;
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
//    LiveVideoBll liveVideoBll;
//
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
//    LiveMessageBll liveMessageBll;
//    VideoChatBll videoChatBll;
//    QuestionBll questionBll;
//    RollCallBll rollCallBll;
//    RedPackageBll redPackageBll;
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
//        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
//                + ".txt"));
//        mLogtf.clear();
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        liveType = activity.getIntent().getIntExtra("type", 0);
//        // 设置不可自动横竖屏
//        setAutoOrientation(false);
//        AppBll.getInstance().registerAppEvent(this);
//        boolean init = initData();
//        if (!init) {
//            onUserBackPressed();
//            return false;
//        }
//        logger.d( "onVideoCreate:time1=" + (System.currentTimeMillis() - startTime) + "," + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        String stuId = LiveAppUserInfo.getInstance().getStuId();
//        LiveGetInfo mGetInfo = LiveVideoEnter.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
//        initAllBll();
//        logger.d( "onVideoCreate:time2=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        logger.d( "onVideoCreate:time3=" + (System.currentTimeMillis() - before));
////        SpeechAssessmentWebPager pager=new SpeechAssessmentWebPager(mContext,"","","",true,"",null);
////        ((RelativeLayout)findViewById(R.id.rl_speech_test)).addView(pager.getRootView());
//        return true;
//    }
//
//    @Override
//    protected void onVideoCreateEnd() {
//        mLiveBll.setLivePlayLog(totalFrameStat);
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
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        mContentView = super.onCreateView(inflater, container, savedInstanceState);
//        initView();
//        return mContentView;
//    }
//
//    private void initView() {
//        // 预加载布局
//        rlFirstBackgroundView = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_first_backgroud);
//        ivTeacherNotpresent = (ImageView) mContentView.findViewById(R.id.iv_course_video_teacher_notpresent);
//        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
//        bottomContent.setVisibility(View.VISIBLE);
//        praiselistContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_praiselist_content);
//        praiselistContent.setVisibility(View.VISIBLE);
//        ivLoading = (ImageView) mContentView.findViewById(R.id.iv_course_video_loading_bg);
//        updateLoadingImage();
//        tvLoadingHint = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_content);
//        // 预加载布局中退出事件
//        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
//        tvLoadingHint.setText("获取课程信息");
//        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
//        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
//                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
//        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//
//        //公开表扬,只有直播有
//        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
//            rankBll.initView(bottomContent, lp);
//        }
//        videoChatBll.initView(bottomContent);
//        //点名
//        rollCallBll.initView(bottomContent);
//        //互动题和懂了吗
//        questionBll.initView(bottomContent, true);
//        //红包
//        redPackageBll.initView(bottomContent);
//        //学习报告
//        learnReportBll.initView(bottomContent);
//        h5CoursewareBll.initView(bottomContent);
//        englishH5CoursewareBll.initView(bottomContent);
//
//        setFirstParam(lp);
//        liveMessageBll.setVideoLayout(lp.width, lp.height);
//        logger.d( "initView:time2=" + (System.currentTimeMillis() - before));
//        final View contentView = mContentView.findViewById(android.R.id.content);
//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        if (videoView.getWidth() <= 0) {
//                            return;
//                        }
//                        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
//                        //logger.i( "setVideoWidthAndHeight:isLand=" + isLand);
//                        if (!isLand) {
//                            return;
//                        }
//                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
//                                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
//                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//                        setFirstParam(lp);
//                        liveMessageBll.setVideoLayout(lp.width, lp.height);
//                        questionBll.setVideoLayout(lp.width, lp.height);
//                        if (rankBll != null) {
//                            rankBll.setVideoLayout(lp.width, lp.height);
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
//            entity.setId(LiveAppUserInfo.getInstance().getStuId()+(i==5?"":"abc"));
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
//        Intent intent = activity.getIntent();
//        courseId = intent.getStringExtra("courseId");
//        vStuCourseID = intent.getStringExtra("vStuCourseID");
//        mVSectionID = intent.getStringExtra("vSectionID");
//        mVideoType = MobEnumUtil.VIDEO_LIVE;
//        if (TextUtils.isEmpty(mVSectionID)) {
//            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
//        XesMobAgent.enterLiveRoomFrom(from);
//        if (liveType == LiveBll.LIVE_TYPE_LIVE) {// 直播
//            mLiveBll = new LiveBllL(activity, vStuCourseID, courseId, mVSectionID, from, null);
//        } else if (liveType == LiveBll.LIVE_TYPE_LECTURE) {
//            mLiveBll = new LiveBllL(activity, mVSectionID, liveType, from);
//        } else if (liveType == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
//            mLiveBll = new LiveBllL(activity, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
//        } else {
//            Toast.makeText(activity, "直播类型不支持", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private void initAllBll() {
//        liveVideoBll = new LiveVideoBll(activity, mLiveBll);
//        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
//        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
//        liveMessageBll = new LiveMessageBll(activity, liveType);
//        liveMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//        questionBll = new QuestionBll(activity, vStuCourseID);
//        liveMessageBll.setQuestionBll(questionBll);
//        videoChatBll = new VideoChatBll(activity, this);
//        rollCallBll = new RollCallBll(activity);
//        redPackageBll = new RedPackageBll(activity);
//        learnReportBll = new LearnReportBll(activity);
//        h5CoursewareBll = new H5CoursewareBll(activity);
//        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
//        questionBll.setShareDataManager(ShareDataManager.getInstance());
//
////        LogToFile.liveBll = mLiveBll;
//        mLiveBll.setQuestionAction(questionBll);
//        mLiveBll.setRollCallAction(rollCallBll);
//        mLiveBll.setReadPackageBll(redPackageBll);
//        mLiveBll.setLearnReportAction(learnReportBll);
//        mLiveBll.setVideoAction(this);
//        mLiveBll.setRoomAction(liveMessageBll);
//        mLiveBll.setH5CoursewareAction(h5CoursewareBll);
//        mLiveBll.setEnglishH5CoursewareAction(englishH5CoursewareBll);
//        mLiveBll.setVideoChatAction(videoChatBll);
//        videoChatBll.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom());
//        mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), false);
//        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
//        mMediaController.setControllerTop(baseLiveMediaControllerTop);
//        setMediaControllerBottomParam(videoView.getLayoutParams());
//
//        liveMessageBll.setLiveBll(mLiveBll);
//        //rollCallBll.setLiveBll(mLiveBll);
//        redPackageBll.setLiveBll(mLiveBll);
//        learnReportBll.setLiveBll(mLiveBll);
//        questionBll.setLiveBll(mLiveBll);
//        questionBll.setVSectionID(mVSectionID);
//        redPackageBll.setVSectionID(mVSectionID);
//        questionBll.setLiveType(liveType);
//        questionBll.initData();
//        questionBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mLiveBll, questionBll.new LiveQuestionSwitchImpl()));
//        questionBll.setBaseSpeechCreat(new LiveSpeechCreat());
//        englishH5CoursewareBll.setShareDataManager(ShareDataManager.getInstance());
//        englishH5CoursewareBll.setLiveType(liveType);
//        englishH5CoursewareBll.setVSectionID(mVSectionID);
//        englishH5CoursewareBll.setLiveBll(mLiveBll);
//        englishH5CoursewareBll.initData();
//        englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mLiveBll, englishH5CoursewareBll.new LiveQuestionSwitchImpl()));
//        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
//            rankBll = new RankBll(activity);
//            rankBll.setLiveMediaController(mMediaController, liveMediaControllerBottom);
//            rankBll.setLiveBll(mLiveBll);
//            englishH5Cache = new EnglishH5Cache(activity, mLiveBll, mVSectionID);
////            englishH5Cache = new EnglishH5CacheZip(this, mLiveBll, mVSectionID);
//        }
//        videoChatBll.setLiveBll(mLiveBll);
//
//        liveLazyBllCreat = new LiveLazyBllCreat(activity, mLiveBll);
//        liveLazyBllCreat.setQuestionBll(questionBll);
//        mLiveBll.setLiveLazyBllCreat(liveLazyBllCreat);
//
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
//        //logger.e( "setMediaControllerBottomParam:paddingBottom=" + paddingBottom + "," + liveMediaControllerBottom.getPaddingBottom());
//        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
//            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
//        }
//    }
//
//    /**
//     * 设置蓝屏界面
//     */
//    private void setFirstParam(ViewGroup.LayoutParams lp) {
//        final View contentView = mContentView.findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
//        int rightMargin = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / LiveVideoConfig.VIDEO_WIDTH + (screenWidth - lp.width) / 2);
//        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
//            params.rightMargin = rightMargin;
//            params.bottomMargin = params.topMargin = topMargin;
//            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
////            rlFirstBackgroundView.setLayoutParams(params);
//            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
////            ivTeacherNotpresent.setLayoutParams(params);
//        }
//        //logger.e( "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
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
//    protected VideoFragment getFragment() {
//        return new LiveVideoPlayFragment();
//    }
//
//    protected class LiveLivePlayerPlayFragment extends VideoFragment {
//
//        @Override
//        protected void onPlayOpenStart() {
//            setFirstBackgroundVisible(View.VISIBLE);
//            mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onPlayOpenSuccess() {
//            TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
//            if (tvFail != null) {
//                tvFail.setVisibility(View.INVISIBLE);
//            }
//            setFirstBackgroundVisible(View.GONE);
//            rollCallBll.onPlayOpenSuccess(videoView.getLayoutParams());
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
//        }
//
//        @Override
//        protected void playComplete() {
//            postDelayedIfNotFinish(new Runnable() {
//
//                @Override
//                public void run() {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            synchronized (mIjkLock) {
//                                onFail(0, 0);
//                            }
//                        }
//                    }.start();
//                }
//            }, 200);
//        }
//
//        @Override
//        protected void onPlayError() {
//            mHandler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    tvLoadingHint.setText("您的手机暂时不支持播放直播");
//                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//                }
//            });
//        }
//
//        @Override
//        public void onTitleShow(boolean show) {
//            liveMessageBll.onTitleShow(show);
//            if (rankBll != null) {
//                rankBll.onTitleShow(show);
//            }
//        }
//
//        protected VPlayerListener getWrapListener() {
//            return liveVideoBll.getPlayListener();
//        }
//
//    }
//
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
//                                liveVideoBll.stopPlayDuration();
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
//    public boolean isFinishing() {
//        return activity.isFinishing();
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
//                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
//                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
//                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
//                        setFirstBackgroundVisible(View.VISIBLE);
//                        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
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
//                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
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
//        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
//            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
//            if (rankBll != null) {
//                rankBll.setGetInfo(mGetInfo);
//            }
//            if (englishH5Cache != null) {
//                englishH5Cache.getCourseWareUrl();
//            }
//            if (IS_SCIENCE) {
//                SpeechFeedBackBll speechFeedBackBll = new SpeechFeedBackBll(activity, mLiveBll);
//                speechFeedBackBll.setGetInfo(getInfo);
////                SpeechCollectiveBll speechFeedBackBll = new SpeechCollectiveBll(this, mLiveBll);
//                speechFeedBackBll.setBottomContent(bottomContent);
//                speechFeedBackAction = speechFeedBackBll;
//                mLiveBll.setSpeechFeedBackAction(speechFeedBackBll);
//                QuestionWebCache questionWebCache = new QuestionWebCache(activity);
//                questionWebCache.startCache();
//            }
//        }
//        logger.d( "onLiveInit:time=" + (System.currentTimeMillis() - before));
//        before = System.currentTimeMillis();
//        //本场成就
//        if (1 == getInfo.getIsAllowStar()) {
////            starBll = new StarInteractBll(this, liveType, getInfo.getStarCount(), mIsLand);
//            starBll = new LiveAchievementBll(activity, liveType, getInfo.getStarCount(), getInfo.getGoldCount(), mIsLand);
//            starBll.setLiveBll(mLiveBll);
//            starBll.initView(bottomContent);
//            mLiveBll.setStarAction(starBll);
//            //能量条
//            EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity);
//            boolean initView = englishSpeekBll.initView(bottomContent, mGetInfo.getMode(), null);
//            if (initView) {
//                englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
//                englishSpeekBll.setLiveBll(mLiveBll);
//                englishSpeekBll.setLiveMessageBll(liveMessageBll);
//                englishSpeekBll.setmShareDataManager(ShareDataManager.getInstance());
//                mLiveBll.setEnglishSpeekAction(englishSpeekBll);
//                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoFragment.this.englishSpeekBll = englishSpeekBll;
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
//        questionBll.setUserName(getInfo);
//        videoChatBll.onLiveInit(getInfo);
//        logger.d( "onLiveInit:time3=" + (System.currentTimeMillis() - before));
//    }
//
//    @Override
//    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
//        mServer = server;
//        liveVideoBll.onLiveStart(server, cacheData, modechange);
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
//                    if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
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
//                    liveVideoBll.stopPlayDuration();
//                    vPlayer.releaseSurface();
//                    vPlayer.stop();
//                }
//                isPlay = false;
//                setFirstBackgroundVisible(View.VISIBLE);
//                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
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
//        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "你来晚了，下课了，等着看回放吧";
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
//    @Override
//    public void onLiveDontAllow(final String msg) {
//        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//        XESToastUtils.showToast(activity, "将在3秒内退出");
//        postDelayedIfNotFinish(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent();
//                intent.putExtra("msg", msg);
//                activity.setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
//                activity.finish();
//            }
//        }, 3000);
//    }
//
//    @Override
//    public void onLiveError(final ResponseEntity responseEntity) {
//        mLogtf.d("onLiveError");
//        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "" + responseEntity.getErrorMsg();
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
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
//        totalFrameStat.onReplay();
//        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
//            if (LiveTopic.MODE_TRANING.endsWith(mGetInfo.getLiveTopic().getMode()) && mGetInfo.getStudentLiveInfo().isExpe()) {
//                tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
//                setFirstBackgroundVisible(View.VISIBLE);
//                mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
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
//                                if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
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
//        liveVideoBll.rePlay(modechange);
//    }
//
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
//                    TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
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
//                        if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
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
//        Toast.makeText(activity, "没有wifi", Toast.LENGTH_SHORT).show();
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
//            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
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
//        Intent intent = new Intent(context, com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoFragment.class);
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
//    public void onWebViewEnd() {
//        englishH5Cache = null;
//    }
//
//    public void updateIcon() {
//        updateLoadingImage();
//        updateRefreshImage();
//    }
//
//    protected void updateLoadingImage() {
//        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
//        if (footerIconEntity != null) {
//            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
//            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
//                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
//        }
//    }
//}
