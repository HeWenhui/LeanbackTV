package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;

import org.json.JSONArray;

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
     * @param srcType         题库
     * @param testId
     * @param testResult
     * @param testDay
     * @param classId
     * @param isRight
     * @param requestCallBack
     */
    public void saveTestRecord(boolean isNewArt, String srcType, String testId, String testResult, String testDay,
                               String classId,
                               int livePlayType, boolean voice, boolean isRight, HttpCallBack requestCallBack) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTSTEST_ANSWER;
            params.addBodyParam("liveId", classId);
            params.addBodyParam("testId", testId);
            params.addBodyParam("answer", testResult);
            params.addBodyParam("isPlayBack", "2");
            params.addBodyParam("isForce", "1");
            setDefaultParameter(params);
            sendPost(url, params, requestCallBack);
            Loger.e("Duncan", "======> liveNewArtsSubmitTestAnswerforback:" + url);

        } else {
            String liveUrl;
            // 如果是录播直播回放
            if (livePlayType == LocalCourseConfig.LIVETYPE_RECORDED) {
                liveUrl = LiveHttpConfig.URL_STUDY_SAVE_TEST_RECORD;
                // 直播讲座
            } else if (livePlayType == LocalCourseConfig.LIVETYPE_LECTURE) {
                liveUrl = LiveHttpConfig.URL_PUBLIC_LIVE_COURSE_SUBMIT_QUESTION;
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
            params.addBodyParam("classId", "" + classId);
            params.addBodyParam("liveId", "" + classId);
            params.addBodyParam("testDay", "" + testDay);
            params.addBodyParam("livePlayType", "" + livePlayType);
//        params.addBodyParam("enstuId", enStuId);
            params.addBodyParam("testId", "" + testId);
            if (voice) {
                params.addBodyParam("answer", "" + testResult);
            } else {
                params.addBodyParam("testAnswer", "" + testResult);
            }
            params.addBodyParam("useVoice", voice ? "1" : "0");
            params.addBodyParam("isRight", isRight ? "1" : "0");
            params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
            sendPost(liveUrl, params, requestCallBack);
        }
    }

    /**
     * 提交h5语音答题答案
     *
     * @param enStuId
     * @param srcType
     * @param testId
     * @param testResult
     * @param testDay
     * @param classId
     * @param isSubmit
     * @param type
     * @param voiceTime
     * @param isRight
     * @param requestCallBack
     */
    public void sumitCourseWareH5(boolean isNewArt, String srcType, String testId, String testResult, String testDay,
                                  String classId, String type, String isSubmit,
                                  double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        if (isNewArt) {
            if ("16".equals(type) || "15".equals(type)) {
                HttpRequestParams params = new HttpRequestParams();
                String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTSH5_ANSWER;
                setDefaultParameter(params);
                params.addBodyParam("testId", testId);
                params.addBodyParam("liveId", classId);
                params.addBodyParam("type", type);
                params.addBodyParam("isRight", isRight ? "1" : "0");
                params.addBodyParam("isPlayBack", "2");
                params.addBodyParam("isSubmit", isSubmit);
                params.addBodyParam("voiceUrl", "");
                params.addBodyParam("voiceTime", "" + voiceTime);
                params.addBodyParam("url", "");
                params.addBodyParam("imageUrl", "");
                params.addBodyParam("userAnswer", LiveVideoConfig.userAnswer);
                params.addBodyParam("answer", LiveVideoConfig.answer);
                sendPost(url, params, requestCallBack);
            } else {
                HttpRequestParams params = new HttpRequestParams();
                String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTS_ANSWER;
                setDefaultParameter(params);
                params.addBodyParam("liveId", classId);
                params.addBodyParam("answers", testResult);
                params.addBodyParam("isPlayBack", "2");
                params.addBodyParam("isForce", isSubmit);
                sendPost(url, params, requestCallBack);
            }
        } else {
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

    }


    /**
     * 获取红包
     *
     * @param operateId
     * @param classId
     * @param requestCallBack
     */
    public void getRedPacket(String operateId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
        sendPost(LiveHttpConfig.URL_STUDY_GET_RED_PACKET, params, requestCallBack);
    }

    /**
     * 获取直播红包
     *
     * @param operateId
     * @param requestCallBack
     */
    public void getLivePlayRedPacket(String operateId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
        sendPost(liveVideoSAConfigInner.URL_STUDY_RECEIVE_LIVE_PLAY_RED_PACKET_GOLD, params, requestCallBack);
    }

    /**
     * 获取体验直播课红包
     *
     * @param requestCallBack
     */
    public void getLivePlayRedPackets(String operateId, String termId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enStuId);
        params.addBodyParam("operateId", operateId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("liveId", liveId);
        sendPost(LiveHttpConfig.URL_AUTO_LIVE_RECEIVE_GOLD, params, requestCallBack);
    }

    //获取语音评测
    public void getSpeechEval(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        sendPost(LiveHttpConfig.URL_LIVE_GET_SPEECHEVAL, params, requestCallBack);
    }

    //语音评测2期答案提交
    public void sendSpeechEvalResult2(boolean isNewArt, String liveId, String id, String stuAnswer, HttpCallBack
            requestCallBack, String isSubmit) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            params.addBodyParam("liveId", liveId);
            params.addBodyParam("testId", id);
            params.addBodyParam("isRejected", "1");
            params.addBodyParam("isSubmit", "" + isSubmit);
            params.addBodyParam("answers", "" + stuAnswer);
            params.addBodyParam("type", "2");
            setDefaultParameter(params);
            Loger.i("Duncan", "sendSpeechEvalResult2:liveId=" + liveId);
            sendPost(LiveHttpConfig.URL_LIVE_SEND_SPEECHEVALUATEARTS, params, requestCallBack);
        } else {
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

    }

    //判断语音评测2期是否作答
    public void speechEval42IsAnswered(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }

    /**
     * 获得广告信息
     */
    public void getAdOnLL(String liveId, String courseId, final HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_LEC_AD;
        sendPost(LiveHttpConfig.URL_LIVE_GET_LEC_AD, params, requestCallBack);
    }

    /**
     * 提交体验课交互信息
     *
     * @param termId
     * @param times
     * @param requestCallBack
     */
    public void sendExpeRecordInteract(String url, String termId, int times, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        setDefaultParameter(params);
        params.addBodyParam("termId", termId);
        params.addBodyParam("times", times + "");
        sendPost(url, params, requestCallBack);
    }

    // 04.11 获取讲座直播回放中更多课程的广告信息
    public void getMoreCourseChoices(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_LEC_AD_CASE, params, requestCallBack);
    }

    // 获取体验课学习报告
    public void getExperienceResult(String termId, String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("orderId", termId);
        params.addBodyParam("liveId", liveId);
        sendPost(LiveHttpConfig.URL_AUTO_LIVE_FEAD_BACK, params, requestCallBack);
    }

    //发送体验课学习反馈
    public void sendExperienceFeedback(String user_id, String plan_id, String subject_id, String grade_id, String
            order_id, String suggest, JSONArray jsonOption, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("user_id", user_id);
        params.addBodyParam("plan_id", plan_id);
        params.addBodyParam("subject_id", subject_id);
        params.addBodyParam("grade_id", grade_id);
        params.addBodyParam("order_id", order_id);
        params.addBodyParam("suggest", suggest);
        params.addBodyParam("option", jsonOption.toString());
//        sendPost("https://www.easy-mock.com/mock/5b57f6919ddd1140ec2eb47b/xueersi.wx.android
// .app/livevideo/feedback",params,requestCallBack);
        sendPost(LiveHttpConfig.URL_AUTO_LIVE_LEARN_FEED_BACK, params, requestCallBack);
    }

