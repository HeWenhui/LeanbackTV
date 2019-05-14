package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.UploadVideoService;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerPermissionPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.UploadAliUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SuperSpeakerBridge implements ISuperSpeakerContract.ISuperSpeakerBridge, ISuperSpeakerContract.IRedPackagePresenter {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    ISuperSpeakerContract.ICameraView iView;
    Context mContext;

    private ISuperSpeakerContract.ICameraPresenter iCameraPresenter;

    private ViewGroup parentView;

    private String liveId;

    private String courseWareId;

    public SuperSpeakerBridge(Context context,
                              ISuperSpeakerContract.ICameraPresenter iCameraPresenter,
                              ViewGroup viewGroup,
                              String liveId,
                              String courseWareId) {
        this.mContext = context;
        this.iCameraPresenter = iCameraPresenter;
        this.parentView = viewGroup;
        this.liveId = liveId;
        this.courseWareId = courseWareId;
    }

    /**
     * 表现录制视频
     */
    @SuppressLint("NewApi")
    @UiThread
    public void performShowRecordCamera(int answerTime, int recordTime) {
        if (iView == null) {
            iView = new SuperSpeakerPermissionPager(mContext, this, liveId, courseWareId, answerTime, recordTime);
        }
        ViewGroup.LayoutParams layoutParams = iView.getView().getLayoutParams();
        if (layoutParams == null) {
            logger.i("layoutParams = null");
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        //如果有录音权限
        if (isHasRecordPermission()) {
            logger.i("has record permission");
            parentView.addView(iView.getView(), layoutParams);
        } else {
            logger.i("no record permission");
            final ViewGroup.LayoutParams finalLayoutParams = layoutParams;
            boolean have = XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onDeny(String permission, int position) {

                        }

                        @Override
                        public void onGuarantee(String permission, int position) {
                            parentView.addView(iView.getView(), finalLayoutParams);
                        }
                    },
                    PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);

        }

    }

    private XesStsUploadListener videoUploadListener = new XesStsUploadListener() {
        @Override
        public void onProgress(XesCloudResult result, int percent) {

            logger.i("video upload percent:" + percent);
        }

        @Override
        public void onSuccess(XesCloudResult result) {
            videoUrl = result.getHttpPath();
            logger.i("video upload succes " + videoUrl);
            XESToastUtils.showToast(mContext, "视频上传成功");
//            uploadSuccess();

            ShareDataManager.getInstance().put(
                    ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                    1,
                    ShareDataManager.SHAREDATA_NOT_CLEAR,
                    false);
            if (Looper.getMainLooper() == Looper.myLooper()) {
                uploadSuccess();
            } else {
                latch.countDown();
            }
        }

        @Override
        public void onError(XesCloudResult result) {
//            videoUrl = "";
//            uploadSuccess();
            logger.i("video upload fail");
            //重试uploadVideoNum次
            if (uploadVideoNum.get() > 0) {
                uploadVideoNum.getAndDecrement();
                uploadVideo();
            }
        }
    };
    private AtomicInteger uploadVideoNum = new AtomicInteger(3);
    private String audioUrl, videoUrl;
    private XesStsUploadListener audioUploadListener = new XesStsUploadListener() {
        @Override
        public void onProgress(XesCloudResult result, int percent) {
            logger.i("audio upload percent:" + percent);

        }

        @Override
        public void onSuccess(XesCloudResult result) {
            audioUrl = result.getHttpPath();
            logger.i("audio upload succes " + audioUrl);
            XESToastUtils.showToast(mContext, "上传音频成功");
            if (Looper.getMainLooper() == Looper.myLooper()) {
                uploadSuccess();
            } else {
                latch.countDown();
                try {
                    latch.await();
                    uploadSuccess();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(XesCloudResult result) {
            audioUrl = "";
            uploadSuccess();
        }
    };
    private CountDownLatch latch;

    private synchronized void uploadSuccess() {
        //允许audioUrl为""，""代表成功
        if (audioUrl == null || videoUrl == null) {
            return;
        }
        if (iCameraPresenter != null) {
            iCameraPresenter.uploadSucess(videoUrl, audioUrl, voiceDecibel);
        }
    }

    private UploadAliUtils uploadAliUtils;

    private String voiceDecibel;

    @Override
    public void submitSpeechShow(String isForce, String averVocieDecibel) {
        long videoDuration = getVideoDuration();
        logger.i("averVocieDecibel = " + averVocieDecibel + "videoDuration =" + videoDuration);

        uploadAliUtils = new UploadAliUtils(mContext);

        ShareDataManager.getInstance().put(
                ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                1,
                ShareDataManager.SHAREDATA_NOT_CLEAR,
                false);
        latch = new CountDownLatch(2);
        this.voiceDecibel = averVocieDecibel;

        Intent intent = new Intent(mContext, UploadVideoService.class);
        intent.putExtra("liveId", liveId);
        intent.putExtra("courseWareId", courseWareId);
//        mContext.startService(intent);
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (iCameraPresenter != null) {
            iCameraPresenter.submitSpeechShow(isForce, String.valueOf(videoDuration));
        }

//        uploadVideo();
//        uploadAudio();

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        private UploadVideoService mService;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof UploadVideoService.UploadBinder) {
                mService = ((UploadVideoService.UploadBinder) service).getService();
                mService.setCallBack(new UploadVideoService.uploadCallback() {
                    @Override
                    public void uploadSuccess(String videoUrl, String audioUrl) {
                        SuperSpeakerBridge.this.videoUrl = videoUrl;
                        SuperSpeakerBridge.this.audioUrl = audioUrl;
                        SuperSpeakerBridge.this.uploadSuccess();
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

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

    private void uploadVideo() {
        uploadAliUtils.uploadFile(StorageUtils.imageUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);

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
    }

    private void uploadAudio() {
        uploadAliUtils.uploadFile(StorageUtils.audioUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, audioUploadListener);
    }

    @Override
    public void removeView(View view) {
        if (view.getParent() == parentView) {
            parentView.removeView(view);
        }
    }

    @Override
    public void pauseVideo() {
        if (iView != null) {
            iView.pauseVideo();
        }
    }

    //    @Override
    public void stopRecord() {
        if (iCameraPresenter != null) {
            iCameraPresenter.stopRecord();
        }
    }

    /**
     * 是否有相机和语音权限
     *
     * @return
     */
    private boolean isHasRecordPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.CAMERA", mContext.getPackageName()));
    }

    private ISuperSpeakerContract.IRedPackageView redPackageView;

    public void updateNum(String num) {
        if (redPackageView == null) {
            redPackageView = new SuperSpeakerRedPackagePager(mContext, this);
        }
        if (redPackageView.getView().getParent() != parentView) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            (parentView).addView(redPackageView.getView(), layoutParams);
        }
        if (redPackageView.getView().getVisibility() != View.VISIBLE) {
            redPackageView.getView().setVisibility(View.VISIBLE);
        }
        redPackageView.updateNum(num);
//        if (iView != null) {
//            iView.updateNum(num);
//        }
    }

    @Override
    public void timeUp() {
        if (iView != null) {
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
            iView.resumeVideo();
        }
    }

    @Override
    public boolean containsView() {
        return parentView != null && iView != null && iView.getView().getParent() == parentView;
    }
}
