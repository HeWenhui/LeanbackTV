package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.config.AppConfig;

/**
 * 直播模块配置
 * URL前缀：接口连接地址
 * SP前缀：ShareDataPreferences缓存Key
 * UMS前缀:大数据统计KEY
 * Created by Administrator on 2017/3/31.
 */
public class LiveVideoConfig {
    /** 直播辅导用户在线心跳 */
    public final static String URL_LIVE_TUTORIAL_USER_ONLINE = AppConfig.HTTP_HOST + "/LiveTutorial/userOnline";
    /** 公开直播用户在线心跳 */
    public final static String URL_LIVE_LECTURE_USER_ONLINE = AppConfig.HTTP_HOST + "/LiveLecture/userOnline";
    /** 直播辅导领取金币 */
    public final static String URL_LIVE_TUTORIAL_GOLD = AppConfig.HTTP_HOST + "/LiveTutorial/receiveGold";
    /** 公开直播领取金币 */
    public final static String URL_LIVE_LECTURE_GOLD = AppConfig.HTTP_HOST + "/LiveLecture/receiveGold";
    /** 直播辅导献花 */
    public final static String URL_LIVE_TUTORIAL_PRAISE_TEACHER = AppConfig.HTTP_HOST + "/LiveTutorial/praiseTeacher";
    /** 直播讲座献花 */
    public final static String URL_LIVE_LECTURE_PRAISE_TEACHER = AppConfig.HTTP_HOST + "/LiveLecture/praiseTeacher";
    /** 得到语音评测试题 */
    public final static String URL_LIVE_GET_SPEECHEVAL = AppConfig.HTTP_HOST + "/StudyCenter/getSpeechEvalInfo";
    /** 发送语音评测答案 */
    public final static String URL_LIVE_SEND_SPEECHEVAL = AppConfig.HTTP_HOST + "/StudyCenter/submitSpeechEval";
    public final static String URL_LIVE_TUTORIAL_SUBMIT_TEST_ANSWER = AppConfig.HTTP_HOST +
            "/LiveTutorial/submitTestAnswer";
    /** 公开直播提交测试题 */
    public final static String URL_LIVE_LECTURE_SUBMIT_TEST_ANSWER = AppConfig.HTTP_HOST + "/LiveLecture/submitTestAnswer";

    /** 播放器异常日志 */
    public final static String URL_LIVE_ON_LOAD_LOGS = "http://netlive.xesv5.com/10011.gif";
    /** 语音评测地址 */
    public static String SPEECH_URL = "https://live.xueersi.com/LivePlayBack/speechEvalResult/";
    /** 获取学习报告-讲座 */
    public final static String URL_LIVE_GET_FEED_BACK = AppConfig.HTTP_HOST + "/LiveLecture/getFeedback";
    /** 获取红包金币 */
    public final static String URL_STUDY_GET_RED_PACKET = AppConfig.HTTP_HOST + "/MyCourse/receiveLiveTutoringGold";
    /** 直播回放提交答案地址 */
    public final static String URL_STUDY_SAVE_TEST_RECORD = AppConfig.HTTP_HOST +
            "/MyCourse/submitLiveTutoringTestAnswer";

    /** 播放器数据初始化 */
    public final static String URL_LIVE_GET_INFO = AppConfig.HTTP_HOST + "/LiveCourse/getInfo";
    /** 直播辅导播放器数据初始化 */
    public final static String URL_LIVE_TUTORIAL_GET_INFO = AppConfig.HTTP_HOST + "/LiveTutorial/initInfo";
    /** 公开直播播放器数据初始化 */
    public final static String URL_LIVE_LECTURE_GET_INFO = AppConfig.HTTP_HOST + "/LiveLecture/initInfo";

