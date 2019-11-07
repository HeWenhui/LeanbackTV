package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

/**
 * 大班-普通直播 灰度控制
 *
 * @author chekun
 * created  at 2019/10/15 13:44
 */
public class BigLiveGrayEntity {
    /**当前直播场次，属于大班整合几期**/
    int planVersion;

    public int getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(int planVersion) {
        this.planVersion = planVersion;
    }
}
