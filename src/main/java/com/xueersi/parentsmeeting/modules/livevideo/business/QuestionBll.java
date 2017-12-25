package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseLiveQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExamQuestionPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionFillInBlankLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionFillInBlankPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionMulitSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionMulitSelectPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSelectLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSelectPortLivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionSubjectivePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.QuestionWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssessmentWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SubjectResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE3;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE4;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE5;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class QuestionBll implements QuestionAction, Handler.Callback, SpeechEvalAction, QuestionWebPager
        .StopWebQuestion {
    String TAG = "QuestionBll";
    SpeechEvaluatorUtils mIse;
    String examQuestionEventId = LiveVideoConfig.LIVE_H5_EXAM;
    String questionEventId = LiveVideoConfig.LIVE_PUBLISH_TEST;
    String understandEventId = LiveVideoConfig.LIVE_DOYOUSEE;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private VideoQuestionLiveEntity videoQuestionLiveEntity;
    private LogToFile mLogtf;
    private Activity activity;
    private LiveBll mLiveBll;
    private LiveTopic mLiveTopic;
    /** 直播id */
    private String mVSectionID;
    /** 直播类型 */
    private int liveType;
    protected ShareDataManager mShareDataManager;
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;
    /** 显示懂了吗 */
    private static final int SHOW_UNDERSTAND = 2;
    /** 没有懂了吗显示 */
    private static final int NO_UNDERSTAND = 3;
    /** 当前是否正在显示互动题 */
    private boolean mIsShowQuestion = false;
    /** 当前是否正在显示懂了吗 */
    private boolean mIsShowUnderstand = false;
    /** 语音答题 */
    private VoiceAnswerPager voiceAnswerPager;
    /** 语音强制提交，外层 */
    private RelativeLayout rlVoiceQuestionContent;
    /** 互动题布局 */
    private BaseLiveQuestionPager baseQuestionPager;
    RelativeLayout bottomContent;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContent;
    /** 互动题作答成功的布局 */
    private RelativeLayout rlQuestionResContent;
    /** video缓存时间 */
    private long videoCachedDuration;
    private LiveGetInfo liveGetInfo;
    private PraiseOrEncourageBll praiseOrEncourageBll;
    /** 存互动题 */
    private static final String QUESTION = "live_question";
    /** 存试卷 */
    private static final String EXAM = "live_exam";
    /** 答题的暂存状态 */
    private HashSet<String> mQueAndBool = new HashSet<>();
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
    private QuestionWebPager questionWebPager;
    /** 试卷页面 */
    private ExamQuestionPager examQuestionPager;
    /** 语音评测页面 */
    private BaseSpeechAssessmentPager speechAssessmentPager;
    private SubjectResultPager subjectResultPager;
    boolean isLand;
    private LiveMessageBll liveMessageBll;
    private boolean isAnaswer = false;

    public QuestionBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void setLiveMessageBll(LiveMessageBll liveMessageBll) {
        this.liveMessageBll = liveMessageBll;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    public void setLiveTopic(LiveTopic mLiveTopic) {
        this.mLiveTopic = mLiveTopic;
    }

    public void setShareDataManager(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
    }

    public void setVideoCachedDuration(long videoCachedDuration) {
        this.videoCachedDuration = videoCachedDuration;
    }

    public void setUserName(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        this.mIse = ise;
    }

    public void setPraiseOrEncourageBll(PraiseOrEncourageBll praiseOrEncourageBll) {
        this.praiseOrEncourageBll = praiseOrEncourageBll;
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

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        this.isLand = isLand;
        this.bottomContent = bottomContent;
        //互动题
        rlQuestionContent = new RelativeLayout(activity);
        rlQuestionContent.setId(R.id.rl_livevideo_content_question);
        bottomContent.addView(rlQuestionContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (rlQuestionResContent == null) {
            rlQuestionResContent = new RelativeLayout(activity);
        }
        bottomContent.addView(rlQuestionResContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//        QuestionMulitSelectLivePager questionMulitSelectLivePager = new QuestionMulitSelectLivePager(activity,
// videoQuestionLiveEntity);
//        rlQuestionContent.addView(questionMulitSelectLivePager.getRootView());
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
        if (mIsShowUnderstand) {
            understand("");
        }
        //测试试卷
        rlQuestionContent.postDelayed(new Runnable() {
            @Override
            public void run() {
//                onExamStart("17174", "1");
            }
        }, 100);
        //测试多选题
//        rlQuestionContent.post(new Runnable() {
//            @Override
//            public void run() {
//                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//                videoQuestionLiveEntity.type = "1";
//                videoQuestionLiveEntity.id = "222";
//                videoQuestionLiveEntity.time = 222;
//                videoQuestionLiveEntity.num = 5;
//                videoQuestionLiveEntity.gold = 1;
//                videoQuestionLiveEntity.srcType = "a";
//                videoQuestionLiveEntity.choiceType = "2";
//                showQuestion(videoQuestionLiveEntity);
//            }
//        });
        //测试互动题排名
//        bottomContent.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONObject jsonObject = new JSONObject("{\"stu\":{\"stuId\":\"11022\",\"rank\":1,\"allRank\":0,
// \"gold\":0,\"rate\":0},\"first\":[]}");
//                    addFighting(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    JSONObject jsonObject = new JSONObject("{\"stu\":{\"stuId\":11022,\"rank\":2,\"gold\":0,
// \"rate\":0},\"first\":{\"stuId\":31203,\"rank\":1,\"gold\":1,\"rate\":9}}");
//                    addFighting(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 1000);
//        mVPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                VideoResultEntity entity = new VideoResultEntity();
//                entity.setStandardAnswer("where SKAFJLSA FJKLSJAFJS FJKLSAJ:::?*&^%$$#");
//                initFillAnswerWrongResultVoice(entity);
//            }
//        }, 3000);
//        mVPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                VideoResultEntity entity = new VideoResultEntity();
//                entity.setStandardAnswer("B");
//                entity.setYourAnswer("B");
//                initSelectAnswerRightResultVoice(entity);
//            }
//        }, 10000);
//        mVPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                VideoResultEntity entity = new VideoResultEntity();
////                entity.setStandardAnswer("living");
//                entity.setStandardAnswer("where SKAFJLSA FJKLSJAFJS FJKLSAJ:::?*&^%$$# where SKAFJLSA FJKLSJAFJS FJKLSAJ:::?*&^%$$# where SKAFJLSA FJKLSJAFJS FJKLSAJ:::?*&^%$$#");
//                initFillinAnswerRightResultVoice(entity);
//            }
//        }, 4000);
//        mVPlayVideoControlHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                VideoResultEntity entity = new VideoResultEntity();
//                entity.setStandardAnswer("A");
//                entity.setYourAnswer("B");
//                initSelectAnswerWrongResultVoice(entity);
//            }
//        }, 27000);
    }

    public void onKeyboardShowing(boolean isShowing) {
        if (examQuestionPager != null) {
            examQuestionPager.onKeyboardShowing(isShowing);
        }
    }

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
                    mIsShowUnderstand = false;
                }
                mLogtf.d(s);
                liveMessageBll.hideInput();
            }
            break;
            case NO_QUESTION: {
                String s = "handleMessage:NO_QUESTION:mIsShow=" + mIsShowQuestion;
                if (mIsShowQuestion) {
                    mIsShowQuestion = false;
                    if (baseQuestionPager != null) {
                        baseQuestionPager.hideInputMode();
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
            case SHOW_UNDERSTAND: {
                mIsShowUnderstand = true;
            }
            break;
            case NO_UNDERSTAND: {
                String s = "handleMessage:NO_UNDERSTAND:mIsShow=" + mIsShowUnderstand;
                if (mIsShowUnderstand) {
                    mIsShowUnderstand = false;
                    if (baseQuestionPager != null) {
                        baseQuestionPager.hideInputMode();
                    }
                    understandViewGone();
                }
                mLogtf.d(s);
            }
            break;
        }
        return false;
    }

    @Override
    public void showQuestion(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (videoQuestionLiveEntity == null) {
            mLogtf.d("showQuestion:noQuestion");
            isAnaswer = false;
            if (voiceAnswerPager != null && !voiceAnswerPager.isEnd()) {
                final VoiceAnswerPager answerPager = voiceAnswerPager;
//                voiceAnswerPager = null;
//                View view = answerPager.getRootView();
//                ViewGroup.LayoutParams lp = view.getLayoutParams();
//                rlQuestionContent.removeView(view);
//                rlVoiceQuestionContent = new RelativeLayout(activity);
//                rlVoiceQuestionContent.addView(view, lp);
//                bottomContent.addView(rlVoiceQuestionContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
                mVPlayVideoControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        answerPager.examSubmitAll("showQuestion");
                    }
                });
            }
            return;
        }
        isAnaswer = true;
        if (this.videoQuestionLiveEntity != null) {
            mLogtf.d("showQuestion:Entity=" + this.videoQuestionLiveEntity.id + "," + videoQuestionLiveEntity.id);
        } else {
            mLogtf.d("showQuestion:Entity=" + videoQuestionLiveEntity.id);
        }
        if (mQueAndBool.contains(videoQuestionLiveEntity.id)) {
            mLogtf.d("showQuestion answer:id=" + videoQuestionLiveEntity.id);
            return;
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("testtype", "" + videoQuestionLiveEntity.type);
        mData.put("testid", "" + videoQuestionLiveEntity.id);
        mData.put("logtype", "receiveInteractTest");
        mData.put("ish5test", "" + videoQuestionLiveEntity.isTestUseH5);
        mLiveBll.umsAgentDebug(questionEventId, mData);
        if (!"4".equals(videoQuestionLiveEntity.type)) {//不是语音评测
            if (videoQuestionLiveEntity.isTestUseH5) {
                mVPlayVideoControlHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (questionWebPager != null && questionWebPager.getTestId().equals(videoQuestionLiveEntity
                                .getvQuestionID())) {
                            return;
                        }
                        questionWebPager = new QuestionWebPager(activity, QuestionBll.this, liveGetInfo
                                .getTestPaperUrl(), liveGetInfo.getStuId(), liveGetInfo.getUname(),
                                liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(), videoQuestionLiveEntity.nonce);
                        rlQuestionContent.addView(questionWebPager.getRootView());
                        setHaveWebQuestion(true);
                    }
                });
                return;
            }
            this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        }
        mVPlayVideoControlHandler.post(new Runnable() {

            @Override
            public void run() {
                mLogtf.d("showQuestion:type=" + videoQuestionLiveEntity.type);
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                    if ("1".equals(videoQuestionLiveEntity.isVoice) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.id)) {
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
                    if ("1".equals(videoQuestionLiveEntity.isVoice) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.id)) {
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
                        speechAssessmentPager.examSubmitAll();
                        rlQuestionContent.removeView(speechAssessmentPager.getRootView());
                    }
                    if (activity instanceof AudioRequest) {
                        AudioRequest audioRequest = (AudioRequest) activity;
                        audioRequest.request(new AudioRequest.OnAudioRequest() {
                            @Override
                            public void requestSuccess() {

                            }
                        });
                    }
                    String speechEvalResultUrl = liveGetInfo.getSpeechEvalUrl();
//                    speechAssessmentPager = new SpeechAssessmentPager(activity, QuestionBll.this, speechEvalUrl,
// liveGetInfo.getStuId(), liveGetInfo.getId(), id);
//                    speechAssessmentPager = new SpeechAssessmentPager(activity, true, liveGetInfo.getId(),
//                            liveGetInfo.getStuId(), QuestionBll.this, id, speechEvalResultUrl);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    if ("1".equals(videoQuestionLiveEntity.isAllow42)) {
                        SpeechAssAutoPager speechAssAutoPager =
                                new SpeechAssAutoPager(activity, liveGetInfo.getId(), id, videoQuestionLiveEntity.nonce,
                                        videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time, QuestionBll.
                                        this);
                        speechAssessmentPager = speechAssAutoPager;
//                        speechAssAutoPager.setIse(mIse);
                        int screenWidth = ScreenUtils.getScreenWidth();
                        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
                        lp.rightMargin = wradio;
//                        rlQuestionContent.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (speechAssessmentPager != null) {
//                                    speechAssessmentPager.examSubmitAll();
//                                }
//                            }
//                        }, 3000);
                    } else {
                        speechAssessmentPager = new SpeechAssessmentWebPager(activity,
                                liveGetInfo.getId(), id, liveGetInfo.getStuId(),
                                true, videoQuestionLiveEntity.nonce, QuestionBll.this);
                    }
                    setHaveSpeech(true);
                    rlQuestionContent.addView(speechAssessmentPager.getRootView(), lp);
//                    speechAssessmentPager = new SpeechAssAutoPager(activity, true, liveGetInfo.getId(),
//                            liveGetInfo.getStuId(), QuestionBll.this, id, speechEvalResultUrl);
//                    setHaveSpeech(true);
//                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT);
//                    int screenWidth = ScreenUtils.getScreenWidth();
//                    int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//                    lp.rightMargin = wradio;
//                    rlQuestionContent.addView(speechAssessmentPager.getRootView(), lp);
                } else {
                    XESToastUtils.showToast(activity, "不支持的试题类型，可能需要升级版本");
                    return;
                }
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        mVPlayVideoControlHandler.sendEmptyMessage(SHOW_QUESTION);
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
     * @param baseVideoQuestionEntity
     * @param entity
     */
    @Override
    public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        boolean isSuccess = false;
        int type = 0;
        if (entity != null) {// 提交成功，否则是已经答过题了
            boolean isVoice = entity.isVoice();
            if (!isVoice && LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity.type)) {
                String url = liveGetInfo.getSubjectiveTestAnswerResult() + "?stuId=" + liveGetInfo.getStuId() + "&testId=" + videoQuestionLiveEntity.getvQuestionID();
                Loger.d(TAG, "showQuestion:url=" + url);
                subjectResultPager = new SubjectResultPager(activity, this,
                        liveGetInfo.getSubjectiveTestAnswerResult(),
                        liveGetInfo.getStuId(), liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                rlQuestionContent.addView(subjectResultPager.getRootView(), params);
            } else {
                type = entity.getResultType();
                if (entity.getIsAnswer() == 1) {
                    XESToastUtils.showToast(activity, "您已经答过此题");
                } else {
                    if (isVoice) {
                        if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                                initSelectAnswerRightResultVoice(entity);
                            } else {
                                initFillinAnswerRightResultVoice(entity);
                            }
                            isSuccess = true;
                            // 回答错误提示
                        } else if (entity.getResultType() == QUE_RES_TYPE2) {
                            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                                initSelectAnswerWrongResultVoice(entity);
                            } else {
                                initFillAnswerWrongResultVoice(entity);
                            }
                            // 填空题部分正确提示
                        }
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
            if (baseQuestionPager != null) {
                baseQuestionPager.onSubSuccess();
            }
            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    mLiveBll.getStuGoldCount();
                }
            }, 5000);
        } else {
            if (baseQuestionPager != null) {
                baseQuestionPager.onSubFailure();
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
            videoQuestionLiveEntity = null;
        } else {
            mLogtf.d("onAnswerReslut:id=null,testId=" + testId + ",type=" + type);
        }
        if (mLiveTopic != null) {
//            mLiveTopic.setTopic(null);
            mLiveTopic.setVideoQuestionLiveEntity(null);
        }
        if (voiceAnswerPager != null) {
            stopVoiceAnswerPager();
        }
        questionViewGone(true);
    }

    @Override
    public void onAnswerFailure() {
        if (baseQuestionPager != null) {
            baseQuestionPager.onSubFailure();
        }
    }

    @Override
    public void onStopQuestion(String ptype) {
        isAnaswer = false;
        if (voiceAnswerPager != null) {
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.examSubmitAll("onStopQuestion");
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
                        mLogtf.d("onStopQuestion:examSubmitAll:id=" + speechAssessmentPager.getId());
                        speechAssessmentPager.examSubmitAll();
                    }
                }
            });
            return;
        }
        if (questionWebPager != null) {
            mLogtf.d("onStopQuestion:questionWebPager");
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (questionWebPager != null) {
                        questionWebPager.examSubmitAll();
                    }
                }
            });
        }
        if ("4".equals(ptype)) {
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
//                Loger.i(TAG, "onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                addFighting(jsonObject);
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                Loger.i(TAG, "onPmFailure:msg=" + msg, error);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                Loger.i(TAG, "onPmError:error=" + responseEntity.getErrorMsg());
//            }
//        });
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
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
                wradio += (screenWidth - width) / 2;
                if (speechAssessmentPager instanceof SpeechAssAutoPager) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechAssessmentPager.getRootView().getLayoutParams();
                    if (wradio != params.rightMargin) {
                        params.rightMargin = wradio;
                        LayoutParamsUtil.setViewLayoutParams(speechAssessmentPager.getRootView(), params);
                    }
                }
                if (voiceAnswerPager != null) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) voiceAnswerPager.getRootView().getLayoutParams();
                    if (wradio != params.rightMargin) {
                        params.rightMargin = wradio;
                        LayoutParamsUtil.setViewLayoutParams(voiceAnswerPager.getRootView(), params);
                    }
                }
            }
        }
    }

    @Override
    public void understand(final String nonce) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "understandReceive");
        mLiveBll.umsAgentDebug(understandEventId, mData);
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                //显示懂了吗？先移除互动题
                removeQuestionViews();
                final View underatandView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_understand,
                        rlQuestionContent,
                        false);
                ((TextView) underatandView.findViewById(R.id.tv_livevideo_under_user)).setText(liveGetInfo.getStuName
                        () + " 你好");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) underatandView.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                rlQuestionContent.addView(underatandView, params);
                View.OnClickListener listener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        boolean isUnderstand = v.getId() == R.id.tv_livevideo_understand_understand;
                        mLogtf.d("understand:isUnderstand=" + isUnderstand);
                        mLiveBll.understand(isUnderstand);
                        removeQuestionViews();
                        mVPlayVideoControlHandler.sendEmptyMessage(NO_UNDERSTAND);
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "sendUnderstand");
                        mData.put("answerType", isUnderstand ? "1" : "0");
                        mData.put("expect", "1");
                        mData.put("nonce", "" + UUID.randomUUID());
                        mData.put("sno", "3");
                        mData.put("stable", "1");
                        mLiveBll.umsAgentDebug2(understandEventId, mData);
                    }
                };
                activity.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener(listener);
                activity.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
                postDelayedIfNotFinish(new Runnable() {
                    public void run() {
                        if (mIsShowUnderstand) {
                            Map<String, String> mData = new HashMap<>();
                            mData.put("logtype", "understandTimeout");
                            mLiveBll.umsAgentDebug(understandEventId, mData);
                            removeQuestionViews();
                            mVPlayVideoControlHandler.sendEmptyMessage(NO_UNDERSTAND);
                        }
                    }
                }, 10000);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "showUnderstand");
                mData.put("nonce", "" + nonce);
                mData.put("ex", "Y");
                mData.put("sno", "2");
                mData.put("stable", "1");
                mLiveBll.umsAgentDebug3(understandEventId, mData);
            }
        };
        mVPlayVideoControlHandler.post(runnable);
        mVPlayVideoControlHandler.sendEmptyMessage(SHOW_UNDERSTAND);
    }

    @Override
    public void onExamStart(final String liveid, final String num, final String nonce) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mExamAndBool.contains(num)) {
                    return;
                }
                if (examQuestionPager != null && num.equals(examQuestionPager.getNum())) {
                    return;
                }
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "receiveExam");
                mData.put("examid", num);
                mLiveBll.umsAgentDebug(examQuestionEventId, mData);
                examQuestionPager = new ExamQuestionPager(activity, mLiveBll, QuestionBll.this, liveGetInfo.getStuId
                        (), liveGetInfo.getUname(), liveid, num, nonce);
                rlQuestionContent.addView(examQuestionPager.getRootView());
                setHaveExam(true);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    @Override
    public void onExamStop() {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (examQuestionPager != null) {
                    examQuestionPager.examSubmitAll();
                }
            }
        });
    }

    public boolean onBack() {
        final boolean haveExam = isHaveExam;
        final boolean haveSpeech = isHaveSpeech;
        final boolean haveWebQuestion = isHaveWebQuestion;
        boolean canback = haveExam || haveSpeech || haveWebQuestion || subjectResultPager != null || voiceAnswerPager != null;
        mLogtf.d("onBack:haveExam=" + haveExam + ",haveSpeech=" + haveSpeech + ",canback=" + canback);
        if (canback) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, (BaseApplication)
                    BaseApplication.getContext(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (examQuestionPager != null) {
                        rlQuestionContent.removeView(examQuestionPager.getRootView());
                        mExamAndBool.add("" + examQuestionPager.getNum());
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "examClose");
                        mData.put("examid", examQuestionPager.getNum());
                        mData.put("closetype", "clickBackButton");
                        mLiveBll.umsAgentDebug(examQuestionEventId, mData);
                    }
                    if (speechAssessmentPager != null) {
                        rlQuestionContent.removeView(speechAssessmentPager.getRootView());
                        mQueAndBool.add("" + speechAssessmentPager.getId());
                        onPause();
                        if (speechAssessmentPager != null) {
                            speechAssessmentPager.jsExamSubmit();
                        }
                    }
                    if (questionWebPager != null) {
                        rlQuestionContent.removeView(questionWebPager.getRootView());
                        mQueAndBool.add("" + questionWebPager.getTestId());
                        Map<String, String> mData = new HashMap<>();
                        mData.put("testid", "" + questionWebPager.getTestId());
                        mData.put("closetype", "clickBackButton");
                        mData.put("logtype", "interactTestClose");
                        mLiveBll.umsAgentDebug(questionEventId, mData);
                    }
                    if (subjectResultPager != null) {
                        rlQuestionContent.removeView(subjectResultPager.getRootView());
                        subjectResultPager = null;
                    }
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.onUserBack();
                        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
                        voiceAnswerPager = null;
                        if (activity instanceof AudioRequest) {
                            AudioRequest audioRequest = (AudioRequest) activity;
                            audioRequest.release();
                        }
                    }
                    mLogtf.d("onBack:Verify");
                    if (haveExam) {
                        setHaveExam(false);
                    } else if (haveSpeech) {
                        setHaveSpeech(false);
                    } else {
                        setHaveWebQuestion(false);
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
        return canback;
    }

    public void stopExam(String num) {
        mExamAndBool.add("" + num);
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
        mLiveBll.getStuGoldCount();
    }

    @Override
    public void stopWebQuestion(BasePager pager, String testId) {
        if (pager instanceof QuestionWebPager) {
            mQueAndBool.add("" + testId);
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
        } else {
            subjectResultPager = null;
        }
        mLiveBll.getStuGoldCount();
    }

    public void umsAgentDebug(String eventId, final Map<String, String> mData) {
        mLiveBll.umsAgentDebug(eventId, mData);
    }

    public void umsAgentDebug2(String eventId, final Map<String, String> mData) {
        mLiveBll.umsAgentDebug2(eventId, mData);
    }

    public void umsAgentDebug3(String eventId, final Map<String, String> mData) {
        mLiveBll.umsAgentDebug3(eventId, mData);
    }

    @Override
    public void stopSpeech(BaseSpeechAssessmentPager pager, String num) {
        Loger.d(TAG, "stopSpeech:num=" + num);
        mQueAndBool.add("" + num);
        if (speechAssessmentPager != null) {
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
            if (speechAssessmentPager.getId().equals(num)) {
                setHaveSpeech(false);
            }
        }
        mLiveBll.getStuGoldCount();
    }

    @Override
    public void onSpeechSuccess(String num) {
        Loger.d(TAG, "onSpeechSuccess:num=" + num);
        mQueAndBool.add("" + num);
//        JSONObject object = new JSONObject();
//        try {
//            object.put("liveType", liveType);
//            object.put("vSectionID", mVSectionID);
//            object.put("testId", num);
//            mShareDataManager.put(QUESTION, object.toString(), ShareDataManager.SHAREDATA_USER);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
        mLiveBll.speechEval42IsAnswered(mVSectionID, num, isAnswered);
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
            if (activity instanceof AudioRequest) {
                AudioRequest audioRequest = (AudioRequest) activity;
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

    /** 填完互动题回调,提交测试题 */
    public PutQuestion mPutQuestion = new PutQuestion() {

        @Override
        public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity2, String result) {
            mLiveBll.liveSubmitTestAnswer((VideoQuestionLiveEntity) videoQuestionLiveEntity2, mVSectionID, result, false, null);
        }
    };

    private void showVoiceAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (voiceAnswerPager != null) {
            if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(videoQuestionLiveEntity.getvQuestionID())) {
                return;
            } else {
                voiceAnswerPager.setEnd();
                voiceAnswerPager.stopPlayer();
                rlQuestionContent.removeView(voiceAnswerPager.getRootView());
                voiceAnswerPager = null;
            }
        }
        JSONObject assess_ref = null;
        try {
            assess_ref = new JSONObject(videoQuestionLiveEntity.assess_ref);
        } catch (JSONException e) {
            mErrorVoiceQue.add(videoQuestionLiveEntity.id);
            showQuestion(videoQuestionLiveEntity);
        }
