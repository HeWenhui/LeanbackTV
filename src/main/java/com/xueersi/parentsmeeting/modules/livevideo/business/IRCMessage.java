package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.Loger;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * IRC消息。连接IRCConnection和LiveBll，控制聊天的连接和断开
 *
 * @author linyuqiang
 */
public class IRCMessage implements IIRCMessage{
    private String TAG = "IRCMessage";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private IRCConnection mConnection;
    private int mConnectCount = 0, mDisconnectCount = 0;
    private IRCCallback mIRCCallback;
    private String[] mChannels;
    private String mNickname;
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
    /** 是不是获得过用户列表 */
    private boolean onUserList = false;
    /** 和服务器的ping，线程池 */
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    private String currentMode;

    public IRCMessage(Context context, int netWorkType, String login, String nickname, String... channel) {
        this.netWorkType = netWorkType;
        this.mChannels = channel;
        this.mNickname = nickname;
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
        return mConnection != null && mConnection.isConnected();
    }

    /**
     * 是不是获得过用户列表
     *
     * @return
     */
    @Override
    public boolean onUserList() {
        return onUserList && mConnection != null && mConnection.isConnected();
    }

    /**
     * 网络变化
     *
     * @param netWorkType
     */
    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            mLogtf.d("onNetWorkChange:connectError=" + connectError);
            if (connectError) {
                connectError = false;
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        connect("onNetWorkChange");
                    }
                });
            }
        }
        if (ircTalkConf != null) {
            ircTalkConf.onNetWorkChange(netWorkType);
        }
    }

    /** 自己发的消息，如果没发送出去，暂时保存下来 */
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
             /*   mConnection.joinChannel("#" + mChannel);
                mConnection.joinChannel("#300141-29981");*/
                for (String channel:mChannels){
                    mConnection.joinChannel("#" + channel);
                }
                if (mIRCCallback != null) {
                    mIRCCallback.onRegister();
                }
            }

            @Override
            public void onMessage(String target, String sender, String login, String hostname, String text) {
                mLogtf.d("onMessage:sender=" + sender + ":" + text);
                //  如果是专属老师
                if (mIRCCallback != null) {
                    if (mChannels.length>1){
                        if (LiveTopic.MODE_CLASS.equals(currentMode)){
                            mIRCCallback.onMessage(target, sender, login, hostname, text);
                        }

                        if (LiveTopic.MODE_TRANING.equals(currentMode)){
                            mIRCCallback.onMessage(target, sender, login, hostname, text);
                        }
                    }else {
                        mIRCCallback.onMessage(target, sender, login, hostname, text);
                    }
                }
            }

            @Override
            public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
                logger.i("onPrivateMessage:sender=" + sender + ",target=" + target + ",message=" + message);
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
                    if (mChannels.length>1){
                        if (LiveTopic.MODE_CLASS.equals(currentMode)){
                            mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                        }

                        if (LiveTopic.MODE_TRANING.equals(currentMode)){
                            mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                        }
                    }else {
                        mIRCCallback.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                    }
                }
            }

            @Override
            public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target,
                                 String notice, String channelId) {
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
                    if (currentMode == null){
                        mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
                    }else {
                        if (mChannels.length>1){
                            if (("#"+mChannels[0]).equals(channelId)){
                                mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
                            }
                            if (("#"+mChannels[1]).equals(channelId)){
                                mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channelId);
                            }
                        }
                    }
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
                    //  如果不是专属老师
                    if (mChannels.length<=1){
                        mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
                    }else {
                        if (("#"+mChannels[0]).equals(channelId)){
                            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
                        }
                        if (("#"+mChannels[1]).equals(channelId)){
                            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
                        }
                    }

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
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            liveThreadPoolExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    IRCMessage.this.connect("onDisconnect");
                                }
                            });
                        }
                    }, 2000);
                }
            }

            @Override
            public void onUserList(String channel, User[] users) {
                onUserList = true;
                String s = "___bug  onUserList:channel=" + channel + ",users=" + users.length;
                mLogtf.d(s);
                if (mIRCCallback != null) {
                    //  如果不是专属老师
                    if (currentMode == null){
                        mIRCCallback.onUserList(channel, users);
                    }else {
                        if (LiveTopic.MODE_CLASS.equals(currentMode) && ("#"+mChannels[0]).equals(channel)){
                            StringBuilder sb = new StringBuilder();
                            for (User user : users) {
                                sb.append(user.getNick());
                            }
                            s = "___bug2  onUserList:channel=" + channel + ",users=" + users.length+"___"+sb.toString();
                            //  mLogtf.d(s);
                            mIRCCallback.onUserList(channel, users);
                        }

                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length>1 && ("#"+mChannels[1]).equals(channel)){
                            StringBuilder sb = new StringBuilder();
                            for (User user : users) {
                                sb.append(user.getNick());
                            }
                            s = "___bug3  onUserList:channel=" + channel + ",users=" + users.length+"___"+sb.toString();
                            // mLogtf.d(s);
                            mIRCCallback.onUserList(channel, users);
                        }
                    }


                }
            }

            @Override
            public void onJoin(String target, String sender, String login, String hostname) {
                if (sender.startsWith("s_") || sender.startsWith("ws_")) {
                    logger.i("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                } else {
                    mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                }
                if (mIRCCallback != null) {
                    //  如果不是专属老师
                    if (currentMode == null){
                        mLogtf.d("onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                        mIRCCallback.onJoin(target, sender, login, hostname);
                    }else {
                        if (LiveTopic.MODE_CLASS.equals(currentMode) && mChannels[0].equals(target)){
                            //mLogtf.d("___personal onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                            mIRCCallback.onJoin(target, sender, login, hostname);
                        }
                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length>1 && mChannels[1].equals(target)){
                            //mLogtf.d("___personal onJoin:target=" + target + ",sender=" + sender + ",login=" + login + ",hostname=" + hostname);
                            mIRCCallback.onJoin(target, sender, login, hostname);
                        }
                    }

                }
            }

            @Override
            public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String
                    channel) {
                if (sourceNick.startsWith("s_") || sourceNick.startsWith("ws_")) {
                    logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                            + sourceHostname + ",reason=" + reason);
                } else {
                    mLogtf.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                            + sourceHostname + ",reason=" + reason);
                }
                if (mIRCCallback != null) {
                    //  如果不是专属老师
                    if (currentMode == null){
                        mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                    }else {
                        if (LiveTopic.MODE_CLASS.equals(currentMode)){
                            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                     /*       logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
                        }
                        if (LiveTopic.MODE_TRANING.equals(currentMode) && mChannels.length>1){
                            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
                          /*  logger.d("onQuit:sourceNick=" + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname="
                                    + sourceHostname + ",reason=" + reason+"___channel "+channel);*/
                        }
                    }

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
                if (mIRCCallback != null) {
                    mIRCCallback.onUnknown(line);
                }
            }
        });
        boolean getserver = ircTalkConf.getserver(businessDataCallBack);
        if (!getserver) {
            ircTalkConf = null;
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connect("create");
                }
            });
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
            mLogtf.d("connect1:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName()
                    + ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
        } catch (NickAlreadyInUseException e) {
            try {
                mConnection.setLogin2("w" + mNickname);
                mConnection.setNickname("w" + mNickname);
                String NICKKey = mConnection.connect(talkConfEntity.getHost(), Integer.parseInt(talkConfEntity.getPort()), "" + talkConfEntity.getPwd());
                connectError = false;
                //mConnection.joinChannel("#" + mChannel);
                mLogtf.d("connect2-1:method=" + method + ",index=" + index + ",NICKKey=" + NICKKey + ",name=" + mConnection.getName() +
                        ",server=" + mConnection.getServer() + ",port=" + talkConfEntity.getPort());
            } catch (NickAlreadyInUseException e1) {
                mLogtf.e("connect2-2", e1);
            } catch (Exception e1) {
                mLogtf.e("connecte2-3:method=" + method + ",name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
            }
        } catch (Exception e) {
            mLogtf.e("connect3:method=" + method + ",name=" + mConnection.getName() + ",server=" + talkConfEntity.getHost() + "," + e.getMessage(), e);
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
            mLogtf.d("connect4:method=" + method + ",connectError=" + connectError + ",netWorkType=" + netWorkType + ",conf=" + (ircTalkConf == null));
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
    @Override
    public String getConnectNickname() {
        if (mConnection.isConnected()) {
            return mConnection.getName();
        }
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
        if (mChannels.length>1 && currentMode!=null ){
            if (LiveTopic.MODE_TRANING.equals(currentMode)){
                mConnection.sendNotice("#" + mChannels[1], notice);
            }
            if (LiveTopic.MODE_CLASS.equals(currentMode)){
                mConnection.sendNotice("#" + mChannels[0], notice);
            }
        }else {
            mConnection.sendNotice("#" + mChannels[0], notice);
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

    /**
     * 发消息,向聊天室发消息
     *
     * @param message 信息
     */
    @Override
    public void sendMessage(String message) {
        if (mChannels.length>1 && currentMode!=null){
            if (LiveTopic.MODE_TRANING.equals(currentMode)){
                mConnection.sendMessage("#" + mChannels[1], message);
                //Loger.d("____bug 22  channel: "+mChannels[1] +"  message:  "+message);
            }

            if (LiveTopic.MODE_CLASS.equals(currentMode)){
                //Loger.d("____bug 23  channel: "+mChannels[0] +"  message:  "+message);
                mConnection.sendMessage("#" + mChannels[0], message);
            }
        }else {
            // Loger.d("____bug 24  channel: "+mChannels[0] +"  message:  "+message);
            mConnection.sendMessage("#" + mChannels[0], message);
        }
    }

    /**
     * 播放器销毁
     */
    @Override
    public void destory() {
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
    @Override
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

    private IConnectService connectService;

    @Override
    public void setConnectService(IConnectService connectService) {
        this.connectService = connectService;
    }

    @Override
    public void modeChange(String mode){
        // 专属切主讲时，断开专属聊天室
      //  Loger.d("___bug  mode change:  "+mode);
        if (currentMode!=null && !currentMode.equals(mode)){
            if (mChannels!=null && mChannels.length>1){
                mConnection.partChannel("#" + mChannels[1]);
              //  Loger.d("___bug33  partchannel:  "+mode);
            }
        }
      //  Loger.d("___modechange:  "+mode);
        if (mChannels.length>1){
            currentMode = mode;
        }
    }
}
