package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

/**
 * Created by linyuqiang on 2018/7/25.
 */
public class WrapSpeechEvalAction implements SpeechEvalAction {
    protected SpeechEvalAction speechEvalAction;
    protected VideoQuestionLiveEntity videoQuestionLiveEntity;
    Context context;

    WrapSpeechEvalAction(Context context) {
        this.context = context;
    }

    public void setSpeechEvalAction(SpeechEvalAction speechEvalAction) {
        this.speechEvalAction = speechEvalAction;
    }

    public void setVideoQuestionLiveEntity(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
    }

    @Override
    public void getSpeechEval(String id, OnSpeechEval onSpeechEval) {
        speechEvalAction.getSpeechEval(id, onSpeechEval);
    }

    @Override
    public void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, String num) {
        speechEvalAction.stopSpeech(pager, baseVideoQuestionEntity, num);
        MediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            mediaPlayerControl.start();
        }
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval) {
        speechEvalAction.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult2(String id, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuAnswer, String isSubmit, OnSpeechEval onSpeechEval) {
        speechEvalAction.sendSpeechEvalResult2(id, videoQuestionLiveEntity, stuAnswer, isSubmit, onSpeechEval);
    }

    @Override
    public void onSpeechSuccess(String num) {
        speechEvalAction.onSpeechSuccess(num);
    }

    @Override
    public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
        speechEvalAction.speechIsAnswered(num, isAnswered);
    }

}
