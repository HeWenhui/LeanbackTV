package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by linyuqiang on 2018/7/31.
 */

public interface PauseNotStopVideoInter extends LiveProvide{
    void setPause(boolean pause);

    boolean getPause();
}
