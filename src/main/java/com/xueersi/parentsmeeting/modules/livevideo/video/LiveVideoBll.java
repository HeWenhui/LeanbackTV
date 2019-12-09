package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.RTCVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.ProgressAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.LiveLogBill;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VPlayerListenerReg;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoPlayDebugUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by linyuqiang on 2018/6/22.
 * 直播bll
 * <p>
 * 所有跟直播中视频播放有关的逻辑处理（不处理任何ui）都在这里操作。
 */
public class LiveVideoBll implements VPlayerListenerReg, ProgressAction {
    private final String TAG = "LiveVideoBll";
    private Logger logger = LoggerFactory.getLogger(TAG);
    /** 直播服务器 */
    private PlayServerEntity mServer;
    private LiveGetInfo mGetInfo;
    private int isFlatfish = 0;
    private int lastIndex;
    /** 直播服务器选择 */
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private ArrayList<PlayServerEntity.PlayserverEntity> failPlayserverEntity = new ArrayList<>();
    private ArrayList<PlayServerEntity.PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
    private BasePlayerFragment videoFragment;
    private Activity activity;
    private TeacherIsPresent teacherIsPresent;
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
    /**
     * 可能是LiveFragmentBase或者
     */
    private VideoAction mVideoAction;
    private int mLiveType;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    public LiveVideoBll(Activity activity, TeacherIsPresent teacherIsPresent, int liveType) {
        this.activity = activity;
        this.teacherIsPresent = teacherIsPresent;
        this.mLiveType = liveType;
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        ProxUtil.getProxUtil().put(activity, VPlayerListenerReg.class, this);
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public void setHttpManager(LiveHttpManager httpManager) {
        this.mHttpManager = httpManager;
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

    /**
     * 这里是给不能直接访问livevideoBll的类使用的,会Gone掉VideoView，除非有这个需求，否则慎用.
     * 切记，调用该方法，必须与playVideoWithViewVisible配对。
     * 只是单纯释放player的话，使用{@link BasePlayerFragment#release()}方法
     */
    @Override
    public void releaseWithViewGone() {
        VideoPlayDebugUtils.umsIfVideoViewIsNotVisible(activity, activity.findViewById(R.id.vv_course_video_video));
        View view = activity.findViewById(R.id.vv_course_video_video);
        if (view != null) {
//            Map<String, String> map = new HashMap<>();
//            map.put("videoview", "gone");
//            UmsAgentManager.umsAgentDebug(activity, "livevideo_videoview", map);
            VideoPlayDebugUtils.umsVideoViewGone(activity, view);
            view.setVisibility(View.GONE);
        }
        stopPlay();
    }

    /**
     * 播放视频并且恢复VideoView未Visibile。
     * 与releaseWithViewGone配合使用
     */
    @Override
    public void playVideoWithViewVisible() {
        if (MediaPlayer.getIsNewIJK()) {

            View view = activity.findViewById(R.id.vv_course_video_video);
            if (view != null) {
                VideoPlayDebugUtils.umsIfVideoViewIsNotVisible(activity, activity.findViewById(R.id.vv_course_video_video));
//                Map<String, String> map = new HashMap<>();
//                map.put("videoview", "visible");
//                UmsAgentManager.umsAgentDebug(activity, "livevideo_videoview", map);
                VideoPlayDebugUtils.umsVideoViewVisible(activity, view);
                view.setVisibility(View.VISIBLE);
            }
            psRePlay(false);
        }
    }

    public void setVideoAction(VideoAction mVideoAction) {
        this.mVideoAction = mVideoAction;
    }

    private LiveTopic mLiveTopic;

    /** 在{@link LiveBll2}获取getInfo成功而之后,{@link LiveBll2#onGetInfoSuccess(LiveGetInfo)} */
    public void onLiveInit(LiveGetInfo getInfo, LiveTopic liveTopic) {
        this.mGetInfo = getInfo;
        isFlatfish = getInfo.getIsFlatfish();
        this.mLiveTopic = liveTopic;
        liveGetPlayServer = new LiveGetPlayServer(activity, new TeacherIsPresent() {

            @Override
            public boolean isPresent() {
                return teacherIsPresent.isPresent();
            }
        }, mLiveType, getInfo, liveTopic);
        liveGetPlayServer.setVideoAction(mVideoAction);
        if (!isGroupClass()) {
            liveGetPlayServer(liveTopic.getMode(), false);
        }

        if (mGetInfo != null) {//设置日志需要的liveid
            LiveLogBill.getInstance().setLiveId(mGetInfo.getId());
        }
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
    }

    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        this.mServer = server;
    }

    public void psRePlay(boolean modeChange) {
        if (isGroupClass() && positon >= 0) {
            playGroupClassVideo();
            return;
        }
        if (nowProtol != MediaPlayer.VIDEO_PROTOCOL_RTMP && nowProtol != MediaPlayer.VIDEO_PROTOCOL_FLV) {
            nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
            videoFragment.playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
//            VideoConfigEntity videoConfigEntity = mGetInfo.getVideoConfigEntity();
//            videoConfigEntity.setUserName(mGetInfo.getUname());
//            videoConfigEntity.setUserId(mGetInfo.getStuId());
//            if (videoConfigEntity != null) {
            videoFragment.enableAutoSpeedPlay(getVideoConfigEntity());
//            }
        } else {
            //这里不能进行协议切换，因为协议切换已经在自动切换线路的时候切换好了
//            if (nowProtol == MediaPlayer.VIDEO_PROTOCOL_RTMP) {
//                nowProtol = MediaPlayer.VIDEO_PROTOCOL_FLV;
//            } else {
//                nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
//            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoFragment.playPSVideo(mGetInfo.getChannelname(), nowProtol);
//                    VideoConfigEntity videoConfigEntity = mGetInfo.getVideoConfigEntity();
//                    videoConfigEntity.setUserName(mGetInfo.getUname());
//                    videoConfigEntity.setUserId(mGetInfo.getStuId());
//                    if (videoConfigEntity != null) {
                    videoFragment.enableAutoSpeedPlay(getVideoConfigEntity());
//                    }
                    MediaPlayer.setNextDispatchTime();
                }
            }, MediaPlayer.getDispatchTime());

        }
    }

    private VideoConfigEntity getVideoConfigEntity() {
        VideoConfigEntity videoConfigEntity = mGetInfo.getVideoConfigEntity();
        if (videoConfigEntity != null) {
            videoConfigEntity.
                    setUserName(mGetInfo.getUname()).
                    setUserId(mGetInfo.getStuId());
        }
        return videoConfigEntity;
    }

    @Deprecated
    /** 直接指定为具体线路只去播放 */
    public void playNewVideo(int pos) {
        if (!MediaPlayer.getIsNewIJK()) {

        } else {
            videoFragment.changePlayLive(pos, MediaPlayer.VIDEO_PROTOCOL_RTMP);
        }
    }

    /** 当前使用的协议,初始值为-1 */
    private int nowProtol = MediaPlayer.VIDEO_PROTOCOL_NO_PROTOL;
    /** 当前处于哪条线路 */
    private int nowPos;

    /**
     * 用户指定切换至哪条线路
     *
     * @param pos
     */
    public void changeLine(int pos) {
        this.nowPos = pos;
        if (nowProtol == MediaPlayer.VIDEO_PROTOCOL_NO_PROTOL) {
            //初始化
            nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
//            videoFragment.playPSVideo(mGetInfo.getChannelname(), nowProtol);
            liveGetPlayServer.liveGetPlayServer(false);
            return;
        }
        //当前线路小于总线路数
        if (pos < total) {
            videoFragment.changePlayLive(pos, nowProtol);
        } else {
            nowProtol = changeProtol(nowProtol);
            liveGetPlayServer.liveGetPlayServer(false);
//            videoFragment.playPSVideo(mGetInfo.getChannelname(), nowProtol);
        }
    }

    /**
     * PSIJK 自动切换线路
     * //     * 缓冲超时{@link #mBufferTimeOutRun}
     * <p>
     * //     * 起播超时{@link #mOpenTimeOutRun}
     * <p>
     * 视频播放失败有很多种情况，目前只有鉴权失败和调度失败需要playLive，其他全部都是changLive
     */
