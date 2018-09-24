package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.KeyBordAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseLiveQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSubjectResultInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.QuestionWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5Pager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.KeyboardPopWindow;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE3;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE5;

/**
 * Created by linyuqiang on 2016/9/23.
 * 互动题bll
 */
public class QuestionBll implements QuestionAction, Handler.Callback, SpeechEvalAction, BaseQuestionWebInter
        .StopWebQuestion, BaseVoiceAnswerCreat.AnswerRightResultVoice, QuestionStatic, QuestionShowReg, KeyboardUtil.OnKeyboardShowingListener, KeyboardPopWindow.KeyboardObserver, LivePagerBack {
    private String TAG = "QuestionBll";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private SpeechEvaluatorUtils mIse;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;
    private String examQuestionEventId = LiveVideoConfig.LIVE_H5_EXAM;
    private String questionEventId = LiveVideoConfig.LIVE_PUBLISH_TEST;
    private String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private VideoQuestionLiveEntity videoQuestionLiveEntity;
    private LogToFile mLogtf;
    private Activity activity;
    private QuestionHttp questionHttp;
    private LiveAndBackDebug liveAndBackDebug;
    private LiveTopic mLiveTopic;
    private BasePager curQuestionView;
    private boolean isTeamPkAllowed = false;
    private boolean webViewCloseByTeacher = false;
    /** 直播id */
    private String mVSectionID;
    /** 直播类型 */
    private int liveType;
    protected ShareDataManager mShareDataManager;
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
    /** 当前是否正在显示互动题 */
    private boolean mIsShowQuestion = false;
    /** 语音答题 */
    private BaseVoiceAnswerPager voiceAnswerPager;
    /** 创建语音答题 */
    private BaseVoiceAnswerCreat baseVoiceAnswerCreat;
    /** 语音强制提交，外层 */
    private RelativeLayout rlVoiceQuestionContent;
    /** 互动题布局 */
    private BaseLiveQuestionPager baseQuestionPager;
    LiveQuestionCreat liveQuestionCreat;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 互动题作答成功的布局 */
    private RelativeLayout rlQuestionResContent;
    /** video缓存时间 */
    private long videoCachedDuration;
    private LiveGetInfo liveGetInfo;
    /** 存互动题 */
    private static final String QUESTION = "live_question";
    /** 存试卷 */
    private static final String EXAM = "live_exam";
    /** 答题的暂存状态 */
    private HashSet<String> mQueAndBool = new HashSet<>();
    /** 答题的暂存状态-可以重复作答的 */
    private HashSet<String> mQueReAnswer = new HashSet<>();
    /** 语音答题错误 */
    private HashSet<String> mErrorVoiceQue = new HashSet<>();
    /** 试卷的暂存状态 */
    private HashSet<String> mExamAndBool = new HashSet<>();
    /** 试卷正在作答 */
    private boolean isHaveExam = false;
    /** 语音评测正在作答 */
    private boolean isHaveSpeech = false;
    /** 网页互动题正在作答 */
    private boolean isHaveWebQuestion = false;
    private BaseQuestionWebInter questionWebPager;
    /** 试卷页面 */
    private BaseExamQuestionInter examQuestionPager;
    private BaseExamQuestionCreat baseExamQuestionCreat;
    /** 语音评测页面 */
    private BaseSpeechAssessmentPager speechAssessmentPager;
    /** 语音评测页面,用户点击返回暂存 */
    private BaseSpeechAssessmentPager speechAssessmentPagerUserBack;
    private BaseSpeechCreat baseSpeechCreat;
    /** 语音评测结束后的事件 */
    private SpeechEndAction speechEndAction;
    /** 语文主观题 */
    private BaseSubjectResultInter subjectResultPager;
    private BaseSubjectResultCreat baseSubjectResultCreat;
    boolean isLand;
    AtomicBoolean isAbLand = new AtomicBoolean();
    private KeyBordAction keyBordAction;
    /** 是不是在显示互动题,结果页或者语音评测top3 */
    private boolean isAnaswer = false;
    private ArrayList<QuestionShowAction> questionShowActions = new ArrayList<>();
    private AnswerRankBll mAnswerRankBll;
    /** 智能私信业务 */
    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private VideoQuestionLiveEntity mVideoQuestionLiveEntity;
    private boolean hasQuestion;
    private boolean hasExam;
    private long submitTime;
    private boolean hasSubmit;
    private String stuCouId;
    private RolePlayAction rolePlayAction;

    public QuestionBll(Activity activity, String stuCouId) {
        ProxUtil.getProxUtil().put(activity, QuestionStatic.class, this);
        ProxUtil.getProxUtil().put(activity, QuestionShowReg.class, this);
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        this.stuCouId = stuCouId;
        liveQuestionCreat = new LiveQuestionCreat(activity, isAbLand, this);
//        KeyboardObserverReg keyboardObserverReg = ProxUtil.getProxUtil().get(activity, KeyboardObserverReg.class);
//        if (keyboardObserverReg != null) {
//            keyboardObserverReg.addKeyboardObserver(this);
//        }
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        this.liveVideoSAConfig = liveVideoSAConfig;
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public void setLiveBll(QuestionHttp mLiveBll) {
        this.questionHttp = mLiveBll;
        liveQuestionCreat.setQuestionHttp(questionHttp);
    }

    public void setLiveAutoNoticeBll(LiveAutoNoticeBll liveAutoNoticeBll) {
        mLiveAutoNoticeBll = liveAutoNoticeBll;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
        liveQuestionCreat.setmVSectionID(mVSectionID);
    }

    public void setLiveTopic(LiveTopic mLiveTopic) {
        this.mLiveTopic = mLiveTopic;
    }

    public void setShareDataManager(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
    }

    @Override
    public void setVideoCachedDuration(long videoCachedDuration) {
        this.videoCachedDuration = videoCachedDuration;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
        isTeamPkAllowed = liveGetInfo != null && "1".equals(liveGetInfo.getIsAllowTeamPk());
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        this.mIse = ise;
    }

    public void initData() {
        try {
            JSONObject jsonObject = new JSONObject(mShareDataManager.getString(QUESTION, "{}", ShareDataManager
                    .SHAREDATA_USER));
            int jsonLiveType = jsonObject.optInt("liveType");
            if (jsonLiveType == liveType) {
                String vSectionID = jsonObject.optString("vSectionID");
                if (mVSectionID.equals(vSectionID)) {
                    String testId = jsonObject.optString("testId");
                    if (!StringUtils.isSpace(testId)) {
                        mQueAndBool.add(testId);
                        mQueReAnswer.add(testId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(mShareDataManager.getString(EXAM, "{}", ShareDataManager
                    .SHAREDATA_USER));
            int jsonLiveType = jsonObject.optInt("liveType");
            if (jsonLiveType == liveType) {
                String vSectionID = jsonObject.optString("vSectionID");
                if (mVSectionID.equals(vSectionID)) {
                    String num = jsonObject.optString("num");
                    if (!StringUtils.isSpace(num)) {
                        mExamAndBool.add(num);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RelativeLayout bottomeContent;
    public void initView(RelativeLayout bottomContent, boolean isLand) {
        this.isLand = isLand;
        isAbLand.set(isLand);
        bottomeContent = bottomContent;
        //互动题
        rlQuestionContent = new RelativeLayout(activity);
        rlQuestionContent.setId(R.id.rl_livevideo_content_question);
        bottomContent.addView(rlQuestionContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (rlQuestionResContent == null) {
            rlQuestionResContent = new RelativeLayout(activity);
        } else {
            ViewGroup group = (ViewGroup) rlQuestionResContent.getParent();
            if (group != null) {
                group.removeView(rlQuestionResContent);
            }
        }
        bottomContent.addView(rlQuestionResContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (videoQuestionLiveEntity != null) {
            showQuestion(videoQuestionLiveEntity);
        }
        if (examQuestionPager != null) {
            ViewGroup group = (ViewGroup) examQuestionPager.getRootView().getParent();
            if (group != null) {
                group.removeView(examQuestionPager.getRootView());
            }
            rlQuestionContent.addView(examQuestionPager.getRootView());
        }
        if (speechAssessmentPager != null) {
            ViewGroup group = (ViewGroup) speechAssessmentPager.getRootView().getParent();
            if (group != null) {
                group.removeView(speechAssessmentPager.getRootView());
            }
            rlQuestionContent.addView(speechAssessmentPager.getRootView());
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        logger.d( "onKeyboardHeightChanged:height=" + height);
        if (baseQuestionPager != null) {
            baseQuestionPager.onKeyboardShowing(height > 0, height);
        }
    }

    @Override
    public void onKeyboardShowing(boolean isShowing) {
        if (examQuestionPager != null) {
            examQuestionPager.onKeyboardShowing(isShowing);
        }
        if (baseQuestionPager != null) {
            baseQuestionPager.onKeyboardShowing(isShowing);
        }
    }

    @Override
    public boolean isAnaswer() {
        return isAnaswer;
    }

    public void setAnaswer(boolean anaswer) {
        isAnaswer = anaswer;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_QUESTION: {
                String s = "handleMessage:SHOW_QUESTION:mIsShow=" + mIsShowQuestion;
                if (!mIsShowQuestion) {
                    mIsShowQuestion = true;
                }
                mLogtf.d(s);
                if (keyBordAction == null) {
                    keyBordAction = ProxUtil.getProxUtil().get(activity, KeyBordAction.class);
                }
                if (keyBordAction != null) {
                    keyBordAction.hideInput();
                }
            }
            break;
            case NO_QUESTION: {
                String s = "handleMessage:NO_QUESTION:mIsShow=" + mIsShowQuestion;
                if (mIsShowQuestion) {
                    mIsShowQuestion = false;
                    if (baseQuestionPager != null) {
                        baseQuestionPager.hideInputMode();
                        baseQuestionPager.onDestroy();
                        rlQuestionContent.removeView(baseQuestionPager.getRootView());
                        baseQuestionPager = null;
                    }
                    s += ",mLiveTopic=" + (mLiveTopic == null) + ",arg1=" + msg.arg1;
                    if (msg.arg1 == 1) {// 完全去掉互动题，=0的时候，暂时去掉
                        if (mLiveTopic != null) {
                            s += ",Question=" + (mLiveTopic.getVideoQuestionLiveEntity() == null);
//                            mLiveTopic.setTopic(null);
                            mLiveTopic.setVideoQuestionLiveEntity(null);
                        }
                    }
                    videoQuestionLiveEntity = null;
                    questionViewGone(msg.arg1 == 0);
                }
                mLogtf.d(s);
            }
            break;
            default:
                break;
        }
        return false;
    }

    public void setRolePlayAction(RolePlayAction rolePlayAction) {
        this.rolePlayAction = rolePlayAction;
        rolePlayAction.setOnError(new RolePlayAction.OnError() {

            @Override
            public void onError(BaseVideoQuestionEntity testId) {
                mQueAndBool.remove(testId.getvQuestionID());
                showQuestion((VideoQuestionLiveEntity) testId);
            }
        });
    }

    @Override
    public void showQuestion(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        logger.e("======> showQuestion 000000");
        setWebViewCloseByTeacher(false);
        if (videoQuestionLiveEntity == null) {
            mLogtf.d("showQuestion:noQuestion");
            if (isAnaswer) {
                onQuestionShow(false, "showQuestion");
            }
            isAnaswer = false;
            if (voiceAnswerPager != null && !voiceAnswerPager.isEnd()) {
                final BaseVoiceAnswerPager answerPager = voiceAnswerPager;
                mVPlayVideoControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        answerPager.examSubmitAll("showQuestion", "");
                    }
                });
            }
            return;
        }


        logger.e( "======> showQuestion 11111");

        //不是语音评测
        if (IS_SCIENCE && !"4".equals(videoQuestionLiveEntity.type)) {
            if (videoQuestionLiveEntity.isTestUseH5) {
                mVPlayVideoControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (questionWebPager != null && questionWebPager.getTestId().equals(videoQuestionLiveEntity
                                .getvQuestionID())) {
                            return;
                        }
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.showRankList(new ArrayList<RankUserEntity>(), XESCODE.STOPQUESTION);
                            questionHttp.sendRankMessage(XESCODE.RANK_STU_RECONNECT_MESSAGE);
                        }
                        hasQuestion = true;
                    }
                });
            }
        }
        mVideoQuestionLiveEntity = videoQuestionLiveEntity;

        logger.e( "======> showQuestion 22222:" + isAnaswer);

        if (!isAnaswer) {
            onQuestionShow(true, "showQuestion");
        }
        isAnaswer = true;
        if (this.videoQuestionLiveEntity != null) {
            mLogtf.d("showQuestion:Entity=" + this.videoQuestionLiveEntity.id + "," + videoQuestionLiveEntity.id);
        } else {
            mLogtf.d("showQuestion:Entity=" + videoQuestionLiveEntity.id);
        }
        if (mQueAndBool.contains(videoQuestionLiveEntity.id)) {
            mLogtf.d("showQuestion answer:id=" + videoQuestionLiveEntity.id);
            boolean reTry = false;
            if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionLiveEntity.type)) {
                if ("1".equals(videoQuestionLiveEntity.isAllow42)) {
                    if (mQueReAnswer.contains(videoQuestionLiveEntity.id)) {
                        mQueReAnswer.remove(videoQuestionLiveEntity.id);
                        reTry = true;
                    }
                }
            }
            if (!reTry) {
                return;
            }
        }

        logger.e( "======> showQuestion 333333:" + isAnaswer);


        if (!IS_SCIENCE && "1".equals(videoQuestionLiveEntity.getIsVoice())) {
            StableLogHashMap logHashMap = new StableLogHashMap("receiveh5test");
            logHashMap.put("sourcetype", "h5test");
            logHashMap.put("testtype", "" + videoQuestionLiveEntity.type);
            logHashMap.put("testid", "" + videoQuestionLiveEntity.id);
            logHashMap.put("stable", "2");
            umsAgentDebugSys(voicequestionEventId, logHashMap.getData());
        } else {
            Map<String, String> mData = new HashMap<>();
            mData.put("testtype", "" + videoQuestionLiveEntity.type);
            mData.put("testid", "" + videoQuestionLiveEntity.id);
            mData.put("logtype", "receiveInteractTest");
            mData.put("ish5test", "" + videoQuestionLiveEntity.isTestUseH5);
            umsAgentDebugSys(questionEventId, mData);
        }
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        //不是语音评测
        if (IS_SCIENCE && !LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionLiveEntity.type)) {
            if (videoQuestionLiveEntity.isTestUseH5) {
                mVPlayVideoControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (questionWebPager != null && questionWebPager.getTestId().equals(videoQuestionLiveEntity
                                .getvQuestionID())) {
                            return;
                        }
                        if (questionWebPager != null) {
                            mLogtf.d("showQuestion:oldTestId=" + questionWebPager.getTestId());
                            questionWebPager.onDestroy();
                            rlQuestionContent.removeView(questionWebPager.getRootView());
                            questionWebPager = null;
                        }
                        QuestionWebX5Pager questionWebPager = new QuestionWebX5Pager(activity, null, QuestionBll.this, liveGetInfo
                                .getTestPaperUrl(), liveGetInfo.getStuId(), liveGetInfo.getUname(),
                                liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                                videoQuestionLiveEntity.nonce, liveGetInfo.getIs_show_ranks(), IS_SCIENCE, stuCouId,
                                "1".equals(liveGetInfo.getIsAllowTeamPk()));
                        questionWebPager.setLivePagerBack(QuestionBll.this);
                        rlQuestionContent.addView(questionWebPager.getRootView());
                        QuestionBll.this.questionWebPager = questionWebPager;
                        setHaveWebQuestion(true);
                    }
                });
                return;
            }
        }

        logger.e( "======> showQuestion 444444:" + isAnaswer);

        if (!IS_SCIENCE && LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionLiveEntity.type)) {
            if (!StringUtils.isEmpty(videoQuestionLiveEntity.roles)) {
                baseSpeechCreat.receiveRolePlay(videoQuestionLiveEntity);
            }
        }
        logger.e("======> showQuestion 55555:" + videoQuestionLiveEntity.isNewArtsH5Courseware());
        if (videoQuestionLiveEntity.isNewArtsH5Courseware()) {
            doNewArtsAnswerQuetion(videoQuestionLiveEntity);
        } else {
            doArtsAnswerQuestion(videoQuestionLiveEntity);
        }
        mVPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
    }

    /**
     * 文科课件平台改版后 文科答题 处理逻辑
     * 1 选择 填空  不再走本地  统一由走h5
     * 2 rolepaly，语音答题等 走新接口
     *
     * @param videoQuestionLiveEntity
     */
    private void doNewArtsAnswerQuetion(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                // 文科选择 填空题
                if ("0".equals(videoQuestionLiveEntity.type) || "1".equals(videoQuestionLiveEntity.type)
                        || "2".equals(videoQuestionLiveEntity.type)) {
                    mVPlayVideoControlHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (questionWebPager != null && questionWebPager.getTestId().equals(videoQuestionLiveEntity
                                    .getvQuestionID())) {
                                return;
                            }
                            if (questionWebPager != null) {
                                mLogtf.d("showQuestion:oldTestId=" + questionWebPager.getTestId());
                                questionWebPager.onDestroy();
                                rlQuestionContent.removeView(questionWebPager.getRootView());
                                questionWebPager = null;
                            }
                            logger.e("====>" + "type:" + videoQuestionLiveEntity.type);
                            QuestionWebX5Pager questionWebPager = new QuestionWebX5Pager(activity, QuestionBll.this,
                                    videoQuestionLiveEntity,liveGetInfo.getId());
                            questionWebPager.setLivePagerBack(QuestionBll.this);
                            rlQuestionContent.addView(questionWebPager.getRootView());
                            QuestionBll.this.questionWebPager = questionWebPager;
                            setHaveWebQuestion(true);
                            activity.getWindow().getDecorView().requestLayout();
                            activity.getWindow().getDecorView().invalidate();
                        }
                    });
                } else if("4".equals(videoQuestionLiveEntity.type) || "5".equals(videoQuestionLiveEntity.type) || "6".equals(videoQuestionLiveEntity.type)){
                    String id = videoQuestionLiveEntity.id;
                    if (speechAssessmentPager != null && id.equals(speechAssessmentPager.getId())) {
                        return;
                    }
                    if (speechAssessmentPager != null) {
                        mLogtf.d("showQuestion:examSubmitAll:id=" + speechAssessmentPager.getId());
                        speechAssessmentPager.onDestroy();
                        speechAssessmentPager.examSubmitAll();
                        if (speechAssessmentPager != null) {
                            rlQuestionContent.removeView(speechAssessmentPager.getRootView());
                        }
                    }
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.request(new AudioRequest.OnAudioRequest() {
                            @Override
                            public void requestSuccess() {
                                if (voiceAnswerPager != null) {
                                    voiceAnswerPager.setAudioRequest();
                                }
                            }
                        });
                    } else {
                        if (voiceAnswerPager != null) {
                            voiceAnswerPager.setAudioRequest();
                        }
                    }
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    if ("1".equals(videoQuestionLiveEntity.isAllow42)) {
                        /** 已经作答 */
                        boolean haveAnswer = mQueAndBool.contains(videoQuestionLiveEntity.id);
                        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
                        String learning_stage = null;
                        if (studentLiveInfo != null) {
                            learning_stage = studentLiveInfo.getLearning_stage();
                        }
                        BaseSpeechAssessmentPager speechAssAutoPager = baseSpeechCreat.createSpeech(activity,
                                liveGetInfo.getId(), videoQuestionLiveEntity.nonce,
                                videoQuestionLiveEntity,
                                haveAnswer, QuestionBll.
                                        this, lp, liveGetInfo, learning_stage);
                        speechAssessmentPager = speechAssAutoPager;
                        speechAssessmentPager.setIse(mIse);
                        speechAssessmentPager.initData();

                    } else {
                        logger.e("走人机000");
                        if ("1".equals(videoQuestionLiveEntity.multiRolePlay)) {
                            if (rolePlayAction != null) {
                                mQueAndBool.add(id);
                                rolePlayAction.teacherPushTest(videoQuestionLiveEntity);
                                return;
                            }
                            logger.e( "走人机010");
                        }
                        if (rolePlayAction != null && id.equals(rolePlayAction.getQuestionId())) {
                            return;
                        }
                        if (rolePlayAction != null) {
                            //走人机也通知多人的关掉WebSocket
                            rolePlayAction.onGoToRobot();
                        }
                        logger.e("走人机111");
                        speechAssessmentPager = baseSpeechCreat.createRolePlay(activity, liveGetInfo,
                                videoQuestionLiveEntity,
                                id, QuestionBll.this, stuCouId);
                        speechAssessmentPager.setIse(mIse);
                        speechAssessmentPager.initData();
                        logger.i("RolePlayerDemoTest", "走人机");
                    }
                    setHaveSpeech(true);
                    rlQuestionContent.addView(speechAssessmentPager.getRootView(), lp);
                } else if(LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity.type)){
                    showSubjectiveQuestion(videoQuestionLiveEntity);
                }
            }
        });

    }

    /**
     * 旧版文科课件本地 答题
     *
     * @param videoQuestionLiveEntity
     */
    private void doArtsAnswerQuestion(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                mLogtf.d("showQuestion:type=" + videoQuestionLiveEntity.type);
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice()) && !mErrorVoiceQue.contains
                            (videoQuestionLiveEntity.id)) {
                        try {
                            showVoiceAnswer(videoQuestionLiveEntity);
                        } catch (Exception e) {
                            mLogtf.d("showQuestion:showVoiceAnswer.error1=" + e.getMessage());
                            mErrorVoiceQue.add(videoQuestionLiveEntity.id);
                            showQuestion(videoQuestionLiveEntity);
                            return;
                        }
                    } else {
                        if ("1".equals(videoQuestionLiveEntity.choiceType)) {
                            showSelectQuestion(videoQuestionLiveEntity);
                        } else if ("2".equals(videoQuestionLiveEntity.choiceType)) {
                            showMulitSelectQuestion(videoQuestionLiveEntity);
                        } else {
                            XESToastUtils.showToast(activity, "不支持的试题类型，可能需要升级版本");
                            return;
                        }
                    }
                } else if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity.type)) {
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice()) && !mErrorVoiceQue.contains
                            (videoQuestionLiveEntity.id)) {
                        try {
                            showVoiceAnswer(videoQuestionLiveEntity);
                        } catch (Exception e) {
                            mLogtf.d("showQuestion:showVoiceAnswer.error2=" + e.getMessage());
                            mErrorVoiceQue.add(videoQuestionLiveEntity.id);
                            showQuestion(videoQuestionLiveEntity);
                            return;
                        }
                    } else {
                        showFillBlankQuestion(videoQuestionLiveEntity);
                    }
                } else if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity.type)) {
                    showSubjectiveQuestion(videoQuestionLiveEntity);
                } else if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionLiveEntity.type)) {
                    String id = videoQuestionLiveEntity.id;
                    if (speechAssessmentPager != null && id.equals(speechAssessmentPager.getId())) {
                        return;
                    }
                    if (speechAssessmentPager != null) {
                        mLogtf.d("showQuestion:examSubmitAll:id=" + speechAssessmentPager.getId());
                        speechAssessmentPager.onDestroy();
                        speechAssessmentPager.examSubmitAll();
                        if (speechAssessmentPager != null) {
                            rlQuestionContent.removeView(speechAssessmentPager.getRootView());
                        }
                    }
                    AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                    if (audioRequest != null) {
                        audioRequest.request(new AudioRequest.OnAudioRequest() {
                            @Override
                            public void requestSuccess() {
                                if (voiceAnswerPager != null) {
                                    voiceAnswerPager.setAudioRequest();
                                }
                            }
                        });
                    } else {
                        if (voiceAnswerPager != null) {
                            voiceAnswerPager.setAudioRequest();
                        }
                    }
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    if ("1".equals(videoQuestionLiveEntity.isAllow42)) {
                        /** 已经作答 */
                        boolean haveAnswer = mQueAndBool.contains(videoQuestionLiveEntity.id);
                        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
                        String learning_stage = null;
                        if (studentLiveInfo != null) {
                            learning_stage = studentLiveInfo.getLearning_stage();
                        }
                        BaseSpeechAssessmentPager speechAssAutoPager = baseSpeechCreat.createSpeech(activity,
                                liveGetInfo.getId(), videoQuestionLiveEntity.nonce,
                                videoQuestionLiveEntity,
                                haveAnswer, QuestionBll.
                                        this, lp, liveGetInfo, learning_stage);
                        speechAssessmentPager = speechAssAutoPager;
                        speechAssessmentPager.setIse(mIse);
                        speechAssessmentPager.initData();

                    } else {
                        if ("1".equals(videoQuestionLiveEntity.multiRolePlay)) {
                            if (rolePlayAction != null) {
                                mQueAndBool.add(id);
                                rolePlayAction.teacherPushTest(videoQuestionLiveEntity);
                                return;
                            }
                        }
                        if (rolePlayAction != null && id.equals(rolePlayAction.getQuestionId())) {
                            return;
                        }
                        if (rolePlayAction != null) {
                            //走人机也通知多人的关掉WebSocket
                            rolePlayAction.onGoToRobot();
                        }
                        speechAssessmentPager = baseSpeechCreat.createRolePlay(activity, liveGetInfo,
                                videoQuestionLiveEntity,
                                id, QuestionBll.this, stuCouId);
                        speechAssessmentPager.setIse(mIse);
                        speechAssessmentPager.initData();
                        logger.i( "走人机");
                    }
                    setHaveSpeech(true);
                    rlQuestionContent.addView(speechAssessmentPager.getRootView(), lp);
                } else {
                    XESToastUtils.showToast(activity, "不支持的试题类型，可能需要升级版本");
                    return;
                }
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    /**
     * 互动题结果解析
     * 返回类型,1,2,3,4,5
     * 1回答正确有金币
     * 2回答错误
     * 3部分正确，有金币
     * 4回答正确无金币
     * 5部分正确，无金币
     *
     * @param liveBasePager
     * @param baseVideoQuestionEntity
     * @param entity
     */
    @Override
    public void onAnswerReslut(LiveBasePager liveBasePager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        BaseLiveQuestionPager tempBaseQuestionPager = null;
        /** 语音答题 */
        BaseVoiceAnswerPager tempVoiceAnswerPager = null;
        if (liveBasePager instanceof BaseLiveQuestionPager) {
            tempBaseQuestionPager = (BaseLiveQuestionPager) liveBasePager;
        } else if (liveBasePager instanceof BaseVoiceAnswerPager) {
            tempVoiceAnswerPager = (BaseVoiceAnswerPager) liveBasePager;
        }
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        boolean isSuccess = false;
        int type = 0;
        if (entity != null) {// 提交成功，否则是已经答过题了
            // 发送已答过这道题的标识
            EventBus.getDefault().post(new ArtsAnswerResultEvent(videoQuestionLiveEntity.id,2));
            boolean isVoice = entity.isVoice();
            if (!isVoice && LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity.type)) {
                String url = liveGetInfo.getSubjectiveTestAnswerResult() + "?stuId=" + liveGetInfo.getStuId() +
                        "&testId=" + videoQuestionLiveEntity.getvQuestionID();
                logger.d( "showQuestion:url=" + url);
                subjectResultPager = baseSubjectResultCreat.creat(activity, this,
                        liveGetInfo.getSubjectiveTestAnswerResult(),
                        liveGetInfo.getStuId(), liveGetInfo.getId(), videoQuestionLiveEntity, stuCouId);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                rlQuestionContent.addView(subjectResultPager.getRootView(), params);
            } else {
                type = entity.getResultType();
                if (entity.getIsAnswer() == 1) {
                    XESToastUtils.showToast(activity, "您已经答过此题");
                } else {
                    if (isVoice) {
                        isSuccess = baseVoiceAnswerCreat.onAnswerReslut(activity, this, voiceAnswerPager,
                                baseVideoQuestionEntity, entity);
                        StableLogHashMap logHashMap = new StableLogHashMap("showResultDialog");
                        logHashMap.put("testid", "" + baseVideoQuestionEntity.getvQuestionID());
                        logHashMap.put("sourcetype", "h5test").addNonce(baseVideoQuestionEntity.nonce);
                        logHashMap.addExY().addExpect("0").addSno("5").addStable("1");
                        umsAgentDebugPv(voicequestionEventId, logHashMap.getData());
                    } else {
                        // 回答正确提示
                        if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                            initAnswerRightResult(entity);
                            isSuccess = true;
                            // 回答错误提示
                        } else if (entity.getResultType() == QUE_RES_TYPE2) {
                            initAnswerWrongResult();
                            // 填空题部分正确提示
                        } else if (entity.getResultType() == QUE_RES_TYPE3 || entity.getResultType() == QUE_RES_TYPE5) {
                            initAnswerPartRightResult(entity);
                            isSuccess = true;
                        }
                    }
                }
            }
            JSONObject object = new JSONObject();
            try {
                object.put("liveType", liveType);
                object.put("vSectionID", mVSectionID);
                object.put("testId", entity.getTestId());
                mShareDataManager.put(QUESTION, object.toString(), ShareDataManager.SHAREDATA_USER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isSuccess) {
            if (tempBaseQuestionPager != null) {
                tempBaseQuestionPager.onSubSuccess();
            }
            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    questionHttp.getStuGoldCount();

                    // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
                    //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));
                }
            }, 5000);
        } else {
            if (tempBaseQuestionPager != null) {
                tempBaseQuestionPager.onSubFailure();
            }
        }
        String testId = "";
        if (entity != null) {
            testId = entity.getTestId();
        }
        if (videoQuestionLiveEntity != null) {
            mLogtf.d("onAnswerReslut:id=" + videoQuestionLiveEntity.id + ",testId=" + testId + ",type=" + type);
            if (StringUtils.isSpace(testId)) {
                mQueAndBool.add(videoQuestionLiveEntity.id);
            } else {
                mQueAndBool.add(testId);
            }
        } else {
            mLogtf.d("onAnswerReslut:id=null,testId=" + testId + ",type=" + type);
        }
        if (tempVoiceAnswerPager != null) {
            stopVoiceAnswerPager(tempVoiceAnswerPager);
        }
        boolean same = true;
        if (tempBaseQuestionPager != null) {
            if (tempBaseQuestionPager != baseQuestionPager) {
                same = false;
            }
        } else if (tempVoiceAnswerPager != null) {
            if (tempVoiceAnswerPager != voiceAnswerPager) {
                same = false;
            }
        }
        if (same) {
            if (mLiveTopic != null) {
                mLiveTopic.setVideoQuestionLiveEntity(null);
            }
            questionViewGone(true);
        }
    }

    @Override
    public void onAnswerFailure() {
        if (baseQuestionPager != null) {
            baseQuestionPager.onSubFailure();
        }
    }

    @Override
    public void onStopQuestion(String ptype, final String nonce) {
        logger.i("=====questionbll  question stop");
        setWebViewCloseByTeacher(true);
        boolean havePager = false;
        boolean oldisAnaswer = isAnaswer;
        isAnaswer = false;
        if (rolePlayAction != null && mVideoQuestionLiveEntity != null) {
            if (mVideoQuestionLiveEntity.id.equals(rolePlayAction.getQuestionId())) {
                rolePlayAction.onStopQuestion(mVideoQuestionLiveEntity, nonce);
            }
        }
        if (voiceAnswerPager != null) {
            havePager = true;
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.examSubmitAll("onStopQuestion", nonce);
//                        stopVoiceAnswerPager();
                    }
                }
            });
        }
        if (speechAssessmentPager != null) {
            mLogtf.d("onStopQuestion:speechAssessmentPager");
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (speechAssessmentPager != null) {
                        BaseSpeechAssessmentPager oldSpeechAssessmentPager = speechAssessmentPager;
                        String id = speechAssessmentPager.getId();
                        mLogtf.d("onStopQuestion:examSubmitAll:id=" + id);
                        speechAssessmentPager.examSubmitAll();
                        if (speechEndAction != null) {
                            speechEndAction.examSubmitAll(oldSpeechAssessmentPager, id);
                        }
                    }
                }
            });
            return;
        } else {
            if (speechAssessmentPagerUserBack != null) {
                havePager = true;
                String id = speechAssessmentPagerUserBack.getId();
                if (speechEndAction != null) {
                    speechEndAction.examSubmitAll(speechAssessmentPagerUserBack, id);
                }
                speechAssessmentPagerUserBack = null;
            }
        }
        int delayTime = 0;
        if (questionWebPager != null) {
            havePager = true;
            curQuestionView = (BasePager) questionWebPager;
            mLogtf.d("onStopQuestion:questionWebPager");
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (questionWebPager != null) {
                        questionWebPager.examSubmitAll();
                    }
                }
            });
            delayTime = 3000;
            closePageByTeamPk((BasePager) questionWebPager);
        } else if (hasQuestion && !hasSubmit) {
            getFullMarkList(XESCODE.STOPQUESTION, delayTime);
            hasQuestion = false;
        }
        if (oldisAnaswer && !havePager) {
            onQuestionShow(false, "onStopQuestion");
        }
        if (hasSubmit) {
            getFullMarkList(XESCODE.STOPQUESTION, delayTime);
            getAutoNotice(0);
            logger.i( "question end");
            hasQuestion = false;
        }
        if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(ptype)) {
            return;
        }
        if (videoQuestionLiveEntity == null) {
            mLogtf.d("onStopQuestion");
        } else {
            mLogtf.d("onStopQuestion:entity=" + videoQuestionLiveEntity);
            videoQuestionLiveEntity = null;
        }
        mVPlayVideoControlHandler.sendMessage(mVPlayVideoControlHandler.obtainMessage(NO_QUESTION, 1, 1));
