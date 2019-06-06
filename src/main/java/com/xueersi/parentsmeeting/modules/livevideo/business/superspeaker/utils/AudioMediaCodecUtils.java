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
    private boolean mIsPalying;

    private MediaCodec mAudioDecoder;


//    public AudioMediaCodecUtils() {
//    }

    public boolean init(String path) {
//        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/parentsmeeting/livevideo/superSpeaker/485219_7.mp4";
        this.mFilePath = path;
        File mAudioFile = new File(mFilePath);
        if (mAudioFile == null || !mAudioFile.exists()) {
//            textView.setText(textView.getText() + "\n文件为空，请先录音");
            return false;
        }
        if (!mIsPalying) {
            mIsPalying = true;
            initAudioDecoder();
//            Executors.newCachedThreadPool().submit(new Runnable() {
//                @Override
//                public void run() {
//                    // 解码
//                    aacToPCM();
//                }
//            });
        } else {
            mIsPalying = false;
        }
        return true;
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
