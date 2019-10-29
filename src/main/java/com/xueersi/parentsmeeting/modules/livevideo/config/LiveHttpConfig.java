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
    public static String chsCoursewareH5 = HTTP_LIVE_CHINESE_HOST + "/Live/coursewareH5/";
    /** 文科新域名 */
    public static String chsSubjectiveTestAnswerResult = HTTP_LIVE_CHINESE_HOST + "/Live/subjectiveTestAnswerResult/";
    /** 语文主观题结果页地址 */
    public static String subjectiveTestAnswerResult = LiveHttpConfig.LIVE_HOST_LIBARTS + "/Live/subjectiveTestAnswerResult/";

    /** 文理半身直播  理科家长旁听数据接口 */
    public static final String URL_HALFBODY_LIVE_STULIVEINFO = LiveHttpConfig.HTTP_HOST_SCIENCE + "/LiveCourse/getStuDateOfVisitedParentPage";
    /** 文理半身直播  文科科家长旁听数据接口 */
    public static final String URL_HALFBODY_LIVE_STULIVEINFO_ARTS = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/getStuDateOfVisitedParentPage";


    /** 理科提交对老师评价 */
    public static String URL_LIVE_SCIENCE_EVALUATE_TEACHER = HTTP_HOST_SCIENCE + "/LiveCourse/submitStuEvaluateTeacher";

    /** 理科获得对老师评价选项 */
    public static String URL_LIVE_SCIENCE_GET_EVALUATE_OPTION = HTTP_HOST_SCIENCE + "/LiveCourse/getEvaluateContent";
    /** 小语获得对老师评价选项 */
    public static String URL_LIVE_CHS_GET_EVALUATE_OPTION = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/getEvaluateInfo";
    /** 小语学生对老师评价 */
    public static String URL_LIVE_CHS_EVALUATE_TEACHER = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/submitEvaluate";
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
    public static String URL_LIVE_CHS_GET_ARTSMORE_COURSEWARE_URL = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/getCourseWareUrl";

    /** 得到h5课件-不区分文理 */
    public static String URL_LIVE_GET_WARE_URL = LiveVideoConfig.HTTP_HOST + "/LiveCourse/getCourseWareUrl";
    /** 理科一次多发课件 */
    public static String URL_LIVE_GET_MORE_WARE_URL = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/courseWarePreLoad";

    /** 半身直播体验课 试题 h5 地址 **/
    public static final String URL_HALFBODY_EXPERIENCE_LIVE_H5 = "https://expclass.xueersi.com/live-rewrite/courseware-sci/index.html";

    /**
     * 文科课件预加载
     */
    public static String URL_LIVE_GET_ARTS_COURSEWARE_URL = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourses/preLoadNewCourseWare";
    /**
     * 英语课件预加载
     */
    public static String URL_LIVE_GET_ENGLISH_COURSEWARE_URL = "https://app.arts.xueersi.com/preloading/preLoading";
    /**
     * 理科课件预加载
     */
    public static String URL_LIVE_GET_SCIENCE_COURSEWARE_URL = LiveVideoConfig.HTTP_HOST + "/science/LiveCourses/preLoadNewCourseWare";

    public static final String URL_GOLD_MICROPHONE_TO_AI = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/isGoldMicrophoneToAi";

    public static final String URL_IS_GOLD_MICROPHONE = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/setGoldMicrophoneData";

    /** NB加试实验 **/
    public static String URL_NB_LOGIN = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/stuLoginNB";
    /** 上传NB 实验答题结果 **/
    @Deprecated
    public static String URL_NB_RESULT_UPLOAD = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/receiveNBResult";
    /** 获取 Nb 试题信息 **/
    public static String URL_NB_COURSE_INFO = LiveHttpConfig.LIVE_HOST_SCIENCE + "/LiveExam/getNBTestInfo";
    /** app端上传演讲秀视频 */
    public static final String SUPER_SPEAKER_UPLOAD_SPEECH_SHOW = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/uploadSpeechShow";
    /** app端摄像头开启状态 */
    public static final String SUPER_SPEAKER_SPEECH_SHOW_CAMERA_STATUS = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/speechShowCameraStatus";
    /** app端提交演讲秀 */
    public static final String SUPER_SPEAKER_SUBMIT_SPEECH_SHOW = LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST + "/LiveCourse/submitSpeechShow";
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

    /** 理科互动题 */
    public static String URL_LIVE_MULTI_TEST = LIVE_HOST + "/Live/getMultiTestPaper";
    /** 理科互动题-老师自传 */
    @Deprecated
    public static String URL_LIVE_TEA_UPLOAD_TEST = LIVE_HOST_SCIENCE + "/Live/teacherUploadTestForAPP/";
    /**
     * 文科三分屏上传精彩瞬间截图url，半身直播走理科的接口
     */
    public static final String ART_TRIPLE_WONDERFUL_MOMENT = HTTP_LIVE_CHINESE_HOST + "/ExamReport/uploadWonderfulMoment";

    /** 课后评价教师文案获取 */
    public static String URL_LIVE_COURSE_GETEVALUATE = AppConfig.HTTP_HOST + "/science/LiveCourse/getEvaluateContentNew";
    /**新需求评价h5页面类型的，判断是否下发课后评价*/
    public static String URL_COURSE_EVALUATE= "http://npsopenapi.xueersi.com/App/nps/isRuleTrigger";
    /** 语文H5默认新地址 */
    public final static String URL_DEFAULT_CHS_H5 = HTTP_LIVE_CHINESE_HOST + "/Live/coursewareH5/";
    public final static String SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE = "sp_en_english_stand_summercours_ewaresize";

    /** 语文主观题获取答案的地址 */
    public static String URL_NEWARTS_SUBMITRESULT_H5 = "https://app.arts.xueersi" +
            ".com/v2/SubjectiveTest/subjectiveTestAnswerResult";

    //    public static String URL_NEWARTS_SUBMITRESULT_H5 = "https://laoshi.xueersi
