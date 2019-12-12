package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * Created by linyuqiang on 2017/3/25.
 * 英语h5课件
 */
public interface EnglishH5CoursewareAction {

    /**
     * h5 课件
     *
     * @param status
     * @param videoQuestionLiveEntity
     */
    void onH5Courseware(String status, VideoQuestionLiveEntity videoQuestionLiveEntity);

    void onH5CoursewareMethod(final String status,String offMethod, final VideoQuestionLiveEntity videoQuestionLiveEntity);

    void onNetWorkChange(int netWorkType);
}
