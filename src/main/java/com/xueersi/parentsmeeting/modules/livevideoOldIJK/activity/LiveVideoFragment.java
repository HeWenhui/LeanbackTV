package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.switchflow.SwitchFlowView;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.business.SpeechBulletScreenIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.presenter.ChineseSpeechBulletScreenIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.presenter.EnglishSpeechBulletIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveVoteBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RankBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.business.ChinesePkBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.EvaluateTeacherBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.TripleScreenBasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone.GoldMicroPhoneBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.learnreport.business.LearnReportIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business.NBH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.business.ArtsPraiseListBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.business.PraiseInteractionBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.presenter.PraiseListIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.AnswerRankIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.ArtsAnswerResultBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.ChsAnswerResultBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.business.RedPackageIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.remark.business.LiveRemarkIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.business.RollCallIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechfeedback.business.SpeechCollectiveIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business.StudyReportBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.switchflow.SwitchFlowBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.switchflow.SwitchFlowRoutePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.switchflow.SwitchRouteSuccessDialog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.understand.business.UnderstandIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videoaudiochat.business.VideoAudioChatIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business.VideoChatIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.worddictation.business.WordDictationIRCBll;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoFragment extends LiveFragmentBase implements VideoAction, BaseLiveMessagePager.OnMsgUrlClick {
    private String TAG = "LiveVideoFragment";
    Logger logger = LiveLoggerFactory.getLogger(TAG);
    private int useSkin;

    public LiveVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_new;
    }

    protected RelativeLayout bottomContent;
    protected RelativeLayout rlMessageBottom;
    protected String vStuCourseID;
    protected String courseId;
    /** 小学英语 */
    private boolean isSmallEnglish;
    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public boolean IS_SCIENCE = true;
    /** 是不是文科 */
    private int isArts;

    protected BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;

    /** onPause状态不暂停视频 */
    PauseNotStopVideoIml pauseNotStopVideoIml;
    private LiveIRCMessageBll liveIRCMessageBll;
    protected String mode = LiveTopic.MODE_TRANING;
    /** 播放器的Fragment */
    protected LivePlayerFragment liveVideoPlayFragment;
    /** 是否是三分屏或者全身直播 */
    protected int pattern;
    /** 切流加载中的布局 */
//    private ConstraintLayout layoutVideoFailRetry;
    /** 切流加载中的按钮o */
    private Button btnVideoFailRetry;
    /** 是否上传切流埋点日志 */
    private boolean isSwitchUpload = false;
    /** 使用金话筒 */
    private int isGoldMicrophone;

    /** {@link #onActivityCreated(Bundle)} */
    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        boolean onVideoCreate = super.onVideoCreate(savedInstanceState);
        if (onVideoCreate) {
            isArts = activity.getIntent().getIntExtra("isArts", -1);
            isSmallEnglish = activity.getIntent().getBooleanExtra("isSmallEnglish", false);
            useSkin = activity.getIntent().getIntExtra("useSkin", 0);
            isGoldMicrophone = activity.getIntent().getIntExtra("isGoldMicrophone", 0);
            //logger.e("========>:onVideoCreate 22222229999000:");
            pattern = activity.getIntent().getIntExtra("pattern", 2);
            String mode2 = activity.getIntent().getStringExtra("mode");
            if (mode2 != null) {
                mode = mode2;
            }
            mLiveBll.addBusinessShareParam("isArts", isArts);
            createLiveVideoAction();
            liveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
            long before = System.currentTimeMillis();
            initAllBll();
            logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
            before = System.currentTimeMillis();
            addBusiness(activity);
            logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
            if ((pattern == 1)) {
                //根据不同直播显示不同加载中动画
                setLoadingView();
            }
        }
        return onVideoCreate;
    }

    /** 设置显示的加载动画 */
    protected void setLoadingView() {
        liveVideoPlayFragment = (LivePlayerFragment) getChildFragmentManager().findFragmentByTag("LivePlayerFragment");
        if (LiveVideoConfig.isSmallChinese) {
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_CHINESE_LOADING);
        } else if (LiveVideoConfig.isPrimary) {
            mLogtf.i("primary_science_loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_SCIENCE_LOADING);
        } else if (isSmallEnglish) {
            mLogtf.i("primary_english_loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_PRIMARY_ENGLISH_LOADING);
        } else {
            mLogtf.i("other loading");
            liveVideoPlayFragment.setLoadingAnimation(TripleScreenBasePlayerFragment.TRIPLE_SCREEN_MIDDLE_LOADING);
        }
        liveVideoPlayFragment.overrideHandlerCallBack();
    }

    @Override
    protected void onBusinessCreate() {
        super.onBusinessCreate();
        List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.initViewF(rlMessageBottom, bottomContent, mIsLand, mContentView);
        }
    }

    @Override
    protected void startGetInfo() {
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
        if (mGetInfo != null) {
            mode = mGetInfo.getMode();
        }
        mLiveBll.getInfo(mGetInfo);
    }

    @Override
    protected void onGlobalLayoutListener() {
        super.onGlobalLayoutListener();
        setMediaControllerBottomParam();
    }


    /** 三分屏使用新的的Loading,在{@link #loadView(int)} 中调用 */
