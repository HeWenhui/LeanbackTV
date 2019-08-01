package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.logerhelper.UmsAgentUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by linyuqiang on 2018/6/6.
 * 直播主观题结果页面
 */
public class SubjectResultX5Pager extends LiveBasePager implements BaseSubjectResultInter {
    private String questionEventId = LiveVideoConfig.LIVE_H5_TEST;
    private Button btSubjectClose;
    private Button bt_livevideo_subject_calljs;
    private WebView wvSubjectWeb;
    private View errorView;
    /** 用户名称 */
    private String stuName;
    /** 用户Id */
    private String stuId;
    private String liveid;
    private String testId;
    /** 试卷地址 */
    private String examUrl = "";
    private String testPaperUrl;
    private String stuCouId;

    public SubjectResultX5Pager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, String testPaperUrl, String stuId, String liveid, String testId, String stuCouId) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.stuId = stuId;
        this.liveid = liveid;
        this.testId = testId;
        this.testPaperUrl = testPaperUrl;
        this.stuCouId = stuCouId;
        initData();
    }

    public String getTestId() {
        return testId;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_subject_question_x5, null);
        btSubjectClose = (Button) view.findViewById(R.id.bt_livevideo_subject_close);
        bt_livevideo_subject_calljs = (Button) view.findViewById(R.id.bt_livevideo_subject_calljs);
        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                view.findViewById(R.id.rl_livevideo_subject_error).setVisibility(View.GONE);
//                wvSubjectWeb.setVisibility(View.VISIBLE);
                wvSubjectWeb.reload();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        btSubjectClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup group = (ViewGroup) mView.getParent();
                group.removeView(mView);
                onPagerClose.onClose(SubjectResultX5Pager.this);
            }
        });
        bt_livevideo_subject_calljs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
//        wvSubjectWeb.loadUrl("file:///android_asset/testjs.html");
        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
        ((AnimationDrawable) ivLoading.getBackground()).start();
//        examUrl = testPaperUrl + "?liveId=" + liveid + "&testId=" + testId
//                + "&stuId=" + stuId + "&stuName=" + stuName;
        if(baseVideoQuestionEntity.isNewArtsH5Courseware()){
            examUrl = LiveHttpConfig.URL_NEWARTS_SUBMITRESULT_H5 + "?liveId=" + liveid + "&testId=" + testId + "&token=" + LiveAppUserInfo.getInstance().getUserToken();
//            examUrl = LiveVideoConfig.URL_NEWARTS_SUBMITRESULT_H5 + "?liveId=" + liveid + "&testId=" + testId;
            Log.e("Duncan","examUrl:" + examUrl);
        } else {
            examUrl = testPaperUrl + "/" + liveid + "?testId=" + testId
                    + "&stuId=" + stuId + "&stuName=" + stuName + "&stuCouId=" + stuCouId;
        }
//        String mEnStuId = LiveAppUserInfo.getInstance().getEnstuId(); // token
//        examUrl = BrowserBll.getAutoLoginURL(mEnStuId, examUrl, "", 0, true);
        mLogtf.d("initData:examUrl=" + examUrl);
        LiveHttpManager liveHttpManager = new LiveHttpManager(mContext);
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.sendGetNoBusiness(examUrl, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.d("onFailure",e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r = response.body().string();
                logger.d("onResponse:r="+r);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        wvSubjectWeb.loadDataWithBaseURL("",r,"text/html", "UTF-8", "");
                    }
                });
            }

        });
//        wvSubjectWeb.loadUrl(examUrl);
//        wvSubjectWeb.loadUrl("http://7.xesweb.sinaapp.com/test/examPaper2.html");
    }


    @android.webkit.JavascriptInterface
    private void addJavascriptInterface() {
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(false);
//        int scale = DeviceUtils.getScreenWidth(mContext) * 100 / 878;
//        wvSubjectWeb.setInitialScale(scale);
//        // 设置可以支持缩放
//        webSetting.setSupportZoom(true);
//        // 设置出现缩放工具
//        webSetting.setBuiltInZoomControls(true);
//        webSetting.setDisplayZoomControls(false);
    }

    public class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, MESSAGE_VERIFY_TYPE);
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
                    ViewGroup group = (ViewGroup) loadView.getParent();
                    group.removeView(loadView);
                }
            }
        }
    }

    public class MyWebViewClient extends WebViewClient {
        String failingUrl;

        @Override
        public void onPageFinished(WebView view, String url) {
            mLogtf.i("onPageFinished:url=" + url + ",failingUrl=" + failingUrl);
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
            if (!url.equals(examUrl)) {
                mLogtf.i("onPageStarted:setInitialScale");
                int scale = ScreenUtils.getScreenWidth() * 100 / 878;
                wvSubjectWeb.setInitialScale(scale);
            }
            super.onPageStarted(view, url, favicon);
        }

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
            if ("xueersi://livevideo/examPaper/close".equals(url) || "http://baidu.com/".equals(url)) {
                ViewGroup group = (ViewGroup) mView.getParent();
                if (group != null) {
                    group.removeView(mView);
                }
                onPagerClose.onClose(SubjectResultX5Pager.this);
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

}
