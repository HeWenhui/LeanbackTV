package com.xueersi.parentsmeeting.modules.livevideo.config;

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
}
