package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.logerhelper.MobAgent;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.dialog.CourseTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.MiddleResult;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by linyuqiang on 2019/3/5.
 * 新课件，去掉h5壳
 */
public class CoursewareNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager, BaseQuestionWebInter {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    /** 理科初高中新课件平台 强制提交js */
    private String jsClientSubmit = "javascript:__CLIENT_SUBMIT__()";
    /** 理科初高中新课件，是不是已经收卷 */
    private boolean isFinish = false;
    /** 理科初高中新课件，是不是已经提交 */
    private boolean isSumit = false;
    private long subMitTime;
    private String liveId;
    private EnglishH5Entity englishH5Entity;
    /** 是不是回放 */
    private boolean isPlayBack;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String url;
    private String id;
    /** 课件类型，新课件没用 */
    private String courseware_type;
    private String nonce;
    private String isShowRanks;
    /** 互动题打开时间 */
    private long entranceTime;
    /** 文理英属性 */
    private int isArts;
    /** 文理的战队pk */
    private boolean allowTeamPk;
    /** 英语新课件 */
    private boolean isNewArtsCourseware;
    private VideoQuestionLiveEntity detailInfo;
    /** 学年 */
    private String educationstage;
    /** 战队pk用，金币 */
    private int mGoldNum = -1;
    /** 战队pk用，能量 */
    private int mEnergyNum = -1;
    /** 加载的布局 */
    private RelativeLayout rlSubjectLoading;
    /** 下方控制条 */
    private RelativeLayout rlCourseControl;
    /** 倒计时 */
    private TextView tvCourseTimeText;
    /** 课件接口失败刷新 */
    private ImageView ivCourseRefresh;
    /** 课件网页刷新 */
    private ImageView ivWebViewRefresh;
    /** 课件题目数量 */
    private TextView tvCourseNum;
    /** 课件上一题 */
    private Button btCoursePre;
    /** 课件下一题 */
    private Button btCourseNext;
    /** 课件提交 */
    private Button btCourseSubmit;
    /** 新课件缓存 */
    private NewCourseCache newCourseCache;
    /** 新课件是否是预加载 */
    private boolean ispreload;
    private LiveAndBackDebug liveAndBackDebug;
    /** 显示下方控制布局 */
    private boolean showControl = false;
    /** 在网页中嵌入js，只嵌入一次 */
    private boolean addJs = false;
    /** 是不是刷新，加载完成 */
    private int isRefresh = 0;
    /** 刷新次数 */
    private int refreshTime = 0;
    private NewCourseSec newCourseSec;
    private ArrayList<NewCourseSec.Test> tests = new ArrayList<>();
    private int currentIndex = 0;
    /** 发送getAnswer的类型 */
    private int getAnswerType = 0;
    /** 加载结果页 */
    private boolean loadResult = false;
    /** 确认提交的弹窗 */
    private CourseTipDialog courseTipDialog;
    /** 保存今天互动题 */
    private String today;
    /** 保存互动题 */
    private JSONObject quesJson;
    /** 保存互动题开始时间 */
    private long startQueTime;
    /** 课件加载 */
    private PreLoad preLoad;
    /** 课件题目数量 */
    private int totalQuestion = -1;

    /**
     * 结果页面 是否是由强制提交 产生的
     */
    private boolean resultGotByForceSubmit;

