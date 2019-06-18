package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.config;

import java.util.ArrayList;

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

    /** 直播-新课件-互动题 */
    public static final String SEC_COURSE_TYPE_QUE = "2";
    /** 直播-新课件-未来课件 */
    public static final String SEC_COURSE_TYPE_FUTURE = "4";
    /** 英语调普通互动题：选择、填空提交接口 http://wiki.xesv5.com/pages/viewpage.action?pageId=12954171 */
    private static ArrayList<String> SUBMIT_MULTI_TEST_TYPES;
    /** 英语调用submitH5的接口类型 */
    private static ArrayList<String> SUBMIT_H5_TYPES;
    /** 英语显示下方控制条的类型 */
    private static ArrayList<String> SHOW_CONTROL_TYPES;
    /**
     * 1-在线教研填空，2-在线教研选择，8-在线教研语文主观题，4-在线教研语音测评，5-在线教研roleplay，6-在线教研语文跟读，7-本地上传普通，9-本地上传课前测，
     * 10-本地上传课中测，11-本地上传出门考，12-本地上传游戏，13-本地上传互动题，14-本地上传语音测评
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=12955807
     */
    public static final String EN_COURSE_TYPE_BLANK = "1";
    public static final String EN_COURSE_TYPE_CHOICE = "2";
    public static final String EN_COURSE_TYPE_COMMON = "7";
    public static final String EN_COURSE_TYPE_BEF = "9";
    public static final String EN_COURSE_TYPE_MID = "10";
    public static final String EN_COURSE_TYPE_OUT = "11";
    /** 直播- 本地上传游戏 */
    public static final String EN_COURSE_TYPE_GAME = "12";
    /** 直播- 本地上传互动题 */
    public static final String EN_COURSE_TYPE_QUE = "13";
    /** 直播- 本地上传-语音答题填空 */
    public static final String EN_COURSE_TYPE_VOICE_BLANK = "15";
    /** 直播- 本地上传-语音答题选择 */
    public static final String EN_COURSE_TYPE_VOICE_CHOICE = "16";
    /** 直播- 本地上传-老课件(含loading页和结果页) */
    public static final String EN_COURSE_TYPE_NEW_GAME = "17";
    public static final String EN_COURSE_TYPE_18 = "18";
    public static final String EN_COURSE_TYPE_19 = "19";
    /** 语文AI主观题*/
    public static final String CHI_COURESWARE_TYPE_AISUBJECTIVE = "17";
    /** 直播- voice cannon */
    public static final String EN_COURSE_TYPE_VOICE_CANNON = "22";
    /** 直播- Cleaning up */
    public static final String EN_COURSE_TYPE_CLEANING_UP = "23";
    /** 直播- 热气球 */
    public static final String EN_COURSE_TYPE_HOT_AIR_BALLON = "24";
    /** 直播-小组互动语音炮弹 */
    public static final int EN_COURSE_GAME_TYPE_1 = 1;
    /** 直播-小组互动Cleaning up */
    public static final int EN_COURSE_GAME_TYPE_2 = 2;

    /** 游戏模式1 */
    public static int GAME_MODEL_1 = 1;
    /** 游戏模式2 */
    public static int GAME_MODEL_2 = 2;

    public static ArrayList getSubmitMultiTestTypes() {
        if (SUBMIT_MULTI_TEST_TYPES == null) {
            SUBMIT_MULTI_TEST_TYPES = new ArrayList<>();
            // 1
            SUBMIT_MULTI_TEST_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_BLANK);
            // 2
            SUBMIT_MULTI_TEST_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_CHOICE);
            // 18
            SUBMIT_MULTI_TEST_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_18);
            // 19
            SUBMIT_MULTI_TEST_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_19);
        }
        return SUBMIT_MULTI_TEST_TYPES;
    }

    public static ArrayList getSubmitH5Types() {
        if (SUBMIT_H5_TYPES == null) {
            SUBMIT_H5_TYPES = new ArrayList<>();
            // 7
            SUBMIT_H5_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_COMMON);
            // 9
            SUBMIT_H5_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_BEF);
            // 10
            SUBMIT_H5_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_MID);
            // 11
            SUBMIT_H5_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_OUT);
            // 12
            SUBMIT_H5_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_GAME);
        }
        return SUBMIT_H5_TYPES;
    }

    /**
     * 英语显示下方控制条
     *
     * @return
     */
    public static ArrayList getShowControlTypes() {
        if (SHOW_CONTROL_TYPES == null) {
            SHOW_CONTROL_TYPES = new ArrayList<>();
            // 1
            SHOW_CONTROL_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_BLANK);
            // 2
            SHOW_CONTROL_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_CHOICE);
            // 11
//            SHOW_CONTROL_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_OUT);
            // 18
            SHOW_CONTROL_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_18);
            // 19
            SHOW_CONTROL_TYPES.add(LiveQueConfig.EN_COURSE_TYPE_19);
        }
        return SHOW_CONTROL_TYPES;
    }

    public static boolean isGroupGame(String type) {
        return com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig.isGroupGame(type);
    }
}
