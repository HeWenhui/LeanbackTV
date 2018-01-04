package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by lyqai on 2018/1/3.
 */

public interface WebViewRequest {
    void requestWebView();

    void releaseWebView();

    void onWebViewEnd();
}
