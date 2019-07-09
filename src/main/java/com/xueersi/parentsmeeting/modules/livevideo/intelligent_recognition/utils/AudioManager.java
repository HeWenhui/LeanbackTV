package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.util.ArrayMap;

public class AudioManager {

    private static volatile AudioManager instance;

    private AudioManager() {
    }

    public static AudioManager newInstance() {
        if (instance == null) {
            synchronized (AudioManager.class) {
                if (instance == null) {
                    instance = new AudioManager();
                }
            }
        }
        return instance;
    }

    private ArrayMap<String, String> map;
}
