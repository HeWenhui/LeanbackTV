package com.xueersi.parentsmeeting.modules.livevideo.config;
/**
*大班整合 直播配置
*@author chekun
*created  at 2019/9/26 12:36
*/
public interface BigLiveCfg {
    /**大班整合bizId — 讲座**/
    int BIGLIVE_BIZID_LECTURE = 2;
    /**大班整合bizId — 直播**/
    int BIGLIVE_BIZID_LIVE = 3;
    /** 大班整合 直播 当前整合版本号：（进入直播间是 判断接口返回的planVersion <= 此值才进入直播间，否则提示升级)
     *  每期大班需更新此字段值**/
    int BIGLIVE_CURRENT_ACCEPTPLANVERSION=2;
}
