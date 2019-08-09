package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SubjectResultX5Pager;

/**
 * Created by 语文主观题结果页 on 2018/7/26.
 */
public interface BaseSubjectResultCreat {
    SubjectResultX5Pager creat(Context context, BaseQuestionWebInter.StopWebQuestion questionBll, String testPaperUrl, String stuId, String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuCouId);
}
