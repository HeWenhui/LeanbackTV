package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeleteUtils {

    static String TAG = "DeleteUtils";
    static Logger logger = LiveLoggerFactory.getLogger(TAG);
    static ThreadPoolExecutor executos = new ThreadPoolExecutor(1, 1,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 循环删除子文件夹
     *
     * @param file
     */
    private static void deleteFor(final File file) {
        synchronized (DeleteUtils.class) {
            if (file == null || file.listFiles() == null) {
                return;
            }
            File[] files = file.listFiles();
            if (files == null) return;
            for (File itemFile : files) {
                if (!itemFile.isDirectory()) {
                    itemFile.delete();
                } else {
                    deleteFor(itemFile);
                    itemFile.delete();
                }
            }
        }
    }

    /**
     * 删除旧的Dir,因为又遍历文件操作，所以强制在子线程调用
     */
    public static void deleteOldDirAsync(final File file, final DeleteCatagry deleteCatagry) {
        synchronized (DeleteUtils.class) {
            executos.execute(new Runnable() {
                @Override
                public void run() {
                    logger.i("start delete file");
                    if (file == null || file.listFiles() == null || deleteCatagry != null) {
                        return;
                    }
                    //buglys上面有报Attempt to get length of null array,加上try,catch
                    try {
                        File[] files = file.listFiles();
                        if (files == null) return;
                        for (File itemFile : files) {
                            //文件夹是日期格式并且不是今天才删除
                            if (deleteCatagry.deleteDirAndFile(itemFile)) {
                                if (!itemFile.isDirectory()) {
                                    itemFile.delete();
                                } else {
                                    deleteFor(itemFile);
                                    itemFile.delete();
                                }
                            }
                            if (deleteCatagry.deleteSpecel(itemFile)) {
                                itemFile.delete();
                            }
                        }
                        logger.i("delete file success");
                        StableLogHashMap hashMap = new StableLogHashMap();
                        hashMap.put("logtype", " deleteCourseware");
                        hashMap.put("dir", file.getPath());
                        hashMap.put("sno", "5");
                        hashMap.put("status", "true");
                        hashMap.put("ip", IpAddressUtil.USER_IP);
                        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
                    } catch (Exception e) {
                        logger.e(e);
                        LiveCrashReport.postCatchedException(TAG, e);
                    }
                }
            });
        }
    }

    public interface DeleteCatagry {
        boolean deleteDirAndFile(File itemFile);

        boolean deleteSpecel(File itemFile);
    }

    public static class DefaultDeleteCatagery implements DeleteCatagry {

        @Override
        public boolean deleteDirAndFile(File itemFile) {
            return false;
        }

        @Override
        public boolean deleteSpecel(File itemFile) {
            return false;
        }
    }
}
