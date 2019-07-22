package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassLiveActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.xutils.xutils.common.Callback;

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
    private VideoAction mVideoAction;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    /** 是否使用PS的播放器 */
//    private int isPSPlayer = 1;
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

    public void setVideoAction(VideoAction mVideoAction) {
        this.mVideoAction = mVideoAction;
    }

    /**
     * 调度，使用LiveTopic的mode
     * <p>
     * 旁听不使用这个
     *
     * @param modechange
     */
    public void liveGetPlayServer(boolean modechange) {
        liveGetPlayServer(mLiveTopic.getMode(), modechange);
    }

    /**
     * 1. {@link LiveBll2#onGetInfoSuccess(LiveGetInfo)}
     * 2. 回调失败走onError之后，{@link #liveGetPlayServer(boolean)}
     * 3. {@link LiveVideoBll#onModeChange(String, boolean)}
     *
     * @param mode
     * @param modechange
     */
    public void liveGetPlayServer(final String mode, final boolean modechange) {
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
        final String serverurl = mGetInfo.getGslbServerUrl() + "?cmd=live_get_playserver&userid=" + mGetInfo.getStuId()
                + "&username=" + mGetInfo.getUname() + "&channelname=" + mGetInfo.getChannelname() + "&cType=1";
        mLogtf.d("liveGetPlayServer:serverurl=" + serverurl);
        if (mGetPlayServerCancle != null) {
            mGetPlayServerCancle.cancel();
            mGetPlayServerCancle = null;
        }
        if (mVideoAction instanceof LiveFragmentBase) {
            ((LiveFragmentBase) mVideoAction).psRePlay(modechange);
        }
        if (mVideoAction instanceof AuditClassLiveActivity) {
            ((AuditClassLiveActivity) mVideoAction).rePlay(modechange);
        }
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

    /**
     * 网络发生变化的监听
     *
     * @param netWorkType
     */
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            logger.i("onNetWorkChange:liveGetPlayServerError=" + liveGetPlayServerError);
            if (liveGetPlayServerError) {
                liveGetPlayServerError = false;
                liveGetPlayServer(mLiveTopic.getMode(), false);
            }
        } else {
            liveGetPlayServerError = true;
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
