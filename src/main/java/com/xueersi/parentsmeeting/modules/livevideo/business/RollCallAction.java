package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

/**
 * Created by linyuqiang on 2016/9/23.
 * 点名，同学点名事件
 */
public interface RollCallAction {
    /**
     * 点名
     *
     * @param stop 是不是取消
     */
    void onRollCall(boolean stop);

    /**
     * 点名
     *
     * @param classSignEntity
     */
    void onRollCall(ClassSignEntity classSignEntity);

    /**
     * 其他同学点名
     *
     * @param classmateEntity
     */
    void onClassmateRollCall(ClassmateEntity classmateEntity);

    /** 停止签到 */
    void stopRollCall();

    /**强制结束签到*/
    void forceCloseRollCall();

}
