package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.FILTER_ACTION;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.intelligent_recognition_sign;

public class IntelligentRecognitionBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (FILTER_ACTION.equals(action)) {
            if (ircReceiver != null) {
                ircReceiver.stop(intent.getStringExtra(intelligent_recognition_sign));
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
