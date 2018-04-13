package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
 * RolePlayer接口
 */
public interface RolePlayAction {

    /** 老师领读 */
    void teacherRead(String liveId, String stuCouId);

    /** 老师发题 */
    void teacherPushTest();


}
