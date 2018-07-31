package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.MediaControllerAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/26.
 */

public class LiveBackStandVoiceAnswerCreat extends LiveStandVoiceAnswerCreat{
    public LiveBackStandVoiceAnswerCreat(Context context, QuestionSwitch questionSwitch, LiveAndBackDebug liveAndBackDebug) {
        super(context, questionSwitch, liveAndBackDebug);
    }

    @Override
    public boolean onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        MediaPlayerControl videoPlayAction = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        if (videoPlayAction != null) {
            VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
            videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            videoPlayAction.start();
        }
        MediaControllerAction mediaControllerAction = ProxUtil.getProxUtil().get(context, MediaControllerAction.class);
        mediaControllerAction.attachMediaController();
        return super.onAnswerReslut(context, questionBll, baseVoiceAnswerPager, baseVideoQuestionEntity, entity);
    }
}
