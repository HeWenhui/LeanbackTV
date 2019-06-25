package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.NewIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.UselessNotice;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.SysLogLable;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 直播间管理类
 *
 * @author chekun
 * created  at 2018/6/20 10:32
 */
public class LiveBll2 extends BaseBll {
    Logger logger = LoggerFactory.getLogger("LiveBll2");
    /**
     * 需处理 topic 业务集合
     */
    private List<TopicAction> mTopicActions = new ArrayList<>();
    /**
     * 需处理 notice 的业务集合
     */
    private Map<Integer, List<NoticeAction>> mNoticeActionMap = new HashMap<>();
    /**
     * 需处理 全量 消息的 业务集合
     */
    private List<MessageAction> mMessageActions = new ArrayList<>();
    /**
     * 所有业务bll 集合
     */
    private List<LiveBaseBll> businessBlls = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AllLiveBasePagerIml allLiveBasePagerIml;
    private TeacherAction teacherAction;
    private final int mLiveType;
    private LogToFile mLogtf;
    private String mLiveId;
    private String mCourseId;
    private LiveGetInfo mGetInfo;
    private LiveVideoSAConfig liveVideoSAConfig;
    /**
     * 区分文理appid
     */
    String appID = UmsConstants.LIVE_APP_ID;
    private LiveHttpManager mHttpManager;
    /**
     * 学生课程id
     */
    private String mStuCouId;
    private int mForm;
    private LiveHttpResponseParser mHttpResponseParser;
    /**
     * 网络类型
     */
    private int netWorkType;
    private final LiveTopic mLiveTopic;
    /**
     * 校准系统时间
     */
    private long sysTimeOffset;
    private final String ROOM_MIDDLE = "L";
    private IIRCMessage mIRCMessage;
    private LiveVideoBll liveVideoBll;
    private String mCurrentDutyId;
    private AtomicBoolean mIsLand;
    private static String Tag = "LiveBll2";
    private LiveUidRx liveUidRx;
    private LiveLog liveLog;
    /**
     * 是否使用新IRC SDK
     */
//    private boolean isNewIRC = false;
    LiveAndBackDebugIml liveAndBackDebugIml;

    /**
     * 直播的
     *
     * @param context
     * @param vStuCourseID 购课id
     * @param courseId     课程id
     * @param vSectionID   场次id
     * @param form         来源
     * @param liveGetInfo
     */
    public LiveBll2(Context context, String vStuCourseID, String courseId, String vSectionID, int form, LiveGetInfo
            liveGetInfo) {
        super(context);
        this.mStuCouId = vStuCourseID;
        this.mCourseId = courseId;
        this.mLiveId = vSectionID;
        this.mLiveType = LiveVideoConfig.LIVE_TYPE_LIVE;
        this.mForm = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("courseId", courseId);
        mHttpManager.addBodyParam("stuCouId", vStuCourseID);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpManager.addBodyParam("form", "" + form);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (liveGetInfo != null) {
            mLiveTopic = liveGetInfo.getLiveTopic();
            mLiveTopic.setMode(liveGetInfo.getMode());
        } else {
            mLiveTopic = new LiveTopic();
        }
        liveAndBackDebugIml = new LiveAndBackDebugIml(context, mLiveType, mLiveId, mCourseId);
        ProxUtil.getProxUtil().put(context, LiveAndBackDebug.class, liveAndBackDebugIml);
        liveLog = new LiveLog(mContext, mLiveType, mLiveId, "NL");
        ProxUtil.getProxUtil().put(context, LiveOnLineLogs.class, liveLog);
        mLogtf = new LogToFile(context, TAG);
        allLiveBasePagerIml = new AllLiveBasePagerIml(context);
    }

