package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.xueersi.common.http.CommonRequestCallBack;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayFailCode;
import com.xueersi.parentsmeeting.modules.livevideo.video.URLDNS;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveThreadPoolExecutor;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.Callback;
import org.xutils.xutils.ex.HttpException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by linyuqiang on 2018/6/23.
 */
public class LiveGetPlayServer {
    private String TAG = "LiveGetPlayServer";
    Logger logger = LoggerFactory.getLogger(TAG);
    private LogToFile mLogtf;
    /** 网络类型 */
    private int netWorkType;
    /** 调度是不是在无网络下失败 */
    private boolean liveGetPlayServerError = false;
    Activity context;
    private final LiveTopic mLiveTopic;
    /** 渠道前缀 */
    private final String CNANNEL_PREFIX = "x_";
    private TeacherIsPresent isPresent;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private PlayServerEntity mServer;
    private Callback.Cancelable mGetPlayServerCancle;
    /** 直播帧数统计 */
    LivePlayLog livePlayLog;
    VideoAction mVideoAction;
    LiveHttpManager mHttpManager;
    LiveHttpResponseParser mHttpResponseParser;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    public LiveGetPlayServer(Activity context, TeacherIsPresent isPresent, int mLiveType, LiveGetInfo mGetInfo, LiveTopic liveTopic) {
        this.context = context;
        this.isPresent = isPresent;
        this.mLiveType = mLiveType;
        this.mGetInfo = mGetInfo;
        this.mLiveTopic = liveTopic;
        mLogtf = new LogToFile(context, TAG);
        mLogtf.clear();
        netWorkType = NetWorkHelper.getNetWorkState(context);
        logger.d("LiveGetPlayServer:netWorkType=" + netWorkType);
    }

    public void setHttpManager(LiveHttpManager httpManager) {
        this.mHttpManager = httpManager;
    }

    public void setHttpResponseParser(LiveHttpResponseParser httpResponseParser) {
        this.mHttpResponseParser = httpResponseParser;
    }

    public void setLivePlayLog(LivePlayLog livePlayLog) {
        this.livePlayLog = livePlayLog;
    }

    public void setVideoAction(VideoAction mVideoAction) {
        this.mVideoAction = mVideoAction;
    }

