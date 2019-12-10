package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SysLogEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogToFile {
    String TAG;
    private static SimpleDateFormat dateFormat;
    public LiveOnLineLogs liveOnLineLogs;
    protected Logger logger = LoggerFactory.getLogger("LogToFile");
    private StableLogHashMap stableLogHashMap;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    public LogToFile(String tag) {
        logger = LiveLoggerFactory.getLogger(tag);
        this.TAG = "OL:" + tag;
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

    /**
     * 一些共有参数
     *
     * @param key
     * @param value
     */
    public void addCommon(String key, String value) {
        if (stableLogHashMap == null) {
            stableLogHashMap = new StableLogHashMap();
        }
        stableLogHashMap.put(key, value);
    }

    public void i(String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, null, stableLogHashMap, message);
        }
        logger.i(message);
//        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void i(SysLogEntity logEntity, String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, logEntity, stableLogHashMap, message);
        }
        logger.i(message);
//        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void d(String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, null, stableLogHashMap, message);
        }
        logger.d(message);
//        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void d(SysLogEntity logEntity, String message) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, logEntity, stableLogHashMap, message);
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
            liveOnLineLogs.getOnloadLogs(TAG, null, stableLogHashMap, message, e);
        }
        logger.e(message, e);
    }

    public void e(SysLogEntity logEntity, String message, Throwable e) {
        if (liveOnLineLogs != null) {
            liveOnLineLogs.getOnloadLogs(TAG, logEntity, stableLogHashMap, message, e);
        }
        logger.e(message, e);
    }
}
