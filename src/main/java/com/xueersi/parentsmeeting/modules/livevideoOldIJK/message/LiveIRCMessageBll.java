package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.SampleMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEvent;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.SendMessageReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business.VideoChatStatusChange;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;

/**
 * Created by lyqai on 2018/6/26.
 */

public class LiveIRCMessageBll extends LiveBaseBll implements MessageAction, NoticeAction, TopicAction {
    private final String TAG = "LiveIRCMessageBll";
    Logger loger = LoggerFactory.getLogger(TAG);

    private int mLiveType;
    private LogToFile mLogtf;
    final Object lock = new Object();
    /** 是不是有分组 */
    private boolean haveTeam = false;
    private long blockTime;
    private LiveTopic mLiveTopic = new LiveTopic();
    /** 主讲老师 */
    private Teacher mMainTeacher;
    /** 主讲教师名字 */
    private String mMainTeacherStr = null;
    /** 辅导教师 */
    private Teacher mCounteacher;
    /** 辅导教师IRC */
    private String mCounTeacherStr = null;
    private VideoAction mVideoAction;
    /** 智能私信业务 */
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;
    private LiveMessageBll mRoomAction;
    /** 星星互动 */
//    private LiveAchievementIRCBll starAction;
    private ArrayList<SendMessageReg.OnSendMsg> onSendMsgs = new ArrayList<>();
    private LiveHttpManager mHttpManager;
    private String mLiveId;
    private LiveHttpResponseParser mHttpResponseParser;

