package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.entity.AnswerEntity;
import com.xueersi.parentsmeeting.entity.AppInfoEntity;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.FooterIconEntity;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.event.AppEvent;
import com.xueersi.parentsmeeting.logerhelper.MobEnumUtil;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecAdvertPagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.business.PutQuestion;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionResultView;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RedPacketAlertDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.PlaybackVideoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseLiveQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.EnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExamQuestionPlaybackPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.H5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionMulitSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSubjectivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssessmentWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SubjectResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoActivity;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.widget.LivePlaybackMediaController;
import com.xueersi.xesalib.umsagent.UmsAgentManager;
import com.xueersi.xesalib.umsagent.UmsConstants;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.time.TimeUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;
import com.xueersi.xesalib.view.layout.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * 直播回放播放页
 *
 * @author Hua
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("unchecked")
public class LivePlayBackVideoActivity extends VideoActivity implements LivePlaybackMediaController.OnPointClick,
        SpeechEvalAction, QuestionWebPager.StopWebQuestion, LiveAndBackDebug, ActivityChangeLand {

    String TAG = "LivePlayBackVideoActivityLog";
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
    /** 各种互动题的页面 */
    /** 语音答题的页面 */
    private VoiceAnswerPager voiceAnswerPager;
    /** 普通互动题，h5显示页面 */
    private QuestionWebPager questionWebPager;
    /** 课前测的页面 */
    private ExamQuestionPlaybackPager examQuestionPlaybackPager;
    /** 语音评测，role play的页面 */
    private BaseSpeechAssessmentPager speechQuestionPlaybackPager;
    /** nb实验的页面 */
    private H5CoursewarePager h5CoursewarePager;
    /** 英语课件的页面 */
    private EnglishH5CoursewarePager englishH5CoursewarePager;
    /** 文科主观题结果的页面 */
    private SubjectResultPager subjectResultPager;
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
    String appID = UmsConstants.LIVE_APP_ID_BACK;
    /** 本地视频 */
    boolean islocal;
    static int times = -1;
    long createTime;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;

    @Override
    protected void onVideoCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onVideoCreate(savedInstanceState);
        times++;
        createTime = System.currentTimeMillis();
        AppBll.getInstance().registerAppEvent(this);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = getIntent();
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);

        // 加载互动题和视频列表
        initView();
        // 请求相应数据
        initData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlQuestionContent.getLayoutParams();
        Loger.d(TAG, "onConfigurationChanged:mIsLand=" + mIsLand);
        if (mIsLand) {
            lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            lp.addRule(RelativeLayout.BELOW, 0);
        } else {
            lp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
        }
        rlQuestionContent.setLayoutParams(lp);
    }

    /** 初始化互动题和竖屏时下方的列表布局 */
    @Override
    protected void attachMediaController() {
        Loger.d(TAG, "attachMediaController:beforeAttach=" + beforeAttach);
        if (resultFailed) {
            Loger.d(TAG, "attachMediaController:resultFailed");
            return;
        }
        if (mMediaController != null) {
            mMediaController.setWindowLayoutType();
            mMediaController.release();
        }

        // 设置当前是否为横屏
        LivePlaybackMediaController mMediaController = new LivePlaybackMediaController(this, this);
        this.mMediaController = mMediaController;
        mMediaController.setAnchorView(videoView.getRootView());
        // 设置播放器横竖屏切换按钮不显示
        mMediaController.setAutoOrientation(false);
        // 播放下一个按钮不显示
        mMediaController.setPlayNextVisable(false);
        // 设置速度按钮显示
        mMediaController.setSetSpeedVisable(true);
        setFileName(); // 设置视频显示名称
        showLongMediaController();
        if (mIsShowQuestion || mIsShowRedpacket || mIsShowDialog) {
            mMediaController.release();
            Loger.d(TAG, "attachMediaController:release:mIsShowQuestion=" + mIsShowQuestion + "," + mIsShowRedpacket
                    + "," + mIsShowDialog);
        }
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        mMediaController.setVideoQuestions("playback" + mVideoEntity.getvLivePlayBackType() + "-", lstVideoQuestion,
                vPlayer.getDuration());
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
        TextView errorInfo = (TextView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id.tv_course_video_errorinfo);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errorInfo.setVisibility(View.VISIBLE);
            String videoKey = getVideoKey();
            if (error == AvformatOpenInputError.HTTP_NOT_FOUND) {
                errorInfo.setText("(" + videoKey + ")" + " 回放未生成");
            }
        }
        if (rlQuestionContent != null) {
            if (lecAdvertPager == null) {
                rlQuestionContent.setVisibility(View.GONE);
            }
//            if (subjectResultPager != null) {
//                for (int i = 0; i < rlQuestionContent.getChildCount(); i++) {
//                    View child = rlQuestionContent.getChildAt(0);
//                    if (child != subjectResultPager.getRootView()) {
//                        rlQuestionContent.removeViewAt(i);
//                        i--;
//                    }
//                }
//            } else {
//                rlQuestionContent.removeAllViews();
//            }
        }
    }

    /** 加载旋转屏时相关布局 */
    @Override
    protected void loadLandOrPortView() {
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(this);
        super.loadLandOrPortView();
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);

        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingContent = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        ImageView ivBack = (ImageView) findViewById(R.id.iv_course_video_back);

        if (ivBack != null) {
            findViewById(R.id.iv_course_video_back).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LivePlayBackVideoActivity.this.onBackPressed();
                }
            });
        }
        // 加载横屏时互动题的列表布局
        rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
//        rlQuestionContent.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
//            @Override
//            public void onChildViewAdded(View parent, View child) {
//                Loger.d(TAG, "onChildViewAdded");
//            }
//
//            @Override
//            public void onChildViewRemoved(View parent, View child) {
//                Loger.d(TAG, "onChildViewRemoved");
//            }
//        });
    }

    /** 竖屏时填充视频列表布局 */
    protected void initData() {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        mRedPacketDialog = new RedPacketAlertDialog(this, baseApplication, false);
        lectureLivePlayBackBll = new LectureLivePlayBackBll(LivePlayBackVideoActivity.this);
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        where = getIntent().getStringExtra("where");
        isArts = getIntent().getIntExtra("isArts", 0);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            LiveVideoConfig.IS_SCIENCE = false;
        } else {
            LiveVideoConfig.IS_SCIENCE = true;
            appID = UmsConstants.LIVE_APP_ID_BACK;
        }
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 设置播放进度
        setmLastVideoPositionKey(mVideoEntity.getVideoCacheKey());
        // mCourseBll.getQuestionLivePlay(section);

        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 统计视频播放key
        mVisitTimeKey = mVideoEntity.getVisitTimeKey();
        // 播放器统计时长发送间隔
        setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());
        // 播放视频
        mWebPath = mVideoEntity.getVideoPath();
//        if (CourseInfoLiveActivity.isTest) {
//            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
//        }
//        mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
        if (islocal) {
            // 互动题播放地址
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        } else {
            getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    if (AppBll.getInstance(LivePlayBackVideoActivity.this).isNetWorkAlert()) {
                        // 互动题播放地址
                        AppBll.getInstance(mBaseApplication);
                        playNewVideo(Uri.parse(mWebPath), mSectionName);
                    } else {
                        AppBll.getInstance(mBaseApplication);
                    }
                    return false;
                }
            });
