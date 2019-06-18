package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.LogerTag;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveLogCallback;
import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportHttp;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertHttp;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.OnSpeechEval;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallAction;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechFeedBackHttp;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;
import org.xutils.xutils.http.RequestParams;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;

/**
 * 处理IRC消息，视频调度
 *
 * @author linyuqiang
 */
public class LiveBll extends BaseBll implements LiveAndBackDebug, IRCState, QuestionHttp, EnglishH5CoursewareHttp, SpeechFeedBackHttp, LearnReportHttp, LecAdvertHttp {
    private String TAG = "LiveBllLog";
    /** 互动题 */
    private QuestionAction mQuestionAction;
    /** 点名 */
    private RollCallAction mRollCallAction;
    /** 视频事件 */
    private VideoAction mVideoAction;

    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser mHttpResponseParser;
    private NewIRCMessage mIRCMessage;
    private String vStuCourseID;
    private String courseId;
    private String mLiveId;
    private String mCurrentDutyId;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private final LiveTopic mLiveTopic = new LiveTopic();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LogToFile mLogtf;
    /**
     * 主讲教师
     */
    private Teacher mMainTeacher;
    /**
     * 主讲教师名字
     */
    private String mMainTeacherStr = null;
    /**
     * 辅导教师
     */
    private Teacher mCounteacher;
    /**
     * 辅导教师IRC
     */
    private String mCounTeacherStr = null;
    /**
     * 渠道前缀
     */
    private final String CNANNEL_PREFIX = "x_";
    private final String ROOM_MIDDLE = "L";
    private Callback.Cancelable mCataDataCancle;
    private Callback.Cancelable mGetPlayServerCancle;
    /**
     * 学习记录提交时间间隔
     */
    private int mHbTime = 300, mHbCount = 0;
    private AtomicInteger mOpenCount = new AtomicInteger(0);
    private AtomicInteger mBufferCount = new AtomicInteger(0);
    private AtomicInteger mRepairBufferCount = new AtomicInteger(0);
    private AtomicInteger mRepairOpenCount = new AtomicInteger(0);
    private AtomicInteger mFailCount = new AtomicInteger(0);
    private AtomicInteger mFailMainTeacherCount = new AtomicInteger(0);
    private AtomicInteger mFailCounTeacherCount = new AtomicInteger(0);
    private AtomicInteger mCompleteCount = new AtomicInteger(0);
    private AtomicInteger mCompleteMainTeacherCount = new AtomicInteger(0);
    private AtomicInteger mCompleteCounTeacherCount = new AtomicInteger(0);

    /**
     * 签到成功 状态码
     */
    private static final int SIGN_STATE_CODE_SUCCESS = 2;
    /**
     * 用户心跳解析错误
     */
    private int userOnlineError = 0;
    private PlayServerEntity mServer;
    private PlayServerEntity.PlayserverEntity playserverEntity;
    /**
     * 网络类型
     */
    private int netWorkType;
    /**
     * 调度是不是在无网络下失败
     */
    private boolean liveGetPlayServerError = false;
    /**
     * 是不是有分组
     */
    private boolean haveTeam = false;
    private int form;
    /**
     * 智能私信业务
     */
    private LiveAutoNoticeBll mLiveAutoNoticeBll;
    long openStartTime;
    /**
     * 区分文理appid
     */
    String appID = UmsConstants.LIVE_APP_ID;
    /**
     * 校准系统时间
     */
    private long sysTimeOffset;
    public static boolean isAllowTeamPk = false;

