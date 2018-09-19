package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience;

import android.app.Activity;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessageStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExperienceLearnFeedbackPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;
import com.xueersi.ui.dataload.PageDataLoadEntity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

//zyy:仿照全身直播的体验课聊天
public class StandLiveVideoExperienceBll extends LiveBackBaseBll implements KeyboardUtil.OnKeyboardShowingListener {
    /**
     * 聊天消失
     */
//    private final String TAG = getClass().getSimpleName();
    private String TAG = getClass().getSimpleName();
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    /**
     * 在线直播的聊天区
     */
    private LiveMessageStandPager mLiveMessagePager;

    private LiveHttpManager mHttpManager;

    private int mNetWorkType;
    /**
     * 聊天服务器 参数获取   接口地址  测试时可以采用写死的方法来测试
     */
    private List<String> chatCfgServerList = new ArrayList<>();
    /**
     * 连接聊天服务器的 chnnel id
     */
    private String expChatId = "";

    private LiveTopic mLiveTopic = new LiveTopic();

    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    /**
     * 站立直播控制器，这里是站立直播体验课，并不适合。
     */
//    LiveStandMediaControllerBottom standMediaControllerBottom;

    protected LivePlayerFragment videoFragment;


    protected LiveMediaController mMediaController;

    private LiveAchievementIRCBll starAction;
    /**
     * 打印日志
     */
    LogToFile logToFile;
    /**
     * 是否打开聊天开关，默认开启
     */
    private boolean openChat = true;

    LectureLivePlayBackBll lectureLivePlayBackBll;

    public StandLiveVideoExperienceBll(Activity activity, LiveBackBll liveBackBll, LectureLivePlayBackBll
            lectureLivePlayBackBll) {
        super(activity, liveBackBll);
        mHttpManager = new LiveHttpManager(mContext);
        this.lectureLivePlayBackBll = lectureLivePlayBackBll;
    }

    private IRCMessage mIRCMessage;
    private final String IRC_CHANNEL_PREFIX = "4L";

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity,
                         LiveGetInfo liveGetInfo,
                         HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
    }

    //
    @Override
    public void initView() {
        super.initView();


        starAction = getInstance(LiveAchievementIRCBll.class);
        videoFragment = new LivePlayerFragment();
        mMediaController = new LiveMediaController(activity, videoFragment);
        baseLiveMediaControllerBottom = new LiveStandMediaControllerBottom(activity, mMediaController, videoFragment);

        mLiveMessagePager = new LiveMessageStandPager(
                mContext,
                this,
                baseLiveMediaControllerBottom,
                liveMessageLandEntities,
                null);
//        初始化默认看不见这个布局

        mLiveMessagePager.setIrcState(videoExperiencIRCState);
        mRootView.addView(mLiveMessagePager.getRootView());
        mLiveMessagePager.setGetInfo(liveGetInfo);
        //默认打开聊天区
        mLiveMessagePager.onopenchat(openChat, "", false);

        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(mLiveMessagePager);
        }
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(mLiveMessagePager);
        }

        connectChatServer();
    }

    /**
     * 连接 聊天服务器
     */
    private void connectChatServer() {
        //避免多次 连接
        if (mIRCMessage != null && mIRCMessage.isConnected()) {
            return;
        }
        expChatId = mVideoEntity.getExpChatId();
        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + liveGetInfo.getLiveType() + "_"
                + expChatId + "_" + liveGetInfo.getStuId() + "_" + liveGetInfo.getStuSex();
        Loger.e("ExperienceLiveVideoActivity", "=====>connectChatServer:channel=" + channel + ":nickname =" +
                chatRoomUid);

        // 获取 聊天服务器地址  的接口地址
        ArrayList<TalkConfHost> talkConfHosts = new ArrayList<>();

        chatCfgServerList = mVideoEntity.getRoomChatCfgServerList();
        //后台没有数据时自己测试用的接口
//        chatCfgServerList.add("chatgslb.xescdn.com");
//        chatCfgServerList.add("chatgslb.xesimg.com");
//        chatCfgServerList.add("10.99.1.15");

        TalkConfHost confHost = null;
        if (chatCfgServerList != null && chatCfgServerList.size() > 0) {
            for (int i = 0; i < chatCfgServerList.size(); i++) {
                confHost = new TalkConfHost();
                confHost.setHost(chatCfgServerList.get(i));
                talkConfHosts.add(confHost);
            }
        }
        mNetWorkType = NetWorkHelper.getNetWorkState(mContext);
        mIRCMessage = new IRCMessage(mContext, mNetWorkType, channel, liveGetInfo.getStuName(), chatRoomUid);
        IRCTalkConf ircTalkConf = new IRCTalkConf(null, liveGetInfo, 4, mHttpManager,
                talkConfHosts);
        mIRCMessage.setIrcTalkConf(ircTalkConf);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();

    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        super.onQuestionEnd(questionEntity);

    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll
            .ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);

        if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT) {
            openChat = true;
        } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_CLOSE_CHAT) {
            openChat = false;
        } else {
            openChat = false;
        }
        Log.e(TAG, "openChat = " + openChat);
        if (mLiveMessagePager != null) {
//            mLiveMessagePager.onQuestionShow(true);
            mLiveMessagePager.onopenchat(openChat, "", false);
        }
    }

    /**
     * 判断当前是什么类型题目所需要实现的接口，这里用来判断是否开启和关闭聊天，
     */

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_OPEN_CHAT, LocalCourseConfig.CATEGORY_CLOSE_CHAT};
    }


    private final IRCCallback mIRCcallback = new IRCCallback() {

        @Override
        public void onStartConnect() {
            Loger.e("ExperiencLvieAvtiv", "=====>onStartConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onStartConnect();
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            Loger.e("ExperiencLvieAvtiv", "=====>onConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onConnect();
            }
        }

        @Override
        public void onRegister() {
            Loger.e("ExperiencLvieAvtiv", "=====>onRegister");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onRegister();
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            Loger.e("ExperiencLvieAvtiv", "=====>onDisconnect");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onDisconnect();
            }

        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            Loger.e("ExperiencLvieAvtiv", "=====>onMessage");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onMessage(target, sender, login, hostname, text, "");
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                     String message) {
            Loger.e("ExperiencLvieAvtiv", "=====>onPrivateMessage");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onPrivateMessage(isSelf, sender, login, hostname, target, message);
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            Loger.e("ExperiencLvieAvtiv", "=====>onChannelInfo");
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String
                notice) {
            Loger.e("ExperiencLvieAvtiv", "=====>onNotice");
        }

        @Override
        public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
            Loger.e("ExperiencLvieAvtiv", "=====>onTopic");

        }

        @Override
        public void onUserList(String channel, User[] users) {
//            Loger.e("ExperiencLvieAvtiv", "=====>onUserList start:" + peopleCount);
//            peopleCount.set(users.length, new Exception());
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onUserList(channel, users);
            }
