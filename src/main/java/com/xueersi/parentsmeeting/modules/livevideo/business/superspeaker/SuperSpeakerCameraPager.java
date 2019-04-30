package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.content.Context;
import android.support.constraint.Group;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.RECORD_TIME;

public class SuperSpeakerCameraPager extends BasePager implements
        ISuperSpeakerContract.ICameraView,
        ISuperSpeakerContract.IRecordManager,
        ISuperSpeakerContract.ICommonPresenter {

    private ISuperSpeakerContract.IRedPackageView redPackageView;

    private ISuperSpeakerContract.ICameraPresenter presenter;

    private ISuperSpeakerContract.ICommonTip iCommonTip;

    private SurfaceView sfvVideo;

    private Camera1Utils camera1Utils;

    private ImageView ivStartRecord, ivStopRecord, ivSubmitRecord, ivBack, ivRestart, ivReversal;

    private Group groupSubmit, groupStop, groupStart, groupRestart, groupReversal;
    /** 开始录制视频的时间 */
    private long startRecordVideoTime;
    /** 结束录制时间 */
    private long stopRecordVideoTime;

    private TextView tvRecordVideoTime;

    private boolean isSurfViewCreat = false;
    /** 是否使用前置摄像头或者后置摄像头,默认faceback,即自拍 */
    private boolean isFacingBack = true;

    public SuperSpeakerCameraPager(Context context, ISuperSpeakerContract.ICameraPresenter presenter) {
        super(context);
        this.presenter = presenter;
        iCommonTip = new SuperSpeakerCommonTipPager(mContext, this);
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

        groupStart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_start);
        groupRestart = view.findViewById(R.id.group_livevideo_super_speaker_record_video_restart);
        groupReversal = view.findViewById(R.id.group_livevideo_super_speaker_record_video_reversal);
        groupStop = view.findViewById(R.id.group_livevideo_super_speaker_record_video_stop);
        groupSubmit = view.findViewById(R.id.group_livevideo_super_speaker_record_video_submit);

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
                camera1Utils.releaseCamera();
            }
        });
        initListener();
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRecordVideo();
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
                if (presenter != null) {
                    presenter.removeView(mView);
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
    public void updateNum(String num) {
        if (redPackageView == null) {
            redPackageView = new SuperSpeakerRedPackagePager(mContext);
        }
        if (redPackageView.getView().getVisibility() != View.VISIBLE) {
            redPackageView.getView().setVisibility(View.VISIBLE);
        }
        redPackageView.updateNum(num);
    }

    @Override
    public View getView() {
        return getRootView();
    }

    @Override
    public void initData() {

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
            camera1Utils.initCamera(isFacingBack, 1920, 1080);
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
        stopRecordVideoTime = System.currentTimeMillis();
        groupStop.setVisibility(View.GONE);
        stopRecordVideo();
        //录制视频小于1s
        if (isInTime()) {
            groupStart.setVisibility(View.VISIBLE);
            groupReversal.setVisibility(View.VISIBLE);
        } else {
            groupSubmit.setVisibility(View.VISIBLE);
            groupRestart.setVisibility(View.VISIBLE);
        }
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
        return stopRecordVideoTime - startRecordVideoTime < RECORD_TIME;
    }

    /**
     * 拍摄视频
     */
    private void performRecordVideo() {
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            logger.i("set surfaceView visible");
            sfvVideo.setVisibility(View.VISIBLE);
        }
        groupStart.setVisibility(View.GONE);
        groupReversal.setVisibility(View.GONE);
        groupStop.setVisibility(View.VISIBLE);
        if (isSurfViewCreat) {
            performStartPreView();
            startRecordVideo();
        }
    }

    private void startRecordVideo() {
        startRecordVideoTime = System.currentTimeMillis();
        camera1Utils.startRecordVideo();
    }

    private void submitVideo(String isForce) {
        if (presenter != null) {
            presenter.submitSpeechShow(isForce);
        }
//        mHttpManager.//sendSuperSpeakersubmitSpeech();
    }

    @Override
    public void removeRedPackageView() {
        if (redPackageView != null && redPackageView.getView() == mView) {
            ((ViewGroup) mView).removeView(redPackageView.getView());
        }
    }

    @Override
    public void removeView(View view) {
        if (view != null && view.getParent() == mView) {
            ((ViewGroup) mView).removeView(view);
        }
    }

    @Override
    public void timeUp() {
        long nowtime = System.currentTimeMillis();
        iCommonTip.timeUp(nowtime - startRecordVideoTime < RECORD_TIME);
    }

    @Override
    public void submitSpeechShow(String isForce) {
        submitVideo(isForce);
    }

}