//    @Override
//    protected LivePlayerFragment getFragment() {
//        LiveLivePlayerPlayFragment liveVideoPlayFragment = new LiveLivePlayerPlayFragment();
//        liveVideoPlayFragment.setLiveFragmentBase(this);
//        return liveVideoPlayFragment;
//    }

    /**
     * 添加 直播间内 所需的功能模块
     *
     * @param activity
     */
    private void addBusiness(Activity activity) {
        //是文科
        BllConfigEntity[] bllConfigEntities;
        if (isArts == 1) {
            bllConfigEntities = AllBllConfig.getLiveBusinessArts();
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            liveIRCMessageBll.setLiveMediaCtrTop(baseLiveMediaControllerTop);

            mLiveBll.addBusinessBll(liveIRCMessageBll);
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RankBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new ArtsPraiseListBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishSpeechBulletIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new WordDictationIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new ArtsAnswerResultBll(activity, mLiveBll));
            VideoChatIRCBll videoChatIRCBll = new VideoChatIRCBll(activity, mLiveBll);
            videoChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            videoChatIRCBll.setLiveFragmentBase(this);
            mLiveBll.addBusinessBll(videoChatIRCBll);
        } else if (isArts == 2) {
            bllConfigEntities = AllBllConfig.live_business_cn;
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            liveIRCMessageBll.setLiveMediaCtrTop(baseLiveMediaControllerTop);
            mLiveBll.addBusinessBll(liveIRCMessageBll);
            mLiveBll.addBusinessBll(new ChinesePkBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RankBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll));
