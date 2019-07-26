package ren.yale.android.cachewebviewlib;

import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ren.yale.android.cachewebviewlib.config.CacheConfig;

/**
 * Created by yale on 2017/9/15.
 */

class CacheWebViewLog {
    private static final String TAG = "CacheWebViewFile";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    private static SimpleDateFormat dateFormat;
    public static String path;
    private static File alldir;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
        alldir = LiveCacheFile.geCacheFile(ContextManager.getContext(), "CacheWebView/log");
        if (!alldir.exists()) {
            alldir.mkdirs();
        }
    }

    public static void d(String log) {
        if (CacheConfig.getInstance().isDebug()) {
            logger.d( log);
            LiveThreadPoolExecutor executor = LiveThreadPoolExecutor.getInstance();
            executor.execute(new WriteThread(TAG + "--!!" + log));
        }
    }

    public static void d(String log, Throwable e) {
        if (CacheConfig.getInstance().isDebug()) {
            logger.d( log);
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
                logger.d( "WriteThread", e);
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
