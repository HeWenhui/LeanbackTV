package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperienceunderstand;

import android.app.Activity;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;

public class StandExperienceUnderstandBll extends StandExperienceEventBaseBll {

    StandExperienceUnderstandPager mPager;

//    List<String> optionList;

//    Map<String, String> map;

    public StandExperienceUnderstandBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);

    }

    @Override
    public void initView() {
        super.initView();
        mPager = new StandExperienceUnderstandPager(mContext, mVideoEntity);
        initData();
        initListener();
    }

    /**
     * 得到懂了么的数据
     */
    private void initData() {
//        optionList = new ArrayList<>();
//        map = mVideoEntity.getUnderStandDifficulty();
//        Iterator<String> iterator = map.keySet().iterator();
//        while (iterator.hasNext()) {
//            optionList.add(iterator.next());
//        }
    }

    private void initListener() {
        mPager.setUnderStandListener(new StandExperienceUnderstandPager.IUnderStandListener() {
            @Override
            public void onClick(int sign) {
                HttpCallBack httpCallBack = new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("pm success");
                    }
                };
                LivePlayBackHttpManager livePlayBackHttpManager = getCourseHttpManager();
                String option = "";
                if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_UNDERSTAND) {//懂了
                    option = "1";
                } else if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_LITTLE_UNDERSTAND) {//半懂
                    option = "2";
                } else if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_NO_UNDERSTAND) {//没懂
                    option = "3";
                }
                logger.i(option);
                livePlayBackHttpManager.sendStandExperienceUnderStand(
                        mVideoEntity.getSubmitUnderStandUrl(),
                        UserBll.getInstance().getMyUserInfoEntity().getStuId(),
                        mVideoEntity.getGradId(),
                        mVideoEntity.getLiveId(),
                        mVideoEntity.getSubjectId(),
                        mVideoEntity.getChapterId(),
                        option,
                        httpCallBack);
                //点击完成之后，不管消息是否送到服务器，都要调用关闭按钮
                if (mPager != null && mPager.getRootView().getParent() == mRootView) {
                    mRootView.removeView(mPager.getRootView());
                }
            }
        });
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        logger.i("显示懂了吗弹窗");
        if (mPager != null) {
            mRootView.addView(mPager.getRootView());
        }
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        super.onQuestionEnd(questionEntity);
        logger.i("移出懂了么窗口");
        if (mPager != null && mPager.getRootView().getParent() == mRootView) {
            mRootView.removeView(mPager.getRootView());
        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_UNDERSTAND
        };
    }
}
