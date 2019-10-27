package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.content.Context;

import com.xueersi.common.broswer.XrsX5Broswer;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class LiveWebLog {
    static String TAG = "LiveWebLog";
    static Logger logger = LoggerFactory.getLogger(TAG);

    public static void init(final Context context, final String liveid) {
        LiveThreadPoolExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                XrsX5Broswer.XrsTbsLogClient tbsLogClient = XrsX5Broswer.getTbsLogClient();
                if (tbsLogClient != null) {
                    File dir = LiveCacheFile.geCacheFile(ContextManager.getContext(), "tbslog");
                    File uploadfile = new File(dir, "uploald_" + System.currentTimeMillis() + ".txt");
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            try {
                                File file = files[i];
                                if (!file.getName().startsWith("uploald")) {
                                    FileOutputStream fileOutputStream = new FileOutputStream(uploadfile, true);
                                    List<String> stringList = FileUtils.readFile2List(file, "utf-8");
                                    if (stringList != null) {
                                        fileOutputStream.write((file.getName() + "---------------\n").getBytes());
                                        for (int j = 0; j < stringList.size(); j++) {
                                            fileOutputStream.write((stringList.get(j) + "\n").getBytes());
                                        }
                                    }
                                    file.delete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    files = dir.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            try {
                                File file = files[i];
                                if (file.getName().startsWith("uploald")) {
                                    uploadCreashFile(context, file);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int index = 0;
                    File file = new File(dir, "tbslog_" + liveid + "_" + index + ".txt");
                    while (file.exists()) {
                        index++;
                        file = new File(dir, "tbslog_" + liveid + "_" + index + ".txt");
                    }
                    tbsLogClient.setLogFile(file);
                }
            }
        });
    }

    /**
     * 直播崩溃上传
     *
     * @param saveFile
     */
    public static void uploadCreashFile(final Context context, final File saveFile) {
        logger.d("uploadCreashFile:saveFile=" + saveFile + ",length=" + saveFile.length());
        if (saveFile.length() == 0) {
            return;
        }
        XesCloudUploadBusiness xesCloudUploadBusiness = new XesCloudUploadBusiness(ContextManager.getContext());
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
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_X5_LOG, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                saveFile.delete();
            }

            @Override
            public void onError(XesCloudResult result) {
                logger.d("asyncUpload:onError=" + result.getErrorCode() + "," + result.getErrorMsg());
                try {
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("uploaderror");
                    stableLogHashMap.put("savefile", "" + saveFile);
                    stableLogHashMap.put("errorCode", "" + result.getErrorCode());
                    stableLogHashMap.put("errorMsg", "" + result.getErrorMsg());
                    UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_X5_LOG, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
    }
}
