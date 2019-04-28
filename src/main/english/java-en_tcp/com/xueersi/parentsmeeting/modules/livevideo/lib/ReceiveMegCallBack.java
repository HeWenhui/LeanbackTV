package com.xueersi.parentsmeeting.modules.livevideo.lib;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * tcp回调
 */
public interface ReceiveMegCallBack {

    void onConnect(GroupGameTcp oldGroupGameTcp);

    void onReceiveMeg(short type, int operation, String msg);

    void onDisconnect(GroupGameTcp oldGroupGameTcp);

    void onReadException(InetSocketAddress inetSocketAddress, Exception e, File saveFile);
}
