package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;

/**
 * 直播模块配置
 * Created by linyuqiang on 2018/2/27.
 */
public class LiveVideoSAConfig {
    String HTTP_HOST;
    public Inner inner;

    public LiveVideoSAConfig(String hostStr) {
        HTTP_HOST = AppConfig.HTTP_HOST + "/" + hostStr;
//        HTTP_HOST = AppConfig.HTTP_HOST;
        inner = new Inner();
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
        ;
        /** 直播课的直播提交测试题 */
        public String URL_LIVE_SUBMIT_TEST_ANSWER = HTTP_HOST + "/LiveCourse/submitTestAnswer";
        /** 直播课的直播提交测试题-语音答题 */
        public String URL_LIVE_SUBMIT_TEST_ANSWER_VOICE = HTTP_HOST + "/LiveCourse/submitTestAnswerUseVoice";
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
        /** 发送语音评测答案-二期，是否作答 */
        public String URL_LIVE_SEND_SPEECHEVAL42_ANSWER = HTTP_HOST + "/LiveCourse/speechEval42IsAnswered";
        /** 获取学习报告 */
        public String URL_LIVE_GET_LEARNING_STAT = HTTP_HOST + "/LiveCourse/getLearningStat";
        /** 直播回放提交答案地址 */
        public String URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK = HTTP_HOST +
                "/LiveCourse/submitTestAnswerForPlayBack";
        /** 获取红包 */
        public String URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD = HTTP_HOST
                + "/LiveCourse/receiveGoldForPlayBack";
        /** 用户试听 */
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
        /** 得到h5课件 */
        public String URL_LIVE_GET_WARE_URL = HTTP_HOST + "/LiveCourse/getCourseWareUrl";
        /** 互动题满分榜接口 */
        public String LIVE_FULL_MARK_LIST_QUESTION = HTTP_HOST + "/LiveCourse/teamTestFullScoreRank";
        /** 互动课件满分榜接口 */
        public String LIVE_FULL_MARK_LIST_H5 = HTTP_HOST + "/LiveCourse/teamCourseWareH5FullScoreRank";
        /** 测试卷满分榜接口 */
        public String LIVE_FULL_MARK_LIST_TEST = HTTP_HOST + "/LiveCourse/teamFullScoreRank";

        /** 获取光荣榜 */
        public String URL_LIVE_GET_HONOR_LIST = HTTP_HOST + "/LiveCourse/getClassExcellentList";
        /** 获取点赞榜 */
        public String URL_LIVE_GET_THUMBS_UP_LIST = HTTP_HOST + "/LiveCourse/getClassStuPraiseList";
        /** 获取进步榜 */
        public String URL_LIVE_GET_PRPGRESS_LIST = HTTP_HOST + "/LiveCourse/getStuIsOnProgressList";
        /** 获取点赞概率 */
        public String URL_LIVE_GET_THUMBS_UP_PROBABILITY = HTTP_HOST + "/LiveCourse/getStuOnList";

        public String coursewareH5 = "https://live.xueersi.com/" +
                (IS_SCIENCE ? ShareBusinessConfig.LIVE_science : ShareBusinessConfig.LIVE_libarts) + "/Live/coursewareH5/";
    }

}
