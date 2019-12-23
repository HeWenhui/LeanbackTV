package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import org.greenrobot.eventbus.EventBus;

public class UserGoldTotal {

    public static String goldNum;
    public static long goldNumTime;

    public static void requestGoldTotal(Context mContext) {
        long time = System.currentTimeMillis() - goldNumTime;
        Loger.d("LiveIRCMessageBll", "requestGoldTotal:goldNum=" + goldNum + ",time=" + time);
        if (AppBll.getInstance().isAlreadyLogin()){
            if (goldNum == null || time > 120000) {
                OtherModulesEnter.requestGoldTotal(mContext);
            } else {
                LiveMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AppEvent.OnGetGoldUpdateEvent event = new AppEvent.OnGetGoldUpdateEvent(goldNum);
                        EventBus.getDefault().post(event);
                    }
                });
            }
        }

    }
}
