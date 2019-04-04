package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;

public interface TcpMessageReg {
    void onConnet(OnTcpReg onTcpReg);

    void send(final short type, final int operation, final String bodyStr);

    void send(final short type, final int operation, final String bodyStr, final SendCallBack sendCallBack);

    void registTcpMessageAction(TcpMessageAction tcpMessageAction);

    void unregistTcpMessageAction(TcpMessageAction tcpMessageAction);

    boolean setTest(int testType, String testId);

    interface OnTcpReg {
        void onReg();
    }
}
