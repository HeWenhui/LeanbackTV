package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AuditIRCCallback;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.IRCTalkConf;

public interface IAuditIRCMessage{

    void onNetWorkChange(int netWorkType);

    void create();

    String getNickname();

    void setIrcTalkConf(IRCTalkConf ircTalkConf);

    void sendNotice(String target, String notice);

    void sendMessage(String target, String message);

    void sendMessage(String target, String psid, String message);

    void destory();

    void setCallback(AuditIRCCallback ircCallback);

    boolean isConnected();

    void startVideo();


}
