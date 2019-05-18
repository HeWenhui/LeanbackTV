package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

public class PrimaryClassConfig {

    /**
     * 上报学生设备状态 http://wiki.xesv5.com/pages/viewpage.action?pageId=18557931
     */
    public final static String URL_LIVE_REPORT_USER_APP_STATUS = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/reportUserAppStatus";
    /**
     * 小班体验获取小组信息 http://wiki.xesv5.com/pages/viewpage.action?pageId=18557933
     */
    public final static String URL_LIVE_GET_MY_TEAM_INFO = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/getMyTeamInfo";
    /**
     * 举报学员  http://wiki.xesv5.com/pages/viewpage.action?pageId=18557929
     */
    public final static String URL_LIVE_REPORT_NAUGHTY_BOY = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/reportNaughtyBoy";
}
