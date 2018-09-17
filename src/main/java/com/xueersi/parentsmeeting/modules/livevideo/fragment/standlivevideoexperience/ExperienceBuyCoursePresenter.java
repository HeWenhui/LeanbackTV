//package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience;
//
//import android.app.Activity;
//import android.util.Log;
//
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.base.BasePager;
//import com.xueersi.common.http.HttpCallBack;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.lib.framework.utils.JsonUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
//import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.contract.IBuyCourseContract;
//
//public class ExperienceBuyCoursePresenter extends LiveBackBaseBll implements IBuyCourseContract.Presenter {
//
//    private boolean isFirstGetResult = true;
//
//    //    private IBuyCourseContract.View mView;
//
//    private BasePager basePager;
//
//    /**
//     * 0 liveback
//     * 1 experience
//     *
//     * @param activity
//     * @param liveBackBll
//     */
//    public ExperienceBuyCoursePresenter(Activity activity, LiveBackBll liveBackBll) {
//        super(activity, liveBackBll);
////        mView = new ExperienceBuyCourseView(activity, this);
//        basePager = new ExperienceBuyCourseView(activity, this);
//    }
//
//    @Override
//    public void initView() {
//        super.initView();
//    }
//
//    @Override
//    public void removeStudyFeedBackView() {
//
//    }
//
//    @Override
//    public void removeBuyCourseView() {
//        if (basePager != null && basePager.getRootView().getParent() == mRootView) {
//            mRootView.removeView(basePager.getRootView());
//        }
//    }
//
//    @Override
//    public void showStudyFeedBackView() {
//
//    }
//
//    @Override
//    public void resultComplete() {
//        super.resultComplete();
//        //请求得到购课页面数据
//        liveBackBll.getCourseHttpManager().getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(),
//                new HttpCallBack() {
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                        ExperienceResult learn = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString()
//                                , ExperienceResult.class);
//                        if (learn != null) {
//                            getDataCallBack.onDataSucess(learn);
//                        }
//                        Log.e("Duncan", "playbackresponseEntity:" + responseEntity);
//                    }
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        Log.e("Duncan", "playbackerrorEntity:" + error);
//                    }
//
//                    @Override
//                    public void onPmError(ResponseEntity responseEntity) {
//                        super.onPmError(responseEntity);
//                        Log.e("Duncan", "playbackerrorEntity:" + responseEntity);
//                    }
//
//                });
//    }
//
//    private ExperienceResult mData;
//    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
//        @Override
//        public void onDataSucess(Object... objData) {
//            // 获取到数据之后的逻辑处理
//            if (objData.length > 0) {
//                mData = (ExperienceResult) objData[0];
//                // 测试体验课播放器的结果页面
//                if (mData != null && isFirstGetResult) {
//                    showPopupwinResult();
//
//                    isFirstGetResult = false;
//                }
//            }
//        }
//
//
//    };
//
//    private void showPopupwinResult() {
//        mRootView.addView(basePager.getRootView());
//    }
//}
