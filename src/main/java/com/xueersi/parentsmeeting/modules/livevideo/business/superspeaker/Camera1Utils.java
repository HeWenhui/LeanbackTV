package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;
import java.io.IOException;
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
    }

    private void getDataInfo(int width, int height) {
        int num = Camera.getNumberOfCameras();

        logger.d("NUM:" + num);
        Camera camera = Camera.open(MediaRecorder.VideoSource.CAMERA);
        Camera.Parameters parameters = camera.getParameters();
        parameters.getSupportedVideoSizes();
        cameraSize = getFitSize(parameters.getSupportedVideoSizes(), width, height);
        camera.release();
        // Log.d(TAG,"size:height="+cameraSize.height+"   width="+cameraSize.width);

        // int num = Camera.getNumberOfCameras();
        //  Log.d(TAG,"size:"+parameters.toString());
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
    public void startRecordVideo() {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象

        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                // 音频源率
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder
                .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 音频格式
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);


        // 视频码率
        mediarecorder.setVideoEncodingBitRate(1080 * 1920);

        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(cameraSize.width, cameraSize.height);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        // mediarecorder.setVideoFrameRate(20);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        String path = Environment.getExternalStorageDirectory() + "/parentsmeeting/love.mp4";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopRecordVideo() {
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;
//            EventBus.getDefault().post(new PlaybackVideoEvent.OnDemoRecordCommpleteEvent());
        }

    }
}
