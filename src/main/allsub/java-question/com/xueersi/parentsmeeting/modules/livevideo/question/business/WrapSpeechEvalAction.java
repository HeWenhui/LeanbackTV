package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

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

//    @Override
//    public void getSpeechEval(String id, AbstractBusinessDataCallBack callBack) {
//        speechEvalAction.getSpeechEval(id, callBack);
//    }

    @Override
    public void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, String num) {
        speechEvalAction.stopSpeech(pager, baseVideoQuestionEntity, num);
        BackMediaPlayerControl mediaPlayerControl = ProxUtil.getProxUtil().get(context, BackMediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            mediaPlayerControl.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
            mediaPlayerControl.start();
        }
    }

    @Override
    public void sendSpeechEvalResult2(String id, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuAnswer, String isSubmit, AbstractBusinessDataCallBack callBack) {
        speechEvalAction.sendSpeechEvalResult2(id, videoQuestionLiveEntity, stuAnswer, isSubmit, callBack);
    }

    @Override
    public void onSpeechSuccess(String num) {
        speechEvalAction.onSpeechSuccess(num);
    }

    @Override
    public void speechIsAnswered(boolean isNewArt,String num, AbstractBusinessDataCallBack callBack) {
        speechEvalAction.speechIsAnswered(isNewArt,num, callBack);
    }

}
