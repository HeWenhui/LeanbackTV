package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;

public class IntelligentRecognitionBroadcast extends BroadcastReceiver {
    final String Init = "";
    final String launchIntent = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (FILTER_ACTION.equals(ircReceiver)) {
            if (ircReceiver != null) {
                ircReceiver.stop(intent.getStringExtra(""));
            }
        }
    }

    private IRCReceiver ircReceiver;

    public IRCReceiver getIrcReceiver() {
        return ircReceiver;
    }

    public void setIrcReceiver(IRCReceiver ircReceiver) {
        this.ircReceiver = ircReceiver;
    }

    public static interface IRCReceiver {
        /**
         * 收到irc的stop命令
         */
        void stop(String goldJSON);
    }
}