    /** 直播星星-讲座 */
    public final static String URL_LIVE_LEC_SETSTAR = AppConfig.HTTP_HOST + "/LiveLecture/setStuStarCount";
    /** 提交互动题答案 */
    public final static String URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION = AppConfig.HTTP_HOST +
            "/LiveLecture/submitTestAnswerForPlayBack";
    public final static String URL_PUBLIC_LIVE_COURSE_GET_MESSAGE = AppConfig.HTTP_HOST +
            "/IrcMessage/getLiveLectureMsgs";
    /** 得到广告信息 */
    public final static String URL_LIVE_GET_LEC_AD = AppConfig.HTTP_HOST + "/LiveLecture/getAdOnLL";

    /** 直播旁听统计 */
    public static String LIVE_VIDEO_AUDIO_LIVE = "live_video_audit_live";
    /** 直播回放倍速 */
    public static String LIVE_VIDEO_PLAYBACK_SPEED = "live_video_playback_speed";

    /** 直播-调试日志 */
    public static String LIVE_DEBUG_LOG = "live_debug_log";

    /** 直播-互动题 */
    public static String LIVE_PUBLISH_TEST = "live_publish_test";

    //新的日志
    /** 直播-H5课件互动题 */
    public static String LIVE_ENGLISH_COURSEWARE = "live_h5waretest";
    /** 直播-接麦 */
    public static String LIVE_LINK_MIRCO = "live_selectmic";
    /** 直播-H5题库互动题 */
    public static String LIVE_H5_TEST = "live_h5test";
    /** 直播-h5测试卷 */
    public static String LIVE_H5_EXAM = "live_exam";
    /** 直播-语音评测 */
    public static String LIVE_SPEECH_TEST = "live_speechtest";
    /** 直播-语音评测-二期 */
    public static String LIVE_SPEECH_TEST2 = "live_speechtest_2";
    /** 直播-NB实验 */
    public static String LIVE_H5_EXPERIMENT = "live_h5experiment";
    /** 直播-星星互动 */
    public static String LIVE_STAR_INTERACT = "live_starinteract";
    /** 直播-懂了吗 */
    public static String LIVE_DOYOUSEE = "live_doyousee";
    /** 直播-进入直播聊天 */
    public static String LIVE_JOINCHAT = "live_joinchat";
    /** 直播-分贝能量条 */
    public static String LIVE_ENGLISH_SPEEK = "live_english_speek";
    /** 直播-h5课件缓存 */
    public static String LIVE_H5_CACHE = "live_h5_cache";
    /** 直播-语音答题 */
    public static String LIVE_TEST_VOICE = "live_test_voice";
    /** 直播-投票 */
    public static String LIVE_VOTE = "live_vote";
    /** 直播-旁听 */
    public static String LIVE_LISTEN = "live_listen";
    /** 直播-表扬榜 */
    public static String LIVE_PRAISE_LIST = "live_praise_list";
    /** 直播讲座-（互动广告）https://wiki.xesv5.com/pages/viewpage.action?pageId=10684534 */
    public static String LEC_ADS = "lecture_ads";

    /** 直播时间-当天据算，live_expe_time按单个场次计算 */
    public static String LIVE_EXPE_TIME = "live_expe_time_all";
    /** 英语能量条提示 */
    public static String LIVE_ENGLISH_TIP = "live_english_speeak_tip";
    /** 英语能量条提示 */
    public static String LEC_LEARN_REPORT = "lec_learn_report";

    // 03.16 体验课互动题提交答案
    public static String LIVE_EXPE_SUBMIT = AppConfig.HTTP_HOST + "/science/AutoLive/submitTestAnswer";
    /**获取标记点列表*/
    public static String URL_LIVE_GET_MARK_POINTS=AppConfig.HTTP_HOST+"/science/LiveCourse/getMarkpoint";
    /**保存标记点*/
    public static String URL_LIVE_SET_MARK_POINTS=AppConfig.HTTP_HOST+"/science/LiveCourse/setMarkpoint";
    /**删除标记点*/
    public static String URL_LIVE_DELETE_MARK_POINTS=AppConfig.HTTP_HOST+"/science/LiveCourse/deleteMarkpoint";
}
