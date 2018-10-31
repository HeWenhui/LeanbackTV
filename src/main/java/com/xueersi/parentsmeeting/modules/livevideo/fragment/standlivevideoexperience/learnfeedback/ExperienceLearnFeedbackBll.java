package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;

import org.json.JSONArray;

public class ExperienceLearnFeedbackBll extends StandExperienceEventBaseBll implements IPresenter, LearnFeedBackPager
        .ISendHttp {

    LearnFeedBackPager mPager;

    private static ExperienceLearnFeedbackBll instance;

//    public static ExperienceLearnFeedbackBll getInstance(Activity activity, StandExperienceLiveBackBll liveBackBll) {
//        if (instance == null) {
//            instance = new ExperienceLearnFeedbackBll(activity, liveBackBll);
//        }
//        return instance;
//    }

    public ExperienceLearnFeedbackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        initListener();
    }

    @Override
    public void initView() {
        mPager = new LearnFeedBackPager(mContext, this, mVideoEntity);
    }

    private void initListener() {
//        experienceLearnFeedbackPager.setLearnFeedBackPagerListener(new ExperienceLearnFeedbackPager
//                .LearnFeedBackPagerListener() {
//            @Override
//            public void submitClick(String useId, String liveId, String subjectId, String gradId, String
//                    chapterId, String suggest, JSONObject jsonObject, HttpCallBack httpCallBack) {
//                getCourseHttpManager().sendExperienceFeedback(useId,
//                        liveId, subjectId, gradId, chapterId, suggest, jsonObject, httpCallBack);
//            }
//
//            @Override
//            public void onClose() {
//                if (experienceLearnFeedbackPager != null && experienceLearnFeedbackPager.getRootView().getParent() ==
//                        mRootView) {
//                    mRootView.removeView(experienceLearnFeedbackPager.getRootView());
//                    mRootView.setBackgroundColor(0);
//                }
//            }
//        });
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
        if (mPager != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRootView.addView(mPager.getRootView(), layoutParams);
//            mRootView.addView(mPager.getRootView());
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