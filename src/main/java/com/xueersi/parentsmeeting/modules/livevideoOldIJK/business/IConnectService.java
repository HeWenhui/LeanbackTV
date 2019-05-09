package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

/**
 * 连接服务器失败,体验课使用
 */
public interface IConnectService {
    /**
     * wiki地址 https://wiki.xesv5.com/pages/viewpage.action?pageId=13842928
     *
     * @param serverIp   聊天服务器ip
     * @param serverPort 聊天服务器端口
     * @param errMsg     链接聊天服务器失败信息
     * @param ip         自己的ip
     */
    void connectChatServiceError(
            String serverIp,
            String serverPort,
            String errMsg,
            String ip);
}
