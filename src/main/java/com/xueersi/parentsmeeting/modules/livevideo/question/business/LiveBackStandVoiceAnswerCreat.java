package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.CreateAnswerReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by linyuqiang on 2018/7/26.
 */

public class LiveBackStandVoiceAnswerCreat extends LiveStandVoiceAnswerCreat {

    public LiveBackStandVoiceAnswerCreat(Context context, QuestionSwitch questionSwitch, LiveAndBackDebug liveAndBackDebug) {
        super(context, questionSwitch, liveAndBackDebug);
    }

    @Override
    public CreateAnswerReslutEntity onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, BackMediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
            mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            mediaPlayerControl.start();
        }
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
        showQuestion.onHide(baseVideoQuestionEntity);
        return super.onAnswerReslut(context, questionBll, baseVoiceAnswerPager, baseVideoQuestionEntity, entity);
    }
}
