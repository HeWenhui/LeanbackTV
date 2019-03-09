package com.xueersi.parentsmeeting.modules.livevideo.question.config;

public class LiveQueConfig {
    /** 直播-新课件获得互动题，点上一步 */
    public static final int GET_ANSWERTYPE_PRE = 1;
    /** 直播-新课件获得互动题，点下一步 */
    public static final int GET_ANSWERTYPE_NEXT = 2;
    /** 直播-新课件获得互动题，点提交 */
    public static final int GET_ANSWERTYPE_SUBMIT = 3;
    /** 直播-新课件获得互动题，强制提交 */
    public static final int GET_ANSWERTYPE_FORCE_SUBMIT = 4;
    /** 直播-新课件获得消息，来源postMessage */
    public static final String GET_ANSWERTYPE_WHERE_MESSAGE = "postMessage";
    /** 直播-新课件获得消息，来源addEventListener */
    public static final String GET_ANSWERTYPE_WHERE_LISTENER = "addEventListener";
    /** 直播-新课件保存互动题 */
    public static final String LIVE_STUDY_REPORT_IMG = "live_new_course_que_save";

    public static final String SEC_COURSE_TYPE_QUE = "2";
    /**
     * 1-在线教研填空，2-在线教研选择，8-在线教研语文主观题，4-在线教研语音测评，5-在线教研roleplay，6-在线教研语文跟读，7-本地上传普通，9-本地上传课前测，
     * 10-本地上传课中测，11-本地上传出门考，12-本地上传游戏，13-本地上传互动题，14-本地上传语音测评
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=12955807
     */
    public static final String EN_COURSE_TYPE_BLANK = "1";
    public static final String EN_COURSE_TYPE_CHOICE = "2";
    public static final String EN_COURSE_TYPE_OUT = "11";
    /** 直播- 本地上传-语音答题填空 */
    public static final String EN_COURSE_TYPE_VOICE_BLANK = "15";
    /** 直播- 本地上传-语音答题选择 */
    public static final String EN_COURSE_TYPE_VOICE_CHOICE = "16";
    /** 直播- 本地上传-老课件(含loading页和结果页) */
    public static final String EN_COURSE_TYPE_NEW_GAME = "17";
    public static final String EN_COURSE_TYPE_19 = "19";
}