    public CoursewareNativePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity,
                                 boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity,
                                 final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose,
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
        if (detailInfo.isTUtor()) {
            isArts = LiveVideoSAConfig.ART_SEC;
        }
        if (isArts != LiveVideoSAConfig.ART_EN) {
            this.educationstage = detailInfo.getEducationstage();
        }
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
        mView = initView();
//        initWebView();
//        setErrorTip("H5课件加载失败，请重试");
//        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
        entranceTime = System.currentTimeMillis() / 1000;
        try {
            NewCourseLog.sno2(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), detailInfo.noticeType);
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_native, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        rlCourseControl = view.findViewById(R.id.rl_livevideo_new_course_control);
        if (isArts != LiveVideoSAConfig.ART_EN && (LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage))) {
            LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_h5_courseware_control_primary, rlCourseControl);
            preLoad = new PrimaryPreLoad();
        } else {
            if (isArts == LiveVideoSAConfig.ART_EN) {
                LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_h5_courseware_control_middle_en, rlCourseControl);
            } else {
                LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_h5_courseware_control_middle, rlCourseControl);
            }
            preLoad = new MiddleSchool();
        }
        //下方控制条的一些布局
        tvCourseNum = view.findViewById(R.id.tv_livevideo_new_course_num);
        tvCourseTimeText = view.findViewById(R.id.tv_livevideo_new_course_time_text);
        btCoursePre = view.findViewById(R.id.bt_livevideo_new_course_pre);
        btCourseNext = view.findViewById(R.id.bt_livevideo_new_course_next);
        btCourseSubmit = view.findViewById(R.id.bt_livevideo_new_course_submit);
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
                if (isArts == LiveVideoSAConfig.ART_EN) {
                    LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent
                            .H5_TYPE_COURSE, id);
                    if (mEnglishH5CoursewareBll != null) {
                        event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
                        mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
                    }
                    EventBus.getDefault().post(event);

                }  else if (!detailInfo.isTUtor())  {
                    if (allowTeamPk && newCourseSec != null) {
                        int gold = newCourseSec.getIsAnswer() == 0 ? mGoldNum : -1;
                        int energy = newCourseSec.getIsAnswer() == 0 ? mEnergyNum : -1;
                        LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(gold, energy, LiveRoomH5CloseEvent
                                .H5_TYPE_COURSE, id);
                        if (mEnglishH5CoursewareBll != null) {
                            event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
                            mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
                        }
                        event.setScienceNewCourseWare(englishH5Entity.getNewEnglishH5());
                        event.setForceSubmit(resultGotByForceSubmit);
                        EventBus.getDefault().post(event);
                    }
                }
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
        newCourseCache = new NewCourseCache(mContext, liveId);
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
                    CrashReport.postCatchedException(new Exception());
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        CourseWebViewClient courseWebViewClient = new CourseWebViewClient();
        newCourseCache.setOnHttpCode(courseWebViewClient);
        wvSubjectWeb.setWebViewClient(courseWebViewClient);
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {

            @Override
            public void postMessage(String where, final JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    if (CourseMessage.REC_close.equals(type)) {
                        handler.post(new Runnable() {
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
        btCoursePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnswerType = LiveQueConfig.GET_ANSWERTYPE_PRE;
                btCoursePre.setEnabled(false);
                btCourseNext.setEnabled(false);
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", CourseMessage.SEND_getAnswer);
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btCourseNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnswerType = LiveQueConfig.GET_ANSWERTYPE_NEXT;
                btCoursePre.setEnabled(false);
                btCourseNext.setEnabled(false);
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", CourseMessage.SEND_getAnswer);
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btCourseSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnswerType = LiveQueConfig.GET_ANSWERTYPE_SUBMIT;
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", CourseMessage.SEND_getAnswer);
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    CrashReport.postCatchedException(e);
                    mLogtf.e("btCourseSubmit", e);
                }
            }
        });
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

    }

    @Override
    public String getUrl() {
        if (isArts == LiveVideoSAConfig.ART_SEC || isArts == LiveVideoSAConfig.ART_CH) {
            String queskey = ("" + englishH5Entity.getPackageId()).hashCode() + "-" + ("" + englishH5Entity.getReleasedPageInfos()).hashCode();
            return queskey;
        }
        return url;
    }

    private void getTodayQues() {
        String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
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
                CrashReport.postCatchedException(e);
                mLogtf.e("getTodayQues", e);
            }
        }
    }

    private String getQuesKey() {
        if (isArts == LiveVideoSAConfig.ART_SEC || isArts == LiveVideoSAConfig.ART_CH) {
            String queskey = englishH5Entity.getPackageId().hashCode() + "-" + englishH5Entity.getReleasedPageInfos().hashCode();
            return queskey;
        } else if (isArts == LiveVideoSAConfig.ART_EN) {
            return id;
        }
        return "";
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
            CrashReport.postCatchedException(e);
            mLogtf.e("getTodayLive", e);
        }
        return null;
    }

    private void saveThisQues(int index, JSONArray userAnswerContent) {
        try {
            String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
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
                mShareDataManager.put(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
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
            String string = mShareDataManager.getString(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
            JSONObject jsonObject = getTodayLive(string);
            if (jsonObject != null) {
                JSONObject todayLiveObj = jsonObject.getJSONObject("todaylive").getJSONObject(liveId);
                String queskey = getQuesKey();
                todayLiveObj.put("start-" + queskey, startQueTime);
                mShareDataManager.put(LiveQueConfig.LIVE_STUDY_REPORT_IMG, "" + jsonObject, ShareDataManager.SHAREDATA_USER);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            mLogtf.e("saveThisQues", e);
        }
    }

    private void onAnswer(final JSONObject message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                NewCourseSec.Test oldTest = tests.get(currentIndex);
                try {
                    JSONArray userAnswerContent = message.getJSONArray("data");
                    oldTest.setUserAnswerContent(userAnswerContent);
                    saveThisQues(currentIndex, userAnswerContent);
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
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
                                        JSONArray answerContent = userAnswerContent.getJSONObject(j).getJSONArray("userAnswerContent");
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
                            courseTipDialog = new CourseTipDialog(mContext, (Application) mContext.getApplicationContext());
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
                                    submit(0, "");
                                }
                            });
                            courseTipDialog.showDialog();
                        } else {
                            submit(0, "");
                        }
                    } else {
                        submit(1, "");
                    }
                } else {
                    if (tests.size() == 1) {
                        submit(0, "");
//                        setViewEnable("onAnswer1");
                    } else {
                        if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_PRE) {
                            currentIndex--;
                            btCourseSubmit.setVisibility(View.GONE);
                        } else if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_NEXT) {
                            currentIndex++;
                        }
                        setViewEnable("onAnswer2");
                        if (currentIndex >= 0 && currentIndex < tests.size()) {
                            setNum(currentIndex + 1);
                            NewCourseSec.Test test = tests.get(currentIndex);
                            addJs = false;
                            NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), test.getPreviewPath(), ispreload, test.getId());
                            wvSubjectWeb.loadUrl(test.getPreviewPath());
                        }
                    }
                }
            }
        });
    }


    /**
     * 英语提交接口是 submitH5 的
     *
     * @return
     */
    private boolean submitH5() {
        boolean submitH5 = false;
        if (LiveQueConfig.EN_COURSE_TYPE_VOICE_BLANK.equals(detailInfo.voiceType) || LiveQueConfig.EN_COURSE_TYPE_VOICE_CHOICE.equals(detailInfo.voiceType)) {
            submitH5 = true;
        } else {
            if (LiveQueConfig.getSubmitH5Types().contains(detailInfo.type)) {
                submitH5 = true;
            }
        }
        return submitH5;
    }

    private void onLoadComplete(final String where, final JSONObject message) {
        handler.post(new Runnable() {
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
                    NewCourseLog.sno4(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), wvSubjectWeb.getUrl(), ispreload, pageid, (System.currentTimeMillis() - pagerStart), isRefresh, refreshTime);
                    isRefresh = 0;
                }
                setViewEnable("onLoadComplete");
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

    private void setViewEnable(String method) {
        mLogtf.d("setViewEnable:method=" + method + ",showControl=" + showControl);
        if (showControl) {
            if (currentIndex == 0) {
                btCoursePre.setEnabled(false);
                if (tests.size() > 0) {
                    btCourseNext.setEnabled(true);
                    btCourseNext.setVisibility(View.VISIBLE);
                }
            } else if (currentIndex == tests.size() - 1) {
                btCourseNext.setEnabled(false);
                btCourseNext.setVisibility(View.INVISIBLE);
                btCourseSubmit.setVisibility(View.VISIBLE);
                if (tests.size() > 0) {
                    btCoursePre.setEnabled(true);
                }
            } else {
                btCoursePre.setEnabled(true);
                btCourseNext.setVisibility(View.VISIBLE);
                btCourseNext.setEnabled(true);
            }
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
    }

    @Override
    public void destroy() {
        isFinish = true;
        wvSubjectWeb.destroy();
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

    @Override
    public void submitData() {
        if (isFinish) {
            return;
        }
        isFinish = true;
        resultGotByForceSubmit = !loadResult;
        if (loadResult) {
            //初中结果页是网页，需要调接口
            if (isArts != LiveVideoSAConfig.ART_EN && (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage))) {
                wvSubjectWeb.loadUrl(jsClientSubmit);
            } else if (detailInfo.isTUtor()){
                wvSubjectWeb.loadUrl(jsClientSubmit);
            }
        } else {
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
                    CrashReport.postCatchedException(e);
                    mLogtf.e("submitData", e);
                }
                if (LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage)) {
                    XESToastUtils.showToast(mContext, "时间到,停止作答!");
                }
            }
        }
    }

    private void submit(int isforce, String nonce) {
        if (loadResult) {
            //初中结果页是网页，需要调接口
            if (isArts != LiveVideoSAConfig.ART_EN && (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage))) {
                wvSubjectWeb.loadUrl(jsClientSubmit);
            }
        } else {
            isSumit = true;
            subMitTime = System.currentTimeMillis();
            if (isArts == LiveVideoSAConfig.ART_EN) {
                int isForce = isforce == 0 ? 1 : 2;
                submitEn(isForce, nonce);
            } else {
                submitSec(isforce, nonce);
            }
            NewCourseLog.sno5(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), isforce == 1, wvSubjectWeb.getUrl(), ispreload);
        }
    }

    private void submitEn(final int isforce, String nonce) {
        if (LiveQueConfig.getSubmitMultiTestTypes().contains(detailInfo.getArtType())) {
            submitMultiTest(isforce, nonce);
        } else {
//            if (LiveQueConfig.EN_COURSE_TYPE_VOICE_BLANK.equals(detailInfo.voiceType) || LiveQueConfig.EN_COURSE_TYPE_VOICE_CHOICE.equals(detailInfo.voiceType)) {
//                submitVoice(isforce, nonce);
//            }
            submitVoice(isforce, nonce);
        }
    }

    /**
     * 英语本地上传语音题提交
     *
     * @param isforce
     * @param nonce
     */
    private void submitVoice(final int isforce, String nonce) {
        NewCourseSec.Test test = tests.get(0);
        JSONArray userAnswerContent = test.getUserAnswerContent();
        JSONArray userAnswerArray = new JSONArray();
        int length = 0;
        if (userAnswerContent != null) {
            length = userAnswerContent.length();
            if (totalQuestion != -1) {
                if (length < totalQuestion) {
                    mLogtf.d("submitVoice:totalQuestion=" + totalQuestion + ",length=" + length + ",type=" + detailInfo.getArtType());
                    for (int i = length; i < totalQuestion; i++) {
                        try {
                            JSONObject answer = new JSONObject();
                            JSONArray userAnswerContent2 = new JSONArray();
                            JSONObject userAnswerContent3 = new JSONObject();
                            userAnswerContent3.put("id", "0");
                            userAnswerContent3.put("text", "");
                            userAnswerContent2.put(userAnswerContent3);
                            answer.put("userAnswerContent", userAnswerContent2);
                            JSONArray rightAnswerContent2 = new JSONArray();
                            answer.put("rightAnswerContent", rightAnswerContent2);
                            JSONArray rightArray = new JSONArray();
                            rightArray.put(0);
                            answer.put("isRight", rightArray);
                            userAnswerContent.put(answer);
                        } catch (Exception e) {
                            MobAgent.httpResponseParserError(TAG, "submitVoice1", e.getMessage());
                            logger.d("submitVoice1", e);
                        }
                    }
                }
            }
            length = userAnswerContent.length();
            for (int j = 0; j < userAnswerContent.length(); j++) {
                JSONObject userAnswer = new JSONObject();
                try {
                    JSONObject answer = userAnswerContent.getJSONObject(j);
                    JSONArray userAnswerContent2 = answer.getJSONArray("userAnswerContent");
                    JSONArray rightAnswerContent2 = answer.getJSONArray("rightAnswerContent");
                    String useranswer = "";
                    for (int k = 0; k < userAnswerContent2.length(); k++) {
                        JSONObject userAnswerContent3 = userAnswerContent2.getJSONObject(k);
                        String id = userAnswerContent3.getString("id");
                        userAnswer.put("id", id);
                        if (k < (userAnswerContent2.length() - 1)) {
                            useranswer += userAnswerContent3.optString("text") + ",";
                        } else {
                            useranswer += userAnswerContent3.optString("text");
                        }
                    }
                    userAnswer.put("useranswer", useranswer);
                    String rightanswer = "";
                    for (int k = 0; k < rightAnswerContent2.length(); k++) {
                        JSONObject rightAnswerContent3 = rightAnswerContent2.getJSONObject(k);
                        if (k < (userAnswerContent2.length() - 1)) {
                            rightanswer += rightAnswerContent3.optString("text") + ",";
                        } else {
                            rightanswer += rightAnswerContent3.optString("text");
                        }
                    }
                    userAnswer.put("answer", rightanswer);
                    userAnswer.put("type", "" + answer.optString("type"));
                    userAnswer.put("rightnum", "" + answer.optString("rightnum"));
                    userAnswer.put("wrongnum", "" + answer.optString("wrongnum"));
                    userAnswer.put("answernums", "" + rightAnswerContent2.length());
                    if (LiveQueConfig.EN_COURSE_TYPE_GAME.equals(detailInfo.getArtType())) {
                        userAnswer.put("isright", 2);
                    } else {
                        int isRight = -1;
                        JSONArray rightArray = null;
                        if (answer.opt("isRight") instanceof JSONArray) {
                            rightArray = answer.getJSONArray("isRight");
                        } else if (answer.opt("isright") instanceof JSONArray) {
                            rightArray = answer.getJSONArray("isright");
                        }
                        if (rightArray != null) {
                            for (int i = 0; i < rightArray.length(); i++) {
                                int isRightInt = rightArray.optInt(i, 0);
                                if (isRightInt == 0) {
                                    if (-1 == isRight) {
                                        isRight = 0;
                                    } else if (2 == isRight) {
                                        isRight = 1;
                                    }
                                } else if (isRightInt == 1) {
                                    if (-1 == isRight) {
                                        isRight = 2;
                                    } else if (0 == isRight) {
                                        isRight = 1;
                                    }
                                }
                            }
                        } else {
                            isRight = 0;
                        }
                        userAnswer.put("isright", isRight);
                    }
                    userAnswer.put("times", "" + answer.optInt("times", -1));
                } catch (JSONException e) {
                    e.printStackTrace();
                    MobAgent.httpResponseParserError(TAG, "submitVoice2", e.getMessage());
                    logger.d("submitVoice2", e);
                }
                userAnswerArray.put(userAnswer);
            }
        }
        int testNum;
        if (length == 0) {
            testNum = totalQuestion;
        } else {
            testNum = length;
        }
        mLogtf.d("submitVoice:testNum=" + testNum + ",length=" + length);
        detailInfo.num = testNum;
        englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime, "" + userAnswerArray, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                JSONObject jsonObject = (JSONObject) objData[0];
                rlCourseControl.setVisibility(View.GONE);
                loadResult = true;
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("stat", 1);
                    jsonObject1.put("data", jsonObject);
                    ArtsAnswerResultEvent artsAnswerResultEvent = new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT);
                    artsAnswerResultEvent.setDetailInfo(detailInfo);
                    artsAnswerResultEvent.setIspreload(ispreload);
                    EventBus.getDefault().post(artsAnswerResultEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onSubmitSuccess(isforce);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("stat", 0);
                        jsonObject1.put("msg", failMsg);
                        EventBus.getDefault().post(new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                onSubmitError(isforce, failMsg);
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
            }
        });
    }

    /**
     * 普通英语题提交
     *
     * @param isforce
     * @param nonce
     */
    private void submitMultiTest(final int isforce, String nonce) {
        JSONArray answerArray = new JSONArray();
        for (int i = 0; i < tests.size(); i++) {
            NewCourseSec.Test test = tests.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("testId", "" + test.getId());
                JSONArray blank = new JSONArray();
                JSONArray choice = new JSONArray();
                JSONArray userAnswerContent = test.getUserAnswerContent();
                if (userAnswerContent != null) {
                    for (int j = 0; j < userAnswerContent.length(); j++) {
                        JSONObject answer = userAnswerContent.getJSONObject(j);
                        JSONArray userAnswerContent2 = answer.getJSONArray("userAnswerContent");
                        for (int k = 0; k < userAnswerContent2.length(); k++) {
                            String str = userAnswerContent2.getJSONObject(k).optString("text");
                            if (LiveQueConfig.EN_COURSE_TYPE_BLANK.equals(test.getTestType()) || LiveQueConfig.EN_COURSE_TYPE_18.equals(test.getTestType())) {
                                blank.put(str);
                            } else {
                                choice.put(str);
                            }
                        }
                    }
                }
                jsonObject.put("blank", blank);
                jsonObject.put("choice", choice);
                answerArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime, "" + answerArray, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                JSONObject jsonObject = (JSONObject) objData[0];
                rlCourseControl.setVisibility(View.GONE);
                loadResult = true;
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("stat", 1);
                    jsonObject1.put("data", jsonObject);
                    ArtsAnswerResultEvent artsAnswerResultEvent = new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT);
                    artsAnswerResultEvent.setDetailInfo(detailInfo);
                    artsAnswerResultEvent.setIspreload(ispreload);
                    EventBus.getDefault().post(artsAnswerResultEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onSubmitSuccess(isforce);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                isSumit = false;
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("stat", 0);
                        jsonObject1.put("msg", failMsg);
                        EventBus.getDefault().post(new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                onSubmitError(isforce, failMsg);
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
            }
        });
    }

    /**
     * 普通理科文科题提交
     *
     * @param isforce
     * @param nonce
     */
    private void submitSec(final int isforce, String nonce) {
        final JSONObject testInfos = new JSONObject();
        for (int i = 0; i < tests.size(); i++) {
            NewCourseSec.Test test = tests.get(i);
            JSONObject json = test.getJson();
            JSONArray userAnswerContent = test.getUserAnswerContent();
            try {
                int userAnswerStatus = 0;
                //用户没有作答,字段不能缺
                if (userAnswerContent == null || userAnswerContent.length() == 0) {
                    userAnswerContent = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    JSONArray array = new JSONArray();
                    //需要填上id 和 text
                    JSONObject emptyJson = new JSONObject();
                    emptyJson.put("id", "");
                    emptyJson.put("text", "");
                    array.put(emptyJson);
                    jsonObject.put("userAnswerContent", array);
                    jsonObject.put("rightnum", 0);
                    jsonObject.put("wrongnum", 0);
                    userAnswerContent.put(jsonObject);
                } else {
                    for (int j = 0; j < userAnswerContent.length(); j++) {
                        JSONObject jsonObject = userAnswerContent.getJSONObject(j);
                        if (jsonObject.get("userAnswerContent") instanceof JSONArray) {
                            JSONArray array = jsonObject.getJSONArray("userAnswerContent");
                            if (array.length() == 0) {
                                array = new JSONArray();
                                //需要填上id 和 text
                                JSONObject emptyJson = new JSONObject();
                                emptyJson.put("id", "");
                                emptyJson.put("text", "");
                                array.put(emptyJson);
                                jsonObject.put("userAnswerContent", array);
                            }
                        } else {
                            jsonObject.put("userAnswerContent", jsonObject.get("userAnswerContent"));
                        }
                        if (!jsonObject.has("rightnum")) {
                            jsonObject.put("rightnum", 0);
                        }
                        if (!jsonObject.has("wrongnum")) {
                            jsonObject.put("wrongnum", 0);
                        }
                    }
                }
                json.put("index", i);
                json.put("hasAnswer", 0);
                json.put("userAnswerStatus", userAnswerStatus);
                json.put("endTime", System.currentTimeMillis() / 1000);
                json.put("userAnswerContent", userAnswerContent);
                testInfos.put(test.getId(), json);
            } catch (JSONException e) {
                CrashReport.postCatchedException(e);
                mLogtf.e("submit", e);
            }
        }
        englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime, testInfos.toString(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                JSONObject jsonObject = (JSONObject) objData[0];
                showScienceAnswerResult(isforce);
                onSubmitSuccess(isforce);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                isSumit = false;
                onSubmitError(isforce, failMsg);
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
            }
        });
    }

    /**
     * 提交成功
     *
     * @param isforce
     */
    private void onSubmitSuccess(int isforce) {
        if (isArts == LiveVideoSAConfig.ART_EN) {
            NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), true, isforce == 2, ispreload, (System.currentTimeMillis() - subMitTime), "");
        } else {
            NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), true, isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), "");
        }
    }

    /**
     * 提交失败
     *
     * @param isforce
     * @param errorMsg
     */
    private void onSubmitError(int isforce, String errorMsg) {
        if (isArts == LiveVideoSAConfig.ART_EN) {
            NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), false, isforce == 2, ispreload, (System.currentTimeMillis() - subMitTime), errorMsg);
        } else {
            NewCourseLog.sno6(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), false, isforce == 1, ispreload, (System.currentTimeMillis() - subMitTime), errorMsg);
        }
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
                if (showControl) {
                    rlCourseControl.setVisibility(View.VISIBLE);
                }
                preLoad.onStop();
            }
        } else {
//            if (isFinish) {
//                //初中结果页是网页，需要调接口
//                if (isArts != LiveVideoSAConfig.ART_EN && (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage))) {
//                    wvSubjectWeb.loadUrl(jsClientSubmit);
//                }
//            }
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
                if (newCourseSec.getIsAnswer() == 1 && !isPlayBack) {
                    rlSubjectLoading.setVisibility(View.GONE);
                    preLoad.onStop();
                    showScienceAnswerResult(0);
                } else {
                    tests = newCourseSec.getTests();
                    if (tests.isEmpty()) {
                        XESToastUtils.showToast(mContext, "互动题为空");
                        return;
                    }
                    if (isArts == LiveVideoSAConfig.ART_EN) {
                        NewCourseSec.Test test = tests.get(0);
                        mLogtf.d("getCourseWareTests:oldtype=" + detailInfo.getArtType() + ",testType=" + test.getTestType());
                        if (StringUtils.isEmpty(detailInfo.getArtType()) || "0".equals(detailInfo.getArtType())) {
                            detailInfo.setArtType(test.getTestType());
                        }
                    } else if (isArts == LiveVideoSAConfig.ART_SEC) {
                        newCourseCache.add(newCourseCache.new FutureCourse());
                    }
                    showControl();
                    if (quesJson != null) {
                        for (int i = 0; i < tests.size(); i++) {
                            NewCourseSec.Test test = tests.get(i);
                            JSONArray userAnswerContent = quesJson.optJSONArray("" + i);
                            test.setUserAnswerContent(userAnswerContent);
                        }
                    }
                    setNum(1);
                    NewCourseSec.Test test = tests.get(0);
                    currentIndex = 0;
                    wvSubjectWeb.loadUrl(test.getPreviewPath());
                    int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
                    if (type != 0) {
                        ispreload = type == 1;
                    } else {
                        ispreload = true;
                    }
                    NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), test.getPreviewPath(), ispreload, test.getId());
                    //设置作答时间
                    if (isArts == LiveVideoSAConfig.ART_EN) {
                        setTimeEn(newCourseSec);
                    } else {
                        setTimeSec(newCourseSec);
                    }
                }
            }

            /**
             * 设置文理时间
             * @param newCourseSec
             */
            private void setTimeSec(NewCourseSec newCourseSec) {
                if ((LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage))) {
                    setTimeSecPrimary(newCourseSec);
                } else {
                    setTimeSecMiddle(newCourseSec);
                }
            }

            /**
             * 设置文理时间,小学倒计时
             * @param newCourseSec
             */
            private void setTimeSecPrimary(NewCourseSec newCourseSec) {
                //小学倒计时
                final long releaseTime;
                if (startQueTime != 0) {
                    releaseTime = newCourseSec.getEndTime() - newCourseSec.getReleaseTime() - (System.currentTimeMillis() - startQueTime) / 1000;
                    mLogtf.d("setTimeSecPrimary:time1=" + (newCourseSec.getEndTime() - newCourseSec.getReleaseTime()) + ",time2=" + ((System.currentTimeMillis() - startQueTime) / 1000));
                } else {
                    releaseTime = newCourseSec.getEndTime() - newCourseSec.getReleaseTime();
                    saveThisQuesStart(System.currentTimeMillis());
                }
                final long startTime = System.currentTimeMillis() / 1000;
                final AtomicInteger negative = new AtomicInteger(0);
                tvCourseTimeText.setText(getTimeNegativePrimary(releaseTime, startTime, negative));
                if (negative.get() == 1) {
                    negative.getAndIncrement();
                    tvCourseTimeText.setTextColor(Color.RED);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String timeStr = getTimeNegativePrimary(releaseTime, startTime, negative);
                        if (loadResult || mView.getParent() == null) {
                            return;
                        }
                        if (negative.get() == 1) {
                            negative.getAndIncrement();
                            tvCourseTimeText.setTextColor(Color.RED);
                        }
                        tvCourseTimeText.setText(timeStr);
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            }

            /**
             * 设置文理时间,初中正计时
             * @param newCourseSec
             */
            private void setTimeSecMiddle(NewCourseSec newCourseSec) {
                final long startTime;
                if (isPlayBack) {
                    startTime = System.currentTimeMillis() / 1000;
                    tvCourseTimeText.setText("0秒");
                } else {
                    startTime = newCourseSec.getReleaseTime();
                    tvCourseTimeText.setText(getTimePositive(startTime));
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvCourseTimeText.setText(getTimePositive(startTime));
                        if (loadResult || mView.getParent() == null) {
                            return;
                        }
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            }

            /**
             * 设置英语时间
             * @param newCourseSec
             */
            private void setTimeEn(NewCourseSec newCourseSec) {
                //英语倒计时
                final long releaseTime = newCourseSec.getReleaseTime() * 60;
                final long startTime = System.currentTimeMillis() / 1000;
                tvCourseTimeText.setText(getTimeNegativeEn(releaseTime, startTime));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String timeStr = getTimeNegativeEn(releaseTime, startTime);
                        if (loadResult || mView.getParent() == null || timeStr == null) {
                            return;
                        }
                        tvCourseTimeText.setText(timeStr);
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            }

            /**
             * 正计时
             * @param startTime
             * @return
             */
            private String getTimePositive(long startTime) {
                long time = System.currentTimeMillis() / 1000 - startTime;
                long second = time % 60;
                long minute = time / 60;
                return minute + "分" + second + "秒";
            }

            /**
             * 倒计时
             * @param startTime
             * @return
             */
            private String getTimeNegativePrimary(long releaseTime, long startTime, AtomicInteger negative) {
                long time = System.currentTimeMillis() / 1000 - startTime;
                long second = (releaseTime - time) % 60;
                long minute = (releaseTime - time) / 60;
                if (releaseTime - time < 0) {
                    second = (time - releaseTime) % 60;
                    minute = (time - releaseTime) / 60;
                    if (negative.get() == 0) {
                        negative.getAndIncrement();
                    }
                    return minute + "分" + second + "秒";
                }
                return minute + "分" + second + "秒";
            }

            /**
             * 倒计时
             * @param startTime
             * @return
             */
            private String getTimeNegativeEn(long releaseTime, long startTime) {
                long time = System.currentTimeMillis() / 1000 - startTime;
                long second = (releaseTime - time) % 60;
                long minute = (releaseTime - time) / 60;
                if (releaseTime - time < 0) {
                    return null;
                }
                return minute + "分" + second + "秒";
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

    /**
     * 设置中间数字
     *
     * @param index
     */
    private void setNum(int index) {
        if (isArts != LiveVideoSAConfig.ART_EN && (LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage))) {
            setNumPrimary(index);
        } else {
            setNumMiddle(index);
        }
    }

    /**
     * 设置中间数字,文理小学
     *
     * @param index
     */
    private void setNumPrimary(int index) {
        SpannableString sp = new SpannableString(index + " / " + tests.size());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(0xff66cbfa);
        sp.setSpan(foregroundColorSpan, ("" + index).length(), sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCourseNum.setText(sp);
    }

    /**
     * 设置中间数字,其他
     *
     * @param index
     */
    private void setNumMiddle(int index) {
        tvCourseNum.setText(index + " / " + tests.size());
    }

    /**
     * 设置下方控件的
     */
    private void showControl() {
        showControl = false;
        if (isArts == LiveVideoSAConfig.ART_EN) {
            for (int i = 0; i < tests.size(); i++) {
                NewCourseSec.Test test = tests.get(i);
                String testType = test.getTestType();
                if (LiveQueConfig.getShowControlTypes().contains(testType)) {
                    showControl = true;
                    break;
                }
            }
        } else {
            if (LiveQueConfig.SEC_COURSE_TYPE_QUE.equals(englishH5Entity.getPackageSource()) || LiveQueConfig.SEC_COURSE_TYPE_FUTURE.equals(englishH5Entity.getPackageSource())) {
                showControl = true;
            }
        }
        if (showControl) {
            if (tests.size() == 1) {
                btCoursePre.setVisibility(View.GONE);
                btCourseNext.setVisibility(View.GONE);
                btCourseSubmit.setVisibility(View.VISIBLE);
            }
        } else {
            rlCourseControl.setVisibility(View.GONE);
        }
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    @Override
    public boolean isResultRecived() {
        return loadResult;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    /** 课件日志 */
    private String getSubtestid() {
        if (tests.size() == 1) {
            return "0";
        }
        return "" + (currentIndex + 1);
    }

    /** 初中课件加载 */
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
                GifDrawable gifDrawable = new GifDrawable(mContext.getResources(), R.drawable.livevide_courseware_primary_load);
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

    /** 课件加载 */
    interface PreLoad {
        /** 课件开始加载 */
        void onStart();

        /** 课件加载中进度 */
        void onProgressChanged(WebView view, int newProgress);

        /** 课件结束加载 */
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                wvSubjectWeb.stopLoading();
                            }
                        });
                        XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                    }
                }
            } else if (WebInstertJs.indexStr().equals(url)) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    handler.post(new Runnable() {
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
     * 显示 理科互动题 结果页
     *
     * @param isforce
     */
    private void showScienceAnswerResult(final int isforce) {
        rlCourseControl.setVisibility(View.GONE);
        if (LiveVideoConfig.EDUCATION_STAGE_1.equals(educationstage)
                || LiveVideoConfig.EDUCATION_STAGE_2.equals(educationstage)) {
            //小学理科 走原生结果页
            englishH5CoursewareSecHttp.getStuTestResult(detailInfo, isPlayBack ? 1 : 0, new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    loadResult = true;
                    PrimaryScienceAnswerResultEntity entity = (PrimaryScienceAnswerResultEntity) objData[0];
                    mGoldNum = entity.getGold();
                    if (allowTeamPk) {
                        mEnergyNum = isforce ==0?entity.getEnergy():0;
                    }

                    // 对外暴露答题结果
                    broadCastAnswerRestult(entity);

                    PrimaryScienceAnserResultPager primaryScienceAnserResultPager = new PrimaryScienceAnserResultPager(mContext, entity, newCourseSec.getIsGame(), new PrimaryScienceAnserResultPager.OnNativeResultPagerClose() {
                        @Override
                        public void onClose() {
                            onClose.onH5ResultClose(CoursewareNativePager.this, getBaseVideoQuestionEntity());
                        }
                    });
                    ((RelativeLayout) mView).addView(primaryScienceAnserResultPager.getRootView(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    NewCourseLog.sno8(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), ispreload, 0);
                }
            });
        } else {
            String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
            loadResult = true;
            NewCourseLog.sno7(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), ispreload);
            wvSubjectWeb.addJavascriptInterface(new MiddleResult(mContext) {
                @Override
                public void onResultPageLoaded(String data) {
                    NewCourseLog.sno8(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), ispreload, (System.currentTimeMillis() - pagerStart));
                }
            }, "xesApp");
            wvSubjectWeb.loadUrl(url);
        }
    }

    /**
     *  对外广播 答题结果
     * @param entity
     */
    private void broadCastAnswerRestult(PrimaryScienceAnswerResultEntity entity) {
        try {
            if(detailInfo != null && detailInfo.englishH5Entity != null){
                JSONObject answerReuslt = new JSONObject();
                answerReuslt.put("isRight",entity.getType());
                answerReuslt.put("goldNum",mGoldNum);
                answerReuslt.put("energyNum",mEnergyNum);
                answerReuslt.put("id",detailInfo.englishH5Entity.getReleasedPageInfos());
                EventBus.getDefault().post(new AnswerResultEvent(answerReuslt.toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
