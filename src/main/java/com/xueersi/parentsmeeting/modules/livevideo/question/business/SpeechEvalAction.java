package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;

/**
 * Created by linyuqiang on 2017/2/28.
 * 语言评测得到试题和停止
 */
public interface SpeechEvalAction {

    /**
     * @param id           试题id
     * @param callBack 回调
     */
//    void getSpeechEval(String id, AbstractBusinessDataCallBack callBack);

    /**
     * 关闭试题
     */
    void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, String num);

    /**
     * 新课件{@linkplain LiveHttpManager#sendSpeechEvalResultNewArts}
     * 旧课件{@linkplain LiveHttpManager#sendSpeechEvalResult2}
     * @param id
     * @param videoQuestionLiveEntity
     * @param stuAnswer
     * @param isSubmit 1(1主动提交,2 强制提交)
     * @param callBack
     */
    void sendSpeechEvalResult2(String id, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuAnswer, String isSubmit, AbstractBusinessDataCallBack callBack);

    void onSpeechSuccess(String num);

    void speechIsAnswered(boolean isNewArt,String num, AbstractBusinessDataCallBack callBack);

}
