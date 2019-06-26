package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ErrorWebViewClient;

import ren.yale.android.cachewebviewlib.CacheWebView;

/**
 * 互动题缓存
 * Created by linyuqiang on 2018/6/11.
 */
public class QuestionWebCache {
    private String TAG = "QuestionWebCache";
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;

    public QuestionWebCache(Context context) {
        this.context = context;
    }

    public void startCache() {

        final CacheWebView webView = new CacheWebView(context);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("https://live.xueersi.com/science/Live/getMultiTestPaper?liveId=119740&testId=365160-1&stuId=-111&stuName=test@talwx.com&stuCouId=12345654&isArts=0&nonce=45645dasf&isTowall=0");

        webView.getWebViewCache().setNeedHttpDns(true);
    }

    private class MyWebViewClient extends ErrorWebViewClient {

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            logger.d("shouldInterceptRequest:url=" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            webView.destroy();
            logger.d("onPageFinished:s=" + s);
        }
    }
}
