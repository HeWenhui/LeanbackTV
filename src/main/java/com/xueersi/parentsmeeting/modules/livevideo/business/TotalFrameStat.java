package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.HardWareUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.umsagent.DeviceInfo;
import com.xueersi.xesalib.utils.app.DeviceUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.string.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * Created by linyuqiang on 2018/4/24.
 * 帧数统计
 */
public class TotalFrameStat extends PlayerService.SimpleVPlayerListener {
    private static String TAG = "TotalFrameStat";
    private LiveBll liveBll;
    private PlayerService vPlayer;
    /** 五秒帧数 */
    private ArrayList<String> frames = new ArrayList<>();
    /** 每秒帧数-5秒统计 */
    private ArrayList<Float> framesPs = new ArrayList<Float>();
    /** 每秒帧数-10秒统计 */
    private ArrayList<Float> framesPsTen = new ArrayList<Float>();
    /** 第一次播放的帧数 */
    long fistDisaplyCount = 0;
    /** 上一次播放的帧数 */
    long lastDisaplyCount = 0;
    float fps = 12.0f;
    /** 帧数5秒统计,开始时间 */
    long frameStart;
    /** 帧数10秒统计,开始时间 */
    long frame10Start;
    /** 视频是不是再缓冲 */
    boolean isBuffer = false;
    private Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private BaseHttpBusiness baseHttpBusiness;
    /** 视频开始播放时间 */
    private long openStart;
    /** 视频播放成功时间 */
    private long openSuccess;
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
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private boolean isLive = true;

