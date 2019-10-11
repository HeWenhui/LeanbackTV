package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.UploadVideoService;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.CameraViewUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.CommonRxObserver;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.TimeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.IS_LIVE;
import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.LAYOUT_SIZE;
import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.RECORD_VALID_TIME;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class SuperSpeakerCameraPager extends SuperSpeakerCameraInflatePager implements
        ISuperSpeakerContract.ICameraView,
        ISuperSpeakerContract.ICommonPresenter {

    private ISuperSpeakerContract.ISuperSpeakerBridge bridge;

    private ISuperSpeakerContract.ICommonTip iCommonTip;

    /** 开始录制视频的时间 */
    private long startRecordVideoTime = -1;
    /** 结束录制时间 */
    private long stopRecordVideoTime;

    /** 上一次使用的摄像头 */
    protected boolean lastFacingBack = !isFacingBack;

    //include_livevideo_super_speaker_record_video_record_time
    /** 本地计时器 */
//    private int localTimer = 0;

    private String liveId;
    /** 试题所有回答时间 */
    private int answerTime = 0;
    /** 相机录制最大时长 */
    private int recordTime = 0;

    /** 是否正在录视频 */
    private boolean isInRecord = false;

    /** 试题时长 */
    private String courseWareId;

    /** 是否开始预览 */
    private boolean isPreView;
    /** 相机是否初始化成功 */
    protected boolean initCamera = false;
    /** 直播或者回放 1代表直播，2代表回放 */
    private String livevideo;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SuperSpeakerCameraPager(Context context,
                                   ISuperSpeakerContract.ISuperSpeakerBridge bridge,
                                   String liveId,
                                   String courseWareId,
                                   int answerTime,
                                   int recordTime,
                                   String back) {
        super(context);
        this.bridge = bridge;
        this.liveId = liveId;
        this.courseWareId = courseWareId;
        this.answerTime = answerTime;
        this.recordTime = recordTime;
        this.livevideo = back;
        initData();
    }


    protected abstract boolean isHasRecordPermission();


    /** 重新拍摄 */
    private void performRestart() {
        initShowView();
        customVideoController2.stop();
        customVideoController2.release();
        CameraViewUtils.deleteOldDir();
        performStartPreView(isFacingBack);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivStartRecord.setOnClickListener(new OnUnDoubleClickListener(1000) {
            @Override
            public void onUnDoubleClick(View v) {
                super.onUnDoubleClick(v);
                performStartRecordVideo();
            }
        });
        ivStopRecord.setOnClickListener(new OnUnDoubleClickListener(1000) {
            @Override
            public void onUnDoubleClick(View v) {
                super.onUnDoubleClick(v);
                logger.i("onUnDoubleClick Stop");
                performStopRecord();
            }
        });
        ivSubmitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_LIVE.equals(livevideo)) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715004));
                } else {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716004));
                }
                submitVideo("2");
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraBackPager == null) {
                    cameraBackPager = new SuperSpeakerCameraBackPager(mContext, livevideo);
                }
                cameraBackPager.setiClickListener(new SuperSpeakerCameraBackPager.IClickListener() {
                    @Override
                    public void onNoClick() {
                        removeView(cameraBackPager.getRootView());
                    }

                    @Override
                    public void onYesClick() {
                        removeCameraView();
                    }
                });
                cameraBackPager.setTvTittle(mContext.getString(R.string.super_speaker_back_camera_tip));
                if (isHasRecordView || isInRecord) {
                    //预览页面退出
                    if (IS_LIVE.equals(livevideo)) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715007));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716007));
                    }
                    cameraBackPager.setTextContentTip(mContext.getString(R.string.super_speaker_back_camera_content_tip));
                } else {
                    //录制前页面退出
                    if (IS_LIVE.equals(livevideo)) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715006));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716006));
                    }
                    cameraBackPager.setTextContentTip(IS_LIVE.equals(livevideo) ?
                            mContext.getString(R.string.super_speaker_back_exit_cannot_record) :
                            mContext.getString(R.string.super_speaker_back_exit));
                }

                if (cameraBackPager.getRootView().getParent() != null) {
                    ((ViewGroup) cameraBackPager.getRootView().getParent()).removeView(cameraBackPager.getRootView());
                }
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ((ViewGroup) mView).addView(cameraBackPager.getRootView(), layoutParams);
            }
        });
        ivRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_LIVE.equals(livevideo)) {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715005));
                } else {
                    UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716005));
                }
                if (cameraBackPager == null) {
                    cameraBackPager = new SuperSpeakerCameraBackPager(mContext, livevideo);
                }
                cameraBackPager.setiClickListener(new SuperSpeakerCameraBackPager.IClickListener() {
                    @Override
                    public void onNoClick() {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(IS_LIVE.equals(livevideo) ? R.string.livevideo_1715012 : R.string.livevideo_1716012));
                        removeView(cameraBackPager.getRootView());
                    }

                    @Override
                    public void onYesClick() {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(IS_LIVE.equals(livevideo) ? R.string.livevideo_1715013 : R.string.livevideo_1716013));
                        performRestart();
                        removeView(cameraBackPager.getRootView());
                    }
                });
                cameraBackPager.setTvTittle(mContext.getString(R.string.super_speaker_back_camera_rerecord_title_tip));
                cameraBackPager.setTextContentTip(mContext.getString(R.string.super_speaker_back_camera_content_tip));
                if (cameraBackPager.getRootView().getParent() != null) {
                    ((ViewGroup) cameraBackPager.getRootView().getParent()).removeView(cameraBackPager.getRootView());
                }
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ((ViewGroup) mView).addView(cameraBackPager.getRootView(), layoutParams);

            }
        });
        ivReversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performReversal();
            }
        });
    }

    protected void performReversal() {
        if (!isHasRecordPermission()) {
            return;
        }
        if (IS_LIVE.equals(livevideo)) {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715002));
        } else {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716002));
        }
        performStartPreView(isFacingBack = !isFacingBack);
    }


    @Override
    public View getView() {
        return getRootView();
    }

    @Override
    public void initData() {
//        tvRecordVideoTime.setText(TimeUtils.getInstance().stringForTimeChs(answerTime));
        logger.i(TimeUtils.stringForTime(answerTime));
        tvStartRecordTotalTime.setText(TimeUtils.stringForTime(recordTime));
        mView.post(coursewareTimer);
    }

    /**
     * 预览
     *
     * @param isFacingBack 使用前置摄像头还是后置摄像头
     */
    protected void performStartPreView(boolean isFacingBack) {
        if (!isHasRecordPermission()) {
            return;
        }
        if (camera1Utils != null && (lastFacingBack != isFacingBack || !camera1Utils.isStartPreview())) {
            lastFacingBack = isFacingBack;
            StorageUtils.setVideoPath(liveId, courseWareId);
//            StorageUtils.videoUrl = LiveHttpConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + courseWareId + ".mp4";
            logger.i(StorageUtils.getVideoPath());
            StableLogHashMap map = new StableLogHashMap().put(ISuperSpeakerContract.VIDEO_URL, StorageUtils.getVideoPath());
            if (camera1Utils.initCamera(isFacingBack, 1280, 720, StorageUtils.getVideoPath())) {
                initCamera = true;
                //把视频按比例拉长
                ViewGroup.LayoutParams layoutParams = CameraViewUtils.handleSize(mContext, camera1Utils.getCameraSize(), sfvVideo);

                map.put(ISuperSpeakerContract.CAMERA_SIZE,
                        camera1Utils.getCameraSize().height + "-" +
                                camera1Utils.getCameraSize().width)
                        .put(ISuperSpeakerContract.INIT_CAMERA, String.valueOf(true));
                if (layoutParams != null) {
                    map.put(LAYOUT_SIZE, layoutParams.height + "-" + layoutParams.width);
                }
                camera1Utils.startPreView();
            } else {
                initCamera = false;
                map.put(ISuperSpeakerContract.INIT_CAMERA, String.valueOf(false));
            }
            UmsAgentManager.umsAgentDebug(mContext, ISuperSpeakerContract.SUPER_SPEAKER_EVNT_ID, map.getData());
        }
    }


    /***
     * 停止拍摄
     */
    private void performStopRecord() {
        logger.i("performStopRecord()");
        if (!isHasRecordPermission()) {
            return;
        }
        if (isInTime()) {
            isHasRecordView = false;
            return;
        }
        long nowTime = System.currentTimeMillis();
//        recordTimerDisposable.dispose();
//        mView.removeCallbacks(recordVideoTimer);
        isHasRecordView = true;
        stopRecordVideoTime = nowTime;
        ivBack.setVisibility(View.VISIBLE);
        groupStop.setVisibility(View.GONE);
        groupSubmit.setVisibility(View.VISIBLE);
        groupRestart.setVisibility(View.VISIBLE);
        isInRecord = false;
        compositeDisposable.add(Observable.
                just(true).
                subscribeOn(Schedulers.io()).
                doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (IS_LIVE.equals(livevideo)) {
                            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715003));
                        } else {
                            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716003));
                        }
                        stopRecordVideo();
                        camera1Utils.releaseCamera();
                        if (sfvVideo.getHolder() != null && sfvVideo.getHolder().getSurface() != null) {
                            sfvVideo.getHolder().getSurface().release();
                        }
                    }
                }).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        long invisibleTime = System.currentTimeMillis();
                        sfvVideo.setVisibility(View.INVISIBLE);
                        invisibleTime = System.currentTimeMillis() - invisibleTime;
                        customVideoController2.setVisibility(View.VISIBLE);
                        customVideoController2.startPlayVideo(StorageUtils.getVideoPath(), 0);
                        if (invisibleTime > 1000) {
                            StableLogHashMap map = new StableLogHashMap().put(ISuperSpeakerContract.CAMERA_INVISIBLE, String.valueOf(invisibleTime));
                            UmsAgentManager.umsAgentDebug(mContext, ISuperSpeakerContract.SUPER_SPEAKER_EVNT_ID, map.getData());
                            logger.i(invisibleTime + "");
                        }
                        CameraViewUtils.sendVideoAlbum(mContext, StorageUtils.getVideoPath());

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        logger.e(throwable);
                        StableLogHashMap map = new StableLogHashMap().put(ISuperSpeakerContract.STOP_RECORD, Log.getStackTraceString(throwable));
                        UmsAgentManager.umsAgentDebug(mContext, ISuperSpeakerContract.SUPER_SPEAKER_EVNT_ID, map.getData());
                    }
                }));

        //录制视频小于1s
        processVideo();
    }

    /**
     * 处理音频放在 {@link UploadVideoService}中
     */
    private void processVideo() {
        StorageUtils.setAudioUrl(liveId, courseWareId);
        logger.i(" audio url:" + StorageUtils.getAudioUrl());
    }

    /**
     * 停止拍摄
     */
    public void stopRecordVideo() {
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
    protected void performStartRecordVideo() {
        initVar();
        if (bridge != null) {
            bridge.sendSuperSpeakerCameraStatus();
        }
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            logger.i("set surfaceView visible");
            sfvVideo.setVisibility(View.VISIBLE);
        }
        ivBack.setVisibility(View.GONE);
        tvStopRecordTotalTime.setText(TimeUtils.stringForTime(recordTime));
        groupStart.setVisibility(View.GONE);
        groupReversal.setVisibility(View.GONE);
        groupStop.setVisibility(View.VISIBLE);

        lottieAnimationView.playAnimation();

        if (isSurfViewCreat) {
//            performStartPreView(true);
            //因为刚开始录制视频，视频初始化有一段时间，所以多延迟0.5s
//            mView.postDelayed(recordVideoTimer, 1500);
//            isInRecord = true;
//            if (IS_LIVE.equals(livevideo)) {
//                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715001));
//            } else {
//                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716001));
//            }
//            startRecordVideo();

            Observable.just(true).
                    subscribeOn(Schedulers.io()).
                    doOnNext(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (IS_LIVE.equals(livevideo)) {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715001));
                            } else {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716001));
                            }
                            startRecordVideo();
                        }
                    }).
                    flatMap(new Function<Boolean, ObservableSource<Long>>() {
                        @Override
                        public ObservableSource<Long> apply(Boolean aBoolean) throws Exception {
                            return Observable.interval(1000, 1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
                        }
                    }).
                    subscribe(new CommonRxObserver<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            super.onSubscribe(d);
                            compositeDisposable.add(d);
//                            recordTimerDisposable.add(d);
                            isInRecord = true;
                        }

                        @Override
                        public void onNext(Long o) {
                            if (!recordVideoT(o) && isInRecord) {
                                disposable.dispose();
                            }
                        }
                    });
        }
    }