//            Loger.e("ExperiencLvieAvtiv", "=====>onUserList end:" + peopleCount);
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {

//            Loger.e("ExperiencLvieAvtiv", "=====>onJoin start:" + peopleCount);
//            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onJoin(target, sender, login, hostname);
            }
//            Loger.e("ExperiencLvieAvtiv", "=====>onJoin end:" + peopleCount);

        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
//            Loger.e("ExperiencLvieAvtiv", "=====>onQuit start:" + peopleCount);
//            peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
            }
//            Loger.e("ExperiencLvieAvtiv", "=====>onQuit end:" + peopleCount);
        }

        @Override
        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
                recipientNick, String reason) {
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
            }
        }

        @Override
        public void onUnknown(String line) {

        }
    };

    private final IRCState videoExperiencIRCState = new IRCState() {
        @Override
        public String getMode() {

            return "";
        }

        @Override
        public boolean isOpenbarrage() {
            return false;
        }

        //是否打开聊天消息
        @Override
        public boolean openchat() {
            return openChat;
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
                        name = liveGetInfo.getStuName();
                    }
                    jsonObject.put("name", name);
                    jsonObject.put("path", "" + liveGetInfo.getHeadImgPath());
                    jsonObject.put("version", "" + liveGetInfo.getHeadImgVersion());
                    jsonObject.put("msg", msg);
//                    if (haveTeam) {
//                        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
//                        String teamId = studentLiveInfo.getTeamId();
//                        jsonObject.put("from", "android_" + teamId);
//                        jsonObject.put("to", teamId);
//                    }
                    lectureLivePlayBackBll.sendRecordInteract(mVideoEntity.getInteractUrl(), mVideoEntity
                            .getChapterId(), 1);
                    mIRCMessage.sendMessage(jsonObject.toString());
                    sendMessage = true;
                    if (starAction != null) {
                        starAction.onSendMsg(msg);
                    }
                } catch (Exception e) {
                    // Loger.e(TAG, "understand", e);
                    UmsAgentManager.umsAgentException(BaseApplication.getContext(), "livevideo_livebll_sendMessage", e);
                    logToFile.e(TAG + ":sendMessage", e);
                }
            }
            return sendMessage;
        }

        @Override
        public void praiseTeacher(String formWhichTeacher, String s, String s1, HttpCallBack gold) {

        }

        @Override
        public boolean isDisable() {
            return false;
        }

        @Override
        public boolean isHaveTeam() {
            return false;
        }

        @Override
        public boolean isSeniorOfHighSchool() {
            return false;
        }

        @Override
        public void getMoreChoice(PageDataLoadEntity mPageDataLoadEntity, AbstractBusinessDataCallBack
                getDataCallBack) {

        }

        @Override
        public boolean isOpenZJLKbarrage() {
            return false;
        }

        @Override
        public boolean isOpenFDLKbarrage() {
            return false;
        }

        @Override
        public String getLKNoticeMode() {
            return "";
        }
    };


    @Override
    public void onDestory() {
        super.onDestory();
        if (mIRCMessage != null) {
            mIRCMessage.setCallback(null);
            mIRCMessage.destory();
            Loger.e(TAG, "=========>:mIRCMessage.destory()");
        }
    }

    @Override
    public void onKeyboardShowing(boolean isShowing) {

    }
}
