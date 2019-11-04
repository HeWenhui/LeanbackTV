package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.logerhelper.UmsAgentUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEvent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionOnSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.TeacherClose;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;

/**
 * @author linyuqiang
 * @date 2017/8/23
 * 普通互动题，h5显示页面
 */
public class QuestionWebX5Pager extends LiveBasePager implements BaseQuestionWebInter {
    private String questionEventId = LiveVideoConfig.LIVE_H5_TEST;
    private Button btSubjectClose;
    private Button btSubjectCalljs;
    /** 刷新次数，防止预加载文件有问题 */
    private int refreshTimes = 0;
    private WebView wvSubjectWeb;
    private View errorView;
    private TeacherClose teacherClose;
    private QuestionOnSubmit onSubmit;
    /** 用户名称 */
    private String stuName;
    /** 用户Id */
    private String stuId;
    private String liveid;
    private String testId;
    private String nonce;
    /** 试卷地址 */
    private String examUrl = "";
    /** 是不是考试结束 */
    private boolean isEnd = false;
    private String testPaperUrl;
    private String jsExamSubmitAll = "javascript:examSubmitAll()";
    private String isShowRanks;
    private final int isArts;
    private String stuCouId;
    private int isTeamPkRoom; //是否是 teampk 房间
    private int mGoldNum;
    private int mEngerNum;
    private boolean allowTeamPk;
    //    private boolean isLive = true;
    private File mMorecacheout;
    private File cacheFile;
    private String type;
    /** 是否接收到或者展示过答题结果页面 **/
    private boolean isAnswerResultRecived;
    /** 是否是中学连对激励系统 */
    private int isOpenNewCourseWare = 0;
    /**
     * 是否是强制提交
     */
    private boolean isForceSubmit;

    /**
     * 文科新课件平台 试题
     **/
    private boolean isNewArtsTest;
    private HashMap header;
    /** 新课件缓存 */
    private NewCourseCache newCourseCache;

    public QuestionWebX5Pager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, String testPaperUrl,
                              String stuId, String stuName, String liveid, String testId,
                              String nonce, String isShowRanks, int isArts, String stuCouId, boolean allowTeamPk) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
//        if (baseVideoQuestionEntity != null) {
//            isLive = baseVideoQuestionEntity.isLive();
//        }
        this.isArts = isArts;
        this.stuId = stuId;
        this.stuName = stuName;
        this.liveid = liveid;
        this.testId = testId;
//        String[] ss = testId.split("-");
//        if (ss.length > 1) {
//            if ("0".equals(ss[1])) {
//                testPaperUrl = LiveVideoConfig.URL_LIVE_TEA_UPLOAD_TEST;
//                jsExamSubmitAll = "javascript:__CLIENT_SUBMIT__()";
//            }
//        }
        this.testPaperUrl = testPaperUrl;
        this.nonce = nonce;
        this.isShowRanks = isShowRanks;
        this.stuCouId = stuCouId;
        this.allowTeamPk = allowTeamPk;
        mLogtf.i("QuestionWebX5Pager:liveid=" + liveid + ",testId=" + testId);
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
        initData();
    }

    public void setQuestionOnSubmit(QuestionOnSubmit questionOnSubmit) {
        this.onSubmit = questionOnSubmit;
    }

    public void setTeacherClose(TeacherClose teacherClose) {
        this.teacherClose = teacherClose;
    }

    /**
     * 重载构造方法 支持 文科新课件平台 H5 题
     *
     * @param context
     * @param testInfo 试题信息
     */
    public QuestionWebX5Pager(Context context, VideoQuestionLiveEntity testInfo, String liveid) {
        super(context);
        isArts = LiveVideoSAConfig.ART_EN;
        examUrl = testInfo.getUrl();
        isNewArtsTest = testInfo.isNewArtsH5Courseware();
        testId = testInfo.getvQuestionID();
        type = testInfo.type;
        this.liveid = liveid;
        mLogtf.i("QuestionWebX5Pager:liveid=" + liveid + ",testId=" + testId);
        cacheFile = LiveCacheFile.geCacheFile(context, "webviewCache");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveid);
        mMorecacheout = new File(todayLiveCacheDir, liveid + "artschild");
        initData();
    }


    @Override
    public String getTestId() {
        return testId;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_subject_question_x5, null);
        btSubjectClose = (Button) view.findViewById(R.id.bt_livevideo_subject_close);
        btSubjectCalljs = (Button) view.findViewById(R.id.bt_livevideo_subject_calljs);
        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                view.findViewById(R.id.rl_livevideo_subject_error).setVisibility(View.GONE);
