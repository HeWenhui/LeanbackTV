package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.base.BaseBll;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.CommonRequestCallBack;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.LogerTag;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.parentsmeeting.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.xesalib.umsagent.UmsAgent;
import com.xueersi.xesalib.umsagent.UmsAgentManager;
import com.xueersi.xesalib.umsagent.UmsConstants;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.string.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;
import org.xutils.xutils.ex.HttpException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 处理IRC消息，视频调度
 *
 * @author linyuqiang
 */
public class AuditClassLiveBll extends BaseBll implements LiveAndBackDebug {
    private String TAG = "AuditClassLiveBllLog";
    String liveListenEventid = LiveVideoConfig.LIVE_LISTEN;
    private QuestionAction mQuestionAction;
    private RollCallAction mRollCallAction;
    private PraiseOrEncourageAction mPraiseOrEncourageAction;
    private RedPackageAction readPackageBll;
    private AuditVideoAction mVideoAction;
    private RoomAction mRoomAction;
    private AuditClassAction auditClassAction;
    private LearnReportAction mLearnReportAction;
    private H5CoursewareAction h5CoursewareAction;
    private EnglishH5CoursewareAction englishH5CoursewareAction;
    private VideoChatAction videoChatAction;
    private LiveHttpManager mHttpManager;
    private LiveVideoSAConfig liveVideoSAConfig;
    private LiveHttpResponseParser mHttpResponseParser;
    private AuditIRCMessage mIRCMessage;
    private String courseId;
    private String mLiveId;
    private String mCurrentDutyId;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private final LiveTopic mLiveTopic = new LiveTopic();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LogToFile mLogtf;
    /** 主讲教师 */
    private Teacher mMainTeacher;
    /** 主讲教师名字 */
    private String mMainTeacherStr = null;
    private String mMainTeacherStatus = "on";
    /** 辅导教师 */
    private Teacher mCounteacher;
    /** 渠道前缀 */
    private final String CNANNEL_PREFIX = "x_";
    /** 主讲老师前缀 */
    public static final String TEACHER_PREFIX = "t_";
    /** 辅导老师前缀 */
    public static String COUNTTEACHER_PREFIX = "f_";
    private final String ROOM_MIDDLE = "L";
    private Callback.Cancelable mCataDataCancle;
    private Callback.Cancelable mGetPlayServerCancle, mGetStudentPlayServerCancle;
    /** 学习记录提交时间间隔 */
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
    /** 录播课的直播 */
    public final static int LIVE_TYPE_TUTORIAL = 1;
    /** 公开直播 */
    public final static int LIVE_TYPE_LECTURE = 2;
    /** 直播课的直播 */
    public final static int LIVE_TYPE_LIVE = 3;
    /** 用户心跳解析错误 */
    private int userOnlineError = 0;
    private PlayServerEntity mServer;
    private PlayServerEntity.PlayserverEntity playserverEntity;
    /** 网络类型 */
    private int netWorkType;
    /** 调度是不是在无网络下失败 */
    private boolean liveGetPlayServerError = false;
    /** 调度是不是在无网络下失败 */
    private boolean liveGetStudyPlayServerError = false;
    /** 是不是有分组 */
    private boolean haveTeam = false;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID;

