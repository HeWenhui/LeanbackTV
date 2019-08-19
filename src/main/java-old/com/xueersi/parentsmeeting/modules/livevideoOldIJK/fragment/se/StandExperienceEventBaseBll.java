package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;

public class StandExperienceEventBaseBll extends LiveBackBaseBll {
    //视频是否完成
    private boolean isResultComplete = false;

    public StandExperienceEventBaseBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    /**
     * 视屏结束时的回调
     *
     * @return
     */
    public void resultComplete() {
        isResultComplete = true;
    }

    protected boolean getIsResultComplete() {
        return isResultComplete;
    }
}