//                wvSubjectWeb.setVisibility(View.VISIBLE);
                if (refreshTimes % 2 == 0) {
                    if (wvSubjectWeb instanceof CacheWebView) {
                        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
                        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
                        logger.d("refreshonClick:NORMAL");
                    }
                } else {
                    if (wvSubjectWeb instanceof CacheWebView) {
                        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
                        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NO_CACHE);
                        if (refreshTimes % 3 == 0) {
                            cacheWebView.clearCache();
                        }
                        logger.d("refreshonClick:NO_CACHE");
                    }
                }
                wvSubjectWeb.reload();
                errorView.setVisibility(View.GONE);
                mView.findViewById(R.id.rl_livevideo_subject_loading).setVisibility(View.VISIBLE);
                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                ((AnimationDrawable) ivLoading.getBackground()).start();
            }
        });
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                onDestroy();
            }
        });
        view.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wvSubjectWeb.reload();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        newCourseCache = new NewCourseCache(mContext, liveid, "99999");
        btSubjectClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isOpenNewCourseWare == 1) {
//                    //中学连对激励
//                    EventBus.getDefault().post(new EvenDriveEvent(EvenDriveEvent.CLOSE_H5));
//                }
                ViewGroup group = (ViewGroup) mView.getParent();
                group.removeView(mView);
                onPagerClose.onClose(QuestionWebX5Pager.this);
            }
        });
        btSubjectCalljs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
        logger.e("=======> isNewArtsTest:" + isNewArtsTest);

        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        wvSubjectWeb.addJavascriptInterface(this, "wx_xesapp");

        // 文科新课件平台 填空选择题
        if (isNewArtsTest) {
            logger.e("=======> loadUrl:" + examUrl);
            wvSubjectWeb.loadUrl(examUrl);
        } else {
            ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
            ((AnimationDrawable) ivLoading.getBackground()).start();
            examUrl = testPaperUrl + "?liveId=" + liveid + "&testId=" + testId
                    + "&stuId=" + stuId + "&stuName=" + stuName + "&isTowall=" + isShowRanks;
            if (!StringUtils.isEmpty(nonce)) {
                examUrl += "&nonce=" + nonce;
            }
            examUrl += "&stuCouId=" + stuCouId;
            examUrl += "&isArts=" + (isArts);
//            examUrl += "&isPlayBack=" + (isLive ? "0" : "1");
            examUrl += "&isShowTeamPk=" + (allowTeamPk ? "1" : "0");
            wvSubjectWeb.loadUrl(examUrl);
            logger.e("======> loadUrl:" + examUrl);
        }
        mGoldNum = -1;
        mEngerNum = -1;

        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEngerNum, LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
                if (teacherClose != null) {
                    logger.e("=======> postEvent closeByTeacher:" + teacherClose.isWebViewCloseByTeacher());
                    event.setCloseByTeahcer(teacherClose.isWebViewCloseByTeacher());
                    teacherClose.setWebViewCloseByTeacher(false);
                }
                event.setForceSubmit(isForceSubmit);
                EventBus.getDefault().post(event);
                mGoldNum = -1;
                mEngerNum = -1;
                // TODO 影响不确定，暂时注释isNewEnglishH5_isNewEnglishH5