// .com/libarts/v2/SubjectiveTest/subjectiveTestAnswerResult";
    /**体验课主观题提交答案*/
//    public static String LIVE_EXPE_SUBMIT_SUBJECT = AppConfig.HTTP_HOST_TEAMPK + "/science/AutoLive/subjectiveSubmit";
    /** 体验课非h5语音互动题提交答案*/
//    public static String URL_EXPE_SUBMIT_SPEECHEVAL = AppConfig.HTTP_HOST_TEAMPK +
// "/science/AutoLive/submitSpeechEval";
    /** 体验课h5语音评测提交答案*/
//    public static String URL_EXPE_SUBMIT_TEST_H5_ANSWER = AppConfig
// .HTTP_HOST_TEAMPK+"/science/AutoLive/submitCourseWareH5AnswerUseVoice";
    /** 获取标记点列表 */
    public static String URL_LIVE_GET_MARK_POINTS = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/getMarkpoint";
    /** 保存标记点 */
    public static String URL_LIVE_SET_MARK_POINTS = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/setMarkpoint";
    /** 删除标记点 */
    public static String URL_LIVE_DELETE_MARK_POINTS = LiveVideoConfig.HTTP_HOST +
            "/science/LiveCourse/deleteMarkpoint";
    /** 体验课播放器上传心跳时间 */
    public static String URL_EXPERIENCE_LIVE_ONLINETIME = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/visitTime";
    /** 回放式体验课上传心跳时间 */
    public static String URL_PLAYBACKPLAYTIME = LiveVideoConfig.HTTP_HOST + "/ExpPlayback/visitTime";
    /** RolePlay请求对话信息 */
    public static String URL_ROLEPLAY_TESTINFOS = LiveVideoConfig.HTTP_HOST + "/libarts/LiveCourse/getRolePlay";
    /** 文科新课件平台RolePlay获取题目信息 */
    public static String URL_ROLEPLAY_NEWARTS_TESTINFOS = "https://app.arts.xueersi.com/v2/MultiRolePlay/getRolePlay";
    /** 提交接口 */
    public static String URL_ROLEPLAY_RESULT = LiveVideoConfig.HTTP_HOST + "/libarts/LiveCourse/submitRolePlay";
    /** 文科新课件平台提交接口 http://wiki.xesv5.com/pages/viewpage.action?pageId=12968144 */
    public static String URL_ROLEPLAY_NEWARTS_RESULT = "https://app.arts.xueersi.com/v2/MultiRolePlay/submitRolePlay";
    /** 讲座直播获取更多课程 */
    public static String URL_LECTURELIVE_MORE_COURSE = LiveVideoConfig.HTTP_HOST + "/LiveLecture/getAdCase";
    /** 获取时时间戳 */
    public static String URL_LIVE_GET_CURTIME = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/getCurTimestamp";
    /** 获取体验直播课红包 */
    public static String URL_AUTO_LIVE_RECEIVE_GOLD = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/receiveGold";
    /** 获取体验课聊天记录 */
    /** 过期20190723 */
    @Deprecated
    public static String URL_AUTO_LIVE_MSGS = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/getLiveCourseMsgs";
    /** 提交体验课交互记录 */
    @Deprecated
    public static String URL_AUTO_LIVE_RECORD_INTERACT = AppConfig.HTTP_HOST_TEAMPK +
            "/secience/AutoLive/recordInteract";
    /** 获取讲座直播回放中更多课程的广告信息 */
    public static String URL_LEC_AD_CASE = LiveVideoConfig.HTTP_HOST + "/LiveLecture/getAdCase";
    /** 获取体验课学习报告 */
    public static String URL_AUTO_LIVE_FEAD_BACK = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/learnFeedback";
    /** 提交体验课学习反馈 */
    public static String URL_AUTO_LIVE_LEARN_FEED_BACK = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/submitFeedback";
    /**
     * 提交体验课退出反馈
     */
    public static String URL_AUTO_LIVE_QUIT_FEED_BACK = AppConfig.HTTP_HOST_TEAMPK + "/science/AutoLive/submitClassQuitFeedback";
    /**
     * 提交体验课新手引导页是否展示
     */
    public static String URL_AUTO_LIVE_NOVIC_GUIDE = AppConfig.HTTP_HOST_TEAMPK + "/science/AutoLive/submitNoviceGuide";
    /**
     * 中学激励系统获取当前连对和最高连对
     */
    public static final String EVEN_DRIVE_PAIR_INFO = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Stimulation/getEvenPairInfo";
    /**
     * 中学激励系统获取连对榜单
     */
    @Deprecated
    public static final String EVEN_DRIVE_PAIR_LIST = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Stimulation/evenPairList";
    /** 中学激励系统学报接口地址 */
    @Deprecated
    public static final String EVEN_DRIVE_STYDU_REPORT = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Stimulation/getJournal";
    /** 中学激励系统点赞地址 */
    @Deprecated
    public static final String EVEN_DRIVE_LIKE = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Stimulation/thumbsUp";
    /**
     * 语音评测地址
     */
    @Deprecated
    public static String SPEECH_URL = LiveHttpConfig.LIVE_HOST + "/LivePlayBack/speechEvalResult/";
    /** 获取学习报告-讲座 */
    public final static String URL_LIVE_GET_FEED_BACK = LiveVideoConfig.HTTP_HOST + "/LiveLecture/getFeedback";
    /** 获取红包金币 */
    public final static String URL_STUDY_GET_RED_PACKET = LiveVideoConfig.HTTP_HOST +
            "/MyCourse/receiveLiveTutoringGold";
    /** 直播回放提交答案地址 */
    public final static String URL_STUDY_SAVE_TEST_RECORD = LiveVideoConfig.HTTP_HOST +
            "/MyCourse/submitLiveTutoringTestAnswer";
    /** 直播辅导用户在线心跳 */
    @Deprecated
    public final static String URL_LIVE_TUTORIAL_USER_ONLINE = LiveVideoConfig.HTTP_HOST + "/LiveTutorial/userOnline";
    /** 公开直播用户在线心跳 */
    public final static String URL_LIVE_LECTURE_USER_ONLINE = LiveVideoConfig.HTTP_HOST + "/LiveLecture/userOnline";
    /** 直播辅导领取金币 */
    @Deprecated
    public final static String URL_LIVE_TUTORIAL_GOLD = LiveVideoConfig.HTTP_HOST + "/LiveTutorial/receiveGold";
    /** 公开直播领取金币 */
    public final static String URL_LIVE_LECTURE_GOLD = LiveVideoConfig.HTTP_HOST + "/LiveLecture/receiveGold";
    /** 直播辅导献花 */
    @Deprecated
    public final static String URL_LIVE_TUTORIAL_PRAISE_TEACHER = LiveVideoConfig.HTTP_HOST +
            "/LiveTutorial/praiseTeacher";
    /** 直播讲座献花 */
    public final static String URL_LIVE_LECTURE_PRAISE_TEACHER = LiveVideoConfig.HTTP_HOST +
            "/LiveLecture/praiseTeacher";
    /** 得到语音评测试题 */
    public final static String URL_LIVE_GET_SPEECHEVAL = LiveVideoConfig.HTTP_HOST + "/StudyCenter/getSpeechEvalInfo";
    /** 发送语音评测答案 */
    @Deprecated
    public final static String URL_LIVE_SEND_SPEECHEVAL = LiveVideoConfig.HTTP_HOST + "/StudyCenter/submitSpeechEval";
    /** 直播辅导发送语音评测答案 */
    @Deprecated
    public final static String URL_LIVE_TUTORIAL_SUBMIT_TEST_ANSWER = LiveVideoConfig.HTTP_HOST +
            "/LiveTutorial/submitTestAnswer";
    /** 公开直播提交测试题 */
    public final static String URL_LIVE_LECTURE_SUBMIT_TEST_ANSWER = LiveVideoConfig.HTTP_HOST +
            "/LiveLecture/submitTestAnswer";

    /** 播放器异常日志 */
    public final static String URL_LIVE_ON_LOAD_LOGS = "https://netlive.xesimg.com/10011.gif";
    /** 得到广告信息 */
    public final static String URL_LIVE_GET_LEC_AD = LiveVideoConfig.HTTP_HOST + "/LiveLecture/getAdOnLL";

    /**
     * 体验课互动题提交答案
     */
    public static String LIVE_EXPE_SUBMIT = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/submitTestAnswer";
    /** 提交互动题答案 */
    public final static String URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION = LiveVideoConfig.HTTP_HOST +
            "/LiveLecture/submitTestAnswerForPlayBack";
    /** 过期20190723 */
    @Deprecated
    public final static String URL_PUBLIC_LIVE_COURSE_GET_MESSAGE = LiveVideoConfig.HTTP_HOST +
            "/IrcMessage/getLiveLectureMsgs";
    //英語获取连对数接口
    public final static String UEL_ENGLISH_EVEN_DRIVE_MSG = "https://app.arts.xueersi.com/v2/stimulation/getRightNums";
    //chs新课件获取连对数量
    public final static String URL_CHINESE_NEW_ARTS_EVEN_DRIVE_MSG = "https://student.chs.xueersi.com/Stimulation/getRightNums";
    //science新课件获取连对数量
    public final static String URL_SCIENCE_NEW_ARTS_EVEN_DRIVE_MSG = "https://student.xueersi.com/science/Stimulation/getRightNums";
    //chs新课件获取自传互动题连对数量
    public final static String URL_CHINESE_SELF_UPLOAD_ARTS_EVEN_DRIVE_MSG = "http://student.chs.xueersi.com/Stimulation/getTestRightNum";
    //science新课件获取自传互动题连对数量
    public final static String URL_SCIENCE_SELF_UPLOAD_ARTS_EVEN_DRIVE_MSG = "http://student.xueersi.com/science/Stimulation/getRightNums";
    public static int HTTP_ERROR_ERROR = 1;
    public static int HTTP_ERROR_FAIL = 2;
    public static int HTTP_ERROR_NULL = 3;
}
