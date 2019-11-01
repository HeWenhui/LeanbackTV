package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.SysLogEntity;

/**
 * 系统日志埋点
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=18910075
 * https://wiki.zhiyinlou.com/pages/viewpage.action?pageId=31222797
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
    /** 三端协议日志 */
    public static SysLogEntity xesWebLog = new SysLogEntity(ShellingScienceWebViewLog, "xesWebLog");
    /** 显示结果页 */
    public static SysLogEntity courseShowResult = new SysLogEntity(ShellingScienceWebViewLog, "showResult");
    /** 关闭结果页 */
    public static SysLogEntity courseCloseResult = new SysLogEntity(ShellingScienceWebViewLog, "closeResult");
    /** 语音答题 lable */
    private static String voiceanswer = "WXMutVoiceAnswer";
    /** 选择题结果 */
    public static SysLogEntity voiceSelectResult = new SysLogEntity(voiceanswer, "voiceSelectResult");
    /** 填空题结果 */
    public static SysLogEntity voiceFillinResult = new SysLogEntity(voiceanswer, "voiceFillinResult");
    /** 加载去壳课件开始 */
    public static SysLogEntity voiceCommit = new SysLogEntity(voiceanswer, "voicecommit");
    /** 切手动 */
    public static SysLogEntity switchQuestion = new SysLogEntity(voiceanswer, "switchQuestion");
    /** 评测失败 */
    public static SysLogEntity voiceError = new SysLogEntity(voiceanswer, "voiceError");
    /** 强制提交 */
    public static SysLogEntity voiceAnswerExamSubmitAll = new SysLogEntity(voiceanswer, "examSubmitAll");

    /** roleplay lable */
    private static String roleplayMachine = "roleplayMachineFlow";
    /** 拉题成功 */
    public static SysLogEntity rolePlayRequestTestInfoSuc = new SysLogEntity(roleplayMachine, "requestTestInfoSuc");
    /** 拉题失败 */
    public static SysLogEntity rolePlayRequestTestInfoErr = new SysLogEntity(roleplayMachine, "requestTestInfoErr");
    /** 拉题 */
    public static SysLogEntity rolePlayGetTest = new SysLogEntity(roleplayMachine, "getTest");
    /** 拉题 */
    public static SysLogEntity rolePlayWait = new SysLogEntity(roleplayMachine, "waitRoleplay");
    /** 启动评测 */
    public static SysLogEntity roleplayStartRecog = new SysLogEntity(roleplayMachine, "startRecog");
    /** 评测开始 */
    public static SysLogEntity roleplayRecogBegin = new SysLogEntity(roleplayMachine, "recogBegin");
    /** 评测成功 */
    public static SysLogEntity roleplayRecogSuccess = new SysLogEntity(roleplayMachine, "recogSuccess");
    /** 评测失败 */
    public static SysLogEntity roleplayRecogError = new SysLogEntity(roleplayMachine, "recogError");
    /** 准备读下一条 */
    public static SysLogEntity roleplayNextRead = new SysLogEntity(roleplayMachine, "roleplayNextRead");
    /** 开始提交 */
    public static SysLogEntity roleplaySubmit = new SysLogEntity(roleplayMachine, "recogSubmit");
    /** 强制提交 */
    public static SysLogEntity roleplayexamSubmitAll = new SysLogEntity(roleplayMachine, "examSubmitAll");
    /** 关闭页面 */
    public static SysLogEntity roleplaStopQues = new SysLogEntity(roleplayMachine, "stopQues");
    /** 关闭页面 */
    public static SysLogEntity roleplayClosePager = new SysLogEntity(roleplayMachine, "closePager");

    /** 语音答题 lable */
    private static String speechEvaluating = "speechEvaluating";
    /** 评测成功 */
    public static SysLogEntity speechCreate = new SysLogEntity(speechEvaluating, "speechcreate");
    /** 评测成功 */
    public static SysLogEntity speechSuccess = new SysLogEntity(speechEvaluating, "recogSuccess");
    /** 评测成功 */
    public static SysLogEntity speechError = new SysLogEntity(speechEvaluating, "recogError");
    /** 强制提交 */
    public static SysLogEntity speechExamSubmitAll = new SysLogEntity(speechEvaluating, "examSubmitAll");
    /** 强制提交 */
    public static SysLogEntity speechStartSubmit = new SysLogEntity(speechEvaluating, "startSubmit");
    /** 强制提交 */
    public static SysLogEntity speechSubmitFail= new SysLogEntity(speechEvaluating, "submitFail");
}
