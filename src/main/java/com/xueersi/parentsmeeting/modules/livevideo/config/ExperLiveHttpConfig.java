package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.os.Environment;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;

public class ExperLiveHttpConfig {
    /** live 域 */
    public static String STUDENT_HOST = LiveVideoConfig.HTTP_HOST;
    /** live 域理科 */
    public static String LIVE_HOST_SCIENCE = STUDENT_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE;
    /** live 域文科 */
    public static String LIVE_HOST_LIBARTS = STUDENT_HOST + "/" + ShareBusinessConfig.LIVE_LIBARTS;

}
