package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;
import java.io.IOException;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.UNITY3D_EVALUATE;

public class AudioRespository {

//    private String path;

    File cacheFile;

    File audioEvaluateFile;
    private EvaluationAudioPlayerDataManager audioManager;
    private Unity3DPlayManager unity3DPlayManager;

    /** unity3D模型是否存在本地 */
    public boolean isUnity3DExist() {
        File unity3D = new File(cacheFile, UNITY3D_EVALUATE);
        return unity3D.exists();
    }

    public AudioRespository(Context context, String path) {
//        getVideoPath(context, path);
        cacheFile = LiveCacheFile.geCacheFile(context.getApplicationContext(), path);
        if (cacheFile != null && !cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    /** 语音测评模型评价是否存在本地 */
    public boolean isAudioJudgeExist(String path) {
        if (audioEvaluateFile == null) {
            audioEvaluateFile = new File(cacheFile, path);
        }
        if (!audioEvaluateFile.exists()) {
            try {
                audioEvaluateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
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

//    public String getVideoPath(final Context context, String path) {
//        if (cacheFile != null && cacheFile.exists()) {
//
//        }
//        Observable.
//                just(path).
//                subscribeOn(Schedulers.io()).
//                map(new Function<String, File>() {
//                    @Override
//                    public File apply(String s) throws Exception {
//                        return cacheFile = LiveCacheFile.geCacheFile(context, s);
//                    }
//                }).
//                subscribe(new Consumer<File>() {
//                    @Override
//                    public void accept(File file) throws Exception {
//                        if (cacheFile != null && !cacheFile.exists()) {
//                            cacheFile.mkdirs();
//                        }
//                    }
//                });
//        cacheFile = LiveCacheFile.geCacheFile(context, path);

//        videoPath =;
//    }
}
