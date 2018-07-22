package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Handler;
import android.os.Looper;

import org.xutils.xutils.common.Callback;
import org.xutils.xutils.http.RequestParams;
import org.xutils.xutils.x;

import java.io.File;

public class LiveLogCallback implements Callback.CommonCallback<File> {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    RequestParams params;
    int tyrCount = 1;
    LiveLogCallback liveLogCallback = this;

    @Override
    public void onError(Throwable throwable, boolean b) {
        if (params != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    x.http().get(params, liveLogCallback);
                }
            }, tyrCount * 2000);
            if (tyrCount < 15) {
                tyrCount++;
            }
        }
    }

    @Override
    public void onSuccess(File file) {

    }

    @Override
    public void onCancelled(CancelledException e) {

    }

    @Override
    public void onFinished() {

    }
}