//                if (isNewArtsTest) {
//                    LiveVideoConfig.isNewEnglishH5 = true;
//                } else {
//                    LiveVideoConfig.isNewEnglishH5 = false;
//                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wvSubjectWeb != null) {
            wvSubjectWeb.destroy();
        }
    }

    @Override
    public boolean isResultRecived() {
        return isAnswerResultRecived;
    }


    /**
     * 文科 课件 答题结果回调
     */
    @JavascriptInterface
    public void showAnswerResult_LiveVideo(String data) {
        logger.e("=========>showAnswerResult_LiveVideo:" + data);
        isAnswerResultRecived = true;
        EventBus.getDefault().post(new ArtsAnswerResultEvent(data, ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
    }


    /**
     * 理科 课件 答题结果回调
     */
    @JavascriptInterface
    public void onAnswerResult_LiveVideo(String data) {
        isAnswerResultRecived = true;
        EventBus.getDefault().post(new AnswerResultEvent(data));
    }

    @android.webkit.JavascriptInterface
    public void addJavascriptInterface() {
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (wvSubjectWeb instanceof CacheWebView) {
            CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
            // TODO: 2018/12/5  
            if (isArts == 0) {
                cacheWebView.getWebViewCache().setNeedHttpDns(!AppConfig.DEBUG);
            } else {
                cacheWebView.getWebViewCache().setNeedHttpDns(false);
            }
            cacheWebView.getWebViewCache().setIsScience(isArts == 0);
        }
//        int scale = DeviceUtils.getScreenWidth(mContext) * 100 / 878;
//        wvSubjectWeb.setInitialScale(scale);
//        // 设置可以支持缩放
//        webSetting.setSupportZoom(true);
//        // 设置出现缩放工具
//        webSetting.setBuiltInZoomControls(true);
//        webSetting.setDisplayZoomControls(false);
    }

    @Override
    public void submitData() {
        isForceSubmit = !isAnswerResultRecived;
        Map<String, String> mData = new HashMap<>();
        mData.put("testid", "" + testId);
        mData.put("logtype", "interactTestEnd");
        umsAgentDebugSys(questionEventId, mData);
//        wvSubjectWeb.loadUrl(String.format("javascript:examSubmitAll(" + code + ")"));
        isEnd = true;
        wvSubjectWeb.loadUrl(jsExamSubmitAll);
        Log.e("QuestionX5Pager", "=======>examSubmitAll called:");
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    public class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext,
// mBaseApplication, false, MESSAGE_VERIFY_TYPE);
//            verifyCancelAlertDialog.initInfo(message);
//            verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    wvSubjectWeb.reload();
//                }
//            });
//            verifyCancelAlertDialog.showDialog();
//            result.confirm();
//            return true;
//        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            ConsoleMessage.MessageLevel mLevel = consoleMessage.messageLevel();
            boolean isRequst = false;
            if (mLevel == ConsoleMessage.MessageLevel.ERROR || mLevel == ConsoleMessage.MessageLevel.WARNING) {
                isRequst = true;
            }
            UmsAgentUtil.webConsoleMessage(mContext, TAG, wvSubjectWeb.getUrl(), consoleMessage, isRequst);
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                if (loadView != null) {
                    ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                    ((AnimationDrawable) ivLoading.getBackground()).stop();
                    /*ViewGroup group = (ViewGroup) loadView.getParent();
                    group.removeView(loadView);*/
                    loadView.setVisibility(View.GONE);
                }
            }
        }
    }

    public class MyWebViewClient extends ErrorWebViewClient {
        String failingUrl;

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
            if ("100000000".equals(type)) {
                File file;
                int index = s.indexOf("courseware_pages");
                if (index != -1) {
                    String url2 = s.substring(index + "courseware_pages".length());
                    int index2 = url2.indexOf("?");
                    if (index2 != -1) {
                        url2 = url2.substring(0, index2);
                    }
                    file = new File(mMorecacheout, url2);
                    logger.e("shouldInterceptRequest:file=" + file + ",file=" + file.exists());
                } else {
                    file = new File(mMorecacheout, MD5Utils.getMD5(s));
                    index = s.lastIndexOf("/");
                    String name = s;
                    if (index != -1) {
                        name = s.substring(index);
                    }
                    logger.e("shouldInterceptRequest:file2=" + file.getName() + ",name=" + name + ",file=" + file.exists());
                }
                if (file.exists()) {
                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file);
                        String extension = MimeTypeMap.getFileExtensionFromUrl(s.toLowerCase());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
                        webResourceResponse.setResponseHeaders(header);
                        logger.e("读取本地资源了old");
                        return webResourceResponse;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return super.shouldInterceptRequest(view, s);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            logger.e("shouldInterceptRequestnew:totalurl=" + request.getUrl().toString());
            if (isNewArtsTest) {
                File file;
                int index = request.getUrl().toString().indexOf("courseware_pages");
                if (index != -1) {
                    String url2 = request.getUrl().toString().substring(index + "courseware_pages".length());
                    int index2 = url2.indexOf("?");
                    if (index2 != -1) {
                        url2 = url2.substring(0, index2);
                    }
                    file = new File(mMorecacheout, url2);
                    logger.e("shouldInterceptRequestnew:fileone=" + file + ",fileone=" + file.exists());
                    logger.e("shouldInterceptRequestnew:realurl=" + request.getUrl().toString());
                } else {
                    file = new File(mMorecacheout, MD5Utils.getMD5(request.getUrl().toString()));
                    index = request.getUrl().toString().lastIndexOf("/");
                    String name = request.getUrl().toString();
                    if (index != -1) {
                        name = request.getUrl().toString().substring(index);
                    }
                    logger.e("shouldInterceptRequestnew:filetwo=" + file.getName() + ",name=" + name + ",filetwo=" + file.exists());
                    logger.e("shouldInterceptRequestnew:ttfurl=" + request.getUrl().toString());
                }
                if (file.exists()) {
                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file);
                        String extension = MimeTypeMap.getFileExtensionFromUrl(request.getUrl().toString().toLowerCase());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
                        HashMap map = new HashMap();
                        map.put("Access-Control-Allow-Origin", "*");
                        webResourceResponse.setResponseHeaders(map);
                        logger.e("读取本地资源了new" + webResourceResponse.getResponseHeaders());
                        return webResourceResponse;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                WebResourceResponse webResourceResponse = newCourseCache.interceptZhongXueKeJian("" + request.getUrl());
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            }
            logger.e("没有本地资源就去网络请求咯咯咯new");
            logger.e("shouldInterceptRequestnew:lasturl=" + request.getUrl().toString());
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mLogtf.i("onPageFinished:url=" + url + ",failingUrl=" + failingUrl + ",isEnd=" + isEnd);
            if (!isNewArtsTest) {
                if (isEnd && url.equals(examUrl)) {
                    wvSubjectWeb.loadUrl(jsExamSubmitAll);
                    mLogtf.i("onPageFinished:examSubmitAll");
                }
            }
            if (failingUrl == null) {
                wvSubjectWeb.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }
            Map<String, String> mData = new HashMap<>();
            mData.put("testid", "" + testId);
            mData.put("logtype", "interactTestDidLoad");
            mData.put("status", "success");
            mData.put("loadurl", url);
            umsAgentDebugSys(questionEventId, mData);
//            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            this.failingUrl = null;
//            if (!url.equals(examUrl)) {
//                mLogtf.i("onPageStarted:setInitialScale");
//                int scale = ScreenUtils.getScreenWidth() * 100 / 878;
//                wvSubjectWeb.setInitialScale(scale);
//            }
            super.onPageStarted(view, url, favicon);
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
//            return super.shouldInterceptRequest(webView, s);
//        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            this.failingUrl = failingUrl;
            UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
                    "&&," + description);
            mLogtf.i("onReceivedError:failingUrl=" + failingUrl + ",errorCode=" + errorCode);
//            super.onReceivedError(view, errorCode, description, failingUrl);
            wvSubjectWeb.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
            Map<String, String> mData = new HashMap<>();
            mData.put("testid", "" + testId);
            mData.put("logtype", "interactTestDidLoad");
            mData.put("status", "fail");
            mData.put("loadurl", failingUrl);
            mData.put("msg", description);
            umsAgentDebugSys(questionEventId, mData);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mLogtf.i("shouldOverrideUrlLoading:url=" + url);

            logger.e("======> shouldOverrideUrlLoading:" + url);

            if (url.contains("science/Live/getMultiTestResult")) {
                if (onSubmit != null) {
                    onSubmit.onSubmit(XESCODE.STOPQUESTION, url.contains("submitType=force"));
                }
                return false;
            }

            if (url.contains(TeamPkBll.TEAMPK_URL_FIFTE)) {
                try {
                    int startIndex = url.indexOf("goldNum=") + "goldNum=".length();
                    if (startIndex != -1) {
                        String teamStr = url.substring(startIndex, url.length());
                        int endIndex = teamStr.indexOf("&");
                        String goldNUmStr = teamStr.substring(0, endIndex);
                        if (!TextUtils.isEmpty(goldNUmStr)) {
                            mGoldNum = Integer.parseInt(goldNUmStr.trim());
                        }
                    }
                    int satrIndex2 = url.indexOf("energyNum=") + "energyNum=".length();
                    if (satrIndex2 != -1) {
                        String tempStr2 = url.substring(satrIndex2);
                        String energyNumStr = null;
                        if (tempStr2.contains("&")) {
                            energyNumStr = tempStr2.substring(0, tempStr2.indexOf("&"));
                        } else {
                            energyNumStr = tempStr2.substring(0, tempStr2.length());
                        }
                        if (!TextUtils.isEmpty(energyNumStr)) {
                            mEngerNum = Integer.parseInt(energyNumStr.trim());
                        }
                        // Log.e("QuestionWebPager","=======>mEngerNum:"+mEngerNum+":"+energyNumStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            if ("xueersi://livevideo/examPaper/close".equals(url) || url.contains("baidu.com")) {
                if (isOpenNewCourseWare == 1) {
                    //中学连对激励
                    EventBus.getDefault().post(new EvenDriveEvent(EvenDriveEvent.CLOSE_H5));
                } else {
                    //连对激励
                    EventBus.getDefault().post(new EvenDriveEvent(EvenDriveEvent.CLOSE_SELF_H5).setTestId(testId));
                }
                ViewGroup group = (ViewGroup) mView.getParent();
                if (group != null) {
                    group.removeView(mView);
                }
                onPagerClose.onClose(QuestionWebX5Pager.this);
                Map<String, String> mData = new HashMap<>();
                mData.put("testid", "" + testId);
                mData.put("closetype", "clickWebCloseButton");
                mData.put("logtype", "interactTestClose");
                umsAgentDebugSys(questionEventId, mData);
            } else {
                if (url.contains("xueersi.com")) {
                    view.loadUrl(url);
                }
            }
            return true;
        }
    }

    public void setOpenNewCourseWare(int openNewCourseWare) {
        this.isOpenNewCourseWare = openNewCourseWare;
    }
}
