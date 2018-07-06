package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
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
    public LiveBll2 liveBll2;
    public static AuditClassLiveBll auditClassLiveBll;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    public LogToFile(String tag, File file) {
        this.TAG = "L:" + tag;
        this.path = file.getPath();
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public LogToFile(LiveBll2 liveBll2, String tag, File file) {
        this.TAG = "L:" + tag;
        this.path = file.getPath();
        File parent = file.getParentFile();
        this.liveBll2 = liveBll2;
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    public LogToFile(Context context, String tag, File file) {
        this.TAG = "L:" + tag;
        this.path = file.getPath();
        File parent = file.getParentFile();
        liveBll2 = ProxUtil.getProxUtil().get(context, LiveBll2.class);
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
            liveBll.getOnloadLogs(TAG, TAG + "**" + message);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG, TAG + "**" + message);
            } else {
                liveBll2.getOnloadLogs(TAG, TAG + "**" + message);
            }
            ;
        }
//        if (BuildConfig.DEBUG) {
//            new Thread(new WriteThread(message)).start();
//        }
    }

    public void d(String message) {
        Loger.i(TAG, message);
        if (liveBll != null) {
            liveBll.getOnloadLogs(TAG, TAG + "**" + message);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG, TAG + "**" + message);
            }
        }
//        if (BuildConfig.DEBUG) {
//            new Thread(new WriteThread(message)).start();
//        }
    }

    public void e(String message, Throwable e) {
        Loger.i(TAG, message, e);
        if (liveBll != null) {
            liveBll.getOnloadLogs(TAG, TAG + "**" + message);
        } else {
            if (auditClassLiveBll != null) {
                auditClassLiveBll.getOnloadLogs(TAG, TAG + "**" + message);
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
