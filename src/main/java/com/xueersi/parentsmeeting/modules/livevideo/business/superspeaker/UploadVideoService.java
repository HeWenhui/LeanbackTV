package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.czt.mp3recorder.util.LameUtil;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.androidaudioconverter.AndroidAudioConverter;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.androidaudioconverter.callback.IConvertCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.androidaudioconverter.model.AudioFormat;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.UploadVideoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.AudioMediaCodecUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.UploadAliUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//https://blog.csdn.net/imxiangzi/article/details/76039978
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UploadVideoService extends Service {
    private String videoUrl, audioUrl = "";
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
                    2,
                    ShareDataManager.SHAREDATA_NOT_CLEAR,
                    false);
//            uploadSuccess();
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

//    private short[] convertShort(byte[] b) {
//        int len = b.length;
//        int shortL = b.length % 2 == 0 ? b.length / 2 : b.length / 2 + 1;
//        short[] aShort = new short[shortL];
//        for (int i = 0; i < aShort.length; i++) {
//            shortL[i] = b[i * 2] + b[i * 2 + 1] << 8;
//
//        }
//}
    /**
     * 录音文件输出
     */
    private FileOutputStream mFileOutputStream;
    private byte[] sampleTotal;

    private void convertMP3() {
        File wavFile = new File(uploadVideoEntity.getVideoLocalUrl());
        logger.i(uploadVideoEntity.getVideoLocalUrl());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                logger.i("success:convert Audio " + convertedFile.getAbsolutePath());
//                Toast.makeText(UploadVideoService.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
                uploadAudio(convertedFile.getAbsolutePath());
            }

            @Override
            public void onFailure(Exception error) {
                logger.w("Error:convert Audio");
                logger.e(error);
                latch.countDown();
            }
        };
        logger.i("Converting audio file...");
//        Toast.makeText(this, "Converting audio file...", Toast.LENGTH_SHORT).show();
        AndroidAudioConverter.with(this)
                .setFile(wavFile)
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert();
    }

    private int num = 0;

    /** 解码音频 */
    private void decodeAudio() {
        try {
            mFileOutputStream = new FileOutputStream(new File(uploadVideoEntity.getAudioLocalUrl()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final AudioMediaCodecUtils codecUtils = new AudioMediaCodecUtils(new AudioMediaCodecUtils.PCMDataListener() {
            @Override
            public void pcmData(byte[] bytes, int size) {
//                short[] shorts = convertShort(bytes);
                short[] shorts;
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 0, size);
                ShortBuffer shortBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
                shorts = new short[size / 2];
                shortBuffer.get(shorts, 0, size / 2);
                byte[] mp3Buffer = new byte[16000 / 20 + 7200];
                sampleTotal = new byte[16000 / 20 + 7200];
                int sampleSize = LameUtil.encode(shorts, shorts, size / 2, mp3Buffer);
                logger.i((num++) + " start decode audio to mp3 " + "buffer = " + sampleSize + " lenth = " + mp3Buffer.length);
                if (sampleSize > 0) {
                    System.arraycopy(mp3Buffer, 0, sampleTotal, 0, sampleSize);
                    try {
                        mFileOutputStream.write(sampleTotal, 0, sampleSize);
                        logger.i("decode success");
                    } catch (FileNotFoundException e) {
                        logger.e(e);
                        e.printStackTrace();
                    } catch (IOException e) {
                        logger.e(e);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void pcmComplete(boolean success) {
                uploadAudio(uploadVideoEntity.getAudioLocalUrl());
            }
        });
        Observable.
                just(codecUtils.init(uploadVideoEntity.getVideoLocalUrl())).
                subscribeOn(Schedulers.io()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        logger.i("初始化 " + aBoolean);
                        if (aBoolean) {
                            logger.i("aac to pcm");
                            codecUtils.aacToPCM();
                        }
                    }
                });
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
//            try {
//                latch.await();
//                uploadSuccess();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onError(XesCloudResult result) {
            audioUrl = "";
            logger.i("audio upload fail");
//            uploadSuccess();
            latch.countDown();
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

    private UploadVideoEntity uploadVideoEntity;

    private synchronized void uploadSuccess() {
        //允许audioUrl为""，""代表成功
        if (videoUrl == null) {
            return;
        }
//        if (callBack != null) {
//            callBack.uploadSuccess(videoUrl, audioUrl);
        LiveHttpManager liveHttpManager = new LiveHttpManager(this);
        logger.i("send uploadSuccess()");
        liveHttpManager.uploadSpeechShow(
                uploadVideoEntity.getLiveId(),
                uploadVideoEntity.getStuCouId(),
                uploadVideoEntity.getStuId(),
                uploadVideoEntity.getIsPlayBack(),
                uploadVideoEntity.getTestId(),
                uploadVideoEntity.getSrcType(),
                videoUrl,
                audioUrl,
                uploadVideoEntity.getIsUpload(),
                uploadVideoEntity.getAverVocieDecibel(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("upload success");
//                        XESToastUtils.showToast(mContext, "通知接口成功");
                        stopSelf();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.i("upload pmError " + responseEntity.getErrorMsg());
                        stopSelf();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("upload pmfail " + msg);
                        stopSelf();
                    }

                }
        );

//        }
    }

    private void uploadVideo(String videoLocalUrl) {
        uploadAliUtils.uploadFile(videoLocalUrl,
                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);
    }

    private void uploadAudio(String audioLocalUrl) {
        audioUploadListener = new AudioUploadListener(audioLocalUrl);
        uploadAliUtils.uploadFile(audioLocalUrl,
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
//        convertMP3();

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
//        liveId = intent.getStringExtra("liveId");
//        courseWareId = intent.getStringExtra("courseWareId");
        uploadVideoEntity = intent.getParcelableExtra("UploadVideoEntity");
        if (uploadVideoEntity == null) {
            return;
        }
        logger.i("sampleRate = " + uploadVideoEntity.getSampleRate());
        liveId = uploadVideoEntity.getLiveId();
        courseWareId = uploadVideoEntity.getTestId();
//        String audioLocalUrl = intent.getStringExtra("audioRemoteUrl");
//        String videoLocalUrl = intent.getStringExtra("videoRemoteUrl");
        String audioLocalUrl = uploadVideoEntity.getAudioLocalUrl();
        String videoLocalUrl = uploadVideoEntity.getVideoLocalUrl();

        audioUploadListener = new AudioUploadListener(audioLocalUrl);
        videoUploadListener = new VideoUploadListener(videoLocalUrl);
        uploadVideo(videoLocalUrl);

//        convertMP3();
        decodeAudio();
//        uploadAudio(audioLocalUrl);
    }

    public class UploadBinder extends Binder {

        public UploadVideoService getService() {
            return UploadVideoService.this;
        }

    }
}
