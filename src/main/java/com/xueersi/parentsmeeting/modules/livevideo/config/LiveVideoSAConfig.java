package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;

/**
 * 直播模块配置
 * Created by linyuqiang on 2018/2/27.
 */
public class LiveVideoSAConfig {
    //LiveGetInfoge 的isArts
    /** 理科 */
    public static final int ART_SEC = 0;
    /** 英语 */
    public static final int ART_EN = 1;
    /** 语文 */
    public static final int ART_CH = 2;
    private final int arts;
    String HTTP_HOST;
    public Inner inner;

    public LiveVideoSAConfig(String hostStr, boolean IS_SCIENCE) {
        HTTP_HOST = LiveVideoConfig.HTTP_HOST + "/" + hostStr;
//        HTTP_HOST = AppConfig.HTTP_HOST;
        this.IS_SCIENCE = IS_SCIENCE;
        inner = new Inner();
        if (IS_SCIENCE) {
            arts = ART_SEC;
        } else {
            arts = ART_EN;
        }
    }

    public LiveVideoSAConfig(String host) {
        HTTP_HOST = host;
        IS_SCIENCE = false;
        inner = new Inner();
        arts = ART_CH;
    }

    public int getArts() {
        return arts;
    }

    /** 是不是文理 */
    public boolean IS_SCIENCE = true;

    public class Inner {
        /** 提交教师评价 */
        public String URL_LIVE_SUBMIT_STU_EVALUATE = HTTP_HOST + "/LiveCourse/submitStuEvaluate";
        /** 直播课签到 */
        public String URL_LIVE_USER_SIGN = HTTP_HOST + "/LiveCourse/userSign";
        /** 直播课的直播用户在线心跳 */
        public String URL_LIVE_USER_ONLINE = HTTP_HOST + "/LiveCourse/userOnline";
        /** 直播课的直播领取金币 */
        public String URL_LIVE_RECEIVE_GOLD = HTTP_HOST + "/LiveCourse/receiveGold";
        /** 直播课的直播领取金币 */
        public String URL_LIVE_ = HTTP_HOST + "/LiveCourse/getReceiveGoldTeamStatus";