//            mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new SpeechCollectiveIRCBll(activity, mLiveBll));
//            mLiveBll.addBusinessBll(new LiveRemarkIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new ChineseSpeechBulletScreenIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseListIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseInteractionBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new ChsAnswerResultBll(activity, mLiveBll));
//            mLiveBll.addBusinessBll(new StudyReportBll(activity, mLiveBll));
            int allowLinkMicNew = activity.getIntent().getIntExtra("allowLinkMicNew", 0);
            VideoChatIRCBll videoChatIRCBll = new VideoChatIRCBll(activity, mLiveBll);
            videoChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            videoChatIRCBll.setLiveFragmentBase(this);
            mLiveBll.addBusinessBll(videoChatIRCBll);
        } else {
            bllConfigEntities = AllBllConfig.getLiveBusinessScience(activity.getIntent());
            liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
            liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
            liveIRCMessageBll.setLiveMediaCtrTop(baseLiveMediaControllerTop);
            mLiveBll.addBusinessBll(liveIRCMessageBll);

            // 语文半身直播 添加 语文pk 业务类
            if (pattern == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY && useSkin == HalfBodyLiveConfig.SKIN_TYPE_CH) {
                mLiveBll.addBusinessBll(new ChinesePkBll(activity, mLiveBll));
            } else {
                mLiveBll.addBusinessBll(new TeamPkBll(activity, mLiveBll));
            }

            mLiveBll.addBusinessBll(new RollCallIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RankBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new EnglishH5CoursewareIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LearnReportIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
//            mLiveBll.addBusinessBll(new SpeechCollectiveIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new LiveRemarkIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new UnderstandIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new SpeechBulletScreenIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseListIRCBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new PraiseInteractionBll(activity, mLiveBll));
            mLiveBll.addBusinessBll(new StudyReportBll(activity, mLiveBll));
            int allowLinkMicNew = activity.getIntent().getIntExtra("allowLinkMicNew", 0);
            if (allowLinkMicNew == 1) {
                VideoAudioChatIRCBll videoAudioChatIRCBll = new VideoAudioChatIRCBll(activity, mLiveBll);
                videoAudioChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
                videoAudioChatIRCBll.setLiveFragmentBase(this);
                mLiveBll.addBusinessBll(videoAudioChatIRCBll);
            } else {
                VideoChatIRCBll videoChatIRCBll = new VideoChatIRCBll(activity, mLiveBll);
                videoChatIRCBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
                videoChatIRCBll.setLiveFragmentBase(this);
                mLiveBll.addBusinessBll(videoChatIRCBll);
            }
        }
        EvaluateTeacherBll evaluateTeacherBll = new EvaluateTeacherBll(activity, mLiveBll);
        evaluateTeacherBll.setLiveFragment(this);
        mLiveBll.addBusinessBll(evaluateTeacherBll);
        if (isGoldMicrophone == 1) {
            mLiveBll.addBusinessBll(new GoldMicroPhoneBll(activity, mLiveBll));
        }
        if ((pattern == 1)) {
            addSwitchFlowBll();
            initSwitchFlowListener();
        }
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
        for (int i = 0; i < bllConfigEntities.length; i++) {
            try {
                BllConfigEntity bllConfigEntity = bllConfigEntities[i];
                String className = bllConfigEntity.className;
                Class<? extends LiveBaseBll> clazz = (Class<? extends LiveBaseBll>) Class.forName(className);
                Constructor<? extends LiveBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBll2.class});
                LiveBaseBll liveBaseBll = constructor.newInstance(activity, mLiveBll);
                mLiveBll.addBusinessBll(liveBaseBll);
                logger.d("addBusiness:business=" + className);
            } catch (Exception e) {
                logger.d("addBusiness:business=", e);
                CrashReport.postCatchedException(e);
            }
        }
    }

    /** 加载切流的Bll */
    private SwitchFlowBll switchFlowBll;
    /** 点击重新加载按钮 */
//    private volatile boolean isSwitchReloadShow;
    /** 当前处于什么状态 */
    private int switchFlowStatus = LiveVideoAction.SWITCH_FLOW_NORMAL;

    /** 视频播放成功 */
    @Override
    protected void onPlayOpenSuccess() {
        super.onPlayOpenSuccess();
        //如果之前是正在切流的状态
        if ((pattern == 1)) {
            if (liveVideoAction != null) {//处于后台时这里容易null,所以加上非空判断
                liveVideoAction.onPlaySuccess();
            }
            if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH) {
                if (isSwitchUpload) {
                    UmsAgentManager.umsAgentCustomerBusiness(getActivity(), getActivity().getResources().getString(R.string
                            .livevideo_switch_flow_170711));
                }
                if (LiveVideoConfig.isPrimary || isSmallEnglish || LiveVideoConfig.isSmallChinese) {
                    SwitchRouteSuccessDialog switchRouteSuccessDialog = new SwitchRouteSuccessDialog(activity);
                    switchRouteSuccessDialog.updateView(nowRoutePos);
                    switchRouteSuccessDialog.showDialogAutoClose(2000);
                } else {
                    XESToastUtils.showToast(activity, "线路" + nowRoutePos + "切换成功");
                }
                mLogtf.i("route " + nowRoutePos + "(add 1) switch success");
            } else if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_RELOAD) {
                if (isSwitchUpload) {
                    UmsAgentManager.umsAgentCustomerBusiness(getActivity(), getActivity().getResources().getString(R.string
                            .livevideo_switch_flow_17079));
                }
            }
        }
        resetStatus();
    }

    /** 重置切换线路的标志位 */
    private void resetStatus() {
        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_NORMAL;
        if (liveVideoAction != null) {
            liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, 0);
        }
    }

