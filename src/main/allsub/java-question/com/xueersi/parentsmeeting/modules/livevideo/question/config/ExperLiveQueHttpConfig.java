package com.xueersi.parentsmeeting.modules.livevideo.question.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.ExperLiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

/**
 * linyuqiang
 * 新课件的一些接口
 */
public class ExperLiveQueHttpConfig {
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/getTestInfos";
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS_CN = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/getCourseWareTests";
    /** 获得课件-文理 https://wiki.zhiyinlou.com/pages/viewpage.action?pageId=30434055 */
    public static String LIVE_GET_COURSEWARE_TESTS_EN = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/v2/commonTestInfo/getTestInfos";

}
