package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.logerhelper.UmsAgentUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.io.File;

import java.io.File;

/**
 * Created by linyuqiang on 2017/3/25
 * 直播基本的WebView，增加刷新
 */
public abstract class BaseWebviewX5Pager extends LiveBasePager {
    protected WebView wvSubjectWeb;
    private TextView tv_error_center_refresh_tip;
    private TextView tv_data_loading_tip;
    protected View errorView;
    private String errorTip;
    private String loadTip;

    public BaseWebviewX5Pager(Context context) {
        super(context);
    }

    protected void initWebView() {
        wvSubjectWeb = (WebView) mView.findViewById(R.id.wv_livevideo_subject_web);
        tv_error_center_refresh_tip = (TextView) mView.findViewById(R.id.tv_error_center_refresh_tip);
        tv_data_loading_tip = (TextView) mView.findViewById(R.id.tv_data_loading_tip);
        errorView = mView.findViewById(R.id.rl_livevideo_subject_error);
        mView.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVisibility(View.GONE);
                wvSubjectWeb.setVisibility(View.VISIBLE);
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                loadView.setVisibility(View.VISIBLE);
                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                try {
                    Drawable drawable = ivLoading.getBackground();
                    if (drawable instanceof AnimationDrawable) {
                        ((AnimationDrawable) drawable).stop();
                    }
                } catch (Exception e) {
                    if (mLogtf != null) {
                        mLogtf.e("btn_error_refresh", e);
                    }
                }
                wvSubjectWeb.reload();
            }
        });
    }

    @Override
    public void initData() {
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
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

    @android.webkit.JavascriptInterface
    protected void addJavascriptInterface() {
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public void loadUrl(String url) {
        wvSubjectWeb.loadUrl(url);
    }

    public void reloadUrl(){
        wvSubjectWeb.reload();
    }

    public void setErrorTip(String errorTip) {
        this.errorTip = errorTip;
        tv_error_center_refresh_tip.setText(errorTip);
    }

    public void setLoadTip(String loadTip) {
        this.loadTip = loadTip;
        tv_data_loading_tip.setText(loadTip);
    }

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                logger.i( "onProgressChanged");
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                if (loadView != null) {
                    ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
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
                    loadView.setVisibility(View.GONE);
//                    ViewGroup group = (ViewGroup) loadView.getParent();
//                    group.removeView(loadView);
                }
            }
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, (BaseApplication)
                    BaseApplication.getContext(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result.confirm();
                }
            });
            cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result.cancel();
                }
            });
            cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo(message,
                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            return true;
        }

//        @Override
//        public void onPermissionRequest(PermissionRequest request) {
////            super.onPermissionRequest(request);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                request.grant(request.getResources());
//            } else {
//                super.onPermissionRequest(request);
//            }
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
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            BaseWebviewX5Pager.this.onReceivedTitle(view, title);
        }
    }

    public class MyWebViewClient extends ErrorWebViewClient {
        String failingUrl;

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (failingUrl == null) {
                wvSubjectWeb.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }
            logger.i( "onPageFinished");
            BaseWebviewX5Pager.this.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            logger.i( "onPageStarted");
            this.failingUrl = null;
            super.onPageStarted(view, url, favicon);
            BaseWebviewX5Pager.this.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
                    "&&," + description);
            this.failingUrl = failingUrl;
            wvSubjectWeb.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
            BaseWebviewX5Pager.this.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return BaseWebviewX5Pager.this.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
            return super.shouldInterceptRequest(view, s);
        }
    }

    public void onReceivedTitle(WebView view, String title) {
    }

    protected void onPageFinished(WebView view, String url) {

    }

    protected void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

    }

    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }
}