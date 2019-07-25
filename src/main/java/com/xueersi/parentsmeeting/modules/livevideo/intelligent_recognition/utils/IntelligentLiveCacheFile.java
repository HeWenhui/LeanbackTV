package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.CACHE_FILE;

class IntelligentLiveCacheFile {
//    private volatile static IntelligentLiveCacheFile instance;

    private IntelligentLiveCacheFile() {
    }

    static File file;

    public static File getInstanceFile(Context context) {
        if (file == null) {
            synchronized (IntelligentLiveCacheFile.class) {
                if (file == null) {
                    file = LiveCacheFile.geCacheFile(context, CACHE_FILE);
                }
            }
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
