package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 乐步 物理实验 配置
 *
 * @author chekun
 * created  at 2019/4/3 14:59
 */
public interface NbCourseWareConfig {
    /**
     * nobook 方提供的 appKey
     */
    String APP_KEY = "111";
    /**
     * nobook 方提供的 appSecret
     */
    String APP_SECRET = "weqwew";

    /**
     * 保密
     */
    String USER_TYPE_SERCRET = "0";
    /**
     * 老师
     */
    String USER_TYPE_TEACHER = "1";
    /**
     * 学生
     */
    String USER_TYPE_STU = "2";
    /**
     * 家长
     */
    String USER_TYPE_PARENT = "3";

    /**noobook 消息指令**/
    /**
     * 学生端发送提交请求
     **/
    String NOBOOK_SUBMIT = "nobook.submit";
    /** 实验结果提交返回 **/
    String NOBOOK_SUBMIT_RESPONSE = "nobook.submit_response";
    /**
     * 一个实验小步  正确消息
     **/
    String NOBOOK_ONE_STEP_CORRECT = "nobook.oneStepCorrect";

    /** 一个实验小步  错误消息 **/
    String NOBOOK_ONE_STEP_WRONG = "nobook.oneStepWrong";
    /**
     * 练习完成
     **/
    String NOBOOK_PRACICE_ONE_COMPLETE = "nobook.oneStepComplete";
    /**
     * 练习当前进度
     **/
    String NOBOOK_PRACICE_ONE_PROGRESS = "nobook.oneStepProgress";
    String NOBOOK_ONLOAD = "onload";

    /**
     * 加载失败
     */
    String NOBOOK_LOAD_ERROR = "load_error";

    /** Nb 实验本地 H5结果页地址 **/
    String LIVE_NB_COURSE_RESULT = "file:///android_asset/newcourse_result/nb/index.html";

    //   onTeachTakeUp :收卷方法
    // testMode: 1:可进入练习模式; 0:不可进入练习模式
    // force:是否强制收卷
    // https://live.xueersi.com//science/LiveExam/getResultStatistic?liveId=377069&stuId=2363976&experimentId=3&force=0&testMode=1
    /**
     * 考场编号
     */
    String EXAM_SN = "wangxiao";

    /** NB 实验资源缓存文件夹 **/
    String LOCAL_RES_DIR = "nbResDir";

    /** NB 实验资源文件根目录 **/
    String NB_RESOURSE_CACHE_DIR = "nbCourseCache2";


    /**和本地结果页通信指令********************************/
    /** 进入联系模式 msg **/
    String RESULTPAGE_INTOTESTMODE = "intoTestMode";
    /** 收起结果页消息 **/
    String RESULTPAGE_TOGGLEPACKUP = "togglePackUp";


    /**
     * NB 实验预加载资源类型
     */
    public static final String RESOURSE_TYPE_NB = "100";

}
