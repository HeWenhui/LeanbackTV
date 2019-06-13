package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

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
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCutImage;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

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
    private LiveThreadPoolExecutor liveThreadPoolExecutor;

    public StudyReportBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mLogtf = new LogToFile(activity, TAG);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo.getAllowSnapshot() == 1) {
            putInstance(StudyReportAction.class, this);
            liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
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
        if (predraw) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    doImgCut(type, view, cut);
                    return false;
                }
            });
        } else {
            doImgCut(type, view, cut);
        }
    }

    private void doImgCut(int type, View view, boolean cut) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Bitmap viewBitmap = getViewBitmap(view, stringBuilder, atomicBoolean);
        if (viewBitmap != null) {
            upLoadViewBitmap(viewBitmap, stringBuilder, atomicBoolean, cut, type);
        } else {
            mLogtf.d("cutImage:type=" + type + ",bmpScreen=null");
        }
    }

    /**
     * 从View 得到bitmap 需在主线程执行
     *
     * @param view
     * @param stringBuilder
     * @param atomicBoolean
     * @return
     */
    private Bitmap getViewBitmap(View view, StringBuilder stringBuilder, AtomicBoolean atomicBoolean) {
        Bitmap resultBitmap = null;
        try {
            if (view != null) {
//                stringBuilder = new StringBuilder();
//                atomicBoolean = new AtomicBoolean(false);
                resultBitmap = LiveCutImage.getViewBitmap(view, stringBuilder, atomicBoolean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLogtf.e("getViewBitmap", e);
            CrashReport.postCatchedException(e);
        }
        return resultBitmap;
    }


    /**
     * 上传截图
     *
     * @param viewBitmap
     */
    private void upLoadViewBitmap(final Bitmap viewBitmap, final StringBuilder stringBuilder, final AtomicBoolean atomicBoolean, final boolean cut, final int type) {
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap saveBitmap = viewBitmap;
                    if (cut) {
                        saveBitmap = LiveCutImage.cutBitmap(saveBitmap);
                    }
                    File savedir = new File(alldir, "type-" + type);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                    File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
                    if (!saveBitmap.isRecycled()) {
                        LiveCutImage.saveImage(saveBitmap, saveFile.getPath());
                        mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder);
                        uploadWonderMoment(type, saveFile.getPath());
                        if (cut || atomicBoolean.get()) {
                            saveBitmap.recycle();
                        }
                    } else {
                        mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder + " bitmap is recycled");
                    }
                } catch (Exception e) {
                    mLogtf.e("cutImage", e);
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    @Override
    public void cutImageAndVideo(final int type, final View view, final boolean cut, boolean predraw) {
        mLogtf.d("cutImageAndVideo:type=" + type + ",cut=" + cut + ",predraw=" + predraw);
        logger.i("studyreportBll" + 1);
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
                        logger.i("cutImageAndVideo:type=" + type + ",bmpScreen=null");
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
                                        CrashReport.postCatchedException(new LiveException(getClass().getSimpleName(), e));
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

    /**
     * 主线程截屏，解决小学语文三分屏，在子线程截图报错bug
     *
     * @param type
     * @param view
     * @param cut
     * @param predraw
     */
//    @Override
//    public void cutImageMainThread(final int type, final View view, final boolean cut, final boolean predraw) {
//        mLogtf.d("cutImage:type=" + type + ",cut=" + cut + ",predraw=" + predraw);
//        logger.i("StudyReportBll" + 1);
//        if (types.contains("" + type)) {
//            logger.i("has contains " + type + ",return;");
//            return;
//        }
//        Observable.
//                just(!types.contains("" + type)).
//                filter(new Predicate<Boolean>() {
//                    @Override
//                    public boolean test(Boolean aBoolean) throws Exception {
//                        return aBoolean;
//                    }
//                }).
//                observeOn(AndroidSchedulers.mainThread()).
//                map(new Function<Boolean, Bitmap>() {
//                    @Override
//                    public Bitmap apply(Boolean aBoolean) throws Exception {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
//                        Bitmap bmpScreen = LiveCutImage.getViewBitmap(view, stringBuilder, atomicBoolean);
//                        return bmpScreen;
//                    }
//                }).
//                filter(new Predicate<Bitmap>() {
//                    @Override
//                    public boolean test(Bitmap bitmap) throws Exception {
//                        return bitmap != null;
//                    }
//                }).
//                observeOn(Schedulers.io()).
//                doOnNext(new Consumer<Bitmap>() {
//                    @Override
//                    public void accept(Bitmap bitmap) throws Exception {
//                        File savedir = new File(alldir, "type-" + type);
//                        if (!savedir.exists()) {
//                            savedir.mkdirs();
//                        }
//                        File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
//                        LiveCutImage.saveImage(bitmap, saveFile.getPath());
//                        uploadWonderMoment(type, saveFile.getPath());
//                        mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder);
//                    }
//                }).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Consumer<Bitmap>() {
//                    @Override
//                    public void accept(Bitmap bmpScreen) throws Exception {
//                        view.destroyDrawingCache();
//
//                        if (cut || atomicBoolean.get()) {
//                            bmpScreen.recycle();
//                        }
//                    }
//                });
//        logger.i("StudyReportBll" + 2);
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    logger.i("StudyReportBll" + 6);
//                    StringBuilder stringBuilder = new StringBuilder();
//                    AtomicBoolean atomicBoolean = new AtomicBoolean(false);
//                    Bitmap bmpScreen = LiveCutImage.getViewBitmap(view, stringBuilder, atomicBoolean);
//                    if (bmpScreen == null) {
//                        mLogtf.d("cutImage:type=" + type + ",bmpScreen=null");
//                        return;
//                    }
//                    if (cut) {
//                        bmpScreen = LiveCutImage.cutBitmap(bmpScreen);
//                    }
//                    File savedir = new File(alldir, "type-" + type);
//                    if (!savedir.exists()) {
//                        savedir.mkdirs();
//                    }
//
//                    File saveFile = new File(savedir, System.currentTimeMillis() + ".jpg");
//                    if (!bmpScreen.isRecycled()) {
//                        logger.i("StudyReportBll" + 7);
//                        LiveCutImage.saveImage(bmpScreen, saveFile.getPath());
//                    } else {
//                        logger.i("StudyReportBll" + 8);
//                        bmpScreen = LiveCutImage.getViewCapture(view, stringBuilder, atomicBoolean);
//                        if (cut) {
//                            bmpScreen = LiveCutImage.cutBitmap(bmpScreen);
//                        }
//                        LiveCutImage.saveImage(bmpScreen, saveFile.getPath());
//                    }
//                    view.destroyDrawingCache();
//                    mLogtf.d("cutImage:type=" + type + ",path=" + saveFile.getPath() + ",creat=" + atomicBoolean.get() + ",sb=" + stringBuilder);
//                    uploadWonderMoment(type, saveFile.getPath());
//                    if (cut || atomicBoolean.get()) {
//                        bmpScreen.recycle();
//                    }
//                } catch (Exception e) {
//                    mLogtf.e("cutImage", e);
//                    CrashReport.postCatchedException(e);
//                }
//            }
//        };
//        logger.i("StudyReportBll" + 3);
//        if (predraw) {
//            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    logger.i("StudyReportBll" + 4);
//                    view.getViewTreeObserver().removeOnPreDrawListener(this);
//                    runnable.run();
//                    return false;
//                }
//            });
//        } else {
//
//            logger.i("StudyReportBll" + 5);
//            runnable.run();
//        }
//
//    }

    private class XesUploadListener implements XesStsUploadListener {

        private int type;
        private File finalFile;

        public XesUploadListener(int type, File finalFile) {
            this.type = type;
            this.finalFile = finalFile;
        }

        @Override
        public void onProgress(XesCloudResult result, int percent) {

        }

        @Override
        public void onSuccess(XesCloudResult result) {
            if (!AppConfig.DEBUG) {
                finalFile.delete();
            }
            logger.i("StudyReportBll" + 10);
            logger.d("asyncUpload:onSuccess=" + result.getHttpPath());
            if (mGetInfo != null) {
                if (mGetInfo.getPattern() == 6) {
                    //半身直播语文 isArts 为 0 ，useSkin为2
                    if (mGetInfo.getIsArts() == 0 && mGetInfo.getUseSkin() == 2) {
                        if (type == LiveVideoConfig.STUDY_REPORT.TYPE_PK_RESULT
                                || type == LiveVideoConfig.STUDY_REPORT.TYPE_AGORA
                                || type == LiveVideoConfig.STUDY_REPORT.TYPE_PRAISE) {
                            getHttpManager().uploadWonderMoment(type, result.getHttpPath(), new UploadImageUrl(type, false));
                        }
                    } else {
                        getHttpManager().uploadWonderMoment(type, result.getHttpPath(), new UploadImageUrl(type, false));
                        logger.i(" pattern:" + mGetInfo.getPattern() + " arts:" + mGetInfo.getIsArts() + " 不在这个范围内");
                    }
                } else if (mGetInfo.getPattern() == 1) {
                    if ((type == LiveVideoConfig.STUDY_REPORT.TYPE_PK_RESULT
                            || type == LiveVideoConfig.STUDY_REPORT.TYPE_AGORA
                            || type == LiveVideoConfig.STUDY_REPORT.TYPE_PRAISE
                            || type == LiveVideoConfig.STUDY_REPORT.TYPE_PK_WIN) && mGetInfo.getIsArts() == 2) {
                        getHttpManager().sendWonderfulMoment(
                                mGetInfo.getStuId(),
                                mGetInfo.getId(),
                                mGetInfo.getStuCouId(),
                                String.valueOf(type),
                                result.getHttpPath(),
                                new UploadImageUrl(type, false));
                    } else {
                        logger.i(" pattern:" + mGetInfo.getPattern() + " arts:" + mGetInfo.getIsArts() + " 不在这个范围内");
                    }
                }
            } else {
                getHttpManager().uploadWonderMoment(type, result.getHttpPath(), new UploadImageUrl(type, false));
            }
        }

        @Override
        public void onError(XesCloudResult result) {
            logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
        }
    }

    private class UploadImageUrl extends HttpCallBack {
        private int type;

        public UploadImageUrl(int type, boolean isShowTip) {
            super(isShowTip);
            this.type = type;
        }

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
    }

    private void uploadWonderMoment(final int type, String path) {
        StringBuilder sbuilder = new StringBuilder("uploadWonderMoment:type=" + type + ",path=" + path);
        if (mGetInfo != null) {
            sbuilder.append(",pattern = " + mGetInfo.getPattern());
        }
        logger.i(sbuilder.toString());
        mLogtf.d(sbuilder.toString());
        final File finalFile = new File(path);
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(activity);
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(path);
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);

        uploadEntity.setCloudPath(mGetInfo.getIsArts() == 2 ? CloudDir.LIVE_ARTS_MOMENT : CloudDir.LIVE_SCIENCE_MOMENT);

//            uploadEntity.setCloudPath(CloudDir.LIVE_SCIENCE_MOMENT);

        xesCloudUploadBusiness.asyncUpload(uploadEntity, new XesUploadListener(type, finalFile));
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