    public LiveIRCMessageBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        this.mLiveType = liveBll.getLiveType();
        mLiveId = liveBll.getLiveId();
        mLogtf = new LogToFile(context, TAG);
        mRoomAction = new LiveMessageBll(context, mLiveType);
        putInstance(SendMessageReg.class, new SendMessageReg() {
            @Override
            public void addOnSendMsg(OnSendMsg onSendMsg) {
                onSendMsgs.add(onSendMsg);
            }

            @Override
            public void removeOnSendMsg(OnSendMsg onSendMsg) {
                onSendMsgs.remove(onSendMsg);
            }
        });
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mLiveTopic = mLiveBll.getLiveTopic();
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
        mVideoAction = getInstance(VideoAction.class);
        mHttpResponseParser = mLiveBll.getHttpResponseParser();
        mHttpManager = mLiveBll.getHttpManager();
//        starAction = getInstance(LiveAchievementIRCBll.class);
//        mRoomAction.setQuestionBll(getInstance(QuestionBll.class));
        VideoChatStatusChange videoChatStatusChange = getInstance(VideoChatStatusChange.class);
        if (videoChatStatusChange != null) {
            videoChatStatusChange.addVideoChatStatusChange(new VideoChatStatusChange.ChatStatusChange() {
                @Override
                public void onVideoChatStatusChange(String voiceChatStatus) {
                    mRoomAction.videoStatus(voiceChatStatus);
                }
            });
        }
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(mRoomAction);
        }
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(mRoomAction);
        }
        EventBus.getDefault().register(this);
        RegMediaPlayerControl regMediaPlayerControl = getInstance(RegMediaPlayerControl.class);
        regMediaPlayerControl.addMediaPlayerControl(new SampleMediaPlayerControl() {
            @Override
            public void onTitleShow(boolean show) {
                mRoomAction.onTitleShow(show);
            }
        });
    }

    public void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        mRoomAction.setLiveBll(new LiveIRCState());
        mRoomAction.setLiveMediaControllerBottom(baseLiveMediaControllerBottom);
    }

    public void setLiveMediaCtrTop(BaseLiveMediaControllerTop mediaCtrTop){
        mRoomAction.setLiveMediaCtrTop(mediaCtrTop);
    }



    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
            if (!StringUtils.isEmpty(studentLiveInfo.getTeamId()) && !"0".equals(studentLiveInfo.getTeamId())) {
                haveTeam = true;
            }
        }
        mCounteacher = new Teacher(mGetInfo.getTeacherName());
        mRoomAction.setLiveGetInfo(getInfo);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (getInfo.getPattern() == 2 && LiveTopic.MODE_CLASS.equals(getInfo.getMode())) {
                mRoomAction.initViewLiveStand(mRootView);
            } else if (getInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY
                    && LiveTopic.MODE_CLASS.equals(getInfo.getMode())) {
                mRoomAction.initHalfBodyLive(mRootView);
            } else {
                mRoomAction.initViewLive(mRootView);
            }
        }
        //中学连对激励系统，教师广播发送学报消息
        if (getInfo.getIsOpenNewCourseWare() == 1) {
            getHttpManager().getEvenLikeData(
//                "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
                    mGetInfo.getGetEvenPairListUrl(),
                    mGetInfo.getStudentLiveInfo().getClassId(),
                    mGetInfo.getId(),
                    mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
                            mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
                        }
                    });
        }
    }

    String currentMode;

    @Override
    public void onModeChange(final String oldMode, final String mode, boolean isPresent) {
        this.currentMode = mode;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //理科，主讲和辅导切换的时候，给出提示（切流）
                if (mRoomAction != null) {
                    logger.i("主讲和辅导切换的时候，给出提示（切流）");
                    mRoomAction.onTeacherModeChange(oldMode, mode, false, mLiveTopic.getCoachRoomstatus()
                            .isZJLKOpenbarrage(), mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage());
                    //mRoomAction.onTeacherModeChange(mode,false);
                }
                if (mGetInfo.getPattern() == 2) {
                    View view = mRoomAction.getView();
                    if (view != null) {
                        view.setVisibility(View.INVISIBLE);
                    }
                    if (LiveTopic.MODE_CLASS.equals(mode)) {
                        mRoomAction.initViewLiveStand(mRootView);
                    } else {
                        mRoomAction.initViewLive(mRootView);
                    }
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                    }
                } else if (mGetInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY) {
                    //延迟 2.5 秒 走相关逻辑(适配转场动画 节奏)
                    final String finalMode = mode;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logger.d("onModeChange:currentMode=" + currentMode + ",finalMode=" + finalMode);
                            if (!currentMode.equals(finalMode)) {
                                return;
                            }
                            View view = mRoomAction.getView();
                            if (view != null) {
                                view.setVisibility(View.INVISIBLE);
                            }

                            if (LiveTopic.MODE_CLASS.equals(mode)) {
                                mRoomAction.initHalfBodyLive(mRootView);
                            } else {
                                mRoomAction.initViewLive(mRootView);
                            }

                            if (view != null) {
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 2500);
                }
            }
        });
    }

    @Override
    public void onStartConnect() {
        if (mRoomAction != null) {
            mRoomAction.onStartConnect();
        }
    }

    @Override
    public void onConnect(IRCConnection connection) {
        if (mRoomAction != null) {
            mRoomAction.onConnect();
        }
    }

    @Override
    public void onRegister() {
        if (mRoomAction != null) {
            mRoomAction.onRegister();
        }
    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {
        if (mRoomAction != null) {
            mRoomAction.onDisconnect();
        }
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {
        if (mRoomAction != null) {
            mRoomAction.onMessage(target, sender, login, hostname, text, "");
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String
            message) {
        logger.e("=====> onPrivateMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" +
                message);
        if (!"T".equals(message) && haveTeam) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
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
                loger.e("onPrivateMessage", e);
            }
        }
        if (mRoomAction != null) {
            mRoomAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
        }
    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    @Override
    public void onUserList(String channel, User[] users) {
        String s = "onUserList:channel=" + channel + ",users=" + users.length;
    /*    Loger.d("___onuserlist:  channel:  "+channel  + "user[]:  "+users.length+"   "+users.toString());
        for (User user : users){
            Loger.d("___onuserlist:  users:  "+user.getNick());
        }*/
        boolean haveMainTeacher = false;//主讲老师
        boolean haveCounteacher = false;//辅导老师
        ArrayList<User> arrayList = new ArrayList<>();
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            String _nick = user.getNick();
            if (_nick != null && _nick.length() > 2) {
                if (_nick.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
                    s += ",mainTeacher=" + _nick;
                    haveMainTeacher = true;
                    synchronized (lock) {
                        mMainTeacher = new Teacher(_nick);
                        mMainTeacherStr = _nick;
                    }
                    if (LiveTopic.MODE_CLASS.endsWith(mLiveTopic.getMode())
                            && mVideoAction != null) {
                        mVideoAction.onTeacherQuit(false);
                    }
                } else if (_nick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
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
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            s += ",haveCounteacher=" + haveCounteacher;
        }
        mLogtf.d(s);
        if (mRoomAction != null) {
            User[] users2 = new User[arrayList.size()];
            arrayList.toArray(users2);
            mRoomAction.onUserList(channel, users2);
        }
    }

    /** 是不是自己组的人 */
    private boolean isMyTeam(String sender) {
        boolean isMyTeam = true;
        ArrayList<String> teamStuIds = mGetInfo.getTeamStuIds();
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE && !teamStuIds.isEmpty()) {
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

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        logger.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
        if (sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            synchronized (lock) {
                mMainTeacher = new Teacher(sender);
                mMainTeacherStr = sender;
            }
            mLogtf.d("onJoin:mainTeacher:target=" + target + ",mode=" + mLiveTopic.getMode());
            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(false);
            }
        } else if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            mCounTeacherStr = sender;
            mCounteacher.isLeave = false;
            mLogtf.d("onJoin:Counteacher:target=" + target + ",mode=" + mLiveTopic.getMode());
            if (LiveTopic.MODE_TRANING.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(false);
            }
        } else {
            if (mRoomAction != null) {
//                    if (sender.startsWith(LiveBll.TEACHER_PREFIX) || sender.startsWith(COUNTTEACHER_PREFIX)) {
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

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                + sourceHostname + ",reason=" + reason);
        if (sourceNick.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            synchronized (lock) {
                mMainTeacher = null;
            }
            mLogtf.d("onQuit:mainTeacher quit");
            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode()) && mVideoAction != null) {
                mVideoAction.onTeacherQuit(true);
            }
        } else if (sourceNick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
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

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {
        mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
                + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
        if (mRoomAction != null) {
            mRoomAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

    @Override
    public void onUnknown(String line) {
        if (mLiveAutoNoticeBll != null) {
            mLiveAutoNoticeBll.onUnknown(line);
        }
    }

    /**
     * 是否是 高三 理科直播 （展示不同聊天 内容：高三理科 以 班级为单位展示,）
     *
     * @return
     */
    public boolean isSeniorOfHighSchool() {
        return mGetInfo != null && mGetInfo.getIsSeniorOfHighSchool() == 1;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        String msg = "onNotice";
        logger.i("收到指令" + type);
        switch (type) {
            case XESCODE.OPENBARRAGE: {
                try {
                    boolean open = object.getBoolean("open");
                    String fromWhichTeacher = object.optString("from");//如果解析不到就默认主讲
                    logger.i("onNotice: XESCODE.OPENBARRAGE fromWhichTeacher = " + fromWhichTeacher);
                    msg += open ? "OPENBARRAGE" : "CLOSEBARRAGE";

                    if (!fromWhichTeacher.equals("t") && !fromWhichTeacher.equals("f")) {
                        logger.i("onNotice: XESCODE.OPENBARRAGE 文科没有form字段");
                        mLiveTopic.getMainRoomstatus().setOpenbarrage(open);
                        if (mRoomAction != null) {
                            mRoomAction.onOpenbarrage(open, true);
                        }
                    } else {
                        mLiveTopic.getCoachRoomstatus().setLKNoticeMode(fromWhichTeacher.equals("t") ? LiveTopic
                                .MODE_CLASS : LiveTopic.MODE_TRANING);
                        mLiveTopic.setLKNoticeMode(fromWhichTeacher.equals("t") ? LiveTopic.MODE_CLASS : LiveTopic
                                .MODE_TRANING);
                        logger.i("onNotice: XESCODE.OPENBARRAGE 理科有form字段 open = " + open);

                        if ("t".equals(fromWhichTeacher)) {
                            //来自主讲的notice 主讲开启鲜花与否
                            mLiveTopic.getCoachRoomstatus().setZJLKOpenbarrage(open);

                            if (mRoomAction != null) {
                                mRoomAction.onOpenbarrage(open, true);
                            }
                        } else {
                            //来自辅导的notice 辅导开启鲜花与否
                            mLiveTopic.getCoachRoomstatus().setFDLKOpenbarrage(open);

                            if (mRoomAction != null) {
                                mRoomAction.onFDOpenbarrage(open, true);
                            }
                        }
                    }
                } catch (Exception e) {
                    loger.e("onNotice:OPENBARRAGE", e);
                }
                //getLearnReport();
                break;
            }
            case XESCODE.GAG: {
                try {
                    msg += "GAG";
                    boolean disable = object.getBoolean("disable");
                    //s_3_13827_11022_1
                    String id = object.getString("id");
                    if (("" + id).contains(mLiveBll.getNickname())) {
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
                    msg += ",disable=" + disable + ",id=" + id + "," + mLiveBll.getNickname();
                } catch (Exception e) {
                    loger.e("onNotice:GAG", e);
                }
            }
            break;
            case XESCODE.OPENCHAT: {
                try {
                    boolean open = object.getBoolean("open");
                    String from = object.optString("from", "t");
                    msg += "from=" + from + ",open=" + open;
                    if ("t".equals(from)) {
                        mLiveTopic.getMainRoomstatus().setOpenchat(open);
                        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
                            if (mRoomAction != null) {
                                mRoomAction.onopenchat(open, LiveTopic.MODE_CLASS, true);
                            }
                        }
                    } else {
                        mLiveTopic.getCoachRoomstatus().setOpenchat(open);
                        if (LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                            if (mRoomAction != null) {
                                mRoomAction.onopenchat(open, LiveTopic.MODE_TRANING, true);
                            }
                        }
                    }
                } catch (Exception e) {
                    loger.e("onNotice:OPENCHAT", e);
                }
            }
            break;
            case XESCODE.TEACHER_MESSAGE:
                try {
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
                } catch (Exception e) {
                    loger.e("TEACHER_MESSAGE", e);
                }
                break;
            case XESCODE.XCR_ROOM_OPEN_VOICEBARRAGE: {
                //开启/关闭弹幕
                String open = object.optString("open", "false");
                if (mRoomAction != null) {
                    if ("true".equals(open)) {
                        mRoomAction.onOpenVoicebarrage(true, true);
                    } else {
                        mRoomAction.onOpenVoicebarrage(false, true);
                    }
                }
                break;
            }
            case XESCODE.START_MICRO: {
                String status = object.optString("status", "off");
                if (mRoomAction != null) {
                    if ("on".equals(status)) {
                        mRoomAction.onOpenVoiceNotic(true, "START_MICRO");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "START_MICRO");
                    }
                }
                break;
            }
            case XESCODE.ARTS_WORD_DICTATION: {
                int state = object.optInt("state", 0);
                if (mRoomAction != null) {
                    if (1 == state) {
                        mRoomAction.onOpenVoiceNotic(true, "ARTS_WORD_DICTATION");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "ARTS_WORD_DICTATION");
                    }
                }
                break;
            }
            case XESCODE.RAISE_HAND: {
                String status = object.optString("status", "off");
                if (mRoomAction != null) {
                    if ("on".equals(status)) {
                        mRoomAction.onOpenVoiceNotic(true, "RAISE_HAND");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "RAISE_HAND");
                    }
                }
                break;
            }
            case XESCODE.RAISE_HAND_SELF: {
                String status = object.optString("status", "off");
                if (mRoomAction != null) {
                    if ("on".equals(status)) {
                        mRoomAction.onOpenVoiceNotic(true, "RAISE_HAND_SELF");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "RAISE_HAND_SELF");
                    }
                }
                break;
            }
            case XESCODE.ARTS_H5_COURSEWARE: {
                String status = object.optString("status", "off");
                if (mRoomAction != null) {
                    if ("on".equals(status)) {
                        mRoomAction.onOpenVoiceNotic(true, "ARTS_H5_COURSEWARE");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "ARTS_H5_COURSEWARE");
                    }
                }
                break;
            }
            case XESCODE.ENGLISH_H5_COURSEWARE: {
                String status = object.optString("status", "off");
                if (mRoomAction != null) {
                    if ("on".equals(status)) {
                        mRoomAction.onOpenVoiceNotic(true, "ENGLISH_H5_COURSEWARE");
                    } else {
                        mRoomAction.onOpenVoiceNotic(false, "ENGLISH_H5_COURSEWARE");
                    }
                }
                break;
            }
            case XESCODE.SENDQUESTION: {
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    userLikeList.clear();
                    isMiddleScienceH5Open = true;
                }
                mRoomAction.onOpenVoiceNotic(true, "SENDQUESTION");
                break;
            }
            case XESCODE.STOPQUESTION: {
                mRoomAction.onOpenVoiceNotic(false, "STOPQUESTION");
//                getHttpManager().getEvenLikeData(
////                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
//                        mGetInfo.getGetEvenPairListUrl(),
//                        mGetInfo.getStudentLiveInfo().getClassId(),
//                        mGetInfo.getId(),
//                        mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
//                                mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
//                            }
//                        });
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    isMiddleScienceH5Open = false;
                    endTime = System.currentTimeMillis();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getHttpManager().getEvenPairInfo(
                                    mGetInfo.getStudentLiveInfo().getClassId(),
                                    mGetInfo.getId(),
                                    mGetInfo.getStudentLiveInfo().getTeamId(),
                                    mGetInfo.getStuId(),
                                    new HttpCallBack() {
                                        @Override
                                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                            mRoomAction.setEvenNum(
                                                    jsonObject.optString("evenPairNum"),
                                                    jsonObject.optString("highestRightNum")
                                            );
                                        }
                                    }
                            );
                        }
                    }, 5000);
                }
                break;
            }
            case XESCODE.ARTS_SEND_QUESTION: {
                mRoomAction.onOpenVoiceNotic(true, "ARTS_SEND_QUESTION");
                break;
            }
            case XESCODE.ARTS_STOP_QUESTION: {
                mRoomAction.onOpenVoiceNotic(false, "ARTS_STOP_QUESTION");
                break;
            }
            case XESCODE.EXAM_START: {
                mRoomAction.onOpenVoiceNotic(true, "EXAM_START");
                break;
            }
            case XESCODE.EXAM_STOP: {
                mRoomAction.onOpenVoiceNotic(false, "EXAM_STOP");
//                getHttpManager().getEvenLikeData(
////                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
//                        mGetInfo.getGetEvenPairListUrl(),
//                        mGetInfo.getStudentLiveInfo().getClassId(),
//                        mGetInfo.getId(),
//                        mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
//                                mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
//                            }
//                        });
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getHttpManager().getEvenPairInfo(
                                    mGetInfo.getStudentLiveInfo().getClassId(),
                                    mGetInfo.getId(),
                                    mGetInfo.getStudentLiveInfo().getTeamId(),
                                    mGetInfo.getStuId(),
                                    new HttpCallBack() {
                                        @Override
                                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                            mRoomAction.setEvenNum(
                                                    jsonObject.optString("evenPairNum"),
                                                    jsonObject.optString("highestRightNum")
                                            );
                                        }
                                    }
                            );
                        }
                    }, 5000);
                }
                break;
            }
            case XESCODE.MULTIPLE_H5_COURSEWARE: {
                boolean isOff = object.optBoolean("open");
                //
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    if (!isOff) {
                        //老师收题之后，更新聊天区连对榜
//                    getHttpManager().getEvenLikeData(
////                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
//                            mGetInfo.getGetEvenPairListUrl(),
//                            mGetInfo.getStudentLiveInfo().getClassId(),
//                            mGetInfo.getId(),
//                            mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
//                                @Override
//                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                    EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
//                                    mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
//                                }
//                            });
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getHttpManager().getEvenPairInfo(
                                        mGetInfo.getStudentLiveInfo().getClassId(),
                                        mGetInfo.getId(),
                                        mGetInfo.getStudentLiveInfo().getTeamId(),
                                        mGetInfo.getStuId(),
                                        new HttpCallBack() {
                                            @Override
                                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                                mRoomAction.setEvenNum(
                                                        jsonObject.optString("evenPairNum"),
                                                        jsonObject.optString("highestRightNum")
                                                );
                                            }
                                        }
                                );
                            }
                        }, 5000);
                        //设置结束时间，判断是否显示XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT点赞消息
                        endTime = System.currentTimeMillis();
