package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.os.Environment;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.http.downloadAppfile.entity.DownLoadFileInfo;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.RunningEnvironment;
import com.xueersi.parentsmeeting.modules.livevideo.LiveAssetsLoadUtil;

import java.util.HashMap;

/**
 * 直播模块配置
 * URL前缀：接口连接地址
 * SP前缀：ShareDataPreferences缓存Key
 * UMS前缀:大数据统计KEY
 * Created by Administrator on 2017/3/31.
 */
public class LiveVideoConfig {
    //是否使用PSIJK
//    public static final boolean getIsNewIJK() = true;
    // private static final String TEST_HOST="https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example";

    public static String HTTP_HOST = AppConfig.HTTP_HOST;
    public static String HTTP_PRIMARY_CHINESE_HOST = "https://app.chs.xueersi.com";
    /** 文科的接口域名 */
    public static String APP_ARTS_HTTP_HOST = "https://app.arts.xueersi.com";
    /** 文科的接口域名 */
    public static String APP_ARTS_WXEN_HTTP_HOST = "https://wxen.arts.xueersi.com";
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
    /** 视频宽度 */
    public static final float VIDEO_WIDTH = 1280f;
    /** 视频高度 */
    public static final float VIDEO_HEIGHT = 720f;
    /** 视频宽高比 */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
    /** 头像宽度 */
    public static final float VIDEO_HEAD_WIDTH = 320f;
    /** 头像高度 */
    public static final float VIDEO_HEAD_HEIGHT = 240f;
    /** 播放器请求 */
    public static final int VIDEO_REQUEST = 210;
    /** 播放器用户返回 */
    public static final int VIDEO_CANCLE = 211;
    /** 播放器java崩溃 */
    public static final int VIDEO_CRASH = 1200;
    /** 播放器最大的高 */
    public static final int VIDEO_MAXIMUM_HEIGHT = 2048;
    /** 播放器最大的宽 */
    public static final int VIDEO_MAXIMUM_WIDTH = 2048;
    /**
     * 录播课的直播
     */
    public final static int LIVE_TYPE_TUTORIAL = 1;
    /**
     * 公开直播，讲座
     */
    public final static int LIVE_TYPE_LECTURE = 2;
    /**
     * 直播课的直播
     */
    public final static int LIVE_TYPE_LIVE = 3;
    /** 直播课的普通直播 */
    public final static int LIVE_PATTERN_COMMON = 1;
    /** 直播课的全身直播 */
    public final static int LIVE_PATTERN_2 = 2;
    /** 半身直播直播类型 */
    public static final int LIVE_TYPE_HALFBODY = 6;
    /** 半身直播直播类型-小组 */
    public static final int LIVE_TYPE_HALFBODY_CLASS = 9;
    /** 视频类型为站立直播体验课 */
    public static final int LIVE_TYPE_STAND_EXPERIENCE = 10000;
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

    /** 播放器数据初始化 */
    public final static String URL_LIVE_GET_INFO = LiveVideoConfig.HTTP_HOST + "/LiveCourse/getInfo";
    // public final static String URL_LIVE_GET_INFO = TEST_HOST + "/LiveCourse/getInfo";

    /** 直播辅导播放器数据初始化 */
    @Deprecated
    public final static String URL_LIVE_TUTORIAL_GET_INFO = LiveVideoConfig.HTTP_HOST + "/LiveTutorial/initInfo";
    /** 公开直播播放器数据初始化 */
    public final static String URL_LIVE_LECTURE_GET_INFO = LiveVideoConfig.HTTP_HOST + "/LiveLecture/initInfo";

    /** 直播星星-讲座 */
    public final static String URL_LIVE_LEC_SETSTAR = LiveVideoConfig.HTTP_HOST + "/LiveLecture/setStuStarCount";
    /** 提交互动题答案 */
    public final static String URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION = LiveVideoConfig.HTTP_HOST +
            "/LiveLecture/submitTestAnswerForPlayBack";
    /** 过期20190723 */
    @Deprecated
    public final static String URL_PUBLIC_LIVE_COURSE_GET_MESSAGE = LiveVideoConfig.HTTP_HOST +
            "/IrcMessage/getLiveLectureMsgs";
    /** 得到广告信息 */
    public final static String URL_LIVE_GET_LEC_AD = LiveVideoConfig.HTTP_HOST + "/LiveLecture/getAdOnLL";

