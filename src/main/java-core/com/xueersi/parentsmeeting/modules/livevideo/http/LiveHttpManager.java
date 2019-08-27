package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.android.arouter.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.CommonRequestCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.LivePluginHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LivePluginRequestParam;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveIntegratedCfg;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoChConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoHttpEnConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLiveEnterParam;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.DNSUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.video.URLDNS;

import org.xutils.xutils.common.Callback;
import org.xutils.xutils.common.Callback.CancelledException;
import org.xutils.xutils.common.util.IOUtil;
import org.xutils.xutils.ex.HttpException;
import org.xutils.xutils.http.RequestParams;
import org.xutils.xutils.x;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 直播网络访问类
 */
public class LiveHttpManager extends BaseHttpBusiness implements LiveHttpAction {
    String TAG = "LiveHttpManager";
    private final Logger logger = LoggerFactory.getLogger(TAG);
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

    @Override
    public LiveVideoSAConfig.Inner getLiveVideoSAConfigInner() {
        return liveVideoSAConfigInner;
    }

    public void addBodyParam(String key, String value) {
        defaultKey.put(key, value);
    }

    public void setDefaultParameter(HttpRequestParams httpRequestParams) {
        for (String key : defaultKey.keySet()) {
            Map<String, String> bodyParams = httpRequestParams.getBodyParams();
            //不顶掉已经有的参数，比如战队pk teamId
            if (bodyParams != null && bodyParams.containsKey(key)) {
                logger.d("setDefaultParameter:key=" + key);
                continue;
            }
            String value = defaultKey.get(key);
            httpRequestParams.addBodyParam(key, value);
        }
    }

    @Override
    public void sendPostDefault(String url, final HttpRequestParams httpRequestParams, HttpCallBack httpCallBack) {
        setDefaultParameter(httpRequestParams);
        sendPost(url, httpRequestParams, httpCallBack);
    }

