package ren.yale.android.cachewebviewlib;

import android.os.Environment;
import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ren.yale.android.cachewebviewlib.config.CacheConfig;
import ren.yale.android.cachewebviewlib.utils.FileUtil;

/**
 * Created by yale on 2017/9/15.
 */

class CacheWebViewLog {
    private static final String TAG = "CacheWebViewFile";

    private static SimpleDateFormat dateFormat;
    public static String path;
    private static File alldir;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
        alldir = new File(BaseApplication.getContext().getExternalCacheDir(), "CacheWebView/log");
        if (alldir == null) {
            alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/CacheWebView/log");
        }
        if (!alldir.exists()) {
            alldir.mkdirs();
        }
    }

    public static void d(String log) {
        if (CacheConfig.getInstance().isDebug()) {
            Loger.d(TAG, log);
            LiveThreadPoolExecutor executor = LiveThreadPoolExecutor.getInstance();
            executor.execute(new WriteThread(TAG + "--!!" + log));
        }
    }

    public static void d(String log, Throwable e) {
        if (CacheConfig.getInstance().isDebug()) {
            Loger.d(TAG, log);
            LiveThreadPoolExecutor executor = LiveThreadPoolExecutor.getInstance();
            executor.execute(new WriteThread(TAG + "--!!" + log, e));
        }
    }

    static class WriteThread implements Runnable {
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
            FileOutputStream os = null;
            try {
                if (path == null) {
                    path = new File(alldir, ss[0] + "-" + android.os.Process.myPid() + ".txt").getPath();
                }
                os = new FileOutputStream(path, true);
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
                if (alldir != null && !alldir.exists()) {
                    alldir.mkdirs();
                }
                Loger.d(TAG, "WriteThread", e);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