//            if (AppConfig.DEBUG) {
//                List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//                VideoQuestionEntity videoQuestionEntity = new VideoQuestionEntity();
//                videoQuestionEntity.setvQuestionType("39804");
//                videoQuestionEntity.setvCategory(LocalCourseConfig.CATEGORY_LEC_ADVERT);
//                videoQuestionEntity.setvQuestionInsretTime(600);
//                videoQuestionEntity.setvEndTime(1600);
//                lstVideoQuestion.add(videoQuestionEntity);
//            }
            //测试红包自动关闭
//            rlFirstBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener
// () {
//                @Override
//                public boolean onPreDraw() {
//                    rlFirstBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
//                    initRedPacketResult(10);
//                    return false;
//                }
//            });
            //测试试卷
//            mQuestionEntity = new VideoQuestionEntity();
//            mQuestionEntity.setvQuestionID("2");
//            mQuestionEntity.setvEndTime(120);
//            showExam();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsShowDialog || examQuestionPlaybackPager != null || speechQuestionPlaybackPager != null ||
                h5CoursewarePager != null ||
                englishH5CoursewarePager != null || questionWebPager != null) {
            if (isInitialized()) {
                vPlayer.pause();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speechQuestionPlaybackPager != null) {
            speechQuestionPlaybackPager.stopPlayer();
        }
    }

    @Override
    protected boolean shouldSendPlayVideo() {
        if (mIsShowQuestion) {
            return true;
        }
        return super.shouldSendPlayVideo();
    }

    @Override
    protected void onPlayOpenStart() {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPlayOpenSuccess() {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.GONE);
        }
        if (mQuestionEntity != null) {
            Loger.d(TAG, "onPlayOpenSuccess:showQuestion:isAnswered=" + mQuestionEntity.isAnswered() + "," +
                    "mIsShowQuestion=" + mIsShowQuestion);
//            showQuestion(mQuestionEntity);
        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        String key = "null";
        if (mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {//直播辅导
                key = where + ":playback2," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity.getCourseId()
                        + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
                key = where + ":playback3," + mVideoEntity.getLiveId();
            } else {
                if (islocal) {
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
                        key = where + ":playback4," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
                    } else {//直播课下载
                        key = where + ":playback5," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
                    }
                } else {
                    key = where + ":playback6," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity
                            .getCourseId() + "-" + mVideoEntity.getLiveId();
                }
            }
        }
        UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_PLAYBACK_SPEED,
                "times=" + times + ",time=" + (System.currentTimeMillis() - createTime) + ",speed=" + speed + ",key="
                        + key);
    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        super.resultFailed(arg1, arg2);
        resultFailed = true;
        mIsShowQuestion = mIsShowRedpacket = false;
        Loger.d(TAG, "resultFailed:arg2=" + arg2);
        if (arg2 != 0 && mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {//直播辅导
                XesMobAgent.onOpenFail(where + ":playback2", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
                        mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId
                        (), mWebPath, arg2);
            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
                XesMobAgent.onOpenFail(where + ":playback3", mVideoEntity.getLiveId(), mWebPath, arg2);
            } else {
                if (islocal) {
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
                        XesMobAgent.onOpenFail(where + ":playback4", mVideoEntity.getCourseId() + "-" + mVideoEntity
                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
                    } else {//直播课下载
                        XesMobAgent.onOpenFail(where + ":playback5", mVideoEntity.getCourseId() + "-" + mVideoEntity
                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
                    }
                } else {
                    XesMobAgent.onOpenFail(where + ":playback6", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
                            mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId(), mWebPath, arg2);
                }
            }
        }
    }

    @Override
    protected String getVideoKey() {
        if (!islocal && mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {
                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
            } else if ("PublicLiveDetailActivity".equals(where)) {
                return mVideoEntity.getLiveId();
            } else {
                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
            }
        }
        return super.getVideoKey();
    }

    /** 视频播放进度实时获取 */
    @Override
    protected void playingPosition(long currentPosition, long duration) {
        super.playingPosition(currentPosition, duration);
        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
            return;
        }
        scanQuestion(currentPosition); // 扫描互动题
    }

    @Override
    public void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position) {
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        mQuestionEntity = videoQuestionEntity;
        mQuestionEntity.setClick(true);
        showQuestion(oldQuestionEntity);
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
                Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 1, 1, mQuestionEntity);
                mPlayVideoControlHandler.sendMessage(msg);
                if (voiceAnswerPager != null) {
                    if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(mQuestionEntity.getvQuestionID())) {
                        Loger.d(TAG, "scanQuestion:stopVoiceAnswerPager1");
                        voiceAnswerPager.setEnd();
                        voiceAnswerPager.stopPlayer();
                        voiceAnswerPager = null;
//                        voiceAnswerPager = null;
                    }
                }
            }
        }
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        getPlayQuetion(TimeUtils.gennerSecond(position));
        if (mQuestionEntity != null && voiceAnswerPager != null) {
            if (!voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(mQuestionEntity.getvQuestionID())) {
                Loger.d(TAG, "scanQuestion:stopVoiceAnswerPager2");
                voiceAnswerPager.setEnd();
                stopVoiceAnswerPager();
            }
        }
        showQuestion(oldQuestionEntity);
    }

    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
        if (mQuestionEntity != oldQuestionEntity && !mQuestionEntity.isAnswered()) {
            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
                if (vPlayer != null) {
                    vPlayer.pause();
                }
                mQuestionEntity.setAnswered(true);
                LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackAlertDialog();
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (vPlayer != null) {
//                            vPlayer.start();
//                        }
                        showExam();
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mQuestionEntity.setAnswered(false);
                        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 2, 2, mQuestionEntity);
                        mPlayVideoControlHandler.sendMessage(msg);
                        seekTo(mQuestionEntity.getvEndTime() * 1000);
                        start();
                    }
                });
                verifyCancelAlertDialog.showDialog();
                return;
            } else {
                if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
                        && LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
                    if (vPlayer != null) {
                        vPlayer.pause();
                    }
                    mQuestionEntity.setAnswered(true);
                    LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackAlertDialog();
                    verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了语音测试题，是否现在开始答题？");
                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (vPlayer != null) {
//                                vPlayer.start();
//                            }
                            showSpeech();
                        }
                    });
                    verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            mQuestionEntity.setAnswered(false);
                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 3, 3, mQuestionEntity);
                            mPlayVideoControlHandler.sendMessage(msg);
                            seekTo(mQuestionEntity.getvEndTime() * 1000);
                            start();
                        }
                    });
                    verifyCancelAlertDialog.showDialog();
                    return;
                } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
                    if (vPlayer != null) {
                        vPlayer.pause();
                    }
                    mQuestionEntity.setAnswered(true);
                    LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackAlertDialog();
                    verifyCancelAlertDialog.initInfo("互动实验提醒", "老师发布了互动实验，是否参与互动？");
                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (vPlayer != null) {
//                                vPlayer.start();
//                            }
                            showH5CoursewarePager();
                        }
                    });
                    verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            mQuestionEntity.setAnswered(false);
                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 4, 4, mQuestionEntity);
                            mPlayVideoControlHandler.sendMessage(msg);
                            seekTo(mQuestionEntity.getvEndTime() * 1000);
                            start();
                        }
                    });
                    verifyCancelAlertDialog.showDialog();
                    return;
                } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
                    if (vPlayer != null) {
                        vPlayer.pause();
                    }
                    mQuestionEntity.setAnswered(true);
                    LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackAlertDialog();
                    verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mQuestionEntity == null) {
                                if (vPlayer != null) {
                                    vPlayer.start();
                                }
                                return;
                            }
                            if ("1".equals(mQuestionEntity.getIsVoice())) {
                                try {
                                    showVoiceAnswer(mQuestionEntity);
                                    if (vPlayer != null) {
                                        vPlayer.start();
                                    }
                                } catch (Exception e) {
                                    showEnglishH5CoursewarePager();
                                }
                            } else {
                                showEnglishH5CoursewarePager();
                            }
                            Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showEnglishH5VoiceAnswer");
                            mPlayVideoControlHandler.sendMessage(msg);
