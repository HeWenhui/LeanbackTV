package com.xueersi.parentsmeeting.modules.livevideo.lib;

/**
 * 消息发送
 */
public interface SendCallBack {
    void onNoOpen();

    void onStart(int seq);

    void onReceiveMeg(short type, int operation, int seq, String msg);

    void onTimeOut();
}
