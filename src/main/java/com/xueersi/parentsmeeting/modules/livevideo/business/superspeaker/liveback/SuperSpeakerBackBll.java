package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.liveback;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;

import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_SUPER_SPEAKER;

public class SuperSpeakerBackBll extends LiveBackBaseBll {
    public SuperSpeakerBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }




    @Override
    public int[] getCategorys() {
        return new int[]{CATEGORY_SUPER_SPEAKER};
    }
}
