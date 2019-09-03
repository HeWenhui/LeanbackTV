package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import android.os.Process;

import com.google.gson.Gson;
import com.hwl.log.LogConfig;
import com.hwl.log.xrsLog.UpdateParamInterface;
import com.hwl.log.xrsLog.XrsLogPublicParam;
import com.hwl.logan.Logan;
import com.hwl.logan.LoganConfig;
import com.hwl.logan.SendLogRunnable;
import com.xueersi.common.logerhelper.matrix.ApmBill;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class LiveLog {

    private static XrsLogPublicParam param;
    private static long time;
    private static SendLogRunnable mSendLogRunnable;
    public static UpdateParamInterface updateInterface;
    public static long index;
    public Gson gson = new Gson();
    public static int processIndex;
    public static int tt;
    public static Logan mLogan;
    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        public void run() {
            sendLog();
        }
    };

    public static Timer timer_log = new Timer();
    public static TimerTask task_log = new TimerTask() {
        public void run() {
            LiveLogEntity log = new LiveLogEntity();
            if (LiveLogBill.param != null) {
                log.liveid = LiveLogBill.param.liveid;
            }
            ApmBill.GetNetIp(LiveLogBill.url);
            log(log);

        }
    };


    public LiveLog() {
    }

    public static Logan getLoganInstance() {
        if (mLogan == null) {
            mLogan = new Logan();
        }

        return mLogan;
    }

    public static void log(LiveLogEntity log) {
        if (getUpParamInterface() != null) {
            param.logIndexId = ++index;
            param.processId = Process.myPid();
        }

        getLoganInstance().wObject(log, 7);
    }


    public static void log(int type, Object log) {
        getLoganInstance().wObject(log, type);
    }

    public static void flushLog() {
        getLoganInstance().f();
    }

    public static void init(long upDateTime, SendLogRunnable sendLogRunnable) {
        time = upDateTime;
        mSendLogRunnable = sendLogRunnable;
        ++processIndex;
    }

    public static void init(LogConfig logConfig, SendLogRunnable sendLogRunnable, int loopTime) {

        LoganConfig config = (new LoganConfig.Builder()).setCachePath(logConfig.mCachePath)
                .setPath(logConfig.mPathPath)
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .setDay(logConfig.mDay)
                .setMaxFile(logConfig.mMaxFile)
                .setMinSDCard(logConfig.mMinSDCard).build();

        getLoganInstance().init(config);
        ++processIndex;
        mSendLogRunnable = sendLogRunnable;
        tt = loopTime;
    }

    public static void setParam(XrsLogPublicParam publicParam) {
        param = publicParam;
    }

    public static void setUpParamInterface(UpdateParamInterface update) {
        updateInterface = update;
    }

    public static XrsLogPublicParam getUpParamInterface() {
        if (updateInterface != null) {
            param = updateInterface.getXrsLogPublicParam();
        }
        return param;
    }

    public static void SendLogRunnable(SendLogRunnable sendLogRunnabl) {
        mSendLogRunnable = sendLogRunnabl;
    }

    public static void sendLog() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String d = dataFormat.format(new Date(System.currentTimeMillis()));
        String[] temp = new String[]{d};
        getLoganInstance().s(temp, mSendLogRunnable);
    }

    public static void stopLog() {
        timer.cancel();
        task.cancel();

        timer_log.cancel();
        task_log.cancel();
    }

    public static void startLog() {
        timer.schedule(task, 5000, tt * 1000);
        timer_log.schedule(task_log, 2000, tt * 1000);
    }

    /**
     * 默认设置
     */
    public static void defaultLog() {

        LiveLogEntity log = new LiveLogEntity();
        if (LiveLogBill.param != null) {
            log.liveid = LiveLogBill.param.liveid;
        }
        ApmBill.GetNetIp(LiveLogBill.url);
        log(log);
        sendLog();
    }


    public static Map<String, Long> getAllFilesInfo() {
        return getLoganInstance().getAllFilesInfo();
    }
}
