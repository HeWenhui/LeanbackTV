package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEvent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.CreateAnswerReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.IntelligentEvaluationH5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by linyuqiang on 2017/3/25.
 * 英语h5课件业务类
 */
public class EnglishH5CoursewareBll implements EnglishH5CoursewareAction, BaseVoiceAnswerCreat.NewArtsAnswerRightResultVoice, LivePagerBack, EnglishShowReg {
    private String TAG = "EnglishH5CoursewareBll";
    protected Logger logger = LiveLoggerFactory.getLogger(TAG);
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    private String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private Context context;
    private Handler handler = LiveMainHandler.getMainHandler();
    /** 互动题作答成功的布局列表 */
    private ArrayList<View> resultViews = new ArrayList<>();
    BaseEnglishH5CoursewarePager h5CoursewarePager;
    private BaseEnglishH5CoursewareCreat baseEnglishH5CoursewareCreat;
    private BaseEnglishH5CoursewarePager curPager;
    /** 语音答题的布局 */
    private BaseVoiceAnswerPager voiceAnswerPager;
    /** 语音答题结果的布局 */
    private View resultView;
    /** 创建语音答题 */
    private BaseVoiceAnswerCreat baseVoiceAnswerCreat;
    private LogToFile logToFile;
    private LiveViewAction liveViewAction;
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
    private SpeechUtils mIse;
    private AnswerRankBll mAnswerRankBll;
    /** 智能私信业务 */
    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    private boolean hasQuestion;
    private boolean isAnaswer = false;
    private ArrayList<QuestionShowAction> questionShowActions = new ArrayList<>();
    private long submitTime;
    private boolean hasSubmit;
    private LiveVideoSAConfig liveVideoSAConfig;
    private boolean IS_SCIENCE = false;
    private int isArts;
    private int isplayback = 0;

