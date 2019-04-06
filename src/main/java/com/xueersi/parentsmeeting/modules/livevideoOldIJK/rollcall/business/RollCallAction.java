package com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.business;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

/**
 * @author linyuqiang
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

    /**
     * 自动签到
     * @param classSignEntity
     * @param classStartTime 课程开始时间
     * @param nowTime        当前时间
     */
    void autoSign(ClassSignEntity classSignEntity,long classStartTime,long nowTime);


    /**
     * 用户签到
     * @param entity
     * @param callBack 结果回调
     *
     */
    void userSign(ClassSignEntity entity, HttpCallBack callBack);

}
