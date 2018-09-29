package com.xueersi.parentsmeeting.modules.livevideo.fragment.learnfeedback;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExperienceLearnFeedbackPager;

import org.json.JSONObject;

public class ExperienceLearnFeedbackBll extends StandExperienceEventBaseBll implements IPresenter {

    ExperienceLearnFeedbackPager experienceLearnFeedbackPager;

    public ExperienceLearnFeedbackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        initListener();
    }

    @Override
    public void initView() {
        experienceLearnFeedbackPager = new ExperienceLearnFeedbackPager(mContext, mVideoEntity, ((Activity) mContext)
                .getWindow());

    }

    private void initListener() {
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
//    @Override
//    public void resultComplete() {
//        super.resultComplete();
//        if (experienceLearnFeedbackPager != null && experienceLearnFeedbackPager.getRootView().getParent() == null) {
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
//                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//            mRootView.addView(experienceLearnFeedbackPager.getRootView(), layoutParams);
//            mRootView.setBackgroundColor(activity.getResources().getColor(R.color.COLOR_CC000000));
//        }
//    }
    @Override
    public void showWindow() {
        if (experienceLearnFeedbackPager != null) {
            mRootView.addView(experienceLearnFeedbackPager.getRootView());
        }
    }

    @Override
    public void removeWindow() {
    }

    @Override
    public void showNextWindow() {
    }
}