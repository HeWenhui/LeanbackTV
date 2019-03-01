package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.ArrayList;

/**
 * 直播日志
 *
 * @author linyuqiang
 * created  at 2019/1/14 17:33
 */
public class LiveLog {
    private Logger logger = LoggerFactory.getLogger("LiveLog");
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

    public LiveLog(Context mContext, int mLiveType, String mLiveId, String getPrefix) {
        this.mContext = mContext;
        this.mLiveType = mLiveType;
        this.mLiveId = mLiveId;
        this.getPrefix = getPrefix;
        type = "a" + mLiveType;
        tid = "" + System.currentTimeMillis();
        times = LIVE_TIME++;
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
        logger.d("setmGetInfo:msg=" + msg.size());
        while (!msg.isEmpty()) {
            PerGetInfoLog perGetInfoLog = msg.remove(0);
            getOnloadLogs(perGetInfoLog.TAG, perGetInfoLog.str);
        }
    }

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
    }

    class PerGetInfoLog {
        String TAG;
        String str;
    }
}
