package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.Group;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.Camera1Utils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.MediaUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.TimeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.CustromVideoController2;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.RECORD_VALID_TIME;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SuperSpeakerCameraPager extends BasePager implements
        ISuperSpeakerContract.ICameraView,
        ISuperSpeakerContract.ICommonPresenter {

    private ISuperSpeakerContract.ISuperSpeakerBridge bridge;

    private ISuperSpeakerContract.ICommonTip iCommonTip;

    private SurfaceView sfvVideo;

    private Camera1Utils camera1Utils;

    private ImageView ivStartRecord, ivStopRecord, ivSubmitRecord, ivBack, ivRestart, ivReversal;

    private Group groupSubmit, groupStop, groupStart, groupRestart, groupReversal;
    /** 开始录制视频的时间 */
    private long startRecordVideoTime;
    /** 结束录制时间 */
    private long stopRecordVideoTime;
    /** 试题发布时长 */
    private TextView tvRecordVideoTime;

    private boolean isSurfViewCreat = false;
    /** 是否使用前置摄像头或者后置摄像头,默认faceback,即自拍 */
    private boolean isFacingBack = true;

    private CustromVideoController2 custromVideoController2;

    private View layoutStartViewTime, layoutStopViewTime;
    //include_livevideo_super_speaker_record_video_record_time
    private TextView tvStopRecordCurrentTime, tvStopRecordTotalTime;
    /** 本地计时器 */
    private int localTimer = 0;

    private String liveId;
    /** 试题所有回答时间 */
    private int answerTime = 0;
    /** 试题记录时间 */
    private int recordTime = 0;

    private TextView tvStartRecordTotalTime;

    public SuperSpeakerCameraPager(Context context, ISuperSpeakerContract.ISuperSpeakerBridge bridge, String liveId, int answerTime, int recordTime) {
        super(context);
        this.bridge = bridge;
        iCommonTip = new SuperSpeakerCommonTipPager(mContext, this);
        this.liveId = liveId;
        this.answerTime = answerTime;
        this.recordTime = recordTime;
        initData();
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

        groupStart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_start);
        groupRestart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_restart);
        groupReversal = view.findViewById(R.id.group_livevideo_super_speaker_record_video_reversal);
        groupStop = view.findViewById(R.id.group_livevideo_super_speaker_record_video_stop);
        groupSubmit = view.findViewById(R.id.group_livevideo_super_speaker_record_video_submit);

        custromVideoController2 = view.findViewById(R.id.custom_controller_livevideo_super_speaker_record_video_video_player);
        camera1Utils = new Camera1Utils(sfvVideo, new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                logger.i("surfaceCreated");
                isSurfViewCreat = true;
//                startRecordVideo();
                performStartPreView(isFacingBack);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                logger.i("surfaceDestroyed");
                holder.getSurface().release();
                camera1Utils.releaseCamera();
            }
        });
        initShowView();
        initListener();

        return view;
    }

    private void initShowView() {
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
    }

    @Override
    public void initListener() {
        super.initListener();
        ivStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performStartRecordVideo();
            }
        });
        ivStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performStopRecord();
            }
        });
        ivSubmitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitVideo("2");
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bridge != null) {

                    bridge.removeView(mView);
                }
            }
        });
        ivRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRestart();

            }
        });
        ivReversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performStartPreView(isFacingBack = !isFacingBack);
            }
        });
    }

    /** 删除旧的文件夹 */
    private void deleteOldDir() {

    }

    @Override
    public View getView() {
        return getRootView();
    }

    @Override
    public void initData() {
//        tvRecordVideoTime.setText(TimeUtils.getInstance().stringForTimeChs(answerTime));
        logger.i(TimeUtils.getInstance().stringForTime(answerTime));
        tvStartRecordTotalTime.setText(TimeUtils.getInstance().stringForTime(recordTime));
        mView.post(coursewareTimer);
    }

    /**
     * 预览
     *
     * @param isFacingBack 使用前置摄像头还是后置摄像头
     */
    private void performStartPreView(boolean isFacingBack) {
//        if(camera1Utils==null){
//            camera1Utils = new Camera1Utils()
//        }
        if (camera1Utils != null) {
            camera1Utils.initCamera(isFacingBack, 1920, 1080, LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + ".mp4");
        }
    }

    /** 重新拍摄 */
    private void performRestart() {
        groupStart.setVisibility(View.VISIBLE);
        groupReversal.setVisibility(View.VISIBLE);
        groupSubmit.setVisibility(View.GONE);
        groupRestart.setVisibility(View.GONE);
        deleteOldDir();
    }

    /***
     * 停止拍摄
     */
    private void performStopRecord() {
        long nowTime = System.currentTimeMillis();
        if (isInTime()) {
            return;
        }
        stopRecordVideoTime = nowTime;
        groupStop.setVisibility(View.GONE);
        stopRecordVideo();
        //录制视频小于1s
//            groupStart.setVisibility(View.VISIBLE);
//            groupReversal.setVisibility(View.VISIBLE);
        groupSubmit.setVisibility(View.VISIBLE);
        groupRestart.setVisibility(View.VISIBLE);
        custromVideoController2.setVisibility(View.VISIBLE);

        custromVideoController2.startPlayVideo("file://" + LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + ".mp4", 0);
        MediaUtils mediaUtils = new MediaUtils();
        String srcPath = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + ".mp4";
        String outVideoPath = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "video.mp4";
        String outAudioPath = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "audio.mp3";
        mediaUtils.process(srcPath, outVideoPath, outAudioPath);
    }

    /**
     * 停止拍摄
     */
    private void stopRecordVideo() {
        if (camera1Utils != null) {
            camera1Utils.stopRecordVideo();
        }
    }

    /** 录制时间是否在条件内 */
    private boolean isInTime() {
        long nowTime = System.currentTimeMillis();
        return nowTime - startRecordVideoTime < RECORD_VALID_TIME;
    }

    /**
     * 拍摄视频
     */
    private void performStartRecordVideo() {
        initVar();
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            logger.i("set surfaceView visible");
            sfvVideo.setVisibility(View.VISIBLE);
        }
        tvStopRecordTotalTime.setText(TimeUtils.getInstance().stringForTime(recordTime));
        groupStart.setVisibility(View.GONE);
        groupReversal.setVisibility(View.GONE);
        groupStop.setVisibility(View.VISIBLE);
        if (isSurfViewCreat) {
//            performStartPreView(true);
            mView.postDelayed(recordVideoTimer, 1000);
            startRecordVideo();
        }
    }

    //初始化变量
    private void initVar() {
        localTimer = 0;
    }

    private void startRecordVideo() {
        startRecordVideoTime = System.currentTimeMillis();
        camera1Utils.startRecordVideo();
    }

    private Runnable recordVideoTimer = new Runnable() {
        @Override
        public void run() {
            localTimer++;
            if (localTimer >= recordTime) {
                return;
            }
            tvStopRecordCurrentTime.setText(TimeUtils.getInstance().stringForTime(localTimer));
            mView.postDelayed(this, 1000);
        }
    };
    /** 试题时间倒计时 */
    private Runnable coursewareTimer = new Runnable() {
        @Override
        public void run() {
            tvRecordVideoTime.setText(TimeUtils.getInstance().stringForTimeChs(answerTime));
            if (answerTime == 0) {
                answerTime++;
                tvRecordVideoTime.setTextColor(0xD95151);
            } else {
                answerTime--;
            }
            mView.postDelayed(coursewareTimer, 1000);
        }
    };

    private void submitVideo(String isForce) {
        if (bridge != null) {
            bridge.submitSpeechShow(isForce);
            bridge.removeView(mView);
        }
    }

