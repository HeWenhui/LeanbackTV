package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class CustomVideoController extends BasePager implements ILocalVideoController {
    private TextView tvTotalTime, tvCurrentTime;
    private String totalTime, currentTime;

    private ImageView ivProgressBar;

    private ILocalVideoPlayer iPlayer;

    private VideoView mVideoView;

    public CustomVideoController(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_custom_videoview, null);
        tvCurrentTime = view.findViewById(R.id.tv_livevideo_super_speaker_video_bottom_time);
        tvTotalTime = view.findViewById(R.id.tv_livevideo_super_speaker_video_bottom_total_time);
        ivProgressBar = view.findViewById(R.id.iv_livevideo_super_speaker_video_controller_bottom_progress_bar);
        mVideoView = view.findViewById(R.id.vv_course_video_video);
        view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {

            }
        });
        iPlayer = new CustomLocalVideoPlayerBridge(mContext);
        return view;
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
        if (iPlayer != null) {
            iPlayer.seekTo(pos);
        }
    }

    @Override
    public void startPlayVideo(String path, int time) {
        if (iPlayer == null) {
            iPlayer = new CustomLocalVideoPlayerBridge(mContext);
        }
        iPlayer.setVideoView(mVideoView);
        iPlayer.startPlayVideo(path, time);
    }

    @Override
    public void initData() {

    }
}
