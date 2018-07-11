package com.xueersi.parentsmeeting.modules.livevideo.question.business;

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
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.EnglishH5CoursewareX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by linyuqiang on 2017/3/25.
 * 英语h5课件业务类
 */
public class EnglishH5CoursewareBll implements EnglishH5CoursewareAction, LiveAndBackDebug, BaseVoiceAnswerCreat.AnswerRightResultVoice {
    String TAG = "EnglishH5CoursewareBll";
    String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());
    /** 互动题作答成功的布局 */
    private RelativeLayout rlQuestionResContent;
    BaseEnglishH5CoursewarePager h5CoursewarePager;
    private BaseEnglishH5CoursewarePager curPager;
    private BaseVoiceAnswerPager voiceAnswerPager;
    /** 创建语音答题 */
    private BaseVoiceAnswerCreat baseVoiceAnswerCreat;
    private LogToFile logToFile;
    RelativeLayout bottomContent;
    /** 语音强制提交，外层 */
    private RelativeLayout rlVoiceQuestionContent;
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
    private LiveGetInfo mGetInfo;
    private EnglishH5CoursewareHttp mLiveBll;
    private LiveAndBackDebug liveAndBackDebug;
    SpeechEvaluatorUtils mIse;
    private AnswerRankBll mAnswerRankBll;
    /** 智能私信业务 */
    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private boolean hasQuestion;
    private boolean isAnaswer = false;
    private ArrayList<QuestionShowAction> questionShowActions = new ArrayList<>();
    private long submitTime;
    private boolean hasSubmit;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;
    private boolean isTeamPkAllowed = false;
    private boolean webViewCloseByTeacher = false;

    public boolean isWebViewCloseByTeacher() {
        return webViewCloseByTeacher;
    }

    public void setWebViewCloseByTeacher(boolean webViewCloseByTeacher) {
        if (h5CoursewarePager != null) {
            this.webViewCloseByTeacher = webViewCloseByTeacher;
        }
    }

    public void setAnswerRankBll(AnswerRankBll answerRankBll) {
        mAnswerRankBll = answerRankBll;
    }

    public EnglishH5CoursewareBll(Context context) {
        logToFile = new LogToFile(context, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        this.context = context;
        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
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

    public void setLiveBll(EnglishH5CoursewareHttp mLiveBll) {
        this.mLiveBll = mLiveBll;
        if (mLiveBll instanceof LiveAndBackDebug) {
            liveAndBackDebug = (LiveAndBackDebug) mLiveBll;
        }
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
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
            if (h5CoursewarePager.isFinish()) {
                h5CoursewarePager.close();
                onQuestionShow(false);
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
                        mH5AndBool.add(videoQuestionLiveEntity.getUrl());
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


    public void setTeamPkAllowed(boolean teamPkAllowed) {
        isTeamPkAllowed = teamPkAllowed;
    }

    @Override
    public void onH5Courseware(final String status, final VideoQuestionLiveEntity videoQuestionLiveEntity) {
//        logToFile.i("onH5Courseware:url=" + url + ",status=" + status);
        handler.post(new Runnable() {
            @Override
            public void run() {
//                if (LiveVideoConfig.isSend) {
//                    showH5Paper(videoQuestionLiveEntity);
//                    return;
//                }
                if ("on".equals(status)) {
                    if (!isAnaswer) {
                        for (QuestionShowAction questionShowAction : questionShowActions) {
                            onQuestionShow(true);
                        }
                    }
                    isAnaswer = true;
                    if (!"1".equals(videoQuestionLiveEntity.getIsVoice()) || mErrorVoiceQue.contains(videoQuestionLiveEntity.getUrl())) {
                        hasQuestion = true;
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.showRankList(new ArrayList<RankUserEntity>(), XESCODE.ENGLISH_H5_COURSEWARE);
                            mLiveBll.sendRankMessage(XESCODE.RANK_STU_RECONNECT_MESSAGE);
                        }
                    }
                    if (mH5AndBool.contains(videoQuestionLiveEntity.getUrl())) {
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
                        if (h5CoursewarePager.getUrl().equals(videoQuestionLiveEntity.getUrl())) {
                            logToFile.i("onH5Courseware:url.equals:" + h5CoursewarePager.getUrl());
                            return;
                        } else {
                            logToFile.i("onH5Courseware:url=" + h5CoursewarePager.getUrl());
                            bottomContent.removeView(h5CoursewarePager.getRootView());
                        }
                    }
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice()) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.getUrl())) {
                        try {
                            showVoiceAnswer(videoQuestionLiveEntity);
                        } catch (Exception e) {
                            logToFile.d("onH5Courseware:showVoiceAnswer.error1=" + e.getMessage());
                            mErrorVoiceQue.add(videoQuestionLiveEntity.getUrl());
                            showH5Paper(videoQuestionLiveEntity);
                        }
                    } else {
                        showH5Paper(videoQuestionLiveEntity);
                    }
                } else {
                    boolean havePager = false;
                    if (voiceAnswerPager != null && !voiceAnswerPager.isEnd()) {
//                        voiceAnswerPager = null;
//                        rlVoiceQuestionContent = new RelativeLayout(liveVideoActivityBase);
//                        View view = answerPager.getRootView();
//                        ViewGroup.LayoutParams lp = view.getLayoutParams();
//                        bottomContent.removeView(view);
//                        rlVoiceQuestionContent.addView(view, lp);
//                        bottomContent.addView(rlVoiceQuestionContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.MATCH_PARENT));
                        voiceAnswerPager.examSubmitAll("onH5Courseware", videoQuestionLiveEntity.nonce);
                        havePager = true;
                    }
                    int delayTime = 0;
                    int isForce = 0;
                    if (h5CoursewarePager != null) {
                        havePager = true;
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
                        isForce = 1;
                    }
                    if (isAnaswer && !havePager) {
                        onQuestionShow(false);
                    }
                    isAnaswer = false;
                    if (hasQuestion) {
                        getFullMarkList(delayTime);
                        getAutoNotice(isForce);
                        hasQuestion = false;
                    }
                    closePageByTeamPk();
                }
            }
        });
    }

    private boolean isPageOnCloseing = false;

    /**
     * 战队pk 自动关闭答题结果页
     */
    private void closePageByTeamPk() {
        // Log.e("EnglishH5CoursewareBll","=======>closePageByTeamPk:"+isTeamPkAllowed);
        if (isTeamPkAllowed && !isPageOnCloseing) {
            isPageOnCloseing = true;
            bottomContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (h5CoursewarePager != null && h5CoursewarePager == curPager) {
                        bottomContent.removeView(h5CoursewarePager.getRootView());
                    }
                    isPageOnCloseing = false;
                }
            }, 6000);
        }
    }


    private void showH5Paper(final VideoQuestionLiveEntity videoQuestionH5Entity) {
        logToFile.i("onH5Courseware:url=" + videoQuestionH5Entity.getUrl());
        StableLogHashMap logHashMap = new StableLogHashMap("receiveCourseware");
        logHashMap.put("coursewareid", videoQuestionH5Entity.id);
        logHashMap.put("coursewaretype", videoQuestionH5Entity.courseware_type);
        logHashMap.put("loadurl", videoQuestionH5Entity.getUrl());
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
        OnH5ResultClose onH5ResultClose = new OnH5ResultClose() {
            @Override
            public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager) {
                if (h5CoursewarePager == null) {
                    return;
                }
                mH5AndBool.add(baseEnglishH5CoursewarePager.getUrl());
                try {
                    JSONObject object = new JSONObject();
                    object.put("liveType", liveType);
                    object.put("vSectionID", mVSectionID);
                    object.put("url", videoQuestionH5Entity.getUrl());
                    mShareDataManager.put(ENGLISH_H5, object.toString(), ShareDataManager.SHAREDATA_USER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                baseEnglishH5CoursewarePager.destroy();
                bottomContent.removeView(baseEnglishH5CoursewarePager.getRootView());
                Loger.d(TAG, "onH5ResultClose:same=" + (baseEnglishH5CoursewarePager == h5CoursewarePager));
                if (baseEnglishH5CoursewarePager == h5CoursewarePager) {
                    h5CoursewarePager = null;
                }
                if (!isAnaswer) {
                    onQuestionShow(false);
                }
                mLiveBll.getStuGoldCount();

                // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
                // EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));

                if (context instanceof WebViewRequest) {
                    WebViewRequest webViewRequest = (WebViewRequest) context;
                    webViewRequest.releaseWebView();
                }
            }
        };
//        h5CoursewarePager = new EnglishH5CoursewarePager(context, false, mVSectionID, videoQuestionH5Entity.url, videoQuestionH5Entity.id,
//                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, this, mAnswerRankBll == null ? "0" : mAnswerRankBll.getIsShow(), IS_SCIENCE);
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        h5CoursewarePager = new EnglishH5CoursewareX5Pager(context, false, mVSectionID, videoQuestionH5Entity.id, englishH5Entity,
                videoQuestionH5Entity.courseware_type, videoQuestionH5Entity.nonce, onH5ResultClose, this, mAnswerRankBll == null ? "0" : mAnswerRankBll.getIsShow(), IS_SCIENCE);
        h5CoursewarePager.setEnglishH5CoursewareBll(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomContent.addView(h5CoursewarePager.getRootView(), lp);
        if (context instanceof WebViewRequest) {
            WebViewRequest webViewRequest = (WebViewRequest) context;
            webViewRequest.requestWebView();
        }
    }

    public void setBaseVoiceAnswerCreat(BaseVoiceAnswerCreat baseVoiceAnswerCreat) {
        this.baseVoiceAnswerCreat = baseVoiceAnswerCreat;
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
            mErrorVoiceQue.add(videoQuestionLiveEntity.getUrl());
            showH5Paper(videoQuestionLiveEntity);
            return;
        }
        BaseVoiceAnswerPager voiceAnswerPager2 =
                baseVoiceAnswerCreat.create(context, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.type, bottomContent, mIse, liveAndBackDebug);
//        voiceAnswerPager2.setIse(mIse);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        int screenWidth = ScreenUtils.getScreenWidth();
//        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
//        params.rightMargin = wradio;
//        bottomContent.addView(voiceAnswerPager2.getRootView(), params);
        voiceAnswerPager = voiceAnswerPager2;
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(context, AudioRequest.class);
        if (audioRequest != null) {
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

    private void stopVoiceAnswerPager() {
        boolean isEnd = voiceAnswerPager.isEnd();
        voiceAnswerPager.stopPlayer();
        bottomContent.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(context, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        if (isEnd) {
            onQuestionShow(false);
        }
    }

    @Override
    public void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    @Override
    public void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    @Override
    public void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /** 语音答题回答错误 */
    @Override
    public void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(context, entity);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    @Override
    public void initQuestionAnswerReslut(final View popupWindow_view) {
        bottomContent.removeView(rlQuestionResContent);
        bottomContent.addView(rlQuestionResContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                bottomContent.removeView(voiceAnswerPager2.getRootView());
                voiceAnswerPager = null;
                onQuestionShow(false);
            }
        } else {
            bottomContent.removeView(voiceAnswerPager2.getRootView());
        }
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
        if (voiceAnswerPager instanceof VoiceAnswerPager) {
            final View contentView = ((Activity) context).findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            if (width > 0) {
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
                wradio += (screenWidth - width) / 2;
                baseVoiceAnswerCreat.setViewLayoutParams(voiceAnswerPager, wradio);
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
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        liveAndBackDebug.umsAgentDebugInter(eventId, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        liveAndBackDebug.umsAgentDebugPv(eventId, mData);
    }

    public interface OnH5ResultClose {
        void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager);
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
                                BaseEnglishH5CoursewarePager oldh5CoursewarePager = h5CoursewarePager;
                                if (h5CoursewarePager == curPager) {
                                    bottomContent.removeView(h5CoursewarePager.getRootView());
                                    h5CoursewarePager = null;
                                    curPager = null;
                                    if (oldh5CoursewarePager.isFinish()) {
                                        onQuestionShow(false);
                                    }
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
    public void getAutoNotice(final int isForce) {
        if (mLiveAutoNoticeBll == null) {
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
                mLiveAutoNoticeBll.getAutoNotice(0, 5);
            }
        }, (int) (7000 + Math.random() * 4000));
    }

    public void registQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.add(questionShowAction);
    }

    public void unRegistQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.remove(questionShowAction);
    }

    /** 直播收到答题切换 */
    public class LiveStandQuestionSwitchImpl extends LiveQuestionSwitchImpl implements LiveStandQuestionSwitch {
        @Override
        public BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity) {
            BaseEnglishH5CoursewarePager h5CoursewarePager = (BaseEnglishH5CoursewarePager) super.questionSwitch(baseQuestionEntity);
            if (h5CoursewarePager != null) {
                h5CoursewarePager.setWebBackgroundColor(0);
                return h5CoursewarePager.getBasePager();
            }
            return null;
        }

        @Override
        public void getTestAnswerTeamStatus(BaseVideoQuestionEntity videoQuestionLiveEntity, AbstractBusinessDataCallBack callBack) {
            if (!"-1".equals(mGetInfo.getRequestTime())) {
                final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
                mLiveBll.getTestAnswerTeamStatus(videoQuestionLiveEntity1, callBack);
            }
        }

        @Override
        public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
            baseVoiceAnswerCreat.onAnswerReslut(context, EnglishH5CoursewareBll.this, voiceAnswerPager, baseVideoQuestionEntity, entity);
        }

        @Override
        public long getRequestTime() {
            try {
                String requestTime = mGetInfo.getRequestTime();
                long time = Long.parseLong(requestTime);
                return time * 1000;
            } catch (Exception e) {

            }
            return 3000;
        }
    }

    /** 直播收到答题切换 */
    public class LiveQuestionSwitchImpl implements QuestionSwitch {

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
            return h5CoursewarePager.getBasePager();
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
                        if (entity.getIsAnswer() == 1) {
                            XESToastUtils.showToast(context, "您已经答过此题");
                        } else {
                            baseVoiceAnswerCreat.onAnswerReslut(context, EnglishH5CoursewareBll.this, voiceAnswerPager, baseVideoQuestionEntity, entity);
                        }
                    }
                    if (voiceAnswerPager instanceof VoiceAnswerPager) {
                        stopVoiceAnswerPager();
                    }
                    mLiveBll.getStuGoldCount();
                    // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
                    //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));

                    mH5AndBool.add(videoQuestionLiveEntity1.getUrl());
                    try {
                        JSONObject object = new JSONObject();
                        object.put("liveType", liveType);
                        object.put("vSectionID", mVSectionID);
                        object.put("url", videoQuestionLiveEntity1.getUrl());
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
        public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {

        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(BaseVoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            if (voiceAnswerPager != null) {
                stopVoiceAnswerPager();
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
    }

    /**
     * 试题隐藏显示
     *
     * @param isShow true显示
     */
    private void onQuestionShow(boolean isShow) {
        for (QuestionShowAction questionShowAction : questionShowActions) {
            questionShowAction.onQuestionShow(isShow);
        }
    }
}
