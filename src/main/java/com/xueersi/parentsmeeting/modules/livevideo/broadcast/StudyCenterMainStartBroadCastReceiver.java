package com.xueersi.parentsmeeting.modules.livevideo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;

public class StudyCenterMainStartBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CoursewarePreload coursewarePreload = new CoursewarePreload(context, "", -1);
        coursewarePreload.getCoursewareInfo();

    }
}
