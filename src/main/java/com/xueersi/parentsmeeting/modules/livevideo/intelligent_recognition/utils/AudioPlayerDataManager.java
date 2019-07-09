package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.REPEAT_WORD;

public class AudioPlayerDataManager<P, V> {

    private static volatile AudioPlayerDataManager instance;

    private AudioPlayerDataManager() {
    }

    public static AudioPlayerDataManager getInstance() {
        if (instance == null) {
            synchronized (AudioPlayerDataManager.class) {
                if (instance == null) {
                    instance = new AudioPlayerDataManager();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private static class AudioData<T> {
        private int nowPlayAudioPos;
        private List<T> audioUrl;

        public int getNowPlayAudioPos() {
            return nowPlayAudioPos;
        }

        public void setNowPlayAudioPos(int nowPlayAudioPos) {
            if (audioUrl != null && nowPlayAudioPos >= audioUrl.size()) {
                nowPlayAudioPos = 0;
            }
            this.nowPlayAudioPos = nowPlayAudioPos;
        }

//        public List<T> getAudioUrl() {
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
        }
    }

    private Map<Integer, AudioData<String>> map;

    private void init() {
//        List<Integer> iLst = Arrays.asList(IntelligentConstants.PERFECT, REPEAT_WORD);
//        for(Integer i:iLst){
        initPerfect();
        initGood();
        initRepeat_word();
        initRepeat_sentence();
    }

    private void initPerfect() {
        AudioData<String> audioData = new AudioData<>();
        List<String> list = new ArrayList<>();
        list.add(Environment.getExternalStorageDirectory() +
                File.separator + "parentsmeeting" + File.separator + "livevideo" +
                File.separator + "01_01_Well_done.mp3");
        audioData.setAudioUrl(list);
        audioData.setNowPlayAudioPos(0);
        map.put(REPEAT_WORD, audioData);
    }

    private void initGood() {

    }

    private void initRepeat_word() {
        AudioData<String> audioData = new AudioData<>();
        List<String> list = new ArrayList<>();
        list.add(Environment.getExternalStorageDirectory() +
                File.separator + "parentsmeeting" + File.separator + "livevideo" +
                File.separator + "05_01_You_made_it_Just_try_to_repeat_one_more_time_Please_read_after_me.mp3");
        audioData.setAudioUrl(list);
        audioData.setNowPlayAudioPos(0);
        map.put(REPEAT_WORD, audioData);
    }


    private void initRepeat_sentence() {

    }

    public String getAudioUrl(Integer integer) {
        return map.get(integer).getAndIncreateAudioUrl();
    }
}