//    @Override
//    public void removeRedPackageView() {
//        if (redPackageView != null && redPackageView.getView() == mView) {
//            ((ViewGroup) mView).removeView(redPackageView.getView());
//        }
//    }

//    @Override
//    public void removeView(View view) {
//        if (view != null && view.getParent() == mView) {
//            ((ViewGroup) mView).removeView(view);
//        }
//    }

    @Override
    public void removeCameraView() {
        if (bridge != null) {
            bridge.removeView(mView);
        }
    }

    @Override
    public void timeUp() {
        long nowtime = System.currentTimeMillis();
        iCommonTip.timeUp(nowtime - startRecordVideoTime < RECORD_VALID_TIME);
    }

    //    @Override
    public void startPlayVideo() {

//        ILocalVideoController controller = new CustomVideoController(mContext);
//        ((CustomVideoController) controller).initData();
//        if (mView != null) {
//            controller.startPlayVideo(LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH, 0);
//        }
    }

    @Override
    public void pauseVideo() {
        if (custromVideoController2 != null) {
            custromVideoController2.pause();
        }
    }

    @Override
    public void resumeVideo() {
        if (custromVideoController2 != null) {
            custromVideoController2.start();
        }
    }

    @Override
    public void submitSpeechShow(String isForce) {
        submitVideo(isForce);
    }

}

