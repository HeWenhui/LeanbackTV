package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseBll;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.browser.event.BrowserEvent;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.PutQuestion;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RedPacketAlertDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExPerienceLiveMessage;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionMulitSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5PlaybackPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.RoundProgressBar;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by David on 2018/3/6.
 * 体验课播放器
 */

public class ExperienceLiveVideoActivity extends LiveVideoActivityBase implements BaseLiveMediaControllerBottom.MediaChildViewClick {
    QuestionBll questionBll;
    private RelativeLayout rlLiveMessageContent;
    LiveMessageBll liveMessageBll;
    /** 横屏聊天信息 */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private ScanRunnable scanRunnable;
    private Handler scanHandler;
    private List<ExPerienceLiveMessage.LiveExMsg> mMsgs;
    private LiveMessagePager mLiveMessagePager;
    private Long timer = 0L;
    private static final Object mIjkLock = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    /** 视频宽度 */
    public static final float VIDEO_WIDTH = 1280f;
    /** 视频高度 */
    public static final float VIDEO_HEIGHT = 720f;
    /** 视频宽高比 */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
    private Long startTime;
    private Long mTotaltime;
    /** 播放时长定时任务(心跳) */
    private final long mPlayDurTime = 300000;
    /** 正在播放 */
    private boolean isPlay = false;
    /** 按Home键的进度模拟 */
    private boolean firstTime = true;
    /** 播放暂停状态的记录 */
    private boolean pause = false;
    /** 播放时长 */
    long playTime = 0;
    /** 上次播放统计开始时间 */
    private Long lastPlayTime;
    /** 播放异常统计开始时间 */
    private Long errorPlayTime;

    // 定时获取聊天记录的任务
    class ScanRunnable implements Runnable {
        HandlerThread handlerThread = new HandlerThread("ScanRunnable");

        ScanRunnable() {
            Loger.i(TAG, "ScanRunnable");
            handlerThread.start();
            scanHandler = new Handler(handlerThread.getLooper());
        }

        void exit() {
            handlerThread.quit();
        }

        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            initOldMessage(mVideoEntity.getLiveId(), mVideoEntity.getCourseId(), timer + Long.parseLong(mVideoEntity.getVisitTimeKey()));
//            initOldMessage(mVideoEntity.getLiveId(),mVideoEntity.getCourseId(),timer + 2970L);
            timer = timer + 10;
            Log.e("Duncan", "timer:" + timer);
            scanHandler.postDelayed(this, 10000);


        }
    }

    /** 播放时长，5分钟统计 */
    private Runnable mPlayDuration = new Runnable() {
        @Override
        public void run() {
            if (isPlay && !isFinishing()) {
                // 上传心跳时间
//                lastPlayTime = System.currentTimeMillis();
//                playTime += mPlayDurTime;
                mLiveBll.uploadExperiencePlayTime(mVideoEntity.getLiveId(), mVideoEntity.getChapterId(), 300L);
                mHandler.postDelayed(this, mPlayDurTime);
            }
        }
    };

    // 体验课相关日志的埋点
    LiveAndBackDebug ums = new LiveAndBackDebug() {
        @Override
        public void umsAgentDebugSys(String eventId, Map<String, String> mData) {

        }

        @Override
        public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
            UmsAgentManager.umsAgentOtherBusiness(ExperienceLiveVideoActivity.this, appID, UmsConstants.uploadSystem, mData);

        }

        @Override
        public void umsAgentDebugPv(String eventId, Map<String, String> mData) {

        }
    };

    private String TAG = "ExpericenceLiveVideoActivityLog";
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    LiveMediaControllerBottom liveMediaControllerBottom;
    /** 初始进入播放器时的预加载界面 */
    /** 老师不在直播间 */
    private ImageView ivTeacherNotpresent;
    RelativeLayout bottomContent;
    RelativeLayout praiselistContent;
    /** 缓冲提示 */
    private TextView tvLoadingHint;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    /** 是不是播放失败 */
    boolean resultFailed = false;
    /** 当前是否正在显示互动题 */
    private boolean mIsShowQuestion = false;
    /** 当前是否正在显示红包 */
    private boolean mIsShowRedpacket = false;
    /** 当前是否正在显示对话框 */
    private boolean mIsShowDialog = false;
    /** 是不是点击返回键或者点周围,取消互动题,而没有使用getPopupWindow */
    boolean mIsBackDismiss = true;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    String beforeAttach;

    /** 是否显示移动网络提示 */
    private boolean mIsShowMobileAlert = true;

    /** 我的课程业务层 */
    LectureLivePlayBackBll lectureLivePlayBackBll;

    /** 声明PopupWindow对象的引用 */
    private PopupWindow mPopupWindow;

    /** 试题对错弹框 */
    PopupWindow mAnswerPopupWindow;

