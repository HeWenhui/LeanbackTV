package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;

public class PSLiveVideoBll extends LiveVideoBll {

    public PSLiveVideoBll(Activity activity, LiveBll2 liveBll, int liveType) {
        super(activity, liveBll, liveType);
    }

    @Override
    public void rePlay(boolean modechange) {

    }
}
