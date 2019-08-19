package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;

public interface LiveHttpAction {
    LiveVideoSAConfig.Inner getLiveVideoSAConfigInner();

    void sendPostDefault(final String url, final HttpRequestParams httpRequestParams, HttpCallBack httpCallBack);

    void sendJsonPost(final String url, final Object paramObject, HttpCallBack httpCallBack);

    void sendJsonPostDefault(final String url, final HttpRequestParams httpRequestParams, HttpCallBack httpCallBack);
}
