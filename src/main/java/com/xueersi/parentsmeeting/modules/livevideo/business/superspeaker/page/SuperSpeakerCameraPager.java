package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.Group;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.UploadVideoService;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.Camera1Utils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.MediaUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.TimeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.CustomVideoController2;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.RECORD_VALID_TIME;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class SuperSpeakerCameraPager extends LiveBasePager implements
        ISuperSpeakerContract.ICameraView,
        ISuperSpeakerContract.ICommonPresenter {

    private ISuperSpeakerContract.ISuperSpeakerBridge bridge;

    private ISuperSpeakerContract.ICommonTip iCommonTip;
    /** 录制相机的view */
    private SurfaceView sfvVideo;
    /** 相机工具类 */
    private Camera1Utils camera1Utils;

    private ImageView ivStartRecord, ivStopRecord, ivSubmitRecord, ivBack, ivRestart, ivReversal;

    private Group groupSubmit, groupStop, groupStart, groupRestart, groupReversal;
    /** 开始录制视频的时间 */
    private long startRecordVideoTime = -1;
    /** 结束录制时间 */
    private long stopRecordVideoTime;
    /** 试题发布时长 */
    private TextView tvRecordVideoTime;

    private boolean isSurfViewCreat = false;
    /** 是否使用前置摄像头或者后置摄像头,默认faceback,即自拍 */
    protected boolean isFacingBack = false;
    /** 视频播放控制器 */
    private CustomVideoController2 customVideoController2;

    private View layoutStartViewTime, layoutStopViewTime;
    //include_livevideo_super_speaker_record_video_record_time
    /** 当前已经录制的时间 */
    private TextView tvStopRecordCurrentTime;
    /** 录制的总时间 */
    private TextView tvStopRecordTotalTime;
    /** 本地计时器 */
    private int localTimer = 0;

    private String liveId;
    /** 试题所有回答时间 */
    private int answerTime = 0;
    /** 相机录制最大时长 */
    private int recordTime = 0;

    private TextView tvStartRecordTotalTime;

    private SuperSpeakerCameraBackPager cameraBackPager;
    /** 是否正在录视频 */
    private boolean isInRecord = false;
    /** 是否已经录制过视频并且录制时间时间大于1s */
    private boolean isHasRecordView = false;
    /** 试题时长 */
    private String courseWareId;

    private LottieAnimationView lottieAnimationView;
    /** 是否开始预览 */
    private boolean isPreView;
    /** 相机是否初始化成功 */
    protected boolean initCamera = false;
    /** 直播或者回放 1代表直播，2代表回放 */
    private int livevideo;

    public SuperSpeakerCameraPager(Context context,
                                   ISuperSpeakerContract.ISuperSpeakerBridge bridge,
                                   String liveId,
                                   String courseWareId,
                                   int answerTime,
                                   int recordTime,
                                   int back) {
        super(context);
        this.bridge = bridge;

        this.liveId = liveId;
        this.courseWareId = courseWareId;
        this.answerTime = answerTime;
        this.recordTime = recordTime;
        this.livevideo = back;
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
//                startRecordVideo();
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

//    protected boolean isHasRecordPermission() {
//        return true;
//    }

    protected abstract boolean isHasRecordPermission();

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
        if (customVideoController2.getVisibility() != View.GONE) {
            customVideoController2.setVisibility(View.INVISIBLE);
        }
        if (sfvVideo.getVisibility() != View.VISIBLE) {
            sfvVideo.setVisibility(View.VISIBLE);
        }
        tvStopRecordCurrentTime.setText(TimeUtils.stringForTime(0));
        isHasRecordView = false;

    }

    /** 重新拍摄 */
    private void performRestart() {
        initShowView();
        customVideoController2.stop();
        customVideoController2.release();
//        groupStart.setVisibility(View.VISIBLE);
//        groupReversal.setVisibility(View.VISIBLE);
//        groupSubmit.setVisibility(View.GONE);
//        groupRestart.setVisibility(View.GONE);
//        customVideoController2.setVisibility(View.GONE);
        deleteOldDir();
        performStartPreView(isFacingBack);
    }

    @Override
    public void initListener() {
        super.initListener();
//        ivStartRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        ivStartRecord.setOnClickListener(new OnUnDoubleClickListener(1000) {
            @Override
            public void onUnDoubleClick(View v) {
                super.onUnDoubleClick(v);
                performStartRecordVideo();
            }
        });