    public LiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int form, LiveGetInfo
            liveGetInfo) {
        super(context);
        this.vStuCourseID = vStuCourseID;
        this.courseId = courseId;
        this.mLiveId = vSectionID;
        this.mLiveType = LiveVideoConfig.LIVE_TYPE_LIVE;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpManager.addBodyParam("form", "" + form);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context,TAG);
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (liveGetInfo != null) {
            mLiveTopic.setMode(liveGetInfo.getMode());
        }
    }

    public LiveBll(Context context, String vSectionID, int type, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context,TAG);
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
    }

    public LiveBll(Context context, String vSectionID, String currentDutyId, int type, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mCurrentDutyId = currentDutyId;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(context,TAG);
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
    }

    /**
     * 播放器异常日志
     *
     * @param str
     */
    public void getOnloadLogs(String TAG, String str) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String bz = UserBll.getInstance().getMyUserInfoEntity().getUserType() == 1 ? "student" : "teacher";
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = null;
        String filenam = "f";
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {//else不会发生
            filenam = packInfo.versionCode + "";
        }
        filenam = Build.VERSION.SDK_INT + "&" + filenam;
        if (mGetInfo == null) {
            UmsAgent.onEvent(mContext, LogerTag.DEBUG_VIDEO_LIVEMSG, LogerTag.DEBUG_VIDEO_LIVEMSG, 0, str);
            return;
        }
        LiveLogCallback liveLogCallback = new LiveLogCallback();
        RequestParams params = mHttpManager.liveOnloadLogs(mGetInfo.getClientLog(), "a" + mLiveType, mLiveId, mGetInfo.getUname(), enstuId,
                mGetInfo.getStuId(), mGetInfo.getTeacherId(), filenam, str, bz, liveLogCallback);
        liveLogCallback.setParams(params);
    }

    /**
     * 用户在线心跳
     */
    private void getUserOnline() {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String teacherId = "";
        if (mGetInfo != null) {
            teacherId = mGetInfo.getTeacherId();
        }
        final String finalTeacherId = teacherId;
        mHbCount++;
        mHttpManager.liveUserOnline(mLiveType, enstuId, mLiveId, teacherId, mCurrentDutyId, mHbTime, new HttpCallBack
                () {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                onFinished();
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                onFinished();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                onFinished();
            }

            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result).getJSONObject("result");
                    int status = object.getInt("status");
                    if (status == 1) {
                        Object dataObj = object.get("data");
                        if (dataObj instanceof JSONObject) {
                            JSONObject data = (JSONObject) dataObj;
                            mLogtf.d("getUserOnline:time=" + data.get("time"));
                        } else {
                            mLogtf.d("getUserOnline:time=" + dataObj);
                        }
                    } else {
                        mLogtf.d("getUserOnline:result=" + result);
                    }
                    userOnlineError = 0;
                } catch (JSONException e) {
                    if (userOnlineError > 5) {
                        return;
                    }
                    userOnlineError++;
                    if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                        //liveId
                        //teacherId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "teacherId=" + finalTeacherId +
                                ",result=" + result);
                    } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {
                        //classId
                        //dutyId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "mCurrentDutyId=" +
                                mCurrentDutyId + ",result=" + result);
                    } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                        //liveId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "result=" + result);
                    }
                    MobAgent.httpResponseParserError(TAG, "getUserOnline", result);
                }
            }

            public void onFinished() {
                postDelayedIfNotFinish(mUserOnlineCall, mHbTime * 1000);
            }
        });
    }

    /**
     * 领取金币
     *
     * @param operateId
     * @param liveId
     * @param callBack
     */
    public void sendReceiveGold(final int operateId, String liveId, final AbstractBusinessDataCallBack callBack) {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("sendReceiveGold:enstuId=" + enstuId + ",operateId=" + operateId + ",liveId=" + liveId);
        mHttpManager.sendReceiveGold(mLiveType, enstuId, operateId, liveId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("sendReceiveGold:onPmSuccess=" + responseEntity.getJsonObject().toString() + ",operateId=" +
                        operateId);
                VideoResultEntity entity = mHttpResponseParser.redPacketParseParser(responseEntity);
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("sendReceiveGold:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("sendReceiveGold:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" + operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 领取红包-站立直播
     *
     * @param operateId
     * @param callBack
     */
    public void getReceiveGoldTeamStatus(final int operateId, final AbstractBusinessDataCallBack callBack) {
        mLogtf.d("sendReceiveGoldStand:operateId=" + operateId + ",liveId=" + mLiveId);
        mHttpManager.getReceiveGoldTeamStatus(operateId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                        "operateId=" +
                        operateId);
                GoldTeamStatus entity = mHttpResponseParser.redGoldTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamStatus:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" +
                        operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    public void getReceiveGoldTeamRank(final int operateId, final AbstractBusinessDataCallBack callBack) {
        mLogtf.d("getReceiveGoldTeamRank:operateId=" + operateId + ",liveId=" + mLiveId);
        mHttpManager.getReceiveGoldTeamRank(operateId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamRank:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                        "operateId=" +
                        operateId);
                GoldTeamStatus entity = mHttpResponseParser.redGoldTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                entity.setHttpUrl(url);
                entity.setHttpRes("" + responseEntity.getJsonObject());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getReceiveGoldTeamRank:onPmFailure=" + msg + ",operateId=" + operateId);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getReceiveGoldTeamRank:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" +
                        operateId);
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 提交测试题
     *  @param liveBasePager
     * @param videoQuestionLiveEntity
     * @param liveId
     * @param testAnswer
     * @param isRight
     * @param isSubmit
     */
    @Override
    public void liveSubmitTestAnswer(final LiveBasePager liveBasePager, final VideoQuestionLiveEntity videoQuestionLiveEntity, String liveId, String
            testAnswer, final boolean isVoice, boolean isRight, final QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("liveSubmitTestAnswer:enstuId=" + enstuId + "," + videoQuestionLiveEntity.srcType + ",testId=" +
                videoQuestionLiveEntity.id + ",liveId=" + liveId + ",testAnswer="
                + testAnswer);
        String userMode = "1";
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                userMode = "0";
            }
        }
        mHttpManager.liveSubmitTestAnswer(mLiveType, enstuId, videoQuestionLiveEntity.srcType,
                videoQuestionLiveEntity.id, liveId, testAnswer, userMode, isVoice, isRight, new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        mLogtf.d("liveSubmitTestAnswer:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                                videoQuestionLiveEntity);
                        VideoResultEntity entity = mHttpResponseParser.parseQuestionAnswer(responseEntity, isVoice);
                        entity.setVoice(isVoice);
                        if (StringUtils.isSpace(entity.getTestId())) {
                            entity.setTestId(videoQuestionLiveEntity.id);
                        }
                        if (answerReslut != null) {
                            answerReslut.onAnswerReslut(videoQuestionLiveEntity, entity);
                        }
                        if (mQuestionAction != null) {
                            mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, entity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        mLogtf.d("liveSubmitTestAnswer:onPmFailure=" + msg + ",testId=" + videoQuestionLiveEntity.id);
                        if (mQuestionAction != null) {
                            mQuestionAction.onAnswerFailure();
                        }
                        if (answerReslut != null) {
                            answerReslut.onAnswerFailure();
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        mLogtf.d("liveSubmitTestAnswer:onPmError=" + responseEntity.getErrorMsg() + ",testId=" +
                                videoQuestionLiveEntity.id);
                        if (!responseEntity.isJsonError()) {
                            if (mQuestionAction != null) {
                                mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                        }
                    }
                });
    }

    @Override
    public void getTestAnswerTeamStatus(final VideoQuestionLiveEntity videoQuestionLiveEntity, final
    AbstractBusinessDataCallBack callBack) {
        mHttpManager.getTestAnswerTeamStatus(videoQuestionLiveEntity.id, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                GoldTeamStatus entity = mHttpResponseParser.testAnswerTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
//                if (AppConfig.DEBUG) {
//                    GoldTeamStatus entity = new GoldTeamStatus();
//                    for (int i = 0; i < 3; i++) {
//                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                        student.setNickname("测试" + i);
//                        student.setGold("90");
//                        student.setAvatar_path(mGetInfo.getHeadImgPath());
//                        student.setRight(i % 2 == 0);
//                        entity.getStudents().add(student);
//                    }
//                    callBack.onDataSucess(entity);
//                } else {
//                    callBack.onDataFail(1, responseEntity.getErrorMsg());
//                }
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    private int test1 = 0;

    /**
     * 站立直播语音评测战况
     *
     * @param testId
     * @param callBack
     */
    public void getSpeechEvalAnswerTeamStatus(String testId, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getSpeechEvalAnswerTeamStatus(testId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                GoldTeamStatus entity = mHttpResponseParser.getSpeechEvalAnswerTeamStatus(responseEntity, mGetInfo
                        .getStuId());
                callBack.onDataSucess(entity);
//                if (AppConfig.DEBUG) {
//                    GoldTeamStatus entity = new GoldTeamStatus();
//                    Random random = new Random();
//                    for (int i = 0; i < 5; i++) {
//                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                        student.setNickname("测试" + (test1++));
//                        student.createShowName();
//                        student.setScore("" + random.nextInt(101));
//                        student.setAvatar_path(mGetInfo.getHeadImgPath());
//                        entity.getStudents().add(student);
//                    }
//                    callBack.onDataSucess(entity);
//                } else {
//                    callBack.onDataFail(1, responseEntity.getErrorMsg());
//                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if (AppConfig.DEBUG) {
                    GoldTeamStatus entity = new GoldTeamStatus();
                    for (int i = 0; i < 3; i++) {
                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                        student.setNickname("测试" + (test1++));
                        student.createShowName();
                        student.setScore("90");
                        student.setAvatar_path(mGetInfo.getHeadImgPath());
                        entity.getStudents().add(student);
                    }
                    callBack.onDataSucess(entity);
                } else {
                    callBack.onDataFail(1, responseEntity.getErrorMsg());
                }
            }
        });
    }

    public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String liveId, String
            testAnswer, String type, String isSubmit, double voiceTime, boolean isRight, final QuestionSwitch
            .OnAnswerReslut answerReslut) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("liveSubmitTestH5Answer:enstuId=" + enstuId + "," + videoQuestionLiveEntity.srcType + ",testId=" +
                videoQuestionLiveEntity.id + ",liveId=" + liveId + ",testAnswer="
                + testAnswer);
        String userMode = "1";
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                userMode = "0";
            }
        }
        mHttpManager.liveSubmitTestH5Answer(enstuId, videoQuestionLiveEntity.srcType,
                videoQuestionLiveEntity.id, liveId, testAnswer, type, userMode, isSubmit, voiceTime, isRight, new
                        HttpCallBack() {

                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                mLogtf.d("liveSubmitTestH5Answer:onPmSuccess=" + responseEntity.getJsonObject()
                                        .toString() +
                                        "," +
                                        videoQuestionLiveEntity);
                                VideoResultEntity entity = mHttpResponseParser.parseQuestionAnswer(responseEntity,
                                        true);
                                entity.setVoice(true);
                                if (StringUtils.isSpace(entity.getTestId())) {
                                    entity.setTestId(videoQuestionLiveEntity.id);
                                }
                                if (answerReslut != null) {
                                    answerReslut.onAnswerReslut(videoQuestionLiveEntity, entity);
                                }
                            }

                            @Override
                            public void onPmFailure(Throwable error, String msg) {
                                mLogtf.d("liveSubmitTestH5Answer:onPmFailure=" + msg + ",testId=" +
                                        videoQuestionLiveEntity.id);
                                if (answerReslut != null) {
                                    answerReslut.onAnswerFailure();
                                }
                            }

                            @Override
                            public void onPmError(ResponseEntity responseEntity) {
                                mLogtf.d("liveSubmitTestH5Answer:onPmError=" + responseEntity.getErrorMsg() + "," +
                                        "testId=" +
                                        videoQuestionLiveEntity.id);
                                if (!responseEntity.isJsonError()) {
                                    if (answerReslut != null) {
                                        answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                                    }
                                }
                            }
                        });
    }

    public void getAllRanking(final AbstractBusinessDataCallBack callBack) {

        if (mGetInfo.getArtsExtLiveInfo() != null
                && mGetInfo.getArtsExtLiveInfo().getNewCourseWarePlatform().equals("1")) {
            getNewArtsRankingData(callBack);
        } else {
            getOldRankingData(callBack);
        }
    }

    /** 获取新文科课件直播间 排行信息 */
    private void getNewArtsRankingData(final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getNewArtsAllRank(mGetInfo.getId(), mGetInfo.getStuCouId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                AllRankEntity allRankEntity = mHttpResponseParser.parseAllRank(responseEntity);
                callBack.onDataSucess(allRankEntity);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getAllRanking:onPmError" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.e("getAllRanking:onPmFailure" + msg);
            }
        });

    }

    private void getOldRankingData(final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.getAllRanking(enstuId, mLiveId, classId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                AllRankEntity allRankEntity = mHttpResponseParser.parseAllRank(responseEntity);
                callBack.onDataSucess(allRankEntity);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getAllRanking:onPmError" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.e("getAllRanking:onPmFailure" + msg);
            }
        });

    }

