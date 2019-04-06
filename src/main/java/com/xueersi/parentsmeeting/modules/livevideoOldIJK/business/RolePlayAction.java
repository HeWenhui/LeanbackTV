package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.VideoQuestionLiveEntity;

/**
 * RolePlayer功能接口
 */
public interface RolePlayAction {

    /**
     * 老师领读
     */
    void teacherRead(String liveId, String stuCouId, String nonce);

    /**
     * 老师发题
     */
    void teacherPushTest(VideoQuestionLiveEntity videoQuestionLiveEntity);

    /**
     * 试题id
     */
    String getQuestionId();

    /**
     * 老师停止发题
     */
    void onStopQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, String nonce);

    /**
     * 走人机RolePlay之后关socket
     */
    void onGoToRobot();
    /**
     * 走人机RolePlay
     */
    void goToRobot();

    void setOnError(OnError onError);

    interface OnError {
        void  onError(BaseVideoQuestionEntity testId);
    }

}
