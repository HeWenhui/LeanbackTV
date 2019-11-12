package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.JsonUtil;
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
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.LivePluginGrayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LiveModuleConfigInfo;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LivePlugin;
import com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity.LivePluginRequestParam;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveActivityState;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.SysLogLable;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.SubGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePostEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveBusinessResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpAction;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.video.TeacherIsPresent;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

import static com.xueersi.common.sharedata.ShareDataManager.SHAREDATA_NOT_CLEAR;

/**
 * 直播间管理类
 *
 * @author chekun
 * created  at 2018/6/20 10:32
 */
public class LiveBll2 extends BaseBll implements TeacherIsPresent {
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
     * 需处理 progress 业务集合
     */
    private List<ProgressAction> mProgressActions = new ArrayList<>();
    /**
     * 所有业务bll 集合
     */
    private List<LiveBaseBll> businessBlls = new ArrayList<>();
    private Handler mHandler = LiveMainHandler.getMainHandler();
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
    LiveAndBackDebug liveAndBackDebugIml;
    private int mState = LiveActivityState.INITIALIZING;

    /**
     * IRC 消息回调
     **/
    private IRCCallback mIRCcallback;
    private LiveBusinessResponseParser mBigLiveHttpParser;
    LivePluginRequestParam mLivePluginRequestParam;
    /**
     * 灰度控制开关控制
     */
    LiveModuleConfigInfo mLiveModuleConfigInfo;
    String lastTopic = "";
    String lastChannel = "";
    String lastSetBy = "";
    long lastDate = 0;
    boolean lastChanged;
    String lastChannelId;
    AbstractBusinessDataCallBack grayControl;

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
        boolean isBigLive = mBaseActivity.getIntent().getBooleanExtra("isBigLive", false);
        if (isBigLive) {
            liveAndBackDebugIml = new LiveDebugBigClassIml(context, mLiveType, mLiveId, mCourseId);
        } else {
            liveAndBackDebugIml = new LiveAndBackDebugIml(context, mLiveType, mLiveId, mCourseId);
        }
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
        boolean isBigLive = mBaseActivity.getIntent().getBooleanExtra("isBigLive", false);
        if (isBigLive) {
            liveAndBackDebugIml = new LiveDebugBigClassIml(context, mLiveType, mLiveId, mCourseId);
        } else {
            liveAndBackDebugIml = new LiveAndBackDebugIml(context, mLiveType, mLiveId, mCourseId);
        }
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

    public LiveHttpAction getLiveHttpAction() {
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
        if (bll instanceof ProgressAction) {
            mProgressActions.add((ProgressAction) bll);
        }
        businessBlls.add(bll);
    }

    /**
     * 添加直播间 业务Bill
     *
     * @param bll
     */
    public void addBusinessBllCreate(LiveBaseBll bll) {
        addBusinessBll(bll);
        bll.onCreate(businessShareParamMap);
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
        if (bll instanceof ProgressAction) {
            mProgressActions.remove(bll);
        }
    }

    public List<LiveBaseBll> getBusinessBlls() {
        return businessBlls;
    }

    public void setTeacherAction(TeacherAction teacherAction) {
        this.teacherAction = teacherAction;
    }

