package com.xueersi.parentsmeeting.widget;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xueersi.parentsmeeting.modules.livevideo.liveLog.LiveLogBill;

public class LiveNetCheckTip {

    public LiveNetCheckTip() {

    }

    /**
     * 阻塞的开始时间戳
     */
    private long stuckStartTimestamp = 0;
    /**
     * 阻塞的次数
     */
    private long stuckCount = 0;
    /**
     * 上次吐司提示的时间戳
     */
    private long tipShowTimestamp;

    private final String tips = "当前网络状态不佳 您可以到我的-设置-网络检测功能检测网络问题";

    /**
     * 网络自检提示显示控制
     */
    public void showTips(Context context) {
        if (context == null) {
            return;
        }
        long curTimeStamp = System.currentTimeMillis();
        if (stuckCount == 0) {
            stuckStartTimestamp = curTimeStamp;
        }
        if (curTimeStamp - stuckStartTimestamp > 60 * 1000) {
            stuckCount = 0;
            stuckStartTimestamp = curTimeStamp;
        }
        if (stuckCount == 1 && curTimeStamp - stuckStartTimestamp <= 60 * 1000
                && curTimeStamp - tipShowTimestamp >= 5 * 60 * 1000) {
            tipShowTimestamp = System.currentTimeMillis();
            Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
            if (toast!=null) {
                toast.setText(tips);
                toast.show();
            }
            stuckCount = 0;
            //日志发送
            LiveLogBill.getInstance().sendStuckLog();
        } else {
            stuckCount = 1;
        }
    }
}
