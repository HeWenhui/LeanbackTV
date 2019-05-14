package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.UploadAliUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadVideoService extends Service {
    private String videoUrl, audioUrl;
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private String liveId, courseWareId;
    private CountDownLatch latch = new CountDownLatch(2);
    private UploadAliUtils uploadAliUtils;
    private AtomicInteger uploadVideoNum = new AtomicInteger(3);
    private XesStsUploadListener videoUploadListener = new XesStsUploadListener() {
        @Override
        public void onProgress(XesCloudResult result, int percent) {

            logger.i("video upload percent:" + percent);
        }

        @Override
        public void onSuccess(XesCloudResult result) {
            videoUrl = result.getHttpPath();
            logger.i("video upload succes " + videoUrl);
            XESToastUtils.showToast(UploadVideoService.this, "视频上传成功");
//            uploadSuccess();

            ShareDataManager.getInstance().put(
                    ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                    1,
                    ShareDataManager.SHAREDATA_NOT_CLEAR,
                    false);
            latch.countDown();
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

    private XesStsUploadListener audioUploadListener = new XesStsUploadListener() {
        @Override
        public void onProgress(XesCloudResult result, int percent) {
            logger.i("audio upload percent:" + percent);
        }

        @Override
        public void onSuccess(XesCloudResult result) {
            audioUrl = result.getHttpPath();
            logger.i("audio upload succes " + audioUrl);
            XESToastUtils.showToast(UploadVideoService.this, "上传音频成功");
            latch.countDown();
            try {
                latch.await();
                uploadSuccess();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(XesCloudResult result) {
            audioUrl = "";
            uploadSuccess();
        }
    };

    public interface uploadCallback {
        void uploadSuccess(String videoUrl, String audioUrl);
    }

    public void setCallBack(uploadCallback callBack) {
        this.callBack = callBack;
    }

    private uploadCallback callBack;

    private synchronized void uploadSuccess() {
        //允许audioUrl为""，""代表成功
        if (videoUrl == null) {
            return;
        }
        if (callBack != null) {
            callBack.uploadSuccess(videoUrl, audioUrl);
            stopSelf();
        }
    }

    private void uploadVideo() {
        uploadAliUtils.uploadFile(StorageUtils.imageUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);
    }

    private void uploadAudio() {
        uploadAliUtils.uploadFile(StorageUtils.audioUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, audioUploadListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        latch = new CountDownLatch(2);
        uploadAliUtils = new UploadAliUtils(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        liveId = intent.getStringExtra("liveId");
        courseWareId = intent.getStringExtra("courseWareId");
        uploadVideo();
        uploadAudio();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new UploadBinder();
    }

    public class UploadBinder extends Binder {

//        private String audioUrl;

//        private String videoUrl;

        public UploadVideoService getService() {
            return UploadVideoService.this;
        }

//        public String getVideoUrl() {
//            return videoUrl;
//        }
//
//        public void setVideoUrl(String videoUrl) {
//            this.videoUrl = videoUrl;
//        }
//
//        public String getAudioUrl() {
//            return audioUrl;
//        }
//
//        public void setAudioUrl(String audioUrl) {
//            this.audioUrl = audioUrl;
//        }
    }
}
