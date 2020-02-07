package com.xueersi.parentsmeeting.modules.livevideo.subscribecourse;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

public class SubscribeCourseBll extends LiveBaseBll {

    public SubscribeCourseBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
    }
}
