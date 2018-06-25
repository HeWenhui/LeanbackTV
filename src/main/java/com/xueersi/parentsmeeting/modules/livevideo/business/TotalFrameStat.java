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

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.HardWareUtil;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.string.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
    long frameStart;
    private Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    /** 是不是开始统计 */
    private boolean isStat = true;
    private BaseHttpBusiness baseHttpBusiness;
    private long openStart;
    private String logurl = LiveVideoConfig.URL_CDN_LOG;
    private String userId;
    /** 当前播放的视频地址 */
    private Uri mUri;
    private String sip;
    private String versionName;
    private String cpuName;
    private String memsize;
    private String channelname;
    private int heartCount;

    public TotalFrameStat(final Activity activity) {
        this.activity = activity;
        baseHttpBusiness = new BaseHttpBusiness(activity);
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        userId = myUserInfoEntity.getStuId();
        versionName = getAppVersionName();
        cpuName = HardWareUtil.getCpuName();
        memsize = DeviceUtils.getAvailRams(activity);
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

    public boolean isStat() {
        return isStat;
    }

    public void setStat(boolean stat) {
        isStat = stat;
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
//            if (!isStat) {
//                return;
//            }
            try {
                if (vPlayer.isInitialized() && lastPlayserverEntity != null) {
                    if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                        IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                        float fps = ijkMediaPlayer.getVideoDecodeFramesPerSecond();
                        if (frames.isEmpty()) {
                            frameStart = System.currentTimeMillis();
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
                        if (framesPsTen.size() == 10) {
                            try {
                                xescdnLogHeart();
                            } catch (OutOfMemoryError error) {

                            }
                        }
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
        sip = "";
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
                        sip = args.getString("ip", "");
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onOpenSuccess() {
        super.onOpenSuccess();
        handler.sendEmptyMessageDelayed(1, 1000);
        long openTime = (System.currentTimeMillis() - openStart);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLogPlay(defaultKey, dataJson);
    }

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

    private void xescdnLogHeart() {
        new Thread("xescdnHeart:" + heartCount++) {
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        xescdnLogHeart(defaultKey);
                    }
                });
            }
        }.start();
    }

    private void xescdnLogHeart(HashMap<String, String> defaultKey) {
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
            long bufferduration = 0;
            float averagefps;
            float fps = 0f;
            float bitrate = 0f;
            if (vPlayer.isInitialized()) {
                bufferduration = vPlayer.getPlayer().getVideoCachedDuration();
                if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                    IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                    bitrate = ijkMediaPlayer.getTcpSpeed();
                    fps = ijkMediaPlayer.getVideoDecodeFramesPerSecond();
                }
            }
            float totalfps = 0;
            for (int i = 0; i < framesPsTen.size(); i++) {
                Float f = framesPsTen.get(i);
                totalfps += f;
            }
            averagefps = totalfps / 10;
            framesPsTen.clear();
            dataJson.put("bufferduration", "" + bufferduration);
            dataJson.put("averagefps", "" + averagefps);
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
        JSONObject requestJson = new JSONObject();
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
            HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(requestJson.toString());
            httpRequestParams.setWriteAndreadTimeOut(2);
            baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Loger.e(TAG, "xescdnLog:onFailure", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        Loger.d(TAG, "xescdnLog:onResponse:response=" + response.body().string());
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

    private int getErrorCode(int arg2) {
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
