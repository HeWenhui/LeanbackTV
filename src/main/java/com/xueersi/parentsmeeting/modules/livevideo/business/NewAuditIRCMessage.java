package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tal100.chatsdk.ChatClient;
import com.tal100.chatsdk.IChatClientListener;
import com.tal100.chatsdk.IPeerChatListener;
import com.tal100.chatsdk.IRoomChatListener;
import com.tal100.chatsdk.PMDefs;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEnvironment;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.tal100.chatsdk.PMDefs.MessagePriority.MSG_PRIORITY_NOTICE;
import static com.tal100.chatsdk.PMDefs.MessagePriority.MSG_PRIORITY_PRI;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class NewAuditIRCMessage implements IAuditIRCMessage {
    private String TAG = "NewAuditIRCMessage";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    String eventid = LiveVideoConfig.LIVE_LISTEN;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private AuditIRCCallback mIRCCallback;
    private String mChannel;
    private String mNickname;
    private String childName;
    private UUID mSid = UUID.randomUUID();
    private LogToFile mLogtf;
    /**
     * 播放器是不是销毁
     */
    private boolean mIsDestory = false;
    private Handler mHandler = LiveMainHandler.getMainHandler();
    /**
     * 调度是不是在无网络下失败
     */
    private boolean connectError = false;
    /**
     * 学生推流失败
     */
    private boolean stuPushSuccess = false;
    String stuPushStatus = "";
    LiveAndBackDebug liveAndBackDebug;
    long enterTime;

    boolean onUserList = false;
    ChatClient mChatClient;
    Context mContext;
    File workSpaceDir;
    private String liveId;
    private String classId;
    private List<String> roomid;
    private PMDefs.LiveInfo liveInfo;
    private boolean isConnected;
    private boolean isFirstLogin = true;
    //消息序号
    private long[] preMsgId = new long[]{0};
    private HashMap<Long, String> msgMap = new HashMap<>();
    private String businessId;

    public NewAuditIRCMessage(LiveEnvironment liveEnvironment, String nickname, String liveId, String classId,String businessId, String channel) {
        this.mChannel = channel;
        this.mNickname = nickname;
        this.liveId = liveId;
        this.classId = classId;
        this.businessId = businessId;
        mContext = liveEnvironment.getActivity();
        workSpaceDir = new File(mContext.getCacheDir(), "irc/workspace");
        mLogtf = liveEnvironment.createLogToFile(TAG);
        mLogtf.d("AuditIRCMessage:channel=" + channel + ",nickname=" + nickname);
        enterTime = System.currentTimeMillis();
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
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
            if (PMDefs.ResultCode.Result_Success == loginResp.code && isFirstLogin) {
                isFirstLogin = false;
                if (roomid == null) {
                    roomid = new ArrayList<>();
                }
                roomid.add("#" + mChannel);
                mChatClient.getRoomManager().joinChatRooms(roomid);
            }
//            else if (PMDefs.ResultCode.Result_ExistNickname == loginResp.code) {
//                mChatClient.logout("Nickname is already in use");
//                liveInfo.nickname = "pt_" + mNickname;
//                mChatClient.setLiveInfo(liveInfo);
//                int logincode = mChatClient.login(LiveAppUserInfo.getInstance().getPsimId(), LiveAppUserInfo.getInstance().getPsimPwd());
//            }
            StableLogHashMap logHashMap = defaultlog("login");
            logHashMap.put("loginCode", "" + loginResp.code);
            logHashMap.put("loginInfo", "" + loginResp.info);
            logHashMap.put("connectCount", "" + mConnectCount);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
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
            if (mIRCCallback != null) {
                mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                        + sourceHostname + ",reason=" + reason);
                if (mIRCCallback != null) {
                    mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                }
            }
            StableLogHashMap logHashMap = defaultlog("logout");
            logHashMap.put("logoutCode", "" + logoutNotice.code);
            logHashMap.put("logoutInfo", "" + logoutNotice.info);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
        }

        @Override
        public void onKickoutNotice(PMDefs.KickoutNotice kickoutNotice) {
            if (kickoutNotice.code == PMDefs.ResultCode.Result_KickoutRepeat || kickoutNotice.code == PMDefs.ResultCode.Result_KickoutRequest){
                mIRCCallback.onPrivateMessage(true, mNickname, "", "", "PRIVMSG", "T");
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
                StableLogHashMap logHashMap = defaultlog("logout");
                logHashMap.put("netStatus", "" + netStatusResp.netStatus);
                liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
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
            final String sender = peerChatMessage.fromUserId.nickname;
            String target = "PRIVMSG";
            String message = peerChatMessage.content;
            logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
            String login = "";
            String hostname = "";
            logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
            if (sender.startsWith("ws") || sender.startsWith("s")) {
                if (sender.endsWith(mNickname)) {
                    if (childName == null) {
                        mIRCCallback.onStudentLeave(false, stuPushStatus);
                    }
                    childName = sender;
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(message);
                        int type = jsonObject.getInt("type");
                        switch (type) {
                            case XESCODE.STUDENT_REPLAY: {
//                                    mHandler.removeCallbacks(startVideoRun);
                                mHandler.removeCallbacks(mStudyTimeoutRunnable);
                                String status = jsonObject.optString("status");
                                stuPushStatus = status;
                                if (!"success".equals(status)) {
                                    stuPushSuccess = false;
                                    if (mIRCCallback != null) {
                                        String msg;
                                        if ("fluentMode".equals(status)) {
                                            msg = "流畅模式";
                                            mHandler.removeCallbacks(startVideoRun);
                                        } else {
                                            if ("publishFail".equals(status)) {
                                                msg = "学生网络异常";
                                            } else if ("forbidden".equals(status)) {
                                                msg = "摄像头已禁用";
                                            } else if ("disconnect".equals(status)) {
                                                msg = "未连接摄像头";
                                            } else if ("unsupported".equals(status)) {
                                                msg = "设备暂不支持";
                                            } else {
                                                msg = "未知异常";
                                            }
                                            startVideo();
                                            mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
                                        }
                                        mIRCCallback.onStudentError(status, msg);
                                    }
                                    //旁听日志
                                    String nonce = jsonObject.optString("nonce");
                                    StableLogHashMap logHashMap = new StableLogHashMap("studentError");
                                    logHashMap.put("nickname", sender);
                                    logHashMap.put("status", status);
                                    logHashMap.addSno("5").addNonce(nonce).addExY().addStable("1");
                                    liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
                                    return;
                                } else {
                                    stuPushSuccess = true;
                                }
                                mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
                            }
                            break;
                            case XESCODE.STUDENT_STU_HEART: {
                                mHandler.removeCallbacks(mStudyTimeoutRunnable);
                                if (mIsDestory) {
                                    return;
                                }
                                mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject heartJson = new JSONObject();
                                        try {
                                            heartJson.put("type", "" + XESCODE.STUDENT_MY_HEART);
                                            if (!mIsDestory) {
                                                sendMessage(sender, heartJson.toString());
                                            }
//                                                heartJson.put("msg", "PONG");
//                                                if (!mIsDestory) {
//                                                    mConnection.sendMessage(sender, heartJson.toString());
//                                                }
                                        } catch (Exception e) {
                                            logger.e("STUDENT_STU_HEART", e);
                                        }
                                    }
                                }, 1500);
                            }
                            return;
                            default: {
                                logger.d("onPrivateMessage:type=" + type);
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mIRCCallback != null) {
                        mIRCCallback.onStudentPrivateMessage(sender, login, hostname, target, message);
                    }
                    return;
                }
            }
            if (sender.startsWith("p") || sender.startsWith("pt")) {
                if (sender.endsWith(mNickname)) {
                    isSelf = true;
                }
            }
            if (mIRCCallback != null) {
                mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
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
            PMDefs.PsIdEntity toUserInfo = sendPeerMessageResp.toUserInfo;
            if (toUserInfo != null) {
                logger.i("ircsdk onSendPeerMessageResponse:toUserInfo=" + toUserInfo.nickname);
            }

            String msg = "";

            //本地消息号
            long preId = sendPeerMessageResp.preMsgId;
            //服务器返回消息号
            long msgId = sendPeerMessageResp.msgId;
            if (msgMap.containsKey(preId)) {
                msg = msgMap.get(preId);
                msgMap.remove(preId);
            }
            StableLogHashMap logHashMap = defaultlog("IRCMessage");
            logHashMap.put("type", "onSendPeerMessageResponse");
            logHashMap.put("code", "" + sendPeerMessageResp.code);
            logHashMap.put("sendPeerMessageRespInfo","" + sendPeerMessageResp.info);
            logHashMap.put("toNickName","" + sendPeerMessageResp.toUserInfo.nickname);
            logHashMap.put("toPsId","" + sendPeerMessageResp.toUserInfo.psid);
            logHashMap.put("preMsgId","" + preId);
            logHashMap.put("msgId","" + msgId);
            logHashMap.put("msg", "" + msg);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap);
        }
    };

    private IRoomChatListener mRoomListener = new IRoomChatListener() {
        private List<PMDefs.PsIdEntity> userLists;

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
                userLists = new ArrayList<>();
                if (mIRCCallback != null) {
                    mLogtf.d("onRegister");
                    if (mIsDestory) {
                        return;
                    }
                    mIRCCallback.onRegister();
                    mHandler.post(startVideoRun);
                    mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
                    mLogtf.d("onConnect:count=" + mConnectCount + ",mIsDestory=" + mIsDestory);

                    mConnectCount++;
                    mLogtf.d("onConnect:name=" + joinRoomResp.userInfo.nickname + ",target=" + joinRoomResp.userInfo.nickname + ",server=" + "");
                    mIRCCallback.onConnect(null);
                }
            }
            StableLogHashMap logHashMap = defaultlog("joinRoom");
            logHashMap.put("joinRoomCode", "" + joinRoomResp.code);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());

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
            String sender = joinRoomNotice.userInfo.nickname;
            String target = joinRoomNotice.roomId;
            String login = "";
            String hostname = "";
            mConnectCount++;
            if (sender.startsWith("s_")) {
                logger.i("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
            } else {
                mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
            }
            if (mIRCCallback != null) {
                mIRCCallback.onJoin(target, sender, login, hostname);
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
            logger.i("ircsdk room Meta data : " + roomMetaData.content);
            if (PMDefs.ResultCode.Result_RoomData == roomMetaData.code) {
                String channel = roomMetaData.roomId;
                String topic = "";
                long date = 0;
                if (roomMetaData.content.containsKey("topic")) {
                    topic = roomMetaData.content.get("topic");
                }
                if (mIRCCallback != null) {
                    if (roomMetaData.content.containsKey("number")) {
                        mIRCCallback.onChannelInfo(channel, Integer.parseInt(roomMetaData.content.get("number")), topic);
                    }
                    mLogtf.d("onTopic:channel=" + channel + ",topic=" + topic);
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
            if (PMDefs.ResultCode.Result_RoomUserList == roomUserList.code){
                if (roomUserList.userList != null && !roomUserList.userList.isEmpty()) {
                    if (userLists == null){
                        userLists = new ArrayList<>();
                    }
                    userLists.addAll(roomUserList.userList);
                }
            }else if (PMDefs.ResultCode.Result_RoomUserListEnd == roomUserList.code) {
                onUserList = true;
                String s = "___bug  onUserList:channel=" + roomUserList.roomId + ",users=" + roomUserList.userList.size();
                if (roomUserList.userList != null && !roomUserList.userList.isEmpty()) {
                    userLists.addAll(roomUserList.userList);
                }
                User[] users = new User[userLists.size()];
                PMDefs.PsIdEntity userEntity;
                for (int i = 0; i < userLists.size(); i++) {
                    userEntity = userLists.get(i);
                    users[i] = new User(userEntity.psid, userEntity.nickname);
                }
                mLogtf.d(s);
                if (mIRCCallback != null) {
                    mIRCCallback.onUserList(roomUserList.roomId, users);
                }
                userLists.clear();
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
            mLogtf.d("onTopic:channel=" + channel + ",topic=" + topic);
            StableLogHashMap logHashMap = defaultlog("roomTopic");
            logHashMap.put("roomCode", "" + roomTopic.code);
            logHashMap.put("roomTopic", "" + roomTopic.topic);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
//            if (PMDefs.ResultCode.Result_RoomTopicEnd == roomTopic.code){
//                return;
//            }
            if (mIRCCallback != null) {
                mIRCCallback.onTopic(channel, topic, "", date, false, channel);
            }
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
                        if (mIRCCallback != null) {
                            mIRCCallback.onTopic(channel, target, "", date, false, channel);
                        }
                    } else if (PMDefs.MessagePriority.MSG_PRIORITY_NOTICE == roomChatMessage.msgPriority) {
                        target = "NOTICE";
                        mLogtf.d("onNotice:target=" + target + ",notice=" + text);
                        if (mIRCCallback != null) {
                            mIRCCallback.onNotice(sender, "", "", target, text, channel);
                        }
                    } else if (MSG_PRIORITY_PRI == roomChatMessage.msgPriority) {
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
                        mLogtf.d("onMessage:sender=" + sender + ":" + text);
                        if (mIRCCallback != null) {
                            mIRCCallback.onMessage(target, name, login, hostname, msg);
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
            String msg = "";
            //本地消息号
            long preId = sendRoomMessageResp.preMsgId;
            //服务器返回消息号
            long msgId = sendRoomMessageResp.msgId;
            if (msgMap.containsKey(preId)) {
                msg = msgMap.get(preId);
                msgMap.remove(preId);
            }
            logger.i("ircsdk onSendRoomMessageResp" + " info: " + sendRoomMessageResp.info + " " +
                    " preMsgId:" + preId + " msgId:" + msgId+ " msg: " + msg + " toRoomId:" + sendRoomMessageResp.toRoomId +
                    " fromUserInfo nickname:" + sendRoomMessageResp.fromUserInfo.nickname);
            StableLogHashMap logHashMap = defaultlog("IRCMessage");
            logHashMap.put("type", "onSendRoomMessageResp");
            logHashMap.put("code", "" + sendRoomMessageResp.code);
            logHashMap.put("roomMessageRespInfo", sendRoomMessageResp.info);
            logHashMap.put("toRoomId","" + sendRoomMessageResp.toRoomId);
            logHashMap.put("preMsgId","" + preId);
            logHashMap.put("msgId","" + msgId);
            logHashMap.put("msg", "" + msg);
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap);
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
        mChatClient = ChatClient.getInstance();
        mChatClient.addListener(mClientListener);
        mChatClient.getRoomManager().addListener(mRoomListener);
        mChatClient.getPeerManager().addListener(mPeerListener);
        String appid = LiveAppUserInfo.getInstance().getPsAppId();
        String appkey = LiveAppUserInfo.getInstance().getPsAppClientKey();
        //irc sdk初始化  code: 0 成功 ，1 参数错误 ， 19 已初始化
        int initcode = mChatClient.init(mContext.getApplicationContext(), appid, appkey, workSpaceDir.getAbsolutePath());
        logger.i("psAppId:" + appid + " PsAppClientKey:" + appkey + " workspace:" + workSpaceDir.getAbsolutePath());
        logger.i("irc sdk initcode: " + initcode);
        //设置直播信息
        liveInfo = new PMDefs.LiveInfo();
        liveInfo.nickname = "p_" + mNickname;
        liveInfo.liveId = liveId;
        if (classId != null) {
            liveInfo.classId = classId;
        } else {
            liveInfo.classId = "";
        }
        if (businessId != null){
            liveInfo.businessId = businessId;
        } else {
            liveInfo.businessId = "1";
        }
//        if (AppConfig.DEBUG){
//            XESToastUtils.showToastLong(mContext,"businessId : " + businessId);
//        }
        if (LiveAppUserInfo.getInstance().getAreaCode() != null) {
            liveInfo.location = LiveAppUserInfo.getInstance().getAreaCode();
        } else {
            liveInfo.location = "";
        }
        int infocode = mChatClient.setLiveInfo(liveInfo);
        //登陆 code: 0 成功， 1 参数错误，11 未初始化，17 已登录，18 正在登陆
        String psimId = LiveAppUserInfo.getInstance().getPsimId();
        String psimKey = LiveAppUserInfo.getInstance().getPsimPwd();
        int logincode = mChatClient.login(psimId, psimKey);
        logger.i("irc sdk logincode:" + logincode);
        StableLogHashMap logHashMap = defaultlog("IRCMessage");
        logHashMap.put("initcode", "" + initcode);
        logHashMap.put("initSDKState", PMDefs.ResultCode.Result_Success == initcode ? "success" : "fail");
        logHashMap.put("logincode", "" + logincode);
        logHashMap.put("initLoginState", PMDefs.ResultCode.Result_Success == logincode ? "success" : "fail");
        logHashMap.put("nickname", mNickname);
        logHashMap.put("PsAppId", appid);
        logHashMap.put("PsAppClientKey", appkey);
        logHashMap.put("PsImId", psimId);
        logHashMap.put("PsImPwd", psimKey);
        logHashMap.put("infocode", "" + infocode);
        logHashMap.put("nickname", liveInfo.nickname);
        logHashMap.put("classId", liveInfo.classId);
        logHashMap.put("businessId", liveInfo.businessId);
        logHashMap.put("location", liveInfo.location);
        logHashMap.put("liveId", liveInfo.liveId);
        logHashMap.put("workspace", workSpaceDir.getAbsolutePath());
        logHashMap.put("time", "" + System.currentTimeMillis());
        logHashMap.put("userid", LiveAppUserInfo.getInstance().getStuId());
        logHashMap.put("where", "NewIRCMessage");
        logHashMap.put("liveId", liveId);
        liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());

    }


    @Override
    public void startVideo() {
        logger.i("startVideo:childName=" + childName);
//        childName = null;
//        if (oldChildName != null) {
//            mHandler.removeCallbacks(startVideoRun);
//            mHandler.post(startVideoRun);
//        }
        stuPushSuccess = false;
        mHandler.removeCallbacks(startVideoRun);
        mHandler.postDelayed(startVideoRun, 10000);
    }

    /**
     * 请求学生视频
     */
    Runnable startVideoRun = new Runnable() {
        @Override
        public void run() {
            if (mIsDestory) {
                return;
            }
            logger.i("startVideoRun:childName=" + childName + ",stuPushSuccess=" + stuPushSuccess);
            if (childName != null && stuPushSuccess) {
//                mHandler.postDelayed(this, 60000);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                String nonce = "" + StableLogHashMap.creatNonce();
                jsonObject.put("type", "" + XESCODE.REQUEST_STUDENT_PUSH);
                jsonObject.put("status", "on");
                jsonObject.put("nonce", nonce);
                String target = "s_" + mNickname;
                sendMessage(target, LiveAppUserInfo.getInstance().getPsimId(), jsonObject.toString());
                target = "ws_" + mNickname;
                sendMessage(target, LiveAppUserInfo.getInstance().getPsimId(), jsonObject.toString());
                //旁听日志
                StableLogHashMap logHashMap = new StableLogHashMap("sendListenCmd");
                logHashMap.put("status", "on");
                logHashMap.put("nickname", mNickname);
                logHashMap.addSno("1").addNonce(nonce).addExpect("1").addStable("1");
                liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(this, 10000);
        }
    };

    @Override
    public String getNickname() {
        return "p_" + mNickname;
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
        mChatClient.getPeerManager().sendPeerMessage(entityList, notice, MSG_PRIORITY_NOTICE,preMsgId);
        msgMap.put(preMsgId[0],notice);
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
        mChatClient.getPeerManager().sendPeerMessage(entityList, message, MSG_PRIORITY_PRI,preMsgId);
        msgMap.put(preMsgId[0],message);
    }

    @Override
    public void sendMessage(String target, String psid, String message) {
        List<PMDefs.PsIdEntity> entityList = new ArrayList<>();
        PMDefs.PsIdEntity psIdEntity = new PMDefs.PsIdEntity(target, psid);
        entityList.add(psIdEntity);
        mChatClient.getPeerManager().sendPeerMessage(entityList, message, MSG_PRIORITY_PRI,preMsgId);
        msgMap.put(preMsgId[0],message);
    }

    /**
     * 播放器销毁
     */
    @Override
    public void destory() {
        mIsDestory = true;
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
            StableLogHashMap logHashMap = defaultlog("unInit");
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap);
        }
        mHandler.removeCallbacks(startVideoRun);
        mHandler.removeCallbacks(mStudyTimeoutRunnable);
        JSONObject jsonObject = new JSONObject();
        try {
            String nonce = "" + StableLogHashMap.creatNonce();
            jsonObject.put("type", "" + XESCODE.REQUEST_STUDENT_PUSH);
            jsonObject.put("status", "off");
            jsonObject.put("nonce", nonce);
            String target = "s_" + mNickname;
            sendMessage(target, jsonObject.toString());
            target = "ws_" + mNickname;
            sendMessage(target, jsonObject.toString());
            //旁听日志
            StableLogHashMap logHashMap = new StableLogHashMap("sendListenCmd");
            logHashMap.put("status", "off");
            logHashMap.put("nickname", mNickname);
            logHashMap.put("time", "" + (System.currentTimeMillis() - enterTime) / 1000);
            logHashMap.addNonce(nonce).addSno("6").addExpect("1").addStable("1");
            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCallback(AuditIRCCallback ircCallback) {
        this.mIRCCallback = ircCallback;
    }

    /**
     * 学生ping超时,提示不在直播间
     */
    private Runnable mStudyTimeoutRunnable = new Runnable() {

        @Override
        public void run() {
            if (mIsDestory) {
                return;
            }
            logger.i("mStudyTimeoutRunnable:childName=" + childName);
            childName = null;
            mIRCCallback.onStudentLeave(true, stuPushStatus);
        }
    };

    private StableLogHashMap defaultlog(String logType) {
        StableLogHashMap logMap = new StableLogHashMap(logType);
        logMap.put("sid", mSid.toString());
        logMap.put("nickname", mNickname);
        logMap.put("time", "" + System.currentTimeMillis());
        logMap.put("uid", LiveAppUserInfo.getInstance().getStuId());
        logMap.put("live_id", liveId);
        logMap.put("channel"  ,mChannel);
        return logMap;
    }

}