//    public void changeNextLine() {
//        this.nowPos++;
//        if (nowProtol == MediaPlayer.VIDEO_PROTOCOL_NO_PROTOL) {
//            //初始化
//            nowProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
////            videoFragment.playPSVideo(mGetInfo.getChannelname(), nowProtol);
//            liveGetPlayServer.liveGetPlayServer(false);
//            return;
//        }
//        //当前线路小于总线路数
//        if (this.nowPos < total) {
//            videoFragment.changePlayLive(this.nowPos, nowProtol);
//        } else {
//            nowProtol = changeProtol(nowProtol);
////            videoFragment.playPSVideo(mGetInfo.getChannelname(), nowProtol);
//            liveGetPlayServer.liveGetPlayServer(false);
//        }
//    }

    /**
     * 切换到当前线路，用于接麦
     */
    public void changeNowLine() {
        videoFragment.changePlayLive(this.nowPos, nowProtol);
    }

    /** 得到转化的协议 */
    public int changeProtol(int now) {
        int tempProtol;
        if (now == MediaPlayer.VIDEO_PROTOCOL_RTMP) {
            tempProtol = MediaPlayer.VIDEO_PROTOCOL_FLV;
        } else {
            tempProtol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
        }
        return tempProtol;
    }

    public VPlayerCallBack.VPlayerListener getPlayListener() {
        return mPlayListener;
    }

    /**
     * 当前线路，一共多少线路
     */
    private int total;
    private VPlayerCallBack.VPlayerListener mPlayListener = new VPlayerCallBack.SimpleVPlayerListener() {

        /**
         * 获取调度接口失败
         */
//        @Override
//        public void getPServerListFail() {
//            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
//                vPlayerListener.getPServerListFail();
//            }
//            mVideoAction.getPServerListFail();
//        }
        @Override
        public void getPSServerList(int cur, int total, boolean modeChange) {
//            liveGetPlayServer.mVideoAction.onLiveStart();
            LiveVideoBll.this.nowPos = cur;
            LiveVideoBll.this.total = total;
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.getPSServerList(cur, total, modeChange);
            }

            mVideoAction.getPSServerList(cur, total, modeChange);

        }

        @Override
        public void onPlaying(long currentPosition, long duration) {
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                mLogtf.d("onPlaying:startRemote");
                stopPlay();
            }
            if (isGroupClass()) {
                int current = (int) currentPosition / 1000;
                logger.d("onPlaying(): current = " + current + ", positon = " + positon);
                if ((positon - current) > 10) {
                    seekGroupClass();
                }
            }
        }

        @Override
        public void onPlaybackComplete() {
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
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
            onFail(0, MediaErrorInfo.PLAY_COMPLETE);
        }

        @Override
        public void onPlayError() {
            mLogtf.d("onPlayError");
            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
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
            if (isGroupClass()) {
                if (isClassEnd()) {
                    videoFragment.release();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            videoFragment.playComplete();
                            if (mVideoAction != null) {
                                mVideoAction.onTeacherNotPresent(false);
                            }
                            mLogtf.d("[zhangyuansun] onOpenSuccess: close RTCVideoPager");
                            //关闭1v2真流
                            RTCVideoAction rtcVideoAction = ProxUtil.getProxUtil().get(activity, RTCVideoAction.class);
                            if (rtcVideoAction != null) {
                                rtcVideoAction.close();
                            }
                        }
                    });
                } else {
                    seekGroupClass();
                }
            }
            VideoChatEvent videoChatEvent = ProxUtil.getProxUtil().get(activity, VideoChatEvent.class);
            if (videoChatEvent != null && videoChatEvent.getStartRemote().get()) {
                mLogtf.d("onOpenSuccess:startRemote=true");
                stopPlay();
                return;
            }
            lastPlayTime = System.currentTimeMillis();
            reportPlayStarTime = System.currentTimeMillis();
            openSuccess = true;
            MediaPlayer.setLastDispatchTimeBlanking();
