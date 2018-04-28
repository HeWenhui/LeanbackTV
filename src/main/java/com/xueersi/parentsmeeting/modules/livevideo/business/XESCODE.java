package com.xueersi.parentsmeeting.modules.livevideo.business;

public interface XESCODE {
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
    /** 点名 */
    int ROLLCALL = 134;
    /** 结束点名 */
    int STOPROLLCALL = 135;
    /** 其他学生点名 */
    int CLASS_MATEROLLCALL = 136;
    /** 直播表扬批评功能 */
    int PRAISE = 138;
    /** 考试开始 */
    int EXAM_START = 142;
    /** 考试停止 */
    int EXAM_STOP = 143;
    /** 考试停止 */
    int SPEECH_RESULT = 144;
    /** 英语h5课件 */
    int ENGLISH_H5_COURSEWARE = 145;
    /** 发布互动实验 */
    int H5_START = 146;
    /** 停止发布互动实验 */
    int H5_STOP = 147;
    /** 老师开启/关闭举手,私发:{"type": "153", "status": "off"/"on"} */
    int RAISE_HAND_SELF = 153;
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
    int ROOM_STAR_OPEN = 165;     // 老师开启星星互动
    int ROOM_STAR_CLOSE = 166;     // 老师关闭星星互动
    int ROOM_STAR_SEND_S = 167;        // 学生发送答案给老师
    int ROOM_STAR_SEND_T = 168;       // 老师私发学生提交的答案
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
    int TEACHER_JOIN_LEVEL = 176;
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
    /** 学习报告-讲座 */
    int LEC_LEARNREPORT = 199;
    /** 语音反馈 */
    int SPEECH_FEEDBACK = 200;
    /** 投票开始 */
    int VOTE_START = 210;
    /** 投票开始，学生重新进入 */
    int VOTE_START_JOIN = 211;
    /** 投票发送给老师答案 */
    int VOTE_SEND = 212;
    /** 上墙-学生发送消息 */

    /** 学生私聊老师点赞 */
    int XCR_ROOM_AGREE_SEND_S = 220;
    /** 老师广播学生点赞 */
    int XCR_ROOM_AGREE_SEND_T = 221;
    /** 学生计算赞数后私发老师 */
    int XCR_ROOM_AGREE_NUM_S = 222;
    /** 开/关榜单 */
    int XCR_ROOM_AGREE_OPEN = 224;

    int RANK_STU_MESSAGE = 225;
    /** 上墙-老师发送消息 */
    int RANK_TEA_MESSAGE = 226;
    /** 上墙-学生重连发送消息 */
    int RANK_STU_RECONNECT_MESSAGE = 227;

    /** 讲座购课广告 */
    int LEC_ADVERT = 2000;
}
