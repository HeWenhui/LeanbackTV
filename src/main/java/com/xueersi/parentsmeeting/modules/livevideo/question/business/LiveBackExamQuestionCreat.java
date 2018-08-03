package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.ExamQuestionX5PlaybackPager;

/**
 * Created by linyuqiang on 2018/7/25.
 * 回放测试卷
 */
public class LiveBackExamQuestionCreat implements BaseExamQuestionCreat {
    private boolean IS_SCIENCE;
    private LiveGetInfo liveGetInfo;
    private BaseExamQuestionInter.ExamStop examStop;
    LivePagerBack livePagerBack;

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

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
    public BaseExamQuestionInter creatBaseExamQuestion(Activity activity, final String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity) {
        ExamQuestionX5PlaybackPager examQuestionPlaybackPager = new ExamQuestionX5PlaybackPager(activity,
                liveid, videoQuestionLiveEntity, IS_SCIENCE, liveGetInfo.getStuCouId(), examStop, livePagerBack);
        return examQuestionPlaybackPager;
    }
}