//        ivStopRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performStopRecord();
//            }
//        });
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
                if (livevideo == 1) {
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
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715007));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716007));
                    }
                    cameraBackPager.setTextContentTip(mContext.getString(R.string.super_speaker_back_camera_content_tip));
                } else {
                    //录制前页面退出
                    if (livevideo == 1) {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715006));
                    } else {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716006));
                    }
                    cameraBackPager.setTextContentTip(livevideo == 1 ?
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
                if (livevideo == 1) {
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

                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(livevideo == 1 ? R.string.livevideo_1715012 : R.string.livevideo_1716012));
                        removeView(cameraBackPager.getRootView());
                    }

                    @Override
                    public void onYesClick() {
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(livevideo == 1 ? R.string.livevideo_1715013 : R.string.livevideo_1716013));
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
        if (livevideo == 1) {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715002));
        } else {
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716002));
        }
        performStartPreView(isFacingBack = !isFacingBack);
    }

    /** 删除旧的文件夹 */
    private void deleteOldDir() {
        File file = new File(StorageUtils.videoUrl);
        if (!file.exists()) {
            return;
        }
        file.delete();
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
//        if(camera1Utils==null){
//            camera1Utils = new Camera1Utils()
//        }
        if (!isHasRecordPermission()) {
            return;
        }
        if (camera1Utils != null) {
            StorageUtils.videoUrl = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + courseWareId + ".mp4";
            logger.i(StorageUtils.videoUrl);
            if (camera1Utils.initCamera(isFacingBack, 1280, 720, StorageUtils.videoUrl)) {
                initCamera = true;
                //把视频按比例拉长
                handleSize(camera1Utils.getCameraSize(), sfvVideo);
            } else {
                initCamera = false;
            }
        }
    }

    /**
     * 按照相机的比例缩小放大这个View
     *
     * @param size
     * @param view
     */
    private void handleSize(Camera.Size size, View view) {
        if (size == null || view == null) return;
        int width = size.width;
        int height = size.height;
        if (width > 0 && height > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                try {
                    Display defaultDisplay = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    defaultDisplay.getSize(point);
                    int screenWidth = point.y;
                    int screenHeight = point.x;
                    logger.i("screenWidth = " + screenWidth + " screenHeight = " + screenHeight);
                    if (screenWidth < screenHeight) {
                        int a = screenHeight;
                        screenHeight = screenWidth;
                        screenWidth = a;
                    }
                    if (height > width) {
                        int a = height;
                        height = width;
                        width = a;
                    }
                    double dw = screenHeight * 1.0 / height;
                    double dh = screenWidth * 1.0 / width;
                    double dd = dw > dh ? dh : dw;
                    logger.i("dd = " + dd);
                    layoutParams.width = (int) (width * dd);
                    layoutParams.height = (int) (height * dd);
                    view.setLayoutParams(layoutParams);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.e(e);
                }

            }
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
        mView.removeCallbacks(recordVideoTimer);
        isHasRecordView = true;
        stopRecordVideoTime = nowTime;
        ivBack.setVisibility(View.VISIBLE);
        groupStop.setVisibility(View.GONE);
        groupSubmit.setVisibility(View.VISIBLE);
        groupRestart.setVisibility(View.VISIBLE);

        isInRecord = false;
        io.reactivex.Observable.
                just(true).
                subscribeOn(Schedulers.io()).
                doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (livevideo == 1) {
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
                        sfvVideo.setVisibility(View.INVISIBLE);
                        customVideoController2.setVisibility(View.VISIBLE);
                        customVideoController2.startPlayVideo(StorageUtils.videoUrl, 0);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        logger.e(throwable);
                    }
                });

        //录制视频小于1s
//            groupStart.setVisibility(View.VISIBLE);
//            groupReversal.setVisibility(View.VISIBLE);


//        ((Activity)mContext).findViewById(R.id.sfv_livevideo_super_speaker_record_video).setVisibility(View.GONE);


//        String srcPath = StorageUtils.videoUrl;
//        StorageUtils.imageUrl = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + courseWareId + "video.mp4";


        processVideo();
    }

    /**
     * 处理音频放在 {@link UploadVideoService#decodeAudio()}中
     */
    private void processVideo() {
        MediaUtils mediaUtils = new MediaUtils();
        extraObservable = new MediaUtils.ExtraObservable();

        extraObservable.addObserver(new ExtractObserber());
        StorageUtils.audioUrl = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + courseWareId + "audio.mp3";
        logger.i(" audio url:" + StorageUtils.audioUrl);
//        mediaUtils.process(StorageUtils.videoUrl, StorageUtils.videoUrl, StorageUtils.audioUrl, extraObservable);
    }

    private MediaUtils.ExtraObservable extraObservable;

    private class ExtractObserber implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof MediaUtils.ExtraObservable) {

            }
        }
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
            mView.postDelayed(recordVideoTimer, 1500);
            isInRecord = true;
            if (livevideo == 1) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715001));
            } else {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1716001));
            }
            startRecordVideo();
        }
    }

    //初始化变量
    private void initVar() {
        localTimer = 0;
    }

    private void startRecordVideo() {
        startRecordVideoTime = System.currentTimeMillis();
        boolean start = camera1Utils.startRecordVideo();
        if (!start) {
            XESToastUtils.showToast(mContext, "视频录制失败");
        }
    }

    private Runnable recordVideoTimer = new Runnable() {
        @Override
        public void run() {
            if (!isInRecord) {
                return;
            }
            localTimer++;
            logger.i("localTimer = " + localTimer + ", recordTime = " + recordTime);
            if (localTimer >= recordTime) {
                performStopRecord();
                return;
            }
            tvStopRecordCurrentTime.setText(TimeUtils.stringForTime(localTimer));
            mView.postDelayed(this, 1000);
        }
    };

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
        mView.removeCallbacks(recordVideoTimer);
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
                customVideoController2.startPlayVideo(StorageUtils.videoUrl, 0);
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

