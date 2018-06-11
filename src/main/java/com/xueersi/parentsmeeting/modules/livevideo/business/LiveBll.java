package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.CommonRequestCallBack;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.LogerTag;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveArtsHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveScienceHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.PraiseListPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.xesalib.umsagent.UmsAgent;
import com.xueersi.xesalib.umsagent.UmsAgentManager;
import com.xueersi.xesalib.umsagent.UmsConstants;
import com.xueersi.xesalib.utils.app.DeviceUtils;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;
import com.xueersi.xesalib.view.layout.dataload.PageDataLoadEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;
import org.xutils.xutils.ex.HttpException;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 处理IRC消息，视频调度
 *
 * @author linyuqiang
 */
public class LiveBll extends BaseBll implements LiveAndBackDebug {
    private String TAG = "LiveBllLog";
    LiveLazyBllCreat liveLazyBllCreat;
    private QuestionAction mQuestionAction;
    private RollCallAction mRollCallAction;
    private PraiseOrEncourageAction mPraiseOrEncourageAction;
    private RedPackageAction readPackageBll;
    private VideoAction mVideoAction;
    private RoomAction mRoomAction;
    private LearnReportAction mLearnReportAction;
    private LecLearnReportAction mLecLearnReportAction;
    private H5CoursewareAction h5CoursewareAction;
    private EnglishH5CoursewareAction englishH5CoursewareAction;
    private VideoChatAction videoChatAction;
    private StarInteractAction starAction;
    private EnglishSpeekAction englishSpeekAction;
    private LiveVoteAction liveVoteAction;
    private PraiseListAction mPraiseListAction;
    private SpeechFeedBackAction speechFeedBackAction;
    private LecAdvertAction lecAdvertAction;
    private RolePlayAction rolePlayAction;
    private LiveHttpManager mHttpManager;
    private LiveScienceHttpManager liveScienceHttpManager;
    private LiveArtsHttpManager liveArtsHttpManager;
    private LiveVideoSAConfig liveVideoSAConfig;
    private LiveHttpResponseParser mHttpResponseParser;
    private IRCMessage mIRCMessage;
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
    /**
     * 主讲老师前缀
     */
    public static final String TEACHER_PREFIX = "t_";
    /**
     * 辅导老师前缀
     */
    public static String COUNTTEACHER_PREFIX = "f_";
    private final String ROOM_MIDDLE = "L";
    private Callback.Cancelable mCataDataCancle;
    private Callback.Cancelable mGetPlayServerCancle;
    /** 直播帧数统计 */
    TotalFrameStat totalFrameStat;
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
     * 录播课的直播
     */
    public final static int LIVE_TYPE_TUTORIAL = 1;
    /**
     * 公开直播
     */
    public final static int LIVE_TYPE_LECTURE = 2;
    /**
     * 直播课的直播
     */
    public final static int LIVE_TYPE_LIVE = 3;

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
     * 满分榜业务
     */
    private AnswerRankBll mAnswerRankBll;
    /**
     * 标记点业务
     */
    private LiveRemarkBll mLiveRemarkBll;
    /**
     * 校准系统时间
     */
    private long sysTimeOffset;
    /**
     * 战队pk业务
     */
    private TeamPkBll mTeamPKBll;
    private TeacherPraiseBll mTeacherPraiseBll1;
    public static boolean isAllowTeamPk = false;