//    public void getStuRanking(HttpCallBack requestCallBack) {
//        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
//        String classId = "";
//        if (mGetInfo.getStudentLiveInfo() != null) {
//            classId = mGetInfo.getStudentLiveInfo().getClassId();
//        }
//        mHttpManager.getStuRanking(enstuId, mLiveId, classId, requestCallBack);
//    }

    /**
     * 用户试听
     */
    public void userModeTime(AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.userModeTime(enstuId, mLiveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("userModeTime:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("userModeTime:onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.e("userModeTime:onPmFailure:msg=" + msg);
            }
        });
    }

    public void setVideoAction(VideoAction videoAction) {
        this.mVideoAction = videoAction;
    }

    /**
     * 是否是 高三 理科直播 （展示不同聊天 内容：高三理科 以 班级为单位展示,）
     *
     * @return
     */
    @Override
    public boolean isSeniorOfHighSchool() {
        return mGetInfo != null && mGetInfo.getIsSeniorOfHighSchool() == 1;
    }

    private long blockTime;

    /**
     * 当前状态，老师是不是在直播间
     */
    public boolean isPresent() {
        return isPresent(mLiveTopic.getMode());
    }

    /**
     * 当前状态
     *
     * @param mode 模式
     */
    private boolean isPresent(String mode) {
        boolean isPresent = true;
        if (mIRCMessage != null && mIRCMessage.onUserList()) {
            if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                isPresent = mMainTeacher != null;
            } else {
                isPresent = !mCounteacher.isLeave;
            }
        }
        return isPresent;
    }

    public LiveGetInfo getGetInfo() {
        return mGetInfo;
    }

    private static final long RETRY_DELAY = 3000;
    private static final long MAX_RETRY_TIME = 4;
    private Runnable initArtsExtLiveInfoTask = new Runnable() {
        int retryCount;

        @Override
        public void run() {
            mHttpManager.getArtsExtLiveInfo(mGetInfo.getId(), mGetInfo.getStuCouId(), new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    ArtsExtLiveInfo info = mHttpResponseParser.parseArtsExtLiveInfo(responseEntity);
                    mGetInfo.setArtsExtLiveInfo(info);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    retry();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    retry();
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    super.onFailure(call, e);
                    retry();
                }

            });
        }

        private void retry() {
            logger.e("======>retry get ArtsExtLiveInfo");
            if (retryCount < MAX_RETRY_TIME) {
                retryCount++;
                postDelayedIfNotFinish(initArtsExtLiveInfoTask, RETRY_DELAY);
            }
        }
    };

    private AtomicBoolean exInfoInited = new AtomicBoolean();

    /**
     * 初始化直接间额外参数
     *
     * @param getInfo
     */
    private void initExtInfo(LiveGetInfo getInfo) {
        if (getInfo != null && getInfo.getIsArts() == 1 && !exInfoInited.get()) {
            exInfoInited.set(true);
            postDelayedIfNotFinish(initArtsExtLiveInfoTask, 0);
        }
    }

    /**
     * 处理用户签到
     */
    private void handleUserSign() {
        if (mRollCallAction != null) {
            //理科自动签到
            if (RollCallBll.OPEN_AUTO_SIGN && mGetInfo.getIsArts() != 1
                    && mGetInfo.getStudentLiveInfo().getSignStatus() != SIGN_STATE_CODE_SUCCESS) {
                ClassSignEntity classSignEntity = new ClassSignEntity();
                classSignEntity.setStuName(mGetInfo.getStuName());
                classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                classSignEntity.setStatus(1);
                long classBeginTime = mGetInfo.getsTime() * 1000;
                long nowTime = (long) (mGetInfo.getNowTime() * 1000);
                mRollCallAction.autoSign(classSignEntity, classBeginTime, nowTime);
            } else {
                if (mGetInfo.getStudentLiveInfo().getSignStatus() != 0 && mGetInfo.getStudentLiveInfo()
                        .getSignStatus()
                        != 2) {
                    ClassSignEntity classSignEntity = new ClassSignEntity();
                    classSignEntity.setStuName(mGetInfo.getStuName());
                    classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                    classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                    classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
                    mRollCallAction.onRollCall(classSignEntity);
                }
            }
        }
    }

    public void getLecLearnReport(final long delayTime, final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getLecLearnReport:enstuId=" + enstuId + ",liveType=" + mLiveType + ",liveId=" + mLiveId + "," +
                "delayTime=" + delayTime);
        mHttpManager.getLearnReport(enstuId, mLiveId, mLiveType, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LearnReportEntity learnReportEntity = mHttpResponseParser.parseLecLearnReport(responseEntity);
                if (learnReportEntity != null) {
                    learnReportEntity.getStu().setStuName(mGetInfo.getStuName());
                    learnReportEntity.getStu().setTeacherName(mGetInfo.getTeacherName());
                    learnReportEntity.getStu().setTeacherIMG(mGetInfo.getTeacherIMG());
                    callBack.onDataSucess(learnReportEntity);
                }
                mLogtf.d("getLecLearnReport:onPmSuccess:learnReportEntity=" + (learnReportEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getLecLearnReport:onPmFailure=" + error + ",msg=" + msg + ",delayTime=" + delayTime);
                if (delayTime < 15000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getLecLearnReport(delayTime + 5000, callBack);
                        }
                    }, delayTime);
                } else {
                    callBack.onDataFail(0, msg);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLecLearnReport:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 提交教师评价
     */
    public synchronized void sendTeacherEvaluate(int[] score, final HttpCallBack requestCallBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("sendTeacherEvaluate:enstuId=" + enstuId + ",liveId=" + mLiveId);
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.sendTeacherEvaluate(enstuId, mLiveId, classId, score, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                requestCallBack.onPmSuccess(responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                requestCallBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                requestCallBack.onPmError(responseEntity);
                //onLiveError(responseEntity);
            }
        });
    }

    /**
     * 结束聊天
     */
    public void stopIRC() {
        if (mIRCMessage != null) {
            mIRCMessage.setCallback(null);
            mIRCMessage.destory();
        }
    }


    /**
     * activity  stop
     */
    public void onStop() {

    }

    /**
     * activity resume
     */
    public void onResume() {

    }


    /**
     * activity退出
     */
    public void onDestroy() {
        mQuestionAction = null;


        if (mRollCallAction != null) {
            mRollCallAction.forceCloseRollCall();
        }

        mRollCallAction = null;
        mVideoAction = null;
        if (mCataDataCancle != null) {
            mCataDataCancle.cancel();
            mCataDataCancle = null;
        }
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        if (mIRCMessage != null) {
            mIRCMessage.setCallback(null);
            mIRCMessage.destory();
        }
        isAllowTeamPk = false;
    }

    private void onLiveFailure(String msg, Runnable runnable) {
        if (runnable == null) {
            showToast(msg);
        } else {
            showToast(msg + "，稍后重试");
            postDelayedIfNotFinish(runnable, 1000);
        }
    }

    private void onLiveError(ResponseEntity responseEntity) {
        if (mVideoAction != null) {
            mVideoAction.onLiveError(responseEntity);
        }
    }

    /**
     * IRC互动题和直播互动题转换
     *
     * @param topic
     * @return
     */
