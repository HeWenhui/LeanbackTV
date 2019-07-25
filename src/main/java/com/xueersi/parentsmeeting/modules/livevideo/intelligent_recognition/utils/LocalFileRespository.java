package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import java.io.File;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_FILE_DIR;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY3D_DIR;

public class LocalFileRespository {

    volatile static LocalFileRespository instance;

    public static LocalFileRespository getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalFileRespository.class) {
                if (instance == null) {
                    instance = new LocalFileRespository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    File cacheFile;

    File audioEvaluateFile;

    File unity3DEvaluateFile;

    /** unity3D模型是否存在本地 */
//    public boolean isUnity3DExist() {
//        File unity3D = new File(cacheFile, UNITY3D_DIR);
//        return unity3D.exists();
//    }
    private LocalFileRespository(Context context) {
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
        return audioEvaluateFile;
    }

    public File getUnity3DEvaluateFile() {

        if (unity3DEvaluateFile == null) {
            String path = UNITY3D_DIR;
            unity3DEvaluateFile = new File(cacheFile, path);
        }
        if (!unity3DEvaluateFile.exists()) {
            unity3DEvaluateFile.mkdirs();
        }
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
}