//                            showEnglishH5CoursewarePager();
                        }
                    });
                    verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            mQuestionEntity.setAnswered(false);
                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 5, 5, mQuestionEntity);
                            mPlayVideoControlHandler.sendMessage(msg);
                            if (mQuestionEntity != null) {
                                seekTo(mQuestionEntity.getvEndTime() * 1000);
                            }
                            start();
                        }
                    });
                    verifyCancelAlertDialog.showDialog();
                    return;
                }
            }
        }
        // 有交互信息并且没有互动题
        if (mQuestionEntity != null && !mQuestionEntity.isAnswered() && !mIsShowQuestion) {
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
            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == mQuestionEntity.getvCategory()) {
//                mQuestionEntity.setAnswered(true);
//                showLecAdvertPager(mQuestionEntity);
            }
            // 互动题结束
        }
    }

    /** 红包隐藏 */
    public void redPacketHide() {
        mRedPacketId = "";
        mIsShowRedpacket = false;
        mRedPacketDialog.cancelDialog();
    }

    /** 显示互动题 */
    private void showQestion() {
        final long before = System.currentTimeMillis();
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                Loger.i(TAG, "showQestion:time=" + (System.currentTimeMillis() - before));
                String type;
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    if (mQuestionEntity.isH5()) {
                        type = "h5";
                        if (vPlayer != null) {
                            vPlayer.pause();
                        }
                        mQuestionEntity.setAnswered(true);
                        LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackAlertDialog();
                        verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了互动题，是否参与？");
                        verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                            if (vPlayer != null) {
//                                vPlayer.start();
//                            }
                                MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                                AppInfoEntity mAppInfoEntity = AppBll.getInstance().getAppInfoEntity();
                                questionWebPager = new QuestionWebPager(LivePlayBackVideoActivity.this,
                                        LivePlayBackVideoActivity.this, "http://live.xueersi" +
                                        ".com/Live/getMultiTestPaper",
                                        userInfoEntity.getStuId(), mAppInfoEntity.getLoginUserName(), mQuestionEntity
                                        .getvSectionID(), mQuestionEntity.getvQuestionID(), "", "0");
                                rlQuestionContent.removeAllViews();
                                rlQuestionContent.addView(questionWebPager.getRootView(), new LayoutParams
                                        (LayoutParams.MATCH_PARENT,
                                                LayoutParams.WRAP_CONTENT));
                                rlQuestionContent.setVisibility(View.VISIBLE);
                            }
                        });
                        verifyCancelAlertDialog.setCancelBtnListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                            mQuestionEntity.setAnswered(false);
                                Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 6, 6, mQuestionEntity);
                                mPlayVideoControlHandler.sendMessage(msg);
                                seekTo(mQuestionEntity.getvEndTime() * 1000);
                                start();
                            }
                        });
                        verifyCancelAlertDialog.showDialog();
                    } else {
                        // 填空题
                        if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(mQuestionEntity.getvQuestionType())) {
                            type = "FillBlank";
                            if ("1".equals(mQuestionEntity.getIsVoice())) {
                                try {
                                    showVoiceAnswer(mQuestionEntity);
                                } catch (Exception e) {
                                    showFillBlankQuestion(mQuestionEntity);
                                }
                            } else {
                                showFillBlankQuestion(mQuestionEntity);
                            }
                            // 选择题
                        } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(mQuestionEntity.getvQuestionType())) {
                            if ("1".equals(mQuestionEntity.getChoiceType())) {   // 单项选择题
                                type = "Select";
                                if ("1".equals(mQuestionEntity.getIsVoice())) {
                                    try {
                                        showVoiceAnswer(mQuestionEntity);
                                    } catch (Exception e) {
                                        showSelectQuestion(mQuestionEntity);
                                    }
                                } else {
                                    showSelectQuestion(mQuestionEntity);
                                }
                            } else if ("2".equals(mQuestionEntity.getChoiceType())) {   // 多项选择题
                                type = "MulitSelect";
                                if ("1".equals(mQuestionEntity.getIsVoice())) {
                                    try {
                                        showVoiceAnswer(mQuestionEntity);
                                    } catch (Exception e) {
                                        showMulitSelectQuestion(mQuestionEntity);
                                    }
                                } else {
                                    showMulitSelectQuestion(mQuestionEntity);
                                }
                            } else {
                                mQuestionEntity.setAnswered(true);
                                XESToastUtils.showToast(LivePlayBackVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                                return;
                            }
                        } else if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
                            type = "SPEECH";
                        } else if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(mQuestionEntity.getvQuestionType())) {
                            type = "Subjective";
                            showSubjectiveQuestion();
                        } else {
                            mQuestionEntity.setAnswered(true);
                            XESToastUtils.showToast(LivePlayBackVideoActivity.this, "不支持的试题类型，可能需要升级版本");
                            return;
                        }
                    }
                    Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showQestion:type=" + type);
                    mPlayVideoControlHandler.sendMessage(msg);
                }
            }
        });
    }

    private void showExam() {
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showExam");
                    mPlayVideoControlHandler.sendMessage(msg);
                    examQuestionPlaybackPager = new ExamQuestionPlaybackPager(LivePlayBackVideoActivity.this,
                            mVideoEntity.getLiveId(), mQuestionEntity.getvQuestionID());
                    rlQuestionContent.removeAllViews();
                    rlQuestionContent.addView(examQuestionPlaybackPager.getRootView(), new LayoutParams(LayoutParams
                            .MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT));
                    rlQuestionContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showSpeech() {
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showSpeech");
                    mPlayVideoControlHandler.sendMessage(msg);
                    MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                    String speechEvalResultUrl = mShareDataManager.getString(ShareBusinessConfig.SP_SPEECH_URL,
                            LiveVideoConfig.SPEECH_URL, ShareDataManager.SHAREDATA_USER);
//                    speechQuestionPlaybackPager = new SpeechAssessmentPager(LivePlayBackVideoActivity.this, false,
// mVideoEntity.getLiveId(), userInfoEntity.getStuId(),
//                            LivePlayBackVideoActivity.this, mQuestionEntity.getvQuestionID(), speechEvalResultUrl);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    if ("1".equals(mQuestionEntity.getIsAllow42())) {
                        speechQuestionPlaybackPager = new SpeechAssAutoPager(LivePlayBackVideoActivity.this,
                                mVideoEntity.getLiveId(), mQuestionEntity.getvQuestionID(),
                                "", mQuestionEntity.getSpeechContent(), mQuestionEntity.getEstimatedTime(), mQuestionEntity.getvEndTime() - mQuestionEntity.getvQuestionInsretTime(), LivePlayBackVideoActivity.this);
//                        int screenWidth = ScreenUtils.getScreenWidth();
//                        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//                        lp.rightMargin = wradio;
                    } else {
                        speechQuestionPlaybackPager = new SpeechAssessmentWebPager(LivePlayBackVideoActivity.this,
                                mVideoEntity.getLiveId(), mQuestionEntity.getvQuestionID(), userInfoEntity.getStuId(),
                                false, "", LivePlayBackVideoActivity.this);
                    }
                    speechQuestionPlaybackPager.initData();
                    rlQuestionContent.removeAllViews();
                    rlQuestionContent.addView(speechQuestionPlaybackPager.getRootView(), lp);
                    rlQuestionContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /** 显示h5课件 */
    private void showH5CoursewarePager() {
        mPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlQuestionContent != null && mQuestionEntity != null) {
                    Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showH5CoursewarePager");
                    mPlayVideoControlHandler.sendMessage(msg);
                    h5CoursewarePager = new H5CoursewarePager(LivePlayBackVideoActivity.this, mQuestionEntity
                            .getH5Play_url());
                    rlQuestionContent.removeAllViews();
                    rlQuestionContent.addView(h5CoursewarePager.getRootView(), new LayoutParams(LayoutParams
                            .MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT));
                    rlQuestionContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /** 显示h5课件 */
    private BasePager showEnglishH5CoursewarePager() {
        if (rlQuestionContent != null && mQuestionEntity != null) {
            Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showEnglishH5CoursewarePager");
            mPlayVideoControlHandler.sendMessage(msg);
            englishH5CoursewarePager = new EnglishH5CoursewarePager(LivePlayBackVideoActivity.this, true, mVideoEntity.getLiveId(), mQuestionEntity.getEnglishH5Play_url(),
                    mQuestionEntity.getvQuestionID(), mQuestionEntity.getvQuestionType(), "", new
                    EnglishH5CoursewareBll.OnH5ResultClose() {
                        @Override
                        public void onH5ResultClose() {
                            stopEnglishH5Exam();
                        }

                    }, this, "0");
            rlQuestionContent.removeAllViews();
            rlQuestionContent.addView(englishH5CoursewarePager.getRootView(), new LayoutParams(LayoutParams
                    .MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            rlQuestionContent.setVisibility(View.VISIBLE);
            return englishH5CoursewarePager;
        }
        return null;
    }

    /** 讲座广告 */
    private void showLecAdvertPager(final VideoQuestionEntity questionEntity) {
        final LecAdvertEntity lecAdvertEntity = new LecAdvertEntity();
        lecAdvertEntity.course_id = questionEntity.getvQuestionType();
        lecAdvertEntity.id = questionEntity.getvQuestionID();
//        PageDataLoadEntity mPageDataLoadEntity = new PageDataLoadEntity(rlQuestionContent, R.id.fl_livelec_advert_content, DataErrorManager.IMG_TIP_BUTTON);
//        PageDataLoadManager.newInstance().loadDataStyle(mPageDataLoadEntity.beginLoading());
        lectureLivePlayBackBll.getAdOnLL(mVideoEntity.getLiveId(), lecAdvertEntity, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                Loger.d(TAG, "showLecAdvertPager:mQuestionEntity=" + (mQuestionEntity == null));
                if (mQuestionEntity != questionEntity) {
                    return;
                }
                if (lecAdvertEntity.isLearn == 1) {
                    return;
                }
                lecAdvertPager = new LecAdvertPager(LivePlayBackVideoActivity.this, lecAdvertEntity, new LecAdvertPagerClose() {

                    @Override
                    public void close() {
                        if (lecAdvertPager != null) {
                            rlQuestionContent.removeView(lecAdvertPager.getRootView());
                        }
                        Loger.d(TAG, "showLecAdvertPager:close=" + (mQuestionEntity == null));
                        mQuestionEntity = null;
                        lecAdvertPager = null;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    @Override
                    public void onPaySuccess(LecAdvertEntity lecAdvertEntity) {

                    }
                }, mVideoEntity.getLiveId(), LivePlayBackVideoActivity.this);
                rlQuestionContent.removeAllViews();
                rlQuestionContent.addView(lecAdvertPager.getRootView(), new LayoutParams
                        (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                rlQuestionContent.setVisibility(View.VISIBLE);
                lecAdvertPager.initStep1();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    private void showVoiceAnswer(final VideoQuestionEntity videoQuestionLiveEntity) throws Exception {
        if (voiceAnswerPager != null) {
            if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(videoQuestionLiveEntity.getvQuestionID())) {
                return;
            } else {
                voiceAnswerPager.setEnd();
                stopVoiceAnswerPager();
            }
        }
        JSONObject assess_ref = new JSONObject(videoQuestionLiveEntity.getAssess_ref());
//        JSONObject assess_ref = new JSONObject();
//        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.getvQuestionType())) {
//            JSONArray answer = new JSONArray();
//            try {
//                answer.put("B");
//                assess_ref.put("answer", answer);
//                JSONArray options = new JSONArray();
//                {
//                    JSONObject options1 = new JSONObject();
//                    options1.put("option", "A");
//                    JSONArray content1 = new JSONArray();
//                    content1.put("yes it is");
//                    options1.put("content", content1);
//                    options.put(options1);
//                }
//                {
//                    JSONObject options1 = new JSONObject();
//                    options1.put("option", "B");
//                    JSONArray content1 = new JSONArray();
//                    content1.put("no it isn't");
//                    options1.put("content", content1);
//                    options.put(options1);
//                }
//                {
//                    JSONObject options1 = new JSONObject();
//                    options1.put("option", "C");
//                    JSONArray content1 = new JSONArray();
//                    content1.put("you are beautiful");
//                    options1.put("content", content1);
//                    options.put(options1);
//                }
//                {
//                    JSONObject options1 = new JSONObject();
//                    options1.put("option", "D");
//                    JSONArray content1 = new JSONArray();
//                    content1.put("you are very good");
//                    options1.put("content", content1);
//                    options.put(options1);
//                }
//                assess_ref.put("options", options);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            JSONArray answer = new JSONArray();
//            try {
//                answer.put("A");
//                assess_ref.put("answer", answer);
//                JSONArray options = new JSONArray();
//                {
//                    JSONObject options1 = new JSONObject();
//                    options1.put("option", "A");
//                    JSONArray content1 = new JSONArray();
//                    content1.put("are");
//                    options1.put("content", content1);
//                    options.put(options1);
//                }
//                assess_ref.put("options", options);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        rlQuestionContent.removeAllViews();
        rlQuestionContent.setVisibility(View.VISIBLE);
        voiceAnswerPager = new VoiceAnswerPager(this, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.getVoiceQuestiontype(), questionSwitch, this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(voiceAnswerPager.getRootView(), params);
        voiceAnswerPager.setAudioRequest();
        VoiceAnswerLog.sno2(this, videoQuestionLiveEntity);
    }

    QuestionSwitch questionSwitch = new QuestionSwitch() {

        @Override
        public String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity) {
            VideoQuestionEntity entity = (VideoQuestionEntity) baseQuestionEntity;
            if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == entity.getvCategory()) {
                return "h5ware";
            } else {
                return "h5test";
            }
        }

        @Override
        public BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity) {
            if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
                if (voiceAnswerPager != null) {
                    stopVoiceAnswerPager();
                }
                BasePager basePager = showEnglishH5CoursewarePager();
                return basePager;
            } else {
                VideoQuestionEntity videoQuestionLiveEntity1 = (VideoQuestionEntity) baseQuestionEntity;
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity1.getvQuestionType())) {
                    if ("1".equals(videoQuestionLiveEntity1.getChoiceType())) {
                        BaseLiveQuestionPager baseQuestionPager = showSelectQuestion(videoQuestionLiveEntity1);
                        if (voiceAnswerPager != null) {
                            stopVoiceAnswerPager();
                        }
                        Loger.d(TAG, "questionSwitch:showSelectQuestion");
                        return baseQuestionPager;
                    } else {
                        BaseLiveQuestionPager baseQuestionPager = showMulitSelectQuestion(videoQuestionLiveEntity1);
                        if (voiceAnswerPager != null) {
                            stopVoiceAnswerPager();
                        }
                        Loger.d(TAG, "questionSwitch:showMulitSelectQuestion");
                        return baseQuestionPager;
                    }
                } else if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.getvQuestionType())) {
                    BaseLiveQuestionPager baseQuestionPager = showFillBlankQuestion(videoQuestionLiveEntity1);
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    Loger.d(TAG, "questionSwitch:showFillBlankQuestion");
                    return baseQuestionPager;
                }
            }
            return null;
        }

        @Override
        public void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, final OnQuestionGet onQuestionGet) {
//            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
//            mLiveBll.getQuestion(videoQuestionLiveEntity1, new AbstractBusinessDataCallBack() {
//
//                @Override
//                public void onDataSucess(Object... objData) {
//                    onQuestionGet.onQuestionGet(videoQuestionLiveEntity1);
//                }
//            });
        }

        @Override
        public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, OnAnswerReslut answerReslut) {
//            mLiveBll.liveSubmitTestAnswer((VideoQuestionLiveEntity) videoQuestionLiveEntity, mVSectionID, result, true, answerReslut);
            VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
            sendQuestionResultVoice(answer, result, sorce, mQuestionEntity, isSubmit, voiceTime, isRight, answerReslut);
        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(VoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            rlQuestionContent.removeView(answerPager.getRootView());
        }
    };

    /**
     * 填空题
     */
    private BaseLiveQuestionPager showFillBlankQuestion(VideoQuestionEntity mQuestionEntity) {
        mVideoCourseQuestionPager = new QuestionFillInBlankLivePager(LivePlayBackVideoActivity.this, mQuestionEntity);
        mVideoCourseQuestionPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(mVideoCourseQuestionPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
        return mVideoCourseQuestionPager;
    }

    /** 显示选择题 */
    public BaseLiveQuestionPager showSelectQuestion(VideoQuestionEntity mQuestionEntity) {
        QuestionSelectLivePager questionSelectPager = new QuestionSelectLivePager(LivePlayBackVideoActivity.this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
        return questionSelectPager;
    }

    /** 显示选择题 */
    public BaseLiveQuestionPager showMulitSelectQuestion(VideoQuestionEntity mQuestionEntity) {
        QuestionMulitSelectLivePager questionSelectPager = new QuestionMulitSelectLivePager(LivePlayBackVideoActivity
                .this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
        return questionSelectPager;
    }

    /** 显示主观题 */
    private void showSubjectiveQuestion() {
        QuestionSubjectivePager questionSelectPager = new QuestionSubjectivePager(LivePlayBackVideoActivity
                .this,
                mQuestionEntity);
        questionSelectPager.setPutQuestion(new PutQuestion() {
            @Override
            public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result) {
                VideoQuestionEntity mQuestionEntity = (VideoQuestionEntity) videoQuestionLiveEntity;
                sendQuestionResult(result, mQuestionEntity);
            }
        });
        rlQuestionContent.removeAllViews();
        rlQuestionContent.addView(questionSelectPager.getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        rlQuestionContent.setVisibility(View.VISIBLE);
    }

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
            BaseVideoQuestionEntity questionEntity = ((PlaybackVideoEvent.OnQuesionDown) event)
                    .getVideoQuestionEntity();
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
            getPopupWindow();
            mMediaController.setWindowLayoutType();
            mMediaController.release();
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
        }
    }

    /**
     * 发送试题答案
     *
     * @param result
     */
    private void sendQuestionResult(String result, final VideoQuestionEntity questionEntity) {
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        AbstractBusinessDataCallBack callBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                PlaybackVideoEvent.OnAnswerReslut onAnswerReslut = (PlaybackVideoEvent.OnAnswerReslut) objData[0];
                VideoResultEntity entity = onAnswerReslut.getVideoResultEntity();
                VideoQuestionEntity questionEntity = onAnswerReslut.getQuestionEntity();
                questionEntity.setAnswered(true);
                rlQuestionContent.removeAllViews();
                if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(questionEntity.getvQuestionType())) {
                    MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                    subjectResultPager = new SubjectResultPager(LivePlayBackVideoActivity.this, LivePlayBackVideoActivity.this,
                            "https://live.xueersi.com/Live/subjectiveTestAnswerResult/" + mVideoEntity.getLiveId(),
                            userInfoEntity.getStuId(), mVideoEntity.getLiveId(), questionEntity.getvQuestionID());
                    rlQuestionContent.addView(subjectResultPager.getRootView());
                    if (vPlayer != null) {
                        vPlayer.pause();
                    }
                } else {
                    questionViewGone("sendQuestionResult");
                    answerResultChk(questionEntity, entity, false);
                }
                if (voiceAnswerPager != null) {
                    stopVoiceAnswerPager();
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == 1) {
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 15, 15, mQuestionEntity);
                    mPlayVideoControlHandler.sendMessage(msg);
                    seekTo(questionEntity.getvEndTime() * 1000);
                    start();
                }
            }
        };
        lectureLivePlayBackBll.saveQuestionResult(loadEntity, questionEntity, result, mVideoEntity.getLiveId(), mVideoEntity.getvLivePlayBackType(), false, false, callBack);
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_ANSWER,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    /**
     * 发送试题答案
     *
     * @param result
     * @param isSubmit
     * @param answerReslut
     */
    private void sendQuestionResultVoice(String answer, String result, int sorce, final VideoQuestionEntity questionEntity, String isSubmit, double voiceTime, boolean isRight, final QuestionSwitch.OnAnswerReslut answerReslut) {
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        AbstractBusinessDataCallBack callBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                PlaybackVideoEvent.OnAnswerReslut onAnswerReslut = (PlaybackVideoEvent.OnAnswerReslut) objData[0];
                VideoResultEntity entity = onAnswerReslut.getVideoResultEntity();
                VideoQuestionEntity questionEntity = onAnswerReslut.getQuestionEntity();
                questionEntity.setAnswered(true);
                rlQuestionContent.removeAllViews();
                questionViewGone("sendQuestionResultVoice");
                if (voiceAnswerPager != null) {
                    stopVoiceAnswerPager();
                }
                if (answerReslut != null) {
                    Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 14, 14, mQuestionEntity);
                    mPlayVideoControlHandler.sendMessage(msg);
                    answerReslut.onAnswerReslut(questionEntity, entity);
                    seekTo(questionEntity.getvEndTime() * 1000);
                    start();
                }
                answerResultChk(questionEntity, entity, true);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (answerReslut != null) {
                    answerReslut.onAnswerFailure();
                }
                if (errStatus == 1) {
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 15, 15, mQuestionEntity);
                    mPlayVideoControlHandler.sendMessage(msg);
                    seekTo(questionEntity.getvEndTime() * 1000);
                    start();
                }
            }
        };
        if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == questionEntity.getvCategory()) {
            JSONObject answerObj = new JSONObject();
            JSONArray answerAnswer = new JSONArray();
            try {
                answerObj.put("id", questionEntity.getvQuestionID());
                answerObj.put("answer", answer);
                if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity.getVoiceQuestiontype())) {
                    answerObj.put("useranswer", sorce);
                } else {
                    answerObj.put("useranswer", result + ":" + sorce);
                }
                answerObj.put("type", questionEntity.getvQuestionType());
                answerObj.put("url", "");
                answerObj.put("voiceTime", "" + voiceTime);
                answerAnswer.put(answerObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String testAnswer = "";
            testAnswer = answerAnswer.toString();
            lectureLivePlayBackBll.saveQuestionH5Result(loadEntity, questionEntity, testAnswer, mVideoEntity.getLiveId(), isSubmit, questionEntity.getvQuestionType(), voiceTime, isRight, callBack);
        } else {
            String testAnswer;
            if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity.getvQuestionType())) {
//                testAnswer = "" + sorce;
                testAnswer = "A";
            } else {
//                testAnswer = result + ":" + sorce;
                testAnswer = result;
            }
            Loger.d(TAG, "saveQuestionResult:testAnswer=" + testAnswer);
            lectureLivePlayBackBll.saveQuestionResult(loadEntity, questionEntity, testAnswer, mVideoEntity.getLiveId(), mVideoEntity.getvLivePlayBackType(), true, isRight, callBack);
        }
        XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_ANSWER,
                XesMobAgent.XES_VIDEO_INTERACTIVE);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (islocal) {
            return;
        }
        if (event.netWorkType == NetWorkHelper.MOBILE_STATE) {
            if (AppBll.getInstance().getAppInfoEntity().isNotificationOnlyWIFI()) {
                EventBus.getDefault().post(new AppEvent.OnlyWIFIEvent());
            } else if (AppBll.getInstance().getAppInfoEntity().isNotificationMobileAlert()) {
                EventBus.getDefault().post(new AppEvent.NowMobileEvent());
            }
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
        stopShowRefresyLayout();
    }

    /**
     * 开启了3G/4G提醒
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-10-12 下午1:49:22
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onNowMobileEvent(AppEvent.NowMobileEvent event) {
        if (mIsShowMobileAlert) {
            mIsShowMobileAlert = false;
            boolean pause = false;
            final boolean initialized = isInitialized();
            if (initialized) {
                if (vPlayer.isPlaying()) {
                    vPlayer.pause();
                    pause = true;
                }
            }
            final boolean finalPause = pause;
            Loger.i(TAG, "onNowMobileEvent:initialized=" + initialized + ",pause=" + pause);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(LivePlayBackVideoActivity
                            .this, mBaseApplication, false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onUserBackPressed();
                        }
                    });
                    cancelDialog.setVerifyBtnListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Loger.i(TAG, "onNowMobileEvent:onClick:initialized=" + initialized + ",finalPause=" +
                                    finalPause);
                            if (initialized) {
                                if (finalPause) {
                                    if (vPlayer != null) {
                                        vPlayer.start();
                                    }
                                }
                            } else {
                                if (StringUtils.isSpace(mWebPath)) {
                                    XESToastUtils.showToast(LivePlayBackVideoActivity.this, "视频资源错误，请您尝试重新播放课程");
                                    onUserBackPressed();
                                } else {
                                    playNewVideo(Uri.parse(mWebPath), mSectionName);
                                }
                            }
                        }
                    });
                    cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo
                            ("您当前使用的是3G/4G网络，是否继续观看？",
                                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
                }
            });
        }
    }

    /**
     * 创建弹起互动题按钮
     */
    protected void initPopuptWindow() {
        Loger.d(TAG, "initPopuptWindow");
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_visible, null, false);
        // 创建PopupWindow
        mPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.getContentView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        RelativeLayout rlQuestionUp = (RelativeLayout) popupWindow_view.findViewById(R.id.rl_pop_question_visible);
        RelativeLayout rlWindowDismiss = (RelativeLayout) popupWindow_view.findViewById(R.id
                .rl_pop_question_visible_dismiss);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setOutsideTouchable(true);
        // 试题显示
        rlQuestionUp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 关闭试题提示栏
                getPopupWindow();
                // 立即显示试题
                showQestion();
            }
        });
        // 试题提示栏消失监听
        rlWindowDismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 试题栏消失，视频控制栏显示
                getPopupWindow();
                // 试题消失
                questionViewGone("initPopuptWindow");

            }
        });
        // 这里是位置显示方式,在屏幕底部
        mPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        rlQuestionContent.removeAllViews();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mIsBackDismiss) {
                    Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 7, 7, mQuestionEntity);
                    mPlayVideoControlHandler.sendMessage(msg);
                }
                mIsBackDismiss = true;
            }
        });
    }

    /***
     * 获取PopupWindow实例
     */
    private void getPopupWindow() {
        if (null != mPopupWindow) {
            mIsBackDismiss = false;
            mPopupWindow.dismiss();
            mPopupWindow = null;
            return;
        } else {
            initPopuptWindow();
        }
    }

    /**
     * 试题布局隐藏
     *
     * @param method
     */
    private void questionViewGone(final String method) {
        Loger.d(TAG, "questionViewGone:method=" + method);
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Loger.d(TAG, "questionViewGone2");
                mIsShowQuestion = false;
                if (rlQuestionContent != null) {
                    if (subjectResultPager != null) {
                        for (int i = 0; i < rlQuestionContent.getChildCount(); i++) {
                            View child = rlQuestionContent.getChildAt(0);
                            if (child != subjectResultPager.getRootView()) {
                                rlQuestionContent.removeViewAt(i);
                                i--;
                            }
                        }
                    } else {
                        beforeAttach = "questionViewGone:method=" + method;
                        attachMediaController();
                        rlQuestionContent.removeAllViews();
                    }
                }
            }
        }, 1000);
    }

    /**
     * 红包布局隐藏
     */
    private void redPacketViewGone() {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mQuestionEntity = null;
                mIsShowRedpacket = false;
                beforeAttach = "redPacketViewGone";
                attachMediaController();
            }
        }.sendEmptyMessageDelayed(0, 1000); // 延迟1秒钟消失
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final VideoQuestionEntity mQuestionEntity) {
        mIsShowRedpacket = true;
        // 如果视频控制栏显示
        if (mMediaController != null) {
            mMediaController.release();
        }

        mRedPacketDialog.setRedPacketConfirmListener(new OnClickListener() {
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
                        lectureLivePlayBackBll.getLivePlayRedPacket(loadEntity, mVideoEntity.getLiveId(), mRedPacketId);
                    }
                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil
                                    .REDPACKET_GRAB,
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
     * 获取互动题
     *
     * @param playPosition
     */
    private void getPlayQuetion(int playPosition) {
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        int startTime, endTime;

        boolean hasQuestionShow = false;
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
            if (videoQuestionEntity.isAnswered()) {
                continue;
            }
            startTime = videoQuestionEntity.getvQuestionInsretTime();
            endTime = videoQuestionEntity.getvEndTime();
            // 红包只有开始时间
            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {//语音评测。在那个点弹出
                    // 在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
//                    if (startTime == playPosition) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        break;
                    }
                } else {
                    // 互动题在开始时间和结束时间之间
                    if (startTime <= playPosition && playPosition < endTime) {
                        mQuestionEntity = videoQuestionEntity;
                        hasQuestionShow = true;
                        break;
                    }
                }
            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime <= playPosition && playPosition < endTime) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == videoQuestionEntity.getvCategory()) {
                // 在开始时间和结束时间之间
                if (startTime == playPosition) {
//                if (startTime == playPosition) {
                    mQuestionEntity = videoQuestionEntity;
                    hasQuestionShow = true;
                    break;
                }
            }
        }
