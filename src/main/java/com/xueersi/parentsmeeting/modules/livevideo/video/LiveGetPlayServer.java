package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.http.CommonRequestCallBack;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

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
    LiveBll2 liveBll;
    public final int mLiveType;
    private LiveGetInfo mGetInfo;
    private PlayServerEntity mServer;
    private Callback.Cancelable mGetPlayServerCancle;
    /** 直播帧数统计 */
    TotalFrameStat totalFrameStat;
    VideoAction mVideoAction;
    LiveHttpManager mHttpManager;
    LiveHttpResponseParser mHttpResponseParser;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    LiveGetPlayServer(Activity context, LiveBll2 liveBll, int mLiveType, LiveGetInfo mGetInfo, LiveTopic liveTopic) {
        this.context = context;
        this.liveBll = liveBll;
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

    public void setTotalFrameStat(TotalFrameStat totalFrameStat) {
        this.totalFrameStat = totalFrameStat;
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
                boolean isPresent = liveBll.isPresent();
                mLogtf.d("liveGetPlayServer:isPresent=" + isPresent);
                if (!isPresent && mVideoAction != null) {
                    mVideoAction.onTeacherNotPresent(true);
                }
            }
        });
        liveGetPlayServer(mLiveTopic.getMode(), modechange);
    }

    private long lastGetPlayServer;

    public void liveGetPlayServer(final String mode, final boolean modechange) {
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            liveGetPlayServerError = true;
            return;
        }
        liveGetPlayServerError = false;
        final long before = System.currentTimeMillis();
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
            mGetInfo.setChannelname(CNANNEL_PREFIX + mGetInfo.getLiveType() + "_" + mGetInfo.getId() + "_"
                    + mGetInfo.getTeacherId());
        }
        if (totalFrameStat != null) {
            totalFrameStat.setChannelname(mGetInfo.getChannelname());
        }
        final String serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname();
        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        final StringBuilder ipsb = new StringBuilder();
        mGetPlayServerCancle = mHttpManager.liveGetPlayServer(ipsb, serverurl, new CommonRequestCallBack<String>() {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLogtf.d("liveGetPlayServer:onError:ex=" + ex + ",isOnCallback=" + isOnCallback + "," + ipsb);
                long time = System.currentTimeMillis() - before;
                if (ex instanceof HttpException) {
                    HttpException error = (HttpException) ex;
                    if (error.getCode() >= 300) {
                        mLogtf.d("liveGetPlayServer:onError:code=" + error.getCode() + ",time=" + time);
                        totalFrameStat.liveGetPlayServer(time, 3, "", ipsb, serverurl);
                        if (time < 15000) {
                            if (mVideoAction != null && mLiveTopic != null) {
                                mVideoAction.onLiveStart(null, mLiveTopic, modechange);
                            }
                            return;
                        }
                    }
                } else {
                    if (ex instanceof UnknownHostException) {
                        totalFrameStat.liveGetPlayServer(time, 1, "", ipsb, serverurl);
                        mVideoAction.onPlayError(0, PlayErrorCode.PLAY_SERVER_CODE_101);
                    } else {
                        if (ex instanceof SocketTimeoutException) {
                            totalFrameStat.liveGetPlayServer(time, 2, "", ipsb, serverurl);
                            mVideoAction.onPlayError(0, PlayErrorCode.PLAY_SERVER_CODE_102);
                        }
                    }
                    mLogtf.e("liveGetPlayServer:onError:isOnCallback=" + isOnCallback, ex);
                }
                long now = System.currentTimeMillis();
                if (now - lastGetPlayServer < 5000) {
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetPlayServer:onError retry1");
                            liveGetPlayServer(modechange);
                        }
                    }, 1000);
                } else {
                    lastGetPlayServer = now;
                    onLiveFailure("直播调度失败", new Runnable() {
                        @Override
                        public void run() {
                            mLogtf.d("liveGetPlayServer:onError retry2");
                            liveGetPlayServer(modechange);
                        }
                    });
                }
            }

            @Override
            public void onSuccess(String result) {
//                Loger.i(TAG, "liveGetPlayServer:onSuccess:result=" + result);
                String s = "liveGetPlayServer:onSuccess";
                try {
                    JSONObject object = new JSONObject(result);
                    PlayServerEntity server = mHttpResponseParser.parsePlayerServer(object);
                    if (server != null) {
                        if (totalFrameStat != null) {
                            long time = System.currentTimeMillis() - before;
                            totalFrameStat.liveGetPlayServer(time, 0, server.getCipdispatch(), ipsb, serverurl);
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
                        s += ",server=null";
                        onLiveFailure("直播调度失败", new Runnable() {

                            @Override
                            public void run() {
                                liveGetPlayServer(modechange);
                            }
                        });
                    }
                    mLogtf.d(s);
                } catch (JSONException e) {
                    MobAgent.httpResponseParserError(TAG, "liveGetPlayServer", result + "," + e.getMessage());
                    // Loger.e(TAG, "liveGetPlayServer", e);
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

    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            Loger.i(TAG, "onNetWorkChange:liveGetPlayServerError=" + liveGetPlayServerError);
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
        mHandler.postDelayed(r, delayMillis);
    }

    public void onDestroy() {
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
    }

}
