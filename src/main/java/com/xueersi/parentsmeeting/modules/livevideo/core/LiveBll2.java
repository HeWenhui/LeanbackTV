package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.CommonRequestCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.TotalFrameStat;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;
import org.xutils.xutils.ex.HttpException;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 直播间管理类
 *
 * @author chekun
 * created  at 2018/6/20 10:32
 */
public class LiveBll2 extends BaseBll implements LiveAndBackDebug {

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

    /**
     * 辅导老师
     */
    private Teacher mCounteacher;

    /**
     * 主讲老师
     */
    private Teacher mMainTeacher;
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


    private String mCurrentDutyId;


    private static String Tag = "LiveBll2";


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

    public LiveBll2(Context context, String vSectionID, int type, int form) {
        super(context);


        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mForm = form;
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


    public LiveBll2(Context context, String vSectionID, String currentDutyId, int type, int form) {
        super(context);

        this.mLiveId = vSectionID;
        this.mLiveType = type;
        this.mCurrentDutyId = currentDutyId;
        this.mForm = form;
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


    public String getLiveId() {
        return mLiveId;
    }


    public int getLiveType() {

        return mLiveType;
    }


    public LiveHttpManager getHttpManager() {
        return mHttpManager;
    }


    public Teacher getCounteacher() {

        return mCounteacher;
    }


    public Teacher getMainTeacher() {
        return mMainTeacher;
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

                    if (mLiveType == LIVE_TYPE_LECTURE) {
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
            if (mLiveType == LIVE_TYPE_LIVE) {
                mHttpManager.liveGetInfo(enstuId, mCourseId, mLiveId, 0, callBack);
            }
            // 录播
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

        Log.e("LiveBll2", "=======>onGetInfoSuccess");

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
        Log.e("LiveBll2", "=======>onGetInfoSuccess 11111111");


        try {
            for (LiveBaseBll businessBll : businessBlls) {
                businessBll.onLiveInited(this.mGetInfo);
                Log.e("LiveBll2", "=======>onGetInfoSuccess 22222222");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("LiveBll2", "=======>onGetInfoSuccess 333333333");


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

        Log.e("LiveBll2", "=======>onGetInfoSuccess 444444444");


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

        Log.e("LiveBll2", "=======>mIRCMessage.create()");
        mLogtf.d(s);
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
            onTopic(channel, topic, "", 0, true);
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String
                notice) {
            try {
                JSONObject object = new JSONObject(notice);
                int mtype = object.getInt("type");
                com.xueersi.lib.log.Loger.e("LiveBll2", "=======>onNotice:" + mtype + ":" + this);
                ///////播放器相关/////////
                switch (mtype) {
                    case XESCODE.MODECHANGE:
                        String mode = object.getString("mode");
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
                            liveGetPlayServer(true);
                        }
                        break;

                }
                //////////////////////


                List<NoticeAction> noticeActions = mNoticeActionMap.get(mtype);
                if (noticeActions != null && noticeActions.size() > 0) {
                    for (NoticeAction noticeAction : noticeActions) {
                        noticeAction.onNotice(object, mtype);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTopic(String channel, String topicstr, String setBy, long date, boolean changed) {
            if (lastTopicstr.equals(topicstr)) {
                mLogtf.i("onTopic(equals):topicstr=" + topicstr);
                return;
            }
            Loger.e(Tag, "======>onTopic:" + topicstr);
            if (TextUtils.isEmpty(topicstr)) {
                return;
            }
            lastTopicstr = topicstr;
            try {
                JSONObject jsonObject = new JSONObject(topicstr);
                LiveTopic liveTopic = mHttpResponseParser.parseLiveTopic(mLiveTopic, jsonObject, mLiveType);
                boolean teacherModeChanged = !mLiveTopic.getMode().equals(liveTopic.getMode());


                ////直播相关//////
                if (mLiveType == LIVE_TYPE_LIVE) {

                    //模式切换
                    if (!(mLiveTopic.getMode().equals(liveTopic.getMode()))) {
                        mLiveTopic.setMode(liveTopic.getMode());
                        mGetInfo.setMode(liveTopic.getMode());
                        if (mVideoAction != null) {
                            boolean isPresent = isPresent(mLiveTopic.getMode());
                            mVideoAction.onModeChange(mLiveTopic.getMode(), isPresent);
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
                    mGetInfo.setMode(liveTopic.getMode());
                }

                if (mTopicActions != null && mTopicActions.size() > 0) {
                    for (TopicAction mTopicAction : mTopicActions) {
                        mTopicAction.onTopic(liveTopic, teacherModeChanged);
                    }
                }

                List<String> disableSpeaking = liveTopic.getDisableSpeaking();
                boolean forbidSendMsg = false;
                for (String id : disableSpeaking) {
                    if (("" + id).contains(mIRCMessage.getNickname())) {
                        forbidSendMsg = true;
                    }
                }

                liveTopic.setDisable(forbidSendMsg);
                mLiveTopic.copy(liveTopic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onUserList(channel, users);
                }
            }
            String s = "onUserList:channel=" + channel + ",users=" + users.length;
            //主讲老师
            boolean haveMainTeacher = false;
            //辅导老师
            boolean haveCounteacher = false;
            for (int i = 0; i < users.length; i++) {
                User user = users[i];
                String nick = user.getNick();
                if (nick != null && nick.length() > 2) {
                    if (nick.startsWith(TEACHER_PREFIX)) {
                        s += ",mainTeacher=" + nick;
                        haveMainTeacher = true;
                        synchronized (mIRCcallback) {
                            if (mMainTeacher == null) {
                                mMainTeacher = new Teacher(nick);
                            } else {
                                mMainTeacher.set_nick(nick);
                            }
                        }

                        if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }

                    } else if (nick.startsWith(COUNTTEACHER_PREFIX)) {
                        haveCounteacher = true;
                        mCounteacher.set_nick(nick);
                        mCounteacher.isLeave = false;
                        s += ",counteacher=" + nick;

                        if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode())
                                && mVideoAction != null) {
                            mVideoAction.onTeacherQuit(false);
                        }


                    }
                } else {
                    s += ",else=" + nick;
                }
            }
            if (!haveCounteacher) {
                mCounteacher.isLeave = true;
            }
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {
            //更新 本地 主/辅讲 态
            if (sender.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    if (mMainTeacher == null) {
                        mMainTeacher = new Teacher(sender);
                    } else {
                        mMainTeacher.set_nick(sender);
                    }
                }
                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }

            } else if (sender.startsWith(COUNTTEACHER_PREFIX)) {
                mCounteacher.isLeave = false;
                mCounteacher.set_nick(sender);

                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(false);
                }

            }
            // 分发消息
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onJoin(target, sender, login, hostname);
                }
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

            Loger.d(TAG, "onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                    + sourceHostname + ",reason=" + reason);
            if (sourceNick.startsWith(TEACHER_PREFIX)) {
                synchronized (mIRCcallback) {
                    mMainTeacher = null;
                }


                if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }


            } else if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                mCounteacher.isLeave = true;

                if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                    mVideoAction.onTeacherQuit(true);
                }
            }


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

    public boolean sendMessage(JSONObject data) {
        boolean result = false;
        try {
            if (mLiveTopic.isDisable()) {
                result = false;
            } else {
                mIRCMessage.sendMessage(data.toString());
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    ///日志上传相关

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        setLogParam(eventId, mData);
        UmsAgentManager.umsAgentOtherBusiness(mContext, appID, UmsConstants.uploadBehavior, mData);
    }

    /**
     * 上传log 添加 公共参数
     *
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
        mNoticeActionMap.clear();
        mTopicActions.clear();
        mMessageActions.clear();


        mVideoAction = null;
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }

        if (mIRCMessage != null) {
            mIRCMessage.destory();
        }


    }

    /////////////////////////////  播放相关 //////////////////////////////////

    private VideoAction mVideoAction;

    public void setVideoAction(VideoAction videoAction) {
        this.mVideoAction = videoAction;
    }


    public PlayerService.SimpleVPlayerListener getVideoListener() {
        return mVideoListener;
    }

    private AtomicInteger mOpenCount = new AtomicInteger(0);
    private AtomicInteger mFailCount = new AtomicInteger(0);
    private AtomicInteger mFailMainTeacherCount = new AtomicInteger(0);
    private AtomicInteger mFailCounTeacherCount = new AtomicInteger(0);
    private AtomicInteger mBufferCount = new AtomicInteger(0);
    private AtomicInteger mCompleteCount = new AtomicInteger(0);
    private AtomicInteger mRepairBufferCount = new AtomicInteger(0);
    private AtomicInteger mCompleteMainTeacherCount = new AtomicInteger(0);
    private AtomicInteger mCompleteCounTeacherCount = new AtomicInteger(0);
    private AtomicInteger mRepairOpenCount = new AtomicInteger(0);


    long openStartTime;


    private PlayerService.SimpleVPlayerListener mVideoListener = new PlayerService.SimpleVPlayerListener() {
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
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            mFailCount.set(mFailCount.get() + 1);
            long openTime = System.currentTimeMillis() - openStartTime;
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

        }

        @Override
        public void onBufferComplete() {
            long bufferTime = System.currentTimeMillis() - bufferStartTime;
            mLogtf.d("onBufferComplete:bufferTime=" + bufferTime);
        }

        @Override
        public void onPlaybackComplete() {
            mCompleteCount.set(mCompleteCount.get() + 1);

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


    /**
     * 调度是不是在无网络下失败
     */
    private boolean liveGetPlayServerError = false;
    private PlayServerEntity mServer;
    private Callback.Cancelable mGetPlayServerCancle;
    /**
     * 直播帧数统计
     */
    private TotalFrameStat totalFrameStat;
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


    public LiveVideoSAConfig getLiveVideoSAConfig() {
        return liveVideoSAConfig;
    }

    private PlayServerEntity.PlayserverEntity playserverEntity;

    public void setPlayserverEntity(PlayServerEntity.PlayserverEntity playserverEntity) {
        this.playserverEntity = playserverEntity;
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
            postDelayedIfNotFinish(mStatisticsRun, mStatisticsdelay);
        }
    };


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


    /**
     * 当前状态，老师是不是在直播间
     */
    public boolean isPresent() {
        return isPresent(mLiveTopic.getMode());
    }


    /**
     * 用户心跳倒计时
     */
    private Runnable mUserOnlineCall = new Runnable() {

        @Override
        public void run() {
            getUserOnline();
        }
    };

    private int mHbTime = 300, mHbCount = 0;

    /**
     * 用户心跳解析错误
     */
    private int userOnlineError = 0;

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
     * 直播间内模块间 数据共享池
     */
    private HashMap<String, Object> businessShareParamMap = new HashMap<String, Object>();

    /**
     * 各模块 调用此方法 暴露自己需要和其他模块共享的参数
     * @param key
     * @param value
     */
    public void addBusinessShareParam(String key, Object value) {
        synchronized (businessShareParamMap) {
            businessShareParamMap.put(key, value);
        }
    }


    /**
     * 各模块调用此方法  查找其他模块暴露的 参数信息
     * @param key
     * @return
     */
    public Object getBusinessShareParam(String key) {
        synchronized (businessShareParamMap) {
            return businessShareParamMap.get(key);
        }
    }


}
