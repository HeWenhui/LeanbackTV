package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class AuditIRCMessage implements IAuditIRCMessage{
    private String TAG = "AuditIRCMessage";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    String eventid = LiveVideoConfig.LIVE_LISTEN;
    private IRCConnection mConnection;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private AuditIRCCallback mIRCCallback;
    private String mChannel;
    private final String mNickname;
    private String childName;
    /** 备用用户聊天服务配置列表 */
    private List<NewTalkConfEntity> mNewTalkConf = new ArrayList<>();
    private IRCTalkConf ircTalkConf;
    /** 从上面的列表选择一个服务器 */
    private int mSelectTalk = 0;
    private LogToFile mLogtf;
    /** 播放器是不是销毁 */
    private boolean mIsDestory = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    /** 网络类型 */
    private int netWorkType;
    /** 调度是不是在无网络下失败 */
    private boolean connectError = false;
    /** 学生推流失败 */
    private boolean stuPushSuccess = false;
    String stuPushStatus = "";
    LiveAndBackDebug liveAndBackDebug;
    long enterTime;

    public AuditIRCMessage(int netWorkType, String channel, String login, String nickname, LiveAndBackDebug liveAndBackDebug) {
        this.netWorkType = netWorkType;
        this.mChannel = channel;
        this.mNickname = nickname;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(TAG);
        mLogtf.clear();
        mLogtf.d("AuditIRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
        enterTime = System.currentTimeMillis();
    }

    /**
     * 是不是连接中
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            mLogtf.d("onNetWorkChange:connectError=" + connectError);
            if (connectError) {
                connectError = false;
                new Thread() {
                    @Override
                    public void run() {
                        connect("onNetWorkChange");
                    }
                }.start();
            }
        }
        if (ircTalkConf != null) {
            ircTalkConf.onNetWorkChange(netWorkType);
        }
    }

    Vector<String> privMsg = new Vector<>();

    @Override
    public void create() {
        mConnection = new IRCConnection(privMsg);
        mConnection.setCallback(new IRCCallback() {

            @Override
            public void onStartConnect() {
                if (mIRCCallback != null) {
                    mIRCCallback.onStartConnect();
                }
            }

            @Override
            public void onRegister() {
                mLogtf.d("onRegister");
                if (mIsDestory) {
                    return;
                }
                if (mIRCCallback != null) {
                    mIRCCallback.onRegister();
                }
                mHandler.post(startVideoRun);
                mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
//                mConnection.joinChannel("#" + mChannel);
            }

            @Override
            public void onMessage(String target, String sender, String login, String hostname, String text) {
                mLogtf.d("onMessage:sender=" + sender + ":" + text);
                if (mIRCCallback != null) {
                    mIRCCallback.onMessage(target, sender, login, hostname, text);
                }
            }

            @Override
            public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target, String message) {
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
                                                    msg = "推流失败";
                                                } else if ("forbidden".equals(status)) {
                                                    msg = "摄像头已禁用";
                                                } else if ("disconnect".equals(status)) {
                                                    msg = "未连接摄像头";
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
                                                    mConnection.sendMessage(sender, heartJson.toString());
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

            @Override
            public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                                 String notice, String channelId) {
                mLogtf.d("onNotice:target=" + target + ",notice=" + notice);
                if (mIRCCallback != null) {
                    mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice,channelId );
                }
            }

            @Override
            public void onChannelInfo(String channel, int userCount, String topic) {
                if (mIRCCallback != null) {
                    mIRCCallback.onChannelInfo(channel, userCount, topic);
                }
            }

            @Override
            public void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
                mLogtf.d("onTopic:channel=" + channel + ",topic=" + topic);
                if (mIRCCallback != null) {
                    mIRCCallback.onTopic(channel, topic, setBy, date, changed,channelId );
                }
            }

            @Override
            public void onConnect(IRCConnection connection) {
                mLogtf.d("onConnect:count=" + mConnectCount + ",mIsDestory=" + mIsDestory);
                if (mIsDestory) {
                    return;
                }
                mConnectCount++;
                String target = "p_" + mNickname;
                if (mConnection.getName().equals(("p_" + mNickname))) {
                    target = "pt_" + mNickname;
                }
//                mConnection.sendMessage(target, "T");
                mLogtf.d("onConnect:name=" + mConnection.getName() + ",target=" + target + ",server=" + mConnection.getServer());
                if (mIRCCallback != null) {
                    mIRCCallback.onConnect(connection);
                }
                mHandler.postDelayed(mPingRunnable, mPingDelay);
            }

            @Override
            public void onDisconnect(IRCConnection connection, boolean isQuitting) {
                if (AuditIRCMessage.this.mConnection != connection) {
                    mLogtf.d("onDisconnect:old");
                    return;
                }
                mDisconnectCount++;
                mLogtf.d("onDisconnect:count=" + mDisconnectCount + ",isQuitting=" + isQuitting);
                if (mIRCCallback != null) {
                    mIRCCallback.onDisconnect(connection, isQuitting);
                }
                mHandler.removeCallbacks(mPingRunnable);
                if (!isQuitting) {
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AuditIRCMessage.this.connect("onDisconnect");
                        }
                    }.start();
                }
            }

            @Override
            public void onUserList(String channel, User[] users) {
                String s = "onUserList:channel=" + channel + ",users=" + users.length;
                for (int i = 0; i < users.length; i++) {
                    User user = users[i];
                    if (!user.getNick().startsWith("s_")) {
                        s += ",user=" + user.getNick();
                    }
                }
                mLogtf.d(s);
                if (mIRCCallback != null) {
                    mIRCCallback.onUserList(channel, users);
                }
            }

            @Override
            public void onJoin(String target, String sender, String login, String hostname) {
                if (sender.startsWith("s_")) {
                    logger.i("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                } else {
                    mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                }
                if (mIRCCallback != null) {
                    mIRCCallback.onJoin(target, sender, login, hostname);
                }
            }

            @Override
            public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String
                    channel) {
                mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                        + sourceHostname + ",reason=" + reason);
                if (mIRCCallback != null) {
                    mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                }
            }

            @Override
            public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                               String recipientNick, String reason) {
                mLogtf.d("onKick:target=" + target + ",kickerNick=" + kickerNick + ",kickerLogin=" + kickerLogin
                        + ",kickerHostname=" + kickerHostname + ",reason=" + reason);
                if (mIRCCallback != null) {
                    mIRCCallback.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
                }
            }

            @Override
            public void onUnknown(String line) {
                if (line.contains("PONG")) {// :100000.irc PONG 100000.irc
                    // :52-1453456527158
                    int index = line.lastIndexOf(":");
                    String pong = line.substring(index + 1);
                    String[] split = pong.split("-");
                    int count = Integer.parseInt(split[0]);
                    long time = Long.parseLong(split[1]);
                    if (count - mPingCout == -1) {
                        mHandler.removeCallbacks(mTimeoutRunnable);
                        mHandler.postDelayed(mPingRunnable, mPingDelay);
                    }
                }
            }
        });
        boolean getserver = ircTalkConf.getserver(businessDataCallBack);
        if (!getserver) {
            ircTalkConf = null;
            new Thread() {
                @Override
                public void run() {
                    connect("create");
                }
            }.start();
        }
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

    /** 请求学生视频 */
    Runnable startVideoRun = new Runnable() {
        @Override
        public void run() {
            if (mConnection == null || mIsDestory) {
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
                mConnection.sendMessage(target, jsonObject.toString());
                target = "ws_" + mNickname;
                mConnection.sendMessage(target, jsonObject.toString());
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

    private synchronized void connect(String method) {
        mHandler.removeCallbacks(mPingRunnable);
        if (mIsDestory) {
            return;
        }
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            connectError = true;
            mLogtf.d("connect(NO_NETWORK):method=" + method);
            return;
        }
        // connection.setLogin2(login);
        mConnection.setLogin2("p_" + mNickname);
        mConnection.setNickname("p_" + mNickname);
        if (mNewTalkConf.isEmpty()) {
            mLogtf.d("connect:mNewTalkConf.isEmpty:ircTalkConf=" + (ircTalkConf == null) + ",method=" + method);
            if (ircTalkConf != null) {
                ircTalkConf.getserver(businessDataCallBack);
            }
            return;
        }
        int index = mSelectTalk++ % mNewTalkConf.size();
        NewTalkConfEntity talkConfEntity = mNewTalkConf.get(index);
        try {
            String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
            //mConnection.joinChannel("#" + mChannel);
            mLogtf.d("connect1:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName()
                    + ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
        } catch (NickAlreadyInUseException e) {
            try {
                mConnection.setLogin2("pt_" + mNickname);
                mConnection.setNickname("pt_" + mNickname);
                String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
                //mConnection.joinChannel("#" + mChannel);
                mLogtf.d("connect2-1:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName() +
                        ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
            } catch (NickAlreadyInUseException e1) {
                mLogtf.e("connect2-2", e1);
            } catch (Exception e1) {
                mLogtf.e("connecte2-3:method=" + method + ",name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
            }
        } catch (Exception e) {
            mLogtf.e("connecte3:name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
        }
        if (!mIsDestory && !mConnection.isConnected()) {
            if (netWorkType != NetWorkHelper.NO_NETWORK && ircTalkConf != null) {
                mNewTalkConf.remove(index);
            }
            mLogtf.d("connect4:method=" + method + ",connectError=" + connectError + ",netWorkType=" + netWorkType + ",conf=" + (ircTalkConf == null));
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    connect("connect2");
                }
            }.start();
        }
    }

    @Override
    public String getNickname() {
        if (mConnection.isConnected()) {
            return mConnection.getName();
        }
        return mNickname;
    }

    /**
     * 直播服务器调度返回
     */
    AbstractBusinessDataCallBack businessDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            mNewTalkConf = (List<NewTalkConfEntity>) objData[0];
            new Thread() {
                @Override
                public void run() {
                    connect("onDataSucess");
                }
            }.start();
        }
    };

    @Override
    public void setIrcTalkConf(IRCTalkConf ircTalkConf) {
        this.ircTalkConf = ircTalkConf;
    }

    /**
     * 发通知
     *
     * @param target 目标
     * @param notice
     */
    @Override
    public void sendNotice(String target, String notice) {
        mConnection.sendNotice(target, notice);
    }

    /**
     * 发消息
     *
     * @param target  目标
     * @param message 信息
     */
    @Override
    public void sendMessage(String target, String message) {
        mConnection.sendMessage(target, message);
    }

    @Override
    public void sendMessage(String target, String psid, String message) {

    }

    /**
     * 发消息,向聊天室发消息
     *
     * @param message 信息
     */
//    public void sendMessage(String message) {
//        mConnection.sendMessage("#" + mChannel, message);
//    }

    /** 播放器销毁 */
    @Override
    public void destory() {
        mIsDestory = true;
        mHandler.removeCallbacks(startVideoRun);
        mHandler.removeCallbacks(mStudyTimeoutRunnable);
        mHandler.removeCallbacks(mPingRunnable);
        mHandler.removeCallbacks(mTimeoutRunnable);
        JSONObject jsonObject = new JSONObject();
        try {
            String nonce = "" + StableLogHashMap.creatNonce();
            jsonObject.put("type", "" + XESCODE.REQUEST_STUDENT_PUSH);
            jsonObject.put("status", "off");
            jsonObject.put("nonce", nonce);
            String target = "s_" + mNickname;
            mConnection.sendMessage(target, jsonObject.toString());
            target = "ws_" + mNickname;
            mConnection.sendMessage(target, jsonObject.toString());
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
        if (mConnection != null) {
            mConnection.disconnect();
        }
        if (ircTalkConf != null) {
            ircTalkConf.destory();
        }
    }

    @Override
    public void setCallback(AuditIRCCallback ircCallback) {
        this.mIRCCallback = ircCallback;
    }

    /** ping的次数 */
    private int mPingCout = 0;
    /** 当前ping的时间 */
    private long mPintBefore = 0;
    /** ping的时间间隔 */
    private final long mPingDelay = 5000;
    /** pong的的时间间隔 */
    private final long mPongDelay = 6000;
    /**
     * 循环ping
     */
    private Runnable mPingRunnable = new Runnable() {

        @Override
        public void run() {
            new Thread() {
                @Override
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    mPintBefore = System.currentTimeMillis();
                    mConnection.sendRawLine("ping :" + (mPingCout++) + "-" + mPintBefore);
                    mHandler.postDelayed(mTimeoutRunnable, mPongDelay);
                }
            }.start();
        }
    };

    /**
     * ping超时,重连
     */
    private Runnable mTimeoutRunnable = new Runnable() {

        @Override
        public void run() {
            new Thread() {
                public void run() {
                    if (mIsDestory) {
                        return;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            }.start();
        }
    };

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
}
