package com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog;

import android.os.Debug;
import android.os.Process;


import com.hwl.log.LogConfig;
import com.hwl.log.xrsLog.UpdateParamInterface;
import com.hwl.log.xrsLog.XrsLogEntity;
import com.hwl.log.xrsLog.XrsLogPublicParam;
import com.hwl.logan.Logan;
import com.hwl.logan.LoganConfig;
import com.hwl.logan.SendLogRunnable;
import com.xrs.bury.ThreadPool;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.DebugLog;

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
            DebugLog.log("task send");

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

        XrsLogEntity outEntity = new XrsLogEntity();
        if (getUpParamInterface() != null) {
            param.logIndexId = ++index;
            param.processId = Process.myPid();
            outEntity.pp = param;
        }
        outEntity.data = JsonUtil.toJson(entity);
        outEntity.type = entity.logType;
        getLoganInstance().wObject(outEntity, 0);
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

    public synchronized static void sendLog() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String d = dataFormat.format(new Date(System.currentTimeMillis()));
        final String[] temp = new String[]{d};
        ThreadPool.execSingle(new Runnable() {
            @Override
            public void run() {
                getLoganInstance().s(temp, mSendLogRunnable);
            }
        });

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