package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.EnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2017/3/25.
 * 英语h5课件业务类
 */
public class EnglishH5CoursewareBll implements EnglishH5CoursewareAction, LiveAndBackDebug {
    String TAG = "EH5CoursewareBll";
    String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());
    /** 互动题作答成功的布局 */
    private RelativeLayout rlQuestionResContent;
    EnglishH5CoursewarePager h5CoursewarePager;
    private EnglishH5CoursewarePager curPager;
    private VoiceAnswerPager voiceAnswerPager;
    private LogToFile logToFile;
    RelativeLayout bottomContent;
    /** 语音强制提交，外层 */
    private RelativeLayout rlVoiceQuestionContent;
    LiveVideoActivityBase liveVideoActivityBase;
    /** 存英语h5 */
    private static final String ENGLISH_H5 = "live_english_h5";
    /** 英语h5的暂存状态 */
    private HashSet<String> mH5AndBool = new HashSet<>();
    /** 语音答题错误 */
    private HashSet<String> mErrorVoiceQue = new HashSet<>();
    /** 直播类型 */
    private int liveType;
    /** 直播id */
    private String mVSectionID;
    protected ShareDataManager mShareDataManager;
    private LiveBll mLiveBll;
    SpeechEvaluatorUtils mIse;
    private AnswerRankBll mAnswerRankBll;
    /**智能私信业务*/
    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private boolean hasQuestion;
    private long submitTime;
    private boolean hasSubmit;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;

    public void setAnswerRankBll(AnswerRankBll answerRankBll) {
        mAnswerRankBll = answerRankBll;
    }

    public EnglishH5CoursewareBll(Context context) {
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.context = context;
        liveVideoActivityBase = (LiveVideoActivityBase) context;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
        rlQuestionResContent = new RelativeLayout(context);
        bottomContent.addView(rlQuestionResContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (h5CoursewarePager != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bottomContent.addView(h5CoursewarePager.getRootView(), lp);
        }
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    public void setShareDataManager(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
    }

    public void setLiveAutoNoticeBll(LiveAutoNoticeBll liveAutoNoticeBll) {
        mLiveAutoNoticeBll = liveAutoNoticeBll;
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        this.mIse = ise;
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        this.liveVideoSAConfig = liveVideoSAConfig;
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
    }

    public void initData() {
        try {
            JSONObject jsonObject = new JSONObject(mShareDataManager.getString(ENGLISH_H5, "{}", ShareDataManager.SHAREDATA_USER));
            int jsonLiveType = jsonObject.optInt("liveType");
            if (jsonLiveType == liveType) {
                String vSectionID = jsonObject.optString("vSectionID");
                if (mVSectionID.equals(vSectionID)) {
                    String url = jsonObject.optString("url");
                    if (!StringUtils.isSpace(url)) {
                        mH5AndBool.add(url);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onBack() {
        if (h5CoursewarePager != null) {
            if (h5CoursewarePager.isFinish) {
                h5CoursewarePager.close();
            } else {
                VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, (BaseApplication)
                        BaseApplication.getContext(), false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (h5CoursewarePager != null) {
                            mH5AndBool.add(h5CoursewarePager.getUrl());
                            h5CoursewarePager.onBack();
                            h5CoursewarePager.destroy();
                            bottomContent.removeView(h5CoursewarePager.getRootView());
                            h5CoursewarePager = null;
                            if (context instanceof WebViewRequest) {
                                WebViewRequest webViewRequest = (WebViewRequest) context;
                                webViewRequest.releaseWebView();
                            }
                        }
                    }
                });
                cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                        VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            }
            return true;
        }
        if (voiceAnswerPager != null) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, (BaseApplication)
                    BaseApplication.getContext(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.onUserBack();
                        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) voiceAnswerPager.getBaseVideoQuestionEntity();
                        mH5AndBool.add(videoQuestionLiveEntity.url);
                        stopVoiceAnswerPager();
                    }
                }
            });
            cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            return true;
        }
        return false;
    }

    public void onPause() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.onPause();
        }
    }

    public void destroy() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.destroy();
        }
    }

    public void onResume() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.onResume();
        }
    }

    @Override
    public void onH5Courseware(final String status, final VideoQuestionLiveEntity videoQuestionLiveEntity) {
//        logToFile.i("onH5Courseware:url=" + url + ",status=" + status);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if ("on".equals(status)) {
                    if (!"1".equals(videoQuestionLiveEntity.getIsVoice()) || mErrorVoiceQue.contains(videoQuestionLiveEntity.url)) {
                        hasQuestion = true;
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.showRankList(new ArrayList<RankUserEntity>(),XESCODE.ENGLISH_H5_COURSEWARE);
                            mLiveBll.sendRankMessage(XESCODE.RANK_STU_RECONNECT_MESSAGE);
                        }
                    }
                    if (mH5AndBool.contains(videoQuestionLiveEntity.url)) {
                        logToFile.i("onH5Courseware:url.contains");
                        return;
                    }
                    if (voiceAnswerPager != null) {
                        VideoQuestionLiveEntity baseVideoQuestionEntity = (VideoQuestionLiveEntity) voiceAnswerPager.getBaseVideoQuestionEntity();
                        if (baseVideoQuestionEntity.id.equals(videoQuestionLiveEntity.id)) {
                            return;
                        } else {
                            stopVoiceAnswerPager();
                        }
                    }
                    if (h5CoursewarePager != null) {
                        if (h5CoursewarePager.getUrl().equals(videoQuestionLiveEntity.url)) {
                            logToFile.i("onH5Courseware:url.equals");
                            return;
                        } else {
                            logToFile.i("onH5Courseware:url=" + h5CoursewarePager.getUrl());
                            bottomContent.removeView(h5CoursewarePager.getRootView());
                        }
                    }
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice()) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.url)) {
                        try {
                            showVoiceAnswer(videoQuestionLiveEntity);
                        } catch (Exception e) {
                            logToFile.d("onH5Courseware:showVoiceAnswer.error1=" + e.getMessage());
                            mErrorVoiceQue.add(videoQuestionLiveEntity.url);
                            showH5Paper(videoQuestionLiveEntity);
                        }
                    } else {
                        showH5Paper(videoQuestionLiveEntity);
                    }
                } else {
                    if (voiceAnswerPager != null && !voiceAnswerPager.isEnd()) {
                        final VoiceAnswerPager answerPager = voiceAnswerPager;
//                        voiceAnswerPager = null;
//                        rlVoiceQuestionContent = new RelativeLayout(liveVideoActivityBase);
//                        View view = answerPager.getRootView();
//                        ViewGroup.LayoutParams lp = view.getLayoutParams();
//                        bottomContent.removeView(view);
//                        rlVoiceQuestionContent.addView(view, lp);
//                        bottomContent.addView(rlVoiceQuestionContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.MATCH_PARENT));
                        answerPager.examSubmitAll("onH5Courseware", videoQuestionLiveEntity.nonce);
                    }
                    int delayTime = 0;
                    int isForce=0;
                    if (h5CoursewarePager != null) {
                        curPager = h5CoursewarePager;
                        h5CoursewarePager.submitData();
                        logToFile.i("onH5Courseware:submitData");
//                        liveVideoActivityBase.setAutoOrientation(true);
//                        bottomContent.removeView(h5CoursewarePager.getRootView());
//                        h5CoursewarePager = null;
                        if (context instanceof WebViewRequest) {
                            WebViewRequest webViewRequest = (WebViewRequest) context;
                            webViewRequest.releaseWebView();
                        }
                        delayTime = 3000;
                        isForce=1;
                    }
                    if(hasQuestion) {
                        getFullMarkList(delayTime);
                        getAutoNotice(isForce);
                        hasQuestion=false;
                    }
                }
            }
        });
    }

    private void showH5Paper(final VideoQuestionLiveEntity videoQuestionH5Entity) {

        logToFile.i("onH5Courseware:url=" + videoQuestionH5Entity.url);
        StableLogHashMap logHashMap = new StableLogHashMap("receiveCourseware");
        logHashMap.put("coursewareid", videoQuestionH5Entity.id);
        logHashMap.put("coursewaretype", videoQuestionH5Entity.courseware_type);
        logHashMap.put("loadurl", videoQuestionH5Entity.url);
        mLiveBll.umsAgentDebug2(eventId, logHashMap.getData());
        h5CoursewarePager = new EnglishH5CoursewarePager(context, false, mVSectionID, videoQuestionH5Entity.url, videoQuestionH5Entity.id,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, new OnH5ResultClose() {
            @Override
            public void onH5ResultClose() {
                if (h5CoursewarePager == null) {
                    return;
                }
                mH5AndBool.add(h5CoursewarePager.getUrl());
                try {
                    JSONObject object = new JSONObject();
                    object.put("liveType", liveType);
                    object.put("vSectionID", mVSectionID);
                    object.put("url", videoQuestionH5Entity.url);
                    mShareDataManager.put(ENGLISH_H5, object.toString(), ShareDataManager.SHAREDATA_USER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                h5CoursewarePager.destroy();
                bottomContent.removeView(h5CoursewarePager.getRootView());
                h5CoursewarePager = null;
                mLiveBll.getStuGoldCount();
                if (context instanceof WebViewRequest) {
                    WebViewRequest webViewRequest = (WebViewRequest) context;
                    webViewRequest.releaseWebView();
                }
            }
        }, this, mAnswerRankBll == null ? "0" : mAnswerRankBll.getIsShow(), IS_SCIENCE);
        h5CoursewarePager.setEnglishH5CoursewareBll(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomContent.addView(h5CoursewarePager.getRootView(), lp);
        if (context instanceof WebViewRequest) {
            WebViewRequest webViewRequest = (WebViewRequest) context;
            webViewRequest.requestWebView();
        }
    }

    private void showVoiceAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (voiceAnswerPager != null) {
            if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(videoQuestionLiveEntity.getvQuestionID())) {
                return;
            } else {
                voiceAnswerPager.setEnd();
                voiceAnswerPager.stopPlayer();
                bottomContent.removeView(voiceAnswerPager.getRootView());
                voiceAnswerPager = null;
            }
        }
        JSONObject assess_ref = null;
        try {
            assess_ref = new JSONObject(videoQuestionLiveEntity.assess_ref);
        } catch (JSONException e) {
            mErrorVoiceQue.add(videoQuestionLiveEntity.url);
            showH5Paper(videoQuestionLiveEntity);
            return;
        }
        VoiceAnswerPager voiceAnswerPager2 = new VoiceAnswerPager(context, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.questiontype, questionSwitch, this);
        voiceAnswerPager2.setIse(mIse);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        bottomContent.addView(voiceAnswerPager2.getRootView(), params);
        voiceAnswerPager = voiceAnswerPager2;
        if (context instanceof AudioRequest) {
            AudioRequest audioRequest = (AudioRequest) context;
            audioRequest.request(new AudioRequest.OnAudioRequest() {
                @Override
                public void requestSuccess() {
                    if (voiceAnswerPager != null) {
                        voiceAnswerPager.setAudioRequest();
                    }
                }
            });
        }
        VoiceAnswerLog.sno2H5Ware(mLiveBll, videoQuestionLiveEntity.type, videoQuestionLiveEntity.id, videoQuestionLiveEntity.nonce);
    }

    QuestionSwitch questionSwitch = new QuestionSwitch() {

        @Override
        public String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity) {
            return "h5ware";
        }

        @Override
        public BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity) {
            VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            if (voiceAnswerPager != null) {
                stopVoiceAnswerPager();
            }
            showH5Paper(videoQuestionLiveEntity1);
            return h5CoursewarePager;
        }

        @Override
        public void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, final OnQuestionGet onQuestionGet) {
//            final VideoQuestionH5Entity videoQuestionLiveEntity1 = (VideoQuestionH5Entity) baseQuestionEntity;
//            mLiveBll.getQuestion(videoQuestionLiveEntity1, new AbstractBusinessDataCallBack() {
//
//                @Override
//                public void onDataSucess(Object... objData) {
//                    onQuestionGet.onQuestionGet(videoQuestionLiveEntity1);
//                }
//            });
        }

        @Override
        public void onPutQuestionResult(final BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, final OnAnswerReslut answerReslut) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
            JSONObject answerObj = new JSONObject();
            JSONArray answerAnswer = new JSONArray();
            try {
                answerObj.put("id", videoQuestionLiveEntity1.id);
                answerObj.put("answer", answer);
                if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
                    answerObj.put("useranswer", sorce);
                } else {
                    answerObj.put("useranswer", result + ":" + sorce);
                }
                answerObj.put("type", videoQuestionLiveEntity1.type);
                answerObj.put("url", "");
                answerObj.put("voiceTime", "" + voiceTime);
                answerAnswer.put(answerObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String testAnswer = "";
            testAnswer = answerAnswer.toString();
            mLiveBll.liveSubmitTestH5Answer(videoQuestionLiveEntity1, mVSectionID, testAnswer, videoQuestionLiveEntity1.courseware_type, isSubmit, voiceTime, isRight, new OnAnswerReslut() {

                @Override
                public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                    answerReslut.onAnswerReslut(baseVideoQuestionEntity, entity);
                    if (entity != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
                        int type = entity.getResultType();
                        if (entity.getIsAnswer() == 1) {
                            XESToastUtils.showToast(context, "您已经答过此题");
                        } else {
                            // 回答正确提示
                            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity1.questiontype)) {
                                    initSelectAnswerRightResultVoice(entity);
                                } else {
                                    initFillinAnswerRightResultVoice(entity);
                                }
//                                isSuccess = true;
                                // 回答错误提示
                            } else if (entity.getResultType() == QUE_RES_TYPE2) {
                                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity1.questiontype)) {
                                    initSelectAnswerWrongResultVoice(entity);
                                } else {
                                    initFillAnswerWrongResultVoice(entity);
                                }
                            }
                            StableLogHashMap logHashMap = new StableLogHashMap("showResultDialog");
                            logHashMap.put("testid", "" + baseVideoQuestionEntity.getvQuestionID());
                            logHashMap.put("sourcetype", "h5ware").addNonce(baseVideoQuestionEntity.nonce);
                            logHashMap.addExY().addExpect("0").addSno("5").addStable("1");
                            umsAgentDebug3(voicequestionEventId, logHashMap.getData());
                        }
                    }
                    if (voiceAnswerPager != null) {
                        stopVoiceAnswerPager();
                    }
                    mH5AndBool.add(videoQuestionLiveEntity1.url);
                    try {
                        JSONObject object = new JSONObject();
                        object.put("liveType", liveType);
                        object.put("vSectionID", mVSectionID);
                        object.put("url", videoQuestionLiveEntity1.url);
                        mShareDataManager.put(ENGLISH_H5, object.toString(), ShareDataManager.SHAREDATA_USER);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnswerFailure() {
                    answerReslut.onAnswerFailure();
                }
            });
        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(VoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            bottomContent.removeView(answerPager.getRootView());
            if (context instanceof AudioRequest) {
                AudioRequest audioRequest = (AudioRequest) context;
                audioRequest.release();
            }
//            if (rlVoiceQuestionContent != null) {
//                rlVoiceQuestionContent.removeAllViews();
//                bottomContent.removeView(rlVoiceQuestionContent);
//                rlVoiceQuestionContent = null;
//                if (context instanceof AudioRequest) {
//                    AudioRequest audioRequest = (AudioRequest) context;
//                    audioRequest.releaseWebView();
//                }
//            }
        }
    };

    private void stopVoiceAnswerPager() {
        voiceAnswerPager.stopPlayer();
        bottomContent.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
        if (context instanceof AudioRequest) {
            AudioRequest audioRequest = (AudioRequest) context;
            audioRequest.release();
        }
    }

    private void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    private void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    private void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    private void initQuestionAnswerReslut(final View popupWindow_view) {
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

    /**
     * 回答问题结果提示框延迟三秒消失
     */
    public void disMissAnswerResult() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlQuestionResContent.removeAllViews();
            }
        }, 3000);
    }

    public void setVideoLayout(int width, int height) {
        if (voiceAnswerPager != null) {
            final View contentView = ((Activity) context).findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            if (width > 0) {
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
                wradio += (screenWidth - width) / 2;
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
    public void onNetWorkChange(int netWorkType) {
        if (voiceAnswerPager != null) {
            voiceAnswerPager.onNetWorkChange(netWorkType);
        }
    }

    @Override
    public void umsAgentDebug(String eventId, Map<String, String> mData) {
        mLiveBll.umsAgentDebug(eventId, mData);
    }

    @Override
    public void umsAgentDebug2(String eventId, Map<String, String> mData) {
        mLiveBll.umsAgentDebug2(eventId, mData);
    }

    @Override
    public void umsAgentDebug3(String eventId, Map<String, String> mData) {
        mLiveBll.umsAgentDebug3(eventId, mData);
    }

    public interface OnH5ResultClose {
        void onH5ResultClose();
    }

    private void getFullMarkList(final int delayTime) {
        if (mAnswerRankBll == null) {
            return;
        }
        /*if (hasQuestion) {
            hasQuestion = false;
        } else {
            return;
        }*/
        //hasSubmit=false;
        mAnswerRankBll.getFullMarkListH5(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                /*long cur = System.currentTimeMillis();
                long delay = 3000;
                if (submitTime != 0 && cur - submitTime > 3000) {
                    delay = 0;
                } else if (submitTime != 0) {
                    delay = 3000 - (cur - submitTime);
                }
                submitTime=0;*/
                final List<FullMarkListEntity> lst = new ArrayList<>();
                try {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    List<FullMarkListEntity> tmplst = JSON.parseArray(jsonObject.optString("ranks"), FullMarkListEntity.class);
                    if (tmplst != null) {
                        lst.addAll(tmplst);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MobAgent.httpResponseParserError(TAG, "getFullMarkListH5", e.getMessage());
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (h5CoursewarePager != null) {
                                if (h5CoursewarePager == curPager) {
                                    bottomContent.removeView(h5CoursewarePager.getRootView());
                                    h5CoursewarePager = null;
                                    curPager = null;
                                } else if (curPager != null) {
                                    bottomContent.removeView(curPager.getRootView());
                                    curPager = null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAnswerRankBll.showFullMarkList(lst, XESCODE.ENGLISH_H5_COURSEWARE);
                    }
                }, delayTime);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (h5CoursewarePager != null) {
                                bottomContent.removeView(h5CoursewarePager.getRootView());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAnswerRankBll.showFullMarkList(new ArrayList<FullMarkListEntity>());
                    }
                }, delayTime);*/
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.hideRankList();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                /*if(mAnswerRankBll==null) {
                    return;
                }
                super.onPmError(responseEntity);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (h5CoursewarePager != null) {
                                bottomContent.removeView(h5CoursewarePager.getRootView());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAnswerRankBll.showFullMarkList(new ArrayList<FullMarkListEntity>());
                    }
                }, delayTime);*/
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.hideRankList();
                }
            }
        });
    }

    public void onSubmit() {
        if (mAnswerRankBll == null) {
            return;
        }
        submitTime = System.currentTimeMillis();
        mLiveBll.sendRankMessage(XESCODE.RANK_STU_MESSAGE);
        /*if(isShowFullMarkList){
            getFullMarkList(3000);
        }else{
            hasSubmit=true;
        }*/
    }
    /**
     * 获取智能私信
     */
    public void getAutoNotice(final int isForce){
        if(mLiveAutoNoticeBll==null){
            return;
        }
        /*if (hasQuestion) {
            hasQuestion = false;
        } else {
            return;
        }*/
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLiveAutoNoticeBll.getAutoNotice(isForce,5);
            }
        },10000);

    }
}
