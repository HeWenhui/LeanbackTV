package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;

public class AuditRoomConfig {
    /** 互动题-错误 */
    public final static int QUESTION_WRONG = 0;
    /** 互动题-正确 */
    public final static int QUESTION_RIGHT = 1;
    /** 互动题-半对 */
    public final static int QUESTION_HALF_RIGHT = 2;
    /** 我的排名 */
    public final static int RATE_MY = 1;
    /** 小组排名 */
    public final static int RATE_TEAM = 2;
    /** 班级排名 */
    public final static int RATE_CLASS = 3;
    /** 有直播 */
    public final static int LIVE_COURSE_HAS = 1;
    /** 无直播 */
    public final static int LIVE_COURSE_NONE = 0;
    /** 旁听课堂数据-理科 */
    public final static String URL_LIVE_COURSE_LIVE_DETAIL_A = AppConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_LIBARTS + "/LiveCourse/getLiveDetailData";
    /** 旁听课堂数据-文科 */
    public final static String URL_LIVE_COURSE_LIVE_DETAIL_S = AppConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE + "/LiveCourse/getLiveDetailData";
    /** 是否有旁听课堂数据 */
    public final static String URL_HAS_LIVE_COURSE = AppConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE + "/LiveCourse/isHasLiveCourse";
    /** 小学语文三分屏替换 */
    public final static String URL_LIVE_CHS_COURSE = "https://app.chs.xueersi.com/LiveCourse/getLiveDetailData";
    /** 最后一次进群时间 */
    public final static String SP_CHAT_ROOM_LOGIN_LAST_TIME = "sp_chat_room_login_last_time";
    /** 直播数据 */
    public final static String SP_CHAT_ROOM_LIVE_DATA = "sp_chat_room_live_data";
}
