package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;
import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;

import org.json.JSONObject;

public interface TcpMessageReg extends LiveProvide {

    void onConnect(OnTcpConnect onTcpConnect);

    void send(final short type, final int operation, final String bodyStr);

    void send(final short type, final int operation, final String bodyStr, final SendCallBack sendCallBack);

    void send(final short type, final int operation, final JSONObject bodyJson, final AbstractBusinessDataCallBack callBack);

    void registTcpMessageAction(TcpMessageAction tcpMessageAction);

    void unregistTcpMessageAction(TcpMessageAction tcpMessageAction);

    interface OnTcpReg {
        void onReg();
    }

    interface OnTcpConnect {
        void onTcpConnect();
    }
}
