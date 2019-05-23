package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;

public interface TcpMessageReg {

    void onConnect(OnTcpConnect onTcpConnect);

    void send(final short type, final int operation, final String bodyStr);

    void send(final short type, final int operation, final String bodyStr, final SendCallBack sendCallBack);

    void registTcpMessageAction(TcpMessageAction tcpMessageAction);

    void unregistTcpMessageAction(TcpMessageAction tcpMessageAction);

    interface OnTcpReg {
        void onReg();
    }

    interface OnTcpConnect {
        void onTcpConnect();
    }
}
