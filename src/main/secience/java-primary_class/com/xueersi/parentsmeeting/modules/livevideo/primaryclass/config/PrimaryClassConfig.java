package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

public class PrimaryClassConfig {

    /**
     * 上报学生设备状态 http://wiki.xesv5.com/pages/viewpage.action?pageId=18557931
     */
//    public final static String URL_LIVE_REPORT_USER_APP_STATUS = LiveHttpConfig.LIVE_HOST + "/science/LiveCourses/reportUserAppStatus";
    public final static String URL_LIVE_REPORT_USER_APP_STATUS = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/reportUserAppStatus";
    /**
     * 小班体验获取小组信息 http://wiki.xesv5.com/pages/viewpage.action?pageId=18557933
     */
    public final static String URL_LIVE_GET_MY_TEAM_INFO = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/getMyTeamInfo";
//    public final static String URL_LIVE_GET_MY_TEAM_INFO  = LiveHttpConfig.LIVE_HOST  + "/science/LiveCourses/getMyTeamInfo";
}
