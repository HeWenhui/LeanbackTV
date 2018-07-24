package com.xueersi.parentsmeeting.modules.livevideo.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.xutils.xutils.common.util.IOUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lyqai on 2018/7/14.
 */

public class LiveService extends Service {
    String TAG = "LiveService";
    private File alldir;
    int livepid;
    Handler handler = new Handler(Looper.getMainLooper());
    SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            boolean isAlive = false;
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == livepid) {
                    isAlive = true;
                    Loger.d(TAG, "onCreate:appProcess=" + appProcess.processName);
                }
            }
            if (!isAlive) {
                try {
                    String s = dateFormat.format(new Date());
                    String[] ss = s.split(",");
                    String path = new File(alldir, ss[0] + "-" + livepid + ".txt").getPath();
                    writeLogcat(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stopSelf();
            } else {
                handler.postDelayed(this, 10000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Loger.d(TAG, "onCreate");
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
        alldir = new File(BaseApplication.getContext().getExternalCacheDir(), "livelog/log");
        if (alldir == null) {
            alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/livelog/log");
        }
        if (!alldir.exists()) {
            alldir.mkdirs();
        }
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        System.exit(0);
        Loger.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        livepid = intent.getIntExtra("livepid", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    public void writeLogcat(String filename) throws IOException {
        String[] args = {"logcat", "-v", "time", "-d"};

        Process process = Runtime.getRuntime().exec(args);

        InputStreamReader input = new InputStreamReader(process.getInputStream());

        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            return;
        }

        OutputStreamWriter output = new OutputStreamWriter(fileStream);
        BufferedReader br = new BufferedReader(input);
        BufferedWriter bw = new BufferedWriter(output);

        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("" + livepid)) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
        } finally {
            IOUtil.closeQuietly(bw);
            IOUtil.closeQuietly(output);
            IOUtil.closeQuietly(br);
            IOUtil.closeQuietly(input);
        }
    }
}