        /** 直播课的直播提交测试题 */
        public String URL_LIVE_SUBMIT_TEST_ANSWER = HTTP_HOST + "/LiveCourse/submitTestAnswer";
        /** 文科新课件平台语文主观题提交测试题 */
        public String URL_LIVE_SUBMIT_NEWARTSTEST_ANSWER = "https://app.arts.xueersi.com/v2/SubjectiveTest/submitTest";
        /** 直播课的直播提交测试题-语音答题 */
        public String URL_LIVE_SUBMIT_TEST_ANSWER_VOICE = HTTP_HOST + "/LiveCourse/submitTestAnswerUseVoice";
        /** 直播课的语音评测小组排名 */
        public String URL_LIVE_SPEECH_TEAM = HTTP_HOST + "/LiveCourse/getSpeechEvalAnswerTeamStatus";
        /** 全身直播语音测评组内战况 */
        public String URL_LIVE_SPEECH_TEAM_STATUS = "https://app.arts.xueersi.com/v2/standLiveStatus/getSpeechEvalAnswerTeamStatus";
        /** 直播课的语音答题小组排名 */
        public String URL_LIVE_ANSWER_TEAM = HTTP_HOST + "/LiveCourse/getTestAnswerTeamStatus";
        /** 全身直播语音答题的小组排名(新课件平台) */
        public String URL_LIVE_NEWSTAND_ANSWER = "https://app.arts.xueersi.com/v2/standLiveStatus/getTestAnswerTeamStatus";
        /** roleplay组内排行榜 */
        public String URL_LIVE_ROLE_TEAM = HTTP_HOST + "/LiveCourse/getRolePlayAnswerTeamRank";
        /** 全身直播roleplayTop3排行榜 */
        public String URL_LIVE_ROLE_TOP3 = "https://app.arts.xueersi.com/v2/standLiveStatus/getRolePlayAnswerTeamRank";
        /** 直播课的直播提交测试题-h5课件 */
        public String URL_LIVE_SUBMIT_TEST_H5_ANSWER = HTTP_HOST + "/LiveCourse/sumitCourseWareH5AnswerUseVoice";
        /** 直播课的文科新版课件对接新提交接口- */
        public String URL_LIVE_SUBMIT_NEWARTS_ANSWER = "https://app.arts.xueersi.com/v2/commonTest/submitMultiTest";
        /** 直播课的文科新版课件对接课件语音答题新提交接口- */
        public String URL_LIVE_SUBMIT_NEWARTSH5_ANSWER = "https://app.arts.xueersi.com/v2/CourseH5Test/submitH5Voice";
        /** 直播献花 */
        public String URL_LIVE_PRAISE_TEACHER = HTTP_HOST + "/LiveCourse/praiseTeacher";
        /** 学生答题排名信息接口 */
        public String URL_LIVE_GET_RANK = HTTP_HOST + "/LiveCourse/getStuRanking";
        /** 学生答题排名信息接口 */
        public String URL_LIVE_GET_TEAM_RANK = HTTP_HOST + "/LiveCourse/getStuGroupTeamClassRanking";
        /** 发送语音评测答案-二期 */
        public String URL_LIVE_SEND_SPEECHEVAL42 = HTTP_HOST + "/LiveCourse/submitSpeechEval42";
        /** 发送语音评测答案-文科新课件 http://wiki.xesv5.com/pages/viewpage.action?pageId=12959505 */
        public String URL_LIVE_SEND_SPEECHEVALUATEARTS = "https://app.arts.xueersi.com/v2/speechEval42/submitSpeechEval42";
        /** 语音评测排行榜 */
        public String URL_LIVE_SPEECH_TEAM_RAND = HTTP_HOST + "/LiveCourse/getSpeechEvalAnswerTeamRank";
        /** 全身直播语音测评Top3排行榜 */
        public String URL_LIVE_ROLE_SPEECH_TEAM_TOP3 = "https://app.arts.xueersi.com/v2/standLiveStatus/getSpeechEvalAnswerTeamRank";
        /** 发送语音评测答案-二期，是否作答 */
        public String URL_LIVE_SEND_SPEECHEVAL42_ANSWER = HTTP_HOST + "/LiveCourse/speechEval42IsAnswered";
        /** 发送语音评测答案-文科新课件平台，是否作答 */
        public String URL_LIVE_SEND_SPEECHEVALUATENEWARTS_ANSWER = "http://laoshi.xueersi.com/libarts/v2/speechEval42/speechEval42IsAnswered";
        /** 获取学习报告 */
        public String URL_LIVE_GET_LEARNING_STAT = HTTP_HOST + "/LiveCourse/getLearningStat";
        /** 直播回放提交答案地址 */
        public String URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK = HTTP_HOST +
                "/LiveCourse/submitTestAnswerForPlayBack";
        /** 获取组内领取红包情况 */
        public String URL_RED_GOLD_TEAM_STATUS = HTTP_HOST
                + "/LiveCourse/getReceiveGoldTeamStatus";
        /** 获取组内领取红包排行 */
        public String URL_RED_GOLD_TEAM_RANK = HTTP_HOST
                + "/LiveCourse/getReceiveGoldTeamRank";
        /** 获取红包 */
        public String URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD = HTTP_HOST
                + "/LiveCourse/receiveGoldForPlayBack";
        /*获取体验直播课的红包*/
        public String URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLDS = HTTP_HOST
                + "/science/AutoLive/receiveGold" ;
        /** 用户试听 */
        @Deprecated
        public String URL_LIVE_USER_MODETIME = HTTP_HOST + "/LiveCourse/userModeTime";
        /** 学生上课情况 */
        public String URL_LIVE_STUDY_INFO = HTTP_HOST + "/LiveCourse/getLiveSimpleData";
        /** 直播星星 */
        public String URL_LIVE_SETSTAR = HTTP_HOST + "/LiveCourse/setStuStarCount";
        /** 学生金币数量 */
        public String URL_LIVE_STUDY_GOLD_COUNT = HTTP_HOST + "/LiveCourse/getStuStarAndGoldAmount";
        /** 学生开口总时长，获得星星数 */
        public String URL_LIVE_TOTAL_OPEN = HTTP_HOST + "/LiveCourse/setTotalOpeningLength";
        /** 统计打点时间段内未开口的学员 */
        public String URL_LIVE_NOT_OPEN = HTTP_HOST + "/LiveCourse/setNotOpeningNum";
        /** 得到试题 */
        public String URL_LIVE_GET_QUESTION = HTTP_HOST + "/LiveCourse/getQuestion";
        /** 得到h5课件-不区分文理 */
        public String URL_LIVE_GET_WARE_URL = LiveVideoConfig.HTTP_HOST + "/LiveCourse/getCourseWareUrl";
        /** 语文一题多发 */
        public String URL_LIVE_CHS_GET_MORE_WARE_URL = HTTP_HOST + "/LiveCourse/courseWarePreLoad";
        /** 理科一次多发课件 */
        public String URL_LIVE_GET_MORE_WARE_URL = LiveVideoConfig.HTTP_HOST + "/science/LiveCourse/courseWarePreLoad";
        /** 文科一发多题课件 */
        public String URL_LIVE_GET_ARTSMORE_COURSEWARE_URL = "https://app.arts.xueersi.com/v2/preLoad/preLoading";
        /** 文科新域名chs预加载 */
        public String URL_LIVE_CHS_GET_ARTSMORE_COURSEWARE_URL = "https://app.chs.xueersi.com/LiveCourse/getCourseWareUrl";
        /** 互动题满分榜接口 */
        public String LIVE_FULL_MARK_LIST_QUESTION = HTTP_HOST + "/LiveCourse/teamTestFullScoreRank";
        /** 互动课件满分榜接口 */
        public String LIVE_FULL_MARK_LIST_H5 = HTTP_HOST + "/LiveCourse/teamCourseWareH5FullScoreRank";
        /** 测试卷满分榜接口 */
        public String LIVE_FULL_MARK_LIST_TEST = HTTP_HOST + "/LiveCourse/teamFullScoreRank";

