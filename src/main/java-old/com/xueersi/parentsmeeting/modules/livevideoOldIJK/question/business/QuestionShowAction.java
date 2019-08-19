package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * Created by linyuqiang on 2018/4/15.
 * 互动题隐藏消失监听
 */
public interface QuestionShowAction {
    void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow);
}
