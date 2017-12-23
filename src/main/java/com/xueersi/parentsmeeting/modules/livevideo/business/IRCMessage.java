package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class IRCMessage {
    private String TAG = "IRCMessage";
    private IRCConnection mConnection;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private IRCCallback mIRCCallback;
    private String mChannel;
    private String mNickname;
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
    /** 和服务器的ping，线程池 */
    private ThreadPoolExecutor pingPool;

    public IRCMessage(int netWorkType, String channel, String login, String nickname) {
        this.netWorkType = netWorkType;
        this.mChannel = channel;
        this.mNickname = nickname;
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        mLogtf.d("IRCMessage:channel=" + channel + ",login=" + login + ",nickname=" + nickname);
        pingPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        pingPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
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
                mConnection.joinChannel("#" + mChannel);
                if (mIRCCallback != null) {
                    mIRCCallback.onRegister();
                }
            }

            @Override
            public void onMessage(String target, String sender, String login, String hostname, String text) {
                mLogtf.d("onMessage:sender=" + sender + ":" + text);
                if (mIRCCallback != null) {
                    mIRCCallback.onMessage(target, sender, login, hostname, text);
                }
            }

            @Override
            public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
                Loger.i(TAG, "onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
                String name = mConnection.getName();
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
                    mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                }
            }

            @Override
            public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                                 String notice) {
                boolean send = true;
                try {
                    JSONObject object = new JSONObject(notice);
                    int mtype = object.getInt("type");
                    if (mtype == XESCODE.SPEECH_RESULT) {
                        send = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (send) {
                    mLogtf.d("onNotice:target=" + target + ",notice=" + notice);
                }
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
                mLogtf.d("onConnect:count=" + mConnectCount);
                mConnectCount++;
                String target = "" + mNickname;
                if (mConnection.getName().equals(mNickname)) {
                    target = "w" + mNickname;
                }
                mConnection.sendMessage(target, "T");
                mLogtf.d("onConnect:name=" + mConnection.getName() + ",server=" + mConnection.getServer());
                if (mIRCCallback != null) {
                    mIRCCallback.onConnect(connection);
                }
                mHandler.postDelayed(mPingRunnable, mPingDelay);
            }

            @Override
            public void onDisconnect(IRCConnection connection, boolean isQuitting) {
                if (IRCMessage.this.mConnection != connection) {
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
                            IRCMessage.this.connect("onDisconnect");
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
        mConnection.setLogin2(mNickname);
        mConnection.setNickname(mNickname);
        int index = mSelectTalk++ % mNewTalkConf.size();
        NewTalkConfEntity talkConfEntity = mNewTalkConf.get(index);
        try {
            String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
            //mConnection.joinChannel("#" + mChannel);
            mLogtf.d("connect:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName()
                    + ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
        } catch (NickAlreadyInUseException e) {
            try {
                mConnection.setLogin2("w" + mNickname);
                mConnection.setNickname("w" + mNickname);
                String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
                //mConnection.joinChannel("#" + mChannel);
                mLogtf.d("connect2:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName() +
                        ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
            } catch (NickAlreadyInUseException e1) {
                Loger.d(TAG, "connect2-1", e1);
            } catch (Exception e1) {
                Loger.d(TAG, "connect2-2", e1);
            }
        } catch (Exception e) {
            mLogtf.e("connecte:name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
        }
        if (!mConnection.isConnected()) {
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
    public void sendNotice(String notice) {
        mConnection.sendNotice("#" + mChannel, notice);
    }

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
    public void sendMessage(String message) {
        mConnection.sendMessage("#" + mChannel, message);
    }

    /** 播放器销毁 */
    public void destory() {
        mIsDestory = true;
        pingPool.shutdownNow();
        mHandler.removeCallbacks(mPingRunnable);
        mHandler.removeCallbacks(mTimeoutRunnable);
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    public void setCallback(IRCCallback ircCallback) {
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
            pingPool.execute(new Runnable() {
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
}
