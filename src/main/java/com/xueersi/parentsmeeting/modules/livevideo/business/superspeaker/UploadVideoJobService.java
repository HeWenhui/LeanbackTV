//package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;
//
//import android.content.Intent;
//import android.os.Looper;
//import android.support.annotation.NonNull;
//import android.support.v4.app.JobIntentService;
//
//import com.xueersi.common.config.AppConfig;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.component.cloud.config.CloudDir;
//import com.xueersi.component.cloud.config.XesCloudConfig;
//import com.xueersi.component.cloud.entity.XesCloudResult;
//import com.xueersi.component.cloud.listener.XesStsUploadListener;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.log.LoggerFactory;
//import com.xueersi.lib.log.logger.Logger;
//import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.UploadAliUtils;
//import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//;
//
//public class UploadVideoJobService extends JobIntentService {
//    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//
//    @Override
//    protected void onHandleWork(@NonNull Intent intent) {
//        UploadAliUtils uploadAliUtils = new UploadAliUtils(this);
//        String imageUrl = intent.getStringExtra("imageurl");
//        uploadAliUtils.uploadFile(imageUrl,
//                AppConfig.DEBUG ? CloudDir.CLOUD_TEST : CloudDir.LIVE_SUPER_SPEAKER,
//                XesCloudConfig.UPLOAD_OTHER, videoUploadListener);
//    }
//
//    private AtomicInteger uploadVideoNum = new AtomicInteger(3);
//    private XesStsUploadListener videoUploadListener = new XesStsUploadListener() {
//        @Override
//        public void onProgress(XesCloudResult result, int percent) {
//
//            logger.i("video upload percent:" + percent);
//        }
//
//        @Override
//        public void onSuccess(XesCloudResult result) {
//            videoUrl = result.getHttpPath();
//            logger.i("video upload succes " + videoUrl);
//            XESToastUtils.showToast(mContext, "视频上传成功");
////            uploadSuccess();
//
//            ShareDataManager.getInstance().put(
//                    ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
//                    1,
//                    ShareDataManager.SHAREDATA_NOT_CLEAR,
//                    false);
//            if (Looper.getMainLooper() == Looper.myLooper()) {
//                uploadSuccess();
//            } else {
//                latch.countDown();
//            }
//        }
//
//        @Override
//        public void onError(XesCloudResult result) {
////            videoUrl = "";
////            uploadSuccess();
//            logger.i("video upload fail");
//            //重试uploadVideoNum次
//            if (uploadVideoNum.get() > 0) {
//                uploadVideoNum.getAndDecrement();
//                uploadVideo();
//            }
//        }
//    };
//}
