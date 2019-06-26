package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by linyuqiang on 2018/1/16.
 */

public interface ActivityChangeLand extends LiveProvide{
    void setAutoOrientation(boolean isAutoOrientation);

    @Deprecated
    void setRequestedOrientation(int requestedOrientation);

    void changeLOrP();
}