//        if (liveType != LiveBll.LIVE_TYPE_LIVE) {
//            return;
//        }
//        mLiveBll.getStuRanking(new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                //{"stu":{"stuId":"11022","rank":0,"allRank":0,"gold":0,"rate":0},"first":[]}
//                //{"stu":{"stuId":11022,"rank":2,"gold":0,"rate":0},"first":{"stuId":31203,"rank":1,"gold":1,
// "rate":9}}
//                logger.i( "onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                addFighting(jsonObject);
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.i( "onPmFailure:msg=" + msg, error);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.i( "onPmError:error=" + responseEntity.getErrorMsg());
//            }
//        });
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (speechAssessmentPager instanceof SpeechAssAutoPager || voiceAnswerPager != null) {
            if (liveVideoPoint.videoWidth > 0) {
                int wradio = liveVideoPoint.getRightMargin();
                if (speechAssessmentPager instanceof SpeechAssAutoPager) {
                    if (baseSpeechCreat != null) {
                        baseSpeechCreat.setViewLayoutParams(speechAssessmentPager, wradio);
                    }
                }
                if (baseVoiceAnswerCreat != null) {
                    if (voiceAnswerPager != null) {
                        baseVoiceAnswerCreat.setViewLayoutParams(voiceAnswerPager, wradio);
                    }
                }
            }
        }
    }

    public void setVideoLayout(int width, int height) {
        if (speechAssessmentPager instanceof SpeechAssAutoPager || voiceAnswerPager != null) {
            final View contentView = activity.findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            int screenHeight = ScreenUtils.getScreenHeight();
            if (width > 0) {
                int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
                wradio += (screenWidth - width) / 2;
                if (speechAssessmentPager instanceof SpeechAssAutoPager) {
                    if (baseSpeechCreat != null) {
                        baseSpeechCreat.setViewLayoutParams(speechAssessmentPager, wradio);
                    }
                }
                if (baseVoiceAnswerCreat != null) {
                    if (voiceAnswerPager != null) {
                        baseVoiceAnswerCreat.setViewLayoutParams(voiceAnswerPager, wradio);
                    }
                }
            }
        }
    }

    @Override
    public void onExamStart(final String liveid, final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (examQuestionPager != null && videoQuestionLiveEntity.id.equals(examQuestionPager.getNum())) {
                    return;
                }
                if (examQuestionPager != null) {
                    mLogtf.d("onExamStart:old:num=" + examQuestionPager.getNum());
                    examQuestionPager.onDestroy();
                    rlQuestionContent.removeView(examQuestionPager.getRootView());
                    setHaveExam(false);
                }
                hasExam = true;
                if (mExamAndBool.contains(videoQuestionLiveEntity.id)) {
                    return;
                }
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "receiveExam");
                mData.put("examid", videoQuestionLiveEntity.id);
                umsAgentDebugSys(examQuestionEventId, mData);
                examQuestionPager = baseExamQuestionCreat.creatBaseExamQuestion(activity, liveid, videoQuestionLiveEntity);
                rlQuestionContent.addView(examQuestionPager.getRootView());
                setHaveExam(true);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }


    public boolean isWebViewCloseByTeacher() {
        return webViewCloseByTeacher;
    }

    public void setWebViewCloseByTeacher(boolean webViewCloseByTeacher) {
        this.webViewCloseByTeacher = webViewCloseByTeacher;
    }

    @Override
    public void onExamStop(final String num) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                int delayTime = 0;
                if (examQuestionPager != null) {
                    curQuestionView = examQuestionPager.getBasePager();
                    mLogtf.i("onExamStop:num=" + num + "," + examQuestionPager.getNum());
                    examQuestionPager.examSubmitAll();
                    delayTime = 3000;
                    closePageByTeamPk(examQuestionPager.getBasePager());
                } else if (hasExam && !hasSubmit) {
                    getFullMarkList(XESCODE.EXAM_STOP, delayTime);
                    hasExam = false;
                }
                if (hasSubmit) {
                    getFullMarkList(XESCODE.EXAM_STOP, delayTime);
                    hasExam = false;
                }
            }
        });
    }


    public void setTeamPkAllowed(boolean teamPkAllowed) {
        isTeamPkAllowed = teamPkAllowed;
    }

    private boolean isPageOnCloseing = false;

    /**
     * 战队pk答题结果页自动关闭
     */
    private void closePageByTeamPk(final BasePager pager) {
        logger.e( "=======>closePageByTeamPk 1111:" + isTeamPkAllowed + ":" + isPageOnCloseing);
        if (isTeamPkAllowed) {
            if (mVPlayVideoControlHandler != null && !isPageOnCloseing) {
                isPageOnCloseing = true;
                mVPlayVideoControlHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logger.e( "=======>closePageByTeamPk 2222:" + curQuestionView);
                        if (pager != null) {
                            if (pager instanceof BaseQuestionWebInter) {
                                setHaveWebQuestion(false);
                            }
                            if (pager instanceof ExamQuestionX5Pager) {
                                pager.onDestroy();
                                setHaveExam(false);
                            }
                            rlQuestionContent.removeView(pager.getRootView());
                        }
                        isPageOnCloseing = false;
                    }
                }, 6000);
            }
        }
    }

    @Override
    public void onBack(final LiveBasePager liveBasePager) {
        VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, (BaseApplication)
                BaseApplication.getContext(), false,
                VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
        cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveBasePager instanceof BaseSpeechAssessmentPager) {
                    boolean isNotNull = speechAssessmentPager != null;
                    if (speechAssessmentPager != null) {
                        speechAssessmentPager.onDestroy();
                        rlQuestionContent.removeView(speechAssessmentPager.getRootView());
                        mQueAndBool.add("" + speechAssessmentPager.getId());
                        onPause();
                    }
                    if (speechAssessmentPager != null) {
                        speechAssessmentPager.jsExamSubmit();
                    }
                    if (speechAssessmentPager != null) {
                        speechAssessmentPager.onDestroy();
                    }
                    speechAssessmentPagerUserBack = speechAssessmentPager;
                    setHaveSpeech(false);
                    if (speechAssessmentPagerUserBack != null && speechEndAction != null) {
                        final String num = speechAssessmentPagerUserBack.getId();
                        speechEndAction.onStopSpeech(speechAssessmentPagerUserBack, speechAssessmentPagerUserBack.getId(),
                                new SpeechEndAction.OnTop3End() {
                                    @Override
                                    public void onShowEnd() {
                                        mLogtf.d("onBack:onShowEnd=" + num + ",isAnaswer=" + isAnaswer + ",UserBack=" + (speechAssessmentPagerUserBack == null));
                                        speechAssessmentPagerUserBack = null;
                                        if (!isAnaswer) {
                                            onQuestionShow(false, "stopSpeech:onShowEnd");
                                        }
                                    }
                                });
                    }
                    if (isNotNull) {
                        return;
                    }
                } else if (liveBasePager instanceof BaseExamQuestionInter) {
                    if (examQuestionPager != null) {
                        rlQuestionContent.removeView(examQuestionPager.getRootView());
                        examQuestionPager.onDestroy();
                        mExamAndBool.add("" + examQuestionPager.getNum());
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "examClose");
                        mData.put("examid", examQuestionPager.getNum());
                        mData.put("closetype", "clickBackButton");
                        umsAgentDebugSys(examQuestionEventId, mData);
                        setHaveExam(false);
                        return;
                    } else {
                        mLogtf.d("onBack:BaseExamQuestionInter");
                    }
                } else if (liveBasePager instanceof BaseQuestionWebInter) {
                    if (questionWebPager != null) {
                        questionWebPager.onDestroy();
                        rlQuestionContent.removeView(questionWebPager.getRootView());
                        mQueAndBool.add("" + questionWebPager.getTestId());
                        Map<String, String> mData = new HashMap<>();
                        mData.put("testid", "" + questionWebPager.getTestId());
                        mData.put("closetype", "clickBackButton");
                        mData.put("logtype", "interactTestClose");
                        umsAgentDebugSys(questionEventId, mData);
                        setHaveWebQuestion(false);
                        return;
                    } else {
                        mLogtf.d("onBack:BaseQuestionWebInter");
                    }
                } else if (liveBasePager instanceof BaseVoiceAnswerPager) {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.onUserBack();
                        voiceAnswerPager.onDestroy();
                        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
                        voiceAnswerPager = null;
                        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
                        if (audioRequest != null) {
                            audioRequest.release();
                        }
                        return;
                    } else {
                        mLogtf.d("onBack:BaseVoiceAnswerPager");
                    }
                } else if (liveBasePager instanceof BaseLiveQuestionPager) {
                    if (baseQuestionPager != null) {
                        baseQuestionPager.onDestroy();
                        rlQuestionContent.removeView(baseQuestionPager.getRootView());
                        baseQuestionPager = null;
                        return;
                    } else {
                        mLogtf.d("onBack:BaseLiveQuestionPager");
                    }
                } else {
                    mLogtf.d("onBack:liveBasePager=" + liveBasePager);
                }
                if (liveBasePager != null) {
                    liveBasePager.onDestroy();
                }
            }
        });
        cancelDialog.setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogtf.d("onBack:Cancel");
            }
        });
        cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
    }

    public boolean onBack() {
        return false;
    }

    public void stopExam(String num, BaseExamQuestionInter baseExamQuestionInter) {
        mExamAndBool.add("" + num);
        rlQuestionContent.removeView(baseExamQuestionInter.getRootView());
        baseExamQuestionInter.onDestroy();
        setHaveExam(false);
        try {
            JSONObject object = new JSONObject();
            object.put("liveType", liveType);
            object.put("vSectionID", mVSectionID);
            object.put("num", num);
            mShareDataManager.put(EXAM, object.toString(), ShareDataManager.SHAREDATA_USER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        questionHttp.getStuGoldCount();
    }

    @Override
    public void stopWebQuestion(BasePager pager, String testId, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        if (pager instanceof BaseQuestionWebInter) {
            mQueAndBool.add("" + testId);
            if (questionWebPager != null) {
                questionWebPager.onDestroy();
            }
            setHaveWebQuestion(false);
            JSONObject object = new JSONObject();
            try {
                object.put("liveType", liveType);
                object.put("vSectionID", mVSectionID);
                object.put("testId", testId);
                mShareDataManager.put(QUESTION, object.toString(), ShareDataManager.SHAREDATA_USER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!isAnaswer) {
                onQuestionShow(false, "stopWebQuestion");
            }
        } else {
            subjectResultPager = null;
        }
        questionHttp.getStuGoldCount();

        // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
        //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));
    }


    @Override
    public void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, final String num) {
        mLogtf.d("stopSpeech:num=" + num + ",isAnaswer=" + isAnaswer + ",same=" + (pager == speechAssessmentPager));
        mQueAndBool.add("" + num);
        if (pager == speechAssessmentPager && speechAssessmentPager != null) {
            speechAssessmentPager.onDestroy();
            rlQuestionContent.removeView(speechAssessmentPager.getRootView());
            if (speechAssessmentPager instanceof SpeechAssAutoPager) {
                JSONObject object = new JSONObject();
                try {
                    object.put("liveType", liveType);
                    object.put("vSectionID", mVSectionID);
                    object.put("testId", num);
                    mShareDataManager.put(QUESTION, object.toString(), ShareDataManager.SHAREDATA_USER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (speechEndAction != null) {
                speechEndAction.onStopSpeech(speechAssessmentPager, num, new SpeechEndAction.OnTop3End() {
                    @Override
                    public void onShowEnd() {
                        mLogtf.d("stopSpeech:onShowEnd=" + num + ",isAnaswer=" + isAnaswer + ",UserBack=" + (speechAssessmentPagerUserBack == null));
                        speechAssessmentPagerUserBack = null;
                        if (!isAnaswer) {
                            onQuestionShow(false, "stopSpeech:onShowEnd");
                        }
                    }
                });
            } else {
                if (!isAnaswer) {
                    onQuestionShow(false, "stopSpeech");
                }
            }
            if (speechAssessmentPager.getId().equals(num)) {
                speechAssessmentPagerUserBack = speechAssessmentPager;
                setHaveSpeech(false);
            }
        } else {
            pager.onDestroy();
            rlQuestionContent.removeView(pager.getRootView());
        }
        questionHttp.getStuGoldCount();
    }


    @Override
    public void onSpeechSuccess(String num) {
        logger.d( "onSpeechSuccess:num=" + num);
        mQueAndBool.add("" + num);
        JSONObject object = new JSONObject();
        try {
            object.put("liveType", liveType);
            object.put("vSectionID", mVSectionID);
            object.put("testId", num);
            mShareDataManager.put(QUESTION, object.toString(), ShareDataManager.SHAREDATA_USER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
        questionHttp.speechEval42IsAnswered(mVSectionID, num, isAnswered);
    }

    private void setHaveExam(boolean haveExam) {
        isHaveExam = haveExam;
        if (!haveExam) {
            examQuestionPager = null;
        }
//        activity.getWindow().setFlags(haveExam ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
// .LayoutParams.FLAG_FULLSCREEN);
    }

    private void setHaveSpeech(boolean haveSpeech) {
        isHaveSpeech = haveSpeech;
        if (!haveSpeech) {
            speechAssessmentPager = null;
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
        }
//        activity.getWindow().setFlags(haveExam ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
// .LayoutParams.FLAG_FULLSCREEN);
    }

    private void setHaveWebQuestion(boolean isHaveWebQuestion) {
        this.isHaveWebQuestion = isHaveWebQuestion;
        if (!isHaveWebQuestion) {
            questionWebPager = null;
        }
//        activity.getWindow().setFlags(haveExam ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
// .LayoutParams.FLAG_FULLSCREEN);
    }

    public void setBaseVoiceAnswerCreat(BaseVoiceAnswerCreat baseVoiceAnswerCreat) {
        this.baseVoiceAnswerCreat = baseVoiceAnswerCreat;
    }

    public void setBaseExamQuestionCreat(BaseExamQuestionCreat baseExamQuestionCreat) {
        this.baseExamQuestionCreat = baseExamQuestionCreat;
    }

    public void setBaseSubjectResultCreat(BaseSubjectResultCreat baseSubjectResultCreat) {
        this.baseSubjectResultCreat = baseSubjectResultCreat;
    }

    private void showVoiceAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (voiceAnswerPager != null) {
            if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(videoQuestionLiveEntity
                    .getvQuestionID())) {
                return;
            } else {
                voiceAnswerPager.setEnd();
                voiceAnswerPager.stopPlayer();
                voiceAnswerPager.onDestroy();
                rlQuestionContent.removeView(voiceAnswerPager.getRootView());
                voiceAnswerPager = null;
            }
            logger.e( "普通互动题展示了0");
        }
        JSONObject assess_ref = null;
        try {
            assess_ref = new JSONObject(videoQuestionLiveEntity.assess_ref);
        } catch (JSONException e) {
            mErrorVoiceQue.add(videoQuestionLiveEntity.id);
            showQuestion(videoQuestionLiveEntity);
        }
        removeQuestionViews();
        BaseVoiceAnswerPager voiceAnswerPager2 =
                baseVoiceAnswerCreat.create(activity, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity
                        .type, rlQuestionContent, mIse);

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        int screenWidth = ScreenUtils.getScreenWidth();
//        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//        params.rightMargin = wradio;
//        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);

        voiceAnswerPager = voiceAnswerPager2;
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.setAudioRequest();
                    }
                }
            });
        } else {
            if (voiceAnswerPager != null) {
                voiceAnswerPager.setAudioRequest();
            }
        }
    }

    /**
     * 填空题
     */
    private void showFillBlankQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        baseQuestionPager = liveQuestionCreat.showFillBlankQuestion(videoQuestionLiveEntity);
        removeQuestionViews();
        RelativeLayout.LayoutParams params;
        if (isLand) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        rlQuestionContent.addView(baseQuestionPager.getRootView(), params);
        mLogtf.i("showFillBlank:Content=" + ((View) rlQuestionContent.getParent()).getVisibility() + "," +
                rlQuestionContent.getVisibility() + ",dur=" + videoCachedDuration + ",time=" + (System
                .currentTimeMillis() - before));
    }

    /**
     * 显示选择题
     */
    private void showSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        baseQuestionPager = liveQuestionCreat.showSelectQuestion(videoQuestionLiveEntity);
        removeQuestionViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (isLand) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        rlQuestionContent.addView(baseQuestionPager.getRootView(), params);
        mLogtf.i("showSelect:Content=" + ((View) rlQuestionContent.getParent()).getVisibility() + "," +
                rlQuestionContent.getVisibility() + ",dur=" + videoCachedDuration + ",time=" + (System
                .currentTimeMillis() - before));
    }

    /**
     * 显示多选择题
     */
    private void showMulitSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        baseQuestionPager = liveQuestionCreat.showMulitSelectQuestion(videoQuestionLiveEntity);
        removeQuestionViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (isLand) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        rlQuestionContent.addView(baseQuestionPager.getRootView(), params);
        mLogtf.i("showMulitSelect:Content=" + ((View) rlQuestionContent.getParent()).getVisibility() + "," +
                rlQuestionContent.getVisibility() + ",dur=" + videoCachedDuration + ",time=" + (System
                .currentTimeMillis() - before));
    }

    /**
     * 文科主观题
     */
    private void showSubjectiveQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        baseQuestionPager = liveQuestionCreat.showSubjectiveQuestion(videoQuestionLiveEntity);
        removeQuestionViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(baseQuestionPager.getRootView(), params);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    @Override
    public void initQuestionAnswerReslut(View popupWindow_view) {
        rlQuestionResContent.addView(popupWindow_view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rlQuestionResContent.removeAllViews();
            }
        });
        disMissAnswerResult();
    }

    @Override
    public void removeQuestionAnswerReslut(View popupWindow_view) {
        rlQuestionResContent.removeView(popupWindow_view);
    }

    @Override
    public void removeBaseVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager2) {
        if (voiceAnswerPager2 == voiceAnswerPager) {
            if (voiceAnswerPager.isEnd()) {
                mLogtf.d("removeBaseVoiceAnswerPager1");
                voiceAnswerPager2.onDestroy();
                rlQuestionContent.removeView(voiceAnswerPager2.getRootView());
                voiceAnswerPager = null;
            }
        } else {
            mLogtf.d("removeBaseVoiceAnswerPager1");
            voiceAnswerPager2.onDestroy();
            rlQuestionContent.removeView(voiceAnswerPager2.getRootView());
        }
    }

    /**
     * 试题布局隐藏
     */
    public void questionViewGone(boolean delay) {
        mIsShowQuestion = false;
        if (keyBordAction == null) {
            keyBordAction = ProxUtil.getProxUtil().get(activity, KeyBordAction.class);
        }
        if (keyBordAction != null) {
            keyBordAction.showInput();
        }
        if (delay) {
            postDelayedIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    removeQuestionViews();
                }
            }, 1000);
        } else {
            postIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    removeQuestionViews();
                }
            });
        }
    }

    /**
     * 回答问题结果提示框延迟三秒消失
     */
    public void disMissAnswerResult() {
        mVPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlQuestionResContent.removeAllViews();
            }
        }, 5000);
    }

    /**
     * 互动题回答正确
     *
     * @param entity
     */
    private void initAnswerRightResult(VideoResultEntity entity) {
        int goldNum = entity.getGoldNum();
        int resultType = entity.getResultType();
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id.tv_pop_question_answer_right_answer_hint);
        if (resultType == QUE_RES_TYPE1) {
            tvGoldHint.setText("" + goldNum);
        } else {
            popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable
                    .bg_pop_question_answer_type4);
            tvGoldHint.setVisibility(View.GONE);
        }
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 语音答题选择题回答正确
     */
    @Override
    public void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 语音答题填空题回答正确
     */
    @Override
    public void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 语音答题回答错误
     */
    @Override
    public void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 语音答题回答错误
     */
    @Override
    public void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答错误
     */
    private void initAnswerWrongResult() {
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_answer_wrong, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 互动题回答部分正确
     */
    private void initAnswerPartRightResult(VideoResultEntity entity) {
        int goldNum = entity.getGoldNum();
        int resultType = entity.getResultType();
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_answer_right, null, false);
        if (resultType == QUE_RES_TYPE3) {
            popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable
                    .bg_pop_question_answer_type3);
            TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id
                    .tv_pop_question_answer_right_answer_hint);
            tvGoldHint.setText("" + goldNum);
        } else {
            popupWindow_view.findViewById(R.id.iv_pop_question_answer_right).setBackgroundResource(R.drawable
                    .bg_pop_question_answer_type5);
            TextView tvGoldHint = (TextView) popupWindow_view.findViewById(R.id
                    .tv_pop_question_answer_right_answer_hint);
            tvGoldHint.setVisibility(View.GONE);
        }
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 移除除了战况以外的view
     */
    private void removeQuestionViews() {
        int oldChildCount = rlQuestionContent.getChildCount();
        for (int i = rlQuestionContent.getChildCount() - 1; i >= 0; i--) {
            View v = rlQuestionContent.getChildAt(i);
            if (v.getId() != R.id.rl_livevideo_fight_root) {
                if (examQuestionPager != null && v == examQuestionPager.getRootView()) {

                } else if (speechAssessmentPager != null && v == speechAssessmentPager.getRootView()) {

                } else if (questionWebPager != null && v == questionWebPager.getRootView()) {

                } else if (subjectResultPager != null && v == subjectResultPager.getRootView()) {

                } else if (voiceAnswerPager != null && v == voiceAnswerPager.getRootView()) {

                } else {
                    rlQuestionContent.removeView(v);
                }
            }
        }
        mLogtf.d("removeQuestionViews:ChildCount=" + rlQuestionContent.getChildCount() + "," + oldChildCount);
    }

    public void postIfNotFinish(Runnable r) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.post(r);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }

    @Override
    public boolean onSpeechResult(final String json) {
        boolean speechResult = false;
//        if (speechAssessmentPager != null) {
//            speechResult = speechAssessmentPager.onSpeechResult(json);
//        }
        return speechResult;
    }

    @Override
    public void getSpeechEval(String id, OnSpeechEval onSpeechEval) {
        questionHttp.getSpeechEval(id, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval
            onSpeechEval) {
        questionHttp.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval) {
        questionHttp.sendSpeechEvalResult2(id, stuAnswer, onSpeechEval);
    }

    public void onPause() {
        if (speechAssessmentPager != null) {
            speechAssessmentPager.stopPlayer();
        }
    }

    private void switchVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager) {
        voiceAnswerPager.stopPlayer();
        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        if (voiceAnswerPager == QuestionBll.this.voiceAnswerPager) {
            QuestionBll.this.voiceAnswerPager = null;
        }
    }

    private void stopVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager) {
        voiceAnswerPager.stopPlayer();
        voiceAnswerPager.onDestroy();
        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        if (voiceAnswerPager == QuestionBll.this.voiceAnswerPager) {
            QuestionBll.this.voiceAnswerPager = null;
        }
    }

    private void stopVoiceAnswerPager() {
        voiceAnswerPager.stopPlayer();
        voiceAnswerPager.onDestroy();
        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
    }

    public void umsAgentDebugSys(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugInter(eventId, mData);
    }

    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugPv(eventId, mData);
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        if (voiceAnswerPager != null) {
            voiceAnswerPager.onNetWorkChange(netWorkType);
        }
    }

    public void setAnswerRankBll(AnswerRankBll bll) {
        mAnswerRankBll = bll;
    }

    private void getFullMarkList(final int type, final int delayTime) {
        /*if(type==XESCODE.STOPQUESTION) {
            if (hasQuestion) {
                hasQuestion = false;
            } else {
                return;
            }
        }else{
            if(hasExam){
                hasExam=false;
            }else{
                return;
            }
        }*/
        hasSubmit = false;
        HttpCallBack callBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                final List<FullMarkListEntity> lst = new ArrayList<>();
                try {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    List<FullMarkListEntity> tmplst = JSON.parseArray(jsonObject.optString("ranks"),
                            FullMarkListEntity.class);
                    if (tmplst != null) {
                        lst.addAll(tmplst);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showFullMarkList(type, lst, delayTime);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                //showFullMarkList(type, new ArrayList<FullMarkListEntity>(), delayTime);
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.hideRankList();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.hideRankList();
                }
            }
        };
        if (mAnswerRankBll != null) {
            if (type == XESCODE.STOPQUESTION) {
                mAnswerRankBll.getFullMarkListQuestion(callBack);
            } else if (type == XESCODE.EXAM_STOP) {
                mAnswerRankBll.getFullMarkListTest(callBack);
            }
        }
    }

    public void onSubmit(int type, boolean isForceSubmit) {
        submitTime = System.currentTimeMillis();
        questionHttp.sendRankMessage(XESCODE.RANK_STU_MESSAGE);
        if (isForceSubmit) {
            getFullMarkList(type, 3000);
            getAutoNotice(1);
            logger.i( "question force submit");
            switch (type) {
                case XESCODE.STOPQUESTION:
                    hasQuestion = false;
                    break;
                case XESCODE.EXAM_STOP:
                    hasExam = false;
                    break;
                default:
                    break;
            }
        } else {
            hasSubmit = true;
        }
    }

    private void showFullMarkList(final int type, final List<FullMarkListEntity> lst, int delayTime) {

        if (mAnswerRankBll == null) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case XESCODE.STOPQUESTION:
                            if (questionWebPager != null) {
                                if (curQuestionView == questionWebPager) {
                                    logger.i("======questionbll  cur==ques");
                                    questionWebPager.onDestroy();
                                    rlQuestionContent.removeView(questionWebPager.getRootView());
                                    questionWebPager = null;
                                    curQuestionView = null;
                                    setHaveWebQuestion(false);
                                } else if (curQuestionView != null) {
                                    logger.i("======questionbll  cur=" + curQuestionView.toString() + "   que=" +
                                            questionWebPager.toString());
                                    rlQuestionContent.removeView(curQuestionView.getRootView());
                                    curQuestionView = null;
                                }
                            }
                            break;
                        case XESCODE.EXAM_STOP:
                            if (examQuestionPager != null) {
                                if (curQuestionView == examQuestionPager) {
                                    logger.i("======questionbll  cur==exa");
                                    examQuestionPager.onDestroy();
                                    rlQuestionContent.removeView(examQuestionPager.getRootView());
                                    examQuestionPager = null;
                                    curQuestionView = null;
                                    setHaveExam(false);
                                } else if (curQuestionView != null) {
                                    logger.i("======questionbll  cur=" + curQuestionView.toString() + "   que=" +
                                            examQuestionPager.toString());
                                    rlQuestionContent.removeView(curQuestionView.getRootView());
                                    curQuestionView = null;
                                }
                            }
                            break;
                        default:
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAnswerRankBll.showFullMarkList(lst, type);

            }
        }, delayTime);
    }

    public void setBaseSpeechCreat(BaseSpeechCreat baseSpeechCreat) {
        this.baseSpeechCreat = baseSpeechCreat;
    }

    public void setSpeechEndAction(SpeechEndAction speechEndAction) {
        this.speechEndAction = speechEndAction;
    }

    public SpeechEndAction getSpeechEndAction() {
        return speechEndAction;
    }

    /**
     * 获取智能私信
     */
    public void getAutoNotice(final int isForce) {
        if (mLiveAutoNoticeBll == null) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLiveAutoNoticeBll.getAutoNotice(isForce, 0);
            }
        }, (int) (7000 + Math.random() * 4000));

    }

    @Override
    public void registQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.add(questionShowAction);
    }

    @Override
    public void unRegistQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.remove(questionShowAction);
    }

    class LiveStandQuestionSwitchImpl extends LiveQuestionSwitchImpl implements LiveStandQuestionSwitch {

        @Override
        public void getTestAnswerTeamStatus(BaseVideoQuestionEntity videoQuestionLiveEntity,
                                            AbstractBusinessDataCallBack callBack) {
            if (!"-1".equals(liveGetInfo.getRequestTime())) {
                final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity)
                        videoQuestionLiveEntity;
                questionHttp.getTestAnswerTeamStatus(videoQuestionLiveEntity1, callBack);
            }
        }

        @Override
        public long getRequestTime() {
            try {
                String requestTime = liveGetInfo.getRequestTime();
                long time = Long.parseLong(requestTime);
                return time * 1000;
            } catch (Exception e) {

            }
            return 3000;
        }
    }

    public class LiveQuestionSwitchImpl implements QuestionSwitch {

        @Override
        public String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity) {
            return "h5test";
        }

        @Override
        public BasePager questionSwitch(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseQuestionEntity) {
            VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            logger.e( "questionSwitch:" + "QuestionBll" + "type:"+ videoQuestionLiveEntity1.type);
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity1.type)) {
                if ("1".equals(videoQuestionLiveEntity1.choiceType)) {
                    showSelectQuestion(videoQuestionLiveEntity1);
                    switchVoiceAnswerPager(baseVoiceAnswerPager);
                    return baseQuestionPager;
                } else {
                    showMulitSelectQuestion(videoQuestionLiveEntity1);
                    switchVoiceAnswerPager(baseVoiceAnswerPager);
                    return baseQuestionPager;
                }
            } else if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
                showFillBlankQuestion(videoQuestionLiveEntity1);
                switchVoiceAnswerPager(baseVoiceAnswerPager);
                return baseQuestionPager;
            }
            return null;
        }

        @Override
        public void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, final OnQuestionGet onQuestionGet) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            questionHttp.getQuestion(videoQuestionLiveEntity1, new AbstractBusinessDataCallBack() {

                @Override
                public void onDataSucess(Object... objData) {
                    onQuestionGet.onQuestionGet(videoQuestionLiveEntity1);
                }
            });
        }

        @Override
        public void onPutQuestionResult(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String
                result, int sorce, boolean isRight, double voiceTime, String isSubmit, OnAnswerReslut answerReslut) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
            String testAnswer;
            if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
