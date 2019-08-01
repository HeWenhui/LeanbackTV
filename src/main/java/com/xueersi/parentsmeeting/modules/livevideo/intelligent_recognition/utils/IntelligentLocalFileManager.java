package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_FILE_DIR;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.CACHE_FILE;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY3D_DIR;

public class IntelligentLocalFileManager {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    volatile static IntelligentLocalFileManager instance;

    public static IntelligentLocalFileManager getInstance(Context context) {
        if (instance == null) {
            synchronized (IntelligentLocalFileManager.class) {
                if (instance == null) {
                    instance = new IntelligentLocalFileManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    File cacheFile;

    File audioEvaluateFile;

    File unity3DEvaluateFile;

    File contentAudioFile;

    /** unity3D模型是否存在本地 */
//    public boolean isUnity3DExist() {
//        File unity3D = new File(cacheFile, UNITY3D_DIR);
//        return unity3D.exists();
//    }
    private IntelligentLocalFileManager(Context context) {
//        getVideoPath(context, path);
//        cacheFile = LiveCacheFile.geCacheFile(context.getApplicationContext(), path);
//        if (cacheFile != null && !cacheFile.exists()) {
//            cacheFile.mkdirs();
//        }
        cacheFile = IntelligentLiveCacheFile.getInstanceFile(context.getApplicationContext());

//        if (!liveCacheFile.exists()) {
//            try {
//                liveCacheFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /** 语音测评模型评价是否存在本地 */
    public boolean isAudioJudgeExist(String path) {
        if (audioEvaluateFile == null) {
            audioEvaluateFile = new File(cacheFile, path);
        }
        return audioEvaluateFile.exists();
    }

    public File getAudioEvaluateFile() {
//        if (TextUtils.isEmpty(path)) {

//        }
        if (audioEvaluateFile == null) {
            String path = AUDIO_EVALUATE_FILE_DIR;
            audioEvaluateFile = new File(cacheFile, path);
        }
        if (!audioEvaluateFile.exists()) {
            audioEvaluateFile.mkdirs();
        }
        logger.i("AudioEvaluateFile:" + audioEvaluateFile.getPath());
        return audioEvaluateFile;
    }

    public File getContentAudioFile(Context context, String liveId, String sourceId) {
        if (contentAudioFile == null) {
            File file = LiveCacheFile.geCacheFile(context, "webviewCache");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = new Date();
            final String today = dateFormat.format(date);
            File todayCacheDir = new File(file, today);
            File todayLiveCacheDir = new File(todayCacheDir, liveId);
            File mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
            contentAudioFile = new File(mMorecacheout, sourceId);
//        mMorecacheout = new File(mMorecacheout, sourceId);//文件名字默认时sourceId
//        if (!mMorecacheout.exists()) {
//            mMorecacheout.mkdirs();
//        }
        }
        return contentAudioFile;
    }

    public File getUnity3DEvaluateFile() {

        if (unity3DEvaluateFile == null) {
            String path = UNITY3D_DIR;
            unity3DEvaluateFile = new File(cacheFile, path);
        }
        if (!unity3DEvaluateFile.exists()) {
            unity3DEvaluateFile.mkdirs();
        }
        logger.i("unity3D" + unity3DEvaluateFile);
        return unity3DEvaluateFile;
    }

    /** 语音测评文本是否预加载完成 */
    public boolean isAudioContentExit(String path) {
        File contentFile = new File(path);
        return contentFile.exists();
    }

//    public File getCacheFile() {
//        return cacheFile;
//    }

    static class IntelligentLiveCacheFile {
        //    private volatile static IntelligentLiveCacheFile instance;

        private IntelligentLiveCacheFile() {
        }

        static File file;

        public static File getInstanceFile(Context context) {
            if (file == null) {
                synchronized (IntelligentLiveCacheFile.class) {
                    if (file == null) {
                        file = LiveCacheFile.geCacheFile(context, CACHE_FILE);
                    }
                }
            }
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }
}
