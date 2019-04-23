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
import android.os.SystemClock;
import android.text.TextUtils;

import com.netease.LDNetDiagnoClient.LDNetTraceClient;
import com.netease.LDNetDiagnoService.JavaTraceResult;
import com.netease.LDNetDiagnoService.LDNetTraceRoute;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.lib.framework.utils.DeviceUtils;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.DNSUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.FileStringUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.HardWareUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.ex.HttpException;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

//import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/9/4.
 * 直播播放日志
 */
public class LivePlayLog extends VPlayerCallBack.SimpleVPlayerListener {

    private static String TAG = "LivePlayLog";
    private Logger logger = LoggerFactory.getLogger(TAG);
    private PlayerService vPlayer;
    /** 每秒帧数-10秒统计 */
    private ArrayList<Float> framesPsTen = new ArrayList<Float>();
    /** 第一次播放的帧数 */
    private long fistDisaplyCount = 0;
    /** 上一次播放的帧数 */
    private long lastDisaplyCount = 0;
    private float videofps = 12.0f;
    /** 帧数10秒统计,开始时间 */
    private long frame10Start;
    private long lastTrafficStatisticByteCount;
    private long trafficStatisticByteCount;
    /** 视频是不是再缓冲 */
    private boolean isBuffer = false;
    /** 视频是不是暂停 */
    private boolean isPause = false;
    /** 视频是不是暂停 */
    private boolean isHavePause = false;
    /** 视频是不是销毁 */
    private boolean isDestory = false;
    /** 缓冲开始时间 */
    private long bufferTime = 0;
    /** 缓冲类型 */
    private int bufType = 0;
    private boolean isSeek = false;
    private Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private BaseHttpBusiness baseHttpBusiness;
    /** 视频开始播放时间 */
    private long openStart;
    /** 视频播放成功时间 */
    private long openSuccess;
    /** 视频播放成功时间 */
    private boolean isOpenSuccess;
    /** 心跳时间 */
    private long lastHeartTime = 0;
    /** 直播云平台日志统计 */
    private String logurl = LiveVideoConfig.URL_CDN_LOG;
    /** 直播云平台日志统计-多个的位置 */
    int logIndex = 0;
    /** 直播云平台日志统计-多个 */
    private String[] logurls = {LiveVideoConfig.URL_CDN_LOG, LiveVideoConfig.URL_CDN_LOG1, LiveVideoConfig.URL_CDN_LOG2};
    private String userId;
    private String psId;
    /** 当前播放的视频地址 */
    private Uri mUri;
    private String mUriHost = "";
    static HashMap<Uri, String> sipMap = new HashMap<>();
    private String sip;
    private String versionName;
    /** cpu名字 */
    private String cpuName;
    /** 可用内存大小 */
    private String memsize;
    private String channelname = "";
    private int heartCount;
    private LDNetTraceClient ldNetTraceClient;
    /** 一个地址5分钟传一次 */
    private HashMap<String, Long> urlTrace = new HashMap<>();
    private LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    /** 保存日志路径 */
    private File saveLogDir;
    private File saveLogDirDebug;
    private boolean isLive = true;
    /** 下载完成 */
    private boolean isDownCom = false;
    private DecimalFormat df = new DecimalFormat("######0.00");
    private int logVersion = 1;
    private int serv;
    private static SimpleDateFormat dateFormat;
    private HashMap<String, Integer> priMap = new HashMap<>();
    private String PRI_KEY_RENDERING = "MEDIA_INFO_VIDEO_RENDERING_START";
    private String PRI_KEY_onOpenFailed = "onOpenFailed";
    private String PRI_KEY_onBufferStart = "onBufferStart";
    private String PRI_KEY_onBufferComplete = "onBufferComplete";
    private String PRI_KEY_HEART = "HEART";
    private String tid = "";
    private String temTid = "undefined";
    //    private HashMap<String, String> tidAndPri = new HashMap<>();
    private PlayBufferEntity bufferStartEntity = new PlayBufferEntity();
    /** 文件 */
    private boolean isNewIJK;

    /** 文件 */
    private File liveLog920;
    /** 日志上传类型 */
    private String LIVE_920_TYPE = "";

