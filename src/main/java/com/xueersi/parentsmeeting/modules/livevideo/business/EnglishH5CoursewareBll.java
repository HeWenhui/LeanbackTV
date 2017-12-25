package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.page.EnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2017/3/25.
 * 英语h5课件业务类
 */
public class EnglishH5CoursewareBll implements EnglishH5CoursewareAction {
    String TAG = "EH5CoursewareBll";
    String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());
    /** 互动题作答成功的布局 */
    private RelativeLayout rlQuestionResContent;
    EnglishH5CoursewarePager h5CoursewarePager;
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

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        this.mIse = ise;
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
                    if ("1".equals(videoQuestionLiveEntity.isVoice) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.url)) {
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
                        answerPager.examSubmitAll("onH5Courseware");
                    }
                    if (h5CoursewarePager != null) {
                        h5CoursewarePager.submitData();
                        logToFile.i("onH5Courseware:submitData");
//                        liveVideoActivityBase.setAutoOrientation(true);
//                        bottomContent.removeView(h5CoursewarePager.getRootView());
//                        h5CoursewarePager = null;
                    }
                }
            }
        });
    }

    private void showH5Paper(final VideoQuestionLiveEntity videoQuestionH5Entity) {
        logToFile.i("onH5Courseware:url=" + videoQuestionH5Entity.url);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "receiveCourseware");
        mData.put("coursewareid", videoQuestionH5Entity.id);
        mData.put("coursewaretype", videoQuestionH5Entity.courseware_type);
        mData.put("loadurl", videoQuestionH5Entity.url);
        mLiveBll.umsAgentDebug2(eventId, mData);
        h5CoursewarePager = new EnglishH5CoursewarePager(context, false, new OnH5ResultClose() {
            @Override
            public void onH5ResultClose() {
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
        }, videoQuestionH5Entity.url, videoQuestionH5Entity.id, videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomContent.addView(h5CoursewarePager.getRootView(), lp);
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
        VoiceAnswerPager voiceAnswerPager2 = new VoiceAnswerPager(context, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.questiontype, questionSwitch);
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
    }

    QuestionSwitch questionSwitch = new QuestionSwitch() {

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
        public void onPutQuestionResult(final BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, double voiceTime, String isSubmit, final OnAnswerReslut answerReslut) {
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
            mLiveBll.liveSubmitTestH5Answer(videoQuestionLiveEntity1, mVSectionID, testAnswer, videoQuestionLiveEntity1.courseware_type, isSubmit, voiceTime, new OnAnswerReslut() {

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
//                    audioRequest.release();
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

    @Override
    public void onNetWorkChange(int netWorkType) {
        if (voiceAnswerPager != null) {
            voiceAnswerPager.onNetWorkChange(netWorkType);
        }
    }

    public interface OnH5ResultClose {
        void onH5ResultClose();

        void umsAgentDebug(String eventId, final Map<String, String> mData);

        void umsAgentDebug2(String eventId, final Map<String, String> mData);

        void umsAgentDebug3(String eventId, final Map<String, String> mData);
    }
}