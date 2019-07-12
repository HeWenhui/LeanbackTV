package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

public interface IntelligentLifecycleObserver extends LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate();
}
