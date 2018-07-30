package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by lyqai on 2018/7/25.
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
    public void stopSpeech(BaseSpeechAssessmentPager pager, String num) {
        speechEvalAction.stopSpeech(pager, num);
        MediaPlayerControl videoPlayAction = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
        videoPlayAction.start();
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval) {
        speechEvalAction.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
    }

    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval) {
        speechEvalAction.sendSpeechEvalResult2(id, stuAnswer, onSpeechEval);
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
