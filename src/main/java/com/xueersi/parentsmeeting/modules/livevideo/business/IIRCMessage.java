package com.xueersi.parentsmeeting.modules.livevideo.business;

public interface IIRCMessage {

    void onNetWorkChange(int netWorkType);

    void create();

    String getConnectNickname();

    String getNickname();

    void sendNotice(String notice);

    void sendNotice(String target, String notice);

    void sendMessage(String target, String message);

    void sendMessage(String message);

    void destory();

    void setCallback(IRCCallback ircCallback);

    void modeChange(String mode);

    boolean onUserList();

    boolean isConnected();
}
