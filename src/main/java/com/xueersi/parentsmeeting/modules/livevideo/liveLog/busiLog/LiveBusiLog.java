package com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog;

import com.hwl.log.LogConfig;
import com.hwl.log.xrsLog.UpdateParamInterface;
import com.hwl.log.xrsLog.XrsLogPublicParam;
import com.hwl.logan.Logan;
import com.hwl.logan.LoganConfig;
import com.hwl.logan.SendLogRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LiveBusiLog {

    private static XrsLogPublicParam param;
    private static long time;
    private static SendLogRunnable mSendLogRunnable;
    public static UpdateParamInterface updateInterface;
    public static long index;
    public static int tt;
    public static int processIndex;
    public static Logan mLogan;

    public static Timer timer = new Timer();
    public static TimerTask task = new TimerTask() {
        public void run() {

//            LiveBusiLogEntity entity=new LiveBusiLogEntity();
//            entity.logType=0;
//            log(entity);
            sendLog();
        }
    };

    public LiveBusiLog() {
    }

    private static Logan getLoganInstance() {
        if (mLogan == null) {
            mLogan = new Logan();
        }

        return mLogan;
    }


    public static void log(int type, Object log) {
        getLoganInstance().wObject(log, type);
    }

    public static void log(LiveBusiLogEntity entity) {
        getLoganInstance().wObject(entity, 0);
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
        LoganConfig config = (new LoganConfig.Builder()).setCachePath(logConfig.mCachePath).
                setPath(logConfig.mPathPath).setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes()).setDay(logConfig.mDay).
                        setMaxFile(logConfig.mMaxFile).setMinSDCard(logConfig.mMinSDCard).build();
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

    public static void sendLogRunnable(SendLogRunnable sendLogRunnabl) {
        mSendLogRunnable = sendLogRunnabl;
    }

    public static void sendLog() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String d = dataFormat.format(new Date(System.currentTimeMillis()));
        String[] temp = new String[]{d};
        getLoganInstance().s(temp, mSendLogRunnable);
    }

    public static Map<String, Long> getAllFilesInfo() {
        return getLoganInstance().getAllFilesInfo();
    }


    public static void stopLog() {
        timer.cancel();
        task.cancel();

    }

    public static void startLog() {
        timer.schedule(task, 5000, tt * 1000);
    }

}