package com.xueersi.parentsmeeting.modules.livevideo.fragment.learnfeedback;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExperienceLearnFeedbackPager;

import org.json.JSONObject;

public class ExperienceLearnFeedbackBll extends LiveBackBaseBll {

    ExperienceLearnFeedbackPager experienceLearnFeedbackPager;

    public ExperienceLearnFeedbackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        experienceLearnFeedbackPager = new ExperienceLearnFeedbackPager(mContext, mVideoEntity, ((Activity) mContext)
                .getWindow());
        experienceLearnFeedbackPager.setLearnFeedBackPagerListener(new ExperienceLearnFeedbackPager
                .LearnFeedBackPagerListener() {
            @Override
            public void submitClick(String useId, String liveId, String subjectId, String gradId, String
                    chapterId, String suggest, JSONObject jsonObject, HttpCallBack httpCallBack) {
                getCourseHttpManager().sendExperienceFeedback(useId,
                        liveId, subjectId, gradId, chapterId, suggest, jsonObject, httpCallBack);
            }

            @Override
            public void onClose() {
                if (experienceLearnFeedbackPager != null && experienceLearnFeedbackPager.getRootView().getParent() ==
                        mRootView) {
                    mRootView.removeView(experienceLearnFeedbackPager.getRootView());
                }
            }
        });
    }


    /**
     * 视屏播放完成回调
     */
    @Override
    public void onUserBackPressed() {
        super.onUserBackPressed();
        if (experienceLearnFeedbackPager != null && experienceLearnFeedbackPager.getRootView().getParent() == null) {
            mRootView.addView(experienceLearnFeedbackPager.getRootView());
        }
    }
}
