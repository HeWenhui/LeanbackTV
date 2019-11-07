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
        /** 直播课的直播提交测试题-语音答题 */
        public String URL_LIVE_SUBMIT_TEST_ANSWER_VOICE = HTTP_HOST + "/LiveCourse/submitTestAnswerUseVoice";
        /** 直播课的语音评测小组排名 */
        public String URL_LIVE_SPEECH_TEAM = HTTP_HOST + "/LiveCourse/getSpeechEvalAnswerTeamStatus";
        /** 直播课的语音答题小组排名 */
        public String URL_LIVE_ANSWER_TEAM = HTTP_HOST + "/LiveCourse/getTestAnswerTeamStatus";

        /** roleplay组内排行榜 */
        public String URL_LIVE_ROLE_TEAM = HTTP_HOST + "/LiveCourse/getRolePlayAnswerTeamRank";
        /** 直播课的直播提交测试题-h5课件 */
        public String URL_LIVE_SUBMIT_TEST_H5_ANSWER = HTTP_HOST + "/LiveCourse/sumitCourseWareH5AnswerUseVoice";
        /** 直播献花 */
        public String URL_LIVE_PRAISE_TEACHER = HTTP_HOST + "/LiveCourse/praiseTeacher";
        /** 学生答题排名信息接口 */
        public String URL_LIVE_GET_RANK = HTTP_HOST + "/LiveCourse/getStuRanking";
        /** 学生答题排名信息接口 */
        public String URL_LIVE_GET_TEAM_RANK = HTTP_HOST + "/LiveCourse/getStuGroupTeamClassRanking";
        /** 发送语音评测答案-二期 */
        public String URL_LIVE_SEND_SPEECHEVAL42 = HTTP_HOST + "/LiveCourse/submitSpeechEval42";

        /** 语音评测排行榜 */
        public String URL_LIVE_SPEECH_TEAM_RAND = HTTP_HOST + "/LiveCourse/getSpeechEvalAnswerTeamRank";

        /** 发送语音评测答案-二期，是否作答 */
        public String URL_LIVE_SEND_SPEECHEVAL42_ANSWER = HTTP_HOST + "/LiveCourse/speechEval42IsAnswered";

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
        @Deprecated
        public String URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLDS = HTTP_HOST
                + "/science/AutoLive/receiveGold";
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
        /** 语文一题多发 */
        public String URL_LIVE_CHS_GET_MORE_WARE_URL = HTTP_HOST + "/LiveCourse/courseWarePreLoad";
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
        public String coursewareH5 = LiveHttpConfig.LIVE_HOST + "/" +
                (IS_SCIENCE ? ShareBusinessConfig.LIVE_SCIENCE : ShareBusinessConfig.LIVE_LIBARTS) + "/Live/coursewareH5/";

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
        /** 战队PK 明星榜 **/
        public String URL_TEMPK_GETSTARSTUDENTS = HTTP_HOST + "/LiveCourse/getStarStudents";
        /** 获取战队pk  进步榜 */
        public String URL_TEMPK_GETPROGRESSSTU = HTTP_HOST + "/LiveCourse/getProgressStudents";
        /** 获取战队成员信息 **/
        public String URL_TEAMPK_GETTEAMMATES = HTTP_HOST + "/LiveCourse/getStudentInfoInTeam";

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

        /** 直播上传精彩瞬间截图接口 */
        public String URL_LIVE_WONDER_MOMENT = HTTP_HOST + "/LiveCourse/uploadWonderfulMomentImg";
        /** 储存学生直播在线时长(App端) https://wiki.xesv5.com/pages/viewpage.action?pageId=13838543 */
        public String URL_LIVE_STU_ONLINE_TIME = HTTP_HOST + "/LiveCourse/saveStuPlanOnlineTime";

        /** 集体发言二期，上传语音文本 http://wiki.xesv5.com/pages/viewpage.action?pageId=18553425 */
        public String URL_UPLOAD_SPEECH_MSG = HTTP_HOST + "/LiveCourses/uploadSpeechMsg";

        /** 学生端大题互动提交 http://wiki.xesv5.com/pages/viewpage.action?pageId=17724881 */
        public String URL_LIVE_SUBMIT_BIG_TEST = HTTP_HOST + "/LiveExam/submitBigTestInteraction";
        /** 学生端获取结果页  http://wiki.xesv5.com/pages/viewpage.action?pageId=17725779 */
        public String URL_LIVE_GET_BIG_TEST_RESULT = HTTP_HOST + "/LiveExam/getStuInteractionResult";
        /** 理科讨论区互动接口 */
        public String URL_LIVE_FORUM_INTERACTION = HTTP_HOST + "/LiveCourse/stuSendMessage";
    }

}
