package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.event.AppEvent;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;

import org.greenrobot.eventbus.EventBus;

public class UserGoldTotal {

    public static String goldNum;
    public static long goldNumTime;

    public static void requestGoldTotal(Context mContext) {
        long time = System.currentTimeMillis() - goldNumTime;
        Loger.d("LiveIRCMessageBll", "requestGoldTotal:goldNum=" + goldNum + ",time=" + time);
        if (goldNum == null || time > 120000) {
            OtherModulesEnter.requestGoldTotal(mContext);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppEvent.OnGetGoldUpdateEvent event = new AppEvent.OnGetGoldUpdateEvent(goldNum);
                    EventBus.getDefault().post(event);
                }
            });
        }
    }
}
