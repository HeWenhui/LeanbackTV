package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.config.AppConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveOnLineLogs;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
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
    public static LiveBll liveBll;
    public static AuditClassLiveBll auditClassLiveBll;
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    public static int LIVE_TIME = 0;
    public LiveOnLineLogs liveOnLineLogs;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    public LogToFile(String tag) {
        this.TAG = "OL:" + tag + ":" + LIVE_TIME;
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
        if (liveBll != null) {
            liveOnLineLogs = liveBll;
        } else if (auditClassLiveBll != null) {
            liveOnLineLogs = auditClassLiveBll;
        }
    }

    public LogToFile(String tag, LiveOnLineLogs liveOnLineLogs) {
        this.TAG = tag + ":" + LIVE_TIME;
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
        this.TAG = tag + ":" + LIVE_TIME;
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
        this.TAG = tag + ":" + LIVE_TIME;
        File file = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + tag + ".txt");
        this.path = file.getPath();
        File parent = file.getParentFile();
        liveOnLineLogs = ProxUtil.getProxUtil().get(context, LiveOnLineLogs.class);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.delete();
    }

    public void clear() {
        if (new File(path).exists()) {
            new File(path).delete();
        }
    }

    public void i(String message) {
        String getPrefix = "";
        if (liveOnLineLogs != null) {
            getPrefix = liveOnLineLogs.getPrefix();
            liveOnLineLogs.getOnloadLogs(TAG, getPrefix + ":" + TAG + "**" + message);
        }
        Loger.i(getPrefix + ":" + TAG, message);
//        if (AppConfig.DEBUG) {
//            liveThreadPoolExecutor.execute(new WriteThread(message));
//        }
        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void d(String message) {
        String getPrefix = "";
        if (liveOnLineLogs != null) {
            getPrefix = liveOnLineLogs.getPrefix();
            liveOnLineLogs.getOnloadLogs(TAG, getPrefix + ":" + TAG + "**" + message);
        }
        Loger.i(getPrefix + ":" + TAG, message);
//        if (AppConfig.DEBUG) {
//            liveThreadPoolExecutor.execute(new WriteThread(message));
//        }
        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void debugSave(String message) {
        Loger.i(TAG, message);
//        if (AppConfig.DEBUG) {
//            liveThreadPoolExecutor.execute(new WriteThread(message));
//        }
        liveThreadPoolExecutor.execute(new WriteThread(message));
    }

    public void e(String message, Throwable e) {
        String getPrefix = "";
        if (liveOnLineLogs != null) {
            getPrefix = liveOnLineLogs.getPrefix();
            liveOnLineLogs.getOnloadLogs(TAG, getPrefix + ":" + TAG + "**" + message + "**" + e);
        }
        Loger.i(getPrefix + ":" + TAG, message, e);
        liveThreadPoolExecutor.execute(new WriteThread(message, e));
    }

    class WriteThread implements Runnable {
        private String message;
        Throwable e;

        public WriteThread(String message) {
            this.message = message;
        }

        public WriteThread(String message, Throwable e) {
            this.message = message;
            this.e = e;
        }

        @Override
        public void run() {
            String s = dateFormat.format(new Date());
            String[] ss = s.split(",");
            try {
                FileOutputStream os = new FileOutputStream(path, true);
                os.write((ss[1] + " message:" + message + "\n").getBytes());
                if (e != null) {
                    if (e instanceof UnknownHostException) {
                        os.write((ss[1] + " errorlog:UnknownHostException\n").getBytes());
                    } else {
                        os.write((ss[1] + " errorlog:" + Log.getStackTraceString(e) + "\n").getBytes());
                    }
                }
                os.close();
            } catch (Exception e) {
            }
        }
    }
}
