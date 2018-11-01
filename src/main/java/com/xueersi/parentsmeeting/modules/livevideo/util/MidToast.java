package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class MidToast {
    private static Toast mToast;

    public static void showToast(final Context content, final String text) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToast(content, text);
                }
            });
        } else {
            if (mToast == null) {
                mToast = Toast.makeText(content, text, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }

    }
}
