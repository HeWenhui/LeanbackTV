package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;

public class Camera1Utils implements IRecordVideoView {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    Camera.Size cameraSize = null;
    private MediaRecorder mediarecorder;// 录制视频的类

    private SurfaceView mSurfaceView;

    private SurfaceHolder surfaceHolder;

    public Camera1Utils(SurfaceView mSurfaceView, SurfaceHolder.Callback2 callback2) {
        this.mSurfaceView = mSurfaceView;
        surfaceHolder = mSurfaceView.getHolder();
        // setType必须设置，要不出错.
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(callback2);
//        mFormatBuilder = new StringBuilder();
//        mFormatter = new Formatter();

    }

    private Camera camera;
    /** 视频播放地址路径 */
    private String videoPath;

    public void initCamera(boolean isFacingBack, int width, int height, String videoPath) {
        if (camera != null) {
            camera.release();
        }

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




        int num = Camera.getNumberOfCameras();
        this.videoPath = videoPath;
        logger.d("NUM:" + num);
//        Camera camera = Camera.open(MediaRecorder.VideoSource.CAMERA);
        camera = Camera.open(isFacingBack ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.getSupportedVideoSizes();
        cameraSize = getFitSize(parameters.getSupportedVideoSizes(), width, height);
        logger.i("cameraSize.height = " + cameraSize.height + " cameraSize.weight =" + cameraSize.width);
        camera.startPreview();
        // Log.d(TAG,"size:height="+cameraSize.height+"   width="+cameraSize.width);

        // int num = Camera.getNumberOfCameras();
        //  Log.d(TAG,"size:"+parameters.toString());
    }

    public void releaseCamera() {
        if (camera != null) {
            //停掉原来摄像头的预览
            camera.stopPreview();
            //移除回调
            camera.setPreviewCallback(null);
            //释放资源
            camera.release();
            //取消原来摄像头
            camera = null;
        }
    }

    private Camera.Size getFitSize(List<Camera.Size> sizes, int realWidth, int realHeight) {
        if (realWidth < realHeight) {
            int t = realHeight;
            realHeight = realWidth;
            realWidth = t;
        }

        for (Camera.Size size : sizes) {
            if (1.0f * size.width / size.height == 1.0f * realWidth / realHeight) {
                return size;
            }
        }
        return sizes.get(0);
    }

    @Override
    public boolean startRecordVideo() {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象

        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                // 音频源率
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder
                .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 音频格式
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mediarecorder.setAudioSamplingRate(16000);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);


        // 视频码率
        mediarecorder.setVideoEncodingBitRate((int) (1080 * 1920 * 0.5f));

        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(cameraSize.width, cameraSize.height);
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
        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(path);
        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
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

    @Override
    public void stopRecordVideo() {
        if (mediarecorder != null) {
            // 停止录制
            try {
                mediarecorder.stop();
            } catch (Exception e) {
                logger.e(e.toString());
            }

            // 释放资源
            mediarecorder.release();
            mediarecorder = null;
//            EventBus.getDefault().post(new PlaybackVideoEvent.OnDemoRecordCommpleteEvent());
        }

    }

    //    private StringBuilder mFormatBuilder = new StringBuilder();
//    private Formatter mFormatter = new Formatter();

    /**
     * 把毫秒转换成：1：20：30这样的形式
     *
     * @param size
     * @return
     */
//    public String stringForTime(int size) {
////        int totalSeconds = timeMs / 1000;
////        int seconds = totalSeconds % 60;
////        int minutes = (totalSeconds / 60) % 60;
////        int hours = totalSeconds / 3600;
//////        mFormatBuilder.setLength(0);
////        if (hours > 0) {
////            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
////        } else {
////            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
////        }
//        String time;
//        if (size < 60) {
//            time = String.format("00:%02d", size % 60);
//        } else if (size < 3600) {
//            time = String.format("%02d:%02d", size / 60, size % 60);
//        } else {
//            time = String.format("%02d:%02d:%02d", size / 3600, size % 3600 / 60, size % 60);
//        }
//        return time;
//    }
}