package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/26.
 */

public class WrapQuestionWebStop implements BaseQuestionWebInter.StopWebQuestion {
    BaseQuestionWebInter.StopWebQuestion stopWebQuestion;
    VideoQuestionLiveEntity videoQuestionLiveEntity;
    Context context;

    WrapQuestionWebStop(Context context) {
        this.context = context;
    }

    public void setStopWebQuestion(BaseQuestionWebInter.StopWebQuestion stopWebQuestion) {
        this.stopWebQuestion = stopWebQuestion;
    }

    public void setVideoQuestionLiveEntity(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
    }

    @Override
    public void stopWebQuestion(BasePager pager, String testId) {
        stopWebQuestion.stopWebQuestion(pager, testId);
        MediaPlayerControl videoPlayAction = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
        videoPlayAction.start();
    }
}
