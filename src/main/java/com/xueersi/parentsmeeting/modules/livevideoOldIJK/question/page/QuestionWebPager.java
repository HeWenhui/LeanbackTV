//package com.xueersi.parentsmeeting.modules.livevideo.page;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Build;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.ConsoleMessage;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.xueersi.common.base.BasePager;
//import com.xueersi.common.logerhelper.LogerTag;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPkBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author linyuqiang
// * @date 2017/8/23
// * 普通互动题，h5显示页面
// */
//public class QuestionWebPager extends BasePager implements BaseQuestionWebInter {
//    private String questionEventId = LiveVideoConfig.LIVE_PUBLISH_TEST;
//    private Button btSubjectClose;
//    private Button btSubjectCalljs;
//    private WebView wvSubjectWeb;
//    private View errorView;
//    private StopWebQuestion questionBll;
//    /** 用户名称 */
//    private String stuName;
//    /** 用户Id */
//    private String stuId;
//    private String liveid;
//    private String testId;
//    private String nonce;
//    private LogToFile logToFile;
//    /** 试卷地址 */
//    private String examUrl = "";
//    /** 是不是考试结束 */
//    private boolean isEnd = false;
//    private String testPaperUrl;
//    private String jsExamSubmitAll = "javascript:examSubmitAll()";
//    private String isShowRanks;
//    private boolean isArts;
//    private String stuCouId;
//    private int isTeamPkRoom; //是否是 teampk 房间
//    private int mGoldNum;
//    private int mEngerNum;
//
//    public QuestionWebPager(Context context, StopWebQuestion questionBll, String testPaperUrl,
//                            String stuId, String stuName, String liveid, String testId,
//                            String nonce, String isShowRanks, boolean isArts, String stuCouId) {
//        super(context);
//        this.isArts = isArts;
//        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
//                + ".txt"));
//        this.questionBll = questionBll;
//        this.stuId = stuId;
//        this.stuName = stuName;
//        this.liveid = liveid;
//        this.testId = testId;
//        this.testPaperUrl = testPaperUrl;
//        this.nonce = nonce;
//        this.isShowRanks = isShowRanks;
//        this.stuCouId = stuCouId;
//        logToFile.i("ExamQuestionPager:liveid=" + liveid + ",testId=" + testId);
//        initData();
//    }
//
//    @Override
//    public String getTestId() {
//        return testId;
//    }
//
//    @Override
//    public View initView() {
//        final View view = View.inflate(mContext, R.layout.page_livevideo_subject_question, null);
//        btSubjectClose = (Button) view.findViewById(R.id.bt_livevideo_subject_close);
//        btSubjectCalljs = (Button) view.findViewById(R.id.bt_livevideo_subject_calljs);
//        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
//        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
//        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                view.findViewById(R.id.rl_livevideo_subject_error).setVisibility(View.GONE);
////                wvSubjectWeb.setVisibility(View.VISIBLE);
//                wvSubjectWeb.reload();
//                errorView.setVisibility(View.GONE);
//                mView.findViewById(R.id.rl_livevideo_subject_loading).setVisibility(View.VISIBLE);
//                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//                ((AnimationDrawable) ivLoading.getBackground()).start();
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void initData() {
//        btSubjectClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewGroup group = (ViewGroup) mView.getParent();
//                group.removeView(mView);
//                questionBll.stopWebQuestion(QuestionWebPager.this, testId);
//            }
//        });
//        btSubjectCalljs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                examSubmitAll();
//            }
//        });
//        addJavascriptInterface();
//        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
//        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
////        wvSubjectWeb.loadUrl("file:///android_asset/testjs.html");
//        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//        ((AnimationDrawable) ivLoading.getBackground()).start();
//        examUrl = testPaperUrl + "?liveId=" + liveid + "&testId=" + testId
//                + "&stuId=" + stuId + "&stuName=" + stuName + "&isTowall=" + isShowRanks;
////        String mEnStuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId(); // token
////        examUrl = BrowserBll.getAutoLoginURL(mEnStuId, examUrl, "", 0, true);
//        if (!StringUtils.isEmpty(nonce)) {
//            examUrl += "&nonce=" + nonce;
//        }
//        examUrl += "&stuCouId=" + stuCouId;
//        examUrl += "&isArts=" + (isArts ? "0" : "1");
//        examUrl += "&isShowTeamPk=" + (LiveBll.isAllowTeamPk ? "1" : "0");
//        wvSubjectWeb.loadUrl(examUrl);
//        Loger.e("QuestionWebPager", "======> loadUrl:" + examUrl);
//
//        mGoldNum = -1;
//        mEngerNum = -1;
//
//        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//                LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEngerNum, LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
//                if (questionBll != null && questionBll instanceof QuestionBll) {
//                    Loger.e("webViewCloseByTeacher", "=======> postEvent closeByTeacher:" + ((QuestionBll) questionBll).isWebViewCloseByTeacher());
//                    event.setCloseByTeahcer(((QuestionBll) questionBll).isWebViewCloseByTeacher());
//                    ((QuestionBll) questionBll).setWebViewCloseByTeacher(false);
//                }
//                EventBus.getDefault().post(event);
//                mGoldNum = -1;
//                mEngerNum = -1;
//            }
//        });
//
////        wvSubjectWeb.loadUrl("http://7.xesweb.sinaapp.com/test/examPaper2.html");
//    }
//
//    @Override
//    public BasePager getBasePager() {
//        return this;
//    }
//
//    @android.webkit.JavascriptInterface
//    private void addJavascriptInterface() {
//        WebSettings webSetting = wvSubjectWeb.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setBuiltInZoomControls(false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
////        int scale = DeviceUtils.getScreenWidth(mContext) * 100 / 878;
////        wvSubjectWeb.setInitialScale(scale);
////        // 设置可以支持缩放
////        webSetting.setSupportZoom(true);
////        // 设置出现缩放工具
////        webSetting.setBuiltInZoomControls(true);
////        webSetting.setDisplayZoomControls(false);
//    }
//
//    public void examSubmitAll() {
//        Map<String, String> mData = new HashMap<>();
//        mData.put("testid", "" + testId);
//        mData.put("logtype", "interactTestEnd");
//        questionBll.umsAgentDebugSys(questionEventId, mData);
////        wvSubjectWeb.loadUrl(String.format("javascript:examSubmitAll(" + code + ")"));
//        isEnd = true;
//        wvSubjectWeb.loadUrl(jsExamSubmitAll);
//    }
//
//    public class MyWebChromeClient extends android.webkit.WebChromeClient {
////        @Override
////        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
////            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, MESSAGE_VERIFY_TYPE);
////            verifyCancelAlertDialog.initInfo(message);
////            verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    wvSubjectWeb.reload();
////                }
////            });
////            verifyCancelAlertDialog.showDialog();
////            result.confirm();
////            return true;
////        }
//
//        @Override
//        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//            ConsoleMessage.MessageLevel mLevel = consoleMessage.messageLevel();
//            boolean isRequst = false;
//            if (mLevel == ConsoleMessage.MessageLevel.ERROR || mLevel == ConsoleMessage.MessageLevel.WARNING) {
//                isRequst = true;
//            }
//            Loger.d(mContext, LogerTag.DEBUG_WEBVIEW_CONSOLE, TAG + ",Level=" + mLevel + "&&," + consoleMessage.sourceId() +
//                    "&&," + consoleMessage.lineNumber() + "&&," + consoleMessage.message(), isRequst);
//            return super.onConsoleMessage(consoleMessage);
//        }
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//            if (newProgress == 100) {
//                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
//                if (loadView != null) {
//                    ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//                    ((AnimationDrawable) ivLoading.getBackground()).stop();
//                    /*ViewGroup group = (ViewGroup) loadView.getParent();
//                    group.removeView(loadView);*/
//                    loadView.setVisibility(View.GONE);
//                }
//            }
//        }
//    }
//
//    public class MyWebViewClient extends WebViewClient {
//        String failingUrl;
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            logToFile.i("onPageFinished:url=" + url + ",failingUrl=" + failingUrl + ",isEnd=" + isEnd);
//            if (isEnd && url.equals(examUrl)) {
//                wvSubjectWeb.loadUrl(jsExamSubmitAll);
//                logToFile.i("onPageFinished:examSubmitAll");
//            }
//            if (failingUrl == null) {
//                wvSubjectWeb.setVisibility(View.VISIBLE);
//                errorView.setVisibility(View.GONE);
//            }
//            Map<String, String> mData = new HashMap<>();
//            mData.put("testid", "" + testId);
//            mData.put("logtype", "interactTestDidLoad");
//            mData.put("status", "success");
//            mData.put("loadurl", url);
//            questionBll.umsAgentDebugSys(questionEventId, mData);
////            super.onPageFinished(view, url);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            this.failingUrl = null;
//            if (!url.equals(examUrl)) {
//                logToFile.i("onPageStarted:setInitialScale");
//                int scale = ScreenUtils.getScreenWidth() * 100 / 878;
//                wvSubjectWeb.setInitialScale(scale);
//            }
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            this.failingUrl = failingUrl;
//            Loger.d(mContext, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
//                    "&&," + description, true);
//            logToFile.i("onReceivedError:failingUrl=" + failingUrl + ",errorCode=" + errorCode);
////            super.onReceivedError(view, errorCode, description, failingUrl);
//            wvSubjectWeb.setVisibility(View.INVISIBLE);
//            errorView.setVisibility(View.VISIBLE);
//            Map<String, String> mData = new HashMap<>();
//            mData.put("testid", "" + testId);
//            mData.put("logtype", "interactTestDidLoad");
//            mData.put("status", "fail");
//            mData.put("loadurl", failingUrl);
//            mData.put("msg", description);
//            questionBll.umsAgentDebugSys(questionEventId, mData);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            logToFile.i("shouldOverrideUrlLoading:url=" + url);
//
//            Loger.e("QuestionWebPager", "======> shouldOverrideUrlLoading:" + url);
//
//            if (url.contains("science/Live/getMultiTestResult")) {
//                if (questionBll instanceof QuestionBll) {
//                    ((QuestionBll) questionBll).onSubmit(XESCODE.STOPQUESTION, url.contains("submitType=force"));
//                }
//                return false;
//            }
//
//            if (url.contains(TeamPkBll.TEAMPK_URL_FIFTE)) {
//                try {
//                    int startIndex = url.indexOf("goldNum=") + "goldNum=".length();
//                    if (startIndex != -1) {
//                        String teamStr = url.substring(startIndex, url.length());
//                        int endIndex = teamStr.indexOf("&");
//                        String goldNUmStr = teamStr.substring(0, endIndex);
//                        if (!TextUtils.isEmpty(goldNUmStr)) {
//                            mGoldNum = Integer.parseInt(goldNUmStr.trim());
//                        }
//                    }
//                    int satrIndex2 = url.indexOf("energyNum=") + "energyNum=".length();
//                    if (satrIndex2 != -1) {
//                        String tempStr2 = url.substring(satrIndex2);
//                        String energyNumStr = null;
//                        if (tempStr2.contains("&")) {
//                            energyNumStr = tempStr2.substring(0, tempStr2.indexOf("&"));
//                        } else {
//                            energyNumStr = tempStr2.substring(0, tempStr2.length());
//                        }
//                        if (!TextUtils.isEmpty(energyNumStr)) {
//                            mEngerNum = Integer.parseInt(energyNumStr.trim());
//                        }
//                        // Log.e("QuestionWebPager","=======>mEngerNum:"+mEngerNum+":"+energyNumStr);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//
//            if ("xueersi://livevideo/examPaper/close".equals(url) || url.contains("baidu.com")) {
//                ViewGroup group = (ViewGroup) mView.getParent();
//                if (group != null) {
//                    group.removeView(mView);
//                }
//                questionBll.stopWebQuestion(QuestionWebPager.this, testId);
//                Map<String, String> mData = new HashMap<>();
//                mData.put("testid", "" + testId);
//                mData.put("closetype", "clickWebCloseButton");
//                mData.put("logtype", "interactTestClose");
//                questionBll.umsAgentDebugSys(questionEventId, mData);
//            } else {
//                if (url.contains("xueersi.com")) {
//                    view.loadUrl(url);
//                }
//            }
//            return true;
//        }
//    }
//
//}
