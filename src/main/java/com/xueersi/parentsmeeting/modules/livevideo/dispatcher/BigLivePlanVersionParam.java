package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;
/**
*直播  是否是大班直播场次请求参数
*@author chekun
*created  at 2019/10/15 14:19
*/
public class BigLivePlanVersionParam {
  /**场次id**/
  int planId;
  /**直播类型id： 讲座：2  直播：3**/
  int bizId;

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
}