//        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
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
        VoiceAnswerPager voiceAnswerPager2 = new VoiceAnswerPager(activity, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch);
        voiceAnswerPager2.setIse(mIse);
        removeQuestionViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);
        voiceAnswerPager = voiceAnswerPager2;
        if (activity instanceof AudioRequest) {
            AudioRequest audioRequest = (AudioRequest) activity;
            audioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.setAudioRequest();
                    }
                }
            });
        }
    }

    QuestionSwitch questionSwitch = new QuestionSwitch() {

        @Override
        public BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity) {
            VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity1.type)) {
                if ("1".equals(videoQuestionLiveEntity1.choiceType)) {
                    showSelectQuestion(videoQuestionLiveEntity1);
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    return baseQuestionPager;
                } else {
                    showMulitSelectQuestion(videoQuestionLiveEntity1);
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    return baseQuestionPager;
                }
            } else if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
                showFillBlankQuestion(videoQuestionLiveEntity1);
                if (voiceAnswerPager != null) {
                    stopVoiceAnswerPager();
                }
                return baseQuestionPager;
            }
            return null;
        }

        @Override
        public void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, final OnQuestionGet onQuestionGet) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            mLiveBll.getQuestion(videoQuestionLiveEntity1, new AbstractBusinessDataCallBack() {

                @Override
                public void onDataSucess(Object... objData) {
                    onQuestionGet.onQuestionGet(videoQuestionLiveEntity1);
                }
            });
        }

        @Override
        public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, double voiceTime, String isSubmit, OnAnswerReslut answerReslut) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
            String testAnswer;
            if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
                testAnswer = "" + sorce;
            } else {
                testAnswer = result + ":" + sorce;
            }
            mLiveBll.liveSubmitTestAnswer(videoQuestionLiveEntity1, mVSectionID, testAnswer, true, answerReslut);
        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(VoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            rlQuestionContent.removeView(answerPager.getRootView());
            voiceAnswerPager = null;
            if (activity instanceof AudioRequest) {
                AudioRequest audioRequest = (AudioRequest) activity;
                audioRequest.release();
            }
//            if (rlVoiceQuestionContent != null) {
//                rlVoiceQuestionContent.removeAllViews();
//                bottomContent.removeView(rlVoiceQuestionContent);
//                rlVoiceQuestionContent = null;
//                if (activity instanceof AudioRequest) {
//                    AudioRequest audioRequest = (AudioRequest) activity;
//                    audioRequest.release();
//                }
//            }
        }
    };

    /** 填空题 */
    private void showFillBlankQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        if (isLand) {
            baseQuestionPager = new QuestionFillInBlankLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionFillInBlankPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
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

    /** 显示选择题 */
    private void showSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        if (isLand) {
            baseQuestionPager = new QuestionSelectLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionSelectPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
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

    /** 显示多选择题 */
    private void showMulitSelectQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        long before = System.currentTimeMillis();
        if (isLand) {
            baseQuestionPager = new QuestionMulitSelectLivePager(activity, videoQuestionLiveEntity);
        } else {
            baseQuestionPager = new QuestionMulitSelectPortLivePager(activity, videoQuestionLiveEntity);
        }
        baseQuestionPager.setPutQuestion(mPutQuestion);
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

    /** 文科主观题 */
    private void showSubjectiveQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        baseQuestionPager = new QuestionSubjectivePager(activity, videoQuestionLiveEntity);
        baseQuestionPager.setPutQuestion(mPutQuestion);
        removeQuestionViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(baseQuestionPager.getRootView(), params);
    }

    /** 创建互动题作答，抢红包结果提示PopupWindow */
    private void initQuestionAnswerReslut(View popupWindow_view) {
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

    /** 试题布局隐藏 */
    private void questionViewGone(boolean delay) {
        mIsShowQuestion = false;
        liveMessageBll.showInput();
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

    /** 试题布局隐藏 */
    private void understandViewGone() {
        mIsShowUnderstand = false;
        if (videoQuestionLiveEntity != null) {
            showQuestion(videoQuestionLiveEntity);
        } else if (mLiveTopic != null && mLiveTopic.getVideoQuestionLiveEntity() != null) {
            VideoQuestionLiveEntity videoQuestionLiveEntity = mLiveTopic.getVideoQuestionLiveEntity();
            showQuestion(videoQuestionLiveEntity);
        } else {
            removeQuestionViews();
        }
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

    /** 语音答题选择题回答正确 */
    private void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题填空题回答正确 */
    private void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(activity, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 互动题回答错误 */
    private void initAnswerWrongResult() {
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_answer_wrong, null, false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 互动题回答部分正确 */
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

    /** 移除除了战况以外的view */
    private void removeQuestionViews() {
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
        mLiveBll.getSpeechEval(id, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval
            onSpeechEval) {
        mLiveBll.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval) {
        mLiveBll.sendSpeechEvalResult2(id, stuAnswer, onSpeechEval);
    }

    public void onPause() {
        if (speechAssessmentPager != null) {
            speechAssessmentPager.stopPlayer();
        }
    }

    private void stopVoiceAnswerPager() {
        voiceAnswerPager.stopPlayer();
        rlQuestionContent.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
        if (activity instanceof AudioRequest) {
            AudioRequest audioRequest = (AudioRequest) activity;
            audioRequest.release();
        }
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        if (voiceAnswerPager != null) {
            voiceAnswerPager.onNetWorkChange(netWorkType);
        }
    }
}