//            mHandler.removeCallbacks(mOpenTimeOutRun);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onOpenSuccess();
            }
            mHandler.removeCallbacks(mPlayDuration);
            mLogtf.d("onOpenSuccess:url=" + vPlayer.getUri() + ",playTime=" + playTime);
            mHandler.postDelayed(mPlayDuration, mPlayDurTime);
            mHandler.removeCallbacks(getVideoCachedDurationRun);
            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
            if (isFlatfish == 1) {
                currentSeiTimetamp = -1;
                mHandler.removeCallbacks(getCurrentSeiTimetamp);
                mHandler.postDelayed(getCurrentSeiTimetamp, 10000);
            }
        }

        @Override
        public void onOpenStart() {
            mLogtf.d("onOpenStart:url=" + vPlayer.getUri());
            openStartTime = System.currentTimeMillis();
            openSuccess = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
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
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            mHandler.removeCallbacks(mPlayDuration);
            onFail(arg1, arg2);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onOpenFailed(arg1, arg2);
            }
            mLogtf.d("onOpenFailed:url=" + vPlayer.getUri() + ",arg2=" + arg2);
            if (lastPlayserverEntity != null) {
                reportPlayStarTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onBufferStart() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onBufferStart();
            }
            mLogtf.d("onBufferStart");
        }

        @Override
        public void onBufferComplete() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
                vPlayerListener.onBufferComplete();
            }
            mLogtf.d("onBufferComplete");
        }
    };

    public void stopPlay() {
        if (isInitialized()) {
            vPlayer.releaseSurface();
            vPlayer.stop();
        }
    }

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (vPlayer != null && vPlayer.isInitialized());
    }

    /**
     * 打开超时,也就是起播超时
     */
