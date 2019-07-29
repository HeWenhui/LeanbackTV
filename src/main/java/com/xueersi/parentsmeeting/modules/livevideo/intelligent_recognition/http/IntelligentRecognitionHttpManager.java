package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

public class IntelligentRecognitionHttpManager {
    private BaseHttpBusiness liveHttpManager;

    public IntelligentRecognitionHttpManager(Context context) {
        liveHttpManager = new LiveHttpManager(context.getApplicationContext());
    }

    /**
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=18569162
     */
    public void getIEResult(Context context,
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

        if (AppConfig.DEBUG) {
            liveHttpManager.sendPost("https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/getMaterialVoiceInfos", params, httpCallBack);
        } else {
            liveHttpManager.sendPost(LiveVideoConfig.SUPER_SPEAKER_SPEECH_SHOW_CAMERA_STATUS, params, httpCallBack);
        }

    }

    /**
     * 英语智能测评第一次提交测评数据
     *
     * @param context
     * @param stuId
     * @param materialId
     * @param isPlayBack
     * @param answers
     * @param liveId
     * @param stuCouId
     * @param testTime
     * @param httpCallBack
     */
    public void getIntelligentSpeechSumbmitResult(Context context,
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
        if (AppConfig.DEBUG) {
            liveHttpManager.sendPost("https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/submitIntellectVoice", params, httpCallBack);
        } else {
            liveHttpManager.sendPost(LiveVideoConfig.URL_INTELLIGENT_SPEECH_SUBMIT, params, httpCallBack);
        }
    }

    /**
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=13837410
     * 全身直播获取语音测评二期TOP3
     *
     * @param liveId       直播id
     * @param testId       试题id
     * @param classId      班级id
     * @param teamId       小组id
     * @param httpCallBack
     */
    public void getIntelligentTop3Data(String liveId,
                                       String testId,
                                       String classId,
                                       String teamId,
                                       HttpCallBack httpCallBack) {

        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("liveId", liveId);
        httpRequestParams.addBodyParam("testId", testId);
        httpRequestParams.addBodyParam("classId", classId);
        httpRequestParams.addBodyParam("teamId", teamId);
        if (AppConfig.DEBUG) {
            liveHttpManager.sendPost("https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/getSpeechEvalAnswerTeamRank", httpRequestParams, httpCallBack);
        } else {
            liveHttpManager.sendPost(LiveVideoConfig.URL_INTELLIGENT_RECOGNITION_TOP3, httpRequestParams, httpCallBack);
        }

    }

    /**
     * 智能语音反馈纠音及重读提交接口
     *
     * @param stuId
     * @param materialId
     * @param isPlayBack
     * @param liveId
     * @param stuCouId
     * @param correctCase
     * @param rereadCase
     * @param httpCallBack
     */
    public void getSubmitIntellectVoiceCorrect(Context context,
                                               String stuId,
                                               String materialId,
                                               String isPlayBack,
                                               String liveId,
                                               String stuCouId,
                                               String correctCase,
                                               String rereadCase,
                                               HttpCallBack httpCallBack) {
        if (liveHttpManager == null) {
            liveHttpManager = new LiveHttpManager(context);
        }
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("materialId", materialId);
        httpRequestParams.addBodyParam("isPlayBack", isPlayBack);
        httpRequestParams.addBodyParam("liveId", liveId);
        httpRequestParams.addBodyParam("stuCouId", stuCouId);
        httpRequestParams.addBodyParam("correctCase", correctCase);
        httpRequestParams.addBodyParam("rereadCase", rereadCase);
        liveHttpManager.sendPost(LiveVideoConfig.URL_SUBMIT_INTELLECT_VOICE_CORRECT, httpRequestParams, httpCallBack);
    }
}
