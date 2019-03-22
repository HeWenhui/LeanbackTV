package com.xueersi.parentsmeeting.modules.livevideo.lib;

public interface ReceiveMegCallBack {
    void onReceiveMeg(short type, int operation, String msg);

    void onDisconnect(GroupGameTcp groupGameTcp);
}