    /**
     * 讲座的
     *
     * @param context
     * @param vSectionID  场次id
     * @param type
     * @param form        来源
     * @param liveGetInfo
     */
    public LiveBll2(Context context, String vSectionID, int type, int form, LiveGetInfo liveGetInfo) {
        super(context);
        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mForm = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        netWorkType = NetWorkHelper.getNetWorkState(context);
        if (liveGetInfo != null) {
            mLiveTopic = liveGetInfo.getLiveTopic();
        } else {
            mLiveTopic = new LiveTopic();
        }
        mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        liveAndBackDebugIml = new LiveAndBackDebugIml(context, mLiveType, mLiveId, "");
        ProxUtil.getProxUtil().put(context, LiveAndBackDebug.class, liveAndBackDebugIml);
        liveLog = new LiveLog(mContext, mLiveType, mLiveId, "NL");
        ProxUtil.getProxUtil().put(context, LiveOnLineLogs.class, liveLog);
        mLogtf = new LogToFile(context, TAG);
        allLiveBasePagerIml = new AllLiveBasePagerIml(context);
    }

    /**
     * 辅导的
     *
     * @param context
     * @param vSectionID    场次id
     * @param currentDutyId
     * @param type
     * @param form          来源
     */
    @Deprecated
    public LiveBll2(Context context, String vSectionID, String currentDutyId, int type, int form) {
        super(context);

        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mCurrentDutyId = currentDutyId;
        this.mForm = form;
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", vSectionID);
        mHttpResponseParser = new LiveHttpResponseParser(context);
        netWorkType = NetWorkHelper.getNetWorkState(context);
        mLiveTopic = new LiveTopic();
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            mLiveTopic.setMode(LiveTopic.MODE_CLASS);
        }
        liveAndBackDebugIml = new LiveAndBackDebugIml(context, mLiveType, mLiveId, "");
        ProxUtil.getProxUtil().put(context, LiveAndBackDebug.class, liveAndBackDebugIml);
        liveLog = new LiveLog(mContext, mLiveType, mLiveId, "NL");
        ProxUtil.getProxUtil().put(context, LiveOnLineLogs.class, liveLog);
        mLogtf = new LogToFile(context, TAG);
        allLiveBasePagerIml = new AllLiveBasePagerIml(context);
    }

    public String getLiveId() {
        return mLiveId;
    }

    public int getLiveType() {
        return mLiveType;
    }

    public LiveHttpManager getHttpManager() {
        return mHttpManager;
    }

    public LiveHttpResponseParser getHttpResponseParser() {
        return mHttpResponseParser;
    }

    public String getStuCouId() {
        return mStuCouId;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public String getMainTeacherStr() {
        return teacherAction.getmMainTeacherStr();
    }

    public String getCounTeacherStr() {
        return teacherAction.getmCounTeacherStr();
    }

    public LiveTopic getLiveTopic() {
        return mLiveTopic;
    }

    public AtomicBoolean getmIsLand() {
        return mIsLand;
    }

    public String getStuName() {
        return mGetInfo.getStuName();
    }

    public void setmIsLand(AtomicBoolean mIsLand) {
        this.mIsLand = mIsLand;
    }

    /**
     * 添加直播间 业务Bill
     *
     * @param bll
     */
    public void addBusinessBll(LiveBaseBll bll) {
        if (bll instanceof TopicAction) {
            mTopicActions.add((TopicAction) bll);
        }
        if (bll instanceof NoticeAction) {
            //获得需要的notice type值
            int[] noticeFilter = ((NoticeAction) bll).getNoticeFilter();
            List<NoticeAction> noticeActions = null;
            if (noticeFilter != null && noticeFilter.length > 0) {
                for (int i = 0; i < noticeFilter.length; i++) {
                    if ((noticeActions = mNoticeActionMap.get(noticeFilter[i])) == null) {
                        noticeActions = new ArrayList<>();
                        mNoticeActionMap.put(noticeFilter[i], noticeActions);
                    }
                    noticeActions.add((NoticeAction) bll);
                }
            }
        }
        if (bll instanceof MessageAction) {
            mMessageActions.add((MessageAction) bll);
        }
        businessBlls.add(bll);
    }

    public void removeBusinessBll(LiveBaseBll bll) {
        businessBlls.remove(bll);
        if (bll instanceof TopicAction) {
            mTopicActions.remove(bll);
        }
        if (bll instanceof NoticeAction) {
            //获得需要的notice type值
            int[] noticeFilter = ((NoticeAction) bll).getNoticeFilter();
            List<NoticeAction> noticeActions = null;
            if (noticeFilter != null && noticeFilter.length > 0) {
                for (int i = 0; i < noticeFilter.length; i++) {
                    if ((noticeActions = mNoticeActionMap.get(noticeFilter[i])) != null) {
                        noticeActions.remove(bll);
                    }
                }
            }
        }
        if (bll instanceof MessageAction) {
            mMessageActions.remove(bll);
        }
    }

    public List<LiveBaseBll> getBusinessBlls() {
        return businessBlls;
    }

    public void setTeacherAction(TeacherAction teacherAction) {
        this.teacherAction = teacherAction;
    }

    public void onCreate() {
        liveUidRx = new LiveUidRx(mContext, true);
        liveUidRx.onCreate();
        //activity创建
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onCreate(businessShareParamMap);
        }
    }

    // 初始化相关
    public void getInfo(LiveGetInfo getInfo) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getInfo:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (getInfo == null) {
            HttpCallBack callBack = new HttpCallBack(false) {

                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    mLogtf.d("getInfo:onPmSuccess" + responseEntity.getJsonObject());
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                        if (object.optInt("isAllow", 1) == 0) {
                            if (mVideoAction != null) {
                                mVideoAction.onLiveDontAllow(object.optString("refuseReason"));
                            }
                            return;
                        }
                    }
                    LiveGetInfo getInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, mLiveType, mForm);
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
                }
            };
            // 直播
            if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                mHttpManager.liveGetInfo(enstuId, mCourseId, mLiveId, 0, callBack);
            }
            // 录播
            else if (mLiveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {
                mHttpManager.liveTutorialGetInfo(enstuId, mLiveId, callBack);
            } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                mHttpManager.liveLectureGetInfo(enstuId, mLiveId, callBack);
            }
        } else {
            onGetInfoSuccess(getInfo);
        }
    }

    /**
     * 获取getInfo成功
     */
    private void onGetInfoSuccess(LiveGetInfo getInfo) {
        logger.e("=======>onGetInfoSuccess");
        this.mGetInfo = getInfo;
        if (this.mGetInfo == null) {
            onLiveFailure("服务器异常", null);
            return;
        }
        boolean newCourse = mBaseActivity.getIntent().getBooleanExtra("newCourse", false);
        mLogtf.d("onGetInfoSuccess:newCourse=" + newCourse);
        mGetInfo.setNewCourse(newCourse);
        if (liveLog != null) {
            liveLog.setGetInfo(mGetInfo);
        }
        liveUidRx.setLiveGetInfo(getInfo);
        getInfo.setStuCouId(mStuCouId);
        if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_EN) {
            appID = UmsConstants.ARTS_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {//
            appID = UmsConstants.LIVE_CN_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST);
        } else {
            appID = UmsConstants.LIVE_APP_ID;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        liveAndBackDebugIml.onGetInfo(getInfo, appID);
        sysTimeOffset = (long) mGetInfo.getNowTime() - System.currentTimeMillis() / 1000;
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mGetInfo.setMode(mLiveTopic.getMode());
        long enterTime = 0;
        try {
            enterTime = enterTime();
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        if (mGetInfo.getStat() == 1) {
            if (mVideoAction != null) {
                mVideoAction.onTeacherNotPresent(true);
            }
            mLogtf.d("onGetInfoSuccess:onTeacherNotPresent");
        }
        String s = "onGetInfoSuccess:enterTime=" + enterTime + ",stat=" + mGetInfo.getStat();
        if (mVideoAction != null) {
            mVideoAction.onLiveInit(mGetInfo);
        }
        logger.d("=======>onGetInfoSuccess 11111111");
        List<LiveBaseBll> businessBllTemps = new ArrayList<>(businessBlls);
        for (LiveBaseBll businessBll : businessBllTemps) {
            try {
                businessBll.onLiveInited(getInfo);
                logger.d("=======>onGetInfoSuccess 22222222:businessBll=" + businessBll);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
                logger.e("=======>onGetInfoSuccess 22222222:businessBll=" + businessBll, e);
            }
        }
        mLogtf.d("onGetInfoSuccess:old=" + businessBlls + ",new=" + businessBllTemps.size());
        businessBllTemps.clear();
        logger.d("=======>onGetInfoSuccess 333333333");
        String channel = "";
        String eChannel = "";
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {
            channel = "1" + ROOM_MIDDLE + mGetInfo.getId();
        } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            if (StringUtils.isEmpty(mGetInfo.getRoomId())) {
                channel = "2" + ROOM_MIDDLE + mGetInfo.getId();
            } else {
                channel = "2" + ROOM_MIDDLE + mGetInfo.getId() + "-" + mGetInfo.getRoomId();
            }
        } else {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = this.mGetInfo.getStudentLiveInfo();
            mHttpManager.addBodyParam("teamId", studentLiveInfo.getTeamId());
            mHttpManager.addBodyParam("classId", "" + studentLiveInfo.getClassId());
            if (!StringUtils.isEmpty(studentLiveInfo.getCourseId())) {
                mCourseId = studentLiveInfo.getCourseId();
                mHttpManager.addBodyParam("courseId", mCourseId);
            }
            channel = mGetInfo.getId() + "-" + studentLiveInfo.getClassId();
            if (mGetInfo.ePlanInfo != null) {
                eChannel = mGetInfo.ePlanInfo.ePlanId + "-" + mGetInfo.ePlanInfo.eClassId;
            }
        }
        logger.e("=======>onGetInfoSuccess 444444444");
        s += ",liveType=" + mLiveType + ",channel=" + channel;
        String nickname = "s_" + mGetInfo.getLiveType() + "_"
                + mGetInfo.getId() + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
        if (TextUtils.isEmpty(eChannel) || LiveTopic.MODE_CLASS.equals(getMode())) {
            mIRCMessage = new NewIRCMessage(mBaseActivity, netWorkType, mGetInfo.getStuName(), nickname, mGetInfo, channel);
        } else {
            mIRCMessage = new NewIRCMessage(mBaseActivity, netWorkType, mGetInfo.getStuName(), nickname, mGetInfo, channel, eChannel);
        }
        //mIRCMessage = new IRCMessage(mBaseActivity, netWorkType, mGetInfo.getStuName(), nickname, (TextUtils.isEmpty(eChannel)|| LiveTopic.MODE_CLASS.equals(getMode()))?channel:channel,eChannel);
        if (mGetInfo != null && mGetInfo.ePlanInfo != null) {
            mIRCMessage.modeChange(mGetInfo.getMode());
        }

        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        logger.e("=======>mIRCMessage.create()");
        mLogtf.d(s);
        liveVideoBll.onLiveInit(getInfo, mLiveTopic);
        mShareDataManager.put(LiveVideoConfig.SP_LIVEVIDEO_CLIENT_LOG, getInfo.getClientLog(), ShareDataManager.SHAREDATA_NOT_CLEAR);
        initExtInfo(getInfo);
    }


    private static final long RETRY_DELAY = 3000;
    private static final long MAX_RETRY_TIME = 4;
    private Runnable initArtsExtLiveInfoTask = new Runnable() {
        int retryCount;

        @Override
        public void run() {
            logger.e("======>initArtsExtLiveInfoTask run:");
            mHttpManager.getArtsExtLiveInfo(mLiveId, mStuCouId, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    ArtsExtLiveInfo info = mHttpResponseParser.parseArtsExtLiveInfo(responseEntity);
                    mGetInfo.setArtsExtLiveInfo(info);
                    List<LiveBaseBll> businessBllTemps = new ArrayList<>(businessBlls);
                    for (LiveBaseBll businessBll : businessBllTemps) {
                        try {
                            businessBll.onArtsExtLiveInited(mGetInfo);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                    mLogtf.d("onGetInfoSuccess:old=" + businessBlls + ",new=" + businessBllTemps.size());
                    businessBllTemps.clear();
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    logger.e("======>onPmFailure run:");
                    retry();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    logger.e("======>onPmError run:");
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
        if (getInfo != null && getInfo.getIsArts() == LiveVideoSAConfig.ART_EN && !exInfoInited.get()) {
            logger.e("======>initExtInfo called:");
            exInfoInited.set(true);
            postDelayedIfNotFinish(initArtsExtLiveInfoTask, 0);
        }
    }

    private final IRCCallback mIRCcallback = new IRCCallback() {
        String lastTopicstr = "";

        @Override
        public void onStartConnect() {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onStartConnect();
                }
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onConnect(connection);
                }
            }
        }

        @Override
        public void onRegister() {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onRegister();
                }
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onDisconnect(connection, isQuitting);
                }
            }
        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onMessage(target, sender, login, hostname, text);
                }
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                }
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            onTopic(channel, topic, "", 0, true, channel);
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String
                notice, String channelId) {
            try {
                JSONObject object = new JSONObject(notice);
                int mtype = object.getInt("type");
                com.xueersi.lib.log.Loger.e("LiveBll2", "=======>onNotice:" + mtype + ":" + this);
                ///////播放器相关/////////
                switch (mtype) {
                    case XESCODE.MODECHANGE:
                        String mode = object.getString("mode");
                        if (mode != null && mIRCMessage != null && mGetInfo != null && mGetInfo.ePlanInfo != null) {
                            mIRCMessage.modeChange(mode);
                        }
                        if (!(mLiveTopic.getMode().equals(mode))) {
                            String oldMode = mLiveTopic.getMode();
                            mLiveTopic.setMode(mode);
                            mGetInfo.setMode(mode);
                            boolean isPresent = isPresent(mode);
                            if (mVideoAction != null) {
                                mVideoAction.onModeChange(mode, isPresent);
                                mLogtf.d(SysLogLable.switchLiveMode, "onNotice:mode=" + mode + ",isPresent=" + isPresent);
                                if (!isPresent) {
                                    mVideoAction.onTeacherNotPresent(true);
                                }
                            }
                            liveVideoBll.onModeChange(mode, isPresent);
                            for (int i = 0; i < businessBlls.size(); i++) {
                                businessBlls.get(i).onModeChange(oldMode, mode, isPresent);
                            }
                        }
                        break;
                    default:
                        break;
                }
                //////////////////////
                List<NoticeAction> noticeActions = mNoticeActionMap.get(mtype);
                if (noticeActions != null && noticeActions.size() > 0) {
                    for (NoticeAction noticeAction : noticeActions) {
                        try {
                            noticeAction.onNotice(sourceNick, target, object, mtype);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                } else {
                    if (UselessNotice.isUsed(mtype)) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("logtype", "onNotice");
                            hashMap.put("livetype", "" + mLiveType);
                            hashMap.put("liveid", "" + mLiveId);
                            hashMap.put("arts", "" + mGetInfo.getIsArts());
                            hashMap.put("pattern", "" + mGetInfo.getPattern());
                            hashMap.put("type", "" + mtype);
                            UmsAgentManager.umsAgentDebug(mContext, LogConfig.LIVE_NOTICE_UNKNOW, hashMap);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            } catch (Exception e) {
                logger.e("onNotice", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed, String channelId) {
            if (lastTopicstr.equals(topicstr)) {
                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
                return;
            }
            logger.e("======>onTopic:" + topicstr);
            if (TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            try {
                JSONObject jsonObject = new JSONObject(topicstr);
                LiveTopic liveTopic = mHttpResponseParser.parseLiveTopic(mLiveTopic, jsonObject, mLiveType);
                boolean teacherModeChanged = !mLiveTopic.getMode().equals(liveTopic.getMode());
                ////直播相关//////
                if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                    //模式切换
                    if (!(mLiveTopic.getMode().equals(liveTopic.getMode()))) {
                        String oldMode = mLiveTopic.getMode();
                        mLiveTopic.setMode(liveTopic.getMode());
                        // Loger.d("___channel: "+channel+"  mode: "+liveTopic.getMode()+"  topic:  "+topicstr);
                        mGetInfo.setMode(liveTopic.getMode());
                        boolean isPresent = isPresent(mLiveTopic.getMode());
                        if (mVideoAction != null) {
                            mVideoAction.onModeChange(mLiveTopic.getMode(), isPresent);
                            mLogtf.d(SysLogLable.switchLiveMode, "onTopic:mode=" + liveTopic.getMode() + ",isPresent=" + isPresent);
                        }
                        if (mIRCMessage != null) {
                            mIRCMessage.modeChange(mLiveTopic.getMode());
                        }
                        liveVideoBll.onModeChange(mLiveTopic.getMode(), isPresent);
                        for (int i = 0; i < businessBlls.size(); i++) {
                            businessBlls.get(i).onModeChange(oldMode, mLiveTopic.getMode(), isPresent);
                        }
                    }
                    if (mVideoAction != null) {
                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())) {
                            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                                mVideoAction.onTeacherNotPresent(true);
                            }
                        }
                    }
                }
                //////////////
                if (teacherModeChanged) {

                    mLiveTopic.setMode(liveTopic.getMode());
                    Loger.setDebug(true);
                    //  Loger.d("___channel: "+channel+"  mode: "+liveTopic.getMode()+"  topic:  "+topicstr);
                    mGetInfo.setMode(liveTopic.getMode());
                }
                if (mTopicActions != null && mTopicActions.size() > 0) {
                    for (TopicAction mTopicAction : mTopicActions) {
                        try {
                            mTopicAction.onTopic(liveTopic, jsonObject, teacherModeChanged);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(e);
                        }

                    }
                }
                mLiveTopic.copy(liveTopic);
            } catch (Exception e) {
                mLogtf.e("onTopic", e);
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onUserList(channel, users);
                }
            }
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {
            // 分发消息
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onJoin(target, sender, login, hostname);
                }
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
            logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                    + sourceHostname + ",reason=" + reason);
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
                }
            }
        }

        @Override
        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
                recipientNick, String reason) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
                }
            }
        }

        @Override
        public void onUnknown(String line) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onUnknown(line);
                }
            }
        }
    };

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
            // 开始时间
            String startTime = liveTime.split(" ")[0];
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
            // 开始时间
            String endTime = liveTime.split(" ")[1];
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

    public long getSysTimeOffset() {
        return sysTimeOffset;
    }

    private void onLiveFailure(String msg, Runnable runnable) {
        if (runnable == null) {
            showToast(msg);
        } else {
            showToast(msg + "，稍后重试");
            postDelayedIfNotFinish(runnable, 1000);
        }
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
    // 发送消息相关

    /**
     * 发送 notice 消息
     *
     * @param targetName notice消息接收方 当target 为null 时 将广播此消息
     * @param data
     * @return
     */
    public boolean sendNotice(String targetName, JSONObject data) {
        boolean result = false;
        try {
            if (targetName != null) {
                mIRCMessage.sendNotice(targetName, data.toString());
            } else {
                mIRCMessage.sendNotice(data.toString());
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean sendNoticeToMain(JSONObject data) {
        if (getMainTeacherStr() != null) {
            return sendNotice(getMainTeacherStr(), data);
        }
        return false;
    }

    public void sendMessageMain(JSONObject data) {
        sendMessage(getMainTeacherStr(), data);
    }

    /**
     * 向辅导发送消息
     *
     * @param data 消息内容
     */
    public boolean sendNoticeToCoun(JSONObject data) {
        if (getCounTeacherStr() != null) {
            return sendNotice(getCounTeacherStr(), data);
        }
        return false;
    }

    public void sendMessageCoun(JSONObject data) {
        sendMessage(getCounTeacherStr(), data);
    }

    /**
     * 发消息
     *
     * @param target 目标
     * @param data   信息
     */
    public void sendMessage(String target, JSONObject data) {
        mIRCMessage.sendMessage(target, data.toString());
    }

    public boolean sendMessage(JSONObject data) {
        mIRCMessage.sendMessage(data.toString());
        return true;
    }

    ///日志上传相关
//    @Override
//    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
//        liveAndBackDebugIml.umsAgentDebugSys(eventId, mData);
//    }
//
//    @Override
//    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
//        liveAndBackDebugIml.umsAgentDebugInter(eventId, mData);
//    }
//
//    @Override
//    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
//        liveAndBackDebugIml.umsAgentDebugPv(eventId, mData);
//    }
//
//    @Override
//    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {
//        liveAndBackDebugIml.umsAgentDebugSys(eventId, stableLogHashMap);
//    }
//
//    @Override
//    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {
//        liveAndBackDebugIml.umsAgentDebugInter(eventId, stableLogHashMap);
//    }
//
//    @Override
//    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {
//        liveAndBackDebugIml.umsAgentDebugPv(eventId, stableLogHashMap);
//    }

    /**
     * 得到当前模式
     */
    public String getMode() {
        String mode;
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
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

    /**
     * activity onPasuse
     */
    public void onPause() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onPause();
        }
    }

    /**
     * 得到短名字
     *
     * @return
     */
    public String getNickname() {
        return mIRCMessage.getNickname();
    }

    /**
     * 得到连接名字
     *
     * @return
     */
    public String getConnectNickname() {
        if (mIRCMessage == null) {
            return "";
        }
        return mIRCMessage.getConnectNickname();
    }

    /**
     * activity onResume
     */
    public void onResume() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onResume();
        }
    }

    /**
     * activity onStop
     */
    public void onStop() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onStop();
        }
    }

    public boolean onUserBackPressed() {
        boolean onUserBackPressed = allLiveBasePagerIml.onUserBackPressed();
        return onUserBackPressed;
    }

    /**
     * activity  onDestroy
     */
    public void onDestory() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onDestory();
        }
        allLiveBasePagerIml.onDestory();
        businessShareParamMap.clear();
        businessBlls.clear();
        mNoticeActionMap.clear();
        mTopicActions.clear();
        mMessageActions.clear();
        mVideoAction = null;
        if (mIRCMessage != null) {
            mIRCMessage.destory();
        }
        if (liveUidRx != null) {
            liveUidRx.onDestory();
        }
    }

    public void onIRCmessageDestory() {
        if (mIRCMessage != null) {
            mIRCMessage.destory();
        }
    }

    /////////////////////////////  播放相关 //////////////////////////////////

    private VideoAction mVideoAction;

    public void setVideoAction(VideoAction videoAction) {
        this.mVideoAction = videoAction;
    }

    public void setLiveVideoBll(LiveVideoBll liveVideoBll) {
        this.liveVideoBll = liveVideoBll;
    }