//    private CompositeDisposable recordTimerDisposable = new CompositeDisposable(compositeDisposable);

    //初始化变量
    private void initVar() {
//        localTimer = 0;
    }

    private void startRecordVideo() {
        startRecordVideoTime = System.currentTimeMillis();
        boolean start = camera1Utils.startRecordVideo();
        if (!start) {
            XESToastUtils.showToast(mContext, "视频录制失败");
        }
    }

//    private Runnable recordVideoTimer = new Runnable() {
//        @Override
//        public void run() {
//            if (!isInRecord) {
//                return;
//            }
//            localTimer++;
//            logger.i("localTimer = " + localTimer + ", recordTime = " + recordTime);
//            if (localTimer >= recordTime) {
//                performStopRecord();
//                return;
//            }
//            tvStopRecordCurrentTime.setText(TimeUtils.stringForTime(localTimer));
//            mView.postDelayed(this, 1000);
//        }
//    };

    private boolean recordVideoT(long o) {
        if (!isInRecord) {
            return false;
        }
        o = o + 1;
        logger.i("o = " + o + ", recordTime = " + recordTime);
        if (o >= recordTime) {
            performStopRecord();
            return false;
        }
        tvStopRecordCurrentTime.setText(TimeUtils.stringForTime(o));
//        mView.postDelayed(this, 1000);
        return true;
    }

    /** 试题时间倒计时 */
    private Runnable coursewareTimer = new CourseWareTimer();

    private class CourseWareTimer implements Runnable {
        /** 倒计时是否结束 */
        private boolean isDownOver;

        @Override
        public void run() {
            tvRecordVideoTime.setText(TimeUtils.stringForTimeChs(answerTime));
            if (answerTime == 0) {
                answerTime++;
                isDownOver = true;
                tvRecordVideoTime.setTextColor(mContext.getResources().getColor(R.color.COLOR_D95151));
            } else if (isDownOver) {
                answerTime++;
            } else {
                answerTime--;
            }
//            logger.i("answerTime:" + answerTime);
            mView.postDelayed(coursewareTimer, 1000);
        }
    }

    /** 提交视频 */
    private void submitVideo(String isForce) {
        if (bridge != null) {
            logger.i("音量大小" + camera1Utils.getVolum() + "");
            bridge.submitSpeechShow(isForce, String.valueOf(camera1Utils.getVolum() - 20));
            removeCameraView();

        }
    }

    //    @Override
    public void removeView(View view) {
        if (view != null && view.getParent() == mView) {
            ((ViewGroup) mView).removeView(view);
        }
    }

    @Override
    public void removeCameraView() {
        isInRecord = false;
        compositeDisposable.dispose();
//        recordTimerDisposable.dispose();
//        mView.removeCallbacks(recordVideoTimer);
        mView.removeCallbacks(coursewareTimer);
        if (customVideoController2 != null) {
            customVideoController2.stop();
            customVideoController2.release();
        }
        stopRecordVideo();
        if (bridge != null) {
            bridge.removeView(mView);

        }
    }

    @Override
    public void timeUp() {
        stopRecordVideo();
        processVideo();
        long nowtime = System.currentTimeMillis();
        if (iCommonTip == null) {
            iCommonTip = new SuperSpeakerCommonTipPager(mContext, this);
        }
        ViewGroup.LayoutParams params = iCommonTip.getView().getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ((ViewGroup) mView).addView(iCommonTip.getView(), params);
        iCommonTip.timeUp(startRecordVideoTime == -1 || (nowtime - startRecordVideoTime < RECORD_VALID_TIME));
        if (isInRecord) {
            performStopRecord();
        }
    }

    @Override
    public void pauseVideo() {
        if (customVideoController2 != null) {
            logger.i("pauseVideo");
            customVideoController2.pause();
        }
    }

    @Override
    public void resumeVideo() {
        if (customVideoController2 != null) {
            logger.i("resumeVideo");
            if (customVideoController2.getVisibility() == View.VISIBLE) {
                customVideoController2.startPlayVideo(StorageUtils.getVideoPath(), 0);
            } else {
                if (isInRecord && !isInTime()) {
                    performStopRecord();
                }
            }
        }
    }

    @Override
    public void submitSpeechShow(String isForce) {
        submitVideo(isForce);
    }

    @Override
    public boolean onUserBackPressed() {
        if (mView != null && mView.getVisibility() == View.VISIBLE && mView.getParent() != null && bridge != null && bridge.containsView()) {
            ivBack.performClick();
            super.onUserBackPressed();
            return true;
        }
        return false;
    }
}

