package com.xueersi.parentsmeeting.modules.livevideo.achievement.entity;

import com.xueersi.lib.framework.entity.BaseEvent;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/6/25 下午4:00
 */

public class UpdateAchievementEvent extends BaseEvent {

    private String mLiveId;


    public UpdateAchievementEvent(String mLiveId) {
        this.mLiveId = mLiveId;
    }

    public String getmLiveId() {
        return mLiveId;
    }

    public void setmLiveId(String mLiveId) {
        this.mLiveId = mLiveId;
    }
}
