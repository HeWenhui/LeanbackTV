package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

public interface TcpMessageReg {
    void send(final short type, final int operation, final String bodyStr);

    void registTcpMessageAction(TcpMessageAction tcpMessageAction);

    void unregistTcpMessageAction(TcpMessageAction tcpMessageAction);
}
