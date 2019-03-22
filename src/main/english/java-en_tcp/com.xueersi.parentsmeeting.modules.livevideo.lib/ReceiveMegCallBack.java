package com.xueersi.parentsmeeting.modules.livevideo.lib;

/**
 * tcp回调
 */
public interface ReceiveMegCallBack {

    void onConnect(GroupGameTcp oldGroupGameTcp);

    void onReceiveMeg(short type, int operation, String msg);

    void onDisconnect(GroupGameTcp oldGroupGameTcp);
}
