//package com.xueersi.parentsmeeting.modules.livevideo.page;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Environment;
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
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSubjectResultInter;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by linyuqiang on 2017/10/31.
// * 直播主观题结果页面
// */
//public class SubjectResultPager extends BasePager implements BaseSubjectResultInter {
//    private String questionEventId = LiveVideoConfig.LIVE_PUBLISH_TEST;
//    private Button btSubjectClose;
//    private Button bt_livevideo_subject_calljs;
//    private WebView wvSubjectWeb;
//    private View errorView;
//    private BaseQuestionWebInter.StopWebQuestion questionBll;
//    /** 用户名称 */
//    private String stuName;
//    /** 用户Id */
//    private String stuId;
//    private String liveid;
//    private String testId;
//    private LogToFile logToFile;
//    /** 试卷地址 */
//    private String examUrl = "";
//    private String testPaperUrl;
//    private String stuCouId;
//
//    public SubjectResultPager(Context context, BaseQuestionWebInter.StopWebQuestion questionBll, String testPaperUrl, String stuId, String liveid, String testId, String stuCouId) {
//        super(context);
//        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
//                + ".txt"));
//        this.questionBll = questionBll;
//        this.stuId = stuId;
//        this.liveid = liveid;
//        this.testId = testId;
//        this.testPaperUrl = testPaperUrl;
//        this.stuCouId = stuCouId;
//        logToFile.i(TAG + ":liveid=" + liveid + ",testId=" + testId);
//        initData();
//    }
//
//    public String getTestId() {
//        return testId;
//    }
//
//    @Override
//    public View initView() {
//        final View view = View.inflate(mContext, R.layout.page_livevideo_subject_question, null);
//        btSubjectClose = (Button) view.findViewById(R.id.bt_livevideo_subject_close);
//        bt_livevideo_subject_calljs = (Button) view.findViewById(R.id.bt_livevideo_subject_calljs);
//        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
//        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
//        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                view.findViewById(R.id.rl_livevideo_subject_error).setVisibility(View.GONE);
////                wvSubjectWeb.setVisibility(View.VISIBLE);
//                wvSubjectWeb.reload();
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
//                questionBll.stopWebQuestion(SubjectResultPager.this, testId);
//            }
//        });
//        bt_livevideo_subject_calljs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        addJavascriptInterface();
//        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
//        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
////        wvSubjectWeb.loadUrl("file:///android_asset/testjs.html");
//        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//        ((AnimationDrawable) ivLoading.getBackground()).start();
////        examUrl = testPaperUrl + "?liveId=" + liveid + "&testId=" + testId
////                + "&stuId=" + stuId + "&stuName=" + stuName;
//        examUrl = testPaperUrl + "?testId=" + testId
//                + "&stuId=" + stuId + "&stuName=" + stuName + "&stuCouId=" + stuCouId;
////        String mEnStuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId(); // token
////        examUrl = BrowserBll.getAutoLoginURL(mEnStuId, examUrl, "", 0, true);
//        logger.d( "initData:examUrl=" + examUrl);
//        wvSubjectWeb.loadUrl(examUrl);
////        wvSubjectWeb.loadUrl("http://7.xesweb.sinaapp.com/test/examPaper2.html");
//    }
//
//    @android.webkit.JavascriptInterface
//    private void addJavascriptInterface() {
//        WebSettings webSetting = wvSubjectWeb.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setBuiltInZoomControls(false);
////        int scale = DeviceUtils.getScreenWidth(mContext) * 100 / 878;
////        wvSubjectWeb.setInitialScale(scale);
////        // 设置可以支持缩放
////        webSetting.setSupportZoom(true);
////        // 设置出现缩放工具
////        webSetting.setBuiltInZoomControls(true);
////        webSetting.setDisplayZoomControls(false);
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
//                    ViewGroup group = (ViewGroup) loadView.getParent();
//                    group.removeView(loadView);
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
//            logToFile.i("onPageFinished:url=" + url + ",failingUrl=" + failingUrl);
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
//            if ("xueersi://livevideo/examPaper/close".equals(url) || "http://baidu.com/".equals(url)) {
//                ViewGroup group = (ViewGroup) mView.getParent();
//                if (group != null) {
//                    group.removeView(mView);
//                }
//                questionBll.stopWebQuestion(SubjectResultPager.this, testId);
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