    public LiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int form, LiveGetInfo
            liveGetInfo) {
        super(context);
        this.vStuCourseID = vStuCourseID;
        this.courseId = courseId;
        this.mLiveId = vSectionID;
        this.mLiveType = LIVE_TYPE_LIVE;
        this.form = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpManager.addBodyParam("form", "" + form);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
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
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (type != LIVE_TYPE_LIVE) {
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
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (type != LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
    }

    public void setLiveLazyBllCreat(LiveLazyBllCreat liveLazyBllCreat) {
        this.liveLazyBllCreat = liveLazyBllCreat;
    }

    /**
     * 播放器数据初始化
     *
     * @param getInfo
     */
    public void getInfo(LiveGetInfo getInfo) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getInfo:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (getInfo == null) {
            HttpCallBack callBack = new HttpCallBack(false) {

                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    mLogtf.d("getInfo:onPmSuccess" + responseEntity.getJsonObject());
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    if (mLiveType == LIVE_TYPE_LECTURE) {
                        if (object.optInt("isAllow", 1) == 0) {
                            if (mVideoAction != null) {
                                mVideoAction.onLiveDontAllow(object.optString("refuseReason"));
                            }
                            return;
                        }
                    }
                    LiveGetInfo getInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, mLiveType, form);
                    onGetInfoSuccess(getInfo);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    mLogtf.d("getInfo:onPmFailure=" + msg);
                    onLiveFailure("初始化失败", new Runnable() {

                        @Override
                        public void run() {
                            getInfo(null);
                        }
                    });
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    mLogtf.d("getInfo:onPmError=" + responseEntity.getErrorMsg());
                    onLiveError(responseEntity);
                }
            };
//        mHttpManager.addBodyParam("enstuId", enstuId);
            if (mLiveType == LIVE_TYPE_LIVE) {// 直播
                mHttpManager.liveGetInfo(enstuId, courseId, mLiveId, 0, callBack);
            } else if (mLiveType == LIVE_TYPE_TUTORIAL) {// 辅导
                mHttpManager.liveTutorialGetInfo(enstuId, mLiveId, callBack);
            } else if (mLiveType == LIVE_TYPE_LECTURE) {
                mHttpManager.liveLectureGetInfo(enstuId, mLiveId, callBack);
            }
        } else {
            onGetInfoSuccess(getInfo);
        }
    }

    public void setTotalFrameStat(TotalFrameStat totalFrameStat) {
        this.totalFrameStat = totalFrameStat;
    }

    /**
     * 播放器异常日志
     *
     * @param str
     */
    public void getOnloadLogs(String TAG, final String str) {
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
        mHttpManager.liveOnloadLogs(mGetInfo.getClientLog(), "a" + mLiveType, mLiveId, mGetInfo.getUname(), enstuId,
                mGetInfo.getStuId(), mGetInfo.getTeacherId(), filenam, str, bz, new Callback.CommonCallback<File>() {

                    @Override
                    public void onSuccess(File o) {
                        //Loger.i(TAG, "getOnloadLogs:onSuccess");
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        //Loger.i(TAG, "getOnloadLogs:onError", throwable);
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                        //Loger.i(TAG, "getOnloadLogs:onCancelled");
                    }

                    @Override
                    public void onFinished() {
                        //Loger.i(TAG, "getOnloadLogs:onFinished");
                    }

                });
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
                    if (mLiveType == LiveBll.LIVE_TYPE_LIVE) {
                        //liveId
                        //teacherId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "teacherId=" + finalTeacherId +
                                ",result=" + result);
                    } else if (mLiveType == LiveBll.LIVE_TYPE_TUTORIAL) {
                        //classId
                        //dutyId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "mCurrentDutyId=" +
                                mCurrentDutyId + ",result=" + result);
                    } else if (mLiveType == LiveBll.LIVE_TYPE_LECTURE) {
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
     *
     * @param videoQuestionLiveEntity
     * @param liveId
     * @param testAnswer
     * @param isRight
     */
    public void liveSubmitTestAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String liveId, String
            testAnswer, final boolean isVoice, boolean isRight, final QuestionSwitch.OnAnswerReslut answerReslut) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("liveSubmitTestAnswer:enstuId=" + enstuId + "," + videoQuestionLiveEntity.srcType + ",testId=" +
                videoQuestionLiveEntity.id + ",liveId=" + liveId + ",testAnswer="
                + testAnswer);
        String userMode = "1";
        if (mLiveType == LIVE_TYPE_LIVE) {
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
                            mQuestionAction.onAnswerReslut(videoQuestionLiveEntity, entity);
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
                                mQuestionAction.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                        }
                    }
                });
    }

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
        if (mLiveType == LIVE_TYPE_LIVE) {
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
                Loger.e(TAG, "getAllRanking:onPmError" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.e(TAG, "getAllRanking:onPmFailure" + msg);
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
                Loger.d(TAG, "userModeTime:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.e(TAG, "userModeTime:onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.e(TAG, "userModeTime:onPmFailure:msg=" + msg);
            }
        });
    }

    public void setQuestionAction(QuestionAction action) {
        this.mQuestionAction = action;
    }

    public void setRollCallAction(RollCallAction action) {
        this.mRollCallAction = action;
    }

    public void setPraiseOrEncourageAction(PraiseOrEncourageAction action) {
        this.mPraiseOrEncourageAction = action;
    }

    public void setReadPackageBll(RedPackageAction redPackageAction) {
        this.readPackageBll = redPackageAction;
    }

    public void setVideoAction(VideoAction videoAction) {
        this.mVideoAction = videoAction;
    }

    public void setRoomAction(RoomAction roomAction) {
        this.mRoomAction = roomAction;
    }

    public void setLearnReportAction(LearnReportAction mLearnReportAction) {
        this.mLearnReportAction = mLearnReportAction;
    }

    public void setLecLearnReportAction(LecLearnReportAction mLearnReportAction) {
        this.mLecLearnReportAction = mLearnReportAction;
    }

    public void setH5CoursewareAction(H5CoursewareAction h5CoursewareAction) {
        this.h5CoursewareAction = h5CoursewareAction;
    }

    public void setEnglishH5CoursewareAction(EnglishH5CoursewareAction englishH5CoursewareAction) {
        this.englishH5CoursewareAction = englishH5CoursewareAction;
    }

    public VideoChatAction getVideoChatAction() {
        return videoChatAction;
    }

    public void setVideoChatAction(VideoChatAction videoChatAction) {
        this.videoChatAction = videoChatAction;
    }

    public void setStarAction(StarInteractAction starAction) {
        this.starAction = starAction;
    }

    public void setEnglishSpeekAction(EnglishSpeekAction englishSpeekAction) {
        this.englishSpeekAction = englishSpeekAction;
    }

    public void setLiveVoteAction(LiveVoteAction liveVoteAction) {
        this.liveVoteAction = liveVoteAction;
    }

    public void setPraiseListAction(PraiseListAction mPraiseListAction) {
        this.mPraiseListAction = mPraiseListAction;
    }

    public void setSpeechFeedBackAction(SpeechFeedBackAction speechFeedBackAction) {
        this.speechFeedBackAction = speechFeedBackAction;
    }

    public void setLecAdvertAction(LecAdvertAction lecAdvertAction) {
        this.lecAdvertAction = lecAdvertAction;
    }

    private final IRCCallback mIRCcallback = new IRCCallback() {

        String lastTopicstr = "";

        @Override
        public void onStartConnect() {
            if (mRoomAction != null) {
                mRoomAction.onStartConnect();
            }
        }

        @Override
        public void onRegister() {
            mLogtf.d("onRegister");
            if (mRoomAction != null) {
                mRoomAction.onRegister();
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            mLogtf.i("onChannelInfo:userCount=" + userCount);
            onTopic(channel, topic, "", 0, true);
        }

        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed) {
            if (lastTopicstr.equals(topicstr)) {
                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
                return;
            }
            Loger.e("LiveBll", "======>onTopic:" + topicstr);

            if (TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            mLogtf.i("onTopic:topicstr=" + topicstr);
            try {
                JSONObject jsonObject = new JSONObject(topicstr);
                LiveTopic liveTopic = mHttpResponseParser.parseLiveTopic(mLiveTopic, jsonObject, mLiveType);
//                mLiveTopic.setMode(LiveTopic.MODE_CLASS);
                mLogtf.d("onTopic:oldmode=" + mLiveTopic.getMode() + ",newmode=" + liveTopic.getMode() + ",topic=" +
                        liveTopic.getVideoQuestionLiveEntity());
                if (mLiveType == LIVE_TYPE_LIVE) {
                    //模式切换
                    if (!(mLiveTopic.getMode().equals(liveTopic.getMode()))) {
                        mLiveTopic.setMode(liveTopic.getMode());
                        mGetInfo.setMode(liveTopic.getMode());
                        if (mVideoAction != null) {
                            boolean isPresent = isPresent(mLiveTopic.getMode());
                            mVideoAction.onModeChange(mLiveTopic.getMode(), isPresent);
                        }
                        //模式切换，断开接麦
                        if (videoChatAction != null) {
                            videoChatAction.quit("off", "", "change");
                        }
                        //模式切换为主讲，关闭表扬榜
                        if (mPraiseListAction != null && liveTopic.getMode().equals(LiveTopic.MODE_CLASS))
                            mPraiseListAction.closePraiseList();
                        liveGetPlayServer(true);
                    }
                    if (mLiveRemarkBll != null) {
                        //主讲
                        Loger.i("LiveRemarkBll", "ontopic____onbreak:" + mLiveTopic.getMainRoomstatus().isOnbreak()
                                + "   mode:" + getMode());
                        if (!liveTopic.getMainRoomstatus().isOnbreak() && liveTopic.getMode().equals(LiveTopic
                                .MODE_CLASS)) {
                            mLiveRemarkBll.setClassReady(true);
                        } else {
                            mLiveRemarkBll.setClassReady(false);
                        }
                    }
                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
                    if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
                        if ("on".equals(mainRoomstatus.getExamStatus())) {
                            String num = mainRoomstatus.getExamNum();
                            mQuestionAction.onExamStart(mLiveId, num, "");
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(num);
                            }
                        } else {
                            mQuestionAction.onExamStop();
                        }
                    }
//                    if (liveVoteAction == null && liveLazyBllCreat != null) {
//                        liveLazyBllCreat.createLiveVoteAction();
//                    }
//                    if (liveVoteAction != null) {
//                        LiveTopic.VoteEntity voteEntity = mainRoomstatus.getVoteEntity();
//                        if (voteEntity != null) {
//                            liveVoteAction.voteStart(voteEntity, false);
//                        } else {
//                            liveVoteAction.onCancle();
//                        }
//                    }
                    if (englishSpeekAction != null) {
                        boolean openDbEnergy = mainRoomstatus.isOpenDbEnergy();
                        if (openDbEnergy) {
                            englishSpeekAction.onDBStart();
                        } else {
                            englishSpeekAction.onDBStop();
                        }
                    }
                    LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
                    if (mVideoAction != null) {
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())) {
                            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                                mVideoAction.onTeacherNotPresent(true);
                            }
                        }
                    }
                    if (videoChatAction != null) {
                        String oldVoiceChatStatus = voiceChatStatus;
                        if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode())) {
                            voiceChatStatus = mainRoomstatus.getOpenhands();
                            videoChatAction.onJoin(mainRoomstatus.getOnmic(), mainRoomstatus.getOpenhands(),
                                    mainRoomstatus.getRoom(), mainRoomstatus.isClassmateChange(), mainRoomstatus
                                            .getClassmateEntities(), "t");
                        } else {
                            coachRoomstatus = liveTopic.getCoachRoomstatus();
                            voiceChatStatus = coachRoomstatus.getOpenhands();
                            videoChatAction.onJoin(coachRoomstatus.getOnmic(), coachRoomstatus.getOpenhands(),
                                    coachRoomstatus.getRoom(), coachRoomstatus.isClassmateChange(), coachRoomstatus
                                            .getClassmateEntities(), "f");
                        }
                        if (mRoomAction != null && !oldVoiceChatStatus.equals(voiceChatStatus)) {
                            mRoomAction.videoStatus(voiceChatStatus);
                        }
                    }
                    if (coachRoomstatus.getListStatus() != 0 && LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())) {
                        if (mPraiseListAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createPraiseListAction();
                        }
                        if (mPraiseListAction != null) {
                            if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_HONOR) {
                                getHonorList(0);
                            } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_PROGRESS) {
                                getProgressList(0);
                            } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP) {
                                getThumbsUpList();
                            }
                        }
                    }
                }
                List<String> disableSpeaking = liveTopic.getDisableSpeaking();
                boolean have = false;
                for (String id : disableSpeaking) {
                    if (("" + id).contains(mIRCMessage.getNickname())) {
                        have = true;
                    }
                }
                liveTopic.setDisable(have);
                mLiveTopic.copy(liveTopic);
                if (liveTopic.getVideoQuestionLiveEntity() != null) {
                    if (mQuestionAction != null) {
                        mQuestionAction.showQuestion(liveTopic.getVideoQuestionLiveEntity());
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.setTestId(liveTopic.getVideoQuestionLiveEntity().getvQuestionID());
                        }
                        if (mLiveAutoNoticeBll != null) {
                            mLiveAutoNoticeBll.setTestId(liveTopic.getVideoQuestionLiveEntity().getvQuestionID());
                            mLiveAutoNoticeBll.setSrcType(liveTopic.getVideoQuestionLiveEntity().srcType);
                        }
                    }
                } else {
                    if (mQuestionAction != null) {
                        mQuestionAction.showQuestion(null);
                    }
                }
                if (mRoomAction != null) {
                    mRoomAction.onOpenbarrage(mLiveTopic.getMainRoomstatus().isOpenbarrage(), false);
                    mRoomAction.onDisable(have, false);
                }
                if (h5CoursewareAction != null && jsonObject.has("h5_Experiment")) {
                    JSONObject h5_Experiment = jsonObject.getJSONObject("h5_Experiment");
                    String play_url = h5_Experiment.optString("play_url");
                    String status = h5_Experiment.optString("status", "off");
                    if (StringUtils.isEmpty(play_url)) {
                        status = "off";
                    }
                    h5CoursewareAction.onH5Courseware(play_url, status);
                }
                if (englishH5CoursewareAction != null && jsonObject.has("H5_Courseware")) {
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    JSONObject h5_Experiment = jsonObject.getJSONObject("H5_Courseware");
                    String play_url = "";
                    String status = h5_Experiment.optString("status", "off");
                    String id = "";
                    String courseware_type = "";
                    if ("on".equals(status)) {
                        LiveVideoConfig.isNewEnglishH5 = false;
                        id = h5_Experiment.getString("id");
                        courseware_type = h5_Experiment.getString("courseware_type");
                        play_url = liveVideoSAConfig.inner.coursewareH5 + mLiveId + "/" + vStuCourseID + "/" + id +
                                "/" + courseware_type
                                + "/" + mGetInfo.getStuId();
                        videoQuestionLiveEntity.id = id;
                        videoQuestionLiveEntity.courseware_type = courseware_type;
                        videoQuestionLiveEntity.url = play_url;
                        videoQuestionLiveEntity.nonce = "";
                        String isVoice = h5_Experiment.optString("isVoice");
                        videoQuestionLiveEntity.setIsVoice(isVoice);
                        if ("1".equals(isVoice)) {
                            videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = h5_Experiment
                                    .optString("questiontype");
                            videoQuestionLiveEntity.assess_ref = h5_Experiment.optString("assess_ref");
                        }
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                            mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                        }
                        if (mLiveAutoNoticeBll != null) {
                            mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                            mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                        }
                    } else {
                        LiveVideoConfig.isNewEnglishH5 = true;
                        if (englishH5CoursewareAction != null) {
                            JSONObject object = jsonObject.optJSONObject("platformTest");
                            if (object != null) {
                                LiveVideoConfig.isSend = object.optBoolean("open");
                                status = "on";
                                String nonce = object.optString("nonce");
                                StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                                String teamId = studentLiveInfo.getTeamId();
                                String classId = studentLiveInfo.getClassId();
                                EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                                englishH5Entity.setNewEnglishH5(true);
                                try {
                                    JSONObject objects = new JSONObject();
                                    objects.put("packageId", object.getString("pId"));
                                    objects.put("packageSource", object.getString("pSrc"));
                                    objects.put("packageAttr", object.getString("pAttr"));
                                    objects.put("releasedPageInfos", object.getString("tests"));
                                    objects.put("teamId", teamId);
                                    objects.put("stuCouId", vStuCourseID);
                                    objects.put("stuId", mGetInfo.getStuId());
                                    objects.put("classId", classId);
                                    mShareDataManager.put(LiveVideoConfig.newEnglishH5, objects.toString(), ShareDataManager.SHAREDATA_USER);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
                }
                if (LiveTopic.MODE_CLASS.equals(getMode())) {
                    if (mRoomAction != null) {
                        mRoomAction.onopenchat(mLiveTopic.getMainRoomstatus().isOpenchat(), LiveTopic.MODE_CLASS,
                                false);
                    }
                } else {
                    if (mRoomAction != null) {
                        mRoomAction.onopenchat(mLiveTopic.getCoachRoomstatus().isOpenchat(), LiveTopic.MODE_TRANING,
                                false);
                    }
                }
                if (mLecLearnReportAction != null) {
                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
                    if (mainRoomstatus.isOpenFeedback()) {
                        mLecLearnReportAction.onLearnReport(mLiveId);
                    }
                }
                if (mPraiseListAction != null) {

                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getCoachRoomstatus();
                    Loger.e(TAG, "listStatus=" + mainRoomstatus.getListStatus());
                    if (mainRoomstatus.getListStatus() == 1) {
                        getHonorList(0);
                    } else if (mainRoomstatus.getListStatus() == 2) {
                        getProgressList(0);
                    } else if (mainRoomstatus.getListStatus() == 3) {
                        getThumbsUpList();
                    }
                }
                if (speechFeedBackAction != null) {
                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
                    String status = mainRoomstatus.getOnVideoChat();
                    if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(getMode())) {
                        String roomId = mainRoomstatus.getAgoraVoiceChatRoom();
                        speechFeedBackAction.start(roomId);
                    } else {
                        speechFeedBackAction.stop();
                    }
                }

                // 战队pk  topic 逻辑
                LiveTopic.TeamPkEntity teamPkEntity = liveTopic.getTeamPkEntity();
                Loger.e("LiveBll", "====>onTopic 112:" + teamPkEntity + ":" + mTeamPKBll);
                if (teamPkEntity != null && mTeamPKBll != null) {
                    //恢复战队pk 相关状态
                    int openBoxStateCode = 0;
                    int alloteamStateCode = 0;
                    int allotpkmanStateCode = 0;

                    if (LiveTopic.MODE_CLASS.equals(getMode()) && teamPkEntity.getRoomInfo1() != null) {
                        openBoxStateCode = teamPkEntity.getRoomInfo1().getOpenbox();
                        alloteamStateCode = teamPkEntity.getRoomInfo1().getAlloteam();
                        allotpkmanStateCode = teamPkEntity.getRoomInfo1().getAllotpkman();
                        Loger.e("LiveBll", "====>onTopic teampk main_teacher_info:" + openBoxStateCode + ":" +
                                alloteamStateCode + ":" + allotpkmanStateCode);
                    } else {
                        if (teamPkEntity.getRoomInfo2() != null) {
                            openBoxStateCode = teamPkEntity.getRoomInfo2().getOpenbox();
                            alloteamStateCode = teamPkEntity.getRoomInfo2().getAlloteam();
                            allotpkmanStateCode = teamPkEntity.getRoomInfo2().getAllotpkman();
                            Loger.e("LiveBll", "====>onTopic teampk assist_teacher_info:" + openBoxStateCode + ":" +
                                    alloteamStateCode + ":" + allotpkmanStateCode);
                        }
                    }

                    if (!mTeamPKBll.isTopicHandled() && alloteamStateCode == 1) {
                        mTeamPKBll.setTopicHandled(true);
                        mTeamPKBll.showTeamSelecting();
                        Loger.e("LiveBll", "====>onTopic showTeamSelecting:");
                        return;
                    }
                    if (allotpkmanStateCode == 1 && !mTeamPKBll.isTopicHandled()) {
                        mTeamPKBll.setTopicHandled(true);
                        mTeamPKBll.startSelectAdversary();
                        Loger.e("LiveBll", "====>onTopic startSelectAdversary:");
                        return;
                    }

                    if (openBoxStateCode == 1 && !mTeamPKBll.isTopicHandled()) {
                        mTeamPKBll.setTopicHandled(true);
                        //mTeamPKBll.resumeOpenBoxScene();
                        mTeamPKBll.showPkResult();
                        Loger.e("LiveBll", "====>onTopic showPkResult:");
                        return;
                    }

                    mTeamPKBll.setTopicHandled(true);
                }

            } catch (JSONException e) {
                mLogtf.e("onTopic", e);
                MobAgent.httpResponseParserError(TAG, "onTopic", e.getMessage());
            }
        }

        String lastNotice = "";
        String voiceChatStatus = "off";

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                             final String notice) {
            // Loger.d(TAG, "onNotice:target=" + target + ",notice=" + notice);
            // mLogtf.i("onNotice:target=" + target + ",notice=" + notice);
            String msg = "onNotice:target=" + target;
            try {
                final JSONObject object = new JSONObject(notice);
                int mtype = object.getInt("type");
                Loger.e("LiveBll", "=====>:onNotice:" + notice);
                Loger.i("===========notice type" + mtype);
                msg += ",mtype=" + mtype + ",voiceChatStatu=" + voiceChatStatus + ",";
                switch (mtype) {
                    case XESCODE.READPACAGE:
                        msg += "READPACAGE";
                        //接麦红包无效
                        if ("off".equals(voiceChatStatus)) {
                            if (readPackageBll != null) {
                                readPackageBll.onReadPackage(object.getInt("id"), new RedPackageAction
                                        .OnReceivePackage() {
                                    @Override
                                    public void onReceivePackage(int operateId) {
                                        // 更新右侧 金币信息
                                        if (mTeamPKBll != null) {
                                            mTeamPKBll.updatePkStateLayout(false);
                                        }
                                    }
                                });
                            }
                        }
                        break;
                    case XESCODE.GAG: {
                        msg += "GAG";
                        boolean disable = object.getBoolean("disable");
                        //s_3_13827_11022_1
                        String id = object.getString("id");
                        if (("" + id).contains(mIRCMessage.getNickname())) {
                            mLiveTopic.setDisable(disable);
                            if (mRoomAction != null) {
                                mRoomAction.onDisable(disable, true);
                            }
                        } else {
                            if (mRoomAction != null) {
                                String name = object.optString("name");
                                mRoomAction.onOtherDisable(id, name, disable);
                            }
                        }
                        msg += ",disable=" + disable + ",id=" + id + "," + mIRCMessage.getNickname();
                    }
                    break;
                    case XESCODE.SENDQUESTION: {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        videoQuestionLiveEntity.type = object.optString("ptype");
                        videoQuestionLiveEntity.id = object.optString("id");
                        videoQuestionLiveEntity.time = object.optDouble("time");
                        videoQuestionLiveEntity.num = object.optInt("num");
                        videoQuestionLiveEntity.gold = object.optDouble("gold");
                        videoQuestionLiveEntity.srcType = object.optString("srcType");
                        videoQuestionLiveEntity.choiceType = object.optString("choiceType", "1");
                        videoQuestionLiveEntity.isTestUseH5 = object.optInt("isTestUseH5", -1) == 1;
                        videoQuestionLiveEntity.nonce = object.optString("nonce", "");
                        videoQuestionLiveEntity.isAllow42 = object.optString("isAllow42", "");
                        videoQuestionLiveEntity.speechContent = object.optString("answer", "");
                        videoQuestionLiveEntity.multiRolePlay = object.optString("multiRolePlay", "0");
//                        if (BuildConfig.DEBUG) {onget
//                            videoQuestionLiveEntity.isTestUseH5 = true;
//                        }
                        String isVoice = object.optString("isVoice");
                        videoQuestionLiveEntity.setIsVoice(isVoice);
                        if ("1".equals(isVoice)) {
                            videoQuestionLiveEntity.questiontype = object.optString("questiontype");
                            videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                        }
                        if (mQuestionAction != null) {
//                            mGetInfo.getLiveTopic().setTopic(getTopicFromQuestion(videoQuestionLiveEntity));
                            mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(videoQuestionLiveEntity);
                            mQuestionAction.showQuestion(videoQuestionLiveEntity);
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                            }
                            if (mLiveAutoNoticeBll != null) {
                                mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.srcType);
                            }

                            if (mQuestionAction instanceof QuestionBll) {
                                ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(false);
                                Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: " +
                                        "SENDQUESTION");
                            }
                        }
                        msg += "SENDQUESTION:id=" + videoQuestionLiveEntity.id + ",gold=" + videoQuestionLiveEntity
                                .gold;
                    }
                    break;
                    case XESCODE.STOPQUESTION:
                        msg += "STOPQUESTION";
