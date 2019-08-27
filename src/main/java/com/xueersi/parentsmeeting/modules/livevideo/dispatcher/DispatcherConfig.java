package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import com.xueersi.common.config.AppConfig;

/**
 * Created by dqq on 2019/7/11.
 */
public class DispatcherConfig {
    public final static String SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE = "sp_en_english_stand_summercours_ewaresize";

    public final static String LIVE_PLAY_BACK_TUTOR_FLAGE = "_t";

    /*** 语文H5默认新地址 */
    public final static String URL_DEFAULT_CHS_H5 = "https://live.chs.xueersi.com/Live/coursewareH5/";

    public static final String stuId = "";

    public final static String URL_PUBLIC_LIVE_COURSE_QUESTION = AppConfig.HTTP_HOST + "/LiveLecture/getTestInfoForPlayBack";

    /** 大班整合 直播灰测检测 **/
    public final static String URL_BIGLIVE_BIG_LIVE_BUSINESS_TEST = AppConfig.HTTP_HOST_LECTUREPIE+"/app/isGrayLecture";
    /** 大班整合 直播灰测默认 **/
    public static  final int PUBLIC_GRAY_CONTROL_DEFALUT = -1;
    /** 大班整合 直播灰测普通直播 **/
    public static  final int PUBLIC_GRAY_CONTROL_COMMON = 0;
    /** 大班整合 直播灰测大班整合 **/
    public static  final int PUBLIC_GRAY_CONTROL_BIG_LIVE = 1;

}
