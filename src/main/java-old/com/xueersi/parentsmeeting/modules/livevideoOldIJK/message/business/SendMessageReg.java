package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business;

public interface SendMessageReg {
    void addOnSendMsg(OnSendMsg onSendMsg);

    void removeOnSendMsg(OnSendMsg onSendMsg);

    interface OnSendMsg {
        void onSendMsg(String msg);
    }
}
