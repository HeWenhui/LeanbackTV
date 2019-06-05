package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * 系统日志埋点
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=18910075
 */
public class SysLogLable {
    /** IRC连接 */
    public static String connectIrcServer = "connectIrcServer";
    /** IRC连接成功 */
    public static String connectIRCSuccess = "connectIRCSuccess";
    /** IRC连接失败 */
    public static String connectIRCDidFailed = "connectIRCDidFailed";
    /** 接收到topic消息 */
    public static String receivedMessageOfTopic = "receivedMessageOfTopic";
    /** 接收到notice消息 */
    public static String receivedMessageOfNotic = "receivedMessageOfNotic";
    /** 主讲辅导态切换 */
    public static String switchLiveMode = "switchLiveMode";
}
