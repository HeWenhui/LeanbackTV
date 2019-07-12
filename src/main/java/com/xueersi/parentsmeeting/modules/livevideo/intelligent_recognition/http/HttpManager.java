package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http;

import android.content.Context;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

public class HttpManager {
    private static LiveHttpManager liveHttpManager;

    public HttpManager() {
//        liveHttpManager = new LiveHttpManager(context.getApplicationContext());
    }

    /**
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=18569162
     */
    public static void getIEResult(Context context,
                                   String liveId,
                                   String materialId,
                                   String stuId,
                                   String stuCouId,
                                   HttpCallBack httpCallBack) {
        if (liveHttpManager == null) {
            liveHttpManager = new LiveHttpManager(context.getApplicationContext());
        }
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("materialId", materialId);
        params.addBodyParam("stuCouId", stuCouId);
//        params.addBodyParam("srcType", srcType);
//        params.addBodyParam("cameraStatus", cameraStatus);
        liveHttpManager.sendPost(LiveVideoConfig.SUPER_SPEAKER_SPEECH_SHOW_CAMERA_STATUS, params, httpCallBack);

    }

    public static void getIntelligentSpeechSumbmitResult(Context context,
                                                         String stuId,
                                                         String materialId,
                                                         String isPlayBack,
                                                         String answers,
                                                         String liveId,
                                                         String stuCouId,
                                                         String testTime,
//                                                         String useClient,
//                                                         String useClientVer,
                                                         HttpCallBack httpCallBack) {
        if (liveHttpManager == null) {
            liveHttpManager = new LiveHttpManager(context.getApplicationContext());
        }
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("materialId", materialId);
        params.addBodyParam("isPlayBack", isPlayBack);
        params.addBodyParam("answers", answers);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("testTime", testTime);
//        params.addBodyParam("useClient", useClient);
//        params.addBodyParam("useClientVer", useClientVer);
        liveHttpManager.sendPost(LiveVideoConfig.URL_INTELLIGENT_SPEECH_SUBMIT, params, httpCallBack);
    }
}