        /** 获取优秀榜 */
        public String URL_LIVE_GET_HONOR_LIST = HTTP_HOST + "/LiveCourse/getClassExcellentList";
        /** 获取计算小超市榜 */
        public String URL_LIVE_GET_MINI_MARKET_LIST = HTTP_HOST + "/LiveCourse/getDayDayPracPraiseList";
        /** 获取点赞榜 */
        public String URL_LIVE_GET_LIKE_LIST = HTTP_HOST + "/LiveCourse/getClassStuPraiseList";
        /** 获取点赞概率 */
        public String URL_LIVE_GET_LIKE_PROBABILITY = HTTP_HOST + "/LiveCourse/getStuOnList";

        /** 存标记点 */
        public String URL_LIVE_SAVE_MARK_POINT = HTTP_HOST + "/LiveCourse/setMarkpoint";
        /** 获取标记点 */
        public String URL_LIVE_GET_MARK_POINT = HTTP_HOST + "/Live/getMarkpoint";
        /** 删除标记点 */
        public String URL_LIVE_DELETE_MARK_POINT = HTTP_HOST + "/Live/deleteMarkpoint";
        /** 获取智能私信 */
        public String URL_LIVE_GET_AUTO_NOTICE = HTTP_HOST + "/LiveCourse/counselorWhisperNew";
        /** 智能私信警告统计 */
        public String URL_LIVE_STATISTICS_AUTO_NOTICE = HTTP_HOST + "/LiveCourse/whisperStatisc";
        /** 语音反馈保存录音地址 */
        public String URL_LIVE_SAVESTU_TALK = HTTP_HOST + "/LiveCourse/saveStuTalkSource";
        /** 理科接麦举手接口 */
        public String URL_LIVE_HANDADD = HTTP_HOST + "/LiveCourse/handAdd";
        /** 理科接麦举手接口 */
        public String URL_LIVE_ADD_STU_HAND_NUM = HTTP_HOST + "/LiveCourse/addStuPutUpHandsNum";
        /** 理科2018接麦举手获得用户列表接口 */
        public String URL_LIVE_STUINFO = HTTP_HOST + "/LiveCourse/getStuInfoByIds";