    /** 直播旁听统计 */
    public static String LIVE_VIDEO_AUDIO_LIVE = "live_video_audit_live";
    /** 直播回放倍速 */
    public static String LIVE_VIDEO_PLAYBACK_SPEED = "live_video_playback_speed";
    /** 直播下载量 */
    public static String LIVE_VIDEO_UID_RX = "live_video_uid_rx";

    /** 直播-调试日志 */
    public static String LIVE_DEBUG_LOG = "live_debug_log";

    /** 直播-互动题 */
    public static String LIVE_PUBLISH_TEST = "live_publish_test";

    //新的日志
    /** 体验播放器-进入播放器 */
    public static String LIVE_EXPERIENCE_ENTER = "LiveFreePlayEvent";
    /** 体验播放器-关闭播放器 */
    public static String LIVE_EXPERIENCE_EXIT = "LiveFreePlayEvent";
    /** 体验播放器-聊天内容 */
    public static String LIVE_EXPERIENCE_IMMSG = "LiveFreePlayEvent";
    /** 体验课事件 */
    public static String LIVE_EXPERIENCE = "experienceLiveClass";

    /**
     * 直播回放体验课
     */
    public static String LIVE_BACK_EXPERIENCE = "liveBackExperience";

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
    /** 站立直播-语音评测 */
    public static String LIVE_STAND_SPEECH_TEST = "standlive_voiceTest";
    /** 站立直播-roleplay */
    public static String LIVE_STAND_ROLEPLAY = "standlive_roleplay";
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
    /** 站立直播-语音答题 */
    public static String LIVE_STAND_TEST_VOICE = "standlive_voiceAnswer";
    /** 直播-投票 */
    public static String LIVE_VOTE = "live_vote";
    /** 直播-旁听 */
    public static String LIVE_LISTEN = "live_listen";
    /** 直播-表扬榜 */
    public static String LIVE_PRAISE_LIST = "live_praise_list";
    /** 直播讲座-（互动广告）https://wiki.xesv5.com/pages/viewpage.action?pageId=10684534 */
    public static String LEC_ADS = "lecture_ads";
    /** 直播-站立直播资源更新 */
    public static String LIVE_STAND_RES_UPDATE = "live_stand_res_update";
    /** 直播-站立直播红包事件 */
    public static String STAND_LIVE_GRANT = "standlive_grant";
    /** 直播-包括ip地址的播放地址 https://wiki.xesv5.com/pages/viewpage.action?pageId=11403335 */
    public static String LIVE_GSLB = "live_gslb";
    /** 调度请求错误失败上报日志 */
    public static String LIVE_CHAT_GSLB = "live_chatgslb";
    /** 聊天服务器连接失败EventId */
    public static final String EXPERIENCE_MESSAGE_CONNECT_ERROR = "experience_message_connect_error";
    /** 站立直播-帧动画 */
    public static String LIVE_FRAME_ANIM = "live_frame_anim";
    /** 直播时间-当天据算，live_expe_time按单个场次计算 */
    public static String LIVE_EXPE_TIME = "live_expe_time_all";
    /** 英语能量条提示 */
    public static String LIVE_ENGLISH_TIP = "live_english_speeak_tip";
    /** 英语能量条提示 */
    public static String LEC_LEARN_REPORT = "lec_learn_report";
    /** 直播日志 */
    public static String SP_LIVEVIDEO_CLIENT_LOG = "sp_livevideo_clientLog";
    /** 直播网页加载失败 */
    public static String LIVE_WEBVIEW_ERROR = "live_webview_error";
    /** 直播预加载启动 */
    public static String LIVE_PRESERVICE_START = "live_preservice_start";
    /** 直播网页弹窗 */
    public static String LIVE_WEBVIEW_JS_ALERT = "live_webview_js_alert";
    /** 直播语音弹幕 */
    public static String LIVE_SPEECH_BULLETSCREEN = "voice_barrage";
    /** 直播-错误码 */
    public static String LIVE_PLAY_ERROR = "live_play_error";
    /** 直播-学习报告截图 */
    public static String LIVE_STUDY_REPORT_IMG = "live_study_report_img";
    /** 直播-小英语音聊天 */
    public static String LIVE_VOICE_CHAT = "voicechat";
    /** 直播-小英语音弹幕 */
    public static String LIVE_VOICE_BULLET = "voicebullet";
    /** 直播-小英语音聊天 */
    public static String LIVE_VOICE_VOLUME = "live_smallenglish_volume";

    public static class ShareData {

    }

