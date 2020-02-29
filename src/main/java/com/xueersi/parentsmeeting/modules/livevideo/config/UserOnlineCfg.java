package com.xueersi.parentsmeeting.modules.livevideo.config;


/**
*用户在线时长相关参数配置
*@author chekun
*created  at 2019/10/29 17:32
*/
public class UserOnlineCfg {
    /**用户在线时长 Android 端 标识**/
    public static final int USER_ONLINE_FROMTYPE = 4;
    /**用户在线时长 老师模式-未知 **/
    public static final int USER_ONLINE_TEACHER_MODE_UNKOWN = 0;
    /**用户在线时长 老师模式- 主讲**/
    public static final int USER_ONLINE_TEACHER_MODE_MAIN = 1;
    /**用户在线时长 老师模式-辅导**/
    public static final int USER_ONLINE_TEACHER_MODE_COUNT = 2;

    /**用户在线时长  课前辅导态**/
    public static final int USER_ONLINE_TEACHER_MODE_COUNT_BEFOR = 3;
    /**用户在线时长 课后辅导态*/
    public static final int USER_ONLINE_TEACHER_MODE_COUNT_AFTER = 4;

    /**用户在线时长 默认观看时间**/
    public static final int USER_DURATION_INTERVAL_DEFUALT = 60;

    /**课前辅导流*/
    public static final int STREAM_MODE_COUNT_BEFOR = 0;
    /**主讲视频流*/
    public static final int STREAM_MODE_MAIN = 1;
    /**课后辅导流*/
    public static final int STREAM_MODE_COUNT_AFTER = 2;


}
