package com.xueersi.parentsmeeting.modules.livevideo.business;

public interface XESCODE {
    /** 开启 */
    String ON = "on";
    /** 关闭 */
    String OFF = "off";

    /** 发红包 */
    int READPACAGE = 101;
    /** 禁言 */
    int GAG = 102;
    /** 发题 */
    int SENDQUESTION = 103;
    /** 停止发题 */
    int STOPQUESTION = 104;
    /** 上课 */
    int CLASSBEGIN = 105;
    /** 懂了吗老师 */
    int UNDERSTANDT = 106;

//    int UNDERSTANDT = 133;
    /** 懂了吗学生 */
    int UNDERSTANDS = 107;
    /** 打开弹幕 */
    int OPENBARRAGE = 108;
    /** 打开聊天 */
    int OPENCHAT = 109;
    /** 献花 */
    int FLOWERS = 110;
    /** 直播模式切换 */
    int MODECHANGE = 111;
    /** 老师聊天 */
    int TEACHER_MESSAGE = 130;
    /** 学习报告 */
    int LEARNREPORT = 133;
//    int LEARNREPORT = 106;
    /** 点名,签到 */
    int ROLLCALL = 134;
    /** 结束点名，结束签到 */
    int STOPROLLCALL = 135;
    /** 其他学生点名，签到 */
    int CLASS_MATEROLLCALL = 136;
    /** 直播表扬批评功能 */
    @Deprecated
    int PRAISE = 138;
    /** 考试开始 */
    int EXAM_START = 142;
    /** 考试停止 */
    int EXAM_STOP = 143;
    /** 考试停止 */
    @Deprecated
    int SPEECH_RESULT = 144;
    /** 英语h5课件 */
    int ENGLISH_H5_COURSEWARE = 145;
    /** 发布互动实验 */
    int H5_START = 146;
    /** 停止发布互动实验 */
    int H5_STOP = 147;
    /** 老师开启/关闭举手,私发:{"type": "153", "status": "off"/"on"} */
    int RAISE_HAND_SELF = 153;
    @Deprecated
    int RAISE_HAND_AGAIN = 154;
    /** 老师开启/关闭举手:{"type": "155", "status": "off"/"on"} */
    int RAISE_HAND = 155;
    /** 学生请求 */
    int REQUEST_MICRO = 156;
    /** 请求接受 */
    int REQUEST_ACCEPT = 157;
    /** 老师发送开启/关闭上麦:{"type": "158", "status": "on"/"off", "students":["aaa", "bbb", "ccc", "ddd", "eee"]} */
    int START_MICRO = 158;
    /** 踢掉学生 */
    int ST_MICRO = 159;
    /** 举手人数 */
    int RAISE_HAND_COUNT = 160;
    /** 老师开启星星互动 */
    int ROOM_STAR_OPEN = 165;
    /** 老老师关闭星星互动 */
    int ROOM_STAR_CLOSE = 166;
    /** 学生发送答案给老师 */
    int ROOM_STAR_SEND_S = 167;
    /** 老师私发学生提交的答案 */
    int ROOM_STAR_SEND_T = 168;
    /** 请求学生端推流 */
    int REQUEST_STUDENT_PUSH = 170;
    /** 学生端开始 */
    int STUDENT_REPLAY = 171;
    /** 学生端直播模式改变 */
    int STUDENT_MODECHANGE = 172;
    /** 学生端数据改变 */
    int STUDENT_UPDATE = 173;
    /** 和学生端心跳 */
    int STUDENT_STU_HEART = 174;
    /** 回复学生端心跳 */
    int STUDENT_MY_HEART = 175;
    /** 老师进出直播室 */
    @Deprecated
    int TEACHER_JOIN_LEVEL = 176;
    /** 排行榜刷新,没用 */
    int RANK_FRESH = 180;
    /** 学生发送秒数指令 */
    int XCR_ROOM_DB_STUDENT = 190;
    /** 表扬学生 */
    int XCR_ROOM_DB_PRAISE = 191;
    /** 提醒学生 */
    int XCR_ROOM_DB_REMIND = 192;
    /** 开启分贝能量条指令 */
    int XCR_ROOM_DB_START = 193;
    /** 开启分贝能量条指令 */
    int XCR_ROOM_DB_CLOSE = 194;
    /** roleplay领读消息指令 */
    int XCR_ROOM_ROLE_READ = 195;
    /** 学习报告-讲座 TODO */
    int LEC_LEARNREPORT = 199;
    /** 语音反馈 */
    int SPEECH_FEEDBACK = 200;
    /** 投票开始 */
    int VOTE_START = 210;
    /** 投票开始，学生重新进入 */
    int VOTE_START_JOIN = 211;
    /** 投票发送给老师答案 */
    int VOTE_SEND = 212;
    /** 理科投票新 */
    int SCIENCE_VOTE = 410;
    /** 上墙-学生发送消息 */

