package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.HashMap;
import java.util.Map;

public class WebViewObserve {
    private String TAG = "WebViewObserve";
    private String enentId = "debug_live_webview_load";
    private static WebViewObserve observe;

    public static WebViewObserve getInstance() {
        if (observe == null) {
            observe = new WebViewObserve();
        }
        return observe;
    }

    private Map<String, String> common = new HashMap<>();

    public void put(String key, String value) {
        try {
            common.put(key, value);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    private String url;

    public void loadUrl(WebView webView, String url) {
        this.url = url;
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("loadurl");
            logHashMap.put("url", url);
            //是不是x5浏览器
            logHashMap.put("isx5", webView.getX5WebViewExtension() != null ? "true" : "false");
            logHashMap.put("freememory", "" + Runtime.getRuntime().freeMemory());
            logHashMap.getData().putAll(common);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), enentId, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }

    public void destory(WebView webView) {
        try {
            StableLogHashMap logHashMap = new StableLogHashMap("destory");
            logHashMap.put("url", url);
            logHashMap.put("freememory", "" + Runtime.getRuntime().freeMemory());
            logHashMap.getData().putAll(common);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), enentId, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }
}
