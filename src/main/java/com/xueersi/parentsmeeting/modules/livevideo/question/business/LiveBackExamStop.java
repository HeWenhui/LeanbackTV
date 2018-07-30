package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by linyuqiang on 2018/7/26.
 * 回放的课件关闭
 */
public class LiveBackExamStop implements BaseExamQuestionInter.ExamStop {
    Context context;
    QuestionBll questionBll;

    public LiveBackExamStop(Context context, QuestionBll questionBll) {
        this.context = context;
        this.questionBll = questionBll;
    }

    @Override
    public void stopExam(BaseExamQuestionInter baseExamQuestionInter, VideoQuestionLiveEntity mQuestionEntity) {
        questionBll.stopExam(mQuestionEntity.getvQuestionID(), baseExamQuestionInter);
        MediaPlayerControl videoPlayAction = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        videoPlayAction.seekTo(mQuestionEntity.getvEndTime() * 1000);
        videoPlayAction.start();
    }
}