//    public VideoQuestionLiveEntity getQuestionFromTopic(TopicEntity topic) {
//        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//        videoQuestionLiveEntity.id = topic.getId();
//        videoQuestionLiveEntity.num = topic.getNum();
//        videoQuestionLiveEntity.gold = topic.getGold_count();
//        videoQuestionLiveEntity.time = topic.getTime();
//        videoQuestionLiveEntity.type = topic.getType();
//        videoQuestionLiveEntity.choiceType = topic.getChoiceType();
//        videoQuestionLiveEntity.srcType = topic.getSrcType();
//        videoQuestionLiveEntity.isTestUseH5 = topic.isTestUseH5();
//        videoQuestionLiveEntity.isAllow42 = topic.getIsAllow42();
//        videoQuestionLiveEntity.speechContent = topic.getSpeechContent();
////        if (BuildConfig.DEBUG) {
////            videoQuestionLiveEntity.isTestUseH5 = true;
////        }
////        if (topic.getType().equals("1")) {
////            videoQuestionLiveEntity.num = 20;
////            videoQuestionLiveEntity.choiceType = "2";
////        }
//        videoQuestionLiveEntity.setStuAnswer(topic.getAnswer());
//        return videoQuestionLiveEntity;
//    }

    /**
     * 直播互动题和IRC互动题转换
     *
     * @param videoQuestionLiveEntity
     * @return
     */
