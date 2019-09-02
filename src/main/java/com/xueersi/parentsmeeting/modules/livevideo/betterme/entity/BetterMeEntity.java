package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

/**
 * 英语小目标 本场小目标
 *
 * @author zhangyuansun
 * created  at 2018/12/24
 */
public class BetterMeEntity {
    /**
     * 目标的类型
     */
    private String aimType;
    /**
     * 实时目标的完成值
     */
    private String aimValue;

    private boolean isFirstReceive;

    public String getAimType() {
        return aimType;
    }

    public void setAimType(String aimType) {
        this.aimType = aimType;
    }

    public String getAimValue() {
        return aimValue;
    }

    public void setAimValue(String aimValue) {
        this.aimValue = aimValue;
    }

    public boolean isFirstReceive() {
        return isFirstReceive;
    }

    public void setFirstReceive(boolean firstReceive) {
        isFirstReceive = firstReceive;
    }
}
