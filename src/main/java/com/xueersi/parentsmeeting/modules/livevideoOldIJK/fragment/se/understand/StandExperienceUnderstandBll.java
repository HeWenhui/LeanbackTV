package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.understand;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionShowReg;

public class StandExperienceUnderstandBll extends StandExperienceEventBaseBll implements IStandExperienceUnderstandContract
        .IUnderStandPresenter {

    StandExperienceUnderstandPager mPager;

    public StandExperienceUnderstandBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);

    }

    @Override
    public void initView() {
        super.initView();
        mPager = new StandExperienceUnderstandPager(mContext, mVideoEntity, this);
        registerInBllHideView();
        initData();
    }

    /**
     * 将这个bll注册在所有的Bll中，在各种其他Bll（目前只有QuestionBll，EnglishH5CoursewareBll）显示时做出相应操作（目前是隐藏聊天区的View）
     */
    private void registerInBllHideView() {
        //在QuestionShowReg中注册(也就是QuestionShowReg唯一实现类QuestionBLl中注册)，为了在QuestionBll显示时隐藏该聊天区
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(mPager);
        }
        //在EnglishShowReg中注册(也就是EnglishShowReg唯一实现类EnglishH5CoursewareBll中注册)，为了在EnglishH5CoursewareBll显示时隐藏该聊天区
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(mPager);
        }
    }

    /**
     * 得到懂了么的数据
     */
    private void initData() {
    }

    //是否移出了懂了么弹窗
    private boolean isRemoveView = false;

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        logger.i("显示懂了吗弹窗");
        if (mPager != null && !isRemoveView) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mRootView.addView(mPager.getRootView(), layoutParams);
        }
    }

//    @Override
//    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
//        super.onQuestionEnd(questionEntity);
//        logger.i("移出懂了么窗口");
//        if (mPager != null && mPager.getRootView().getParent() == mRootView) {
//            mRootView.removeView(mPager.getRootView());
//        }
//    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_UNDERSTAND
        };
    }

    @Override
    public void removeView() {
        logger.i("移出懂了么窗口");
        if (mPager != null && mPager.getRootView().getParent() == mRootView) {
            mRootView.removeView(mPager.getRootView());
            isRemoveView = true;
        }
    }

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
}
