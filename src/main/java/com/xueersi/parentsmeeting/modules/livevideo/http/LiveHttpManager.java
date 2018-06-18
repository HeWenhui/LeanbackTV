package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.http.CommonRequestCallBack;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;

import org.xutils.xutils.common.Callback;
import org.xutils.xutils.common.Callback.CancelledException;
import org.xutils.xutils.common.util.IOUtil;
import org.xutils.xutils.ex.HttpException;
import org.xutils.xutils.http.RequestParams;
import org.xutils.xutils.x;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * 直播网络访问类
 */
public class LiveHttpManager extends BaseHttpBusiness {
    String TAG = "LiveHttpManager";
    HashMap<String, String> defaultKey = new HashMap<>();
    LiveVideoSAConfig.Inner liveVideoSAConfigInner;
    private LiveVideoSAConfig liveVideoSAConfig;

    public LiveHttpManager(Context context) {
        super(context);
    }

    public void setLiveVideoSAConfig(LiveVideoSAConfig liveVideoSAConfig) {
        this.liveVideoSAConfig = liveVideoSAConfig;
        liveVideoSAConfigInner = liveVideoSAConfig.inner;
    }

    public void addBodyParam(String key, String value) {
        defaultKey.put(key, value);
    }

    void setDefaultParameter(HttpRequestParams httpRequestParams) {
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            httpRequestParams.addBodyParam(key, value);
        }
    }

    /**
     * 播放器数据初始化
     *
     * @param enstuId         用户加密ID
     * @param courseId        课程ID
     * @param liveId          直播ID
     * @param isAudit         是不是旁听 isAudit
     * @param requestCallBack
     */
    public void liveGetInfo(String enstuId, String courseId, String liveId, int isAudit, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("isAudit", "" + isAudit);
        setDefaultParameter(params);
        sendPost(LiveVideoConfig.URL_LIVE_GET_INFO, params, requestCallBack);
    }

    /**
     * 直播辅导播放器数据初始化
     *
     * @param enstuId         用户加密ID
     * @param liveId          直播ID
     * @param requestCallBack
     */
    public void liveTutorialGetInfo(String enstuId, String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("classId", liveId);
        sendPost(LiveVideoConfig.URL_LIVE_TUTORIAL_GET_INFO, params, requestCallBack);
    }

    /**
     * 公开直播播放器数据初始化
     *
     * @param enstuId         用户加密ID
     * @param liveId          直播ID
     * @param requestCallBack
     */
    public void liveLectureGetInfo(String enstuId, String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        requestCallBack.url = LiveVideoConfig.URL_LIVE_LECTURE_GET_INFO;
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        sendPost(LiveVideoConfig.URL_LIVE_LECTURE_GET_INFO, params, requestCallBack);
    }

    int getTimes = 1;

    public Callback.Cancelable liveGetPlayServer(final StringBuilder ipsb, final String url2, final CommonRequestCallBack<String>
            requestCallBack) {
        final HttpURLConnectionCancelable cancelable = new HttpURLConnectionCancelable();
        new Thread("liveGetPlayServer:" + getTimes) {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                try {
                    URL url = new URL(url2);
                    InetAddress inetAddress = InetAddress.getByName(url.getHost());
                    ipsb.append(inetAddress.getHostAddress());
                    Loger.d(TAG, "liveGetPlayServer:host=" + url.getHost() + ",ip=" + inetAddress.getHostAddress());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    URL url = new URL(url2);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    cancelable.connection = connection;
                    cancelable.callback = requestCallBack;
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.connect();
                    int statusCode = connection.getResponseCode();
                    if (statusCode == 200) {
                        final String result = IOUtil.readStr(connection.getInputStream());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (!cancelable.isCancel) {
                                    requestCallBack.onSuccess(result);
                                }
                            }
                        });
                    } else {
                        final HttpException exception = new HttpException(statusCode, "");
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (!cancelable.isCancel) {
                                    requestCallBack.onError(exception, false);
                                }
                            }
                        });
                    }
                } catch (final Exception e) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (!cancelable.isCancel) {
                                requestCallBack.onError(e, false);
                            }
                        }
                    });
                }
            }
        }.start();
        return cancelable;
    }


    class HttpURLConnectionCancelable implements Callback.Cancelable {
        HttpURLConnection connection;
        boolean isCancel;
        Callback.CommonCallback<String> callback;

        @Override
        public void cancel() {
            isCancel = true;
            if (connection != null) {
                new Thread() {
                    public void run() {
                        connection.disconnect();
                        if (callback != null) {
                            callback.onCancelled(new CancelledException("disconnect"));
                        }
                    }
                }.start();
            } else {
                if (callback != null) {
                    callback.onCancelled(new CancelledException("connection == null"));
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return isCancel;
        }
    }

    // 1 time_local 请求时间 25/Jun/2015:16:29:10 +0800
    // 2 remote_addr ip 202.108.48.140
    // 3 type 直播类型 1 1代表直播小组；2代表直播讲座；（getinfo中的liveType）
    // 4 groupid 直播组ID 1020 一场直播的组id;(getinfo中的id)
    // 5 uname 用户名 test520@qq.com 老师为getInfo中的teacherName，学生为getInfo中的uname
    // 6 uid 用户的uid 1.40759E+15 聊天返回的uid
    // 7 stuid 学生Id 5000102 老师填写--，学生填写getInfo中的stuId
    // 8 tpid pid 171 getInfo中的teacherId
    // 9 filename 文件名 PlayMedia 日志发生的文件名
    // 10 message 消息 DimensionChange videoWidth_=800, videoHeight_=600 日志详情条目
    // 11 bz 标识 student 日志类型（student 、teacher）
    // 播放器异常日志

    /**
     * filename暂时用做版本号
     */
    public void liveOnloadLogs(String url, String type, String groupid, String uname, String uid, String stuid,
                               String tpid, String filename, String str, String bz, Callback.CommonCallback requestCallBack) {
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("type", type);
        params.addBodyParameter("groupid", groupid);
        params.addBodyParameter("uname", uname);
        params.addBodyParameter("uid", ("[" + uid + "]").replaceAll("#", "/az/"));
        params.addBodyParameter("stuid", stuid);
        params.addBodyParameter("tpid", tpid);
        params.addBodyParameter("filename", filename);
        params.addBodyParameter("str", ("[" + str + "]").replaceAll("#", "/az/"));
        params.addBodyParameter("bz", bz);
//        sendGetNoBusiness(url, params, callback);
        x.http().get(params, requestCallBack);
    }

    public void liveOnloadLogs2(String url, String type, String groupid, String uname, String uid, String stuid,
                                String tpid, String filename, String str, String bz, okhttp3.Callback callback) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("type", type);
        params.addBodyParam("groupid", groupid);
        params.addBodyParam("uname", uname);
        params.addBodyParam("uid", ("[" + uid + "]").replaceAll("#", "/az/"));
        params.addBodyParam("stuid", stuid);
        params.addBodyParam("tpid", tpid);
        params.addBodyParam("filename", filename);
        params.addBodyParam("str", ("[" + str + "]").replaceAll("#", "/az/"));
        params.addBodyParam("bz", bz);
        sendGetNoBusiness(url, params, callback);
    }

    /**
     * 用户在线心跳
     *
     * @param type            直播类型
     * @param enstuId         用户加密ID
     * @param liveId          直播ID
     * @param currentDutyId   当前责任ID
     * @param hbTime          观看时长
     * @param requestCallBack
     */
    public void liveUserOnline(int type, String enstuId, String liveId, String teacherId, String currentDutyId, int
            hbTime, HttpCallBack requestCallBack) {
        String url;
        HttpRequestParams params = new HttpRequestParams();
        if (type == LiveBll.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_USER_ONLINE;
            params.addBodyParam("liveId", liveId);
            params.addBodyParam("teacherId", teacherId);
            setDefaultParameter(params);
        } else if (type == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveVideoConfig.URL_LIVE_TUTORIAL_USER_ONLINE;
            params.addBodyParam("classId", liveId);
            params.addBodyParam("dutyId", currentDutyId);
        } else if (type == LiveBll.LIVE_TYPE_LECTURE) {// 公开直播
            url = LiveVideoConfig.URL_LIVE_LECTURE_USER_ONLINE;
            params.addBodyParam("liveId", liveId);
        } else {
            return;
        }
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("hbTime", "" + hbTime);
        params.addBodyParam("fromType", "4");
        sendPost(url, params, requestCallBack);
    }

    /**
     * 领取金币
     *
     * @param type            视频类型
     * @param enstuId         用户加密ID
     * @param operateId       金币ID
     * @param liveid          直播ID
     * @param requestCallBack
     */
    public void sendReceiveGold(int type, String enstuId, int operateId, String liveid,
                                HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveBll.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_RECEIVE_GOLD;
            requestCallBack.url = url;
            params.addBodyParam("liveId", liveid);
            setDefaultParameter(params);
        } else if (type == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveVideoConfig.URL_LIVE_TUTORIAL_GOLD;
            requestCallBack.url = LiveVideoConfig.URL_LIVE_TUTORIAL_GOLD;
            params.addBodyParam("classId", liveid);
        } else if (type == LiveBll.LIVE_TYPE_LECTURE) {// 公开直播
            url = LiveVideoConfig.URL_LIVE_LECTURE_GOLD;
            requestCallBack.url = LiveVideoConfig.URL_LIVE_LECTURE_GOLD;
            params.addBodyParam("liveId", liveid);
        } else {
            return;
        }
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("operateId", "" + operateId);
        sendPost(url, params, requestCallBack);
    }

    public void getReceiveGoldTeamStatus(int operateId,
                                         HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_RED_GOLD_TEAM_STATUS;
        requestCallBack.url = url;
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("operateId", "" + operateId);
        sendPost(url, params, requestCallBack);
    }

    public void getReceiveGoldTeamRank(int operateId,
                                       HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_RED_GOLD_TEAM_RANK;
        requestCallBack.url = url;
        setDefaultParameter(params);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("operateId", "" + operateId);
        sendPost(url, params, requestCallBack);
    }

    /**
     * 提交测试题
     *
     * @param type            视频类型
     * @param enstuId         用户加密ID
     * @param testId          测试题ID
     * @param liveId          直播ID
     * @param testAnswer      测试题答案
     * @param isRight
     * @param requestCallBack
     */
    public void liveSubmitTestAnswer(int type, String enstuId, String srcType, String testId, String liveId, String
            testAnswer, String userMode, boolean isVoice,
                                     boolean isRight, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveBll.LIVE_TYPE_LIVE) {// 直播
            if (isVoice) {
                url = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER_VOICE;
            } else {
                url = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER;
            }
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveVideoConfig.URL_LIVE_TUTORIAL_SUBMIT_TEST_ANSWER;
            params.addBodyParam("classId", liveId);
        } else if (type == LiveBll.LIVE_TYPE_LECTURE) {// 公开
            url = LiveVideoConfig.URL_LIVE_LECTURE_SUBMIT_TEST_ANSWER;
            params.addBodyParam("liveId", liveId);
        } else {
            return;
        }
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("testId", testId);
        if (isVoice) {
            params.addBodyParam("answer", testAnswer);
        } else {
            params.addBodyParam("testAnswer", testAnswer);
        }
        params.addBodyParam("userMode", userMode);
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("useVoice", isVoice ? "1" : "0");
        params.addBodyParam("isRight", isRight ? "1" : "0");
        sendPost(url, params, requestCallBack);
    }

    public void getTestAnswerTeamStatus(String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_LIVE_ANSWER_TEAM;
//        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        params.addBodyParam("testId", testId);
        sendPost(url, params, requestCallBack);
    }

    public void getSpeechEvalAnswerTeamStatus(String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_LIVE_SPEECH_TEAM;
//        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        params.addBodyParam("testId", testId);
        sendPost(url, params, requestCallBack);
    }

    public void liveSubmitTestH5Answer(String enstuId, String srcType, String testId, String liveId, String
            testAnswer, String type, String userMode, String isSubmit,
                                       double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_H5_ANSWER;
//        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testAnswer", testAnswer);
        params.addBodyParam("type", type);
        params.addBodyParam("isPlayBack", "0");
        params.addBodyParam("testNum", "1");
        params.addBodyParam("isSubmit", isSubmit);
        params.addBodyParam("userMode", userMode);
        if (!StringUtils.isSpace(srcType)) {
            params.addBodyParam("srcType", srcType);
        }
        params.addBodyParam("useVoice", "1");
        params.addBodyParam("voiceTime", "" + voiceTime);
        params.addBodyParam("isRight", isRight ? "1" : "0");
        sendPost(url, params, requestCallBack);
    }

    public void getAllRanking(String enstuId, String liveId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_TEAM_RANK, params, requestCallBack);
    }

    public void getStuRanking(String enstuId, String liveId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_RANK, params, requestCallBack);
    }

    /**
     * 获取学习报告
     *
     * @param enstuId         用户加密ID
     * @param liveId          直播ID
     * @param livetype
     * @param requestCallBack
     */
    public void getLearnReport(String enstuId, String liveId, int livetype, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
//        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        String url;
        if (livetype == LiveBll.LIVE_TYPE_LIVE) {
            url = liveVideoSAConfigInner.URL_LIVE_GET_LEARNING_STAT;
        } else {
            url = LiveVideoConfig.URL_LIVE_GET_FEED_BACK;
        }
        sendPost(url, params, requestCallBack);
    }

    /**
     * 提交教师评价
     *
     * @param enstuId
     * @param liveId
     * @param score           RatingBar的星数。
     * @param requestCallBack
     */
    public void sendTeacherEvaluate(String enstuId, String liveId, String classId, int[] score,
                                    HttpCallBack
                                            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("knowledgeScored", "" + score[0]);
        params.addBodyParam("interactionScored", "" + score[1]);
        params.addBodyParam("mentalityScored", "" + score[2]);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SUBMIT_STU_EVALUATE, params, requestCallBack);
    }

    public void userSign(String enstuId, String liveId, String classId, String teacherId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teacherId", teacherId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_USER_SIGN, params, requestCallBack);
    }

    public void praiseTeacher(int type, String enstuId, String liveId, String teacherId, String ftype, String educationStage, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveBll.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_PRAISE_TEACHER;
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveVideoConfig.URL_LIVE_TUTORIAL_PRAISE_TEACHER;
            params.addBodyParam("classId", liveId);
        } else if (type == LiveBll.LIVE_TYPE_LECTURE) {// 公开
            url = LiveVideoConfig.URL_LIVE_LECTURE_PRAISE_TEACHER;
            params.addBodyParam("liveId", liveId);
        } else {
            return;
        }
        params.addBodyParam("teacherId", teacherId);
        if (!StringUtils.isEmpty(educationStage)) {
            params.addBodyParam("educationStage", educationStage);
        }
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("type", ftype);
        sendPost(url, params, requestCallBack);
    }

    public void getSpeechEval(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        setDefaultParameter(params);
        sendPost(LiveVideoConfig.URL_LIVE_GET_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult(String enstuId, String liveId, String id, String stuAnswer, String times, int
            entranceTime, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("stuAnswer", "" + stuAnswer);
        params.addBodyParam("times", "" + times);
        params.addBodyParam("entranceTime", "" + entranceTime);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        Loger.i(TAG, "sendSpeechEvalResult:enstuId=" + enstuId + ",liveId=" + liveId);
        sendPost(LiveVideoConfig.URL_LIVE_SEND_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult2(String enstuId, String liveId, String id, String stuAnswer, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "");
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        Loger.i(TAG, "sendSpeechEvalResult2:enstuId=" + enstuId + ",liveId=" + liveId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42, params, requestCallBack);
    }

    /** 语音评测排行榜 */
    public void getSpeechEvalAnswerTeamRank(String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("testId", id);
        setDefaultParameter(params);
        Loger.i(TAG, "getSpeechEvalAnswerTeamRank:id=" + id);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SPEECH_TEAM_RAND, params, requestCallBack);
    }

    public void speechEval42IsAnswered(String enstuId, String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        Loger.i(TAG, "speechEval42IsAnswered:enstuId=" + enstuId + ",liveId=" + liveId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }

    public void getRoomid(String url, Callback.CacheCallback<String> cacheCallback) {
//        HttpRequestParams params = new HttpRequestParams(url);
//        x.http().get(params, cacheCallback);
    }

    public void getToken(String roomId, String username, Callback.CacheCallback<String> cacheCallback) {
//        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("roomId", roomId);
////        params.addBodyParameter("roomId", "5919852a17a5017013e7633d");
//        params.addBodyParam("username", username);
//        params.addBodyParam("role", "presenter");
//        sendPost("https://test-rtc.xesimg.com:12443/token", params, cacheCallback);
    }

    /**
     * 用户试听
     *
     * @param enstuId
     * @param liveId          直播id
     * @param requestCallBack
     */
    public void userModeTime(String enstuId, String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_USER_MODETIME;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void getStudentLiveInfo(String enstuId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_STUDY_INFO;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void setStuStarCount(int type, String enstuId, String liveId, String starId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveBll.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_SETSTAR;
            requestCallBack.url = url;
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            return;
        } else if (type == LiveBll.LIVE_TYPE_LECTURE) {// 公开
            url = LiveVideoConfig.URL_LIVE_LEC_SETSTAR;
            requestCallBack.url = url;
            params.addBodyParam("liveId", liveId);
        } else {
            return;
        }
        params.addBodyParam("starId", starId);
//        params.addBodyParam("enstuId", enstuId);
        sendPost(url, params, requestCallBack);
    }

    public void getStuGoldCount(String enstuId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_STUDY_GOLD_COUNT;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void setTotalOpeningLength(String enstuId, String courseId, String liveId, String classId, String duration, String speakingNum, String speakingLen, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("courseId", liveId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("duration", duration);
        params.addBodyParam("speakingNum", speakingNum);
        params.addBodyParam("speakingLen", speakingLen);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_TOTAL_OPEN;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void setNotOpeningNum(String enstuId, String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_NOT_OPEN;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void getQuestion(String enstuId, String liveId, String questionId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("id", questionId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_QUESTION;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获得预加载课件地址
     */
    public void getCourseWareUrl(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_WARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获取一次多发的预加载课件地址
     */
    public void getMoreCoureWareUrl(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_MORE_WARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获得广告信息
     */
    public void getAdOnLL(String courseId, final HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("courseId", courseId);
        requestCallBack.url = LiveVideoConfig.URL_LIVE_GET_LEC_AD;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 互动题获取满分榜
     *
     * @param testId
     * @param classId
     * @param teamId
     * @param callBack
     */
    public void getFullMarkListQuestion(String testId, String classId, String teamId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("testId", testId);
        String i = JSON.toJSON(params).toString();
        sendPost(liveVideoSAConfigInner.LIVE_FULL_MARK_LIST_QUESTION, params, callBack);
    }

    /**
     * 测试卷获取满分榜
     *
     * @param classId
     * @param teamId
     * @param callBack
     */
    public void getFullMarkListTest(String classId, String teamId, String testPlan, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("testPlan", testPlan);
        sendPost(liveVideoSAConfigInner.LIVE_FULL_MARK_LIST_TEST, params, callBack);
    }

    /**
     * 课件获取满分榜
     *
     * @param classId
     * @param teamId
     * @param testId
     * @param type
     * @param callBack
     */
    public void getFullMarkListH5(String classId, String teamId, String testId, String type, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("type", type);
        String i = JSON.toJSON(params).toString();
        sendPost(liveVideoSAConfigInner.LIVE_FULL_MARK_LIST_H5, params, callBack);
    }

    /**
     * 获取光荣榜
     *
     * @param classId         班级Id
     * @param enstuId         学生Id加密串
     * @param liveId          场次Id
     * @param status          是否点赞标志位
     * @param requestCallBack
     */
    public void getHonorList(String classId, String enstuId, String liveId, String status, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("status", status);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_HONOR_LIST, params, requestCallBack);
    }

    /**
     * 获取点赞榜
     *
     * @param classId         班级Id
     * @param stuId           学生Id加密串
     * @param requestCallBack
     */
    public void getThumbsUpList(String classId, String stuId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_THUMBS_UP_LIST, params, requestCallBack);
    }

    /**
     * 获取进步榜
     *
     * @param classId         班级Id
     * @param enstuId         学生Id加密串
     * @param liveId          场次Id
     * @param status          是否点赞标志位
     * @param requestCallBack
     */
    public void getProgressList(String classId, String enstuId, String liveId, String status, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("status", status);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_PRPGRESS_LIST, params, requestCallBack);
    }

    /**
     * 获取点赞概率标识
     *
     * @param classId         班级Id
     * @param enstuId         学生Id加密串
     * @param requestCallBack
     */
    public void getThumbsUpProbability(String classId, String enstuId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_THUMBS_UP_PROBABILITY, params, requestCallBack);
    }

    /**
     * 保存标记点
     *
     * @param time
     * @param url
     * @param callBack
     */
    public void saveLiveMark(String time, String url, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("curTime", time);
        params.addBodyParam("imageUrl", url);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SAVE_MARK_POINT, params, callBack);
    }

    /**
     * 获取标记点
     *
     * @param callBack
     */
    public void getMarkPoints(String liveId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", UserBll.getInstance().getMyUserInfoEntity().getStuId());
        params.addBodyParam("liveId", liveId);
//        params.addBodyParam("stuId","15657");
//        params.addBodyParam("liveId","107070");
        sendPost(LiveVideoConfig.URL_LIVE_GET_MARK_POINTS, params, callBack);
    }

    /**
     * 删除标记点
     *
     * @param callBack
     */
    public void deleteMarkPoints(String livdId, long time, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", UserBll.getInstance().getMyUserInfoEntity().getStuId());
        setDefaultParameter(params);
//        params.addBodyParam("stuId","15657");
//        params.addBodyParam("liveId","107070");
        params.addBodyParam("curTime", "" + time);
        params.addBodyParam("liveId", livdId);
        sendPost(LiveVideoConfig.URL_LIVE_DELETE_MARK_POINTS, params, callBack);
    }

    /**
     * 获取智能私信
     *
     * @param classId
     * @param testId
     * @param srcType
     * @param type
     * @param isForce
     * @param callBack
     */
    public void getAutoNotice(String classId, String testId, String srcType, int type, int isForce, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("srcType", srcType);
        params.addBodyParam("type", "" + type);
        params.addBodyParam("isForce", "" + isForce);
        params.addBodyParam("stuId", UserBll.getInstance().getMyUserInfoEntity().getStuId());
        setDefaultParameter(params);
        Loger.i(LiveAutoNoticeBll.class.getSimpleName(), JSON.toJSON(params).toString());
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_AUTO_NOTICE, params, callBack);
    }

    /**
     * 智能私信统计接口
     */
    public void autoNoticeStatisc(String classId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("stuId", UserBll.getInstance().getMyUserInfoEntity().getStuId());
        params.addBodyParam("type", "11");
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_STATISTICS_AUTO_NOTICE, params, callBack);
    }

    /* 上传体验课播放器播放时长的接口 */
    public void uploadExperiencePlayingTime(String liveId, String termId, Long hbTime, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("termId", termId);
        params.addBodyParam("hbTime", hbTime.toString());
        params.addBodyParam("sessid", UserBll.getInstance().getMyUserInfoEntity().getSessionId());
        sendPost(LiveVideoConfig.URL_EXPERIENCE_LIVE_ONLINETIME, params, callBack);
    }
    /**战队pk 相关*/

    /**
     * 获取分队信息
     *
     * @param classId
     * @param teamId
     * @param requestCallBack
     */
    public void getTeamInfo(String id, String classId, String teamId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_PKTEAMINFO, params, requestCallBack);
    }

    /** roleplay组内排行榜 */
    public void getRolePlayAnswerTeamRank(String testId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("testId", testId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_ROLE_TEAM, params, callBack);
    }

    /** 直播讲座获取更多课程的信息 */
    public void getMoreChoiceCount(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        sendPost(LiveVideoConfig.URL_LECTURELIVE_MORE_COURSE, params, requestCallBack);
    }

    public void getCurTime(HttpCallBack callBack) {
        sendGetNoBusiness(LiveVideoConfig.URL_LIVE_GET_CURTIME, new HttpRequestParams(), callBack);
    }

    /** 存储学生语音反馈音源 */
    public void saveStuTalkSource(String stuId, String talkSourcePath, String service, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("talkSourcePath", talkSourcePath);
        params.addBodyParam("service", service);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SAVESTU_TALK, params, callBack);
    }

    /**
     * 获取pk 对手信息
     *
     * @param classId
     * @param teamId
     */
    public void getPkAdversary(String classId, String teamId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_MATCHTEAM, params, requestCallBack);
    }

    /**
     * 学生获取自己宝箱
     *
     * @param isWin
     * @param classId
     * @param teamId
     * @param stuId
     */
    public void getStuChest(int isWin, String classId, String teamId, String stuId, String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("isWin", isWin + "");
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_GETSTUCHESTURL + "/" + liveId, params, requestCallBack);

    }

    public void getEnergyNumAndContributionStar(String liveId, String teamId,
                                                String classId, String stuId,
                                                String testId, String testPlan,
                                                HttpCallBack requestCallBack) {


        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_GETSTUCHESTURL + "/" + liveId, params, requestCallBack);


    }

    /**
     * 获取战队开宝箱结果
     *
     * @param liveId
     * @param stuId
     * @param teamId
     * @param classId
     */
    public void getClassChestResult(String liveId, String stuId, String teamId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_GETCLASSCHESTRESULT + "/" + liveId, params, requestCallBack);
    }


    /**
     * 投票 能量
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param requestCallBack
     */
    public void addPersonAndTeamEnergy(String liveId, int addEnergy, String teamId, String classId, String stuId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("addEnergy", addEnergy + "");
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_ADDPERSONANDTEAMENERGY + "/" + liveId, params, requestCallBack);
    }


    /**
     * 请求 学生当前场次 的总能量值 和自己金币 及对手总能量值
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param requestCallBack
     */
    public void liveStuGoldAndTotalEnergy(String liveId, String teamId, String classId, String stuId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_LIVESTUGOLDANDTOTALENERGY + "/" + liveId, params, requestCallBack);
    }

    /**
     * 每题战队能量 和贡献之星
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param testId
     * @param testPlan        互动课件或者互动题时 testPlan= ''; 测试卷请求时testId= ' '
     * @param requestCallBack
     */
    public void teamEnergyNumAndContributionStar(String liveId, String teamId, String classId, String stuId, String testId,
                                                 String testPlan, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testPlan", testPlan);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR + "/" + liveId, params, requestCallBack);
    }


    /**
     * 每题pk 结果
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param requestCallBack
     */
    public void stuPKResult(String liveId, String teamId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_STUPKRESULT + "/" + liveId, params, requestCallBack);
    }

}
