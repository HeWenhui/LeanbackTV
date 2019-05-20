package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.TimeUtils;

import java.util.Observable;
import java.util.Observer;

@SuppressLint("ViewConstructor")
public class CustomVideoController2 extends ConstraintLayout implements ILocalVideoController {

    private TextView tvTotalTime, tvCurrentTime;
    private String totalTime, currentTime;

    private ImageView ivProgressBar;

    private ILocalVideoPlayer iPlayer;

    private SurfaceView mVideoView;

    private ImageView ivProcessBarBkg;
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public CustomVideoController2(Context context) {
        super(context);
        init();
    }

    public CustomVideoController2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomVideoController2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //    private String videoPath;
    private int time;
//    private boolean isCreate;

//    private Observer observer;

    private VideoObservable videoObservable;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.page_livevideo_super_speaker_custom_videoview, this);
        tvCurrentTime = findViewById(R.id.tv_livevideo_super_speaker_video_bottom_time);
        tvTotalTime = findViewById(R.id.tv_livevideo_super_speaker_video_bottom_total_time);
        ivProgressBar = findViewById(R.id.iv_livevideo_super_speaker_video_controller_bottom_progress_bar);
        mVideoView = findViewById(R.id.vv_super_speaker_course_video_video);
        ivProcessBarBkg = findViewById(R.id.iv_livevideo_super_speaker_video_controller_bottom_progress_bar_background);
        SurfaceHolder holder = mVideoView.getHolder();
        videoObservable = new VideoObservable();
        videoObservable.addObserver(new VideoObserver());
//        observer = new Observer() {
//            @Override
//            public void update(Observable o, Object arg) {
//                iPlayer.startPlayVideo(videoPath, time);
//            }
//        };
//        Observable observable = new Observable();
//        observable.addObserver(observer);
//        ivProgressBar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//
//            }
//        });
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                logger.i("videoView创建成功");
                videoObservable.setIsCreate(1);
//                if (videoPath != null) {
//                    startPlayVideo(videoPath, 0);
//                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                logger.i("videoView销毁");
                if (iPlayer != null) {
                    iPlayer.stop();
                    iPlayer.release();
                }
            }
        });
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

    private long videoDuration;

    private long currentPosition;

    @Override
    public void startPlayVideo(final String path, final int time) {
        if (iPlayer == null) {
            iPlayer = new CustomLocalVideoPlayerBridge(getContext());
        }

        iPlayer.setListener(new VPlayerCallBack.SimpleVPlayerListener() {
            @Override
            public void onPlaying(long currentPosition, long duration) {
                logger.i("currentPosition:" + currentPosition + " duration:" + duration);
                long durationSize = duration / 1000l;
                if ((duration % 1000L) > 500) {
                    durationSize = duration / 1000l + 1;
                }
                videoDuration = durationSize;
                tvTotalTime.setText(TimeUtils.stringForTime(durationSize));
                long currentSize = currentPosition / 1000l;
                if ((currentPosition % 1000l) > 500) {
                    currentSize = currentPosition / 1000l + 1;
                }
                if (currentSize > durationSize) {
                    currentSize = durationSize;
                }
                CustomVideoController2.this.currentPosition = currentSize;
                tvCurrentTime.setText(TimeUtils.stringForTime(currentSize));
//                int width = ivProcessBarBkg.getWidth();
//                Drawable drawable = getContext().getResources().getDrawable(R.drawable.bg_livevideo_super_speaker_video_controller_bottom_progress_bar);
                ViewGroup.LayoutParams layoutParams = ivProgressBar.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                int bkgWidth = SizeUtils.getMeasuredWidth(ivProcessBarBkg);
                layoutParams.width = (int) (bkgWidth * (currentSize * 1.0 / durationSize));
                logger.i("width:" + layoutParams.width + " bkgWidth:" + bkgWidth);
                ivProgressBar.setLayoutParams(layoutParams);
            }

            @Override
            public void onPlaybackComplete() {
                super.onPlaybackComplete();
                ViewGroup.LayoutParams layoutParams = ivProgressBar.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                logger.i("onPlaybackComplete() currentPosition = " + CustomVideoController2.this.currentPosition + " videoDuration=" + videoDuration);
                if (CustomVideoController2.this.currentPosition != videoDuration) {

                    int bkgWidth = SizeUtils.getMeasuredWidth(ivProcessBarBkg);
                    layoutParams.width = (int) (bkgWidth);
                    ivProgressBar.setLayoutParams(layoutParams);
                    tvCurrentTime.setText(TimeUtils.stringForTime(videoDuration));
                } else {
                    layoutParams.width = 1;//这里设置伟1是因为imageview使用fitXY时，设置为0会充满布局
                    ivProgressBar.setLayoutParams(layoutParams);
                    tvCurrentTime.setText(TimeUtils.stringForTime(0));
                }
                iPlayer.stop();
                iPlayer.release();
                CustomVideoController2.this.startPlayVideo(path, 0);
                iPlayer.setVideoView(mVideoView);
                iPlayer.startPlayVideo(path, 0);
            }
        });
        iPlayer.setVideoView(mVideoView);

        this.time = time;
        logger.i("path:" + path + " time:" + time);
        videoObservable.setVideoPath(path);
//        iPlayer.startPlayVideo(path, time);

    }

    private class VideoObservable extends Observable {
        private String videoPath;

        private int isCreate;

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
            setChanged();
            if (!TextUtils.isEmpty(videoPath) && isCreate > 0) {
                logger.i("videoPath通知更改");
                notifyObservers();
            }
        }

        public int getIsCreate() {
            return isCreate;
        }

        public void setIsCreate(int isCreate) {
            this.isCreate = isCreate;
            setChanged();
            logger.i("isCreate通知更改");
            if (!TextUtils.isEmpty(videoPath) && isCreate > 0) {
                notifyObservers();
            }
        }
    }

    private class VideoObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof VideoObservable) {
                if (((VideoObservable) o).getIsCreate() == 1 && !TextUtils.isEmpty(((VideoObservable) o).getVideoPath())) {
                    logger.i("file path : " + ((VideoObservable) o).getVideoPath());
                    iPlayer.setVideoView(mVideoView);
                    iPlayer.startPlayVideo(((VideoObservable) o).getVideoPath(), 0);
                }
            }
        }

    }
}
