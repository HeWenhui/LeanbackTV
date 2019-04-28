package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.buycourse;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll;

public class ExperienceBuyCourseExperiencePresenter extends StandExperienceEventBaseBll implements IExperiencePresenter {
    //第一次拿到数据才暂时，第二次再请求就不展示了
    private boolean isFirstGetResult = true;

    private ExperienceBuyCourseView mPager;

    /**
     * 0 liveback
     * 1 experience
     *
     * @param activity
     * @param liveBackBll
     */
    public ExperienceBuyCourseExperiencePresenter(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        mPager = new ExperienceBuyCourseView(activity, this);
    }

    @Override
    public void showNextWindow() {
        //强制转换前判断，防止别人误用liveBackBll。
        if (liveBackBll instanceof StandExperienceLiveBackBll) {
            // 不采用这种方式，在展示下一个View前可能会有业务逻辑去处理所以交给LiveBackBll去处理。
//            (new StandExperienceLearnFeedbackBll(activity, (StandExperienceLiveBackBll) liveBackBll)).showWindow();
            for (LiveBackBaseBll liveBackBaseBll : liveBackBll.getLiveBackBaseBlls()) {
                if (liveBackBaseBll instanceof StandExperienceLearnFeedbackBll) {
                    ((StandExperienceLiveBackBll) liveBackBll).showNextWindow((StandExperienceLearnFeedbackBll)
                            liveBackBaseBll);
                }
            }
            //不推荐采用这种方式，在展示下一个View前可能会有业务逻辑去处理，该业务逻辑属于上层，应该由LiveBackBll处理，所以交给LiveBackBll去处理。
            //(new StandExperienceLearnFeedbackBll(activity, (StandExperienceLiveBackBll) liveBackBll)).showWindow();
        }
    }

    @Override
    public void showWindow() {
        //请求得到购课页面数据
        liveBackBll.getCourseHttpManager().getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        ExperienceResult learn = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString()
                                , ExperienceResult.class);
                        if (learn != null) {
                            getDataCallBack.onDataSucess(learn);
                        }
                        logger.i("playbackresponseEntity:" + responseEntity);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logger.i("playbackerrorEntity:" + error);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.i("playbackerrorEntity:" + responseEntity);
                    }
                });
    }

    //从后台拿到的课程数据
    private ExperienceResult mData;
    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            // 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                mData = (ExperienceResult) objData[0];
                // 测试体验课播放器的结果页面
                if (mData != null && isFirstGetResult) {
                    showRealWindow();//展示页面
                    isFirstGetResult = false;
                }
            }
        }
    };

    private void showRealWindow() {
        mPager.updateView(mData);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRootView.addView(mPager.getRootView(), layoutParams);
    }

    @Override
    public void removeWindow() {
        if (mPager != null && mPager.getRootView().getParent() == mRootView) {
            mRootView.removeView(mPager.getRootView());
        }
    }
}
