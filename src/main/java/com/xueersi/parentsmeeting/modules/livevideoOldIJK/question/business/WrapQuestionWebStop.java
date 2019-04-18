package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseQuestionWebInter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

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
    public void stopWebQuestion(BasePager pager, String testId, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        stopWebQuestion.stopWebQuestion(pager, testId, baseVideoQuestionEntity);
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
        showQuestion.onHide(videoQuestionLiveEntity);
        MediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
        mediaPlayerControl.start();
    }
}