//    private int switchFlowPos = 1;
    /** 当前处于线路哪条线路,比list中的实际多1 */
    private int nowRoutePos = 1;

    private void addSwitchFlowBll() {
        if (getSwitchFlowView() == null) {
            return;
        }
        switchFlowBll = new SwitchFlowBll(activity, mLiveBll);
        mLiveBll.addBusinessBll(switchFlowBll);
        switchFlowBll.setmView(getSwitchFlowView(), liveMediaControllerBottom,
                new SwitchFlowView.IReLoad() {
                    @Override
                    public void reLoad() {
//                        isSwitchReloadShow = true;
                        mLogtf.i("switchFlowView click reload");
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                            }
                        }
                        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_RELOAD;
                        isSwitchUpload = true;
                        //1.重新加载,显示加载中
                        rePlay(false);
                        //2. 自动切流
                        liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, nowRoutePos);
                        if (mGetInfo != null && mGetInfo.getLiveTopic() != null) {
                            mLiveVideoBll.liveGetPlayServer(mGetInfo.getLiveTopic().getMode(), false);
                        }
                    }
                },
                new SwitchFlowRoutePager.ItemClickListener() {
                    @Override
                    public void itemClick(int pos) {
                        mLogtf.i("switchFlowView click switch,click pos=" + pos);
                        switchFlowStatus = LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH;
                        isSwitchUpload = true;
                        if (!mLiveBll.isPresent()) {
                            if (mContentView.findViewById(R.id.iv_course_video_teacher_notpresent) != null) {
                                mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                            }
                        }
                        //todo 显示线路切换中的字样
                        mLiveVideoBll.playNewVideo(pos);
                        nowRoutePos = pos + 1;
                        liveVideoAction.setVideoSwitchFlowStatus(switchFlowStatus, nowRoutePos);
                        liveVideoAction.rePlay(false);
//                        liveVideoAction.setVideoSwitchFlowStatus(LiveAc);
//                        tvLoadingTint.setText("线路" + String.valueOf(pos) + "切换中...");
                    }
                });
    }

    /**
     * 获取切流的btn，
     * 在{@link #addSwitchFlowBll()}里面调用
     * {@link SwitchFlowBll}}
     */
    private SwitchFlowView getSwitchFlowView() {
//        switchFlowView =
        return liveMediaControllerBottom.getSwitchFlowView();
    }

    private void initSwitchFlowListener() {
        btnVideoFailRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.i("点击重试按钮");
                if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_RELOAD) {
                    mLogtf.i("click again btn,SWITCH_FLOW_RELOAD");
                    //1.重新加载,显示加载中
                    rePlay(false);
                    //2. 自动切流
                    if (mGetInfo != null && mGetInfo.getLiveTopic() != null) {
                        mLiveVideoBll.liveGetPlayServer(mGetInfo.getLiveTopic().getMode(), false);
                    }
                } else if (switchFlowStatus == LiveVideoAction.SWITCH_FLOW_ROUTE_SWITCH) {
                    mLogtf.i("click again btn,SWITCH_FLOW_ROUTE_SWITCH");
                    rePlay(false);
                } else {
                    mLogtf.i("click again btn,other");
                    rePlay(false);
                }
                if (!mLiveBll.isPresent()) {
                    mContentView.findViewById(R.id.iv_course_video_teacher_notpresent).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    @Override
    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        rlMessageBottom = mContentView.findViewById(R.id.rl_course_message_bottom);

//        tvLoadingTint = mContentView.findViewById(R.id.tv_course_video_loading_content);

        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        createMediaControlerTop();
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        createMediaControllerBottom();

        // TODO: 2018/10/23  添加了LayoutParams 是否会有其他异常？
        bottomContent.addView(liveMediaControllerBottom, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        android.util.Log.e("HalfBody", "====>LiveVideoFragment initView:add mediaContriller:"
                + liveMediaControllerBottom.getClass().getSimpleName());

        pattern = activity.getIntent().getIntExtra("pattern", 2);
        if ((pattern == 1)) {
            btnVideoFailRetry = mContentView.findViewById(R.id.btn_livevideo_switch_flow_retry_btn);
        }
        //如果是三分屏，则需要添加加载中的监听器
    }

    @Override
    protected void onFail(int arg1, int arg2) {
        super.onFail(arg1, arg2);
        isSwitchUpload = false;
    }

    /** 更新调度的list，{@link com.xueersi.parentsmeeting.modules.livevideoOldIJK.video.LiveGetPlayServer#liveGetPlayServer(String, boolean),}无论成功时报都会走 */
    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        super.onLiveStart(server, cacheData, modechange);
        if ((pattern == 1) && switchFlowBll != null) {
            if (server != null) {
                switchFlowBll.setListRoute(server.getPlayserver());
                logger.i(server.getPlayserver().size());
            } else {
                switchFlowBll.setListRoute(null);
                logger.i("null");
            }
        }
    }

    protected void createMediaControllerBottom() {
        Intent intent = activity.getIntent();
        LiveVideoConfig.isPrimary = intent.getBooleanExtra("isPrimary", false);
        LiveVideoConfig.isSmallChinese = intent.getBooleanExtra("isSmallChinese", false);
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

    protected void createMediaControlerTop() {
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
    }


    @Override
    public boolean initData() {
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
        logger.d("====>initAllBll:" + bottomContent);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam();
        videoFragment.setIsAutoOrientation(false);
        mLiveBll.addBusinessShareParam("videoView", videoView);
        mLiveBll.addBusinessShareParam("mMediaController", mMediaController);
        mLiveBll.addBusinessShareParam("liveMediaControllerBottom", liveMediaControllerBottom);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity);
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam() {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = liveVideoPoint.y2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLand.get()) {
            mMediaController.setControllerBottom(liveMediaControllerBottom, false);
            setMediaControllerBottomParam();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveStop) {
            mHaveStop = false;
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                return;
            }
            if (!pauseNotStopVideoIml.getPause()) {
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
                    }
                });
            }
            pauseNotStopVideoIml.setPause(false);
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
        VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
        if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
            return;
        }
        if (!pauseNotStopVideoIml.getPause()) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            if (openSuccess) {
                                mLiveVideoBll.stopPlayDuration();
                                logger.d("onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                            }
                            vPlayer.releaseSurface();
                            vPlayer.stop();
                        } else {
                            logger.d("onPause:isInitialized=false");
                        }
                        isPlay = false;
                    }
                }
            });
        }
        if (mLiveBll != null) {
            mLiveBll.onPause();
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
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        mode = mGetInfo.getMode();
        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        logger.d("onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        LiveVideoFragment.this.mode = mGetInfo.getMode();
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (isInitialized()) {
                    mLiveVideoBll.stopPlayDuration();
                    vPlayer.releaseSurface();
                    vPlayer.stop();
                }
                isPlay = false;
                if (liveVideoAction != null) {
                    liveVideoAction.onModeChange(mode, isPresent);
                }
            }
        });
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        if (liveVideoAction != null) {
            liveVideoAction.onPlayError(errorCode, playErrorCode);
        }
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     *
     * @param modechange
     */
    @Override
    public void rePlay(boolean modechange) {
        mLogtf.d("rePlay:mHaveStop=" + mHaveStop);
        if (mGetInfo == null || liveVideoAction == null) {//上次初始化尚未完成
            return;
        }
        VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
        if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
            return;
        }
        liveVideoAction.rePlay(modechange);
        mLiveVideoBll.rePlay(modechange);
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }

}
