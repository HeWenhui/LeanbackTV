package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 大班整合直播间-相关配置
 *
 * @author chenkun
 * @version 1.0, 2019-08-19 12:37
 */

public interface LiveIntegratedCfg {

    /**整合直播间 域名**/
    String HTTP_HOST = "http://student.live.xueersi.com";

    /**进入直播间 接口**/
    String LIVE_ENTER =HTTP_HOST+"/v1/student/classroom/plan/enter";


}
