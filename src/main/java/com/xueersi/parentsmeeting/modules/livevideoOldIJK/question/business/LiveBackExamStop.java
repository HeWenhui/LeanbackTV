package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseExamQuestionInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

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
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
        showQuestion.onHide(mQuestionEntity);
        MediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        mediaPlayerControl.seekTo(mQuestionEntity.getvEndTime() * 1000);
        mediaPlayerControl.start();
    }
}
