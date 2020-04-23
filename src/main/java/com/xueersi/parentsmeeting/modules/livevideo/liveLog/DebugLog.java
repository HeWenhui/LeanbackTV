package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import android.util.Log;

/**
 * @ClassName DebugLog
 * @Description TODO
 * @Author lizheng
 * @Date 2019-12-04 20:49
 * @Version 1.0
 */
public class DebugLog {
    public static final boolean LOG = false;

    public static void log(String log) {
        if (LOG) {
            Log.e("liveBusLog", log);
        }

    }
}
