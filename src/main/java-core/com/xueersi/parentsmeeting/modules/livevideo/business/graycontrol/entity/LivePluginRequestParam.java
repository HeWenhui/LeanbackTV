package com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity;


/**
 * 直播Plugin 接口请求参数定义
 *
 * @author shixiaoqiang
 */
public class LivePluginRequestParam {

    /**
     * 场次id
     */
    public int planId;
    /**
     * 业务Id : 2讲座3 直播
     */
    public int bizId;
    public  int isPlayback;

    public String url;
}
