package com.xueersi.parentsmeeting.modules.livevideo.service;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;
import com.tal.speech.speechrecognigen.ISpeechRecognitnGen;
import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.speakerrecognition.SpeakerRecognitionerInterface;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeechRecogGenBinder extends ISpeechRecognitnGen.Stub {
    private Logger logger = LoggerFactory.getLogger("SpeechRecogGenBinder");
    private final Object lock = new Object();
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
    private boolean isStart = false;
    boolean init = false;
    private int index = 1;
    private boolean destory = false;
    private Context context;
    //    private SpeakerPredict speakerPredict;
    private SpeakerRecognitionerInterface speakerRecognitionerInterface;
    private AtomicBoolean audioRequest;

    public SpeechRecogGenBinder() {
        audioRequest = new AtomicBoolean();
        logger.d("SpeechRecogGenBinder");
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

    @Override
    public void start(final ISpeechRecognitnCall iSpeechRecognitnCall) throws RemoteException {
        logger.d("start");
        if (isStart) {
            return;
        }
        audioRequest.set(false);
        isStart = true;
        pingPool.execute(new Runnable() {
            @Override
            public void run() {
                if (audioRequest.get() || destory) {
                    logger.d("start:audioRequest=" + audioRequest + ",destory=" + destory);
                    return;
                }
                speakerRecognitionerInterface = SpeakerRecognitionerInterface
                        .getInstance();
                boolean result = speakerRecognitionerInterface.init();
                logger.d("start:result=" + result);
                if (!result) {
                    return;
                }
                init = true;
                MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
                String stuId = userInfoEntity.getStuId();
                if (mAudioRecord == null) {
                    try {
                        initAudioRecorder();
                    } catch (Exception e) {
                        return;
                    }
                }
                try {
                    mAudioRecord.startRecording();
                } catch (Exception e) {
                    return;
                }
                while (isStart) {
                    if (mAudioRecord != null) {
                        int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
//                                byte[] pcm_data = toByteArray(mPCMBuffer, readSize);
//                            logger.d("start:predict=" + readSize + ",pcm_data=" + pcm_data.length);
                        synchronized (lock) {
                            if (destory || audioRequest.get()) {
                                return;
                            }
                            //小于0是错误码
                            if (readSize > 0) {
                                String predict = speakerRecognitionerInterface.predict(mPCMBuffer, readSize, index++, stuId, false);
                                if (!StringUtils.isEmpty(predict)) {
                                    logger.d("start:predict=" + predict);
//                                    if (speakerPredict != null) {
//                                        speakerPredict.onPredict(predict);
//                                    }
                                    try {
                                        iSpeechRecognitnCall.onPredict(predict);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (lastReadSize != readSize) {

                                }
                                lastReadSize = readSize;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void stop() throws RemoteException {
        logger.d("stop");
        audioRequest.set(true);
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

    @Override
    public void release() throws RemoteException {
        logger.d("release");
        synchronized (lock) {
            destory = true;
            if (speakerRecognitionerInterface != null) {
                speakerRecognitionerInterface.speakerRecognitionerFree();
            }
        }
    }
}
