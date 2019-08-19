package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.ExamQuestionX5PlaybackPager;

/**
 * Created by linyuqiang on 2018/7/25.
 * 回放测试卷
 */
public class LiveBackExamQuestionCreat implements BaseExamQuestionCreat {
    private int isArts;
    private LiveGetInfo liveGetInfo;
    private BaseExamQuestionInter.ExamStop examStop;
    LivePagerBack livePagerBack;

    public void setLivePagerBack(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    public void setArts(int arts) {
        this.isArts = arts;
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
                liveid, videoQuestionLiveEntity, isArts, liveGetInfo.getStuCouId(), examStop, livePagerBack);
        return examQuestionPlaybackPager;
    }
}
