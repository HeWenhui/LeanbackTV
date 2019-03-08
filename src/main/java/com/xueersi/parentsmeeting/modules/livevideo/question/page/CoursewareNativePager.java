package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.dialog.CourseTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linyuqiang on 2019/3/5.
 * 新课件，去掉h5壳
 */
public class CoursewareNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager, BaseQuestionWebInter {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    /** 理科初高中新课件平台 强制提交js */
    private String jsClientSubmit = "javascript:__CLIENT_SUBMIT__()";
    private boolean isFinish = false;
    private String liveId;
    private EnglishH5Entity englishH5Entity;
    private boolean isPlayBack;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String url;
    private String id;
    private String courseware_type;
    private String nonce;
    private String isShowRanks;
    private long entranceTime;
    private int isArts;
    private boolean allowTeamPk;
    private boolean isNewArtsCourseware;
    private VideoQuestionLiveEntity detailInfo;
    private String educationstage;
    private RelativeLayout rlSubjectLoading;
    private ProgressBar pgCourseProg;
    private RelativeLayout rlCourseControl;
    private TextView tvCourseTimeText;
    private TextView tvDataLoadingTip;
    private ImageView ivCourseRefresh;
    private ImageView iv_livevideo_subject_refresh;
    private TextView tvCourseNum;
    private ImageView ivLoading;
    private Button btCoursePre;
    private Button btCourseNext;
    private Button btCourseSubmit;
    private NewCourseCache newCourseCache;
    /** 显示下方控制布局 */
    boolean showControl = false;
    /** 在网页中嵌入js，只嵌入一次 */
    private boolean addJs = false;
    private ArrayList<NewCourseSec.Test> tests = new ArrayList<>();
    private int currentIndex = 0;
    private int getAnswerType = 0;
    /** 加载结果页 */
    private boolean loadResult = false;
    private CourseTipDialog courseTipDialog;
    private String today;
    private JSONObject quesJson;

