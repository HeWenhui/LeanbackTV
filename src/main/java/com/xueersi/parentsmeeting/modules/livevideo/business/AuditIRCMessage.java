package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class AuditIRCMessage {
    private String TAG = "AuditIRCMessage";
    String eventid = LiveVideoConfig.LIVE_LISTEN;
    private IRCConnection mConnection;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private AuditIRCCallback mIRCCallback;
    private String mChannel;
    private final String mNickname;
    private String childName;
    /** 备用用户聊天服务配置列表 */
    private List<NewTalkConfEntity> mNewTalkConf;
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

    public AuditIRCMessage(int netWorkType, String channel, String login, String nickname, LiveAndBackDebug liveAndBackDebug) {
        this.netWorkType = netWorkType;
        this.mChannel = channel;
        this.mNickname = nickname;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        mLogtf.d("AuditIRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
    }

    /**
     * 是不是连接中
     *
     * @return
     */
    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

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
    }

    Vector<String> privMsg = new Vector<>();

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
                Loger.i(TAG, "onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
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
                                            mIRCCallback.onStudentError(msg);
                                            mHandler.postDelayed(mStudyTimeoutRunnable, 15000);
                                        }
                                        //旁听日志
                                        StableLogHashMap stableLogHashMap = new StableLogHashMap("studentError");
                                        stableLogHashMap.put("nickname", sender);
                                        stableLogHashMap.put("status", status);
                                        stableLogHashMap.addSno("5").addEx("Y").addStable("1");
                                        liveAndBackDebug.umsAgentDebug(eventid, stableLogHashMap.getData());
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
                                                Loger.e(TAG, "STUDENT_STU_HEART", e);
                                            }
                                        }
                                    }, 1500);
                                }
                                return;
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
                                 String notice) {
                mLogtf.d("onNotice:target=" + target + ",notice=" + notice);
                if (mIRCCallback != null) {
                    mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
                }
            }

            @Override
            public void onChannelInfo(String channel, int userCount, String topic) {
                if (mIRCCallback != null) {
                    mIRCCallback.onChannelInfo(channel, userCount, topic);
                }
            }

            @Override
            public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
                mLogtf.d("onTopic:channel=" + channel + ",topic=" + topic);
                if (mIRCCallback != null) {
                    mIRCCallback.onTopic(channel, topic, setBy, date, changed);
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
                    Loger.i(TAG, "onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                } else {
                    mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                }
                if (mIRCCallback != null) {
                    mIRCCallback.onJoin(target, sender, login, hostname);
                }
            }

            @Override
            public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
                mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                        + sourceHostname + ",reason=" + reason);
                if (mIRCCallback != null) {
                    mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
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
        new Thread() {
            public void run() {
                connect("create");
            }
        }.start();
    }

    public void startVideo() {
        Loger.i(TAG, "startVideo:childName=" + childName);
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
            Loger.i(TAG, "startVideoRun:childName=" + childName + ",stuPushSuccess=" + stuPushSuccess);
            if (childName != null && stuPushSuccess) {
//                mHandler.postDelayed(this, 60000);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "" + XESCODE.REQUEST_STUDENT_PUSH);
                jsonObject.put("status", "on");
                String target = "s_" + mNickname;
                mConnection.sendMessage(target, jsonObject.toString());
                target = "ws_" + mNickname;
                mConnection.sendMessage(target, jsonObject.toString());
                //旁听日志
                StableLogHashMap stableLogHashMap = new StableLogHashMap("sendListenCmd");
                stableLogHashMap.put("status", "on");
                stableLogHashMap.put("nickname", mNickname);
                stableLogHashMap.addSno("1").addExpect("1").addStable("1");
                liveAndBackDebug.umsAgentDebug(eventid, stableLogHashMap.getData());
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
        int index = mSelectTalk++ % mNewTalkConf.size();
        NewTalkConfEntity talkConfEntity = mNewTalkConf.get(index);
        try {
            String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
            //mConnection.joinChannel("#" + mChannel);
            mLogtf.d("connect:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName()
                    + ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
        } catch (NickAlreadyInUseException e) {
            try {
                mConnection.setLogin2("pt_" + mNickname);
                mConnection.setNickname("pt_" + mNickname);
                String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
                //mConnection.joinChannel("#" + mChannel);
                mLogtf.d("connect2:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName() +
                        ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
            } catch (NickAlreadyInUseException e1) {
                mLogtf.e("NickAlreadyInUse2", e);
            } catch (Exception e1) {
                mLogtf.e("NickAlreadyInUse3", e);
            }
        } catch (Exception e) {
            mLogtf.e("connecte:name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
        }
        if (!mIsDestory && !mConnection.isConnected()) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    connect("connect");
                }
            }.start();
        }
    }

    public String getNickname() {
        if (mConnection.isConnected()) {
            return mConnection.getName();
        }
        return mNickname;
    }

    /** 设置备用用户聊天服务配置列表 */
    public void setNewTalkConf(List<NewTalkConfEntity> newTalkConf) {
        this.mNewTalkConf = newTalkConf;
    }

    /**
     * 发通知
     *
     * @param notice
     */
//    public void sendNotice(String notice) {
//        mConnection.sendNotice("#" + mChannel, notice);
//    }

    /**
     * 发通知
     *
     * @param target 目标
     * @param notice
     */
    public void sendNotice(String target, String notice) {
        mConnection.sendNotice(target, notice);
    }

    /**
     * 发消息
     *
     * @param target  目标
     * @param message 信息
     */
    public void sendMessage(String target, String message) {
        mConnection.sendMessage(target, message);
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
    public void destory() {
        mIsDestory = true;
        mHandler.removeCallbacks(startVideoRun);
        mHandler.removeCallbacks(mStudyTimeoutRunnable);
        mHandler.removeCallbacks(mPingRunnable);
        mHandler.removeCallbacks(mTimeoutRunnable);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "" + XESCODE.REQUEST_STUDENT_PUSH);
            jsonObject.put("status", "off");
            String target = "s_" + mNickname;
            mConnection.sendMessage(target, jsonObject.toString());
            target = "ws_" + mNickname;
            mConnection.sendMessage(target, jsonObject.toString());
            //旁听日志
            StableLogHashMap stableLogHashMap = new StableLogHashMap("sendListenCmd");
            stableLogHashMap.put("status", "off");
            stableLogHashMap.put("nickname", mNickname);
            stableLogHashMap.creatNonce().addSno("6").addExpect("1").addStable("1");
            liveAndBackDebug.umsAgentDebug(eventid, stableLogHashMap.getData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

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
            Loger.i(TAG, "mStudyTimeoutRunnable:childName=" + childName);
            childName = null;
            mIRCCallback.onStudentLeave(true, stuPushStatus);
        }
    };
}
