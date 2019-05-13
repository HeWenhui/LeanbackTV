package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MediaUtils {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//    private MediaExtractor mMediaExtractor;

//    private MediaMuxer mMediaMuxer;

    public MediaUtils() {

    }

    public interface SampleCallBack {
        int callbackVolume();
    }

    /**  */
    public static class ExtraObservable extends Observable {
        private boolean extractVideoSuccess;
        private boolean extractAudioSuccess;
        /** 计算音量，目前计算有问题，暂时没有使用 */
        private int volume;

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            setChanged();
            this.volume = volume;
        }

        public boolean isExtractVideoSuccess() {
            return extractVideoSuccess;
        }

        public void setExtractVideoSuccess(boolean extractVideoSuccess) {
            setChanged();
            this.extractVideoSuccess = extractVideoSuccess;
        }

        public boolean isExtractAudioSuccess() {
            return extractAudioSuccess;
        }

        public void setExtractAudioSuccess(boolean extractAudioSuccess) {
            setChanged();
            this.extractAudioSuccess = extractAudioSuccess;
        }
    }

    //    private Executor
    public void process(final String srcVideoPath, final String outVideoPath, final String outAudioPath, final ExtraObservable observable) {
//        Executors.newCachedThreadPool().submit(new FutureTask<Integer>());
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                boolean videoExtraSuccess = videoExtractor(srcVideoPath, outVideoPath);
                boolean audioExtraSuccess = audioExtractor(srcVideoPath, outAudioPath);
                if (observable != null) {
                    observable.setExtractAudioSuccess(audioExtraSuccess);
                    observable.setExtractVideoSuccess(videoExtraSuccess);
                    observable.setVolume(volume);
                    logger.i("volume = " + volume);
                    observable.notifyObservers();
                }
            }
        });

    }

    private long time = 0;

    /** 视频分离 */
    private boolean videoExtractor(String srcPath, String outPath) {

        MediaExtractor mMediaExtractor = new MediaExtractor();
        MediaMuxer mMediaMuxer = null;
        try {
            mMediaExtractor.setDataSource(srcPath);

            int mVideoTrackIndex = -1;
            int framerate = 0;
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat format = mMediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (!mime.startsWith("video/")) {
                    continue;
                }
                framerate = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                mMediaExtractor.selectTrack(i);
                mMediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                mVideoTrackIndex = mMediaMuxer.addTrack(format);
                mMediaMuxer.start();
            }

            if (mMediaMuxer == null) {
                return false;
            }

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info.presentationTimeUs = 0;
            ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
            int sampleSize = 0;
            while ((sampleSize = mMediaExtractor.readSampleData(buffer, 0)) > 0) {

                info.offset = 0;
                info.size = sampleSize;
                info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                info.presentationTimeUs += 1000 * 1000 / framerate;
                mMediaMuxer.writeSampleData(mVideoTrackIndex, buffer, info);
                mMediaExtractor.advance();
            }
            time = info.presentationTimeUs;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mMediaExtractor != null) {
                mMediaExtractor.release();
            }
            if (mMediaMuxer != null) {
                mMediaMuxer.stop();
                mMediaMuxer.release();
            }

        }
    }

    private int volume;

    /** 音频分离 */
    private boolean audioExtractor(String srcPath, String outPath) {
        // 音频的MediaExtractor
        MediaExtractor mAudioExtractor = new MediaExtractor();
        MediaMuxer mMediaMuxer = null;
        try {
            mMediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mAudioExtractor.setDataSource(srcPath);


            int audioTrackIndex = -1;
            for (int i = 0; i < mAudioExtractor.getTrackCount(); i++) {
                MediaFormat format = mAudioExtractor.getTrackFormat(i);
                if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    mAudioExtractor.selectTrack(i);
                    audioTrackIndex = mMediaMuxer.addTrack(format);
                    mMediaMuxer.start();
                    break;
                }
            }
            if (-1 != audioTrackIndex) {
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                info.presentationTimeUs = 0;
                ByteBuffer buffer = ByteBuffer.allocate(100 * 1024);

                int sampleNum = 0;
                int sampleSum = 0;
                while (true) {
                    int sampleSize = mAudioExtractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) {
                        break;
                    }
                    info.offset = 0;
                    info.size = sampleSize;
                    info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    info.presentationTimeUs = mAudioExtractor.getSampleTime();
//                    logger.i("使用秒数");
                    mMediaMuxer.writeSampleData(audioTrackIndex, buffer, info);

                    mAudioExtractor.advance();
                    sampleSum += realTimeCalculateRealVolume(buffer.array(), sampleSize);
                    sampleNum++;
                }
                volume = sampleSum / sampleNum;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mAudioExtractor != null) {
                mAudioExtractor.release();
            }
            if (mMediaMuxer != null) {
                try {
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                } catch (Exception e) {
                    logger.i(e);
//                    CrashReport.startCrashReport();
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 语文金话筒实时返回音频
     *
     * @param buffer
     * @param readSize
     * @return
     */
    private int realTimeCalculateRealVolume(byte[] buffer, int readSize) {
        double sum = 0;
//                logger.i("for1 " + readSize);
        for (int i = 0; i < readSize; i++) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            sum += buffer[i] * buffer[i];
        }
//                logger.i("for2 " + readSize);
        if (readSize > 0) {
            double amplitude = sum / readSize;
            int volume = (int) Math.sqrt(amplitude);
            volume = (volume * 30 / 10000);
            return volume;
//                    logger.i("send " + System.currentTimeMillis());
        }
        return 0;
    }
}
