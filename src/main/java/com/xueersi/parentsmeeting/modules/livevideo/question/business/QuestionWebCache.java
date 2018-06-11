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
import com.xueersi.xesalib.utils.log.Loger;

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
//        webView.loadUrl("https://live.xueersi.com/science/Live/getMultiTestPaper?liveId=163256&testId=100817-1&stuId=31203&stuName=lyq2@qq.com&isTowall=0&stuCouId=8156421&isArts=0&isShowTeamPk=1");
    }

    public class MyWebViewClient extends ErrorWebViewClient {

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

    }
}
