package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;

public class LiveErrorEventBus {
    protected Logger logger = LoggerFactory.getLogger("LiveErrorEventBus");
    private Context context;

    public LiveErrorEventBus(Context context) {
        this.context = context;
        EventBus eventBus = getDefault(context);
        eventBus.register(this);
        eventBus.post(new SubscriberExceptionEvent(eventBus, new Exception(), "1", "2"));
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMainThread(SubscriberExceptionEvent event) {
        logger.d("onEventMainThread:SubscriberExceptionEvent=" + event.causingEvent + ",Subscriber=" + event.causingSubscriber, event.throwable);
        CrashReport.postCatchedException(new Exception(event.causingEvent + "&&" + event.causingSubscriber, event.throwable));
    }

    public static EventBus getDefault(Context context) {
        EventBus.getDefault();
        EventBus eventBus = ProxUtil.getProxUtil().get(context, EventBus.class);
        if (eventBus == null) {
//            eventBus = EventBus.builder().build();
            eventBus = new EventBus() {
                @Override
                public void register(Object subscriber) {
                    super.register(subscriber);
                }

                @Override
                public synchronized void unregister(Object subscriber) {
                    super.unregister(subscriber);
                }
            };
            ProxUtil.getProxUtil().put(context, EventBus.class, eventBus);
        }
        return eventBus;
    }

    public void destory() {
        EventBus eventBus = getDefault(context);
        eventBus.unregister(this);
    }
}
