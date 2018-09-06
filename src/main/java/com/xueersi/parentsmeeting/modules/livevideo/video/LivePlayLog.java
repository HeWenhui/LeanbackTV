package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.netease.LDNetDiagnoClient.LDNetTraceClient;
import com.netease.LDNetDiagnoService.JavaTraceResult;
import com.netease.LDNetDiagnoService.LDNetTraceRoute;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.framework.utils.BarUtils;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.DNSUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.HardWareUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.AvformatOpenInputError;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/9/4.
 * 直播播放日志
 */
public class LivePlayLog extends PlayerService.SimpleVPlayerListener {
    private static String TAG = "LivePlayLog";
    private Logger logger = LoggerFactory.getLogger(TAG);
    private PlayerService vPlayer;
    /** 每秒帧数-10秒统计 */
    private ArrayList<Float> framesPsTen = new ArrayList<Float>();
    /** 第一次播放的帧数 */
    long fistDisaplyCount = 0;
    /** 上一次播放的帧数 */
    long lastDisaplyCount = 0;
    float fps = 12.0f;
    /** 帧数10秒统计,开始时间 */
    long frame10Start;
    long lastTrafficStatisticByteCount;
    /** 视频是不是再缓冲 */
    boolean isBuffer = false;
    /** 缓冲开始时间 */
    long bufferTime = 0;
    /** 缓冲类型 */
    int bufType = 1;
    boolean isSeek = false;
    private Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private BaseHttpBusiness baseHttpBusiness;
    /** 视频开始播放时间 */
    private long openStart;
    /** 视频播放成功时间 */
    private long openSuccess;
    /** 心跳时间 */
    private long heartTime;
    /** onNativeInvoke时间 */
    private long onNativeInvoke;
    private String logurl = LiveVideoConfig.URL_CDN_LOG;
    private String userId;
    /** 当前播放的视频地址 */
    private Uri mUri;
    static HashMap<Uri, String> sipMap = new HashMap<>();
    private String sip;
    private String versionName;
    /** cpu名字 */
    private String cpuName;
    /** 可用内存大小 */
    private String memsize;
    private String channelname;
    private int heartCount;
    LDNetTraceClient ldNetTraceClient;
    /** 一个地址5分钟传一次 */
    HashMap<String, Long> urlTrace = new HashMap<>();
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private boolean isLive = true;
    private DecimalFormat df = new DecimalFormat("######0.00");
    String logVersion = "1";
    String serv = "120";

