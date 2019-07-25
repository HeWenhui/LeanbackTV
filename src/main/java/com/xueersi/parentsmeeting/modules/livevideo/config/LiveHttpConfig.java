package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.os.Environment;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;

public class LiveHttpConfig {
    /** live 域 */
    public static String LIVE_HOST = "https://live.xueersi.com";
    /** live 域理科 */
    public static String LIVE_HOST_SCIENCE = LIVE_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE;
    /** live 域文科 */
    public static String LIVE_HOST_LIBARTS = LIVE_HOST + "/" + ShareBusinessConfig.LIVE_LIBARTS;

    public static String HTTP_HOST_SCIENCE = LiveVideoConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE;
    public static String HTTP_HOST_LIBARTS = LiveVideoConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_LIBARTS;

    public static String HTTP_LIVE_CHINESE_HOST = "https://live.chs.xueersi.com";
    public static String HTTP_APP_ENGLISH_HOST = AppConfig.HTTP_HOST_ARTS;
    /** 文科新域名 */
    public static String chsCoursewareH5 = "https://live.chs.xueersi.com/Live/coursewareH5/";
    /** 文科新域名 */
    public static String chsSubjectiveTestAnswerResult = "https://live.chs.xueersi.com/Live/subjectiveTestAnswerResult/";
    /** 语文主观题结果页地址 */
    public static String subjectiveTestAnswerResult = LiveHttpConfig.LIVE_HOST_LIBARTS + "/Live/subjectiveTestAnswerResult/";

    /** 文理半身直播  理科家长旁听数据接口 */
    public static final String URL_HALFBODY_LIVE_STULIVEINFO = LiveHttpConfig.HTTP_HOST_SCIENCE + "/LiveCourse/getStuDateOfVisitedParentPage";
    /** 文理半身直播  文科科家长旁听数据接口 */
    public static final String URL_HALFBODY_LIVE_STULIVEINFO_ARTS = "https://app.chs.xueersi.com/LiveCourse/getStuDateOfVisitedParentPage";


    /** 理科提交对老师评价 */
    public static String URL_LIVE_SCIENCE_EVALUATE_TEACHER = HTTP_HOST_SCIENCE + "/LiveCourse/submitStuEvaluateTeacher";

    /** 理科获得对老师评价选项 */
    public static String URL_LIVE_SCIENCE_GET_EVALUATE_OPTION = HTTP_HOST_SCIENCE + "/LiveCourse/getEvaluateContent";
    /** 小语获得对老师评价选项 */
    public static String URL_LIVE_CHS_GET_EVALUATE_OPTION = "https://app.chs.xueersi.com/LiveCourse/getEvaluateInfo";
    /** 小语学生对老师评价 */
    public static String URL_LIVE_CHS_EVALUATE_TEACHER = "https://app.chs.xueersi.com/LiveCourse/submitEvaluate";
    /**
     * 文科新课件平台
     * 加载H5 页面地址
     */
    public static String URL_ARTS_H5_URL = "https://static.arts.xueersi.com/kejian/";

    public static String URL_ARTS_COURSE_H5_URL = "https://live.arts.xueersi.com/v2/live/courseWareH5";

    /** 文科新课件平台 RolePlay 页面加载地址 */
    public static String URL_NEWARTS_ROALPLAY_URL = "https://static.arts.xueersi.com/art_live/roleplay/";
    /** 全身直播新课件平台 RolePlay 页面加载地址 */
    public static String URL_NEWARTS_STANDROALPLAY_URL = "https://static.arts.xueersi.com/art_live/roleplayStand/";
    /** 文科新课件平台 语文跟读 页面加载地址 */
    public static String URL_NEWARTS_CHINESEREADING_URL = "https://static.arts.xueersi.com/art_live/Chinese-speech-touch/";

    /** 出门测表扬榜 */
    public static final String URL_LIVE_PRAISE_TUTOR_LIST = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LivePraise/getPraiseData";

    /** 回放获取弹幕接口(英语) */
    public static final String URL_ENGLISH_GET_VOICE_BARRAGE_MSG = LiveVideoConfig.APP_ARTS_HTTP_HOST + "/LiveCourse/getVoiceBarrageForPlayBack";

    /** 发送语音评测答案-文科新课件平台，是否作答 */
    public static String URL_LIVE_SEND_SPEECHEVALUATENEWARTS_ANSWER = HTTP_HOST_LIBARTS + "/v2/speechEval42/speechEval42IsAnswered";

    /** 文科新域名chs预加载 */
    public static String URL_LIVE_CHS_GET_ARTSMORE_COURSEWARE_URL = "https://app.chs.xueersi.com/LiveCourse/getCourseWareUrl";

    /** 得到h5课件-不区分文理 */
    public static String URL_LIVE_GET_WARE_URL = LiveVideoConfig.HTTP_HOST + "/LiveCourse/getCourseWareUrl";
    /** 理科一次多发课件 */
    public static String URL_LIVE_GET_MORE_WARE_URL = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/courseWarePreLoad";

    /** 半身直播体验课 试题 h5 地址 **/
    public static final String URL_HALFBODY_EXPERIENCE_LIVE_H5 = "https://expclass.xueersi.com/live-rewrite/courseware-sci/index.html";