        /** h5课件地址 */
        public String coursewareH5 = "https://live.xueersi.com/" +
                (IS_SCIENCE ? ShareBusinessConfig.LIVE_SCIENCE : ShareBusinessConfig.LIVE_LIBARTS) + "/Live/coursewareH5/";
        /** 文科新域名 */
        public String chsCoursewareH5 = "https://live.chs.xueersi.com/Live/coursewareH5/";
        /** 文科新域名 */
        public String chsSubjectiveTestAnswerResult = "https://live.chs.xueersi.com/Live/subjectiveTestAnswerResult/";
        /** 语文主观题结果页地址 */
        public String subjectiveTestAnswerResult = "https://live.xueersi.com/" + ShareBusinessConfig.LIVE_LIBARTS
                + "/Live/subjectiveTestAnswerResult/";


        /**战队pk 相关接口*/

        /** 获取分队信息 */
        public String URL_TEMPK_PKTEAMINFO = HTTP_HOST + "/LiveCourse/getTeamNameAndMembers";
        /** pk对手信息 */
        public String URL_TEMPK_MATCHTEAM = HTTP_HOST + "/LiveCourse/getMatchResult";

        /** 获取本场次 金币，能量信息 */
        public String URL_TEMPK_LIVESTUGOLDANDTOTALENERGY = HTTP_HOST + "/LiveCourse/liveStuGoldAndTotalEnergy";
        /** 添加能能量值接口 */
        public String URL_TEMPK_ADDPERSONANDTEAMENERGY = HTTP_HOST + "/LiveCourse/addPersonAndTeamEnergy";
        /** 学生开宝箱 */
        public String URL_TEMPK_GETSTUCHESTURL = HTTP_HOST + "/LiveCourse/getStuChest";
        /** 班级宝箱结果 */
        public String URL_TEMPK_GETCLASSCHESTRESULT = HTTP_HOST + "/LiveCourse/getClassChestResult";
        /** 战队pk结果 */
        public String URL_TEMPK_STUPKRESULT = HTTP_HOST + "/LiveCourse/stuPKResult";
        /** 贡献之星结果 */
        public String URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR = HTTP_HOST + "/LiveCourse/teamEnergyNumAndContributionStar";
        /**战队PK 明星榜**/
        public String URL_TEMPK_GETSTARSTUDENTS = HTTP_HOST + "/LiveCourse/getStarStudents";
        /**获取战队pk  进步榜*/
        public String URL_TEMPK_GETPROGRESSSTU = HTTP_HOST + "/LiveCourse/getProgressStudents";
        /**获取战队成员信息**/
        public String URL_TEAMPK_GETTEAMMATES = HTTP_HOST + "/LiveCourse/getStudentInfoInTeam";

        /** 文科表扬榜 */
        public String URL_ARTS_PRAISE_LIST = "https://app.arts.xueersi.com/LiveRank/getRankData";

        /** 学生端上传用户发言语句，用户统计分词结果 */
        public String URL_UPLOAD_VOICE_BARRAGE = HTTP_HOST + "/LiveCourse/uploadVoiceBarrage";
        /** 回放获取弹幕接口 */
        public String URL_GET_VOICE_BARRAGE_MSG = HTTP_HOST + "/LiveCourse/getVoiceBarrageMsg";

        /** 贡献之星结果多题型 */
        public String URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTARMUL = HTTP_HOST + "/LiveCourse/teamEnergyNumAndContributionStarNew";

