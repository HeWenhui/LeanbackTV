package com.xueersi.parentsmeeting.modules.livevideo.business;

public interface IrcAction {
    void sendMessage(String message);

    void sendNotice(String notice);

    void sendNotice(String target, String notice);

    String getNickname();
}
