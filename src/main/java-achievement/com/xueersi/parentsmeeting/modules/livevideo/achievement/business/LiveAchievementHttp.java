package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

/**
 * Created by lyqai on 2018/7/5.
 */

public interface LiveAchievementHttp {
    void setStuStarCount(long reTryTime, final String starId, final AbstractBusinessDataCallBack
            callBack);

    void sendStat(int i);
}
