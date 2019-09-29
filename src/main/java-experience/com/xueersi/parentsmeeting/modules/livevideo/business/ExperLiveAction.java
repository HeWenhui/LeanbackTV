package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class ExperLiveAction {
    protected Logger logger =  LiveLoggerFactory.getLogger(getClass().getSimpleName());
    protected Activity activity;
    protected RelativeLayout rlFirstBackgroundView;
    protected ImageView ivTeacherNotpresent;
    protected ExpLiveInfo expLiveInfo;

    public ExperLiveAction(Activity activity, RelativeLayout mContentView, ExpLiveInfo expLiveInfo) {
        this.activity = activity;
        this.expLiveInfo = expLiveInfo;
        ivTeacherNotpresent = mContentView.findViewById(R.id.iv_course_video_teacher_notpresent);
        ivTeacherNotpresent.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rlFirstBackgroundView = mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        rlFirstBackgroundView.setVisibility(View.GONE);
        setLayout();
    }

    protected void setLayout() {
        LiveVideoPoint.getInstance().addVideoSizeChangeAndCall(activity, new LiveVideoPoint.VideoSizeChange() {
            @Override
            public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
                setThreeFen();
            }
        });
    }

    protected void setThreeFen() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivTeacherNotpresent.getLayoutParams();
        int topAndBottom = liveVideoPoint.y2;
        int leftMargin = liveVideoPoint.x2;
        int rightMargin = liveVideoPoint.getRightMargin();
        if (params.topMargin != topAndBottom || params.leftMargin != leftMargin || params.rightMargin != rightMargin) {
            params.topMargin = topAndBottom;
            params.leftMargin = leftMargin;
            params.rightMargin = rightMargin;
            ivTeacherNotpresent.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
        params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.topMargin != topAndBottom || params.leftMargin != leftMargin || params.rightMargin != rightMargin) {
            params.topMargin = topAndBottom;
            params.leftMargin = leftMargin;
            params.rightMargin = rightMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
        }
    }

    public void onPlayOpenSuccess() {
        if (rlFirstBackgroundView.getVisibility() != View.GONE) {
            rlFirstBackgroundView.setVisibility(View.GONE);
        }

        if (ivTeacherNotpresent.getVisibility() != View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    public void onModeChanged(int mode) {
        if (mode == ExperConfig.COURSE_STATE_1) {
            // 课前状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        } else if (mode == ExperConfig.COURSE_STATE_2) {
            // 课中状态,播放回放视频
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_wait_teacher);
        } else if (mode == ExperConfig.COURSE_STATE_3) {
            // 课后状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_wait_teacher);
        } else if (mode == ExperConfig.COURSE_STATE_4) {
            // 结束状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
        } else {
            // 等待状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        }
        if (mode == ExperConfig.COURSE_STATE_1 || mode == ExperConfig.COURSE_STATE_3) {
            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }
        } else if (mode == ExperConfig.COURSE_STATE_2) {
            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }
        } else {
            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }
            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }
        }
    }

    public void resultFailed(VideoPlayState videoPlayState, int arg1, int arg2) {
        if (arg2 == MediaErrorInfo.PLAY_COMPLETE) {
            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }

        } else if (arg2 == MediaErrorInfo.PSChannelNotExist) {

            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }

        } else {
            if (videoPlayState.isPlaying) {
                if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                }
                if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                    rlFirstBackgroundView.setVisibility(View.VISIBLE);
                }
//                playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
            }
        }
    }
}
