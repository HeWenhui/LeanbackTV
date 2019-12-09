package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.analytics.umsagent.DeviceInfo;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page.SpeechFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.RtcEngine;

/**
 * Created by linyuqiang on 2018/1/11.
 * 语音反馈
 */
public class SpeechFeedBackBll implements SpeechFeedBackAction {
    String TAG = "SpeechFeedBackBll";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    LogToFile logToFile;
    boolean isStart = false;
    Activity activity;
    RelativeLayout bottomContent;
    SpeechFeedBackHttp liveBll;
    LiveAndBackDebug liveAndBackDebug;
    LiveGetInfo mGetInfo;
    SpeechFeedBackPager speechFeedBackPager;
    private String nonce;
    /** 每次读取的字节大小 */
    private int mBufferSize;
    /** 采样率 */
    private static final int DEFAULT_SAMPLING_RATE = 16000;
    /** 设置为单声道 */
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    /** 音频格式 */
    private static final PCMFormat DEFAULT_AUDIO_FORMAT = PCMFormat.PCM_16BIT;
    /** 设置每8000帧作为一个周期，通知一下需要编码 */
    private static final int FRAME_COUNT = 8000;
    /** 录音源 */
    private static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /** 录音对象 */
    private AudioRecord mAudioRecord = null;
    /** 原始录音数据 */
    private short[] mPCMBuffer;
    private WorkerThread mWorkerThread;
    FileOutputStream outputStream;
    //    RtcEngine mRtcEngine;
    File saveVideoFile;
    private String roomId;
    private long joinTime;
    long startTime;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    private void initAudioRecorder() throws IOException {
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat());
        int bytesPerFrame = DEFAULT_AUDIO_FORMAT.getBytesPerFrame();
        int frameSize = mBufferSize / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT);
        }
        mBufferSize = frameSize * bytesPerFrame;
        mPCMBuffer = new short[mBufferSize];

        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);
    }

    public SpeechFeedBackBll(Activity activity, SpeechFeedBackHttp liveBll) {
        this.activity = activity;
        this.liveBll = liveBll;
        logToFile = new LogToFile(activity, TAG);
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
    }

    @Override
    public void setNonce(String s) {
        this.nonce = s;
    }

    public void setBottomContent(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }


    @Override
    public void start(final String roomId) {
        if (isStart) {
            return;
        }
        isStart = true;
        this.roomId = roomId;
        logToFile.d("start:roomId=" + roomId);
        final List<PermissionItem> unList = new ArrayList<>();
        List<PermissionItem> unList2 = XesPermission.checkPermissionUnPerList(activity, new LiveActivityPermissionCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {
                umsagentCommand(2, "on", 0);
            }

            @Override
            public void onGuarantee(String permission, int position) {
                unList.remove(0);
                if (unList.isEmpty()) {
                    if (isStart && roomId.equals(SpeechFeedBackBll.this.roomId)) {
                        umsagentCommand(2, "on", 1);
                        startVoice();
                    }
                }
            }
        }, PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);
        logToFile.d("start:unList2=" + unList2.size());
        unList.addAll(unList2);
        if (unList.isEmpty()) {
            umsagentCommand(2, "on", 1);
            startVoice();
        }
    }

    private void startVoice() {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                logToFile.i("start:speechFeedBackPager=" + (speechFeedBackPager == null));
                speechFeedBackPager = new SpeechFeedBackPager(activity);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int screenWidth = ScreenUtils.getScreenWidth();
                int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
                params.rightMargin = wradio;
                logger.i( "start:addView");
                bottomContent.addView(speechFeedBackPager.getRootView(), params);
            }
        });
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.d( "start:startRecording:mAudioRecord=" + (mAudioRecord == null));
                    //initAudioRecorder();
                    int stuid = Integer.parseInt(LiveAppUserInfo.getInstance().getStuId());
                    long time = System.currentTimeMillis();
                    mWorkerThread = new WorkerThread(activity, stuid, true);
                    try {
                        File alldir = LiveCacheFile.geCacheFile(activity, "speechfeed");
                        if (!alldir.exists()) {
                            alldir.mkdirs();
                        }
                        saveVideoFile = new File(alldir, "ise" + System.currentTimeMillis() + ".pcm");
                        outputStream = new FileOutputStream(saveVideoFile);
                        mWorkerThread.setOnEngineCreate(new WorkerThread.OnEngineCreate() {
                            @Override
                            public void onEngineCreate(RtcEngine mRtcEngine) {
//                                SpeechFeedBackBll.this.mRtcEngine = mRtcEngine;
                                mRtcEngine.registerAudioFrameObserver(new IAudioFrameObserver() {
                                    @Override
                                    public boolean onRecordFrame(byte[] bytes, int i, int i1, int i2, int i3) {
                                        if (outputStream != null) {
                                            try {
                                                outputStream.write(bytes);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return false;
                                    }

                                    @Override
                                    public boolean onPlaybackFrame(byte[] bytes, int i, int i1, int i2, int i3) {
                                        return false;
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        logger.d( "start:setOnEngineCreate", e);
                    }
                    mWorkerThread.eventHandler().addEventHandler(agEventHandler);
                    mWorkerThread.start();
                    mWorkerThread.waitForReady();
                    int vProfile = Constants.VIDEO_PROFILE_120P;
                    mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
                    logToFile.i("onJoinChannel:isStart=" + isStart + ",roomId=" + roomId);
                    if (isStart) {
                        mWorkerThread.joinChannel(null, roomId, stuid, new WorkerThread.OnJoinChannel() {
                            @Override
                            public void onJoinChannel(int joinChannel) {
                                logToFile.i("onJoinChannel:joinChannel=" + joinChannel + ",isStart=" + isStart);
                                //VideoChatLog.sno4(liveBll, nonce, room, joinChannel);
                                if (!isStart) {
                                    mWorkerThread.leaveChannel(roomId, new WorkerThread.OnLeaveChannel() {
                                        @Override
                                        public void onLeaveChannel(int leaveChannel) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                    //mAudioRecord.startRecording();
                    /*while (isStart) {
                        if (mAudioRecord != null) {
                            int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
                            if (readSize > 0) {
                                calculateRealVolume(mPCMBuffer, readSize);
                            }
                        }
                    }*/
                    logger.d( "start:startRecording:end;time=" + (System.currentTimeMillis() - time));
                } catch (Exception e) {
                    logger.e( "initAudioRecorder", e);
                }
            }
        });
    }

    private AGEventHandler agEventHandler = new AGEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            logToFile.i("joinchannelsuccess:channel=" + channel + ",uid=" + uid);
            joinTime = System.currentTimeMillis();
            umsagentJoin();
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
//                            String mainTeacherId = mGetInfo.getMainTeacherId();
//                            logger.i( "onUserJoined:uid=" + uid + ",mainTeacherId=" + mainTeacherId);
//                            if (!("" + uid).equals(mainTeacherId)) {
//                                int mute = mWorkerThread.getRtcEngine().muteRemoteAudioStream(uid, true);
//                                logger.i( "onUserJoined:uid=" + uid + ",mute=" + mute);
//                            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            logToFile.i("useroffline:uid=" + uid + ",reason=" + reason);
        }

        @Override
        public void onError(int err) {
            if (err == 1108) {
                XESToastUtils.showToast(activity, "请检查是否获取麦克风权限");
            }
            umsagentError(err);
            logToFile.i("onError:err=" + err);
            //SpeechFeedBackBll.this.stop();
        }

        @Override
        public void onVolume(int volume) {
            speechFeedBackPager.setVolume(volume / 4);
        }
    };

    @Override
    public void stop() {
        if (!isStart) {
            return;
        }
        isStart = false;
        umsagentCommand(5, "off", 1);
        logToFile.d("stop:mAudioRecord=" + (mAudioRecord == null) + ",mWorkerThread=" + (mWorkerThread == null));
        if (mWorkerThread != null) {
//            if (mRtcEngine != null) {
//                mRtcEngine.registerAudioFrameObserver(null);
//            }
            mWorkerThread.leaveChannel(roomId, new WorkerThread.OnLeaveChannel() {
                @Override
                public void onLeaveChannel(int leaveChannel) {
                    umsagentLeave();
                }
            });
            mWorkerThread.eventHandler().removeEventHandler(agEventHandler);
            mWorkerThread.exit();
            try {
                mWorkerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.d( "stop:close", e);
            }
            outputStream = null;
            long time = System.currentTimeMillis() - startTime;
            int netWorkType = NetWorkHelper.getNetWorkState(activity);
            StableLogHashMap hashMap = new StableLogHashMap("uploadfile");
            hashMap.put("time", "" + time);
            hashMap.put("networktype", "" + netWorkType);
            hashMap.put("length", "" + saveVideoFile.length());
            liveAndBackDebug.umsAgentDebugSys("live_voice", hashMap.getData());
            final File finalFile = saveVideoFile;
            XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(activity);
            CloudUploadEntity uploadEntity = new CloudUploadEntity();
            uploadEntity.setFilePath(finalFile.getPath());
            uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
            uploadEntity.setCloudPath(CloudDir.LIVE_FEED_BACK);
            if (netWorkType == NetWorkHelper.WIFI_STATE) {
                xesCloudUploadBusiness.asyncUpload(uploadEntity, new XesStsUploadListener() {
                    @Override
                    public void onProgress(XesCloudResult result, int percent) {

                    }

                    @Override
                    public void onSuccess(XesCloudResult result) {
                        finalFile.delete();
                        logger.d( "asyncUpload:onSuccess=" + result.getHttpPath());
                        String service = DeviceInfo.getDeviceName();
                        liveBll.saveStuTalkSource(result.getHttpPath(), service);
//                    http://testmv.xesimg.com/app/live_feed_back/2018/04/27/31203_1524811986079_ise1524811975319.mp3
                    }

                    @Override
                    public void onError(XesCloudResult result) {
                        logger.d( "asyncUpload:onError=" + result);
                    }
                });
            }
        }
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (speechFeedBackPager != null) {
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    logger.i( "remove view");
                    bottomContent.removeView(speechFeedBackPager.getRootView());
                    speechFeedBackPager = null;
                }
            });
        }
    }

    /**
     * 计算录音音量
     *
     * @param buffer   buffer
     * @param readSize readSize
     */
    private void calculateRealVolume(short[] buffer, int readSize) {
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            int volume = (int) Math.sqrt(amplitude);
            volume = (volume * 30 / 10000);
            volume = (volume > 30 ? 30 : volume);
            if (speechFeedBackPager != null) {
                speechFeedBackPager.setVolume(volume * 3);
            }
        }
    }

    @Override
    public void setVideoLayout(int width, int height) {
        if (speechFeedBackPager != null) {
            final View contentView = activity.findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            int screenHeight = ScreenUtils.getScreenHeight();
            if (width > 0) {
                int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
                wradio += (screenWidth - width) / 2;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechFeedBackPager.getRootView().getLayoutParams();
                if (wradio != params.rightMargin) {
                    params.rightMargin = wradio;
                    LayoutParamsUtil.setViewLayoutParams(speechFeedBackPager.getRootView(), params);
                }
            }
        }
    }

    /**
     * 判断是是否有录音权限
     */
    public static boolean isHasPermission(final Context context) {
        int audioSource = MediaRecorder.AudioSource.MIC;
        // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
        int sampleRateInHz = 44100;
        // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        // 缓冲区字节大小
        int bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }

    /**
     * 接收语音反馈指令日志
     *
     * @param sno
     * @param cmd
     * @param micStatus
     */
    private void umsagentCommand(int sno, String cmd, int micStatus) {
        HashMap<String, String> map = new HashMap<>();
        map.put("sno", "" + sno);
        map.put("nonce", nonce);
        map.put("stable", "1");
        map.put("logtype", "voiceInterationCmd");
        map.put("command", cmd);
        map.put("status", "" + micStatus);
        map.put("channelname", roomId);
        liveAndBackDebug.umsAgentDebugSys("live_voice", map);
    }

    /**
     * 加入连麦房间日志
     */
    private void umsagentJoin() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sno", "3");
        map.put("nonce", nonce);
        map.put("stable", "1");
        map.put("ex", "Y");
        map.put("logtype", "joinChannelSuccess");
        map.put("channelname", roomId);
        liveAndBackDebug.umsAgentDebugSys("live_voice", map);
    }

    /**
     * 离开连麦房间日志
     */
    private void umsagentLeave() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sno", "6");
        map.put("nonce", nonce);
        map.put("stable", "1");
        map.put("ex", "Y");
        map.put("logtype", "leaveChannel");
        map.put("channelname", roomId);
        map.put("duration", "" + (System.currentTimeMillis() - joinTime) / 1000);
        liveAndBackDebug.umsAgentDebugSys("live_voice", map);
    }

    /**
     * 连麦错误日志
     *
     * @param errCode
     */
    private void umsagentError(int errCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nonce", nonce);
        map.put("stable", "2");
        map.put("logtype", "voiceInterationError");
        map.put("errcode", "" + errCode);
        liveAndBackDebug.umsAgentDebugSys("live_voice", map);
    }
}
