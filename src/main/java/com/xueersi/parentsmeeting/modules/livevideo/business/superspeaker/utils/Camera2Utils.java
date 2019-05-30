//package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.params.StreamConfigurationMap;
//import android.media.MediaRecorder;
//import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
//import android.util.Size;
//import android.view.Surface;
//import android.view.TextureView;
//import android.widget.Toast;
//
//import com.xueersi.lib.log.LoggerFactory;
//import com.xueersi.lib.log.logger.Logger;
//import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.IRecordVideoView;
//import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.AutoFitTextureView;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//
///**
// * Camera2的工具类，要求targetVersion>=21 (5.0系统)
// *
// * @author zyy 2019/4/28
// */
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//public class Camera2Utils implements IRecordVideoView {
//
//    private Context mContext;
//    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//
//    @Override
//    public boolean startRecordVideo() {
//        return false;
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
//    /**
//     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
//     */
//    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
//
//    /**
//     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
//     */
//    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
//
//        @Override
//        public void onOpened(@NonNull CameraDevice cameraDevice) {
//            mCameraDevice = cameraDevice;
//            startPreview();
//            mCameraOpenCloseLock.release();
//            if (null != mTextureView) {
//                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
//            }
//        }
//
//        @Override
//        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
//            mCameraOpenCloseLock.release();
//            cameraDevice.close();
//            mCameraDevice = null;
//        }
//
//        @Override
//        public void onError(@NonNull CameraDevice cameraDevice, int error) {
//            mCameraOpenCloseLock.release();
//            cameraDevice.close();
//            mCameraDevice = null;
//            Activity activity = getActivity();
//            if (null != activity) {
//                activity.finish();
//            }
//        }
//
//    };
//
//
//    /**
//     * An {@link AutoFitTextureView} for camera preview.
//     */
//    private AutoFitTextureView mTextureView;
//
//    /**
//     * A reference to the current {@link android.hardware.camera2.CameraCaptureSession} for
//     * preview.
//     */
//    private CameraCaptureSession mPreviewSession;
//
//    /**
//     * Start the camera preview.
//     */
//    private void startPreview() {
//        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
//            return;
//        }
//        try {
//            closePreviewSession();
//            SurfaceTexture texture = mTextureView.getSurfaceTexture();
//            assert texture != null;
//            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//
//            Surface previewSurface = new Surface(texture);
//            mPreviewBuilder.addTarget(previewSurface);
//
//            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
//                    new CameraCaptureSession.StateCallback() {
//
//                        @Override
//                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                            mPreviewSession = session;
//                            updatePreview();
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                            Activity activity = getActivity();
//                            if (null != activity) {
//                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }, mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
//     */
//    @SuppressWarnings("MissingPermission")
//    private void openCamera(int width, int height) {
//        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
//            requestVideoPermissions();
//            return;
//        }
////        final Activity activity = getActivity();
////        if (null == activity || activity.isFinishing()) {
////            return;
////        }
//        if (mContext == null) return;
//        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            logger.d("tryAcquire");
//            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
//                throw new RuntimeException("Time out waiting to lock camera opening.");
//            }
//            String cameraId = manager.getCameraIdList()[1];
//
//            // Choose the sizes for camera preview and video recording
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//            StreamConfigurationMap map = characteristics
//                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//            if (map == null) {
//                throw new RuntimeException("Cannot get available preview/video sizes");
//            }
//            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
//            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
//                    width, height, mVideoSize);
//
//            int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//            } else {
//                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
//            }
//            configureTransform(width, height);
//            mMediaRecorder = new MediaRecorder();
//            AssertUtil.openCamera(cameraId, mStateCallback, null);
//        } catch (CameraAccessException e) {
//            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
//            activity.finish();
//        } catch (NullPointerException e) {
//            // Currently an NPE is thrown when the Camera2API is used but not supported on the
//            // device this code runs.
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//        } catch (InterruptedException e) {
//            throw new RuntimeException("Interrupted while trying to lock camera opening.");
//        }
//    }
//
//    private void closeCamera() {
//        try {
//            mCameraOpenCloseLock.acquire();
//            closePreviewSession();
//            if (null != mCameraDevice) {
//                mCameraDevice.close();
//                mCameraDevice = null;
//            }
//            if (null != mMediaRecorder) {
//                mMediaRecorder.release();
//                mMediaRecorder = null;
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException("Interrupted while trying to lock camera closing.");
//        } finally {
//            mCameraOpenCloseLock.release();
//        }
//    }
//
//    private void closePreviewSession() {
//        if (mPreviewSession != null) {
//            mPreviewSession.close();
//            mPreviewSession = null;
//        }
//    }
//
//    /**
//     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
//     * {@link TextureView}.
//     */
//    private TextureView.SurfaceTextureListener mSurfaceTextureListener
//            = new TextureView.SurfaceTextureListener() {
//
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
//                                              int width, int height) {
//            openCamera(width, height);
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
//                                                int width, int height) {
//            configureTransform(width, height);
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
//            return true;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//        }
//
//    };
//
//    /**
//     * The {@link android.util.Size} of video recording.
//     */
//    private Size mVideoSize;
//
//    /**
//     * MediaRecorder
//     */
//    private MediaRecorder mMediaRecorder;
//
//    /**
//     * Whether the app is recording video now
//     */
//    private boolean mIsRecordingVideo;
//
//}
