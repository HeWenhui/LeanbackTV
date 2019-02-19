package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 直播日志
 *
 * @author linyuqiang
 * created  at 2019/1/14 17:33
 */
public class LiveLog implements LiveOnLineLogs {
    private Logger logger = LoggerFactory.getLogger("LiveLog");
    private static SimpleDateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
    }

    private Context mContext;
    private LiveGetInfo mGetInfo;
    private int mLiveType;
    /** 直播id */
    private String mLiveId;
    /** 直播的类型。直播，讲座 */
    private String type;
    /** 播放的类型。直播，回放，旁听 */
    private String getPrefix;
    /** 进直播的时候，生成一次 */
    private String tid;
    /** 进直播的次数，内存中 */
    private int times;
    private ArrayList<PerGetInfoLog> msg = new ArrayList<>();
    /** 进直播的次数，内存中 */
    public static int LIVE_TIME = 0;
    /** 日志顺序 */
    private int logIndex = 0;
    private LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private File logFile;

    public LiveLog(Context mContext, int mLiveType, String mLiveId, String getPrefix) {
        this.mContext = mContext;
        this.mLiveType = mLiveType;
        this.mLiveId = mLiveId;
        this.getPrefix = getPrefix;
        type = "a" + mLiveType;
        tid = "" + System.currentTimeMillis();
        times = LIVE_TIME++;
        File logDir = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + mLiveId + "-" + getPrefix);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        logFile = new File(logDir, tid + ".txt");
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
        logger.d("setmGetInfo:msg=" + msg.size());
        while (!msg.isEmpty()) {
            PerGetInfoLog perGetInfoLog = msg.remove(0);
            getOnloadLogs(perGetInfoLog.TAG, perGetInfoLog.str);
        }
    }

    @Override
    public void getOnloadLogs(String TAG, String str) {
        if (mGetInfo == null) {
            PerGetInfoLog perGetInfoLog = new PerGetInfoLog();
            perGetInfoLog.TAG = TAG;
            perGetInfoLog.str = str;
            msg.add(perGetInfoLog);
            return;
        }
//        LiveLogCallback liveLogCallback = new LiveLogCallback();
//        RequestParams params = mHttpManager.liveOnloadLogs(mGetInfo.getClientLog(), type, mLiveId, mGetInfo.getUname(), enstuId,
//                mGetInfo.getStuId(), mGetInfo.getTeacherId(), mFileName, str, bz, liveLogCallback);
//        liveLogCallback.setParams(params);
        StableLogHashMap logHashMap = new StableLogHashMap();
        logHashMap.put("tag", "" + TAG);
        logHashMap.put("tid", "" + tid);
        logHashMap.put("times", "" + times);
        logHashMap.put("str", "" + str);
        logHashMap.put("prefix", "" + getPrefix);
        logHashMap.put("liveid", "" + mLiveId);
        logHashMap.put("type", "" + type);
        logHashMap.put("logindex", "" + logIndex++);
        logHashMap.put("teacherId", "" + mGetInfo.getTeacherId());
        UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_VIDEO_LIVEMSG, logHashMap.getData());
        if (AppConfig.DEBUG) {
            liveThreadPoolExecutor.execute(new WriteThread(TAG + "-" + str));
        }
    }

    @Override
    public void getOnloadLogs(String TAG, String str, Throwable e) {
        if (mGetInfo == null) {
            PerGetInfoLog perGetInfoLog = new PerGetInfoLog();
            perGetInfoLog.TAG = TAG;
            perGetInfoLog.str = str;
            msg.add(perGetInfoLog);
            return;
        }
//        LiveLogCallback liveLogCallback = new LiveLogCallback();
//        RequestParams params = mHttpManager.liveOnloadLogs(mGetInfo.getClientLog(), type, mLiveId, mGetInfo.getUname(), enstuId,
//                mGetInfo.getStuId(), mGetInfo.getTeacherId(), mFileName, str, bz, liveLogCallback);
//        liveLogCallback.setParams(params);
        StableLogHashMap logHashMap = new StableLogHashMap();
        logHashMap.put("tag", "" + TAG);
        logHashMap.put("tid", "" + tid);
        logHashMap.put("times", "" + times);
        logHashMap.put("str", "" + str);
        logHashMap.put("prefix", "" + getPrefix);
        logHashMap.put("liveid", "" + mLiveId);
        logHashMap.put("type", "" + type);
        logHashMap.put("logindex", "" + logIndex++);
        logHashMap.put("throwable", "" + Log.getStackTraceString(e));
        logHashMap.put("teacherId", "" + mGetInfo.getTeacherId());
        UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_VIDEO_LIVEMSG, logHashMap.getData());
        if (AppConfig.DEBUG) {
            liveThreadPoolExecutor.execute(new WriteThread(TAG + "-" + str));
        }
    }

    @Override
    public void saveOnloadLogs(String TAG, String str) {
        if (AppConfig.DEBUG) {
            liveThreadPoolExecutor.execute(new WriteThread(TAG + "-" + str));
        }
    }

    @Override
    public void saveOnloadLogs(String TAG, String str, Throwable e) {
        if (AppConfig.DEBUG) {
            liveThreadPoolExecutor.execute(new WriteThread(TAG + "-" + str, e));
        }
    }

    class PerGetInfoLog {
        String TAG;
        String str;
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
                FileOutputStream os = new FileOutputStream(logFile, true);
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
