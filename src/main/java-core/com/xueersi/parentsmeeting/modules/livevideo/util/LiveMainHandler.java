package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 直播主线程handler，2019/8/6
 */
public class LiveMainHandler {

    public static Handler getMainHandler() {
        Handler mHandler = new Handler(Looper.getMainLooper());
        return mHandler;
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static boolean post(Runnable runnable) {
        return mHandler.post(runnable);
    }

    public static boolean postDelayed(Runnable r, long delayMillis) {
        return mHandler.postDelayed(r, delayMillis);
    }
}
