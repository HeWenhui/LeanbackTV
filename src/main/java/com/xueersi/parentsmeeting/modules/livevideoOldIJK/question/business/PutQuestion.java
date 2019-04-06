package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;


import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseLiveQuestionPager;

/**
 * 互动题事件
 * Created by linyuqiang on 2017/2/9.
 */

public interface PutQuestion {
    /**
     * 提交互动题
     *
     * @param baseLiveQuestionPager
     * @param result
     */
    void onPutQuestionResult(BaseLiveQuestionPager baseLiveQuestionPager, BaseVideoQuestionEntity videoQuestionLiveEntity, String result);
}