//    public TopicEntity getTopicFromQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity) {
//        TopicEntity topic = new TopicEntity();
//        topic.setId(videoQuestionLiveEntity.id);
//        topic.setNum((int) videoQuestionLiveEntity.num);
//        topic.setGold_count((int) videoQuestionLiveEntity.gold);
//        topic.setTime((int) videoQuestionLiveEntity.time);
//        topic.setType(videoQuestionLiveEntity.type);
//        topic.setChoiceType(videoQuestionLiveEntity.choiceType);
//        topic.setAnswer(videoQuestionLiveEntity.getStuAnswer());
//        topic.setSrcType(videoQuestionLiveEntity.srcType);
//        topic.setIsAllow42(videoQuestionLiveEntity.isAllow42);
//        topic.setSpeechContent(videoQuestionLiveEntity.speechContent);
//        return topic;
//    }

    /**
     * 用户心跳倒计时
     */
    private Runnable mUserOnlineCall = new Runnable() {

        @Override
        public void run() {
            getUserOnline();
        }
    };

    /**
     * 懂了吗提交
     *
     * @param understand
     */
    public void understand(boolean understand, String nonce) {
        if (mMainTeacherStr != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.UNDERSTANDS);
                jsonObject.put("understand", understand);
                jsonObject.put("nonce", nonce);
                mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
                mLogtf.d("understand ok");
            } catch (Exception e) {
                // logger.e( "understand", e);
                mLogtf.e("understand", e);
            }
        } else {
            mLogtf.d("understand mMainTeacherStr=null");
        }
    }

    @Override
    public void praiseTeacher(final String formWhichTeacher, String ftype, String educationStage, final HttpCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
        mHttpManager.praiseTeacher(mLiveType, enstuId, mLiveId, teacherId, ftype, educationStage, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("praiseTeacher:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                if (responseEntity.getJsonObject() instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        sendFlowerMessage(jsonObject.getInt("type"), formWhichTeacher);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onPmSuccess(responseEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("onPmFailure:msg=" + msg);
                callBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("praiseTeacher:onPmFailure:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onPmError(responseEntity);
            }
        });
    }

    /**
     * 点名成功，状态设置为2.发notice信息
     */
    public void onRollCallSuccess() {
        try {
            mGetInfo.getStudentLiveInfo().setSignStatus(2);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.CLASS_MATEROLLCALL);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("name", "" + mGetInfo.getStuName());
            jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
            jsonObject.put("Version", "" + mGetInfo.getHeadImgVersion());
            mIRCMessage.sendNotice(jsonObject.toString());
            mLogtf.d("onRollCallSuccess ok");
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("onRollCallSuccess", e);
        }
    }

    @Override
    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    public boolean isConnected() {
        if (mIRCMessage == null) {
            return false;
        }
        return mIRCMessage.isConnected();
    }

    @Override
    public boolean isHaveTeam() {
        return haveTeam;
    }

    /**
     * 是否开启聊天
     */
    @Override
    public boolean openchat() {
        boolean openchat;
        if (LiveTopic.MODE_CLASS.equals(getMode())) {
            openchat = mLiveTopic.getMainRoomstatus().isOpenchat();
        } else {
            openchat = mLiveTopic.getCoachRoomstatus().isOpenchat();
        }
        mLogtf.d("openchat:getMode=" + getMode() + ",isOpenchat=" + openchat);
        return openchat;
    }

    private SendMsgListener mSendMsgListener;

    public void setSendMsgListener(SendMsgListener listener) {
        mSendMsgListener = listener;
    }

    /** 发送消息回调 */
    public interface SendMsgListener {
        void onMessageSend(String msg, String targetName);
    }


    /**
     * 发生聊天消息
     */
    @Override
    public boolean sendMessage(String msg, String name) {
        if (mSendMsgListener != null) {
            mSendMsgListener.onMessageSend(msg, name);
        }

        if (mLiveTopic.isDisable()) {
            return false;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
                if (StringUtils.isEmpty(name)) {
                    name = mGetInfo.getStuName();
                }
                jsonObject.put("name", name);
                jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                jsonObject.put("version", "" + mGetInfo.getHeadImgVersion());
                jsonObject.put("msg", msg);
                if (haveTeam) {
                    StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    String teamId = studentLiveInfo.getTeamId();
                    jsonObject.put("from", "android_" + teamId);
                    jsonObject.put("to", teamId);
                }
                mIRCMessage.sendMessage(jsonObject.toString());
            } catch (Exception e) {
                // logger.e( "understand", e);
                UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                mLogtf.e("sendMessage", e);
            }
            return true;
        }
    }

    /**
     * 发送上墙信号聊天消息
     */
    @Override
    public void sendRankMessage(int code) {
        if (mLiveTopic.isDisable()) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", code + "");
            jsonObject.put("classId", mGetInfo.getStudentLiveInfo().getClassId());
            jsonObject.put("teamId", mGetInfo.getStudentLiveInfo().getTeamId());
            mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否开启献花
     */
    @Override
    public boolean isOpenbarrage() {
        return mLiveTopic.getMainRoomstatus().isOpenbarrage();
    }

    /**
     * 理科主讲是否开启献花
     */
    @Override
    public boolean isOpenZJLKbarrage() {
        return mLiveTopic.getCoachRoomstatus().isZJLKOpenbarrage();
    }

    /**
     * 理科辅导老师是否开启献花
     */
    @Override
    public boolean isOpenFDLKbarrage() {
        return mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage();
    }

    /**
     * 发生献花消息
     */
    public void sendFlowerMessage(int ftype, String frommWhichTeacher) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.FLOWERS);
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("ftype", ftype);

            if (frommWhichTeacher != null) {
                jsonObject.put("to", frommWhichTeacher);
            }
            mIRCMessage.sendMessage(jsonObject.toString());
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendFlowerMessage", e);
        }
    }

    public void requestMicro(String nonce, String from) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.REQUEST_MICRO);
            jsonObject.put("status", "on");
            jsonObject.put("network", "normal");
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("img", mGetInfo.getStuImg());
            jsonObject.put("courseid", courseId);
            jsonObject.put("nonce", nonce);
            jsonObject.put("times", mGetInfo.getStuLinkMicNum());
            if ("t".equals(from)) {
                mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
            } else {
                mIRCMessage.sendNotice(mCounTeacherStr, jsonObject.toString());
            }
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("requestMicro", e);
        }
    }

    /**
     * 放弃举手
     */
    public void giveupMicro(String from) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.REQUEST_MICRO);
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("status", "off");
            if ("t".equals(from)) {
                mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
            } else {
                mIRCMessage.sendNotice(mCounTeacherStr, jsonObject.toString());
            }
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("giveupMicro", e);
        }
    }

    /**
     * 发送星星互动答案
     *
     * @param index 答案位置
     */
    public void sendStat(int index) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.ROOM_STAR_SEND_S);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("answer", index);
