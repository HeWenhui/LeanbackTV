package com.xueersi.parentsmeeting.modules.livevideo.business;


import com.xueersi.common.entity.BaseVideoQuestionEntity;

/**
 * 互动题事件
 * Created by linyuqiang on 2017/2/9.
 */

public interface PutQuestion {
    /**
     * 提交互动题
     *
     * @param result
     */
    void onPutQuestionResult(BaseVideoQuestionEntity videoQuestionLiveEntity, String result);
}
