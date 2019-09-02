package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

import org.json.JSONArray;

/**
 * Created by linyuqiang on 2018/7/5.
 */

public interface QuestionSecHttp extends QuestionHttp {
    void submitBigTestInteraction(VideoQuestionLiveEntity videoQuestionLiveEntity, JSONArray userAnswer, long startTime, int isForce, AbstractBusinessDataCallBack callBack);

    void getStuInteractionResult(VideoQuestionLiveEntity videoQuestionLiveEntity,AbstractBusinessDataCallBack callBack);
}
