package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

/**
 * 小班体验的一些配置
 *
 * @author linyuqiang
 */
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

    /** 老师操作类型，视频 */
    public static int MMTYPE_VIDEO = 1;
    /** 老师操作类型，音频 */
    public static int MMTYPE_AUDIO = 2;
    /** 自己是视频宽度 */
    public static int VIDEO_WIDTH = 320;
    /** 自己是视频高度 */
    public static int VIDEO_HEIGHT = 240;
    /** 视频状态 远端视频正常*/
    public static int VIDEO_STATE_1 = 1;
    /** 视频状态 端视频卡住*/
    public static int VIDEO_STATE_2 = 2;
}
