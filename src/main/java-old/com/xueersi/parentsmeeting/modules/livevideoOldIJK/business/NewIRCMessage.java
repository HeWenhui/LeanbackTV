package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.content.Context;

import com.tal100.chatsdk.ChatClient;
import com.tal100.chatsdk.IChatClientListener;
import com.tal100.chatsdk.IPeerChatListener;
import com.tal100.chatsdk.IRoomChatListener;
import com.tal100.chatsdk.PMDefs;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tal100.chatsdk.PMDefs.MessagePriority.MSG_PRIORITY_NOTICE;

/**
 * IRC消息。连接IRC SDK和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class NewIRCMessage implements IIRCMessage {
    private String TAG = "IRCMessage";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private int mConnectCount = 0, mDisconnectCount = 0;
    private IRCCallback mIRCCallback;
    private String[] mChannels;
    private String mNickname;

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

    Context context = ContextManager.getContext();
    ChatClient mChatClient;
    Context mContext;
    File workSpaceDir = new File(context.getCacheDir(), "irc/workspace");
    private String currentMode;
    LiveGetInfo mLiveInfo;
    private List<String> roomid;
    private PMDefs.LiveInfo liveInfo;
    private boolean isConnected;
    private boolean isFirstLogin = true;
    private LiveAndBackDebug liveAndBackDebug;
    private String eventid = "IRCMessage";

    public NewIRCMessage(Context context, int netWorkType, String login, String nickname, LiveGetInfo liveInfo, LiveAndBackDebug liveAndBackDebug, String... channel) {
        this.netWorkType = netWorkType;
        this.mChannels = channel;
        this.mNickname = nickname;
        this.mContext = context;
        this.mLiveInfo = liveInfo;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        mLogtf.d("IRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
    }

    /**
     * 是不是连接中
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 是不是获得过用户列表
     *
     * @return
     */
    @Override
    public boolean onUserList() {
        return onUserList;
    }

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
            if (PMDefs.ResultCode.Result_Success == loginResp.code && isFirstLogin) {
                isFirstLogin = false;
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
            if (PMDefs.NetStatus.PMNetStatus_Connecting == netStatusResp.netStatus) {
                if (mIRCCallback != null) {
                    mIRCCallback.onStartConnect();
                    mIRCCallback.onRegister();
                }
            } else if (PMDefs.NetStatus.PMNetStatus_Unkown == netStatusResp.netStatus ||
                    PMDefs.NetStatus.PMNetStatus_Unavailable == netStatusResp.netStatus ||
                    PMDefs.NetStatus.PMNetStatus_ServerFailed == netStatusResp.netStatus ||
                    PMDefs.NetStatus.PMNetStatus_DisConnected == netStatusResp.netStatus) {
                mDisconnectCount++;
                mLogtf.d("onDisconnect:count=" + mDisconnectCount + ",isQuitting=" + false + ",netstatus=" + netStatusResp.netStatus);
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
            String target = "";
            String message = peerChatMessage.content;
            int priority = peerChatMessage.msgPriority;
            String channel = "";
            String login = "";
            String hostname = "";
            JSONObject msgJosn = null;
            try {
                msgJosn = new JSONObject(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);

            if (PMDefs.MessagePriority.MSG_PRIORITY_PRI == priority) {
                target = "PRIVMSG";
                //旁听
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
                                    PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(sender, peerChatMessage.fromUserId.psid);
                                    List<PMDefs.PsIdEntity> psIdEntityList = new ArrayList<>();
                                    psIdEntityList.add(psIdEntity);
                                    mChatClient.getPeerManager().sendPeerMessage(psIdEntityList, jsonObject.toString(), PMDefs.MessagePriority.MSG_PRIORITY_PRI);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                } else {//判断是否是踢人消息
                    if (mNickname.startsWith("ws")) {
                        if (mNickname.endsWith(sender)) {
                            isSelf = true;
                        }
                    } else if (mNickname.startsWith("s")) {
                        if (sender.endsWith(mNickname)) {
                            isSelf = true;
                        }
                    }
                }
                if (mIRCCallback != null) {
                    //专属老师
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
            } else if (MSG_PRIORITY_NOTICE == priority) {//一对一notic消息
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
                    mLogtf.d("onNotice:target=" + target + ",notice=" + message);
                }
                if (mIRCCallback != null) {
                    mIRCCallback.onNotice(sender, "", "", target, message, channel);
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
                //进入聊天室发送踢人消息
                if (mIRCCallback != null) {
//                    mIRCCallback.onRegister();
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
                    mChatClient.getPeerManager().sendPeerMessage(userList, "T", PMDefs.MessagePriority.MSG_PRIORITY_PRI);
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
            String sender = joinRoomNotice.userInfo.nickname;
            String target = joinRoomNotice.roomId;
            String login = "";
            String hostname = "";
            if (joinRoomNotice.userInfo.nickname.startsWith("s_") || joinRoomNotice.userInfo.nickname.startsWith("ws_")) {
                logger.i("onJoin:target=" + target + ",sender=" + sender + ",login=" + "" + ",hostname=" + "");
            } else {
                mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + "" + ",hostname=" + "");
            }
            if (mIRCCallback != null) {
                //  如果不是专属老师
                if (currentMode == null) {
                    mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + "" + ",hostname=" + "");
                    mIRCCallback.onJoin(target, sender, login, hostname);
                } else {
                    if (LiveTopic.MODE_CLASS.equals(currentMode) && mChannels[0].equals(joinRoomNotice.roomId)) {
                        mIRCCallback.onJoin(target, sender, login, hostname);
                    }
                    if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length > 1 && mChannels[1].equals(joinRoomNotice.roomId)) {
                        mIRCCallback.onJoin(target, sender, login, hostname);
                    }
                }

            }
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
                String topic = "";
                long date = 0;
                if (roomMetaData.content.containsKey("topic")) {
                    topic = roomMetaData.content.get("topic");
                }
                if (mIRCCallback != null) {
                    if (roomMetaData.content.containsKey("number")) {
                        mIRCCallback.onChannelInfo(channel, Integer.parseInt(roomMetaData.content.get("number")), JsonUtil.toJson(roomMetaData.content.get("topic")));
                    }
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
                    } else if (MSG_PRIORITY_NOTICE == roomChatMessage.msgPriority) {
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
                        if (msgJosn.has("name")) {
                            name = msgJosn.getString("name");
                        }
                        if (msgJosn.has("msg")) {
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
            logger.i("ircsdk onSendRoomMessageResp" + " info: " + sendRoomMessageResp.info);
            //495为BLOCK回调
            if (495 == sendRoomMessageResp.code) {
                mIRCCallback.onUnknown("BLOCK");
            }
        }
    };

    @Override
    public void onNetWorkChange(int netWorkType) {

    }

    @Override
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
        //irc sdk初始化  code: 0 成功 ，1 参数错误 ， 19 已初始化
        int initcode = mChatClient.init(mContext.getApplicationContext(), myUserInfoEntity.getPsAppId(), myUserInfoEntity.getPsAppClientKey(), workSpaceDir.getAbsolutePath());
        logger.i("irc sdk initcode: " + initcode);
        if (PMDefs.ResultCode.Result_Success != initcode) {
            if (liveAndBackDebug != null) {
                StableLogHashMap logHashMap = new StableLogHashMap("IRCMessage");
                logHashMap.put("initcode", "" + initcode);
                logHashMap.put("nickname", mNickname);
                logHashMap.put("PsAppId", myUserInfoEntity.getPsAppId());
                logHashMap.put("PsAppClientKey", myUserInfoEntity.getPsAppClientKey());
                logHashMap.put("workspace", workSpaceDir.getAbsolutePath());
                logHashMap.put("time", "" + System.currentTimeMillis());
                logHashMap.put("userid", UserBll.getInstance().getMyUserInfoEntity().getStuId());
                logHashMap.put("where", "NewIRCMessage");
                logHashMap.put("liveId", mLiveInfo.getId());
                liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
            }
            if (!workSpaceDir.exists()) {
                workSpaceDir.mkdirs();
            }
            logger.i("psAppId:" + myUserInfoEntity.getPsAppId() + " PsAppClientKey:" + myUserInfoEntity.getPsAppClientKey() + " workspace:" + workSpaceDir.getAbsolutePath());
            initcode = mChatClient.init(mContext.getApplicationContext(), myUserInfoEntity.getPsAppId(), myUserInfoEntity.getPsAppClientKey(), workSpaceDir.getAbsolutePath());
            logger.i("irc sdk initagain initcode: " + initcode);
        }
        //设置直播信息
        liveInfo = new PMDefs.LiveInfo();
        liveInfo.nickname = mNickname;
        liveInfo.realname = myUserInfoEntity.getRealName();
        liveInfo.liveId = mLiveInfo.getId();
        if (mLiveInfo.getStuName() != null) {
            liveInfo.username = mLiveInfo.getStuName();
        } else {
            liveInfo.username = "";
        }
        if (mLiveInfo.getStudentLiveInfo() != null && mLiveInfo.getStudentLiveInfo().getClassId() != null) {
            liveInfo.classId = mLiveInfo.getStudentLiveInfo().getClassId();
        } else {
            liveInfo.classId = "";
        }
        liveInfo.businessId = "1";
        if (myUserInfoEntity.getAreaCode() != null) {
            liveInfo.location = myUserInfoEntity.getAreaCode();
        } else {
            liveInfo.username = mNickname;
        }
        mChatClient.setLiveInfo(liveInfo);
        //登陆 code: 0 成功， 1 参数错误，11 未初始化，17 已登录，18 正在登陆
        int logincode = mChatClient.login(myUserInfoEntity.getPsimId(), myUserInfoEntity.getPsimPwd());
        logger.i("irc sdk logincode:" + logincode);
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

    /**
     * 得到连接名字
     *
     * @return
     */
    @Override
    public String getConnectNickname() {
        return mNickname;
    }

    /**
     * 得到短名字
     *
     * @return
     */
    @Override
    public String getNickname() {
        return mNickname;
    }

    @Override
    public void setIrcTalkConf(IRCTalkConf ircTalkConf) {
        this.ircTalkConf = ircTalkConf;
    }

    /**
     * 发通知
     *
     * @param notice
     */
    @Override
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
                mChatClient.getRoomManager().sendRoomMessage(roomid, notice, MSG_PRIORITY_NOTICE);
            }
            if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                roomid.add("#" + mChannels[0]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, notice, MSG_PRIORITY_NOTICE);
            }
        } else {
            roomid.add("#" + mChannels[0]);
            mChatClient.getRoomManager().sendRoomMessage(roomid, notice, MSG_PRIORITY_NOTICE);
        }
    }


    /**
     * 发通知
     *
     * @param target 目标
     * @param notice
     */
    @Override
    public void sendNotice(String target, String notice) {
        List<PMDefs.PsIdEntity> entityList = new ArrayList<>();
        PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(target, "");
        entityList.add(psIdEntity);
        mChatClient.getPeerManager().sendPeerMessage(entityList, notice, MSG_PRIORITY_NOTICE);
    }

    /**
     * 发消息
     *
     * @param target  目标
     * @param message 信息
     */
    @Override
    public void sendMessage(String target, String message) {
        List<PMDefs.PsIdEntity> entityList = new ArrayList<>();
        PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(target, "");
        entityList.add(psIdEntity);
        mChatClient.getPeerManager().sendPeerMessage(entityList, message, PMDefs.MessagePriority.MSG_PRIORITY_PRI);
    }

    /**
     * 发消息,向聊天室发消息
     *
     * @param message 信息
     */
    @Override
    public void sendMessage(String message) {
        if (roomid == null) {
            roomid = new ArrayList<>();
        }
        roomid.clear();

        if (mChannels.length > 1 && currentMode != null) {
            if (LiveTopic.MODE_TRANING.equals(currentMode)) {
//                mConnection.sendMessage("#" + mChannels[1], message);
                roomid.add("#" + mChannels[1]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, message, PMDefs.MessagePriority.MSG_PRIORITY_PRI);
                //Loger.d("____bug 22  channel: "+mChannels[1] +"  message:  "+message);
            }

            if (LiveTopic.MODE_CLASS.equals(currentMode)) {
                //Loger.d("____bug 23  channel: "+mChannels[0] +"  message:  "+message);
//                mConnection.sendMessage("#" + mChannels[0], message);
                roomid.add("#" + mChannels[0]);
                mChatClient.getRoomManager().sendRoomMessage(roomid, message, PMDefs.MessagePriority.MSG_PRIORITY_PRI);
            }
        } else {
            // Loger.d("____bug 24  channel: "+mChannels[0] +"  message:  "+message);
//            mConnection.sendMessage("#" + mChannels[0], message);
            roomid.add("#" + mChannels[0]);
            mChatClient.getRoomManager().sendRoomMessage(roomid, message, PMDefs.MessagePriority.MSG_PRIORITY_PRI);
        }
//        mChatClient.getRoomManager().sendRoomMessage(roomid, message, 1);
    }

    /**
     * 播放器销毁
     */
    @Override
    public void destory() {
        if (mChatClient != null) {
            logger.i("ircsdk onDestroy");
            mChatClient.logout("relogin");
            if (roomid != null && !roomid.isEmpty()) {
                mChatClient.getRoomManager().leaveChatRooms(roomid);
            }
            mChatClient.unInit();
            mChatClient.getPeerManager().removeListener(mPeerListener);
            mChatClient.getRoomManager().removeListener(mRoomListener);
            mChatClient.removeListener(mClientListener);
        }
        isConnected = false;
        mIsDestory = true;
        if (ircTalkConf != null) {
            ircTalkConf.destory();
        }
    }

    @Override
    public void setCallback(IRCCallback ircCallback) {
        this.mIRCCallback = ircCallback;
    }


    @Override
    public void setConnectService(IConnectService connectService) {
    }

    //模式切换
    @Override
    public void modeChange(String mode) {
        if (mChannels.length > 1) {
            currentMode = mode;
        }
    }
}
