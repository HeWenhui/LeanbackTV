package com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business;

import android.app.Activity;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page.SpeechFeedBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.IOException;

/**
 * Created by linyuqiang on 2018/1/11.
 * 语音反馈
 */
public class SpeechFeedBackBllOld implements SpeechFeedBackAction {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    String TAG = "SpeechFeedBackBll";
    boolean isStart = false;
    Activity activity;
    RelativeLayout bottomContent;
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
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    public SpeechFeedBackBllOld(Activity activity,SpeechFeedBackIRCBll backIRCBll) {
        this.activity = activity;
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
        mPCMBuffer = new short[mBufferSize];

        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);
    }

    public void setBottomContent(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    @Override
    public void setNonce(String s) {

    }

    @Override
    public void start(String roomId) {
        if (isStart) {
            return;
        }
        logger.d("start:roomId=" + roomId);
        isStart = true;
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.d("start:startRecording:mAudioRecord=" + (mAudioRecord == null));
                    initAudioRecorder();
                    bottomContent.post(new Runnable() {
                        @Override
                        public void run() {
                            speechFeedBackPager = new SpeechFeedBackPager(activity);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                                    .LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            int screenWidth = ScreenUtils.getScreenWidth();
                            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig
                                    .VIDEO_WIDTH);
                            params.rightMargin = wradio;
                            bottomContent.addView(speechFeedBackPager.getRootView(), params);
                        }
                    });
                    long time = System.currentTimeMillis();
                    mAudioRecord.startRecording();
                    while (isStart) {
                        if (mAudioRecord != null) {
                            int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
                            if (readSize > 0) {
                                calculateRealVolume(mPCMBuffer, readSize);
                            }
                        }
                    }
                    logger.d("start:startRecording:end;time=" + (System.currentTimeMillis() - time));
                } catch (IOException e) {
                    logger.e("initAudioRecorder", e);
                }
            }
        });
    }

    @Override
    public void stop() {
        if (!isStart) {
            return;
        }
        logger.d("stop:mAudioRecord=" + (mAudioRecord == null));
        isStart = false;
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (speechFeedBackPager != null) {
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechFeedBackPager.getRootView()
                        .getLayoutParams();
                if (wradio != params.rightMargin) {
                    params.rightMargin = wradio;
                    LayoutParamsUtil.setViewLayoutParams(speechFeedBackPager.getRootView(), params);
                }
            }
        }
    }

}