//    /** 统一的加载动画 */
//    private LoadingDialog mProgressDialog;

    /** 红包弹窗 */
    private RedPacketAlertDialog mRedPacketDialog;

    /** 互动题 */
    private VideoQuestionEntity mQuestionEntity;
    /** 互动题为空的异常 */
    private Exception questionEntityNullEx;
    /** 各种互动题的页面 */
    /** 语音答题的页面 */
    private VoiceAnswerPager voiceAnswerPager;
    /** 课前测的页面,暂时没有 */
    @Deprecated
    private BaseExamQuestionInter examQuestionPlaybackPager;
    /** 语音评测，role play的页面 */
    private BaseSpeechAssessmentPager speechQuestionPlaybackPager;
    /** 讲座购课广告的页面 */
    private LecAdvertPager lecAdvertPager;
    /** 填空题布局 */
    QuestionFillInBlankLivePager mVideoCourseQuestionPager;

    /** 红包id */
    private String mRedPacketId;
    /** 播放路径名 */
    private String mWebPath;
    /** 节名称 */
    private String mSectionName;
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
    /** 加载视频提示 */
    private ImageView ivLoading;
    private TextView tvLoadingContent;
    /** 从哪个页面跳转 */
    String where;
    int isArts;
    /** 区分文理appid */
    String appID = UmsConstants.OPERAT_APP_ID;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE;
    /** 本地视频 */
    boolean islocal;
    static int times = -1;
    long createTime;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private LiveBll mLiveBll;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /** 当前时间，豪妙 */
    private long currentMsg = 0;
    private ExPerienceLiveMessage mMessage;
    private Boolean send = false;
    private String testIdKey = "ExperienceLiveQuestion";
    private RoundProgressBar mProgressbar;
    private PopupWindow mWindow;
    private ExperienceResult mData;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = getIntent();
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);
        initAllBll();
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        AppBll.getInstance().registerAppEvent(this);
//        initView();
        loadData();
        return true;
    }

    private void initAllBll() {
        questionBll = new QuestionBll(this, mVideoEntity.getStuCourseId());
        mLiveBll = new LiveBll(this, mVideoEntity.getSectionId(), mVideoEntity.getChapterId(), 1, 0);
        liveMessageBll = new LiveMessageBll(this, 1);

    }

    private void initView() {
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController, this);
        liveMediaControllerBottom.experience();
        int topGap = (ScreenUtils.getScreenHeight() - videoView.getLayoutParams().height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (liveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            liveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
//        ivTeacherNotpresent.setImageResource(R.drawable.);
        bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);

        praiselistContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_praiselist_content);
        praiselistContent.setVisibility(View.VISIBLE);
        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
        if (mIsLand) {
            // 加载横屏时互动题的列表布局
            rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_contents);
        } else {
            if (rlQuestionContent != null) {
                rlQuestionContent.removeAllViews();
                rlQuestionContent = null;
            }
        }
        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        setFirstParam(lp);
        final View contentView = findViewById(android.R.id.content);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
//                        if (videoView.getWidth() <= 0) {
//                            return;
//                        }
                        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                        //Loger.i(TAG, "setVideoWidthAndHeight:isLand=" + isLand);
                        if (!isLand) {
                            return;
                        }
                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
                                (int) VIDEO_HEIGHT, VIDEO_RATIO);
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        setFirstParam(lp);
                        mLiveMessagePager.setVideoWidthAndHeight(lp.width, lp.height);
                    }
                });
            }
        }, 10);
    }

    private void setFirstParam(ViewGroup.LayoutParams lp) {
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
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
//            rlFirstBackgroundView.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
//            ivTeacherNotpresent.setLayoutParams(params);
        }

    }

    private void initMessagePager(RelativeLayout bottomContent) {
        rlLiveMessageContent = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlLiveMessageContent, params);

        long before = System.currentTimeMillis();
        mLiveMessagePager = new LiveMessagePager(this, questionBll, ums, liveMediaControllerBottom, liveMessageLandEntities, null);