    /**
     * 体验课互动题提交答案
     */
    public static String LIVE_EXPE_SUBMIT = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/submitTestAnswer";
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
    public static String URL_AUTO_LIVE_MSGS = LiveVideoConfig.HTTP_HOST + "/science/AutoLive/getLiveCourseMsgs";
    /** 提交体验课交互记录 */
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
    /** 视频播放失败的eventId */
    public final static String STAND_EXPERIENCE_LIVE_PLAY_ERROR = "stand_experience_live_play_error";

    /** 理科互动题 */
    public static String URL_LIVE_MULTI_TEST = LiveHttpConfig.LIVE_HOST + "/Live/getMultiTestPaper";
    /** 理科互动题-老师自传 */
    @Deprecated
    public static String URL_LIVE_TEA_UPLOAD_TEST = LiveHttpConfig.LIVE_HOST_SCIENCE + "/Live/teacherUploadTestForAPP/";
    /** 语文主观题获取答案的地址 */
    public static String URL_NEWARTS_SUBMITRESULT_H5 = "https://app.arts.xueersi" +
            ".com/v2/SubjectiveTest/subjectiveTestAnswerResult";
//    public static String URL_NEWARTS_SUBMITRESULT_H5 = "https://laoshi.xueersi
// .com/libarts/v2/SubjectiveTest/subjectiveTestAnswerResult";
    /** 直播云平台日志统计 */
    public static String URL_CDN_LOG = "http://log.xescdn.com/log";
    public static String URL_CDN_LOG1 = "http://log1.xescdn.com/log";
    public static String URL_CDN_LOG2 = "http://log2.xescdn.com/log";

    public static String URL_CND_LOG_IP = "http://42.62.96.154:80/log";

    /** 920日志IP 地址 */
    public static String SP_URL_LIVE_CND_LOG_920_TYPE = "sp_url_live_cnd_log_920_type";
    /** 920类型 IP */
    public static String LIVE_LOG_920_IP = "ip";
    /** 920类型 HOST */
    public static String LIVE_LOG_920_HOST = "host";


    /** 更多课程数量的记录 */
    public static int MORE_COURSE;
    public static Boolean isloading = false;
    /** 讲座直播广告Id */
    public static String LECTUREADID;

    @Deprecated
    public static Boolean isNewEnglishH5 = false;
    /** 一题多发发题和收题的标志 */
    @Deprecated
    public static Boolean isSend = false;
    public static String newEnglishH5 = "NewEnglishH5";
    /** 强制收题的标志 */
    public static EnglishH5Entity englishH5Entity;
    /** 小学阶段年级的标识 */
    public static Boolean isPrimary = false;
    /** 小学语文换肤 */
    public static Boolean isSmallChinese = false;
    /** 一题多发的直播回放的标识 */
    public static Boolean isMulLiveBack = false;
    /** 年级阶段的标识 */
    /** 文科回放一发多题的标识 */
    public static Boolean isNewArtsLiveBack = false;
    /** 年级阶段的标识 */
    public static String educationstage;
    /** 一发多题的日志 */
    public static String nonce;
    /** 直播回放一发多题的拼装参数 */
    /** 直播回放一发多题的拼装参数 */
    /** 文理科的标志 */
    public static Boolean isScience = false;
    /** 直播回放一发多题的拼装参数 */
    // 直播回放的URL
    public static String LIVEPLAYBACKINFOS;
    // 直播回放的stuCouId
    public static String LIVEPLAYBACKSTUID;
    // 直播回放的classId
    public static String LIVEPLAYBACKCLASSID;
    // 直播回放的teamId
    public static String LIVEPLAYBACKTEAMID;
    // 直播回放的edustage
    public static String LIVEPLAYBACKSTAGE;
    // 直播回放的type
    public static String LIVEPLAYBACKTYPE;
    /** 一发多题的两个动态接口-理科 */
    public static String LIVEMULPRELOAD;
    public static String LIVEMULH5URL;
    /** 一发多题的两个动态接口-语文 */
    public static String LIVEMULPRELOADCHS;
    public static String LIVEMULH5URLCHS;
    /** AI体验课互动题答题结果 */
    public static Boolean isAITrue = false;
    /** AI体验课已答互动题序号 */
    public static int aiQuestionIndex = -1;
    /** AI体验课退出时，记录当前的进度 */
    public static HashMap<String, Long> liveKey = new HashMap<>();
    public static HashMap<String, Long> curentTime = new HashMap<>();
    public static HashMap<String, Boolean> livefinish = new HashMap<>();
    /** 战队PK改版 */
    @Deprecated
    public static String tests;
    @Deprecated
    public static String ctId;
    @Deprecated
    public static String pSrc;

