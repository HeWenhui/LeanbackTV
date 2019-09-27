package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class InfoUtils {
    static String TAG = "InfoUtils";
    static Logger logger = LoggerFactory.getLogger(TAG);

    public static List mergeNbList(List<CoursewareInfoEntity> coursewareInfoEntities) {
        List<CoursewareInfoEntity.NbCoursewareInfo> ansList = new LinkedList<>();
        for (CoursewareInfoEntity coursewareInfoEntity : coursewareInfoEntities) {
            ansList = appendList(ansList, coursewareInfoEntity.getAddExperiments());
            ansList = appendList(ansList, coursewareInfoEntity.getFreeExperiments());
        }
        return ansList;
    }

    public static <T> List<T> appendList(List<T> totalList, List<T> list) {
        if (totalList != null && list != null) {
            for (T item : list) {
                if (!totalList.contains(item)) {
                    totalList.add(item);
                }
            }
        }
        return totalList;
    }

    public static File getNbFilePath(Context mContext) {
        File cacheDir = LiveCacheFile.geCacheFile(mContext,
                NbCourseWareConfig.NB_RESOURSE_CACHE_DIR);
        File todayCacheDir = new File(cacheDir, getToday());
        if (!todayCacheDir.exists()) {
            todayCacheDir.mkdirs();
        }
        return todayCacheDir;
    }

    public static String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * 同步删除某个文件或者文件夹
     *
     * @param fileName
     * @return
     */
    @WorkerThread
    public static synchronized boolean deleteDirSync(String fileName) {
        if (fileName != null) {
            return deleteDirSync(new File(fileName));
        } else {
            return false;
        }
    }

    /**
     * 同步删除某个文件或者文件夹
     *
     * @param file
     * @return
     */
    @WorkerThread
    public static synchronized boolean deleteDirSync(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                if (file.listFiles() != null) {
                    for (File iFile : file.listFiles()) {
                        deleteDirSync(iFile);
                    }
                    return file.delete();
                }
            } else {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 是否是课件的文件夹(课件文件夹由日期构成)
     *
     * @return
     */
    private static boolean isCoursewareDir(String fileName) {
        try {
            Integer.parseInt(fileName);
            return true;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }

    }

    /**
     * 删除不是今天的文件夹
     *
     * @param filePath
     * @return
     */
    public static synchronized boolean deleteNotTodayOldDirSync(String filePath) {
//        logger.i("start delete file");
        File file = new File(filePath);
        //buglys上面有报Attempt to get length of null array,加上try,catch
        try {
            if (file == null) {
                return false;
            }
            File[] files = file.listFiles();

            if (files == null) {
                return file.delete();
            }
            for (File itemFile : files) {
                //文件夹是日期格式并且不是今天才删除
                if (!itemFile.getName().equals(getToday()) &&
                        isCoursewareDir(itemFile.getName())) {
                    if (!itemFile.isDirectory()) {
                        itemFile.delete();
                    } else {
                        deleteDirSync(itemFile);
                    }
                }
            }
//            logger.i("delete file success");
            StableLogHashMap hashMap = new StableLogHashMap();
            hashMap.put("logtype", " deleteCourseware");
            hashMap.put("dir", file.getAbsolutePath());
            hashMap.put("sno", "5");
            hashMap.put("status", "true");
            hashMap.put("ip", IpAddressUtil.USER_IP);
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, LogConfig.PRE_LOAD_START, hashMap.getData());
        } catch (Exception e) {
            logger.e(e);
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }
        return true;
    }

    public static boolean eqaulsMd5(String md5, File save) {
        boolean equals = false;
        if (fileIsExists(save.getAbsolutePath())) {
            String filemd5 = FileUtils.getFileMD5ToString(save);
            equals = md5.equalsIgnoreCase(filemd5);
//            if (!equals) {
//                InfoUtils.deleteDirSync(save);
//            }
        }
        return equals;
    }


    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }

        return true;
    }


}
