package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.audio;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.RxFilter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentLocalFileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class EvaluationAudioPlayerDataManager {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
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
//        queue.add(IntelligentConstants.END_GOOD_BYE_4);
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
//                    instance.init();
                }
            }
        }
        return instance;
    }

    private static class AudioData<T> {
        private int nowPlayAudioPos;
        private List<T> audioUrl;
//        private String audioListName;


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

    private Map<Integer, AudioData<String>> map = new ConcurrentHashMap<>();

    private void init() {
//        List<Integer> iLst = Arrays.asList(IntelligentConstants.PERFECT, REPEAT_WORD);
//        for(Integer i:iLst){
//        initPerfect();
//        initGood();
//        initRepeat_word();
//        initRepeat_sentence();
        initAudioPath();
    }

    public Observable<File> initAudioPath() {
        IntelligentLocalFileManager audioRespository = IntelligentLocalFileManager.getInstance(context);

        return Observable.
                just(new File(audioRespository.getAudioEvaluateFile(), "ieAudio")).
                filter(RxFilter.filterFile()).
                subscribeOn(Schedulers.io()).
                flatMap(new Function<File, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(File file) throws Exception {
                        return Observable.fromArray(file.listFiles());
                    }
                }).
                filter(RxFilter.filterFile()).
                doOnNext(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
//                        int pos = queue.poll();
//                        Integer.valueOf(file.getName());
//                        if (String.valueOf(pos).equals(file.getName())) {
//
//                        }
                        int fileNameToInt = 0;
                        try {
                            logger.i(" file Name:" + file.getName());
                            fileNameToInt = Integer.valueOf(file.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.e(e.getMessage());
                        }

                        AudioData<String> audioData = new AudioData<>();
                        List<String> itemFileNames = new ArrayList<>();
                        for (File itemFile : file.listFiles()) {
                            itemFileNames.add(itemFile.getPath());
                            logger.i("pos=" + fileNameToInt + " judge File url " + itemFile.getPath());
                        }
                        audioData.setAudioUrl(itemFileNames);
                        map.put(fileNameToInt, audioData);
                    }
                });
    }

    private boolean workFinish = false;

    public String getJudgeAudioUrl(Integer integer) {
        logger.i("JudgeAudioUrl-Integer:" + integer);
        if (map != null && map.get(integer) != null) {
            String url = map.get(integer).getAndIncreateAudioUrl();
            logger.i("JudgeAudioUrl:" + url);
            return url;
        } else {
            logger.i("JudgeAudioUrl:null" + " map " + (map != null ? " valid " : " null"));
            return null;
        }
    }

}
