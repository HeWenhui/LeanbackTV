package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.CustomLocalVideoPlayerBridge;

public class Camera1Utils implements IRecordVideoView {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    Camera.Size cameraSize = null;
    private MediaRecorder mediarecorder;// 录制视频的类

//    private SurfaceView mSurfaceView;

    private SurfaceHolder surfaceHolder;

    public Camera1Utils(SurfaceView mSurfaceView) {
//        this.mSurfaceView = mSurfaceView;

        surfaceHolder = mSurfaceView.getHolder();
        // setType必须设置，要不出错.
//        surfaceHolder.addCallback(callback2);
//        mFormatBuilder = new StringBuilder();
//        mFormatter = new Formatter();

    }

    private Camera camera;
    /** 视频播放地址路径 */
    private String videoPath;

    private boolean isFacingBack;

    public boolean initCamera(boolean isFacingBack, int width, int height, String videoPath) {
        if (camera != null) {
            releaseCamera();
        }
        this.isFacingBack = isFacingBack;
//        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//            @Override
//            public void onPreviewFrame(byte[] data, Camera camera) {
//                try {
//                    MediaCodec codec = MediaCodec.createByCodecName("1234");
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        int num = Camera.getNumberOfCameras();
        this.videoPath = videoPath;
//        logger.d("NUM:" + num);

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == (isFacingBack ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                camera = Camera.open(cameraInfo.facing);
            }
        }
        //魅族手机如果禁止权限，不会再次申请权限。
        if (camera == null) {
            return false;
        }
//        Camera camera = Camera.open(MediaRecorder.VideoSource.CAMERA);
//        camera = Camera.open(isFacingBack ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            cameraSize = getFitSize(parameters.getSupportedVideoSizes(), width, height);
            logger.i("cameraSize.height = " + cameraSize.height + " cameraSize.weight =" + cameraSize.width);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            logger.e(e);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e);
            return false;
        }
        return true;
        // Log.d(TAG,"size:height="+cameraSize.height+"   width="+cameraSize.width);

        // int num = Camera.getNumberOfCameras();
        //  Log.d(TAG,"size:"+parameters.toString());
    }

    // FIXME: 2019/5/14
    //https://bugly.qq.com/v2/crash-reporting/crashes/a0df5ed682/1120?pid=1  1120
    public void releaseCamera() {
//        Observable.
//                just(true).
//                subscribeOn(Schedulers.io()).
//                subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
        if (camera != null) {
            try {
                //停掉原来摄像头的预览
                camera.stopPreview();
                //移除回调
                camera.setPreviewCallback(null);
                //释放资源
                camera.release();
                //取消原来摄像头
                camera = null;
            } catch (Exception e) {
                e.printStackTrace();
                logger.e(e);
            }
        }
        if (mediarecorder != null) {
            try {
                mediarecorder.stop();
                mediarecorder.release();
                mediarecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
                logger.e(e);
            }

        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        logger.i(throwable);
//                    }
//                });
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private Camera.Size getFitSize(List<Camera.Size> sizes, int realWidth, int realHeight) {
        if (realWidth < realHeight) {
            int t = realHeight;
            realHeight = realWidth;
            realWidth = t;
        }
        for (Camera.Size size : sizes) {
            if (size.width == realWidth && size.height == realHeight) {
                logger.i("width = " + size.width + " ,height = " + size.height);
                return size;
            }
        }
        for (Camera.Size size : sizes) {
            if (1.0f * size.width / size.height == 1.0f * realWidth / realHeight) {
                return size;
            }
        }
        return sizes.get(0);
    }

    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;

    /**
     * 通过系统的CamcorderProfile设置MediaRecorder的录制参数
     * 首先查看系统是否包含对应质量的封装参数，然后再设置，根据具体需要的视频质量进行判断和设置
     */
//    private void setProfile() {
//        CamcorderProfile profile = null;
//        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
//        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
//        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//        }
//        if (profile != null) {
//            mediarecorder.setProfile(profile);
//        }
//    }
    @Override
    public boolean startRecordVideo() {
        if (camera == null) {
            return false;
        }
//        camera.release();
        volum = volumSum = volumNum = 0;
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
//        mMediaRecorder.setCamera(mCamera);
        camera.unlock();
        if (!isFacingBack) {
            mediarecorder.setCamera(camera);
        }
        // 设置录制视频源为Camera(相机)
//        mediarecorder.setOrientationHint(270);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 音频源率
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 音频格式
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediarecorder.setAudioSamplingRate(16000);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 视频码率
        mediarecorder.setVideoEncodingBitRate((int) (cameraSize.width * cameraSize.height * 2));

        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(cameraSize.width, cameraSize.height);
        logger.i("start Record:camera.width = " + cameraSize.width + " height =" + cameraSize.height);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        // mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());

        String path = videoPath;
        File file = new File(path);
        if (file != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
//        if (!file.exists()) {
//            try {
//                file.mkdirs();
//                file.createNewFile();
        logger.i("create " + path + " success");
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.i("create " + path + " fail");
//            }
//        }
        mediarecorder.setMaxDuration(MAX_LENGTH);
        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(path);
        try {
//            camera.lock();
//            camera.unlock();
            // 准备录制
            mediarecorder.prepare();

            // 开始录制
            mediarecorder.start();
            isStop.set(false);
            updateMicStatus();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    // FIXME: 2019/5/16 如何让mediaRecord和camera同时释放
    @Override
    public void stopRecordVideo() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mUpdateMicStatusTimer);
        }
        if (mediarecorder != null) {
            // 停止录制
            isStop.set(true);
            try {
//                mediarecorder.stop();
                volum = volumSum / volumNum;
                logger.i("camera volum = " + volum);
                // 释放资源
//                mediarecorder.release();
                //                mediarecorder = null;
                logger.i(System.currentTimeMillis());
                releaseCamera();
                logger.i(System.currentTimeMillis());

            } catch (Exception e) {
                logger.e(e.toString());
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private AtomicBoolean isStop = new AtomicBoolean();
    /** 总的音量大小 */
    private int volumSum = 0;
    /** 统计的音量数量 */
    private int volumNum = 0;
    /** 总音量平均值 */
    private int volum;

    public int getVolum() {
        return volum;
    }

    private void updateMicStatus() {
        if (mediarecorder != null && !isStop.get()) {
            double ratio = (double) mediarecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }
            volumNum++;
            volumSum += db;
            logger.i("分贝值：" + db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

}