//        mLiveMessagePager = liveMessagePager;
        Loger.d(TAG, "initViewLive:time1=" + (System.currentTimeMillis() - before));

//        mLiveMessagePager.getInfo = getInfo;
//        mLiveMessagePager.urlclick = urlclick;
//        liveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setMessageBll(liveMessageBll);
        mLiveMessagePager.setIrcState(mLiveBll);
        mLiveMessagePager.onModeChange(mLiveBll.getMode());
        // 03.08 设置假的聊天连接成功信号
        if (!pause) {
            mLiveMessagePager.onConnects();
        }
        mLiveMessagePager.setIsRegister(true);
        // 03.13 设置假的上课人数
        if (mMessage != null && mMessage.getOnlineNum().size() != 0 && mMessage.getOnlineNum().get(0).getOnlineNum() != null) {
            mLiveMessagePager.showPeopleCount(Integer.parseInt(mMessage.getOnlineNum().get(0).getOnlineNum()));
        } else {
            mLiveMessagePager.showPeopleCount(3);
        }
        // 03.22 设置统计日志的公共参数
        mLiveMessagePager.setLiveTermId(mVideoEntity.getLiveId(), mVideoEntity.getChapterId());
//        if (mode != null) {
//            mLiveMessagePager.onopenchat(openchat, mode, false);
//        }
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);
    }

    private void loadData() {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        mRedPacketDialog = new RedPacketAlertDialog(this, baseApplication, false);
        lectureLivePlayBackBll = new LectureLivePlayBackBll(ExperienceLiveVideoActivity.this, "");
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        where = getIntent().getStringExtra("where");
        isArts = getIntent().getIntExtra("isArts", 0);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID_BACK;
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 播放视频
        mWebPath = mVideoEntity.getVideoPath();
        playNewVideo(Uri.parse(mWebPath), mSectionName);

    }

    public interface GetExperienceLiveMsgs {
        void getLiveExperienceMsgs(ExPerienceLiveMessage liveMessageGroupEntity);

        void onPmFailure();
    }

    private void initOldMessage(String liveId, String classId, Long start) {
        lectureLivePlayBackBll.getExperienceMsgs(liveId, classId, start, new GetExperienceLiveMsgs() {
            @Override
            public void getLiveExperienceMsgs(ExPerienceLiveMessage liveMessageGroupEntity) {
                mMessage = liveMessageGroupEntity;
                sendMessage();
            }

            @Override
            public void onPmFailure() {

            }
        });
    }

    private void sendMessage() {
        if (mMessage != null && mMessage.getMsg() != null && mMessage.getMsg().size() > 0) {
            mMsgs = new ArrayList<>();
            for (int i = 0; i < mMessage.getMsg().size(); i++) {
                if ("130".equals(mMessage.getMsg().get(i).getText().getType())) {
                    mMsgs.add(mMessage.getMsg().get(i));
                }
            }
            if (mMsgs.size() > 0) {
                send = true;
            }
        }
        if (mMessage != null && mMessage.getOnlineNum().size() != 0 && mMessage.getOnlineNum().get(0).getOnlineNum() != null) {
            mLiveMessagePager.showPeopleCount(Integer.parseInt(mMessage.getOnlineNum().get(0).getOnlineNum()));
        } else {
            mLiveMessagePager.showPeopleCount(3);
        }
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, ExperienceLiveVideoActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPlayOpenSuccess() {
        isPlay = true;
        mTotaltime = getDuration();
        Log.e("mqtt", "mTotaltime:" + mTotaltime);
        Log.e("mqtt", "seekto:" + mVideoEntity.getVisitTimeKey());
        // 03.22 统计用户进入体验播放器的时间
        StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayEnter");
        logHashMap.put("liveid", mVideoEntity.getLiveId());
        logHashMap.put("termid", mVideoEntity.getChapterId());
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_ENTER);
        ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_ENTER, logHashMap.getData());
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.GONE);
            initView();
            initMessagePager(bottomContent);
            if (firstTime) {
                startTime = System.currentTimeMillis();
                firstTime = false;
            }
            if (mTotaltime < Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000) {
                // 03.21 提示直播已结束
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
                vPlayer.releaseSurface();
                vPlayer.stop();
                // 测试体验课播放器的结果页面
                lectureLivePlayBackBll.getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(), getDataCallBack);
                return;
            }
            seekTo(Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System.currentTimeMillis() - startTime));
        }
        if (mQuestionEntity != null) {
            Loger.d(TAG, "onPlayOpenSuccess:showQuestion:isAnswered=" + mQuestionEntity.isAnswered() + "," +
                    "mIsShowQuestion=" + mIsShowQuestion);
//            showQuestion(mQuestionEntity);
        }
        // 心跳时间的统计
        mHandler.postDelayed(mPlayDuration, mPlayDurTime);
    }

    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            // 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                mData = (ExperienceResult) objData[0];
                // 测试体验课播放器的结果页面
                if (mData != null) {
                    showPopupwinResult();
                }

            }

        }
    };

    private void showPopupwinResult() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View result = inflater.inflate(R.layout.pop_experience_livevideo_result, null);
        mWindow = new PopupWindow(result, dp2px(this, 295), dp2px(this, 343), false);
        mWindow.setOutsideTouchable(false);
        mWindow.showAtLocation(result, Gravity.CENTER, 0, 0);
        mProgressbar = (RoundProgressBar) result.findViewById(R.id.roundProgressBar);
        TextView recommand = (TextView) result.findViewById(R.id.tv_detail_result);
        TextView beat = (TextView) result.findViewById(R.id.tv_result);
        TextView totalscore = (TextView) result.findViewById(R.id.tv_total_score);
        beat.setText("恭喜，你打败了" + mData.getBeat() + "%的学生");
        if (TextUtils.isEmpty(mData.getRecommend())) {
            recommand.setVisibility(View.GONE);
        } else {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("推荐您报名" + mData.getRecommend());
        }
        totalscore.setText(mData.getCorrect() + "%");
        mProgressbar.setMax(100);
        if (mData.getCorrect() > 0) {
            mProgressbar.setProgress(mData.getCorrect());
        } else {
            mProgressbar.setProgress(0);
        }
        ImageButton shut = (ImageButton) result.findViewById(R.id.ib_shut);
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        Button chat = (Button) result.findViewById(R.id.bt_chat);
        if (TextUtils.isEmpty(mData.getWechatNum())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mData.getWechatNum());
                Toast.makeText(ExperienceLiveVideoActivity.this, "您已复制老师微信号，快去添加吧!", Toast.LENGTH_LONG).show();
            }
        });
        Button apply = (Button) result.findViewById(R.id.bt_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.getUrl() != null) {
                    BrowserActivity.openBrowser(ExperienceLiveVideoActivity.this, mData.getUrl());
                } else {
                    Toast.makeText(ExperienceLiveVideoActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onPlayOpenStart() {
        setFirstBackgroundVisible(View.VISIBLE);
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);


    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    /**
     * 试题布局隐藏
     */
    private void questionViewGone() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mIsShowQuestion = false;
                if (rlQuestionContent != null) {
                    rlQuestionContent.removeAllViews();
                }
            }
        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
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

    // 03.13 根据播放的进度获取互动题
    @Override
    protected void playingPosition(long currentPosition, long duration) {
        super.playingPosition(currentPosition, duration);
        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
            return;
        }
        currentMsg = currentPosition;
        // 扫描互动题
        scanQuestion(currentPosition);
        Log.e("Duncan", "currentPosition:" + currentPosition);
        // 获取聊天记录
        if (scanRunnable == null) {
            scanRunnable = new ScanRunnable();
            scanHandler.post(scanRunnable);
        }
        //发送聊天记录
        if (send && mMsgs.size() > 0) {
            for (int i = 0; i < mMsgs.size(); i++) {
                if (currentMsg / 1000 == mMsgs.get(i).getReleative_time()) {
                    mLiveMessagePager.addMessage(mMsgs.get(i).getText().getName(), LiveMessageEntity.MESSAGE_TIP, mMsgs.get(i).getText().getMsg(), "");
                    mMsgs.remove(i);
                }
            }
        }
    }

    /** 扫描是否有需要弹出的互动题 */
    public void scanQuestion(long position) {

        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }

        // 互动题结束，隐藏互动题
        if (mQuestionEntity != null && mQuestionEntity.getvEndTime() != 0
                && mQuestionEntity.getvEndTime() == TimeUtils.gennerSecond(position)) {
            // 如果是互动题，则提示时间结束
            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
                    && !mQuestionEntity.isAnswered()) {
                XESToastUtils.showToast(this, "答题时间结束...");
                mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
            }
        }

        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        getPlayQuetion(TimeUtils.gennerSecond(position));
        showQuestion(oldQuestionEntity);
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
        if (oldQuestionEntity == null && mQuestionEntity != null && !mQuestionEntity.isAnswered()) {
            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                if (vPlayer != null) {
                    vPlayer.pause();
                }
                mQuestionEntity.setAnswered(true);
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vPlayer != null) {
                            vPlayer.start();
                        }
                        showExam();
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seekTo(mQuestionEntity.getvEndTime() * 1000);
                    }
                });
                verifyCancelAlertDialog.showDialog();
                return;
            }
        }
        // 有交互信息并且没有互动题
        if (mQuestionEntity != null && !mQuestionEntity.isAnswered() && !mIsShowQuestion && !(mQuestionEntity.getvQuestionID() + mVideoEntity.getLiveId()).equals(mShareDataManager.getString(testIdKey, "", 1))) {
            // 互动题
            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
                if (!(mMediaController != null && mMediaController.isShow())) {
                    // 红包隐藏
                    redPacketHide();
                    showQestion();
                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_SHOW,
                            XesMobAgent.XES_VIDEO_INTERACTIVE);
                }
                // 红包
            } else if (LocalCourseConfig.CATEGORY_REDPACKET == mQuestionEntity.getvCategory()) {
                if (("" + mRedPacketId).equals(mQuestionEntity.getvQuestionID())) {
                    return;
                }
                mRedPacketId = mQuestionEntity.getvQuestionID();
                showRedPacket(mQuestionEntity);
                XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_SHOW,
                        XesMobAgent.XES_VIDEO_INTERACTIVE);
            } else if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                // 红包隐藏
                redPacketHide();
                showExam();
            }
            // 互动题结束
        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final VideoQuestionEntity mQuestionEntity) {
        mIsShowRedpacket = true;
        // 如果视频控制栏显示
        mRedPacketDialog.setRedPacketConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.bt_livevideo_redpackage_cofirm) {
                    mQuestionEntity.setAnswered(true);
                    DataLoadEntity loadEntity = new DataLoadEntity(mContext);
                    loadEntity.setLoadingTip(R.string.loading_tip_default);
                    // 获取红包
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {
                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
                        lectureLivePlayBackBll.getRedPacket(loadEntity, mVideoEntity.getLiveId(), mRedPacketId);
                    } else if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVETYPE_LECTURE) {
                        publicLiveCourseRedPacket();
                    } else {
                        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
                        lectureLivePlayBackBll.getLivePlayRedPackets(loadEntity, mRedPacketId, mVideoEntity.getLiveId(), mVideoEntity.getChapterId());
                    }
                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_GRAB,
                            XesMobAgent.XES_VIDEO_INTERACTIVE);
                }
                redPacketViewGone();
            }
        }).showDialog();
    }

    /**
     * 公开直播红包逻辑
     */
    public void publicLiveCourseRedPacket() {
        initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
    }

    /**
     * 红包布局隐藏
     */
    private void redPacketViewGone() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mIsShowRedpacket = false;
            }
        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
    }

    /**
     * 获取互动题
     *
     * @param playPosition
     */
    private void getPlayQuetion(int playPosition) {
        Log.e("Duncan", "getPlayQuetion:" + playPosition);
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        int startTime, endTime;

        boolean hasQuestionShow = false;
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = null;
            if (lstVideoQuestion.get(i) != null) {
                videoQuestionEntity = lstVideoQuestion.get(i);
            } else {
                continue;
            }
//            if (videoQuestionEntity.isAnswered()) {
//                continue;
//            }
            startTime = videoQuestionEntity.getvQuestionInsretTime();
            endTime = videoQuestionEntity.getvEndTime();
            // 03.24 聊天指令的控制设置
//            if(13 == videoQuestionEntity.getvCategory()){
//                if (startTime == playPosition) {
//                    mLiveMessagePager.onopenchat(true,"in-class",true);
//                    break;
//                }
//
//            }
//            if(14 == videoQuestionEntity.getvCategory()){
//                if (startTime == playPosition) {
//                    mLiveMessagePager.onDisable(true,true);
//                    break;
//                }
//            }
            // 红包只有开始时间
            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                // 互动题在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
                // 互动题在开始时间和结束时间之间
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            }
        }
