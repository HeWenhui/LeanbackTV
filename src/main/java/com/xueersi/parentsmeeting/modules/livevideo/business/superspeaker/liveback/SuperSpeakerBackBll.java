package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.liveback;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;

public class SuperSpeakerBackBll extends LiveBackBaseBll {
    public SuperSpeakerBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{};
    }
}
