package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

/**
 * 英语小目标 实时获取学生目标完成度
 *
 * @author zhangyuansun
 * created  at 2018/12/12
 */
public class AimRealTimeValEntity {
    /**
     * 目标的类型
     */
    private String type;
    /**
     * 实时目标的完成值
     */
    private String realTimeVal;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRealTimeVal() {
        return realTimeVal;
    }

    public void setRealTimeVal(String realTimeVal) {
        this.realTimeVal = realTimeVal;
    }
}
