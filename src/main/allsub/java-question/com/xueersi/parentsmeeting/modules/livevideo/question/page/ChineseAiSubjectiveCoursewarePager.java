package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ChsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionOnSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.dialog.CourseTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.CourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import pl.droidsonroids.gif.GifDrawable;

/**
 * FileName: ChineseAiSubjectiveCoursewarePager
 * Author: WangDe
 * Date: 2019/3/27 10:38
 * Description: 语文AI主观题 新课件，去掉h5壳
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChineseAiSubjectiveCoursewarePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager, BaseQuestionWebInter {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    /**
     * 理科初高中新课件平台 强制提交js
     */
//    private String jsClientSubmit = "javascript:__CLIENT_SUBMIT__()";
    /**
     * 理科初高中新课件，是不是已经收卷
     */
    private boolean isFinish = false;
    /**
     * 理科初高中新课件，是不是已经提交
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
    String aiUrl;

    /**
     * 强制收题
     */
    private final int NOTFORCE = 0;

    private final int FORCE = 1;
    private ScrollView svSubjectWeb;
    private KeyboardUtil.OnKeyboardShowingListener mKeyboardListener;
    private LiveHttpManager mLiveHttpManager;
    private boolean isFirstAI = true;

    public ChineseAiSubjectiveCoursewarePager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity,
                                              boolean isPlayBack, String liveId, String id,
                                              EnglishH5Entity englishH5Entity,
                                              final String courseware_type, String nonce,
                                              EnglishH5CoursewareBll.OnH5ResultClose onClose,
                                              String isShowRanks, int isArts, boolean allowTeamPk, String aiUrl) {
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
        this.aiUrl = aiUrl;
        if (isArts != LiveVideoSAConfig.ART_EN) {
            this.educationstage = detailInfo.getEducationstage();
        }
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
        liveAndBackDebug.addCommonData("isplayback", isPlayBack ? "1" : "0");
        mView = initView();
        entranceTime = System.currentTimeMillis() / 1000;
        try {
            if (isPlayBack) {
                NewCourseLog.sno1back(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType, detailInfo.isTUtor());
            } else {
                NewCourseLog.sno2(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType, detailInfo.isTUtor());
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_chs_ai_h5_courseware_native, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        svSubjectWeb = view.findViewById(R.id.sv_livevideo_web);
        preLoad = new MiddleSchool();
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

//                if (allowTeamPk && newCourseSec != null && newCourseSec.getIsAnswer() == 0) {
//                    LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent
//                            .H5_TYPE_COURSE, id);
//                    if (mEnglishH5CoursewareBll != null) {
//                        event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
//                        mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
//                    }
//                    EventBus.getDefault().post(event);
//                }
                preLoad.onStop();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        today = dateFormat.format(date);
        getTodayQues();
        newCourseCache = new NewCourseCache(mContext, liveId, "99999");
        addJavascriptInterface();
        wvSubjectWeb.getSettings().setLoadWithOverviewMode(false);
//        wvSubjectWeb.getSettings().setDisplayZoomControls(false);
        wvSubjectWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mKeyboardListener = new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                ChineseAiSubjectiveCoursewarePager.this.onKeyboardShowing(isShowing);
            }
        };
        if (isPlayBack) {
            KPSwitchFSPanelLinearLayout v_livevideo_question_content_bord = mView.findViewById(R.id.v_livevideo_question_content_bord);
            KeyboardUtil.attach((Activity) mContext, v_livevideo_question_content_bord, new KeyboardUtil.OnKeyboardShowingListener() {
                @Override
                public void onKeyboardShowing(boolean isShowing) {
                    ChineseAiSubjectiveCoursewarePager.this.onKeyboardShowing(isShowing);
                }
            });
        } else {
            KeyboardUtil.registKeyboardShowingListener(mKeyboardListener);
        }
//        KeyboardUtil.attach((Activity) mContext, new KPSwitchFSPanelLinearLayout(mContext), mKeyboardListener);
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
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
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, "99999", creattime, new StaticWeb.OnMessage() {

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
//                        onLoadComplete(where, message);
                        onQuestionStatus(message);
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
                wvSubjectWeb.reload();
            }
        });
    }

    @Override
    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public void close() {
        onClose.onH5ResultClose(this, getBaseVideoQuestionEntity());
    }

    @Override
    public String getUrl() {
        String queskey =
                ("" + englishH5Entity.getPackageId()).hashCode() + "-" + ("" + englishH5Entity.getReleasedPageInfos()).hashCode();
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

    private void saveThisQues(int index, JSONArray userAnswerContent) {
        try {
            String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}",
                    ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = getTodayLive(string);
            if (jsonObject != null) {
                JSONObject todayLiveObj = jsonObject.getJSONObject("todaylive").getJSONObject(liveId);
                String queskey = getQuesKey();
                JSONObject ques = todayLiveObj.optJSONObject("ques-" + queskey);
                if (ques == null) {
                    ques = new JSONObject();
                }
                ques.put("" + index, userAnswerContent);
                todayLiveObj.put("ques-" + queskey, ques);
                quesJson = ques;
                mShareDataManager.put(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "" + jsonObject,
                        ShareDataManager.SHAREDATA_USER);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            mLogtf.e("saveThisQues", e);
        }
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
//                    String d = message.getString("data");
//                    JSONArray data = new JSONArray(d);
                    JSONArray data = msg.getJSONArray("data");
                    if (data != null && data.length() > 0) {
                        JSONArray userAnswerContent = data.getJSONObject(0).optJSONArray("userAnswerContent");
                        JSONArray rightAnswerContent = data.getJSONObject(0).optJSONArray("rightAnswerContent");
                        String maxScore = data.getJSONObject(0).optString("maxScore", "0");
                        oldTest.setUserAnswerContent(userAnswerContent);
                        oldTest.setRightAnswerContent(rightAnswerContent);
                        oldTest.setMaxScore(maxScore);
                    }
                    saveThisQues(currentIndex, data);
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                logger.d("onAnswer:answer:getAnswerType=" + getAnswerType + ",index=" + currentIndex);
                if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_SUBMIT || getAnswerType == LiveQueConfig.GET_ANSWERTYPE_FORCE_SUBMIT) {
                    if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_SUBMIT) {
                        boolean needTip = false;
                        a:
                        for (int i = 0; i < tests.size(); i++) {
                            NewCourseSec.Test test = tests.get(i);
                            JSONArray userAnswerContent = test.getUserAnswerContent();
                            if (userAnswerContent == null) {
                                needTip = true;
                                break;
                            } else {
                                for (int j = 0; j < userAnswerContent.length(); j++) {
                                    try {
                                        JSONArray answerContent = userAnswerContent.getJSONObject(j).getJSONArray(
                                                "userAnswerContent");
                                        if (answerContent.length() == 0) {
                                            needTip = true;
                                            break a;
                                        } else {
                                            for (int k = 0; k < answerContent.length(); k++) {
                                                JSONObject object = answerContent.getJSONObject(k);
                                                if (StringUtils.isEmpty(object.optString("text"))) {
                                                    needTip = true;
                                                    break a;
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (needTip) {
                            if (courseTipDialog != null) {
                                courseTipDialog.cancelDialog();
                            }
                            courseTipDialog = new CourseTipDialog(mContext,
                                    (Application) mContext.getApplicationContext());
                            courseTipDialog.setOnClick(new CourseTipDialog.OnClick() {

                                @Override
                                public void onCancle(CourseTipDialog dialog, View view) {
                                    dialog.cancelDialog();
                                    if (dialog == courseTipDialog) {
                                        courseTipDialog = null;
                                    }
                                }

                                @Override
                                public void onCommit(CourseTipDialog dialog, View view) {
                                    dialog.cancelDialog();
                                    if (dialog == courseTipDialog) {
                                        courseTipDialog = null;
                                    }
                                    submit(0, nonce);
                                }
                            });
                            courseTipDialog.showDialog();
                        } else {
                            submit(0, nonce);
                        }
                    } else {
                        submit(1, nonce);
                    }
                } else {
                    if (tests.size() == 1) {
                        submit(0, nonce);
                    }

                }
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
                            (System.currentTimeMillis() - pagerStart), isRefresh, refreshTime, detailInfo.isTUtor());
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
        KeyboardUtil.unRegistKeyboardShowingListener(mKeyboardListener);
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
        if (courseTipDialog != null) {
            courseTipDialog.cancelDialog();
            courseTipDialog = null;
        }
        if (!isSumit) {
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
//            XESToastUtils.showToast(mContext, "时间到,停止作答!");
        }
    }

    private void submit(int isforce, String nonce) {
        isSumit = true;
        KeyboardUtil.hideKeyboard(wvSubjectWeb);
        subMitTime = System.currentTimeMillis();
        submitAnswer(isforce, nonce);
        NewCourseLog.sno5(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), isforce == 1,
                wvSubjectWeb.getUrl(), ispreload, detailInfo.isTUtor());
    }

    /**
     * 提交
     *
     * @param isforce
     * @param nonce
     */
    private void submitAnswer(final int isforce, final String nonce) {
        JSONObject data = new JSONObject();
        JSONObject dataJson = new JSONObject();
//        for (int i = 0; i < tests.size(); i++) {
        if (!tests.isEmpty()) {
            NewCourseSec.Test test = tests.get(0);
            testId = test.getId();
            dataJson = test.getJson();
            JSONArray userAnswerContent = test.getUserAnswerContent();
            JSONArray rightAnswerContent = test.getRightAnswerContent();
            try {
                int userAnswerStatus = 0;
                //用户没有作答,字段不能缺
                if (userAnswerContent == null || userAnswerContent.length() == 0) {
                    userAnswerContent = new JSONArray();
                    //需要填上id 和 text
                    JSONObject emptyJson = new JSONObject();
                    emptyJson.put("id", "");
                    emptyJson.put("text", "");
                    emptyJson.put("score", "");
                    emptyJson.put("scoreKey", "");
                    userAnswerContent.put(emptyJson);
                } else {
                    for (int i = 0; i < userAnswerContent.length(); i++) {
                        JSONObject temp = userAnswerContent.getJSONObject(i);
                        String text = temp.optString("text");
                        if (text != null) {
                            text = text.replaceAll(" ", "");
                            temp.remove("text");
                            temp.put("text", text);
                        }
                    }
                }
                if (rightAnswerContent == null || rightAnswerContent.length() == 0) {
                    rightAnswerContent = new JSONArray();
                    //需要填上id 和 text
                    JSONObject emptyJson = new JSONObject();
                    emptyJson.put("text", "");
                    emptyJson.put("score", "");
                    emptyJson.put("scoreKey", "");
                    rightAnswerContent.put(emptyJson);
                }
                dataJson.put("testid", test.getId());
                dataJson.put("userid", LiveAppUserInfo.getInstance().getStuId());
                dataJson.put("hasAnswer", isforce);
                dataJson.put("liveId", liveId);
                dataJson.put("gradeType", Integer.parseInt(LiveAppUserInfo.getInstance().getGradeCode()));
                dataJson.put("deviceid", 8);
                dataJson.put("totalScore", 0);
                if (test.getMaxScore() != null && !test.getMaxScore().isEmpty()) {
                    dataJson.put("maxScore", Integer.parseInt(test.getMaxScore()));
                } else {
                    dataJson.put("maxScore", 0);
                }
                dataJson.put("lostReason", "");
                dataJson.put("rightAnswerContent", rightAnswerContent);
                dataJson.put("userAnswerContent", userAnswerContent);
                data.put(test.getId(), dataJson);
            } catch (JSONException e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                mLogtf.e("submit", e);
            }
        }
//        }
        if (mLiveHttpManager == null) {
            mLiveHttpManager = new LiveHttpManager(mContext);
        }

        mLiveHttpManager.submitChineseAISubjectiveAnswer(aiUrl, data.toString(), new AiHttpCallBack(isforce, data));

        /** 强制收题直接显示结果页*/
//        if (FORCE == isforce) {
//            ChineseAISubjectResultEntity resultEntity = new ChineseAISubjectResultEntity();
//            List<String> answerList = new ArrayList<>();
//            resultEntity.setTotalScore(0);
//            try {
//                if (dataJson != null && dataJson.has("rightAnswerContent")) {
//                    JSONArray rightAnswer = dataJson.getJSONArray("rightAnswerContent");
//                    for (int i = 0; i < rightAnswer.length(); i++) {
//                        answerList.add(rightAnswer.getJSONObject(i).optString("text"));
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            resultEntity.setRightAnswers(answerList);
//            resultEntity.setGold(0);
//            ChsAnswerResultEvent artsAnswerResultEvent = new ChsAnswerResultEvent("", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT);
//            artsAnswerResultEvent.setDetailInfo(detailInfo);
//            artsAnswerResultEvent.setIspreload(ispreload);
//            artsAnswerResultEvent.setResultEntity(resultEntity);
//            EventBus.getDefault().post(artsAnswerResultEvent);
////            addResultPager(1, resultEntity);
//            if (onClose != null) {
//                loadResult = true;
//                onClose.onH5ResultClose(ChineseAiSubjectiveCoursewarePager.this, getBaseVideoQuestionEntity());
//            }
//        }

    }

    class AiHttpCallBack extends HttpCallBack {

        private int isforce;
        private JSONObject data;

        AiHttpCallBack(int isforce, JSONObject data) {
            super(false);
            this.isforce = isforce;
            this.data = data;
        }

        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

            JSONObject response = (JSONObject) responseEntity.getJsonObject();
            if (response.has("totalScore")) {
                XESToastUtils.showToast(mContext, "答题结果提交失败，请刷新后重新作答！(10001)");
                return;
            }
            if (response.has(testId)) {
                JSONObject resultData = response.getJSONObject(testId);
                JSONArray userAnswerContent = resultData.getJSONArray("userAnswerContent");
                JSONArray rightAnswerContent = resultData.getJSONArray("rightAnswerContent");
                resultData.remove("rightAnswerContent");
                resultData.remove("userAnswerContent");
                resultData.remove("testid");
                resultData.put("id", testId);
                resultData.put("index", 0);
                resultData.put("userAnswerStatus", 0);
                resultData.put("endTime", System.currentTimeMillis() / 1000);
                resultData.put("srcType", LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE);
                JSONArray userAnswer = new JSONArray();
                JSONObject userContent = new JSONObject();
                userContent.put("userAnswerContent", userAnswerContent);
                JSONObject rightContent = new JSONObject();
                userContent.put("rightAnswerContent", rightAnswerContent);
                userAnswer.put(userContent);
                userAnswer.put(rightContent);
                resultData.put("userAnswerContent", userAnswer);
            }

            englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime,
                    response.toString(), new AbstractBusinessDataCallBack() {
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
            if (FORCE == isforce) {
                if (onClose != null) {
                    loadResult = true;
                    onClose.onH5ResultClose(ChineseAiSubjectiveCoursewarePager.this, getBaseVideoQuestionEntity());
                }
            }
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
            super.onPmFailure(error, msg);
            if (isFirstAI) {
                isFirstAI = false;
                mLiveHttpManager.submitChineseAISubjectiveAnswer(aiUrl, data.toString(), this);
            } else {
                XESToastUtils.showToast(mContext, msg);
            }
            isSumit = false;
            onSubmitError(isforce, msg);
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
            super.onPmError(responseEntity);
            XESToastUtils.showToast(mContext, "答题结果提交失败，请刷新后重新作答！（10001）");
        }
    }

    /**
     * 提交成功
     *
     * @param isforce
     */
    private void onSubmitSuccess(int isforce) {
        NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), true,
                isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), "", detailInfo.isTUtor());
    }

    /**
     * 提交失败
     *
     * @param isforce
     * @param errorMsg
     */
    private void onSubmitError(int isforce, String errorMsg) {
        NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), false,
                isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), errorMsg, detailInfo.isTUtor());

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
        } else {
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
                    if (isPlayBack) {
                        try {
                            NewCourseLog.sno2back(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType, "false", detailInfo.isTUtor());
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                    return;
                }
                if (isPlayBack) {
                    try {
                        NewCourseLog.sno2back(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType, "true", detailInfo.isTUtor());
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
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
                wvSubjectWeb.loadUrl(test.getPreviewPath());
                int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
                if (type != 0) {
                    ispreload = type == 1;
                } else {
                    ispreload = true;
                }
                if (newCourseSec.getIsAnswer() == 1 && !isPlayBack) {
//                    rlSubjectLoading.setVisibility(View.GONE);
//                    preLoad.onStop();
                    showAnswerResult(0);
                } else {
                    NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),
                            getSubtestid(), test.getPreviewPath(), ispreload, test.getId(), detailInfo.isTUtor());
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
                if (isPlayBack) {
                    try {
                        NewCourseLog.sno2back(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType, "false", detailInfo.isTUtor());
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
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
     * 初中课件加载
     */
    private class MiddleSchool implements PreLoad {
        private ImageView ivLoading;
        private ProgressBar pgCourseProg;
        private TextView tvDataLoadingTip;

        @Override
        public void onStart() {
            ivLoading = mView.findViewById(R.id.iv_data_loading_show);
            pgCourseProg = mView.findViewById(R.id.pg_livevideo_new_course_prog);
            tvDataLoadingTip = mView.findViewById(R.id.tv_data_loading_tip);
            logger.d("MiddleSchool:onStart");
            try {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.animlst_app_loading);
                ivLoading.setBackground(drawable);
                ((AnimationDrawable) drawable).start();
            } catch (Exception e) {
                if (mLogtf != null) {
                    mLogtf.e("initData", e);
                }
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pgCourseProg.setProgress(newProgress);
            tvDataLoadingTip.setText("加载中 " + newProgress + "%");
        }

        @Override
        public void onStop() {
            logger.d("MiddleSchool:onStart:ivLoading=null?" + (ivLoading == null));
            if (ivLoading != null) {
                try {
                    Drawable drawable = ivLoading.getBackground();
                    if (drawable instanceof AnimationDrawable) {
                        ((AnimationDrawable) drawable).stop();
                    }
                } catch (Exception e) {
                    if (mLogtf != null) {
                        mLogtf.e("onProgressChanged", e);
                    }
                }
            }
        }
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
                    logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse == null));
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
     * 显示 AI语文主观题 结果页
     *
     * @param isforce
     */
    private void showAnswerResult(final int isforce) {

        isSumit = true;
        if (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage)) {
            englishH5CoursewareSecHttp.getStuTestResult(detailInfo, isPlayBack ? 1 : 0,
                    new AbstractBusinessDataCallBack() {

                        @Override
                        public void onDataSucess(Object... objData) {
//                            addResultPager(isforce, (ChineseAISubjectResultEntity) objData[0]);
                            ChsAnswerResultEvent chsAnswerResultEvent = new ChsAnswerResultEvent(objData[1] + "", ChsAnswerResultEvent.TYPE_AI_CHINESE_ANSWERRESULT);
                            chsAnswerResultEvent.setDetailInfo(detailInfo);
                            chsAnswerResultEvent.setIspreload(ispreload);
                            chsAnswerResultEvent.setResultEntity((ChineseAISubjectResultEntity) objData[0]);
                            EventBus.getDefault().post(chsAnswerResultEvent);
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            super.onDataFail(errStatus, failMsg);
                            XESToastUtils.showToast(mContext, "答题结果提交失败，请刷新后重新作答！(10003)" + errStatus);
                        }
                    });
        }
    }

    /**
     * 显示结果页
     *
     * @param isforce
     * @param resultEntity
     */
//    private void addResultPager(final int isforce, ChineseAISubjectResultEntity resultEntity) {
//        loadResult = true;
//        if (isforce == 0) {
//            mGoldNum = resultEntity.getGold();
//            if (allowTeamPk) {
//                mEnergyNum = resultEntity.getEnergy();
//            }
//        }
//        answerResultPager = new ChiAnswerResultPager(mContext, resultEntity, new AnswerResultStateListener() {
//            @Override
//            public void onCompeletShow() {
//
//            }
//
//            @Override
//            public void onAutoClose(BasePager basePager) {
//            }
//
//            @Override
//            public void onCloseByUser() {
//                if (isforce == 0){
//                    onClose.onH5ResultClose(ChineseAiSubjectiveCoursewarePager.this, getBaseVideoQuestionEntity());
//                }
//            }
//        });
//        ((RelativeLayout) mView.getParent()).addView(answerResultPager.getRootView(),
//                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
//        NewCourseLog.sno8(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts),
//                ispreload, 0);
//    }
    public void onKeyboardShowing(boolean isShowing) {
        ViewGroup.MarginLayoutParams lpsc = (ViewGroup.MarginLayoutParams) svSubjectWeb.getLayoutParams();
        svSubjectWeb.post(new Runnable() {
            @Override
            public void run() {
                svSubjectWeb.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        int bottomMargin;
        if (isShowing) {
            int panelHeight = KeyboardUtil.getValidPanelHeight(mContext);
            bottomMargin = panelHeight;
        } else {
            bottomMargin = 0;
        }
        if (bottomMargin != lpsc.bottomMargin) {
            lpsc.bottomMargin = bottomMargin;
//            wvSubjectWeb.setLayoutParams(lp);
//            lp.setMargins(0,-bottomMargin,0,bottomMargin);
            lpsc.setMargins(0, 0, 0, bottomMargin);
            LayoutParamsUtil.setViewLayoutParams(svSubjectWeb, lpsc);
        }
    }

    public void setAISubjectUrl(String aiUrl) {
        this.aiUrl = aiUrl;
    }
}
