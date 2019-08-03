package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.audio;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentLocalFileManager;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ContentAudioManager {

    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
//    private Map<String, String> map;

    private File liveCacheFile;

    private static volatile ContentAudioManager instance;
    /** 本地图片url */
    private String localImgUrl;

    private HashMap<String, String> map;

    private String sentence;

    public static ContentAudioManager init(Context context, String liveId, String resourseId) {
        if (instance == null) {
            synchronized (ContentAudioManager.class) {
                if (instance == null) {
                    instance = new ContentAudioManager(context, liveId, resourseId);
                }
            }
        }
        return instance;
    }

    private ContentAudioManager(Context context, String liveId, String resourseId) {
        try {
            IntelligentLocalFileManager intelligentLocalFileManager = IntelligentLocalFileManager.getInstance(context);
            liveCacheFile = intelligentLocalFileManager.getContentAudioFile(context, liveId, resourseId);
            initAudioPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSentence() {
        return sentence;
    }

    public HashMap<String, String> getAudioMap() {
        return map;
    }

    private void initAudioPath() {
        if (map == null) {
            map = new HashMap<>();
        }
        try {
            for (File itemFile : liveCacheFile.listFiles()) {
                logger.i("itemFile Name:" + itemFile);
                for (File audioFile : itemFile.listFiles()) {
                    String fileName = audioFile.getName();
                    if (fileName.endsWith(".mp3")) {
                        if (hasChar(fileName)) {
                            map.put(fileName.toLowerCase(), audioFile.getPath());
                        } else {
                            sentence = audioFile.getPath();
                        }
                    } else {
                        localImgUrl = fileName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocalImgUrl() {
        return localImgUrl;
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
    public String getAudioContentUrlFromLocal(String wordName, boolean isWord) {
        if ((isWord && TextUtils.isEmpty(wordName)) ||
                liveCacheFile == null || !liveCacheFile.exists()) {
            logger.i("getAudioContentUrlFromLocal:liveCacheFile " + liveCacheFile + " not exist");
            return "";
        }
        try {
            for (File itemFile : liveCacheFile.listFiles()) {
                logger.i("getAudioContentUrlFromLocal itemFile Name:" + itemFile);
                for (File audioFile : itemFile.listFiles()) {
                    String fileName = audioFile.getName();
                    if (!fileName.endsWith(".mp3")) {
                        continue;
                    }
                    if (isWord) {
                        if (TextUtils.isEmpty(fileName) || fileName.length() < 4) {
                            continue;
                        }
                        logger.i("getAudioContentUrlFromLocal:word:" + fileName);
                        if (wordName.toLowerCase().equals(fileName.substring(0, fileName.length() - 4).toLowerCase())) {
                            return audioFile.getPath();
                        }
                    } else {
                        logger.i("getAudioContentUrlFromLocal:sentence:" + fileName);
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
        logger.i("getAudioContentUrlFromLocal:" + liveCacheFile + " null");
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
        for (int ii = 0; ii < fileName.length(); ii++) {
            char charA = fileName.charAt(ii);
            if (charA == '.') {
                break;
            }
            if ((charA >= 'a' && charA <= 'z') || (charA >= 'A' && charA <= 'Z')) {
                return true;
            }
        }
        return false;
    }
}
