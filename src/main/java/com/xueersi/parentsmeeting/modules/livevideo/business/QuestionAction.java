package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;

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
     * @param videoQuestionLiveEntity
     * @param entity
     */
    void onAnswerReslut(BaseVideoQuestionEntity videoQuestionLiveEntity, VideoResultEntity entity);

    /** 提交答题网络失败 */
    void onAnswerFailure();

    /** 结束互动题 */
    void onStopQuestion(String ptype, String nonce);

    /** 懂了吗 */
    void understand(String nonce);

    /** 考试开始 */
    void onExamStart(String liveid, String num, String nonce);

    /** 考试结束 */
    void onExamStop();

    /** 语音评测刷新 */
    boolean onSpeechResult(String json);

    void onNetWorkChange(int netWorkType);
}
