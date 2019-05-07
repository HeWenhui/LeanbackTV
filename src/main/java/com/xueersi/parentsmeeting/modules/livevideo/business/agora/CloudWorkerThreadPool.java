package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.Constants;

import com.xes.ps.rtcstream.RTCEngine;

import io.agora.rtc.video.VideoCanvas;

import static io.agora.rtc.Constants.RAW_AUDIO_FRAME_OP_MODE_READ_WRITE;

/**
 * 云平台声网调用，放在线程池里
 */
public class CloudWorkerThreadPool {
    private final static String TAG = "WorkerThreadPool";
    protected static Logger logger = LiveLoggerFactory.getLogger(TAG);
    /** 和服务器的ping，线程池 */
    private static ThreadPoolExecutor poolExecutor;
    private final Context mContext;
    /**
     * 是否有本地视频
     */
    boolean enableLocalVideo = false;
    private static final int ACTION_WORKER_THREAD_QUIT = 0X1010; // quit this thread

    private static final int ACTION_WORKER_JOIN_CHANNEL = 0X2010;

    private static final int ACTION_WORKER_LEAVE_CHANNEL = 0X2011;

    private static final int ACTION_WORKER_CONFIG_ENGINE = 0X2012;

    private static final int ACTION_WORKER_PREVIEW = 0X2014;

    private OnEngineCreate onEngineCreate;
    MyEngineEventHandler.OnLastmileQuality onLastmileQuality;
    private String appid;

    private boolean mReady;

    private RTCEngine mRtcEngine;

    public final void enablePreProcessor() {
        if (mEngineConfig.mClientRole == Constants.CLIENT_ROLE_BROADCASTER) {
            if (Constant.PRP_ENABLED) {

            }
        }
    }

    public final void setPreParameters(float lightness, float smoothness) {
        if (mEngineConfig.mClientRole == Constants.CLIENT_ROLE_BROADCASTER) {
            if (Constant.PRP_ENABLED) {
            }
        }

        Constant.PRP_DEFAULT_LIGHTNESS = lightness;
        Constant.PRP_DEFAULT_SMOOTHNESS = smoothness;

    }

    public final void disablePreProcessor() {

    }

    public interface OnJoinChannel {
        void onJoinChannel(int joinChannel);
    }

    public interface OnLeaveChannel {
        void onLeaveChannel(int leaveChannel);
    }

    /**
     * 0: 方法调用成功
     * <0: 方法调用失败
     * ERR_INVALID_ARGUMENT (-2)：传递的参数无效
     * ERR_NOT_READY (-3)：没有成功初始化
     * ERR_REFUSED (-5)：SDK不能发起通话，可能是因为处于另一个通话中，或者创建频道失败。
     *
     * @param channelKey
     * @param channel
     * @param uid
     * @param onJoinChannel
     */
    public final void joinChannel(final String channelKey, final String channel, final int uid, final OnJoinChannel onJoinChannel) {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    ensureRtcEngineReadyLock();
                } catch (Exception e) {
                    onJoinChannel.onJoinChannel(-11111);
                    return;
                }
                int joinChannel = mRtcEngine.joinRoom();
                onJoinChannel.onJoinChannel(joinChannel);
                logger.d("joinChannel:channelKey=" + channelKey + ",channel=" + channel + ",uid=" + uid + ",joinChannel="
                        + joinChannel);
                mEngineConfig.mChannel = channel;

