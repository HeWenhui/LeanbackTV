package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.common.config.AppConfig;

/**
 * 直播模块配置,英语接口
 * Created by linyuqiang on 2018/11/19.
 * https://wiki.xesv5.com/pages/viewpage.action?pageId=14027641
 */
public class LiveVideoHttpEnConfig {
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    /** 学生获取战队信息 go */
    public static String URL_LIVE_SELF_TEAM = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/team-pk/getSelfTeamInfo";
    /** 学生获取战队信息 php */
    public static String URL_LIVE_GETENGLISH_PK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getEnglishPkGroup";
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    public static String URL_LIVE_REPORT_STUINFO = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/team-pk/reportStuInfo";
    /** 学生获取战队信息 http://wiki.xesv5.com/pages/viewpage.action?pageId=17708660 */
    public static String URL_LIVE_REPORT_InteractiveInfo = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/team-pk/reportInteractiveInfo";
    /** https://wiki.xesv5.com/pages/viewpage.action?pageId=14028119 */
    public static String URL_LIVE_UPDATA_GROUP = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/LiveCourses/updataEnglishPkGroup";
    public static String URL_LIVE_UPDATA_PK_RANK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/updataEnglishPkByTestId";
    public static String URL_LIVE_PK_TOTAL_RANK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getEnglishPkTotalRank";
    /** 学生点赞上报 go https://wiki.xesv5.com/pages/viewpage.action?pageId=14039396 */
    public static String URL_LIVE_REPORT_STULIKE = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/team-pk/reportStuLike";

    /** 英语小目标 -获取学生段位信息 */
    public static String URL_LIVE_GET_STU_SEGMENT = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getStuSegment";
    /** 英语小目标 -获取学生这节课小目标 */
    public static String URL_LIVE_BETTER_ME = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/betterMe";
    /** 英语小目标 -实时获取学生目标完成度 */
    public static String URL_LIVE_GET_STU_AIM_REALTIME_VAL = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getStuAimRealTimeVal";
    /** 英语小目标 -获取小目标结果 */
    public static String URL_LIVE_GET_STU_AIM_RESULT = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getStuAimResult";
}
