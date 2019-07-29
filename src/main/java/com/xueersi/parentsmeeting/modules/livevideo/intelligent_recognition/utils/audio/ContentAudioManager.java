package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.audio;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentLocalFileManager;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ContentAudioManager {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//    private Map<String, String> map;

    private File liveCacheFile;

    public ContentAudioManager(Context context, String liveId, String resourseId) {

        try {
            IntelligentLocalFileManager intelligentLocalFileManager = IntelligentLocalFileManager.getInstance(context);
            liveCacheFile = intelligentLocalFileManager.getContentAudioFile(context, liveId, resourseId);
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
        return Observable.fromArray(liveCacheFile.listFiles()).flatMap(new Function<File, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(File file) throws Exception {
                return Observable.fromArray(file.list());
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return TextUtils.isEmpty(s) || s.length() < 4;
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                //根据名字匹配
                String fileName = s.substring(0, s.length() - 4).toLowerCase();

                boolean isEquals = wordName.toLowerCase().equals(fileName);
                logger.i("fileName:" + s + " isEquals:" + isEquals);
                return isEquals;
            }
        }).subscribeOn(Schedulers.io()).take(1);
    }

    /**
     * 同{@link #getRxAudioContent(String)}
     * 音频文件是否存在,如果文件过多，可能存在for循环遍历耗时的情况，建议在子线程中使用该方法
     *
     * @param wordName 需要播放的音频文件的名字，注意大小写
     * @param isWord   是单词还是句子
     * @return
     */
    @WorkerThread
    public String getAudioContentUrl(String wordName, boolean isWord) {
        if ((isWord && TextUtils.isEmpty(wordName)) ||
                liveCacheFile == null || !liveCacheFile.exists()) {
            logger.i("getLocalSentenceUrl:" + liveCacheFile + " not exist");
            return "";
        }
        try {
            for (File itemFile : liveCacheFile.listFiles()) {
                logger.i("itemFile Name:" + itemFile);
                for (File audioFile : itemFile.listFiles()) {
                    String fileName = audioFile.getName();
                    if (!fileName.endsWith(".mp3")) {
                        continue;
                    }
                    if (isWord) {
                        if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                            continue;
                        }
                        logger.i("getLocalSentenceUrl:" + fileName);
                        if (wordName.toLowerCase().equals(fileName.substring(fileName.lastIndexOf(File.separator), fileName.length() - 4).toLowerCase())) {
                            return audioFile.getPath();
                        }
                    } else {
                        logger.i("getLocalSentenceUrl:" + fileName);
                        if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                            continue;
                        }
                        if (!hasChar(fileName)) {
                            return audioFile.getPath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.i("getLocalSentenceUrl:" + liveCacheFile + " null");
        return "";
    }

    private String getLocalSentenceUrl() {
        if (liveCacheFile == null || !liveCacheFile.exists()) {
            logger.i("getLocalSentenceUrl:" + liveCacheFile + " not exist");
            return "";
        }
        logger.i("liveCache.size = " + liveCacheFile.listFiles().length);

        for (File itemFile : liveCacheFile.listFiles()) {
            for (File audioFile : itemFile.listFiles()) {
                String fileName = audioFile.getName();
                logger.i("getLocalSentenceUrl:" + fileName);
                if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                    continue;
                }
                if (!hasChar(fileName)) {
                    return audioFile.getPath();
                }
            }
        }
        logger.i("getLocalSentenceUrl:" + liveCacheFile + " null");
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

    private boolean hasChar(String fileName) {
        if (fileName.endsWith(".mp3")) {
            for (int ii = 0; ii < fileName.length(); ii++) {
                char charA = fileName.charAt(ii);
                if (charA == '.') {
                    break;
                }
                if ((charA >= 'a' && charA <= 'z') || (charA >= 'A' && charA <= 'Z')) {
                    return true;
                }
            }
        }
        return false;
    }
}
