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
import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.HardWareUtil;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.umsagent.DeviceInfo;
import com.xueersi.xesalib.utils.app.DeviceUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                            frames.add("" + (int) (totalfps / 5));
                            if (frames.size() == 12) {
                                send("frames12");
                            }
                        }
                        if (framesPsTen.size() == 10) {
                            xescdnLogHeart();
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
        stableLogHashMap.put("message", "server: " + lastPlayserverEntity.getAddress() + " vdownload:" + vdownload);
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
            dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
            dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
            dataJson.put("playlatency", "" + openTime);
            dataJson.put("cputype", "" + cpuName);
            dataJson.put("memsize", "" + memsize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xescdnLogPlay(defaultKey, dataJson);
    }

    public void liveGetPlayServer(long delay, int code, String cipdispatch, StringBuilder ipsb) {
        Loger.d(TAG, "liveGetPlayServer:delay=" + delay + ",ipsb=" + ipsb.toString());
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "0");
        defaultKey.put("delay", "" + delay);
        defaultKey.put("code", "" + code);
        defaultKey.put("traceId", "" + UUID.randomUUID());
        defaultKey.put("sip", "" + ipsb);
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
        new Thread() {
            @Override
            public void run() {
                final HashMap<String, String> defaultKey = new HashMap<>();
                double cpuRate = HardWareUtil.getCPURateDesc();
                DecimalFormat df = new DecimalFormat("######0.00");
                defaultKey.put("cpu", "" + df.format(cpuRate));
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory();
                defaultKey.put("mem", "" + totalMemory);
                Loger.d(TAG, "xescdnLogHeart:cpuRate=" + cpuRate + ",totalMemory=" + totalMemory);
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
        defaultKey.put("traceId", "" + UUID.randomUUID());
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("channelname", "" + channelname);
            dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
            dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
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
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("timestamp", "" + System.currentTimeMillis());
        params.addBodyParam("appid", AppConfig.getPsAppId());
        params.addBodyParam("serviceType", "6");
        params.addBodyParam("uid", "" + userId);
        params.addBodyParam("agent", "m-android " + versionName);
        params.addBodyParam("data", dataJson.toString());
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            params.addBodyParam(key, value);
        }
        params.setWriteAndreadTimeOut(2000);
        baseHttpBusiness.sendPostNoBusiness(logurl, params, new Callback() {
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
            dataJson.put("errorcode", "" + arg2);
            AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
            dataJson.put("errmsg", error == null ? "" : error.getTag());
            dataJson.put("channelname", "" + channelname);
            dataJson.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
            dataJson.put("provide", "" + lastPlayserverEntity.getProvide());
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
}
