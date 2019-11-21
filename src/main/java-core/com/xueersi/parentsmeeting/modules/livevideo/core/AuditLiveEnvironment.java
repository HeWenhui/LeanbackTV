package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

public class AuditLiveEnvironment implements LiveEnvironment {
    Activity activity;
    LiveGetInfo liveGetInfo;
    boolean isBigLive;

    public AuditLiveEnvironment(Activity activity) {
        this.activity = activity;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setBigLive(boolean bigLive) {
        isBigLive = bigLive;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public LiveGetInfo getLiveGetInfo() {
        return liveGetInfo;
    }

    @Override
    public boolean isExper() {
        return false;
    }

    @Override
    public boolean isBack() {
        return false;
    }

    @Override
    public boolean isBigLive() {
        return isBigLive;
    }

    @Override
    public LiveAndBackDebug getLiveAndBackDebug() {
        return ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
    }

    @Override
    public LogToFile createLogToFile(String TAG) {
        return new LogToFile(activity,TAG);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return ProxUtil.getProxUtil().get(activity, clazz);
    }
}