    public AuditClassLiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int form) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = LIVE_TYPE_LIVE;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        mLiveTopic.setMode(LiveTopic.MODE_CLASS);
    }

    public AuditClassLiveBll(Context context, String vSectionID, int type) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        mLiveTopic.setMode(LiveTopic.MODE_CLASS);
    }

    /**
     * 播放器数据初始化
     */
    public void getInfo() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getInfo:enstuId=" + enstuId + ",liveId=" + mLiveId);
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
                onGetInfoSuccess(object);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getInfo:onPmFailure=" + msg);
                onLiveFailure("初始化失败", new Runnable() {

                    @Override
                    public void run() {
                        getInfo();
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getInfo:onPmError=" + responseEntity.getErrorMsg());
                onLiveError(responseEntity);
            }
        };
        if (mLiveType == LIVE_TYPE_LIVE) {// 直播
            mHttpManager.liveGetInfo(enstuId, "", mLiveId, 1, callBack);
        }
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
     * 领取金币
     *
     * @param operateId
     * @param liveId
     */
    public void sendReceiveGold(final int operateId, String liveId) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("sendReceiveGold:enstuId=" + enstuId + ",operateId=" + operateId + ",liveId=" + liveId);
        mHttpManager.sendReceiveGold(mLiveType, enstuId, operateId, liveId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("sendReceiveGold:onPmSuccess=" + responseEntity.getJsonObject().toString() + ",operateId=" +
                        operateId);
                if (readPackageBll != null) {
                    VideoResultEntity entity = mHttpResponseParser.redPacketParseParser(responseEntity);
                    readPackageBll.onGetPackage(entity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("sendReceiveGold:onPmFailure=" + msg + ",operateId=" + operateId);
                if (readPackageBll != null) {
                    readPackageBll.onGetPackageFailure(operateId);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("sendReceiveGold:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" + operateId);
                if (readPackageBll != null) {
                    readPackageBll.onGetPackageError(operateId);
                }
            }
        });
    }

    public void setVideoAction(AuditVideoAction videoAction) {
        this.mVideoAction = videoAction;
    }

    public void setAuditClassAction(AuditClassAction auditClassAction) {
        this.auditClassAction = auditClassAction;
    }

    private final AuditIRCCallback mIRCcallback = new AuditIRCCallback() {

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
//            if (lastTopicstr.equals(topicstr)) {
//                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
//                return;
//            }
//            lastTopicstr = topicstr;
//            mLogtf.i("onTopic:topicstr=" + topicstr);
//            try {
//                JSONObject jsonObject = new JSONObject(topicstr);
//                LiveTopic liveTopic = mHttpResponseParser.parseLiveTopic(mLiveTopic, jsonObject, mLiveType);
////                mLiveTopic.setMode(LiveTopic.MODE_CLASS);
//                mLogtf.d("onTopic:oldmode=" + mLiveTopic.getMode() + ",newmode=" + liveTopic.getMode() + ",topic=" +
//                        liveTopic.getTopic());
//                if (mLiveType == LIVE_TYPE_LIVE) {
//                    if (!(mLiveTopic.getMode().equals(liveTopic.getMode()))) {
//                        mLiveTopic.setMode(liveTopic.getMode());
//                        liveGetPlayServer();
//                    }
//                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
//                    if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
//                        if ("on".equals(mainRoomstatus.getExamStatus())) {
//                            String num = mainRoomstatus.getExamNum();
//                            mQuestionAction.onExamStart(mLiveId, num);
//                        } else {
//                            mQuestionAction.onExamStop();
//                        }
//                    }
//                    if (mVideoAction != null) {
//                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())) {
//                            if (mGetInfo.getStudentLiveInfo().isExpe()) {
//                                mVideoAction.onTeacherNotPresent(true);
//                            }
//                        }
//                    }
//                    if (videoChatAction != null) {
//                        if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode())) {
//                            videoChatAction.onJoin(mainRoomstatus.getOnmic(), mainRoomstatus.getOpenhands(), mainRoomstatus.isClassmateChange(), mainRoomstatus.getClassmateEntities());
//                        } else {
//                            LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
//                            videoChatAction.onJoin(coachRoomstatus.getOnmic(), coachRoomstatus.getOpenhands(), coachRoomstatus.isClassmateChange(), coachRoomstatus.getClassmateEntities());
//                        }
//                    }
//                }
//                List<String> disableSpeaking = liveTopic.getDisableSpeaking();
//                boolean have = false;
//                for (String id : disableSpeaking) {
//                    if (mIRCMessage.getNickname().equals(id)) {
//                        have = true;
//                    }
//                }
//                liveTopic.setDisable(have);
//                mLiveTopic.copy(liveTopic);
//                if (liveTopic.getTopic() != null) {
//                    if (mQuestionAction != null) {
//                        mQuestionAction.showQuestion(getQuestionFromTopic(liveTopic.getTopic()));
//                    }
//                } else {
//                    if (mQuestionAction != null) {
//                        mQuestionAction.showQuestion(null);
//                    }
//                }
//                if (LiveTopic.MODE_CLASS.equals(getMode())) {
//                    if (mRoomAction != null) {
//                        mRoomAction.onopenchat(mLiveTopic.getMainRoomstatus().isOpenchat(), LiveTopic.MODE_CLASS,
//                                false);
//                    }
//                } else {
//                    if (mRoomAction != null) {
//                        mRoomAction.onopenchat(mLiveTopic.getCoachRoomstatus().isOpenchat(), LiveTopic.MODE_TRANING,
//                                false);
//                    }
//                }
//                if (mRoomAction != null) {
//                    mRoomAction.onOpenbarrage(mLiveTopic.getMainRoomstatus().isOpenbarrage(), false);
//                    mRoomAction.onDisable(have, false);
//                }
//                if (h5CoursewareAction != null && jsonObject.has("h5_Experiment")) {
//                    JSONObject h5_Experiment = jsonObject.getJSONObject("h5_Experiment");
//                    String play_url = h5_Experiment.optString("play_url");
//                    String status = h5_Experiment.optString("status", "off");
//                    if (StringUtils.isEmpty(play_url)) {
//                        status = "off";
//                    }
//                    h5CoursewareAction.onH5Courseware(play_url, status);
//                }
//                if (englishH5CoursewareAction != null && jsonObject.has("H5_Courseware")) {
//                    JSONObject h5_Experiment = jsonObject.getJSONObject("H5_Courseware");
//                    String play_url = "";
//                    String status = h5_Experiment.optString("status", "off");
//                    if ("on".equals(status)) {
//                        play_url = "https://live.xueersi.com/Live/coursewareH5/" + mLiveId + "/" + h5_Experiment.getString("id") + "/" + h5_Experiment.getString("courseware_type")
//                                + "/" + mGetInfo.getStuId();
//                    }
//                    englishH5CoursewareAction.onH5Courseware(play_url, status);
//                }
//            } catch (JSONException e) {
//                mLogtf.e("onTopic", e);
//                MobAgent.httpResponseParserError(TAG, "onTopic", e.getMessage());
//            }
        }

        String lastNotice = "";

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                             final String notice) {
            // Loger.d(TAG, "onNotice:target=" + target + ",notice=" + notice);
            // mLogtf.i("onNotice:target=" + target + ",notice=" + notice);
//            String msg = "onNotice:target=" + target;
//            try {
//                JSONObject object = new JSONObject(notice);
//                int mtype = object.getInt("type");
//                msg += ",mtype=" + mtype + ",";
//                switch (mtype) {
//                    case XESCODE.READPACAGE:
//                        msg += "READPACAGE";
//                        if (readPackageBll != null) {
//                            readPackageBll.onReadPackage(object.getInt("id"));
//                        }
//                        break;
//                    case XESCODE.GAG: {
//                        msg += "GAG";
//                        boolean disable = object.getBoolean("disable");
//                        //s_3_13827_11022_1
//                        String id = object.getString("id");
//                        if (mIRCMessage.getNickname().equals(id)) {
//                            mLiveTopic.setDisable(disable);
//                            if (mRoomAction != null) {
//                                mRoomAction.onDisable(disable, true);
//                            }
//                        }
//                        msg += ",disable=" + disable + ",id=" + id + "," + mIRCMessage.getNickname();
//                    }
//                    break;
//                    case XESCODE.SENDQUESTION:
//                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//                        videoQuestionLiveEntity.type = object.getString("ptype");
//                        videoQuestionLiveEntity.id = object.getString("id");
//                        videoQuestionLiveEntity.time = object.getDouble("time");
//                        videoQuestionLiveEntity.num = object.getInt("num");
//                        videoQuestionLiveEntity.gold = object.getDouble("gold");
//                        videoQuestionLiveEntity.srcType = object.optString("srcType");
//                        videoQuestionLiveEntity.choiceType = object.optString("choiceType", "1");
//                        if (mQuestionAction != null) {
//                            mGetInfo.getLiveTopic().setTopic(getTopicFromQuestion(videoQuestionLiveEntity));
//                            mQuestionAction.showQuestion(videoQuestionLiveEntity);
//                        }
//                        msg += "SENDQUESTION:id=" + videoQuestionLiveEntity.id + ",gold=" + videoQuestionLiveEntity.gold;
//                        break;
//                    case XESCODE.STOPQUESTION:
//                        msg += "STOPQUESTION";
//                        mGetInfo.getLiveTopic().setTopic(null);
//                        if (mQuestionAction != null) {
//                            mQuestionAction.onStopQuestion(object.getString("ptype"));
//                        }
//                        break;
//                    case XESCODE.CLASSBEGIN: {
//                        boolean begin = object.getBoolean("begin");
//                        mLiveTopic.getMainRoomstatus().setClassbegin(begin);
//                        msg += begin ? "CLASSBEGIN" : "CLASSEND";
//                    }
//                    break;
//                    case XESCODE.UNDERSTANDT:
//                        msg += "UNDERSTANDT";
//                        if (mQuestionAction != null) {
//                            mQuestionAction.understand();
//                        }
//                        break;
//                    case XESCODE.OPENBARRAGE: {
//                        boolean open = object.getBoolean("open");
//                        msg += open ? "OPENBARRAGE" : "CLOSEBARRAGE";
//                        mLiveTopic.getMainRoomstatus().setOpenbarrage(open);
//                        mLogtf.d(msg);
//                        if (mRoomAction != null) {
//                            mRoomAction.onOpenbarrage(open, true);
//                        }
//                        //getLearnReport();
//                        break;
//                    }
//                    case XESCODE.OPENCHAT: {
//                        boolean open = object.getBoolean("open");
//                        String from = object.optString("from", "t");
//                        msg += "from=" + from + ",open=" + open;
//                        if ("t".equals(from)) {
//                            mLiveTopic.getMainRoomstatus().setOpenchat(open);
//                            if (LiveTopic.MODE_CLASS.equals(getMode())) {
//                                if (mRoomAction != null) {
//                                    mRoomAction.onopenchat(open, LiveTopic.MODE_CLASS, true);
//                                }
//                            }
//                        } else {
//                            mLiveTopic.getCoachRoomstatus().setOpenchat(open);
//                            if (LiveTopic.MODE_TRANING.equals(getMode())) {
//                                if (mRoomAction != null) {
//                                    mRoomAction.onopenchat(open, LiveTopic.MODE_TRANING, true);
//                                }
//                            }
//                        }
//                    }
//                    break;
//                    case XESCODE.MODECHANGE: {
//                        String mode = object.getString("mode");
//                        msg += ",mode=" + mode;
//                        mLogtf.d("onNotice:oldmode=" + mLiveTopic.getMode() + ",newmode=" + mode);
//                        if (!(mLiveTopic.getMode().equals(mode))) {
//                            if (mVideoAction != null) {
//                                boolean isPresent = isPresent(mode);
//                                mVideoAction.onModeChange(mode, isPresent);
//                                if (!isPresent) {
//                                    mVideoAction.onTeacherNotPresent(true);
//                                }
//                            }
//                            mLiveTopic.setMode(mode);
//                            liveGetPlayServer();
//                        }
//                    }
//                    break;
//                    case XESCODE.TEACHER_MESSAGE:
//                        if (mRoomAction != null) {
//                            String name;
//                            if (sourceNick.startsWith("t")) {
//                                name = "主讲老师";
//                                mRoomAction.onMessage(target, name, "", "", object.getString("msg"));
//                            } else {
//                                name = "辅导老师";
//                                String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
//                                String to = object.optString("to", "All");
//                                if ("All".equals(to) || teamId.equals(to)) {
//                                    mRoomAction.onMessage(target, name, "", "", object.getString("msg"));
//                                }
//                            }
//                        }
//                        break;
//                    case XESCODE.LEARNREPORT: {
//                        msg += "LEARNREPORT";
//                        getLearnReport(2);
//                        break;
//                    }
//                    case XESCODE.ROLLCALL: {
//                        msg += "ROLLCALL";
//                        if (mRollCallAction != null) {
//                            mRollCallAction.onRollCall(false);
//                            msg += ",signStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus();
//                            if (mGetInfo.getStudentLiveInfo().getSignStatus() != 2) {
//                                ClassSignEntity classSignEntity = new ClassSignEntity();
//                                classSignEntity.setStuName(mGetInfo.getStuName());
//                                classSignEntity.setTeacherName(mGetInfo.getTeacherName());
//                                classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
//                                classSignEntity.setStatus(1);
//                                mRollCallAction.onRollCall(classSignEntity);
//                            }
//                        }
//                        break;
//                    }
//                    case XESCODE.STOPROLLCALL: {
//                        msg += "STOPROLLCALL";
//                        if (mRollCallAction != null) {
//                            mRollCallAction.onRollCall(true);
//                            if (mGetInfo.getStudentLiveInfo().getSignStatus() != 2) {
//                                mGetInfo.getStudentLiveInfo().setSignStatus(3);
//                                ClassSignEntity classSignEntity = new ClassSignEntity();
//                                classSignEntity.setStuName(mGetInfo.getStuName());
//                                classSignEntity.setTeacherName(mGetInfo.getTeacherName());
//                                classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
//                                classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
//                                mRollCallAction.onRollCall(classSignEntity);
//                            }
//                        }
//                        break;
//                    }
//                    case XESCODE.CLASS_MATEROLLCALL: {
//                        if (RollCallBll.IS_SHOW_CLASSMATE_SIGN) {
//                            if (mRollCallAction != null) {
//                                List<String> headImgUrl = mGetInfo.getHeadImgUrl();
//                                ClassmateEntity classmateEntity = new ClassmateEntity();
//                                String id = object.optString("id");
//                                classmateEntity.setId(id);
//                                classmateEntity.setName(object.getString("name"));
//                                if (!headImgUrl.isEmpty()) {
//                                    try {
//                                        String img = headImgUrl.get(0) + "/" + object.getString("path") + "/" +
//                                                mGetInfo.getImgSizeType() + "?" + object.getString("Version");
//                                        classmateEntity.setImg(img);
//                                    } catch (JSONException e) {
//                                        MobAgent.httpResponseParserError(TAG, "onNotice:setImg", e.getMessage());
//                                    }
//                                    msg += "CLASS_MATEROLLCALL，" + classmateEntity.getName() + ",img=" + classmateEntity.getImg();
//                                } else {
//                                    msg += "CLASS_MATEROLLCALL，" + classmateEntity.getName() + ",no head";
//                                }
//                                mRollCallAction.onClassmateRollCall(classmateEntity);
//                            }
//                        }
//                        break;
//                    }
//                    case XESCODE.PRAISE: {
//                        msg += "PRAISE";
//                        if (mPraiseOrEncourageAction != null) {
//                            mPraiseOrEncourageAction.onPraiseOrEncourage(object);
//                        }
//                    }
//                    break;
//                    case XESCODE.EXAM_START: {
//                        msg += "EXAM_START";
//                        if (mQuestionAction != null) {
//                            String num = object.optString("num", "0");
//                            mQuestionAction.onExamStart(mLiveId, num);
//                        }
//                    }
//                    break;
//                    case XESCODE.EXAM_STOP: {
//                        msg += "EXAM_STOP";
//                        if (mQuestionAction != null) {
//                            mQuestionAction.onExamStop();
//                        }
//                    }
//                    break;
//                    case XESCODE.SPEECH_RESULT: {
//                        msg += "SPEECH_RESULT";
//                        if (notice.equals(lastNotice)) {
//                            return;
//                        }
//                        boolean speechResult = false;
//                        if (mQuestionAction != null) {
//                            speechResult = mQuestionAction.onSpeechResult(object.toString());
//                        }
//                        if (speechResult) {
//                            lastNotice = notice;
//                        }
//                    }
//                    break;
//                    case XESCODE.ENGLISH_H5_COURSEWARE: {
//                        if (englishH5CoursewareAction != null) {
//                            String play_url = "";
//                            String status = object.optString("status", "off");
//                            if ("on".equals(status)) {
//                                play_url = "https://live.xueersi.com/Live/coursewareH5/" + mLiveId + "/" + object.getString("id") + "/" + object.getString("courseware_type")
//                                        + "/" + mGetInfo.getStuId();
//                            }
//                            englishH5CoursewareAction.onH5Courseware(play_url, status);
//                        }
//                    }
//                    break;
//                    case XESCODE.H5_START: {
//                        if (h5CoursewareAction != null) {
//                            String play_url = object.getString("play_url");
//                            h5CoursewareAction.onH5Courseware(play_url, "on");
//                        }
//                    }
//                    break;
//                    case XESCODE.H5_STOP: {
//                        if (h5CoursewareAction != null) {
//                            h5CoursewareAction.onH5Courseware("", "off");
//                        }
//                    }
//                    break;
//                    case XESCODE.RAISE_HAND_SELF:
//                        if (videoChatAction != null) {
//                            String status = object.optString("status", "off");
//                            int num = object.optInt("num", 0);
//                            msg += "RAISE_HAND_SELF:status=" + status + ",num=" + num;
//                            videoChatAction.raiseHandStatus(status, num);
//                        }
//                        break;
////                    case XESCODE.RAISE_HAND_AGAIN:
////                        if (videoChatAction != null) {
////                            videoChatAction.raisehand("on", true);
////                        }
////                        break;
//                    case XESCODE.RAISE_HAND:
//                        if (videoChatAction != null) {
//                            String status = object.optString("status", "off");
//                            msg += "RAISE_HAND:status=" + status;
//                            videoChatAction.raisehand(status);
//                        }
//                        break;
//                    case XESCODE.REQUEST_ACCEPT:
//                        if (videoChatAction != null) {
//                            videoChatAction.requestAccept();
//                        }
//                        break;
//                    case XESCODE.START_MICRO:
//                        if (videoChatAction != null) {
//                            String status = object.optString("status", "off");
//                            boolean contain = false;
//                            if (status.equals("on")) {
//                                JSONArray students = object.optJSONArray("students");
//                                if (students != null) {
//                                    for (int i = 0; i < students.length(); i++) {
//                                        if (mGetInfo.getStuId().equals(students.getString(i))) {
//                                            contain = true;
//                                        }
//                                    }
//                                }
//                            }
//                            videoChatAction.startMicro(status, contain);
//                        }
//                        break;
//                    case XESCODE.ST_MICRO: {
//                        if (videoChatAction != null) {
//                            String status = object.optString("status", "off");
//                            videoChatAction.quit(status);
//                        }
//                        break;
//                    }
//                    case XESCODE.RAISE_HAND_COUNT: {
//                        if (videoChatAction != null) {
//                            int count = object.optInt("num", 0);
//                            videoChatAction.raiseHandCount(count);
//                        }
//                        break;
//                    }
//                    default:
//                        msg += "default";
//                        break;
//                }
//                // Loger.d(TAG, "onNotice:msg=" + msg);
//                mLogtf.i("onNotice:msg=" + msg);
//            } catch (JSONException e) {
//                // Loger.e(TAG, "onNotice", e);
//                mLogtf.e("onNotice:" + notice, e);
//                MobAgent.httpResponseParserError(TAG, "onNotice", e.getMessage());
//            }
        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            if (mRoomAction != null) {
                mRoomAction.onMessage(target, sender, login, hostname, text);
            }
        }

        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {
//            if (!"T".equals(message) && haveTeam) {
//                StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
//                String teamId = studentLiveInfo.getTeamId();
//                try {
//                    JSONObject jsonObject = new JSONObject(message);
//                    int type = jsonObject.getInt("type");
//                    if (type == XESCODE.TEACHER_MESSAGE) {
//                        String to = jsonObject.optString("to", teamId);
//                        if (!teamId.equals(to)) {
//                            return;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (isSelf && "T".equals(message)) {
//                if (mVideoAction != null) {
//                    mVideoAction.onKick();
//                }
//            }
//            if (mRoomAction != null) {
//                mRoomAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
//            }
        }

        @Override
        public void onStudentLeave(boolean leave, String stuPushStatus) {
            if (mVideoAction != null) {
                mVideoAction.onStudentLeave(leave, stuPushStatus);
            }
        }

        @Override
        public void onStudentError(String msg) {
            if (mVideoAction != null) {
                mVideoAction.onStudentError(msg);
            }
        }

        @Override
        public void onStudentPrivateMessage(String sender, String login, String hostname, String target, String message) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                int type = jsonObject.getInt("type");
                switch (type) {
                    case XESCODE.STUDENT_REPLAY: {
                        String playUrl = jsonObject.optString("playUrl");
                        mMainTeacherStatus = jsonObject.optString("mainTeacher");
                        String coachTeacher = jsonObject.optString("coachTeacher");
                        if (coachTeacher.equals("off")) {
                            mCounteacher.isLeave = true;
                        } else {
                            mCounteacher.isLeave = false;
                        }
                        String mode = jsonObject.optString("mode", LiveTopic.MODE_CLASS);
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, true);
                            }
                            mLiveTopic.setMode(mode);
                            liveGetPlayServer(true);
                        }
                        if (!StringUtils.isEmpty(playUrl)) {
                            if (mVideoAction != null) {
                                mVideoAction.onStudentLiveUrl(playUrl);
                            }
                        } else {
                            liveGetStudentPlayServer();
                        }
                        mLogtf.d("onStudentPrivateMessage:playUrl=" + playUrl);

                        //旁听日志
                        StableLogHashMap logHashMap = new StableLogHashMap("startPlay");
                        logHashMap.put("nickname", sender);
                        logHashMap.put("playurl", playUrl);
                        String nonce = jsonObject.optString("nonce");
                        logHashMap.addSno("4").addNonce(nonce).addExY().addStable("1");
                        umsAgentDebug(liveListenEventid, logHashMap.getData());
                    }
                    break;
                    case XESCODE.STUDENT_MODECHANGE: {
                        String mode = jsonObject.optString("mode", LiveTopic.MODE_CLASS);
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, true);
                            }
                            mLiveTopic.setMode(mode);
                            liveGetPlayServer(true);
                        }
                    }
                    break;
                    case XESCODE.STUDENT_UPDATE: {
                        getStudentLiveInfo();
                    }
                    break;
