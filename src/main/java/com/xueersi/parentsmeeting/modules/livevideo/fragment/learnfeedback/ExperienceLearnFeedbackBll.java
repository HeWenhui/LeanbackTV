package com.xueersi.parentsmeeting.modules.livevideo.fragment.learnfeedback;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
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
                    mRootView.setBackgroundColor(0);
                }
            }
        });
    }


    /**
     * 视屏播放完成回调
     * 把学习反馈弹窗放到正中间
     */
    @Override
    public void onUserBackPressed() {
        super.onUserBackPressed();
        if (experienceLearnFeedbackPager != null && experienceLearnFeedbackPager.getRootView().getParent() == null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRootView.addView(experienceLearnFeedbackPager.getRootView(), layoutParams);
            mRootView.setBackgroundColor(activity.getResources().getColor(R.color.COLOR_CC000000));
        }
    }
}