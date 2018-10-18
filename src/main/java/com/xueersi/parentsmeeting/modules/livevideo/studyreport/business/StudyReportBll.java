package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCutImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

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
    File alldir = LiveCacheFile.geCacheFile(activity, "studyreport");
    ArrayList<String> types = new ArrayList<>();

    public StudyReportBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mLogtf = new LogToFile(activity, TAG);
        putInstance(StudyReportAction.class, this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo.getAllowSnapshot() == 1) {
            initData();
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    private void initData() {
        String str = mShareDataManager.getString(LiveVideoConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
        try {
            mLogtf.d("initData:jsonObject=" + str);
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.has("liveId")) {
                String liveid = jsonObject.getString("liveId");
                if (mLiveId.equals(liveid)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("types");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        types.add(jsonArray.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            mLogtf.e("initData", e);
        }
    }

    public void onFirstRemoteVideoDecoded(final int uid) {
        if (types.contains("" + LiveVideoConfig.STUDY_REPORT.TYPE_AGORA)) {
            return;
        }
        boolean load = MediaPreProcessing.isLoad();
        mLogtf.d("onFirstRemoteVideoDecoded:load=" + load);
        if (load) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    createPlugin();
                    final File agoradir = new File(alldir, "type-2/" + uid);
                    if (!agoradir.exists()) {
                        agoradir.mkdirs();
                    }
                    final File saveFile = new File(agoradir, System.currentTimeMillis() + ".jpg");
                    mLogtf.d("onFirstRemoteVideoDecoded:saveFile=" + saveFile);
                    mediaDataObserverPlugin.saveRenderVideoShot(saveFile.getPath(), uid, new MediaDataObserverPlugin.OnRenderVideoShot() {
                        @Override
                        public void onRenderVideoShot(String path) {
                            Bitmap bitmap = LiveCutImage.cutBitmap(path);
                            mLogtf.d("onRenderVideoShot:path=" + path + ",bitmap=null?" + (bitmap == null));
                            if (!AppConfig.DEBUG) {
                                new File(path).delete();
                            }
                            if (bitmap == null) {
                                return;
                            }
                            File saveFile = new File(agoradir, System.currentTimeMillis() + ".jpg");
                            LiveCutImage.saveImage(bitmap, saveFile.getPath());
                            bitmap.recycle();
                            uploadWonderMoment(LiveVideoConfig.STUDY_REPORT.TYPE_AGORA, saveFile.getPath());
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

    @Override
    public void cutImage(final int type, final View view, final boolean cut, boolean predraw) {
        mLogtf.d("cutImage:type=" + type + ",cut=" + cut + ",predraw=" + predraw);
        if (types.contains("" + type)) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bmpScreen = view.getDrawingCache();
                if (cut) {
                    bmpScreen = LiveCutImage.cutBitmap(bmpScreen);
                }
                File savedir = new File(alldir, "type-" + type);
                if (!savedir.exists()) {
                    savedir.mkdirs();
                }
                File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
                LiveCutImage.saveImage(bmpScreen, saveFile.getPath());
                view.destroyDrawingCache();
                mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath());
                uploadWonderMoment(type, saveFile.getPath());
                if (cut) {
                    bmpScreen.recycle();
                }
            }
        };
        if (predraw) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    runnable.run();
                    return false;
                }
            });
        } else {
            runnable.run();
        }
    }

    private void uploadWonderMoment(final int type, String path) {
        mLogtf.d("uploadWonderMoment:type=" + type + ",path=" + path);
        final File finalFile = new File(path);
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(activity);
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(path);
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.LIVE_SCIENCE_MOMENT);
        xesCloudUploadBusiness.asyncUpload(uploadEntity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                if (!AppConfig.DEBUG) {
                    finalFile.delete();
                }
                logger.d("asyncUpload:onSuccess=" + result.getHttpPath());
                getHttpManager().uploadWonderMoment(type, result.getHttpPath(), new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logger.d("onPmSuccess:type=" + type + ",responseEntity=" + responseEntity.getJsonObject());
                        types.add("" + type);
                        String str = mShareDataManager.getString(LiveVideoConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            String liveid = jsonObject.optString("liveId");
                            JSONArray jsonArray;
                            if (mLiveId.equals(liveid)) {
                                jsonArray = jsonObject.getJSONArray("types");
                            } else {
                                jsonObject.put("liveId", mLiveId);
                                jsonArray = new JSONArray();
                                jsonObject.put("types", jsonArray);
                            }
                            jsonArray.put("" + type);
                            mShareDataManager.put(LiveVideoConfig.LIVE_STUDY_REPORT_IMG, jsonObject.toString(), ShareDataManager.SHAREDATA_USER);
                            logger.d("onPmSuccess:jsonObject=" + jsonObject);
                        } catch (Exception e) {
                            mShareDataManager.put(LiveVideoConfig.LIVE_STUDY_REPORT_IMG, "{}", ShareDataManager.SHAREDATA_USER);
                            mLogtf.e("onPmSuccess", e);
                        }
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