//                        mGetInfo.getLiveTopic().setTopic(null);

                        if (mTeamPKBll != null) {
                            mTeamPKBll.setNonce(object.optString("nonce", ""));
                            mTeamPKBll.showCurrentPkResult();
                            Loger.e("TeamPkBll", "======>showCurrentPkResult: called in STOPQUESTION");
                        }

                        mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(null);
                        if (mQuestionAction != null) {
                            mQuestionAction.onStopQuestion(object.getString("ptype"), object.optString("ptype"));
                            if (mQuestionAction instanceof QuestionBll) {
                                ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                                Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: " +
                                        "STOPQUESTION");
                            }
                        }
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.setNonce(object.optString("nonce"));
                        }


//                        getStuGoldCount();
                        break;
                    case XESCODE.CLASSBEGIN: {
                        boolean begin = object.getBoolean("begin");
                        mLiveTopic.getMainRoomstatus().setClassbegin(begin);
                        msg += begin ? "CLASSBEGIN" : "CLASSEND";
                        Loger.i("LiveRemarkBll", "classBegin____onbreak:" + mLiveTopic.getMainRoomstatus().isOnbreak()
                                + "   mode:" + getMode());
                        if (!mLiveTopic.getMainRoomstatus().isOnbreak() && mLiveRemarkBll != null && LiveTopic
                                .MODE_CLASS.equals(getMode())) {
                            mLiveRemarkBll.setClassReady(true);
                        }
                        //正式上课 结束关闭签到相关UI
                        if (mRollCallAction != null) {
                            mRollCallAction.forceCloseRollCall();
                        }
                    }
                    break;
                    case XESCODE.UNDERSTANDT:
                        msg += "UNDERSTANDT";
                        if ("off".equals(voiceChatStatus)) {//接麦懂了么无效
                            if (mQuestionAction != null) {
                                String nonce = object.optString("nonce");
                                mQuestionAction.understand(nonce);
                            }
                        }
                        break;
                    case XESCODE.OPENBARRAGE: {
                        boolean open = object.getBoolean("open");
                        msg += open ? "OPENBARRAGE" : "CLOSEBARRAGE";
                        mLiveTopic.getMainRoomstatus().setOpenbarrage(open);
                        mLogtf.d(msg);
                        if (mRoomAction != null) {
                            mRoomAction.onOpenbarrage(open, true);
                        }
                        //getLearnReport();
                        break;
                    }
                    case XESCODE.OPENCHAT: {
                        boolean open = object.getBoolean("open");
                        String from = object.optString("from", "t");
                        msg += "from=" + from + ",open=" + open;
                        if ("t".equals(from)) {
                            mLiveTopic.getMainRoomstatus().setOpenchat(open);
                            if (LiveTopic.MODE_CLASS.equals(getMode())) {
                                if (mRoomAction != null) {
                                    mRoomAction.onopenchat(open, LiveTopic.MODE_CLASS, true);
                                }
                            }
                        } else {
                            mLiveTopic.getCoachRoomstatus().setOpenchat(open);
                            if (LiveTopic.MODE_TRANING.equals(getMode())) {
                                if (mRoomAction != null) {
                                    mRoomAction.onopenchat(open, LiveTopic.MODE_TRANING, true);
                                }
                            }
                        }
                    }
                    break;
                    case XESCODE.MODECHANGE: {
                        String mode = object.getString("mode");
                        msg += ",mode=" + mode;
                        mLogtf.d("onNotice:oldmode=" + mLiveTopic.getMode() + ",newmode=" + mode);
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            mLiveTopic.setMode(mode);
                            mGetInfo.setMode(mode);
                            if (mVideoAction != null) {
                                boolean isPresent = isPresent(mode);
                                mVideoAction.onModeChange(mode, isPresent);
                                if (!isPresent) {
                                    mVideoAction.onTeacherNotPresent(true);
                                }
                            }
                            //模式切换，断开接麦
                            if (videoChatAction != null) {
                                videoChatAction.quit("off", "", "change");
                            }
                            //模式切换为主讲，关闭表扬榜
                            if (mPraiseListAction != null && mode.equals(LiveTopic.MODE_CLASS))
                                mPraiseListAction.closePraiseList();
                            liveGetPlayServer(true);
                        }
                    }
                    break;
                    case XESCODE.TEACHER_MESSAGE:
                        if (mRoomAction != null) {
                            String name;
                            if (sourceNick.startsWith("t")) {
                                name = "主讲老师";
                                String teacherImg = "";
                                try {
                                    teacherImg = mGetInfo.getMainTeacherInfo().getTeacherImg();
                                } catch (Exception e) {

                                }
                                mRoomAction.onMessage(target, sourceNick, "", "", object.getString("msg"), teacherImg);
                            } else {
                                name = "辅导老师";
                                String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
                                String to = object.optString("to", "All");
                                if ("All".equals(to) || teamId.equals(to)) {
                                    String teacherIMG = mGetInfo.getTeacherIMG();
                                    mRoomAction.onMessage(target, sourceNick, "", "", object.getString("msg"),
                                            teacherIMG);
                                }
                            }
                        }
                        break;
                    case XESCODE.LEARNREPORT: {
                        msg += "LEARNREPORT";
                        getLearnReport(2, 1000);
                        break;
                    }
                    case XESCODE.ROLLCALL: {
                        msg += "ROLLCALL";
                        if (mRollCallAction != null) {
                            //自动签到
                            mRollCallAction.onRollCall(false);
                            msg += ",signStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus();
                            if (mGetInfo.getStudentLiveInfo().getSignStatus() != 2) {
                                ClassSignEntity classSignEntity = new ClassSignEntity();
                                classSignEntity.setStuName(mGetInfo.getStuName());
                                classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                                classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                                classSignEntity.setStatus(1);
                                mRollCallAction.onRollCall(classSignEntity);
                            }
                        }
                        break;
                    }
                    case XESCODE.STOPROLLCALL: {
                        msg += "STOPROLLCALL";
                        if (mRollCallAction != null) {

                            mRollCallAction.onRollCall(true);
                            if (mGetInfo.getStudentLiveInfo().getSignStatus() != 2) {
                                mGetInfo.getStudentLiveInfo().setSignStatus(3);
                                ClassSignEntity classSignEntity = new ClassSignEntity();
                                classSignEntity.setStuName(mGetInfo.getStuName());
                                classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                                classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                                classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
                                mRollCallAction.onRollCall(classSignEntity);
                            }
                        }
                        break;
                    }
                    case XESCODE.CLASS_MATEROLLCALL: {
                        if (RollCallBll.IS_SHOW_CLASSMATE_SIGN) {
                            if (mRollCallAction != null) {
                                List<String> headImgUrl = mGetInfo.getHeadImgUrl();
                                ClassmateEntity classmateEntity = new ClassmateEntity();
                                String id = object.optString("id");
                                classmateEntity.setId(id);
                                classmateEntity.setName(object.getString("name"));
                                if (!headImgUrl.isEmpty()) {
                                    try {
                                        String img = headImgUrl.get(0) + "/" + object.getString("path") + "/" +
                                                mGetInfo.getImgSizeType() + "?" + object.getString("Version");
                                        classmateEntity.setImg(img);
                                    } catch (JSONException e) {
                                        MobAgent.httpResponseParserError(TAG, "onNotice:setImg", e.getMessage());
                                    }
                                    msg += "CLASS_MATEROLLCALL，" + classmateEntity.getName() + ",img=" +
                                            classmateEntity.getImg();
                                } else {
                                    msg += "CLASS_MATEROLLCALL，" + classmateEntity.getName() + ",no head";
                                }
                                mRollCallAction.onClassmateRollCall(classmateEntity);
                            }
                        }
                        break;
                    }
                    case XESCODE.PRAISE: {
                        msg += "PRAISE";
                        if (mPraiseOrEncourageAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createPraiseOrEncourageAction();
                        }
                        if (mPraiseOrEncourageAction != null) {
                            final JSONObject finalObject = object;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPraiseOrEncourageAction.onPraiseOrEncourage(finalObject);
                                }
                            });
                        }
                    }
                    break;
                    case XESCODE.EXAM_START: {
                        msg += "EXAM_START";
                        if (mQuestionAction != null) {
                            String num = object.optString("num", "0");
                            String nonce = object.optString("nonce");
                            mQuestionAction.onExamStart(mLiveId, num, nonce);
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(num);
                            }
                            if (mQuestionAction instanceof QuestionBll) {
                                ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(false);
                                Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: EXAM_START");
                            }

                        }
                    }
                    break;
                    case XESCODE.EXAM_STOP: {
                        msg += "EXAM_STOP";
                        if (mTeamPKBll != null) {
                            mTeamPKBll.setNonce(object.optString("nonce", ""));
                            mTeamPKBll.showCurrentPkResult();
                            Loger.e("TeamPkBll", "======>showCurrentPkResult: called in EXAM_STOP");
                        }

                        if (mQuestionAction != null) {
                            mQuestionAction.onExamStop();
                            if (mQuestionAction instanceof QuestionBll) {
                                ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                                Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: EXAM_STOP");
                            }
                        }
                        if (mAnswerRankBll != null) {
                            mAnswerRankBll.setNonce(object.optString("nonce"));
                        }

                    }
                    break;
                    case XESCODE.SPEECH_RESULT: {
                        msg += "SPEECH_RESULT";
                        if (notice.equals(lastNotice)) {
                            return;
                        }
                        boolean speechResult = false;
                        if (mQuestionAction != null) {
                            speechResult = mQuestionAction.onSpeechResult(object.toString());
                        }
                        lastNotice = notice;
//                        if (speechResult) {
//                            lastNotice = notice;
//                        }
                    }
                    break;
                    case XESCODE.ENGLISH_H5_COURSEWARE: {
                        LiveVideoConfig.isNewEnglishH5 = false;
                        if (englishH5CoursewareAction != null) {
                            VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                            String play_url = "";
                            String status = object.optString("status", "off");
                            String nonce = object.optString("nonce");
                            String id = "";
                            String courseware_type = "";
                            if ("on".equals(status)) {
                                id = object.getString("id");
                                courseware_type = object.getString("courseware_type");
                                play_url = liveVideoSAConfig.inner.coursewareH5 + mLiveId + "/" + vStuCourseID + "/"
                                        + id + "/" + courseware_type
                                        + "/" + mGetInfo.getStuId();
                                videoQuestionLiveEntity.id = id;
                                videoQuestionLiveEntity.courseware_type = courseware_type;
                                videoQuestionLiveEntity.url = play_url;
                                videoQuestionLiveEntity.nonce = nonce;
                                String isVoice = object.optString("isVoice");
                                videoQuestionLiveEntity.setIsVoice(isVoice);
                                if ("1".equals(isVoice)) {
                                    videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = object
                                            .optString("questiontype");
                                    videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                                }
                                if (mAnswerRankBll != null) {
                                    mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                    mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                                }
                                if (mLiveAutoNoticeBll != null) {
                                    mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                    mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                                }
                                if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                    ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher
                                            (false);
                                }
                            } else {
                                if (mAnswerRankBll != null) {
                                    mAnswerRankBll.setNonce(object.optString("nonce"));
                                }
                                if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                    ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher(true);
                                }
                                if (mTeamPKBll != null) {
                                    mTeamPKBll.showCurrentPkResult();
                                    if (mTeamPKBll != null) {
                                        mTeamPKBll.setNonce(object.optString("nonce", ""));
                                        mTeamPKBll.showCurrentPkResult();
                                        Loger.e("TeamPkBll", "======>showCurrentPkResult: called in " +
                                                "ENGLISH_H5_COURSEWARE");
                                    }
                                }
                            }
                            englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
                        }
                    }
                    break;
                    case XESCODE.H5_START: {
                        if (h5CoursewareAction != null) {
                            String play_url = object.getString("play_url");
                            h5CoursewareAction.onH5Courseware(play_url, "on");
                        }
                    }
                    break;
                    case XESCODE.H5_STOP: {
                        if (h5CoursewareAction != null) {
                            h5CoursewareAction.onH5Courseware("", "off");
                        }
                    }
                    break;
                    case XESCODE.MULTIPLE_H5_COURSEWARE: {
                        LiveVideoConfig.isNewEnglishH5 = true;
                        if (englishH5CoursewareAction != null) {
                            VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                            LiveVideoConfig.isSend = object.optBoolean("open");
                            String status = "";
                            String nonce = object.optString("nonce");
                            StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                            String teamId = studentLiveInfo.getTeamId();
                            String classId = studentLiveInfo.getClassId();
                            EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                            englishH5Entity.setNewEnglishH5(true);
                            try {
                                JSONObject objects = new JSONObject();
                                objects.put("packageId", object.getString("pId"));
                                objects.put("packageSource", object.getString("pSrc"));
                                objects.put("packageAttr", object.getString("pAttr"));
                                objects.put("releasedPageInfos", object.getString("tests"));
                                objects.put("teamId", teamId);
                                objects.put("stuCouId", vStuCourseID);
                                objects.put("stuId", mGetInfo.getStuId());
                                objects.put("classId", classId);
                                mShareDataManager.put(LiveVideoConfig.newEnglishH5, objects.toString(), ShareDataManager.SHAREDATA_USER);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
                        }
                    }
                    break;
                    case XESCODE.RAISE_HAND_SELF: {
                        String from = object.optString("from", "t");
                        msg += ",RAISE_HAND_SELF:from=" + from + ",mode=" + getMode();
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            if (videoChatAction != null) {
                                String status = object.optString("status", "off");
                                int num = object.optInt("num", 0);
                                msg += "RAISE_HAND_SELF:status=" + status + ",num=" + num;
                                videoChatAction.raiseHandStatus(status, num, from);
                            }
                        }
                    }
                    break;
