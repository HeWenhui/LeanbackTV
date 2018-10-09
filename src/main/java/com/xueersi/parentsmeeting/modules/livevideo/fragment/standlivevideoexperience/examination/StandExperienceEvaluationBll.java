package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.media.CenterLayout;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;

public class StandExperienceEvaluationBll extends StandExperienceEventBaseBll implements IPresenter {

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

    @Override
    public void showWindow() {
        if (mPager != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRootView.addView(mPager.getRootView(), layoutParams);
        }
    }

    @Override
    public void removeWindow() {

    }

    @Override
    public void showNextWindow() {

    }

//    @Override
//    public void resultComplete() {
//
//    }
}