    /**
     * 调度，使用LiveTopic的mode
     *
     * @param modechange
     */
    public void liveGetPlayServer(boolean modechange) {
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean isPresent = LiveGetPlayServer.this.isPresent.isPresent();
                mLogtf.d("liveGetPlayServer:isPresent=" + isPresent);
                if (!isPresent && mVideoAction != null) {
                    mVideoAction.onTeacherNotPresent(true);
                }
            }
        });
        liveGetPlayServer(mLiveTopic.getMode(), modechange);
    }

    private long lastGetPlayServer;

    /**
     * 1. {@link LiveBll2#onGetInfoSuccess(LiveGetInfo)}
     * 2. 回调失败走onError之后，{@link #liveGetPlayServer(boolean)}
     * 3. {@link LiveVideoBll#onModeChange(String, boolean)}
     *
     * @param mode
     * @param modechange
     */
    public void liveGetPlayServer(final String mode, final boolean modechange) {
        mHandler.removeCallbacks(timeLiveGetPlay);
        if (timeLiveGetPlay.modechange != modechange) {
            timeLiveGetPlay.modechange = modechange;
            liveGetPlayTime = 0;
        }
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            liveGetPlayServerError = true;
            return;
        }
        liveGetPlayServerError = false;
        final long before = SystemClock.elapsedRealtime();
        // http://gslb.xueersi.com/xueersi_gslb/live?cmd=live_get_playserver&userid=000041&username=xxxxxx
        // &channelname=88&remote_ip=116.76.97.244
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            String channelname = "";
            if (mLiveType != 3) {
                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
                        + mGetInfo.getTeacherId();
            } else {
                channelname = CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId();
            }
            mGetInfo.setChannelname(channelname);
        } else {
            if (mGetInfo.ePlanInfo == null) {
                mGetInfo.setChannelname(CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
                        + mGetInfo.getTeacherId());
            } else {
                mGetInfo.setChannelname(CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.ePlanInfo.ePlanId + "_"
                        + mGetInfo.ePlanInfo.eTeacherId);
            }
        }
        if (livePlayLog != null) {
            livePlayLog.setChannelname(mGetInfo.getChannelname());
        }
        final String serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname() + "&cType=1";
        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        final URLDNS urldns = new URLDNS();
        mGetPlayServerCancle = mHttpManager.liveGetPlayServer(urldns, serverurl, new CommonRequestCallBack<String>() {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLogtf.d("liveGetPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback + "," + urldns);
                long time = SystemClock.elapsedRealtime() - before;
                if (ex instanceof HttpException) {
                    HttpException error = (HttpException) ex;
                    if (error.getCode() >= 300) {
                        mLogtf.d("liveGetPlayServer:onError:code=" + error.getCode() + ",time=" + time);
                        livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode20, 20, "", urldns, serverurl);
                        if (time < 15000) {
                            if (mVideoAction != null && mLiveTopic != null) {
                                mVideoAction.onLiveStart(null, mLiveTopic, modechange);
                            }
                            return;
                        }
                    }
                } else {
                    if (ex instanceof UnknownHostException) {
                        livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode10, 10, "", urldns, serverurl);
                        mVideoAction.onPlayError(0, PlayErrorCode.PLAY_SERVER_CODE_101);
                    } else {
                        if (ex instanceof SocketTimeoutException) {
                            livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode15, PlayFailCode.TIME_OUT, "", urldns, serverurl);
                            mVideoAction.onPlayError(0, PlayErrorCode.PLAY_SERVER_CODE_102);
                        }
                    }
                    mLogtf.e("liveGetPlayServer:onError:isOnCallback=" + isOnCallback, ex);
                }
                long now = System.currentTimeMillis();
                if (now - lastGetPlayServer < 5000) {
                    onLiveFailureRunnable.setModeChange(modechange);
                    onLiveFailureRunnable.setLogInfo("liveGetPlayServer:onError retry1");
                    postDelayedIfNotFinish(onLiveFailureRunnable, 1000);
                } else {
                    lastGetPlayServer = now;
                    onLiveFailureRunnable.setModeChange(modechange);
                    onLiveFailureRunnable.setLogInfo("liveGetPlayServer:onError retry2");
                    postDelayedIfNotFinish(onLiveFailureRunnable, 0);
//                    onLiveFailure("直播调度失败", new Runnable() {
//                        @Override
//                        public void run() {
//                            mLogtf.d("liveGetPlayServer:onError retry2");
//                            liveGetPlayServer(modechange);
//                        }
//                    });
                }
            }

            @Override
            public void onSuccess(String result) {
//                logger.i( "liveGetPlayServer:onSuccess:result=" + result);
                String s = "liveGetPlayServer:onSuccess";
                try {
                    JSONObject object = new JSONObject(result);
                    PlayServerEntity server = mHttpResponseParser.parsePlayerServer(object);
                    if (server != null) {
                        s += ",code=" + server.getCode();
                        if (server.getCode() == 200) {
                            liveGetPlayTime = 0;
                            if (livePlayLog != null) {
                                long time = SystemClock.elapsedRealtime() - before;
                                livePlayLog.liveGetPlayServer(time, PlayFailCode.PlayFailCode0, 0, server.getCipdispatch(), urldns, serverurl);
                            }
                            s += ",mode=" + mode + ",server=" + server.getAppname() + ",rtmpkey=" + server.getRtmpkey();
                            if (LiveTopic.MODE_CLASS.equals(mode)) {
                                mGetInfo.setSkeyPlayT(server.getRtmpkey());
                            } else {
                                mGetInfo.setSkeyPlayF(server.getRtmpkey());
                            }
                            mServer = server;
                            if (mVideoAction != null && mLiveTopic != null) {
                                mVideoAction.onLiveStart(server, mLiveTopic, modechange);
                            }
                        } else {
                            postDelayedIfNotFinish(timeLiveGetPlay, 10000);
                        }
                    } else {
                        s += ",server=null,result=" + result;
                        onLiveFailureRunnable.setModeChange(modechange);
                        postDelayedIfNotFinish(onLiveFailureRunnable, 0);
//                        onLiveFailure("直播调度失败", new Runnable() {
//
//                            @Override
//                            public void run() {
//                                liveGetPlayServer(modechange);
//                            }
//                        });
                    }
                    mLogtf.d(s);
                } catch (JSONException e) {
                    MobAgent.httpResponseParserError(TAG, "liveGetPlayServer", result + "," + e.getMessage());
                    // logger.e( "liveGetPlayServer", e);
                    mLogtf.e("liveGetPlayServer", e);
                    onLiveFailure("直播调度失败", new Runnable() {

                        @Override
                        public void run() {
                            liveGetPlayServer(modechange);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

        });
    }

    private OnLiveFailureRunnable onLiveFailureRunnable = new OnLiveFailureRunnable();

    private class OnLiveFailureRunnable implements Runnable {

        private boolean modechange;
        private String strLogInfo;

        public void setModeChange(boolean modechange) {
            this.modechange = modechange;
        }

        private void setLogInfo(String logInfo) {
            this.strLogInfo = logInfo;
        }

        @Override
        public void run() {
//           (new Runnable() {
//                @Override
//                public void run() {
            mLogtf.d(strLogInfo);
            liveGetPlayServer(modechange);

        }
//            }, 1000);
//        }
    }

    ;
    private Runnable postDelayedIfNotFinishRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private class PostDelayedIfNotFinishRunnable implements Runnable {

        @Override
        public void run() {

        }
    }

    private long liveGetPlayTime = 0;
    private long maxTime = 30 * 60 * 1000;

    private class TimeLiveGetPlay implements Runnable {
        boolean modechange = false;

        @Override
        public void run() {
            if (liveGetPlayTime == 0) {
                liveGetPlayTime = System.currentTimeMillis() - 10000;
            }
            long time = System.currentTimeMillis() - liveGetPlayTime;
            if (time > maxTime) {
                liveGetPlayTime = 0;
                mLogtf.d("timeLiveGetPlay:time1=" + time);
                if (mVideoAction != null) {
                    mVideoAction.onLiveTimeOut();
                }
            } else {
                logger.d("timeLiveGetPlay:time2=" + time);
                liveGetPlayServer(modechange);
            }
        }
    }

    private TimeLiveGetPlay timeLiveGetPlay = new TimeLiveGetPlay();

    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            logger.i("onNetWorkChange:liveGetPlayServerError=" + liveGetPlayServerError);
            if (liveGetPlayServerError) {
                liveGetPlayServerError = false;
                liveGetPlayServer(mLiveTopic.getMode(), false);
            }
        }
    }

    private void onLiveFailure(String msg, Runnable runnable) {
        if (runnable == null) {
            showToast(msg);
        } else {
            showToast(msg + "，稍后重试");
            postDelayedIfNotFinish(runnable, 1000);
        }
    }

    /**
     * 弹出toast，判断Video是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        ActivityStatic activityStatic = (ActivityStatic) context;
        if (activityStatic.isResume()) {
            XESToastUtils.showToast(context, text);
        }
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (context.isFinishing()) {
            return;
        }
        mHandler.removeCallbacks(r);
        mHandler.postDelayed(r, delayMillis);
    }

    public void onDestroy() {
        mHandler.removeCallbacks(timeLiveGetPlay);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
    }

}
