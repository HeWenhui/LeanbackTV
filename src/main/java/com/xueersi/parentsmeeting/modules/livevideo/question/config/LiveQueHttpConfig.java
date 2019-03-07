package com.xueersi.parentsmeeting.modules.livevideo.question.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

public class LiveQueHttpConfig {
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS = LiveHttpConfig.LIVE_HOST + "/science/LiveExam/getCourseWareTests";
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS_CN = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/getCourseWareTests";

    /** 提交课件-理科 */
    public static String LIVE_SUBMIT_COURSEWARE = LiveHttpConfig.LIVE_HOST + "/science/LiveExam/submitCourseWareTests";
    /** 提交课件-文科 */
    public static String LIVE_SUBMIT_COURSEWARE_CN = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveExam/submitCourseWareTests";
    public static String LIVE_SUBMIT_COURSEWARE_RESULT = LiveHttpConfig.LIVE_HOST + "/scistatic/middleSchoolCoursewareResultPage/app/dev/index.html";
}
