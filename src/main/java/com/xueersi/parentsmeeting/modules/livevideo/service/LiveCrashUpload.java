package com.xueersi.parentsmeeting.modules.livevideo.service;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;

/**
 * Created by linyuqiang on 2017/5/15.
 * 直播崩溃上传
 */
public class LiveCrashUpload {
    private String TAG = "LiveCrashUpload";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Context context;

    public LiveCrashUpload(Context context) {
        this.context = context;
    }

    /**
     * 直播崩溃上传
     *
     * @param saveFile
     */
    public void uploadCreashFile(final File saveFile, final boolean haveSignal, final File uploadfile) {
        logger.d("uploadCreashFile:saveFile=" + saveFile + ",length=" + saveFile.length());
        if (saveFile.length() == 0) {
            return;
        }
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(BaseApplication.getContext());
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(saveFile.getPath());
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.CLOUD_TEST);
        xesCloudUploadBusiness.asyncUpload(uploadEntity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                try {
                    String httpPath = result.getHttpPath();
                    logger.d("asyncUpload:onSuccess=" + httpPath);
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploadsuc");
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("httppath", httpPath);
                    stableLogHashMap.put("havesignal", "" + haveSignal);
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_CRASH_UPLOAD, stableLogHashMap.getData());
                    saveFile.renameTo(uploadfile);
                } catch (Exception e) {
                    CrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
                try {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploaderror");
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("errorCode", "" + result.getErrorCode());
                    stableLogHashMap.put("errorMsg", "" + result.getErrorMsg());
                    stableLogHashMap.put("havesignal", "" + haveSignal);
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_CRASH_UPLOAD, stableLogHashMap.getData());
                } catch (Exception e) {
                    CrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }
}
