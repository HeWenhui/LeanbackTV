package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;

/**
 * 延迟发送请求
 */
public interface LiveHttpDelayAction {

    void sendPostDefault(final String url, final HttpRequestParams httpRequestParams,
                         long delayTime,
                         HttpCallBack httpCallBack);

    void sendJsonPost(final String url, final Object paramObject, long delayTime,
                      HttpCallBack httpCallBack);

    void sendJsonPostDefault(final String url, final HttpRequestParams httpRequestParams,
                             long delayTime, HttpCallBack httpCallBack);
}