//                    case XESCODE.TEACHER_JOIN_LEVEL: {
//                        String name = jsonObject.optString("name");
//                        int status = jsonObject.optInt("status", 1);
//                        if (name.startsWith(TEACHER_PREFIX)) {
//                            if (status == 1) {
//                                mMainTeacherStatus = "on";
//                            } else {
//                                mMainTeacherStatus = "off";
//                            }
//                        } else if (name.startsWith(COUNTTEACHER_PREFIX)) {
//                            mCounteacher.isLeave = status == 0;
//                        }
//                    }
//                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                        haveCounteacher = true;
                        mCounteacher.isLeave = false;
                        s += ",counteacher=" + _nick;
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }
                    } else {
                        boolean isMyTeam = isMyTeam(user.getNick());
                        if (isMyTeam) {
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
            if (mLiveType == AuditClassLiveBll.LIVE_TYPE_LIVE && !teamStuIds.isEmpty()) {
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
                    if (isMyTeam) {
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
//                    if (sourceNick.startsWith(LiveBll.TEACHER_PREFIX) || sourceNick.startsWith(LiveBll.COUNTTEACHER_PREFIX)) {
//                        //老师不计算在内
//                        return;
//                    }
                    boolean isMyTeam = isMyTeam(sourceNick);
                    if (isMyTeam) {
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
        }
    };

    /** 当前状态，老师是不是在直播间 */
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
//        if (mIRCMessage != null && mIRCMessage.isConnected()) {
//            if (LiveTopic.MODE_CLASS.endsWith(mode)) {
//                isPresent = mMainTeacherStatus.equals("on");
//            } else {
//                isPresent = !mCounteacher.isLeave;
//            }
//        }
        return isPresent;
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    /**
     * 请求房间状态成功
     *
     * @param object
     */
    private void onGetInfoSuccess(JSONObject object) {
        mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, mLiveType, LiveVideoBusinessConfig.ENTER_FROM_1);
        if (mGetInfo == null) {
            onLiveFailure("服务器异常", null);
            return;
        }
        if (mGetInfo.getIsArts() == 1) {
            appID = UmsConstants.ARTS_APP_ID;
            LiveVideoConfig.IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_libarts);
            liveVideoSAConfig.IS_SCIENCE = false;
        } else {
            LiveVideoConfig.IS_SCIENCE = true;
            appID = UmsConstants.LIVE_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_science);
            liveVideoSAConfig.IS_SCIENCE = true;
        }
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        if (mGetInfo.getStat() == 1) {
            if (mVideoAction != null) {
                mVideoAction.onTeacherNotPresent(true);
            }
            mLogtf.d("onGetInfoSuccess:onTeacherNotPresent");
        }
        mCounteacher = new Teacher(mGetInfo.getTeacherName());
        String s = "onGetInfoSuccess:stat=" + mGetInfo.getStat();
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
            channel = "2" + ROOM_MIDDLE + mGetInfo.getId();
        } else {
            StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            courseId = studentLiveInfo.getCourseId();
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
            channel = mGetInfo.getId() + "-" + studentLiveInfo.getClassId();
        }
        s += ",liveType=" + mLiveType + ",channel=" + channel;
        String nickname = mGetInfo.getLiveType() + "_"
                + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
        mIRCMessage = new AuditIRCMessage(netWorkType, channel, mGetInfo.getStuName(), nickname, this);
        mIRCMessage.setNewTalkConf(newTalkConf);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        s += ",newTalkConf=" + newTalkConf.size();
        // Loger.d(TAG, s);
        mLogtf.d(s);
        if (mGetInfo.getStudentLiveInfo() != null) {
            if (mGetInfo.getStudentLiveInfo().getEvaluateStatus() == 1) {
                mLogtf.d("onGetInfoSuccess:getLearnReport");
                getLearnReport(1);
            }
            mLogtf.d("onGetInfoSuccess:getSignStatus=" + mGetInfo.getStudentLiveInfo().getSignStatus());
            if (mGetInfo.getStudentLiveInfo().getSignStatus() != 0 && mGetInfo.getStudentLiveInfo().getSignStatus()
                    != 2) {
                if (mRollCallAction != null) {
                    ClassSignEntity classSignEntity = new ClassSignEntity();
                    classSignEntity.setStuName(mGetInfo.getStuName());
                    classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                    classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                    classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
                    mRollCallAction.onRollCall(classSignEntity);
                }
            }
        }
        mLogtf.d("onGetInfoSuccess:mode=" + mLiveTopic.getMode());
        liveGetPlayServerFirst();
    }

    /**
     * 获取学习报告
     */
    private synchronized void getLearnReport(final int from) {
        XesMobAgent.liveLearnReport("request:" + from);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getLearnReport:enstuId=" + enstuId + ",liveId=" + mLiveId);
        mHttpManager.getLearnReport(enstuId, mLiveId, mLiveType, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LearnReportEntity learnReportEntity = mHttpResponseParser.parseLearnReport(responseEntity);
                if (mLearnReportAction != null && learnReportEntity != null) {
                    learnReportEntity.getStu().setStuName(mGetInfo.getStuName());
                    learnReportEntity.getStu().setTeacherName(mGetInfo.getTeacherName());
                    learnReportEntity.getStu().setTeacherIMG(mGetInfo.getTeacherIMG());
                    mLearnReportAction.onLearnReport(learnReportEntity);
                }
                XesMobAgent.liveLearnReport("request-ok:" + from);
                mLogtf.d("getLearnReport:onPmSuccess:learnReportEntity=" + (learnReportEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                XesMobAgent.liveLearnReport("request-fail:" + from);
                mLogtf.d("getLearnReport:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                XesMobAgent.liveLearnReport("request-error:" + from);
                mLogtf.d("getLearnReport:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 签名
     */
    public synchronized void getStudentLiveInfo() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mHttpManager.getStudentLiveInfo(enstuId, mLiveId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                String oldMode = mLiveTopic.getMode();
                StudyInfo studyInfo = mHttpResponseParser.parseStudyInfo(responseEntity, oldMode);
                if (auditClassAction != null) {
                    auditClassAction.onGetStudyInfo(studyInfo);
                }
                String mode = studyInfo.getMode();
                Loger.d(TAG, "getStudentLiveInfo:onPmSuccess:" + responseEntity.getJsonObject() + ",mode=" + oldMode + "," + mode);
                if (!oldMode.equals(mode)) {
                    if (mVideoAction != null) {
                        mVideoAction.onModeChange(mode, true);
                    }
                    mLiveTopic.setMode(mode);
                    liveGetPlayServer(true);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                Loger.e(TAG, "getStudentLiveInfo:onPmFailure:msg=" + msg, error);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Loger.e(TAG, "getStudentLiveInfo:onPmError:errorMsg=" + responseEntity.getErrorMsg());
            }
        });
    }

    /** 第一次调度，不判断老师状态 */
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
        String serverurl;
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
        serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname();
        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        mLogtf.d("liveGetPlayServer:modeTeacher=" + getModeTeacher());
        mGetPlayServerCancle = mHttpManager.liveGetPlayServer(serverurl, new CommonRequestCallBack<String>() {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLogtf.d("liveGetPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback);
                if (ex instanceof HttpException) {
                    HttpException error = (HttpException) ex;
                    if (error.getCode() >= 300) {
                        long time = System.currentTimeMillis() - before;
                        mLogtf.d("liveGetPlayServer:onError:code=" + error.getCode() + ",time=" + time);
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

    public void startVideo() {
        if (mIRCMessage != null) {
            mIRCMessage.startVideo();
        }
    }

    private long lastGetStudentPlayServer;

    public void liveGetStudentPlayServer() {
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            liveGetStudyPlayServerError = true;
            return;
        }
        liveGetStudyPlayServerError = false;
        final long before = System.currentTimeMillis();
        String serverurl;
        // http://gslb.xueersi.com/xueersi_gslb/live?cmd=live_get_playserver&userid=000041&username=xxxxxx
        // &channelname=88&remote_ip=116.76.97.244
        String channelname = "s_" + mGetInfo.getId() + "_" + mGetInfo.getStuId();
//        channelname = "s_49247_11681";
        mGetInfo.setStudentChannelname(channelname);
        serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getStudentChannelname();
        mLogtf.d("liveGetStudentPlayServer:serverurl=" + serverurl);
        if (mGetStudentPlayServerCancle != null) {
            mGetStudentPlayServerCancle.cancel();
            mGetStudentPlayServerCancle = null;
        }
        mGetStudentPlayServerCancle = mHttpManager.liveGetPlayServer(serverurl, new CommonRequestCallBack<String>() {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mIRCMessage != null) {
//                    mIRCMessage.startVideo();
                }
                mLogtf.d("liveGetStudentPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback);
                if (ex instanceof HttpException) {
                    HttpException error = (HttpException) ex;
                    if (error.getCode() >= 300) {
                        long time = System.currentTimeMillis() - before;
                        mLogtf.d("liveGetStudentPlayServer:onError:code=" + error.getCode() + ",time=" + time);
                        if (time < 15000) {
                            if (mVideoAction != null && mLiveTopic != null) {
                                mVideoAction.onStudentLiveStart(null);
                            }
                            return;
                        }
                    }
                } else {
                    mLogtf.e("liveGetStudentPlayServer:onError:isOnCallback=" + isOnCallback, ex);
                }
                long now = System.currentTimeMillis();
                if (now - lastGetStudentPlayServer < 5000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetStudentPlayServer:onError retry1");
                            liveGetStudentPlayServer();
                        }
                    }, 1000);
                } else {
                    lastGetStudentPlayServer = now;
                    onLiveFailure("直播调度失败", new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetStudentPlayServer:onError retry2");
                            liveGetStudentPlayServer();
                        }
                    });
                }
            }

            @Override
            public void onSuccess(String result) {
//                Loger.i(TAG, "liveGetPlayServer:onSuccess:result=" + result);
                String s = "liveGetStudentPlayServer:onSuccess";
                try {
                    JSONObject object = new JSONObject(result);
                    PlayServerEntity server = mHttpResponseParser.parsePlayerServer(object);
                    if (server != null) {
                        s += ",server=" + server.getAppname() + ",rtmpkey=" + server.getRtmpkey();
                        mGetInfo.setSkeyPlayF(server.getRtmpkey());
//                        mServer = server;
                        if (mVideoAction != null && mLiveTopic != null) {
                            mVideoAction.onStudentLiveStart(server);
                        }
                    } else {
                        s += ",server=null";
                        onLiveFailure("直播调度失败", new Runnable() {

                            @Override
                            public void run() {
                                liveGetStudentPlayServer();
                            }
                        });
                    }
                    mLogtf.d(s);
                } catch (JSONException e) {
                    MobAgent.httpResponseParserError(TAG, "liveGetStudentPlayServer", result + "," + e.getMessage());
                    // Loger.e(TAG, "liveGetPlayServer", e);
                    mLogtf.e("liveGetStudentPlayServer", e);
                    onLiveFailure("直播调度失败", new Runnable() {

                        @Override
                        public void run() {
                            liveGetStudentPlayServer();
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
     * activity退出
     */
    public void onDestroy() {
        mQuestionAction = null;
        mRollCallAction = null;
        mPraiseOrEncourageAction = null;
        readPackageBll = null;
        mVideoAction = null;
        mRoomAction = null;
        mLearnReportAction = null;
        h5CoursewareAction = null;
        englishH5CoursewareAction = null;
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
        videoChatAction = null;
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
        long openStartTime;
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

    public boolean isDisable() {
        return mLiveTopic.isDisable();
    }

    public boolean isConnected() {
        if (mIRCMessage == null) {
            return false;
        }
        return mIRCMessage.isConnected();
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

    /** 得到老师名字 */
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

    /** 得到当前模式 */
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

    public String getNickname() {
        return mIRCMessage.getNickname();
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

    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, final OnSpeechEval onSpeechEval) {
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
                + "&userId=" + mGetInfo.getStuId() + "&name=" + mGetInfo.getStuName() + "&role=presenter&url=" + mGetInfo.getStuImg();
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
            if (liveGetStudyPlayServerError) {
                liveGetStudyPlayServerError = true;
                liveGetStudentPlayServer();
            }
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

    public void streamReport(MegId msgid, String channelname, long connsec) {
        if (mServer == null || playserverEntity == null) {
            return;
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
        entity.addBodyParam("server", playserverEntity.getAddress());
        entity.addBodyParam("appname", mServer.getAppname());
        entity.addBodyParam("reconnnum", "" + (mOpenCount.get() - 1));
        entity.addBodyParam("connsec", "" + (connsec / 1000));
        entity.addBodyParam("cfrom", "android");
        entity.addBodyParam("detail", msgid.detail);
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
    public void umsAgentDebug(String eventId, final Map<String, String> mData) {
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
    public void umsAgentDebug2(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getStuName());
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
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }

    /**
     * 展现日志
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebug3(String eventId, final Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getStuName());
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
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData);
    }
}
