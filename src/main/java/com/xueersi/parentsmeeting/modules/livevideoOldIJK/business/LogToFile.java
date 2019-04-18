package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogToFile {
    String TAG;
    private static SimpleDateFormat dateFormat;
    /** 静态唯一 */
    public static LiveOnLineLogs auditClassLiveBll;
    public LiveOnLineLogs liveOnLineLogs;
    protected Logger logger = LoggerFactory.getLogger("LogToFile");

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    public LogToFile(String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = "OL:" + tag;
        if (auditClassLiveBll != null) {
            liveOnLineLogs = auditClassLiveBll;
        }
    }

    public LogToFile(String tag, LiveOnLineLogs liveOnLineLogs) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        this.liveOnLineLogs = liveOnLineLogs;
    }

    public void setLiveOnLineLogs(LiveOnLineLogs liveOnLineLogs) {
        this.liveOnLineLogs = liveOnLineLogs;
    }

    public LogToFile(LiveOnLineLogs liveBll2, String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        liveOnLineLogs = liveBll2;
    }

    public LogToFile(Context context, String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        liveOnLineLogs = ProxUtil.getProxUtil().get(context, LiveOnLineLogs.class);
    }

    @Deprecated
    public void clear() {
//        if (new File(path).exists()) {
//            new File(path).delete();
//        }
    }

    public void i(String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, message);
        }
        logger.i(message);
//        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void d(String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, message);
        }
        logger.d(message);
//        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void debugSave(String message) {
        logger.i(message);
        if (liveOnLineLogs != null) {
            liveOnLineLogs.saveOnloadLogs(TAG, message);
        }
    }

    public void e(String message, Throwable e) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, message, e);
        }
        logger.e(message, e);
    }
}