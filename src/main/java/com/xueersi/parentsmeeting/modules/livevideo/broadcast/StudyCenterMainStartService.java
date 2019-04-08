package com.xueersi.parentsmeeting.modules.livevideo.broadcast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

/**
 * 学习中心启动广播
 */
public class StudyCenterMainStartService extends Service {
    private Logger logger = LiveLoggerFactory.getLogger("StudyCenterMainStartService");

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.i("onStartCommand:startId=" + startId);
        CoursewarePreload coursewarePreload = new CoursewarePreload(this, -1);
        coursewarePreload.setmHttpManager(new LiveHttpManager(this));
        coursewarePreload.getCoursewareInfo("");
        StableLogHashMap logHashMap = new StableLogHashMap("onStartCommand");
        logHashMap.put("startId", "" + startId);
        UmsAgentManager.umsAgentDebug(this, LiveVideoConfig.LIVE_PRESERVICE_START, logHashMap.getData());
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
