package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.config.AppConfig;

/**
 * 直播模块配置
 * Created by linyuqiang on 2018/2/27.
 */
public class LiveVideoSAConfig {
    String HTTP_HOST;

    public LiveVideoSAConfig(String hostStr) {
//        HTTP_HOST = AppConfig.HTTP_HOST + "/" + hostStr;
        HTTP_HOST = AppConfig.HTTP_HOST;
    }

    /** 提交教师评价 */
    public final static String URL_LIVE_SUBMIT_STU_EVALUATE = AppConfig.HTTP_HOST + "/LiveCourse/submitStuEvaluate";
    /** 直播课签到 */
    public final static String URL_LIVE_USER_SIGN = AppConfig.HTTP_HOST + "/LiveCourse/userSign";
    /** 直播课的直播用户在线心跳 */
    public final static String URL_LIVE_USER_ONLINE = AppConfig.HTTP_HOST + "/LiveCourse/userOnline";
    /** 直播辅导用户在线心跳 */
    public final static String URL_LIVE_TUTORIAL_USER_ONLINE = AppConfig.HTTP_HOST + "/LiveTutorial/userOnline";
    /** 公开直播用户在线心跳 */
    public final static String URL_LIVE_LECTURE_USER_ONLINE = AppConfig.HTTP_HOST + "/LiveLecture/userOnline";
    /** 直播课的直播领取金币 */
    public final static String URL_LIVE_RECEIVE_GOLD = AppConfig.HTTP_HOST + "/LiveCourse/receiveGold";
    /** 直播辅导领取金币 */
    public final static String URL_LIVE_TUTORIAL_GOLD = AppConfig.HTTP_HOST + "/LiveTutorial/receiveGold";
    /** 公开直播领取金币 */
    public final static String URL_LIVE_LECTURE_GOLD = AppConfig.HTTP_HOST + "/LiveLecture/receiveGold";
    /** 直播课的直播提交测试题 */
    public final static String URL_LIVE_SUBMIT_TEST_ANSWER = AppConfig.HTTP_HOST + "/LiveCourse/submitTestAnswer";
    /** 直播课的直播提交测试题-语音答题 */
    public final static String URL_LIVE_SUBMIT_TEST_ANSWER_VOICE = AppConfig.HTTP_HOST + "/LiveCourse/submitTestAnswerUseVoice";
    /** 直播课的直播提交测试题-h5课件 */
    public final static String URL_LIVE_SUBMIT_TEST_H5_ANSWER = AppConfig.HTTP_HOST + "/LiveCourse/sumitCourseWareH5AnswerUseVoice";
    /** 直播献花 */
    public final static String URL_LIVE_PRAISE_TEACHER = AppConfig.HTTP_HOST + "/LiveCourse/praiseTeacher";
    /** 直播辅导献花 */
    public final static String URL_LIVE_TUTORIAL_PRAISE_TEACHER = AppConfig.HTTP_HOST + "/LiveTutorial/praiseTeacher";
    /** 直播讲座献花 */
    public final static String URL_LIVE_LECTURE_PRAISE_TEACHER = AppConfig.HTTP_HOST + "/LiveLecture/praiseTeacher";
    /** 学生答题排名信息接口 */
    public final static String URL_LIVE_GET_RANK = AppConfig.HTTP_HOST + "/LiveCourse/getStuRanking";
    /** 学生答题排名信息接口 */
    public final static String URL_LIVE_GET_TEAM_RANK = AppConfig.HTTP_HOST + "/LiveCourse/getStuGroupTeamClassRanking";
    /** 得到语音评测试题 */
    public final static String URL_LIVE_GET_SPEECHEVAL = AppConfig.HTTP_HOST + "/StudyCenter/getSpeechEvalInfo";
    /** 发送语音评测答案 */
    public final static String URL_LIVE_SEND_SPEECHEVAL = AppConfig.HTTP_HOST + "/StudyCenter/submitSpeechEval";
    /** 发送语音评测答案-二期 */
    public final static String URL_LIVE_SEND_SPEECHEVAL42 = AppConfig.HTTP_HOST + "/LiveCourse/submitSpeechEval42";
    /** 发送语音评测答案-二期，是否作答 */
    public final static String URL_LIVE_SEND_SPEECHEVAL42_ANSWER = AppConfig.HTTP_HOST + "/LiveCourse/speechEval42IsAnswered";
    public final static String URL_LIVE_TUTORIAL_SUBMIT_TEST_ANSWER = AppConfig.HTTP_HOST +
            "/LiveTutorial/submitTestAnswer";
    /** 公开直播提交测试题 */
    public final static String URL_LIVE_LECTURE_SUBMIT_TEST_ANSWER = AppConfig.HTTP_HOST + "/LiveLecture/submitTestAnswer";
    /** h5课件请求是不是互动题 */
    public final static String URL_LIVE_LECTURE_VOICE_WARE = AppConfig.HTTP_HOST + "/LiveCourse/getVoiceWareTestInfo";