//        Loger.i(TAG, "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",mQuestionEntity=" + (mQuestionEntity != null));
        if (mQuestionEntity != null) {
            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                if (mQuestionEntity.getvEndTime() < playPosition) {
                    if (examQuestionPlaybackPager != null) {
                        examQuestionPlaybackPager.examSubmitAll();
                        if (vPlayer != null) {
                            vPlayer.pause();
                        }
                        Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
                    }
                }
                return;
            }
        }
        // 如果没有互动题则移除
        if (!hasQuestionShow && mQuestionEntity != null) {
            startTime = mQuestionEntity.getvQuestionInsretTime();
            //播放器seekto的误差
            Loger.i(TAG, "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + ",startTime=" + startTime);
            if (mQuestionEntity.isClick()) {
                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
                    return;
                }
            }
            mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
        }
    }

    @Override
    protected void resultComplete() {
        // 播放完毕直接退出
//        onUserBackPressed();
        // 直播结束后，显示结束的提示图片
        ivTeacherNotpresent.setVisibility(View.VISIBLE);
//        ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
        ivTeacherNotpresent.setBackgroundResource(R.drawable.live_free_play_end);
        // 获取学生的学习反馈
        lectureLivePlayBackBll.getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(), getDataCallBack);
        EventBus.getDefault().post(new BrowserEvent.ExperienceLiveEndEvent(1));
        if (scanRunnable != null) {
            scanRunnable.exit();
        }

    }

    @Override
    protected void onRefresh() {
        if (AppBll.getInstance(this).isNetWorkAlert()) {
            videoBackgroundRefresh.setVisibility(View.GONE);
//            Loger.d(TAG, "onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
//            if (rlQuestionContent.getChildCount() > 0) {
//                rlQuestionContent.setVisibility(View.VISIBLE);
//            }
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        }
        AppBll.getInstance(mBaseApplication);
    }

    @Deprecated
    private void showExam() {
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    mPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    videoQuestionLiveEntity.id = mQuestionEntity.getvQuestionID();
                    examQuestionPlaybackPager = new ExamQuestionX5PlaybackPager(ExperienceLiveVideoActivity.this, mVideoEntity.getLiveId(), videoQuestionLiveEntity,
                            false, "", new BaseExamQuestionInter.ExamStop() {
                        @Override
                        public void stopExam( VideoQuestionLiveEntity videoQuestionLiveEntity) {

                        }
                    });
                    rlQuestionContent.removeAllViews();
                    rlQuestionContent.addView(examQuestionPlaybackPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rlQuestionContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /** 显示互动题 */
    private void showQestion() {
        final long before = System.currentTimeMillis();
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                Loger.i(TAG, "showQestion:time=" + (System.currentTimeMillis() - before));
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    // 填空题
                    if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(mQuestionEntity.getvQuestionType())) {
                        showFillBlankQuestion();
                        // 选择题
                    } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
                        if ("1".equals(mQuestionEntity.getChoiceType())) {   // 单项选择题
                            showSelectQuestion();
                        } else if ("2".equals(mQuestionEntity.getChoiceType())) {   // 多项选择题
                            showMulitSelectQuestion();
                        } else {
                            XESToastUtils.showToast(ExperienceLiveVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                            return;
                        }
                    } else {
                        XESToastUtils.showToast(ExperienceLiveVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                        return;
                    }
                    mPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
                }
            }
        });
    }

    /**
     * 填空题
     */
    private void showFillBlankQuestion() {
        mVideoCourseQuestionPager = new QuestionFillInBlankLivePager(ExperienceLiveVideoActivity.this, mQuestionEntity);
        mVideoCourseQuestionPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(mVideoCourseQuestionPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

    /** 显示选择题 */
    public void showSelectQuestion() {
        QuestionSelectLivePager questionSelectPager = new QuestionSelectLivePager(ExperienceLiveVideoActivity.this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

    /** 显示多选题 */
    public void showMulitSelectQuestion() {
        QuestionMulitSelectLivePager questionSelectPager = new QuestionMulitSelectLivePager(ExperienceLiveVideoActivity.this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

    /** 红包隐藏 */
    public void redPacketHide() {
        mRedPacketId = "";
        mIsShowRedpacket = false;
        mRedPacketDialog.cancelDialog();
    }

    /**
     * 获取红包成功
     *
     * @param goldNum
     * @param msg
     */
    private void initRedPacketFirstResult(int goldNum, String msg) {
        msg = "+" + goldNum + "金币";
        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
        popupWindow_view.setBackgroundColor(getResources().getColor(R.color.mediacontroller_bg));
        SpannableString msp = new SpannableString(msg);
        float screenDensity = ScreenUtils.getScreenDensity();
        // 字体
        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msp);
//        rlRedpacketContent.addView(view);
//        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlRedpacketContent.removeAllViews();
//            }
//        });
        ImageView ivRedpackageLight = (ImageView) popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_light);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_livevideo_light_rotate);
        ivRedpackageLight.startAnimation(animation);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(View popupWindow_view) {
        // 创建PopupWindow
        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        // 这里是位置显示方式,在屏幕底部
        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAnswerPopupWindow != null) {
                    mAnswerPopupWindow.dismiss();
                }
            }
        });
        disMissAnswerPopWindow();
    }

    /** 回答问题结果提示框延迟三秒消失 */
    public void disMissAnswerPopWindow() {
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAnswerPopupWindow != null) {
                    try {
                        mAnswerPopupWindow.dismiss();
                    } catch (Exception e) {

                    }
                }
            }
        }, 3000);// 延迟3秒钟消失
    }

    /**
     * 发送试题答案
     *
     * @param result
     */
    private void sendQuestionResult(String result, VideoQuestionEntity questionEntity) {
        if (questionEntity == null) {
            return;
        }
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        lectureLivePlayBackBll.saveQuestionResults(loadEntity, questionEntity.getSrcType(), questionEntity.getvQuestionID(), result,
                mVideoEntity.getLiveId(), mVideoEntity.getChapterId(), mVideoEntity.getvLivePlayBackType());
        questionEntity.setAnswered(true);
        // 03.22 本地缓存答过题的testId
//        mShareDataManager.put(questionEntity.getvQuestionID(),true,1);
        mShareDataManager.put(testIdKey, questionEntity.getvQuestionID() + mVideoEntity.getLiveId(), 1);
        questionViewGone();
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_ANSWER,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    private Handler mPlayVideoControlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_QUESTION:
                    mIsShowQuestion = true;
                    if (mMediaController != null) {
                        mMediaController.showLong();
                    }
                    break;
                case NO_QUESTION:
                    if (mVideoCourseQuestionPager != null) {
                        mVideoCourseQuestionPager.hideInputMode();
                    }
                    mQuestionEntity = null;
                    questionViewGone();
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
            }
        }
    };

    /**
     * 主界面响应事件
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-5-6 上午11:13:22 //
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlaybackVideoEvent event) {
        if (event instanceof PlaybackVideoEvent.OnQuesionDown) {
            BaseVideoQuestionEntity questionEntity = ((PlaybackVideoEvent.OnQuesionDown) event).getVideoQuestionEntity();
            // 填空题答案保存
            if (questionEntity != null && questionEntity.getAnswerEntityLst() != null
                    && questionEntity.getAnswerEntityLst().size() != 0) {
                saveQuestionAnswer(questionEntity.getAnswerEntityLst());
                // 选择题答案保存
            } else {
                if (this.mQuestionEntity != null) {
                    if (this.mQuestionEntity.getvQuestionID().equals(questionEntity.getvQuestionID())) {
                        this.mQuestionEntity = (VideoQuestionEntity) questionEntity;
                    }
                }
            }

        } else if (event instanceof PlaybackVideoEvent.OnPlayVideoWebError) {
            String result = ((PlaybackVideoEvent.OnPlayVideoWebError) event).getResult();
            // 如果没有结果提示显示
            if (TextUtils.isEmpty(result)) {
                initRedPacketFirstResult(0, "金币+" + 0 + "枚金币");
            } else {
                localQuesitonResult(result);
            }
        } else if (event instanceof PlaybackVideoEvent.OnGetRedPacket) {
            VideoResultEntity entity = ((PlaybackVideoEvent.OnGetRedPacket) event).getVideoResultEntity();
            // 获取金币成功
            if (entity.getResultType() == 1) {
                initRedPacketResult(entity.getGoldNum());
                // 已经获取过金币
            } else if (entity.getResultType() == 0) {
                initRedPacketOtherResult();
            }
        } else if (event instanceof PlaybackVideoEvent.OnAnswerReslut) {
            VideoResultEntity entity = ((PlaybackVideoEvent.OnAnswerReslut) event).getVideoResultEntity();
            answerResultChk(entity);
        }
    }

    /** 保存学生填空题答案 */
    private void saveQuestionAnswer(List<AnswerEntity> answerEntityLst) {
        if (mQuestionEntity != null) {
            mQuestionEntity.setAnswerEntityLst(answerEntityLst);
        }
    }

    /**
     * 互动题本地结果验证
     *
     * @param result
     */
    private void localQuesitonResult(String result) {
        boolean isRight = true;
        VideoResultEntity entity = new VideoResultEntity();
        try {
            if (mQuestionEntity != null) {
                // 选择题
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
                    if (!TextUtils.equals(mQuestionEntity.getvQuestionAnswer(), result)) {
                        isRight = false;
                    }
                    // 填空题
                } else {
                    int rightNum = 0;
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String stuAnswer = jsonArray.getString(i).trim();
                        String rightAnswer = mQuestionEntity.getAnswerEntityLst().get(i).getRightAnswer();
                        if (TextUtils.equals(stuAnswer, rightAnswer)) {
                            rightNum++;
                        }
                    }
                    if (rightNum == 0) {
                        isRight = false;
                    } else if (rightNum != jsonArray.length()) {
                        entity.setRightNum(rightNum);
                    }

                }
            }
            // 回答正确
            if (isRight) {
                entity.setGoldNum(0);
                entity.setResultType(1);
                // 填空题部分正确
                if (entity.getRightNum() != 0) {
                    entity.setResultType(3);
                }
            } else {
                // 回答错误
                entity.setGoldNum(0);
                entity.setResultType(2);
            }
            answerResultChk(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 互动题结果解析
     *
     * @param entity
     */
    private void answerResultChk(VideoResultEntity entity) {
        // 回答正确提示
        if (entity.getResultType() == 1) {
            initAnswerRightResult(entity.getGoldNum());
            // 回答错误提示
        } else if (entity.getResultType() == 2) {
            initAnswerWrongResult();
            // 填空题部分正确提示
        } else if (entity.getResultType() == 3) {
            initAnswerPartRightResult(entity.getGoldNum());
        }
        mPlayVideoControlHandler.sendEmptyMessage(NO_QUESTION);
    }

    /**
     * 互动题回答正确
     *
     * @param goldNum
     */
    private void initAnswerPartRightResult(int goldNum) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable.bg_pop_question_answer_type3);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("" + goldNum);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答正确
     *
     * @param goldNum
     */
    private void initAnswerRightResult(int goldNum) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        tvGoldHint.setText("" + goldNum);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答错误
     */
    private void initAnswerWrongResult() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_wrong, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 获取红包成功
     *
     * @param goldNum
     */
    private void initRedPacketResult(int goldNum) {
        String msg = "+" + goldNum + "金币";
        View popupWindow_view = getLayoutInflater().inflate(R.layout.dialog_red_packet_success, null, false);
        SpannableString msp = new SpannableString(msg);
        float screenDensity = ScreenUtils.getScreenDensity();
        // 字体
        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msp);
        popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerPopupWindow.dismiss();
            }
        });
        // 创建PopupWindow
        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        // 这里是位置显示方式,在屏幕底部
        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAnswerPopupWindow != null) {
                    mAnswerPopupWindow.dismiss();
                }
            }
        });
        final TextView tvAutoclose = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    mAnswerPopupWindow.dismiss();
                } else {
                    if (mAnswerPopupWindow != null && mAnswerPopupWindow.isShowing()) {
                        tvAutoclose.setText(count.get() + "秒自动关闭");
                        mPlayVideoControlHandler.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000);
    }

    /**
     * 以获取过红包
     */
    private void initRedPacketOtherResult() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlay = false;
        if (scanRunnable != null) {
            scanRunnable.exit();
        }
        // 03.22 统计用户离开体验播放器的时间
        StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayExit");
        logHashMap.put("liveid", mVideoEntity.getLiveId());
        logHashMap.put("termid", mVideoEntity.getChapterId());
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_EXIT);
        ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_EXIT, logHashMap.getData());
    }

//    /** 刷新界面重新加载视频 */
//    protected void onRefresh() {
//        if (mIsEnalbePlayer) {
//            loadView(mLayoutVideo);
//            Log.e("Duncan","refresh");
//        }
//    }


    @Override
    public void onPause() {
        super.onPause();
        isPlay = false;
        pause = true;
        vPlayer.releaseSurface();
        vPlayer.stop();
//        vPlayer.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        playNewVideo(Uri.parse(mWebPath), mSectionName);

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
    public void onMediaViewClick(View child) {

    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        super.resultFailed(arg1, arg2);
        resultFailed = true;
        mIsShowQuestion = mIsShowRedpacket = false;
    }
}
