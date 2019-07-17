package com.xueersi.parentsmeeting.modules.livevideo.core;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * 直播崩溃日志
 *
 * @author linyuqiang
 * created  at 2019/7/9 17:32
 */
public class LiveCrashReport {
    public static void postCatchedException(String TAG, Throwable var0) {
        LiveCrashReport.postCatchedException(new LiveException(TAG, var0));
    }

    public static void postCatchedException(Throwable var0) {
        CrashReport.postCatchedException(var0);
    }
}