//    private Runnable mOpenTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            if (isInitialized()) {
//                vPlayer.releaseSurface();
//                vPlayer.stop();
//            }
//            mVideoAction.onPlayError(0, PlayErrorCode.PLAY_TIMEOUT_300);
//            long openTimeOut = System.currentTimeMillis() - openStartTime;
//            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
//
//            Map<String, String> map = new HashMap<>();
//            map.put("param", "openTimeOut");
//            map.put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_VIDEO_FAIL);
//            UmsAgentManager.umsAgentDebug(activity, LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map);
//            if (MediaPlayer.getIsNewIJK()) {
//                changeNextLine();
//            } else {
//                liveGetPlayServer.liveGetPlayServer(false);
//            }
//        }
//    };

//    public void liveGetPlayServer() {
//        liveGetPlayServer.liveGetPlayServer(false);
//    }

    /**
     * 缓冲超时
     */
//    private Runnable mBufferTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            if (isInitialized()) {
//                vPlayer.releaseSurface();
//                vPlayer.stop();
//            }
//            long openTime = System.currentTimeMillis() - openStartTime;
//            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
//            for (VPlayerCallBack.VPlayerListener vPlayerListener : mPlayStatistics) {
//                if (vPlayerListener instanceof LiveVPlayerListener) {
//                    LiveVPlayerListener vPlayerListener1 = (LiveVPlayerListener) vPlayerListener;
//                    vPlayerListener1.onBufferTimeOutRun();
//                }
//            }
//            if (!MediaPlayer.getIsNewIJK()) {
//                liveGetPlayServer.liveGetPlayServer(false);
//            } else {
////                changeLine(nowPos + 1);
//                changeNextLine();
//            }
//        }
//    };
    public void stopPlayDuration() {
        mHandler.removeCallbacks(mPlayDuration);
        currentSeiTimetamp = -1;
        mHandler.removeCallbacks(getCurrentSeiTimetamp);
        playTime += (System.currentTimeMillis() - lastPlayTime);
        logger.d("onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
    }

    /** 播放时长，7分钟统计 */
    private Runnable mPlayDuration = new Runnable() {
        @Override
        public void run() {
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
                    }
                });
                //logger.i( "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
            }
        }
    };

    /** 视频时间 */
    private long currentSeiTimetamp = -2;

    public long getCurrentSeiTimetamp() {
        return currentSeiTimetamp;
    }

    /**
     * 得到Video播放时间戳
     */
    private Runnable getCurrentSeiTimetamp = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isPlay && !activity.isFinishing()) {
                currentSeiTimetamp = vPlayer.getCurrentSeiTimetamp();
                logger.i("getCurrentSeiTimetamp:time=" + currentSeiTimetamp);
                mHandler.postDelayed(getCurrentSeiTimetamp, 500);
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
        if (!MediaPlayer.getIsNewIJK()) {
            liveGetPlayServer.liveGetPlayServer(false);
        } else {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!teacherIsPresent.isPresent() && mVideoAction != null) {
                        mVideoAction.onTeacherNotPresent(true);
                    }
                }
            });
            switch (arg2) {
                case MediaErrorInfo.PSPlayerError: {
                    mVideoAction.onPlayError(MediaErrorInfo.PSPlayerError, PlayErrorCode.PLAY_SERVER_CODE_101);
                    //播放器错误
//                    autoChangeNextLine();
                    scheduleRePlay(1000);
                    break;
                }
                case MediaErrorInfo.PSDispatchFailed: {
                    //调度失败，建议重新访问playLive或者playVod频道不存在
                    //调度失败，延迟1s再次访问调度

//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            nowProtol = changeProtol(nowProtol);
//                            psRePlay(false);
//                        }
//                    }, 1000);
                    scheduleRePlay(1000);
                }
                break;

                case MediaErrorInfo.PSChannelNotExist: {
                    //提示用户等待,交给上层来处理

                    break;
                }
                case MediaErrorInfo.PSServer403: {
                    //防盗链鉴权失败，需要重新访问playLive或者playVod
//                    playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
//                    liveGetPlayServer.liveGetPlayServer(false);
                    psRePlay(false);
                }
                break;
                case MediaErrorInfo.PLAY_COMPLETE: {
//                    playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_RTMP);
//                    liveGetPlayServer.liveGetPlayServer(false);
                    if (isGroupClass()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mVideoAction != null) {
                                    mVideoAction.onTeacherNotPresent(false);
                                }
                                mLogtf.d("[zhangyuansun] PLAY_COMPLETE: close RTCVideoPager");
                                //关闭1v2真流
                                RTCVideoAction rtcVideoAction = ProxUtil.getProxUtil().get(activity, RTCVideoAction.class);
                                if (rtcVideoAction != null) {
                                    rtcVideoAction.close();
                                }
                                //关闭红包
                                RedPackageAction redPackageAction = ProxUtil.getProxUtil().get(activity, RedPackageAction.class);
                                if (redPackageAction != null) {
                                    redPackageAction.onRemoveRedPackage();
                                }
                            }
                        });
                    } else {
                        psRePlay(false);
                    }
                }
                break;
                default:
                    //除了这四种情况，还有播放失败的情况
