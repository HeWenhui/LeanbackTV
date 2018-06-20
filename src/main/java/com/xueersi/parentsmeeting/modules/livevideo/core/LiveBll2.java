package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播间管理类
 *
 * @author chekun
 * created  at 2018/6/20 10:32
 */
public class LiveBll2 extends BaseBll implements LiveAction {

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
    private final int mLiveType;

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


    private final LiveTopic mLiveTopic = new LiveTopic();

    /**
     * 校准系统时间
     */
    private long sysTimeOffset;

    private Teacher mCounteacher;

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
    private IRCMessage mIRCMessage;


    public LiveBll2(Context context, String vStuCourseID, String courseId, String vSectionID, int form, LiveGetInfo
            liveGetInfo) {
        super(context);

        this.mStuCouId = vStuCourseID;
        this.mCourseId = courseId;
        this.mLiveId = vSectionID;
        this.mLiveType = LIVE_TYPE_LIVE;
        this.mForm = form;

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


    public void addBusinessBll(LiveBaseBll bll) {

        if (bll instanceof TopicAction) {
            mTopicActions.add((TopicAction) bll);
        }
        if (bll instanceof NoticeAction) {
            int[] noticeFilter = ((NoticeAction) bll).getNoticeFilter();
            List<NoticeAction> noticeActions = null;
            if (noticeFilter != null && noticeFilter.length > 0) {
                for (int i = 0; i < noticeFilter.length; i++) {
                    if ((noticeActions = mNoticeActionMap.get(i)) == null) {
                        noticeActions = new ArrayList<>();
                        mNoticeActionMap.put(i, noticeActions);
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
            if (mLiveType == LIVE_TYPE_LIVE) {
                mHttpManager.liveGetInfo(enstuId, mCourseId, mLiveId, 0, callBack);
            }
            // 辅导
            else if (mLiveType == LIVE_TYPE_TUTORIAL) {
                mHttpManager.liveTutorialGetInfo(enstuId, mLiveId, callBack);
            } else if (mLiveType == LIVE_TYPE_LECTURE) {
                mHttpManager.liveLectureGetInfo(enstuId, mLiveId, callBack);
            }
        } else {
            onGetInfoSuccess(getInfo);
        }
    }


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

        sysTimeOffset = (long) mGetInfo.getNowTime() - System.currentTimeMillis() / 1000;
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        mGetInfo.setMode(mLiveTopic.getMode());
        long enterTime = 0;
        try {
            enterTime = enterTime();
        } catch (Exception e) {
        }

        mCounteacher = new Teacher(mGetInfo.getTeacherName());
        String s = "onGetInfoSuccess:enterTime=" + enterTime + ",stat=" + mGetInfo.getStat();
        LiveGetInfo.NewTalkConfEntity talkConfEntity = new LiveGetInfo.NewTalkConfEntity();
        talkConfEntity.setHost(mGetInfo.getTalkHost());
        talkConfEntity.setPort(mGetInfo.getTalkPort());
        talkConfEntity.setPwd(mGetInfo.getTalkPwd());
        List<LiveGetInfo.NewTalkConfEntity> newTalkConf = new ArrayList<LiveGetInfo.NewTalkConfEntity>();
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
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = this.mGetInfo.getStudentLiveInfo();
            mHttpManager.addBodyParam("teamId", studentLiveInfo.getTeamId());
            mHttpManager.addBodyParam("classId", "" + studentLiveInfo.getClassId());
            if (!StringUtils.isEmpty(studentLiveInfo.getCourseId())) {
                mCourseId = studentLiveInfo.getCourseId();
                mHttpManager.addBodyParam("courseId", mCourseId);
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
        mLogtf.d(s);
    }



    private final IRCCallback mIRCcallback = new IRCCallback() {

        @Override
        public void onStartConnect() {

        }

        @Override
        public void onConnect(IRCConnection connection) {

        }

        @Override
        public void onRegister() {

        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {

        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {

        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {

        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {

        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String
                notice) {
            try {
                JSONObject object = new JSONObject(notice);
                int mtype = object.getInt("type");
                List<NoticeAction>  noticeActions = mNoticeActionMap.get(mtype);
                if(noticeActions != null && noticeActions.size() > 0){
                    for (NoticeAction noticeAction : noticeActions) {
                        noticeAction.onNotice(object,mtype);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {

        }

        @Override
        public void onUserList(String channel, User[] users) {

        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {

        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

        }

        @Override
        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

        }

        @Override
        public void onUnknown(String line) {

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

    @Override
    public boolean sendNotice(String targetName, JSONObject data) {
        boolean result = false;
        try {
            if(targetName != null){
                mIRCMessage.sendNotice(targetName, data.toString());
            }else{
                mIRCMessage.sendNotice(data.toString());
            }
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean sendMessage(JSONObject data) {
        boolean result = false;
        try {
            if (mLiveTopic.isDisable()) {
                result = false;
            } else {
                mIRCMessage.sendMessage(data.toString());
                result = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

    ///日志上传相关

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        setLogParam(eventId,mData);
        UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
    }


    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }


    /**
     * 上传log 添加 公共参数
     * @param eventId
     * @param mData
     */
    private void setLogParam(String eventId, Map<String, String> mData) {
        mData.put("userid", mGetInfo.getStuId());
        mData.put("uname", mGetInfo.getUname());
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            mData.put("classid", studentLiveInfo.getClassId());
            mData.put("teamid", studentLiveInfo.getTeamId());
        }
        mData.put("courseid", mCourseId);
        mData.put("teacherid", mGetInfo.getMainTeacherId());
        mData.put("coachid", mGetInfo.getTeacherId());
        mData.put("liveid", mLiveId);
        mData.put("livetype", "" + mLiveType);
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        mData.put("teacherrole", LiveTopic.MODE_CLASS.equals(getMode()) ? "1" : "4");
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadShow, mData);
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


    /**
     * activity onPasuse
     */
    public void onPause() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onPause();
        }

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

    /**
     * activity  onDestroy
     */
    public void onDestory() {
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.onDestory();
        }
        businessBlls.clear();
    }
}
