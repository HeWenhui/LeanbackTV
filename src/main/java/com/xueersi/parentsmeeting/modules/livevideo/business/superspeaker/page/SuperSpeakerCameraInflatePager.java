package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.support.constraint.Group;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.Camera1Utils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.TimeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.CustomVideoController2;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

public abstract class SuperSpeakerCameraInflatePager extends LiveBasePager {
    /** 录制相机的view */
    protected SurfaceView sfvVideo;

    protected ImageView ivStartRecord, ivStopRecord, ivSubmitRecord, ivBack, ivRestart, ivReversal;

    protected Group groupSubmit, groupStop, groupStart, groupRestart, groupReversal;

    /** 视频播放控制器 */
    protected CustomVideoController2 customVideoController2;

    protected View layoutStartViewTime, layoutStopViewTime;

    /** 当前已经录制的时间 */
    protected TextView tvStopRecordCurrentTime;
    /** 录制的总时间 */
    protected TextView tvStopRecordTotalTime;

    protected TextView tvStartRecordTotalTime;

    protected SuperSpeakerCameraBackPager cameraBackPager;

    /** 试题发布时长 */
    protected TextView tvRecordVideoTime;

    protected LottieAnimationView lottieAnimationView;

    protected boolean isSurfViewCreat = false;

    /** 是否使用前置摄像头或者后置摄像头,默认faceback,即自拍 */
    protected boolean isFacingBack = false;
    /** 相机工具类 */
    protected Camera1Utils camera1Utils;

    /** 是否已经录制过视频并且录制时间时间大于1s */
    protected boolean isHasRecordView = false;

    public SuperSpeakerCameraInflatePager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_record_video, null);
        sfvVideo = view.findViewById(R.id.sfv_livevideo_super_speaker_record_video);

        ivStartRecord = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_start);
        ivStopRecord = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_stop);
        ivSubmitRecord = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_submit);
        ivRestart = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_restart);
        ivReversal = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_reversal);
        ivBack = view.findViewById(R.id.iv_livevideo_super_speaker_record_video_back);
        tvRecordVideoTime = view.findViewById(R.id.tv_livevideo_super_speaker_record_video_time);


        layoutStartViewTime = view.findViewById(R.id.include_livevideo_super_speaker_record_video_record_time);
        layoutStopViewTime = view.findViewById(R.id.include_livevideo_super_speaker_record_video_stop_time);

        tvStartRecordTotalTime = layoutStartViewTime.findViewById(R.id.tv_livevideo_super_speaker_record_video_record_time_total);
        tvStopRecordCurrentTime = layoutStopViewTime.findViewById(R.id.tv_livevideo_super_speaker_record_video_record_time_currenttime);
        tvStopRecordTotalTime = layoutStopViewTime.findViewById(R.id.tv_livevideo_super_speaker_record_video_record_time_total);

        lottieAnimationView = view.findViewById(R.id.lottie_livevideo_super_speaker_record_video);

        groupStart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_start);
        groupRestart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_restart);
        groupReversal = view.findViewById(R.id.group_livevideo_super_speaker_record_video_reversal);
        groupStop = view.findViewById(R.id.group_livevideo_super_speaker_record_video_stop);
        groupSubmit = view.findViewById(R.id.group_livevideo_super_speaker_record_video_submit);

        customVideoController2 = view.findViewById(R.id.custom_controller_livevideo_super_speaker_record_video_video_player);
//        sfvVideo.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        SurfaceHolder holder = sfvVideo.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                logger.i("surfaceCreated");
                isSurfViewCreat = true;
                performStartPreView(isFacingBack);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                logger.i("surfaceDestroyed");
                try {
                    if (holder.getSurface() != null) {
                        holder.getSurface().release();
                    }
                    if (camera1Utils != null) {
                        camera1Utils.releaseCamera();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        camera1Utils = new Camera1Utils(sfvVideo);
        initShowView();
        initListener();

        return view;
    }


    protected void initShowView() {
        if (groupStart != null && groupStart.getVisibility() != View.VISIBLE) {
            groupStart.setVisibility(View.VISIBLE);
        }
        if (groupReversal != null && groupReversal.getVisibility() != View.VISIBLE) {
            groupReversal.setVisibility(View.VISIBLE);
        }
        if (groupSubmit.getVisibility() != View.GONE) {
            groupSubmit.setVisibility(View.GONE);
        }
        if (groupRestart.getVisibility() != View.GONE) {
            groupRestart.setVisibility(View.GONE);
        }
        if (groupStop.getVisibility() != View.GONE) {
            groupStop.setVisibility(View.GONE);
        }
        if (customVideoController2.getVisibility() != View.GONE) {
            customVideoController2.setVisibility(View.INVISIBLE);
        }
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            sfvVideo.setVisibility(View.VISIBLE);
        }
        tvStopRecordCurrentTime.setText(TimeUtils.stringForTime(0));
        isHasRecordView = false;
    }

    protected abstract void performStartPreView(boolean show);


}
