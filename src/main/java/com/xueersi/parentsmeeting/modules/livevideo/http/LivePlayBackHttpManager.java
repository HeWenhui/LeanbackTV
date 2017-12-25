package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.string.StringUtils;


/**
 * 直播回放网络访问类
 */
public class LivePlayBackHttpManager extends BaseHttpBusiness {


    public LivePlayBackHttpManager(Context context) {
        super(context);
    }

    /**
     * 提交互动题
     *
     * @param enStuId
     * @param srcType         题库
     * @param testId
     * @param testResult
     * @param testDay
     * @param classId
     * @param requestCallBack
     */
    public void saveTestRecord(String enStuId, String srcType, String testId, String testResult, String testDay,
                               String classId,
                               int livePlayType, boolean voice, HttpCallBack requestCallBack) {
        String liveUrl;
        // 如果是录播直播回放
        if (livePlayType == LocalCourseConfig.LIVETYPE_RECORDED) {
            liveUrl = LiveVideoConfig.URL_STUDY_SAVE_TEST_RECORD;
            // 直播讲座
        } else if (livePlayType == LocalCourseConfig.LIVETYPE_LECTURE) {
            liveUrl = LiveVideoConfig.URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION;
        } else {
            liveUrl = LiveVideoConfig.URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK;
        }
        HttpRequestParams params = new HttpRequestParams();
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", classId);
        params.addBodyParam("testDay", testDay);
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testResult);
        params.addBodyParam("useVoice", voice ? "1" : "0");
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(liveUrl, params, requestCallBack);
    }

    public void sumitCourseWareH5(String enStuId, String srcType, String testId, String testResult, String testDay,
                                  String classId, String isSubmit, String type,
                                  double voiceTime, HttpCallBack requestCallBack) {
        String liveUrl = LiveVideoConfig.URL_LIVE_SUBMIT_TEST_H5_ANSWER;
        HttpRequestParams params = new HttpRequestParams();
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", classId);
        params.addBodyParam("testDay", testDay);
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testResult);
        params.addBodyParam("type", type);
        params.addBodyParam("isPlayBack", "1");
        params.addBodyParam("testNum", "1");
        params.addBodyParam("isSubmit", isSubmit);
        params.addBodyParam("useVoice", "1");
        params.addBodyParam("voiceTime", "" + voiceTime);
        sendPost(liveUrl, params, requestCallBack);
    }


    /**
     * 获取红包
     *
     * @param enStuId
     * @param operateId
     * @param classId
     * @param requestCallBack
     */
    public void getRedPacket(String enStuId, String operateId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(LiveVideoConfig.URL_STUDY_GET_RED_PACKET, params, requestCallBack);
    }

    /**
     * 获取直播红包
     *
     * @param enStuId
     * @param operateId
     * @param requestCallBack
     */
    public void getLivePlayRedPacket(String enStuId, String operateId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(LiveVideoConfig.URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD, params, requestCallBack);
    }

    public void getLiveLectureMsgs(String enstuId, String keyName, int count, String start, int sort,
                                   HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("keyName", keyName);
        params.addBodyParam("count", "" + count);
        params.addBodyParam("start", start);
        params.addBodyParam("sort", "" + sort);
        sendPost(LiveVideoConfig.URL_PUBLIC_LIVE_COURSE_GET_MESSAGE, params, requestCallBack);
    }

    public void getSpeechEval(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        sendPost(LiveVideoConfig.URL_LIVE_GET_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult(String enstuId, String liveId, String id, String stuAnswer, String times, int
            entranceTime, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("stuAnswer", "" + stuAnswer);
        params.addBodyParam("times", "" + times);
        params.addBodyParam("entranceTime", "" + entranceTime);
        params.addBodyParam("type", "2");
        sendPost(LiveVideoConfig.URL_LIVE_SEND_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult2(String enstuId, String liveId, String id, String stuAnswer, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "1");
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "2");
        sendPost(LiveVideoConfig.URL_LIVE_SEND_SPEECHEVAL42, params, requestCallBack);
    }

    public void speechEval42IsAnswered(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        sendPost(LiveVideoConfig.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }

    public void getVoiceWareTestInfo(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        sendPost(LiveVideoConfig.URL_LIVE_LECTURE_VOICE_WARE, params, requestCallBack);
    }
}