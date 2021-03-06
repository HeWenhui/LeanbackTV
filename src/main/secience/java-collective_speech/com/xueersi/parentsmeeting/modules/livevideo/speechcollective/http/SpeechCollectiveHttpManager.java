package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

public class SpeechCollectiveHttpManager {
    LiveHttpManager liveHttpManager;

    public SpeechCollectiveHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
    }

    public void uploadSpeechMsg(String voiceId, String msg, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("voiceId", "" + voiceId);
        httpRequestParams.addBodyParam("msg", "" + msg);
        liveHttpManager.setDefaultParameter(httpRequestParams);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfig().inner.URL_UPLOAD_SPEECH_MSG, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                callBack.onDataSucess(responseEntity);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                callBack.onDataFail(0, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(1, msg);
            }
        });
    }
}
