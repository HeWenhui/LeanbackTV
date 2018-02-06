package com.xueersi.parentsmeeting.modules.livevideo.business;

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
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.IOException;

import io.agora.rtc.Constants;

/**
 * Created by linyuqiang on 2018/1/11.
 * 语音反馈
 */
public class SpeechFeedBackBll implements SpeechFeedBackAction {
    String TAG = "SpeechFeedBackBll";
    boolean isStart = false;
    Activity activity;
    RelativeLayout bottomContent;
    LiveBll liveBll;
    SpeechFeedBackPager speechFeedBackPager;
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
    private String roomId;

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

    public SpeechFeedBackBll(Activity activity, LiveBll liveBll) {
        this.activity = activity;
        this.liveBll = liveBll;
    }

    public void setBottomContent(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    @Override
    public void start(final String roomId) {
        if (isStart) {
            return;
        }
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                speechFeedBackPager = new SpeechFeedBackPager(activity);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int screenWidth = ScreenUtils.getScreenWidth();
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
                params.rightMargin = wradio;
                Loger.i(TAG,"add view");
                bottomContent.addView(speechFeedBackPager.getRootView(), params);
            }
        });
        if(!isHasPermission(activity)){
            XESToastUtils.showToast(activity,"请检查是否获取录音权限");
            return;
        }
        this.roomId=roomId;
        Loger.d(TAG, "start:roomId=" + roomId);
        isStart = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    Loger.d(TAG, "start:startRecording:mAudioRecord=" + (mAudioRecord == null));
                    //initAudioRecorder();
                    int stuid=Integer.parseInt(UserBll.getInstance().getMyUserInfoEntity().getStuId());
                    long time = System.currentTimeMillis();
                    mWorkerThread=new WorkerThread(activity, stuid);
                    mWorkerThread.eventHandler().addEventHandler(new AGEventHandler() {
                        @Override
                        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
                        }

                        @Override
                        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                            Loger.i(TAG,"joinchannelsuccess");

                        }

                        @Override
                        public void onUserOffline(int uid, int reason) {
                            Loger.i(TAG,"useroffline");
                        }

                        @Override
                        public void onError(int err) {
                            if(err==1108){
                                XESToastUtils.showToast(activity,"请检查是否获取麦克风权限");
                            }
                            Loger.i(TAG,"error"+err);
                            //SpeechFeedBackBll.this.stop();
                        }

                        @Override
                        public void onVolume(int volume) {
                            speechFeedBackPager.setVolume(volume/4);
                        }
                    });
                    mWorkerThread.start();
                    mWorkerThread.waitForReady();
                    int vProfile = Constants.VIDEO_PROFILE_120P;
                    mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
                    mWorkerThread.joinChannel(null, roomId, stuid, new WorkerThread.OnJoinChannel() {
                        @Override
                        public void onJoinChannel(int joinChannel) {
                            //VideoChatLog.sno4(liveBll, nonce, room, joinChannel);
                        }
                    });
                    //mAudioRecord.startRecording();
                    /*while (isStart) {
                        if (mAudioRecord != null) {
                            int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
                            if (readSize > 0) {
                                calculateRealVolume(mPCMBuffer, readSize);
                            }
                        }
                    }*/
                    Loger.d(TAG, "start:startRecording:end;time=" + (System.currentTimeMillis() - time));
                } catch (Exception e) {
                    Loger.e(TAG, "initAudioRecorder", e);
                }
            }
        }.start();
    }

    @Override
    public void stop() {
        if (!isStart) {
            return;
        }
        Loger.d(TAG, "stop:mAudioRecord=" + (mAudioRecord == null));
        mWorkerThread.leaveChannel(roomId, new WorkerThread.OnLevelChannel() {
            @Override
            public void onLevelChannel(int leaveChannel) {

            }
        });
        isStart = false;
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (speechFeedBackPager != null) {
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    Loger.i(TAG,"remove view");
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
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
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
    public static boolean isHasPermission(final Context context){
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
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
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
}
