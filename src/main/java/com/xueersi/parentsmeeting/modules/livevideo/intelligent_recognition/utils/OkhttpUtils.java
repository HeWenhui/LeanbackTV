package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import okhttp3.OkHttpClient;

public class OkhttpUtils {
    private final static OkHttpClient okHttpClient = new OkHttpClient();

    public static OkHttpClient getOkHttpClient() {
//        if (okHttpClient == null) {
//            synchronized (OkhttpUtils.class) {
//                if (okHttpClient == null) {
//                    okHttpClient = new OkHttpClient();
//                }
//            }
//        }
        return okHttpClient;
    }
}
