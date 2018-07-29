package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.utils.string.StringUtils;

import java.util.HashMap;


/**
 * 直播回放网络访问类
 */
public class LivePlayBackHttpManager extends BaseHttpBusiness {
    LiveVideoSAConfig.Inner liveVideoSAConfigInner;
    HashMap<String, String> defaultKey = new HashMap<>();

    public LivePlayBackHttpManager(Context context) {
        super(context);
    }

    public void addBodyParam(String key, String value) {
        defaultKey.put(key, value);
    }

    private void setDefaultParameter(HttpRequestParams httpRequestParams) {
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            httpRequestParams.addBodyParam(key, value);
        }
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        liveVideoSAConfigInner = liveVideoSAConfig.inner;
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
     * @param isRight
     * @param requestCallBack
     */
    public void saveTestRecord(String enStuId, String srcType, String testId, String testResult, String testDay,
                               String classId,
                               int livePlayType, boolean voice, boolean isRight, HttpCallBack requestCallBack) {
        String liveUrl;
        // 如果是录播直播回放
        if (livePlayType == LocalCourseConfig.LIVETYPE_RECORDED) {
            liveUrl = LiveVideoConfig.URL_STUDY_SAVE_TEST_RECORD;
            // 直播讲座
        } else if (livePlayType == LocalCourseConfig.LIVETYPE_LECTURE) {
            liveUrl = LiveVideoConfig.URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION;
        } else {
            if (voice) {
                liveUrl = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER_VOICE;
            } else {
                liveUrl = liveVideoSAConfigInner.URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK;
            }
        }
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", classId);
        params.addBodyParam("testDay", testDay);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("testId", testId);
        if (voice) {
            params.addBodyParam("answer", testResult);
        } else {
            params.addBodyParam("testAnswer", testResult);
        }
        params.addBodyParam("useVoice", voice ? "1" : "0");
        params.addBodyParam("isRight", isRight ? "1" : "0");
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(liveUrl, params, requestCallBack);
    }

    public void sumitCourseWareH5(String enStuId, String srcType, String testId, String testResult, String testDay,
                                  String classId, String isSubmit, String type,
                                  double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        String liveUrl = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_H5_ANSWER;
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", classId);
        params.addBodyParam("testDay", testDay);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testResult);
        params.addBodyParam("type", type);
        params.addBodyParam("isPlayBack", "1");
        params.addBodyParam("testNum", "1");
        params.addBodyParam("isSubmit", isSubmit);
        params.addBodyParam("useVoice", "1");
        params.addBodyParam("voiceTime", "" + voiceTime);
        params.addBodyParam("isRight", isRight ? "1" : "0");
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
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enStuId);
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
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(liveVideoSAConfigInner.URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD, params, requestCallBack);
    }

    /**
     * 获取体验直播课红包
     *
     * @param enStuId
     * @param requestCallBack
     */
    public void getLivePlayRedPackets(String enStuId, String operateId,String termId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("liveId", liveId);
        sendPost("http://laoshi.xueersi.com/science/AutoLive/receiveGold", params, requestCallBack);
    }

    public void getLiveLectureMsgs(String enstuId, String keyName, int count, String start, int sort,
                                   HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("keyName", keyName);
        params.addBodyParam("count", "" + count);
        params.addBodyParam("start", start);
        params.addBodyParam("sort", "" + sort);
        sendPost(LiveVideoConfig.URL_PUBLIC_LIVE_COURSE_GET_MESSAGE, params, requestCallBack);
    }

    public void getSpeechEval(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        sendPost(LiveVideoConfig.URL_LIVE_GET_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult(String enstuId, String liveId, String id, String stuAnswer, String times, int
            entranceTime, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
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
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "1");
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "2");
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42, params, requestCallBack);
    }

    public void speechEval42IsAnswered(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }

    /** 获得广告信息 */
    public void getAdOnLL(String enstuId, String liveId, String courseId, final HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        requestCallBack.url = LiveVideoConfig.URL_LIVE_GET_LEC_AD;
        sendPost(LiveVideoConfig.URL_LIVE_GET_LEC_AD, params, requestCallBack);
    }

    // 03.14 获取体验课聊天记录
    public void getExperiencenMsgs(String liveId, String classId,Long start,
                                   HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("start", start.toString());
        sendPost("http://laoshi.xueersi.com/science/AutoLive/getLiveCourseMsgs", params, requestCallBack);

    }

    // 04.11 获取讲座直播回放中更多课程的广告信息
    public void getMoreCourseChoices(String liveId, HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        sendPost("http://laoshi.xueersi.com/LiveLecture/getAdCase", params, requestCallBack);

    }

    // 获取体验课学习报告
    public void getExperienceResult(String termId,String liveId,HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("orderId", termId);
        params.addBodyParam("liveId", liveId);
        sendPost("http://laoshi.xueersi.com/science/AutoLive/learnFeedback", params, requestCallBack);
    }

    /**
     * 提交互动题
     *
     * @param enStuId
     * @param srcType         题库
     * @param testId
     * @param testResult
     * @param liveId
     * @param termId
     * @param isRight
     * @param requestCallBack
     */
    public void saveTestRecords(String enStuId, String srcType, String testId, String testResult, String liveId,
                                String termId,
                                int livePlayType, boolean voice, boolean isRight, HttpCallBack requestCallBack) {
        String liveUrl = LiveVideoConfig.LIVE_EXPE_SUBMIT;
        // 如果是录播直播回放
//        if (livePlayType == LocalCourseConfig.LIVETYPE_RECORDED) {
//            liveUrl = LiveVideoConfig.URL_STUDY_SAVE_TEST_RECORD;
//            // 直播讲座
//        } else if (livePlayType == LocalCourseConfig.LIVETYPE_LECTURE) {
//            liveUrl = LiveVideoConfig.URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION;
//        } else {
//            if (voice) {
//                liveUrl = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER_VOICE;
//            } else {
//                liveUrl = liveVideoSAConfigInner.URL_STUDY_SAVE_ANSWER_FOR_PLAYBACK;
//            }
//        }
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testResult);
        sendPost(liveUrl, params, requestCallBack);
    }

    /* 上传回放式体验课播放器播放时长的接口 */
    public void uploadPlaybackPlayTime(int liveId, Long hbTime, HttpCallBack callBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId + "");
        params.addBodyParam("enstuId", UserBll.getInstance().getMyUserInfoEntity().getEnstuId());
        params.addBodyParam("hbTime", hbTime.toString());
        sendPost(LiveVideoConfig.URL_PLAYBACKPLAYTIME, params, callBack);
    }

    /**
     * 直播回放视频播放访问时长接口
     *
     * @param enStuId
     * @param stuCouId
     * @param liveid
     * @param hbTime
     */
    public void sendLiveCourseVisitTime(String enStuId, String stuCouId, String liveid, int hbTime, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("liveId", liveid);
        params.addBodyParam("hbTime", "" + hbTime);
        params.addBodyParam("systemName", "android");
        params.addBodyParam("fromType", "4");
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(liveVideoSAConfigInner.URL_LIVE_VISITTIME, params, httpCallBack);
    }
}
