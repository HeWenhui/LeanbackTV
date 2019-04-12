package com.xueersi.parentsmeeting.modules.livevideo.business;

public interface IAuditIRCMessage{

    void onNetWorkChange(int netWorkType);

    void create();

    String getNickname();

    void setIrcTalkConf(IRCTalkConf ircTalkConf);

    void sendNotice(String target, String notice);

    void sendMessage(String target, String message);

    void sendMessage(String target,String psid, String message);

    void destory();

    void setCallback(AuditIRCCallback ircCallback);

    boolean isConnected();

    void startVideo();


}
