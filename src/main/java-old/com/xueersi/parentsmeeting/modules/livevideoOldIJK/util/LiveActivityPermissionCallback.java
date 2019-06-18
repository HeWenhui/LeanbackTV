package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import com.xueersi.common.permission.ActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LivePermissionActivity;

/**
 * Created by lyqai on 2018/8/2.
 */

public abstract class LiveActivityPermissionCallback extends ActivityPermissionCallback {
    public String getClassName() {
        return LivePermissionActivity.class.getName();
    }
}
