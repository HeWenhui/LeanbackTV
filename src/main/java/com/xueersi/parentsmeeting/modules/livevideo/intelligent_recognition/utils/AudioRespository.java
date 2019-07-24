package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import java.io.File;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_FILE_NAME;

public class AudioRespository {

//    private String path;

    File cacheFile;

    File audioEvaluateFile;

    /** unity3D模型是否存在本地 */
//    public boolean isUnity3DExist() {
//        File unity3D = new File(cacheFile, UNITY3D_EVALUATE);
//        return unity3D.exists();
//    }
    public AudioRespository(Context context, String path) {
//        getVideoPath(context, path);
//        cacheFile = LiveCacheFile.geCacheFile(context.getApplicationContext(), path);
//        if (cacheFile != null && !cacheFile.exists()) {
//            cacheFile.mkdirs();
//        }
        cacheFile = IntelligentLiveCacheFile.getInstanceFile(context.getApplicationContext());
        audioEvaluateFile = new File(cacheFile, AUDIO_EVALUATE_FILE_NAME);
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
//        if (!audioEvaluateFile.exists()) {
//            try {
//                audioEvaluateFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return true;
    }

    public File getAudioEvaluateFile(String path) {
        if (audioEvaluateFile == null) {
            audioEvaluateFile = new File(cacheFile, path);
        }
        return audioEvaluateFile;
    }

    /** 语音测评文本是否预加载完成 */
    public boolean isAudioContentExit(String path) {
        File contentFile = new File(path);
        return contentFile.exists();
    }

    public File getCacheFile() {
        return cacheFile;
    }
}
