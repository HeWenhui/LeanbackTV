package com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import com.tencent.bugly.crashreport.CrashReport;
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
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCutImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo.getAllowSnapshot() == 1) {
            putInstance(StudyReportAction.class, this);
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
                    mediaDataObserverPlugin.saveRenderVideoSnapshot(saveFile.getPath(), uid, new MediaDataObserverPlugin.OnRenderVideoShot() {
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
                mediaDataObserverPlugin.addDecodeBuffer(uid);//720P
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
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                    Bitmap bmpScreen = LiveCutImage.getViewBitmap(view, stringBuilder, atomicBoolean);
                    if (bmpScreen == null) {
                        mLogtf.d("cutImage:type=" + type + ",bmpScreen=null");
                        return;
                    }
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
                    mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder);
                    uploadWonderMoment(type, saveFile.getPath());
                    if (cut || atomicBoolean.get()) {
                        bmpScreen.recycle();
                    }
                } catch (Exception e) {
                    mLogtf.e("cutImage", e);
                    CrashReport.postCatchedException(e);
                }
            }
        };

        final Thread taskThread = new Thread(runnable);
        if (predraw) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    // runnable.run();
                    taskThread.start();
                    return false;
                }
            });
        } else {
            // runnable.run();
            taskThread.start();
        }
    }

    @Override
    public void cutImageAndVideo(final int type, final View view, final boolean cut, boolean predraw) {
        mLogtf.d("cutImageAndVideo:type=" + type + ",cut=" + cut + ",predraw=" + predraw);
        if (types.contains("" + type)) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                    Bitmap bmpScreen = LiveCutImage.getViewBitmap(view, stringBuilder, atomicBoolean);
                    if (bmpScreen == null) {
                        mLogtf.d("cutImageAndVideo:type=" + type + ",bmpScreen=null");
                        return;
                    }
                    if (cut) {
                        bmpScreen = LiveCutImage.cutBitmap(bmpScreen);
                    }
                    final File savedir = new File(alldir, "type-" + type);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                    final File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
                    LiveCutImage.saveImage(bmpScreen, saveFile.getPath());
                    view.destroyDrawingCache();
                    mLogtf.d("cutImageAndVideo:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder);
                    {
                        PlayerService vPlayer = (PlayerService) mLiveBll.getBusinessShareParam("vPlayer");
                        new PlayerView().getBitmap(vPlayer, activity, new PlayerView.OnGetBitmap() {
                            @Override
                            public void onGetBitmap(Bitmap videoBitmap) {
                                mLogtf.d("onGetBitmap:videoBitmap=null?" + (videoBitmap == null));
                                if (videoBitmap == null) {
                                    uploadWonderMoment(type, saveFile.getPath());
                                } else {
                                    try {
                                        Bitmap oldBitmap = BitmapFactory.decodeFile(saveFile.getPath());
                                        int width = (int) (videoBitmap.getWidth() * (LiveVideoConfig.VIDEO_WIDTH - LiveVideoConfig.VIDEO_HEAD_WIDTH) / LiveVideoConfig.VIDEO_WIDTH);
                                        Bitmap createBitmap = Bitmap.createBitmap(width, videoBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                        Canvas canvas = new Canvas(createBitmap);
                                        int left = (oldBitmap.getWidth() - videoBitmap.getWidth()) / 2;
                                        int top = (oldBitmap.getHeight() - videoBitmap.getHeight()) / 2;
                                        canvas.drawBitmap(videoBitmap, 0, 0, null);
                                        canvas.drawBitmap(oldBitmap, -left, -top, null);
                                        File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
                                        LiveCutImage.saveImage(createBitmap, saveFile.getPath());
                                        uploadWonderMoment(type, saveFile.getPath());
                                        if (AppConfig.DEBUG) {
                                            File videoSaveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
                                            LiveCutImage.saveImage(videoBitmap, videoSaveFile.getPath());
                                        }
                                        oldBitmap.recycle();
                                        createBitmap.recycle();
                                        videoBitmap.recycle();
                                        logger.d("onGetBitmap:create=" + createBitmap.getWidth() + ",old=" + oldBitmap.getWidth() + ",height=" + oldBitmap.getHeight() + ",left=" + left + ",top=" + top);
                                    } catch (Exception e) {
                                        CrashReport.postCatchedException(e);
                                        uploadWonderMoment(type, saveFile.getPath());
                                    }
                                }
                            }
                        });
                    }
                    if (cut || atomicBoolean.get()) {
                        bmpScreen.recycle();
                    }
                } catch (Exception e) {
                    mLogtf.e("cutImageAndVideo", e);
                    CrashReport.postCatchedException(e);
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
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
            }
        });
    }

    private void createPlugin() {
        if (mediaDataObserverPlugin == null) {
            mediaDataObserverPlugin = MediaDataObserverPlugin.the();
            MediaPreProcessing.setCallback(mediaDataObserverPlugin);
            MediaPreProcessing.setVideoCaptureByteBuffer(mediaDataObserverPlugin.byteBufferCapture);
            MediaPreProcessing.setAudioRecordByteBuffer(mediaDataObserverPlugin.byteBufferAudioRecord);
            MediaPreProcessing.setAudioPlayByteBuffer(mediaDataObserverPlugin.byteBufferAudioPlay);
            MediaPreProcessing.setBeforeAudioMixByteBuffer(mediaDataObserverPlugin.byteBufferBeforeAudioMix);
            MediaPreProcessing.setAudioMixByteBuffer(mediaDataObserverPlugin.byteBufferAudioMix);
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

//                @Override
//                public void onPlaybackAudioFrameBeforeMixing(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {
//
//                }

                @Override
                public void onPlaybackAudioFrameBeforeMixing(int uid, byte[] data, int audioFrameType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength){

                }
                @Override
                public void onMixedAudioFrame(byte[] data, int videoType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {

                }
            });
        }
    }
}
