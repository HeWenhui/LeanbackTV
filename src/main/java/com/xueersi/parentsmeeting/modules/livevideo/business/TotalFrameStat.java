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
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.umsagent.DeviceInfo;
import com.xueersi.xesalib.utils.app.DeviceUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

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
    /** 每秒帧数 */
    private ArrayList<Float> framesPs = new ArrayList<Float>();
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

    public TotalFrameStat(Activity activity) {
        this.activity = activity;
        baseHttpBusiness = new BaseHttpBusiness(activity);
        MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        userId = myUserInfoEntity.getStuId();
        versionName = getAppVersionName();
        cpuName = getCpuName();
        Field[] fields = Build.class.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Loger.d(TAG, "TotalFrameStat:field=" + field.get(null));
            } catch (IllegalAccessException e) {
                Loger.e(TAG, "TotalFrameStat", e);
            }
        }
        memsize = DeviceUtils.getAvailRams(activity);
    }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text;
            while ((text = br.readLine()) != null) {
                if (text.contains("Hardware")) {
                    int index = text.indexOf(":");
                    String cpu;
                    if (index == -1) {
                        cpu = text.substring(8);
                    } else {
                        cpu = text.substring(index + 1);
                    }
                    cpu = cpu.trim();
                    Loger.d(TAG, "getCpuName:text=" + text + ",cpu=" + cpu);
                    return cpu;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //华为手机获取不到,取系统变量
        //三星
        //Build.HARDWARE qcom
        //Build.BOARD msm8998
        //华为
        //Build.HARDWARE kirin970
        //Build.BOARD BLA
        return Build.HARDWARE;
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
                        float totalfps = 0;
                        if (framesPs.size() == 5) {
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
        handler.sendEmptyMessage(1);
        long openTime = (System.currentTimeMillis() - openStart);
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "600");
        defaultKey.put("playlatency", "" + openTime);
        defaultKey.put("cputype", "" + cpuName);
        defaultKey.put("memsize", "" + memsize);
        defaultKey.put("channelname", "" + channelname);
        defaultKey.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
        defaultKey.put("provide", "" + lastPlayserverEntity.getProvide());
        defaultKey.put("errorcode", "0");
        defaultKey.put("errmsg", "");
        xescdnLog(defaultKey);
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

    private void xescdnLog(HashMap<String, String> defaultKey) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("timestamp", "" + System.currentTimeMillis());
        params.addBodyParam("appid", AppConfig.getPsAppId());
        params.addBodyParam("uid", "" + userId);
        params.addBodyParam("os", "" + Build.VERSION.SDK_INT);
        params.addBodyParam("device", "" + DeviceInfo.getDeviceName());
        params.addBodyParam("url", "" + mUri);
        String remoteIp = getRemoteIp();
        params.addBodyParam("sip", "" + remoteIp);
        params.addBodyParam("agent", "m-android " + versionName);
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            params.addBodyParam(key, value);
        }
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
        defaultKey.put("playlatency", "" + openTime);
        defaultKey.put("cputype", "" + cpuName);
        defaultKey.put("memsize", "" + memsize);
        defaultKey.put("channelname", "" + channelname);
        defaultKey.put("appname", "" + lastPlayserverEntity.getServer().getAppname());
        defaultKey.put("provide", "" + lastPlayserverEntity.getProvide());
        defaultKey.put("errorcode", "" + arg2);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        defaultKey.put("errmsg", error == null ? "" : error.getTag());
        xescdnLog(defaultKey);
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
