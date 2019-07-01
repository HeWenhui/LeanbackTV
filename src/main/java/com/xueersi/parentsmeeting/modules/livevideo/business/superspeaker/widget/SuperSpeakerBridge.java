package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;

import com.czt.mp3recorder.util.LameUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.UploadVideoService;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.UploadVideoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerPermissionPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class SuperSpeakerBridge implements ISuperSpeakerContract.ISuperSpeakerBridge, ISuperSpeakerContract.IRedPackagePresenter {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    ISuperSpeakerContract.ICameraView iView;
    Context mContext;

    private ISuperSpeakerContract.ICameraPresenter iCameraPresenter;

    private ViewGroup parentView;

    private String liveId;

    private String courseWareId;
    /** 直播或者回放 1代表直播，2代表回放 */
    private int livevideo;

    private UploadVideoEntity uploadVideoEntity;

    public SuperSpeakerBridge(Context context,
                              ISuperSpeakerContract.ICameraPresenter iCameraPresenter,
                              ViewGroup viewGroup,
                              String liveId,
                              String courseWareId,
                              int back,
                              UploadVideoEntity uploadVideoEntity) {
        this.mContext = context;
        this.iCameraPresenter = iCameraPresenter;
        this.parentView = viewGroup;
        this.liveId = liveId;
        this.courseWareId = courseWareId;
        this.livevideo = back;
        this.uploadVideoEntity = uploadVideoEntity;
    }

    /**
     * 表现录制视频
     */
    @SuppressLint("NewApi")
    @UiThread
    public void performShowRecordCamera(int answerTime, int recordTime) {
//        AndroidAudioConverter.load(mContext, new ILoadCallback() {
//            @Override
//            public void onSuccess() {
//                // Great!
//                logger.i("load FFMpeg success");
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                logger.e(error);
//                // FFmpeg is not supported by device
//                error.printStackTrace();
//            }
//        });
        //初始化lame
        LameUtil.init(16000, AudioFormat.CHANNEL_IN_DEFAULT, 16000,
                128, 2);
        if (iView == null) {
            iView = new SuperSpeakerPermissionPager(mContext, this, liveId, courseWareId, answerTime, recordTime, livevideo);
        }
        ViewGroup.LayoutParams layoutParams = iView.getView().getLayoutParams();
        if (layoutParams == null) {
            logger.i("layoutParams = null");
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        //如果有录音权限
        logger.i("has record permission");
        parentView.addView(iView.getView(), layoutParams);
    }

    //阿里云地址
    @Deprecated
    private String audioRemoteUrl, videoRemoteUrl;
    //声音分贝数
    private String voiceDecibel;
    private Intent serViceIntent;

    @Override
    public void submitSpeechShow(String isForce, String averVocieDecibel) {
        long videoDuration = getVideoDuration() / 1000l + 1;
        logger.i("averVocieDecibel = " + averVocieDecibel + "videoDuration =" + videoDuration);

        ShareDataManager.getInstance().put(
                ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                1,
                ShareDataManager.SHAREDATA_NOT_CLEAR,
                false);
//        latch = new CountDownLatch(2);
        this.voiceDecibel = averVocieDecibel;

        serViceIntent = new Intent(mContext, UploadVideoService.class);
        uploadVideoEntity.setAudioLocalUrl(StorageUtils.audioUrl);
        uploadVideoEntity.setVideoLocalUrl(StorageUtils.videoUrl);
        uploadVideoEntity.setAverVocieDecibel(averVocieDecibel);
        uploadVideoEntity.setSampleRate(16000);
//        uploadVideoEntity.setTestId(courseWareId);
//        uploadVideoEntity.setLiveId(liveId);

        serViceIntent.putExtra("UploadVideoEntity", uploadVideoEntity);
//        serViceIntent.putExtra("liveId", liveId);
//        serViceIntent.putExtra("courseWareId", courseWareId);
//        serViceIntent.putExtra("videoRemoteUrl", StorageUtils.videoUrl);
//        serViceIntent.putExtra("audioRemoteUrl", StorageUtils.audioUrl);
//        mContext.startService(intent);
        mContext.startService(serViceIntent);
        serviceConnection = new UploadServiceConnction();
        mContext.bindService(serViceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        logger.i("bindService success");
        if (iCameraPresenter != null) {
            iCameraPresenter.submitSpeechShow(isForce, String.valueOf(videoDuration));
        }

//        uploadVideo();
//        uploadAudio();

    }

    @Override
    public void sendSuperSpeakerCameraStatus() {
        if (iCameraPresenter != null) {
            iCameraPresenter.sendSuperSpeakerCameraStatus();
        }
    }

    @Deprecated
    private ServiceConnection serviceConnection;

    /**
     * 视频上传成功回调，暂时弃用，上传完全放到{@link UploadVideoService}中
     */
    @Deprecated
    private class UploadServiceConnction implements ServiceConnection {
        private UploadVideoService mService;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof UploadVideoService.UploadBinder) {
                mService = ((UploadVideoService.UploadBinder) service).getService();
                mService.setCallBack(new UploadVideoService.uploadCallback() {
                    @Override
                    public void uploadSuccess(String videoUrl, String audioUrl) {
                        SuperSpeakerBridge.this.videoRemoteUrl = videoUrl;
                        SuperSpeakerBridge.this.audioRemoteUrl = audioUrl;
                        SuperSpeakerBridge.this.uploadSuccess();
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

    }

    /**
     * 上传成功接口,暂时弃用，已经放到{@link UploadVideoService#uploadSuccess()}中
     */
    @Deprecated
    private void uploadSuccess() {
        //允许audioUrl为""，""代表成功
        if (audioRemoteUrl == null || videoRemoteUrl == null) {
            return;
        }
        if (iCameraPresenter != null) {
            logger.i("videoRemoteUrl:" + videoRemoteUrl +
                    " audioRemoteUrl:" + audioRemoteUrl +
                    " voiceDecibel:" + voiceDecibel);
            iCameraPresenter.uploadSucess(videoRemoteUrl, audioRemoteUrl, voiceDecibel);
        }
    }

    /** 获取视频时长 */
    private long getVideoDuration() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(StorageUtils.videoUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration();
    }

//    private void uploadVideo() {
//        uploadAliUtils.uploadFile(StorageUtils.videoRemoteUrl,
//                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
//                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);

//        Observable.create(new ObservableOnSubscribe<XesStsUploadListener>() {
//            @Override
//            public void subscribe(ObservableEmitter<XesStsUploadListener> e) throws Exception {
//                e.onNext(videoUploadListener);
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(new Consumer<XesStsUploadListener>() {
//                    @Override
//                    public void accept(XesStsUploadListener xesStsUploadListener) throws Exception {
//
//                    }
//                })
//                .subscribe(new Consumer<XesStsUploadListener>() {
//                    @Override
//                    public void accept(XesStsUploadListener xesStsUploadListener) throws Exception {
//
//                    }
//                });
//    }

//    private void uploadAudio() {
//        uploadAliUtils.uploadFile(StorageUtils.audioRemoteUrl,
//                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
//                XesCloudConfig.UPLOAD_OTHER, audioUploadListener);
//    }

    @Override
    public void removeView(final View view) {
        Observable.
                just(true).
//                observeOn(AndroidSchedulers.mainThread()).
//                doOnNext(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (iCameraPresenter != null) {
//                            iCameraPresenter.showAnima();
//                        }
//                        logger.i("nowtime:" + System.currentTimeMillis());
//                    }
//                }).
//                delay(2, TimeUnit.SECONDS).//delay走的子线程
        observeOn(AndroidSchedulers.mainThread()).
                doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        logger.i("进入doOnNxt " + System.currentTimeMillis());
                        if (view.getParent() == parentView) {
//                            if (serviceConnection != null) {
//                                logger.i("unbindService");
//                                mContext.unbindService(serviceConnection);
//                                serviceConnection = null;
//                            }
//                            if (serViceIntent != null) {
//                                logger.i("stopService");
//                                mContext.stopService(serViceIntent);
//                                serViceIntent = null;
//                            }
                            logger.i("移除view");
                            parentView.removeView(view);
                        } else {
                            logger.i("view 父布局不是parentView");
                        }
                    }
                }).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (iCameraPresenter != null) {
                            logger.i("开始播放直播");
                            iCameraPresenter.startLiveVideo();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        logger.i(throwable);
                    }
                });


    }

    @Override
    public void pauseVideo() {
        if (iView != null) {
            iView.pauseVideo();
        }
    }

    //    @Override
//    public void stopRecord() {
//        if (iCameraPresenter != null) {
//            iCameraPresenter.stopRecord();
//        }
//    }

    /**
     * 是否有相机和语音权限
     *
     * @return
     */
//    private boolean isHasRecordPermission() {
//        PackageManager pkm = mContext.getPackageManager();
//        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
//                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName())
//                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.CAMERA", mContext.getPackageName()));
//    }

    private ISuperSpeakerContract.IRedPackageView redPackageView;

    /**
     * 更新金币数量
     *
     * @param num
     */
    public void updateNum(String num) {
        if (redPackageView == null) {
            redPackageView = new SuperSpeakerRedPackagePager(mContext, this);
        }
        if (redPackageView.getView().getParent() != parentView) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            parentView.addView(redPackageView.getView(), layoutParams);
        }
        if (redPackageView.getView().getVisibility() != View.VISIBLE) {
            redPackageView.getView().setVisibility(View.VISIBLE);
        }
        redPackageView.updateNum(num);
//        if (iView != null) {
//            iView.updateNum(num);
//        }
    }

    /**
     * 教师端收题
     */
    @Override
    public void timeUp() {
        if (containsView()) {
            iView.timeUp();
        }
    }

//    @Override
//    public void startPlayVideo() {
//
//    }

    @Override
    public void resumeVideo() {
        if (iView != null) {
            logger.i("resumeVideo");
            iView.resumeVideo();
        }
    }

    @Override
    public boolean containsView() {
        return parentView != null && iView != null && iView.getView().getParent() == parentView;
    }

    /**
     * 停止录制视频
     */
    public void stopRecordVideo() {
        if (iView != null) {
            iView.stopRecordVideo();
        }
    }

    /**
     * 移除当前View
     */
    public void removeView() {
        if (iView != null && iView.getView().getParent() == parentView) {
            parentView.removeView(iView.getView());
        }
    }

}
