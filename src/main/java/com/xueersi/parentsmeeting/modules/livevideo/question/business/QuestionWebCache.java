package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;

import ren.yale.android.cachewebviewlib.CacheWebView;

/**
 * 互动题缓存
 * Created by linyuqiang on 2018/6/11.
 */
public class QuestionWebCache {
    private String TAG = "QuestionWebCache";
    private Context context;

    public QuestionWebCache(Context context) {
        this.context = context;
    }

    public void startCache() {
        CacheWebView webView = new CacheWebView(context);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("https://live.xueersi.com/science/Live/getMultiTestPaper?liveId=119740&testId=365160-1&stuId=-111&stuName=test@talwx.com&stuCouId=12345654&isArts=0&nonce=45645dasf&isTowall=0");
        webView.getWebViewCache().setNeedHttpDns(true);
    }

    public class MyWebViewClient extends ErrorWebViewClient {

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            webView.destroy();
            logger.d( "onPageFinished:s=" + s);
        }
    }
}
