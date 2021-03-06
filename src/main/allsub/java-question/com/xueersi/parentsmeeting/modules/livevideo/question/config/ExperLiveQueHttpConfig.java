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
    public static String LIVE_GET_COURSEWARE_TESTS_EN = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/getCourseTestInfo";
//    http://student.xueersi.com/science/AutoLive/submitCourseWareTest

    /** 提交课件-理科 */
    public static String LIVE_SUBMIT_COURSEWARE = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/submitCourseWareTest";

    /** 课件结果页-小学理科 */
    public static String LIVE_GET_STU_TESTS_RESULT = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/getStuTestResult";

    /** 获取回放events */
    public static String LIVE_GET_ENG_EVNET = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/getEngPlaybackEvents";

    /** 提交课件-英语 */
    public static String LIVE_SUBMIT_COURSEWARE_EN = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/commonSubmitMultiTest";
    public static String LIVE_SUBMIT_COURSEWARE_H5_EN = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/submitH5Eng";
    public static String LIVE_SUBMIT_COURSEWARE_VOICE_EN = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/submitH5VoiceEng";
    /** 文科新课件平台RolePlay获取题目信息 https://wiki.zhiyinlou.com/pages/viewpage.action?pageId=30434055 */
    public static String URL_ROLEPLAY_NEWARTS_TESTINFOS = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/getRolePlayEng";
    /** 文科新课件平台提交接口 https://wiki.zhiyinlou.com/pages/viewpage.action?pageId=30434055 */
    public static String URL_ROLEPLAY_NEWARTS_RESULT = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/submitRolePlayEng";
    public static String URL_LIVE_SEND_SPEECHEVALUATEARTS = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/submitSpeechEval42";

    /** 发送语音评测答案-二期，是否作答 */
    public static String URL_LIVE_SEND_SPEECHEVAL42_ANSWER = ExperLiveHttpConfig.LIVE_HOST_SCIENCE + "/AutoLive/speechEval42IsAnswered";
}
