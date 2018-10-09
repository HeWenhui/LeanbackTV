package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;

public class StandExperienceEvaluationBll extends StandExperienceEventBaseBll {

    private StandExperienceEvaluationPager mPager;

    public StandExperienceEvaluationBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        mPager = new StandExperienceEvaluationPager(activity);
    }

    public void showEvaluation() {
        if (mPager != null) {
            mRootView.addView(mPager.getRootView());
        }
    }

//    @Override
//    public void resultComplete() {
//
//    }
}
