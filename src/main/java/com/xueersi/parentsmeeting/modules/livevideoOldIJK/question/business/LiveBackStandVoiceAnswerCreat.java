package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.entity.CreateAnswerReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/26.
 */

public class LiveBackStandVoiceAnswerCreat extends LiveStandVoiceAnswerCreat {

    public LiveBackStandVoiceAnswerCreat(Context context, QuestionSwitch questionSwitch, LiveAndBackDebug liveAndBackDebug) {
        super(context, questionSwitch, liveAndBackDebug);
    }

    @Override
    public CreateAnswerReslutEntity onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        MediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
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
