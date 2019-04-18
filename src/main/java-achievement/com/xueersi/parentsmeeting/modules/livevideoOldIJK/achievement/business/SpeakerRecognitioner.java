package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.speakerrecognition.SpeakerRecognitionerInterface;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/9/10.
 * 新的声纹注册
 */
public class SpeakerRecognitioner {
    String TAG = "SpeakerRecognitioner";
    private final Object lock = new Object();
    Logger logger = LoggerFactory.getLogger("SpeakerRecognitioner");
    /** 和服务器的ping，线程池 */
    private ThreadPoolExecutor pingPool;
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
    private byte[] mPCMBuffer;
    boolean isStart = false;
    int index = 1;
    boolean destory = false;
    Context context;
    private SpeakerPredict speakerPredict;
    SpeakerRecognitionerInterface speakerRecognitionerInterface;
    private LogToFile logToFile;
    private AtomicBoolean audioRequest;

    public SpeakerRecognitioner(Context context, AtomicBoolean audioRequest) {
        logger.setLogMethod(false);
        this.audioRequest = audioRequest;
        this.context = context;
        logToFile = new LogToFile(context, TAG);
        pingPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, "SpeakerRecognitioner-" + r) {
                    @Override
                    public synchronized void start() {
                        logger.d("newThread:start");
                        super.start();
                    }
                };
                logger.d("newThread:r=" + r);
                return thread;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
    }

    public void setSpeakerPredict(SpeakerPredict speakerPredict) {
        this.speakerPredict = speakerPredict;
    }

    private void initAudioRecorder() throws IOException {
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat());
        int bytesPerFrame = DEFAULT_AUDIO_FORMAT.getBytesPerFrame();
        int frameSize = mBufferSize / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT);
        }
        mBufferSize = frameSize * bytesPerFrame;
        if (mBufferSize < 24000) {
            mPCMBuffer = new byte[24000];
        } else {
            mPCMBuffer = new byte[mBufferSize];
        }
        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);
    }

    private int lastReadSize;

    public void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        pingPool.execute(new Runnable() {
            @Override
            public void run() {
                logToFile.d("start:audioRequest=" + audioRequest.get() + ",destory=" + destory);
                if (audioRequest.get() || destory) {
                    return;
                }
                speakerRecognitionerInterface = SpeakerRecognitionerInterface
                        .getInstance();
                MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                String stuId = userInfoEntity.getStuId();
                if (mAudioRecord == null) {
                    try {
                        initAudioRecorder();
                    } catch (Exception e) {
                        logToFile.e("start:initAudioRecorder", e);
                        return;
                    }
                }
                try {
                    mAudioRecord.startRecording();
                } catch (Exception e) {
                    logToFile.e("start:startRecording", e);
                    return;
                }
                while (isStart) {
                    if (mAudioRecord != null) {
                        int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
//                                byte[] pcm_data = toByteArray(mPCMBuffer, readSize);
//                            logger.d("start:predict=" + readSize + ",pcm_data=" + pcm_data.length);
                        synchronized (lock) {
                            if (destory || audioRequest.get()) {
                                logToFile.d("start:predict=" + destory + ",Request=" + audioRequest.get());
                                return;
                            }
                            //小于0是错误码
                            if (readSize > 0) {
                                String predict = speakerRecognitionerInterface.predict(mPCMBuffer, readSize, index++, stuId, false);
                                if (!StringUtils.isEmpty(predict)) {
                                    logger.d("start:predict=" + predict);
                                    if (speakerPredict != null) {
                                        speakerPredict.onPredict(predict);
                                    }
                                }
                            } else {
                                if(lastReadSize!=readSize){
                                    logToFile.d("start:readSize=" + readSize);
                                }
                                lastReadSize=readSize;
                            }
                        }
                    }
                }
            }
        });
    }

    public void stop() {
        logToFile.d("stop:isStart=" + isStart);
        isStart = false;
        if (mAudioRecord != null) {
            try {
                mAudioRecord.stop();
                mAudioRecord.release();
            } catch (Exception e) {

            }
            mAudioRecord = null;
        }
    }

    public void destory() {
        logToFile.d("destory:isStart=" + isStart);
        synchronized (lock) {
            destory = true;
            if (speakerRecognitionerInterface != null) {
                speakerRecognitionerInterface.speakerRecognitionerFree();
            }
        }
    }

    public interface SpeakerPredict {
        void onPredict(String predict);
    }
}