    static {
        dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault());
    }

    public LivePlayLog(final Activity activity, boolean isLive) {
        isNewIJK = MediaPlayer.getIsNewIJK();
        if (!isNewIJK) {
            logger.setLogMethod(false);
            if (isLive) {
                serv = 120;
                priMap.put(PRI_KEY_RENDERING, 120);
                priMap.put(PRI_KEY_onOpenFailed, 121);
                priMap.put(PRI_KEY_onBufferStart, 122);
                priMap.put(PRI_KEY_onBufferComplete, 123);
                priMap.put(PRI_KEY_HEART, 124);
            } else {
                serv = 220;
                priMap.put(PRI_KEY_RENDERING, 220);
                priMap.put(PRI_KEY_onOpenFailed, 221);
                priMap.put(PRI_KEY_onBufferStart, 222);
                priMap.put(PRI_KEY_onBufferComplete, 223);
                priMap.put(PRI_KEY_HEART, 224);
            }
            this.activity = activity;
            baseHttpBusiness = new BaseHttpBusiness(activity);
            ldNetTraceClient = new LDNetTraceClient(activity);
            MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
            userId = myUserInfoEntity.getStuId();
            psId = UserBll.getInstance().getMyUserInfoEntity().getPsimId();
            versionName = getAppVersionName();
            cpuName = HardWareUtil.getCpuName();
            memsize = DeviceUtils.getAvailRams(activity);
            this.isLive = isLive;
            saveLogDir = LiveCacheFile.geCacheFile(activity, "liveplaylog");
            saveLogDirDebug = LiveCacheFile.geCacheFile(activity, "liveplaylogdebug");
            liveLog920 = LiveCacheFile.geCacheFile(activity, "liveLog920");
        }
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                uploadOld();
//            }
//        }, 20000);
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
//                        logger.d( "testCpu:cpuRate=" + cpuRate + ",totalMemory=" + totalMemory + ",CPURateDesc=" + df.format(CPURateDesc));
//                    }
//                }
//            }.start();
//        }
    }

    public void setLive(boolean live) {
        if (!isNewIJK) {
            if (this.isLive != live) {
                isLive = live;
                if (isLive) {
                    serv = 120;
                    priMap.put(PRI_KEY_RENDERING, 120);
                    priMap.put(PRI_KEY_onOpenFailed, 121);
                    priMap.put(PRI_KEY_onBufferStart, 122);
                    priMap.put(PRI_KEY_onBufferComplete, 123);
                    priMap.put(PRI_KEY_HEART, 124);
                } else {
                    serv = 220;
                    priMap.put(PRI_KEY_RENDERING, 220);
                    priMap.put(PRI_KEY_onOpenFailed, 221);
                    priMap.put(PRI_KEY_onBufferStart, 222);
                    priMap.put(PRI_KEY_onBufferComplete, 223);
                    priMap.put(PRI_KEY_HEART, 224);
                }
            }
        }
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
            if (!isNewIJK) {
                //不是直播，不统计心跳
//            if (!isLive) {
//                return;
//            }
                try {
                    if (vPlayer.isInitialized()) {
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
                            logger.d("handleHeartMessage:fps=" + fps + ",disaplyCount=" + disaplyCount + "," + (disaplyCount - lastDisaplyCount));
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
                                long time = SystemClock.elapsedRealtime() - frame10Start;
                                float averagefps = (float) (((double) (disaplyCount - fistDisaplyCount)) * 1000 / time);
                                logger.d("handleHeartMessage:fps=" + (disaplyCount - fistDisaplyCount) / 15 + ",averagefps=" + averagefps + ",time=" + time);
                                if (lastHeartTime == 0) {
                                    time = 15000;
                                } else {
                                    time = SystemClock.elapsedRealtime() - lastHeartTime;
                                }
                                xescdnLogHeart(framesPsTenTemp, averagefps, bufferduration, bitrate, trafficStatisticByteCount - lastTrafficStatisticByteCount, time);
                                if (TextUtils.equals(bufferStartEntity.getTip(), tid)) {
                                    if (bufferStartEntity.getStartTime() >= frame10Start && bufferStartEntity.getEndTime() < System.currentTimeMillis()) {
                                        float bufferTime = (videofps - averagefps) * time / videofps;
                                        float bufferTime2 = bufferStartEntity.getEndTime() - bufferStartEntity.getStartTime();
                                        logger.d("handleHeartMessage:bufferTime=" + bufferTime + ",bufferTime2=" + bufferTime2 + ",time=" + time);
                                    }
                                }
                                fistDisaplyCount = disaplyCount;
                                lastTrafficStatisticByteCount = trafficStatisticByteCount;
                                LivePlayLog.this.trafficStatisticByteCount = lastTrafficStatisticByteCount;
                                lastHeartTime = frame10Start = SystemClock.elapsedRealtime();
                            } else {
                                try {
                                    if (vPlayer.isInitialized()) {
                                        trafficStatisticByteCount = ijkMediaPlayer.getTrafficStatisticByteCount();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            lastDisaplyCount = disaplyCount;
                            if (!isLive) {
                                if (!isDownCom) {
                                    long currentPosition = ijkMediaPlayer.getCurrentPosition();
                                    long duration = ijkMediaPlayer.getDuration();
                                    if (currentPosition > duration / 2) {
                                        long bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                                        if (currentPosition + bufferduration + 500 > duration) {
                                            isDownCom = true;
                                            downCom();
                                        }
                                    }
                                }
                            }
//                        if (lastFps != 0) {
//                            frames.add("" + ((int) ((lastFps + fps) * 5 / 2)));
//                        } else {
//                            frames.add("" + ((int) (fps * 5)));
//                        }
//                        lastFps = fps;
                        }
                    } else {
                        framesPsTen.add(0.0f);
                        logger.d("handleHeartMessage:isInitialized=false");
                    }
                } catch (Exception e) {
                    UmsAgentManager.umsAgentException(BaseApplication.getContext(), TAG + "handleHeartMessage", e);
                }
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    private void downCom() {
        if (!isNewIJK) {
            logger.d("downCom");
            HashMap<String, Object> defaultKey = new HashMap<>();
            defaultKey.put("ver", logVersion);
            defaultKey.put("serv", serv);
            defaultKey.put("pri", 225);
            addDefault(defaultKey);

            defaultKey.put("cpu", getCpuRate());
            defaultKey.put("mem", getMemRate());
            String cip = oldCipdispatch;
            defaultKey.put("cip", "" + cip);
            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
            defaultKey.put("sip", "" + getRemoteIp(""));
            defaultKey.put("tid", "" + tid);

            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("url", "" + mUri);
                dataJson.put("uri", channelname);
                if (lastPlayserverEntity != null) {
                    dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                } else {
                    dataJson.put("node", "xrs_back");
                }
                long heartTime;
                if (lastHeartTime == 0) {
                    heartTime = 0;
                } else {
                    heartTime = (SystemClock.elapsedRealtime() - this.lastHeartTime);
                }
                dataJson.put("hbDur", heartTime);
                dataJson.put("dnDur", heartTime);
                long bytes = trafficStatisticByteCount - lastTrafficStatisticByteCount;
                if (bytes < 0) {
                    bytes = 0;
                    logger.d("downCom:now=" + trafficStatisticByteCount + ",last=" + lastTrafficStatisticByteCount);
                }
                dataJson.put("bytes", bytes);
                dataJson.put("uid", "" + userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            xescdnLog2(defaultKey, dataJson, false);
        }
    }

    private void send(String method, int code, long dur) {
        if (!isNewIJK) {
            logger.d("send:method=" + method + ",framesPsTen=" + framesPsTen.size() + ",tid=" + tid);
            framesPsTen.clear();
//        if (StringUtils.isEmpty(tid)) {
//            return;
//        }
            if (vPlayer == null) {
                return;
            }
            HashMap<String, Object> defaultKey = new HashMap<>();
            defaultKey.put("ver", logVersion);
            defaultKey.put("serv", serv);
            defaultKey.put("pri", priMap.get(PRI_KEY_onOpenFailed));
            addDefault(defaultKey);
            defaultKey.put("cpu", getCpuRate());
            defaultKey.put("mem", getMemRate());
            String cip = oldCipdispatch;
            defaultKey.put("cip", "" + cip);
            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
            defaultKey.put("sip", "" + getRemoteIp(""));
            defaultKey.put("tid", "" + tid);

            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("url", "" + mUri);
                dataJson.put("uri", channelname);
                if (lastPlayserverEntity != null) {
                    dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                } else {
                    dataJson.put("node", "xrs_back");
                }
                dataJson.put("code", code);
                dataJson.put("msg", "Success");
                dataJson.put("method", activity.getClass().getSimpleName() + "-" + method);
                long bufferduration = dur;
                if (vPlayer.isInitialized()) {
//                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
//                bufferduration = ijkMediaPlayer.getVideoCachedDuration();
//                logger.d("send:method=" + method + ",bufferduration=" + bufferduration);
                }
                dataJson.put("bufType", bufType);
                if (isBuffer) {
                    dataJson.put("bufDur", (System.currentTimeMillis() - bufferTime));
                } else {
                    dataJson.put("bufDur", 0);
                }
                dataJson.put("latency", bufferduration);
                long time = SystemClock.elapsedRealtime() - frame10Start;
                float averagefps = (float) (((double) (lastDisaplyCount - fistDisaplyCount)) * 1000 / time);
                dataJson.put("avgFps", averagefps);
                dataJson.put("fps", videofps);
                dataJson.put("playBuf", bufferduration);
                long heartTime;
                if (lastHeartTime == 0) {
                    heartTime = 0;
                } else {
                    heartTime = (SystemClock.elapsedRealtime() - this.lastHeartTime);
                }
                dataJson.put("hbDur", heartTime);
                long bytes = trafficStatisticByteCount - lastTrafficStatisticByteCount;
                if (bytes < 0) {
                    bytes = 0;
                    logger.d("send:now=" + trafficStatisticByteCount + ",last=" + lastTrafficStatisticByteCount);
                }
                dataJson.put("bytes", bytes);
//            dataJson.put("allpri", "" + tidAndPri.get(tid));
                dataJson.put("uid", "" + userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            xescdnLog2(defaultKey, dataJson, false);
        }
    }

    public void onPause(long dur) {
        if (!isNewIJK) {
            logger.d("onPause:isDestory=" + isDestory);
            isPause = true;
            isHavePause = true;
            handler.removeMessages(1);
            if (!isDestory) {
                if (mUri != null) {
                    send("onPause", 0, dur);
                }
            }
        }
    }

    public void onReplay() {
        if (!isNewIJK) {
            isPause = false;
            handler.removeMessages(1);
        }
//        send("onReplay");
    }

    @Override
    public void onOpenStart() {
        if (!isNewIJK) {
            super.onOpenStart();
            if (!isLive) {
                tid = "" + UUID.randomUUID();
            } else {
                if (isHavePause) {
                    tid = "" + UUID.randomUUID();
                    isHavePause = false;
                }
            }
            //体验课为空
            if (StringUtils.isEmpty(tid)) {
                tid = "" + UUID.randomUUID();
            }
            temTid = tid;
            isOpenSuccess = false;
            framesPsTen.clear();
            handler.removeMessages(1);
            lastHeartTime = 0;
            openStart = System.currentTimeMillis();
            mUri = vPlayer.getUri();
            if (mUri != null) {
                mUriHost = DNSUtil.getHost(mUri.toString());
            }
            if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                ijkMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
                    @Override
                    public boolean onNativeInvoke(int what, Bundle args) {
                        logger.d("onOpenStart:what=" + what + "," + mUri + ",args=" + args);
                        if (what == CTRL_DID_TCP_OPEN) {
                            sip = args.getString("ip", "0.0.0.0");
                            sipMap.put(mUri, sip);
                            long openTime = (System.currentTimeMillis() - openSuccess);
                            logger.d("onOpenStart:what=" + what + "," + mUri + ",openTime=" + openTime);
                        }
                        return false;
                    }
                });
                ijkMediaPlayer.setOnInfoListener2(new IMediaPlayer.OnInfoListener() {
                    boolean haveStart = false;

                    @Override
                    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                        logger.d("onInfo:what=" + what + "," + extra);
                        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            if (haveStart) {
                                return false;
                            }
                            haveStart = true;
                            final long openTime = (System.currentTimeMillis() - openStart);
                            logger.d("onInfo:what=3," + (System.currentTimeMillis() - openSuccess));
                            getFps();
                            final String finalTid = tid;
                            liveThreadPoolExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    URLDNS urldns = new URLDNS();
                                    try {
                                        DNSUtil.getDns(urldns, mUriHost);
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    }

                                    HashMap<String, Object> defaultKey = new HashMap<>();
                                    defaultKey.put("ver", logVersion);
                                    defaultKey.put("serv", serv);
                                    defaultKey.put("pri", priMap.get(PRI_KEY_RENDERING));
                                    addDefault(defaultKey);
                                    defaultKey.put("cpu", getCpuRate());
                                    defaultKey.put("mem", getMemRate());
                                    String cip = oldCipdispatch;
                                    defaultKey.put("cip", "" + cip);
                                    defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                                    String hostIp = getRemoteIp(urldns.ip);
                                    defaultKey.put("sip", "" + hostIp);
                                    defaultKey.put("tid", "" + finalTid);

                                    JSONObject dataJson = new JSONObject();
                                    try {
                                        dataJson.put("url", "" + mUri);
                                        dataJson.put("uri", channelname);
                                        if (lastPlayserverEntity != null) {
                                            dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                                        } else {
                                            dataJson.put("node", "xrs_back");
                                        }
                                        dataJson.put("code", 0);
                                        dataJson.put("msg", "Success");
                                        dataJson.put("dns", urldns.time);
                                        dataJson.put("delay", openTime);
                                        dataJson.put("uid", "" + userId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    xescdnLog2(defaultKey, dataJson, false);
                                }
                            });
                        }
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onOpenSuccess() {
        if (!isNewIJK) {
            super.onOpenSuccess();
            sip = sipMap.get(mUri);
            lastDisaplyCount = fistDisaplyCount = 0;
            lastTrafficStatisticByteCount = 0;
            frame10Start = SystemClock.elapsedRealtime();
            openSuccess = System.currentTimeMillis();
            handler.sendEmptyMessageDelayed(1, 1000);
            long openTime = (System.currentTimeMillis() - openStart);
            logger.d("onOpenSuccess:openTime=" + openTime + ",sipMap=" + sipMap.size() + ",sip=" + sip);
            isOpenSuccess = true;
        }
    }

    public void stopPlay() {
        if (!isNewIJK) {
            isHavePause = true;
            handler.removeMessages(1);
            send("stopPlay", 0, 0);
        }
    }

    public void onBufferTimeOut() {
        if (!isNewIJK) {
            handler.removeMessages(1);
            send("onBufferTimeOut", 15, 0);
        }
    }

    @Override
    public void onBufferStart() {
        if (!isNewIJK) {
            super.onBufferStart();
            isBuffer = true;
            if (isSeek) {
                bufType = 2;
            } else {
                bufType = 1;
            }
            bufferTime = System.currentTimeMillis();
            logger.d("onBufferStart:isInitialized=" + vPlayer.isInitialized());
            bufferStartEntity.setTip(tid);
            bufferStartEntity.setStartTime(bufferTime);
            HashMap<String, Object> defaultKey = new HashMap<>();
            defaultKey.put("ver", logVersion);
            defaultKey.put("serv", serv);
            defaultKey.put("pri", priMap.get(PRI_KEY_onBufferStart));
            addDefault(defaultKey);
            defaultKey.put("cpu", getCpuRate());
            defaultKey.put("mem", getMemRate());
            String cip = oldCipdispatch;
            defaultKey.put("cip", "" + cip);
            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
            String msip = getRemoteIp("");
            defaultKey.put("sip", "" + msip);
            defaultKey.put("tid", "" + tid);

            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("url", "" + mUri);
                dataJson.put("uri", channelname);
                ;
                if (lastPlayserverEntity != null) {
                    dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                } else {
                    dataJson.put("node", "xrs_back");
                }
                dataJson.put("bufType", bufType);
                dataJson.put("uid", "" + userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            xescdnLog2(defaultKey, dataJson, false);
            startTraceRoute("" + mUriHost, msip, cip, false);
        }
    }

    @Override
    public void onBufferComplete() {
        if (!isNewIJK) {
            super.onBufferComplete();
            isBuffer = false;
            bufferStartEntity.setEndTime(System.currentTimeMillis());
            logger.d("onBufferComplete:isInitialized=" + vPlayer.isInitialized());
            HashMap<String, Object> defaultKey = new HashMap<>();
            defaultKey.put("ver", logVersion);
            defaultKey.put("serv", serv);
            defaultKey.put("pri", priMap.get(PRI_KEY_onBufferComplete));
            addDefault(defaultKey);
            defaultKey.put("cpu", getCpuRate());
            defaultKey.put("mem", getMemRate());
            String cip = oldCipdispatch;
            defaultKey.put("cip", "" + cip);
            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
            String msip = getRemoteIp("");
            defaultKey.put("sip", "" + msip);
            defaultKey.put("tid", "" + tid);

            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("url", "" + mUri);
                dataJson.put("uri", channelname);
                if (lastPlayserverEntity != null) {
                    dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                } else {
                    dataJson.put("node", "xrs_back");
                }
                dataJson.put("bufType", bufType);
                dataJson.put("bufDur", (System.currentTimeMillis() - bufferTime));
                dataJson.put("uid", "" + userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bufType = 0;
            xescdnLog2(defaultKey, dataJson, false);
        }
    }

    String oldCipdispatch = "";

//    private String getMemRate() {
//        int totalRam = HardWareUtil.getTotalRam();
//        long availMemory = HardWareUtil.getAvailMemory(activity) / 1024;
//        double memRate = (double) ((totalRam - availMemory) * 100) / (double) totalRam;
//        return "" + df.format(memRate);
//    }

    private double getMemRate() {
        double memRate = 0;
        if (!isNewIJK) {
            int totalRam = HardWareUtil.getTotalRam();
            if (totalRam == 0) {
                return 0;
            }
            long availMemory = HardWareUtil.getAvailMemory(activity) / 1024;
            memRate = (double) ((totalRam - availMemory) * 100) / (double) totalRam;
            boolean error = false;
            if (memRate > 100) {
                memRate = 100;
                error = true;
            } else if (memRate < 0) {
                memRate = 0;
                error = false;
            }
            if (error) {
                logger.d("getMemRate:totalRam=" + totalRam + ",availMemory=" + availMemory);
            }
        }
        return memRate;
    }

    private double getCpuRate() {
        double cpuRate = 0;
        if (!isNewIJK) {
            cpuRate = HardWareUtil.getCPURateDesc();
        }
        return cpuRate;
    }

//    private String getCpuRate() {
//        double cpuRate = HardWareUtil.getCPURateDesc();
//        return "" + df.format(cpuRate);
//    }

    /**
     * 设置默认参数
     *
     * @param defaultKey
     */
    private void addDefault(HashMap<String, Object> defaultKey) {
        if (!isNewIJK) {
            defaultKey.put("ts", System.currentTimeMillis());
            defaultKey.put("appId", "" + UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
            defaultKey.put("psId", "" + psId);
            defaultKey.put("agent", "m-android_" + versionName);
            defaultKey.put("os", "" + Build.VERSION.SDK_INT);
            defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
            defaultKey.put("arch", "" + cpuName);
            defaultKey.put("net", getNet());
            int totalRam = HardWareUtil.getTotalRam();
            defaultKey.put("ram", totalRam);
        }
    }

    public void liveGetPlayServer(final long delay, PlayFailCode playFailCode, int code, String cipdispatch, URLDNS urldns, final String url) {
        if (!isNewIJK) {
            tid = "" + UUID.randomUUID();
            logger.d("liveGetPlayServer:delay=" + delay + ",ipsb=" + urldns.ip);
            HashMap<String, Object> defaultKey = new HashMap<>();
            defaultKey.put("ver", logVersion);
            defaultKey.put("serv", serv);
            defaultKey.put("pri", 0);
            addDefault(defaultKey);
            defaultKey.put("cpu", getCpuRate());
            defaultKey.put("mem", getMemRate());
            if (StringUtils.isEmpty(cipdispatch)) {
                cipdispatch = oldCipdispatch;
            }
            oldCipdispatch = cipdispatch;
            defaultKey.put("cip", "" + cipdispatch);
            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
            defaultKey.put("sip", "" + urldns.ip);
            defaultKey.put("tid", "" + tid);

            JSONObject dataJson = new JSONObject();
            try {
                dataJson.put("url", url);
                dataJson.put("code", code);
                if (code == 0) {
                    dataJson.put("msg", "Success");
                } else {
                    dataJson.put("msg", playFailCode.getTip());
                }
                dataJson.put("dns", urldns.time);
                dataJson.put("delay", delay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            xescdnLog2(defaultKey, dataJson, false);

            if (code == PlayFailCode.TIME_OUT) {
                startTraceRoute("" + url, urldns.ip, cipdispatch, false);
            }
        }
    }

    private String getRemoteIp(String defaultIp) {

        String remoteIp = "";
        if (!isNewIJK) {
            if (lastPlayserverEntity != null) {
                String ipAddress = lastPlayserverEntity.getIpAddress();
                if (StringUtils.isEmpty(ipAddress)) {
                    if (StringUtils.isEmpty(sip)) {
                        if (StringUtils.isEmpty(defaultIp)) {
                            remoteIp = lastPlayserverEntity.getAddress();
                        } else {
                            remoteIp = defaultIp;
                            sip = defaultIp;
                        }
                    } else {
                        remoteIp = sip;
                    }
                } else {
                    remoteIp = ipAddress;
                }
            } else {
                remoteIp = sip;
            }
        }
        return remoteIp;
    }

    private void xescdnLogHeart(final ArrayList<Float> framesPsTen, final float averagefps, final long bufferduration, final float bitrate, final long bytes, final long time) {
        if (!isNewIJK) {
            final String finalTid = tid;
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
//                logger.d( "xescdnLogHeart:cpuRate=" + cpuRate + ",availMemory=" + availMemory);
//                float totalfps = 0;
//                for (int i = 0; i < framesPsTen.size(); i++) {
//                    Float f = framesPsTen.get(i);
//                    totalfps += f;
//                }
//                defaultKey.put("net", "" + getNet());
//                float averagefps2 = totalfps / 10f;
//                logger.d( "xescdnLogHeart:averagefps=" + averagefps + "," + averagefps2);
//                xescdnLogHeart(defaultKey, averagefps, averagefps2, bufferduration, bitrate);

                    HashMap<String, Object> defaultKey = new HashMap<>();
                    defaultKey.put("ver", logVersion);
                    defaultKey.put("serv", serv);
                    defaultKey.put("pri", priMap.get(PRI_KEY_HEART));
                    addDefault(defaultKey);
                    defaultKey.put("cpu", getCpuRate());
                    defaultKey.put("mem", getMemRate());
                    String cip = oldCipdispatch;
                    defaultKey.put("cip", "" + cip);
                    defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                    String remoteIp = getRemoteIp("");
                    defaultKey.put("sip", "" + remoteIp);
                    defaultKey.put("tid", "" + finalTid);

                    JSONObject dataJson = new JSONObject();
                    try {
                        dataJson.put("url", "" + mUri);
                        dataJson.put("uri", channelname);
                        if (lastPlayserverEntity != null) {
                            dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                        } else {
                            dataJson.put("node", "xrs_back");
                        }
                        dataJson.put("latency", bufferduration);
                        dataJson.put("avgFps", averagefps);
                        dataJson.put("fps", videofps);
                        dataJson.put("playBuf", bufferduration);
                        dataJson.put("hbDur", time);
                        dataJson.put("bytes", bytes);
                        dataJson.put("uid", "" + userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    xescdnLog2(defaultKey, dataJson, false);
                }
            });
        }
    }

    private void uploadOld() {
        if (!isNewIJK) {
            File[] fs = saveLogDir.listFiles();
            if (fs != null && fs.length > 0) {
                File file = fs[0];
                String string = FileStringUtil.readFromFile(file);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    jsonObject.put("serv", 920);
                    jsonObject.put("pri", 920);
                    xescdnLogUrl(jsonObject, file);
                } catch (JSONException e) {
                    logger.e("uploadOld", e);
                }
            }
        }
    }

//    private void uploadFile() {
//        File[] fs = saveLogDir.listFiles();
//        ArrayList<LogErrorEntity> logErrorEntities = new ArrayList<>();
//        if (fs != null) {
//            for (int i = 0; i < fs.length; i++) {
//                File file = fs[i];
//                String string = FileStringUtil.readFromFile(file);
//                try {
//                    JSONObject jsonObject = new JSONObject(string);
//                    JSONObject pridata = jsonObject.optJSONObject("pridata");
//                    if (pridata != null) {
//                        LogErrorEntity logErrorEntity = new LogErrorEntity();
//                        logErrorEntity.url = pridata.optString("saveurl");
//                        int index = logErrorEntities.indexOf(logErrorEntity);
//                        long savetime = pridata.optLong("savetime");
//                        if (index != -1) {
//                            logErrorEntity = logErrorEntities.get(index);
//                            logErrorEntity.count++;
//                            if (logErrorEntity.lastTime < savetime) {
//                                logErrorEntity.lastTime = savetime;
//                            }
//                            if (logErrorEntity.firstTime > savetime) {
//                                logErrorEntity.firstTime = savetime;
//                            }
//                        } else {
//                            logErrorEntity.count = 0;
//                            logErrorEntity.firstTime = savetime;
//                            logErrorEntity.lastTime = savetime;
//                            logErrorEntities.add(logErrorEntity);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (!logErrorEntities.isEmpty()) {
//            HashMap<String, String> defaultKey = new HashMap<>();
//            defaultKey.put("ver", logVersion);
//            defaultKey.put("serv", serv);
//            defaultKey.put("pri", "920");
//            defaultKey.put("ts", "" + System.currentTimeMillis());
//            defaultKey.put("appId", "" + UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
//            defaultKey.put("psId", UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
//            defaultKey.put("agent", "m-android_" + versionName);
//            defaultKey.put("os", "" + Build.VERSION.SDK_INT);
//            defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
//            defaultKey.put("arch", "" + cpuName);
//            int totalRam = HardWareUtil.getTotalRam();
//            defaultKey.put("ram", "" + totalRam);
//            defaultKey.put("net", "" + getNet());
//            defaultKey.put("cpu", "" + getCpuRate());
//            defaultKey.put("mem", "" + getMemRate());
//            String cip = oldCipdispatch;
//            defaultKey.put("cip", "" + cip);
//            defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
//            String msip = getRemoteIp();
//            defaultKey.put("sip", "" + msip);
//            defaultKey.put("tid", "" + tid);
//
//            JSONArray dataJson = new JSONArray();
//            try {
//                JSONObject requestJson = new JSONObject();
//
//                for (String key : defaultKey.keySet()) {
//                    String value = defaultKey.get(key);
//                    requestJson.put(key, value);
//                }
//                for (int i = 0; i < logErrorEntities.size(); i++) {
//                    LogErrorEntity logErrorEntity = logErrorEntities.get(i);
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("url", "" + logErrorEntity.url);
//                    jsonObject.put("count", logErrorEntity.count);
//                    jsonObject.put("firstTime", logErrorEntity.firstTime);
//                    jsonObject.put("lastTime", logErrorEntity.lastTime);
//                    dataJson.put(jsonObject);
//                }
//                requestJson.put("pridata", dataJson);
//                xescdnLogUrl(requestJson);
//            } catch (JSONException e) {
//                logger.d("uploadFile", e);
//            }
//        }
//    }

    private void saveStrToFile(String savestr) {
        if (!isNewIJK) {
            if (!saveLogDir.exists()) {
                saveLogDir.mkdirs();
            }
            File[] fs = saveLogDir.listFiles();
            if (fs != null && fs.length > 0) {
                fs[0].delete();
            }
            String s = dateFormat.format(new Date());
            String error = "save" + s + ".txt";
            File file = new File(saveLogDir, error);
            FileStringUtil.saveStrToFile(savestr, file, false);
        }
//        String string = FileStringUtil.readFromFile(file);
//        logger.d("saveStrToFile:equals=" + (string.equals(savestr)));
    }

    private void saveLogToFile(String tid, String savestr) {
        if (!isNewIJK) {
            if (!saveLogDirDebug.exists()) {
                saveLogDirDebug.mkdirs();
            }
            File file = new File(saveLogDirDebug, tid + ".txt");
            FileStringUtil.saveStrToFile(savestr + "\n", file, true);
        }
//        String string = FileStringUtil.readFromFile(file);
//        logger.d("saveStrToFile:equals=" + (string.equals(savestr)));
    }

    private void xescdnLogUrl(JSONObject requestJson, final File file) {
        if (!isNewIJK) {
            final HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(requestJson.toString());
            httpRequestParams.setWriteAndreadTimeOut(10);
            final AtomicInteger retryInt = new AtomicInteger(0);
            baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    logger.e("xescdnLogUrl:onFailure", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    file.delete();
                    if (response.body() != null) {
                        logger.d("xescdnLogUrl:onResponse:retry=" + retryInt.get() + ",response=" + response.body().string());
                    } else {
                        logger.d("xescdnLogUrl:onResponse:response=null");
                    }
                }
            });
        }
    }

    private long xescdnLog2Before = 0;

    private void xescdnLog2(HashMap<String, Object> defaultKey, final JSONObject dataJson, final boolean saveToFile) {
        if (!isNewIJK) {
            String tid = "" + defaultKey.get("tid");
            String pri = "" + defaultKey.get("pri");
            logger.d("xescdnLog2:tid=" + tid + ",pri=" + pri);
//        if (AppConfig.DEBUG) {
//            logurl = logurls[logIndex++ % logurls.length];
//        }
//        String priStr = tidAndPri.get(tid);
//        if (priStr == null) {
//            priStr = "" + pri;
//        } else {
//            priStr += "," + pri;
//        }
//        tidAndPri.put(tid, priStr);
            final String templogurl = logurl;
            final JSONObject requestJson = new JSONObject();
            try {
                requestJson.put("pridata", dataJson);
                for (String key : defaultKey.keySet()) {
                    Object value = defaultKey.get(key);
                    requestJson.put(key, value);
                }
                final HttpRequestParams httpRequestParams = new HttpRequestParams();
                httpRequestParams.setJson(requestJson.toString());
                httpRequestParams.setWriteAndreadTimeOut(10);
                final AtomicInteger retryInt = new AtomicInteger(0);
                if (AppConfig.DEBUG) {
                    final String tidfinal = (String) defaultKey.get("tid");
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            saveLogToFile(tidfinal, requestJson.toString());
                        }
                    });
                }
                baseHttpBusiness.baseSendPostNoBusinessJson(logurl, httpRequestParams, new Callback() {
                    Callback callback = this;

                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.e("xescdnLog:onFailure", e);
                        if (retryInt.get() < 2) {
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
                        } else {
                            try {
                                dataJson.put("saveurl", templogurl);
                                dataJson.put("savetime", System.currentTimeMillis());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
//                    saveStrToFile(requestJson.toString());
                        if (e instanceof SocketTimeoutException) {
                            final long now = System.currentTimeMillis();
                            if (now - xescdnLog2Before < 5 * 60 * 1000) {
                                return;
                            }
                            liveThreadPoolExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    URLDNS urldns = new URLDNS();
                                    try {
                                        DNSUtil.getDns(urldns, logurl);
                                        startTraceRoute(logurl, urldns.ip, oldCipdispatch, true);
                                        xescdnLog2Before = now;
                                    } catch (UnknownHostException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            logger.d("xescdnLog:onResponse:retry=" + retryInt.get() + ",response=" + response.body().string());
                        } else {
                            logger.d("xescdnLog:onResponse:response=null");
                        }
//                    if (AppConfig.DEBUG) {
//                        try {
//                            dataJson.put("saveurl", templogurl);
//                            dataJson.put("savetime", System.currentTimeMillis());
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                        saveStrToFile(requestJson.toString());
//                    }
                    }
                });
            } catch (JSONException e) {
                logger.e("xescdnLog2", e);
            }
        }
    }

    @Override
    public void onOpenFailed(int arg1, final int arg2) {
        if (!isNewIJK) {
            handler.removeMessages(1);
            final long heartTime;
            if (lastHeartTime == 0) {
                heartTime = 0;
            } else {
                heartTime = (SystemClock.elapsedRealtime() - this.lastHeartTime);
            }
            final long delay = System.currentTimeMillis() - openStart;
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
            final boolean isOpenSuccessfinal = isOpenSuccess;
            final String finalTid;
            if (!StringUtils.isEmpty(tid)) {
                finalTid = tid;
            } else {
//            CrashReport.postCatchedException(new Exception());
                return;
            }
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> defaultKey = new HashMap<>();
                    defaultKey.put("ver", logVersion);
                    defaultKey.put("serv", serv);
                    if (isOpenSuccessfinal) {
                        defaultKey.put("pri", priMap.get(PRI_KEY_onOpenFailed));
                    } else {
                        defaultKey.put("pri", priMap.get(PRI_KEY_RENDERING));
                    }
                    addDefault(defaultKey);
                    defaultKey.put("cpu", getCpuRate());
                    defaultKey.put("mem", getMemRate());
                    String cip = oldCipdispatch;
                    defaultKey.put("cip", "" + cip);
                    defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                    String msip = getRemoteIp("");
                    defaultKey.put("sip", "" + msip);
                    defaultKey.put("tid", "" + finalTid);

                    JSONObject dataJson = new JSONObject();
                    PlayFailCode playFailCode = getErrorCode(arg2);
                    try {
                        dataJson.put("url", "" + mUri);
                        dataJson.put("uri", channelname);
                        if (lastPlayserverEntity != null) {
                            dataJson.put("node", "" + lastPlayserverEntity.getProvide());
                        } else {
                            dataJson.put("node", "xrs_back");
                        }
                        dataJson.put("code", playFailCode.getCode());
                        dataJson.put("msg", "" + playFailCode.getTip());
                        if (isOpenSuccessfinal) {
                            long bufferduration = 0;
                            if (vPlayer.isInitialized()) {
//                            IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
//                            bufferduration = ijkMediaPlayer.getVideoCachedDuration();
                            }
                            dataJson.put("bufType", bufType);
                            if (isBuffer) {
                                dataJson.put("bufDur", (System.currentTimeMillis() - bufferTime));
                            } else {
                                dataJson.put("bufDur", 0);
                            }
                            dataJson.put("latency", bufferduration);
                            long time = SystemClock.elapsedRealtime() - frame10Start;
                            float averagefps = (float) (((double) (lastDisaplyCount - fistDisaplyCount)) * 1000 / time);
                            dataJson.put("avgFps", averagefps);
                            dataJson.put("fps", videofps);
                            dataJson.put("playBuf", bufferduration);
                            dataJson.put("hbDur", heartTime);
                            long bytes = trafficStatisticByteCount - lastTrafficStatisticByteCount;
                            if (bytes < 0) {
                                bytes = 0;
                                logger.d("onOpenFailed:now=" + trafficStatisticByteCount + ",last=" + lastTrafficStatisticByteCount);
                            }
                            dataJson.put("bytes", bytes);
                        } else {
                            URLDNS urldns = new URLDNS();
                            try {
                                DNSUtil.getDns(urldns, mUriHost);
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                            }
                            dataJson.put("dns", urldns.time);
                            dataJson.put("delay", delay);
                        }
                        dataJson.put("uid", "" + userId);
                    } catch (JSONException e) {
                        logger.e("onOpenFailed", e);
                    }
                    xescdnLog2(defaultKey, dataJson, false);

                    if (playFailCode.getCode() == PlayFailCode.TIME_OUT) {
                        if (msip != null) {
                            msip = DNSUtil.getHost(msip);
                        }
                        startTraceRoute("" + mUriHost, msip, cip, false);
                    }
                }
            });
        }
    }

    /**
     * TraceRoute 路由
     *
     * @param url        地址
     * @param msip       服务端ip
     * @param cip        客户端ip
     * @param saveToFile
     */
    private void startTraceRoute(final String url, final String msip, final String cip, final boolean saveToFile) {
        if (!isNewIJK) {
            Long time = urlTrace.get(url);
            long now = System.currentTimeMillis();
            if (time != null) {
                if (now - time < 5 * 60 * 1000) {
                    return;
                }
            }
            urlTrace.put(url, now);
            Bundle bundle = new Bundle();
            String host = DNSUtil.getHost(url);
            final String finalTid = tid;
            ldNetTraceClient.startTraceRoute(host, bundle, new LDNetTraceRoute.LDNetTraceRouteListener() {
                @Override
                public void OnNetTraceUpdated(String log) {
//                logger.d("OnNetTraceUpdated:log=" + log);
                }

                @Override
                public void OnNetTraceFinished() {

                }

                @Override
                public void onTraceRouteEnd(JavaTraceResult[] javaTraceResults) {
                    if (javaTraceResults.length > 0) {
                        HashMap<String, Object> defaultKey = new HashMap<>();
                        defaultKey.put("ver", logVersion);
                        defaultKey.put("serv", serv);
                        defaultKey.put("pri", 1);
                        addDefault(defaultKey);
                        defaultKey.put("cpu", getCpuRate());
                        defaultKey.put("mem", getMemRate());
                        defaultKey.put("cip", "" + cip);
                        defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                        defaultKey.put("sip", "" + msip);
                        defaultKey.put("tid", "" + finalTid);

                        JSONObject dataJson = new JSONObject();
                        try {
                            dataJson.put("url", url);
                            dataJson.put("ldnetversion", LDNetTraceRoute.VERSION);
                            JSONArray traceArray = new JSONArray();
                            for (int i = 0; i < javaTraceResults.length; i++) {
                                JavaTraceResult javaTraceResult = javaTraceResults[i];
                                JSONObject traceObj = new JSONObject();
                                traceObj.put("ttl", javaTraceResult.ttl);
                                traceObj.put("send", 4);
                                traceObj.put("best", javaTraceResult.rttMin);
                                ArrayList<String> times = javaTraceResult.getTimes();
                                if (times.size() > 0) {
                                    try {
                                        traceObj.put("last", Float.parseFloat(times.get(times.size() - 1)));
                                    } catch (Exception e) {

                                    }
                                } else {
                                    traceObj.put("last", 0);
                                }
                                traceObj.put("worst", javaTraceResult.rttMax);
                                traceObj.put("avrg", javaTraceResult.rttAvg);
                                traceObj.put("loss", javaTraceResult.lost);
                                traceObj.put("recv", javaTraceResult.receivedpackets);
                                traceObj.put("sip", javaTraceResult.bothHost);
                                traceArray.put(traceObj);
                            }
                            dataJson.put("trace", traceArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        xescdnLog2(defaultKey, dataJson, saveToFile);
                    }
                }
            });
        }
    }

    @Override
    public void onPlaybackComplete() {
        if (!isNewIJK) {
            super.onPlaybackComplete();
            handler.removeMessages(1);
            send("onPlaybackComplete", 0, 0);
            sip = "";
        }
    }

    @Override
    public void onPlayError() {
        if (!isNewIJK) {
            super.onPlayError();
            handler.removeMessages(1);
        }
//        send("onPlayError", 0);
    }

    public void destory() {
        if (!isNewIJK) {
            isDestory = true;
            logger.d("destory:isPause=" + isPause);
            handler.removeMessages(1);
            if (!isPause) {
                if (mUri != null) {
                    send("destory", 0, 0);
                }
            }
            ldNetTraceClient.destory();
        }
    }

    public String getAppVersionName() {
        String versionName = "";
        if (!isNewIJK) {
            try {
                PackageManager pm = activity.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
                versionName = pi.versionName;
            } catch (Exception var4) {

            }
        }
        return versionName;
    }

    public void seekTo(long pos) {
        isSeek = true;
    }

    /**
     * seek完成
     */
    @Override
    public void onSeekComplete() {
        isSeek = false;
    }

    private void getFps() {
        if (!isNewIJK) {
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
                            logger.d("getFps:bundle1=" + bundle1);
                            if (bundle1.containsKey("fps_num") && bundle1.containsKey("fps_den")) {
                                int fps_num = Integer.parseInt(bundle1.getString("fps_num"));
                                int fps_den = Integer.parseInt(bundle1.getString("fps_den"));
                                videofps = (float) fps_num / (float) fps_den;
                                logger.d("getFps:fps_num=" + fps_num + ",fps_den=" + fps_den + ",fps=" + videofps);
                            }
                            break;
                        }
                    }
                }
                }
            } catch (Exception e) {
                UmsAgentManager.umsAgentException(BaseApplication.getContext(), TAG + "getFps", e);
            }
        }
    }

    public static int getErrorCodeInt(int arg2) {
        int code = -1111;
        if (!MediaPlayer.getIsNewIJK()) {
            PlayFailCode code1 = getErrorCode(arg2);
            if (code1 != null) {
                code = code1.code;
            }
        }
        return code;
    }

    public static PlayFailCode getErrorCode(int arg2) {
        if (!MediaPlayer.getIsNewIJK()) {
            AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
            if (error != null) {
                switch (error) {
                    case EHOSTUNREACH:
                        return PlayFailCode.PlayFailCode10;
                    case ETIMEDOUT:
                        return PlayFailCode.PlayFailCode15;
                    case ECONNREFUSED:
                        return new PlayFailCode(16, "Connection refuse");
                    case EIO:
                        return new PlayFailCode(17, "Io error");
                    case HTTP_UNAUTHORIZED:
                        return PlayFailCode.PlayFailCode20;
                    case HTTP_FORBIDDEN:
                        return PlayFailCode.PlayFailCode20;
                    case HTTP_NOT_FOUND:
                        return PlayFailCode.PlayFailCode20;
                    case HTTP_OTHER_4XX:
                        return PlayFailCode.PlayFailCode20;
                    case HTTP_SERVER_ERROR:
                        return PlayFailCode.PlayFailCode20;
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
        }
        return new PlayFailCode(arg2, "other-" + arg2);
    }

    private int getNet() {

        int net = 0;
        if (!MediaPlayer.getIsNewIJK()) {
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
                        UmsAgentManager.umsAgentDebug(BaseApplication.getContext(), TAG, "getNetworkType:strNetworkType=" + strNetworkType);
                    }
                }
            } catch (Exception e) {
                net = -1024;
                UmsAgentManager.umsAgentException(BaseApplication.getContext(), TAG + "getNetworkType", e);
            }
        }
        return net;
    }


    //////////////   920 日志/////////////////


    /**
     * 上传920 日志
     *
     * @param dataJson
     */
    public void postLiveLog920(String dataJson) {
        if (!MediaPlayer.getIsNewIJK()) {
            if (TextUtils.isEmpty(LIVE_920_TYPE) || TextUtils.equals(LIVE_920_TYPE, LiveVideoConfig.LIVE_LOG_920_HOST)) {
                postLiveLogHost920(dataJson, true);
            } else {
                postLiveLogIp920(dataJson, 0, "", 1l, true);
            }
        }
    }

    /**
     * 上传日志 host方式
     *
     * @param dataJson
     */
    public void postLiveLogHost920(final String dataJson, final boolean isFirst) {
        if (!MediaPlayer.getIsNewIJK()) {
            final HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(dataJson);
            httpRequestParams.setWriteAndreadTimeOut(10);
            final long time = System.currentTimeMillis();
            baseHttpBusiness.baseSendPostNoBusinessJson(LiveVideoConfig.URL_CDN_LOG, httpRequestParams, new Callback() {
                @Override
                public void onFailure(Call call, IOException ex) {
                    if (isFirst) {
                        int code = -1;
                        String msg = "otherError";
                        if (ex instanceof HttpException) {
                            HttpException error = (HttpException) ex;
                            if (error.getCode() >= 300) {
                                code = PlayFailCode.PlayFailCode20.code;
                                msg = PlayFailCode.PlayFailCode20.tip;
                            }
                        } else if (ex instanceof UnknownHostException) {
                            code = PlayFailCode.PlayFailCode10.code;
                            msg = PlayFailCode.PlayFailCode10.tip;

                        } else if (ex instanceof SocketTimeoutException) {
                            code = PlayFailCode.PlayFailCode15.code;
                            msg = PlayFailCode.PlayFailCode15.tip;

                        }

                        long dely = System.currentTimeMillis() - time;

                        postLiveLogIp920(dataJson, code, msg, dely, false);
                    } else {
                        // 保存日志到文件
                        save920History(httpRequestParams);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LIVE_920_TYPE = LiveVideoConfig.LIVE_LOG_920_HOST;
                }
            });
        }
    }


    /**
     * 上传日志 ip方式
     *
     * @param code
     * @param msg
     * @param delay
     */
    public void postLiveLogIp920(final String dataJson, final int code, final String msg, final long
            delay, final boolean isFirst) {
        if (!MediaPlayer.getIsNewIJK()) {
            final HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(dataJson);
            httpRequestParams.setWriteAndreadTimeOut(10);
            baseHttpBusiness.baseSendPostNoBusinessJson(LiveVideoConfig.URL_CND_LOG_IP, httpRequestParams, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isFirst) {
                        postLiveLogHost920(dataJson, false);
                    } else {
                        // 保存日志到文件
                        save920History(httpRequestParams);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!isFirst) {
                        onHostError(LiveVideoConfig.URL_CDN_LOG, code, msg, delay);
                    }
                    LIVE_920_TYPE = LiveVideoConfig.LIVE_LOG_920_IP;
                }
            });
        }
    }

    /**
     * 域名不通，IP 上传日志成功 日志上报
     *
     * @param url
     * @param code
     * @param msg
     * @param delay
     */
    private void onHostError(String url, int code, String msg, long delay) {
        if (!MediaPlayer.getIsNewIJK()) {
            JSONObject jsonObject = getHostErrorLog(url, code, msg, delay);
            HttpRequestParams httpRequestParams = new HttpRequestParams();
            httpRequestParams.setJson(String.valueOf(jsonObject));
            baseHttpBusiness.baseSendPostNoBusinessJson(LiveVideoConfig.URL_CND_LOG_IP, httpRequestParams, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
            getTraceRouteLog(url);
        }
        // Loger.d("livelog_920", jsonObject.toString());
    }


    /**
     * 获取host 日志 pri 0
     *
     * @param url
     * @param code
     * @param msg
     * @param delay
     * @return
     */
    private JSONObject getHostErrorLog(String url, int code, String msg, long delay) {
        JSONObject jsonObject = getDefaultInfo();
        if (!MediaPlayer.getIsNewIJK()) {
            try {
                jsonObject.put("pri", "0");
                jsonObject.put("host_log", "hostlog");
                JSONObject priData = new JSONObject();
                priData.put("url", url);
                priData.put("code", code + "");
                priData.put("msg", msg);
                URLDNS urldns = new URLDNS();
                try {
                    DNSUtil.getDns(urldns, url);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                priData.put("dns", urldns.time);
                priData.put("delay", delay);
                jsonObject.put("pridata", priData);
            } catch (Exception ex) {

            }
        }
        return jsonObject;
    }


    /**
     * 默认参数获取
     *
     * @return
     */
    private JSONObject getDefaultInfo() {

        JSONObject defaultKey = new JSONObject();
        if (!MediaPlayer.getIsNewIJK()) {
            try {
                String psId = UserBll.getInstance().getMyUserInfoEntity().getPsimId();
                defaultKey.put("ver", "1");
                defaultKey.put("serv", "920");
                defaultKey.put("ts", System.currentTimeMillis());
                defaultKey.put("appId", "" + UserBll.getInstance().getMyUserInfoEntity().getPsAppId());
                defaultKey.put("psId", "" + psId);
                defaultKey.put("agent", "m-android_" + AppUtils.getAppVersionName(BaseApplication.getContext()));
                defaultKey.put("os", "" + Build.VERSION.SDK_INT);
                defaultKey.put("dev", "" + DeviceInfo.getDeviceName());
                defaultKey.put("arch", "" + HardWareUtil.getCpuName());
                defaultKey.put("net", getNet());
                int totalRam = HardWareUtil.getTotalRam();
                defaultKey.put("ram", totalRam);
                defaultKey.put("cpu", HardWareUtil.getCPURateDesc());
                defaultKey.put("mem", getMemRate());


                defaultKey.put("cip", "" + oldCipdispatch);
                defaultKey.put("lip", "" + IRCTalkConf.getHostIP());
                String hostIp = getRemoteIp("");
                defaultKey.put("sip", "" + hostIp);
                defaultKey.put("tid", "" + AppBll.getInstance().getAppInfoEntity().getAppUUID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultKey;

    }


    /**
     * 获取traceRoute信息和pri 1 日志
     *
     * @param url
     */
    private void getTraceRouteLog(final String url) {
        if (!MediaPlayer.getIsNewIJK()) {
            Bundle bundle = new Bundle();
            String host = DNSUtil.getHost(url);
            final JSONObject deFaultJson = getDefaultInfo();
            ldNetTraceClient.startTraceRoute(host, bundle, new LDNetTraceRoute.LDNetTraceRouteListener() {
                @Override
                public void OnNetTraceUpdated(String log) {
//                logger.d("OnNetTraceUpdated:log=" + log);
                }

                @Override
                public void OnNetTraceFinished() {

                }

                @Override
                public void onTraceRouteEnd(JavaTraceResult[] javaTraceResults) {
                    if (javaTraceResults.length > 0) {


                        try {
                            deFaultJson.put("pri", "1");
                            deFaultJson.put("url", url);
                            deFaultJson.put("host_log", "hostlog");
                            deFaultJson.put("ldnetversion", LDNetTraceRoute.VERSION);
                            JSONArray traceArray = new JSONArray();
                            for (int i = 0; i < javaTraceResults.length; i++) {
                                JavaTraceResult javaTraceResult = javaTraceResults[i];
                                JSONObject traceObj = new JSONObject();
                                traceObj.put("ttl", javaTraceResult.ttl);
                                traceObj.put("send", 4);
                                traceObj.put("best", javaTraceResult.rttMin);
                                ArrayList<String> times = javaTraceResult.getTimes();
                                if (times.size() > 0) {
                                    try {
                                        traceObj.put("last", Float.parseFloat(times.get(times.size() - 1)));
                                    } catch (Exception e) {

                                    }
                                } else {
                                    traceObj.put("last", 0);
                                }
                                traceObj.put("worst", javaTraceResult.rttMax);
                                traceObj.put("avrg", javaTraceResult.rttAvg);
                                traceObj.put("loss", javaTraceResult.lost);
                                traceObj.put("recv", javaTraceResult.receivedpackets);
                                traceObj.put("sip", javaTraceResult.bothHost);
                                traceArray.put(traceObj);
                            }
                            deFaultJson.put("trace", traceArray);
                            //  Loger.d("livelog_920", deFaultJson.toString());
                            HttpRequestParams httpRequestParams = new HttpRequestParams();
                            httpRequestParams.setJson(String.valueOf(deFaultJson));
                            baseHttpBusiness.baseSendPostNoBusinessJson(LiveVideoConfig.URL_CND_LOG_IP,
                                    httpRequestParams, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                        }
                                    });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    String liveLogPath920 = "liveLog920";

    private void save920History(HttpRequestParams httpRequestParams) {
        if (!MediaPlayer.getIsNewIJK()) {
            try {
                String jsonStr = httpRequestParams.getJson();
                JSONObject jsonObject = new JSONObject(jsonStr);
                StringBuffer stringBuffer = new StringBuffer();
                String url = jsonObject.getString("url");
                String sip = jsonObject.getString("sip");
                String cip = jsonObject.getString("cip");
                String lip = jsonObject.getString("lip");
                String priIndex = jsonObject.getString("priIndex");
                stringBuffer.append(url);
                stringBuffer.append(sip);
                stringBuffer.append(cip);
                stringBuffer.append(lip);

                JSONArray historyLogArray = getOld920Log();
                boolean isHave = false;
                if (historyLogArray != null && historyLogArray.length() > 0) {
                    JSONObject historyJson = null;
                    for (int i = 0; i < historyLogArray.length(); i++) {
                        historyJson = historyLogArray.getJSONObject(i);
                        if (TextUtils.equals(historyJson.optString("priIndex"), priIndex)) {
                            int cnt = historyJson.optInt("cnt");
                            historyJson.put("lts", System.currentTimeMillis());
                            historyJson.put("cnt", cnt + 1);
                            isHave = true;
                            break;
                        }
                    }
                }
                // 如果有历史日志
                if (!isHave) {
                    if (historyLogArray == null) {
                        historyLogArray = new JSONArray();
                    }
                    JSONObject json = new JSONObject();
                    json.put("url", url);
                    json.put("sip", sip);
                    json.put("cip", cip);
                    json.put("lip", lip);
                    json.put("fts", System.currentTimeMillis());
                    json.put("priIndex", priIndex);
                    historyLogArray.put(json);
                }
                if (!liveLog920.exists()) {
                    liveLog920.mkdirs();
                }
                File file = new File(liveLog920, liveLogPath920 + ".txt");
                FileStringUtil.saveStrToFile(com.alibaba.fastjson.JSONObject.toJSONString(historyLogArray) + "\n", file, true);

            } catch (Exception e) {

            }
        }
    }


    private JSONArray getOld920Log() {
        JSONArray jsonArray = null;
        if (!MediaPlayer.getIsNewIJK()) {
            try {
                if (liveLog920 == null) {
                    return null;
                }
                File[] fs = liveLog920.listFiles();
                File file920 = null;
                if (fs != null && fs.length > 0) {
                    for (int i = 0; i < fs.length; i++) {
                        if (TextUtils.equals(fs[i].getName(), liveLogPath920)) {
                            file920 = fs[i];
                            break;
                        }
                    }
                    if (file920 != null) {
                        String string = FileStringUtil.readFromFile(file920);

                        jsonArray = new JSONArray(string);
                    }
                }
            } catch (Exception e) {
            }
        }
        return jsonArray;
    }

}
