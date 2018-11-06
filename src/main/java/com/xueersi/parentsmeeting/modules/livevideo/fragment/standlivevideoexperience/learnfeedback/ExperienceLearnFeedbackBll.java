package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceLiveBackBll;

import org.json.JSONArray;

/**
 * 学习反馈窗口的Presenter层
 */
public class ExperienceLearnFeedbackBll extends StandExperienceEventBaseBll implements LearnFeedBackContract.ISendHttp {

    StandExperienceLearnFeedBackPager mPager;

    public ExperienceLearnFeedbackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        initListener();
    }

    @Override
    public void initView() {
        mPager = new StandExperienceLearnFeedBackPager(activity, this, mVideoEntity);
    }

    private void initListener() {
    }

    @Override
    public void showWindow() {
        if (mPager != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            logger.i("添加学习反馈窗口");
            mRootView.addView(mPager.getRootView(), layoutParams);
        }
    }

    @Override
    public void removeWindow() {
        if (mPager != null && mPager.getRootView().getParent() ==
                mRootView) {
            mRootView.removeView(mPager.getRootView());
            mRootView.setBackgroundColor(0);
        }
    }

    @Override
    public void showNextWindow() {
    }

    @Override
    public void sendHttp(String useId, String liveId, String subjectId, String gradId, String chapterId, String
            suggest, JSONArray jsonObject, HttpCallBack httpCallBack) {
        getCourseHttpManager().sendExperienceFeedback(useId, liveId, subjectId, gradId, chapterId, suggest,
                jsonObject, httpCallBack);
    }
}