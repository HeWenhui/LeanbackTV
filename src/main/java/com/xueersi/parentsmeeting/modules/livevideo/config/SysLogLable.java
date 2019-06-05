package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.SysLogEntity;

/**
 * 系统日志埋点
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=18910075
 */
public class SysLogLable {
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

    /** 主讲辅导态切换 */
    private static String ShellingScienceWebViewLog = "ShellingScienceWebViewLog";
}