    public void sendJsonPostDefault(String url, final HttpRequestParams httpRequestParams, HttpCallBack httpCallBack) {
        setDefaultParameter(httpRequestParams);
        sendJsonPost(url, httpRequestParams, httpCallBack);
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    @Override
    public void sendPost(final String url, final HttpRequestParams httpRequestParams, HttpCallBack httpCallBack) {
        long before = System.currentTimeMillis();
        super.sendPost(url, httpRequestParams, httpCallBack);
        logger.d("sendPost:time=" + (System.currentTimeMillis() - before) + ",url=" + url);
    }

    /**
     * 播放器数据初始化
     *
     * @param courseId        课程ID
     * @param liveId          直播ID
     * @param isAudit         是不是旁听 isAudit
     * @param requestCallBack
     */
    public void liveGetInfo(String courseId, String liveId, int isAudit, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("isAudit", "" + isAudit);
        setDefaultParameter(params);
        sendPost(LiveVideoConfig.URL_LIVE_GET_INFO, params, requestCallBack);
    }

    /**
     * 直播辅导播放器数据初始化
     *
     * @param liveId          直播ID
     * @param requestCallBack
     */
    public void liveTutorialGetInfo(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", liveId);
        sendPost(LiveVideoConfig.URL_LIVE_TUTORIAL_GET_INFO, params, requestCallBack);
    }

    /**
     * 公开直播播放器数据初始化
     *
     * @param liveId          直播ID
     * @param requestCallBack
     */
    public void liveLectureGetInfo(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        requestCallBack.url = LiveVideoConfig.URL_LIVE_LECTURE_GET_INFO;
        params.addBodyParam("liveId", liveId);
        sendPost(LiveVideoConfig.URL_LIVE_LECTURE_GET_INFO, params, requestCallBack);
    }


    /**
     * 大班整合直播间-直播信息接口
     * @param planId  场次id
     * @param bizId 直播类型：1 直播,2:讲座
     * @param stuCould 学生课程id
     */
    public void bigLiveEnter(int planId, int bizId, int stuCould, HttpCallBack requestCallBack){

        BigLiveEnterParam param = new BigLiveEnterParam();
        param.setBizId(bizId);
        param.setPlanId(planId);
        param.setStuCouId(stuCould);
        sendJsonPost(LiveIntegratedCfg.LIVE_ENTER,param,requestCallBack);
    }


    int getTimes = 1;

    public Callback.Cancelable liveGetPlayServer(final URLDNS urldns, final String url2, final
    CommonRequestCallBack<String>
            requestCallBack) {
        final HttpURLConnectionCancelable cancelable = new HttpURLConnectionCancelable();
        LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
        liveThreadPoolExecutor.execute(new Runnable() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                try {
                    DNSUtil.getDns(urldns, url2);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(url2);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    cancelable.connection = connection;
                    cancelable.callback = requestCallBack;
//                    connection.setRequestProperty("Connection", "Keep-Alive");
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
                    logger.d("liveGetPlayServer:disconnect=" + (connection != null));
                    try {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (Exception e2) {

                    }
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
        });
        return cancelable;
    }

    public Callback.Cancelable liveGetPlayServer2(final URLDNS urldns, final String url2, final
    CommonRequestCallBack<String>
            requestCallBack) {
        final HttpURLConnectionCancelable cancelable = new HttpURLConnectionCancelable();
        HttpRequestParams params = new HttpRequestParams();
        params.addHeaderParam("Connection", "Keep-Alive");
        baseSendPostNoBusiness(url2, params, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (cancelable.isCancelled()) {
                    return;
                }
                requestCallBack.onError(e, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (cancelable.isCancelled()) {
                    return;
                }
                requestCallBack.onSuccess(response.body().string());
            }
        });
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
                LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        connection.disconnect();
                        if (callback != null) {
                            callback.onCancelled(new CancelledException("disconnect"));
                        }
                    }
                });
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
    @Deprecated
    public RequestParams liveOnloadLogs(String url, String type, String groupid, String uname, String uid, String stuid,
                                        String tpid, String filename, String str, String bz, Callback.CommonCallback
                                                requestCallBack) {
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
        return params;
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
     * @param liveId          直播ID
     * @param currentDutyId   当前责任ID
     * @param hbTime          观看时长
     * @param requestCallBack
     */
    public void liveUserOnline(int type, String liveId, String teacherId, String currentDutyId, int
            hbTime, HttpCallBack requestCallBack) {
        String url;
        HttpRequestParams params = new HttpRequestParams();
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_USER_ONLINE;
            params.addBodyParam("liveId", liveId);
            params.addBodyParam("teacherId", teacherId);
            setDefaultParameter(params);
        } else if (type == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveHttpConfig.URL_LIVE_TUTORIAL_USER_ONLINE;
            params.addBodyParam("classId", liveId);
            params.addBodyParam("dutyId", currentDutyId);
        } else if (type == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 公开直播
            url = LiveHttpConfig.URL_LIVE_LECTURE_USER_ONLINE;
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
     * @param operateId       金币ID
     * @param liveid          直播ID
     * @param requestCallBack
     */
    public void sendReceiveGold(int type, int operateId, String liveid,
                                HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_RECEIVE_GOLD;
            requestCallBack.url = url;
            params.addBodyParam("liveId", liveid);
            setDefaultParameter(params);
        } else if (type == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveHttpConfig.URL_LIVE_TUTORIAL_GOLD;
            requestCallBack.url = LiveHttpConfig.URL_LIVE_TUTORIAL_GOLD;
            params.addBodyParam("classId", liveid);
        } else if (type == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 公开直播
            url = LiveHttpConfig.URL_LIVE_LECTURE_GOLD;
            requestCallBack.url = LiveHttpConfig.URL_LIVE_LECTURE_GOLD;
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
     * 中学数学，连对激励系统
     * 获取连对榜单
     *
     * @param url
     * @param classId      班级id
     * @param planId       场次id
     * @param teamId       小组ID
     * @param httpCallBack
     */
    public void getEvenLikeData(String url, String classId, String planId, String teamId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("planId", planId);
        params.addBodyParam("teamId", teamId);
        sendPost(url, params, httpCallBack);
    }

    /**
     * 中学激励系统接口，用来更新聊天界面的最高连对和当前连对
     *
     * @param classId
     * @param liveId
     * @param teamId
     * @param stuId
     * @param httpCallBack
     */
    public void getEvenPairInfo(String classId, String liveId, String teamId, String stuId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        sendPost(LiveHttpConfig.EVEN_DRIVE_PAIR_INFO, params, httpCallBack);
    }

    /**
     * 提交测试题
     *
     * @param type            视频类型
     * @param testId          测试题ID
     * @param liveId          直播ID
     * @param testAnswer      测试题答案
     * @param isRight
     * @param requestCallBack
     */
    public void liveSubmitTestAnswer(int type, String srcType, String testId, String liveId, String
            testAnswer, String userMode, boolean isVoice,
                                     boolean isRight, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            if (isVoice) {
                url = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER_VOICE;
            } else {
                url = liveVideoSAConfigInner.URL_LIVE_SUBMIT_TEST_ANSWER;
            }
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveHttpConfig.URL_LIVE_TUTORIAL_SUBMIT_TEST_ANSWER;
            params.addBodyParam("classId", liveId);
        } else if (type == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 公开
            url = LiveHttpConfig.URL_LIVE_LECTURE_SUBMIT_TEST_ANSWER;
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

    /**
     * 提交文科新课件平台测试题
     *
     * @param testId          测试题ID
     * @param liveId          直播ID
     * @param testAnswer      测试题答案
     * @param requestCallBack
     */
    public void liveNewArtsSubmitTestAnswer(String testId, String liveId, String
            testAnswer, String isForce, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTSTEST_ANSWER;
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("answer", testAnswer);
        params.addBodyParam("isPlayBack", "1");
        params.addBodyParam("isForce", isForce);
        setDefaultParameter(params);
        sendPost(url, params, requestCallBack);
        Loger.e("Duncan", "======> liveNewArtsSubmitTestAnswer:" + url);
    }


    public void getTestAnswerTeamStatus(boolean isNewArt, String testId, HttpCallBack requestCallBack) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            String url = LiveHttpConfig.URL_LIVE_NEWSTAND_ANSWER;
            setDefaultParameter(params);
            params.addBodyParam("testId", testId);
            sendPost(url, params, requestCallBack);
        } else {
            HttpRequestParams params = new HttpRequestParams();
            String url = liveVideoSAConfigInner.URL_LIVE_ANSWER_TEAM;
            setDefaultParameter(params);
            params.addBodyParam("testId", testId);
            sendPost(url, params, requestCallBack);
        }

    }

    public void getSpeechEvalAnswerTeamStatus(boolean isNewArt, String testId, HttpCallBack requestCallBack) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            String url = LiveHttpConfig.URL_LIVE_SPEECH_TEAM_STATUS;
            setDefaultParameter(params);
            params.addBodyParam("testId", testId);
            sendPost(url, params, requestCallBack);
        } else {
            HttpRequestParams params = new HttpRequestParams();
            String url = liveVideoSAConfigInner.URL_LIVE_SPEECH_TEAM;
//        params.addBodyParam("enstuId", enstuId);
            setDefaultParameter(params);
            params.addBodyParam("testId", testId);
            sendPost(url, params, requestCallBack);
        }

    }

    public void liveSubmitTestH5Answer(String srcType, String testId, String liveId, String
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

    // 普通语音答题的提交
    public void liveSubmitNewArtsH5Answer(String srcType, String testId, String liveId, String
            testAnswer, String type, String userMode, String isSubmit,
                                          double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTS_ANSWER;
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("answers", testAnswer);
        params.addBodyParam("isPlayBack", "1");
        params.addBodyParam("isForce", isSubmit);
        params.addBodyParam("Cookie", LiveAppUserInfo.getInstance().getUserToken());
        sendPost(url, params, requestCallBack);
    }

    // H5语音答题的提交
    public void liveSubmitNewArtsRealH5Answer(String srcType, String testId, String liveId, String
            testAnswer, String type, String userMode, String isSubmit,
                                              double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        String types = srcType;
        HttpRequestParams params = new HttpRequestParams();
        String url = LiveHttpConfig.URL_LIVE_SUBMIT_NEWARTSH5_ANSWER;
        setDefaultParameter(params);
        params.addBodyParam("testId", testId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("type", types);
        params.addBodyParam("isRight", isRight ? "1" : "0");
        params.addBodyParam("isPlayBack", "1");
        params.addBodyParam("isSubmit", isSubmit);
        params.addBodyParam("voiceUrl", "");
        params.addBodyParam("voiceTime", "" + voiceTime);
        params.addBodyParam("url", "");
        params.addBodyParam("imageUrl", "");
        params.addBodyParam("userAnswer", LiveVideoConfig.userAnswer);
        params.addBodyParam("answer", LiveVideoConfig.answer);
        sendPost(url, params, requestCallBack);
    }

    /**
     * 提交语文AI主观题答案
     *
     * @param data
     * @param callBack
     */
    public void submitChineseAISubjectiveAnswer(String url, String data, final HttpCallBack callBack) {

//        url= "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/submitChineseAISubjectiveAnswer";
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("testInfos", data);
        sendPost(url, httpRequestParams, callBack);
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
     * @param liveId          直播ID
     * @param livetype
     * @param requestCallBack
     */
    public void getLearnReport(String liveId, int livetype, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
//        params.addBodyParam("enstuId", enstuId);
        setDefaultParameter(params);
        String url;
        if (livetype == LiveVideoConfig.LIVE_TYPE_LIVE) {
            url = liveVideoSAConfigInner.URL_LIVE_GET_LEARNING_STAT;
        } else {
            url = LiveHttpConfig.URL_LIVE_GET_FEED_BACK;
        }
        sendPost(url, params, requestCallBack);
    }

    /**
     * 提交教师评价
     *
     * @param liveId
     * @param score           RatingBar的星数。
     * @param requestCallBack
     */
    public void sendTeacherEvaluate(String liveId, String classId, int[] score,
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

    public void userSign(String liveId, String classId, String teacherId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teacherId", teacherId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_USER_SIGN, params, requestCallBack);
    }

    public void praiseTeacher(int type, String liveId, String teacherId, String ftype, String
            educationStage, HttpCallBack
                                      requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_PRAISE_TEACHER;
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            url = LiveHttpConfig.URL_LIVE_TUTORIAL_PRAISE_TEACHER;
            params.addBodyParam("classId", liveId);
        } else if (type == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 公开
            url = LiveHttpConfig.URL_LIVE_LECTURE_PRAISE_TEACHER;
            params.addBodyParam("liveId", liveId);
        } else {
            return;
        }
        params.addBodyParam("teacherId", teacherId);
        if (!StringUtils.isEmpty(educationStage)) {
            params.addBodyParam("educationStage", educationStage);
        }
        params.addBodyParam("type", ftype);
        sendPost(url, params, requestCallBack);
    }

    public void getSpeechEval(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_LIVE_GET_SPEECHEVAL, params, requestCallBack);
    }

    public void sendSpeechEvalResult2(String liveId, String id, String stuAnswer, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "");
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        logger.i("sendSpeechEvalResult2:liveId=" + liveId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42, params, requestCallBack);
    }

    public void sendSpeechEvalResultNewArts(String liveId, String id, String stuAnswer, String isSubmit, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "");
        params.addBodyParam("isSubmit", "" + isSubmit);
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        Loger.i(TAG, "sendSpeechEvalResult2:liveId=" + liveId);
        sendPost(LiveHttpConfig.URL_LIVE_SEND_SPEECHEVALUATEARTS, params, requestCallBack);
    }

    /** 语音评测排行榜  兼容全身直播新课件平台改版的Top3 */
    public void getSpeechEvalAnswerTeamRank(boolean isNewArt, String id, HttpCallBack requestCallBack) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            params.addBodyParam("testId", id);
            setDefaultParameter(params);
            logger.i("getSpeechEvalAnswerTeamRank:id=" + id);
            sendPost(LiveHttpConfig.URL_LIVE_ROLE_SPEECH_TEAM_TOP3, params, requestCallBack);
        } else {
            HttpRequestParams params = new HttpRequestParams();
            params.addBodyParam("testId", id);
            setDefaultParameter(params);
            logger.i("getSpeechEvalAnswerTeamRank:id=" + id);
            sendPost(liveVideoSAConfigInner.URL_LIVE_SPEECH_TEAM_RAND, params, requestCallBack);
        }

    }

    public void speechEval42IsAnswered(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        setDefaultParameter(params);
        logger.i("speechEval42IsAnswered:liveId=" + liveId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }

    /** 文科新课件平台语音评测是否已答过 */
    public void speechNewArtEvaluateIsAnswered(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        setDefaultParameter(params);
        Loger.i(TAG, "speechNewArtEvaluateIsAnswered:liveId=" + liveId);
        sendPost(LiveHttpConfig.URL_LIVE_SEND_SPEECHEVALUATENEWARTS_ANSWER, params, requestCallBack);
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

    public void getStudentLiveInfo(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_STUDY_INFO;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 文理半身直播 旁听数据接口
     *
     * @param liveId   直播id
     * @param stuCouId 学生id
     * @param isArts   是否是文科
     */
    public void getHalfBodyStuLiveInfo(String liveId, String stuCouId, boolean isArts, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        setDefaultParameter(params);
        String url = isArts ? LiveHttpConfig.URL_HALFBODY_LIVE_STULIVEINFO_ARTS
                : LiveHttpConfig.URL_HALFBODY_LIVE_STULIVEINFO;

        sendPost(url, params, requestCallBack);
    }


    public void setStuStarCount(int type, String liveId, String starId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        String url;
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE) {// 直播
            url = liveVideoSAConfigInner.URL_LIVE_SETSTAR;
            requestCallBack.url = url;
            params.addBodyParam("liveId", liveId);
            setDefaultParameter(params);
        } else if (type == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
            return;
        } else if (type == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 公开
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

    public void getStuGoldCount(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_STUDY_GOLD_COUNT;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void setTotalOpeningLength(String courseId, String liveId, String classId, String
            duration, String speakingNum, String speakingLen, HttpCallBack
                                              requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("duration", duration);
        params.addBodyParam("speakingNum", speakingNum);
        //暂时去掉，这个数据太大20190731
//        params.addBodyParam("speakingLen", speakingLen);
        params.addBodyParam("speakingLen", "");
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_TOTAL_OPEN;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void setNotOpeningNum(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_NOT_OPEN;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    public void getQuestion(String liveId, String questionId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
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
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_WARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获取理科一次多发的预加载课件本地资源
     */
    public void getMoreCoureWareUrl(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        requestCallBack.url = TextUtils.isEmpty(LiveVideoConfig.LIVEMULPRELOAD) ? LiveHttpConfig
                .URL_LIVE_GET_MORE_WARE_URL : LiveVideoConfig.LIVEMULPRELOAD;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获取语文一次多发的预加载课件本地资源
     */
    public void getChineseCoureWareUrl(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);

        requestCallBack.url = TextUtils.isEmpty(LiveVideoConfig.LIVEMULPRELOADCHS) ? liveVideoSAConfigInner
                .URL_LIVE_CHS_GET_MORE_WARE_URL : LiveVideoConfig.LIVEMULPRELOADCHS;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获取文科一发多题的预加载课件本地资源
     */
    public void getArtsMoreCoureWareUrl(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_ARTSMORE_COURSEWARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 语文预加载互动题
     *
     * @param liveId
     * @param requestCallBack
     */
    public void getArtsCourewareInfo(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        if (liveId != null && !"".equals(liveId)) {
            params.addBodyParam("liveId", liveId);
        }
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_ARTS_COURSEWARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 英语预加载互动题
     *
     * @param liveId
     * @param requestCallBack
     */
    public void getEnglishCourewareInfo(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        if (liveId != null && !"".equals(liveId)) {
            params.addBodyParam("liveId", liveId);
        }
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_ENGLISH_COURSEWARE_URL;
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 理科预加载互动题
     *
     * @param liveId
     * @param requestCallBack
     */
    public void getScienceCourewareInfo(String liveId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        if (liveId != null && !"".equals(liveId)) {
            params.addBodyParam("liveId", liveId);
        }
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_SCIENCE_COURSEWARE_URL;
//        requestCallBack.url = "https://laoshi.xueersi.com/science/LiveCourses/preLoadNewCourseWare/?liveId=355540";
        sendPost(requestCallBack.url, params, requestCallBack);
    }

    /**
     * 获得广告信息
     */
    public void getAdOnLL(String courseId, final HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("courseId", courseId);
        requestCallBack.url = LiveHttpConfig.URL_LIVE_GET_LEC_AD;
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
        if (callBack != null) {
            callBack.url = liveVideoSAConfigInner.LIVE_FULL_MARK_LIST_H5;
        }
        sendPost(liveVideoSAConfigInner.LIVE_FULL_MARK_LIST_H5, params, callBack);
    }

    /**
     * 获取优秀榜
     *
     * @param stuId           学生Id
     * @param liveId          场次Id
     * @param classId         班级Id
     * @param requestCallBack
     */
    public void getExcellentList(String status, String stuId, String liveId, String classId, String teamId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("status", status);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        if (requestCallBack != null) {
            requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_HONOR_LIST;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_HONOR_LIST, params, requestCallBack);
    }


    public void getPraoseTutorList(String rankId, String liveId, String courseId, String counselorId, HttpCallBack
            requestCallBack) {

        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
//        liveId = "373963";
//        courseId = "49568";
//        counselorId = "2632";
//        rankId = "1";
        params.addBodyParam("rankId", rankId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("counselorId", counselorId);


        sendPost(LiveHttpConfig.URL_LIVE_PRAISE_TUTOR_LIST, params, requestCallBack);
    }

    /**
     * 获取计算小超市榜
     *
     * @param stuId           学生Id
     * @param liveId          场次Id
     * @param classId         班级Id
     * @param requestCallBack
     */
    public void getMiniMarketList(String stuId, String liveId, String classId, String stuCouId, String courseId, String teamId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        if (requestCallBack != null) {
            requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_MINI_MARKET_LIST;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_MINI_MARKET_LIST, params, requestCallBack);
    }

    /**
     * 获取点赞榜
     *
     * @param stuId           学生Id
     * @param liveId          场次Id
     * @param classId         班级Id
     * @param requestCallBack
     */
    public void getLikeList(String stuId, String liveId, String classId, String teamId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        if (requestCallBack != null) {
            requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_LIKE_LIST;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_LIKE_LIST, params, requestCallBack);
    }

    /**
     * 获取点赞概率标识
     *
     * @param classId         班级Id
     * @param requestCallBack
     */
    public void getLikeProbability(String classId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        setDefaultParameter(params);
        if (requestCallBack != null) {
            requestCallBack.url = liveVideoSAConfigInner.URL_LIVE_GET_LIKE_PROBABILITY;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_LIKE_PROBABILITY, params, requestCallBack);
    }

    /**
     * 保存标记点
     *
     * @param time
     * @param url
     * @param callBack
     */
    public void saveLiveMark(String liveId, String type, String time, String url, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("curTime", time);
        params.addBodyParam("imageUrl", url);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("markType", type);
        setDefaultParameter(params);
        if (callBack != null) {
            callBack.url = liveVideoSAConfigInner.URL_LIVE_SAVE_MARK_POINT;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_SAVE_MARK_POINT, params, callBack);
    }


    /**
     * 获取标记点
     *
     * @param callBack
     */
    public void getMarkPoints(String liveId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", LiveAppUserInfo.getInstance().getStuId());
        params.addBodyParam("liveId", liveId);
//        params.addBodyParam("stuId","15657");
//        params.addBodyParam("liveId","107070");
        if (callBack != null) {
            callBack.url = LiveHttpConfig.URL_LIVE_GET_MARK_POINTS;
        }
        sendPost(LiveHttpConfig.URL_LIVE_GET_MARK_POINTS, params, callBack);
    }

    /**
     * 删除标记点
     *
     * @param callBack
     */
    public void deleteMarkPoints(String livdId, long time, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", LiveAppUserInfo.getInstance().getStuId());
        setDefaultParameter(params);
//        params.addBodyParam("stuId","15657");
//        params.addBodyParam("liveId","107070");
        params.addBodyParam("curTime", "" + time);
        params.addBodyParam("liveId", livdId);
        if (callBack != null) {
            callBack.url = LiveHttpConfig.URL_LIVE_DELETE_MARK_POINTS;
        }
        sendPost(LiveHttpConfig.URL_LIVE_DELETE_MARK_POINTS, params, callBack);
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
    public void getAutoNotice(String classId, String testId, String srcType, int type, int isForce, HttpCallBack
            callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("srcType", srcType);
        params.addBodyParam("type", "" + type);
        params.addBodyParam("isForce", "" + isForce);
        params.addBodyParam("stuId", LiveAppUserInfo.getInstance().getStuId());
        setDefaultParameter(params);
        Loger.i(LiveAutoNoticeBll.class.getSimpleName(), JSON.toJSON(params).toString());
        if (callBack != null) {
            callBack.url = liveVideoSAConfigInner.URL_LIVE_GET_AUTO_NOTICE;
        }
        sendPost(liveVideoSAConfigInner.URL_LIVE_GET_AUTO_NOTICE, params, callBack);
    }

    /**
     * 智能私信统计接口
     */
    public void autoNoticeStatisc(String classId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("stuId", LiveAppUserInfo.getInstance().getStuId());
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
        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
        sendPost(LiveHttpConfig.URL_EXPERIENCE_LIVE_ONLINETIME, params, callBack);
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

    /** roleplay组内排行榜 兼容全身直播新课件的改版Top3 */
    public void getRolePlayAnswerTeamRank(boolean isNewArt, String testId, HttpCallBack callBack) {
        if (isNewArt) {
            HttpRequestParams params = new HttpRequestParams();
            params.addBodyParam("testId", testId);
            setDefaultParameter(params);
            sendPost(LiveHttpConfig.URL_LIVE_ROLE_TOP3, params, callBack);
        } else {
            HttpRequestParams params = new HttpRequestParams();
            params.addBodyParam("testId", testId);
            setDefaultParameter(params);
            sendPost(liveVideoSAConfigInner.URL_LIVE_ROLE_TEAM, params, callBack);
        }

    }

    /** 直播讲座获取更多课程的信息 */
    public void getMoreChoiceCount(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_LECTURELIVE_MORE_COURSE, params, requestCallBack);
    }

    public void getCurTime(HttpCallBack callBack) {
        sendGetNoBusiness(LiveHttpConfig.URL_LIVE_GET_CURTIME, new HttpRequestParams(), callBack);
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
     * 投票 能量
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param releaseId
     * @param requestCallBack
     */
    public void addPersonAndTeamEnergy(String liveId, int addEnergy, String teamId, String classId, String stuId,
                                       String releaseId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("addEnergy", addEnergy + "");
        params.addBodyParam("releaseId", releaseId + "");
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_ADDPERSONANDTEAMENERGY + "/" + liveId, params, requestCallBack);
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
    public void teamEnergyNumAndContributionStar(String liveId, String teamId, String classId, String stuId, String
            testId,
                                                 String testPlan, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testPlan", testPlan);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR + "/" + liveId, params,
                requestCallBack);
    }

    /**
     * 理科战队pk  获取战队成员信息
     *
     * @param classId
     * @param teamId
     * @param httpCallBack
     */
    public void getTeamMates(String classId, String teamId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEAMPK_GETTEAMMATES, params, httpCallBack);
    }


    /**
     * 获取分队信息
     *
     * @param classId
     * @param teamId
     * @param requestCallBack
     */
    public void getCHTeamInfo(boolean isHalfBody, String id, String classId, String teamId, int useSkin, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_PKTEAMINFO(isHalfBody), params, requestCallBack);
    }

    /**
     * 获取pk 对手信息
     *
     * @param classId
     * @param teamId
     */
    public void getCHPkAdversary(boolean isHalfBody, String classId, String teamId, int useSkin, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_MATCHTEAM(isHalfBody), params, requestCallBack);
    }

    /**
     * 学生获取自己宝箱
     *
     * @param isWin
     * @param classId
     * @param teamId
     * @param stuId
     * @param isAIPartner 是否是 Ai伴侣直播间
     */
    public void getCHStuChest(boolean isHalfBody, int isWin, String classId, String teamId, String stuId, String liveId, boolean isAIPartner, int useSkin, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("isWin", isWin + "");
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isAIPartner", isAIPartner ? "1" : "0");
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_GETSTUCHESTURL(isHalfBody) + "/" + liveId, params, requestCallBack);

    }

    /**
     * 获取战队开宝箱结果
     *
     * @param liveId
     * @param stuId
     * @param teamId
     * @param classId
     * @param isAIPartner
     */
    public void getCHClassChestResult(boolean isHalfBody, String liveId, String stuId, String teamId, String classId, boolean isAIPartner, int useSkin,
                                      HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isAIPartner", isAIPartner ? "1" : "0");
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_GETCLASSCHESTRESULT(isHalfBody) + "/" + liveId, params, requestCallBack);
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
    public void addCHPersonAndTeamEnergy(boolean isHalfBody, String liveId, int addEnergy, String teamId, String classId, String stuId, int useSkin,
                                         HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("addEnergy", addEnergy + "");
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_ADDPERSONANDTEAMENERGY(isHalfBody) + "/" + liveId, params, requestCallBack);
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
    public void liveCHStuGoldAndTotalEnergy(boolean isHalfBody, String liveId, String teamId, String classId, String stuId, int useSkin, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_LIVESTUGOLDANDTOTALENERGY(isHalfBody) + "/" + liveId, params, requestCallBack);
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
    public void teamCHEnergyNumAndContributionStar(boolean isHalfBody, String liveId, String teamId, String classId, String stuId, String testId, String testPlan, int useSkin, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testPlan", testPlan);
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR(isHalfBody) + "/" + liveId, params, requestCallBack);
    }

    /**
     * 每题pk 结果
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param requestCallBack
     */
    public void stuCHPKResult(boolean isHalfBody, String liveId, String teamId, String classId, String testId, String testPlan, int useSkin, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testPlan", testPlan);
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_STUPKRESULT(isHalfBody) + "/" + liveId, params, requestCallBack);
    }

    /**
     * 每题战队能量 和贡献之星
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param tests
     * @param ctId            互动课件或者互动题时 testPlan= ''; 测试卷请求时testId= ' '
     * @param requestCallBack
     */
    public void teamCHEnergyNumAndContributionmulStar(boolean isHalfBody, String liveId, String teamId, String classId, String stuId,
                                                      String tests,
                                                      String ctId, String pSrc, int useSkin, HttpCallBack requestCallBack) {

        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("tests", tests + "");
        params.addBodyParam("ctId", ctId + "");
        params.addBodyParam("pSrc", pSrc + "");
        params.addBodyParam("useSkin", useSkin + "");
        setDefaultParameter(params);
        sendPost(LiveVideoChConfig.URL_CHPK_TEAMENERGYNUMANDCONTRIBUTIONSTARMUL(isHalfBody) + "/" + liveId, params, requestCallBack);
    }

    /**
     * 理科接麦举手接口
     *
     * @param requestCallBack
     */
    public void chatHandAdd(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_HANDADD, params, requestCallBack);
    }

    /**
     * 理科2018接麦举手接口
     *
     * @param stuId
     * @param requestCallBack
     */
    public void addStuPutUpHandsNum(String stuId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_ADD_STU_HAND_NUM, params, requestCallBack);
    }

    /**
     * 理科2018接麦举手获得用户列表接口
     *
     * @param requestCallBack
     */
    public void getStuInfoByIds(String stuIds, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("stuIds", stuIds);
        sendPost(liveVideoSAConfigInner.URL_LIVE_STUINFO, params, requestCallBack);
    }

    /**
     * 文科表扬榜
     *
     * @param rankId      榜单id
     * @param liveId      直播id
     * @param courseId    课程id
     * @param counselorId 辅导老师id
     */
    public void getArtsRankData(String rankId, String liveId, String courseId, String counselorId, HttpCallBack
            requestCallBack) {

        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("rankId", rankId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("counselorId", counselorId);
        //setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_ARTS_PRAISE_LIST, params, requestCallBack);
    }


    /**
     * 学生端上传用户发言语句，用户统计分词结果
     *
     * @param requestCallBack
     */
    public void uploadVoiceBarrage(String liveId, String stuId, String voiceId, String msg, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("voiceId", voiceId);
        params.addBodyParam("msg", msg);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_UPLOAD_VOICE_BARRAGE, params, requestCallBack);
    }


    /**
     * 立刻点赞送特效礼物
     *
     * @param requestCallBack
     */
    public void praiseSendGift(String liveId, String stuId, String stuCouId, int type, String teacherId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("enstuId", stuId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("type", String.valueOf(type));
        params.addBodyParam("teacherId", teacherId);
        sendPost(liveVideoSAConfigInner.URL_LIVE_PRAISE_GIFT, params, requestCallBack);
    }

    /**
     * 低端设备检测信息
     *
     * @param requestCallBack
     */

    public void getDeviceDetectionInfo(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("deviceMemory", Integer.toString(DeviceUtils.getTotalRam(mContext)));
        params.addBodyParam("apiLevel", Build.VERSION.SDK_INT + "");
        setDefaultParameter(params);
        sendPost(LiveVideoConfig.URL_CHECK_DEVICE, params, requestCallBack);
    }

    /**
     * 获取文科直播间 额外信息
     *
     * @param requestCallBack
     */
    public void getArtsExtLiveInfo(String liveId, String stuCouId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_ARTS_ROOM_INFO, params, requestCallBack);
    }

    /**
     * 直播上传精彩瞬间截图接口
     *
     * @param requestCallBack
     */
    public void uploadWonderMoment(int type, String url, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("type", "" + type);
        params.addBodyParam("url", url);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_WONDER_MOMENT, params, requestCallBack);
    }

    /**
     * 文科三分屏上传精彩瞬间截图url，半身直播走理科的接口
     */
    public void sendWonderfulMoment(String stuId, String liveId, String stuCouId, String type, String url, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("type", type);
        params.addBodyParam("url", url);
        sendPost(LiveHttpConfig.ART_TRIPLE_WONDERFUL_MOMENT, params, httpCallBack);
    }

    public void saveStuPlanOnlineTime(String stuId, String gradeId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("gradeId", gradeId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_LIVE_STU_ONLINE_TIME, params, requestCallBack);
    }

    /**
     * 弹幕推送
     *
     * @param json     json数据
     * @param callback
     */
    public void pushSpeechBullet(String json, okhttp3.Callback callback) {
        logger.i("speechbul,pushSpeechBullet: json = " + json);
        HttpRequestParams params = new HttpRequestParams();
        params.setJson(json);
        params.setWriteAndreadTimeOut(10);
        String url;
//        if (AppConfig.DEBUG) {
//            url = "http://10.99.2.31/v1/push";
//        } else {
//            url = "https://pushirc.arts.xueersi.com/v1/push";
//        }
        url = "https://pushirc.arts.xueersi.com/v1/push";
        baseSendPostNoBusinessJson(url, params, callback);
    }


    /**
     * 文科提交对老师评价
     *
     * @param liveId
     * @param courseId
     * @param teacherId
     * @param teacherScore
     * @param teacherOption
     * @param counselorId
     * @param requestCallBack
     */
    public void saveArtsEvaluationTeacher(String liveId, String courseId, String teacherId, String teacherScore, String
            teacherOption, String counselorId, String counselorScore, String
                                                  counselorOption, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("teacherScore", teacherScore);
        params.addBodyParam("teacherOption", teacherOption);
        params.addBodyParam("counselorId", counselorId);
        params.addBodyParam("counselorScore", counselorScore);
        params.addBodyParam("counselorOption", counselorOption);
        params.addBodyParam("classId", classId);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveHttpConfig.URL_LIVE_ARTS_EVALUATE_TEACHER, params, requestCallBack);
    }

    /**
     * 理科高中提交对老师评价
     *
     * @param liveId
     * @param requestCallBack
     */
    public void saveEvaluationTeacher(String liveId, String courseId, String teacherId, String teacherScore, String
            teacherOption, String counselorId, String counselorScore, String
                                              counselorOption, String classId, String mainInput, String tutorInput, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("teacherScore", teacherScore);
        params.addBodyParam("teacherOption", teacherOption);
        params.addBodyParam("counselorId", counselorId);
        params.addBodyParam("counselorScore", counselorScore);
        params.addBodyParam("counselorOption", counselorOption);

        params.addBodyParam("teacherEvaluateContent", mainInput);
        params.addBodyParam("counselorEvaluateContent", tutorInput);
        params.addBodyParam("classId", classId);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveHttpConfig.URL_LIVE_SCIENCE_EVALUATE_TEACHER, params, requestCallBack);
    }


    /**
     * 理科提交对老师评价
     *
     * @param liveId
     * @param courseId
     * @param teacherId
     * @param teacherScore
     * @param teacherOption
     * @param counselorScore
     * @param counselorOption
     * @param classId
     * @param requestCallBack
     */
    public void saveScienceEvaluationTeacher(String liveId, String courseId, String teacherId, String teacherScore, String
            teacherOption, String counselorId, String counselorScore, String
                                                     counselorOption, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("teacherScore", teacherScore);
        params.addBodyParam("teacherOption", teacherOption);
        params.addBodyParam("counselorId", counselorId);
        params.addBodyParam("counselorScore", counselorScore);
        params.addBodyParam("counselorOption", counselorOption);
        params.addBodyParam("classId", classId);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveHttpConfig.URL_LIVE_SCIENCE_EVALUATE_TEACHER, params, requestCallBack);
    }

    /**
     * 小语提交对老师评价
     *
     * @param liveId
     * @param requestCallBack
     */
    public void saveChsEvaluationTeacher(String liveId, String courseId, String teacherId, String teacherScore, String
            teacherOption, String counselorId, String counselorScore, String
                                                 counselorOption, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teacherId", teacherId);
        params.addBodyParam("teacherScore", teacherScore);
        params.addBodyParam("teacherOption", teacherOption);
        params.addBodyParam("counselorId", counselorId);
        params.addBodyParam("counselorScore", counselorScore);
        params.addBodyParam("counselorOption", counselorOption);
        params.addBodyParam("classId", classId);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveHttpConfig.URL_LIVE_CHS_EVALUATE_TEACHER, params, requestCallBack);
    }

    public void getArtsEvaluationOption(String isLittleEnglish, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("isLittleEnglish", isLittleEnglish);
        sendPost(LiveHttpConfig.URL_LIVE_ARTS_GET_EVALUATE_OPTION, params, requestCallBack);
    }

    public void getChsEvaluationOption(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        sendPost(LiveHttpConfig.URL_LIVE_CHS_GET_EVALUATE_OPTION, params, requestCallBack);
    }

    public void getSciecneEvaluationOption(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        sendPost(LiveHttpConfig.URL_LIVE_SCIENCE_GET_EVALUATE_OPTION, params, requestCallBack);
    }

    /**
     * 学生获取战队信息
     *
     * @param requestCallBack
     */
    public void getSelfTeamInfo(String stu_id, String unique_id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stu_id", "" + stu_id);
        params.addHeaderParam("Connection", "Close");
        setDefaultParameter(params);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_SELF_TEAM + "?unique_id=" + unique_id, params, requestCallBack);
    }

    /**
     * 学生获取战队信息 php
     *
     * @param requestCallBack
     */
    public void getEnglishPkGroup(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addHeaderParam("Connection", "Close");
        setDefaultParameter(params);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_GETENGLISH_PK, params, requestCallBack);
    }

    /**
     * 学生上报个人信息
     *
     * @param mode
     * @param requestCallBack
     */
    public void reportStuInfo(String mode, String stu_id, String stu_name, String stu_head, String stu_energy, String stu_lose_flag, String nick_name, String unique_id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("mode", "" + mode);
        params.addBodyParam("stu_id", "" + stu_id);
        params.addBodyParam("stu_name", stu_name);
        params.addBodyParam("stu_head", stu_head);
        params.addBodyParam("stu_energy", stu_energy);
        params.addBodyParam("stu_lose_flag", stu_lose_flag);
        params.addBodyParam("nick_name", nick_name);
        params.addBodyParam("unique_id", unique_id);
        setDefaultParameter(params);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_REPORT_STUINFO + "?unique_id=" + unique_id, params, requestCallBack);
    }

    /**
     * go-战队pk-更新用户分组
     *
     * @param requestCallBack
     */
    public void updataEnglishPkGroup(String isNewGroup, String isA, String robotId, String stuId, String info, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("isNewGroup", "" + isNewGroup);
        setDefaultParameter(params);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_UPDATA_GROUP, params, requestCallBack);
    }

    /**
     * go-战队pk-更新用户分组
     *
     * @param requestCallBack
     */
    public void updataEnglishPkByTestId(String teamId, String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("pkTeamId", "" + teamId);
        params.addBodyParam("testId", "" + testId);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_UPDATA_PK_RANK, params, requestCallBack);
    }

    /**
     * go-战队pk-更新用户分组
     *
     * @param requestCallBack
     */
    public void getEnglishPkTotalRank(String teamId, String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("pkTeamId", "" + teamId);
        params.addBodyParam("testId", "" + testId);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_PK_TOTAL_RANK, params, requestCallBack);
    }

    /**
     * 小理战队PK 二期 获取黑马榜
     *
     * @param liveId
     * @param classId
     * @param requestCallBack
     */
    public void getTeamPkProgressStudent(String liveId, String classId, String courseId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        setDefaultParameter(params);
        sendPost(liveVideoSAConfigInner.URL_TEMPK_GETPROGRESSSTU, params, requestCallBack);
    }

    /**
     * go-战队pk-更新用户分组
     *
     * @param requestCallBack
     */
    public void reportStuLike(String unique_id, String stu_id, String nick_name,
                              String teamId, String testId, String like_info,
                              HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("stu_id", "" + stu_id);
        params.addBodyParam("nick_name", "" + nick_name);
        params.addBodyParam("team_id", "" + teamId);
        params.addBodyParam("test_id", "" + testId);
        params.addBodyParam("like_info", "" + like_info);
        sendPost(LiveVideoHttpEnConfig.URL_LIVE_REPORT_STULIKE + "?unique_id=" + unique_id, params, requestCallBack);
    }

    /**
     * 学报接口地址
     *
     * @param url
     * @param classId 班级id
     * @param liveId  场次id
     * @param teamId  小组ID
     * @param stuId   学生ID
     */
    public void getJournalUrl(String url, String classId, String liveId, String teamId, String stuId, HttpCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("classId", classId);
        httpRequestParams.addBodyParam("liveId", liveId);
        httpRequestParams.addBodyParam("teamId", teamId);
        httpRequestParams.addBodyParam("stuId", stuId);
        sendPost(url, httpRequestParams, callBack);
    }

    /**
     * 是否走在线语音识别
     */
    public void getIsOnlineRecognize(String liveId, String id, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        setDefaultParameter(params);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("id", id);
        sendPost(LiveHttpConfig.URL_GOLD_MICROPHONE_TO_AI, params, httpCallBack);
    }

    /** 是否开启了麦克风权限，以及是否是金话筒 */
    public void sendIsGoldPhone(String liveId, String isOpenMicrophone, String isGoldMicrophone, String id, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("isOpenMicrophone", isOpenMicrophone);
        params.addBodyParam("isGoldMicrophone", isGoldMicrophone);
        params.addBodyParam("id", id);
        sendPost(LiveHttpConfig.URL_IS_GOLD_MICROPHONE, params, httpCallBack);
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
     * 教师反馈
     *
     * @param liveId
     * @param courseId
     * @param isPlayBack
     * @param httpCallBack
     */
    public void getFeedBack(String liveId, String courseId, String isPlayBack, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("isPlayBack", isPlayBack);
        sendPost(LiveHttpConfig.URL_LIVE_COURSE_GETEVALUATE, params, httpCallBack);
    }

    /**
     * 获取直播Plugin配置信息
     *
     * @param requestCallBack
     */
    public void getLivePluginConfigInfo(LivePluginRequestParam param, HttpCallBack requestCallBack) {

        sendJsonPost(LivePluginHttpConfig.URL_MODULE_INIT, param, requestCallBack);
    }

}
