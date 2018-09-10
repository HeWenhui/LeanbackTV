package com.xueersi.parentsmeeting.modules.livevideo.fragment.learnfeedback;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRootView.addView(experienceLearnFeedbackPager.getRootView());
        }
    }
}