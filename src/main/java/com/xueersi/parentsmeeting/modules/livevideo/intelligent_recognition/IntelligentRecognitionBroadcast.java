package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xueersi.common.route.XueErSiRouter;

public class IntelligentRecognitionBroadcast extends BroadcastReceiver {
    final String Init = "";
    final String launchIntent = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Init)) {

        } else if (action.equals(launchIntent)) {
            XueErSiRouter.startModule("/dictation/Launch");
        }
    }
}