//        Loger.i(TAG, "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",
// mQuestionEntity=" + (mQuestionEntity != null));
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
            } else if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
                    if (mQuestionEntity.getvEndTime() < playPosition) {
                        if (speechQuestionPlaybackPager != null) {
                            speechQuestionPlaybackPager.examSubmitAll();
                            if (vPlayer != null) {
                                vPlayer.pause();
                            }
                            Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
                        }
                    }
                    return;
                } else {
                    if (mQuestionEntity.getvEndTime() < playPosition) {
                        if (questionWebPager != null && mQuestionEntity.getvQuestionID().equals(questionWebPager
                                .getTestId())) {
                            questionWebPager.examSubmitAll();
                            if (vPlayer != null) {
                                vPlayer.pause();
                            }
                            Loger.i(TAG, "getPlayQuetion:examSubmitAll2:playPosition=" + playPosition);
                            return;
                        }
                    }
                }
            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
                if (mQuestionEntity.getvEndTime() < playPosition) {
                    stopH5Exam();
                }
                return;
            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
                if (mQuestionEntity.getvEndTime() < playPosition) {
                    if (englishH5CoursewarePager != null) {
                        englishH5CoursewarePager.submitData();
                        if (vPlayer != null) {
                            vPlayer.pause();
                        }
                        Loger.i(TAG, "getPlayQuetion:submitData:playPosition=" + playPosition);
                    }
                }
                return;
            }
        }
        // 如果没有互动题则移除
        if (!hasQuestionShow && mQuestionEntity != null) {
            startTime = mQuestionEntity.getvQuestionInsretTime();
            //播放器seekto的误差
            Loger.i(TAG, "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + "," +
                    "startTime=" + startTime);
            if (mQuestionEntity.isClick()) {
                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
                    return;
                }
            }
            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 8, 8, mQuestionEntity);
            mPlayVideoControlHandler.sendMessage(msg);
        }
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
        popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener
                () {
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
        popupWindow_view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAnswerPopupWindow != null) {
                    mAnswerPopupWindow.dismiss();
                }
            }
        });
        ImageView ivRedpackageLight = (ImageView) popupWindow_view.findViewById(R.id.iv_livevideo_redpackage_light);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_livevideo_light_rotate);
        ivRedpackageLight.startAnimation(animation);
        final TextView tvAutoclose = (TextView) popupWindow_view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        mPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    if (mAnswerPopupWindow != null) {
                        mAnswerPopupWindow.dismiss();
                    }
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
     * 以获取过红包
     */
    private void initRedPacketOtherResult() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答正确
     *
     * @param goldNum
     */
    private void initAnswerPartRightResult(int goldNum) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable
                .bg_pop_question_answer_type3);
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

    private void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(this, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    private void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(this, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(this, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(this, entity);
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
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(View popupWindow_view) {
        if (rlQuestionContent == null) {
            return;
        }
        // 创建PopupWindow
        mAnswerPopupWindow = new PopupWindow(popupWindow_view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT, true);
        // 这里是位置显示方式,在屏幕底部
        mAnswerPopupWindow.showAtLocation(rlQuestionContent, Gravity.BOTTOM, 0, 0);
        popupWindow_view.setOnClickListener(new OnClickListener() {

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
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mAnswerPopupWindow != null) {
                    try {
                        mAnswerPopupWindow.dismiss();
                    } catch (Exception e) {

                    }
                }
            }
        }.sendEmptyMessageDelayed(0, 3000); // 延迟3秒钟消失
    }

    /** 保存学生填空题答案 */
    private void saveQuestionAnswer(List<AnswerEntity> answerEntityLst) {
        if (mQuestionEntity != null) {
            mQuestionEntity.setAnswerEntityLst(answerEntityLst);
        }
    }

    @Override
    protected void onUserBackPressed() {
        OnClickListener onClickListener;
        if (examQuestionPlaybackPager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopExam();
                }
            };
        } else if (speechQuestionPlaybackPager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        speechQuestionPlaybackPager.stopPlayer();
                        speechQuestionPlaybackPager.jsExamSubmit();
                        stopSpeech(speechQuestionPlaybackPager, speechQuestionPlaybackPager.getId());
                    } catch (Exception e) {

                    }
                }
            };
        } else if (h5CoursewarePager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopH5Exam();
                }
            };
        } else if (englishH5CoursewarePager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopEnglishH5Exam();
                }
            };
        } else if (questionWebPager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopWebQuestion(questionWebPager, questionWebPager.getTestId());
                }
            };
        } else if (subjectResultPager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlQuestionContent.removeView(subjectResultPager.getRootView());
                    subjectResultPager = null;
                    mIsShowQuestion = false;
                    if (vPlayer != null) {
                        vPlayer.start();
                    }
                    beforeAttach = "onUserBack:subjectResultPager";
                    attachMediaController();
                }
            };
        } else if (voiceAnswerPager != null) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.onUserBack();
                        VideoQuestionEntity questionEntity = (VideoQuestionEntity) voiceAnswerPager.getBaseVideoQuestionEntity();
                        questionEntity.setAnswered(true);
                        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
                        voiceAnswerPager = null;
                        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 16, 16, mQuestionEntity);
                        mPlayVideoControlHandler.sendMessage(msg);
                        if (vPlayer != null) {
                            vPlayer.start();
                        }
                    }
                    attachMediaController();
                }
            };
        } else {
            super.onUserBackPressed();
            return;
        }
        VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(this, (BaseApplication) BaseApplication
                .getContext(), false,
                VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
        cancelDialog.setVerifyBtnListener(onClickListener);
        cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
    }

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        if (mAnswerPopupWindow != null) {
            try {
                mAnswerPopupWindow.dismiss();
                mAnswerPopupWindow = null;
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void resultComplete() {
        // 没有广告，播放完毕直接退出
        if (lecAdvertPager == null) {
            onUserBackPressed();
        }
    }

    private Handler mPlayVideoControlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_QUESTION:
                    if (resultFailed) {
                        Loger.d(TAG, "handleMessage:SHOW_QUESTION.msg=" + msg.obj + ",resultFailed=" + resultFailed);
                        return;
                    }
                    mIsShowQuestion = true;
                    if (mMediaController != null) {
                        mMediaController.setWindowLayoutType();
                        mMediaController.release();
                        Loger.d(TAG, "handleMessage:SHOW_QUESTION:msg=" + msg.obj);
                    }
                    break;
                case NO_QUESTION:
                    if (mVideoCourseQuestionPager != null) {
                        mVideoCourseQuestionPager.hideInputMode();
                    }
                    Object obj = msg.obj;
                    Loger.d(TAG, "handleMessage:NO_QUESTION=" + msg.arg1 + "," + (obj == mQuestionEntity));
                    mQuestionEntity = null;
                    questionViewGone("NO_QUESTION");
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
            }
        }
    };

    protected void onRefresh() {
        resultFailed = false;
        if (AppBll.getInstance(this).isNetWorkAlert()) {
            videoBackgroundRefresh.setVisibility(View.GONE);
            Loger.d(TAG, "onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
            if (rlQuestionContent.getChildCount() > 0) {
                rlQuestionContent.setVisibility(View.VISIBLE);
            }
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        }
//        if (AppBll.getInstance(this).isNetWorkAlert()) {
//            loadView(mLayoutVideo);
//            initView();
//            initData();
//        }
        AppBll.getInstance(mBaseApplication);
    }

    /**
     * 互动题结果解析
     *
     * @param entity
     */
    private void answerResultChk(VideoQuestionEntity questionEntity, VideoResultEntity entity, boolean isVoice) {
        // 回答正确提示
        if (entity.getResultType() == VideoResultEntity.QUE_RES_TYPE1 || entity.getResultType() == VideoResultEntity.QUE_RES_TYPE4) {
            if (isVoice) {
                String type;
                String sourcetype;
                if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == questionEntity.getvCategory()) {
                    type = questionEntity.getVoiceQuestiontype();
                    sourcetype = "h5ware";
                } else {
                    type = questionEntity.getvQuestionType();
                    sourcetype = "h5test";
                }
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                    initSelectAnswerRightResultVoice(entity);
                } else {
                    initFillinAnswerRightResultVoice(entity);
                }
                StableLogHashMap logHashMap = new StableLogHashMap("showResultDialog");
                logHashMap.put("testid", "" + questionEntity.getvQuestionID());
                logHashMap.put("sourcetype", sourcetype).addNonce(questionEntity.nonce);
                logHashMap.addExY().addExpect("0").addSno("5").addStable("1");
                umsAgentDebug3(voicequestionEventId, logHashMap.getData());
            } else {
                initAnswerRightResult(entity.getGoldNum());
            }
            // 回答错误提示
        } else if (entity.getResultType() == VideoResultEntity.QUE_RES_TYPE2) {
            if (isVoice) {
                String type;
                if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == questionEntity.getvCategory()) {
                    type = questionEntity.getVoiceQuestiontype();
                } else {
                    type = questionEntity.getvQuestionType();
                }
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                    initSelectAnswerWrongResultVoice(entity);
                } else {
                    initFillAnswerWrongResultVoice(entity);
                }
            } else {
                initAnswerWrongResult();
            }
            // 填空题部分正确提示
        } else if (entity.getResultType() == VideoResultEntity.QUE_RES_TYPE3) {
            initAnswerPartRightResult(entity.getGoldNum());
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
            answerResultChk(mQuestionEntity, entity, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSpeechEval(String id, OnSpeechEval onSpeechEval) {
        lectureLivePlayBackBll.getSpeechEval(mVideoEntity.getLiveId(), id, onSpeechEval);
    }

    @Override
    public void stopSpeech(BaseSpeechAssessmentPager pager, String num) {
        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 9, 9, mQuestionEntity);
        mPlayVideoControlHandler.sendMessage(msg);
        if (speechQuestionPlaybackPager != null) {
            rlQuestionContent.removeView(speechQuestionPlaybackPager.getRootView());
            speechQuestionPlaybackPager = null;
        }
        if (mQuestionEntity != null && mIsShowQuestion) {
            seekTo(mQuestionEntity.getvEndTime() * 1000);
            start();
        }
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval
            onSpeechEval) {
        lectureLivePlayBackBll.sendSpeechEvalResult(mVideoEntity.getLiveId(), id, stuAnswer, times, entranceTime,
                onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval) {
        lectureLivePlayBackBll.sendSpeechEvalResult2(mVideoEntity.getLiveId(), id, stuAnswer,
                onSpeechEval);
    }

    @Override
    public void onSpeechSuccess(String num) {

    }

    @Override
    public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
        lectureLivePlayBackBll.speechEval42IsAnswered(mVideoEntity.getLiveId(), num, isAnswered);
    }

    public void stopExam() {
        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 10, 10, mQuestionEntity);
        mPlayVideoControlHandler.sendMessage(msg);
        examQuestionPlaybackPager = null;
        if (mQuestionEntity != null && mIsShowQuestion) {
            seekTo(mQuestionEntity.getvEndTime() * 1000);
            start();
        }
    }

    public void stopH5Exam() {
        if (h5CoursewarePager != null) {
            rlQuestionContent.removeView(h5CoursewarePager.getRootView());
            h5CoursewarePager = null;
            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 11, 11, mQuestionEntity);
            mPlayVideoControlHandler.sendMessage(msg);
            start();
        }
    }

    public void stopEnglishH5Exam() {
        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 12, 12, mQuestionEntity);
        mPlayVideoControlHandler.sendMessage(msg);
        englishH5CoursewarePager = null;
        if (mQuestionEntity != null && mIsShowQuestion) {
            seekTo(mQuestionEntity.getvEndTime() * 1000);
            start();
        }
    }

    @Override
    public void stopWebQuestion(BasePager pager, String testId) {
        if (pager instanceof QuestionWebPager) {
            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 13, 13, mQuestionEntity);
            mPlayVideoControlHandler.sendMessage(msg);
            questionWebPager = null;
            if (mQuestionEntity != null && mIsShowQuestion) {
                start();
            }
        } else {
            subjectResultPager = null;
            mIsShowQuestion = false;
            if (vPlayer != null) {
                vPlayer.start();
            }
            beforeAttach = "stopWebQuestion";
            attachMediaController();
        }
    }

    private void stopVoiceAnswerPager() {
        voiceAnswerPager.stopPlayer();
        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
    }

    @Override
    public void umsAgentDebug(String eventId, Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("clits", "" + System.currentTimeMillis());
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(this, appID, eventId, mData);
    }

    public void umsAgentDebug2(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(this, appID, UmsConstants.uploadBehavior, mData);
    }

    public void umsAgentDebug3(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(this, appID, UmsConstants.uploadShow, mData);
    }

    @Override
    public void setAutoOrientation(boolean isAutoOrientation) {
        super.setAutoOrientation(isAutoOrientation);
    }

    class LivePlayBackAlertDialog extends VerifyCancelAlertDialog {

        public LivePlayBackAlertDialog() {
            super(LivePlayBackVideoActivity.this, LivePlayBackVideoActivity.this.mBaseApplication, false,
                    TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
        }

        @Override
        public void showDialog() {
            super.showDialog();
            mIsShowDialog = true;
        }

        @Override
        public void cancelDialog() {
            super.cancelDialog();
            mIsShowDialog = false;
        }
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     */
    public static void intentTo(Activity context, Bundle bundle, String where) {
        intentTo(context, bundle, where, VIDEO_REQUEST);
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, LivePlayBackVideoActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void updateIcon() {
        Log.d("zhang",TAG+":updateIcon()");
        updateLoadingImage();
        updateRefreshImage();

    }

    protected void updateLoadingImage() {
        Log.d("zhang",TAG+":updateLoadingImage()");
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null ){
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
                ImageLoader.with(this).load(loadingNoClickUrl).into(ivLoading);
        }
    }
}
