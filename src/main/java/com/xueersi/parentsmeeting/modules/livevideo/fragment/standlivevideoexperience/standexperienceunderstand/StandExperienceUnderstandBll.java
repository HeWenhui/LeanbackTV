package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperienceunderstand;

import android.app.Activity;
import android.content.Context;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;

public class StandExperienceUnderstandBll extends LiveBackBaseBll {

    StandExperienceUnderstandPager mPager;

    public StandExperienceUnderstandBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);

    }

    @Override
    public void initView() {
        super.initView();
        mPager = new StandExperienceUnderstandPager(mContext);
        initListener();
    }

    private void initListener() {
        mPager.setUnderStandListener(new StandExperienceUnderstandPager.IUnderStandListener() {
            @Override
            public void onClick(int sign) {
                if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_UNDERSTAND) {//懂了

                } else if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_LITTLE_UNDERSTAND) {//半懂

                } else if (sign == StandExperienceUnderstandPager.STAND_EXPERIENCE_NO_UNDERSTAND) {//没懂

                }
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

        if (mPager != null) {
            mRootView.addView(mPager.getRootView());
        }

    }

    @Override
    public int[] getCategorys() {
        return new int[]{
                LocalCourseConfig.CATEGORY_UNDERSTAND
        };
    }
}