//                    isHasReceiveLike = false;
                        isMiddleScienceH5Open = false;
                    } else {
//                    isHasReceiveLike = false;
                        userLikeList.clear();
                        isMiddleScienceH5Open = true;
                    }
                }
                break;
            }
            case XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT: {
                //点赞
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    logger.i("收到点赞消息");
                    String senderId = object.optString("from");
                    if (isInLikeTime() && !userLikeList.contains(senderId)) {
                        String likeSender = object.optString("stuName");
                        logger.i(likeSender + " 刚刚赞了你");
                        mRoomAction.addMessage("", LiveMessageEntity.EVEN_DRIVE_LIKE, likeSender + " 刚刚赞了你");
                        userLikeList.add(senderId);
//                    isHasReceiveLike = true;
                    } else {
                        logger.i("超过时间或者senderId重复");
                    }
                }

//                logger.i("获取学报");
//                getHttpManager().getJournalUrl(
//                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/getJournal",
////                        mGetInfo.getGetJournalUrl(),
//                        mGetInfo.getStudentLiveInfo().getClassId(),
//                        mGetInfo.getId(),
//                        mGetInfo.getStudentLiveInfo().getTeamId(),
//                        mGetInfo.getStuId(),
//                        new HttpCallBack() {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                                String message = jsonObject.getString("message");
//                                logger.i(message);
//                                if (!TextUtils.isEmpty(message)) {
//                                    mRoomAction.addMessage("提示", LiveMessageEntity.EVEN_DRIVE_REPORT, message);
//                                }
//                            }
//                        });
//
//
//                //中学连对激励系统，教师广播发送学报消息
//                logger.i("中学连对激励系统，教师广播发送学报消息");
//                getHttpManager().getEvenLikeData(
////                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
//                        mGetInfo.getGetEvenPairListUrl(),
//                        mGetInfo.getStudentLiveInfo().getClassId(),
//                        mGetInfo.getId(),
//                        mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
//                                mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
//                            }
//                        });

                break;
            }
            case XESCODE.EvenDrive.BROADCAST_STUDY_REPORT: {
                //获取学报
                if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                    logger.i("获取学报");
                    //中学连对激励系统，教师广播发送学报消息
                    logger.i("中学连对激励系统，教师广播发送学报消息");

                    getHttpManager().getJournalUrl(
//                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/getJournal",
                            mGetInfo.getGetJournalUrl(),
                            mGetInfo.getStudentLiveInfo().getClassId(),
                            mGetInfo.getId(),
                            mGetInfo.getStudentLiveInfo().getTeamId(),
                            mGetInfo.getStuId(),
                            new HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                    String message = jsonObject.getString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        mRoomAction.addMessage("提示", LiveMessageEntity.EVEN_DRIVE_REPORT, message);
                                    }
                                }
                            });
