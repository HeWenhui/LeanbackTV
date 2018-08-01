package com.xueersi.parentsmeeting.modules.livevideo.message;

import android.app.Activity;
import android.os.Environment;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.SampleMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.Teacher;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStatusChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/6/26.
 */

public class LiveIRCMessageBll extends LiveBaseBll implements MessageAction, NoticeAction, TopicAction {
    private final String TAG = "LiveIRCMessageBll";
    Logger loger = LoggerFactory.getLogger(TAG);
    /** 主讲老师前缀 */
    public static final String TEACHER_PREFIX = "t_";
    /** 辅导老师前缀 */
    public static String COUNTTEACHER_PREFIX = "f_";

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
    private LiveAchievementIRCBll starAction;
    private LiveHttpManager mHttpManager;
    private String mLiveId;
    private LiveHttpResponseParser mHttpResponseParser;

    public LiveIRCMessageBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        this.mLiveType = liveBll.getLiveType();
        mLiveId = liveBll.getLiveId();
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mRoomAction = new LiveMessageBll(context, mLiveType);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mLiveTopic = mLiveBll.getLiveTopic();
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
        mVideoAction = getInstance(VideoAction.class);
        mHttpResponseParser = mLiveBll.getHttpResponseParser();
        mHttpManager = mLiveBll.getHttpManager();
        starAction = getInstance(LiveAchievementIRCBll.class);
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
            } else {
                mRoomAction.initViewLive(mRootView);
            }
        }
    }

    @Override
    public void onModeChange(final String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mGetInfo.getPattern() == 2) {
                    if (LiveTopic.MODE_CLASS.equals(mode)) {
                        mRoomAction.initViewLiveStand(mRootView);
                    } else {
                        mRoomAction.initViewLive(mRootView);
                    }
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
        Loger.e("LiveBll", "=====> onPrivateMessage:" + sender + ":" + login + ":" + hostname + ":" + target + ":" +
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
                    synchronized (lock) {
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
        Loger.d(TAG, "onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
        if (sender.startsWith(TEACHER_PREFIX)) {
            synchronized (lock) {
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
        Loger.d(TAG, "onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                + sourceHostname + ",reason=" + reason);
        if (sourceNick.startsWith(TEACHER_PREFIX)) {
            synchronized (lock) {
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
        switch (type) {
            case XESCODE.OPENBARRAGE: {
                try {
                    boolean open = object.getBoolean("open");
                    msg = open ? "OPENBARRAGE" : "CLOSEBARRAGE";
                    mLiveTopic.getMainRoomstatus().setOpenbarrage(open);
                    mLogtf.d(msg);
                    if (mRoomAction != null) {
                        mRoomAction.onOpenbarrage(open, true);
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
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.OPENBARRAGE, XESCODE.GAG, XESCODE.OPENCHAT, XESCODE.TEACHER_MESSAGE
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
            mRoomAction.onOpenbarrage(liveTopic.getMainRoomstatus().isOpenbarrage(), false);
            mRoomAction.onDisable(forbidSendMsg, false);
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
                    if (starAction != null) {
                        starAction.onSendMsg(msg);
                    }
                } catch (Exception e) {
                    // Loger.e(TAG, "understand", e);
                    UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                    mLogtf.e("sendMessage", e);
                }
            }
            return sendMessage;
        }

        @Override
        public void praiseTeacher(String ftype, String educationStage, final HttpCallBack callBack) {
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
                    Loger.e("Duncan", "responseEntity:" + responseEntity);
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
    public void sendFlowerMessage(int ftype) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.FLOWERS);
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("ftype", ftype);
            mLiveBll.sendMessage(jsonObject);
//            mIRCMessage.sendMessage(mMainTeacherStr, jsonObject.toString());
        } catch (Exception e) {
            // Loger.e(TAG, "understand", e);
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
        EventBus.getDefault().unregister(this);
    }
}
