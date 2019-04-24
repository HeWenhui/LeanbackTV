//package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;
//
//import android.graphics.ImageFormat;
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.params.StreamConfigurationMap;
//import android.media.ImageReader;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.util.Size;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Camera2的工具类，要求targetVersion>=21 (5.0系统)
// *
// * @author zyy 2019/4/28
// */
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//public class Camera2Utils implements IRecordVideoView {
//    @Override
//    public void startRecordVideo() {
//
//    }
//
//    @Override
//    public void stopRecordVideo() {
//
//    }
//
//    /**
//     * 设置最佳尺寸
//     *
//     * @param sizes
//     * @param width
//     * @param height
//     * @return
//     */
//    private Size getPreferredPreviewSize(Size[] sizes, int width, int height) {
//        List<Size> collectorSizes = new ArrayList<>();
//        for (Size option : sizes) {
//            if (width > height) {
//                if (option.getWidth() > width && option.getHeight() > height) {
//                    collectorSizes.add(option);
//                }
//            } else {
//                if (option.getHeight() > width && option.getWidth() > height) {
//                    collectorSizes.add(option);
//                }
//            }
//        }
//        if (collectorSizes.size() > 0) {
//            return Collections.min(collectorSizes, new Comparator<Size>() {
//                @Override
//                public int compare(Size s1, Size s2) {
//                    return Long.signum(s1.getWidth() * s1.getHeight() - s2.getWidth() * s2.getHeight());
//                }
//            });
//        }
//        return sizes[0];
//    }
//
//    /**
//     * 预览尺寸
//     */
//    private Size mPreviewSize;
//    private int mSurfaceWidth;
//    private int mSurfaceHeight;
//
//    /**
//     * 检查相机是否有录音权限
//     */
//    private void checkRecordVideoPermission() {
//
//    }
//
//    /*** 相机管理类*/
//    CameraManager mCameraManager;
//
//    /*** 指定摄像头ID对应的Camera实体对象*/
//    CameraDevice mCameraDevice;
//
//    /**
//     * 打开指定摄像头ID的相机
//     *
//     * @param width
//     * @param height
//     * @param cameraId
//     */
//
//    private void openCamera(int width, int height, int cameraId) {
//        checkRecordVideoPermission();
////        if (ActivityCompat.checkSelfPermission(Camera2Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            return;
////        }
//        try {
//            mSurfaceWidth = width;
//            mSurfaceHeight = height;
////            getCameraId(cameraId);
//            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId + "");
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            // 获取设备方向
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            int totalRotation = sensorToDeviceRotation(characteristics, rotation);
//            boolean swapRotation = totalRotation == 90 || totalRotation == 270;
//            int rotatedWidth = mSurfaceWidth;
//            int rotatedHeight = mSurfaceHeight;
//            if (swapRotation) {
//                rotatedWidth = mSurfaceHeight;
//                rotatedHeight = mSurfaceWidth;
//            }
//            // 获取最佳的预览尺寸
//            mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
//            if (swapRotation) {
//                texture.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            } else {
//                texture.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
//            }
//            if (mImageReader == null) {
//                // 创建一个ImageReader对象，用于获取摄像头的图像数据,maxImages是ImageReader一次可以访问的最大图片数量
//                mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
//                        ImageFormat.JPEG, 2);
//                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
//            }
//            //检查是否支持闪光灯
//            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//            mFlashSupported = available == null ? false : available;
//            mCameraManager.openCamera(mCameraId + "", mStateCallback, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
