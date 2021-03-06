package com.xueersi.parentsmeeting.modules.livevideo.broadcast;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * 学习中心启动广播
 */
public class StudyCenterMainStartService extends Service {
    private String TAG = "StudyCenterMainStartService";
    private Logger logger = LiveLoggerFactory.getLogger("StudyCenterMainStartService");

    @Override
    public void onCreate() {
        super.onCreate();
        loadClass();
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

    private void loadClass() {
        long before = System.currentTimeMillis();
        ArrayList<BllConfigEntity> bllConfigEntities = AllBllConfig.getLiveBusinessScience(null);
        bllConfigEntities.addAll(AllBllConfig.getLiveBusinessArts());
        bllConfigEntities.addAll(AllBllConfig.getLiveBusinessCn());
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            String className = "";
            try {
                BllConfigEntity bllConfigEntity = bllConfigEntities.get(i);
                className = bllConfigEntity.className;
                Class<?> c = Class.forName(className);
                Class<? extends LiveBaseBll> clazz;
                if (BusinessCreat.class.isAssignableFrom(c)) {
                    Class<? extends BusinessCreat> creatClazz = (Class<? extends BusinessCreat>) c;
                    BusinessCreat businessCreat = creatClazz.newInstance();
                    Class[] classes = businessCreat.reloadClass();
                    if (classes != null) {
                        for (int j = 0; j < classes.length; j++) {
                            Class clazz2 = classes[j];
                            Constructor<? extends LiveBaseBll> constructor = clazz2.getConstructor(new Class[]{Activity.class, LiveBll2.class});
                            logger.d("loadClass:clazz2=" + clazz2);
                        }
                    }
                    continue;
                } else if (LiveBaseBll.class.isAssignableFrom(c)) {
                    clazz = (Class<? extends LiveBaseBll>) c;
                } else {
                    continue;
                }
                Constructor<? extends LiveBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBll2.class});
            } catch (Exception e) {
                logger.e("loadClass:className=" + className, e);
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        logger.d("loadClass:time=" + (System.currentTimeMillis() - before));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
