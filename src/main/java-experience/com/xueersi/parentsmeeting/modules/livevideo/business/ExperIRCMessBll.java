package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.ExperienceIrcState;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExperIRCMessBll extends LiveBackBaseBll implements TopicAction, NoticeAction, MessageAction {
    private LiveMessagePager mLiveMessagePager;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private ExperienceIrcState mExpIrcState;
    private String teacherNick = null;

    public ExperIRCMessBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        super.initView();

        mExpIrcState = new ExperienceIrcState(mContext, liveGetInfo, liveGetInfo.getLiveTopic(), mVideoEntity, getmHttpManager());
        BaseLiveMediaControllerBottom liveMediaControllerBottom = ProxUtil.getProxUtil().get(mContext, BaseLiveMediaControllerBottom.class);
        mLiveMessagePager = new LiveMessagePager(mContext, liveMediaControllerBottom, liveMessageLandEntities, null);
        mLiveMessagePager.setGetInfo(liveGetInfo);


        // 关联聊天人数
        mLiveMessagePager.setPeopleCount(peopleCount);

        // TODO: 2018/8/11 设置ircState
        //mLiveMessagePager.setLiveBll(mLiveBll);
        mLiveMessagePager.setIrcState(mExpIrcState);


        mLiveMessagePager.onModeChange(mExpIrcState.getMode());
        mLiveMessagePager.setIsRegister(true);

        // 隐藏锁屏按钮
        mLiveMessagePager.hideclock();
        addView(LiveVideoLevel.LEVEL_MES, mLiveMessagePager.getRootView());
    }

    public void onopenchat(boolean openchat, final String mode, boolean fromNotice) {
        mLiveMessagePager.onopenchat(openchat, mode, fromNotice);
        mExpIrcState.setChatOpen(openchat);
    }

    @Override
    public void onStartConnect() {
        logger.d("onStartConnect:mLiveMessagePager=" + mLiveMessagePager);
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onStartConnect();
        }
    }

    @Override
    public void onConnect(IRCConnection connection) {
        logger.d("onConnect:mLiveMessagePager=" + mLiveMessagePager);
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onConnect();
        }
    }

    @Override
    public void onRegister() {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onRegister();
        }
    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

        if (mLiveMessagePager != null) {
            mLiveMessagePager.onDisconnect();
        }
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {
        Log.i("expTess", "onMessage");
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onMessage(target, sender, login, hostname, text, "");
        }
    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

        Log.i("expTess", "onPrivateMessage");

        if (isSelf && "T".equals(message)) {
            LiveMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    XESToastUtils.showToast("您的帐号已在其他设备登录，请重新进入直播间");
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

    }

    @Override
    public void onUserList(String channel, User[] users) {
        Log.i("expTess", "onUserList");
        peopleCount.set(users.length, new Exception());
        int count = users != null ? users.length : 0;

        for (int index = 0; index < count; index++) {
            User user = users[index];
            if (user.getNick().startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
                // 辅导老师已在直播间
                teacherNick = user.getNick();
                peopleCount.set(users.length - 1, new Exception());
                break;
            }
        }

        if (mLiveMessagePager != null) {
            mLiveMessagePager.onUserList(channel, users);
        }
        logger.i("=====>onUserList end:" + peopleCount);
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        Log.i("expTess", "onJoin");
        if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            // 辅导老师进来了
            teacherNick = sender;
        } else {
            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onJoin(target, sender, login, hostname);
            }
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        Log.i("expTess", "onQuit");

        if (!sourceNick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            // 辅导老师离开了
            peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
            }
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

    @Override
    public void onUnknown(String line) {

    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject json, boolean modeChange) {
        try {
            handleTopicChat(json);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
        try {
            if (!json.has("disable_speaking")) {
                return;
            }
            JSONArray disableSpeakingArray = json.getJSONArray("disable_speaking");
            boolean selfDisable = false;

            for (int i = 0; i < disableSpeakingArray.length(); i++) {
                JSONObject object = disableSpeakingArray.getJSONObject(i);
                String id = object.getString("id");
                IrcAction ircAction = ProxUtil.getProvide(activity, IrcAction.class);
                if (id.equals("" + ircAction.getNickname())) {
                    selfDisable = true;
                    break;
                }
            }

            if (liveGetInfo.getLiveTopic().isDisable() != selfDisable) {
                liveGetInfo.getLiveTopic().setDisable(true);
                mLiveMessagePager.onDisable(selfDisable, true);
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
        }
    }


    /**
     * 处理聊天topic
     *
     * @param json
     * @throws Exception
     */
    protected void handleTopicChat(JSONObject json) throws Exception {
        if (!json.has("room_2")) {
            return;
        }

        json = json.getJSONObject("room_2");

        if (!json.has("isCalling")) {
            return;
        }

        boolean openchat = json.getBoolean("openchat");

        if (mExpIrcState.openchat() != openchat) {
            mExpIrcState.setChatOpen(openchat);
            mLiveMessagePager.onopenchat(openchat, LiveTopic.MODE_TRANING, true);
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {

        // 老师聊天
        if (type == XESCODE.TEACHER_MESSAGE) {
            String name;
            if (sourceNick.startsWith("t")) {
                name = "主讲老师";
                String teacherImg = "";
                String message = "";
                try {
                    teacherImg = liveGetInfo.getMainTeacherInfo().getTeacherImg();
                    message = data.getString("msg");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mLiveMessagePager.onMessage(target, sourceNick, "", "", message, teacherImg);
            } else {
                name = "辅导老师";
                String teamId = liveGetInfo.getStudentLiveInfo().getTeamId();
                String to = "";
                String message = "";

                try {
                    to = data.optString("to", "All");
                    message = data.getString("msg");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ("All".equals(to) || teamId.equals(to)) {
                    String teacherIMG = liveGetInfo.getTeacherIMG();
                    mLiveMessagePager.onMessage(target, sourceNick, "", "", message, teacherIMG);
                }
            }
        } else if (type == XESCODE.GAG) {
            // 禁言
            try {
                String id = data.getString("id");
                boolean disable = data.getBoolean("disable");
                IrcAction ircAction = ProxUtil.getProvide(activity, IrcAction.class);
                String nickName = "" + ircAction.getNickname();
                if (nickName.equals(id)) {
                    mLiveMessagePager.onDisable(disable, true);
                    liveGetInfo.getLiveTopic().setDisable(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == XESCODE.OPENCHAT) {

            // 开关聊天区
            try {
                String mode = "";

                if (sourceNick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
                    mode = LiveTopic.MODE_TRANING;
                } else {
                    mode = LiveTopic.MODE_CLASS;
                }

                boolean open = data.getBoolean("open");
                mExpIrcState.setChatOpen(open);
                mLiveMessagePager.onopenchat(open, mode, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.TEACHER_MESSAGE, XESCODE.GAG, XESCODE.OPENCHAT};
    }

    private List<VideoQuestionEntity> roomChatEvent = new ArrayList<>();

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        int qSize = lstVideoQuestion != null ? lstVideoQuestion.size() : 0;
        VideoQuestionEntity entity = null;
        for (int i = 0; i < qSize; i++) {
            entity = lstVideoQuestion.get(i);
            if (LocalCourseConfig.CATEGORY_OPEN_CHAT == entity.getvCategory() || LocalCourseConfig.CATEGORY_CLOSE_CHAT == entity.getvCategory()) {
                roomChatEvent.add(lstVideoQuestion.get(i));
            }
        }
    }

    @Override
    public void onPositionChanged(long position) {
        int chatSize = roomChatEvent != null ? roomChatEvent.size() : 0;

        for (int i = 0; i < chatSize; i++) {
            // 处理聊天事件 开闭事件
            handleChatEvent(TimeUtils.gennerSecond(position), roomChatEvent.get(i));
        }
    }

    private int lastCheckTime = 0;
    private static final int MAX_CHECK_TIME_RANG = 2;
    private boolean isRoomChatAvailable = true;
    private boolean isChatSateInited;

    private void handleChatEvent(int playPosition, VideoQuestionEntity chatEntity) {
        //出现视频快进
        if ((playPosition - lastCheckTime) >= MAX_CHECK_TIME_RANG || !isChatSateInited) {
            // isChatSateInited = false;
            boolean roomChatAvalible = recoverChatState(playPosition);
            logger.i("=====> resetRoomChatState_:roomChatAvalible=" + roomChatAvalible + ":" + isChatSateInited);
            isChatSateInited = true;
        } else {
            if (chatEntity != null) {
                logger.i("=====>handleChatEvent:category=" + chatEntity.getvCategory());
                //关闭聊天
                if (LocalCourseConfig.CATEGORY_CLOSE_CHAT == chatEntity.getvCategory()) {
                    logger.i("=====> CATEGORY_CLOSE_CHAT 11111:" + chatEntity.getvQuestionInsretTime() + ":"
                            + playPosition);
                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer close chat called begin");
                        onopenchat(false, "in-class", true);
                        isRoomChatAvailable = false;
                        logger.i("=====> teahcer close chat called end 11111");
                    }
                } else if (LocalCourseConfig.CATEGORY_OPEN_CHAT == chatEntity.getvCategory()) {
                    // 开启聊天
                    logger.i("=====> CATEGORY_OPEN_CHAT  22222:" + chatEntity.getvQuestionInsretTime() + ":" + playPosition);

                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer open chat called begin");
                        onopenchat(true, "in-class", true);
                        isRoomChatAvailable = true;
                        logger.i("=====> teahcer open chat called  end 111111");
                    }
                }
            }
        }

        lastCheckTime = playPosition;
    }


    /**
     * 当进入直播间 或者 发生 视频快进的情况时
     * 恢复聊天状态
     */
    private boolean recoverChatState(int playPosition) {
        List<VideoQuestionEntity> lstVideoQuestion = roomChatEvent;
        boolean roomChatAvalible = true;

        if (lstVideoQuestion != null && lstVideoQuestion.size() > 0) {
            for (VideoQuestionEntity entity : lstVideoQuestion) {
                if (entity.getvQuestionInsretTime() <= playPosition) {
                    if (entity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT) {
                        roomChatAvalible = true;
                    } else if (entity.getvCategory() == LocalCourseConfig.CATEGORY_CLOSE_CHAT) {
                        roomChatAvalible = false;
                    }
                }
            }
        }

        if (!roomChatAvalible) {
            onopenchat(false, "in-class", isRoomChatAvailable);
        } else {
            onopenchat(true, "in-class", !isRoomChatAvailable);
        }
        return roomChatAvalible;
    }
}