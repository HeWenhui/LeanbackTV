package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.dialog.CourseTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CoursewareNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    private boolean isFinish = false;
    private String liveId;
    EnglishH5Entity englishH5Entity;
    private boolean isPlayBack;
    EnglishH5CoursewareBll.OnH5ResultClose onClose;
    EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    String url;
    String id;
    String courseware_type;
    String nonce;
    String isShowRanks;
    long entranceTime;
    int isArts;
    boolean allowTeamPk;
    boolean isNewArtsCourseware;
    VideoQuestionLiveEntity detailInfo;
    String educationstage;
    private File cacheFile;
    private File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private HashMap header;
    RelativeLayout rl_livevideo_subject_loading;
    ProgressBar pg_livevideo_new_course_prog;
    RelativeLayout rl_livevideo_new_course_control;
    TextView tv_data_loading_tip;
    ImageView iv_livevideo_course_refresh;
    TextView tv_livevideo_new_course_num;
    ImageView ivLoading;
    Button iv_livevideo_new_course_pre;
    Button iv_livevideo_new_course_next;
    Button iv_livevideo_new_course_submit;
    NewCourseCache newCourseCache;
    boolean addJs = false;
    ArrayList<NewCourseSec.Test> tests = new ArrayList<>();
    private int currentIndex = 0;
    private int getAnswerType = 0;
    private boolean loadResult = false;
    CourseTipDialog courseTipDialog;

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
        tv_data_loading_tip = view.findViewById(R.id.tv_data_loading_tip);
        iv_livevideo_course_refresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        tv_livevideo_new_course_num = view.findViewById(R.id.tv_livevideo_new_course_num);
        rl_livevideo_subject_loading = view.findViewById(R.id.rl_livevideo_subject_loading);
        pg_livevideo_new_course_prog = view.findViewById(R.id.pg_livevideo_new_course_prog);
        rl_livevideo_new_course_control = view.findViewById(R.id.rl_livevideo_new_course_control);
        iv_livevideo_new_course_pre = view.findViewById(R.id.iv_livevideo_new_course_pre);
        iv_livevideo_new_course_next = view.findViewById(R.id.iv_livevideo_new_course_next);
        iv_livevideo_new_course_submit = view.findViewById(R.id.iv_livevideo_new_course_submit);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        cacheFile = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/parentsmeeting/webviewCache");
        if (cacheFile == null) {
            cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
        }
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        if (isNewArtsCourseware) {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "artschild");
        } else {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
        }
        mPublicCacheout = new File(cacheFile, EnglishH5Cache.mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        newCourseCache = new NewCourseCache(mContext);
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {

            @Override
            public void postMessage(final JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    if ("close".equals(type)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else if ("submitAnswer".equals(type)) {
//                        submit(message);
                    } else if ("answer".equals(type)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                NewCourseSec.Test oldTest = tests.get(currentIndex);
                                try {
                                    oldTest.setUserAnswerContent(message.getJSONArray("data"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                logger.d("postMessage:answer:getAnswerType=" + getAnswerType + ",index=" + currentIndex);
                                if (getAnswerType == 3 || getAnswerType == 4) {
                                    if (getAnswerType == 3) {
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
                                    if (getAnswerType == 1) {
                                        currentIndex--;
                                        iv_livevideo_new_course_submit.setVisibility(View.GONE);
                                    } else if (getAnswerType == 2) {
                                        currentIndex++;
                                    }
                                    if (currentIndex == 0) {
                                        iv_livevideo_new_course_pre.setEnabled(false);
                                        if (tests.size() > 0) {
                                            iv_livevideo_new_course_next.setEnabled(true);
                                        }
                                    } else if (currentIndex == tests.size() - 1) {
                                        iv_livevideo_new_course_next.setEnabled(false);
                                        iv_livevideo_new_course_next.setVisibility(View.INVISIBLE);
                                        iv_livevideo_new_course_submit.setVisibility(View.VISIBLE);
                                        if (tests.size() > 0) {
                                            iv_livevideo_new_course_pre.setEnabled(true);
                                        }
                                    } else {
                                        iv_livevideo_new_course_pre.setEnabled(true);
                                        iv_livevideo_new_course_next.setVisibility(View.VISIBLE);
                                        iv_livevideo_new_course_next.setEnabled(true);
                                    }
                                    if (currentIndex >= 0 && currentIndex < tests.size()) {
                                        tv_livevideo_new_course_num.setText((currentIndex + 1) + " / " + tests.size());
                                        NewCourseSec.Test test = tests.get(currentIndex);
                                        addJs = false;
                                        wvSubjectWeb.loadUrl(test.getPreviewPath());
                                    }
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), "xesApp");
        iv_livevideo_new_course_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", "getAnswer");
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAnswerType = 1;
                iv_livevideo_new_course_pre.setEnabled(false);
                iv_livevideo_new_course_next.setEnabled(false);
            }
        });
        iv_livevideo_new_course_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", "getAnswer");
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAnswerType = 2;
                iv_livevideo_new_course_pre.setEnabled(false);
                iv_livevideo_new_course_next.setEnabled(false);
            }
        });
        iv_livevideo_new_course_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("type", "getAnswer");
                    JSONObject resultData = new JSONObject();
                    jsonData.put("data", resultData);
                    StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAnswerType = 3;
            }
        });
        iv_livevideo_course_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_livevideo_course_refresh.setVisibility(View.GONE);
                getCourseWareTests();
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
        return null;
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
    public void submitData() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", "getAnswer");
            JSONObject resultData = new JSONObject();
            jsonData.put("data", resultData);
            StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getAnswerType = 4;
        if (courseTipDialog != null) {
            courseTipDialog.cancelDialog();
        }
        courseTipDialog = null;
    }

    private void submit(final int isforce, String nonce) {
        JSONObject testInfos = new JSONObject();
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
                e.printStackTrace();
            }
        }
        englishH5CoursewareSecHttp.submitCourseWareTests(detailInfo, isforce, nonce, entranceTime, testInfos.toString(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                JSONObject jsonObject = (JSONObject) objData[0];
                int toAnswered = jsonObject.optInt("toAnswered");
                rl_livevideo_new_course_control.setVisibility(View.GONE);
                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
                loadResult = true;
                wvSubjectWeb.loadUrl(url);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
//                String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, isforce, "");
//                wvSubjectWeb.loadUrl(url);
            }
        });
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (!loadResult) {
            pg_livevideo_new_course_prog.setProgress(newProgress);
            tv_data_loading_tip.setText("加载中 " + newProgress + "%");
            if (newProgress == 100) {
                rl_livevideo_subject_loading.setVisibility(View.GONE);
                rl_livevideo_new_course_control.setVisibility(View.VISIBLE);
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
                if (newCourseSec.getIsAnswer() == 1) {
                    rl_livevideo_subject_loading.setVisibility(View.GONE);
                    String url = englishH5CoursewareSecHttp.getResultUrl(detailInfo, 0, "");
                    loadResult = true;
                    wvSubjectWeb.loadUrl(url);
                } else {
                    tests = newCourseSec.getTests();
                    if (tests.isEmpty()) {
                        XESToastUtils.showToast(mContext, "互动题为空");
                        return;
                    }
                    tv_livevideo_new_course_num.setText("1 / " + tests.size());
                    NewCourseSec.Test test = tests.get(0);
                    currentIndex = 0;
                    wvSubjectWeb.loadUrl(test.getPreviewPath());
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
                iv_livevideo_course_refresh.setVisibility(View.VISIBLE);
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
                    }
                }
            } else if (WebInstertJs.indexStr().equals(url)) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
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
