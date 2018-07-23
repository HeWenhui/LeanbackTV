package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoQuestionAction;

/**
 * Created by lyqai on 2018/7/17.
 */

public class QuestionPlayBackBll extends LiveBackBaseBll {
    QuestionBll questionBll;

    public QuestionPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        questionBll = new QuestionBll(activity, liveBackBll.getStuCourId());
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_QUESTION, LocalCourseConfig.CATEGORY_EXAM};
    }
}
