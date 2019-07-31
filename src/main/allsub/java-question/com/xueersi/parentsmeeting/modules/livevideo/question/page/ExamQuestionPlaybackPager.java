//package com.xueersi.parentsmeeting.modules.livevideo.question.page;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Build;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.ConsoleMessage;
//import android.webkit.JsResult;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.common.base.BasePager;
//import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.common.entity.MyUserInfoEntity;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.entity.AppInfoEntity;
//import com.xueersi.common.logerhelper.LogerTag;
//import com.xueersi.common.business.UserBll;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import cn.dreamtobe.kpswitch.util.KeyboardUtil;
//import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
//
//import static com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog.MESSAGE_VERIFY_TYPE;
//
//
///**
// * Created by linyuqiang on 2016/12/7.
// * 直播回放试卷答题页面
// */
//public class ExamQuestionPlaybackPager extends BasePager implements BaseExamQuestionInter {
//    String TAG = "ExamQuestionPlaybackPager";
//    private Button btSubjectClose;
//    Button bt_livevideo_subject_calljs;
//    private WebView wvSubjectWeb;
//    private String liveid;
//    private String num;
//    ExamStop examStop;
//    private View errorView;
//    /** 试卷地址 */
//    private String examUrl = "";
//    /** 是不是考试结束 */
//    private boolean isEnd = false;
//    String jsExamSubmitAll = "javascript:examSubmitAll()";
//    boolean isArts;
//    String stuCouId;
//
//    public ExamQuestionPlaybackPager(Context context, String liveid, String num, boolean isArts, String stuCouId, ExamStop examStop) {
//        super(context);
//        this.examStop = examStop;
//        this.liveid = liveid;
//        this.isArts = isArts;
//        this.num = num;
//        this.stuCouId = stuCouId;
//        initData();
//    }
//
//    @Override
//    public String getNum() {
//        return num;
//    }
//
//    @Override
//    public View initView() {
//        View view = View.inflate(mContext, R.layout.page_livebackvideo_subject_question, null);
//        btSubjectClose = (Button) view.findViewById(R.id.bt_livevideo_subject_close);
//        bt_livevideo_subject_calljs = (Button) view.findViewById(R.id.bt_livevideo_subject_calljs);
//        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
//        KeyboardUtil.attach((Activity) mContext, new KPSwitchFSPanelLinearLayout(mContext), new KeyboardUtil.OnKeyboardShowingListener() {
//            @Override
//            public void onKeyboardShowing(boolean isShowing) {
//                ExamQuestionPlaybackPager.this.onKeyboardShowing(isShowing);
//            }
//        });
//        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
//        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
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
//                examStop.stopExam();
//            }
//        });
//        bt_livevideo_subject_calljs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                examSubmitAll();
//            }
//        });
//        addJavascriptInterface();
//        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
//        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
//        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//        ((AnimationDrawable) ivLoading.getBackground()).start();
////        wvSubjectWeb.loadUrl("file:///android_asset/testjs.html");
//        MyUserInfoEntity userInfoEntity = LiveAppUserInfo.getInstance();
//        AppInfoEntity mAppInfoEntity = AppBll.getInstance().getAppInfoEntity();
//        String EXAM_URL = mShareDataManager.getString(ShareBusinessConfig.SP_LIVE_EXAM_URL, ShareBusinessConfig.EXAM_URL, ShareDataManager.SHAREDATA_USER);
//        if (isArts) {
//            EXAM_URL = mShareDataManager.getString(ShareBusinessConfig.SP_LIVE_EXAM_URL_SCIENCE, EXAM_URL, ShareDataManager.SHAREDATA_USER);
//            EXAM_URL = EXAM_URL.replace(ShareBusinessConfig.LIVE_LIBARTS, ShareBusinessConfig.LIVE_SCIENCE);
//        } else {
//            EXAM_URL = mShareDataManager.getString(ShareBusinessConfig.SP_LIVE_EXAM_URL_LIBARTS, EXAM_URL, ShareDataManager.SHAREDATA_USER);
//            EXAM_URL = EXAM_URL.replace(ShareBusinessConfig.LIVE_SCIENCE, ShareBusinessConfig.LIVE_LIBARTS);
//        }
//        if (EXAM_URL.contains("xueersi.com/LiveExam")) {
//            String host = isArts ? ShareBusinessConfig.LIVE_SCIENCE : ShareBusinessConfig.LIVE_LIBARTS;
//            EXAM_URL = EXAM_URL.replace("xueersi.com/LiveExam", "xueersi.com/" + host + "/LiveExam");
//        }
//        examUrl = EXAM_URL + "?liveId=" + liveid
//                + "&testPlan=" + num + "&isPlayBack=1&stuId=" + userInfoEntity.getStuId() + "&stuName=" + mAppInfoEntity.getLoginUserName();
//        examUrl += "&isArts=" + (isArts ? "0" : "1") + "&stuCouId=" + stuCouId;
//        wvSubjectWeb.loadUrl(examUrl);
//    }
//
//    @Override
//    public void onKeyboardShowing(boolean isShowing) {
//        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) wvSubjectWeb.getLayoutParams();
//        int bottomMargin;
//        if (isShowing) {
//            int panelHeight = KeyboardUtil.getValidPanelHeight(mContext);
//            bottomMargin = panelHeight;
//        } else {
//            bottomMargin = 0;
//        }
//        if (bottomMargin != lp.bottomMargin) {
//            lp.bottomMargin = bottomMargin;
////            wvSubjectWeb.setLayoutParams(lp);
//            LayoutParamsUtil.setViewLayoutParams(wvSubjectWeb, lp);
//        }
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
////        wvSubjectWeb.setInitialScale(DeviceUtils.getScreenWidth(mContext) * 100 / 878);
//    }
//
//    @Override
//    public void examSubmitAll() {
//        isEnd = true;
////        wvSubjectWeb.loadUrl(String.format("javascript:examSubmitAll(" + code + ")"));
//        wvSubjectWeb.loadUrl(jsExamSubmitAll);
//    }
//
//    @Override
//    public BasePager getBasePager() {
//        return this;
//    }
//
//    public class MyWebChromeClient extends android.webkit.WebChromeClient {
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, MESSAGE_VERIFY_TYPE);
//            verifyCancelAlertDialog.initInfo(message);
//            verifyCancelAlertDialog.showDialog();
//            result.confirm();
//            return true;
//        }
//
//        @Override
//        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//            ConsoleMessage.MessageLevel mLevel = consoleMessage.messageLevel();
//            boolean isRequst = false;
//            if (mLevel == ConsoleMessage.MessageLevel.ERROR || mLevel == ConsoleMessage.MessageLevel.WARNING) {
//                isRequst = true;
//            }
//            Loger.d(mContext, LogerTag.DEBUG_WEBVIEW_CONSOLE, "ExamQuestionPlaybackPager,Level=" + mLevel + "&&," + consoleMessage.sourceId() +
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
//            logger.i( "onPageFinished:url=" + url + ",failingUrl=" + failingUrl);
//            if (failingUrl == null) {
//                wvSubjectWeb.setVisibility(View.VISIBLE);
//                errorView.setVisibility(View.GONE);
//            }
////            super.onPageFinished(view, url);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            this.failingUrl = null;
//            if (!url.equals(examUrl)) {
//                logger.i( "onPageStarted:setInitialScale");
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
//            logger.i( "onReceivedError:failingUrl=" + failingUrl + ",errorCode=" + errorCode);
////            super.onReceivedError(view, errorCode, description, failingUrl);
//            wvSubjectWeb.setVisibility(View.INVISIBLE);
//            errorView.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if ("xueersi://livevideo/examPaper/close".equals(url) || "http://baidu.com/".equals(url)) {
//                ViewGroup group = (ViewGroup) mView.getParent();
//                if (group != null) {
//                    group.removeView(mView);
//                    examStop.stopExam();
//                }
//                logger.i( "shouldOverrideUrlLoading:stopExam");
//            } else {
//                if (url.contains("xueersi.com")) {
//                    view.loadUrl(url);
//                }
//            }
//            return true;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        wvSubjectWeb.stopLoading();
//        wvSubjectWeb.destroy();
//    }
//
//}
