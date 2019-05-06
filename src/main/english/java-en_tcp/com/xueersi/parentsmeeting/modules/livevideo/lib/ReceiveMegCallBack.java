package com.xueersi.parentsmeeting.modules.livevideo.lib;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * tcp回调
 */
public interface ReceiveMegCallBack {

    void onConnect(GroupGameTcp oldGroupGameTcp);

    void onReceiveMeg(short type, int operation, String msg);

    void onDisconnect(InetSocketAddress inetSocketAddress, Object obj, GroupGameTcp oldGroupGameTcp);

    void onLog(InetSocketAddress inetSocketAddress, HashMap<String, String> logs);

    void onReadEnd(InetSocketAddress inetSocketAddress, Exception e, File saveFile);

    void onReadException(InetSocketAddress inetSocketAddress, Exception e, File saveFile);
}