//                    case XESCODE.RAISE_HAND_AGAIN:
//                        if (videoChatAction != null) {
//                            videoChatAction.raisehand("on", true);
//                        }
//                        break;
                    case XESCODE.RAISE_HAND: {
                        String from = object.optString("from", "t");
                        msg += ",RAISE_HAND:from=" + from + ",mode=" + getMode();
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            String status = object.optString("status", "off");
                            voiceChatStatus = status;
                            if (videoChatAction != null) {
                                msg += "RAISE_HAND:status=" + status;
                                videoChatAction.raisehand(status, from, object.optString("nonce"));
                            }
                            if (mRoomAction != null) {
                                mRoomAction.videoStatus(status);
                            }
                        }
                    }
                    break;
                    case XESCODE.REQUEST_ACCEPT: {
                        String from = object.optString("from", "t");
                        msg += ",REQUEST_ACCEPT:from=" + from + ",mode=" + getMode();
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            if (videoChatAction != null) {
                                videoChatAction.requestAccept(from, object.optString("nonce"));
                            }
                        }
                    }
                    break;
                    case XESCODE.START_MICRO: {
                        String from = object.optString("from", "t");
                        msg += ",START_MICRO:from=" + from + ",mode=" + getMode();
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            if (videoChatAction != null) {
                                String room = object.optString("room");
                                String status = object.optString("status", "off");
                                String nonce = object.optString("nonce", "");
                                boolean contain = false;
                                if (status.equals("on")) {
                                    JSONArray students = object.optJSONArray("students");
                                    if (students != null) {
                                        for (int i = 0; i < students.length(); i++) {
                                            if (mGetInfo.getStuId().equals(students.getString(i))) {
                                                contain = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                videoChatAction.startMicro(status, nonce, contain, room, from);
                            }
                        }
                    }
                    break;
                    case XESCODE.ST_MICRO: {
                        String from = object.optString("from", "t");
                        String status = object.optString("status", "off");
                        msg += ",ST_MICRO:from=" + from + ",mode=" + getMode() + ",status=" + status;
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            if (videoChatAction != null) {
                                String room = object.optString("room");
                                videoChatAction.quit(status, room, from);
                            }
                        }
                        break;
                    }
                    case XESCODE.RAISE_HAND_COUNT: {
                        String from = object.optString("from", "t");
                        msg += ",RAISE_HAND_COUNT:from=" + from + ",mode=" + getMode();
                        if ("t".equals(from) && LiveTopic.MODE_CLASS.equals(getMode()) || "f".equals(from) &&
                                LiveTopic.MODE_TRANING.equals(getMode())) {
                            if (videoChatAction != null) {
                                int count = object.optInt("num", 0);
                                videoChatAction.raiseHandCount(count);
                            }
                        }
                        break;
                    }
                    case XESCODE.ROOM_STAR_OPEN: {
                        if (starAction != null) {
                            JSONArray array = object.optJSONArray("data");
                            ArrayList<String> data = new ArrayList<>();
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    data.add(array.optString(i));
                                }
                            }
                            String nonce = object.optString("nonce");
                            String starid = object.optString("starid");
                            starAction.onStarStart(data, starid, "", nonce);
                        }
                        break;
                    }
                    case XESCODE.ROOM_STAR_CLOSE: {
                        if (starAction != null) {
                            String id = object.getString("id");
                            Object answerObj = object.get("answer");
                            ArrayList<String> answer = new ArrayList<>();
                            if (answerObj instanceof JSONArray) {
                                JSONArray array = (JSONArray) answerObj;
                                for (int i = 0; i < array.length(); i++) {
                                    answer.add(array.optString(i));
                                }
                            } else {
                                answer.add("" + answerObj);
                            }
                            String nonce = object.optString("nonce");
                            starAction.onStarStop(id, answer, nonce);
                        }
                        break;
                    }
                    case XESCODE.ROOM_STAR_SEND_T: {
                        if (starAction != null) {
                            JSONArray array = object.optJSONArray("data");
                            ArrayList<String> data = new ArrayList<>();
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    data.add(array.optString(i));
                                }
                            }
                            int index = object.optInt("answer", -1);
                            String answer = "";
                            if (index >= 0 && index < data.size()) {
                                answer = data.get(index);
                            }
                            String starid = object.optString("starid");
                            starAction.onStarStart(data, starid, answer, "");
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_DB_PRAISE: {
                        msg += ",XCR_ROOM_DB_PRAISE";
                        if (englishSpeekAction != null) {
                            int answer = object.getInt("answer");
                            englishSpeekAction.praise(answer);
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_DB_REMIND: {
                        msg += ",XCR_ROOM_DB_REMIND";
                        if (englishSpeekAction != null) {
                            int answer = object.getInt("answer");
                            englishSpeekAction.remind(answer);
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_DB_START: {
                        msg += ",XCR_ROOM_DB_START";
                        if (englishSpeekAction != null) {
                            englishSpeekAction.onDBStart();
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_ROLE_READ: {
                        msg += ",XCR_ROOM_ROLE_READ";
                        Loger.i("RolePlayerDemoTest", "收到老师分组完成的消息" + XESCODE.XCR_ROOM_ROLE_READ);
                        if (rolePlayAction == null && liveLazyBllCreat != null) {
                            rolePlayAction = liveLazyBllCreat.createRolePlayBll();
                        }
                        if (rolePlayAction != null) {
                            Loger.i("RolePlayerDemoTest", "学生去请分组信息");
                            String nonce = object.optString("nonce");
                            rolePlayAction.teacherRead(mLiveId, vStuCourseID, nonce);
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_DB_CLOSE: {
                        msg += ",XCR_ROOM_DB_CLOSE";
                        if (englishSpeekAction != null) {
                            englishSpeekAction.onDBStop();
                        }
                        break;
                    }
                    case XESCODE.LEC_LEARNREPORT: {
                        msg += ",LEC_LEARNREPORT";
                        if (mLecLearnReportAction != null) {
                            mLecLearnReportAction.onLearnReport(mLiveId);
                        }
                        break;
                    }
                    case XESCODE.SPEECH_FEEDBACK: {
                        msg += ",SPEECH_FEEDBACK";
                        if (speechFeedBackAction != null) {
                            String status = object.getString("status");
                            if ("on".equals(status) && LiveTopic.MODE_CLASS.equals(getMode())) {
                                String roomId = object.getString("roomId");
                                speechFeedBackAction.start(roomId);
                            } else {
                                speechFeedBackAction.stop();
                            }
                        }
                        break;
                    }
                    case XESCODE.VOTE_START: {
                        msg += ",VOTE_START";
                        String open = object.optString("open");
                        if (liveVoteAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createLiveVoteAction();
                        }
                        if (liveVoteAction != null) {
                            String choiceId = object.getString("choiceId");
                            int choiceType = object.optInt("choiceType");
                            int choiceNum = object.optInt("choiceNum");
                            LiveTopic.VoteEntity voteEntity = new LiveTopic.VoteEntity();
                            voteEntity.setChoiceNum(choiceNum);
                            voteEntity.setChoiceType(choiceType);
                            voteEntity.setChoiceId(choiceId);
                            voteEntity.setNonce(object.optString("nonce"));
                            if ("on".equals(open)) {
                                liveVoteAction.voteStart(voteEntity);
                            } else if ("off".equals(open)) {
                                ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
                                JSONArray result = object.getJSONArray("result");
                                int total = 0;
                                for (int i = 0; i < result.length(); i++) {
                                    LiveTopic.VoteResult voteResult = new LiveTopic.VoteResult();
                                    int people = result.getInt(i);
//                                    people += 10 * new Random().nextInt(22);
                                    voteResult.setPople(people);
                                    total += people;
                                    voteResults.add(voteResult);
                                }
                                voteEntity.setTotal(total);
                                liveVoteAction.voteStop(voteEntity);
                            }
                        }
                        break;
                    }
                    case XESCODE.VOTE_START_JOIN: {
                        msg += ",VOTE_START_JOIN";
                        String open = object.optString("open");
                        if (liveVoteAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createLiveVoteAction();
                        }
                        if (liveVoteAction != null) {
                            String choiceId = object.getString("choiceId");
                            int choiceType = object.optInt("choiceType");
                            int choiceNum = object.optInt("choiceNum");
                            LiveTopic.VoteEntity voteEntity = new LiveTopic.VoteEntity();
                            voteEntity.setChoiceNum(choiceNum);
                            voteEntity.setChoiceType(choiceType);
                            voteEntity.setChoiceId(choiceId);
                            int answer = object.getInt("answer");
                            liveVoteAction.voteJoin(voteEntity, answer);
                        }
                        break;
                    }
                    case XESCODE.RANK_TEA_MESSAGE:
                        try {
                            List<RankUserEntity> lst = JSON.parseArray(object.optString("stuInfo"), RankUserEntity
                                    .class);
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.showRankList(lst, -1);
                            }
                        } catch (Exception e) {
                            Loger.i("=====notice " + e.getMessage());
                        }
                    default:
                        msg += "default";
                        break;
                    case XESCODE.XCR_ROOM_AGREE_OPEN: {
                        msg += ",XCR_ROOM_AGREE_OPEN";
                        if (mPraiseListAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createPraiseListAction();
                        }
                        if (mPraiseListAction != null) {
                            String open = object.optString("open");
                            int zanType = object.optInt("zanType");
                            String nonce = object.optString("nonce");
                            if ("on".equals(open)) {
                                mPraiseListAction.onReceivePraiseList(zanType, nonce);
                                switch (zanType) {
                                    case PraiseListPager.PRAISE_LIST_TYPE_HONOR:
                                        getHonorList(0);
                                        break;
                                    case PraiseListPager.PRAISE_LIST_TYPE_PROGRESS:
                                        getProgressList(0);
                                        break;
                                    case PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP:
                                        getThumbsUpList();
                                        break;
                                    default:
                                        break;
                                }
                            } else if ("off".equals(open)) {
                                if (mPraiseListAction != null) {
                                    mPraiseListAction.closePraiseList();
                                }
                            }
                        }
                        break;
                    }
                    case XESCODE.XCR_ROOM_AGREE_SEND_T: {
                        msg += ",XCR_ROOM_AGREE_SEND_T";
                        if (mPraiseListAction == null && liveLazyBllCreat != null) {
                            liveLazyBllCreat.createPraiseListAction();
                        }
                        if (mPraiseListAction != null) {
                            if (mPraiseListAction.getThumbsUpProbability() == 0) {
                                getThumbsUpProbability();
                            }
                            JSONArray agreeForms = object.optJSONArray("agreeFroms");
                            boolean isTeacher = object.optBoolean("isTeacher");
                            Loger.i(TAG, "agreeForms=" + agreeForms.toString());
                            Loger.i(TAG, "isTeacher=" + isTeacher);
                            if (isTeacher) {
                                if (mPraiseListAction != null && agreeForms.length() != 0) {
                                    mPraiseListAction.showPraiseScroll(mGetInfo.getStuName(), agreeForms.getString(0));
                                }
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 0; i < agreeForms.length(); i++) {
                                    String stuName = agreeForms.getString(i);
                                    Loger.i(TAG, "stuName=" + stuName);
                                    list.add(stuName);
                                }
                                if (mPraiseListAction != null && list.size() != 0) {
                                    mPraiseListAction.receiveThumbsUpNotice(list);
                                }
                            }
                        }

                        break;
                    }
                    case XESCODE.LEC_ADVERT: {
                        if (lecAdvertAction != null) {
                            LecAdvertEntity entity = new LecAdvertEntity();
                            entity.course_id = object.optString("course_id");
                            entity.id = object.optString("id");
                            entity.nonce = object.optString("nonce");
                            lecAdvertAction.start(entity);
                        }
                        break;
                    }
                    case XESCODE.TEACHER_PRAISE: {
                        if (mTeacherPraiseBll1 != null) {
                            mTeacherPraiseBll1.showTeacherPraise();
                            String nonce = object.optString("nonce", "");
                            TeamPkLog.receiveVoicePraise(LiveBll.this, nonce);
                        }
                        break;
                    }

                    case XESCODE.TEAM_PK_TEAM_SELECT: {

                        Loger.e("LiveBll", "=====>: notice teampk  show teamselect:" + mtype);

                        if (mTeamPKBll != null) {
                            String open = object.optString("open");
                            String nonce = object.optString("nonce", "");
                            if (open.equals("1")) {
                                mTeamPKBll.startTeamSelect();
                                TeamPkLog.receiveCreateTeam(LiveBll.this, nonce, true);
                            } else if (open.equals("0")) {
                                mTeamPKBll.stopTeamSelect();
                                TeamPkLog.receiveCreateTeam(LiveBll.this, nonce, false);
                            }
                        }
                        break;
                    }
                    case XESCODE.TEAM_PK_SELECT_PKADVERSARY: {
                        String open = object.optString("open");
                        if (mTeamPKBll != null) {
                            String nonce = object.optString("nonce", "");
                            if (open.equals("1")) {
                                mTeamPKBll.startSelectAdversary();
                                TeamPkLog.receiveMatchOpponent(LiveBll.this, nonce, true);
                                Loger.e("LiveBll", "====>onNotice startSelectAdversary:");
                            } else if (open.equals("0")) {
                                mTeamPKBll.stopSelectAdversary();
                                TeamPkLog.receiveMatchOpponent(LiveBll.this, nonce, false);
                            }
                        }
                        break;
                    }
                    case XESCODE.TEAM_PK_PUBLIC_PK_RESULT:
                        if (mTeamPKBll != null) {
                            String nonce = object.optString("nonce", "");
                            TeamPkLog.receivePkResult(LiveBll.this, nonce, true);
                            mTeamPKBll.showPkResult();
                        }
                        break;
                    case XESCODE.TEAM_PK_PUBLIC_CONTRIBUTION_STAR: {
                        if (mTeamPKBll != null) {
                            String nonce = object.optString("nonce", "");
                            TeamPkLog.receiveClassBoxInfo(LiveBll.this, nonce, true);
                            mTeamPKBll.showClassChest();
                        }
                        break;
                    }
                    case XESCODE.TEAM_PK_EXIT_PK_RESULT: {
                        if (mTeamPKBll != null) {
                            mTeamPKBll.closeCurrentPkResult();
                        }
                        break;
                    }

//                    case XESCODE.LEC_ADVERT: {
//                        if (lecAdvertAction != null) {
//                            LecAdvertEntity entity = new LecAdvertEntity();
//                            entity.course_id = object.optString("course_id");
//                            entity.id = object.optString("id");
//                            entity.nonce = object.optString("nonce");
//                            lecAdvertAction.start(entity);
//                        }
//                        break;
//                    }
                }
                mLogtf.i("onNotice:msg=" + msg);
                // Loger.d(TAG, "onNotice:msg=" + msg);
            } catch (JSONException e) {
                // Loger.e(TAG, "onNotice", e);
                mLogtf.e("onNotice:" + notice, e);
                MobAgent.httpResponseParserError(TAG, "onNotice", e.getMessage());
            }
        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            Loger.e("LiveBll", "=====> onMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" + text);

            if (mRoomAction != null) {
                mRoomAction.onMessage(target, sender, login, hostname, text, "");
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {
            Loger.e("LiveBll", "=====> onPrivateMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" + message);
            if (!"T".equals(message) && haveTeam) {
                StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                String teamId = studentLiveInfo.getTeamId();
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    int type = jsonObject.getInt("type");
                    if (type == XESCODE.TEACHER_MESSAGE) {
                        String to = jsonObject.optString("to", teamId);
                        if (!isSeniorOfHighSchool() && !teamId.equals(to)) {
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (mRoomAction != null) {
                mRoomAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            mLogtf.d("onDisconnect:isQuitting=" + isQuitting);
            if (mRoomAction != null) {
                mRoomAction.onDisconnect();
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            if (mRoomAction != null) {
                mRoomAction.onConnect();
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            String s = "onUserList:channel=" + channel + ",users=" + users.length;
            boolean haveMainTeacher = false;//主讲老师
            boolean haveCounteacher = false;//辅导老师
            ArrayList<User> arrayList = new ArrayList<>();
            for (int i = 0; i < users.length; i++) {
                User user = users[i];
                String _nick = user.getNick();
                if (_nick != null && _nick.length() > 2) {
                    if (_nick.startsWith(TEACHER_PREFIX)) {
                        s += ",mainTeacher=" + _nick;
                        haveMainTeacher = true;
                        synchronized (mIRCcallback) {
                            mMainTeacher = new Teacher(_nick);
                            mMainTeacherStr = _nick;
                        }
                        if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }
                    } else if (_nick.startsWith(COUNTTEACHER_PREFIX)) {
                        mCounTeacherStr = _nick;
                        haveCounteacher = true;
                        mCounteacher.isLeave = false;
                        s += ",counteacher=" + _nick;
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }
                    } else {
                        boolean isMyTeam = isMyTeam(user.getNick());
                        if (isMyTeam || isSeniorOfHighSchool()) {
                            arrayList.add(user);
                        }
                    }
                } else {
                    s += ",else=" + _nick;
                }
            }
            if (!haveCounteacher) {
                mCounteacher.isLeave = true;
            }
            if (arrayList.isEmpty()) {// 学生人数为空
                s += ",arrayList=isSpace";
            }
            s += ",haveMainTeacher=" + haveMainTeacher;
            if (mLiveType == LIVE_TYPE_LIVE) {
                s += ",haveCounteacher=" + haveCounteacher;
            }
            mLogtf.d(s);
            if (mRoomAction != null) {
                User[] users2 = new User[arrayList.size()];
                arrayList.toArray(users2);
                mRoomAction.onUserList(channel, users2);
            }
        }

        /**是不是自己组的人*/
        private boolean isMyTeam(String sender) {
            boolean isMyTeam = true;
            ArrayList<String> teamStuIds = mGetInfo.getTeamStuIds();
            if (mLiveType == LiveBll.LIVE_TYPE_LIVE && !teamStuIds.isEmpty()) {
                isMyTeam = false;
                String split[] = sender.split("_");
                if (split.length > 4) {
                    String uid = split[3];
                    for (int j = 0; j < teamStuIds.size(); j++) {
                        String string = teamStuIds.get(j);
                        if (("" + string).equals(uid)) {
                            isMyTeam = true;
                            break;
                        }
                    }
                }
            }
            return isMyTeam;
        }

        public void onJoin(String target, String sender, String login, String hostname) {
            Loger.d(TAG, "onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
            if (sender.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    mMainTeacher = new Teacher(sender);
                    mMainTeacherStr = sender;
                }
                mLogtf.d("onJoin:mainTeacher:target=" + target + ",mode=" + mLiveTopic.getMode());
                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }
            } else if (sender.startsWith(COUNTTEACHER_PREFIX)) {
                mCounTeacherStr = sender;
                mCounteacher.isLeave = false;
                mLogtf.d("onJoin:Counteacher:target=" + target + ",mode=" + mLiveTopic.getMode());
                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }
            } else {
                if (mRoomAction != null) {
//                    if (sender.startsWith(LiveBll.TEACHER_PREFIX) || sender.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                    boolean isMyTeam = isMyTeam(sender);
                    if (isMyTeam || isSeniorOfHighSchool()) {
                        mRoomAction.onJoin(target, sender, login, hostname);
                    }
                }
            }
        }

        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
            Loger.d(TAG, "onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                    + sourceHostname + ",reason=" + reason);
            if (sourceNick.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    mMainTeacher = null;
                }
                mLogtf.d("onQuit:mainTeacher quit");
                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }
            } else if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                mCounteacher.isLeave = true;
                mLogtf.d("onQuit:Counteacher quit");
                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }
            } else {
                if (mRoomAction != null) {
//                    if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll
// .COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                    boolean isMyTeam = isMyTeam(sourceNick);
                    if (isMyTeam || isSeniorOfHighSchool()) {
                        mRoomAction.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
                    }
                }
            }
        }

        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                           String recipientNick, String reason) {
            mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
                    + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
            if (mRoomAction != null) {
                mRoomAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
            }
        }

        public void onUnknown(String line) {
            if (line.contains("BLOCK")) {//发送了敏感词
                if (mLiveAutoNoticeBll != null) {
                    if (System.currentTimeMillis() - blockTime > 2 * 60 * 1000) {
                        blockTime = System.currentTimeMillis();
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                mLiveAutoNoticeBll.showNotice(mGetInfo.getTeacherName(), mGetInfo.getTeacherIMG());
                            }
                        }, 10000);
                    }

                }
            }
        }
    };


    /**
     * 是否是 高三 理科直播 （展示不同聊天 内容：高三理科 以 班级为单位展示,）
     *
     * @return
     */
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
        if (mIRCMessage != null && mIRCMessage.isConnected()) {
            if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                isPresent = mMainTeacher != null;
            } else {
                isPresent = !mCounteacher.isLeave;
            }
        }
        return isPresent;
    }

    public AnswerRankBll getAnswerRankBll() {
        return mAnswerRankBll;
    }

    public PraiseListAction getPraiseListAction() {
        return mPraiseListAction;
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    public LiveGetInfo getGetInfo() {
        return mGetInfo;
    }

    /**
     * 请求房间状态成功
     *
     * @param getInfo
     */
    private void onGetInfoSuccess(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
        if (this.mGetInfo == null) {
            onLiveFailure("服务器异常", null);
            return;
        }
        if (mGetInfo.getIsArts() == 1) {
            appID = UmsConstants.ARTS_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        if (mLiveType == LIVE_TYPE_LIVE) {
            if (mGetInfo.getIsArts() == 1) {
                liveArtsHttpManager = new LiveArtsHttpManager(mHttpManager);
            } else {
                liveScienceHttpManager = new LiveScienceHttpManager(mHttpManager);
            }
        }
        //判断是否有智能私信功能
        Loger.i("LiveAutoNoticeBll", "isshowNotice:" + mGetInfo.getIsShowCounselorWhisper());
        sysTimeOffset = (long) mGetInfo.getNowTime() - System.currentTimeMillis() / 1000;
        if (!mGetInfo.getIsShowMarkPoint().equals("1")) {
            if (mLiveRemarkBll != null) {
                mLiveRemarkBll.hideBtMark();
            }
        }
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLiveAutoNoticeBll.showNotice("hello","");
            }
        },3000);*/
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        if (mGetInfo.getStudentLiveInfo() != null
                && mGetInfo.getIs_show_ranks().equals("1")) {
            mAnswerRankBll = liveLazyBllCreat.createAnswerRankBll();
            mAnswerRankBll.setLiveHttpManager(mHttpManager);
            if (mQuestionAction instanceof QuestionBll) {
                ((QuestionBll) mQuestionAction).setAnswerRankBll(mAnswerRankBll);
            }
            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setAnswerRankBll(mAnswerRankBll);
            }
            mAnswerRankBll.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
            mAnswerRankBll.setTeamId(mGetInfo.getStudentLiveInfo().getTeamId());
            mAnswerRankBll.setIsShow(mGetInfo.getIs_show_ranks());
        }
        mGetInfo.setMode(mLiveTopic.getMode());
        long enterTime = 0;
        try {
            enterTime = enterTime();
        } catch (Exception e) {
        }
        if (mGetInfo.getStat() == 1) {
            if (mVideoAction != null) {
                mVideoAction.onTeacherNotPresent(true);
            }
            mLogtf.d("onGetInfoSuccess:onTeacherNotPresent");
        }
        mCounteacher = new Teacher(mGetInfo.getTeacherName());
        String s = "onGetInfoSuccess:enterTime=" + enterTime + ",stat=" + mGetInfo.getStat();
        if (mVideoAction != null) {
            mVideoAction.onLiveInit(mGetInfo);
        }
        NewTalkConfEntity talkConfEntity = new NewTalkConfEntity();
        talkConfEntity.setHost(mGetInfo.getTalkHost());
        talkConfEntity.setPort(mGetInfo.getTalkPort());
        talkConfEntity.setPwd(mGetInfo.getTalkPwd());
        List<NewTalkConfEntity> newTalkConf = new ArrayList<NewTalkConfEntity>();
        newTalkConf.add(talkConfEntity);
        if (mGetInfo.getNewTalkConf() != null) {
            newTalkConf.addAll(mGetInfo.getNewTalkConf());
        }
        String channel = "";
        if (mLiveType == LIVE_TYPE_TUTORIAL) {
            channel = "1" + ROOM_MIDDLE + mGetInfo.getId();
        } else if (mLiveType == LIVE_TYPE_LECTURE) {
            if (StringUtils.isEmpty(mGetInfo.getRoomId())) {
                channel = "2" + ROOM_MIDDLE + mGetInfo.getId();
            } else {
                channel = "2" + ROOM_MIDDLE + mGetInfo.getId() + "-" + mGetInfo.getRoomId();
            }
        } else {
            StudentLiveInfoEntity studentLiveInfo = this.mGetInfo.getStudentLiveInfo();
            mHttpManager.addBodyParam("teamId", studentLiveInfo.getTeamId());
            mHttpManager.addBodyParam("classId", "" + studentLiveInfo.getClassId());
            if (!StringUtils.isEmpty(studentLiveInfo.getCourseId())) {
                courseId = studentLiveInfo.getCourseId();
                mHttpManager.addBodyParam("courseId", courseId);
            }
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
            channel = mGetInfo.getId() + "-" + studentLiveInfo.getClassId();
        }
        s += ",liveType=" + mLiveType + ",channel=" + channel;
        String nickname = "s_" + mGetInfo.getLiveType() + "_"
                + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
        mIRCMessage = new IRCMessage(netWorkType, channel, mGetInfo.getStuName(), nickname);
        mIRCMessage.setNewTalkConf(newTalkConf);
        IRCTalkConf ircTalkConf = new IRCTalkConf(getInfo, mLiveType, mHttpManager, getInfo.getNewTalkConfHosts());
        mIRCMessage.setIrcTalkConf(ircTalkConf);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        s += ",newTalkConf=" + newTalkConf.size();
        // Loger.d(TAG, s);
        mLogtf.d(s);
        if (mGetInfo.getStudentLiveInfo() != null) {
            if (mGetInfo.getStudentLiveInfo().getEvaluateStatus() == 1) {
                mLogtf.d("onGetInfoSuccess:getLearnReport");
                getLearnReport(1, 1000);
            }
            mLogtf.d("onGetInfoSuccess:getSignStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus());
            //  根据 借口返回状态  判断是否显示签到
            handleUserSign();
        }
        mLogtf.d("onGetInfoSuccess:mode=" + mLiveTopic.getMode());
        if (isTeamPkRoom(getInfo)) {

            if (mQuestionAction instanceof QuestionBll) {
                ((QuestionBll) mQuestionAction).setTeamPkAllowed(true);
            }
            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setTeamPkAllowed(true);
            }

            mTeamPKBll = liveLazyBllCreat.createTeamPkBll();
            mTeamPKBll.setHttpManager(mHttpManager);
            mTeamPKBll.setLiveBll(this);
            mTeamPKBll.setRoomInitInfo(mGetInfo);
            mTeamPKBll.attachToRootView();
            isAllowTeamPk = true;
        } else {
            isAllowTeamPk = false;
        }
        liveGetPlayServerFirst();
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

    /**
     * 是否是Pk 直播间
     *
     * @param getInfo
     * @return
     */
    private boolean isTeamPkRoom(LiveGetInfo getInfo) {
        boolean result = false;
        if (getInfo.getIsAllowTeamPk() != null) {
            result = getInfo.getIsAllowTeamPk().equals("1");
        }
        return result;
    }

    public LiveAutoNoticeBll getLiveAutoNoticeBll() {
        return mLiveAutoNoticeBll;
    }

    public LiveRemarkBll getLiveRemarkBll() {
        return mLiveRemarkBll;
    }

    /**
     * 进入直播间时间
     *
     * @return
     */
    private long enterTime() {
        String liveTime = mGetInfo.getLiveTime();
        if ("".endsWith(liveTime)) {
            return 0;
        }
        {
            String startTime = liveTime.split(" ")[0];// 开始时间
            String[] times = startTime.split(":");
            String startTimeHour = times[0];
            String startTimeMinute = times[1];
            String msg = "enterTime:startTime=" + startTime + ",Hour=" + startTimeHour + ",Minute=" + startTimeMinute;
            Calendar calendar = Calendar.getInstance();
            long milliseconds1 = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeHour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeMinute));
            long milliseconds2 = calendar.getTimeInMillis();
            msg += ",time=" + (milliseconds1 - milliseconds2) + "," + ((milliseconds1 - milliseconds2) / 60000);
            mLogtf.d(msg);
            XesMobAgent.enterLiveRoom(0, (milliseconds1 - milliseconds2) / 60000);
        }
        long milliseconds1, milliseconds2;
        {
            String endTime = liveTime.split(" ")[1];// 开始时间
            String[] times = endTime.split(":");
            String endTimeHour = times[0];
            String endTimeMinute = times[1];
            String msg = "enterTime:endTime=" + endTime + ",Hour=" + endTimeHour + ",Minute=" + endTimeMinute;
            Calendar calendar = Calendar.getInstance();
            milliseconds1 = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeHour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeMinute));
            milliseconds2 = calendar.getTimeInMillis();
            msg += ",time=" + (milliseconds1 - milliseconds2) + "," + ((milliseconds1 - milliseconds2) / 60000);
            mLogtf.d(msg);
            XesMobAgent.enterLiveRoom(1, (milliseconds1 - milliseconds2) / 60000);
        }
        return (milliseconds1 - milliseconds2) / 60000;
    }

    /**
     * 获取学习报告
     */
    private synchronized void getLearnReport(final int from, final long delayTime) {
        XesMobAgent.liveLearnReport("request:" + from);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getLearnReport:enstuId=" + enstuId + ",liveType=" + mLiveType + ",liveId=" + mLiveId + "," +
                "delayTime=" + delayTime);
        mHttpManager.getLearnReport(enstuId, mLiveId, mLiveType, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LearnReportEntity learnReportEntity = mHttpResponseParser.parseLearnReport(responseEntity);
                if (learnReportEntity != null) {
                    learnReportEntity.getStu().setStuName(mGetInfo.getStuName());
                    learnReportEntity.getStu().setTeacherName(mGetInfo.getTeacherName());
                    learnReportEntity.getStu().setTeacherIMG(mGetInfo.getTeacherIMG());
                    if (mLearnReportAction != null) {
                        mLearnReportAction.onLearnReport(learnReportEntity);
                    }
                }
                XesMobAgent.liveLearnReport("request-ok:" + from);
                mLogtf.d("getLearnReport:onPmSuccess:learnReportEntity=" + (learnReportEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                XesMobAgent.liveLearnReport("request-fail:" + from);
                mLogtf.d("getLearnReport:onPmFailure=" + error + ",msg=" + msg + ",delayTime=" + delayTime);
                if (delayTime < 15000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            getLearnReport(3, delayTime + 5000);
                        }
                    }, delayTime);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                XesMobAgent.liveLearnReport("request-error:" + from);
                mLogtf.d("getLearnReport:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
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
     * 签名
     */
    public synchronized void userSign(final HttpCallBack requestCallBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.userSign(enstuId, mLiveId, classId, mGetInfo.getTeacherId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mGetInfo.getStudentLiveInfo().setSignStatus(2);
                requestCallBack.onPmSuccess(responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                requestCallBack.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                requestCallBack.onPmError(responseEntity);
            }
        });
    }

    /**
     * 第一次调度，不判断老师状态
     */
    public void liveGetPlayServerFirst() {
        liveGetPlayServer(mLiveTopic.getMode(), false);
    }

    /**
     * 调度，使用LiveTopic的mode
     *
     * @param modechange
     */
    public void liveGetPlayServer(boolean modechange) {
        new Thread() {
            @Override
            public void run() {
                boolean isPresent = isPresent(mLiveTopic.getMode());
                mLogtf.d("liveGetPlayServer:isPresent=" + isPresent);
                if (!isPresent && mVideoAction != null) {
                    mVideoAction.onTeacherNotPresent(true);
                }
            }
        }.start();
        liveGetPlayServer(mLiveTopic.getMode(), modechange);
    }

    private long lastGetPlayServer;

    private void liveGetPlayServer(final String mode, final boolean modechange) {
        if (mLiveType == LIVE_TYPE_LIVE) {
            if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mode)) {
                mLogtf.d("liveGetPlayServer:isExpe");
                return;
            }
        }
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            liveGetPlayServerError = true;
            return;
        }
        liveGetPlayServerError = false;
        final long before = System.currentTimeMillis();
        // http://gslb.xueersi.com/xueersi_gslb/live?cmd=live_get_playserver&userid=000041&username=xxxxxx
        // &channelname=88&remote_ip=116.76.97.244
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            String channelname = "";
            if (mLiveType != 3) {
                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
                        + mGetInfo.getTeacherId();
            } else {
                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId();
            }
            mGetInfo.setChannelname(channelname);
        } else {
            mGetInfo.setChannelname(CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
                    + mGetInfo.getTeacherId());
        }
        if (totalFrameStat != null) {
            totalFrameStat.setChannelname(mGetInfo.getChannelname());
        }
        final String serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname();
        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        mLogtf.d("liveGetPlayServer:modeTeacher=" + getModeTeacher());
        final StringBuilder ipsb = new StringBuilder();
        mGetPlayServerCancle = mHttpManager.liveGetPlayServer(ipsb, serverurl, new CommonRequestCallBack<String>() {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLogtf.d("liveGetPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback + "," + ipsb);
                long time = System.currentTimeMillis() - before;
                if (ex instanceof HttpException) {
                    HttpException error = (HttpException) ex;
                    if (error.getCode() >= 300) {
                        mLogtf.d("liveGetPlayServer:onError:code=" + error.getCode() + ",time=" + time);
                        totalFrameStat.liveGetPlayServer(time, 3, "", ipsb, serverurl);
                        if (time < 15000) {
                            if (mVideoAction != null && mLiveTopic != null) {
                                mVideoAction.onLiveStart(null, mLiveTopic, modechange);
                            }
                            mHandler.removeCallbacks(mStatisticsRun);
                            postDelayedIfNotFinish(mStatisticsRun, 300000);
                            return;
                        }
                    }
                } else {
                    if (ex instanceof UnknownHostException) {
                        totalFrameStat.liveGetPlayServer(time, 1, "", ipsb, serverurl);
                    } else {
                        if (ex instanceof SocketTimeoutException) {
                            totalFrameStat.liveGetPlayServer(time, 2, "", ipsb, serverurl);
                        }
                    }
                    mLogtf.e("liveGetPlayServer:onError:isOnCallback=" + isOnCallback, ex);
                }
                long now = System.currentTimeMillis();
                if (now - lastGetPlayServer < 5000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetPlayServer:onError retry1");
                            liveGetPlayServer(modechange);
                        }
                    }, 1000);
                } else {
                    lastGetPlayServer = now;
                    onLiveFailure("直播调度失败", new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetPlayServer:onError retry2");
                            liveGetPlayServer(modechange);
                        }
                    });
                }
            }

            @Override
            public void onSuccess(String result) {
//                Loger.i(TAG, "liveGetPlayServer:onSuccess:result=" + result);
                String s = "liveGetPlayServer:onSuccess";
                try {
                    JSONObject object = new JSONObject(result);
                    PlayServerEntity server = mHttpResponseParser.parsePlayerServer(object);
                    if (server != null) {
                        if (totalFrameStat != null) {
                            long time = System.currentTimeMillis() - before;
                            totalFrameStat.liveGetPlayServer(time, 0, server.getCipdispatch(), ipsb, serverurl);
                        }
                        s += ",mode=" + mode + ",server=" + server.getAppname() + ",rtmpkey=" + server.getRtmpkey();
                        if (LiveTopic.MODE_CLASS.equals(mode)) {
                            mGetInfo.setSkeyPlayT(server.getRtmpkey());
                        } else {
                            mGetInfo.setSkeyPlayF(server.getRtmpkey());
                        }
                        mServer = server;
                        if (mVideoAction != null && mLiveTopic != null) {
                            mVideoAction.onLiveStart(server, mLiveTopic, modechange);
                        }
                        mHandler.removeCallbacks(mStatisticsRun);
                        postDelayedIfNotFinish(mStatisticsRun, 5 * 60 * 1000);
                    } else {
                        s += ",server=null";
                        onLiveFailure("直播调度失败", new Runnable() {

                            @Override
                            public void run() {
                                liveGetPlayServer(modechange);
                            }
                        });
                    }
                    mLogtf.d(s);
                } catch (JSONException e) {
                    MobAgent.httpResponseParserError(TAG, "liveGetPlayServer", result + "," + e.getMessage());
                    // Loger.e(TAG, "liveGetPlayServer", e);
                    mLogtf.e("liveGetPlayServer", e);
                    onLiveFailure("直播调度失败", new Runnable() {

                        @Override
                        public void run() {
                            liveGetPlayServer(modechange);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
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
        if (mTeamPKBll != null) {
            mTeamPKBll.onStop();
        }
    }

    /**
     * activity resume
     */
    public void onResume() {
        if (mTeamPKBll != null) {
            mTeamPKBll.onReusme();
        }
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
        mPraiseOrEncourageAction = null;
        readPackageBll = null;
        mVideoAction = null;
        mRoomAction = null;
        mLearnReportAction = null;
        h5CoursewareAction = null;
        englishH5CoursewareAction = null;
        videoChatAction = null;
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
        if (mPraiseListAction != null) {
            mPraiseListAction.destory();
            mPraiseListAction = null;
        }

        if (mTeamPKBll != null) {
            mTeamPKBll.onDestroy();
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

    private SimpleVPlayerListener mVideoListener = new SimpleVPlayerListener() {
        long bufferStartTime;
        boolean isOpenSuccess = false;

        @Override
        public void onOpenStart() {
            isOpenSuccess = false;
            mOpenCount.set(mOpenCount.get() + 1);
            openStartTime = System.currentTimeMillis();
            mLogtf.d("onOpenStart");

        }

        @Override
        public void onOpenSuccess() {
            isOpenSuccess = true;
            mHandler.removeCallbacks(mUserOnlineCall);
            postDelayedIfNotFinish(mUserOnlineCall, mHbTime * 1000);
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenSuccess:openTime=" + openTime);
            streamReport(MegId.MEGID_12102, mGetInfo.getChannelname(), openTime);
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            if (isOpenSuccess) {
                streamReport(MegId.MEGID_12103, mGetInfo.getChannelname(), -1);
            }
            mFailCount.set(mFailCount.get() + 1);
            long openTime = System.currentTimeMillis() - openStartTime;
            mLogtf.d("onOpenFailed:openTime=" + openTime + ",failCount=" + mFailCount.get() + "," + getModeTeacher()
                    + ",NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
            String mode = mLiveTopic.getMode();
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                synchronized (mIRCcallback) {
                    if (mMainTeacher == null) {
                        mFailMainTeacherCount.set(mFailMainTeacherCount.get() + 1);
                    }
                }
            } else {
                if (mCounteacher.isLeave) {
                    mFailCounTeacherCount.set(mFailCounTeacherCount.get() + 1);
                }
            }
            mHandler.removeCallbacks(mUserOnlineCall);
        }

        @Override
        public void onBufferStart() {
            bufferStartTime = System.currentTimeMillis();
            mBufferCount.set(mBufferCount.get() + 1);
            mLogtf.d("onBufferStart:bufferCount=" + mBufferCount.get() + "," + getModeTeacher() + ",NetWorkState=" +
                    NetWorkHelper
                            .getNetWorkState(mContext));
        }

        @Override
        public void onBufferComplete() {
            long bufferTime = System.currentTimeMillis() - bufferStartTime;
            mLogtf.d("onBufferComplete:bufferTime=" + bufferTime);
        }

        @Override
        public void onPlaybackComplete() {
            mCompleteCount.set(mCompleteCount.get() + 1);
            mLogtf.d("onPlaybackComplete:completeCount=" + mCompleteCount.get() + "," + getModeTeacher() + "," +
                    "NetWorkState=" +
                    NetWorkHelper.getNetWorkState(mContext));
            String mode = mLiveTopic.getMode();
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                synchronized (mIRCcallback) {
                    if (mMainTeacher == null) {
                        mCompleteMainTeacherCount.set(mCompleteMainTeacherCount.get() + 1);
                    }
                }
            } else {
                if (mCounteacher.isLeave) {
                    mCompleteCounTeacherCount.set(mCompleteCounTeacherCount.get() + 1);
                }
            }
        }
    };

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
                // Loger.e(TAG, "understand", e);
                mLogtf.e("understand", e);
            }
        } else {
            mLogtf.d("understand mMainTeacherStr=null");
        }
    }

    public void praiseTeacher(String ftype, String educationStage, final HttpCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
        mHttpManager.praiseTeacher(mLiveType, enstuId, mLiveId, teacherId, ftype, educationStage, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("praiseTeacher:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                if (responseEntity.getJsonObject() instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        sendFlowerMessage(jsonObject.getInt("type"));
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

    public void setStuStarCount(final long reTryTime, final String starId, final AbstractBusinessDataCallBack
            callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.setStuStarCount(mLiveType, enstuId, mLiveId, starId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                callBack.onDataSucess();
                mLogtf.d("setStuStarCount:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(1, msg);
                mLogtf.d("setStuStarCount:onPmFailure:msg=" + msg);
                postDelayedIfNotFinish(new Runnable() {
                    @Override
                    public void run() {
                        setStuStarCount(reTryTime + 1000, starId, callBack);
                    }
                }, reTryTime);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(2, responseEntity.getErrorMsg());
                mLogtf.d("setStuStarCount:onPmFailure:responseEntity=" + responseEntity.getErrorMsg());
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
            // Loger.e(TAG, "understand", e);
            mLogtf.e("onRollCallSuccess", e);
        }
    }

    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    public boolean isConnected() {
        if (mIRCMessage == null) {
            return false;
        }
        return mIRCMessage.isConnected();
    }

    public boolean isHaveTeam() {
        return haveTeam;
    }

    /**
     * 是否开启聊天
     */
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

    /**
     * 发生聊天消息
     */
    public boolean sendMessage(String msg, String name) {
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
                if (starAction != null) {
                    starAction.onSendMsg(msg);
                }
            } catch (Exception e) {
                // Loger.e(TAG, "understand", e);
                UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                mLogtf.e("sendMessage", e);
            }
            return true;
        }
    }

    /**
     * 发送上墙信号聊天消息
     */
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
    public boolean isOpenbarrage() {
        return mLiveTopic.getMainRoomstatus().isOpenbarrage();
    }

    /**
     * 发生献花消息
     */
    public void sendFlowerMessage(int ftype) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.FLOWERS);
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("ftype", ftype);
            mIRCMessage.sendMessage(jsonObject.toString());
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // Loger.e(TAG, "understand", e);
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
            // Loger.e(TAG, "understand", e);
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
            // Loger.e(TAG, "understand", e);
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
            // Loger.e(TAG, "understand", e);
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
            // Loger.e(TAG, "understand", e);
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
            // Loger.e(TAG, "understand", e);
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
     * 得到老师名字
     */
    public String getModeTeacher() {
        String mainnick = "null";
        synchronized (mIRCcallback) {
            if (mMainTeacher != null) {
                mainnick = mMainTeacher.get_nick();
            }
        }
        if (mCounteacher == null) {
            return "mode=" + getMode() + ",mainnick=" + mainnick + ",coun=null";
        } else {
            return "mode=" + getMode() + ",mainnick=" + mainnick + ",coun.isLeave=" + mCounteacher.isLeave;
        }
    }

    /**
     * 得到当前模式
     */
    public String getMode() {
        String mode;
        if (mLiveType == LIVE_TYPE_LIVE) {
            if (mLiveTopic == null) {
                mode = LiveTopic.MODE_CLASS;
            } else {
                mode = mLiveTopic.getMode();
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

    public void sendSpeechEvalResult2(final String id, final String stuAnswer, final OnSpeechEval onSpeechEval) {
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

    public void getStuGoldCount() {
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                String liveid = mGetInfo.getId();
                String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
                mHttpManager.getStuGoldCount(enstuId, liveid, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        Loger.i(TAG, "getStuGoldCount:onPmSuccess=" + responseEntity.getJsonObject());
                        if (starAction != null) {
                            StarAndGoldEntity starAndGoldEntity = mHttpResponseParser.parseStuGoldCount(responseEntity);
                            mGetInfo.setGoldCount(starAndGoldEntity.getGoldCount());
                            mGetInfo.setStarCount(starAndGoldEntity.getStarCount());
                            starAction.onGetStar(starAndGoldEntity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        Loger.i(TAG, "getStuGoldCount:onPmFailure=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        Loger.i(TAG, "getStuGoldCount:onPmError=" + responseEntity.getErrorMsg());
                    }
                });
            }
        }, 500);
    }

    public void setTotalOpeningLength(final long reTryTime, final String duration, final String speakingNum, final
    String speakingLen, final float x, final float y) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        mHttpManager.setTotalOpeningLength(enstuId, courseId, liveid, classId, duration, speakingNum, speakingLen,
                new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        Loger.d(TAG, "setTotalOpeningLength:onPmSuccess" + responseEntity.getJsonObject());
                        if (starAction != null) {
                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                            int star = jsonObject.getInt("star");
                            if (star > 0) {
                                starAction.onStarAdd(star, x, y);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Loger.d(TAG, "setTotalOpeningLength:onFailure");
                        super.onFailure(call, e);
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                setTotalOpeningLength(reTryTime + 1000, duration, speakingNum, speakingLen, x, y);
                            }
                        }, reTryTime);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.d(TAG, "setTotalOpeningLength:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                    }
                });
    }

    public void setNotOpeningNum() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.setNotOpeningNum(enstuId, mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Loger.d(TAG, "setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.e(TAG, "setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Loger.d(TAG, "setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
            }
        });
    }

    public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, final AbstractBusinessDataCallBack
            callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.getQuestion(enstuId, mGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(), new
                HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        Loger.d(TAG, "getQuestion:onPmSuccess" + responseEntity.getJsonObject());
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Loger.e(TAG, "getQuestion:onFailure", e);
                        super.onFailure(call, e);
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.d(TAG, "getQuestion:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                        callBack.onDataSucess();
                    }
                });
    }

    public void getCourseWareUrl(HttpCallBack requestCallBack) {
        mHttpManager.getCourseWareUrl(requestCallBack);
    }

    public void getMoreCourseWareUrl(String liveId, HttpCallBack requestCallBack) {
        mHttpManager.getMoreCoureWareUrl(liveId, requestCallBack);
    }

