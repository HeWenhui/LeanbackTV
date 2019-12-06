package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.io.File;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import static io.agora.rtc.Constants.RAW_AUDIO_FRAME_OP_MODE_READ_WRITE;

/**
 * @deprecated
 * 使用 WorkerThreadPool 同步，这个exit耗时
 */
@Deprecated
public class WorkerThread extends Thread {
    private final static String TAG = "WorkerThread";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    private final Context mContext;
    /**
     * 是否声音回调
     */
    boolean audioCallBack = false;
    /**
     * 是否有本地视频
     */
    boolean enableLocalVideo = false;
    private static final int ACTION_WORKER_THREAD_QUIT = 0X1010; // quit this thread

    private static final int ACTION_WORKER_JOIN_CHANNEL = 0X2010;

    private static final int ACTION_WORKER_LEAVE_CHANNEL = 0X2011;

    private static final int ACTION_WORKER_CONFIG_ENGINE = 0X2012;

    private static final int ACTION_WORKER_PREVIEW = 0X2014;

    /**
     * 是否使用自采集音频
     */
    private boolean isExternalAudio;
    private OnEngineCreate onEngineCreate;
    MyEngineEventHandler.OnLastmileQuality onLastmileQuality;
    private String appid;

    private static final class WorkerThreadHandler extends Handler {

        private WorkerThread mWorkerThread;

        WorkerThreadHandler(WorkerThread thread) {
            this.mWorkerThread = thread;
        }

        public void release() {
            mWorkerThread = null;
        }

        @Override
        public void handleMessage(Message msg) {
            if (this.mWorkerThread == null) {
                logger.e("handler is already released! " + msg.what);
                return;
            }

            switch (msg.what) {
                case ACTION_WORKER_THREAD_QUIT:
                    mWorkerThread.exit();
                    break;
                case ACTION_WORKER_JOIN_CHANNEL: {
                    Object[] data = (Object[]) msg.obj;
                    mWorkerThread.joinChannel((String) data[0], (String) data[1], msg.arg1, (OnJoinChannel) data[2]);
                }
                break;
                case ACTION_WORKER_LEAVE_CHANNEL:
                    Object[] data = (Object[]) msg.obj;
                    mWorkerThread.leaveChannel((String) data[0], (OnLeaveChannel) data[1]);
                    break;
                case ACTION_WORKER_CONFIG_ENGINE:
                    Object[] configData = (Object[]) msg.obj;
                    mWorkerThread.configEngine((int) configData[0], (int) configData[1]);
                    break;
                case ACTION_WORKER_PREVIEW:
                    Object[] previewData = (Object[]) msg.obj;
                    mWorkerThread.preview((boolean) previewData[0], (SurfaceView) previewData[1], (int) previewData[2]);
                    break;
            }
        }
    }

    private WorkerThreadHandler mWorkerHandler;

    private boolean mReady;

