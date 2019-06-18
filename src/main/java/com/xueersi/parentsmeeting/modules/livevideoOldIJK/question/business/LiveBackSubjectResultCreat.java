package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SubjectResultX5Pager;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的互动题结果页
 */
public class LiveBackSubjectResultCreat implements BaseSubjectResultCreat {
    private LiveGetInfo liveGetInfo;
    private WrapQuestionWebStop wrapQuestionWebStop;

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    public void setWrapQuestionWebStop(WrapQuestionWebStop wrapQuestionWebStop) {
        this.wrapQuestionWebStop = wrapQuestionWebStop;
    }

    @Override
    public SubjectResultX5Pager creat(Context context, BaseQuestionWebInter.StopWebQuestion questionBll, String testPaperUrl, String stuId, String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuCouId) {
        wrapQuestionWebStop.setStopWebQuestion(questionBll);
        wrapQuestionWebStop.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        SubjectResultX5Pager subjectResultPager = new SubjectResultX5Pager(context, videoQuestionLiveEntity, wrapQuestionWebStop,
                liveGetInfo.getSubjectiveTestAnswerResult(),
                liveGetInfo.getStuId(), liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                stuCouId);
        return subjectResultPager;
    }
}
