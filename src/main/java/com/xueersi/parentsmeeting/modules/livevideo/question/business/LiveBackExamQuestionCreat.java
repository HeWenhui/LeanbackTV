package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5PlaybackPager;

import java.util.ArrayList;

/**
 * Created by lyqai on 2018/7/25.
 */

public class LiveBackExamQuestionCreat implements BaseExamQuestionCreat {
    private boolean IS_SCIENCE;
    private LiveGetInfo liveGetInfo;
    BaseExamQuestionInter.ExamStop examStop;

    public void setIS_SCIENCE(boolean IS_SCIENCE) {
        this.IS_SCIENCE = IS_SCIENCE;
    }

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setExamStop(BaseExamQuestionInter.ExamStop examStop) {
        this.examStop = examStop;
    }

    @Override
    public BaseExamQuestionInter creatBaseExamQuestion(Activity activity, QuestionBll questionBll, final String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity) {
        ExamQuestionX5PlaybackPager examQuestionPlaybackPager = new ExamQuestionX5PlaybackPager(activity,
                liveid, videoQuestionLiveEntity, IS_SCIENCE, liveGetInfo.getStuCouId(), examStop);
        return examQuestionPlaybackPager;
    }
}
