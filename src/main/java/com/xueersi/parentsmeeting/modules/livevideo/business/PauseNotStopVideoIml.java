package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/31.
 */

public class PauseNotStopVideoIml implements PauseNotStopVideoInter {

    /** onPause状态不暂停视频 */
    AtomicBoolean onPauseNotStopVideo = new AtomicBoolean(false);

    public PauseNotStopVideoIml(Activity activity) {
        ProxUtil.getProxUtil().put(activity, PauseNotStopVideoInter.class, this);
    }

    public PauseNotStopVideoIml(Activity activity, AtomicBoolean onPauseNotStopVideo) {
        ProxUtil.getProxUtil().put(activity, PauseNotStopVideoInter.class, this);
        this.onPauseNotStopVideo = onPauseNotStopVideo;
    }

    @Override
    public void setPause(boolean pause) {
        onPauseNotStopVideo.set(true);
    }

    @Override
    public boolean getPause() {
        return onPauseNotStopVideo.get();
    }
}
