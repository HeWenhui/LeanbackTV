package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoQuestionAction;

/**
 * Created by lyqai on 2018/7/17.
 */

public class QuestionPlayBackBll extends LiveBackBaseBll implements VideoQuestionAction {
    QuestionBll questionBll;

    public QuestionPlayBackBll(Activity activity, LiveBackBll liveBackBll, RelativeLayout mRootView) {
        super(activity, liveBackBll, mRootView);
        questionBll = new QuestionBll(activity, liveBackBll.getStuCourId());
    }

    @Override
    public void showQuestion(VideoQuestionEntity questionEntity) {

    }
}
