package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.File;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class WorkerThread extends Thread {
    private final static String TAG = "WorkerThread";

    private final Context mContext;
    boolean feadback = false;
    private static final int ACTION_WORKER_THREAD_QUIT = 0X1010; // quit this thread

    private static final int ACTION_WORKER_JOIN_CHANNEL = 0X2010;

    private static final int ACTION_WORKER_LEAVE_CHANNEL = 0X2011;

    private static final int ACTION_WORKER_CONFIG_ENGINE = 0X2012;

    private static final int ACTION_WORKER_PREVIEW = 0X2014;

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
                Loger.e(TAG, "handler is already released! " + msg.what);
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
                    mWorkerThread.leaveChannel((String) data[0], (OnLevelChannel) data[1]);
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
            Loger.d(TAG, "wait for " + WorkerThread.class.getSimpleName());
        }
    }

    @Override
    public void run() {
        Loger.d(TAG, "start to run");
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

    public interface OnLevelChannel {
        void onLevelChannel(int leaveChannel);
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
            Loger.w(TAG, "joinChannel() - worker thread asynchronously " + channelKey + "," + channel + " " + uid);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_JOIN_CHANNEL;
            envelop.obj = new Object[]{channelKey, channel, onJoinChannel};
            envelop.arg1 = uid;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
        int joinChannel = mRtcEngine.joinChannel(null, channel, "OpenLive", uid);
        onJoinChannel.onJoinChannel(joinChannel);
        Loger.d(TAG, "joinChannel:channelKey=" + channelKey + ",channel=" + channel + ",uid=" + uid + ",joinChannel=" + joinChannel);
        mEngineConfig.mChannel = channel;

        enablePreProcessor();
        Loger.d(TAG, "joinChannel " + channel + " " + uid);
    }

    public final void leaveChannel(String channel, OnLevelChannel onLevelChannel) {
        if (Thread.currentThread() != this) {
            Loger.w(TAG, "leaveChannel() - worker thread asynchronously " + channel);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_LEAVE_CHANNEL;
            envelop.obj = new Object[]{channel, onLevelChannel};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        if (mRtcEngine != null) {
            int leaveChannel = mRtcEngine.leaveChannel();
            onLevelChannel.onLevelChannel(leaveChannel);
        }

        disablePreProcessor();

        int clientRole = mEngineConfig.mClientRole;
        mEngineConfig.reset();
        Loger.d(TAG, "leaveChannel " + channel + " " + clientRole);
    }

    private EngineConfig mEngineConfig;

    public final EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    private final MyEngineEventHandler mEngineEventHandler;

    public final void configEngine(int cRole, int vProfile) {
        if (Thread.currentThread() != this) {
            Loger.w(TAG, "configEngine() - worker thread asynchronously " + cRole + " " + vProfile);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_CONFIG_ENGINE;
            envelop.obj = new Object[]{cRole, vProfile};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
        mEngineConfig.mClientRole = cRole;
        mEngineConfig.mVideoProfile = vProfile;

        mRtcEngine.setVideoProfile(mEngineConfig.mVideoProfile, true);

        mRtcEngine.setClientRole(cRole, "");
        if (feadback) {
            mRtcEngine.enableAudioVolumeIndication(500, 3);
        }
        Loger.d(TAG, "configEngine " + cRole + " " + mEngineConfig.mVideoProfile);
    }

    public final void preview(boolean start, SurfaceView view, int uid) {
        if (Thread.currentThread() != this) {
            Loger.w(TAG, "preview() - worker thread asynchronously " + start + " " + view + " " + (uid & 0XFFFFFFFFL));
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_PREVIEW;
            envelop.obj = new Object[]{start, view, uid};
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();
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

    private RtcEngine ensureRtcEngineReadyLock() {
        if (mRtcEngine == null) {
            String appId;
            if (AppConfig.DEBUG) {
                appId = mContext.getString(R.string.agora_private_app_id_debug);
            } else {
                appId = mContext.getString(R.string.agora_private_app_id_release);
            }
            if (TextUtils.isEmpty(appId)) {
                throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
            }
            Loger.d(TAG, "ensureRtcEngineReadyLock:appId=" + appId);
            mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
//            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            mRtcEngine.enableVideo();
            mRtcEngine.enableLocalVideo(false);
//            mRtcEngine.disableVideo();
            File dir = new File(Environment.getExternalStorageDirectory()
                    + "/parentsmeeting/agoralog");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mRtcEngine.setLogFile(new File(dir, "agora-rtc.log").getPath());
            mRtcEngine.enableDualStreamMode(true);
        }
        return mRtcEngine;
    }

    public MyEngineEventHandler eventHandler() {
        return mEngineEventHandler;
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    public final void exit() {
        if (Thread.currentThread() != this) {
            Loger.w(TAG, "exit() - exit app thread asynchronously");
            mWorkerHandler.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT);
            return;
        }

        mReady = false;

        // TODO should remove all pending(read) messages

        Loger.d(TAG, "exit() > start");

        // exit thread looper
        Looper.myLooper().quit();

        mWorkerHandler.release();

        Loger.d(TAG, "exit() > end");
    }

    public WorkerThread(Context context, int mUid, boolean feadback) {
        this.mContext = context;
        this.feadback = feadback;
        this.mEngineConfig = new EngineConfig();
        this.mEngineConfig.mUid = mUid;
        this.mEngineEventHandler = new MyEngineEventHandler(mContext, this.mEngineConfig, feadback);
    }
}
