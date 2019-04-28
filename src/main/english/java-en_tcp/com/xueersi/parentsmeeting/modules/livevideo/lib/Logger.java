package com.xueersi.parentsmeeting.modules.livevideo.lib;

import android.util.Log;

/**
 * tcp 暂时的日志，为了在异常的时候被bugly上传
 */
public class Logger {
    String TAG;

    public Logger(String TAG) {
        this.TAG = TAG;
    }

    public void d(String msg) {
        Log.d(TAG, msg);
    }

    public void e(String msg, Throwable e) {
        Log.e(TAG, msg, e);
    }
}
