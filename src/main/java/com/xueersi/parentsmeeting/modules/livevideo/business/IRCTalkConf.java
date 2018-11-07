package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.util.DNSUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 直播聊天调度
 *
 * @author lyqai
 * @date 2018/5/22
 */
public class IRCTalkConf {
    private static String TAG = "IRCTalkConf";
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static String eventId = LiveVideoConfig.LIVE_CHAT_GSLB;
    private LogToFile mLogtf;
    private BaseHttpBusiness baseHttpBusiness;
    private ArrayList<TalkConfHost> hosts;
    /**
     * 从上面的列表选择一个服务器
     */
    private int mSelectTalk = 0;
    private String liveId;
    private LiveGetInfo liveGetInfo;
    private String classid;
    private int mLiveType;
    private HttpCallBack callBack;
    private AbstractBusinessDataCallBack businessDataCallBack;
    private String baseHost = null;
    private static final int GET_SERVER = 1;
    private static final int GET_SERVER_TIMEOUT = 2000;
    private static final int GET_SERVER_NEXT = 2000;
    private static final int GET_SERVER_NEXT_LOOP = 30000;
    /**
     * 调度获取聊天服务器地址失败的 回调
     */
    private ChatServiceError chatServiceError;
    /**
     * 网络类型
     */
    private int netWorkType;
    /**
     * 调度是不是在无网络下失败
     */
    private boolean connectError = false;
    /**
     * 播放器是不是销毁
     */
    private boolean mIsDestory = false;
    private static String hostIp = null;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            logger.d("handleMessage:what=" + msg.what);
            getserver();
        }
    };

    public IRCTalkConf(Context context, LiveGetInfo liveGetInfo, int mLiveType, BaseHttpBusiness baseHttpBusiness,
                       ArrayList<TalkConfHost> hosts) {
//        if (AppConfig.DEBUG) {
//            TalkConfHost talkConfHost = hosts.remove(hosts.size() - 1);
//            hosts.add(0, talkConfHost);
//        }

        this.liveId = liveGetInfo.getId();
        this.mLiveType = mLiveType;
        this.baseHttpBusiness = baseHttpBusiness;
        this.hosts = hosts;
        mLogtf = new LogToFile(context, TAG);
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            classid = studentLiveInfo.getClassId();
        }
        for (int i = 0; i < hosts.size(); i++) {
            TalkConfHost talkConfHost = hosts.get(i);
            if (!talkConfHost.isIp()) {
                baseHost = talkConfHost.getHost();
                baseHost = DNSUtil.getHost(baseHost);
                break;
            }
        }
        netWorkType = NetWorkHelper.getNetWorkState(BaseApplication.getContext());
        if (baseHost == null) {
            baseHost = "chatgslb.xescdn.com";
        }
        mLogtf.d("baseHost=" + baseHost);
    }

    public boolean getserver(final AbstractBusinessDataCallBack businessDataCallBack) {
        this.businessDataCallBack = businessDataCallBack;
        mLogtf.d("getserver:hosts=" + hosts.size());
        if (hosts.isEmpty()) {
            return false;
        }
        handler.removeMessages(GET_SERVER);
        handler.sendEmptyMessage(GET_SERVER);
        return true;
    }

    /**
     * 调度获得服务器列表
     */
    private void getserver() {
        if (mIsDestory) {
            return;
        }
        final HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveid", liveId);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            params.addBodyParam("appid", "1");
            params.addBodyParam("classid", classid);
        } else {
            params.addBodyParam("appid", "2");
        }
        params.addBodyParam("ip", getHostIP());
        params.setWriteAndreadTimeOut(GET_SERVER_TIMEOUT);
        final TalkConfHost talkConfHost = hosts.get(mSelectTalk++ % hosts.size());
        final String host = talkConfHost.getHost();
        String url;
        if (host.startsWith("http")) {
            url = host + "/getserver";
        } else {
            url = "http://" + host + "/getserver";
        }
        if (talkConfHost.isIp()) {
            params.addHeaderParam("Host", baseHost);
        }
        if (mSelectTalk == hosts.size()) {
            mSelectTalk = 0;
        }
        callBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (callBack != this) {
                    return;
                }
                handler.removeMessages(GET_SERVER);
                mLogtf.d("onPmSuccess:url=" + url + ",jsonObject=" + responseEntity.getJsonObject());
                JSONArray jsonArray = (JSONArray) responseEntity.getJsonObject();
                List<LiveGetInfo.NewTalkConfEntity> mNewTalkConf = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        LiveGetInfo.NewTalkConfEntity entity = new LiveGetInfo.NewTalkConfEntity();
                        entity.setHost(jsonObject.getString("host"));
                        entity.setPort(jsonObject.getString("port"));
                        entity.setPwd(jsonObject.getString("pwd"));
                        mNewTalkConf.add(entity);
                    } catch (Exception e) {

                    }
                }
                if (!mIsDestory) {
                    businessDataCallBack.onDataSucess(mNewTalkConf);//回调IRCMessage中businessDataCallBack的onDataSucess方法
                }
