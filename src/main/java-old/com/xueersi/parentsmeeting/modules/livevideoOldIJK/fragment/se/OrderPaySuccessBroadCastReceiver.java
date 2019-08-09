package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xueersi.common.event.AppEvent;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import org.greenrobot.eventbus.EventBus;

public class OrderPaySuccessBroadCastReceiver extends BroadcastReceiver {
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.i("购买课程成功");
        EventBus.getDefault().post(new AppEvent.OnPaySuccessEvent());
    }

}
