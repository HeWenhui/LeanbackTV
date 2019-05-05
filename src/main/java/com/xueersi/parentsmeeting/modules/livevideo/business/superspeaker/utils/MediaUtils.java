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
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MediaUtils {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//    private MediaExtractor mMediaExtractor;

//    private MediaMuxer mMediaMuxer;

    public MediaUtils() {

    }

    //    private Executor
    public void process(final String srcVideoPath, final String outVideoPath, final String outAudioPath) {
//        Executors.newCachedThreadPool().submit(new FutureTask<Integer>());
        Executors.newCachedThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                videoExtractor(srcVideoPath, outVideoPath);
                audioExtractor(srcVideoPath, outAudioPath);
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
        } finally {
            if (mMediaExtractor != null) {
                mMediaExtractor.release();
            }
            if (mMediaMuxer != null) {
                mMediaMuxer.stop();
                mMediaMuxer.release();
            }

        }
        return false;
    }

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
                while (true) {
                    int sampleSize = mAudioExtractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) {
                        break;
                    }
                    info.offset = 0;
                    info.size = sampleSize;
                    info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    info.presentationTimeUs = mAudioExtractor.getSampleTime();
                    logger.i("使用秒数");
                    mMediaMuxer.writeSampleData(audioTrackIndex, buffer, info);

                    mAudioExtractor.advance();
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mAudioExtractor != null) {
                mAudioExtractor.release();
            }
            if (mMediaMuxer != null) {
                mMediaMuxer.stop();
                mMediaMuxer.release();
            }

        }
        return true;
    }
}
