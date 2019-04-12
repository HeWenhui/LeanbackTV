package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

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
    public BasePager questionSwitch(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseQuestionEntity) {
        return questionSwitch.questionSwitch(baseVoiceAnswerPager, baseQuestionEntity);
    }

    @Override
    public String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity) {
        return questionSwitch.getsourcetype(baseQuestionEntity);
    }

    @Override
    public void onPutQuestionResult(BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, OnAnswerReslut answerReslut) {
        questionSwitch.onPutQuestionResult(baseVoiceAnswerPager, videoQuestionLiveEntity, answer, result, sorce, isRight, voiceTime, isSubmit, answerReslut);
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
        LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
        showQuestion.onHide(baseVideoQuestionEntity);
        MediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, MediaPlayerControl.class);
        if (mediaPlayerControl == null){
            //体验课
            LiveVideoActivityBase mediaPlayerControl1;
            mediaPlayerControl1 = ProxUtil.getProxUtil().get(context, LiveVideoActivityBase.class);
//            mediaPlayerControl1.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            mediaPlayerControl1.start();
        }else {
            mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            mediaPlayerControl.start();
        }

    }

    @Override
    public void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        questionSwitch.onAnswerTimeOutError(baseVideoQuestionEntity, entity);
    }
}