    public final void waitForReady() {
        while (!mReady) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.d("wait for " + WorkerThread.class.getSimpleName());
        }
    }

    @Override
    public void run() {
        logger.d("start to run");
        Looper.prepare();

        mWorkerHandler = new WorkerThreadHandler(this);

//        ensureRtcEngineReadyLock();

        mReady = true;

        // enter thread looper
        Looper.loop();
    }

    private RtcEngine mRtcEngine;

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
    public final void joinChannel(String channelKey, final String channel, int uid, OnJoinChannel onJoinChannel) {
        if (Thread.currentThread() != this) {
            logger.w("joinChannel() - worker thread asynchronously " + channelKey + "," + channel + " " + uid);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_JOIN_CHANNEL;
            envelop.obj = new Object[]{channelKey, channel, onJoinChannel};
            envelop.arg1 = uid;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        try {
            ensureRtcEngineReadyLock();
        } catch (Exception e) {
            onJoinChannel.onJoinChannel(-11111);
            return;
        }
        int joinChannel = mRtcEngine.joinChannel(null, channel, "OpenLive", uid);
        onJoinChannel.onJoinChannel(joinChannel);
        logger.d("joinChannel:channelKey=" + channelKey + ",channel=" + channel + ",uid=" + uid + ",joinChannel="
                + joinChannel);
        mEngineConfig.mChannel = channel;

        enablePreProcessor();
        logger.d("joinChannel " + channel + " " + uid);
    }

    public final void leaveChannel(String channel, OnLeaveChannel onLeaveChannel) {
        if (Thread.currentThread() != this && mWorkerHandler != null) {
            logger.w("leaveChannel() - worker thread asynchronously " + channel);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_LEAVE_CHANNEL;
            envelop.obj = new Object[]{channel, onLeaveChannel};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        if (mRtcEngine != null) {
            int leaveChannel = mRtcEngine.leaveChannel();
            onLeaveChannel.onLeaveChannel(leaveChannel);
        }

        disablePreProcessor();

        int clientRole = mEngineConfig.mClientRole;
        mEngineConfig.reset();
        logger.d("leaveChannel " + channel + " " + clientRole);
    }

    private EngineConfig mEngineConfig;

    public final EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    private final MyEngineEventHandler mEngineEventHandler;

    public final void configEngine(int cRole, int vProfile) {
        if (Thread.currentThread() != this) {
            logger.w("configEngine() - worker thread asynchronously " + cRole + " " + vProfile);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_CONFIG_ENGINE;
            envelop.obj = new Object[]{cRole, vProfile};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        try {
            ensureRtcEngineReadyLock();
        } catch (Exception e) {
            return;
        }
        mEngineConfig.mClientRole = cRole;
        mEngineConfig.mVideoProfile = vProfile;
        if (vProfile > 0) {
            mRtcEngine.setVideoProfile(mEngineConfig.mVideoProfile, true);
        }

        mRtcEngine.setClientRole(cRole);
        if (audioCallBack) {
            mRtcEngine.enableAudioVolumeIndication(500, 3,false);
            mRtcEngine.muteAllRemoteAudioStreams(true);
        }
        logger.d("configEngine " + cRole + " " + mEngineConfig.mVideoProfile);
    }

    public final void preview(boolean start, SurfaceView view, int uid) {
        if (Thread.currentThread() != this) {
            logger.w("preview() - worker thread asynchronously " + start + " " + view + " " + (uid & 0XFFFFFFFFL));
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_PREVIEW;
            envelop.obj = new Object[]{start, view, uid};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        try {
            ensureRtcEngineReadyLock();
        } catch (Exception e) {
            return;
        }
        if (start) {
            mRtcEngine.setupLocalVideo(new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid));
            mRtcEngine.startPreview();
        } else {
            mRtcEngine.stopPreview();
        }
    }

    public static String getDeviceID(Context context) {
        // XXX according to the API docs, this value may change after factory reset
        // use Android id as device id
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    private RtcEngine ensureRtcEngineReadyLock() throws Exception {
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
            mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
//            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            mRtcEngine.enableVideo();
            mRtcEngine.enableLocalVideo(enableLocalVideo);
//            mRtcEngine.disableVideo();
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/parentsmeeting/agoralog");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mRtcEngine.setLogFile(new File(dir, "agora-rtc.log").getPath());
            mRtcEngine.enableDualStreamMode(true);
            if (isExternalAudio) {
                mRtcEngine.setRecordingAudioFrameParameters(16000, 1, RAW_AUDIO_FRAME_OP_MODE_READ_WRITE, 1024);
                mRtcEngine.setExternalAudioSource(true, 16000, 1);
            }
            if (onEngineCreate != null) {
                onEngineCreate.onEngineCreate(mRtcEngine);
            }
            if (onLastmileQuality != null) {
                mEngineEventHandler.setOnLastmileQuality(new MyEngineEventHandler.OnLastmileQuality() {
                    @Override
                    public void onLastmileQuality(int quality) {
                        onLastmileQuality.onLastmileQuality(quality);
//                        mRtcEngine.disableLastmileTest();
                    }

                    @Override
                    public void onQuit() {
                        onLastmileQuality.onQuit();
                    }
                });
                mRtcEngine.enableLastmileTest();
            }
        }
        return mRtcEngine;
    }

    public MyEngineEventHandler eventHandler() {
        return mEngineEventHandler;
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    public void execute(Runnable runnable) {
        mWorkerHandler.post(runnable);
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    public final void exit() {
        if (Thread.currentThread() != this) {
            logger.w("exit() - exit app thread asynchronously");
            mWorkerHandler.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT);
            return;
        }

        mReady = false;
        RtcEngine.destroy();
        // TODO should remove all pending(read) messages

        logger.d("exit() > start");

        // exit thread looper
        Looper.myLooper().quit();

        mWorkerHandler.release();

        logger.d("exit() > end");
    }

    public WorkerThread(Context context, int mUid, boolean audioCallBack) {
        this.mContext = context;
        this.audioCallBack = audioCallBack;
        this.mEngineConfig = new EngineConfig();
        this.mEngineConfig.mUid = mUid;
        this.mEngineEventHandler = new MyEngineEventHandler(mContext, this.mEngineConfig, audioCallBack);
    }

    public void setEnableLocalVideo(boolean enableLocalVideo) {
        this.enableLocalVideo = enableLocalVideo;
    }

    /**
     * @param context
     * @param mUid
     * @param feadback
     * @param isExternalAudio 是否使用自采集音频数据
     */
    public WorkerThread(Context context, int mUid, boolean feadback, boolean isExternalAudio) {
        this(context, mUid, feadback);
        this.isExternalAudio = isExternalAudio;
    }

    public void setOnEngineCreate(OnEngineCreate onEngineCreate) {
        this.onEngineCreate = onEngineCreate;
    }

    public interface OnEngineCreate {
        void onEngineCreate(RtcEngine mRtcEngine);
    }

    public void enableLastmileTest(MyEngineEventHandler.OnLastmileQuality onLastmileQuality) {
        this.onLastmileQuality = onLastmileQuality;
        try {
            ensureRtcEngineReadyLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableLastmileTest() {
        if (onLastmileQuality != null) {
            onLastmileQuality.onQuit();
        }
        if (mRtcEngine != null) {
            mRtcEngine.disableLastmileTest();
        }
    }
}
