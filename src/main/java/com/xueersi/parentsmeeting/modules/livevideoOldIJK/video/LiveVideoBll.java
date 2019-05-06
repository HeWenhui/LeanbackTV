package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

import android.app.Activity;
import android.net.Uri;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.video.LivePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LivePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.business.VPlayerListenerReg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by linyuqiang on 2018/6/22.
 * 直播bll
 */
public class LiveVideoBll implements VPlayerListenerReg {
    private final String TAG = "LiveVideoBll";
    private Logger logger = LoggerFactory.getLogger(TAG);
    /** 直播服务器 */
    private PlayServerEntity mServer;
    private LiveGetInfo mGetInfo;
    /** 直播帧数统计 */
    private LivePlayLog livePlayLog;
    private int lastIndex;
    /** 直播服务器选择 */
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private ArrayList<PlayServerEntity.PlayserverEntity> failPlayserverEntity = new ArrayList<>();
    private ArrayList<PlayServerEntity.PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
    private BasePlayerFragment videoFragment;
    private Activity activity;
    private LiveBll2 mLiveBll;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser mHttpResponseParser;
    /** 上次播放统计开始时间 */
    long lastPlayTime;
    private LogToFile mLogtf;
    private WeakHandler mHandler = new WeakHandler(null);
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 正在播放 */
    private boolean isPlay = false;
    /** 是否播放成功 */
    boolean openSuccess = false;
    /** 直播调度 */
    private LiveGetPlayServer liveGetPlayServer;
    long openStartTime;
    /** 缓冲超时 */
    private final long mBufferTimeout = 5000;
    /** 打开超时 */
    private final long mOpenTimeOut = 15000;
    /** 播放时长定时任务 */
    private final long mPlayDurTime = 420000;
    /** 直播缓存打开统计 */
    private ArrayList<VPlayerCallBack.VPlayerListener> mPlayStatistics = new ArrayList<>();
    /** 播放时长 */
    private long playTime = 0;
    /** live_report_play_duration 开始时间 */
    protected long reportPlayStarTime;
    private LiveVideoReportBll liveVideoReportBll;
    private VideoAction mVideoAction;
    private int mLiveType;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    public LiveVideoBll(Activity activity, LiveBll2 liveBll, int liveType) {
        this.activity = activity;
        this.mLiveBll = liveBll;
        this.mLiveType = liveType;
        mLogtf = new LogToFile(activity, TAG);
        livePlayLog = new LivePlayLog(activity, true);
        liveVideoReportBll = new LiveVideoReportBll(activity, liveBll);
        liveVideoReportBll.setLivePlayLog(livePlayLog);
        mPlayStatistics.add(liveVideoReportBll.getVideoListener());
        mLogtf.clear();
        ProxUtil.getProxUtil().put(activity, VPlayerListenerReg.class, this);
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
        livePlayLog.setvPlayer(vPlayer);
    }

    public void setHttpManager(LiveHttpManager httpManager) {
        this.mHttpManager = httpManager;
        liveVideoReportBll.setHttpManager(httpManager);
    }

    public void setHttpResponseParser(LiveHttpResponseParser httpResponseParser) {
        this.mHttpResponseParser = httpResponseParser;
    }

