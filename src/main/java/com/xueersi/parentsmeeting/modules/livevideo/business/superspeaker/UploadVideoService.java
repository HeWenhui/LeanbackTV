package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.UploadAliUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

//https://blog.csdn.net/imxiangzi/article/details/76039978
public class UploadVideoService extends Service {
    private String videoUrl, audioUrl;
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private String liveId, courseWareId;
    private CountDownLatch latch = new CountDownLatch(2);
    private UploadAliUtils uploadAliUtils;
    private AtomicInteger uploadVideoNum = new AtomicInteger(3);
    private XesStsUploadListener videoUploadListener;

    private class VideoUploadListener implements XesStsUploadListener {
        String videoLocalUrl;

        public VideoUploadListener(String videoLocalUrl) {
            this.videoLocalUrl = videoLocalUrl;
        }

        @Override
        public void onProgress(XesCloudResult result, int percent) {

            logger.i("video upload percent:" + percent);
        }

        @Override
        public void onSuccess(XesCloudResult result) {
            videoUrl = result.getHttpPath();
            logger.i("video upload succes " + videoUrl);
//            XESToastUtils.showToast(UploadVideoService.this, "视频上传成功");
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
                uploadVideo(videoLocalUrl);
            }
        }
    }

    private class AudioUploadListener implements XesStsUploadListener {
        private String audioLocalUrl;

        public AudioUploadListener(String audioLocalUrl) {
            this.audioLocalUrl = audioLocalUrl;
        }

        @Override
        public void onProgress(XesCloudResult result, int percent) {
            logger.i("audio upload percent:" + percent);
        }

        @Override
        public void onSuccess(XesCloudResult result) {
            audioUrl = result.getHttpPath();
            deleteAudioFile(audioLocalUrl);
            logger.i("audio upload succes " + audioUrl);
//            XESToastUtils.showToast(UploadVideoService.this, "上传音频成功");
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

        private void deleteAudioFile(String url) {
            if (TextUtils.isEmpty(url)) {
                File file = new File(url);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private XesStsUploadListener audioUploadListener;

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

    private void uploadVideo(String videoUrl) {
        uploadAliUtils.uploadFile(videoUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);
    }

    private void uploadAudio(String audioUrl) {
        uploadAliUtils.uploadFile(audioUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, audioUploadListener);
    }

    @Override
    public void onCreate() {
        logger.i("调用onCreate");
        super.onCreate();
        uploadAliUtils = new UploadAliUtils(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.i("调用onStartCommand");
        performUploadUrl(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        logger.i("调用bindService");

        return super.bindService(service, conn, flags);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        logger.i("调用onBind");
//        performUploadUrl(intent);
        return new UploadBinder();
    }

    private void performUploadUrl(Intent intent) {
        latch = new CountDownLatch(2);
        liveId = intent.getStringExtra("liveId");
        courseWareId = intent.getStringExtra("courseWareId");
        String audioLocalUrl = intent.getStringExtra("audioRemoteUrl");
        String videoLocalUrl = intent.getStringExtra("videoRemoteUrl");
        audioUploadListener = new AudioUploadListener(audioLocalUrl);
        videoUploadListener = new VideoUploadListener(videoLocalUrl);
        uploadVideo(videoLocalUrl);
        uploadAudio(audioLocalUrl);
    }

    public class UploadBinder extends Binder {

        public UploadVideoService getService() {
            return UploadVideoService.this;
        }

    }
}