        /** 直播回放的用户在线心跳 */
        public String URL_LIVE_VISITTIME = HTTP_HOST + "/LiveCourse/visitTime";

        /** 点赞送礼物扣除金币接口 */
        public String URL_LIVE_PRAISE_GIFT = HTTP_HOST + "/LiveCourse/highPraiseTeacher";

        /**
         * 文科新课件平台
         * 加载H5 页面地址
         */
        public String URL_ARTS_H5_URL = "https://static.arts.xueersi.com/kejian/";

        public String URL_ARTS_COURSE_H5_URL = "https://live.arts.xueersi.com/v2/live/courseWareH5";

        /** 文科新课件平台 RolePlay 页面加载地址 */
        public String URL_NEWARTS_ROALPLAY_URL = "https://static.arts.xueersi.com/art_live/roleplay/";
        /** 全身直播新课件平台 RolePlay 页面加载地址 */
        public String URL_NEWARTS_STANDROALPLAY_URL = "https://static.arts.xueersi.com/art_live/roleplayStand/";
        /** 文科新课件平台 语文跟读 页面加载地址 */
        public String URL_NEWARTS_CHINESEREADING_URL = "https://static.arts.xueersi.com/art_live/Chinese-speech-touch/";
        /**
         * 直播间 文科差异化参数配置接口 (注：直播间文科差异化配置参数 获取接口)
         */
        public String URL_ARTS_ROOM_INFO = "https://app.arts.xueersi.com/v2/Live/getInfoBaseData";

        /** 文科新课件平台 排名接口 */
        public String URL_ARTS_TEAM_CLASS_RANK = "https://app.arts.xueersi.com/v2/LiveRank/getStuGroupTeamClassRanking";
        /** 直播上传精彩瞬间截图接口 */
        public String URL_LIVE_WONDER_MOMENT = HTTP_HOST + "/LiveCourse/uploadWonderfulMomentImg";
        /** 储存学生直播在线时长(App端) https://wiki.xesv5.com/pages/viewpage.action?pageId=13838543 */
        public String URL_LIVE_STU_ONLINE_TIME = HTTP_HOST + "/LiveCourse/saveStuPlanOnlineTime";


        /** 文科学生对老师评价 */
        public String URL_LIVE_ARTS_EVALUATE_TEACHER = "https://app.arts.xueersi.com/LiveCourse/submitStuEvaluateTeacher";
        /** 理科提交对老师评价 */
        public String URL_LIVE_SCIENCE_EVALUATE_TEACHER = "https://laoshi.xueersi.com/science/LiveCourse/submitStuEvaluateTeacher";
        /** 文科获得对老师评价选项 */
        public String URL_LIVE_ARTS_GET_EVALUATE_OPTION = "https://app.arts.xueersi.com/LiveCourse/showEvaluationOptions";
        /** 理科获得对老师评价选项 */
        public String URL_LIVE_SCIENCE_GET_EVALUATE_OPTION = "https://laoshi.xueersi.com/science/LiveCourse/getEvaluateContent";
        /** 小语获得对老师评价选项 */
        public String URL_LIVE_CHS_GET_EVALUATE_OPTION = "https://app.chs.xueersi.com/LiveCourse/getEvaluateInfo";
        /** 小语学生对老师评价 */
        public String URL_LIVE_CHS_EVALUATE_TEACHER = "https://app.chs.xueersi.com/LiveCourse/submitEvaluate";

        /** 学生端大题互动提交 http://wiki.xesv5.com/pages/viewpage.action?pageId=17724881 */
        public String URL_LIVE_SUBMIT_BIG_TEST = HTTP_HOST + "/LiveCourse/submitBigTestInteraction";
        /** 学生端获取结果页  http://wiki.xesv5.com/pages/viewpage.action?pageId=17725779 */
        public String URL_LIVE_GET_BIG_TEST_RESULT = HTTP_HOST + "/LiveCourse/getStuInteractionResult";
    }


}
