package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * 互动题,懂了吗事件
 * Created by linyuqiang on 2016/8/18.
 */
public interface QuestionAction {
    /**
     * 显示互动题
     *
     * @param videoQuestionLiveEntity
     */
    void showQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity);

    /**
     * 显示互动题-大题
     *
     * @param videoQuestionLiveEntity
     */
    void showBigQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isOpen);

    /**
     * @param liveBasePager
     * @param videoQuestionLiveEntity
     * @param entity
     */
    void onAnswerReslut(LiveBasePager liveBasePager, BaseVideoQuestionEntity videoQuestionLiveEntity, VideoResultEntity entity);

    /** 提交答题网络失败 */
    void onAnswerFailure();

    /** 结束互动题 */
    void onStopQuestion(String method, String ptype, String nonce);

    /** 考试开始 */
    void onExamStart(String liveid, VideoQuestionLiveEntity videoQuestionLiveEntity);

    /**
     * 考试结束
     *
     * @param num
     */
    void onExamStop(String num);

    /** 语音评测刷新 */
    @Deprecated
    boolean onSpeechResult(String json);

    void onNetWorkChange(int netWorkType);
}
