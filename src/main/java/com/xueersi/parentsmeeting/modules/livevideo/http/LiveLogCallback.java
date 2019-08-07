package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import org.xutils.xutils.common.Callback;
import org.xutils.xutils.http.RequestParams;
import org.xutils.xutils.x;

import java.io.File;

/**
 * 直播日志重传
 *
 * @author linyuqiang
 */
public class LiveLogCallback implements Callback.CommonCallback<File> {
    private Handler mHandler = LiveMainHandler.getMainHandler();
    RequestParams params;
    int tyrCount = 1;
    LiveLogCallback liveLogCallback = this;

    @Override
    public void onError(Throwable throwable, boolean b) {
        if (params != null && tyrCount < 20) {
            long delayMillis = tyrCount * 1500;
            if (tyrCount > 15) {
                delayMillis = 15 * 1500;
            }
            tyrCount++;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    x.http().get(params, liveLogCallback);
                }
            }, delayMillis);
        }
    }

    public void setParams(RequestParams params) {
        this.params = params;
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
