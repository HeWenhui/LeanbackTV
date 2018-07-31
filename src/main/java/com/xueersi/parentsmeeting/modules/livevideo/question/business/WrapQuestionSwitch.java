package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.io.File;

/**
 * Created by linyuqiang on 2018/7/26.
 * 直播回放的语音答题切换
 */
public class WrapQuestionSwitch implements QuestionSwitch {
    Context context;
    QuestionSwitch questionSwitch;
    protected VideoQuestionLiveEntity videoQuestionLiveEntity;

    public WrapQuestionSwitch(Context context, QuestionSwitch questionSwitch) {
        this.context = context;
        this.questionSwitch = questionSwitch;
    }

    public void setVideoQuestionLiveEntity(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
    }

    @Override
    public BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity) {
        return questionSwitch.questionSwitch(baseQuestionEntity);
    }

    @Override
    public String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity) {
        return questionSwitch.getsourcetype(baseQuestionEntity);
    }

    @Override
    public void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, OnAnswerReslut answerReslut) {
        questionSwitch.onPutQuestionResult(videoQuestionLiveEntity, answer, result, sorce, isRight, voiceTime, isSubmit, answerReslut);
    }

    @Override
    public void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, OnQuestionGet onQuestionGet) {
        questionSwitch.getQuestion(baseQuestionEntity, onQuestionGet);
    }

    @Override
    public void uploadVoiceFile(File file) {
        questionSwitch.uploadVoiceFile(file);
    }

    @Override
    public void stopSpeech(BaseVoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
        questionSwitch.stopSpeech(answerPager, baseVideoQuestionEntity);
        MediaControllerAction mediaControllerAction = ProxUtil.getProxUtil().get(context, MediaControllerAction.class);
        mediaControllerAction.attachMediaController();
        MediaPlayerControl videoPlayAction = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
        videoPlayAction.start();
    }

    @Override
    public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        questionSwitch.onAnswerTimeOutError(baseVideoQuestionEntity, entity);
    }
}
