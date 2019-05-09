package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

public interface SpeechCollectiveHttp {
    void uploadSpeechMsg(String voiceId, String msg, final AbstractBusinessDataCallBack callBack);

    void sendSpeechMsg(String voiceId, String msg);
}
