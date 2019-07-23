package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ChsSpeakEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionOnSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.CourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.ChsSpeakLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.dialog.CourseTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import pl.droidsonroids.gif.GifDrawable;

/**
 * FileName: SpeakChineseCoursewarePager
 * Author: WangDe
 * Date: 2019/5/14 16:50
 * Description: 开讲吧新课件，去掉h5壳
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class SpeakChineseCoursewarePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager,
        BaseQuestionWebInter {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    /**
     * 理科初高中新课件平台 强制提交js
     */
//    private String jsClientSubmit = "javascript:__CLIENT_SUBMIT__()";
    /**
     * 是不是已经收卷
     */
    private boolean isFinish = false;
    /**
     * 是不是已经提交
     */
    private boolean isSumit = false;
    private long subMitTime;
    private String liveId;
    private EnglishH5Entity englishH5Entity;
    /**
     * 是不是回放
     */
    private boolean isPlayBack;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String url;
    private String id;
    /**
     * 课件类型，新课件没用
     */
    private String courseware_type;
    private String nonce;
    private String isShowRanks;
    /**
     * 互动题打开时间
     */
    private long entranceTime;
    /**
     * 文理英属性
     */
    private int isArts;
    /**
     * 文理的战队pk
     */
    private boolean allowTeamPk;
    /**
     * 英语新课件
     */
    private boolean isNewArtsCourseware;
    private VideoQuestionLiveEntity detailInfo;
    /**
     * 学年
     */
    private String educationstage;
    /**
     * 战队pk用，金币
     */
    private int mGoldNum = -1;
    /**
     * 战队pk用，能量
     */
    private int mEnergyNum = -1;
    /**
     * 加载的布局
     */
    private RelativeLayout rlSubjectLoading;

    /**
     * 课件接口失败刷新
     */
    private ImageView ivCourseRefresh;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;
    /**
     * 新课件缓存
     */
    private NewCourseCache newCourseCache;
    /**
     * 新课件是否是预加载
     */
    private boolean ispreload;
    private ContextLiveAndBackDebug liveAndBackDebug;
    /**
     * 显示下方控制布局
     */
    private boolean showControl = false;
    /**
     * 在网页中嵌入js，只嵌入一次
     */
    private boolean addJs = false;
    /**
     * 是不是刷新，加载完成
     */
    private int isRefresh = 0;
    /**
     * 刷新次数
     */
    private int refreshTime = 0;
    private NewCourseSec newCourseSec;
    private ArrayList<NewCourseSec.Test> tests = new ArrayList<>();
    private int currentIndex = 0;
    /**
     * 发送getAnswer的类型
     */
    private int getAnswerType = 0;
    /**
     * 加载结果页
     */
    private boolean loadResult = false;
    /**
     * 确认提交的弹窗
     */
    private CourseTipDialog courseTipDialog;
    /**
     * 保存今天互动题
     */
    private String today;
    /**
     * 保存互动题
     */
    private JSONObject quesJson;
    /**
     * 保存互动题开始时间
     */
    private long startQueTime;
    /**
     * 课件加载
     */
    private PreLoad preLoad;
    /**
     * 课件题目数量
     */
    private int totalQuestion = -1;
    private String testId = "";

    private CourseWareHttpManager courseWareHttpManager;
    /**
     * 强制收题
     */
    private final int NOTFORCE = 0;

    private final int FORCE = 1;
    private boolean isFirstAI = true;
    private LiveHttpManager mLiveHttpManager;
    private SpeechUtils speechUtils;
    /**
     * 是否正在语音识别中
     */
    private boolean isAssessing;

    private boolean resultGotByForceSubmit;
    /**
     * 评测文本
     */
    private TreeMap<Integer, String> assessMap;
    /**
     * 答案文本
     **/
    private TreeMap<Integer, String> answerMap;

    /**
     * 是否语音方式答题
     */
    private Boolean isSpeakAnswer;
    /**
     * 语音识别是否是由刷新引起的
     **/
    private boolean startAssesByRefresh;
    /** 评测返回通过分数线*/
    private final int CUT_OFF_SCORE = 70;
    private File saveVideoFile;
    private String assessContent;


    public SpeakChineseCoursewarePager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity,
                                       boolean isPlayBack, String liveId, String id,
                                       EnglishH5Entity englishH5Entity,
                                       final String courseware_type, String nonce,
                                       EnglishH5CoursewareBll.OnH5ResultClose onClose,
                                       String isShowRanks, int isArts, boolean allowTeamPk) {
        super(context, false);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.liveId = liveId;
        this.englishH5Entity = englishH5Entity;
        this.url = englishH5Entity.getUrl();
        this.isPlayBack = isPlayBack;
        this.onClose = onClose;
        this.id = id;
        this.courseware_type = courseware_type;
        this.nonce = nonce;
        this.isShowRanks = isShowRanks;
        this.isArts = isArts;
        this.allowTeamPk = allowTeamPk;
        this.isNewArtsCourseware = englishH5Entity.isArtsNewH5Courseware();
        LiveVideoConfig.englishH5Entity = englishH5Entity;
        this.detailInfo = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        if (isArts != LiveVideoSAConfig.ART_EN) {
            this.educationstage = detailInfo.getEducationstage();
        }
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
        liveAndBackDebug.addCommonData("isplayback", isPlayBack ? "1" : "0");
        mView = initView();
        entranceTime = System.currentTimeMillis() / 1000;
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_native, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);

        preLoad = new PrimaryPreLoad();
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            long before;

            @Override
            public void onViewAttachedToWindow(View v) {
                before = System.currentTimeMillis();

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (mLogtf != null) {
                    mLogtf.d("onViewDetachedFromWindow:reloadurl=" + wvSubjectWeb.getUrl() + ",,time=" + (System
                            .currentTimeMillis() - before));
                }

                if (allowTeamPk && newCourseSec != null) {
                    int gold = newCourseSec.getIsAnswer() == 0 ? mGoldNum : -1;
                    int energy = newCourseSec.getIsAnswer() == 0 ? mEnergyNum : -1;
                    LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(gold, energy, LiveRoomH5CloseEvent
                            .H5_TYPE_COURSE, id);
                    if (mEnglishH5CoursewareBll != null) {
                        event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
                        mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
                    }
                    event.setEnglishH5Entity(englishH5Entity);
                    event.setForceSubmit(resultGotByForceSubmit);
                    EventBus.getDefault().post(event);
                }
                cancleAssess();
                preLoad.onStop();
                isDestory = true;
                // fixme 如果是回放当页面移除时 讲媒体控制栏再次显示
                if (isPlayBack) {
                    resetMediaCtr();
                }
                if (mainHandler != null) {
                    mainHandler.removeCallbacks(null);
                }
                ChsSpeakEvent event = new ChsSpeakEvent();
                event.setEventType(ChsSpeakEvent.EVENT_TYPE_PAGE_CLOSE);
                EventBus.getDefault().post(event);
            }
        });
        return view;
    }

    /**
     * 回放 页面关闭时重新显示媒体控制栏
     */
    private void resetMediaCtr() {
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(mContext, LiveBackBll.ShowQuestion.class);
        if (showQuestion != null) {
            showQuestion.onHide(getBaseVideoQuestionEntity());
        }
    }

    @Override
    public void initData() {
        super.initData();
        initSpeachAssess();
        String testid = "";
        try {
            testid = NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts);
            mLogtf.addCommon("testid", "" + testid);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        today = dateFormat.format(date);
        getTodayQues();
        newCourseCache = new NewCourseCache(mContext, liveId, testid);
        addJavascriptInterface();
        wvSubjectWeb.getSettings().setLoadWithOverviewMode(false);
        wvSubjectWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
                    LiveCrashReport.postCatchedException(new Exception());
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

        CourseWebViewClient courseWebViewClient = new CourseWebViewClient();
        newCourseCache.setOnHttpCode(courseWebViewClient);
        wvSubjectWeb.setWebViewClient(courseWebViewClient);
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, "99999", creattime, new StaticWeb
                .OnMessage() {

            @Override
            public void postMessage(String where, final JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    if (CourseMessage.REC_close.equals(type)) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else if (CourseMessage.REC_submitAnswer.equals(type)) {
//                        submit(message);
                    } else if (CourseMessage.REC_answer.equals(type)) {
                        onAnswer(message);
                    } else if (CourseMessage.REC_loadComplete.equals(type)) {
                        onLoadComplete(where, message);
                    } else if (CourseMessage.REC_SubmitAnswer.equals(type)) {
//                        onLoadComplete(where, message);
                    } else if (CourseMessage.REC_QuestionStatus.equals(type)) {
                        onQuestionStatus(message);
                    } else if (CourseMessage.REC_AssessData.equals(type)) {
                        onAssessStart(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), "xesApp");
        ivCourseRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCourseRefresh.setVisibility(View.GONE);
                getCourseWareTests();
            }
        });
        ivWebViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJs = false;
                isRefresh = 1;
                refreshTime++;
                mLogtf.d("ivWebViewRefresh:refreshTime=" + refreshTime);
                isAssessing = false;
                cancleAssess();
                startAssesByRefresh = true;
                wvSubjectWeb.reload();
            }
        });

    }

    /**
     * 初始化识别库
     */
    private void initSpeachAssess() {
        speechUtils = SpeechUtils.getInstance(mContext.getApplicationContext());
        speechUtils.prepar(Constants.ASSESS_PARAM_LANGUAGE_CH, new SpeechEvaluatorUtils.OnFileSuccess() {
            @Override
            public void onFileInit(int code) {
                logger.d("onFileInit ");
            }

            @Override
            public void onFileSuccess() {
                logger.d("onFileSuccess ");
            }

            @Override
            public void onFileFail() {
                logger.d("onFileFail ");
            }
        });
    }

    @Override
    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public void close() {
    }

    @Override
    public String getUrl() {
        String queskey =
                ("" + englishH5Entity.getPackageId()).hashCode() + "-" + ("" + englishH5Entity.getReleasedPageInfos()
                ).hashCode();
        return queskey;

    }

    @Override
    public void setQuestionOnSubmit(QuestionOnSubmit questionOnSubmit) {

    }

    private void getTodayQues() {
        String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}",
                ShareDataManager.SHAREDATA_USER);
        JSONObject jsonObject = getTodayLive(string);
        if (jsonObject != null) {
            try {
                JSONObject todayObj = jsonObject.getJSONObject("todaylive");
                if (todayObj.has(liveId)) {
                    JSONObject todayLiveObj = todayObj.getJSONObject(liveId);
                    String queskey = getQuesKey();
                    quesJson = todayLiveObj.optJSONObject("ques-" + queskey);
                    startQueTime = todayLiveObj.optLong("start-" + queskey);
                }
            } catch (JSONException e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                mLogtf.e("getTodayQues", e);
            }
        }
    }

    private String getQuesKey() {
        String queskey =
                englishH5Entity.getPackageId().hashCode() + "-" + englishH5Entity.getReleasedPageInfos().hashCode();
        return queskey;

    }

    private JSONObject getTodayLive(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String savetoday = jsonObject.optString("today");
            JSONObject todayObj;
            JSONObject todayLiveObj;
            if (TextUtils.equals(today, savetoday)) {
                todayObj = jsonObject.getJSONObject("todaylive");
                if (todayObj.has(liveId)) {
                    todayLiveObj = todayObj.getJSONObject(liveId);
                } else {
                    todayLiveObj = new JSONObject();
                }
            } else {
                todayObj = new JSONObject();
                jsonObject.put("today", today);
                jsonObject.put("todaylive", todayObj);
                todayLiveObj = new JSONObject();
            }
            todayObj.put(liveId, todayLiveObj);
            return jsonObject;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            mLogtf.e("getTodayLive", e);
        }
        return null;
    }

    /**
     * 保存互动题开始时间
     *
     * @param startQueTime
     */
    private void saveThisQuesStart(long startQueTime) {
        try {
            String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}",
                    ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = getTodayLive(string);
            if (jsonObject != null) {
                JSONObject todayLiveObj = jsonObject.getJSONObject("todaylive").getJSONObject(liveId);
                String queskey = getQuesKey();
                todayLiveObj.put("start-" + queskey, startQueTime);
                mShareDataManager.put(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "" + jsonObject,
                        ShareDataManager.SHAREDATA_USER);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            mLogtf.e("saveThisQues", e);
        }
    }

    private void onAnswer(final JSONObject message) {
        recognizeSuccess = true;
        cancleAssess();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                NewCourseSec.Test oldTest = tests.get(currentIndex);
                try {
                    JSONObject msg = new JSONObject();
                    if (message.has("message")) {
                        msg = message.getJSONObject("message");
                    }
                    if (message.has("data")) {
                        msg = message;
                    }
                    JSONArray data = msg.getJSONArray("data");
                    if (data != null && data.length() > 0) {
                        JSONArray userAnswerContent = data.getJSONObject(0).optJSONArray("userAnswerContent");
                        JSONArray rightAnswerContent = data.getJSONObject(0).optJSONArray("rightAnswerContent");
                        oldTest.setUserAnswerContent(userAnswerContent);
                        oldTest.setRightAnswerContent(rightAnswerContent);
                    }
                    if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_FORCE_SUBMIT) {
                        submit(1, nonce, data);
                    } else {
                        submit(0, nonce, data);
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                logger.d("onAnswer:answer:getAnswerType=" + getAnswerType + ",index=" + currentIndex);

            }
        });
    }

    private void onLoadComplete(final String where, final JSONObject message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (LiveQueConfig.GET_ANSWERTYPE_WHERE_MESSAGE.equals(where)) {
                    JSONObject jsonData = new JSONObject();
                    if (quesJson != null) {
                        JSONArray userAnswerContent = quesJson.optJSONArray("" + currentIndex);
                        if (userAnswerContent != null && userAnswerContent.length() > 0) {
                            try {
                                JSONObject answerObj = userAnswerContent.getJSONObject(0);
                                JSONArray userAnswerContent2 = answerObj.getJSONArray("userAnswerContent");
                                if (userAnswerContent2.length() > 0) {
                                    jsonData.put("type", CourseMessage.SEND_lookAnswerStatus);
                                    JSONObject resultData = new JSONObject();
                                    resultData.put("isCanAnswer", 1);
                                    resultData.put("userAnswerContent", userAnswerContent2);
                                    jsonData.put("data", resultData);
                                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String pageid = "";
                    if (currentIndex >= 0 && currentIndex < tests.size()) {
                        pageid = tests.get(currentIndex).getId();
                    }
                    NewCourseLog.sno4(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),
                            getSubtestid(), wvSubjectWeb.getUrl(), ispreload, pageid,
                            (System.currentTimeMillis() - pagerStart), isRefresh, refreshTime,detailInfo.isTUtor());
                    isRefresh = 0;
                }
            }
        });
    }

    private void onQuestionStatus(JSONObject message) {
        try {
            JSONObject data = message.getJSONObject("data");
            totalQuestion = data.optInt("totalQuestion", -1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAssessStart(JSONObject message) {
        try {
            JSONArray data = message.getJSONArray("data");
            if (data != null && data.length() > 0) {
                JSONObject content = data.getJSONObject(0);
                JSONArray assessData = content.getJSONArray("assessDataContent");
                JSONArray rightAnswer = content.getJSONArray("rightAnswerContent");
                assessMap = new TreeMap<>();
                answerMap = new TreeMap<>();
                if (rightAnswer != null) {
                    for (int i = 0; i < rightAnswer.length(); i++) {
                        int id = rightAnswer.getJSONObject(i).getInt("id");
                        String text = rightAnswer.getJSONObject(i).getString("text");
                        answerMap.put(id, text);
                    }
                }
                //字段未空为切到手动答题
                if (assessData != null && assessData.length() != 0) {
                    isSpeakAnswer = true;
                    for (int i = 0; i < assessData.length(); i++) {
                        int id = assessData.getJSONObject(i).getInt("id");
                        String text = assessData.getJSONObject(i).getString("text");
                        assessMap.put(id, text);
                    }
                    boolean hasAudidoPermission = XesPermission.hasSelfPermission(mContext, Manifest.permission
                            .RECORD_AUDIO);
                    // 检查用户麦克风权限
                    if (hasAudidoPermission) {
                        startAssess();
                    } else {
                        //如果没有麦克风权限，申请麦克风权限
                        XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {
                            /**
                             * 结束
                             */
                            @Override
                            public void onFinish() {
                                logger.i("onFinish()");
                            }

                            /**
                             * 用户拒绝某个权限
                             */
                            @Override
                            public void onDeny(String permission, int position) {
                                logger.i("onDeny()");
                            }

                            /**
                             * 用户允许某个权限
                             */
                            @Override
                            public void onGuarantee(String permission, int position) {
                                logger.i("onGuarantee()");
                                startAssess();
                                ;
                            }
                        }, PermissionConfig.PERMISSION_CODE_AUDIO);
                    }
                } else {
                    isSpeakAnswer = false;
                    cancleAssess();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Runnable assessTask = new Runnable() {
        @Override
        public void run() {
            if (assessMap != null && answerMap != null) {
                assessContent = "";
                String answerContent = "";
                for (Integer key : assessMap.keySet()) {
                    assessContent += assessMap.get(key) + "|";
                }
                for (Integer key : answerMap.keySet()) {
                    answerContent += answerMap.get(key);
                }
                // 如果测评文案为空 返回
                if (TextUtils.isEmpty(assessContent)) {
                    return;
                }
                assessContent = assessContent.substring(0, assessContent.length() - 1);
                logger.d("assessContent :" + assessContent);
                File dir = LiveCacheFile.geCacheFile(mContext, "speakChinese");
                FileUtils.deleteDir(dir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                /* 语音保存位置 */
                saveVideoFile = new File(dir, "speakChinese" + System.currentTimeMillis()
                        +"U"+ LiveAppUserInfo.getInstance().getStuId() +"P"+liveId+ ".mp3");
                SpeechParamEntity mParam = new SpeechParamEntity();
                mParam.setRecogType(SpeechConfig.SPEECH_CHINESE_EVALUATOR_OFFLINE_ONLINE);
                mParam.setLang(Constants.ASSESS_PARAM_LANGUAGE_CH);
                mParam.setStrEvaluator(assessContent);
                mParam.setLocalSavePath(saveVideoFile.getPath());
                mParam.setMultRef(false);
                mParam.setVad_max_sec("90");
                mParam.setVad_pause_sec("90");
                mParam.setMult("1");
                if (!isAssessing) {
                    final String finalAnswerContent = answerContent;
                    isAssessing = true;
                    speechUtils.startRecog(mParam, new EvaluatorListener() {
                        @Override
                        public void onBeginOfSpeech() {
                            logger.d("onBeginOfSpeech curTime: " + System.currentTimeMillis());
                        }

                        @Override
                        public void onResult(ResultEntity result) {
                            if (ResultEntity.EVALUATOR_ING == result.getStatus()) {
                                String word = "";
                                int score = 0;
                                if (!result.getLstPhonemeScore().isEmpty()) {
                                    for (int i = 0; i < result.getLstPhonemeScore().size(); i++) {
                                        word += result.getLstPhonemeScore().get(i).getWord();
                                    }
                                    score = result.getScore();
                                    JSONObject jsonData = new JSONObject();
                                    JSONObject data = new JSONObject();
                                    try {
                                        jsonData.put("type", CourseMessage.SEND_getAnswer);
                                        JSONObject resultData = new JSONObject();
                                        if (score >= CUT_OFF_SCORE) {
                                            if (answerMap.containsKey(result.getNewSenIdx())){
                                                resultData.put("isRight", 1);
                                            }else {
                                                resultData.put("isRight", 0);
                                            }
                                            JSONArray userAnswerArray = new JSONArray();
                                            JSONObject userAnswer = new JSONObject();
                                            userAnswer.put("id", result.getNewSenIdx());
                                            userAnswer.put("text", word);
                                            userAnswerArray.put(userAnswer);
                                            resultData.put("userAnswerContent", userAnswerArray);
                                            jsonData.put("data", resultData);
                                            logger.e("onResult: " + jsonData.toString());
                                            StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");

                                        } else {
                                            logger.i("result score" + score + "result text" + word);
                                        }
                                    } catch (JSONException e) {
                                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                                        mLogtf.e("submitData", e);
                                    }
                                    logger.d("onResult: status:" + result.getStatus() +
                                            " word:" + word +
                                            " word score:" + score +
                                            " contscore:" + result.getContScore() +
                                            " partscore:" + result.getPartScore() +
                                            " pronscore:" + result.getPronScore() +
                                            " score:" + result.getScore() +
                                            " level:" + result.getLevel() +
                                            " newsenids:" + result.getNewSenIdx()
                                    );

                                }
                            } else if (ResultEntity.SUCCESS == result.getStatus()) {
                                logger.e("SUCCESS curTime ");
                                isAssessing = false;
                            } else if (ResultEntity.ERROR == result.getStatus()) {
                                logger.e("ERROR");
                                isAssessing = false;
                                Map<String, String> errorData = new HashMap<>();
                                errorData.put("error_code", "" + result.getErrorNo());
                                liveAndBackDebug.umsAgentDebugSys(eventId, errorData);
                                if (result.getErrorNo() == ResultCode.MUTE_AUDIO || result.getErrorNo() == ResultCode
                                        .MUTE) {
                                    if (!recognizeSuccess && isSpeakAnswer && !isDestory) {
                                        XESToastUtils.showToast(mContext, "声音有点小,再来一次吧");
                                    }
                                }
                                onRecognizeStop();
                            }
                        }

                        @Override
                        public void onVolumeUpdate(int volume) {
                            logger.d("volume:" + volume);
                        }
                    });
                }
            }
        }
    };

    /**
     * 开始AI语音评测
     *
     * @param assessMap
     * @param answerMap
     */
    private void startAssess() {
        if (startAssesByRefresh) {
            startAssesByRefresh = false;
            mainHandler.removeCallbacks(assessTask);
            mainHandler.postDelayed(assessTask, 900);
        } else {
            mainHandler.removeCallbacks(assessTask);
            assessTask.run();
        }
    }

    private boolean isDestory = false;
    /**
     * 识别成功
     **/
    private boolean recognizeSuccess = false;

    /**
     * 重启语音识别
     **/
    private void onRecognizeStop() {
        if (isAttach()) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!recognizeSuccess && !isDestory && isSpeakAnswer) {
                        startAssess();
                    }
                }
            }, 300);
        }
    }

    /**
     * 不需要返回结果的 调用cancle
     * 取消识别语音评测
     */
    private void cancleAssess() {
        if (speechUtils != null) {
            speechUtils.cancel();
        }
        isAssessing = false;
    }

    /**
     * 需要识别结果的 调用stop
     * 停止识别
     */
    private void stopAssess() {
        if (speechUtils != null) {
            speechUtils.cancel();
        }
        isAssessing = false;
    }

    @Override
    public void onBack() {
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("closetype", "clickBackButton");
        logHashMap.put("isFinish", "" + isFinish);
        umsAgentDebugSys(eventId, logHashMap.getData());
        LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent
                .H5_TYPE_COURSE, id);
        EventBus.getDefault().post(event);
    }

    @Override
    public void destroy() {
        isFinish = true;
        wvSubjectWeb.destroy();
        cancleAssess();
    }

    @Override
    public void onPause() {
        wvSubjectWeb.onPause();
    }

    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

    @Override
    public String getTestId() {
        return id;
    }

    //强制收题
    @Override
    public void submitData() {
        if (isFinish) {
            return;
        }
        isFinish = true;
        getAnswerType = LiveQueConfig.GET_ANSWERTYPE_FORCE_SUBMIT;
        if (!isSumit) {
            stopAssess();
            resultGotByForceSubmit = true;
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("type", CourseMessage.SEND_getAnswer);
                JSONObject resultData = new JSONObject();
                jsonData.put("data", resultData);
                StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
            } catch (JSONException e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                mLogtf.e("submitData", e);
            }
            XESToastUtils.showToast(mContext, "时间到,停止作答!");
        }
    }

    private void submit(int isforce, String nonce, JSONArray data) {
        isSumit = true;
        //回放已作答过了 直接toast 提示
        if (isPlayBack && newCourseSec != null && newCourseSec.getIsAnswer() == 1) {
            XESToastUtils.showToast(mContext, "该题已作答");
            showAnswerResult(isforce);
        } else {
            subMitTime = System.currentTimeMillis();
            submitAnswer(isforce, nonce, data);
            ChsSpeakLog.anserMode(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), 
                    isSpeakAnswer
                    ? "0" : "1", wvSubjectWeb.getUrl(), isPlayBack);
            if (isSpeakAnswer && saveVideoFile != null){
                ChsSpeakLog.uploadLOG(mContext,liveAndBackDebug,NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),assessContent,liveId,saveVideoFile);
            }
            NewCourseLog.sno5(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), isforce == 1,
                    wvSubjectWeb.getUrl(), ispreload,detailInfo.isTUtor());
        }
    }

    /**
     * 提交
     *
     * @param isforce
     * @param nonce
     */
    private void submitAnswer(final int isforce, final String nonce, JSONArray data) {
        final JSONObject testInfos = new JSONObject();
        NewCourseSec.Test test = tests.get(0);
        JSONObject json = test.getJson();
        try {
            json.put("userAnswerContent", data);
            json.put("index", 0);
            json.put("userAnswerStatus", 0);
            testInfos.put(tests.get(0).getId(), json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime,
                testInfos.toString(), new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        JSONObject jsonObject = (JSONObject) objData[0];
                        showAnswerResult(isforce);
                        onSubmitSuccess(isforce);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        XESToastUtils.showToast(mContext, "答题结果提交失败，请刷新后重新作答！(10002)" + errStatus);
                        isSumit = false;
                        onSubmitError(isforce, failMsg);
                    }
                });


    }

    /**
     * 提交成功
     *
     * @param isforce
     */
    private void onSubmitSuccess(int isforce) {
        NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), true,
                isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), "",detailInfo.isTUtor());
    }

    /**
     * 提交失败
     *
     * @param isforce
     * @param errorMsg
     */
    private void onSubmitError(int isforce, String errorMsg) {
        NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), false,
                isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), errorMsg,detailInfo.isTUtor());

    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        mEnglishH5CoursewareBll = englishH5CoursewareBll;
    }

    private long pagerStart;

    @Override
    protected void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        pagerStart = System.currentTimeMillis();
    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (!loadResult) {
            preLoad.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                rlSubjectLoading.setVisibility(View.GONE);
                preLoad.onStop();
            }
        }
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        preLoad.onStart();
        getCourseWareTests();
    }

    private void getCourseWareTests() {
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                newCourseSec = (NewCourseSec) objData[0];
                logger.d("onDataSucess:time=" + (newCourseSec.getEndTime() - newCourseSec.getReleaseTime()));

                tests = newCourseSec.getTests();
                if (tests.isEmpty()) {
                    XESToastUtils.showToast(mContext, "互动题为空");
                    return;
                }
//                    showControl();
                if (quesJson != null) {
                    for (int i = 0; i < tests.size(); i++) {
                        NewCourseSec.Test test = tests.get(i);
                        JSONArray userAnswerContent = quesJson.optJSONArray("" + i);
                        test.setUserAnswerContent(userAnswerContent);
                    }
                }
                NewCourseSec.Test test = tests.get(0);
                currentIndex = 0;
                int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
                if (type != 0) {
                    ispreload = type == 1;
                } else {
                    ispreload = true;
                }
                if (newCourseSec.getIsAnswer() == 1 && !isPlayBack) {
                    rlSubjectLoading.setVisibility(View.GONE);
                    preLoad.onStop();
                    //如果答完了 直接关闭
                    if (onClose != null) {
                        onClose.onH5ResultClose(SpeakChineseCoursewarePager.this, getBaseVideoQuestionEntity());
                    }
//                    showAnswerResult(0);
                } else {
                    wvSubjectWeb.loadUrl(test.getPreviewPath());
                    NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),
                            getSubtestid(), test.getPreviewPath(), ispreload, test.getId(),detailInfo.isTUtor());
                }
            }


            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg + ",请刷新");
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
                logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
            }
        });
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    /**
     * 课件日志
     */
    private String getSubtestid() {
        if (tests.size() == 1) {
            return "0";
        }
        return "" + (currentIndex + 1);
    }

    /**
     * 小学课件加载-文理
     */
    private class PrimaryPreLoad implements PreLoad {

        @Override
        public void onStart() {
            logger.d("PrimaryPreLoad:onStart");
            try {
                mView.findViewById(R.id.ll_livevideo_subject_loadingl_content).setVisibility(View.GONE);
                GifDrawable gifDrawable = new GifDrawable(mContext.getResources(),
                        R.drawable.livevide_courseware_primary_load);
                rlSubjectLoading.setBackground(gifDrawable);
                gifDrawable.start();
            } catch (Throwable e) {
                logger.e("PrimaryPreLoad:onStart", e);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        @Override
        public void onStop() {
            logger.d("PrimaryPreLoad:onStop");
            Drawable drawable = rlSubjectLoading.getBackground();
            if (drawable instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) drawable;
                gifDrawable.stop();
                gifDrawable.recycle();
            }
        }
    }

    /**
     * 课件加载
     */
    interface PreLoad {
        /**
         * 课件开始加载
         */
        void onStart();

        /**
         * 课件加载中进度
         */
        void onProgressChanged(WebView view, int newProgress);

        /**
         * 课件结束加载
         */
        void onStop();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains("baidu.com")) {
            onClose.onH5ResultClose(this, getBaseVideoQuestionEntity());
            StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
            logHashMap.put("coursewareid", id);
            logHashMap.put("coursewaretype", courseware_type);
            logHashMap.put("closetype", "clickWebCloseButton");
            umsAgentDebugSys(eventId, logHashMap.getData());
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return englishH5Entity;
    }

    @Override
    public boolean isResultRecived() {
        return loadResult;
    }

    class CourseWebViewClient extends MyWebViewClient implements OnHttpCode {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl() + "";
            if (url.contains(".html")) {
                if (!addJs) {
                    addJs = true;
                    WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(view, url);
                    logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse ==
                            null));
                    if (webResourceResponse != null) {
                        return webResourceResponse;
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wvSubjectWeb.stopLoading();
                            }
                        });
                        XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                    }
                }
            } else if (url.contains(WebInstertJs.indexStr())) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            wvSubjectWeb.stopLoading();
                        }
                    });
                    XESToastUtils.showToast(mContext, "通信文件加载失败，请刷新");
                }
            }
            WebResourceResponse webResourceResponse = newCourseCache.shouldInterceptRequest(view, url);
            if (webResourceResponse != null) {
                logger.d("shouldInterceptRequest:url=" + url);
                return webResourceResponse;
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        protected void otherMsg(StableLogHashMap logHashMap, String loadUrl) {
            logHashMap.put("testid", NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts));
            logHashMap.put("ispreload", "" + ispreload);
            logHashMap.put("testsource", "" + ispreload);
            logHashMap.put("errtype", "webView");
            logHashMap.put("subtestid", getSubtestid());
            if (XESCODE.ARTS_SEND_QUESTION == detailInfo.noticeType) {
                logHashMap.put("testsource", "PlatformTest");
            } else if (XESCODE.ARTS_H5_COURSEWARE == detailInfo.noticeType) {
                logHashMap.put("testsource", "PlatformCourseware");
            }
            logHashMap.put("eventid", "" + LogConfig.LIVE_H5PLAT);
        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
        }
    }

    /**
     * 显示 结果页
     *
     * @param isforce
     */
    private void showAnswerResult(final int isforce) {
        isSumit = true;
        englishH5CoursewareSecHttp.getStuTestResult(detailInfo, isPlayBack ? 1 : 0,
                new AbstractBusinessDataCallBack() {

                    @Override
                    public void onDataSucess(Object... objData) {
//                            addResultPager(isforce, (ChineseAISubjectResultEntity) objData[0]);
                        logger.i("showAnswerResult");
                        loadResult = true;
                        PrimaryScienceAnswerResultEntity entity = (PrimaryScienceAnswerResultEntity) objData[0];
                        mGoldNum = entity.getGold();
                        if (allowTeamPk) {
                            mEnergyNum = isforce == 0 ? entity.getEnergy() : 0;
                        }

                        // 对外暴露答题结果
                        broadCastAnswerRestult(entity);

                        PrimaryScienceAnserResultPager primaryScienceAnserResultPager = new
                                PrimaryScienceAnserResultPager(mContext, entity, 1, new
                                PrimaryScienceAnserResultPager.OnNativeResultPagerClose() {
                                    @Override
                                    public void onClose() {
                                        onClose.onH5ResultClose(SpeakChineseCoursewarePager.this,
                                                getBaseVideoQuestionEntity());
                                    }
                                });
                        ((RelativeLayout) mView).addView(primaryScienceAnserResultPager.getRootView(), new
                                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                                .LayoutParams.MATCH_PARENT));
                        NewCourseLog.sno8(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),
                                ispreload, 0,detailInfo.isTUtor());
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        XESToastUtils.showToast(mContext, "获取作答结果失败");
                    }
                });

    }

    /**
     * 对外广播 答题结果
     *
     * @param entity
     */
    private void broadCastAnswerRestult(PrimaryScienceAnswerResultEntity entity) {
        try {
            if (detailInfo != null && detailInfo.englishH5Entity != null) {
                JSONObject answerReuslt = new JSONObject();
                answerReuslt.put("isRight", entity.getType());
                answerReuslt.put("goldNum", mGoldNum);
                answerReuslt.put("energyNum", mEnergyNum);
                answerReuslt.put("id", detailInfo.englishH5Entity.getReleasedPageInfos());
                EventBus.getDefault().post(new AnswerResultEvent(answerReuslt.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestory = true;
        cancleAssess();
    }
    /**
     * 上传交互日志、阿里云
     *
     * @param msg
     */
    private void uploadLOG(String assessContent) {
        final Map<String, String> mData = new HashMap<>();
        mData.put("userid", LiveAppUserInfo.getInstance().getStuId());
        mData.put("liveid", liveId);
        mData.put("assessContent", assessContent);
        uploadCloud(saveVideoFile.getPath(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                XesCloudResult result = (XesCloudResult) objData[0];
                mData.put("url", result.getHttpPath());
                mData.put("upload", "success");
                umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                mData.put("upload", "fail");
                mData.put("url", "");
                umsAgentDebugInter(LiveVideoConfig.LIVE_VOICE_CHAT, mData);
            }
        });
    }

    XesCloudUploadBusiness uploadBusiness;

    private void uploadCloud(String path, final AbstractBusinessDataCallBack callBack) {
            if (uploadBusiness == null) {
                uploadBusiness = new XesCloudUploadBusiness(mContext);
            }
            final CloudUploadEntity entity = new CloudUploadEntity();
            entity.setFilePath(path);
            entity.setCloudPath(CloudDir.LIVE_SPEAK_CHINESE);
        entity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                logger.i("upload Success:" + result.getHttpPath());
                callBack.onDataSucess(result);
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.e("upload Error:" + result.getErrorMsg());
                callBack.onDataFail(0, result.getErrorMsg());
            }
        });

    }
}
