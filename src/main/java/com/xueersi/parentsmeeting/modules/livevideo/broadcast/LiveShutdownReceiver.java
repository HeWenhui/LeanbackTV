package com.xueersi.parentsmeeting.modules.livevideo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

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

public class LiveShutdownReceiver extends BroadcastReceiver {
    private Logger logger = LoggerFactory.getLogger("LiveShutdownReceiver");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            LiveCrashReport.postCatchedException(new Exception("" + android.os.Process.myPid()));
            logger.d("onReceive:process=" + BaseApplication.getCurProcessName(context));
            File alldir = LiveCacheFile.geCacheFile(ContextManager.getContext(), "livelog/shutdown");
            if (!alldir.exists()) {
                alldir.mkdirs();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
            try {
                String s = dateFormat.format(new Date());
                String[] ss = s.split(",");
                String path = new File(alldir, ss[0] + "-" + android.os.Process.myPid() + ".txt").getPath();
                writeLogcat(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