    /**
     * 开启和发布榜单
     */
    int XCR_ROOM_PRAISELIST_OPEN = 294;
    /**
     * 学生告诉教师点赞个数
     */
    int XCR_ROOM_PRAISELIST_SEND_LIKE = 290;
    /**
     * 老师广播赞数，包含一键表扬 和 某某学生点了多少赞
     */
    int XCR_ROOM_PRAISELIST_LIKE_STUTENT = 291;
    /**
     * 老师广播赞数，告诉学生 当前各个战队有多少赞
     */
    int XCR_ROOM_PRAISELIST_LIKE_TEAM = 293;

    int RANK_STU_MESSAGE = 225;
    /** 上墙-老师发送消息 */
    int RANK_TEA_MESSAGE = 226;
    /** 上墙-学生重连发送消息 */
    int RANK_STU_RECONNECT_MESSAGE = 227;

    /** 讲座购课广告 TODO */
    int LEC_ADVERT = 2000;

    /** 老师点赞 */
    int TEACHER_PRAISE = 236;
    /** 老师点赞-集体发言 */
    int TEACHER_VOICE_PRAISE = 274;
    /** 战队pk，未知类型237 */
    int TEAMPK_237 = 237;


    /** 分队仪式 */
    int TEAM_PK_TEAM_SELECT = 230;

    /** 分配PK 对手 */
    int TEAM_PK_SELECT_PKADVERSARY = 231;

    /** 分队仪式 学生准备ok */
    int TEAM_PK_STUDENT_READY = 232;

    /** 公布pk 结果 */
    int TEAM_PK_PUBLIC_PK_RESULT = 233;

    /** 公布 本轮pk  战队pk 情况 */
    int TEAM_PK_PUBLIC_CONTRIBUTION_STAR = 234;

    /** 退出每题pk 结果 */
    int TEAM_PK_EXIT_PK_RESULT = 235;
    /** 公布明星榜 **/
    int TEAM_PK_STAR_RANK_LIST = 301;
    /** 公布黑马榜 **/
    int TEAM_PK_BLACK_RANK_LIST = 302;
    /** 教师端结束pk 统计 **/
    int TEAM_PK_PK_END = 303;
    /** 战队PK答对超难题 **/
    int TEAM_PK_PARISE_ANWSER_RIGHT = 304;
    /** 战队pk老师徽章表扬 **/
    int TEAM_PK_TEACHER_PRAISE = 305;
    /** 战队pk老师聊天  http://wiki.xesv5.com/pages/viewpage.action?pageId=18556157**/
    int TEAM_PK_MESSAGE = 320;
    /** 战队pk老师分组  http://wiki.xesv5.com/pages/viewpage.action?pageId=18556157**/
    int TEAM_PK_GROUP = 325;

    /** 一题多发 */
    /** 一题多发 收题发题都是251 TODO */
    int MULTIPLE_H5_COURSEWARE = 251;

    /** 大题互动 http://wiki.xesv5.com/pages/viewpage.action?pageId=17724551 */
    int QUES_BIG = 252;
    /** 开启/关闭语音弹幕 */
    int XCR_ROOM_DANMU_OPEN = 260;

    /** 发送语音弹幕 */
    int XCR_ROOM_DANMU_SEND = 261;
    /** 发送集体发言 */
    int XCR_ROOM_SPEECH_COLL = 273;
    /** 语文：开启/关闭语音弹幕 */
    int XCR_ROOM_CHINESE_DANMU_OPEN = 290;

    /** 语文：发送语音弹幕 */
    int XCR_ROOM_CHINESE_DANMU_SEND = 291;

    /** 提醒学生标记 */
    int MARK_POINT_TIP = 800;

    /**
     * 老师开启或者关闭点赞
     */
    int PRAISE_SWITCH = 265;
    /** 点赞消息 */
    int PRAISE_MESSAGE = 266;

    /** 班级点赞数量消息 */
    int PRAISE_CLASS_NUM = 267;
    /** 集体语言互动消息 */
    int SPEECH_COLLECTIVE = 270;

    /**Nb 加试 消息指令**/
    int NB_EXAM = 310;
    /**nb 加试实验 学生提交成功**/
    int NB_ADDEXPERIMENT_SUBMIT_SUCCESS = 311;


    /** 辅导老师发题 */
    int QUESTION_TUTOR = 315;

    /** 辅导老师 表扬榜on */
    int TUTOR_ROOM_PRAISE_OPEN = 400;
    /** 辅导老师 表扬榜 发送点赞数 */
    int TUTOR_ROOM_PRAISE_SENT_LIKE = 401;
    /** 辅导老师 表扬-点赞 */
    int TUTOR_ROOM_PRAISE_LIKE = 402;
    /** 辅导老师 表扬- 点赞总数*/
    int TUTOR_ROOM_PRAISE_LIKE_TOTAL = 403;

