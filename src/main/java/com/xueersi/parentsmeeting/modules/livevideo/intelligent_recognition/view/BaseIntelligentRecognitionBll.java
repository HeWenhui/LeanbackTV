package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.support.v4.app.FragmentActivity;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http.IntelligentRecognitionHttpResponseParser;

public class BaseIntelligentRecognitionBll {
    private IntelligentRecognitionHttpManager httpManager;

    protected FragmentActivity mActivity;

    private IntelligentRecognitionHttpResponseParser intelligentRecognitionHttpResponseParser;

    protected BaseIntelligentRecognitionBll(FragmentActivity context) {
        httpManager = new IntelligentRecognitionHttpManager(mActivity = context);
        intelligentRecognitionHttpResponseParser = new IntelligentRecognitionHttpResponseParser();
    }

    protected IntelligentRecognitionHttpManager getHttpManager() {
        return httpManager;
    }

    protected IntelligentRecognitionHttpResponseParser getHttpResponseParser() {
        return intelligentRecognitionHttpResponseParser;
    }

}
