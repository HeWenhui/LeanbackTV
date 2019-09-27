package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
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
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.HalfBodyExpLiveMsgPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 体验课聊天，录直播
 */
public class ExperHalfbodyIRCMessBll extends LiveBackBaseBll implements MessageAction, ExperModeChange {
    private BaseLiveMessagePager mLiveMessagePager;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private ExperienceIrcState mExpIrcState;
    private List<VideoQuestionEntity> roomChatEvent;
    /** 暂时通过录直播有这个变量，判断状态 */
    private ExpLiveInfo expLiveInfo;

    public ExperHalfbodyIRCMessBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    public void initView() {
        super.initView();
        mExpIrcState = new ExperienceIrcState(mContext, liveGetInfo, liveGetInfo.getLiveTopic(), mVideoEntity, getmHttpManager());
        initPager();
    }

    private void initPager() {
        int mode;
        if (expLiveInfo == null) {
            mode = ExperConfig.COURSE_STATE_2;
        } else {
            mode = expLiveInfo.getMode();
        }
        boolean isRegister = false;
        if (mLiveMessagePager != null) {
            getLiveViewAction().removeView(mLiveMessagePager.getRootView());
            isRegister = mLiveMessagePager.isRegister();
        }
        BaseLiveMediaControllerBottom liveMediaControllerBottom = ProxUtil.getProxUtil().get(activity, BaseLiveMediaControllerBottom.class);
        if (mode == ExperConfig.COURSE_STATE_2) {
            HalfBodyExpLiveMsgPager msgPager = new HalfBodyExpLiveMsgPager(activity,
                    liveMediaControllerBottom,
                    liveMessageLandEntities, null);
            mLiveMessagePager = msgPager;
            // 关联聊天人数
            msgPager.setPeopleCount(peopleCount);
            msgPager.setIrcState(mExpIrcState);
            msgPager.onModeChange(mExpIrcState.getMode());
            msgPager.setIsRegister(isRegister);
            msgPager.setGetInfo(liveGetInfo);
        } else {
            LiveMessagePager liveMessagePager = new LiveMessagePager(activity, liveMediaControllerBottom, liveMessageLandEntities, null);
            liveMessagePager.setIsRegister(isRegister);
            liveMessagePager.setIrcState(mExpIrcState);
            liveMessagePager.setPeopleCount(peopleCount);
            liveMessagePager.setGetInfo(liveGetInfo);
            mLiveMessagePager = liveMessagePager;
        }
        onopenchat(mExpIrcState.openchat(), liveGetInfo.getLiveTopic().getMode(), false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        getLiveViewAction().addView(LiveVideoLevel.LEVEL_MES, mLiveMessagePager.getRootView(), params);
    }

    @Override
    public void onModeChange(int oldmode, int mode) {
        if (expLiveInfo != null) {
            initPager();
        }
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
        peopleCount.set(users.length, new Exception());
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onUserList(channel, users);
        }
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        logger.e("=====>onJoin start:" + peopleCount);
        peopleCount.set(peopleCount.get() + 1, new Exception(sender));
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onJoin(target, sender, login, hostname);
        }
        logger.e("=====>onJoin end:" + peopleCount);
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        Log.i("expTess", "onQuit");
        logger.e("=====>onQuit start:" + peopleCount);
        peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
        if (mLiveMessagePager != null) {
            mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
        }
        logger.e("=====>onQuit end:" + peopleCount);
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
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        expLiveInfo = ProxUtil.getProxUtil().get(activity, ExpLiveInfo.class);
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        //初始化 老师开关聊天事件
        if (lstVideoQuestion != null && lstVideoQuestion.size() > 0) {
            roomChatEvent = new ArrayList<VideoQuestionEntity>();
            VideoQuestionEntity entity = null;
            for (int i = 0; i < lstVideoQuestion.size(); i++) {
                entity = lstVideoQuestion.get(i);
                if (LocalCourseConfig.CATEGORY_OPEN_CHAT == entity.getvCategory() || LocalCourseConfig
                        .CATEGORY_CLOSE_CHAT == entity.getvCategory()) {
                    roomChatEvent.add(lstVideoQuestion.get(i));
                }
            }
        }
    }

    @Override
    public void onPositionChanged(long position) {
        if (roomChatEvent != null && roomChatEvent.size() > 0) {
            for (int i = 0; i < roomChatEvent.size(); i++) {
                // 处理聊天事件 开闭事件
                handleChatEvent(TimeUtils.gennerSecond(position), roomChatEvent.get(i));
            }
        }
    }

    private int lastCheckTime = 0;
    private static final int MAX_CHECK_TIME_RANG = 2;
    private boolean isRoomChatAvailable = true;
    /**
     * 当前聊天 状态是否初始化完成了
     */
    private boolean isChatSateInited = false;

    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {
        mLiveMessagePager.onopenchat(openchat, mode, fromNotice);
        mExpIrcState.setChatOpen(openchat);
    }

    private void handleChatEvent(int playPosition, VideoQuestionEntity chatEntity) {
        //出现视频快进
        if ((playPosition - lastCheckTime) >= MAX_CHECK_TIME_RANG || !isChatSateInited) {
            boolean roomChatAvalible = recoverChatState(playPosition);
            isChatSateInited = true;
        } else {
            if (chatEntity != null) {
                //关闭聊天
                if (LocalCourseConfig.CATEGORY_CLOSE_CHAT == chatEntity.getvCategory()) {
                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        onopenchat(false, "in-class", true);
                        isRoomChatAvailable = false;
                    }
                } else if (LocalCourseConfig.CATEGORY_OPEN_CHAT == chatEntity.getvCategory()) {
                    // 开启聊天
                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        onopenchat(true, "in-class", true);
                        isRoomChatAvailable = true;
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
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
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
