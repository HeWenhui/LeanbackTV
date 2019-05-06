package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by linyuqiang on 2018/8/17.
 * 直播获得缓存目录
 */
public class LiveCacheFile {

    public static File geCacheFile(Context context, String cache) {
        return com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile.geCacheFile(context, cache);
    }

    public static File geFileDir(Context context, String cache) {
        File alldir;
        File cacheDir = context.getExternalFilesDir("live");
        if (cacheDir != null) {
            alldir = new File(cacheDir, cache);
        } else {
            alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live/" + cache);
        }
        if (!alldir.exists()) {
            alldir.mkdirs();
        }
        return alldir;
    }
}
