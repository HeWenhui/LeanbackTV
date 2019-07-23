package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;
import android.os.Environment;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.RxFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.AUDIO_EVALUATE_FILE_NAME;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.CACHE_FILE;

public class EvaluationAudioPlayerDataManager {

    private Queue<Integer> queue;

    {
        queue = new LinkedList();
        queue.add(IntelligentConstants.PERFECT);
        queue.add(IntelligentConstants.GOOD);
        queue.add(IntelligentConstants.FEED_BACK_SENTENCE_1_0);
        queue.add(IntelligentConstants.FEED_BACK_WORD_1);
        queue.add(IntelligentConstants.FEED_BACK_SENTENCE_1_1);
        queue.add(IntelligentConstants.FEED_BACK_SENTENCE_2_0);
        queue.add(IntelligentConstants.FEED_BACK_SENTENCE_2_1);
        queue.add(IntelligentConstants.FEED_BACK_WORD_2_0);
        queue.add(IntelligentConstants.FEED_BACK_WORD_2_1);
        queue.add(IntelligentConstants.FEED_BACK_WORD_3_0);
        queue.add(IntelligentConstants.FEED_BACK_WORD_3_1);
        queue.add(IntelligentConstants.END_GOOD_BYE_1);
        queue.add(IntelligentConstants.END_GOOD_BYE_2);
        queue.add(IntelligentConstants.END_GOOD_BYE_3);
        queue.add(IntelligentConstants.END_GOOD_BYE_4);
//        queue.add(IntelligentConstants.GOOD);
//        queue.add(IntelligentConstants.PERFECT);
//        queue.add(IntelligentConstants.GOOD);
    }

    //    private List<Integer> list = Arrays.
    private static volatile EvaluationAudioPlayerDataManager instance;
    private Context context;

    private EvaluationAudioPlayerDataManager(Context context) {
        this.context = context;
    }

    public static EvaluationAudioPlayerDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (EvaluationAudioPlayerDataManager.class) {
                if (instance == null) {
                    instance = new EvaluationAudioPlayerDataManager(context);
                    instance.init();
                }
            }
        }
        return instance;
    }

    private static class AudioData<T> {
        private int nowPlayAudioPos;
        private List<T> audioUrl;

//        public int getNowPlayAudioPos() {
//            return nowPlayAudioPos;
//        }

//        public void setNowPlayAudioPos(int nowPlayAudioPos) {
//            if (audioUrl != null && nowPlayAudioPos >= audioUrl.size()) {
//                nowPlayAudioPos = 0;
//            }
//            this.nowPlayAudioPos = nowPlayAudioPos;
//        }

//        public List<T> getJudgeAudioUrl() {
//            return audioUrl;
//        }

        public T getAndIncreateAudioUrl() {
            if (audioUrl == null || audioUrl.size() == 0) {
                return null;
            }
            T url = audioUrl.get(0);
            if (nowPlayAudioPos < audioUrl.size()) {
                url = audioUrl.get(nowPlayAudioPos);
            }
            ++nowPlayAudioPos;
            if (nowPlayAudioPos > audioUrl.size()) {
                nowPlayAudioPos = 0;
            }
            return url;
        }

        public void setAudioUrl(List<T> audioUrl) {
            this.audioUrl = audioUrl;
            nowPlayAudioPos = 0;
        }
    }

    private Map<Integer, AudioData<String>> map;

    private void init() {
//        List<Integer> iLst = Arrays.asList(IntelligentConstants.PERFECT, REPEAT_WORD);
//        for(Integer i:iLst){
//        initPerfect();
//        initGood();
//        initRepeat_word();
//        initRepeat_sentence();
        initAudioPath();
    }

    private void initAudioPath() {
        AudioRespository audioRespository = new AudioRespository(context, CACHE_FILE);
        Observable.
                just(audioRespository.getAudioEvaluateFile(AUDIO_EVALUATE_FILE_NAME)).
                filter(RxFilter.filterFile()).
                subscribeOn(Schedulers.io())
                .flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) throws Exception {
                        return Observable.fromArray(file.listFiles());
                    }
                }).
                filter(RxFilter.filterFile()).
                subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        AudioData<String> audioData = new AudioData<>();
                        audioData.setAudioUrl(Arrays.asList(file.list()));
                        map.put(queue.poll(), audioData);
                    }
                });
    }

    private void initGood() {

    }

    private void initRepeat_word() {
        AudioData<String> audioData = new AudioData<>();
        List<String> queue = new ArrayList<>();
        queue.add(Environment.getExternalStorageDirectory() +
                File.separator + "parentsmeeting" + File.separator + "livevideo" +
                File.separator + "05_01_You_made_it_Just_try_to_repeat_one_more_time_Please_read_after_me.mp3");
        audioData.setAudioUrl(queue);
//        audioData.setNowPlayAudioPos(0);
//        map.put(IntelligentConstants., audioData);
    }


    public String getJudgeAudioUrl(Integer integer) {
        if (map != null && map.get(integer) != null) {
            return map.get(integer).getAndIncreateAudioUrl();
        } else {
            return null;
        }
    }

}