    private boolean isTeamPkAllowed = false;
    private boolean webViewCloseByTeacher = false;
    private String mUrl;

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
        logToFile = new LogToFile(context, TAG);
        this.context = context;
        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        ProxUtil.getProxUtil().put(context, EnglishShowReg.class, this);
        EventBus.getDefault().register(this);
    }

    public void setIsplayback(int isplayback) {
        this.isplayback = isplayback;
    }

    public void initView(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
        if (h5CoursewarePager != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            liveViewAction.addView(LiveVideoLevel.LEVEL_QUES, h5CoursewarePager.getRootView(), lp);
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
        isTeamPkAllowed = mGetInfo != null && "1".equals(mGetInfo.getIsAllowTeamPk());
    }

    public void setIse(SpeechUtils ise) {
        this.mIse = ise;
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        this.liveVideoSAConfig = liveVideoSAConfig;
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
        isArts = liveVideoSAConfig.getArts();
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

    @Override
    public void onBack(LiveBasePager liveBasePager) {
        if (liveBasePager instanceof BaseEnglishH5CoursewarePager) {
            if (h5CoursewarePager != null) {
                if (h5CoursewarePager.isFinish()) {
                    h5CoursewarePager.close();
                    VideoQuestionLiveEntity videoQuestionLiveEntity = null;
                    if (liveBasePager.getBaseVideoQuestionEntity() instanceof VideoQuestionLiveEntity) {
                        videoQuestionLiveEntity = (VideoQuestionLiveEntity) liveBasePager.getBaseVideoQuestionEntity();
                    }
                    onQuestionShow(videoQuestionLiveEntity, false, "onBack");
                } else {
                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, ContextManager.getApplication(), false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    final LiveBasePager liveBase = liveBasePager;
                    cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (h5CoursewarePager != null) {
                                mH5AndBool.add(h5CoursewarePager.getUrl());
                                h5CoursewarePager.onBack();
                                h5CoursewarePager.destroy();
                                liveViewAction.removeView(h5CoursewarePager.getRootView());

                                WebViewRequest webViewRequest = ProxUtil.getProxUtil().get(context, WebViewRequest.class);
                                if (webViewRequest != null) {
                                    webViewRequest.releaseWebView();
                                }
                                if (isMiddleScience()) {
                                    EventBus.getDefault().post(new EvenDriveEvent(EvenDriveEvent.CLOSE_H5));
                                }
                                if (isplayback == 1) {
                                    BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, BackMediaPlayerControl.class);
                                    if (mediaPlayerControl != null) {
                                        if (liveBase.getBaseVideoQuestionEntity() instanceof VideoQuestionLiveEntity) {
                                            VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) liveBase.getBaseVideoQuestionEntity();
                                            mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                                            mediaPlayerControl.start();
                                            LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
                                            showQuestion.onHide(videoQuestionLiveEntity);
                                        }

                                    }
                                }
                                h5CoursewarePager = null;
                            }
                        }
                    });
                    cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                            VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
                }
            } else {
                logToFile.d("onBack:BaseEnglishH5CoursewarePager");
                if (liveBasePager != null) {
                    liveBasePager.onDestroy();
                }
            }
        } else if (liveBasePager instanceof BaseVoiceAnswerPager) {
            if (voiceAnswerPager != null) {
                VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, ContextManager.getApplication(), false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (voiceAnswerPager != null) {
                            voiceAnswerPager.onUserBack();
                            VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) voiceAnswerPager.getBaseVideoQuestionEntity();
                            mH5AndBool.add(videoQuestionLiveEntity.getUrl());
                            stopVoiceAnswerPager(null);
                        }
                    }
                });
                cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("您正在答题，是否结束作答？",
                        VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            } else {
                logToFile.d("onBack:BaseVoiceAnswerPager");
                if (liveBasePager != null) {
                    liveBasePager.onDestroy();
                }
            }
        }
    }

    /**
     * 是否是中学理科
     *
     * @return
     */
    private boolean isMiddleScience() {
        return mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC
                && !LiveVideoConfig.isPrimary
                && !LiveVideoConfig.isSmallChinese
                && !mGetInfo.getSmallEnglish();
    }

    public boolean onBack() {
        return false;
    }

    public void onPause() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.onPause();
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
                    if (LiveVideoConfig.isMulLiveBack || videoQuestionLiveEntity.englishH5Entity.getNewEnglishH5()) {
                        if (h5CoursewarePager != null) {
                            if (LiveVideoConfig.englishH5Entity.equals(videoQuestionLiveEntity.englishH5Entity)) {
                                logToFile.i("onH5Courseware:equals:English=" + h5CoursewarePager.getEnglishH5Entity());
                                return;
                            } else {
                                logToFile.i("onH5Courseware:English=" + h5CoursewarePager.getEnglishH5Entity());
                                h5CoursewarePager.destroy();
                                liveViewAction.removeView(h5CoursewarePager.getRootView());
                            }
                        }
                        if (isArts == LiveVideoSAConfig.ART_SEC || isArts == LiveVideoSAConfig.ART_CH) {
                            EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                            String queskey = ("" + englishH5Entity.getPackageId()).hashCode() + "-" + ("" + englishH5Entity.getReleasedPageInfos()).hashCode();
                            if (mH5AndBool.contains(queskey)) {
                                logToFile.i("onH5Courseware:queskey=" + queskey);
                                return;
                            }
                        }
                        //小学不显示语文AI主观题
                        if ((LiveVideoConfig.EDUCATION_STAGE_1.equals(videoQuestionLiveEntity.getEducationstage())
                                || LiveVideoConfig.EDUCATION_STAGE_2.equals(videoQuestionLiveEntity.getEducationstage()))
                                && LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(videoQuestionLiveEntity.englishH5Entity.getPackageAttr())) {
                            return;
                        }
                        showH5Paper(videoQuestionLiveEntity);
                        return;
                    }
                    if (!isAnaswer) {
                        onQuestionShow(videoQuestionLiveEntity, true, "onH5Courseware:start");
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
                            stopVoiceAnswerPager(null);
                        }
                    }
                    if (h5CoursewarePager != null) {
                        if (h5CoursewarePager.getUrl().equals(videoQuestionLiveEntity.getUrl())) {
                            logToFile.i("onH5Courseware:url.equals:" + h5CoursewarePager.getUrl());
                            return;
                        } else {
                            logToFile.i("onH5Courseware:url=" + h5CoursewarePager.getUrl());
                            h5CoursewarePager.destroy();
                            liveViewAction.removeView(h5CoursewarePager.getRootView());
                        }
                    }
                    logger.e("======>EnglishH5CoursewareBll:" + "H5语音答题开启1" + "getIsVoice():" + videoQuestionLiveEntity.getIsVoice() + "getUrl():" + videoQuestionLiveEntity.getUrl());
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice()) && !mErrorVoiceQue.contains(videoQuestionLiveEntity.getUrl())) {
                        try {
                            showVoiceAnswer(videoQuestionLiveEntity);
                            logger.e("======>EnglishH5CoursewareBll:" + "H5语音答题开启2voiceType:" + videoQuestionLiveEntity.voiceType);
                        } catch (Exception e) {
                            logger.e("======>EnglishH5CoursewareBll:" + "H5语音答题开启3");
                            logToFile.d("onH5Courseware:showVoiceAnswer.error1=" + e.getMessage());
                            mErrorVoiceQue.add(videoQuestionLiveEntity.getUrl());
                            showH5Paper(videoQuestionLiveEntity);
                        }
                    } else {
                        showH5Paper(videoQuestionLiveEntity);
                    }
                } else {
                    logger.e("======>EnglishH5CoursewareBll:" + "H5语音答题关闭1");
                    boolean havePager = false;
                    logger.e("======>EnglishH5CoursewareBll: voiceAnswerPager=" + voiceAnswerPager);
                    if (voiceAnswerPager != null && !voiceAnswerPager.isEnd()) {
                        voiceAnswerPager.examSubmitAll("onH5Courseware", videoQuestionLiveEntity.nonce);
                        havePager = true;
                        logger.e("======>EnglishH5CoursewareBll:" + "H5语音答题关闭2");
                    }
                    int delayTime = 0;
                    int isForce = 0;
                    logger.e("======>EnglishH5CoursewareBll: h5CoursewarePager=" + h5CoursewarePager);
                    if (h5CoursewarePager != null) {
                        havePager = true;
                        curPager = h5CoursewarePager;
                        Log.e("mqtt", "submitData" + "one");
                        h5CoursewarePager.submitData();
                        Log.e("mqtt", "submitData" + "two");
                        logToFile.i("onH5Courseware:submitData");
                        WebViewRequest webViewRequest = ProxUtil.getProxUtil().get(context, WebViewRequest.class);
                        if (webViewRequest != null) {
                            webViewRequest.releaseWebView();
                        }
                        delayTime = 3000;
                        isForce = 1;
                    }
                    if (isAnaswer && !havePager && resultView == null) {
                        Log.e("mqtt", "submitData" + "three");
                        onQuestionShow(null, false, "onH5Courseware:end");
                    }
                    isAnaswer = false;
                    if (hasQuestion) {
                        getFullMarkList(delayTime);
                        getAutoNotice(isForce);
                        hasQuestion = false;
                    }
                    if (!videoQuestionLiveEntity.isTUtor()) {
                        closePageByTeamPk();
                    }
                }
            }
        });
    }

    private boolean isPageOnCloseing = false;

    /**
     * 强制关闭webview
     **/
    public void froceClose(final String method) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (h5CoursewarePager != null) {
                    if (mLiveBll != null) {
                        mLiveBll.getStuGoldCount("forceClose:" + method);
                    }
                    h5CoursewarePager.close();
                    //如果重写了close。调用了onClose.onH5ResultClose 方法以后，h5CoursewarePager为空
                    if (h5CoursewarePager != null) {
                        h5CoursewarePager.destroy();
                        liveViewAction.removeView(h5CoursewarePager.getRootView());
                        h5CoursewarePager = null;
                        onQuestionShow(null, false, "forceClose:" + method);
                    }
                }
            }
        });
    }

    /**
     * 战队pk 自动关闭答题结果页
     */
    private void closePageByTeamPk() {
        // Log.e("EnglishH5CoursewareBll","=======>closePageByTeamPk:"+isTeamPkAllowed);
        if (isTeamPkAllowed && !isPageOnCloseing && h5CoursewarePager != null) {
            isPageOnCloseing = true;
            long timeDelay = h5CoursewarePager.isResultRecived() ? 0L : 6000L;
            //Log.e("EnglishH5CoursewareBll","=======>closePageByTeamPk222:"+timeDelay);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (h5CoursewarePager != null && h5CoursewarePager == curPager) {
                        h5CoursewarePager.destroy();
                        liveViewAction.removeView(h5CoursewarePager.getRootView());
                    }
                    isPageOnCloseing = false;
                }
            }, timeDelay);
        }
    }


    private void showH5Paper(final VideoQuestionLiveEntity videoQuestionH5Entity) {
        mUrl = videoQuestionH5Entity.getUrl();
        EnglishH5Entity englishH5Entity = videoQuestionH5Entity.englishH5Entity;
        if (englishH5Entity.getNewEnglishH5()) {
            logToFile.i("showH5Paper:packageId=" + englishH5Entity.getPackageId() + ",Released=" + englishH5Entity.getReleasedPageInfos());
        } else {
            logToFile.i("showH5Paper:url=" + videoQuestionH5Entity.getUrl());
        }
        StableLogHashMap logHashMap = new StableLogHashMap("receiveCourseware");
        logHashMap.put("testid", videoQuestionH5Entity.id);
        logHashMap.put("coursewaretype", videoQuestionH5Entity.courseware_type);
        logHashMap.put("loadurl", videoQuestionH5Entity.getUrl());
        logHashMap.put("isplayback", "" + isplayback);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
        OnH5ResultClose onH5ResultClose = new OnH5ResultClose() {
            @Override
            public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {

                EventBus.getDefault().post(new EvenDriveEvent(EvenDriveEvent.CLOSE_H5));

                mH5AndBool.add(baseEnglishH5CoursewarePager.getUrl());
                if (!videoQuestionH5Entity.englishH5Entity.getNewEnglishH5()) {
                    saveH5AnswerRecord(videoQuestionH5Entity.getUrl());
                }
                baseEnglishH5CoursewarePager.destroy();
                liveViewAction.removeView(baseEnglishH5CoursewarePager.getRootView());
                logger.d("onH5ResultClose:pager=" + baseEnglishH5CoursewarePager + ",old=" + h5CoursewarePager);
                if (baseEnglishH5CoursewarePager == h5CoursewarePager) {
                    h5CoursewarePager = null;
                }
                if (!isAnaswer) {
                    onQuestionShow(null, false, "onH5ResultClose");
                }
                mLiveBll.getStuGoldCount("showH5Paper");

                WebViewRequest webViewRequest = ProxUtil.getProxUtil().get(context, WebViewRequest.class);
                if (webViewRequest != null) {
                    webViewRequest.releaseWebView();
                }
            }
        };
        h5CoursewarePager = baseEnglishH5CoursewareCreat.creat(context, videoQuestionH5Entity, onH5ResultClose,
                mVSectionID);
        if (h5CoursewarePager != null && !(h5CoursewarePager instanceof IntelligentEvaluationH5Pager)) {
            h5CoursewarePager.setEnglishH5CoursewareBll(this);
            if (mLiveBll instanceof EnglishH5CoursewareSecHttp) {
                h5CoursewarePager.setEnglishH5CoursewareSecHttp((EnglishH5CoursewareSecHttp) mLiveBll);
            }
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);
            liveViewAction.addView(LiveVideoLevel.LEVEL_QUES, h5CoursewarePager.getRootView(), lp);
            WebViewRequest webViewRequest = ProxUtil.getProxUtil().get(context, WebViewRequest.class);
            if (webViewRequest != null) {
                webViewRequest.requestWebView();
            }
        }
    }


    /**
     * 持久化保存已作答过的试题地址
     *
     * @param url
     */
    private void saveH5AnswerRecord(String url) {
        //非 理科、语文pk直播间  才保存 作答过的Url
        if (!isTeamPkAllowed) {
            try {
                JSONObject object = new JSONObject();
                object.put("liveType", liveType);
                object.put("vSectionID", mVSectionID);
                object.put("url", url);
                mShareDataManager.put(ENGLISH_H5, object.toString(), ShareDataManager.SHAREDATA_USER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setBaseVoiceAnswerCreat(BaseVoiceAnswerCreat baseVoiceAnswerCreat) {
        this.baseVoiceAnswerCreat = baseVoiceAnswerCreat;
    }

    public void setBaseEnglishH5CoursewareCreat(BaseEnglishH5CoursewareCreat baseEnglishH5CoursewareCreat) {
        this.baseEnglishH5CoursewareCreat = baseEnglishH5CoursewareCreat;
    }

    private void showVoiceAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (voiceAnswerPager != null) {
            if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(videoQuestionLiveEntity
                    .getvQuestionID())) {
                logToFile.d("showVoiceAnswer:id=" + videoQuestionLiveEntity.id);
                return;
            } else {
                voiceAnswerPager.setEnd();
                voiceAnswerPager.stopPlayer();
                voiceAnswerPager.onDestroy();
                liveViewAction.removeView(voiceAnswerPager.getRootView());
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
                baseVoiceAnswerCreat.create(context, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.type, liveViewAction, mIse);
        voiceAnswerPager2.setIse(mIse);
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
        } else {
            if (voiceAnswerPager != null) {
                voiceAnswerPager.setAudioRequest();
            }
        }
    }

    private void stopVoiceAnswerPager(final View resultView) {
        boolean isEnd = voiceAnswerPager.isEnd();
        this.resultView = resultView;
        voiceAnswerPager.stopPlayer();
        voiceAnswerPager.onDestroy();
        liveViewAction.removeView(voiceAnswerPager.getRootView());
        voiceAnswerPager = null;
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(context, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        logToFile.d("stopVoiceAnswerPager:isAnaswer=" + isAnaswer + ",isEnd=" + isEnd + ",resultView=" + resultView);
        if (isEnd) {
            if (resultView == null) {
                onQuestionShow(null, false, "stopVoiceAnswerPager");
            }
        }
        if (resultView != null) {
            resultView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    logToFile.d("stopVoiceAnswerPager:onViewAttachedToWindow:isAnaswer=" + isAnaswer);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    logToFile.d("stopVoiceAnswerPager:onViewDetachedFromWindow:isAnaswer=" + isAnaswer);
                    EnglishH5CoursewareBll.this.resultView = null;
                    if (!isAnaswer) {
                        onQuestionShow(null, false, "stopVoiceAnswerPager:resultView");
                    }
                }
            });
        }
    }

    private void switchVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager) {
        boolean isEnd = voiceAnswerPager.isEnd();
        voiceAnswerPager.stopPlayer();
        voiceAnswerPager.onDestroy();
        liveViewAction.removeView(voiceAnswerPager.getRootView());
        if (EnglishH5CoursewareBll.this.voiceAnswerPager == voiceAnswerPager) {
            EnglishH5CoursewareBll.this.voiceAnswerPager = null;
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(context, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
            if (isEnd) {
                onQuestionShow(null, false, "switchVoiceAnswerPager");
            }
        }
    }

    @Override
    public void initSelectAnswerRightResultVoice(VideoResultEntity entity) {
        entity.setPreEnglish(mGetInfo != null && mGetInfo.getSmallEnglish());

        final View popupWindow_view = QuestionResultView.initSelectAnswerRightResultVoice(context, entity);
        boolean isAutoDissMiss = !entity.isPreEnglish();
        initQuestionAnswerReslut(popupWindow_view, isAutoDissMiss);
    }

    @Override
    public void initFillinAnswerRightResultVoice(VideoResultEntity entity) {
        entity.setPreEnglish(mGetInfo != null && mGetInfo.getSmallEnglish());

        View popupWindow_view = QuestionResultView.initFillinAnswerRightResultVoice(context, entity);
        boolean isAutoDissMiss = !entity.isPreEnglish();
        initQuestionAnswerReslut(popupWindow_view, isAutoDissMiss);
    }

    /** 语音答题回答错误 */
    @Override
    public void initSelectAnswerWrongResultVoice(VideoResultEntity entity) {
        entity.setPreEnglish(mGetInfo != null && mGetInfo.getSmallEnglish());

        View popupWindow_view = QuestionResultView.initSelectAnswerWrongResultVoice(context, entity);
        boolean isAutoDissMiss = !entity.isPreEnglish();
        initQuestionAnswerReslut(popupWindow_view, isAutoDissMiss);
    }

    /** 语音答题回答错误 */
    @Override
    public void initFillAnswerWrongResultVoice(VideoResultEntity entity) {
        entity.setPreEnglish(mGetInfo != null && mGetInfo.getSmallEnglish());

        View popupWindow_view = QuestionResultView.initFillAnswerWrongResultVoice(context, entity);

        boolean isAutoDissMiss = !entity.isPreEnglish();
        initQuestionAnswerReslut(popupWindow_view, isAutoDissMiss);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    @Override
    public void initQuestionAnswerReslut(final View popupWindow_view) {

        initQuestionAnswerReslut(popupWindow_view, true);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    public void initQuestionAnswerReslut(final View popupWindow_view, boolean autodisMiss) {
        logger.d("initQuestionAnswerReslut");
        popupWindow_view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            long before;

            @Override
            public void onViewAttachedToWindow(View view) {
                before = System.currentTimeMillis();
                logger.d("initQuestionAnswerReslut:onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                logToFile.d("initQuestionAnswerReslut:onViewDetachedFromWindow:time=" + (System.currentTimeMillis() - before));
            }
        });
        resultViews.add(popupWindow_view);
        liveViewAction.addView(LiveVideoLevel.LEVEL_QUES, popupWindow_view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resultViews.remove(popupWindow_view);
                liveViewAction.removeView(popupWindow_view);
            }
        });
        if (autodisMiss) {
            disMissAnswerResult();
        }
    }

    @Override
    public void removeQuestionAnswerReslut(View popupWindow_view) {
        resultViews.remove(popupWindow_view);
        liveViewAction.removeView(popupWindow_view);
    }

    @Override
    public void removeBaseVoiceAnswerPager(BaseVoiceAnswerPager voiceAnswerPager2) {
        if (voiceAnswerPager2 == null) {
            logToFile.d("removeBaseVoiceAnswerPager:pager=null");
            return;
        }
        if (voiceAnswerPager2 == voiceAnswerPager) {
            if (voiceAnswerPager.isEnd()) {
                voiceAnswerPager2.onDestroy();
                liveViewAction.removeView(voiceAnswerPager2.getRootView());
                voiceAnswerPager = null;
                onQuestionShow(null, false, "removeBaseVoiceAnswerPager");
            }
        } else {
            voiceAnswerPager2.onDestroy();
            liveViewAction.removeView(voiceAnswerPager2.getRootView());
        }
    }

    /**
     * 回答问题结果提示框延迟三秒消失
     */
    public void disMissAnswerResult() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeAllResultViews();
            }
        }, 3000);
    }

    private void removeAllResultViews() {
        logToFile.d("removeAllResultViews:size=" + resultViews.size());
        while (!resultViews.isEmpty()) {
            View view = resultViews.remove(0);
            liveViewAction.removeView(view);
        }
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (voiceAnswerPager instanceof VoiceAnswerPager) {
            int wradio = liveVideoPoint.getRightMargin();
            baseVoiceAnswerCreat.setViewLayoutParams(voiceAnswerPager, wradio);
        } else {
            if (h5CoursewarePager instanceof LiveVideoPoint.VideoSizeChange) {
                LiveVideoPoint.VideoSizeChange change = (LiveVideoPoint.VideoSizeChange) h5CoursewarePager;
                change.videoSizeChange(liveVideoPoint);
            }
        }
    }

    public void setVideoLayout(int width, int height) {
        if (voiceAnswerPager instanceof VoiceAnswerPager) {
            final View contentView = ((Activity) context).findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            if (width > 0) {
                int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
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
    public View initArtsAnswerRightResultVoice(AnswerResultEntity entity) {
        final View popupWindow_view = QuestionResultView.initArtsAnswerRightResultVoice(context, entity, new AnswerResultStateListener() {
            @Override
            public void onCompeletShow() {
                disMissAnswerResult();
            }

            @Override
            public void onAutoClose(BasePager basePager) {
                removeAllResultViews();
            }

            @Override
            public void onCloseByUser() {
            }
        });
        initQuestionAnswerReslut(popupWindow_view, false);
        return popupWindow_view;
    }

    public interface OnH5ResultClose {
        void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity);
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
                    List<FullMarkListEntity> tmplst = JSON.parseArray(jsonObject.optString("ranks"),
                            FullMarkListEntity.class);
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
                                    h5CoursewarePager.destroy();
                                    liveViewAction.removeView(h5CoursewarePager.getRootView());
                                    h5CoursewarePager = null;
                                    curPager = null;
                                    if (oldh5CoursewarePager.isFinish()) {
                                        onQuestionShow(null, false, "getFullMarkList");
                                    }
                                } else if (curPager != null) {
                                    curPager.destroy();
                                    liveViewAction.removeView(curPager.getRootView());
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

    @Override
    public void registQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.add(questionShowAction);
    }

    @Override
    public void unRegistQuestionShow(QuestionShowAction questionShowAction) {
        questionShowActions.remove(questionShowAction);
    }

    /**
     * 直播收到答题切换
     */
    public class LiveStandQuestionSwitchImpl extends LiveQuestionSwitchImpl implements LiveStandQuestionSwitch {
        @Override
        public BasePager questionSwitch(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseQuestionEntity) {
            BaseEnglishH5CoursewarePager h5CoursewarePager = (BaseEnglishH5CoursewarePager) super.questionSwitch(baseVoiceAnswerPager, baseQuestionEntity);
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
            logToFile.d("onAnswerTimeOutError:question=" + baseVideoQuestionEntity + ",pager=" + (voiceAnswerPager == null));
            if (voiceAnswerPager != null) {
                baseVoiceAnswerCreat.onAnswerReslut(context, EnglishH5CoursewareBll.this, voiceAnswerPager, baseVideoQuestionEntity, entity);
            }
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
        public BasePager questionSwitch(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseQuestionEntity) {
            logger.e("questionSwitch:" + "EnglishH5Bll");
            VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) baseQuestionEntity;
            switchVoiceAnswerPager(baseVoiceAnswerPager);
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
        public void onPutQuestionResult(BaseVoiceAnswerPager baseVoiceAnswerPager, final BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, final OnAnswerReslut answerReslut) {
            final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
            if (videoQuestionLiveEntity1.isNewArtsH5Courseware()) {
                logger.d("onPutQuestionResultNewArts0");

                JSONArray answers = new JSONArray();
                JSONObject answerdetail = new JSONObject();
                JSONArray blanks = new JSONArray();
                JSONArray choices = new JSONArray();
                if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(videoQuestionLiveEntity1.type)) {
                    try {
                        if (isRight) {
                            blanks.put(0, result);
                            LiveVideoConfig.userAnswer = result;
                        } else {
                            blanks.put(0, "");
                            LiveVideoConfig.userAnswer = "";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        choices.put(0, result);
                        LiveVideoConfig.userAnswer = result;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    answerdetail.put("blank", blanks);
                    answerdetail.put("choice", choices);
                    answerdetail.put("useVoice", "1");
                    answerdetail.put("voiceTime", voiceTime + "");
                    answerdetail.put("voiceUrl", "");
                    answerdetail.put("testId", videoQuestionLiveEntity1.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                answers.put(answerdetail);
                String testAnswer = "";
                testAnswer = answers.toString();
//                if ("0".equals(isSubmit)) {
//                    isSubmit = "1";
//                } else if ("1".equals(isSubmit)) {
//                    isSubmit = "2";
//                }
                LiveVideoConfig.answer = answer;
                // 文科新课件平台的对接
                mLiveBll.liveSubmitTestH5Answer(videoQuestionLiveEntity1, mVSectionID, testAnswer, videoQuestionLiveEntity1.courseware_type, isSubmit, voiceTime, isRight, new OnAnswerReslut() {

                    @Override
                    public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                        logger.d("onPutQuestionResultNewArts5");
                        logToFile.d("liveSubmitTestH5Answer:question=" + baseVideoQuestionEntity + ",pager=" + (voiceAnswerPager == null));
                        answerReslut.onAnswerReslut(baseVideoQuestionEntity, entity);
                        View resultView = null;
                        if (entity != null) {
                            if (entity.getIsAnswer() == 1) {
                                XESToastUtils.showToast(context, "您已经答过此题");
                            } else {
                                CreateAnswerReslutEntity createAnswerReslutEntity = baseVoiceAnswerCreat.onAnswerReslut(context, EnglishH5CoursewareBll.this, voiceAnswerPager, baseVideoQuestionEntity, entity);
                                resultView = createAnswerReslutEntity.resultView;
                            }
                        }
                        if (voiceAnswerPager instanceof VoiceAnswerPager) {
                            stopVoiceAnswerPager(resultView);
                        }
                        if (resultView == null) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLiveBll.getStuGoldCount("liveSubmitTestH5Answer");
                                }
                            }, 2500);
                        } else {
                            resultView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                @Override
                                public void onViewAttachedToWindow(View view) {

                                }

                                @Override
                                public void onViewDetachedFromWindow(View view) {
                                    mLiveBll.getStuGoldCount("liveSubmitTestH5Answer:Detached");
                                }
                            });
                        }
                        // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
                        //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));

                        mH5AndBool.add(videoQuestionLiveEntity1.getUrl());
                        saveH5AnswerRecord(videoQuestionLiveEntity1.getUrl());
                    }

                    @Override
                    public void onAnswerFailure() {
                        answerReslut.onAnswerFailure();
                    }
                });

            } else {
                //  final VideoQuestionLiveEntity videoQuestionLiveEntity1 = (VideoQuestionLiveEntity) videoQuestionLiveEntity;
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
                        logToFile.d("liveSubmitTestH5Answer:question=" + baseVideoQuestionEntity + ",pager=" + (voiceAnswerPager == null));
                        answerReslut.onAnswerReslut(baseVideoQuestionEntity, entity);
                        if (entity != null) {
                            if (entity.getIsAnswer() == 1) {
                                XESToastUtils.showToast(context, "您已经答过此题");
                            } else {
                                baseVoiceAnswerCreat.onAnswerReslut(context, EnglishH5CoursewareBll.this, voiceAnswerPager, baseVideoQuestionEntity, entity);
                            }
                        }
                        if (voiceAnswerPager instanceof VoiceAnswerPager) {
                            stopVoiceAnswerPager(null);
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLiveBll.getStuGoldCount("liveSubmitTestH5Answer");
                            }
                        }, 2500);
                        // TODO: 2018/6/25  代码整理完 用下面方法 更新 本场成就信息
                        //EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));

                        mH5AndBool.add(videoQuestionLiveEntity1.getUrl());
                        saveH5AnswerRecord(videoQuestionLiveEntity1.getUrl());
                    }

                    @Override
                    public void onAnswerFailure() {
                        answerReslut.onAnswerFailure();
                    }
                });
            }

        }

        @Override
        public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {

        }

        @Override
        public void uploadVoiceFile(File file) {

        }

        @Override
        public void stopSpeech(BaseVoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
            if (answerPager == null) {
                logToFile.d("stopSpeech:answerPager == null");
                return;
            }
            boolean same = answerPager == voiceAnswerPager;
            logToFile.d("stopSpeech:answerPager=same?" + same);
            if (answerPager == voiceAnswerPager) {
                stopVoiceAnswerPager(null);
            } else {
                answerPager.stopPlayer();
                answerPager.onDestroy();
                liveViewAction.removeView(answerPager.getRootView());
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

    public void destroy() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.destroy();
        }
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArtsResultCmplShow(AnswerResultCplShowEvent event) {
        Loger.e("EnglishH5CoursewareBll:onArtsResultCmplShow ");
        froceClose(event.getMethod());
    }

    /**
     * 试题隐藏显示
     *
     * @param videoQuestionLiveEntity
     * @param isShow                  true显示
     * @param method
     */
    private void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow, String method) {
        if (videoQuestionLiveEntity != null) {
            logToFile.d("onQuestionShow:isShow=" + isShow + ",id=" + videoQuestionLiveEntity.id + ",method=" + method);
        } else {
            logToFile.d("onQuestionShow:isShow=" + isShow + ",method=" + method);
        }
        for (QuestionShowAction questionShowAction : questionShowActions) {
            questionShowAction.onQuestionShow(videoQuestionLiveEntity, isShow);
        }
    }

}
