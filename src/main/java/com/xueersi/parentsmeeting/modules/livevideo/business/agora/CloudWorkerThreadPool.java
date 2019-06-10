package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.SurfaceView;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.Constants;

import com.xes.ps.rtcstream.RTCEngine;

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
    boolean enableLocalAudio = false;
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
     * @param onJoinChannel
     */
    public final void joinChannel(final OnJoinChannel onJoinChannel) {
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
                enablePreProcessor();
            }
        });
    }

    public final void leaveChannel() {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mRtcEngine != null) {
                    mRtcEngine.leaveRoom();
                }
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

    public final void preview(final boolean start, final SurfaceView view) {
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

    public void setAppid(String appid) {
        this.appid = appid;
    }

    private RTCEngine ensureRtcEngineReadyLock() throws Exception {
        if (mRtcEngine == null) {
            mRtcEngine = new RTCEngine(mContext, mEngineEventHandler.rtcEngineEventListener);
            int init = mRtcEngine.initWithToken(token);
            if (init != 0) {
                mRtcEngine = null;
                onEngineCreate.onEngineCreate(null);
                return null;
            }
            mRtcEngine.enableVideo();
            mRtcEngine.enableLocalVideo(enableLocalVideo);
            mRtcEngine.muteLocalAudio(!enableLocalAudio);
//            try {
//                Field field = mRtcEngine.getClass().getDeclaredField("mRtcEngine");
//                field.setAccessible(true);
//                Object object = field.get(mRtcEngine);
//                Method method = object.getClass().getDeclaredMethod("enableAudioVolumeIndication", Integer.TYPE, Integer.TYPE);
//                method.invoke(object, 500, 3);
//            } catch (Exception e) {
//                logger.d("ensureRtcEngineReadyLock", e);
//            }
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
                if (mRtcEngine != null) {
                    mRtcEngine.destory();
                }
                logger.d("exit() > end");
            }
        });
        // TODO should remove all pending(read) messages
    }

    public CloudWorkerThreadPool(Context context, String token) {
        this.mContext = context;
        this.token = token;
        this.mEngineConfig = new EngineConfig();
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

    public void setEnableLocalAudio(boolean enableLocalAudio) {
        this.enableLocalAudio = enableLocalAudio;
    }

    public void start() {
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ensureRtcEngineReadyLock();
                } catch (Exception e) {
                    e.printStackTrace();
                    onEngineCreate.onEngineCreate(null);
                }
            }
        });
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
