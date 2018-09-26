package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.app.Activity;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;

public class RecommondCourseBll extends LiveBackBaseBll {

    private RecommondCoursePager mPager;

    public RecommondCourseBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        mPager = new RecommondCoursePager(mContext);

    }

    private void initListener() {
        if (mPager != null) {
            mPager.setClickListener(new RecommondCoursePager.ClickListener() {
                //跳转到购课页面
                @Override
                public void clickBuyCourse() {

                }
            });
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        if (mPager == null) {
            mPager = new RecommondCoursePager(mContext);
        }
        //添加窗口
        mRootView.addView(mPager.getRootView());
    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_RECOMMOND_COURSE
        };

    }

    /**
     * 购买课程之后的回调
     *
     * @param isSuccess 购课成功的回调
     */
    @Override
    public void buyRecommondCourseComplete(Boolean isSuccess) {
        super.buyRecommondCourseComplete(isSuccess);
        if (isSuccess) {
            if (mPager != null && mPager.getRootView().getParent() == mRootView) {
                mRootView.removeView(mPager.getRootView());
            }
        }
    }
}
