package com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.entity;


import com.xueersi.lib.framework.entity.BaseEvent;

/**
 * 红包相关事件
 *
 * @author chenkun
 * @version 1.0, 2018/6/25 下午2:58
 */

public class RedPackageEvent extends BaseEvent {


    public static  final  int STATE_CODE_SUCCESS = 1;

    public static  final  int STATE_CODE_FAILED = 0;

    private String mLiveId;

    private int goldNum;

    private String operateId;

    private int stateCode;


    /**
     *
     * @param mLiveId    直播间 直播id
     * @param goldNum    红包金币数
     * @param operateId  红包id
     * @param stateCode  状态值  成功/失败
     *
     */
    public RedPackageEvent(String mLiveId, int goldNum, String operateId, int stateCode) {
        this.mLiveId = mLiveId;
        this.goldNum = goldNum;
        this.operateId = operateId;
        this.stateCode = stateCode;
    }


    public String getLiveId() {
        return mLiveId;
    }

    public int getGoldNum() {
        return goldNum;
    }

    public String getOperateId() {
        return operateId;
    }

    public int getStateCode() {
        return stateCode;
    }
}