//                if (callBack != this) {
//                    return;
//                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                mLogtf.e("onPmFailure:msg=" + msg, error);
                if (callBack != this) {
                    return;
                }
                StableLogHashMap stableLogHashMap = new StableLogHashMap();
                stableLogHashMap.put("gslburl", url);
                stableLogHashMap.put("errmsg", msg);
                stableLogHashMap.put("ip", getHostIP());
                stableLogHashMap.put("netWorkType", "" + netWorkType);
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), eventId, stableLogHashMap.getData());
                //体验课获取失败

                if (chatServiceError != null) {
                    chatServiceError.getChatUrlFailure(
                            getHost(url),
                            msg,
                            eventId,
                            "Error",
                            "Android",
                            IpAddressUtil.USER_IP
                    );
                }
//                if (mLiveType == LiveVideoConfig.LIVE_TYPE_STAND_EXPERIENCE) {
//                    StableLogHashMap experienceMap = new StableLogHashMap();
//                    experienceMap.put("url", url);
//                    experienceMap.put("errmsg", msg);
//                    experienceMap.put("liveid", liveId);
//
//                }
                reTry();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                mLogtf.d("onPmError:responseEntity=" + responseEntity.getErrorMsg());
                if (callBack != this) {
                    return;
                }
                StableLogHashMap stableLogHashMap = new StableLogHashMap();
                stableLogHashMap.put("gslburl", url);
                stableLogHashMap.put("errmsg", responseEntity.getErrorMsg());
                stableLogHashMap.put("ip", getHostIP());
                UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), eventId, stableLogHashMap.getData());
                reTry();
            }

            void reTry() {
                handler.removeMessages(GET_SERVER);
                mLogtf.d("reTry:netWorkType=" + netWorkType);
                if (netWorkType == NetWorkHelper.NO_NETWORK) {
                    connectError = true;
                    return;
                }
                if (mSelectTalk == hosts.size()) {
                    mSelectTalk = 0;
                    handler.sendEmptyMessageDelayed(GET_SERVER, GET_SERVER_NEXT_LOOP);
                } else {
                    handler.sendEmptyMessageDelayed(GET_SERVER, GET_SERVER_NEXT);
                }
            }
        };
        callBack.url = url;
        baseHttpBusiness.sendGet(url, params, callBack);
        handler.sendEmptyMessageDelayed(GET_SERVER, GET_SERVER_TIMEOUT);
    }

    /**
     * 网络变化
     *
     * @param netWorkType
     */
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        hostIp = null;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            mLogtf.d("onNetWorkChange:connectError=" + connectError);
            if (connectError) {
                connectError = false;
                handler.sendEmptyMessage(GET_SERVER);
            }
        }
    }

    /**
     * 播放器销毁
     */
    public void destory() {
        mIsDestory = true;
        handler.removeMessages(GET_SERVER);
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        if (hostIp != null) {
            return hostIp;
        }
        hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;

    }

    /**
     * wiki: https://wiki.xesv5.com/pages/viewpage.action?pageId=13842928
     * 回调
     * 调度获取聊天服务器地址失败的
     */
    public static interface ChatServiceError {
        void getChatUrlFailure(String url, String errMsg, String eventId, String logtype, String os, String ip);
    }

    public void setChatServiceError(ChatServiceError chatServiceError) {
        this.chatServiceError = chatServiceError;
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
            return ip.substring(pos <= 0 ? 0 : pos + 2, i);
        } catch (Exception e) {
            return "";
        }
    }
}
