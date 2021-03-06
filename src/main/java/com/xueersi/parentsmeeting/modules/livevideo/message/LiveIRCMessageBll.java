package com.xueersi.parentsmeeting.modules.livevideo.message;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.SampleMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEvent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TeacherAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.SendMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveAnimRepository;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveUtils;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.TasksDataSource;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEvent.CALL_BACK_UPDATE_EVEN_RIGHT;

//import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;

/**
 * Created by linyuqiang on 2018/6/26.
 * 直播聊天
 */
public class LiveIRCMessageBll extends LiveBaseBll implements MessageAction, NoticeAction, TopicAction, TeacherAction {
    private final String TAG = "LiveIRCMessageBll";

    private int mLiveType;
    private LogToFile mLogtf;
    private final Object lock = new Object();
    /**
     * 是不是有分组
     */
    private boolean haveTeam = false;
    private LiveTopic mLiveTopic = new LiveTopic();
    /**
     * 主讲老师
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
    private VideoAction mVideoAction;
    /**
     * 智能私信业务
     */
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;
    private LiveMessageBll mRoomAction;
    /**
     * 星星互动
     */
//    private LiveAchievementIRCBll starAction;
    private ArrayList<SendMessageReg.OnSendMsg> onSendMsgs = new ArrayList<>();
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser mHttpResponseParser;

