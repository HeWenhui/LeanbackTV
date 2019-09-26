package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.livemessage;

import android.app.Activity;
import android.content.Intent;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperModeChange;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IrcAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.NewIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.ExperLiveMessageStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
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
public class StandExperienceMessageBll extends StandExperienceEventBaseBll implements KeyboardUtil
        .OnKeyboardShowingListener, MessageAction, ExperModeChange {
    /**
     * 聊天消失
     */
//    private final String TAG = getClass().getSimpleName();
    private String TAG = getClass().getSimpleName();

    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    /**
     * 在线直播的聊天区
     */
    private BaseLiveMessagePager mLiveMessagePager;
    private ExperLiveMessageBll mRoomAction;
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

    /**
     * 打印日志
     */
    LogToFile logToFile;
    /**
     * 是否打开聊天开关，默认开启
     */
    private boolean openChat = true;
    private ExpLiveInfo expLiveInfo;

    public StandExperienceMessageBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    private final String IRC_CHANNEL_PREFIX = "#4L";

    /** 是否使用新IRC SDK */
//    private boolean isNewIRC = false;
    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity,
                         LiveGetInfo liveGetInfo,
                         HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        mRoomAction = new ExperLiveMessageBll(activity);
        expLiveInfo = ProxUtil.getProxUtil().get(activity, ExpLiveInfo.class);
    }

    //
    @Override
    public void initView() {
        super.initView();
//        starAction = getInstance(LiveAchievementIRCBll.class);
        videoFragment = new LivePlayerFragment();
        mMediaController = new LiveMediaController(activity, videoFragment);
        baseLiveMediaControllerBottom = ProxUtil.getProxUtil().get(activity, BaseLiveMediaControllerBottom.class);
        if (baseLiveMediaControllerBottom == null) {
            baseLiveMediaControllerBottom = new LiveStandMediaControllerBottom(activity, mMediaController, videoFragment);
        }
        mRoomAction.setBaseLiveMediaControllerBottom(baseLiveMediaControllerBottom);
        initPager();
        registerInBllToHideView();
    }

    private void initPager() {
        int mode;
        if (expLiveInfo == null) {
            mode = ExperConfig.COURSE_STATE_2;
        } else {
            mode = expLiveInfo.getMode();
        }
        mLiveMessagePager = mRoomAction.initExper(getLiveViewAction(), mode);
        mLiveMessagePager.setIrcState(videoExperiencIRCState);
        mLiveMessagePager.setGetInfo(liveGetInfo);
        //默认打开聊天区
        mLiveMessagePager.onopenchat(openChat, "", false);
    }

    /**
     * 将这个bll注册在所有的Bll中，在各种其他Bll（目前只有QuestionBll，EnglishH5CoursewareBll）显示时做出相应操作（目前是隐藏聊天区的View）
     */
    private void registerInBllToHideView() {
        //在QuestionShowReg中注册(也就是QuestionShowReg唯一实现类QuestionBLl中注册)，为了在QuestionBll显示时隐藏该聊天区
        QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
        if (questionShowReg != null) {
            questionShowReg.registQuestionShow(mLiveMessagePager);
        }
        //在EnglishShowReg中注册(也就是EnglishShowReg唯一实现类EnglishH5CoursewareBll中注册)，为了在EnglishH5CoursewareBll显示时隐藏该聊天区
        EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
        if (englishShowReg != null) {
            englishShowReg.registQuestionShow(mLiveMessagePager);
        }
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
        logger.i("openChat = " + openChat);
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

    @Override
    public void onModeChange(int oldmode, int mode) {
        logger.d("onModeChange:mode=" + mode + "," + expLiveInfo);
        if (expLiveInfo != null) {
            initPager();
        }
    }

    @Override
    public void onStartConnect() {
        logger.i("=====>onStartConnect");
        mRoomAction.onStartConnect();
    }

    @Override
    public void onConnect(IRCConnection connection) {
        logger.i("=====>onConnect");
        mRoomAction.onConnect();
    }

    @Override
    public void onRegister() {
        logger.i("=====>onRegister");
        mRoomAction.onRegister();
    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {
        logger.i("=====>onDisconnect");
        mRoomAction.onDisconnect();
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {
        logger.i("=====>onMessage");

        if (mLiveMessagePager != null) {
            mLiveMessagePager.onMessage(target, sender, login, hostname, text, "");
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target,
                                 String message) {
        logger.i("=====>onPrivateMessage:isSelf=" + isSelf);
        if (isSelf && "T".equals(message)) {
            post(new Runnable() {
                @Override
                public void run() {
                    XESToastUtils.showToast(activity, "您的帐号已在其他设备登录，请重新进入直播间");
                    Intent intent = new Intent();
                    intent.putExtra("msg", "您的帐号已在其他设备登录，请重新进入直播间");
                    activity.setResult(ShareBusinessConfig.LIVE_USER_KICK, intent);
                    activity.finish();
                }
            });
        } else {
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onPrivateMessage(isSelf, sender, login, hostname, target, message);
            }
        }
    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {
        logger.i("=====>onChannelInfo");
    }

    @Override
    public void onUserList(String channel, User[] users) {
//            Loger.e("ExperiencLvieAvtiv", "=====>onUserList start:" + peopleCount);
//            peopleCount.set(users.length, new Exception());
        mRoomAction.onUserList(channel, users);
//            Loger.e("ExperiencLvieAvtiv", "=====>onUserList end:" + peopleCount);
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        mRoomAction.onJoin(target, sender, login, hostname);
//            Loger.e("ExperiencLvieAvtiv", "=====>onJoin start:" + peopleCount);
//            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
//            Loger.e("ExperiencLvieAvtiv", "=====>onJoin end:" + peopleCount);

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        mRoomAction.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {
        mRoomAction.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
    }

    @Override
    public void onUnknown(String line) {

    }

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
                    sendRecordInteract(mVideoEntity.getInteractUrl(), mVideoEntity
                            .getChapterId(), 1);
                    IrcAction ircAction = ProxUtil.getProvide(activity, IrcAction.class);
                    ircAction.sendMessage(jsonObject.toString());
                    sendMessage = true;
                } catch (Exception e) {
                    // logger.e( "understand", e);
                    UmsAgentManager.umsAgentException(ContextManager.getContext(), "livevideo_livebll_sendMessage", e);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onKeyboardShowing(boolean isShowing) {

    }

    public void sendRecordInteract(String url, String termId, int times) {
        getCourseHttpManager().sendExpeRecordInteract(url, termId, times, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("sendRecordInteract : Success");
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("sendRecordInteract : Failure,msg: " + msg);
                super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("sendRecordInteract : Error");
                super.onPmError(responseEntity);
            }
        });
    }

}
