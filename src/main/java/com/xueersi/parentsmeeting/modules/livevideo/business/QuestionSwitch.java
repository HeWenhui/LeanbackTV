package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.VoiceAnswerPager;

import java.io.File;

/**
 * Created by lyqai on 2017/12/6.
 * 语音答题的一些接口
 */
public interface QuestionSwitch {
    BasePager questionSwitch(BaseVideoQuestionEntity baseQuestionEntity);

    String getsourcetype(BaseVideoQuestionEntity baseQuestionEntity);

    /**
     * 提交答案
     *
     * @param videoQuestionLiveEntity
     * @param answer
     * @param result
     * @param sorce
     * @param isRight
     * @param voiceTime
     * @param isSubmit
     * @param answerReslut
     */
    void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String answer, String result, int sorce, boolean isRight, double voiceTime, String isSubmit, OnAnswerReslut answerReslut);

    /**
     * 得到互动题，暂时没用
     */
    void getQuestion(BaseVideoQuestionEntity baseQuestionEntity, OnQuestionGet onQuestionGet);

    /**
     * 上传文件
     *
     * @param file
     */
    void uploadVoiceFile(File file);

    void stopSpeech(BaseVoiceAnswerPager answerPager, BaseVideoQuestionEntity baseVideoQuestionEntity);

    /**
     * 语音答题强制提交，评测错误
     *
     * @param baseVideoQuestionEntity
     * @param entity
     */
    void onAnswerTimeOutError(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity);

    /**
     * 得到互动题，暂时没用
     */
    interface OnQuestionGet {
        void onQuestionGet(BaseVideoQuestionEntity baseQuestionEntity);
    }

    /**
     * 提交互动题，得到结果
     */
    interface OnAnswerReslut {
        void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity);

        /**
         * 提交答题网络失败
         */
        void onAnswerFailure();
    }

    interface OnQuesVoiceTeamReslut {
        void onDataFail(int errStatus, String failMsg);
    }
}