//    public void getPreDownload(String url,String path){
//        mHttpManager.download(url,path,new DownloadCallBack(){
//            @Override
//            protected void onDownloadSuccess() {
//
//            }
//
//            @Override
//            protected void onDownloadFailed() {
//
//            }
//        });
//    }

    public void getAdOnLL(final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getAdOnLL(lecAdvertEntity.course_id, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Loger.d(TAG, "getAdOnLL:onPmSuccess=" + responseEntity.getJsonObject());
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
                Loger.d(TAG, "getAdOnLL:onPmError=" + responseEntity.getErrorMsg());
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError(responseEntity
// .getErrorMsg()));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                Loger.d(TAG, "getAdOnLL:onFailure", e);
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError());
            }
        });
    }

    /**
     * roleplay组内排行榜
     */
    public void getRolePlayAnswerTeamRank(String testId, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getRolePlayAnswerTeamRank(testId, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("getRolePlayAnswerTeamRank:responseEntity=" + responseEntity.getJsonObject());
                GoldTeamStatus entity = mHttpResponseParser.parseRolePlayTeamRank(responseEntity, mGetInfo);
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.d(TAG, "getRolePlayAnswerTeamRank:msg=" + msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.d(TAG, "getRolePlayAnswerTeamRank:onPmError=" + responseEntity.getErrorMsg());
                callBack.onDataFail(1, responseEntity.getErrorMsg());
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
                Loger.d(TAG, "saveStuTalkSource:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.d(TAG, "saveStuTalkSource:onPmFailure" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.d(TAG, "saveStuTalkSource:onPmError" + responseEntity.getErrorMsg());
            }
        });
    }

    public LiveScienceHttpManager getLiveScienceHttpManager() {
        return liveScienceHttpManager;
    }

    public LiveArtsHttpManager getLiveArtsHttpManager() {
        return liveArtsHttpManager;
    }

    public Call download(final String url, final String saveDir, DownloadCallBack downloadCallBack) {
        return mHttpManager.download(url, saveDir, downloadCallBack);
    }

    static HashMap<String, String> channelAndRoomid = new HashMap();

    public void getToken(final LicodeToken licodeToken) {
        final String id = "x_" + mLiveType + "_" + mGetInfo.getId();
        String roomid = channelAndRoomid.get(id);
        Loger.i(TAG, "getToken:id=" + id + ",roomid=null?" + (roomid == null));
        if (roomid != null) {
            mHttpManager.getToken(roomid, mGetInfo.getStuId(), new Callback.CacheCallback<String>() {

                @Override
                public boolean onCache(String result) {
                    return false;
                }

                @Override
                public void onSuccess(String result) {
                    licodeToken.onToken(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    licodeToken.onError(ex);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            return;
        }
        String url = "https://test-rtc.xesimg.com:12443/room?id=" + id
                + "&userId=" + mGetInfo.getStuId() + "&name=" + mGetInfo.getStuName() + "&role=presenter&url=" +
                mGetInfo.getStuImg();
        mHttpManager.getRoomid(url, new Callback.CacheCallback<String>() {

            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                int roomidIndex = result.indexOf("roomId");
                if (roomidIndex != -1) {
                    result = result.substring(roomidIndex + 8);
                }
                roomidIndex = result.indexOf("\"");
                if (roomidIndex != -1) {
                    result = result.substring(0, roomidIndex);
                }
                Loger.i(TAG, "getToken:getRoomid:onSuccess:result=" + result);
                channelAndRoomid.put(id, result);
                mHttpManager.getToken(result, mGetInfo.getStuId(), new CacheCallback<String>() {

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }

                    @Override
                    public void onSuccess(String result) {
                        licodeToken.onToken(result);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        licodeToken.onError(ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Loger.e(TAG, "getToken:getRoomid:onError", ex);
                licodeToken.onError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            Loger.i(TAG, "onNetWorkChange:liveGetPlayServerError=" + liveGetPlayServerError);
            if (liveGetPlayServerError) {
                liveGetPlayServerError = false;
                liveGetPlayServer(mLiveTopic.getMode(), false);
            }
        }
        if (englishH5CoursewareAction != null) {
            englishH5CoursewareAction.onNetWorkChange(netWorkType);
        }
        if (mQuestionAction != null) {
            mQuestionAction.onNetWorkChange(netWorkType);
        }
        if (mIRCMessage != null) {
            mIRCMessage.onNetWorkChange(netWorkType);
        }
    }

    /**
     * 统计间隔
     */
    private long mStatisticsdelay = 300000;
    /**
     * 统计的runnable
     */
    private Runnable mStatisticsRun = new Runnable() {

        @Override
        public void run() {
            mBufferCount.set(mBufferCount.get() > 1000 ? 1000 : mBufferCount.get());
            mRepairBufferCount.set(mRepairBufferCount.get() > 1000 ? 1000 : mRepairBufferCount.get());
            mRepairOpenCount.set(mRepairOpenCount.get() > 1000 ? 1000 : mRepairOpenCount.get());
            mFailCount.set(mFailCount.get() > 1000 ? 1000 : mFailCount.get());
            mFailMainTeacherCount.set(mFailMainTeacherCount.get() > 1000 ? 1000 : mFailMainTeacherCount.get());
            mFailCounTeacherCount.set(mFailCounTeacherCount.get() > 1000 ? 1000 : mFailCounTeacherCount.get());
            mCompleteCount.set(mCompleteCount.get() > 1000 ? 1000 : mCompleteCount.get());
            mCompleteMainTeacherCount.set(mCompleteMainTeacherCount.get() > 1000 ? 1000 : mCompleteMainTeacherCount
                    .get());
            mCompleteCounTeacherCount.set(mCompleteCounTeacherCount.get() > 1000 ? 1000 : mCompleteCounTeacherCount
                    .get());
            XesMobAgent.liveStatistics(mBufferCount.get(), mRepairBufferCount.get(), mRepairOpenCount.get(), mFailCount
                            .get(),
                    mFailMainTeacherCount.get(), mFailCounTeacherCount.get(), mCompleteCount.get(),
                    mCompleteMainTeacherCount.get(), mCompleteCounTeacherCount.get());
            postDelayedIfNotFinish(mStatisticsRun, mStatisticsdelay);
        }
    };

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

    public SimpleVPlayerListener getVideoListener() {
        return mVideoListener;
    }

    public void setPlayserverEntity(PlayServerEntity.PlayserverEntity playserverEntity) {
        this.playserverEntity = playserverEntity;
    }

    public void live_report_play_duration(String channelname, long cost, PlayServerEntity.PlayserverEntity
            playserverEntity, String detail) {
        if (this.playserverEntity == null) {
            return;
        }
        String url = mGetInfo.getGslbServerUrl();
        HttpRequestParams entity = new HttpRequestParams();
        entity.addBodyParam("cmd", "live_report_play_duration");
        entity.addBodyParam("userid", mGetInfo.getStuId());
        entity.addBodyParam("username", mGetInfo.getUname());
        entity.addBodyParam("channelname", channelname);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        date.setTime(openStartTime);
        entity.addBodyParam("start", "" + dateFormat.format(date));
        entity.addBodyParam("cost", "" + (cost / 1000));
        entity.addBodyParam("ccode", mServer.getCcode());
        entity.addBodyParam("pcode", mServer.getPcode());
        entity.addBodyParam("acode", "");
        entity.addBodyParam("icode", mServer.getIcode());
        entity.addBodyParam("servercc", this.playserverEntity.getCcode());
        entity.addBodyParam("serverpc", this.playserverEntity.getPcode());
        entity.addBodyParam("serverac", this.playserverEntity.getAcode());
        entity.addBodyParam("serveric", this.playserverEntity.getIcode());
        try {
            if (DeviceUtils.isTablet(mContext)) {
                entity.addBodyParam("cfrom", "androidpad");
            } else {
                entity.addBodyParam("cfrom", "android");
            }
        } catch (Exception e) {
            entity.addBodyParam("cfrom", "android");
        }
        entity.addBodyParam("detail", detail);
        mHttpManager.sendGetNoBusiness(url, entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.i(TAG, "live_report_play_duration:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Loger.i(TAG, "live_report_play_duration:onResponse:response=" + response.message());
            }
        });
    }

    public void setTeacherPriaseBll(TeacherPraiseBll teacherPraiseBll) {
        mTeacherPraiseBll1 = teacherPraiseBll;
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

    public void setAnswerRankBll(AnswerRankBll bll) {
        mAnswerRankBll = bll;
        mAnswerRankBll.setLiveHttpManager(mHttpManager);
    }

    //    dns_resolve_stream?host=liveali.xescdn.com&stream=x_3_55873&app=live_server

    /**
     * 使用第三方视频提供商提供的调度接口获得第三方播放域名对应的包括ip地址的播放地址
     */
    public void dns_resolve_stream(final PlayServerEntity.PlayserverEntity playserverEntity, final PlayServerEntity
            mServer, String channelname, final AbstractBusinessDataCallBack callBack) {
        if (StringUtils.isEmpty(playserverEntity.getIp_gslb_addr())) {
            callBack.onDataFail(3, "empty");
            return;
        }
        final StringBuilder url;
        final String provide = playserverEntity.getProvide();
        if ("wangsu".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr());
        } else if ("ali".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr() + "/dns_resolve_stream");
        } else {
            callBack.onDataFail(3, "other");
            return;
        }
        HttpRequestParams entity = new HttpRequestParams();
//        curl -v ip_gslb_addr里的地址 -H "WS_URL:livewangsu.xescdn.com/live_server/x_3_55873" -H "WS_RETIP_NUM:1" -H
// "WS_URL_TYPE:3"
        if ("wangsu".equals(provide)) {
            String WS_URL = playserverEntity.getAddress() + "/" + mServer.getAppname() + "/" + channelname;
            entity.addHeaderParam("WS_URL", WS_URL);
            entity.addHeaderParam("WS_RETIP_NUM", "1");
            entity.addHeaderParam("WS_URL_TYPE", "3");
        } else {
            url.append("?host=" + playserverEntity.getAddress());
            url.append("&stream=" + channelname);
            url.append("&app=" + mServer.getAppname());
        }
//        entity.addBodyParam("host", playserverEntity.getAddress());
//        entity.addBodyParam("stream", channelname);
//        entity.addBodyParam("app", mServer.getAppname());
        final AtomicBoolean haveCall = new AtomicBoolean();
        final AbstractBusinessDataCallBack dataCallBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                Loger.d(TAG, "dns_resolve_stream:onDataSucess:haveCall=" + haveCall.get() + ",objData=" + objData[0]);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataSucess(objData);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                Loger.d(TAG, "dns_resolve_stream:onDataFail:haveCall=" + haveCall.get() + ",errStatus=" + errStatus +
                        ",failMsg=" + failMsg);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataFail(errStatus, failMsg);
                }
            }
        };
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                dataCallBack.onDataFail(0, "timeout");
            }
        }, 2000);
        mHttpManager.sendGetNoBusiness(url.toString(), entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.i(TAG, "dns_resolve_stream:onFailure=", e);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataCallBack.onDataFail(0, "onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int code = response.code();
                        String r = "";
                        try {
                            r = response.body().string();
                            Loger.i(TAG, "dns_resolve_stream:onResponse:url=" + url + ",response=" + code + "," + r);
                            if (response.code() >= 200 && response.code() <= 300) {
                                if ("wangsu".equals(provide)) {
//                        rtmp://111.202.83.208/live_server/x_3_55873?wsiphost=ipdb&wsHost=livewangsu.xescdn.com
                                    String url = r.replace("\n", "");
                                    int index1 = url.substring(7).indexOf("/");
                                    if (index1 != -1) {
                                        String host = url.substring(7, 7 + index1);
                                        playserverEntity.setIpAddress(host);
                                    }
                                    dataCallBack.onDataSucess(provide, url);
                                    return;
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(r);
                                        String host = jsonObject.getString("host");
                                        JSONArray ipArray = jsonObject.optJSONArray("ips");
                                        String ip = ipArray.getString(0);
                                        String url = "rtmp://" + ip + "/" + host + "/" + mServer.getAppname() + "/" +
                                                mGetInfo.getChannelname();
                                        playserverEntity.setIpAddress(host);
                                        dataCallBack.onDataSucess(provide, url);
                                        mLogtf.d("dns_resolve_stream:ip_gslb_addr=" + playserverEntity
                                                .getIp_gslb_addr() + ",ip=" + ip);
                                        return;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dataCallBack.onDataFail(1, r);
                    }
                });
            }
        });
    }

    public void streamReport(MegId msgid, String channelname, long connsec) {
        if (mServer == null || playserverEntity == null) {
            return;
        }
        if (MegId.MEGID_12107 == msgid) {
            boolean isPresent = true;
            if (mIRCMessage != null) {
                if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())) {
                    isPresent = mMainTeacher != null;
                } else {
                    isPresent = !mCounteacher.isLeave;
                }
            }
            if (!isPresent) {
                return;
            }
        }
        String url = mGetInfo.getLogServerUrl();
        HttpRequestParams entity = new HttpRequestParams();
        entity.addBodyParam("msgid", msgid.msgid);
        entity.addBodyParam("userid", mGetInfo.getStuId());
        entity.addBodyParam("username", mGetInfo.getUname());
        entity.addBodyParam("channelname", channelname);
        entity.addBodyParam("ccode", mServer.getCcode());
        entity.addBodyParam("pcode", mServer.getPcode());
        entity.addBodyParam("acode", "");
        entity.addBodyParam("icode", mServer.getIcode());
        entity.addBodyParam("servercc", playserverEntity.getCcode());
        entity.addBodyParam("serverpc", playserverEntity.getPcode());
        entity.addBodyParam("serverac", playserverEntity.getAcode());
        entity.addBodyParam("serveric", playserverEntity.getIcode());
        entity.addBodyParam("servergroup", playserverEntity.getGroup());
        if (StringUtils.isEmpty(playserverEntity.getIpAddress())) {
            entity.addBodyParam("server", playserverEntity.getAddress());
        } else {
            entity.addBodyParam("server", playserverEntity.getIpAddress());
        }
        entity.addBodyParam("appname", mServer.getAppname());
        entity.addBodyParam("reconnnum", "" + (mOpenCount.get() - 1));
        entity.addBodyParam("connsec", "" + (connsec / 1000));
        try {
            if (DeviceUtils.isTablet(mContext)) {
                entity.addBodyParam("cfrom", "androidpad");
            } else {
                entity.addBodyParam("cfrom", "android");
            }
        } catch (Exception e) {
            entity.addBodyParam("cfrom", "android");
        }
        if (playserverEntity.isUseFlv()) {
            entity.addBodyParam("detail", msgid.detail + " flv");
        } else {
            entity.addBodyParam("detail", msgid.detail);
        }
        mHttpManager.sendGetNoBusiness(url, entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Loger.i(TAG, "streamReport:onFailure=", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Loger.i(TAG, "streamReport:onResponse:response=" + response.message());
            }
        });
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

    /**
     * 附带主辅态的系统日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentSystemWithTeacherRole(String eventId, Map<String, String> data) {
        data.put("teacherrole", getMode().equals(LiveTopic.MODE_CLASS) ? "1" : "4");
        umsAgentDebugSys(eventId, data);
    }

    /**
     * 附带主辅态的交互日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentInteractionWithTeacherRole(String eventId, Map<String, String> data) {
        data.put("teacherrole", getMode().equals(LiveTopic.MODE_CLASS) ? "1" : "4");
        umsAgentDebugInter(eventId, data);
    }

    /**
     * 附带主辅态的展现日志
     *
     * @param eventId
     * @param data
     */
    public void umsAgentShowWithTeacherRole(String eventId, Map<String, String> data) {
        data.put("teacherrole", getMode().equals(LiveTopic.MODE_CLASS) ? "1" : "4");
        umsAgentDebugInter(eventId, data);
    }

    /**
     * 获取光荣榜
     */
    public synchronized void getHonorList(final int status) {
        if (mPraiseListAction != null && status == 0 && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_HONOR)
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        if (mPraiseListAction != null && status == 0)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_HONOR);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getHonorList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.getHonorList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                HonorListEntity honorListEntity = mHttpResponseParser.parseHonorList(responseEntity);
                if (mPraiseListAction != null && honorListEntity != null) {
                    if (status == 0) {

                        mPraiseListAction.onHonerList(honorListEntity);
                    } else if (status == 1) {
                        if (honorListEntity.getPraiseStatus() == 1)
                            mPraiseListAction.showThumbsUpToast();
                        else
                            mPraiseListAction.setThumbsUpBtnEnabled(true);
                    }

                }
                mLogtf.d("getHonorList:onPmSuccess:honorListEntity=" + (honorListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                    vcDialog.showDialog();
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getHonorList(0);
                        }
                    });
                    if (mPraiseListAction != null)
                        mPraiseListAction.setCurrentListType(0);
                } else if (status == 1 && mPraiseListAction != null) {
                    mPraiseListAction.setThumbsUpBtnEnabled(true);
                }
                mLogtf.d("getHonorList:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                showToast("" + responseEntity.getErrorMsg());
                mLogtf.d("getHonorList:onPmError=" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞榜
     */
    public synchronized void getThumbsUpList() {
        if (mPraiseListAction != null && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_THUMBS_UP)
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        if (mPraiseListAction != null)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getThumbsUpList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.getThumbsUpList(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ThumbsUpListEntity thumbsUpListEntity = mHttpResponseParser.parseThumbsUpList(responseEntity);
                if (mPraiseListAction != null && thumbsUpListEntity != null) {
                    mPraiseListAction.onThumbsUpList(thumbsUpListEntity);
                }
                mLogtf.d("getThumbsUpList:onPmSuccess:thumbsUpListEntity=" + (thumbsUpListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getThumbsUpList:onPmFailure=" + error + ",msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                vcDialog.showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getThumbsUpList();
                    }
                });
                if (mPraiseListAction != null)
                    mPraiseListAction.setCurrentListType(0);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getThumbsUpList:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取进步榜
     */
    public synchronized void getProgressList(final int status) {
        if (mPraiseListAction != null && status == 0 && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_PROGRESS)
            //如果当前榜单类型和新开启榜单类型相同，则退出
            return;
        if (mPraiseListAction != null)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_PROGRESS);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getProgressList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.getProgressList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ProgressListEntity progressListEntity = mHttpResponseParser.parseProgressList(responseEntity);
                if (mPraiseListAction != null && progressListEntity != null) {
                    if (status == 0) {

                        mPraiseListAction.onProgressList(progressListEntity);
                    } else if (status == 1) {
                        if (progressListEntity.getPraiseStatus() == 1)
                            mPraiseListAction.showThumbsUpToast();
                        else
                            mPraiseListAction.setThumbsUpBtnEnabled(true);
                    }

                }
                mLogtf.d("getProgressList:onPmSuccess:progressListEntity=" + (progressListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                    vcDialog.showDialog();
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getProgressList(0);
                        }
                    });
                    if (mPraiseListAction != null)
                        mPraiseListAction.setCurrentListType(0);
                } else if (status == 1 && mPraiseListAction != null) {
                    mPraiseListAction.setThumbsUpBtnEnabled(true);
                }
                mLogtf.d("getProgressList:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                showToast("" + responseEntity.getErrorMsg());
                mLogtf.d("getProgressList:onPmError=" + responseEntity.getErrorMsg());
            }

        });
    }

    /**
     * 获取点赞概率标识
     */
    public synchronized void getThumbsUpProbability() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getThumbsUpProbability:enstuId=" + enstuId + ",liveId=" + mLiveId);
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        mHttpManager.getThumbsUpProbability(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ThumbsUpProbabilityEntity thumbsUpProbabilityEntity = mHttpResponseParser.parseThumbsUpProbability
                        (responseEntity);
                if (mPraiseListAction != null && thumbsUpProbabilityEntity != null) {
                    mPraiseListAction.setThumbsUpProbability(thumbsUpProbabilityEntity);
                }
                mLogtf.d("getThumbsUpProbability:onPmSuccess:thumbsUpProbabilityEntity=" + (thumbsUpProbabilityEntity
                        == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getThumbsUpProbability:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getThumbsUpProbability:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 学生私聊老师点赞
     */
    public void sendThumbsUp() {
        mLogtf.i("sendThumbsUp");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_SEND_S);
            jsonObject.put("agreeFrom", "" + mGetInfo.getStuName());
            mIRCMessage.sendNotice(mCounTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            mLogtf.e("sendThumbsUp", e);
        }
    }

    /**
     * 学生计算赞数后私发老师
     */
    public void sendThumbsUpNum(int agreeNum) {
        mLogtf.i("sendThumbsUpNum:agreeNum=" + agreeNum + ",mCounTeacherStr=" + mCounTeacherStr);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_NUM_S);
            jsonObject.put("agreeNum", agreeNum);
            mIRCMessage.sendNotice(mCounTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            mLogtf.e("sendThumbsUpNum", e);
        }
    }

    public void setLiveRemarkBll(LiveRemarkBll liveRemarkBll) {
        mLiveRemarkBll = liveRemarkBll;
        mLiveRemarkBll.setHttpManager(mHttpManager);
        if (mGetInfo != null) {
            mLiveRemarkBll.setSysTimeOffset(sysTimeOffset);
        }
        if (mGetInfo != null && !"1".equals(mGetInfo.getIsShowMarkPoint())) {
            mLiveRemarkBll.hideBtMark();
        }
        Loger.i("LiveRemarkBll", "setlivebll____onbreak:" + mLiveTopic.getMainRoomstatus().isOnbreak()
                + "   stat:" + mGetInfo.getStat() + "   mode:" + getMode());
        if (!mLiveTopic.getMainRoomstatus().isOnbreak() && LiveTopic.MODE_CLASS.equals(getMode())) {
            mLiveRemarkBll.setClassReady(true);
        } else {
            mLiveRemarkBll.setClassReady(false);
        }
    }

    public void setLiveAutoNoticeBll(LiveAutoNoticeBll liveAutoNoticeBll) {
        mLiveAutoNoticeBll = liveAutoNoticeBll;
        mLiveAutoNoticeBll.setHttpManager(mHttpManager);
        mLiveAutoNoticeBll.setLiveId(mLiveId);
    }

    // 03.22 上传体验课播放器的心跳时间
    public void uploadExperiencePlayTime(String liveId, String termId, Long hbtime) {
        mHttpManager.uploadExperiencePlayingTime(liveId, termId, hbtime, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Loger.e("Duncan", "uploadexperiencetime:" + responseEntity.getJsonObject());
            }
        });
    }


    public String getLiveId() {
        return mLiveId;
    }


    /**
     * 分队仪式 学生准备完毕
     */
    public void sendStudentReady() {
        mLogtf.i("sendStudentReady");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.TEAM_PK_STUDENT_READY);
            jsonObject.put("stuId", "" + mGetInfo.getStuId());
            mIRCMessage.sendNotice(mCounTeacherStr, jsonObject.toString());
            mIRCMessage.sendNotice(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            mLogtf.e("sendStudentReady", e);
        }
        TeamPkLog.sendReady(this);
    }


    // 04.04 获取更多课程
    public void getMoreChoice(final PageDataLoadEntity pageDataLoadEntity, final AbstractBusinessDataCallBack
            getDataCallBack) {
        mHttpManager.getMoreChoiceCount(mLiveId, new HttpCallBack(pageDataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Loger.e("Duncan", "responseEntity:" + responseEntity);
                MoreChoice choiceEntity = mHttpResponseParser.parseMoreChoice(responseEntity);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });
    }
}