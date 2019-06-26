package com.xueersi.parentsmeeting.modules.livevideo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

/**
 * 学习中心启动广播
 */
public class StudyCenterMainStartBroadCastReceiver extends BroadcastReceiver {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public void onReceive(Context context, Intent intent) {
        CoursewarePreload coursewarePreload = new CoursewarePreload(context, -1);
        logger.i("receive broadcast");
        coursewarePreload.setmHttpManager(new LiveHttpManager(context));
        coursewarePreload.getCoursewareInfo("");
    }
}
