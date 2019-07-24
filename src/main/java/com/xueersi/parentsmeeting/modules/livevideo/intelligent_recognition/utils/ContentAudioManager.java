package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ContentAudioManager {

    private Map<String, String> map;

    private File liveCacheFile;

    public ContentAudioManager(Context context, String liveId, String resourseId) {
        try {
            liveCacheFile = new File(LiveCacheFile.geCacheFile(context, liveId), resourseId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 音频文件是否存在,如果文件过多，可能存在for循环遍历耗时的情况，建议在子线程中使用该方法
     *
     * @param wordName 需要播放的音频文件的名字，注意大小写
     * @return
     */
    public Observable<String> getRxAudioContent(final String wordName) {
        return Observable.fromArray(liveCacheFile.list()).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return TextUtils.isEmpty(s) || s.length() < 4;
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return wordName.toLowerCase().equals(s.substring(s.lastIndexOf(File.separator), s.length() - 4).toLowerCase());
            }
        }).subscribeOn(Schedulers.io()).take(1);


//        Observable.just(wordName).filter(new Predicate<String>() {
//            @Override
//            public boolean test(String s) throws Exception {
//                return TextUtils.isEmpty(s) || liveCacheFile == null || !liveCacheFile.exists();
//            }
//        });

    }

    /**
     * 同{@link #getRxAudioContent(String)}
     * 音频文件是否存在,如果文件过多，可能存在for循环遍历耗时的情况，建议在子线程中使用该方法
     *
     * @param wordName 需要播放的音频文件的名字，注意大小写
     * @return
     */
    @WorkerThread
    public String getAudioContentUrl(String wordName) {
        if (TextUtils.isEmpty(wordName) || liveCacheFile == null || !liveCacheFile.exists()) {
            return "";
        }
        try {
            for (String fileName : liveCacheFile.list()) {
                if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                    continue;
                }
                if (wordName.toLowerCase().equals(fileName.substring(fileName.lastIndexOf(File.separator), fileName.length() - 4).toLowerCase())) {
                    return fileName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLocalSentenceUrl() {
        if (liveCacheFile == null || !liveCacheFile.exists()) {
            return "";
        }
        for (String fileName : liveCacheFile.list()) {
            if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                continue;
            }
            if (hasDigit(fileName)) {
                return fileName;
            }
        }
        return "";
    }

    private boolean hasDigit(String fileName) {
        for (int ii = 0; ii < fileName.length(); ii++) {
            char charA = fileName.charAt(ii);
            if (charA >= '0' && charA <= '9') {
                return true;
            }
        }
        return false;
    }
}
