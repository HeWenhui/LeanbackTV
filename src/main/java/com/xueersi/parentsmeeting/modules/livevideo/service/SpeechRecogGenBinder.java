package com.xueersi.parentsmeeting.modules.livevideo.service;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;
import com.tal.speech.speechrecognigen.ISpeechRecognitnGen;
import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.common.util.LoadSoCallBack;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.speakerrecognition.SpeakerRecognitionerInterface;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeechRecogGenBinder extends ISpeechRecognitnGen.Stub {
    private String TAG = "SpeechRecogGenBinder";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
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
    private int index = 0;
    private boolean destory = false;
    private Context context;
    //    private SpeakerPredict speakerPredict;
    private SpeakerRecognitionerInterface speakerRecognitionerInterface;
    private boolean loadSo = false;
    private ISpeechRecognitnCall iSpeechRecognitnCall;

    public SpeechRecogGenBinder(Context context) {
        logger.d("SpeechRecogGenBinder");
        this.context = context;
        pingPool = new ThreadPoolExecutor(1, 1,
                30L, TimeUnit.SECONDS,
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
        pingPool.allowCoreThreadTimeOut(true);
        checkResoure();
    }

    private void checkResoure() {
        SpeakerRecognitionerInterface.checkResoureDownload(context, new LoadSoCallBack() {
            @Override
            public void start() {
            }

            @Override
            public void success() {
                pingPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (destory) {
                            return;
                        }
                        speakerRecognitionerInterface = SpeakerRecognitionerInterface.getInstance();
                        boolean result = speakerRecognitionerInterface.init();
                        logger.d("init:result=" + result + ",isStart=" + isStart);
                        if (result) {
                            byte[] pcmdata = new byte[10];
                            String stuId = LiveAppUserInfo.getInstance().getStuId();
                            int enrollIvector = speakerRecognitionerInterface.
                                    enrollIvector(pcmdata, pcmdata.length, index++, stuId, false);
                            logger.d("init:stuId=" + stuId + ",enrollIvector=" + enrollIvector);
                            if (iSpeechRecognitnCall != null) {
                                try {
                                    iSpeechRecognitnCall.enrollIvector(enrollIvector);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (enrollIvector == 0) {
                                loadSo = true;
                                if (isStart) {
                                    startSpeech();
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                logger.d("checkResoureDownload:errorCode=" + errorCode + ",errorMsg=" + errorMsg);
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
            //用安卓log为了让bugly统计到
            Log.d(TAG, "initAudioRecorder:mBufferSize=" + mBufferSize);
        } else {
            mPCMBuffer = new byte[mBufferSize];
        }
        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);
    }

    private int lastReadSize;

    @Override
    public void check(ISpeechRecognitnCall iSpeechRecognitnCall) {
        this.iSpeechRecognitnCall = iSpeechRecognitnCall;
        logger.d("check:loadSo=" + loadSo);
        checkResoure();
    }

    @Override
    public void startSpeech(final ISpeechRecognitnCall iSpeechRecognitnCall) throws RemoteException {
        logger.d("start:loadSo=" + loadSo);
        this.iSpeechRecognitnCall = iSpeechRecognitnCall;
        if (isStart) {
            return;
        }
        isStart = true;
        if (!loadSo) {
            return;
        }
        startSpeech();
    }

    public void startSpeech() {
        pingPool.execute(new Runnable() {
            @Override
            public void run() {
                if (destory || !isStart) {
                    Log.d(TAG, "start:destory=" + destory + ",isStart=" + isStart);
                    return;
                }
                String stuId = LiveAppUserInfo.getInstance().getStuId();
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
                        //目前没有锁的必要了
                        synchronized (lock) {
                            if (destory) {
                                return;
                            }
                            //小于0是错误码
                            if (readSize > 0 && isStart) {
                                //用安卓log为了让bugly统计到
                                Log.d(TAG, "startSpeech:index=" + index + ",readSize=" + readSize);
                                String predict = speakerRecognitionerInterface.predict(mPCMBuffer, readSize, index++, stuId, false);
                                if (!StringUtils.isEmpty(predict)) {
//                                    if (speakerPredict != null) {
//                                        speakerPredict.onPredict(predict);
//                                    }
                                    try {
                                        boolean request = iSpeechRecognitnCall.onPredict(predict);
                                        logger.d("start:request=" + request + ",predict=" + predict);
                                        if (request) {
                                            stop();
                                            break;
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                        stop();
                                        break;
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
    public void stopSpeech() throws RemoteException {
        stop();
    }

    private void stop() {
        logger.d("stop");
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
        //目前没有锁的必要了
        synchronized (lock) {
            destory = true;
            pingPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (speakerRecognitionerInterface != null) {
                        speakerRecognitionerInterface.speakerRecognitionerFree();
                        speakerRecognitionerInterface = null;
                    }
                }
            });
        }
    }
}