    /** 播放器异常日志 */
    public final static String URL_LIVE_ON_LOAD_LOGS = "http://netlive.xesv5.com/10011.gif";
    /** 语音评测地址 */
    public static String SPEECH_URL = "https://live.xueersi.com/LivePlayBack/speechEvalResult/";
    /** 获取学习报告 */
    public final static String URL_LIVE_GET_LEARNING_STAT = AppConfig.HTTP_HOST + "/LiveCourse/getLearningStat";
    /** 获取学习报告-讲座 */
    public final static String URL_LIVE_GET_FEED_BACK = AppConfig.HTTP_HOST + "/LiveLecture/getFeedback";
    /** 获取红包金币 */
    public final static String URL_STUDY_GET_RED_PACKET = AppConfig.HTTP_HOST + "/MyCourse/receiveLiveTutoringGold";
    /** 直播回放提交答案地址 */
    public final static String URL_STUDY_SAVE_TEST_RECORD = AppConfig.HTTP_HOST +
            "/MyCourse/submitLiveTutoringTestAnswer";
    /** 直播回放提交答案地址 */
    public final static String URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK = AppConfig.HTTP_HOST +
            "/LiveCourse/submitTestAnswerForPlayBack";
    /** 获取红包 */
    public final static String URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD = AppConfig.HTTP_HOST
            + "/LiveCourse/receiveGoldForPlayBack";
    /** 播放器数据初始化 */
    public final static String URL_LIVE_GET_INFO = AppConfig.HTTP_HOST + "/LiveCourse/getInfo";
    /** 直播辅导播放器数据初始化 */
    public final static String URL_LIVE_TUTORIAL_GET_INFO = AppConfig.HTTP_HOST + "/LiveTutorial/initInfo";
    /** 公开直播播放器数据初始化 */
    public final static String URL_LIVE_LECTURE_GET_INFO = AppConfig.HTTP_HOST + "/LiveLecture/initInfo";
    /** 用户试听 */
    public final static String URL_LIVE_USER_MODETIME = AppConfig.HTTP_HOST + "/LiveCourse/userModeTime";
    /** 学生上课情况 */
    public final static String URL_LIVE_STUDY_INFO = AppConfig.HTTP_HOST + "/LiveCourse/getLiveSimpleData";
    /** 直播星星 */
    public final static String URL_LIVE_SETSTAR = AppConfig.HTTP_HOST + "/LiveCourse/setStuStarCount";
    /** 直播星星-讲座 */
    public final static String URL_LIVE_LEC_SETSTAR = AppConfig.HTTP_HOST + "/LiveLecture/setStuStarCount";
    /** 学生金币数量 */
    public final static String URL_LIVE_STUDY_GOLD_COUNT = AppConfig.HTTP_HOST + "/LiveCourse/getStuStarAndGoldAmount";
    /** 提交互动题答案 */
    public final static String URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION = AppConfig.HTTP_HOST +
            "/LiveLecture/submitTestAnswerForPlayBack";
    public final static String URL_PUBLIC_LIVE_COURSE_GET_MESSAGE = AppConfig.HTTP_HOST +
            "/IrcMessage/getLiveLectureMsgs";
    /** 学生开口总时长，获得星星数 */
    public final static String URL_LIVE_TOTAL_OPEN = AppConfig.HTTP_HOST + "/LiveCourse/setTotalOpeningLength";
    /** 统计打点时间段内未开口的学员 */
    public final static String URL_LIVE_NOT_OPEN = AppConfig.HTTP_HOST + "/LiveCourse/setNotOpeningNum";
    /** 得到试题 */
    public final static String URL_LIVE_GET_QUESTION = AppConfig.HTTP_HOST + "/LiveCourse/getQuestion";
    /** 得到h5课件 */
    public final static String URL_LIVE_GET_WARE_URL = AppConfig.HTTP_HOST + "/LiveCourse/getCourseWareUrl";
    /** 得到广告信息 */
    public final static String URL_LIVE_GET_LEC_AD = AppConfig.HTTP_HOST + "/LiveLecture/getAdOnLL";
    /** 互动题满分榜接口 */
    public static String LIVE_FULL_MARK_LIST_QUESTION = AppConfig.HTTP_HOST + "/LiveCourse/teamTestFullScoreRank";
    /** 互动课件满分榜接口 */
    public static String LIVE_FULL_MARK_LIST_H5 = AppConfig.HTTP_HOST + "/LiveCourse/teamCourseWareH5FullScoreRank";
    /** 测试卷满分榜接口 */
    public static String LIVE_FULL_MARK_LIST_TEST = AppConfig.HTTP_HOST + "/LiveCourse/teamFullScoreRank";

    /** 获取光荣榜 */
    public final static String URL_LIVE_GET_HONOR_LIST = AppConfig.HTTP_HOST + "/LiveCourse/getClassExcellentList";
    /** 获取点赞榜 */
    public final static String URL_LIVE_GET_THUMBS_UP_LIST = AppConfig.HTTP_HOST + "/LiveCourse/getClassStuPraiseList";
    /** 获取进步榜 */
    public final static String URL_LIVE_GET_PRPGRESS_LIST = AppConfig.HTTP_HOST + "/LiveCourse/getStuIsOnProgressList";
    /** 获取点赞概率 */
    public final static String URL_LIVE_GET_THUMBS_UP_PROBABILITY = AppConfig.HTTP_HOST + "/LiveCourse/getStuOnList";

}
