package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.airbnb.lottie.L;
import com.tal100.chatsdk.ChatClient;
import com.tal100.chatsdk.IChatClientListener;
import com.tal100.chatsdk.IPeerChatListener;
import com.tal100.chatsdk.IRoomChatListener;
import com.tal100.chatsdk.PMDefs;
import com.tal100.pushsdk.utils.JsonUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class IRCMessage {
    private String TAG = "IRCMessage";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private IRCConnection mConnection;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private IRCCallback mIRCCallback;
    private String[] mChannels;
    private String mNickname;
    /**
     * 备用用户聊天服务配置列表
     */
    private List<NewTalkConfEntity> mNewTalkConf = new ArrayList<>();
    private IRCTalkConf ircTalkConf;
    /**
     * 从上面的列表选择一个服务器
     */
    private int mSelectTalk = 0;
    private LogToFile mLogtf;
    /**
     * 播放器是不是销毁
     */
    private boolean mIsDestory = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 网络类型
     */
    private int netWorkType;
    /**
     * 调度是不是在无网络下失败
     */
    private boolean connectError = false;
    /**
     * 是不是获得过用户列表
     */
    private boolean onUserList = false;
    /**
     * 和服务器的ping，线程池
     */
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    Context context = ContextManager.getContext();
    ChatClient mChatClient;
    Context mContext;
    File workSpaceDir = new File(context.getCacheDir(), "irc/workspace");
    private String currentMode;
    LiveGetInfo mLiveInfo;
    private List<String> roomid;
    private PMDefs.LiveInfo liveInfo;
    private boolean isConnected;

    public IRCMessage(Context context, int netWorkType, String login, String nickname, String... channel) {
        this.netWorkType = netWorkType;
        this.mChannels = channel;
        this.mNickname = nickname;
        this.mContext = context;
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        mLogtf.d("IRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
    }

    public IRCMessage(Context context, int netWorkType, String login, String nickname, LiveGetInfo liveInfo, String... channel) {
        this.netWorkType = netWorkType;
        this.mChannels = channel;
        this.mNickname = nickname;
        this.mContext = context;
        this.mLiveInfo = liveInfo;
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        mLogtf.d("IRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
    }

    /**
     * 是不是连接中
     *
     * @return
     */
    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    /**
     * 是不是获得过用户列表
     *
     * @return
     */
    public boolean onUserList() {
        return onUserList;
//        return onUserList && mConnection != null && mConnection.isConnected();
    }

    /**
     * 网络变化
     *
     * @param netWorkType
     */
//    public void onNetWorkChange(int netWorkType) {
//        this.netWorkType = netWorkType;
//        if (netWorkType != NetWorkHelper.NO_NETWORK) {
//            mLogtf.d("onNetWorkChange:connectError=" + connectError);
//            if (connectError) {
//                connectError = false;
//                liveThreadPoolExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        connect("onNetWorkChange");
//                    }
//                });
//            }
//        }
//        if (ircTalkConf != null) {
//            ircTalkConf.onNetWorkChange(netWorkType);
//        }
//    }

    /**
     * 自己发的消息，如果没发送出去，暂时保存下来
     */
    Vector<String> privMsg = new Vector<>();
    private IChatClientListener mClientListener = new IChatClientListener() {

        /**
         * 登陆回调
         * @param loginResp
         */
        @Override
        public void onLoginResponse(PMDefs.LoginResp loginResp) {
            // 0-成功, 432-错误昵称, 464-密码错误，436-昵称冲突, 433-昵称已存在，

            logger.i("ircsdk login code:" + loginResp.code);
            logger.i("ircsdk login info:" + loginResp.info);
            String target = mNickname;
            if (PMDefs.ResultCode.Result_Success == loginResp.code) {
                if (roomid == null) {
                    roomid = new ArrayList<>();
                }
                for (int i = 0; i < mChannels.length; i++) {
                    roomid.add("#" + mChannels[i]);
                }
                mChatClient.getRoomManager().joinChatRooms(roomid);
            } else if (PMDefs.ResultCode.Result_NicknameAlreadyExist == loginResp.code) {
                mChatClient.logout("Nickname is already in use");
                if (mNickname.startsWith("s")) {
                    target = "w" + mNickname;
                } else if (mNickname.startsWith("ws")) {
                    target = mNickname.substring(1);
                }
                mNickname = target;
                liveInfo.nickname = target;
                mChatClient.setLiveInfo(liveInfo);
                MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                int logincode = mChatClient.login(myUserInfoEntity.getPsimId(), myUserInfoEntity.getPsimPwd());
            }
        }

        /**
         * 聊天室中用户登出，聊天室中其他用户会收到登出通知
         * @param logoutNotice
         */
        @Override
        public void onLogoutNotice(PMDefs.LogoutNotice logoutNotice) {
            //0表示成功，非0表示失败，5 接入服务器异常导致退出
            logger.i("ircsdk loginout code:" + logoutNotice.code);
            logger.i("ircsdk loginout info:" + logoutNotice.info);
            logger.i("ircsdk loginout userinfo:" + logoutNotice.userInfo.nickname);
            String sourceNick = logoutNotice.userInfo.nickname;
            String reason = logoutNotice.info;
            String sourceLogin = logoutNotice.userInfo.psid;
            String sourceHostname = "";
            if (sourceNick.startsWith("s_") || sourceNick.startsWith("ws_")) {
                logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                        + sourceHostname + ",reason=" + reason);
            } else {
                mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                        + sourceHostname + ",reason=" + reason);
            }
            if (mIRCCallback != null) {
                //  如果不是专属老师
                if (currentMode == null) {
                    mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                } else {
                    if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                        mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                     /*       logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
                    }
                    if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1) {
                        mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                          /*  logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
                    }
                }

            }
        }

        /**
         * 网络变化
         * @param netStatusResp
         */
        @Override
        public void onNetStatusChanged(PMDefs.NetStatusResp netStatusResp) {
            logger.i("ircsdk net status:" + netStatusResp.netStatus);
            if (PMDefs.ResultCode.Result_SeqRepeat == netStatusResp.netStatus) {
                if (mIRCCallback != null) {
                    mIRCCallback.onStartConnect();
                }
            } else if (PMDefs.ResultCode.Result_AccessServerError == netStatusResp.netStatus){
                mDisconnectCount++;
                mLogtf.d("onDisconnect:count=" + mDisconnectCount + ",isQuitting=" + false);
                if (mIRCCallback != null) {
                    mIRCCallback.onDisconnect(null, false);
                }
            }
        }
    };
    //单聊
    private IPeerChatListener mPeerListener = new IPeerChatListener() {

        /**
         * 收到单聊消息
         * @param peerChatMessage
         */
        @Override
        public void onRecvPeerMessage(PMDefs.PeerChatMessage peerChatMessage) {
            //  long		msgId;
            //  long 		timestamp;
            //  int		msgPriority;		//消息优先级
            //	PsIdEntity	fromUserInfo;	//发送用户信息
            //	PsIdEntity	toUserInfo;		//接收用户信息
            //	String		content;		//消息内容
            boolean isSelf = false;
            String sender = peerChatMessage.fromUserId.nickname;
            String target = "PRIVMSG";
            String message = peerChatMessage.content;
            logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
            String name = mConnection.getName();
            String login = "";
            String hostname = "";
            if (sender.startsWith("p") || sender.startsWith("pt")) {
                String subStr = mNickname.substring(1);
                if (sender.endsWith(subStr)) {
                    try {
                        JSONObject studentObj = new JSONObject(message);
                        int type = studentObj.getInt("type");
                        if (type == XESCODE.REQUEST_STUDENT_PUSH) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("type", "" + XESCODE.STUDENT_REPLAY);
                                jsonObject.put("playUrl", "");
                                jsonObject.put("status", "unsupported");
                                mConnection.sendMessage(sender, jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            } else {
                if (name.startsWith("ws")) {
                    if (name.endsWith(sender)) {
                        isSelf = true;
                    }
                } else if (name.startsWith("s")) {
                    if (sender.endsWith(name)) {
                        isSelf = true;
                    }
                }
            }
            if (mIRCCallback != null) {
                if (mChannels.length > 1) {
                    if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                        mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                    }

                    if (LiveTopic.MODE_TRANING.equals(currentMode)) {
                        mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                    }
                } else {
                    mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                }
            }

        }

        /**
         * 发送单聊消息回调
         * @param sendPeerMessageResp
         */
        @Override
        public void onSendPeerMessageResponse(PMDefs.SendPeerMessageResp sendPeerMessageResp) {
            /** int 		code; 				//401-昵称不存在
             String		info;				//描述信息
             PsIdEntity	fromUserInfo;
             PsIdEntity	toUserInfo;
             成功 code 0 目前不支持
             **/

            logger.i("ircsdk onSendPeerMessageResponse");
            logger.i("ircsdk onSendPeerMessageResponse code" + sendPeerMessageResp.code);
            logger.i("ircsdk onSendPeerMessageResponse info" + sendPeerMessageResp.info);
        }
    };

    private IRoomChatListener mRoomListener = new IRoomChatListener() {

        /**
         * 本人进入聊天室回调响应
         * @param joinRoomResp
         */
        @Override
        public void onJoinRoomResponse(PMDefs.JoinRoomResp joinRoomResp) {
            // 0-加入成功 403-聊天室不存在， 405-加入聊天室过多，471-聊天室上限已满
            logger.i("ircsdk join room code: " + joinRoomResp.code);
            logger.i("ircsdk join room info " + joinRoomResp.info);
            if (PMDefs.ResultCode.Result_Success == joinRoomResp.code) {
                isConnected = true;
                if (mIRCCallback != null) {
                    mIRCCallback.onRegister();
                    mIRCCallback.onConnect(null);
                    String target = mNickname;
                    if (mNickname.startsWith("s")) {
                        target = "w" + mNickname;
                    } else if (mNickname.startsWith("ws")) {
                        target = mNickname.substring(1);
                    }
                    PMDefs.PsIdEntity user = new PMDefs.PsIdEntity(target, UserBll.getInstance().getMyUserInfoEntity().getPsimId());
                    List<PMDefs.PsIdEntity> userList = new ArrayList<>();
                    userList.add(user);
                    mChatClient.getPeerManager().sendPeerMessage(userList, "T", 1);
                }
            }

        }

        /**
         * 进入聊天室回调通知
         * @param joinRoomNotice
         */
        @Override
        public void onJoinRoomNotice(PMDefs.JoinRoomNotice joinRoomNotice) {
            logger.i("ircsdk onJoinRoomNotic" + joinRoomNotice.info);
            logger.i("ircsdk ");
            mLogtf.d("onConnect:count=" + mConnectCount);
            mConnectCount++;
            //如果相同nickname加入 被踢
            boolean isSelf = false;
            if (mNickname.startsWith("ws")) {
                if (mNickname.endsWith(joinRoomNotice.userInfo.nickname)) {
                    isSelf = true;
                    mIRCCallback.onPrivateMessage(isSelf, joinRoomNotice.userInfo.nickname, joinRoomNotice.roomId, "", "PRIVMSG", "T");
                }
            } else if (mNickname.startsWith("s")) {
                if (joinRoomNotice.userInfo.nickname.endsWith(mNickname)) {
                    isSelf = true;
                    mIRCCallback.onPrivateMessage(isSelf, joinRoomNotice.userInfo.nickname, joinRoomNotice.roomId, "", "PRIVMSG", "T");
                }
            }
            if (joinRoomNotice.userInfo.nickname.startsWith("s_") || joinRoomNotice.userInfo.nickname.startsWith("ws_")) {
                logger.i("onJoin:target=" + joinRoomNotice.roomId + ",sender=" + joinRoomNotice.userInfo.nickname + ",login=" + "" + ",hostname=" + "");
            } else {
                mLogtf.d("onJoin:target=" + joinRoomNotice.roomId + ",sender=" + joinRoomNotice.userInfo.nickname + ",login=" + "" + ",hostname=" + "");
            }
            if (mIRCCallback != null) {
                //  如果不是专属老师
                if (currentMode == null) {
                    mLogtf.d("onJoin:target=" + joinRoomNotice.roomId + ",sender=" + joinRoomNotice.userInfo.nickname + ",login=" + "" + ",hostname=" + "");
                    mIRCCallback.onJoin(joinRoomNotice.info, joinRoomNotice.userInfo.nickname, "", "");
                } else {
                    if (LiveTopic.MODE_CLASS.equals(currentMode) && mChannels[0].equals(joinRoomNotice.roomId)) {
                        mIRCCallback.onJoin(joinRoomNotice.roomId, joinRoomNotice.userInfo.nickname, "", "");
                    }
                    if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1 && mChannels[1].equals(joinRoomNotice.roomId)) {
                        mIRCCallback.onJoin(joinRoomNotice.roomId, joinRoomNotice.userInfo.nickname, "", "");
                    }
                }

            }


//            mHandler.postDelayed(mPingRunnable, mPingDelay);

        }

        /**
         *  用户成功加入聊天室后，会收到系统广播的聊天室信息。
         * @param roomMetaData
         */
        @Override
        public void onRecvRoomMetaData(PMDefs.RoomMetaData roomMetaData) {
            //code 322-聊天室信息 323-聊天室信息返回结束
            logger.i("ircsdk room Meta data code: " + roomMetaData.code);
            logger.i("ircsdk room Meta data : " + roomMetaData.content.toString());
            if (PMDefs.ResultCode.Result_RoomData == roomMetaData.code) {
                String channel = roomMetaData.roomId;
                String topic = roomMetaData.content.get("topic");
                long date = 0;
                if (mIRCCallback != null) {
                    mIRCCallback.onChannelInfo(roomMetaData.roomId, Integer.parseInt(roomMetaData.content.get("number")), JsonUtil.toJson(roomMetaData.content.get("topic")));
                    onTopic(channel, topic, date);
                }
            }

        }

        /**
         * 昵称列表通知
         * @param roomUserList
         */
        @Override
        public void onRecvRoomUserList(PMDefs.RoomUserList roomUserList) {
            // 353-聊天室昵称列表 366-聊天室昵称列表结束
            logger.i("ircsdk room user code: " + roomUserList.code);
            logger.i("ircsdk room user list size: " + roomUserList.userList.size());
            if (PMDefs.ResultCode.Result_RoomUserList == roomUserList.code) {
                onUserList = true;
                String s = "___bug  onUserList:channel=" + roomUserList.roomId + ",users=" + roomUserList.userList.size();
                if (roomUserList.userList != null && roomUserList.userList.size() > 0) {
                    User[] users = new User[roomUserList.userList.size()];
                    PMDefs.PsIdEntity userEntity;
                    for (int i = 0; i < roomUserList.userList.size(); i++) {
                        userEntity = roomUserList.userList.get(i);
                        users[i] = new User(userEntity.psid, userEntity.nickname);
                    }
                    mLogtf.d(s);
                    if (mIRCCallback != null) {
                        //  如果不是专属老师
                        if (currentMode == null) {
                            mIRCCallback.onUserList(roomUserList.roomId, users);
                        } else {
                            if (LiveTopic.MODE_CLASS.equals(currentMode) && ("#" + mChannels[0]).equals(roomUserList.roomId)) {
                                StringBuilder sb = new StringBuilder();
                                for (User user : users) {
                                    sb.append(user.getNick());
                                }
                                s = "___bug2  onUserList:channel=" + roomUserList.roomId + ",users=" + users.length + "___" + sb.toString();
                                //  mLogtf.d(s);
                                mIRCCallback.onUserList(roomUserList.roomId, users);
                            }

                            if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1 && ("#" + mChannels[1]).equals(roomUserList.roomId)) {
                                StringBuilder sb = new StringBuilder();
                                for (User user : users) {
                                    sb.append(user.getNick());
                                }
                                s = "___bug3  onUserList:channel=" + roomUserList.roomId + ",users=" + users.length + "___" + sb.toString();
                                // mLogtf.d(s);
                                mIRCCallback.onUserList(roomUserList.roomId, users);
                            }
                        }
                    }
                }
            }

        }

        /**
         *  当用户成功加入聊天室后，会收到系统广播的聊天室topic。
         * @param roomTopic
         */
        @Override
        public void onRecvRoomTopic(PMDefs.RoomTopic roomTopic) {
            // 332-topic内容， 333-聊天室topic内容结束 可忽略
            logger.i("ircsdk room topic code" + roomTopic.code);
            logger.i("ircsdk room topic" + roomTopic.topic);
            String channel = roomTopic.roomId;
            String topic = roomTopic.topic;
            long date = 0;
            onTopic(channel, topic, date);

        }

        /**
         *  退出聊天室 (暂不可用)
         * @param leaveRoomResp
         */
        @Override
        public void onLeaveRoomResponse(PMDefs.LeaveRoomResp leaveRoomResp) {
            //0-成功退出 442-你不在该聊天室
            logger.i("ircsdk leave room code" + leaveRoomResp.code);
        }

        /**
         * 退出聊天室 通知
         * @param leaveRoomNotice
         */
        @Override
        public void onLeaveRoomNotice(PMDefs.LeaveRoomNotice leaveRoomNotice) {
            logger.i("ircsdk onLeaveRoomNotic");
//            mDisconnectCount++;
//            if (leaveRoomNotice.userInfo.nickname.equals(mNickname)){
//                mLogtf.d("onDisconnect:count=" + mDisconnectCount + ",isQuitting=" + isQuitting);
//                if (mIRCCallback != null) {
//                    mIRCCallback.onDisconnect(null, true);
//                }
//                mHandler.removeCallbacks(mPingRunnable);
//                if (!isQuitting) {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            liveThreadPoolExecutor.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    IRCMessage.this.connect("onDisconnect");
//                                }
//                            });
//                        }
//                    }, 2000);
//                }
//            }

        }

        /**
         * 接收群聊.信息
         * @param roomChatMessage
         */
        @Override
        public void onRecvRoomMessage(PMDefs.RoomChatMessage roomChatMessage) {
            //0-topic, 1-notice,99-primsg
            logger.i("ircsdk room chat message priority " + roomChatMessage.msgPriority);
            logger.i("ircsdk onRecvRoomMessage" + " sender=" + roomChatMessage.fromUserId.nickname + ":" + roomChatMessage.content);
            mLogtf.d("onMessage:sender=" + roomChatMessage.fromUserId.nickname + ":" + roomChatMessage.content);
            try {
                String sender = roomChatMessage.fromUserId.nickname;
                String text = roomChatMessage.content;
                String channel = roomChatMessage.toRoomId;
                long date = roomChatMessage.timestamp;
                String login = "";
                String hostname = "";
                String target = "";
                JSONObject msgJosn = new JSONObject(roomChatMessage.content);

                //  如果是专属老师
                if (mIRCCallback != null) {
                    if (PMDefs.MessagePriority.MSG_PRIORITY_TOPIC == roomChatMessage.msgPriority) {
                        target = "TOPIC";
                        mLogtf.d("onTopic:channel=" + channel + ",topic=" + text);
                        onTopic(channel, text, date);
                    } else if (PMDefs.MessagePriority.MSG_PRIORITY_NOTICE == roomChatMessage.msgPriority) {
                        target = "NOTICE";
                        boolean send = true;
                        try {
                            int mtype = msgJosn.getInt("type");
                            if (mtype == XESCODE.SPEECH_RESULT) {
                                send = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (send) {
                            mLogtf.d("onNotice:target=" + target + ",notice=" + text);
                        }
                        if (mIRCCallback != null) {
                            if (currentMode == null) {
                                mIRCCallback.onNotice(sender, "", "", target, text, channel);
                            } else {
                                if (mChannels.length > 1) {
                                    if (("#" + mChannels[0]).equals(roomChatMessage.toRoomId)) {
                                        mIRCCallback.onNotice(sender, "", "", target, text, channel);
                                    }
                                    if (("#" + mChannels[1]).equals(roomChatMessage.toRoomId)) {
                                        mIRCCallback.onNotice(sender, "", "", target, text, channel);
                                    }
                                }
                            }
                        }
                    } else if (PMDefs.MessagePriority.MSG_PRIORITY_PRI == roomChatMessage.msgPriority) {
                        mLogtf.d("onMessage:sender=" + sender + ":" + text);
                        target = "PRIVMSG";
                        String name = sender;
                        String msg = "";
                        if (msgJosn.has("name")){
                            name = msgJosn.getString("name");
                        }
                        if (msgJosn.has("msg")){
                            msg = msgJosn.getString("msg");
                        }
                        //  如果是专属老师
                        if (mIRCCallback != null) {
                            if (sender.startsWith("t_") || sender.startsWith("f_")) {
                                if (mChannels.length > 1) {
                                    if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                                        mIRCCallback.onMessage(target, name, login, hostname, msg);
                                    }
                                    if (LiveTopic.MODE_TRANING.equals(currentMode)) {
                                        mIRCCallback.onMessage(target, name, login, hostname, msg);
                                    }
                                } else {
                                    mIRCCallback.onMessage(target, name, login, hostname, msg);
                                }
                            } else if (sender.startsWith("s_") || sender.startsWith("ws_")) {
                                boolean isSelf = false;
                                if (name.startsWith("ws")) {
                                    if (name.endsWith(sender)) {
                                        isSelf = true;
                                    }
                                } else if (name.startsWith("s")) {
                                    if (sender.endsWith(name)) {
                                        isSelf = true;
                                    }
                                }
                                mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, text);
                            }

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /**
         * 发送消息-回调响应
         * @param sendRoomMessageResp
         */
        @Override
        public void onSendRoomMessageResp(PMDefs.SendRoomMessageResp sendRoomMessageResp) {
            // 0-成功， 442->发送者不在聊天室
            logger.i("ircsdk send room message resp:" + sendRoomMessageResp.code);
            logger.i("ircsdk onSendRoomMessageResp");
        }
    };

    public void create() {

        if (!workSpaceDir.exists()) {
            workSpaceDir.mkdirs();
        }
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mChatClient = ChatClient.getInstance();
        mChatClient.addListener(mClientListener);
        mChatClient.getRoomManager().addListener(mRoomListener);
        mChatClient.getPeerManager().addListener(mPeerListener);
        String appid = myUserInfoEntity.getPsAppId();
        int initcode = mChatClient.init(mContext.getApplicationContext(), myUserInfoEntity.getPsAppId(), myUserInfoEntity.getPsAppClientKey(), workSpaceDir.getAbsolutePath());
        liveInfo = new PMDefs.LiveInfo();
        liveInfo.nickname = mNickname;
        liveInfo.realname = myUserInfoEntity.getRealName();
        liveInfo.liveId = mLiveInfo.getId();
        liveInfo.username = mLiveInfo.getStuName();
        liveInfo.classId = mLiveInfo.getStudentLiveInfo().getClassId();
        liveInfo.businessId = "1";
        liveInfo.location = myUserInfoEntity.getAreaCode();
        mChatClient.setLiveInfo(liveInfo);
        logger.d("businessid:" + liveInfo.businessId + "      liveid:" + liveInfo.liveId);
        int logincode = mChatClient.login(myUserInfoEntity.getPsimId(), myUserInfoEntity.getPsimPwd());


//        mConnection = new IRCConnection(privMsg);
//        mConnection.setCallback(new IRCCallback() {
//
//            @Override
//            public void onStartConnect() {
//                if (mIRCCallback != null) {
//                    mIRCCallback.onStartConnect();
//                }
//            }
//
//            @Override
//            public void onRegister() {
//                mLogtf.d("onRegister");
//             /*   mConnection.joinChannel("#" + mChannel);
//                mConnection.joinChannel("#300141-29981");*/
//                for (String channel : mChannels) {
//                    mConnection.joinChannel("#" + channel);
//                }
//                if (mIRCCallback != null) {
//                    mIRCCallback.onRegister();
//                }
//            }
//
//            @Override
//            public void onMessage(String target, String sender, String login, String hostname, String text) {
//                mLogtf.d("onMessage:sender=" + sender + ":" + text);
//                //  如果是专属老师
//                if (mIRCCallback != null) {
//                    if (mChannels.length > 1) {
//                        if (LiveTopic.MODE_CLASS.equals(currentMode)) {
//                            mIRCCallback.onMessage(target, sender, login, hostname, text);
//                        }
//
//                        if (LiveTopic.MODE_TRANING.equals(currentMode)) {
//                            mIRCCallback.onMessage(target, sender, login, hostname, text);
//                        }
//                    } else {
//                        mIRCCallback.onMessage(target, sender, login, hostname, text);
//                    }
//                }
//            }
//
//            @Override
//            public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
//                logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
//                String name = mConnection.getName();
//                if (sender.startsWith("p") || sender.startsWith("pt")) {
//                    String subStr = mNickname.substring(1);
//                    if (sender.endsWith(subStr)) {
//                        try {
//                            JSONObject studentObj = new JSONObject(message);
//                            int type = studentObj.getInt("type");
//                            if (type == XESCODE.REQUEST_STUDENT_PUSH) {
//                                JSONObject jsonObject = new JSONObject();
//                                try {
//                                    jsonObject.put("type", "" + XESCODE.STUDENT_REPLAY);
//                                    jsonObject.put("playUrl", "");
//                                    jsonObject.put("status", "unsupported");
//                                    mConnection.sendMessage(sender, jsonObject.toString());
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        return;
//                    }
//                } else {
//                    if (name.startsWith("ws")) {
//                        if (name.endsWith(sender)) {
//                            isSelf = true;
//                        }
//                    } else if (name.startsWith("s")) {
//                        if (sender.endsWith(name)) {
//                            isSelf = true;
//                        }
//                    }
//                }
//                if (mIRCCallback != null) {
//                    if (mChannels.length > 1) {
//                        if (LiveTopic.MODE_CLASS.equals(currentMode)) {
//                            mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
//                        }
//
//                        if (LiveTopic.MODE_TRANING.equals(currentMode)) {
//                            mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
//                        }
//                    } else {
//                        mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
//                    }
//                }
//            }
//
//            @Override
//            public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
//                                 String notice, String channelId) {
//                boolean send = true;
//                try {
//                    JSONObject object = new JSONObject(notice);
//                    int mtype = object.getInt("type");
//                    if (mtype == XESCODE.SPEECH_RESULT) {
//                        send = false;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (send) {
//                    mLogtf.d("onNotice:target=" + target + ",notice=" + notice);
//                }
//                if (mIRCCallback != null) {
//                    if (currentMode == null) {
//                        mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
//                    } else {
//                        if (mChannels.length > 1) {
//                            if (("#" + mChannels[0]).equals(channelId)) {
//                                mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
//                            }
//                            if (("#" + mChannels[1]).equals(channelId)) {
//                                mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onChannelInfo(String channel, int userCount, String topic) {
//                if (mIRCCallback != null) {
//                    mIRCCallback.onChannelInfo(channel, userCount, topic);
//                }
//            }
//
//            @Override
//            public void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
//                mLogtf.d("onTopic:channel=" + channel + ",topic=" + topic);
//                if (mIRCCallback != null) {
//                    //  如果不是专属老师
//                    if (mChannels.length <= 1) {
//                        mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
//                    } else {
//                        if (("#" + mChannels[0]).equals(channelId)) {
//                            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
//                        }
//                        if (("#" + mChannels[1]).equals(channelId)) {
//                            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onConnect(IRCConnection connection) {
//                mLogtf.d("onConnect:count=" + mConnectCount);
//                mConnectCount++;
//                String target = "" + mNickname;
//                if (mConnection.getName().equals(mNickname)) {
//                    target = "w" + mNickname;
//                }
//                mConnection.sendMessage(target, "T");
//                mLogtf.d("onConnect:name=" + mConnection.getName() + ",server=" + mConnection.getServer());
//                if (mIRCCallback != null) {
//                    mIRCCallback.onConnect(connection);
//                }
//                mHandler.postDelayed(mPingRunnable, mPingDelay);
//            }
//
//            @Override
//            public void onDisconnect(IRCConnection connection, boolean isQuitting) {
//                if (IRCMessage.this.mConnection != connection) {
//                    mLogtf.d("onDisconnect:old");
//                    return;
//                }
//                mDisconnectCount++;
//                mLogtf.d("onDisconnect:count=" + mDisconnectCount + ",isQuitting=" + isQuitting);
//                if (mIRCCallback != null) {
//                    mIRCCallback.onDisconnect(connection, isQuitting);
//                }
//                mHandler.removeCallbacks(mPingRunnable);
//                if (!isQuitting) {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            liveThreadPoolExecutor.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    IRCMessage.this.connect("onDisconnect");
//                                }
//                            });
//                        }
//                    }, 2000);
//                }
//            }
//
//            @Override
//            public void onUserList(String channel, User[] users) {
//                onUserList = true;
//                String s = "___bug  onUserList:channel=" + channel + ",users=" + users.length;
//                mLogtf.d(s);
//                if (mIRCCallback != null) {
//                    //  如果不是专属老师
//                    if (currentMode == null) {
//                        mIRCCallback.onUserList(channel, users);
//                    } else {
//                        if (LiveTopic.MODE_CLASS.equals(currentMode) && ("#" + mChannels[0]).equals(channel)) {
//                            StringBuilder sb = new StringBuilder();
//                            for (User user : users) {
//                                sb.append(user.getNick());
//                            }
//                            s = "___bug2  onUserList:channel=" + channel + ",users=" + users.length + "___" + sb.toString();
//                            //  mLogtf.d(s);
//                            mIRCCallback.onUserList(channel, users);
//                        }
//
//                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1 && ("#" + mChannels[1]).equals(channel)) {
//                            StringBuilder sb = new StringBuilder();
//                            for (User user : users) {
//                                sb.append(user.getNick());
//                            }
//                            s = "___bug3  onUserList:channel=" + channel + ",users=" + users.length + "___" + sb.toString();
//                            // mLogtf.d(s);
//                            mIRCCallback.onUserList(channel, users);
//                        }
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onJoin(String target, String sender, String login, String hostname) {
//                if (sender.startsWith("s_") || sender.startsWith("ws_")) {
//                    logger.i("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
//                } else {
//                    mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
//                }
//                if (mIRCCallback != null) {
//                    //  如果不是专属老师
//                    if (currentMode == null) {
//                        mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
//                        mIRCCallback.onJoin(target, sender, login, hostname);
//                    } else {
//                        if (LiveTopic.MODE_CLASS.equals(currentMode) && mChannels[0].equals(target)) {
//                            //mLogtf.d("___personal onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
//                            mIRCCallback.onJoin(target, sender, login, hostname);
//                        }
//                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1 && mChannels[1].equals(target)) {
//                            //mLogtf.d("___personal onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
//                            mIRCCallback.onJoin(target, sender, login, hostname);
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String
//                    channel) {
//                if (sourceNick.startsWith("s_") || sourceNick.startsWith("ws_")) {
//                    logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
//                            + sourceHostname + ",reason=" + reason);
//                } else {
//                    mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
//                            + sourceHostname + ",reason=" + reason);
//                }
//                if (mIRCCallback != null) {
//                    //  如果不是专属老师
//                    if (currentMode == null) {
//                        mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
//                    } else {
//                        if (LiveTopic.MODE_CLASS.equals(currentMode)) {
//                            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
//                     /*       logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
//                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
//                        }
//                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1) {
//                            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
//                          /*  logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
//                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
//                               String recipientNick, String reason) {
//                mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
//                        + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
//                if (mIRCCallback != null) {
//                    mIRCCallback.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
//                }
//            }
//
//            @Override
//            public void onUnknown(String line) {
//                if (line.contains("PONG")) {// :100000.irc PONG 100000.irc
//                    // :52-1453456527158
//                    int index = line.lastIndexOf(":");
//                    String pong = line.substring(index + 1);
//                    String[] split = pong.split("-");
//                    int count = Integer.parseInt(split[0]);
//                    long time = Long.parseLong(split[1]);
//                    if (count - mPingCout == -1) {
//                        mHandler.removeCallbacks(mTimeoutRunnable);
//                        mHandler.postDelayed(mPingRunnable, mPingDelay);
//                    }
//                }
//                if (mIRCCallback != null) {
//                    mIRCCallback.onUnknown(line);
//                }
//            }
//        });
//        boolean getserver = ircTalkConf.getserver(businessDataCallBack);
//        if (!getserver) {
//            ircTalkConf = null;
//            liveThreadPoolExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    connect("create");
//                }
//            });
//        }
    }

    private void onTopic(String channel, String topic, long date) {
        if (mIRCCallback != null) {
            //  如果不是专属老师
            if (mChannels.length <= 1) {
                mIRCCallback.onTopic(channel, topic, "", date, false, channel);
            } else {
                if (("#" + mChannels[0]).equals(channel)) {
                    mIRCCallback.onTopic(channel, topic, "", date, false, channel);
                }
                if (("#" + mChannels[1]).equals(channel)) {
                    mIRCCallback.onTopic(channel, topic, "", date, false, channel);
                }
            }
        }
    }

    private synchronized void connect(String method) {
        mHandler.removeCallbacks(mPingRunnable);
        onUserList = false;
        if (mIsDestory) {
            return;
        }
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            connectError = true;
            mLogtf.d("connect(NO_NETWORK):method=" + method);
            return;
        }

        IRCConnection old = mConnection;
        mConnection = new IRCConnection(privMsg);
        mConnection.setCallback(old.getCallback());
        old.setCallback(null);
        old.disconnect();

        // connection.setLogin2(login);
        mConnection.setLogin2(mNickname);
        mConnection.setNickname(mNickname);
        if (mNewTalkConf.isEmpty()) {
            mLogtf.d("connect:mNewTalkConf.isEmpty:ircTalkConf=" + (ircTalkConf == null) + ",method=" + method);
            if (ircTalkConf != null) {
                ircTalkConf.getserver(businessDataCallBack);
            }
            return;
        }
        int index = mSelectTalk++ % mNewTalkConf.size();
        NewTalkConfEntity talkConfEntity = mNewTalkConf.get(index);
        //是不是连接错误
        boolean connectError = true;
        try {
            String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
            connectError = false;
            //mConnection.joinChannel("#" + mChannel);
            mLogtf.d("connect:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName()
                    + ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
        } catch (NickAlreadyInUseException e) {
            try {
                mConnection.setLogin2("w" + mNickname);
                mConnection.setNickname("w" + mNickname);
                String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
                connectError = false;
                //mConnection.joinChannel("#" + mChannel);
                mLogtf.d("connect2:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName() +
                        ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
            } catch (NickAlreadyInUseException e1) {
                mLogtf.e("connect2-1", e1);
            } catch (Exception e1) {
                mLogtf.e("connecte2:method=" + method + ",name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
            }
        } catch (Exception e) {
            mLogtf.e("connecte:method=" + method + ",name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
        }
        if (connectError || !mConnection.isConnected()) {
            if (netWorkType != NetWorkHelper.NO_NETWORK && ircTalkConf != null) {
                mNewTalkConf.remove(index);
            }
            //如果不为null,上传日志（体验课时不为空）
            final NewTalkConfEntity finalNewTalkConfEntity = talkConfEntity;
            final String finalMethod = method;
            if (connectService != null) {
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {

                        connectService.connectChatServiceError(
                                getHost(finalNewTalkConfEntity.getHost()),
                                finalNewTalkConfEntity.getPort(),
                                finalMethod + "Connect Failure",
                                IpAddressUtil.USER_IP);
                    }
                });

            }
            mLogtf.d("connect:method=" + method + ",connectError=" + connectError + ",netWorkType=" + netWorkType + ",conf=" + (ircTalkConf == null));
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            connect("connect2");
                        }
                    });
                }
            }, 2000);

        }
    }

    /**
     * 根据url获取服务器的ip地址
     *
     * @param ip
     * @return
     */
    private String getHost(String ip) {
        try {
            int len = ip.length();
            int pos = ip.indexOf("//");
            int i = pos + 2;
            while (i < len) {
                if (ip.charAt(i) == '/') {
                    break;
                }
                i++;
            }
//            return ip.substring(pos <= 0 ? 0 : pos + 2, i);
            String url = ip.substring(pos <= 0 ? 0 : pos + 2, i);
            return InetAddress.getByName(url).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到连接名字
     *
     * @return
     */
    public String getConnectNickname() {
//        if (mConnection.isConnected()) {
//            return mConnection.getName();
//        }
        return mNickname;
    }

    /**
     * 得到短名字
     *
     * @return
     */
    public String getNickname() {
        return mNickname;
    }

    /**
     * 直播服务器调度返回
     */
    AbstractBusinessDataCallBack businessDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            mNewTalkConf = (List<NewTalkConfEntity>) objData[0];
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connect("onDataSucess");
                }
            });
        }
    };

    public void setIrcTalkConf(IRCTalkConf ircTalkConf) {
        this.ircTalkConf = ircTalkConf;
    }

    /**
     * 发通知
     *
     * @param notice
     */
    public void sendNotice(String notice) {
        // 如果是专属老师
        if (roomid == null) {
            roomid = new ArrayList<>();
        }
        roomid.clear();
        if (mChannels.length > 1 && currentMode != null) {
            if (LiveTopic.MODE_TRANING.equals(currentMode)) {
//                mChatClient.getRoomManager().sendRoomMessage()
                roomid.add("#" + mChannels[1]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, notice, 1);
//                mConnection.sendNotice("#" + mChannels[1], notice);
            }
            if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                roomid.add("#" + mChannels[0]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, notice, 1);
//                mConnection.sendNotice("#" + mChannels[0], notice);
            }
        } else {
            roomid.add("#" + mChannels[0]);
            mChatClient.getRoomManager().sendRoomMessage(roomid, notice, 1);
//            mConnection.sendNotice("#" + mChannels[0], notice);
        }
    }


    /**
     * 发通知
     *
     * @param target 目标
     * @param notice
     */
    public void sendNotice(String target, String notice) {
        List<PMDefs.PsIdEntity> entityList= new ArrayList<>();
        PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(target,"");
        entityList.add(psIdEntity);
        mChatClient.getPeerManager().sendPeerMessage(entityList,notice,1);
//        mConnection.sendNotice(target, notice);
    }

    /**
     * 发消息
     *
     * @param target  目标
     * @param message 信息
     */
    public void sendMessage(String target, String message) {
        List<PMDefs.PsIdEntity> entityList= new ArrayList<>();
        PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(target,"");
        entityList.add(psIdEntity);
        mChatClient.getPeerManager().sendPeerMessage(entityList,message,1);
//        mConnection.sendMessage(target, message);
    }

    /**
     * 发消息,向聊天室发消息
     *
     * @param message 信息
     */
    public void sendMessage(String message) {
        if (roomid == null) {
            roomid = new ArrayList<>();
        }
        roomid.clear();

        if (mChannels.length > 1 && currentMode != null) {
            if (LiveTopic.MODE_TRANING.equals(currentMode)) {
//                mConnection.sendMessage("#" + mChannels[1], message);
                roomid.add("#" + mChannels[1]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, message, 99);
                //Loger.d("____bug 22  channel: "+mChannels[1] +"  message:  "+message);
            }

            if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                //Loger.d("____bug 23  channel: "+mChannels[0] +"  message:  "+message);
//                mConnection.sendMessage("#" + mChannels[0], message);
                roomid.add("#" + mChannels[0]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, message, 99);
            }
        } else {
            // Loger.d("____bug 24  channel: "+mChannels[0] +"  message:  "+message);
//            mConnection.sendMessage("#" + mChannels[0], message);
            roomid.add("#" + mChannels[0]);
            mChatClient.getRoomManager().sendRoomMessage(roomid, message, 99);
        }
//        mChatClient.getRoomManager().sendRoomMessage(roomid, message, 1);
    }

    /**
     * 播放器销毁
     */
    public void destory() {
        if (mChatClient != null) {
            logger.i("ircsdk ondestory");
            mChatClient.getRoomManager().leaveChatRooms(roomid);
            mChatClient.logout("relogin");
            mChatClient.unInit();
            mChatClient.getPeerManager().removeListener(mPeerListener);
            mChatClient.getRoomManager().removeListener(mRoomListener);
            mChatClient.removeListener(mClientListener);

        }

        mIsDestory = true;
        mHandler.removeCallbacks(mPingRunnable);
        mHandler.removeCallbacks(mTimeoutRunnable);
        new Thread() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        }.start();
        if (ircTalkConf != null) {
            ircTalkConf.destory();
        }
    }

    public void setCallback(IRCCallback ircCallback) {
        this.mIRCCallback = ircCallback;
    }

    /**
     * ping的次数
     */
    private int mPingCout = 0;
    /**
     * 当前ping的时间
     */
    private long mPintBefore = 0;
    /**
     * ping的时间间隔
     */
    private final long mPingDelay = 5000;
    /**
     * pong的的时间间隔
     */
    private final long mPongDelay = 6000;
    /**
     * 循环ping
     */
    private Runnable mPingRunnable = new Runnable() {

        @Override
        public void run() {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    mPintBefore = System.currentTimeMillis();
                    mConnection.sendRawLine("ping :" + (mPingCout++) + "-" + mPintBefore);
                    mHandler.postDelayed(mTimeoutRunnable, mPongDelay);
                }
            });
        }
    };

    /**
     * ping超时,重连
     */
    private Runnable mTimeoutRunnable = new Runnable() {

        @Override
        public void run() {
            if (mIsDestory) {
                return;
            }
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            IRCConnection old = mConnection;
                            mConnection = new IRCConnection(privMsg);
                            mConnection.setCallback(old.getCallback());
                            old.setCallback(null);
                            old.disconnect();
                            if (mIRCCallback != null) {
                                mIRCCallback.onDisconnect(old, false);
                            }
                            connect("mTimeoutRunnable");
                        }
                    });
                }
            }, 2000);
        }
    };

    /**
     * 连接服务器失败,体验课使用
     */
    public interface ConnectService {
        /**
         * wiki地址 https://wiki.xesv5.com/pages/viewpage.action?pageId=13842928
         *
         * @param serverIp   聊天服务器ip
         * @param serverPort 聊天服务器端口
         * @param errMsg     链接聊天服务器失败信息
         * @param ip         自己的ip
         */
        void connectChatServiceError(
                String serverIp,
                String serverPort,
                String errMsg,
                String ip);
    }

    private ConnectService connectService;

    public void setConnectService(ConnectService connectService) {
        this.connectService = connectService;
    }

    public void modeChange(String mode) {
        // 专属切主讲时，断开专属聊天室
        //  Loger.d("___bug  mode change:  "+mode);
        if (currentMode != null && !currentMode.equals(mode)) {
            if (mChannels != null && mChannels.length > 1) {
                mConnection.partChannel("#" + mChannels[1]);
                //  Loger.d("___bug33  partchannel:  "+mode);
            }
        }
        //  Loger.d("___modechange:  "+mode);
        if (mChannels.length > 1) {
            currentMode = mode;
        }
    }
}
