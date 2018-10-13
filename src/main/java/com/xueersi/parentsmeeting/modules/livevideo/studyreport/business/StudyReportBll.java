package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;

import io.agora.rtc.plugin.rawdata.MediaDataAudioObserver;
import io.agora.rtc.plugin.rawdata.MediaDataObserverPlugin;
import io.agora.rtc.plugin.rawdata.MediaDataVideoObserver;
import io.agora.rtc.plugin.rawdata.MediaPreProcessing;

/**
 * 学习报告截图
 *
 * @author 林玉强
 * created  at 2018/10/12
 */
public class StudyReportBll extends LiveBaseBll implements StudyReportAction {
    Handler handler = new Handler(Looper.getMainLooper());
    private LogToFile mLogtf;
    private MediaDataObserverPlugin mediaDataObserverPlugin;

    public StudyReportBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mLogtf = new LogToFile(activity, TAG);
        putInstance(StudyReportAction.class, this);
    }

    public void onFirstRemoteVideoDecoded(final int uid) {
        boolean load = MediaPreProcessing.isLoad();
        mLogtf.d("onFirstRemoteVideoDecoded:load=" + load);
        if (load) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    createPlugin();
                    File alldir = LiveCacheFile.geCacheFile(activity, "agora/" + uid);
                    if (!alldir.exists()) {
                        alldir.mkdirs();
                    }
                    File saveFile = new File(alldir, System.currentTimeMillis() + ".jpg");
                    mLogtf.d("onFirstRemoteVideoDecoded:saveFile=" + saveFile);
                    mediaDataObserverPlugin.saveRenderVideoShot(saveFile.getPath(), uid, new MediaDataObserverPlugin.OnRenderVideoShot() {
                        @Override
                        public void onRenderVideoShot(String path) {
                            mLogtf.d("onRenderVideoShot:path=" + path);
                            uploadWonderMoment(path, 2);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                createPlugin();
                mediaDataObserverPlugin.addDecodeBuffer(uid, 1382400);//720P
            }
        });
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                createPlugin();
                mediaDataObserverPlugin.removeDecodeBuffer(uid);
            }
        });
    }

    private void uploadWonderMoment(String path, final int type) {
        final File finalFile = new File(path);
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(activity);
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(path);
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.LIVE_STUDY_REPORT);
        xesCloudUploadBusiness.asyncUpload(uploadEntity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                finalFile.delete();
                logger.d("asyncUpload:onSuccess=" + result.getHttpPath());
                getHttpManager().uploadWonderMoment(type, result.getHttpPath(), new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logger.d("onPmSuccess:type=" + type + ",responseEntity=" + responseEntity.getJsonObject());
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.d("onPmError:type=" + type + ",responseEntity=" + responseEntity.getErrorMsg());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.d("onPmFailure:type=" + type + ",msg=" + msg, error);
                    }
                });
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result);
            }
        });
    }

    private void createPlugin() {
        if (mediaDataObserverPlugin == null) {
            mediaDataObserverPlugin = MediaDataObserverPlugin.the();
            MediaPreProcessing.setCallback(mediaDataObserverPlugin);
            MediaPreProcessing.setVideoCaptureByteBUffer(mediaDataObserverPlugin.byteBufferCapture);
            MediaPreProcessing.setAudioRecordByteBUffer(mediaDataObserverPlugin.byteBufferAudioRecord);
            MediaPreProcessing.setAudioPlayByteBUffer(mediaDataObserverPlugin.byteBufferAudioPlay);
            MediaPreProcessing.setBeforeAudioMixByteBUffer(mediaDataObserverPlugin.byteBufferBeforeAudioMix);
            MediaPreProcessing.setAudioMixByteBUffer(mediaDataObserverPlugin.byteBufferAudioMix);
            mediaDataObserverPlugin.addVideoObserver(new MediaDataVideoObserver() {
                @Override
                public void onCaptureVideoFrame(byte[] data, int frameType, int width, int height, int bufferLength, int yStride, int uStride, int vStride, int rotation, long renderTimeMs) {

                }

                @Override
                public void onRenderVideoFrame(int uid, byte[] data, int frameType, int width, int height, int bufferLength, int yStride, int uStride, int vStride, int rotation, long renderTimeMs) {

                }
            });
            mediaDataObserverPlugin.addAudioObserver(new MediaDataAudioObserver() {
                @Override
                public void onRecordAudioFrame(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {

                }

                @Override
                public void onPlaybackAudioFrame(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {

                }

                @Override
                public void onPlaybackAudioFrameBeforeMixing(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {

                }

                @Override
                public void onMixedAudioFrame(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {

                }
            });
        }
    }
}
