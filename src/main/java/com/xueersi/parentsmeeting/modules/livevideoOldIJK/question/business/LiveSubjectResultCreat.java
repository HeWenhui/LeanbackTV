package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SubjectResultX5Pager;

/**
 * Created by lyqai on 2018/7/26.
 */

public class LiveSubjectResultCreat implements BaseSubjectResultCreat {
    private LiveGetInfo liveGetInfo;

    public void setLiveGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    @Override
    public SubjectResultX5Pager creat(Context context, BaseQuestionWebInter.StopWebQuestion questionBll, String testPaperUrl, String stuId, String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuCouId) {
        SubjectResultX5Pager subjectResultPager = new SubjectResultX5Pager(context, videoQuestionLiveEntity, questionBll,
                liveGetInfo.getSubjectiveTestAnswerResult(),
                liveGetInfo.getStuId(), liveGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(),
                stuCouId);
        return subjectResultPager;
    }
}