                enablePreProcessor();
                logger.d("joinChannel " + channel + " " + uid);
            }
        });
    }

    public final void leaveChannel(final String channel, final OnLeaveChannel onLeaveChannel) {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mRtcEngine != null) {
                    mRtcEngine.leaveRoom();
                }

                disablePreProcessor();

                int clientRole = mEngineConfig.mClientRole;
                mEngineConfig.reset();
                logger.d("leaveChannel " + channel + " " + clientRole);
            }
        });
    }

    private EngineConfig mEngineConfig;
    private String token;

    public final EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    private final CloudEngineEventHandler mEngineEventHandler;

    public final void configEngine(final int cRole, final int vProfile) {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ensureRtcEngineReadyLock();
                } catch (Exception e) {
                    return;
                }
                mEngineConfig.mClientRole = cRole;
                mEngineConfig.mVideoProfile = vProfile;
                logger.d("configEngine " + cRole + " " + mEngineConfig.mVideoProfile);
            }
        });
    }

    public final void preview(final boolean start, final SurfaceView view, final int uid) {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ensureRtcEngineReadyLock();
                } catch (Exception e) {
                    return;
                }
                if (start) {
                    mRtcEngine.setupLocalVideo(view);
                    mRtcEngine.startPreview();
                } else {
                    mRtcEngine.stopPreview();
                }
            }
        });
    }

    public static String getDeviceID(Context context) {
        // XXX according to the API docs, this value may change after factory reset
        // use Android id as device id
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }


    private RTCEngine ensureRtcEngineReadyLock() throws Exception {
        if (mRtcEngine == null) {
            String appId;
            if (StringUtils.isEmpty(appid)) {
                if (AppConfig.DEBUG) {
                    appId = mContext.getString(R.string.agora_private_app_id_debug);
                } else {
                    appId = mContext.getString(R.string.agora_private_app_id_release);
                }
            } else {
                appId = appid;
            }
            if (TextUtils.isEmpty(appId)) {
                throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
            }
            logger.d("ensureRtcEngineReadyLock:appId=" + appId);
            mRtcEngine = new RTCEngine(mContext, mEngineEventHandler.rtcEngineEventListener);
            mRtcEngine.initWithToken("");
            mRtcEngine.enableVideo();
            mRtcEngine.enableLocalVideo(enableLocalVideo);
//            mRtcEngine.disableVideo();
            if (onEngineCreate != null) {
                onEngineCreate.onEngineCreate(mRtcEngine);
            }
//            if (onLastmileQuality != null) {
//                mEngineEventHandler.setOnLastmileQuality(new MyEngineEventHandler.OnLastmileQuality() {
//                    @Override
//                    public void onLastmileQuality(int quality) {
//                        onLastmileQuality.onLastmileQuality(quality);
////                        mRtcEngine.disableLastmileTest();
//                    }
//
//                    @Override
//                    public void onQuit() {
//                        onLastmileQuality.onQuit();
//                    }
//                });
//                mRtcEngine.enableLastmileTest();
//            }
        }
        return mRtcEngine;
    }

    public CloudEngineEventHandler eventHandler() {
        return mEngineEventHandler;
    }

    public RTCEngine getRtcEngine() {
        return mRtcEngine;
    }

    public void execute(Runnable runnable) {
        poolExecutor.execute(runnable);
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    public final void exit() {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.d("exit() > start");
                mReady = false;
                mRtcEngine.destory();
                logger.d("exit() > end");
            }
        });
        // TODO should remove all pending(read) messages
    }

    public CloudWorkerThreadPool(Context context, int mUid, String token) {
        this.mContext = context;
        this.token = token;
        this.mEngineConfig = new EngineConfig();
        this.mEngineConfig.mUid = mUid;
        this.mEngineEventHandler = new CloudEngineEventHandler(mContext);
        if (poolExecutor == null) {
            poolExecutor = new ThreadPoolExecutor(1, 1,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r, "agora-Pool-" + r) {
                        @Override
                        public synchronized void start() {
                            logger.d("newThread:start:id=" + getId());
                            super.start();
                        }
                    };
                    logger.d("newThread:r=" + r);
                    return thread;
                }
            }, new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    logger.d("rejectedExecution:r=" + r);
                }
            });
            poolExecutor.allowCoreThreadTimeOut(true);
        }
    }

    public void setEnableLocalVideo(boolean enableLocalVideo) {
        this.enableLocalVideo = enableLocalVideo;
    }

    public void setOnEngineCreate(OnEngineCreate onEngineCreate) {
        this.onEngineCreate = onEngineCreate;
    }

    public interface OnEngineCreate {
        void onEngineCreate(RTCEngine mRtcEngine);
    }

    public void enableLastmileTest(MyEngineEventHandler.OnLastmileQuality onLastmileQuality) {
        this.onLastmileQuality = onLastmileQuality;
        try {
            ensureRtcEngineReadyLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