//    public void liveGetPlayServer() {
//        if (liveVideoBll != null) {
//            liveVideoBll.liveGetPlayServer();
//        }
//    }

    /**
     * 当前状态
     *
     * @param mode 模式
     */
    private boolean isPresent(String mode) {
        boolean isPresent = true;
        if (mIRCMessage != null && mIRCMessage.onUserList()) {
            isPresent = teacherAction.isPresent(mode);
        }
        return isPresent;
    }

    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    /**
     * 当前状态，老师是不是在直播间
     */
    public boolean isPresent() {
        return isPresent(mLiveTopic.getMode());
    }

    /**
     * 直播间内模块间 数据共享池
     */
    private HashMap<String, Object> businessShareParamMap = new HashMap<String, Object>();

    /**
     * 各模块 调用此方法 暴露自己需要和其他模块共享的参数
     *
     * @param key
     * @param value
     */
    public void addBusinessShareParam(String key, Object value) {
        synchronized (businessShareParamMap) {
            businessShareParamMap.put(key, value);
        }
    }

    /**
     * 各模块 调用此方法 暴露自己需要和其他模块共享的参数
     *
     * @param key
     */
    public void removeBusinessShareParam(String key) {
        synchronized (businessShareParamMap) {
            businessShareParamMap.remove(key);
        }
    }

    HashMap<Class, ArrayList<LiveEvent>> eventMap = new HashMap<>();

    public void postEvent(Class c, Object object) {
        ArrayList<LiveEvent> arrayList = eventMap.get(c);
        if (arrayList != null) {
            mLogtf.d("postEvent(isEmpty):c=" + c + ",size=" + arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).onEvent(object);
            }
        } else {
            mLogtf.d("postEvent(null):c=" + c);
        }
    }

    public void registEvent(Class c, LiveEvent object) {
        ArrayList<LiveEvent> arrayList = eventMap.get(c);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            eventMap.put(c, arrayList);
        }
        arrayList.add(object);
    }

    /**
     * 各模块调用此方法  查找其他模块暴露的 参数信息
     *
     * @param key
     * @return
     */
    public Object getBusinessShareParam(String key) {
        synchronized (businessShareParamMap) {
            return businessShareParamMap.get(key);
        }
    }

    /**
     * 测试notice
     */
    public void testNotice(String notice) {
        mIRCcallback.onNotice("", "", "", "", notice, "");
    }
}
