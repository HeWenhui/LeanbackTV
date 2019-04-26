package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

/**
 * Created by lyqai on 2018/1/16.
 */

public interface ActivityChangeLand {
    void setAutoOrientation(boolean isAutoOrientation);

    @Deprecated
    void setRequestedOrientation(int requestedOrientation);

    void changeLOrP();
}
