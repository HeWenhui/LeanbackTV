package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

        import com.xueersi.common.base.AbstractBusinessDataCallBack;
        import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

public interface EnglishH5CoursewareSecHttp extends EnglishH5CoursewareHttp {
    @Deprecated
    void getCourseWareTests(String url, String params, AbstractBusinessDataCallBack callBack);

    void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack);

    void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, int isforce, String nonce, long entranceTime, String testInfos, AbstractBusinessDataCallBack callBack);

    String getResultUrl(VideoQuestionLiveEntity detailInfo, int isforce, String nonce);

    void getStuTestResult(VideoQuestionLiveEntity detailInfo, int isPlayBack, AbstractBusinessDataCallBack callBack);
}
