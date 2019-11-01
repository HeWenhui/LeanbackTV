package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * MediaCodec解码AAC(MediaRecord录制的AAC，已经编好码了)
 * <p>
 * 参考博客  https://www.cnblogs.com/Sharley/p/5964490.html
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AudioMediaCodecUtils {
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private String mFilePath;

    /** 是否正在播放 */
    private boolean mIsPalying = false;

    private MediaCodec mAudioDecoder;

    public AudioMediaCodecUtils() {
        mIsPalying = false;
    }

    /**
     * 初始化文件音频文件夹
     *
     * @param path
     * @return
     */
    public boolean init(String path) {
        this.mFilePath = path;
        File mAudioFile = new File(mFilePath);
        if (mAudioFile == null || !mAudioFile.exists()) {
            return false;
        }
        if (!mIsPalying) {
            mIsPalying = true;
            return initAudioDecoder();
        }
        return false;
    }

    private MediaExtractor mMediaExtractor;

    /**
     * 初始化解码器
     */
    private boolean initAudioDecoder() {
        try {
            mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(mFilePath);
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {//获取音频轨道
                    mMediaExtractor.selectTrack(i);//选择此音频轨道
                    mAudioDecoder = MediaCodec.createDecoderByType(mime);//创建Decode解码器
                    mAudioDecoder.configure(format, null, null, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mAudioDecoder == null) {
//            Log.e(TAG, "mAudioDecoder is null");
            return false;
        }

        mAudioDecoder.start();//启动MediaCodec ，等待传入数据
        return true;
    }

    boolean isFinish = false;

    /**
     * aacToPCM
     */
    public void aacToPCM() {
        MediaCodec.BufferInfo decodeBufferInfo = new MediaCodec.BufferInfo();
        while (!isFinish && mIsPalying) {
            try {
                int inputIdex = mAudioDecoder.dequeueInputBuffer(10000);//等待10s
                if (inputIdex < 0) {
                    isFinish = true;
                }
                ByteBuffer inputBuffer = mAudioDecoder.getInputBuffer(inputIdex);
                inputBuffer.clear();
                int samplesize = mMediaExtractor.readSampleData(inputBuffer, 0);
                if (samplesize > 0) {
                    mAudioDecoder.queueInputBuffer(inputIdex, 0, samplesize, 0, 0);
                    mMediaExtractor.advance();
                } else {
                    isFinish = true;
                }
                int outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                ByteBuffer outputBuffer;
                byte[] chunkPCM;

                while (outputIndex >= 0) {            //每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
                    outputBuffer = mAudioDecoder.getOutputBuffer(outputIndex);
                    chunkPCM = new byte[decodeBufferInfo.size];
                    outputBuffer.get(chunkPCM);
                    outputBuffer.clear();
//                    Byte[] bytes = new Byte[chunkPCM.length];
                    if (listener != null) {
                        listener.pcmData(chunkPCM, decodeBufferInfo.size);
                    }
//                audioTrack.write(chunkPCM, 0, decodeBufferInfo.size);
                    mAudioDecoder.releaseOutputBuffer(outputIndex, false);
                    outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.e(e);
                if (listener != null) {
                    listener.pcmComplete(false);
                }
                return;
            }
        }
        stopPlay();
        listener.pcmComplete(true);
    }

    public static class PCMEntity {
        private byte[] bytes;
        private int size;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        private PCMEntity(byte[] bytes, int size) {
            this.bytes = bytes;
            this.size = size;
        }

        static PCMEntity create(byte[] bytes, int size) {
            return new PCMEntity(bytes, size);
        }
    }

    public Flowable<PCMEntity> rxAACToPCM() {
        return Flowable.
                create(new FlowableOnSubscribe<PCMEntity>() {
                    @Override
                    public void subscribe(FlowableEmitter<PCMEntity> e) throws Exception {
                        aacRxToPCM(e);
                    }
                }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io());
    }

    public Observable rxObservableAACToPCM() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                aacRxToPCM(e);
            }
        });
    }

    /**
     * aacToPCM
     */
    public boolean aacRxToPCM(FlowableEmitter<PCMEntity> emitter) {
        MediaCodec.BufferInfo decodeBufferInfo = new MediaCodec.BufferInfo();
        while (!isFinish && mIsPalying) {
            try {
                int inputIdex = mAudioDecoder.dequeueInputBuffer(10000);//等待10s
                if (inputIdex < 0) {
                    isFinish = true;
                }
                ByteBuffer inputBuffer = mAudioDecoder.getInputBuffer(inputIdex);
                inputBuffer.clear();
                int samplesize = mMediaExtractor.readSampleData(inputBuffer, 0);
                if (samplesize > 0) {
                    mAudioDecoder.queueInputBuffer(inputIdex, 0, samplesize, 0, 0);
                    mMediaExtractor.advance();
                } else {
                    isFinish = true;
                }
                int outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                ByteBuffer outputBuffer;
                byte[] chunkPCM;

                while (outputIndex >= 0) {            //每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
                    outputBuffer = mAudioDecoder.getOutputBuffer(outputIndex);
                    chunkPCM = new byte[decodeBufferInfo.size];
                    outputBuffer.get(chunkPCM);
                    outputBuffer.clear();
                    emitter.onNext(PCMEntity.create(chunkPCM, decodeBufferInfo.size));
//                    Byte[] bytes = new Byte[chunkPCM.length];
//                    if (listener != null) {
//                        listener.pcmData(chunkPCM, decodeBufferInfo.size);
//                    }
//                audioTrack.write(chunkPCM, 0, decodeBufferInfo.size);
                    mAudioDecoder.releaseOutputBuffer(outputIndex, false);
                    outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                }
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.e(e1);
//                if (listener != null) {
//                    listener.pcmComplete(false);
//                }
                return false;
            }
        }
        stopPlay();
//        listener.pcmComplete(true);
        return true;
    }

    /**
     * aacToPCM
     */
    public boolean aacRxToPCM(ObservableEmitter<PCMEntity> emitter) {
        MediaCodec.BufferInfo decodeBufferInfo = new MediaCodec.BufferInfo();
        while (!isFinish && mIsPalying) {
            try {
                int inputIdex = mAudioDecoder.dequeueInputBuffer(10000);//等待10s
                if (inputIdex < 0) {
                    isFinish = true;
                }
                ByteBuffer inputBuffer = mAudioDecoder.getInputBuffer(inputIdex);
                inputBuffer.clear();
                int samplesize = mMediaExtractor.readSampleData(inputBuffer, 0);
                if (samplesize > 0) {
                    mAudioDecoder.queueInputBuffer(inputIdex, 0, samplesize, 0, 0);
                    mMediaExtractor.advance();
                } else {
                    isFinish = true;
                }
                int outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                ByteBuffer outputBuffer;
                byte[] chunkPCM;

                while (outputIndex >= 0) {            //每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
                    outputBuffer = mAudioDecoder.getOutputBuffer(outputIndex);
                    chunkPCM = new byte[decodeBufferInfo.size];
                    outputBuffer.get(chunkPCM);
                    outputBuffer.clear();
                    emitter.onNext(PCMEntity.create(chunkPCM, decodeBufferInfo.size));
//                    Byte[] bytes = new Byte[chunkPCM.length];
//                    if (listener != null) {
//                        listener.pcmData(chunkPCM, decodeBufferInfo.size);
//                    }
//                audioTrack.write(chunkPCM, 0, decodeBufferInfo.size);
                    mAudioDecoder.releaseOutputBuffer(outputIndex, false);
                    outputIndex = mAudioDecoder.dequeueOutputBuffer(decodeBufferInfo, 10000);

                }
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.e(e1);
//                if (listener != null) {
//                    listener.pcmComplete(false);
//                }
                return false;
            }
        }
        emitter.onComplete();
        stopPlay();
//        listener.pcmComplete(true);
        return true;
    }

    private void stopPlay() {
        mIsPalying = false;
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                palyBtn.setText("播放");
//                textView.setText(textView.getText() + "\n播放结束");
//            }
//        });
        if (mAudioDecoder != null) {
            mAudioDecoder.stop();
            mAudioDecoder.release();
            mAudioDecoder = null;
        }
    }

    private PCMDataListener listener;

    public AudioMediaCodecUtils(PCMDataListener listener) {
        this.listener = listener;
    }

    public interface PCMDataListener {
        void pcmData(byte[] bytes, int size);

        void pcmComplete(boolean success);
    }

}
