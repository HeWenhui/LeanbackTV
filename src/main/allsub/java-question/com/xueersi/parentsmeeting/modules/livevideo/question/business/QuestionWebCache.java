package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import ren.yale.android.cachewebviewlib.CacheWebResourceResponse;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.RequestIntercept;

/**
 * 互动题缓存
 * Created by linyuqiang on 2018/6/11.
 */
public class QuestionWebCache {
    private String TAG = "QuestionWebCache";
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;
    private int newProgress;
    private int urlindex = 0;
    private LiveThreadPoolExecutor threadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    /** 是不是已经开始加载 */
    private static boolean startLoad = false;

    public QuestionWebCache(Context context) {
        this.context = context;
    }

    public void startCache() {
        logger.d("startCache:startLoad=" + startLoad);
        if (startLoad) {
            return;
        }
        startLoad = true;
        final Handler handler = LiveMainHandler.getMainHandler();
        final CacheWebView webView = new CacheWebView(context);
        //20秒超时，权限加载
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.d("startCache:destroy1");
                    startLoad = false;
                    webView.stopLoading();
                    webView.destroy();
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
        };
        handler.postDelayed(runnable, 20000);
        webView.setWebViewClient(new MyWebViewClient());
        final String examUrl = "https://live.xueersi.com/science/Live/getMultiTestPaper?liveId=119740&testId=365160-1&stuId=-111&stuName=test@talwx.com&stuCouId=12345654&isArts=0&nonce=45645dasf&isTowall=0";
        webView.loadUrl(examUrl);

        webView.getWebViewCache().setNeedHttpDns(true);
        webView.setRequestIntercept(new RequestIntercept() {
            @Override
            public void onIntercept(final String url, CacheWebResourceResponse webResourceResponse) {
                final int startProgress = newProgress;
                final boolean isIntercept = webResourceResponse != null;
                final boolean ispreload = isIntercept && webResourceResponse.isFile();
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StableLogHashMap stableLogHashMap = new StableLogHashMap("interceptrequestv1");
                            stableLogHashMap.put("courseurl", "" + examUrl);
                            stableLogHashMap.put("url", url);
                            stableLogHashMap.put("urlindex", "" + (urlindex++));
                            stableLogHashMap.put("newProgress", "" + newProgress);
                            stableLogHashMap.put("startProgress", "" + startProgress);
                            stableLogHashMap.put("liveId", "119740");
                            stableLogHashMap.put("testid", "365160-1");
                            stableLogHashMap.put("isIntercept", "" + isIntercept);
                            stableLogHashMap.put("ispreload", "" + ispreload);
                            UmsAgentManager.umsAgentDebug(context, LiveVideoConfig.LIVE_H5_TEST_PRELOAD, stableLogHashMap.getData());
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                });
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            boolean hasdestroy = false;

            @Override
            public void onProgressChanged(WebView webView, int i) {
                newProgress = i;
                if (i == 100) {
                    startLoad = false;
                    if (hasdestroy) {
                        return;
                    }
                    hasdestroy = true;
                    handler.removeCallbacks(runnable);
                }
            }
        });
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