    /** 2018接麦 */
    interface AgoraChat {
        /** 开启/关闭举手 */
        int RAISE_HAND = 280;
        /** 学生上/下麦 */
        int STUDY_ONMIC = 281;
        /** 当前举手人数 */
        int RAISE_HAND_COUNT = 282;
        /** 举手 */
        int STU_RAISE_HAND = 283;
        /** 点赞 */
        int PRAISE_STU = 286;
    }

    interface EvenDrive {
        /** 学生之间私发点赞消息 */
        int PRAISE_PRIVATE_STUDENT = 299;
        /** 教师广播发送学报消息 */
        int BROADCAST_STUDY_REPORT = 300;
    }

    /** 文科表扬榜  开始notice */
    int ARTS_PRAISE_START = 1000;
    /** 文科表扬榜 学生上报点赞数 */
    int ARTS_SEND_PRAISE_NUM = 1001;
    /** 文科表扬榜接受到点赞数 */
    int ARTS_RECEIVE_PRAISE_NUM = 1002;
    /** 文科语音弹幕  开启/关闭弹幕 */
    int XCR_ROOM_OPEN_VOICEBARRAGE = 1005;
    /** 文科语音弹幕  弹幕消息 */
    int XCR_ROOM_VOICEBARRAGE = 1006;
    /** 文科语音弹幕  表扬消息 */
    int XCR_ROOM_VOICEBARRAGEPRAISE = 1007;

    /**
     * 文科新课件平台 对接notice 指令
     */
    /** 文科在线教研 发题 */
    int ARTS_SEND_QUESTION = 1104;
    /** 文科在线教研收题 */
    int ARTS_STOP_QUESTION = 1105;
    int ARTS_STOP_RANKING = 180;
    /** 文科设计组课件收发题 */
    int ARTS_H5_COURSEWARE = 1145;
    /** 文科教师端提醒交卷 **/
    int ARTS_REMID_SUBMIT = 1161;

    /** 文科 表扬学生（多题作答表扬全对、语音作答按分数区间表扬） */
    int ARTS_PARISE_ANSWER_RIGHT = 1160;

    /** 文科单题表扬 */
    int ARTS_PRAISE_ANSWER_RIGHT_SINGLE = 1162;
    /** 文科单词听写 */
    int ARTS_WORD_DICTATION = 1003;

    /** 英语战队pk */
    interface EnTeamPk {

        int XCR_ROOM_TEAMPK_OPEN = 1050;//  ("1004")  //通知战队pk分组
        int XCR_ROOM_TEAMPK_RESULT = 1051;//     ("1005")  //发布战队PK结果
        int XCR_ROOM_TEAMPK_GO = 1020;//     ("1005")  //发布战队PK结果
        int XCR_ROOM_TEAMPK_STULIKE = 1021;//     ("1021")  //学生点赞上报
    }

    /** 语文幼升小金话筒 */
    int ARTS_GOLD_MICROPHONE = 3000;
    /** 语文幼升小发送语音识别消息 */
    int ARTS_GOLD_MICROPHONE_SEND_TEACHER = 3001;
    /** 发布结束演讲秀notice */
    int SUPER_SPEAKER_TAKE_CAMERA = 3003;
    /** 演讲秀发给老师消息 */
    int SUPER_SPEAKER_SEND_MESSAGE = 3004;

    interface ExpLive{
        int XEP_MODE_CHANGE = 500;
        int XEP_BACK_FINISH = 501;
    }
    /** 大班整合 送礼物*/
    int LIVE_BUSINESS_SEND_GIFT = 106;
    /** 大班整合 感谢学生*/
    int LIVE_BUSINESS_GIFT_THANK_STUDENT = 10110;

    int FORUM_INTERACTION = 412;
    int FORUM_INTERACTION_PRAISE = 10105;
    /** 英语1v2 小组互动+上台发题+语音测评 开始*/
    int GROUPCLASS_EVENTS_START = -10000;
    /** 英语1v2 小组互动+上台发题+语音测评 结束*/
    int GROUPCLASS_EVENTS_END = -10001;
    /** 英语1v2 视频态 打开*/
    int GROUPCLASS_VIDEO_START = -10002;
    /** 英语1v2 视频态 关闭*/
    int GROUPCLASS_VIDEO_END = -10003;
    /** 英语1v2 视频态 打开*/
    int GROUPCLASS_VIDEO_ROLL_CALL_START = -10004;
    /** 英语1v2 视频态 打开*/
    int GROUPCLASS_VIDEO_ROLL_CALL_END = -10005;


    /**大班整合切换主辅 模式**/
    int LIVE_BUSINESS_MODE_CHANGE = 102;

    /** 轻直播人气*/
    int LIGHTLIVE_ROOM_STUDENT_NUM = 501;
    /** 轻直播公告*/
    int LIGHTLIVE_ROOM_NOTICE = 502;
}
