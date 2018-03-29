package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;

/**
 * 聊天房间事件
 * Created by linyuqiang on 2016/8/18.
 */
public interface RoomAction {

    /** 开始连接 */
    void onStartConnect();

    /** 在连接 */
    void onConnect();

    /** 在注册 */
    void onRegister();

    /** 断开连接 */
    void onDisconnect();

    /** 连接后，用户列表 */
    void onUserList(String channel, User[] users);

    /** 正常消息 */
    void onMessage(String target, String sender, String login, String hostname, String text, String headurl);

    /** 私聊消息 */
    void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String
            message);

    /** 用户加入 */
    void onJoin(String target, String sender, String login, String hostname);

    /** 用户退出 */
    void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason);

    /** 被踢，目前没用到 */
    void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick,
                String reason);

    /**
     * 被禁言
     *
     * @param disable    是不是被禁言
     * @param fromNotice 从irc的onNotice来的命令，不是禁言状态提示，onTopic的不提示
     */
    void onDisable(boolean disable, boolean fromNotice);

    /**
     * 关闭开启聊天
     *
     * @param openchat 是否开启聊天
     * @param mode     直播或辅导模式
     */
    void onopenchat(boolean openchat, String mode, boolean fromNotice);

    /** 关闭开启弹幕 */
    void onOpenbarrage(final boolean openbarrage, boolean fromNotice);

    /** 语音聊天状态 */
    void videoStatus(String status);
}
