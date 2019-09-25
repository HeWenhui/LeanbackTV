package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.util.Log;

import com.xueersi.common.event.AppEvent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperienceIRCBll {
    String TAG = "ExperienceIRCBll";
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;
    private IIRCMessage mIRCMessage;
    private LiveGetInfo mGetInfo;
    private String expChatId;
    private static final String IRC_CHANNEL_PREFIX = "#4L";
    private LogToFile mLogtf;
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
    private int mNetWorkType;

    public ExperienceIRCBll(Context context, String expChatId, LiveGetInfo liveGetInfo) {
        this.context = context;
        this.expChatId = expChatId;
        this.mGetInfo = liveGetInfo;
        mLogtf = new LogToFile(context, TAG);
        mNetWorkType = NetWorkHelper.getNetWorkState(context);
        ProxUtil.getProxUtil().put(context, IrcAction.class, new IrcAction() {
            @Override
            public void sendMessage(String message) {
                mIRCMessage.sendMessage(message);
            }

            @Override
            public void sendNotice(String notice) {
                mIRCMessage.sendNotice(notice);
            }

            @Override
            public void sendNotice(String target, String notice) {
                mIRCMessage.sendNotice(target, notice);
            }

            @Override
            public String getNickname() {
                return mIRCMessage.getNickname();
            }
        });
    }

    public void addBll(LiveBackBaseBll businessBll) {
        if (businessBll instanceof TopicAction) {
            addTopic((TopicAction) businessBll);
        }
        if (businessBll instanceof NoticeAction) {
            addNotice((NoticeAction) businessBll);
        }
        if (businessBll instanceof MessageAction) {
            mMessageActions.add((MessageAction) businessBll);
        }
    }

    private void addTopic(TopicAction topicAction) {
        mTopicActions.add(topicAction);
    }

    public void addNotice(NoticeAction noticeAction) {
        //获得需要的notice type值
        int[] noticeFilter = noticeAction.getNoticeFilter();
        List<NoticeAction> noticeActions = null;
        if (noticeFilter != null && noticeFilter.length > 0) {
            for (int i = 0; i < noticeFilter.length; i++) {
                if ((noticeActions = mNoticeActionMap.get(noticeFilter[i])) == null) {
                    noticeActions = new ArrayList<>();
                    mNoticeActionMap.put(noticeFilter[i], noticeActions);
                }
                noticeActions.add(noticeAction);
            }
        }
    }

    public void onCreate() {
        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + "4" + "_" + expChatId + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();

        mIRCMessage = new NewIRCMessage(context, chatRoomUid, mGetInfo.getId(), "", channel);
        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();
    }

    // IRC 回调处理
    private final IRCCallback mIRCcallback = new IRCCallback() {

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
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                }
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            onTopic(channel, topic, "", 0, true, channel);
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice, String channelId) {

            int mtype = -1;
            JSONObject object = null;

            try {
                object = new JSONObject(notice);
                mtype = object.getInt("type");
                List<NoticeAction> noticeActions = mNoticeActionMap.get(mtype);
                if (noticeActions != null && noticeActions.size() > 0) {
                    for (NoticeAction noticeAction : noticeActions) {
                        try {
                            noticeAction.onNotice(sourceNick, target, object, mtype);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                } else {
                    if (UselessNotice.isUsed(mtype)) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("logtype", "experonNotice");
                            hashMap.put("expChatId", "" + expChatId);
                            hashMap.put("arts", "" + mGetInfo.getIsArts());
                            hashMap.put("pattern", "" + mGetInfo.getPattern());
                            hashMap.put("type", "" + mtype);
                            UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_NOTICE_UNKNOW, hashMap);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                }
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(TAG, e);
            }
        }

        @Override
        public void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
            Log.i("expTess", "onTopic");
            LiveTopic liveTopic = new LiveTopic();
            JSONTokener jsonTokener = null;
            try {
                jsonTokener = new JSONTokener(topic);
                JSONObject jsonObject = new JSONObject(jsonTokener);
                if (mTopicActions != null && mTopicActions.size() > 0) {
                    for (TopicAction mTopicAction : mTopicActions) {
                        try {
                            mTopicAction.onTopic(liveTopic, jsonObject, false);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }

                    }
                }
            } catch (Exception e) {
                try {
                    if (jsonTokener != null) {
                        mLogtf.e("onTopic:token=" + jsonTokener, e);
                    } else {
                        mLogtf.e("onTopic", e);
                    }
                } catch (Exception e2) {
                    mLogtf.e("onTopic", e);
                }
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onUserList(channel, users);
                }
            }
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {
            // 分发消息
            if (mMessageActions != null && mMessageActions.size() > 0) {
                for (MessageAction mesAction : mMessageActions) {
                    mesAction.onJoin(target, sender, login, hostname);
                }
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
            logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                    + sourceHostname + ",reason=" + reason);
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

    public void onNetWorkChange(AppEvent event) {
        mNetWorkType = event.netWorkType;
    }

    public void onDestory() {
        mIRCMessage.setCallback(null);
        mIRCMessage.destory();
    }
}
