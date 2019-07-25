package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;

public class LiveHttpConfig {
    /** live 域 */
    public static String LIVE_HOST = "https://live.xueersi.com";
    /** live 域理科 */
    public static String LIVE_HOST_SCIENCE = "https://live.xueersi.com/" + ShareBusinessConfig.LIVE_SCIENCE;
    /** live 域文科 */
    public static String LIVE_HOST_LIBARTS = "https://live.xueersi.com/" + ShareBusinessConfig.LIVE_LIBARTS;
    public static String HTTP_LIVE_CHINESE_HOST = "https://live.chs.xueersi.com";
    public static String HTTP_APP_ENGLISH_HOST = "https://app.arts.xueersi.com";
    public static int HTTP_ERROR_ERROR = 1;
    public static int HTTP_ERROR_FAIL = 2;
    public static int HTTP_ERROR_NULL = 3;
}
