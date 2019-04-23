package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;

public interface IRCCallback {

    void onStartConnect();

    void onConnect(IRCConnection connection);

    void onRegister();

    void onDisconnect(IRCConnection connection, boolean isQuitting);

    void onMessage(String target, String sender, String login, String hostname, String text);

    void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message);

    void onChannelInfo(String channel, int userCount, String topic);

    void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice, String
            channelId);

    void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId);

    void onUserList(String channel, User[] users);

    void onJoin(String target, String sender, String login, String hostname);

    void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel);

    void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick,
                String reason);

    void onUnknown(String line);
}