    public static interface SubjectIds {
        /**
         * 学科id，语文
         */
        public String SUBJECT_ID_CH = "1";
    }

    /** 直播心跳时长 */
    public static int LIVE_HB_TIME = 300;

    /** 教育阶段1 */
    public final static String EDUCATION_STAGE_1 = "1";
    /** 教育阶段2 */
    public final static String EDUCATION_STAGE_2 = "2";
    /** 教育阶段3 */
    public final static String EDUCATION_STAGE_3 = "3";
    /** 教育阶段4 */
    public final static String EDUCATION_STAGE_4 = "4";

    public final static String LIVE_PK = "live_pk";

    /** 直播-roleplay */
    public static String LIVE_ROLE_PLAY = "live_mutiroleplay";
    public static final String SP_LIVEVIDEO_MARK_POINT_COUNT = "sp_livevideo_mark_point_count";
    /** 低端设备检测 */
    public static String URL_CHECK_DEVICE = HTTP_HOST + "/Users/checkDevice";
//    public static String URL_CHECK_DEVICE = "https://www.easy-mock.com/mock/5b57f6919ddd1140ec2eb47b/xueersi.wx
// .android.app" + "/Users/checkDevice";

//    /** 文科新课件平台的标识 */
//    public static Boolean isNewArts = false;
    /** 全身直播的标识 */
    public static Boolean isStandLive = false;
    /** H5语音答题新增字段 */
    public static String userAnswer;
    public static String answer;

    /** 学习报告精彩瞬间 */
    public interface STUDY_REPORT {
        /** 贡献之星 */
        int TYPE_PK_RESULT = 1;
        /** 被选接麦 */
        int TYPE_AGORA = 2;
        /** 语音表扬 */
        int TYPE_PRAISE = 3;
        /** 进步榜 */
        int TYPE_4 = 4;
        /** 优秀榜 */
        int TYPE_5 = 5;
        /** 获赞榜 */
        int TYPE_6 = 6;
        /** pk获胜 */
        int TYPE_PK_WIN = 7;
        /** pk开宝箱 */
        int TYPE_PK_GOLD = 8;
        /** 抢红包 */
        int TYPE_RED_PACKAGE = 9;
    }

    public static int IRC_TYPE_NOTICE = 0;
    public static int IRC_TYPE_TOPIC = 1;

    /**
     * 体验课类型
     */
    public static interface ExperiencLiveType {
        /** 半身直播体验课 */
        int HALF_BODY = 1001;
        /** 普通直播体验课 */
        int NORMAL = 1000;
    }

    public static boolean assetsDownloadTag = true;

    public static DownLoadFileInfo getDownLoadFileInfo() {
        DownLoadFileInfo info = AppBll.getInstance().getDownLoadFileByFileName("assets.zip");
        if (info == null) {
            info = new DownLoadFileInfo();
            info.fileName = "assets.zip";
            info.fileMD5 = "0026c20e191b4adab347af523fc1e62c";
            info.fileType = 0;
            info.fileUrl = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/asserts/livevideo/1.0.1/assets.zip";
            info.needManualDownload = true;
            info.id = 0;
        }
        info.dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        UmsAgentManager.umsAgentDebug(RunningEnvironment.sAppContext, LiveAssetsLoadUtil.TAG, "assets message:" + info.toString());

        /*if (XueErSiRunningEnvironment.debug) {
            info.dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            info.dirPath  = LoadFileUtils.geCacheFile(RunningEnvironment.sAppContext, "downloadAssets").getAbsolutePath();
        }*/
        return info;
    }

    /**
     * 文科三分屏上传精彩瞬间截图url，半身直播走理科的接口
     */
    public static final String ART_TRIPLE_WONDERFUL_MOMENT = "https://live.chs.xueersi.com/ExamReport/uploadWonderfulMoment";

    /** 课后评价教师文案获取 */
    public static String URL_LIVE_COURSE_GETEVALUATE = AppConfig.HTTP_HOST + "/science/LiveCourse/getEvaluateContentNew";

    public final static String LIVE_PLAY_BACK_TUTOR_FLAGE = "_t";
    /** 语文H5默认新地址 */
    public final static String URL_DEFAULT_CHS_H5 = "https://live.chs.xueersi.com/Live/coursewareH5/";
    public final static String SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE = "sp_en_english_stand_summercours_ewaresize";
}
