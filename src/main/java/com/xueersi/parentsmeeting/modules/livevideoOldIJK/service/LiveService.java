package com.xueersi.parentsmeeting.modules.livevideoOldIJK.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by linyuqiang on 2018/7/14.
 * 直播守护进程
 */
public class LiveService extends Service {
    String TAG = "LiveService";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
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
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos;
            try {
                //bugly 2555
                runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
                stopSelf();
                return;
            }
            //bugly 2053
            if (runningAppProcessInfos != null) {
                for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                    if (appProcess.pid == livepid) {
                        isAlive = true;
                        logger.d("onCreate:appProcess=" + appProcess.processName);
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
            } else {
                handler.postDelayed(this, 15000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        logger.d("onCreate");
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
        alldir = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/cache");
        if (!alldir.exists()) {
            alldir.mkdirs();
        }
        File[] files = alldir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                File uploadfile = new File(file.getParentFile(), "upload" + file.getName());
                if (!file.getName().contains("upload")) {
                    List<String> stringList = FileUtils.readFile2List(file, "utf-8");
                    if (stringList == null) {
                        continue;
                    }
                    boolean start = false;
                    List<String> nativeList = new ArrayList<>();
                    for (int j = stringList.size() - 1; j >= 0; j--) {
                        String string = stringList.get(j);
                        if (string.contains("Fatal signal")) {
                            start = true;
                        }
                        if (start) {
                            nativeList.add(string);
                            logger.d("onCreate:string=" + string);
                            if (nativeList.size() > 20) {
                                break;
                            }
                        }
                    }
                    if (!nativeList.isEmpty()) {
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        for (int j = nativeList.size() - 1; j >= 0; j--) {
                            String string = nativeList.get(j);
                            jsonArray.put(string);
                        }
                        try {
                            jsonObject.put("eventid2", "livevideo_crash");
                            jsonObject.put("message", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Exception exception = new Exception(jsonObject.toString());
                        FileLogger.writeExceptionLog(exception);
                    }
                    file.renameTo(uploadfile);
                }
            }
        }
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        System.exit(0);
        logger.d("onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        livepid = intent.getIntExtra("livepid", 0);
        return START_NOT_STICKY;
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
                bw.write(line);
                bw.newLine();
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
