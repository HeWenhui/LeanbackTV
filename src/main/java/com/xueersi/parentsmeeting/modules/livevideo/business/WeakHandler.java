package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * handler的弱引用
 * Created by linyuqiang on 2016/4/15.
 */
public class WeakHandler extends Handler {
    private WeakReference<Handler.Callback> reference;

    public WeakHandler(Handler.Callback callback) {
        reference = new WeakReference<Callback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        Handler.Callback callback = reference.get();
        if(callback!=null){
            callback.handleMessage(msg);
        }
    }
}