    public TotalFrameStat(final Activity activity, boolean isLive) {
        this.activity = activity;
        baseHttpBusiness = new BaseHttpBusiness(activity);
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
                        if (frames.isEmpty()) {
                            frameStart = System.currentTimeMillis() - 1000;
                        }
                        framesPs.add(fps);
                        framesPsTen.add(fps);
                        if (framesPs.size() == 5) {
                            float totalfps = 0;
                            for (int i = 0; i < framesPs.size(); i++) {
                                Float f = framesPs.get(i);
                                totalfps += f;
                            }
                            framesPs.clear();
                            frames.add("" + (int) (totalfps));
                            if (frames.size() == 12) {
                                send("frames12");
                            }
                        }
                        Loger.d(TAG, "handleMessage:fps=" + fps + ",disaplyCount=" + disaplyCount + "," + (disaplyCount - lastDisaplyCount));
                        if (framesPsTen.size() == 10) {
                            ArrayList<Float> framesPsTenTemp = new ArrayList<Float>(framesPsTen);
                            framesPsTen.clear();
                            long bufferduration = 0;
                            float bitrate = 0f;
                            try {
                                if (vPlayer.isInitialized()) {
                                    bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                                    bitrate = ijkMediaPlayer.getTcpSpeed() * 8 / 1000;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            long time = System.currentTimeMillis() - frame10Start;
                            Loger.d(TAG, "handleMessage:fps=" + (disaplyCount - fistDisaplyCount) / 10 + ",time=" + time);
                            float averagefps = (float) (((double) (disaplyCount - fistDisaplyCount)) * 1000 / time);
                            xescdnLogHeart(framesPsTenTemp, averagefps, bufferduration, bitrate);
                            fistDisaplyCount = disaplyCount;
                            frame10Start = System.currentTimeMillis();
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
        //不是直播，不统计心跳
        if (!isLive) {
            return;
        }
        Loger.d(TAG, "send:method=" + method + ",frames=" + frames.size());
        if (frames.isEmpty()) {
            return;
        }
        StringBuilder vdownload = new StringBuilder();
        for (int i = 0; i < frames.size(); i++) {
            vdownload.append(frames.get(i));
            if (i != frames.size() - 1) {
                vdownload.append(",");
            }
        }
        frames.clear();
        framesPs.clear();
        long time = System.currentTimeMillis() - frameStart;
        StableLogHashMap stableLogHashMap = new StableLogHashMap("glsbSpeed");
        stableLogHashMap.put("activity", activity.getClass().getSimpleName());
        stableLogHashMap.put("method", method);
        stableLogHashMap.put("time", "" + time);
        if (lastPlayserverEntity != null) {
            stableLogHashMap.put("message", "server: " + lastPlayserverEntity.getAddress() + " vdownload:" + vdownload);
        } else {
            stableLogHashMap.put("message", "server: null" + " vdownload:" + vdownload);
        }
        Loger.e(activity, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData(), true);
    }

    public void onPause() {
        handler.removeMessages(1);
        send("onPause");
    }

    public void onReplay() {
        handler.removeMessages(1);
        send("onReplay");
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
        frame10Start = System.currentTimeMillis();
        openSuccess = System.currentTimeMillis();
        handler.sendEmptyMessageDelayed(1, 1000);
        long openTime = (System.currentTimeMillis() - openStart);
        long onNativeInvokeTime = (System.currentTimeMillis() - onNativeInvoke);
        Loger.d(TAG, "onOpenSuccess:openTime=" + openTime + ",Invoke=" + onNativeInvokeTime + ",sipMap=" + sipMap.size() + ",sip=" + sip);
        getFps();
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "600");
        defaultKey.put("url", "" + mUri);
        String remoteIp = getRemoteIp();
        defaultKey.put("sip", "" + remoteIp);
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("errorcode", "0");
            dataJson.put("errmsg", "");
            dataJson.put("channelname", "" + channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
                dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("playlatency", "" + openTime);
            dataJson.put("cputype", "" + cpuName);
            dataJson.put("memsize", "" + memsize);
            dataJson.put("playtype", "" + (isLive ? "1" : "2"));
        } catch (Exception e) {
            Loger.e(BaseApplication.getContext(), TAG, "onOpenSuccess", e, true);
        }
        xescdnLogPlay(defaultKey, dataJson);
    }

    @Override
    public void onBufferStart() {
        super.onBufferStart();
        isBuffer = true;
        Loger.d(TAG, "onBufferStart:isInitialized=" + vPlayer.isInitialized());
    }

    @Override
    public void onBufferComplete() {
        super.onBufferComplete();
        isBuffer = false;
        Loger.d(TAG, "onBufferComplete:isInitialized=" + vPlayer.isInitialized());
    }

    String oldCipdispatch = "";

    public void liveGetPlayServer(long delay, int code, String cipdispatch, StringBuilder ipsb, String url) {
        Loger.d(TAG, "liveGetPlayServer:delay=" + delay + ",ipsb=" + ipsb.toString());
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "0");
        defaultKey.put("delay", "" + delay);
        defaultKey.put("code", "" + code);
        defaultKey.put("traceId", "" + UUID.randomUUID());
        defaultKey.put("sip", "" + ipsb);
        defaultKey.put("url", "" + url);
        JSONObject dataJson = new JSONObject();
        if (StringUtils.isEmpty(cipdispatch)) {
            cipdispatch = oldCipdispatch;
        } else {
            oldCipdispatch = cipdispatch;
        }
        try {
            dataJson.put("cipdispatch", "" + cipdispatch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLogPlay(defaultKey, dataJson);
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

    private void xescdnLogHeart(final ArrayList<Float> framesPsTen, final float averagefps, final long bufferduration, final float bitrate) {
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final HashMap<String, String> defaultKey = new HashMap<>();
                double cpuRate = HardWareUtil.getCPURateDesc();
                DecimalFormat df = new DecimalFormat("######0.00");
                defaultKey.put("cpu", "" + df.format(cpuRate));
                long availMemory = HardWareUtil.getAvailMemory(activity) / 1024;
                int totalRam = HardWareUtil.getTotalRam();
                double memRate = (double) ((totalRam - availMemory) * 100) / (double) totalRam;
                defaultKey.put("mem", "" + df.format(memRate));
                Loger.d(TAG, "xescdnLogHeart:cpuRate=" + cpuRate + ",availMemory=" + availMemory);
                float totalfps = 0;
                for (int i = 0; i < framesPsTen.size(); i++) {
                    Float f = framesPsTen.get(i);
                    totalfps += f;
                }
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
                defaultKey.put("net", "" + net);
                float averagefps2 = totalfps / 10f;
                Loger.d(TAG, "xescdnLogHeart:averagefps=" + averagefps + "," + averagefps2);
                xescdnLogHeart(defaultKey, averagefps, averagefps2, bufferduration, bitrate);
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
            requestJson.put("data", dataJson);
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

    @Override
    public void onOpenFailed(int arg1, int arg2) {
        super.onOpenFailed(arg1, arg2);
        handler.removeMessages(1);
        send("onOpenFailed");
        long openTime = (System.currentTimeMillis() - openStart);
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "601");
        defaultKey.put("url", "" + mUri);
        String remoteIp = getRemoteIp();
        defaultKey.put("sip", "" + remoteIp);
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("errorcode", "" + getErrorCode(arg2));
            AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
            dataJson.put("errmsg", error == null ? "" : error.getTag());
            dataJson.put("channelname", "" + channelname);
            if (lastPlayserverEntity != null) {
                dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
                dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
            }
            dataJson.put("playlatency", "" + openTime);
            dataJson.put("cputype", "" + cpuName);
            dataJson.put("memsize", "" + memsize);
            dataJson.put("playtype", "" + (isLive ? "1" : "2"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLogPlay(defaultKey, dataJson);
        sip = "";
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

    public static int getErrorCode(int arg2) {
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            switch (error) {
                case DECODER_NOT_FOUND:
                    return 10000;
                case DEMUXER_NOT_FOUND:
                    return 10001;
                case EIO:
                    return 10002;
                case STREAM_NOT_FOUND:
                    return 10003;
                case INPUT_CHANGED:
                    return 10004;
                case INVALIDDATA:
                    return 10005;
                case BUFFER_TOO_SMALL:
                    return 10006;
                case ETIMEDOUT:
                    return 10007;
                case HTTP_BAD_REQUEST:
                    return 20002;
                case HTTP_UNAUTHORIZED:
                    return 20003;
                case HTTP_FORBIDDEN:
                    return 20004;
                case HTTP_NOT_FOUND:
                    return 20005;
                case HTTP_OTHER_4XX:
                    return 20006;
                case HTTP_SERVER_ERROR:
                    return 20007;
                default:
            }
        }
        return 0;
    }
}
