package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 大班整合直播间入口请求参数
 *
 * @author chenkun
 * @version 1.0, 2019-08-20 11:50
 */

public class BigLiveEnterParam {

    /**场次id**/
    private int planId;
    /**直播类型**/
    private int bizId;
    /**课程id:讲座课  可不传**/
    private int stuCouId;

    private int acceptPlanVersion;

    public BigLiveEnterParam(){}


    public BigLiveEnterParam(int planId, int bizId, int stuCouId) {
        this.planId = planId;
        this.bizId = bizId;
        this.stuCouId = stuCouId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public int getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(int stuCouId) {
        this.stuCouId = stuCouId;
    }

    public int getAcceptPlanVersion() {
        return acceptPlanVersion;
    }

    public void setAcceptPlanVersion(int acceptPlanVersion) {
        this.acceptPlanVersion = acceptPlanVersion;
    }
}
