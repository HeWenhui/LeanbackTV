package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.SysLogEntity;

/**
 * 系统日志埋点
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=18910075
 */
public class SysLogLable {
    /** IRC lable */
    private static String LiveBaseControllerIM = "LiveBaseControllerIM";
    /** IRC连接 */
    public static SysLogEntity connectIrcServer = new SysLogEntity(LiveBaseControllerIM, "connectIrcServer");
    /** IRC连接成功 */
    public static SysLogEntity connectIRCSuccess = new SysLogEntity(LiveBaseControllerIM, "connectIRCSuccess");
    /** IRC连接失败 */
    public static SysLogEntity connectIRCDidFailed = new SysLogEntity(LiveBaseControllerIM, "connectIRCDidFailed");
    /** 接收到topic消息 */
    public static SysLogEntity receivedMessageOfTopic = new SysLogEntity(LiveBaseControllerIM, "receivedMessageOfTopic");
    /** 接收到notice消息 */
    public static SysLogEntity receivedMessageOfNotic = new SysLogEntity(LiveBaseControllerIM, "receivedMessageOfNotic");
    /** 主讲辅导态切换 */
    public static SysLogEntity switchLiveMode = new SysLogEntity(LiveBaseControllerIM, "switchLiveMode");
    /** 老师进入 */
    public static SysLogEntity teacherJoin = new SysLogEntity(LiveBaseControllerIM, "teacherJoin");
    /** 老师退出 */
    public static SysLogEntity teacherQuit = new SysLogEntity(LiveBaseControllerIM, "teacherQuit");

    /** 去壳互动题 lable */
    private static String ShellingScienceWebViewLog = "ShellingScienceWebViewLog";
    /** 加载去壳课件开始 */
    public static SysLogEntity loadCourseWareStart = new SysLogEntity(ShellingScienceWebViewLog, "loadCourseWareStart");
    /** 加载去壳课件成功 */
    public static SysLogEntity didFinishLoadWithReuestURL = new SysLogEntity(ShellingScienceWebViewLog, "didFinishLoadWithReuestURL");
    /** 加载去壳课件失败 */
    public static SysLogEntity didFailLoadWithWithReuestURL = new SysLogEntity(ShellingScienceWebViewLog, "didFailLoadWithWithReuestURL");
    /** 课件返回数据 */
    public static SysLogEntity courseMessage = new SysLogEntity(ShellingScienceWebViewLog, "courseMessage");
    /** 切换加载在线课件 */
    public static SysLogEntity changeToOnlineLoad = new SysLogEntity(ShellingScienceWebViewLog, "changeToOnlineLoad");
    /** 提交答案失败 */
    public static SysLogEntity submitAnswertFailed = new SysLogEntity(ShellingScienceWebViewLog, "submitAnswertFailed");
    /** 提交答案成功 */
    public static SysLogEntity submitAnswerSuccess = new SysLogEntity(ShellingScienceWebViewLog, "submitAnswerSuccess");
    /** 获取答题结果开始 */
    public static SysLogEntity fetchAnswerStart = new SysLogEntity(ShellingScienceWebViewLog, "fetchAnswerStart");
    /** 获取答题结果成功 */
    public static SysLogEntity fetchAnswerSuccess = new SysLogEntity(ShellingScienceWebViewLog, "fetchAnswerSuccess");
    /** 强制收卷 */
    public static SysLogEntity ShellingCommit = new SysLogEntity(ShellingScienceWebViewLog, "ShellingCommit");
    /** 手动退出界面 */
    public static SysLogEntity backButtonClick = new SysLogEntity(ShellingScienceWebViewLog, "backButtonClick");
}
