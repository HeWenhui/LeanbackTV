package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 直播模块配置,英语接口
 * Created by linyuqiang on 2018/11/19.
 */
public class LiveVideoHttpEnConfig {
    /** 战队pk接口域名 */
    public static String TEAKMPK_HTTP_HOST = "https://wxen.arts.xueersi.com";
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    /** 学生获取战队信息 go */
    public static String URL_LIVE_SELF_TEAM = TEAKMPK_HTTP_HOST + "/team-pk/getSelfTeamInfo";
    /** 学生获取战队信息 php */
    public static String URL_LIVE_GETENGLISH_PK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourses/getEnglishPkRank";
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    public static String URL_LIVE_REPORT_STUINFO = TEAKMPK_HTTP_HOST + "/team-pk/reportStuInfo";
    /** https://wiki.xesv5.com/pages/viewpage.action?pageId=14028119 */
    public static String URL_LIVE_UPDATA_GROUP = TEAKMPK_HTTP_HOST + "/LiveCourses/updataEnglishPkGroup";
    public static String URL_LIVE_UPDATA_PK_RANK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/updataEnglishPkByTestId";
}