//                    autoChangeNextLine();
                    scheduleRePlay(1000);
                    break;
            }
        }
//        else {
//            changeLine(nowPos + 1);
//        }
    }

    private void scheduleRePlay(int ms) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nowProtol = changeProtol(nowProtol);
                psRePlay(false);
            }
        }, ms);
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

    /** 网络发生变化 */
    public void onNetWorkChange(int netWorkType) {
        videoFragment.onNetWorkChange(netWorkType);
        if (liveGetPlayServer != null) {
            liveGetPlayServer.onNetWorkChange(netWorkType);
        }
    }

    public void onDestroy() {
        if (liveGetPlayServer != null) {
            liveGetPlayServer.onDestroy();
        }
        mPlayStatistics.clear();
    }

    private int positon;

    @Override
    public void onProgressChanged(int progress) {
        positon = progress;
        if (positon == 0) {
            playGroupClassVideo();
        }
    }

    @Override
    public void onProgressBegin(int beginProgress) {
        positon = beginProgress;
        if (positon >= 0) {
            playGroupClassVideo();
        }
    }

    private void playGroupClassVideo() {
        logger.d("playGroupClassVideo()");
        //英语1v2录直播 播放网络文件
        String videoPath = mGetInfo.getRecordStandliveEntity().getVideoPath();
        videoFragment.playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
    }

    private void seekGroupClass() {
        logger.d("seekGroupClass()");
        videoFragment.seekTo(positon * 1000);
    }

    private boolean isGroupClass() {
        logger.d("isGroupClass()");
        return mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_GROUP_CLASS;
    }

    private boolean isClassEnd() {
        int duration = (int) vPlayer.getDuration() / 1000;
        mLogtf.d("isGroupClassEnd(): duration = " + duration + ", positon = " + positon);
        return (duration > 0 && duration < positon);
    }
}
