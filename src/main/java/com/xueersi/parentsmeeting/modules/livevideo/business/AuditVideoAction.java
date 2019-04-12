package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;

/**
 * Created by lyqai on 2017/7/25.
 */

public interface AuditVideoAction extends VideoAction {
    void onStudentLeave(boolean leave, String stuPushStatus);

    void onStudentError(String status, String msg);

    /**
     * 学生直播开始
     */
    @Deprecated
    void onStudentLiveStart(PlayServerEntity server);

    /**
     * 学生直播开始
     */
    void onStudentLiveUrl(String playUrl);

    void onKick();
}
