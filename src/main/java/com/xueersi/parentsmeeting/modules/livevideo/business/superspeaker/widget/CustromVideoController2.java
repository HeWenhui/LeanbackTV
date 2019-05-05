package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;

@SuppressLint("ViewConstructor")
public class CustromVideoController2 extends ConstraintLayout implements ILocalVideoController {

    private TextView tvTotalTime, tvCurrentTime;
    private String totalTime, currentTime;

    private ImageView ivProgressBar;

    private ILocalVideoPlayer iPlayer;

    private VideoView mVideoView;


    public CustromVideoController2(Context context) {
        super(context);
        init();
    }

    public CustromVideoController2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustromVideoController2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.page_livevideo_super_speaker_custom_videoview, this);
        tvCurrentTime = findViewById(R.id.tv_livevideo_super_speaker_video_bottom_time);
        tvTotalTime = findViewById(R.id.tv_livevideo_super_speaker_video_bottom_total_time);
        ivProgressBar = findViewById(R.id.iv_livevideo_super_speaker_video_controller_bottom_progress_bar);
        mVideoView = findViewById(R.id.vv_course_video_video);

    }

    /**
     * 设置总体时间
     *
     * @param totalTime
     */
    @Override
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
        tvTotalTime.setText(totalTime);
    }

    /**
     * 设置当前时间
     *
     * @param currentTime
     */
    @Override
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
        tvCurrentTime.setText(currentTime);

    }

    @Override
    public void pause() {
        if (iPlayer != null) {
            iPlayer.pause();
        }
    }

    @Override
    public void start() {
        if (iPlayer != null) {
            iPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (iPlayer != null) {
            iPlayer.stop();
        }
    }

    @Override
    public void release() {
        if (iPlayer != null) {
            iPlayer.release();
        }
    }

    @Override
    public void seekTo(long pos) {
//        if (iPlayer != null) {
//            iPlayer.seekTo(pos);
//        }
    }

    @Override
    public void startPlayVideo(final String path, final int time) {
        if (iPlayer == null) {
            iPlayer = new CustomLocalVideoPlayerBridge(getContext());
        }

        iPlayer.setListener(new VPlayerCallBack.SimpleVPlayerListener() {
            @Override
            public void onPlaying(long currentPosition, long duration) {
                tvTotalTime.setText(String.valueOf(duration / 1000l));
                tvCurrentTime.setText(String.valueOf(currentPosition / 1000l));
            }

            @Override
            public void onPlaybackComplete() {
                super.onPlaybackComplete();
                iPlayer.setVideoView(mVideoView);
                iPlayer.startPlayVideo(path, time);
            }
        });
        iPlayer.setVideoView(mVideoView);
        iPlayer.startPlayVideo(path, time);

    }
}