    public CoursewareNativePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity,
                                 final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose,
                                 String isShowRanks, int isArts, boolean allowTeamPk) {
        super(context);
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
        if (isArts == 0) {
            this.educationstage = detailInfo.getEducationstage();
        }
//        initWebView();
//        setErrorTip("H5课件加载失败，请重试");
//        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
        entranceTime = System.currentTimeMillis() / 1000;
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_native, null);
        ivLoading = (ImageView) view.findViewById(R.id.iv_data_loading_show);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        tvDataLoadingTip = view.findViewById(R.id.tv_data_loading_tip);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        iv_livevideo_subject_refresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        tvCourseNum = view.findViewById(R.id.tv_livevideo_new_course_num);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        pgCourseProg = view.findViewById(R.id.pg_livevideo_new_course_prog);
        rlCourseControl = view.findViewById(R.id.rl_livevideo_new_course_control);
        tvCourseTimeText = view.findViewById(R.id.tv_livevideo_new_course_time_text);
        btCoursePre = view.findViewById(R.id.bt_livevideo_new_course_pre);
        btCourseNext = view.findViewById(R.id.bt_livevideo_new_course_next);
        btCourseSubmit = view.findViewById(R.id.bt_livevideo_new_course_submit);
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
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());
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
        iv_livevideo_subject_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJs = false;
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
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (needTip) {
                            courseTipDialog = new CourseTipDialog(mContext, (Application) mContext.getApplicationContext());
                            courseTipDialog.setOnClick(new CourseTipDialog.OnClick() {

                                @Override
                                public void onCancle(View view) {
                                    if (courseTipDialog != null) {
                                        courseTipDialog.cancelDialog();
                                    }
                                    courseTipDialog = null;
                                }

                                @Override
                                public void onCommit(View view) {
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
                        if (isArts == LiveVideoSAConfig.ART_EN) {
                            if (LiveQueConfig.EN_COURSE_TYPE_VOICE_BLANK.equals(detailInfo.voiceType) || LiveQueConfig.EN_COURSE_TYPE_VOICE_CHOICE.equals(detailInfo.voiceType)) {
                                submitVoice(1, "");
                            }
                        }
                        setViewEnable("onAnswer1");
                    } else {
                        if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_PRE) {
                            currentIndex--;
                            btCourseSubmit.setVisibility(View.GONE);
                        } else if (getAnswerType == LiveQueConfig.GET_ANSWERTYPE_NEXT) {
                            currentIndex++;
                        }
                        setViewEnable("onAnswer2");
                        if (currentIndex >= 0 && currentIndex < tests.size()) {
                            tvCourseNum.setText((currentIndex + 1) + " / " + tests.size());
                            NewCourseSec.Test test = tests.get(currentIndex);
                            addJs = false;
                            wvSubjectWeb.loadUrl(test.getPreviewPath());
                        }
                    }
                }
            }
        });
    }

    private void onLoadComplete(final String where, final JSONObject message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if ("postMessage".equals(where)) {
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
                }
                setViewEnable("onLoadComplete");
            }
        });
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
        if (loadResult) {
            wvSubjectWeb.loadUrl(jsClientSubmit);
        } else {
            getAnswerType = LiveQueConfig.GET_ANSWERTYPE_FORCE_SUBMIT;
            if (courseTipDialog != null) {
                courseTipDialog.cancelDialog();
            }
            courseTipDialog = null;
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
        }
    }

    private void submitVoice(final int isforce, String nonce) {
        NewCourseSec.Test test = tests.get(0);
        JSONArray userAnswerContent = test.getUserAnswerContent();
        JSONArray userAnswerArray = new JSONArray();
        int testNum = 0;
        if (userAnswerContent != null) {
            testNum = userAnswerContent.length();
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
                        useranswer += userAnswerContent3.optString("text") + ",";
                    }
                    userAnswer.put("useranswer", useranswer);
                    String rightanswer = "";
                    for (int k = 0; k < rightAnswerContent2.length(); k++) {
                        JSONObject rightAnswerContent3 = rightAnswerContent2.getJSONObject(k);
                        rightanswer += rightAnswerContent3.optString("text") + ",";
                    }
                    userAnswer.put("answer", rightanswer);
                    userAnswer.put("type", "" + answer.optString("type"));
                    userAnswer.put("rightnum", "" + answer.optString("rightnum"));
                    userAnswer.put("wrongnum", "" + answer.optString("wrongnum"));
                    userAnswer.put("answernums", "" + rightAnswerContent2.length());
                    userAnswer.put("isright", "" + answer.getJSONArray("isRight").optString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                    logger.d("submitVoice", e);
                }
                userAnswerArray.put(userAnswer);
            }
        }
        logger.d("submitVoice:testNum=" + testNum);
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
                    EventBus.getDefault().post(new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
            }
        });
    }

    private void submit(final int isforce, String nonce) {
        if (loadResult) {
            wvSubjectWeb.loadUrl(jsClientSubmit);
        } else {
            if (isArts == LiveVideoSAConfig.ART_EN) {
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
                                    if (LiveQueConfig.EN_COURSE_TYPE_BLANK.equals(test.getTestType())) {
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
                            EventBus.getDefault().post(new ArtsAnswerResultEvent(jsonObject1 + "", ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
                    }
                });
            } else {
                final JSONObject testInfos = new JSONObject();
                for (int i = 0; i < tests.size(); i++) {
                    NewCourseSec.Test test = tests.get(i);
                    JSONObject json = test.getJson();
                    JSONArray userAnswerContent = test.getUserAnswerContent();
                    try {
                        if (userAnswerContent == null) {
                            userAnswerContent = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            JSONArray array = new JSONArray();
                            jsonObject.put("userAnswerContent", array);
                        }
                        json.put("index", i);
                        json.put("hasAnswer", 0);
                        json.put("userAnswerStatus", userAnswerContent.length() > 0 ? 1 : 0);
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
                        int toAnswered = jsonObject.optInt("toAnswered");
                        if(LiveVideoConfig.isPrimary){
                            englishH5CoursewareSecHttp.getStuTestResult(detailInfo, isforce, testInfos.toString(), new AbstractBusinessDataCallBack() {
                                @Override
                                public void onDataSucess(Object... objData) {
                                    PrimaryScienceAnswerResultEntity entity = (PrimaryScienceAnswerResultEntity) objData[0];
                                    PrimaryScienceAnserResultPager primaryScienceAnserResultPager = new PrimaryScienceAnserResultPager(mContext, entity);
                                    ((RelativeLayout)mView).addView(primaryScienceAnserResultPager.getRootView(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                }
                            });
                        }
                        else{
                            rlCourseControl.setVisibility(View.GONE);
                            String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
                            loadResult = true;
                            wvSubjectWeb.loadUrl(url);
                        }
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
                    }
                });
            }
        }
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (!loadResult) {
            pgCourseProg.setProgress(newProgress);
            tvDataLoadingTip.setText("加载中 " + newProgress + "%");
            if (newProgress == 100) {
                rlSubjectLoading.setVisibility(View.GONE);
                if (showControl) {
                    rlCourseControl.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        try {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.animlst_app_loading);
            ivLoading.setBackground(drawable);
            ((AnimationDrawable) drawable).start();
        } catch (Exception e) {
            if (mLogtf != null) {
                mLogtf.e("initData", e);
            }
        }
        getCourseWareTests();
    }

    private void getCourseWareTests() {
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                NewCourseSec newCourseSec = (NewCourseSec) objData[0];
                logger.d("onDataSucess:newCourseSec=" + newCourseSec);
                if (newCourseSec.getIsAnswer() == 1 && !isPlayBack) {
                    rlSubjectLoading.setVisibility(View.GONE);
                    String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, 0, "");
                    loadResult = true;
                    wvSubjectWeb.loadUrl(url);
                } else {
                    tests = newCourseSec.getTests();
                    if (tests.isEmpty()) {
                        XESToastUtils.showToast(mContext, "互动题为空");
                        return;
                    }
                    showControl();
                    if (quesJson != null) {
                        for (int i = 0; i < tests.size(); i++) {
                            NewCourseSec.Test test = tests.get(i);
                            JSONArray userAnswerContent = quesJson.optJSONArray("" + i);
                            test.setUserAnswerContent(userAnswerContent);
                        }
                    }
                    tvCourseNum.setText("1 / " + tests.size());
                    NewCourseSec.Test test = tests.get(0);
                    currentIndex = 0;
                    wvSubjectWeb.loadUrl(test.getPreviewPath());
                    final long startTime;
                    if (isPlayBack) {
                        startTime = System.currentTimeMillis() / 1000;
                        tvCourseTimeText.setText("0秒");
                    } else {
                        startTime = newCourseSec.getReleaseTime();
                        tvCourseTimeText.setText(getTime(startTime));
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvCourseTimeText.setText(getTime(startTime));
                            if (loadResult || mView.getParent() == null) {
                                return;
                            }
                            handler.postDelayed(this, 1000);
                        }
                    }, 1000);
                }
            }

            private String getTime(long startTime) {
                long time = System.currentTimeMillis() / 1000 - startTime;
                long second = time % 60;
                long minute = time / 60;
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

    private void showControl() {
        showControl = false;
        if (isArts == LiveVideoSAConfig.ART_EN) {
            for (int i = 0; i < tests.size(); i++) {
                NewCourseSec.Test test = tests.get(i);
                String testType = test.getTestType();
                if (LiveQueConfig.EN_COURSE_TYPE_BLANK.equals(testType) || LiveQueConfig.EN_COURSE_TYPE_CHOICE.equals(testType)
                        || LiveQueConfig.EN_COURSE_TYPE_OUT.equals(testType) || LiveQueConfig.EN_COURSE_TYPE_19.equals(testType)) {
                    showControl = true;
                    break;
                }
            }
        } else {
            if (LiveQueConfig.SEC_COURSE_TYPE_QUE.equals(englishH5Entity.getPackageSource())) {
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
    public void setWebBackgroundColor(int color) {

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

    class CourseWebViewClient extends MyWebViewClient {

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
                        view.stopLoading();
                        XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                    }
                }
            } else if (WebInstertJs.indexStr().equals(url)) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    view.stopLoading();
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
    }
}