    public LivePlayLog(final Activity activity, boolean isLive) {
        logger.setLogMethod(false);
        this.activity = activity;
        baseHttpBusiness = new BaseHttpBusiness(activity);
        ldNetTraceClient = new LDNetTraceClient(activity);
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        userId = myUserInfoEntity.getStuId();
        versionName = getAppVersionName();
        cpuName = HardWareUtil.getCpuName();
        memsize = DeviceUtils.getAvailRams(activity);
        this.isLive = isLive;

//        if (AppConfig.DEBUG) {
//            logurl = "http://10.99.1.251/log";
//        }
//        if (AppConfig.DEBUG) {
//            new Thread() {
//                @Override
//                public void run() {
//                    while (!activity.isFinishing()) {
//                        final HashMap<String, String> defaultKey = new HashMap<>();
//                        float cpuRate = HardWareUtil.getProcessCpuRate();
//                        defaultKey.put("cpu", "" + cpuRate);
//                        Runtime runtime = Runtime.getRuntime();
//                        long totalMemory = runtime.totalMemory();
//                        defaultKey.put("mem", "" + totalMemory);
//                        double CPURateDesc = HardWareUtil.getCPURateDesc();
//                        DecimalFormat df = new DecimalFormat("######0.00");
//                        Loger.d(TAG, "testCpu:cpuRate=" + cpuRate + ",totalMemory=" + totalMemory + ",CPURateDesc=" + df.format(CPURateDesc));
//                    }
//                }
//            }.start();
//        }
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    /**
     * 设置播放地址
     *
     * @param lastPlayserverEntity
     */
    public void setLastPlayserverEntity(PlayServerEntity.PlayserverEntity lastPlayserverEntity) {
        this.lastPlayserverEntity = lastPlayserverEntity;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public String getCpuName() {
        return cpuName;
    }

    public String getMemsize() {
        return memsize;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
//        float lastFps = 0;

        @Override
        public void handleMessage(Message msg) {
            //不是直播，不统计心跳
            if (!isLive) {
                return;
            }
            try {
                if (vPlayer.isInitialized() && lastPlayserverEntity != null) {
                    if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                        IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                        float fps;
                        if (isBuffer) {
                            fps = 0;
                        } else {
                            fps = ijkMediaPlayer.getVideoOutputFramesPerSecond();
                        }
                        long disaplyCount = ijkMediaPlayer.getDisaplyCount();
                        framesPsTen.add(fps);
                        Loger.d(TAG, "handleMessage:fps=" + fps + ",disaplyCount=" + disaplyCount + "," + (disaplyCount - lastDisaplyCount));
                        if (framesPsTen.size() == 15) {
                            ArrayList<Float> framesPsTenTemp = new ArrayList<Float>(framesPsTen);
                            framesPsTen.clear();
                            long bufferduration = 0;
                            float bitrate = 0f;
                            long trafficStatisticByteCount = 0;
                            try {
                                if (vPlayer.isInitialized()) {
                                    bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                                    bitrate = ijkMediaPlayer.getTcpSpeed() * 8 / 1000;
                                    trafficStatisticByteCount = ijkMediaPlayer.getTrafficStatisticByteCount();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            long time = System.currentTimeMillis() - frame10Start;
                            Loger.d(TAG, "handleMessage:fps=" + (disaplyCount - fistDisaplyCount) / 10 + ",time=" + time);
                            float averagefps = (float) (((double) (disaplyCount - fistDisaplyCount)) * 1000 / time);
                            xescdnLogHeart(framesPsTenTemp, averagefps, bufferduration, bitrate, trafficStatisticByteCount - lastTrafficStatisticByteCount);
                            fistDisaplyCount = disaplyCount;
                            lastTrafficStatisticByteCount = trafficStatisticByteCount;
                            heartTime = frame10Start = System.currentTimeMillis();
                        }
                        lastDisaplyCount = disaplyCount;
//                        if (lastFps != 0) {
//                            frames.add("" + ((int) ((lastFps + fps) * 5 / 2)));
//                        } else {
//                            frames.add("" + ((int) (fps * 5)));
//                        }
//                        lastFps = fps;
                    }
                }
            } catch (Exception e) {
                Loger.e(BaseApplication.getContext(), TAG, "handleMessage", e, true);
            }
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    private void send(String method) {
        framesPsTen.clear();
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("ver", logVersion);
        defaultKey.put("serv", serv);
        defaultKey.put("pri", "121");
        defaultKey.put("ts", "" + System.currentTimeMillis());
        defaultKey.put("appid", "xes20001");
        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
        defaultKey.put("agent", "m-android_" + versionName);
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
        defaultKey.put("arch", "" + cpuName);
        int totalRam = HardWareUtil.getTotalRam();
        defaultKey.put("ram", "" + (totalRam / 1024));
        defaultKey.put("net", "" + getNet());
        defaultKey.put("cpu", "" + getCpuRate());
        defaultKey.put("mem", "" + getMemRate());
        String cip = oldCipdispatch;
        if (StringUtils.isEmpty(cip)) {
            cip = IpAddressUtil.USER_IP;
        }
        defaultKey.put("cip", "" + cip);
        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
        defaultKey.put("sip", "" + getRemoteIp());
        defaultKey.put("tid", "" + UUID.randomUUID());

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("url", mUri);
            dataJson.put("uri", channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("node", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("code", "0");
            dataJson.put("msg", "Success");
            dataJson.put("method", "" + method);
            long bufferduration = 0;
            long trafficStatisticByteCount = lastTrafficStatisticByteCount;
            if (vPlayer.isInitialized()) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                trafficStatisticByteCount = ijkMediaPlayer.getTrafficStatisticByteCount();
            }
            if (isBuffer) {
                dataJson.put("bufType", "" + bufType);
                dataJson.put("bufDur", "" + (System.currentTimeMillis() - bufferTime));
            }
            dataJson.put("latency", "" + bufferduration);
            long time = System.currentTimeMillis() - frame10Start;
            float averagefps = (float) (((double) (lastDisaplyCount - fistDisaplyCount)) * 1000 / time);
            dataJson.put("avgFps", "" + averagefps);
            dataJson.put("fps", "" + fps);
            dataJson.put("playBuf", "" + bufferduration);
            dataJson.put("hbDur", "" + heartTime);
            dataJson.put("bytes", "" + (trafficStatisticByteCount - lastTrafficStatisticByteCount));
            dataJson.put("uid", "" + userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog2(defaultKey, dataJson);
    }

    public void onPause() {
        handler.removeMessages(1);
        send("onPause");
    }

    public void onReplay() {
        handler.removeMessages(1);
//        send("onReplay");
    }

    @Override
    public void onOpenStart() {
        super.onOpenStart();
        framesPsTen.clear();
        handler.removeMessages(1);
        openStart = System.currentTimeMillis();
        mUri = vPlayer.getUri();
        if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
            IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
            ijkMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
                @Override
                public boolean onNativeInvoke(int what, Bundle args) {
                    Loger.d(TAG, "onOpenStart:what=" + what + "," + mUri + ",args=" + args);
                    if (what == CTRL_DID_TCP_OPEN) {
                        onNativeInvoke = System.currentTimeMillis();
                        sip = args.getString("ip", "0.0.0.0");
                        sipMap.put(mUri, sip);
                        long openTime = (System.currentTimeMillis() - openSuccess);
                        Loger.d(TAG, "onOpenStart:what=" + what + "," + mUri + ",openTime=" + openTime);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onOpenSuccess() {
        super.onOpenSuccess();
        sip = sipMap.get(mUri);
        lastDisaplyCount = fistDisaplyCount = 0;
        lastTrafficStatisticByteCount = 0;
        frame10Start = System.currentTimeMillis();
        openSuccess = System.currentTimeMillis();
        handler.sendEmptyMessageDelayed(1, 1000);
        final long openTime = (System.currentTimeMillis() - openStart);
        long onNativeInvokeTime = (System.currentTimeMillis() - onNativeInvoke);
        Loger.d(TAG, "onOpenSuccess:openTime=" + openTime + ",Invoke=" + onNativeInvokeTime + ",sipMap=" + sipMap.size() + ",sip=" + sip);
        getFps();

        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                URLDNS urldns = new URLDNS();
                try {
                    DNSUtil.getDns(urldns, mUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                HashMap<String, String> defaultKey = new HashMap<>();
                defaultKey.put("ver", logVersion);
                defaultKey.put("serv", serv);
                defaultKey.put("pri", "120");
                defaultKey.put("ts", "" + System.currentTimeMillis());
                defaultKey.put("appid", "xes20001");
                defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
                defaultKey.put("agent", "m-android_" + versionName);
                defaultKey.put("os", "" + Build.VERSION.SDK_INT);
                defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
                defaultKey.put("arch", "" + cpuName);
                int totalRam = HardWareUtil.getTotalRam();
                defaultKey.put("ram", "" + (totalRam / 1024));
                defaultKey.put("net", "" + getNet());
                defaultKey.put("cpu", "" + getCpuRate());
                defaultKey.put("mem", "" + getMemRate());
                String cip = oldCipdispatch;
                if (StringUtils.isEmpty(cip)) {
                    cip = IpAddressUtil.USER_IP;
                }
                defaultKey.put("cip", "" + cip);
                defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                String hostIp = getRemoteIp();
                defaultKey.put("sip", "" + hostIp);
                defaultKey.put("tid", "" + UUID.randomUUID());

                JSONObject dataJson = new JSONObject();
                try {
                    dataJson.put("url", mUri);
                    dataJson.put("uri", channelname);
                    if (lastPlayserverEntity != null) {
                        dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                    }
                    dataJson.put("code", "0");
                    dataJson.put("msg", "Success");
                    dataJson.put("dns", "" + urldns.time);
                    dataJson.put("delay", "" + openTime);
                    dataJson.put("uid", "" + userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                xescdnLog2(defaultKey, dataJson);
            }
        });
    }

    public void onBufferTimeOut() {
        send("onBufferTimeOut");
    }

    @Override
    public void onBufferStart() {
        super.onBufferStart();
        isBuffer = true;
        bufferTime = System.currentTimeMillis();
        Loger.d(TAG, "onBufferStart:isInitialized=" + vPlayer.isInitialized());

        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("ver", logVersion);
        defaultKey.put("serv", serv);
        defaultKey.put("pri", "122");
        defaultKey.put("ts", "" + System.currentTimeMillis());
        defaultKey.put("appid", "xes20001");
        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
        defaultKey.put("agent", "m-android_" + versionName);
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
        defaultKey.put("arch", "" + cpuName);
        int totalRam = HardWareUtil.getTotalRam();
        defaultKey.put("ram", "" + (totalRam / 1024));
        defaultKey.put("net", "" + getNet());
        defaultKey.put("cpu", "" + getCpuRate());
        defaultKey.put("mem", "" + getMemRate());
        String cip = oldCipdispatch;
        if (StringUtils.isEmpty(cip)) {
            cip = IpAddressUtil.USER_IP;
        }
        defaultKey.put("cip", "" + cip);
        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
        String msip = getRemoteIp();
        defaultKey.put("sip", "" + msip);
        defaultKey.put("tid", "" + UUID.randomUUID());

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("url", mUri);
            dataJson.put("uri", channelname);
            ;
            if (lastPlayserverEntity != null) {
                dataJson.put("node", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("bufType", "" + bufType);
            dataJson.put("uid", "" + userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog2(defaultKey, dataJson);
        startTraceRoute("" + mUri, msip, cip);
    }

    @Override
    public void onBufferComplete() {
        super.onBufferComplete();
        isBuffer = false;
        Loger.d(TAG, "onBufferComplete:isInitialized=" + vPlayer.isInitialized());
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("ver", logVersion);
        defaultKey.put("serv", serv);
        defaultKey.put("pri", "123");
        defaultKey.put("ts", "" + System.currentTimeMillis());
        defaultKey.put("appid", "xes20001");
        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
        defaultKey.put("agent", "m-android_" + versionName);
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
        defaultKey.put("arch", "" + cpuName);
        int totalRam = HardWareUtil.getTotalRam();
        defaultKey.put("ram", "" + (totalRam / 1024));
        defaultKey.put("net", "" + getNet());
        defaultKey.put("cpu", "" + getCpuRate());
        defaultKey.put("mem", "" + getMemRate());
        String cip = oldCipdispatch;
        if (StringUtils.isEmpty(cip)) {
            cip = IpAddressUtil.USER_IP;
        }
        defaultKey.put("cip", "" + cip);
        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
        String msip = getRemoteIp();
        defaultKey.put("sip", "" + msip);
        defaultKey.put("tid", "" + UUID.randomUUID());

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("url", mUri);
            dataJson.put("uri", channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("node", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("bufType", "" + bufType);
            dataJson.put("bufDur", "" + (System.currentTimeMillis() - bufferTime));
            dataJson.put("uid", "" + userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog2(defaultKey, dataJson);
    }

    String oldCipdispatch = "";

    private String getMemRate() {
        int totalRam = HardWareUtil.getTotalRam();
        long availMemory = HardWareUtil.getAvailMemory(activity) / 1024;
        double memRate = (double) ((totalRam - availMemory) * 100) / (double) totalRam;
        return "" + df.format(memRate);
    }

    private String getCpuRate() {
        double cpuRate = HardWareUtil.getCPURateDesc();
        return "" + df.format(cpuRate);
    }

    public void liveGetPlayServer(final long delay, int code, String cipdispatch, final URLDNS urldns, final String url) {
        Loger.d(TAG, "liveGetPlayServer:delay=" + delay + ",ipsb=" + urldns.ip);
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("ver", logVersion);
        defaultKey.put("serv", serv);
        defaultKey.put("pri", "0");
        defaultKey.put("ts", "" + System.currentTimeMillis());
        defaultKey.put("appid", "xes20001");
        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
        defaultKey.put("agent", "m-android_" + versionName);
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
        defaultKey.put("arch", "" + cpuName);
        int totalRam = HardWareUtil.getTotalRam();
        defaultKey.put("ram", "" + (totalRam / 1024));
        defaultKey.put("net", "" + getNet());
        defaultKey.put("cpu", "" + getCpuRate());
        defaultKey.put("mem", "" + getMemRate());
        if (StringUtils.isEmpty(cipdispatch)) {
            cipdispatch = oldCipdispatch;
        }
        oldCipdispatch = cipdispatch;
        defaultKey.put("cip", "" + cipdispatch);
        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
        defaultKey.put("sip", "" + urldns.ip);
        defaultKey.put("tid", "" + UUID.randomUUID());

        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("url", url);
            dataJson.put("code", "" + code);
            if (code == 0) {
                dataJson.put("msg", "Success");
            } else {
                dataJson.put("msg", "Fail");
            }
            dataJson.put("dns", "" + urldns.time);
            dataJson.put("delay", "" + delay);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog2(defaultKey, dataJson);

        if (code == PlayFailCode.TIME_OUT) {
            startTraceRoute("" + url, urldns.ip, cipdispatch);
        }
    }

    private String getRemoteIp() {
        String remoteIp;
        if (lastPlayserverEntity != null) {
            String ipAddress = lastPlayserverEntity.getIpAddress();
            if (StringUtils.isEmpty(ipAddress)) {
                if (StringUtils.isEmpty(sip)) {
                    remoteIp = lastPlayserverEntity.getAddress();
                } else {
                    remoteIp = sip;
                }
            } else {
                remoteIp = ipAddress;
            }
        } else {
            remoteIp = sip;
        }
        return remoteIp;
    }

    private void xescdnLogHeart(final ArrayList<Float> framesPsTen, final float averagefps, final long bufferduration, final float bitrate, final long bytes) {
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
//                final HashMap<String, String> defaultKey = new HashMap<>();
//                double cpuRate = HardWareUtil.getCPURateDesc();
//                DecimalFormat df = new DecimalFormat("######0.00");
//                defaultKey.put("cpu", "" + df.format(cpuRate));
//                long availMemory = HardWareUtil.getAvailMemory(activity) / 1024;
//                int totalRam = HardWareUtil.getTotalRam();
//                double memRate = (double) ((totalRam - availMemory) * 100) / (double) totalRam;
//                defaultKey.put("mem", "" + df.format(memRate));
//                Loger.d(TAG, "xescdnLogHeart:cpuRate=" + cpuRate + ",availMemory=" + availMemory);
//                float totalfps = 0;
//                for (int i = 0; i < framesPsTen.size(); i++) {
//                    Float f = framesPsTen.get(i);
//                    totalfps += f;
//                }
//                defaultKey.put("net", "" + getNet());
//                float averagefps2 = totalfps / 10f;
//                Loger.d(TAG, "xescdnLogHeart:averagefps=" + averagefps + "," + averagefps2);
//                xescdnLogHeart(defaultKey, averagefps, averagefps2, bufferduration, bitrate);

                HashMap<String, String> defaultKey = new HashMap<>();
                defaultKey.put("ver", logVersion);
                defaultKey.put("serv", serv);
                defaultKey.put("pri", "124");
                defaultKey.put("ts", "" + System.currentTimeMillis());
                defaultKey.put("appid", "xes20001");
                defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
                defaultKey.put("agent", "m-android_" + versionName);
                defaultKey.put("os", "" + Build.VERSION.SDK_INT);
                defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
                defaultKey.put("arch", "" + cpuName);
                int totalRam = HardWareUtil.getTotalRam();
                defaultKey.put("ram", "" + (totalRam / 1024));
                defaultKey.put("net", "" + getNet());
                defaultKey.put("cpu", "" + getCpuRate());
                defaultKey.put("mem", "" + getMemRate());
                String cip = oldCipdispatch;
                if (StringUtils.isEmpty(cip)) {
                    cip = IpAddressUtil.USER_IP;
                }
                defaultKey.put("cip", "" + cip);
                defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                String remoteIp = getRemoteIp();
                defaultKey.put("sip", "" + remoteIp);
                defaultKey.put("tid", "" + UUID.randomUUID());

                JSONObject dataJson = new JSONObject();
                try {
                    dataJson.put("url", mUri);
                    dataJson.put("uri", channelname);
                    if (lastPlayserverEntity != null) {
                        dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                    }
                    dataJson.put("latency", "" + bufferduration);
                    dataJson.put("avgFps", "" + averagefps);
                    dataJson.put("fps", "" + fps);
                    dataJson.put("playBuf", "" + bufferduration);
                    dataJson.put("hbDur", "" + 30000);
                    dataJson.put("bytes", "" + bytes);
                    dataJson.put("uid", "" + userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                xescdnLog2(defaultKey, dataJson);
            }
        });
    }

    private void xescdnLogHeart(HashMap<String, String> defaultKey, float averagefps, float averagefps2, long bufferduration, float bitrate) {
        defaultKey.put("dataType", "603");
        defaultKey.put("traceId", "" + UUID.randomUUID());
        String remoteIp = getRemoteIp();
        defaultKey.put("sip", "" + remoteIp);
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("channelname", "" + channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
                dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("bufferduration", "" + bufferduration);
            dataJson.put("averagefps", "" + averagefps);
            dataJson.put("averagefps2", "" + averagefps2);
            dataJson.put("fps", "" + fps);
            dataJson.put("bitrate", "" + bitrate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog(defaultKey, dataJson);
    }

    private void xescdnLogPlay(HashMap<String, String> defaultKey, JSONObject dataJson) {
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("device", "" + DeviceInfo.getDeviceName());
        xescdnLog(defaultKey, dataJson);
    }

    private void xescdnLog(HashMap<String, String> defaultKey, JSONObject dataJson) {
        final JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("timestamp", "" + System.currentTimeMillis());
            requestJson.put("appid", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
            requestJson.put("serviceType", "6");
            requestJson.put("uid", "" + userId);
            requestJson.put("agent", "m-android " + versionName);
            requestJson.put("pridata", dataJson);
            for (String key : defaultKey.keySet()) {
                String value = defaultKey.get(key);
                requestJson.put(key, value);
            }
            final HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(requestJson.toString());
            httpRequestParams.setWriteAndreadTimeOut(2);
            final AtomicInteger retryInt = new AtomicInteger(0);
            baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, new Callback() {
                Callback callback = this;

                @Override
                public void onFailure(Call call, IOException e) {
                    Loger.e(TAG, "xescdnLog:onFailure", e);
                    if (retryInt.get() < 10) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HttpRequestParams httpRequestParams = new HttpRequestParams();
                                try {
                                    JSONObject dataJson = requestJson.getJSONObject("data");
                                    dataJson.put("retry", retryInt.get());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                httpRequestParams.setJson(requestJson.toString());
                                httpRequestParams.setWriteAndreadTimeOut(10);
                                baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, callback);
                            }
                        }, retryInt.incrementAndGet() * 1000);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        Loger.d(TAG, "xescdnLog:onResponse:retry=" + retryInt.get() + ",response=" + response.body().string());
                    } else {
                        Loger.d(TAG, "xescdnLog:onResponse:response=null");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void xescdnLog2(HashMap<String, String> defaultKey, JSONObject dataJson) {
        final JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("pridata", dataJson);
            for (String key : defaultKey.keySet()) {
                String value = defaultKey.get(key);
                requestJson.put(key, value);
            }
            final HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(requestJson.toString());
            httpRequestParams.setWriteAndreadTimeOut(10);
            final AtomicInteger retryInt = new AtomicInteger(0);
            baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, new Callback() {
                Callback callback = this;

                @Override
                public void onFailure(Call call, IOException e) {
                    Loger.e(TAG, "xescdnLog:onFailure", e);
                    if (retryInt.get() < 10) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HttpRequestParams httpRequestParams = new HttpRequestParams();
                                try {
                                    JSONObject dataJson = requestJson.getJSONObject("pridata");
                                    dataJson.put("retry", retryInt.get());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                httpRequestParams.setJson(requestJson.toString());
                                httpRequestParams.setWriteAndreadTimeOut(10);
                                baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, callback);
                            }
                        }, retryInt.incrementAndGet() * 1000);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        Loger.d(TAG, "xescdnLog:onResponse:retry=" + retryInt.get() + ",response=" + response.body().string());
                    } else {
                        Loger.d(TAG, "xescdnLog:onResponse:response=null");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpenFailed(int arg1, int arg2) {
        super.onOpenFailed(arg1, arg2);
        handler.removeMessages(1);

        long heartTime = (System.currentTimeMillis() - this.heartTime);
//        HashMap<String, String> defaultKey = new HashMap<>();
//        defaultKey.put("dataType", "601");
//        defaultKey.put("url", "" + mUri);
//        String remoteIp = getRemoteIp();
//        defaultKey.put("sip", "" + remoteIp);
//        JSONObject dataJson = new JSONObject();
//        try {
//            dataJson.put("errorcode", "" + getErrorCode(arg2));
//            AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
//            dataJson.put("errmsg", error == null ? "" : error.getTag());
//            dataJson.put("channelname", "" + channelname);
//            if (lastPlayserverEntity != null) {
//                dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
//                dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
//            }
//            dataJson.put("playlatency", "" + openTime);
//            dataJson.put("cputype", "" + cpuName);
//            dataJson.put("memsize", "" + memsize);
//            dataJson.put("playtype", "" + (isLive ? "1" : "2"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        xescdnLogPlay(defaultKey, dataJson);

        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("ver", logVersion);
        defaultKey.put("serv", serv);
        defaultKey.put("pri", "121");
        defaultKey.put("ts", "" + System.currentTimeMillis());
        defaultKey.put("appid", "xes20001");
        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
        defaultKey.put("agent", "m-android_" + versionName);
        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
        defaultKey.put("arch", "" + cpuName);
        int totalRam = HardWareUtil.getTotalRam();
        defaultKey.put("ram", "" + (totalRam / 1024));
        defaultKey.put("net", "" + getNet());
        defaultKey.put("cpu", "" + getCpuRate());
        defaultKey.put("mem", "" + getMemRate());
        String cip = oldCipdispatch;
        if (StringUtils.isEmpty(cip)) {
            cip = IpAddressUtil.USER_IP;
        }
        defaultKey.put("cip", "" + cip);
        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
        String msip = getRemoteIp();
        defaultKey.put("sip", "" + msip);
        defaultKey.put("tid", "" + UUID.randomUUID());

        JSONObject dataJson = new JSONObject();
        PlayFailCode playFailCode = getErrorCode(arg2);
        try {
            dataJson.put("url", mUri);
            dataJson.put("uri", channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("node", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("code", "" + playFailCode.getCode());
            dataJson.put("msg", "" + playFailCode.getTip());
            long bufferduration = 0;
            long trafficStatisticByteCount = lastTrafficStatisticByteCount;
            if (vPlayer.isInitialized()) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                trafficStatisticByteCount = ijkMediaPlayer.getTrafficStatisticByteCount();
            }
            if (isBuffer) {
                dataJson.put("bufType", "" + bufType);
                dataJson.put("bufDur", "" + (System.currentTimeMillis() - bufferTime));
            }
            dataJson.put("latency", "" + bufferduration);
            long time = System.currentTimeMillis() - frame10Start;
            float averagefps = (float) (((double) (lastDisaplyCount - fistDisaplyCount)) * 1000 / time);
            dataJson.put("avgFps", "" + averagefps);
            dataJson.put("fps", "" + fps);
            dataJson.put("playBuf", "" + bufferduration);
            dataJson.put("hbDur", "" + heartTime);
            dataJson.put("bytes", "" + (trafficStatisticByteCount - lastTrafficStatisticByteCount));
            dataJson.put("uid", "" + userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLog2(defaultKey, dataJson);

        if (playFailCode.getCode() == PlayFailCode.TIME_OUT) {
            startTraceRoute("" + mUri, msip, cip);
        }
    }

    /**
     * TraceRoute 路由
     *
     * @param url  地址
     * @param msip 服务端ip
     * @param cip  客户端ip
     */
    private void startTraceRoute(final String url, final String msip, final String cip) {
        Long time = urlTrace.get(url);
        long now = System.currentTimeMillis();
        if (time != null) {
            if (now - time < 5 * 60 * 1000) {
                return;
            }
        }
        urlTrace.put(url, now);
        try {
            Bundle bundle = new Bundle();
            final URL finalUri = new URL(url);
            ldNetTraceClient.startTraceRoute(finalUri.getHost(), bundle, new LDNetTraceRoute.LDNetTraceRouteListener() {
                @Override
                public void OnNetTraceUpdated(String log) {
                    logger.d("OnNetTraceUpdated:log=" + log);
                }

                @Override
                public void OnNetTraceFinished() {

                }

                @Override
                public void onTraceRouteEnd(JavaTraceResult[] javaTraceResults) {
                    if (javaTraceResults.length > 0) {
                        HashMap<String, String> defaultKey = new HashMap<>();
                        defaultKey.put("ver", logVersion);
                        defaultKey.put("serv", serv);
                        defaultKey.put("pri", "1");
                        defaultKey.put("ts", "" + System.currentTimeMillis());
                        defaultKey.put("appid", "xes20001");
                        defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
                        defaultKey.put("agent", "m-android_" + versionName);
                        defaultKey.put("os", "" + Build.VERSION.SDK_INT);
                        defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
                        defaultKey.put("arch", "" + cpuName);
                        int totalRam = HardWareUtil.getTotalRam();
                        defaultKey.put("ram", "" + (totalRam / 1024));
                        defaultKey.put("net", "" + getNet());
                        defaultKey.put("cpu", "" + getCpuRate());
                        defaultKey.put("mem", "" + getMemRate());
                        defaultKey.put("cip", "" + cip);
                        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                        defaultKey.put("sip", "" + msip);
                        defaultKey.put("tid", "" + UUID.randomUUID());

                        JSONObject dataJson = new JSONObject();
                        try {
                            dataJson.put("url", url);
                            JSONArray traceArray = new JSONArray();
                            for (int i = 0; i < javaTraceResults.length; i++) {
                                JavaTraceResult javaTraceResult = javaTraceResults[i];
                                JSONObject traceObj = new JSONObject();
                                traceObj.put("ttl", javaTraceResult.ttl);
                                traceObj.put("send", 4);
                                traceObj.put("best", "" + javaTraceResult.rttMin);
                                ArrayList<String> times = javaTraceResult.getTimes();
                                if (times.size() > 0) {
                                    traceObj.put("last", "" + times.get(times.size() - 1));
                                } else {
                                    traceObj.put("last", "0");
                                }
                                traceObj.put("worst", "" + javaTraceResult.rttMax);
                                traceObj.put("avrg", "" + javaTraceResult.rttAvg);
                                traceObj.put("loss", 4 - javaTraceResult.receivedpackets);
                                traceObj.put("recv", javaTraceResult.receivedpackets);
                                traceObj.put("sip", javaTraceResult.bothHost);
                                traceArray.put(traceObj);
                            }
                            dataJson.put("trace", traceArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        xescdnLog2(defaultKey, dataJson);
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaybackComplete() {
        super.onPlaybackComplete();
        handler.removeMessages(1);
        send("onPlaybackComplete");
        sip = "";
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        handler.removeMessages(1);
        send("onPlayError");
    }

    public void destory() {
        handler.removeMessages(1);
        send("destory");
        ldNetTraceClient.destory();
    }

    public String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception var4) {

        }
        return versionName;
    }

    public void seekTo(long pos) {
        isSeek = true;
        bufType = 2;
    }

    /** seek完成 */
    public void onSeekComplete() {
        isSeek = false;
        bufType = 1;
    }

    private void getFps() {
        try {
            if (vPlayer.isInitialized() && lastPlayserverEntity != null) {
                if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                    IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                    Bundle bundle = ijkMediaPlayer.getMediaMeta();
                    ArrayList arrayList = bundle.getParcelableArrayList("streams");
                    Set<String> keys = bundle.keySet();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Bundle bundle1 = (Bundle) arrayList.get(i);
                        if ("video".equals(bundle1.getString("type"))) {
                            Loger.d(TAG, "getFps:bundle1=" + bundle1);
                            if (bundle1.containsKey("fps_num") && bundle1.containsKey("fps_den")) {
                                int fps_num = Integer.parseInt(bundle1.getString("fps_num"));
                                int fps_den = Integer.parseInt(bundle1.getString("fps_den"));
                                fps = (float) fps_num / (float) fps_den;
                                Loger.d(TAG, "getFps:fps_num=" + fps_num + ",fps_den=" + fps_den + ",fps=" + fps);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Loger.e(BaseApplication.getContext(), TAG, "getFps", e, true);
        }
    }

    public static PlayFailCode getErrorCode(int arg2) {
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            switch (error) {
                case EHOSTUNREACH:
                    return new PlayFailCode(10, "Failed to resolve hostname");
                case ETIMEDOUT:
                    return new PlayFailCode(PlayFailCode.TIME_OUT, "Connection timed out");
                case ECONNREFUSED:
                    return new PlayFailCode(16, "Connection refuse");
                case EIO:
                    return new PlayFailCode(17, "Io error");
                case HTTP_UNAUTHORIZED:
                    return new PlayFailCode(20, "Server Error");
                case HTTP_FORBIDDEN:
                    return new PlayFailCode(20, "Server Error");
                case HTTP_NOT_FOUND:
                    return new PlayFailCode(20, "Server Error");
                case HTTP_OTHER_4XX:
                    return new PlayFailCode(20, "Server Error");
                case HTTP_SERVER_ERROR:
                    return new PlayFailCode(20, "Server Error");
                case HTTP_BAD_REQUEST:
                    return new PlayFailCode(21, "Client Error");
                case STREAM_NOT_FOUND:
                    return new PlayFailCode(60, "Stream not found");
                case EOF:
                    return new PlayFailCode(61, "End of file");
                case INPUT_CHANGED:
                    return new PlayFailCode(62, "Input changed");
                case INVALIDDATA:
                    return new PlayFailCode(63, "Invalid data found");
                case BUFFER_TOO_SMALL:
                    return new PlayFailCode(64, "Buffer too small");
                case DECODER_NOT_FOUND:
                    return new PlayFailCode(71, "Decoder not found");
                case DEMUXER_NOT_FOUND:
                    return new PlayFailCode(72, "Demuxer not found");
                default:
                    return new PlayFailCode(arg2, "" + error.getTag());
            }
        }
        return new PlayFailCode(arg2, "other");
    }

    private int getNet() {
        int net = 0;
        try {
            String strNetworkType = NetWorkHelper.getNetworkType(activity);
            if (NetWorkHelper.NETWORK_TYPE_2G.equals(strNetworkType)) {
                net = 1;
            } else if (NetWorkHelper.NETWORK_TYPE_3G.equals(strNetworkType)) {
                net = 2;
            } else if (NetWorkHelper.NETWORK_TYPE_4G.equals(strNetworkType)) {
                net = 3;
            } else if (NetWorkHelper.NETWORK_TYPE_WIFI.equals(strNetworkType)) {
                net = 5;
            } else {
                if (!StringUtils.isEmpty(strNetworkType)) {
                    net = 10;
                    Loger.d(BaseApplication.getContext(), TAG, "getNetworkType:strNetworkType=" + strNetworkType, true);
                }
            }
        } catch (Exception e) {
            net = -1024;
            Loger.e(BaseApplication.getContext(), TAG, "getNetworkType", e, true);
        }
        return net;
    }
}
