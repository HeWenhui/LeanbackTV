package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience;

import android.app.Activity;

import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessageStandPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

//仿照三分屏的体验课聊天
public class StandLiveVideoExperienceBll extends LiveBackBaseBll implements KeyboardUtil.OnKeyboardShowingListener {
    /**
     * 聊天消失
     */
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

    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    /**
     * 站立直播控制器，这里是站立直播体验课，并不适合。
     */
    LiveStandMediaControllerBottom standMediaControllerBottom;

    protected LivePlayerFragment videoFragment;

    protected LiveMediaController mMediaController;

    public StandLiveVideoExperienceBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        mHttpManager = new LiveHttpManager(mContext);
    }

    private IRCMessage mIRCMessage;
    private final String IRC_CHANNEL_PREFIX = "#4L";

    @Override

    public void initView() {
        super.initView();
        videoFragment = new LivePlayerFragment();
        mMediaController = new LiveMediaController(activity, videoFragment);
        standMediaControllerBottom = new LiveStandMediaControllerBottom(activity, mMediaController, null);

        mLiveMessagePager = new LiveMessageStandPager(
                mContext,
                this,
                standMediaControllerBottom,
                liveMessageLandEntities,
                null);
    }

    @Override


    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);

        chatCfgServerList.add("");

    }

    /**
     * 连接 聊天服务器
     */
    private void connectChatServer() {
        //避免多次 连接
        if (mIRCMessage != null && mIRCMessage.isConnected()) {
            return;
        }
        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + liveGetInfo.getLiveType() + "_"
                + expChatId + "_" + liveGetInfo.getStuId() + "_" + liveGetInfo.getStuSex();
        Loger.e("ExperienceLiveVideoActivity", "=====>connectChatServer:channel=" + channel + ":nickname =" +
                chatRoomUid);

        // 获取 聊天服务器地址  的接口地址
        ArrayList<TalkConfHost> talkConfHosts = new ArrayList<>();
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
        IRCTalkConf ircTalkConf = new IRCTalkConf(null, liveGetInfo, liveGetInfo.getLiveType(), mHttpManager,
                talkConfHosts);
        mIRCMessage.setIrcTalkConf(ircTalkConf);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();

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


    @Override
    public void onKeyboardShowing(boolean isShowing) {

    }
}