//    public void saveExperienceTestRecord(String url, String enStuId, String srcType, String testId, String
//            testResult, String testDay, String classId, int livePlayType, boolean voice, boolean isRight, HttpCallBack
//                                                 requestCallBack) {
//        String liveUrl;
//        // 如果是录播直播回放
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
//        HttpRequestParams params = new HttpRequestParams();
//        setDefaultParameter(params);
//        if (!StringUtils.isSpace(srcType)) {
//            params.addBodyParam("srcType", srcType);
//        }
//        params.addBodyParam("classId", classId);
//        params.addBodyParam("liveId", classId);
//        params.addBodyParam("testDay", testDay);
////        params.addBodyParam("enstuId", enStuId);
//        params.addBodyParam("testId", testId);
//        if (voice) {
//            params.addBodyParam("answer", testResult);
//        } else {
//            params.addBodyParam("testAnswer", testResult);
//        }
//        params.addBodyParam("useVoice", voice ? "1" : "0");
//        params.addBodyParam("isRight", isRight ? "1" : "0");
//        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
//        sendPost(url, params, requestCallBack);
//    }

    //发送体验课语音评测答案
    public void sendExpSpeechEvalResult(String url,
                                        String liveId,
                                        String testId,
                                        String termId,
                                        String isArts,
                                        String answers,
                                        HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("isArts", isArts);
        params.addBodyParam("answers", answers);
        sendPost(url, params, requestCallBack);
    }

    /**
     * 获取体验课语音评测
     *
     * @param enstuId
     * @param liveId
     * @param id
     * @param requestCallBack
     */
    public void getExpeSpeechEval(String enstuId, String liveId, String id, String url, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        sendPost(url, params, requestCallBack);
    }

    /**
     * 体验课提交互动题
     *
     * @param srcType         题库
     * @param testId
     * @param testResult
     * @param liveId
     * @param termId
     * @param isRight
     * @param requestCallBack
     */
    public void saveTestRecords(String srcType, String testId, String testResult, String liveId,
                                String termId,
                                int livePlayType, boolean voice, boolean isRight, String isArts, String questionType,
                                String url,
                                HttpCallBack requestCallBack) {
        String liveUrl = LiveHttpConfig.LIVE_EXPE_SUBMIT;
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
        /** 语文主观题提交*/
        if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(questionType)) {
            liveUrl = url;
            params.addBodyParam("isArts", isArts);
        }
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testResult);
        sendPost(liveUrl, params, requestCallBack);
    }

    //提交体验课h5语音答题
    public void submitExperienceCourseWareH5(
            String url,
            String liveId,
            String testId,
            String termId,
            String answer,
            double voiceTime,
            boolean isRight,
            String isArts,
            HttpCallBack requestCallBack) {
//        String liveUrl = LiveVideoConfig.URL_EXPE_SUBMIT_TEST_H5_ANSWER;
        HttpRequestParams params = new HttpRequestParams();
//        setDefaultParameter(params);
//        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("answer", answer);
        params.addBodyParam("voiceTime", "" + voiceTime);
        params.addBodyParam("isArts", isArts);
        params.addBodyParam("isRight", isRight ? "1" : "0");
        sendPost(url, params, requestCallBack);
//        sendPost("http://student.xueersi.com/science/AutoLive/submitCourseWareH5AnswerUseVoice", params,
// requestCallBack);
    }

    /**
     * 直播回放视频播放访问时长接口
     *
     * @param stuCouId
     * @param liveid
     * @param hbTime
     */
    public void sendLiveCourseVisitTime(String stuCouId, String liveid, int hbTime, HttpCallBack
            httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("liveId", liveid);
        params.addBodyParam("hbTime", "" + hbTime);
        params.addBodyParam("systemName", "android");
        params.addBodyParam("fromType", "4");
        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
        sendPost(liveVideoSAConfigInner.URL_LIVE_VISITTIME, params, httpCallBack);
    }

    /**
     * 回放获取弹幕接口(理科、语文)
     *
     * @param requestCallBack
     */
    public void getVoiceBarrageMsg(String liveId, String stuCouId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_GET_VOICE_BARRAGE_MSG, params, requestCallBack);
    }

    /**
     * 回放获取弹幕接口(英语)
     *
     * @param requestCallBack
     */
    public void getVoiceBarrageForPlayBack(String groupId, String startTime, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("groupId", groupId);
        params.addBodyParam("startTime", startTime);
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_ENGLISH_GET_VOICE_BARRAGE_MSG, params, requestCallBack);
    }

    /**
     * 发送课中体验课懂了吗
     *
     * @param url
     * @param userId       学生id
     * @param gradeId      年级id
     * @param planId       场次id
     * @param subjectId    学科id
     * @param orderId      订单id
     * @param optionTpye   选项：1听懂了，很简单,2似懂非懂,3没听懂，太难了
     * @param httpCallBack
     */
    public void sendStandExperienceUnderStand(
            String url,
            String userId,
            String gradeId,
            String planId,
            String subjectId,
            String orderId,
            String optionTpye,
            HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("userId", userId);
        params.addBodyParam("orderId", orderId);
        params.addBodyParam("gradeId", gradeId);
        params.addBodyParam("planId", planId);
        params.addBodyParam("subjectId", subjectId);
        params.addBodyParam("optionType", optionTpye);
        sendPost(url, params, httpCallBack);
    }

    /**
     * 获取课中推荐课程信息
     *
     * @param url
     * @param teacherId    主讲老师id
     * @param gradeId      年级id
     * @param subjectId    学科id
     * @param orderId      订单id
     * @param httpCallBack
     */
    public void getRecommondCourseInfo(String url, String teacherId, String gradeId, String subjectId, String
            orderId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("gradeId", gradeId);
        params.addBodyParam("subjectId", subjectId);
        params.addBodyParam("orderId", orderId);
        sendPost(url, params, httpCallBack);
    }

    /**
     * @param url
     * @param liveId       场次id
     * @param orderId      订单id
     * @param httpCallBack
     */
    public void getBuyCourseBannerInfo(String url, String liveId, String orderId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("orderId", orderId);
        sendPost(url, params, httpCallBack);
    }

    public void saveStuPlanOnlineTime(String stuId, String gradeId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("gradeId", gradeId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_STU_ONLINE_TIME, params, requestCallBack);
    }

    public void sendExperienceQuitFeedback(String stuId, String termId, String content, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("content", content);
        sendPost(LiveHttpConfig.URL_AUTO_LIVE_QUIT_FEED_BACK, params, requestCallBack);
    }

    public void sumbitExperienceNoviceGuide(String stuId, String termId, String subjectId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("subjectId", subjectId);
        sendPost(LiveHttpConfig.URL_AUTO_LIVE_NOVIC_GUIDE, params, requestCallBack);
    }


    /**
     * app端提交演讲秀
     *
     * @param liveId       场次id
     * @param stuCouId     学生课程id
     * @param stuId        学生id
     * @param isPlayBack   是否回放(1:直播,2:回放)
     * @param testId       互动题所属题目Id
     * @param srcType      互动题所属题目类型
     * @param isForce      是否是强制提交（1：是 2：否）
     * @param httpCallBack
     */
    public void sendSuperSpeakersubmitSpeech(String liveId, String stuCouId, String stuId, String isPlayBack, String testId, String srcType, String isForce, String videoDuration, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isPlayBack", isPlayBack);
        params.addBodyParam("testId", testId);
        params.addBodyParam("srcType", srcType);
        params.addBodyParam("isForce", isForce);
        params.addBodyParam("videoDuration", videoDuration);
        sendPost(LiveHttpConfig.SUPER_SPEAKER_SUBMIT_SPEECH_SHOW, params, httpCallBack);
    }


    /**
     * app端上传演讲秀视频
     *
     * @param liveId           场次id
     * @param stuCouId         学生课程id
     * @param stuId            学生id
     * @param isPlayBack       是否回放(1:直播,2:回放)
     * @param testId           互动题所属题目Id
     * @param srcType          互动题所属题目类型
     * @param video_url        提交的视频地址
     * @param voice_url        提交的音频地址
     * @param isUpload         是否上传成功(1:上传成功 2：上传失败)
     * @param averVocieDecibel 平均声音分贝数
     */
    public void uploadSpeechShow(String liveId, String stuCouId, String stuId, String isPlayBack, String testId, String srcType, String video_url, String voice_url, String isUpload, String averVocieDecibel
            , HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isPlayBack", isPlayBack);
        params.addBodyParam("testId", testId);
        params.addBodyParam("srcType", srcType);
        params.addBodyParam("video_url", video_url);
        params.addBodyParam("voice_url", voice_url);
        params.addBodyParam("isUpload", isUpload);
        params.addBodyParam("averVocieDecibel", averVocieDecibel);
        sendPost(LiveHttpConfig.SUPER_SPEAKER_UPLOAD_SPEECH_SHOW, params, httpCallBack);
    }


    /**
     * app摄像头开启状态
     *
     * @param liveId 场次id
     * @param stuId  学生id
     * @param testId 互动题所属题目Id
     */
    public void sendSuperSpeakerCameraStatus(String liveId, String stuId, String testId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
//        params.addBodyParam("srcType", srcType);
//        params.addBodyParam("cameraStatus", cameraStatus);
        sendPost(LiveHttpConfig.SUPER_SPEAKER_SPEECH_SHOW_CAMERA_STATUS, params, httpCallBack);

    }

    public void getStuGoldCount(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_STUDY_GOLD_COUNT;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 领取金币
     *
     * @param operateId       金币ID
     * @param liveid          直播ID
     * @param requestCallBack
     */
    public void sendReceiveGold(int operateId, String liveid, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_LIVE_RECEIVE_GOLD;
        requestCallBack.url = url;
        params.addBodyParam("liveId", liveid);
        setDefaultParameter(params);
        //新增参数：红包类型 type=0 默认直播红包不用改 type=1 录播课直播红包 type=2 录播课回放红包
        params.addBodyParam("type", "2");
        params.addBodyParam("operateId", "" + operateId);
        sendPost(url, params, requestCallBack);
    }

}