//                testAnswer = "" + sorce;
                testAnswer = "A";
            } else {
//                testAnswer = result + ":" + sorce;
                testAnswer = result;
            }
            questionHttp.liveSubmitTestAnswer(baseVoiceAnswerPager, videoQuestionLiveEntity1, mVSectionID, testAnswer, true, isRight,
                    answerReslut);
        }

        @Override
        public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {

        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(BaseVoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            mLogtf.d("stopSpeech:voiceAnswerPager:end=" + answerPager.isEnd());
            stopVoiceAnswerPager(answerPager);
        }
    }

    /**
     * 试题隐藏显示
     *
     * @param isShow true显示
     * @param method
     */
    private void onQuestionShow(boolean isShow, String method) {
        mLogtf.d("onQuestionShow:isShow=" + isShow + ",method=" + method);
        for (QuestionShowAction questionShowAction : questionShowActions) {
            questionShowAction.onQuestionShow(isShow);
        }
    }


    /**
     * 强制关闭当前 答题页面
     */
    public void forceClose() {
        if (mVPlayVideoControlHandler != null) {
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    Loger.e("QuestionBll", "=======>forceClose 2222:" + curQuestionView);
                    if (questionWebPager != null) {
                        rlQuestionContent.removeView(questionWebPager.getRootView());
                        if (questionWebPager instanceof BaseQuestionWebInter) {
                            setHaveWebQuestion(false);
                        }
                    }
                }
            });
        }
    }
}
