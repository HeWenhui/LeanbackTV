package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

public interface EnglishH5CoursewareSecHttp extends EnglishH5CoursewareHttp {
    void getCourseWareTests(String url, String params, AbstractBusinessDataCallBack callBack);

    void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack);
}