    @Override
    public void addVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener) {
        mPlayStatistics.add(vPlayerListener);
    }

    @Override
    public void removeVPlayerListener(VPlayerCallBack.VPlayerListener vPlayerListener) {
        mPlayStatistics.remove(vPlayerListener);
    }

    public void setVideoAction(VideoAction mVideoAction) {
        this.mVideoAction = mVideoAction;
    }

    /** 在{@link LiveBll2}获取getInfo成功而之后,{@link LiveBll2#onGetInfoSuccess(LiveGetInfo)} */
    public void onLiveInit(LiveGetInfo getInfo, LiveTopic liveTopic) {
        this.mGetInfo = getInfo;
        liveGetPlayServer = new LiveGetPlayServer(activity, new TeacherIsPresent() {

            @Override
            public boolean isPresent() {
                return mLiveBll.isPresent();
            }
        }, mLiveType, getInfo, liveTopic);
        liveGetPlayServer.setHttpManager(mHttpManager);
        liveGetPlayServer.setHttpResponseParser(mHttpResponseParser);
        liveGetPlayServer.setLivePlayLog(livePlayLog);
        liveGetPlayServer.setVideoAction(mVideoAction);
        liveVideoReportBll.onLiveInit(getInfo, liveTopic);
        liveGetPlayServer(liveTopic.getMode(), false);
    }

    /**
     * 直播模式变化H
     *
     * @param mode      模式
     * @param isPresent 老师在不在直播间H
     */
    public void onModeChange(String mode, boolean isPresent) {
        mLogtf.d("onModeChange:mode=" + mode + ",isPresent=" + isPresent);
        liveGetPlayServer.liveGetPlayServer(mode, true);
    }

    public void liveGetPlayServer(final String mode, final boolean modechange) {
        liveGetPlayServer.liveGetPlayServer(mode, modechange);
    }

    public void setVideoFragment(BasePlayerFragment videoFragment) {
        this.videoFragment = videoFragment;
        if (videoFragment instanceof LivePlayerFragment) {
            LivePlayerFragment livePlayerFragment = (LivePlayerFragment) videoFragment;
            livePlayerFragment.setLivePlayLog(livePlayLog);
        }
    }

    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        this.mServer = server;
        liveVideoReportBll.setServer(server);
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     *
     * @param modechange
     */
    public void rePlay(boolean modechange) {
        if (livePlayLog != null) {
            livePlayLog.onReplay();
        }
        String url;
        String msg = "rePlay:";
        if (mServer == null) {
            livePlayLog.setLastPlayserverEntity(null);
            String rtmpUrl = null;
            String[] rtmpUrls = mGetInfo.getRtmpUrls();
            if (rtmpUrls != null) {
                rtmpUrl = rtmpUrls[(lastIndex++) % rtmpUrls.length];
            }
            if (rtmpUrl == null) {
                rtmpUrl = mGetInfo.getRtmpUrl();
            }
            url = rtmpUrl + "/" + mGetInfo.getChannelname();
            msg += "mServer=null";
            liveVideoReportBll.setPlayserverEntity(null);
        } else {
            List<PlayServerEntity.PlayserverEntity> playservers = mServer.getPlayserver();
//            for (int i = 0; i < playservers.size(); i++) {
//                final PlayserverEntity playserverEntity = playservers.get(i);
//                mLiveBll.dns_resolve_stream(playserverEntity, mGetInfo.getChannelname(), mServer.getAppname(), new AbstractBusinessDataCallBack() {
//                    @Override
//                    public void onDataSucess(Object... objData) {
//                        String ip = (String) objData[0];
//                        mLogtf.d("dns_resolve_stream:ip=" + ip);
//                    }
//
//                    @Override
//                    public void onDataFail(int errStatus, String failMsg) {
//                        mLogtf.d("dns_resolve_stream:onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
//                        super.onDataFail(errStatus, failMsg);
//                    }
//                });
//            }
            msg += "playservers=" + playservers.size();
            PlayServerEntity.PlayserverEntity entity = null;
            boolean useFlv = false;
            if (lastPlayserverEntity == null) {
                msg += ",lastPlayserverEntity=null";
                entity = playservers.get(0);
            } else {
                msg += ",failPlayserverEntity=" + failPlayserverEntity.size();
                if (!failPlayserverEntity.isEmpty()) {
                    boolean allRtmpFail = true;
                    boolean allFlvFail = true;
                    List<PlayServerEntity.PlayserverEntity> flvPlayservers = new ArrayList<>();
                    for (int i = 0; i < playservers.size(); i++) {
                        PlayServerEntity.PlayserverEntity playserverEntity = playservers.get(i);
                        if (!StringUtils.isEmpty(playserverEntity.getFlvpostfix())) {
                            flvPlayservers.add(playserverEntity);
                            if (!failFlvPlayserverEntity.contains(playserverEntity)) {
                                allFlvFail = false;
                            }
                        }
                        if (!failPlayserverEntity.contains(playserverEntity)) {
                            allRtmpFail = false;
                        }
                    }
                    if (allFlvFail) {
                        msg += ",allFlvFail";
                        failPlayserverEntity.clear();
                        failFlvPlayserverEntity.clear();
                    } else {
                        if (allRtmpFail) {
                            if (flvPlayservers.isEmpty()) {
                                failPlayserverEntity.clear();
                            } else {
                                if (!lastPlayserverEntity.isUseFlv()) {
                                    entity = flvPlayservers.get(0);
                                    entity.setUseFlv(true);
                                    useFlv = true;
                                    msg += ",setUseFlv1";
                                } else {
                                    for (int i = 0; i < flvPlayservers.size(); i++) {
                                        PlayServerEntity.PlayserverEntity playserverEntity = flvPlayservers.get(i);
                                        if (lastPlayserverEntity.getAddress().equals(playserverEntity.getAddress())) {
                                            if (modechange) {
                                                entity = flvPlayservers.get(i % flvPlayservers.size());
                                            } else {
                                                entity = flvPlayservers.get((i + 1) % flvPlayservers.size());
                                            }
                                            entity.setUseFlv(true);
                                            useFlv = true;
                                            msg += ",setUseFlv2,modechange=" + modechange;
                                            break;
                                        }
                                    }
                                    if (entity == null) {
                                        msg += ",entity=null1";
                                        entity = flvPlayservers.get(0);
                                    }
                                }
                            }
                        }
                    }
                }
                if (entity == null) {
                    for (int i = 0; i < playservers.size(); i++) {
                        PlayServerEntity.PlayserverEntity playserverEntity = playservers.get(i);
                        if (lastPlayserverEntity.equals(playserverEntity)) {
                            if (modechange) {
                                entity = playservers.get(i % playservers.size());
                            } else {
                                entity = playservers.get((i + 1) % playservers.size());
                            }
                            msg += ",entity=null2,modechange=" + modechange;
                            break;
                        }
                    }
                }
                if (entity == null) {
                    msg += ",entity=null3";
                    entity = playservers.get(0);
                }
            }
            lastPlayserverEntity = entity;
            liveVideoReportBll.setPlayserverEntity(entity);
            livePlayLog.setLastPlayserverEntity(entity);
            if (useFlv) {
                url = "http://" + entity.getAddress() + ":" + entity.getHttpport() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname() + entity.getFlvpostfix();
            } else {
                if (StringUtils.isEmpty(entity.getIp_gslb_addr())) {
                    url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                } else {
                    final PlayServerEntity.PlayserverEntity finalEntity = entity;
                    dns_resolve_stream(entity, mServer, mGetInfo.getChannelname(), new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            if (finalEntity != lastPlayserverEntity) {
                                return;
                            }
                            String provide = (String) objData[0];
                            String url;
                            if ("wangsu".equals(provide)) {
                                url = objData[1] + "&username=" + mGetInfo.getUname() + "&cfrom=android";
                                videoFragment.playNewVideo(Uri.parse(url), mGetInfo.getName());
                            } else if ("ali".equals(provide)) {
                                url = (String) objData[1];
                                StringBuilder stringBuilder = new StringBuilder(url);
                                addBody("Sucess", stringBuilder);
                                url = stringBuilder + "&username=" + mGetInfo.getUname();
                                videoFragment.playNewVideo(Uri.parse(url), mGetInfo.getName());
                            } else {
                                return;
                            }
                            StableLogHashMap stableLogHashMap = new StableLogHashMap("glsb3rdDnsReply");
                            stableLogHashMap.put("message", "" + url);
                            stableLogHashMap.put("activity", activity.getClass().getSimpleName());
                            UmsAgentManager.umsAgentDebug(activity, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData());
                        }

                        @Override
                        public void onDataFail(int errStatus, String failMsg) {
                            if (finalEntity != lastPlayserverEntity) {
                                return;
                            }
                            String url = "rtmp://" + finalEntity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                            StringBuilder stringBuilder = new StringBuilder(url);
                            addBody("Fail", stringBuilder);
                            videoFragment.playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
                        }
                    });
                    return;
                }
            }
            msg += ",entity=" + entity.getIcode();
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        msg += addBody("rePlay", stringBuilder);
        msg += ",url=" + stringBuilder;
        mLogtf.d(msg);
        videoFragment.playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
    }

    /** 直接指定为只去播放 */
    public void playNewVideo(int pos) {
        String url = constructUrl(pos);
        logger.i("加载的url = " + url);
        if (url != null) {
            videoFragment.playNewVideo(Uri.parse(url), mGetInfo.getName());
        }
    }

    /** 构造url */
    private String constructUrl(int pos) {
        String url = "";
        String msg = "";
        if (mServer == null) {
            livePlayLog.setLastPlayserverEntity(null);
            String rtmpUrl = null;
            String[] rtmpUrls = mGetInfo.getRtmpUrls();
            if (rtmpUrls != null) {
                rtmpUrl = rtmpUrls[(pos) % rtmpUrls.length];
            }
            if (rtmpUrl == null) {
                rtmpUrl = mGetInfo.getRtmpUrl();
            }
            url = rtmpUrl + "/" + mGetInfo.getChannelname();
            msg += "mServer=null";
            liveVideoReportBll.setPlayserverEntity(null);
        } else {
            List<PlayServerEntity.PlayserverEntity> playservers = mServer.getPlayserver();
            PlayServerEntity.PlayserverEntity entity = playservers.get(pos);
            if (StringUtils.isEmpty(entity.getIp_gslb_addr())) {
                url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
            } else {
                final PlayServerEntity.PlayserverEntity finalEntity = entity;
                dns_resolve_stream(entity, mServer, mGetInfo.getChannelname(), new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        if (finalEntity != lastPlayserverEntity) {
                            return;
                        }
                        String provide = (String) objData[0];
                        String url;
                        if ("wangsu".equals(provide)) {
                            url = objData[1] + "&username=" + mGetInfo.getUname() + "&cfrom=android";
                            videoFragment.playNewVideo(Uri.parse(url), mGetInfo.getName());
                        } else if ("ali".equals(provide)) {
                            url = (String) objData[1];
                            StringBuilder stringBuilder = new StringBuilder(url);
                            addBody("Sucess", stringBuilder);
                            url = stringBuilder + "&username=" + mGetInfo.getUname();
                            videoFragment.playNewVideo(Uri.parse(url), mGetInfo.getName());
                        } else {
                            return;
                        }
                        StableLogHashMap stableLogHashMap = new StableLogHashMap("glsb3rdDnsReply");
                        stableLogHashMap.put("message", "" + url);
                        stableLogHashMap.put("activity", activity.getClass().getSimpleName());
                        UmsAgentManager.umsAgentDebug(activity, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData());
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        if (finalEntity != lastPlayserverEntity) {
                            return;
                        }
                        String url = "rtmp://" + finalEntity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
                        StringBuilder stringBuilder = new StringBuilder(url);
                        addBody("Fail", stringBuilder);
                        videoFragment.playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
                    }
                });
                return "";
            }
        }
        return url;
    }

    /**
     * 直播地址的一些通用参数
     *
     * @param method
     * @param url
     * @return
     */
    protected String addBody(String method, StringBuilder url) {
        String msg = "";
        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
                msg += ",t1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
                    url.append("?" + mGetInfo.getSkeyPlayT() + "&cfrom=android");
                    msg += ",t2";
                } else {
                    url.append("?cfrom=android");
                    msg += ",t3";
                }
            }
        } else {
            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
                msg += ",f1";
            } else {
                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayF())) {
                    url.append("?" + mGetInfo.getSkeyPlayF() + "&cfrom=android");
                    msg += ",f2";
                } else {
                    url.append("?cfrom=android");
                    msg += ",f3";
                }
            }
        }
        logger.d("addBody:method=" + method + ",url=" + url);
        return msg;
    }

    public VPlayerCallBack.VPlayerListener getPlayListener() {
        return mPlayListener;
    }

    private VPlayerCallBack.VPlayerListener mPlayListener = new VPlayerCallBack.SimpleVPlayerListener() {

        @Override
        public void onPlaying(long currentPosition, long duration) {
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                mLogtf.d("onPlaying:startRemote");
                stopPlay();
            }
        }

        @Override
        public void onPlaybackComplete() {
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onPlaybackComplete();
            }
            mLogtf.d("onPlaybackComplete");
            if (openSuccess) {
                playTime += (System.currentTimeMillis() - lastPlayTime);
            }
            isPlay = false;
            openSuccess = false;
            onFail(0, 0);
        }

        @Override
        public void onPlayError() {
            mLogtf.d("onPlayError");
            isPlay = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onPlayError();
            }
            if (openSuccess) {
                playTime += (System.currentTimeMillis() - lastPlayTime);
            }
            openSuccess = false;
        }

        @Override
        public void onOpenSuccess() {
            isPlay = true;
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                mLogtf.d("onOpenSuccess:startRemote=true");
                stopPlay();
                return;
            }
            lastPlayTime = System.currentTimeMillis();
            reportPlayStarTime = System.currentTimeMillis();
            openSuccess = true;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onOpenSuccess();
            }
            mHandler.removeCallbacks(mPlayDuration);
            mLogtf.d("onOpenSuccess:playTime=" + playTime);
            mHandler.postDelayed(mPlayDuration, mPlayDurTime);
            mHandler.removeCallbacks(getVideoCachedDurationRun);
            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
        }

        @Override
        public void onOpenStart() {
            mLogtf.d("onOpenStart");
            openStartTime = System.currentTimeMillis();
            openSuccess = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onOpenStart();
            }
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            isPlay = false;
            if (openSuccess) {
                playTime += (System.currentTimeMillis() - lastPlayTime);
            }
            openSuccess = false;
            mHandler.removeCallbacks(mOpenTimeOutRun);
            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            onFail(arg1, arg2);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onOpenFailed(arg1, arg2);
            }
            mLogtf.d("onOpenFailed:arg2=" + arg2);
            if (lastPlayserverEntity != null) {
                liveVideoReportBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "fail reconnect");
                reportPlayStarTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onBufferStart() {
            mHandler.removeCallbacks(mBufferTimeOutRun);
            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onBufferStart();
            }
            mLogtf.d("onBufferStart");
        }

        @Override
        public void onBufferComplete() {
            mHandler.removeCallbacks(mBufferTimeOutRun);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onBufferComplete();
            }
            mLogtf.d("onBufferComplete");
        }
    };

    public void stopPlay() {
        if (isInitialized()) {
            livePlayLog.stopPlay();
            vPlayer.releaseSurface();
            vPlayer.stop();
        }
    }

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (vPlayer != null && vPlayer.isInitialized());
    }

    /**
     * 打开超时
     */
    private Runnable mOpenTimeOutRun = new Runnable() {

        @Override
        public void run() {
            if (isInitialized()) {
                vPlayer.releaseSurface();
                vPlayer.stop();
            }
            mVideoAction.onPlayError(0, PlayErrorCode.PLAY_TIMEOUT_300);
            long openTimeOut = System.currentTimeMillis() - openStartTime;
            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
            liveGetPlayServer.liveGetPlayServer(false);
        }
    };

    public void liveGetPlayServer() {
        liveGetPlayServer.liveGetPlayServer(false);
    }

    /**
     * 缓冲超时
     */
    private Runnable mBufferTimeOutRun = new Runnable() {

        @Override
        public void run() {
            livePlayLog.onBufferTimeOut();
            if (isInitialized()) {
                vPlayer.releaseSurface();
                vPlayer.stop();
            }
            long openTime = System.currentTimeMillis() - openStartTime;
            if (openTime > 40000) {
                liveVideoReportBll.streamReport(LiveVideoReportBll.MegId.MEGID_12107, mGetInfo.getChannelname(), openTime);
            } else {
                liveVideoReportBll.streamReport(LiveVideoReportBll.MegId.MEGID_12137, mGetInfo.getChannelname(), openTime);
            }
            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
            if (lastPlayserverEntity != null) {
                liveVideoReportBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "buffer empty reconnect");
                reportPlayStarTime = System.currentTimeMillis();
            }
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                if (vPlayerListener instanceof LiveVPlayerListener) {
                    LiveVPlayerListener vPlayerListener1 = (LiveVPlayerListener) vPlayerListener;
                    vPlayerListener1.onBufferTimeOutRun();
                }
            }
            liveGetPlayServer.liveGetPlayServer(false);
        }
    };

    public void stopPlayDuration() {
        mHandler.removeCallbacks(mPlayDuration);
        playTime += (System.currentTimeMillis() - lastPlayTime);
        logger.d("onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
        livePlayLog.onPause(0);
    }

    /** 播放时长，7分钟统计 */
    private Runnable mPlayDuration = new Runnable() {
        @Override
        public void run() {
            if (lastPlayserverEntity != null) {
                lastPlayTime = System.currentTimeMillis();
                playTime += mPlayDurTime;
                logger.d("mPlayDuration:playTime=" + playTime / 1000);
                liveVideoReportBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "normal");
                reportPlayStarTime = System.currentTimeMillis();
            }
            if (isPlay && !activity.isFinishing()) {
                mHandler.postDelayed(this, mPlayDurTime);
            }
        }
    };

    /**
     * 得到Video缓存时间
     */
    private Runnable getVideoCachedDurationRun = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isPlay && !activity.isFinishing()) {
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long videoCachedDuration = vPlayer.getVideoCachedDuration();
                        QuestionStatic questionStatic = ProxUtil.getProxUtil().get(activity, QuestionStatic.class);
                        if (questionStatic != null) {
                            questionStatic.setVideoCachedDuration(videoCachedDuration);
                        }
                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
                        mLogtf.d("videoCachedDuration=" + videoCachedDuration);
                        if (videoCachedDuration > 10000) {
                            liveVideoReportBll.streamReport(LiveVideoReportBll.MegId.MEGID_12130, mGetInfo.getChannelname(), -1);
                            if (lastPlayserverEntity != null) {
                                liveVideoReportBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "play delay reconnect");
                                reportPlayStarTime = System.currentTimeMillis();
                            }
                        }
                    }
                });
                //logger.i( "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
            }
        }
    };

    /**
     * 播放失败，或者完成时调用
     */
    public void onFail(int arg1, final int arg2) {
        if (lastPlayserverEntity != null) {
            if (lastPlayserverEntity.isUseFlv()) {
                if (!failFlvPlayserverEntity.contains(lastPlayserverEntity)) {
                    failFlvPlayserverEntity.add(lastPlayserverEntity);
                }
            } else {
                if (!failPlayserverEntity.contains(lastPlayserverEntity)) {
                    failPlayserverEntity.add(lastPlayserverEntity);
                }
            }
        }
        liveGetPlayServer.liveGetPlayServer(false);
    }


    /**
     * 使用第三方视频提供商提供的调度接口获得第三方播放域名对应的包括ip地址的播放地址
     */
    public void dns_resolve_stream(final PlayServerEntity.PlayserverEntity playserverEntity, final PlayServerEntity
            mServer, String channelname, final AbstractBusinessDataCallBack callBack) {
        if (StringUtils.isEmpty(playserverEntity.getIp_gslb_addr())) {
            callBack.onDataFail(3, "empty");
            return;
        }
        final StringBuilder url;
        final String provide = playserverEntity.getProvide();
        if ("wangsu".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr());
        } else if ("ali".equals(provide)) {
            url = new StringBuilder("http://" + playserverEntity.getIp_gslb_addr() + "/dns_resolve_stream");
        } else {
            callBack.onDataFail(3, "other");
            return;
        }
        HttpRequestParams entity = new HttpRequestParams();


        if ("wangsu".equals(provide)) {
            String WS_URL = playserverEntity.getAddress() + "/" + mServer.getAppname() + "/" + channelname;
            entity.addHeaderParam("WS_URL", WS_URL);
            entity.addHeaderParam("WS_RETIP_NUM", "1");
            entity.addHeaderParam("WS_URL_TYPE", "3");
        } else {
            url.append("?host=" + playserverEntity.getAddress());
            url.append("&stream=" + channelname);
            url.append("&app=" + mServer.getAppname());
        }
        final AtomicBoolean haveCall = new AtomicBoolean();
        final AbstractBusinessDataCallBack dataCallBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                logger.d("dns_resolve_stream:onDataSucess:haveCall=" + haveCall.get() + ",objData=" + objData[0]);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataSucess(objData);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("dns_resolve_stream:onDataFail:haveCall=" + haveCall.get() + ",errStatus=" + errStatus +
                        ",failMsg=" + failMsg);
                if (!haveCall.get()) {
                    haveCall.set(true);
                    callBack.onDataFail(errStatus, failMsg);
                }
            }
        };
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                dataCallBack.onDataFail(0, "timeout");
            }
        }, 2000);
        mHttpManager.sendGetNoBusiness(url.toString(), entity, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                logger.i("dns_resolve_stream:onFailure=", e);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataCallBack.onDataFail(0, "onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    final int code = response.code();
                    final String r = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.i("dns_resolve_stream:onResponse:url=" + url + ",response=" + code + "," + r);
                                if (response.code() >= 200 && response.code() <= 300) {
                                    if ("wangsu".equals(provide)) {
//                        rtmp://111.202.83.208/live_server/x_3_55873?wsiphost=ipdb&wsHost=livewangsu.xescdn.com
                                        String url = r.replace("\n", "");
                                        int index1 = url.substring(7).indexOf("/");
                                        if (index1 != -1) {
                                            String host = url.substring(7, 7 + index1);
                                            playserverEntity.setIpAddress(host);
                                        }
                                        dataCallBack.onDataSucess(provide, url);
                                        return;
                                    } else {
                                        try {
                                            JSONObject jsonObject = new JSONObject(r);
                                            String host = jsonObject.getString("host");
                                            JSONArray ipArray = jsonObject.optJSONArray("ips");
                                            String ip = ipArray.getString(0);
                                            String url = "rtmp://" + ip + "/" + host + "/" + mServer.getAppname() + "/" +
                                                    mGetInfo.getChannelname();
                                            playserverEntity.setIpAddress(ip);
                                            dataCallBack.onDataSucess(provide, url);
                                            mLogtf.d("dns_resolve_stream:ip_gslb_addr=" + playserverEntity
                                                    .getIp_gslb_addr() + ",ip=" + ip);
                                            return;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dataCallBack.onDataFail(1, r);
                        }
                    });
                } catch (Exception e) {
                    dataCallBack.onDataFail(1, e.getMessage());
                }
            }
        });
    }

    /**
     * 接口失败，重新请求，判断video是不是存活
     *
     * @param r           重新请求的事件
     * @param delayMillis
     */
    private void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void onNetWorkChange(int netWorkType) {
        if (liveGetPlayServer != null) {
            liveGetPlayServer.onNetWorkChange(netWorkType);
        }
    }

    public void onDestroy() {
        if (liveGetPlayServer != null) {
            liveGetPlayServer.onDestroy();
        }
        liveVideoReportBll.onDestory();
        mPlayStatistics.clear();
    }

}