//                getHttpManager().getEvenLikeData(
////                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
//                        mGetInfo.getGetEvenPairListUrl(),
//                        mGetInfo.getStudentLiveInfo().getClassId(),
//                        mGetInfo.getId(),
//                        mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
//                                mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
//                            }
//                        });
                }
                break;
            }

            default:
                break;
        }
        mLogtf.d(msg);
    }

    public void postDelayIfNotFinished(Runnable runnable, long time) {
        if (mHandler != null) {
            mHandler.postDelayed(runnable, time);
        }
    }

    /** 列表，用户点赞列表 */
    private List<String> userLikeList = new CopyOnWriteArrayList<>();

    /**
     * 是否在点赞时间里面
     * 现在点赞消息是在  发题至收题后15s.
     *
     * @return
     */
    private boolean isInLikeTime() {
        long nowTime = System.currentTimeMillis();
        logger.i("isMiddleScienceH5Open " + isMiddleScienceH5Open);
        return (isMiddleScienceH5Open || (((nowTime - endTime) < TIME_SEND_PRIVATE_MSG)));
    }

    //当前互动题是否处于打开状态
    private boolean isMiddleScienceH5Open = false;
    /**
     * 中学激励系统，15s内来判断是否显示点赞消息
     */
    protected final long TIME_SEND_PRIVATE_MSG = 15 * 1000;
    //中学激励系统，收题时间,判断是否在15s内来决定点赞
    private long endTime;
    //中学激励系统，这段时间是否接收过点赞消息,一道题目只显示一次点赞消息
