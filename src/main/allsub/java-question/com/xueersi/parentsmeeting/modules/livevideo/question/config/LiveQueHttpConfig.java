package com.xueersi.parentsmeeting.modules.livevideo.question.config;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoChConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.Config;

/**
 * linyuqiang
 * 新课件的一些接口
 */
public class LiveQueHttpConfig {
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getCourseWareTests";
    /** 获得课件-理科 */
    public static String LIVE_GET_COURSEWARE_TESTS_CN = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/getCourseWareTests";
    /** 获得课件-英语 https://wiki.zhiyinlou.com/pages/viewpage.action?pageId=31364820 */
    public static String LIVE_GET_COURSEWARE_TESTS_EN = LiveHttpConfig.HTTP_APP_ENGLISH_HOST + "/v2/commonTestInfo/getTestInfos";

    // http://live.xueersi.com/science/Tutorship/getCourseWareTest
    // ?stuId=58074&stuCouId=9649079&packageId=59148&liveId=376269&packageSource=2&packageAttr=1&releasedPageInfos=%5B%7B%2272853%22%3A%5B%2221%22%2C%2220188%22%5D%7D%5D&benchmark=0&json=1


    /** 提交课件-理科 */
    public static String LIVE_SUBMIT_COURSEWARE = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/submitCourseWareTests";
    /** 提交课件-文科 */
    public static String LIVE_SUBMIT_COURSEWARE_CN = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/submitCourseWareTests";
    /** 提交课件-英语 http://wiki.xesv5.com/pages/viewpage.action?pageId=12954171 */
    public static String LIVE_SUBMIT_COURSEWARE_EN = LiveHttpConfig.HTTP_APP_ENGLISH_HOST + "/v2/commonTest/submitMultiTest";
    public static String LIVE_SUBMIT_COURSEWARE_VOICE_EN = LiveHttpConfig.HTTP_APP_ENGLISH_HOST + "/v2/CourseH5Test/submitH5";
    /** 提交课件—英语小组互动 */
    public static String LIVE_SUBMIT_COURSEWARE_GROUPGAME_EN = LiveHttpConfig.HTTP_APP_ENGLISH_HOST + "/v2/CourseH5Test/submitGroupGame";

    /** 课件结果页-理科本地 */
    public static String LIVE_SUBMIT_COURSEWARE_RESULT_FILE = "file:///android_asset/newcourse_result/sec/middleSchoolCourseware/index.html";
    /** 课件结果页-文科本地 */
//    public static String LIVE_SUBMIT_COURSEWARE_RESULT_FILE_CN = "file:///android_asset/newcourse_result/chs/middleSchoolCourseware/index.html";
    /** 课件结果页-理科线上 */
    @Deprecated
    public static String LIVE_SUBMIT_COURSEWARE_RESULT = LiveHttpConfig.LIVE_HOST + "/scistatic/middleSchoolCoursewareResultPage/app/dev/index.html";
    /** 课件结果页-小学理科 */
    public static String LIVE_GET_STU_TESTS_RESULT = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getStuTestResult";
    /** 课件结果页-小学文科 */
    public static String LIVE_GET_STU_TESTS_RESULT_CN = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/getStuTestResult";
//    public static String LIVE_GET_STU_TESTS_RESULT = "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example" + "/getStuTestResult";


    /** 辅导老师结果页 */
    public static String LIVE_SUBMIT_COURSEWARE_RESULT_TUTOR_FILE = "file:///android_asset/newcourse_tutor/index.html";

    /** 辅导 获取试题 */
    public static String LIVE_GET_COURSEWARE_TUTOR_TESTS = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Tutorship/getCourseWareTest";
    /** 辅导 提交试题 */
    public static String LIVE_GET_COURSEWARE_SUBMIT_TESTS = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Tutorship/submitCourseWareTest";
    /** 辅导 获取课件结果 */

    /** 投票查询是否已作答 */
    public static String LIVE_IS_SUBMIT_COURSEWARE_VOTE = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/vote/stuCheckCommit";
    /** 投票提交投票 */
    public static String LIVE_SUBMIT_COURSEWARE_VOTE = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/vote/stuCommitVote";
    /** 投票学生点击折叠次数统计 */
    public static String LIVE_SUBMIT_COURSEWARE_VOTE_FOLD_COUNT = LiveVideoConfig.APP_ARTS_WXEN_HTTP_HOST + "/vote/stuCommitFold";
    public static String LIVE_GET_COURSEWARE_TUTOR_RESULT = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Tutorship/getStuTestResult";

    /** 辅导 加载课件 */
    public static String TUTOR_COURSE_URL = LiveHttpConfig.LIVE_HOST + "/scistatic/outDoorTest/index.html";

    /** 小组互动-上报答题信息 */
    public static String LIVE_GROUPGAME_REPORT = "/report/operate";

    /** 理科新投票提交 */
    public static String LIVE_SCIENCE_VOTE_SUBMIT = LiveHttpConfig.HTTP_HOST_SCIENCE + "/LiveCourse/commitVote";

}
