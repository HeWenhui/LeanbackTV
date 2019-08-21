package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 大班整合直播间-相关配置
 *
 * @author chenkun
 * @version 1.0, 2019-08-19 12:37
 */

public interface LiveIntegratedCfg {

    /**整合直播间 域名**/
    String HTTP_HOST = "https://studentlive.xueersi.com";

    /**进入直播间 接口**/
    String LIVE_ENTER =HTTP_HOST+"/v1/student/classroom/plan/enter";

    /**进入回放 接口**/
    String LIVE_PLAY_BACK_ENTER = HTTP_HOST+"/v1/student/classroom/playback/enter";


}
