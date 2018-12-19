package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.common.config.AppConfig;

/**
 * 直播模块配置,英语接口
 * Created by linyuqiang on 2018/11/19.
 * https://wiki.xesv5.com/pages/viewpage.action?pageId=14027641
 */
public class LiveVideoHttpEnConfig {
    /** 战队pk接口域名 */
    public static String TEAKMPK_HTTP_HOST = "http://10.99.2.107:4001";
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    /** 学生获取战队信息 go */
    public static String URL_LIVE_SELF_TEAM = TEAKMPK_HTTP_HOST + "/team-pk/getSelfTeamInfo";
    /** 学生获取战队信息 php */
    public static String URL_LIVE_GETENGLISH_PK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getEnglishPkGroup";
    //    public static String URL_LIVE_SELF_TEAM = "http://teampk.arts.xesv5.com/getSelfTeamInfo";
    public static String URL_LIVE_REPORT_STUINFO = TEAKMPK_HTTP_HOST + "/team-pk/reportStuInfo";
    /** https://wiki.xesv5.com/pages/viewpage.action?pageId=14028119 */
    public static String URL_LIVE_UPDATA_GROUP = TEAKMPK_HTTP_HOST + "/LiveCourses/updataEnglishPkGroup";
    public static String URL_LIVE_UPDATA_PK_RANK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/updataEnglishPkByTestId";
    public static String URL_LIVE_PK_TOTAL_RANK = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getEnglishPkTotalRank";

    /** 英语小目标 -获取学生段位信息 */
    public static String URL_LIVE_GET_STU_SEGMENT = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourses/getStuSegment";
    /** 英语小目标 -获取学生这节课小目标 */
    public static String URL_LIVE_BETTER_ME = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourses/betterMe";
    /** 英语小目标 -实时获取学生目标完成度 */
    public static String URL_LIVE_GET_STU_AIM_REALTIME_VAL = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourses/getStuAimRealTimeVal";
    /** 英语小目标 -获取小目标结果 */
    public static String URL_LIVE_GET_STU_AIM_RESULT = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourses/getStuAimResult";

    static {
        if (AppConfig.DEBUG) {
            URL_LIVE_GET_STU_SEGMENT = "http://10.99.2.49:7300/mock/5bc837b4e2d3f348f1284293/example/getStuSegment";
            URL_LIVE_BETTER_ME = "http://10.99.2.49:7300/mock/5bc837b4e2d3f348f1284293/example/betterMe";
            URL_LIVE_GET_STU_AIM_RESULT = "http://10.99.2.49:7300/mock/5bc837b4e2d3f348f1284293/example/getStuAimResult";
            URL_LIVE_GET_STU_AIM_REALTIME_VAL = "http://10.99.2.49:7300/mock/5bc837b4e2d3f348f1284293/example/getStuAimRealTimeVal";
        }
    }
}
