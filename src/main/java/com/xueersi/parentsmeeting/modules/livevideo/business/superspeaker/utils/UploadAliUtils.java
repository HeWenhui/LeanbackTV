package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import android.content.Context;

import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.io.File;
import java.text.DecimalFormat;

public class UploadAliUtils {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private Context mContext;

    XesCloudUploadBusiness business;
    File file;

    public UploadAliUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 上传至阿里云
     */
    public void uploadFile(String path, CloudDir cloudPath, int type, XesStsUploadListener xesStsUploadListener) {
        business = new XesCloudUploadBusiness(mContext);
//        final String path = LiveVideoConfig.SUPER_SPEAKER_VIDEO_PATH;

        CloudUploadEntity entity = new CloudUploadEntity();
//        String id = UUID.randomUUID().toString();
//        entity.setFileId(id);
        entity.setCloudPath(cloudPath);
        entity.setFilePath(path);
        entity.setType(type);
        file = new File(path);
        if (!file.exists()) {
            XESToastUtils.showToast(mContext, "录制失败");
            return;
        }
        business.asyncUpload(entity, xesStsUploadListener
//                new XesStsUploadListener() {
//            @Override
//            public void onProgress(XesCloudResult result, int percent) {
//                if (percent % 10 == 0) {
//                    XESToastUtils.showToast(mContext, "上传进度：" + percent + "    " + "视频总大小:" + getDataSize(file.length()));
//                }
//            }
//
//            @Override
//            public void onSuccess(XesCloudResult result) {
//                XESToastUtils.showToast(mContext, "complete");
//                logger.i("uploadFilePath : " + result.getUploadFilePath());
////                mNotificationManager.cancel(1099);
//
//            }
//
//            @Override
//            public void onError(XesCloudResult result) {
//                XESToastUtils.showToast(mContext, JSON.toJSONString(result));
//
//            }
//    }
        );

    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 > 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

}
