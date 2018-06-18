package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/6/11.
 */
public class ErrorWebViewClient extends WebViewClient {
    private String TAG;

    public ErrorWebViewClient(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        if (!webResourceRequest.isForMainFrame()) {
            String url = webResourceRequest.getUrl().toString();
            int index = url.indexOf("?");
            if (index != -1) {
                url = url.substring(0, index);
            }
            HashMap<String, String> map = new HashMap();
            map.put("tag", TAG);
            map.put("url", url);
            map.put("errorcode", "" + webResourceError.getErrorCode());
            map.put("description", "" + webResourceError.getDescription());
            Loger.d(webView.getContext(), LiveVideoConfig.LIVE_WEBVIEW_ERROR, map, true);
        }
        super.onReceivedError(webView, webResourceRequest, webResourceError);
    }
}
