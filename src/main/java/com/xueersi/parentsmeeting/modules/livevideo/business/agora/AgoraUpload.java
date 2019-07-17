package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

import android.content.Context;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
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

public class AgoraUpload {
    private String TAG = "AgoraUpload";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Context context;

    public AgoraUpload(Context context) {
        this.context = context;
    }

    public void upload(final String patch, final String liveId) {
        if (patch == null) {
            logger.d("uploaderror");
            return;
        }
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(ContextManager.getContext());
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(patch);
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
                    stableLogHashMap.put("savefile", "" + patch);
                    stableLogHashMap.put("liveId", "" + liveId);
                    stableLogHashMap.put("httppath", httpPath);
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_AGORA_UPLOAD, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
                try {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploaderror");
                    stableLogHashMap.put("savefile", "" + patch);
                    stableLogHashMap.put("liveId", "" + liveId);
                    stableLogHashMap.put("errorCode", "" + result.getErrorCode());
                    stableLogHashMap.put("errorMsg", "" + result.getErrorMsg());
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_AGORA_UPLOAD, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }
}
