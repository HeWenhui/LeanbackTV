package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogToFile {
    String TAG;
    String path;
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
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
        if (auditClassLiveBll != null) {
            liveOnLineLogs = auditClassLiveBll;
        }
    }

    public LogToFile(String tag, LiveOnLineLogs liveOnLineLogs) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
        this.liveOnLineLogs = liveOnLineLogs;
    }

    public void setLiveOnLineLogs(LiveOnLineLogs liveOnLineLogs) {
        this.liveOnLineLogs = liveOnLineLogs;
    }

    public LogToFile(LiveOnLineLogs liveBll2, String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        liveOnLineLogs = liveBll2;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
    }

    public LogToFile(Context context, String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = tag + "";
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        liveOnLineLogs = ProxUtil.getProxUtil().get(context, LiveOnLineLogs.class);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
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
