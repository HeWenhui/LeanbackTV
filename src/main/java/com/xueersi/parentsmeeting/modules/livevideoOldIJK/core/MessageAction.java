package com.xueersi.parentsmeeting.modules.livevideoOldIJK.core;


import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot.User;

/**
* 直播间 除（notice,topic 外的全量消息）
*@author chekunonMediaViewClick
*created  at 2018/6/20 10:08
*/
public interface MessageAction {

    void onStartConnect();

    void onConnect(IRCConnection connection);

    void onRegister();

    void onDisconnect(IRCConnection connection, boolean isQuitting);

    void onMessage(String target, String sender, String login, String hostname, String text);

    void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message);

    void onChannelInfo(String channel, int userCount, String topic);

    void onUserList(String channel, User[] users);

    void onJoin(String target, String sender, String login, String hostname);

    void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason);

    void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick,
                String reason);

    void onUnknown(String line);

}
