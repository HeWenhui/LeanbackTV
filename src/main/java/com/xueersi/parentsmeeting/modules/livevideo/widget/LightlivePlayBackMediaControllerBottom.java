package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.IPlayBackMediaCtr;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom2;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.widget
 * @ClassName: LightlivePlayBackMediaControllerBottom
 * @Description: 轻直播回放底部控制栏
 * @Author: WangDe
 * @CreateDate: 2019/12/25 17:42
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/25 17:42
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightlivePlayBackMediaControllerBottom extends MediaControllerBottom2 {
    /** 播放控制栏的总长 */
    static final int DEFAULT_SEEKBAR_VALUE = 1000;
    public LightlivePlayBackMediaControllerBottom(Context context, IPlayBackMediaCtr controller, BackMediaPlayerControl player) {
        super(context, controller, player);
    }

    @Override
    protected View inflateLayout() {
        return LayoutInflater.from(getContext()).inflate(R.layout.lightlive_playback_pop_mediacontroller_bottom, this);
    }

    @Override
    protected void initResources() {
        inflateLayout();
        findViewItems();
    }

    @Override
    protected void findViewItems() {
        mEndTime = findViewById(R.id.tv_video_mediacontroller_controls_timetotal); // 视频总时长显示
        mCurrentTime = findViewById(R.id.tv_video_mediacontroller_controls_timecurrent); // 当前播放的进度显示
        rl_video_mediacontroller_speeds = findViewById(R.id.rl_video_mediacontroller_speeds_content);
        mSetSpeed = findViewById(R.id.tv_video_mediacontroller_controls_speed);
        // 倍速设置
        setSpeedInfo();
        // 播放暂停按钮
        mPauseButton = findViewById(R.id.iv_video_mediacontroller_controls_playpause);
        mPauseButton.setOnClickListener(mPauseListener);
        // 控制进度栏
        mProgress = findViewById(R.id.sbar_video_mediacontroller_controls_seekbar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(DEFAULT_SEEKBAR_VALUE);
        mProgress.setPadding(0, 0, 0, 0);
    }

    @Override
    public void setAutoOrientation(final boolean autoOrientation) {
//        super.setAutoOrientation(autoOrientation);
        post(new Runnable() {
            @Override
            public void run() {
                if(autoOrientation){
                    mSetSpeed.setVisibility(VISIBLE);
                }else{
                    mSetSpeed.setVisibility(GONE);
                }
            }
        });

    }

    @Override
    public void setPlayNextVisable(boolean playNextVisable) {
    }

    @Override
    public void setVideoStatus(int code, int status, String values) {
    }

    @Override
    public void onShow() {
        if (mPlayer.isPlayInitialized()) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
        mPauseButton.requestFocus();
    }

    @Override
    public void onHide() {
        if (rl_video_mediacontroller_speeds != null) {
            rl_video_mediacontroller_speeds.setVisibility(INVISIBLE);
        }
    }


}
