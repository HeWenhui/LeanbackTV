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
 * @author linyuqiang
 * created  at 2019/1/14 17:33
 */
public class LiveLog {
    private Logger logger = LoggerFactory.getLogger("LiveLog");
    private Context mContext;
    private LiveGetInfo mGetInfo;
    private int mLiveType;
    private String mLiveId;
    private String type;
    private String getPrefix;
    private ArrayList<PerGetInfoLog> msg = new ArrayList<>();

    public LiveLog(Context mContext, int mLiveType, String mLiveId, String getPrefix) {
        this.mContext = mContext;
        this.mLiveType = mLiveType;
        this.mLiveId = mLiveId;
        this.getPrefix = getPrefix;
        type = "a" + mLiveType;
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
        logHashMap.put("str", "" + str);
        logHashMap.put("prefix", "" + getPrefix);
        logHashMap.put("liveid", "" + mLiveId);
        logHashMap.put("type", "" + type);
        logHashMap.put("teacherId", "" + mGetInfo.getTeacherId());
        UmsAgentManager.umsAgentDebug(mContext, LogerTag.DEBUG_VIDEO_LIVEMSG, logHashMap.getData());
    }

    class PerGetInfoLog {
        String TAG;
        String str;
    }
}