//            if (LiveTopic.MODE_CLASS.equals(getMode())) {
//                mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
//            } else {
//                mIRCMessage.sendNotice(mCounteacher.get_nick(), jsonObject.toString());
//            }
            mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendStat", e);
        }
    }

    /**
     * 学生发送秒数指令
     */
    public void sendDBStudent(int time) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_DB_STUDENT);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("duration", "" + time);
            mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendDBStudent", e);
        }
    }

    public void sendVote(int answer, String nonce) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.VOTE_SEND);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("answer", "" + answer);
            jsonObject.put("nonce", "" + nonce);
            mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendVote", e);
        }
    }

    /**
     * 直播修复
     *
     * @param isbuffer true是缓冲超时，false是视频打开超时
     */
    public void repair(boolean isbuffer) {
        if (isbuffer) {
            mRepairBufferCount.set(mRepairBufferCount.get() + 1);
        } else {
            mRepairOpenCount.set(mRepairOpenCount.get() + 1);
        }
    }

    /**
     * 得到当前模式
     */
    @Override
    public String getMode() {
        String mode;
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            mode = mLiveTopic.getMode();
        } else {
            mode = LiveTopic.MODE_CLASS;
        }
        return mode;
    }


    /**
     * 得到当前理科的notice模式
     */
    @Override
    public String getLKNoticeMode() {
        String mode;
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (mLiveTopic == null) {
                mode = LiveTopic.MODE_CLASS;
            } else {
                mode = mLiveTopic.getLKNoticeMode();
            }
        } else {
            mode = LiveTopic.MODE_CLASS;
        }
        return mode;
    }

    public String getConnectNickname() {
        return mIRCMessage.getConnectNickname();
    }


    public String getNickname() {
        return mIRCMessage.getNickname();
    }

    public String getStuName() {
        return mGetInfo.getStuName();
    }

    @Override
    public void getSpeechEval(String id, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.getSpeechEval(enstuId, liveid, id, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                SpeechEvalEntity speechEvalEntity = mHttpResponseParser.parseSpeechEval(responseEntity);
                if (speechEvalEntity != null) {
                    onSpeechEval.onSpeechEval(speechEvalEntity);
                } else {
                    responseEntity = new ResponseEntity();
                    responseEntity.setStatus(false);
                    responseEntity.setErrorMsg("出了点意外，请稍后试试");
                    responseEntity.setJsonError(true);
                    onSpeechEval.onPmError(responseEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, final OnSpeechEval
            onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult:onPmSuccess=" + responseEntity.getJsonObject());
                onSpeechEval.onSpeechEval(null);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("sendSpeechEvalResult:onPmFailure=" + msg);
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void sendSpeechEvalResult2(final String id, final String stuAnswer, String isSubmit, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult2:onPmSuccess=" + responseEntity.getJsonObject());
//                Handler handler=new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                        onSpeechEval.onSpeechEval(jsonObject);
//                    }
//                },2000);
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                onSpeechEval.onSpeechEval(jsonObject);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("sendSpeechEvalResult2:onPmFailure=" + msg);
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult2:onPmError=" + responseEntity.getErrorMsg());
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    public void getSpeechEvalAnswerTeamRank(final String id, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getSpeechEvalAnswerTeamRank(id, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmSuccess=" + responseEntity.getJsonObject());
                GoldTeamStatus entity = mHttpResponseParser.parseSpeechTeamRank(responseEntity, mGetInfo);
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmFailure=" + msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmError=" + responseEntity.getErrorMsg());
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void speechEval42IsAnswered(final String id, String num, final SpeechEvalAction.SpeechIsAnswered
            isAnswered) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.speechEval42IsAnswered(enstuId, id, num, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                mLogtf.i("speechEval42IsAnswered:onPmSuccess=" + jsonObject);
                boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                isAnswered.isAnswer(isAnswer);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("speechEval42IsAnswered:onPmFailure=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("speechEval42IsAnswered:onPmError=" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void getStuGoldCount(String method) {
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                String liveid = mGetInfo.getId();
                String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
                mHttpManager.getStuGoldCount(enstuId, liveid, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("getStuGoldCount:onPmSuccess=" + responseEntity.getJsonObject());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("getStuGoldCount:onPmFailure=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.i("getStuGoldCount:onPmError=" + responseEntity.getErrorMsg());
                    }
                });
            }
        }, 500);
    }

    public void setNotOpeningNum() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.setNotOpeningNum(enstuId, mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                logger.e("setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, final AbstractBusinessDataCallBack
            callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.getQuestion(enstuId, mGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(), new
                HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.d("getQuestion:onPmSuccess" + responseEntity.getJsonObject());
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.e("getQuestion:onFailure", e);
                        super.onFailure(call, e);
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.d("getQuestion:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                        callBack.onDataSucess();
                    }
                });
    }

    public void getCourseWareUrl(HttpCallBack requestCallBack) {
        mHttpManager.getCourseWareUrl(requestCallBack);
    }

    public void getAdOnLL(final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getAdOnLL(lecAdvertEntity.course_id, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("getAdOnLL:onPmSuccess=" + responseEntity.getJsonObject());
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                int isLearn = jsonObject.optInt("isLearn", 0);
                lecAdvertEntity.isLearn = isLearn;
                if (isLearn == 0) {
                    lecAdvertEntity.limit = jsonObject.optString("limit");
                    lecAdvertEntity.signUpUrl = jsonObject.optString("signUpUrl");
                    lecAdvertEntity.saleName = jsonObject.optString("saleName");
                    lecAdvertEntity.courseId = jsonObject.optString("courseId");
                    lecAdvertEntity.classId = jsonObject.optString("classId");
                }
                callBack.onDataSucess();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("getAdOnLL:onPmError=" + responseEntity.getErrorMsg());
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError(responseEntity
// .getErrorMsg()));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                logger.d("getAdOnLL:onFailure", e);
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError());
            }
        });
    }


    /**
     * 存储学生语音反馈音源
     *
     * @param talkSourcePath
     */
    public void saveStuTalkSource(String talkSourcePath, String service) {
        mHttpManager.saveStuTalkSource(mGetInfo.getStuId(), talkSourcePath, service, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("saveStuTalkSource:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.d("saveStuTalkSource:onPmFailure" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("saveStuTalkSource:onPmError" + responseEntity.getErrorMsg());
            }
        });
    }

    public Call download(final String url, final String saveDir, DownloadCallBack downloadCallBack) {
        return mHttpManager.download(url, saveDir, downloadCallBack);
    }

    /**
     * 弹出toast，判断Video是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isResume()) {
            XESToastUtils.showToast(mContext, text);
        }
    }

    /**
     * 接口失败，重新请求，判断video是不是存活
     *
     * @param r           重新请求的事件
     * @param delayMillis
     */
    private void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public enum MegId {
        MEGID_12102("12102", "startplay"), MEGID_12103("12103", "fail"),
        MEGID_12107("12107", "bufreconnect"), MEGID_12137("12137", "bufreconnect"),
        MEGID_12130("12130", "delay");
        String msgid;
        String detail;

        MegId(String msgid, String detail) {
            this.msgid = msgid;
            this.detail = detail;
        }
    }

    /**
     * 调试信息
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugSys(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
    }

    /**
     * 交互日志
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }

    /**
     * 展现日志
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", courseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData);
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {

    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {

    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {

    }

    // 03.22 上传体验课播放器的心跳时间
    public void uploadExperiencePlayTime(String liveId, String termId, Long hbtime) {
        mHttpManager.uploadExperiencePlayingTime(liveId, termId, hbtime, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("uploadexperiencetime:" + responseEntity.getJsonObject());
            }
        });
    }

    public String getLiveId() {
        return mLiveId;
    }

    // 04.04 获取更多课程
    @Override
    public void getMoreChoice(final PageDataLoadEntity pageDataLoadEntity, final AbstractBusinessDataCallBack
            getDataCallBack) {
        mHttpManager.getMoreChoiceCount(mLiveId, new HttpCallBack(pageDataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.e("responseEntity:" + responseEntity);
                MoreChoice choiceEntity = mHttpResponseParser.parseMoreChoice(responseEntity);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });
    }

    public void setChatOpen(boolean open) {
        if (LiveTopic.MODE_CLASS.equals(getMode())) {
            mLiveTopic.getMainRoomstatus().setOpenchat(open);
        } else {
            mLiveTopic.getCoachRoomstatus().setOpenchat(open);
        }
    }


}