    /**
     * 文科课件预加载
     */
    public static String URL_LIVE_GET_ARTS_COURSEWARE_URL = "https://app.chs.xueersi.com/LiveCourses/preLoadNewCourseWare";
    /**
     * 英语课件预加载
     */
    public static String URL_LIVE_GET_ENGLISH_COURSEWARE_URL = "https://app.arts.xueersi.com/preloading/preLoading";
    /**
     * 理科课件预加载
     */
    public static String URL_LIVE_GET_SCIENCE_COURSEWARE_URL = LiveVideoConfig.HTTP_HOST + "/science/LiveCourses/preLoadNewCourseWare";

    public static final String URL_GOLD_MICROPHONE_TO_AI = "https://app.chs.xueersi.com/LiveCourse/isGoldMicrophoneToAi";

    public static final String URL_IS_GOLD_MICROPHONE = "https://app.chs.xueersi.com/LiveCourse/setGoldMicrophoneData";

    /** NB加试实验 **/
    public static String URL_NB_LOGIN = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/stuLoginNB";
    /** 上传NB 实验答题结果 **/
    @Deprecated
    public static String URL_NB_RESULT_UPLOAD = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/receiveNBResult";
    /** 获取 Nb 试题信息 **/
    public static String URL_NB_COURSE_INFO = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getNBTestInfo";
    /** app端上传演讲秀视频 */
    public static final String SUPER_SPEAKER_UPLOAD_SPEECH_SHOW = "https://app.chs.xueersi.com/LiveCourse/uploadSpeechShow";
    /** app端摄像头开启状态 */
    public static final String SUPER_SPEAKER_SPEECH_SHOW_CAMERA_STATUS = "https://app.chs.xueersi.com/LiveCourse/speechShowCameraStatus";
    /** app端提交演讲秀 */
    public static final String SUPER_SPEAKER_SUBMIT_SPEECH_SHOW = "https://app.chs.xueersi.com/LiveCourse/submitSpeechShow";
    /** 超级演讲秀存储视频的地方 */
    public static final String SUPER_SPEAKER_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/parentsmeeting/livevideo/superSpeaker/";

    /** 文科获得对老师评价选项 */
    public static String URL_LIVE_ARTS_GET_EVALUATE_OPTION = HTTP_APP_ENGLISH_HOST + "/LiveCourse/showEvaluationOptions";
    /** 全身直播语音测评组内战况 */
    public static String URL_LIVE_SPEECH_TEAM_STATUS = HTTP_APP_ENGLISH_HOST + "/v2/standLiveStatus/getSpeechEvalAnswerTeamStatus";
    /** 全身直播语音答题的小组排名(新课件平台) */
    public static String URL_LIVE_NEWSTAND_ANSWER = HTTP_APP_ENGLISH_HOST + "/v2/standLiveStatus/getTestAnswerTeamStatus";
    /** 全身直播roleplayTop3排行榜 */
    public static String URL_LIVE_ROLE_TOP3 = HTTP_APP_ENGLISH_HOST + "/v2/standLiveStatus/getRolePlayAnswerTeamRank";
    /** 直播课的文科新版课件对接新提交接口- */
    public static String URL_LIVE_SUBMIT_NEWARTS_ANSWER = HTTP_APP_ENGLISH_HOST + "/v2/commonTest/submitMultiTest";
    /** 直播课的文科新版课件对接课件语音答题新提交接口- */
    public static String URL_LIVE_SUBMIT_NEWARTSH5_ANSWER = HTTP_APP_ENGLISH_HOST + "/v2/CourseH5Test/submitH5Voice";
    /** 发送语音评测答案-文科新课件 http://wiki.xesv5.com/pages/viewpage.action?pageId=12959505 */
    public static String URL_LIVE_SEND_SPEECHEVALUATEARTS = HTTP_APP_ENGLISH_HOST + "/v2/speechEval42/submitSpeechEval42";
    /** 全身直播语音测评Top3排行榜 */
    public static String URL_LIVE_ROLE_SPEECH_TEAM_TOP3 = HTTP_APP_ENGLISH_HOST + "/v2/standLiveStatus/getSpeechEvalAnswerTeamRank";

    /** 文科一发多题课件 */
    public static String URL_LIVE_GET_ARTSMORE_COURSEWARE_URL = HTTP_APP_ENGLISH_HOST + "/v2/preLoad/preLoading";
    /**
     * 直播间 文科差异化参数配置接口 (注：直播间文科差异化配置参数 获取接口)
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=12963335
     */
    public static String URL_ARTS_ROOM_INFO = HTTP_APP_ENGLISH_HOST + "/v2/Live/getInfoBaseData";
    /** 文科新课件平台 排名接口 */
    public static String URL_ARTS_TEAM_CLASS_RANK = HTTP_APP_ENGLISH_HOST + "/v2/LiveRank/getStuGroupTeamClassRanking";

    /** 文科学生对老师评价 */
    public static String URL_LIVE_ARTS_EVALUATE_TEACHER = HTTP_APP_ENGLISH_HOST + "/LiveCourse/submitStuEvaluateTeacher";
    /** 文科表扬榜 */
    public static String URL_ARTS_PRAISE_LIST = HTTP_APP_ENGLISH_HOST + "/LiveRank/getRankData";
    /** 文科新课件平台语文主观题提交测试题 */
    public static String URL_LIVE_SUBMIT_NEWARTSTEST_ANSWER = HTTP_APP_ENGLISH_HOST + "/v2/SubjectiveTest/submitTest";

    public static int HTTP_ERROR_ERROR = 1;
    public static int HTTP_ERROR_FAIL = 2;
    public static int HTTP_ERROR_NULL = 3;
}
