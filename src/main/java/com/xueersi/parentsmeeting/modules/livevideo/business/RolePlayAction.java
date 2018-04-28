package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * RolePlayer功能接口
 */
public interface RolePlayAction {

    /** 老师领读 */
    void teacherRead(String liveId, String stuCouId);

    /** 老师发题 */
    void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity);

    /** 试题id */
    String getQuestionId();

    /** 老师停止发题 */
    void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity);

    /** 走人机RolePlay */
    void onGoToRobot();
}
