package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SuperSpeakerCameraPager extends BasePager implements
        ISuperSpeakerContract.ICameraView,
        ISuperSpeakerContract.IRecordManager,
        ISuperSpeakerContract.ICommonPresenter {

    private ISuperSpeakerContract.IRedPackageView redPackageView;

    private ISuperSpeakerContract.ICameraPresenter presenter;

    private ISuperSpeakerContract.ICommonTip iCommonTip;

    private ImageView ivRecordCamera;

    private SurfaceView sfvVideo;

    private Camera1Utils camera1Utils;

    public SuperSpeakerCameraPager(Context context, ISuperSpeakerContract.ICameraPresenter presenter) {
        super(context);
        this.presenter = presenter;
        iCommonTip = new SuperSpeakerCommonTipPager(mContext, this);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_super_speaker_record_video, null);
        ivRecordCamera = view.findViewById(R.id.btn_livevideo_super_speaker_record_video);
        sfvVideo = view.findViewById(R.id.sfv_livevideo_super_speaker_record_video);
        camera1Utils = new Camera1Utils(sfvVideo, new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        return view;
    }

    @Override
    public void initListener() {
        super.initListener();
        ivRecordCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRecordVideo();

            }
        });
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
    public void initData() {

    }

    private void stopRecordVideo() {
        if (camera1Utils != null) {
            camera1Utils.stopRecordVideo();
        }
    }

    private void performRecordVideo() {
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            sfvVideo.setVisibility(View.VISIBLE);
        }
        startRecordVideo();
    }

    private void startRecordVideo() {
        camera1Utils.startRecordVideo();
    }

    private void submitVideo() {
        if (presenter != null) {
            presenter.submitSpeechShow("2");
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
        iCommonTip.timeUp();
    }

    @Override
    public void submitSpeechShow(String isForce) {
        if (presenter != null) {
            presenter.submitSpeechShow(isForce);
        }
    }
}