    public LiveIRCMessageBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        liveBll.setTeacherAction(this);
        this.mLiveType = liveBll.getLiveType();
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
//        语音聊天状态，弹幕分离，就不需要了
//        VideoChatStatusChange videoChatStatusChange = getInstance(VideoChatStatusChange.class);
//        if (videoChatStatusChange != null) {
//            videoChatStatusChange.addVideoChatStatusChange(new VideoChatStatusChange.ChatStatusChange() {
//                @Override
//                public void onVideoChatStatusChange(String voiceChatStatus) {
//                    mRoomAction.videoStatus(voiceChatStatus);
//                }
//            });
//        }
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
        mRoomAction.setLiveBll(new LiveIRCState());
        mRoomAction.setLiveHttpManager(mHttpManager);
        BaseLiveMediaControllerTop controllerTop = getInstance(BaseLiveMediaControllerTop.class);
        setLiveMediaControllerTop(controllerTop);
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = getInstance(BaseLiveMediaControllerBottom.class);
        setLiveMediaControllerBottom(baseLiveMediaControllerBottom);
    }

    private void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
        mRoomAction.setLiveMediaControllerBottom(baseLiveMediaControllerBottom);
    }

    private void setLiveMediaControllerTop(BaseLiveMediaControllerTop controllerTop) {
        mRoomAction.setBaseLiveMediaControllerTop(controllerTop);
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
            if (getInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2 && LiveTopic.MODE_CLASS.equals(getInfo.getMode())) {
                mRoomAction.initViewLiveStand(getLiveViewAction());
            } else if ((getInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY || getInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS)
                    && LiveTopic.MODE_CLASS.equals(getInfo.getMode())) {
                mRoomAction.initHalfBodyLive(getLiveViewAction());
            } else {
                mRoomAction.initViewLive(getLiveViewAction());
            }
        }
        //中学连对激励系统，教师广播发送学报消息
        if (EvenDriveUtils.getOldEvenDrive(getInfo)) {

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
        } else if (getInfo != null && getInfo.getIsArts() != 1) {
            getEvenDriveAnim(getInfo);
        }
    }


    String currentMode;

    @Override
    public void onModeChange(final String oldMode, final String mode, boolean isPresent) {
        this.currentMode = mode;
        post(new Runnable() {
            @Override
            public void run() {
                //理科，主讲和辅导切换的时候，给出提示（切流）
                if (mRoomAction != null) {
                    logger.i("主讲和辅导切换的时候，给出提示（切流）");
                    mRoomAction.onTeacherModeChange(oldMode, mode, false, mLiveTopic.getCoachRoomstatus()
                            .isZJLKOpenbarrage(), mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage());
                    //mRoomAction.onTeacherModeChange(mode,false);
                }
                if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
                    View view = mRoomAction.getView();
                    if (view != null) {
                        view.setVisibility(View.INVISIBLE);
                    }
                    if (LiveTopic.MODE_CLASS.equals(mode)) {
                        mRoomAction.initViewLiveStand(getLiveViewAction());
                    } else {
                        mRoomAction.initViewLive(getLiveViewAction());
                    }
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                    }
                } else if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY || mGetInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS) {
                    //延迟 2.5 秒 走相关逻辑(适配转场动画 节奏)
                    final String finalMode = mode;
                    postDelayed(new Runnable() {
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
                                mRoomAction.initHalfBodyLive(getLiveViewAction());
                            } else {
                                mRoomAction.initViewLive(getLiveViewAction());
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
                logger.e("onPrivateMessage", e);
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

    /**
     * 是不是自己组的人
     */
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
                if (sender.startsWith("p")) {
                    //旁听不计算在内
                    return;
                }
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
                if (sourceNick.startsWith("p")) {
                    //旁听不计算在内
                    return;
                }
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
                    logger.e("onNotice:OPENBARRAGE", e);
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
                    if (("" + id).endsWith(mLiveBll.getNickname()) || mLiveBll.getNickname().endsWith("" + id)) {
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
                    logger.e("onNotice:GAG", e);
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
                    logger.e("onNotice:OPENCHAT", e);
                }
            }
            break;
            case XESCODE.TEACHER_MESSAGE:
                try {
                    if (mRoomAction != null) {
                        String name;
                        //需要传递老师姓名 暂时使用这个变量
                        String hostname;
                        if (sourceNick.startsWith("t")) {
                            name = "主讲老师";
                            hostname = object.optString("name","主讲老师");
                            String teacherImg = "";
                            try {
                                teacherImg = mGetInfo.getMainTeacherInfo().getTeacherImg();
                            } catch (Exception e) {

                            }
                            mRoomAction.onMessage(target, sourceNick, "", hostname, object.getString("msg"), teacherImg);
                        } else {
                            name = "辅导老师";
                            hostname = object.optString("name","辅导老师");
                            String teamId = "";
                            if(mGetInfo != null && mGetInfo.getStudentLiveInfo() != null){
                                teamId = mGetInfo.getStudentLiveInfo().getTeamId();
                            }
                            String to = object.optString("to", "All");
                            if ("All".equals(to) || teamId.equals(to)) {
                                String teacherIMG = mGetInfo.getTeacherIMG();
                                mRoomAction.onMessage(target, sourceNick, "", hostname, object.getString("msg"),
                                        teacherIMG);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.e("TEACHER_MESSAGE", e);
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
                mRoomAction.onOpenVoiceNotic(true, "SENDQUESTION");
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    userLikeList.clear();
                    isMiddleScienceEvenDriveH5Open = true;
                }
                break;
            }
            case XESCODE.STOPQUESTION: {
                mRoomAction.onOpenVoiceNotic(false, "STOPQUESTION");
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    isMiddleScienceEvenDriveH5Open = false;
                    endTime = System.currentTimeMillis();
                    postDelayed(new Runnable() {
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
                } else if (EvenDriveUtils.isOpenStimulation(mGetInfo)) {

                    try {
                        if (EvenDriveUtils.getIsArts(mGetInfo)) {
                            delayGetEvenDriveAnim(mGetInfo);
                        } else {
                            String test_id = object.getString("test_id");
                            if (selfUploadRunnable == null) {
                                selfUploadRunnable = new SelfUploadRunnable(mGetInfo, test_id);
                            }
                            postDelayedIfNotFinish(selfUploadRunnable, 6000);
                        }

                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(TAG, e);
                    }
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
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    postDelayed(new Runnable() {
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
                } else if (EvenDriveUtils.getIsChsAndSci(mGetInfo)) {
                    delayGetEvenDriveAnim(mGetInfo);
                }
                break;
            }
            case XESCODE.MULTIPLE_H5_COURSEWARE: {
                boolean isOpen = object.optBoolean("open");
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    if (!isOpen) {
                        //老师收题之后，更新聊天区连对榜
                        postDelayed(new Runnable() {
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
                        isMiddleScienceEvenDriveH5Open = false;
                    } else {
                        userLikeList.clear();
//                    isHasReceiveLike = false;
                        isMiddleScienceEvenDriveH5Open = true;
                    }
                } else if (EvenDriveUtils.getIsChsAndSci(mGetInfo)) {
                    if (!isOpen) {
                        delayGetEvenDriveAnim(mGetInfo);
                    }
                }
                break;
            }
            case XESCODE.QUES_BIG: {
                boolean isOpen = object.optBoolean("isOpen");
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    if (!isOpen) {
                        //老师收题之后，更新聊天区连对榜
                        postDelayed(new Runnable() {
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
                        isMiddleScienceEvenDriveH5Open = false;
                    } else {
                        userLikeList.clear();
//                    isHasReceiveLike = false;
                        isMiddleScienceEvenDriveH5Open = true;
                    }
                } else if (EvenDriveUtils.getIsChsAndSci(mGetInfo)) {
                    if (!isOpen) {
                        delayGetEvenDriveAnim(mGetInfo);
                    }
                }
                break;
            }
            case XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT: {
                //点赞
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    logger.i("receive Appreciate message");
                    String senderId = object.optString("from");
                    if (isInLikeTime() && !userLikeList.contains(senderId)) {
                        String likeSender = object.optString("stuName");
                        logger.i(likeSender + " Appreciate you just now");
                        mRoomAction.addMessage("", LiveMessageEntity.EVEN_DRIVE_LIKE, likeSender + " 刚刚赞了你");
                        userLikeList.add(senderId);
//                    isHasReceiveLike = true;
                    } else {
                        logger.i("超过时间或者senderId重复");
                    }
                }
                break;
            }
            case XESCODE.EvenDrive.BROADCAST_STUDY_REPORT: {
                if (EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
                    //获取学报
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
                }
                break;
            }

            default:
                break;
        }
        mLogtf.d(msg);
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
        logger.i("isMiddleScienceH5Open " + isMiddleScienceEvenDriveH5Open);
        return (isMiddleScienceEvenDriveH5Open || (((nowTime - endTime) < TIME_SEND_PRIVATE_MSG)));
    }

    //当前互动题是否处于打开状态(用来判断中学连对激励是否显示点赞消息)
    private boolean isMiddleScienceEvenDriveH5Open = false;
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
                XESCODE.EvenDrive.BROADCAST_STUDY_REPORT, XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT, XESCODE.MULTIPLE_H5_COURSEWARE, XESCODE.QUES_BIG,
        };
    }

    /**
     * 是否是新版文科 课件 topic消息
     *
     * @param jsonObject
     * @return
     */
    private boolean isNewArtsH5Courseware(JSONObject jsonObject) {
        return (jsonObject.has("coursewareH5") || jsonObject.has("coursewareOnlineTech"));
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        List<String> disableSpeaking = liveTopic.getDisableSpeaking();
        boolean forbidSendMsg = false;
        for (String id : disableSpeaking) {
            if (("" + id).endsWith(mLiveBll.getNickname()) || mLiveBll.getNickname().endsWith("" + id)) {
                forbidSendMsg = true;
            }
        }
        JSONObject object = jsonObject.optJSONObject("platformTest");
        if (object != null && !object.toString().equals("{}")) {
            isMiddleScienceEvenDriveH5Open = true;
        }
        if (isNewArtsH5Courseware(jsonObject)) {
            JSONObject onlineJobj = jsonObject.optJSONObject("coursewareOnlineTech");
            if (onlineJobj != null && "on".equals(onlineJobj.optString("status"))) {
                JSONObject onlineTechObj = jsonObject.optJSONObject("coursewareOnlineTech");
                if (onlineTechObj != null && !"{}".equals(onlineTechObj.toString())) {
                    String status = onlineTechObj.optString("status");
                    if ("on".equals(status)) {
                        isMiddleScienceEvenDriveH5Open = true;
                    }
                }
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
                            if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
                                logger.i("mLiveTopic.getCoachRoomstatus().isZJLKOpenbarrage() =  " + mLiveTopic
                                        .getCoachRoomstatus().isZJLKOpenbarrage());
                                //理科的主讲！！！！！！！mLiveTopic.getCoachRoomstatus()
                                mRoomAction.onOpenbarrage(liveTopic.getCoachRoomstatus().isZJLKOpenbarrage(), false);
                                mRoomAction.onDisable(forbidSendMsg, false);
                            } else {
                                logger.i("mLiveTopic.getCoachRoomstatus().isFDLKOpenbarrage() =  " + mLiveTopic
                                        .getCoachRoomstatus().isFDLKOpenbarrage());
                                //辅导
                                mRoomAction.onFDOpenbarrage(liveTopic.getCoachRoomstatus().isFDLKOpenbarrage(), false);
                                mRoomAction.onDisable(forbidSendMsg, false);
                            }

                        }
                    } else {
                        //文科的room2里面没有openbarrage字段
                        logger.i("文科的room2里面没有openbarrage字段");
                        if (mRoomAction != null) {
                            logger.i("mLiveTopic.getMainRoomstatus().isOpenbarrage() =  " + mLiveTopic
                                    .getMainRoomstatus().isOpenbarrage());
                            mRoomAction.onOpenbarrage(liveTopic.getMainRoomstatus().isOpenbarrage(), false);
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
        UserGoldTotal.goldNum = event.goldNum;
        UserGoldTotal.goldNumTime = System.currentTimeMillis();
        mRoomAction.onGetMyGoldDataEvent(event.goldNum);
    }

    @Override
    public void initView() {
        mRoomAction.initView(getLiveViewAction(), mIsLand.get());
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
        public boolean sendMessage(String msg, String name, Map<String, String> map) {
            boolean sendMessage = false;
            if (mLiveTopic.isDisable()) {
                return false;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    if (map != null) {
                        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                        for (; iterator.hasNext(); ) {
                            Map.Entry<String, String> _entry = iterator.next();
                            String key = _entry.getKey();
                            String value = _entry.getValue();
                            jsonObject.put(key, value);
                        }
                    }
                    jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
                    if (StringUtils.isEmpty(name)) {
                        name = mGetInfo.getStuName();
                    }
                    jsonObject.put("name", name);
                    jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                    jsonObject.put("version", "" + mGetInfo.getHeadImgVersion());
                    jsonObject.put("msg", msg);
                    if (mGetInfo.getBetterMe().getStuSegment() != null) {
                        jsonObject.put("segment", mGetInfo.getBetterMe().getStuSegment().getSegment());
                        jsonObject.put("segmentType", mGetInfo.getBetterMe().getStuSegment().getSegmentType());
                        jsonObject.put("star", mGetInfo.getBetterMe().getStuSegment().getStar());
                    }
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                } catch (Exception e) {
                    // logger.e( "understand", e);
                    UmsAgentManager.umsAgentException(ContextManager.getContext(), "livevideo_livebll_sendMessage", e);
                    mLogtf.e("sendMessage", e);
                }
            }
            return sendMessage;
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
                    if (mGetInfo.getBetterMe().getStuSegment() != null) {
                        jsonObject.put("segment", mGetInfo.getBetterMe().getStuSegment().getSegment());
                        jsonObject.put("segmentType", mGetInfo.getBetterMe().getStuSegment().getSegmentType());
                        jsonObject.put("star", mGetInfo.getBetterMe().getStuSegment().getStar());
                    }
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
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                } catch (Exception e) {
                    // logger.e( "understand", e);
                    UmsAgentManager.umsAgentException(ContextManager.getContext(), "livevideo_livebll_sendMessage", e);
                    mLogtf.e("sendMessage", e);
                }
            }
            return sendMessage;
        }

        @Override
        public void praiseTeacher(final String formWhichTeacher, String ftype, String educationStage, final
        HttpCallBack callBack) {
            String teacherId = mGetInfo.getMainTeacherInfo().getTeacherId();
            mHttpManager.praiseTeacher(mLiveType, mLiveId, teacherId, ftype, educationStage, new
                    HttpCallBack() {

                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mLogtf.d("praiseTeacher:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                            if (responseEntity.getJsonObject() instanceof JSONObject) {
                                try {
                                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                    if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC || mGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
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
            if (mCounteacher == null) {
                if (mGetInfo != null) {
                    mCounteacher = new Teacher(mGetInfo.getTeacherName());
                }
            }
            isPresent = mCounteacher != null && !mCounteacher.isLeave;
        }
        return isPresent;
    }

    public String getmMainTeacherStr() {
        return mMainTeacherStr;
    }

    public String getmCounTeacherStr() {
        return mCounTeacherStr;
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
    public void onDestroy() {
        super.onDestroy();
        mRoomAction.onDestroy();
        onSendMsgs.clear();
        EventBus.getDefault().unregister(this);
        if (evenDriveRunnable != null) {
            removeCallbacks(evenDriveRunnable);
        }
        if (selfUploadRunnable != null) {
            removeCallbacks(selfUploadRunnable);
        }
    }
//
//    protected boolean isOpenStimulation(LiveGetInfo getInfo) {
//        return getInfo != null &&
//                getInfo.getEvenDriveInfo() != null &&
//                OPEN_STIMULATION == getInfo.getEvenDriveInfo().getIsOpenStimulation();
//    }


    /**
     * 英语成功之后请求
     *
     * @param getInfo
     */
    public void onArtsExtLiveInited(LiveGetInfo getInfo) {
        getEvenDriveAnim(getInfo);
    }

    private EvenDriveAnimRepository animRepositor;

    private void getEvenDriveAnim(final LiveGetInfo getInfo) {
        if (EvenDriveUtils.isOpenStimulation(getInfo)) {
            if (animRepositor == null) {
                animRepositor = new EvenDriveAnimRepository(
                        mContext, getInfo, mHttpManager, null);
            }
            animRepositor.getDataSource(EvenDriveAnimRepository.EvenDriveQuestionType.INIT_EVEN_NUM, "",
                    new TasksDataSource.LoadAnimCallBack() {
                        @Override
                        public void onDataNotAvailable(String msg) {

                        }

                        @Override
                        public void onDatasLoaded(String num, boolean numChange) {
                            mRoomAction.setEvenNum(
                                    getInfo.getEvenDriveInfo().getEvenNum() + "",
                                    getInfo.getEvenDriveInfo().getHighestNum() + ""
                            );
                        }
                    });
        }
    }

    /**
     * 自传互动题
     *
     * @param _getInfo
     * @param testId
     */
    private void getEvenDriveUploadAnim(final LiveGetInfo _getInfo, String testId) {
        if (EvenDriveUtils.isOpenStimulation(_getInfo)) {
            if (animRepositor == null) {
                animRepositor = new EvenDriveAnimRepository(
                        mContext, _getInfo, mHttpManager, null);
            }
            animRepositor.getDataSource(EvenDriveAnimRepository.EvenDriveQuestionType.QUES_TYPE_CHS_SELF_UPLOAD, testId,
                    new TasksDataSource.LoadAnimCallBack() {
                        @Override
                        public void onDataNotAvailable(String msg) {

                        }

                        @Override
                        public void onDatasLoaded(String num, boolean numChange) {
                            mRoomAction.setEvenNum(
                                    _getInfo.getEvenDriveInfo().getEvenNum() + "",
                                    _getInfo.getEvenDriveInfo().getHighestNum() + ""
                            );
                        }
                    });
        }
    }

    /**
     * 老师收题时，如果用户属于强制提交，
     * info接口必须在获取结果页接口请求之后调用，否者连对会被清零。
     * 延迟3s钟，假设收题结果页一定已经返回
     *
     * @param getInfo
     */
    private void delayGetEvenDriveAnim(final LiveGetInfo getInfo) {
        evenDriveRunnable = new EDRunnable(getInfo);
        postDelayedIfNotFinish(evenDriveRunnable, 6000);
//        Observable.
//                just(true).
//                delay(3, TimeUnit.SECONDS).
//                subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//
//                    }
//                });
//        if (EvenDriveUtils.isOpenStimulation(getInfo)) {
//            if (animRepositor == null) {
//                animRepositor = new EvenDriveAnimRepository(
//                        mContext, getInfo, mHttpManager, null);
//            }
//            animRepositor.getDataSource(EvenDriveAnimRepository.EvenDriveQuestionType.INIT_EVEN_NUM, "",
//                    null);
//        }
    }

    private EDRunnable evenDriveRunnable;
    private SelfUploadRunnable selfUploadRunnable;

    /**
     * 中学连对激励延迟使用的Run
     */
    private class EDRunnable implements Runnable {
        private LiveGetInfo _getInfo;

        public EDRunnable(LiveGetInfo _getInfo) {
            this._getInfo = _getInfo;
        }

        @Override
        public void run() {
            if (evenDriveRunnable == this) {
                evenDriveRunnable = null;
            }
            getEvenDriveAnim(_getInfo);
        }
    }

    /**
     * 中学连对激励延迟使用的Run
     */
    private class SelfUploadRunnable implements Runnable {
        private LiveGetInfo _getInfo;
        private String testId;

        public SelfUploadRunnable(LiveGetInfo _getInfo, String testId) {
            this._getInfo = _getInfo;
            this.testId = testId;
        }

        @Override
        public void run() {
            if (selfUploadRunnable == this) {
                selfUploadRunnable = null;
            }
            getEvenDriveUploadAnim(_getInfo, testId);
        }
    }

    /**
     * 中学激励系统,用户关闭页面后，更新连天区的信息
     *
     * @param evenDriveEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEvenDrive(EvenDriveEvent evenDriveEvent) {
        if (evenDriveEvent.getStatus() == EvenDriveEvent.CLOSE_H5
                && EvenDriveUtils.getOldEvenDrive(mGetInfo)) {
            //老师收题之后，更新聊天区连对榜
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
        } else if (evenDriveEvent.getStatus() == CALL_BACK_UPDATE_EVEN_RIGHT) {
            if (mRoomAction != null && mGetInfo != null && mGetInfo.getEvenDriveInfo() != null) {
                mRoomAction.setEvenNum(mGetInfo.getEvenDriveInfo().getEvenNum() + "", mGetInfo.getEvenDriveInfo().getHighestNum() + "");
            }
        } else if (EvenDriveUtils.isOpenStimulation(mGetInfo)) {
            if (evenDriveEvent.getStatus() == EvenDriveEvent.CLOSE_SELF_H5) {
                getEvenDriveUploadAnim(mGetInfo, evenDriveEvent.getTestId());
            } else {
                getEvenDriveAnim(mGetInfo);
            }
        }
    }
}
