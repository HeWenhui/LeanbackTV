package com.xueersi.parentsmeeting.modules.livevideo.groupgame.cloud;

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

import java.io.File;

public class GroupGameUpload {
    private String TAG = "GroupGameUpload";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Context context;
    private String live_id;
    private String testId;

    public GroupGameUpload(Context context, String live_id, String testId) {
        this.context = context;
        this.live_id = live_id;
        this.testId = testId;
        logger.d("GroupGameUpload:live_id=" + live_id + ",testId=" + testId);
    }

    /**
     * 把语音文件上传阿里云
     */
    public void uploadWonderMoment(final File saveFile, final String speech, final int errorcode) {
        logger.d("uploadWonderMoment:saveFile=" + saveFile + ",length=" + saveFile.length());
        if (saveFile.length() == 0) {
            return;
        }
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(ContextManager.getContext());
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(saveFile.getPath());
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.GROUP_INTERACTIVE);
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
                    stableLogHashMap.put("liveid", live_id);
                    stableLogHashMap.put("testId", testId);
                    stableLogHashMap.put("speech", speech);
                    stableLogHashMap.put("errorcode", "" + errorcode);
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("httppath", httpPath);
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_GAME_VOICE, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
                try {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploaderror");
                    stableLogHashMap.put("liveid", live_id);
                    stableLogHashMap.put("testId", testId);
                    stableLogHashMap.put("speech", speech);
                    stableLogHashMap.put("errorcode", "" + errorcode);
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("errorCode", "" + result.getErrorCode());
                    stableLogHashMap.put("errorMsg", "" + result.getErrorMsg());
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_GAME_VOICE, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }

    /**
     * 把语音文件上传阿里云
     */
    public void uploadWonderMoment(final File saveFile, final String speech, final String scores, final int errorcode) {
        logger.d("uploadWonderMoment:saveFile=" + saveFile + ",length=" + saveFile.length());
        if (saveFile.length() == 0) {
            return;
        }
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(ContextManager.getContext());
        CloudUploadEntity uploadEntity = new CloudUploadEntity();
        uploadEntity.setFilePath(saveFile.getPath());
        uploadEntity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadEntity.setCloudPath(CloudDir.GROUP_INTERACTIVE);
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
                    stableLogHashMap.put("liveid", live_id);
                    stableLogHashMap.put("testId", testId);
                    stableLogHashMap.put("speech", speech);
                    stableLogHashMap.put("scores", scores);
                    stableLogHashMap.put("errorcode", "" + errorcode);
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("httppath", httpPath);
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_GAME_VOICE, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
                try {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploaderror");
                    stableLogHashMap.put("liveid", live_id);
                    stableLogHashMap.put("testId", testId);
                    stableLogHashMap.put("speech", speech);
                    stableLogHashMap.put("errorcode", "" + errorcode);
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("errorCode", "" + result.getErrorCode());
                    stableLogHashMap.put("errorMsg", "" + result.getErrorMsg());
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_GAME_VOICE, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }
}