    public void onCreate() {
        mState = LiveActivityState.CREATED;
        liveUidRx = new LiveUidRx(mContext, true);
        liveUidRx.onCreate();
        //activity创建
        long before = System.currentTimeMillis();
        ArrayList<LiveBllLog.BusinessTime> businessTimes = new ArrayList<>();
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onCreate(businessShareParamMap);
            long time = (System.currentTimeMillis() - before);
            if (time > 5) {
                LiveBllLog.BusinessTime businessTime = new LiveBllLog.BusinessTime(businessBll.getClass().getSimpleName(), time);
                businessTimes.add(businessTime);
            }
            before = System.currentTimeMillis();
        }
        LiveBllLog.onCreateEnd(mBaseActivity, businessTimes);
    }

    // 初始化相关
    public void getInfo(LiveGetInfo getInfo) {
        mLogtf.d("getInfo:liveId=" + mLiveId);
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
                mHttpManager.liveGetInfo(mCourseId, mLiveId, 0, callBack);
            }
            // 录播
            else if (mLiveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {
                mHttpManager.liveTutorialGetInfo(mLiveId, callBack);
            } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                mHttpManager.liveLectureGetInfo(mLiveId, callBack);
            }
        } else {
            onGetInfoSuccess(getInfo);
        }
    }


    /**
     * 获取大班整合直播间数据
     *
     * @param getInfo
     */
    public void getBigLiveInfo(LiveGetInfo getInfo) {

        if (mBigLiveHttpParser == null) {
            mBigLiveHttpParser = new LiveBusinessResponseParser();
        }

        if (getInfo == null) {
            HttpCallBack callBack = new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    mLogtf.d("getInfo:onPmSuccess" + responseEntity.getJsonObject());
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    LiveGetInfo liveGetInfo = mBigLiveHttpParser.parseLiveEnter(object, mLiveTopic, mLiveType, mForm);
                    onGetInfoSuccess(liveGetInfo);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    mLogtf.d("getInfo:onPmFailure=" + msg);
                    onLiveFailure("初始化失败", new Runnable() {
                        @Override
                        public void run() {
                            getBigLiveInfo(null);
                        }
                    });
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    mLogtf.d("getInfo:onPmError=" + responseEntity.getErrorMsg());
                }
            };
            int iPlanId = 0;
            int iStuCouId = -1;
            try {
                iPlanId = Integer.parseInt(mLiveId);
                iStuCouId = Integer.parseInt(mStuCouId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mHttpManager.bigLiveEnter(iPlanId, mLiveType, iStuCouId, callBack);
        } else {
            onGetInfoSuccess(getInfo);

        }

    }


    /**
     * 获取getInfo成功
     */
    private void onGetInfoSuccess(LiveGetInfo getInfo) {

        this.mGetInfo = getInfo;
        if (this.mGetInfo == null) {
            onLiveFailure("服务器异常", null);
            return;
        }

        if (getInfo.isBigLive()) {
            initBigLiveRoom(getInfo);
            grayBusinessControl();
        } else {
            initLiveRoom(getInfo);
        }
        if (mGetInfo.getPattern() == 8) {
            get1V2VirtualStuData();
        }
    }

    /**
     * 初始化大班整合直播间
     */

    private void initBigLiveRoom(LiveGetInfo getInfo) {
        // 添加网络请求公共参数
        if (mHttpManager != null) {
            mHttpManager.addHeaderParams("switch-grade", getInfo.getGrade() + "");
            String subjectId = (getInfo.getSubjectIds() != null && getInfo.getSubjectIds().length > 0) ?
                    getInfo.getSubjectIds()[0] : "";
            mHttpManager.addHeaderParams("switch-subject", subjectId);
            mHttpManager.addHeaderParams("bizId", mLiveType + "");
            mHttpManager.addHeaderParams("SESSIONID", AppBll.getInstance().getLiveSessionId());
            //Log.e("ckTrac","====>LiveBll2_initBigLiveRoom:"+ AppBll.getInstance().getLiveSessionId());
        }
        if (liveLog != null) {
            liveLog.setGetInfo(mGetInfo);
        }
        liveUidRx.setLiveGetInfo(getInfo);
        getInfo.setStuCouId(mStuCouId);

        appID = UmsConstants.LIVE_BUSINESS_APP_ID;
        liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        if (liveAndBackDebugIml instanceof LiveDebugGetInfo) {
            LiveDebugGetInfo liveDebugGetInfo = (LiveDebugGetInfo) liveAndBackDebugIml;
            liveDebugGetInfo.onGetInfo(getInfo, appID);
        }
        sysTimeOffset = mGetInfo.getNowTime() - System.currentTimeMillis() / 1000;
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mGetInfo.setMode(mLiveTopic.getMode());

        long enterTime = 0;
        try {
            enterTime = enterTime();
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }

        String s = "onGetInfoSuccess:enterTime=" + enterTime + ",stat=" + mGetInfo.getStat();
        if (mVideoAction != null) {
            mVideoAction.onLiveInit(mGetInfo);
        }


        addCommonData(getInfo, true);
        //链接IRC
        String channel = "";
        String eChannel = "";

        //http 添加公共参数()
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = this.mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId())) {
                mHttpManager.addBodyParam("teamId", studentLiveInfo.getTeamId());
            }
            if (!StringUtils.isEmpty(studentLiveInfo.getClassId())) {
                mHttpManager.addBodyParam("classId", "" + studentLiveInfo.getClassId());
            }
            if (!StringUtils.isEmpty(studentLiveInfo.getCourseId())) {
                mCourseId = studentLiveInfo.getCourseId();
                mHttpManager.addBodyParam("courseId", mCourseId);
            }
        }

        //缓存PsAppid，PsAppKey
        LiveAppUserInfo.getInstance().setPsAppId(getInfo.getPsAppId());
        LiveAppUserInfo.getInstance().setPsAppKey(getInfo.getPsAppKey());
        //缓存磐石 psimId ，psimpwd
        LiveAppUserInfo.getInstance().setPsimId(getInfo.getPsId());
        LiveAppUserInfo.getInstance().setPsimPwd(getInfo.getPsPwd());
        //设置ircNIck
        LiveAppUserInfo.getInstance().setIrcNick(getInfo.getIrcNick());
        updatePsInfo(getInfo);

        //房间号默认取第一个
        channel = "";
        String[] channelArray = null;
        if (getInfo.getIrcRoomList() != null && getInfo.getIrcRoomList().size() > 0) {
            channelArray = new String[getInfo.getIrcRoomList().size()];
            getInfo.getIrcRoomList().toArray(channelArray);
        }


        if (channelArray == null) {
            channelArray = new String[]{channel};
        }

        s += ",liveType=" + mLiveType + ",channel=" + channel;
        String nickname = getInfo.getIrcNick();
        mIRCMessage = new NewIRCMessage(mBaseActivity, nickname, mGetInfo.getId(),
                mGetInfo.getStudentLiveInfo().getClassId(), channelArray);
        mIRCcallback = new BigLiveIRCCallBackImp();
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        mLogtf.d(s);
        liveVideoBll.onLiveInit(getInfo, mLiveTopic);
        mShareDataManager.put(LiveVideoConfig.SP_LIVEVIDEO_CLIENT_LOG, getInfo.getClientLog(), SHAREDATA_NOT_CLEAR);

    }

    /**
     * 更新用户信息中的磐石信息
     **/
    private void updatePsInfo(LiveGetInfo getInfo) {
        try {
            if (getInfo != null) {
                //更新UserBll 中磐石信息
                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                if (!TextUtils.isEmpty(getInfo.getPsAppId())) {
                    myUserInfoEntity.setPsAppId(getInfo.getPsAppId());
                }
                if (!TextUtils.isEmpty(getInfo.getPsAppKey())) {
                    myUserInfoEntity.setPsAppClientKey(getInfo.getPsAppKey());
                }
                if (!TextUtils.isEmpty(getInfo.getPsId())) {
                    myUserInfoEntity.setPsimId(getInfo.getPsId());
                }
                if (!TextUtils.isEmpty(getInfo.getPsPwd())) {
                    myUserInfoEntity.setPsimPwd(getInfo.getPsPwd());
                }
                UserBll.getInstance().saveMyUserInfo(myUserInfoEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCommonData(LiveGetInfo getInfo, boolean isBase) {
        // 通知业务类 直播间初始数据获取成功
        List<LiveBaseBll> businessBllTemps = new ArrayList<>(businessBlls);
        ArrayList<LiveBllLog.BusinessTime> businessTimes = new ArrayList<>();
        long before = System.currentTimeMillis();
        for (LiveBaseBll businessBll : businessBllTemps) {
            logger.d("grayControl__initmoudle_data" + businessBll.getClass().getSimpleName());
            if (businessBll.getPluginId() == -1 && !isBase) {
                continue;
            }
            try {
                businessBll.onLiveInited(getInfo);
                long time = (System.currentTimeMillis() - before);
                if (time > 10) {
                    LiveBllLog.BusinessTime businessTime =
                            new LiveBllLog.BusinessTime(businessBll.getClass().getSimpleName(), time);
                    businessTimes.add(businessTime);
                }
                before = System.currentTimeMillis();
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                logger.e("=======>onGetInfoSuccess 22222222:businessBll=" + businessBll, e);
            }
        }
        LiveBllLog.onGetInfoEnd(getInfo, businessTimes);
        businessBllTemps.clear();
        if (!isBase && mIRCcallback != null) {
            logger.e("mTopicActions___channel=" + mTopicActions.size());
            if (mIRCcallback != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIRCcallback.onTopic(lastChannel, lastTopic, lastSetBy, lastDate, lastChanged, lastChannelId);

                    }
                }, 1000);
            }
        }
    }

    /**
     * 初始化直播间（非大班整合直播间）
     *
     * @param getInfo
     */
    private void initLiveRoom(LiveGetInfo getInfo) {
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
        if (liveAndBackDebugIml instanceof LiveDebugGetInfo) {
            LiveDebugGetInfo liveDebugGetInfo = (LiveDebugGetInfo) liveAndBackDebugIml;
            liveDebugGetInfo.onGetInfo(getInfo, appID);
        }
        sysTimeOffset = mGetInfo.getNowTime() - System.currentTimeMillis() / 1000;
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mGetInfo.setMode(mLiveTopic.getMode());
        long enterTime = 0;
        try {
            enterTime = enterTime();
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
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
        ArrayList<LiveBllLog.BusinessTime> businessTimes = new ArrayList<>();
        long before = System.currentTimeMillis();
        for (LiveBaseBll businessBll : businessBllTemps) {
            try {
                businessBll.onLiveInited(getInfo);
                long time = (System.currentTimeMillis() - before);
                if (time > 10) {
                    LiveBllLog.BusinessTime businessTime = new LiveBllLog.BusinessTime(businessBll.getClass()
                            .getSimpleName(), time);
                    businessTimes.add(businessTime);
                }
                before = System.currentTimeMillis();
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                logger.e("=======>onGetInfoSuccess 22222222:businessBll=" + businessBll, e);
            }
        }
        LiveBllLog.onGetInfoEnd(getInfo, businessTimes);
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
        String classId = "";
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            classId = studentLiveInfo.getClassId();
        }
        if (TextUtils.isEmpty(eChannel) || LiveTopic.MODE_CLASS.equals(getMode())) {
            mIRCMessage = new NewIRCMessage(mBaseActivity, nickname, mGetInfo.getId(), classId, channel);
            channel = "#" + channel;
            mIRCMessage = new NewIRCMessage(mBaseActivity, nickname, mGetInfo.getId(), classId, channel);
        } else {
            mIRCMessage = new NewIRCMessage(mBaseActivity, nickname, mGetInfo.getId(), classId, channel, eChannel);
            channel = "#" + channel;
            eChannel = "#" + eChannel;
            mIRCMessage = new NewIRCMessage(mBaseActivity, nickname, mGetInfo.getId(), classId, channel, eChannel);
        }


        if (mGetInfo != null && mGetInfo.ePlanInfo != null) {
            mIRCMessage.modeChange(mGetInfo.getMode());
        }

        mIRCcallback = new IRCCallBackImp();
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
        logger.e("=======>mIRCMessage.create()");
        mLogtf.d(s);
        liveVideoBll.onLiveInit(getInfo, mLiveTopic);
        mShareDataManager.put(LiveVideoConfig.SP_LIVEVIDEO_CLIENT_LOG, getInfo.getClientLog(), ShareDataManager
                .SHAREDATA_NOT_CLEAR);
        initExtInfo(getInfo);
        //英语1v2 开启定时器 监听直播进度
        if (isGroupClass()) {
            startGroupClassTimer();
        }
    }

    private void startGroupClassTimer() {
        int diffBegin = mGetInfo.getRecordStandliveEntity().getDiffBegin();
        long currentTime = SystemClock.elapsedRealtime();
        diffBegin += Math.round((double) (currentTime - mGetInfo.getCreatTime()) / 1000);
        mTimer = new Timer();
        mTimerTask = new ScanningTimerTask(diffBegin);
        mTimer.schedule(mTimerTask, 0, 1000);
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
                    mGetInfo.setBlockChinese(info.getBolockChinese() == 1);
                    mGetInfo.setArtsExtLiveInfo(info);
                    List<LiveBaseBll> businessBllTemps = new ArrayList<>(businessBlls);
                    for (LiveBaseBll businessBll : businessBllTemps) {
                        try {
                            businessBll.onArtsExtLiveInited(mGetInfo);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
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


    /**
     * irc 状态监听
     */
    private class IRCCallBackImp implements IRCCallback {

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
                                mLogtf.d(SysLogLable.switchLiveMode, "onNotice:mode=" + mode + ",isPresent=" +
                                        isPresent);
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            } catch (Exception e) {
                logger.e("onNotice", e);
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed, String
                channelId) {
            if (lastTopicstr.equals(topicstr)) {
                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
                return;
            }
            logger.e("======>onTopic:" + topicstr);
            if (TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            JSONTokener jsonTokener = null;
            try {
                jsonTokener = new JSONTokener(topicstr);
                JSONObject jsonObject = new JSONObject(jsonTokener);
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
                            mLogtf.d(SysLogLable.switchLiveMode, "onTopic:mode=" + liveTopic.getMode() + ",isPresent" +
                                    "=" + isPresent);
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }

                    }
                }
                mLiveTopic.copy(liveTopic);
            } catch (Exception e) {
                try {
                    if (jsonTokener != null) {
                        mLogtf.e("onTopic:token=" + jsonTokener, e);
                    } else {
                        mLogtf.e("onTopic", e);
                    }
                } catch (Exception e2) {
                    mLogtf.e("onTopic", e);
                }
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
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason,
                           String channel) {
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

    }

    ;


    /**
     * 大班整合 IRC 回调
     */
    private class BigLiveIRCCallBackImp extends IRCCallBackImp {


        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed,
                            String channelId) {
            if (lastTopicstr.equals(topicstr) && TextUtils.isEmpty(lastTopic)) {
                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
                return;
            }
            lastTopic = topicstr;
            lastChannel = channel;
            lastSetBy = setBy;
            lastDate = date;
            lastChanged = changed;
            lastChannelId = channelId;
            sendTopic(channel, topicstr, setBy, date, changed, channelId);
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                             String notice, String channelId) {
            try {
                JSONObject object = new JSONObject(notice);
                int mtype = object.getInt("type");
                com.xueersi.lib.log.Loger.e("LiveBll2", "=======>onNotice:" + mtype + ":" + this);

                List<NoticeAction> noticeActions = mNoticeActionMap.get(mtype);
                if (noticeActions != null && noticeActions.size() > 0) {
                    for (NoticeAction noticeAction : noticeActions) {
                        try {
                            noticeAction.onNotice(sourceNick, target, object, mtype);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            } catch (Exception e) {
                logger.e("onNotice", e);
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        public void sendTopic(String channel, String topicstr, String setBy, long date, boolean changed,
                              String channelId) {
            logger.e("======>onTopic:" + topicstr);
            if (TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            JSONTokener jsonTokener = null;
            try {
                jsonTokener = new JSONTokener(topicstr);
                JSONObject jsonObject = new JSONObject(jsonTokener);
                LiveTopic liveTopic = mBigLiveHttpParser.parseBigLiveTopic(mLiveTopic, jsonObject, mLiveType);
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
                            mLogtf.d(SysLogLable.switchLiveMode, "onTopic:mode=" + liveTopic.getMode() + ",isPresent" +
                                    "=" + isPresent);
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }

                    }
                }
                mLiveTopic.copy(liveTopic);
            } catch (Exception e) {
                try {
                    if (jsonTokener != null) {
                        mLogtf.e("onTopic:token=" + jsonTokener, e);
                    } else {
                        mLogtf.e("onTopic", e);
                    }
                } catch (Exception e2) {
                    mLogtf.e("onTopic", e);
                }
            }
        }
    }


    /**
     * 进入直播间时间
     *
     * @return
     */
    private long enterTime() {
        String liveTime = mGetInfo.getLiveTime();
        {
            // 开始时间
            String msg = "enterTime:liveTime=" + liveTime;
            Calendar calendar = Calendar.getInstance();
            long milliseconds1 = calendar.getTimeInMillis();
            calendar.setTimeInMillis(mGetInfo.getsTime() * 1000);
            long milliseconds2 = calendar.getTimeInMillis();
            msg += ",starttime=" + (milliseconds1 - milliseconds2) + "," + ((milliseconds1 - milliseconds2) / 60000);
            mLogtf.d(msg);
        }
        long milliseconds1, milliseconds2;
        {
            // 开始时间
            String msg = "enterTime:liveTime=" + liveTime;
            Calendar calendar = Calendar.getInstance();
            milliseconds1 = calendar.getTimeInMillis();
            calendar.setTimeInMillis(mGetInfo.geteTime() * 1000);
            milliseconds2 = calendar.getTimeInMillis();
            msg += ",endtime=" + (milliseconds1 - milliseconds2) + "," + ((milliseconds1 - milliseconds2) / 60000);
            mLogtf.d(msg);
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
        mState = LiveActivityState.STARTED;
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
        mState = LiveActivityState.RESUMED;
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onResume();
        }

        if (mGetInfo != null) {
            int diffBegin = mGetInfo.getRecordStandliveEntity().getDiffBegin();
            long currentTime = SystemClock.elapsedRealtime();
            diffBegin += Math.round((double) (currentTime - mGetInfo.getCreatTime()) / 1000);
            if (mTimerTask != null) {
                mTimerTask.setPosition(diffBegin);
            }
        }
    }

    /**
     * activity onStart
     */
    public void onStart() {
        mState = LiveActivityState.STARTED;
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onStart();
        }
    }

    /**
     * activity onStop
     */
    public void onStop() {
        mState = LiveActivityState.STOPPED;
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
    public void onDestroy() {
        mState = LiveActivityState.INITIALIZING;
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onDestroy();
        }
        allLiveBasePagerIml.onDestroy();
        businessShareParamMap.clear();
        businessBlls.clear();
        mNoticeActionMap.clear();
        mTopicActions.clear();
        mMessageActions.clear();
        mProgressActions.clear();
        mVideoAction = null;
        if (mIRCMessage != null) {
            mIRCMessage.destory();
        }
        if (liveUidRx != null) {
            liveUidRx.onDestroy();
        }
        //清空场次缓存信息
        LiveAppUserInfo.getInstance().clearCachData();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
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
        mProgressActions.add(liveVideoBll);
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
            if (teacherAction != null) {
                isPresent = teacherAction.isPresent(mode);
            }
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
        if (isGroupClass()) {
            return true;
        } else {
            return isPresent(mLiveTopic.getMode());
        }
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


    public void get1V2VirtualStuData(){
        if(mGetInfo==null || mGetInfo.getPattern()!=8
                || (mGetInfo.getRecordStandliveEntity()!=null && mGetInfo.getRecordStandliveEntity().getPartnerType() ==1)) {
            return;
        }
        final LivePostEntity entity = new LivePostEntity();
        entity.bizId = 3;
        if(!TextUtils.isEmpty(mGetInfo.getId())) {
            entity.planId = Integer.valueOf(mGetInfo.getId());
        }
        entity.gender = LiveAppUserInfo.getInstance().getSexProcess();
        if(mGetInfo.getRecordStandliveEntity()!=null) {
            entity.videoId = mGetInfo.getRecordStandliveEntity().getVideoId();
        }
        mHttpManager.get1V2VirtualStuData(entity, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                SubGroupEntity entity1 = mHttpResponseParser.parse1V2VirtualStuData(responseEntity);
                if(entity1!=null) {
                    mGetInfo.setSubGroupEntity(entity1);
                }
            }
        });
    }
    /**
     * 设置直播Plugin配置信息
     *
     * @param liveModuleConfigInfo
     */
    public synchronized void setLiveModuleConfigInfo(LiveModuleConfigInfo liveModuleConfigInfo) {

        mShareDataManager.put(LivePluginGrayConfig.LIVE_PLUGIN_CONFIG_INFO + getPluginKey(),
                JsonUtil.objectToJson(liveModuleConfigInfo), SHAREDATA_NOT_CLEAR, true);
    }

    public String getPluginKey() {
        if (mLivePluginRequestParam == null) {
            return "";
        }
        return "_" + mLivePluginRequestParam.bizId + "_" + mLivePluginRequestParam.planId + "_" +
                mLivePluginRequestParam.isPlayback;
    }


    public void grayBusinessControl() {
        if (grayControl != null && mGetInfo != null) {
            LivePluginRequestParam param = new LivePluginRequestParam();
            param.bizId = 2;
            if (!TextUtils.isEmpty(mGetInfo.getId())) {
                param.planId = Integer.valueOf(mGetInfo.getId());
            }
            param.url = mGetInfo.getInitModuleUrl();
            getLivePluingConfigInfo(param, grayControl);
        }
    }


    /**
     * 获取直播plugin配置信息
     *
     * @param callBack
     */
    public void getLivePluingConfigInfo(LivePluginRequestParam param, final AbstractBusinessDataCallBack callBack) {
        mLivePluginRequestParam = param;

        mHttpManager.getLivePluginConfigInfo(param, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("getLivePluingConfigInfo" + responseEntity.getJsonObject().toString());
                if (responseEntity != null) {

                    JSONObject json = (JSONObject) responseEntity.getJsonObject();
                    String jsonString = (String) responseEntity.getJsonObject().toString();
                    if (json != null) {
                        mLiveModuleConfigInfo = (LiveModuleConfigInfo) JsonUtil.jsonToObject(jsonString,
                                LiveModuleConfigInfo.class);
                        mGetInfo.setLiveModuleConfigInfo(mLiveModuleConfigInfo);
                    }
                    if (!isEmpty(mLiveModuleConfigInfo)) {
                        callBack.onDataSucess(mLiveModuleConfigInfo);
                    }
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                //super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                //super.onPmError(responseEntity);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //super.onFailure(call, e);
            }

        });
    }


    /**
     * 根据moduleId 查找 Plugin
     *
     * @param pluginId
     * @return
     */
    public LivePlugin getLivePluginByModuleId(int pluginId) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = mLiveModuleConfigInfo;
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (pluginId == plugins.get(i).pluginId) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }

        return plugin;
    }


    /**
     * 根据pluginName 查找 Plugin
     *
     * @param pluginName
     * @return
     */
    public LivePlugin getLivePluginByPluginName(String pluginName) {
        LivePlugin plugin = null;
        LiveModuleConfigInfo info = mLiveModuleConfigInfo;
        if (info != null && info.plugins != null) {
            List<LivePlugin> plugins = info.plugins;
            for (int i = 0; i < plugins.size(); i++) {
                if (pluginName.equals(plugins.get(i).pluginName)) {
                    plugin = plugins.get(i);
                    break;
                }
            }
        }
        return plugin;
    }


    /**
     * 根据moudlid key 返回属性
     *
     * @param pluginId
     * @param key
     * @return
     */
    public String getProperties(int pluginId, String key) {
        LivePlugin plugin = getLivePluginByModuleId(LivePluginGrayConfig.MOUDLE_GIFT);
        if (plugin != null) {
            Map<String, String> maplist = plugin.properties;
            if (maplist != null) {
                return maplist.get(key);
            }
        }
        return "";
    }


    /**
     * 根据moudlid 功能是否打开
     *
     * @param pluginId
     * @return
     */
    public boolean isMoudleAllowed(int pluginId) {
        LivePlugin plugin = getLivePluginByModuleId(LivePluginGrayConfig.MOUDLE_GIFT);
        if (plugin != null) {
            return plugin.isAllowed;
        }
        return false;
    }

    public void setGrayCtrolListener(AbstractBusinessDataCallBack grayControl) {
        this.grayControl = grayControl;
    }

    /**
     * 获取服务器时间
     *
     * @param callBack
     */
    public void getServerTime(AbstractBusinessDataCallBack callBack) {

        mHttpManager.getServerTime(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

            }

            @Override
            public void onPmFailure(Throwable error, String msg) {

            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {

            }

        });
    }

    private Timer mTimer;
    private ScanningTimerTask mTimerTask;

    class ScanningTimerTask extends TimerTask {
        int position;

        ScanningTimerTask(int position) {
            this.position = position;
            logger.d("onProgressBegin : positon = " + position);
            if (mProgressActions != null && mProgressActions.size() > 0) {
                for (ProgressAction mProgressAction : mProgressActions) {
                    try {
                        mProgressAction.onProgressBegin(position);
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }

                }
            }
        }

        @Override
        public void run() {
            position++;
            logger.d("onProgressChanged : position = " + position + ", mState = " + mState);
            if (mState == LiveActivityState.STOPPED) {
                if (position == 0) {
                    classBeginInBackground = true;
                }
                return;
            }
            if (mProgressActions != null && mProgressActions.size() > 0) {
                if (classBeginInBackground) {
                    for (ProgressAction mProgressAction : mProgressActions) {
                        try {
                            mProgressAction.onProgressChanged(0);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                    classBeginInBackground = false;
                }
                for (ProgressAction mProgressAction : mProgressActions) {
                    try {
                        mProgressAction.onProgressChanged(position);
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }

                }
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    boolean classBeginInBackground = false;

    boolean isGroupClass() {
        return mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_GROUP_CLASS;
    }
}
