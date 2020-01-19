package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.xueersi.common.util.XrsBroswer;
import com.xueersi.lib.monitor.AppMonitor;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.browser.business.XesWebViewCookieUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.WebViewObserve;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.config.CacheConfig;
import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;


/**
 * Created by yale on 2017/9/15.
 */

public class CacheWebView extends WebView {
    private static String TAG = "CacheWebView";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private static final String CACHE_NAME = "CacheWebView";
    private static final int CACHE_SIZE = 200 * 1024 * 1024;
    private String mAppCachePath = "";
    private CacheWebViewClient mCacheWebViewClient;


    private WebViewCache mWebViewCache;

    public CacheWebView(Context context) {
        super(context);
        init();
    }

    public CacheWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CacheWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initData();
        initSettings();
        initWebViewClient();
    }

    private void initData() {

        mWebViewCache = new WebViewCache(this);
        File cacheFile = new File(getContext().getCacheDir(), CACHE_NAME);
        try {
            mWebViewCache.openCache(getContext(), cacheFile.getAbsolutePath(), CACHE_SIZE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEncoding(String encoding) {
        if (TextUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        mCacheWebViewClient.setEncoding(encoding);
    }

    public void setCacheInterceptor(CacheInterceptor interceptor) {
        mCacheWebViewClient.setCacheInterceptor(interceptor);
    }

    public void setRequestIntercept(RequestIntercept requestIntercept) {
        mCacheWebViewClient.setRequestIntercept(requestIntercept);
    }

    public static CacheConfig getCacheConfig() {
        return CacheConfig.getInstance();
    }

    public WebViewCache getWebViewCache() {
        return mWebViewCache;
    }

    public void setWebViewClient(WebViewClient client) {
        mCacheWebViewClient.setCustomWebViewClient(client);
    }

    private void initWebViewClient() {
        mCacheWebViewClient = new CacheWebViewClient();
        super.setWebViewClient(mCacheWebViewClient);
        mCacheWebViewClient.setUserAgent(this.getSettings().getUserAgentString());
        mCacheWebViewClient.setWebViewCache(mWebViewCache);
    }

    public void setCacheStrategy(WebViewCache.CacheStrategy cacheStrategy) {
        mCacheWebViewClient.setCacheStrategy(cacheStrategy);
        if (cacheStrategy == WebViewCache.CacheStrategy.NO_CACHE) {
            setWebViewDefaultNoCache();
        } else {
            setWebViewDefaultCacheMode();
        }
    }

    public static CacheWebView cacheWebView(Context context) {
        return new CacheWebView(context);
    }

    public static void servicePreload(Context context, String url) {
        servicePreload(context, url, null);
    }

    public static void servicePreload(Context context, String url, HashMap<String, String> headerMap) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Intent intent = new Intent(context, CachePreLoadService.class);
            intent.putExtra(CachePreLoadService.KEY_URL, url);
            if (headerMap != null) {
                intent.putExtra(CachePreLoadService.KEY_URL_HEADER, headerMap);
            }
            intent.setPackage(AppConfig.APPLICATION_ID);
            context.startService(intent);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    public void setEnableCache(boolean enableCache) {
        mCacheWebViewClient.setEnableCache(enableCache);
    }

    public void loadUrl(String url) {
        url = getSessionUrl(url);
        if (url.startsWith("http")) {
            mCacheWebViewClient.addVisitUrl(url);
        }
        super.loadUrl(url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        url = getSessionUrl(url);
        mCacheWebViewClient.addVisitUrl(url);
        if (additionalHttpHeaders != null) {
            mCacheWebViewClient.addHeaderMap(url, additionalHttpHeaders);
            super.loadUrl(url, additionalHttpHeaders);
        } else {
            super.loadUrl(url);
        }

    }

    private static String sessionId;

    private String getSessionUrl(String url) {
        XrsBroswer.writeLog("getSessionUrl:url=" + url);
        if (url == null) {
            return null;
        }
        if (url.startsWith("javascript")) {
            return url;
        }
        XesWebViewCookieUtils.syncWebLogin(url);
        //网页地址增加参数
//        try {
//            if (sessionId == null) {
//                sessionId = UmsAgentTrayPreference.getInstance().getString(UmsAgentTrayPreference.UMSAGENT_APP_SESSID);
//            }
//            if (url.contains("?")) {
//                url += "&client_sessionid=" + sessionId;
//            } else {
//                url += "?client_sessionid=" + sessionId;
//            }
//            logger.d("getSessionUrl:url=" + url);
//            return url;
//        } catch (Exception e) {
//            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
//        }
        WebViewObserve.getInstance().loadUrl(this, url);
        return url;
    }

    public void setBlockNetworkImage(boolean isBlock) {
        mCacheWebViewClient.setBlockNetworkImage(isBlock);
    }

    private void initSettings() {
        WebSettings webSettings = this.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        webSettings.setTextZoom(100);
        AppMonitor.getInstance().showUpX5WebView(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        setWebViewDefaultCacheMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        setCachePath();

    }

    private void setWebViewDefaultNoCache() {
        WebSettings webSettings = this.getSettings();
        webSettings.setCacheMode(
                WebSettings.LOAD_NO_CACHE);
    }

    private void setWebViewDefaultCacheMode() {
        WebSettings webSettings = this.getSettings();
        if (NetworkUtils.isConnected(this.getContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    public String getUserAgent() {
        return this.getSettings().getUserAgentString();
    }

    public void setUserAgent(String userAgent) {
        WebSettings webSettings = this.getSettings();
        webSettings.setUserAgentString(userAgent);
        mCacheWebViewClient.setUserAgent(userAgent);
    }

    private void setCachePath() {

        File cacheFile = new File(this.getContext().getCacheDir(), CACHE_NAME);
        String path = cacheFile.getAbsolutePath();
        mAppCachePath = path;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        WebSettings webSettings = this.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(path);
    }

    public void clearCache() {
        CacheWebViewLog.d("clearCache");
        FileUtil.deleteDirs(mAppCachePath, false);
        mWebViewCache.clean();
    }

    public void destroy() {

        CacheWebViewLog.d("destroy");
        mCacheWebViewClient.clear();
        mWebViewCache.release();

        this.stopLoading();
        this.getSettings().setJavaScriptEnabled(false);
        this.clearHistory();
        this.removeAllViews();

        ViewParent viewParent = this.getParent();

        if (viewParent == null) {
            super.destroy();
            return;
        }
        ViewGroup parent = (ViewGroup) viewParent;
        parent.removeView(this);
        super.destroy();
        WebViewObserve.getInstance().destory(this);
    }

    @Override
    public void goBack() {
        if (canGoBack()) {
            mCacheWebViewClient.clearLastUrl();
            super.goBack();
        }
    }

    public void evaluateJS(String strJsFunction) {
        this.evaluateJS(strJsFunction, null);
    }

    public void evaluateJS(String strJsFunction, ValueCallback valueCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && valueCallback != null) {
            this.evaluateJavascript("javascript:" + strJsFunction, valueCallback);
        } else {
            this.loadUrl("javascript:" + strJsFunction);
        }
    }

}