//    private boolean isHasReceiveLike = false;

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.OPENBARRAGE, XESCODE.GAG, XESCODE.OPENCHAT, XESCODE.TEACHER_MESSAGE, XESCODE.START_MICRO,
                XESCODE.ARTS_WORD_DICTATION, XESCODE.RAISE_HAND, XESCODE.XCR_ROOM_OPEN_VOICEBARRAGE, XESCODE
                .RAISE_HAND_SELF, XESCODE.ENGLISH_H5_COURSEWARE, XESCODE.ARTS_H5_COURSEWARE, XESCODE.SENDQUESTION,
                XESCODE.ARTS_SEND_QUESTION, XESCODE.EXAM_START, XESCODE.STOPQUESTION, XESCODE.EXAM_STOP, XESCODE.ARTS_STOP_QUESTION,
                XESCODE.EvenDrive.BROADCAST_STUDY_REPORT, XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT, XESCODE.MULTIPLE_H5_COURSEWARE
        };
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        List<String> disableSpeaking = liveTopic.getDisableSpeaking();
        boolean forbidSendMsg = false;
        for (String id : disableSpeaking) {
            if (("" + id).contains(mLiveBll.getNickname())) {
                forbidSendMsg = true;
            }
        }

        liveTopic.setDisable(forbidSendMsg);
        if (mRoomAction != null) {
            try {
                if (jsonObject.has("room_2")) {
                    JSONObject status = jsonObject.getJSONObject("room_2");
                    if (status.has("openbarrage")) {
                        //理科的room2里面才有openbarrage字段
                        logger.i("理科的room2里面才有openbarrage字段 ");

                        if (mRoomAction != null) {
                            if (LiveTopic.MODE_CLASS.equals(mLiveTopic.getMode())) {
                                logger.i("mLiveTopic.getCoachRoomstatus().isZJLKOpenbarrage() =  " + mLiveTopic
                                        .getCoachRoomstatus().isZJLKOpenbarrage());
                                //理科的主讲！！！！！！！mLiveTopic.getCoachRoomstatus()
                                mRoomAction.onOpenbarrage(mLiveTopic.getCoachRoomstatus().isZJLKOpenbarrage(), false);
                                mRoomAction.onDisable(forbidSendMsg, false);
                            } else {
                                logger.i("mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage() =  " + mLiveTopic
                                        .getCoachRoomstatus().isFDLKOpenbarrage());
                                //辅导
                                mRoomAction.onFDOpenbarrage(mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage(), false);
                                mRoomAction.onDisable(forbidSendMsg, false);
                            }

                        }
                    } else {
                        //文科的room2里面没有openbarrage字段
                        logger.i("文科的room2里面没有openbarrage字段");
                        if (mRoomAction != null) {
                            logger.i("mLiveTopic.getMainRoomstatus().isOpenbarrage() =  " + mLiveTopic
                                    .getMainRoomstatus().isOpenbarrage());
                            mRoomAction.onOpenbarrage(mLiveTopic.getMainRoomstatus().isOpenbarrage(), false);
                            mRoomAction.onDisable(forbidSendMsg, false);
                        }
                    }
                }
            } catch (Exception e) {

            }
            if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
                mRoomAction.onopenchat(liveTopic.getMainRoomstatus().isOpenchat(), LiveTopic.MODE_CLASS,
                        false);
            } else {
                mRoomAction.onopenchat(liveTopic.getCoachRoomstatus().isOpenchat(), LiveTopic.MODE_TRANING,
                        false);
            }
        }
    }

    public void onTitleShow(boolean show) {
        mRoomAction.onTitleShow(show);
    }

    public static String goldNum;
    public static long goldNumTime;

    public static void requestGoldTotal(Context mContext) {
        long time = System.currentTimeMillis() - goldNumTime;
        Loger.d("LiveIRCMessageBll", "requestGoldTotal:goldNum=" + goldNum + ",time=" + time);
        if (goldNum == null || time > 120000) {
            OtherModulesEnter.requestGoldTotal(mContext);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppEvent.OnGetGoldUpdateEvent event = new AppEvent.OnGetGoldUpdateEvent(goldNum);
                    EventBus.getDefault().post(event);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
        LiveIRCMessageBll.goldNum = event.goldNum;
        LiveIRCMessageBll.goldNumTime = System.currentTimeMillis();
        mRoomAction.onGetMyGoldDataEvent(event.goldNum);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        mRoomAction.initView(bottomContent, mIsLand.get());
    }

    //发送聊天消息所需要的IRCState
    class LiveIRCState implements IRCState {

        @Override
        public String getMode() {
            return mLiveBll.getMode();
        }

        @Override
        public boolean isOpenbarrage() {
            return mLiveTopic.getMainRoomstatus().isOpenbarrage();
        }

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

        @Override
        public boolean sendMessage(String msg, String name) {
            boolean sendMessage = false;
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
                        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                        String teamId = studentLiveInfo.getTeamId();
                        jsonObject.put("from", "android_" + teamId);
                        jsonObject.put("to", teamId);
                    }
                    sendMessage = mLiveBll.sendMessage(jsonObject);
                    for (int i = 0; i < onSendMsgs.size(); i++) {
                        try {
                            onSendMsgs.get(i).onSendMsg(msg);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(e);
                        }
                    }
                } catch (Exception e) {
                    // logger.e( "understand", e);
                    UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                    mLogtf.e("sendMessage", e);
                }
            }
            return sendMessage;
        }

        @Override
        public void praiseTeacher(final String formWhichTeacher, String ftype, String educationStage, final
        HttpCallBack callBack) {
            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
            mHttpManager.praiseTeacher(mLiveType, enstuId, mLiveId, teacherId, ftype, educationStage, new
                    HttpCallBack() {

                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mLogtf.d("praiseTeacher:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                            if (responseEntity.getJsonObject() instanceof JSONObject) {
                                try {
                                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                    if (mGetInfo.getIsArts() == 0) {
                                        sendFlowerMessage(jsonObject.getInt("type"), formWhichTeacher);
                                    } else {
                                        sendFlowerMessage(jsonObject.getInt("type"));
                                    }
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

        @Override
        public boolean isDisable() {
            return mLiveTopic.isDisable();
        }

        @Override
        public boolean isHaveTeam() {
            return haveTeam;
        }

        @Override
        public boolean isSeniorOfHighSchool() {
            return mGetInfo != null && mGetInfo.getIsSeniorOfHighSchool() == 1;
        }

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
    }

    /**
     * 当前状态
     *
     * @param mode 模式
     */
    public boolean isPresent(String mode) {
        boolean isPresent = true;
        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
            isPresent = mMainTeacher != null;
        } else {
            isPresent = !mCounteacher.isLeave;
        }
        return isPresent;
    }

    public Teacher getCounteacher() {
        return mCounteacher;
    }

    public Teacher getMainTeacher() {
        return mMainTeacher;
    }

    public String getmMainTeacherStr() {
        return mMainTeacherStr;
    }

    public String getmCounTeacherStr() {
        return mCounTeacherStr;
    }

    /**
     * 得到老师名字
     */
    public String getModeTeacher(String mode) {
        String mainnick = "null";
        if (mMainTeacher != null) {
            mainnick = mMainTeacher.get_nick();
        }
        if (mCounteacher == null) {
            return "mode=" + mode + ",mainnick=" + mainnick + ",coun=null";
        } else {
            return "mode=" + mode + ",mainnick=" + mainnick + ",coun.isLeave=" + mCounteacher.isLeave;
        }
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
            mLiveBll.sendMessage(jsonObject);
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendFlowerMessage", e);
        }
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
            mLiveBll.sendMessage(jsonObject);
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendFlowerMessage", e);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mRoomAction != null) {
            mRoomAction.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mRoomAction.onDestroy();
        onSendMsgs.clear();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 中学激励系统,用户关闭页面后，更新连天区的信息
     *
     * @param evenDriveEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEvenDrive(EvenDriveEvent evenDriveEvent) {
        if (evenDriveEvent.getStatus() == EvenDriveEvent.CLOSE_H5
                && mGetInfo.getIsOpenNewCourseWare() == 1) {
            //老师收题之后，更新聊天区连对榜
            logger.i("update livemessage evendrive data");
            getHttpManager().getEvenLikeData(
//                        "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
                    mGetInfo.getGetEvenPairListUrl(),
                    mGetInfo.getStudentLiveInfo().getClassId(),
                    mGetInfo.getId(),
                    mGetInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
                            mRoomAction.setEvenNum(String.valueOf(evenDriveEntity.getMyEntity().getEvenPairNum()), evenDriveEntity.getMyEntity().getHighestRightNum());
                        }
                    });
            //设置结束时间，判断是否显示XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT点赞消息
//            endTime = System.currentTimeMillis();
//            isHasReceiveLike = false;
        }
    }

    private boolean isMiddleScience() {
        return mGetInfo.getIsArts() == 0 &&
                !LiveVideoConfig.isSmallChinese &&
                !LiveVideoConfig.isPrimary &&
                !mGetInfo.getSmallEnglish();
    }
}
