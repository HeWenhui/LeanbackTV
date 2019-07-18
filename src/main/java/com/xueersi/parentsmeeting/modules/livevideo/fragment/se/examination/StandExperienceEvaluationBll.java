package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll;

public class StandExperienceEvaluationBll extends
        StandExperienceEventBaseBll implements IExperiencePresenter {

    private IStandExperienceEvaluationContract.IEvaluationView mView;

    public StandExperienceEvaluationBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        mView = new StandExperienceEvaluationPager(activity, this);
    }

//    @Override
//    public void resultComplete() {
//        super.resultComplete();
//    }

    /**
     * 加载url:
     * http://exp-class.xesv5.com/wholeBodyLive/expClassApp/index
     * .html#/index?nowLevel=12&liveId=210285&gradeId=1&subjectId=2&teacherId=2671&orderId=2523&userId=11
     */
    @Override
    public void showWindow() {
        if (mView != null && mRootView != null) {
//            if (!TextUtils.isEmpty(mVideoEntity.getExamUrl())) {
//                String url = mVideoEntity.getExamUrl() + "#/index?nowLevel=" + "&liveId=" + mVideoEntity.getLiveId() +
//                        "&gradeId=" + mVideoEntity.getGradId() + "&subjectId=" + mVideoEntity.getSubjectId() +
//                        "&teacherId=" + mVideoEntity.getTeacherId() + "&orderId=" + mVideoEntity.getChapterId() +
//                        "&userId=" + UserBll.getInstance().getMyUserInfoEntity().getStuId();

//            ActivityChangeLand activityChangeLand = ProxUtil.getProxUtil().get(activity, ActivityChangeLand.class);
//            activityChangeLand.changeLOrP();
            logger.i("旋转屏幕");
            mView.showWebView(mVideoEntity.getExamUrl());
            if(mView.getRootView().getParent()==null) {
                mRootView.addView(mView.getRootView(), RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            }
        }
    }

    private final String ExpandUrl(String url) {
        return "&" + url + "=";
    }

    @Override
    public void removeWindow() {

        if (mView.getRootView() != null && mView.getRootView().getParent() == mRootView) {
            mRootView.removeView(mView.getRootView());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView instanceof StandExperienceEvaluationPager) {
            ((StandExperienceEvaluationPager) mView).onDestroy();
        }
    }

    /**
     * 显示下一个View
     */
    @Override
    public void showNextWindow() {
        for (LiveBackBaseBll liveBackBaseBll : liveBackBll.getLiveBackBaseBlls()) {
            if (liveBackBaseBll instanceof StandExperienceLearnFeedbackBll) {
                ((StandExperienceLiveBackBll) liveBackBll).showNextWindow((StandExperienceLearnFeedbackBll) liveBackBaseBll);
//        不推荐采用这种方式，在展示下一个View前可能会有业务逻辑去处理，该业务逻辑属于上层，应该由LiveBackBll处理，所以交给LiveBackBll去处理。
//        ((StandExperienceLearnFeedbackBll) liveBackBaseBll).showWindow();

            }
        }
    }

}
