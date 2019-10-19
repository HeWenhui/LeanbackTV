package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.logerhelper.UmsAgentUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

public class BaseCoursewareNativePager extends LiveBasePager {
    protected WebView wvSubjectWeb;
    /** 失败地址 */
    protected String failingUrl = null;
    public static String XES_LOADING_X5_ERROR_COUNT = "xes_loading_x5_error_count";

    public BaseCoursewareNativePager(Context context) {
        super(context);
    }

    public BaseCoursewareNativePager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    public BaseCoursewareNativePager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
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

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            BaseCoursewareNativePager.this.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult jsResult) {
            StableLogHashMap logHashMap = new StableLogHashMap("onJsAlert");
            logHashMap.put("tag", TAG);
            logHashMap.put("url", url);
            logHashMap.put("message", message);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LiveVideoConfig.LIVE_WEBVIEW_JS_ALERT, logHashMap.getData());
            return super.onJsAlert(webView, url, message, jsResult);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,
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
            StableLogHashMap logHashMap = new StableLogHashMap("onJsConfirm");
            logHashMap.put("tag", TAG);
            logHashMap.put("url", url);
            logHashMap.put("message", message);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LiveVideoConfig.LIVE_WEBVIEW_JS_ALERT, logHashMap.getData());
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
            if (AppConfig.DEBUG) {
                mLogtf.debugSave("onConsoleMessage:level=" + consoleMessage.messageLevel() + ",sourceId=" + consoleMessage.sourceId()
                        + ",lineNumber=" + consoleMessage.lineNumber() + ",message=" + consoleMessage.message());
            }
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            BaseCoursewareNativePager.this.onReceivedTitle(view, title);
            int count =  UmsAgentTrayPreference.getInstance().getInt(XES_LOADING_X5_ERROR_COUNT,0);

            UmsAgentTrayPreference.getInstance().put(XES_LOADING_X5_ERROR_COUNT,count-1);
        }
    }

    protected void onProgressChanged(WebView view, int newProgress) {

    }

    public class MyWebViewClient extends ErrorWebViewClient {

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            BaseCoursewareNativePager.this.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            logger.i("onPageStarted");
            BaseCoursewareNativePager.this.failingUrl = null;
            super.onPageStarted(view, url, favicon);
            BaseCoursewareNativePager.this.onPageStarted(view, url, favicon);

            int count =  UmsAgentTrayPreference.getInstance().getInt(XES_LOADING_X5_ERROR_COUNT,0);

            UmsAgentTrayPreference.getInstance().put(XES_LOADING_X5_ERROR_COUNT,count+1);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
                    "&&," + description);
            BaseCoursewareNativePager.this.failingUrl = failingUrl;
            BaseCoursewareNativePager.this.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return BaseCoursewareNativePager.this.shouldOverrideUrlLoading(view, url);
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
