package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.util.Log;

import com.xueersi.xesalib.utils.log.Loger;

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

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    public LogToFile(String tag, String path) {
        this.TAG = "LogToFile:" + tag;
        this.path = path;
        File parent = new File(path).getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public LogToFile(String tag, File file) {
        this.TAG = "L:" + tag;
        this.path = file.getPath();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public void clear() {
        if (new File(path).exists()) {
            new File(path).delete();
        }
    }

    public void i(String message) {
        Loger.i(TAG, message);
        if (liveBll != null) {
            liveBll.getOnloadLogs(TAG + "**" + message);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG + "**" + message);
            }
        }
//        if (BuildConfig.DEBUG) {
//            new Thread(new WriteThread(message)).start();
//        }
    }

    public void d(String message) {
        Loger.i(TAG, message);
        if (liveBll != null) {
            liveBll.getOnloadLogs(TAG + "**" + message);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG + "**" + message);
            }
        }
//        if (BuildConfig.DEBUG) {
//            new Thread(new WriteThread(message)).start();
//        }
    }

    public void e(String message, Throwable e) {
        Loger.i(TAG, message, e);
        if (liveBll != null) {
            liveBll.getOnloadLogs(TAG + "**" + message + "$$" + e);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG + "**" + message);
            }
        }
//        new Thread(new WriteThread(message, e)).start();
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
