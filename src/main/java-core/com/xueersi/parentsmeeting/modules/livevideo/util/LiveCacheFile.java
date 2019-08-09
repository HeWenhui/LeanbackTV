package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.os.Environment;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;

/**
 * Created by linyuqiang on 2018/8/17.
 * 直播获得缓存目录
 */
public class LiveCacheFile {

    public static File geCacheFile(Context context, String cache) {
        File alldir;
        String msg = "";
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            alldir = new File(cacheDir, cache);
            //外置存储失败。直接存到内置
            if (!alldir.exists()) {
                boolean mkdirs = alldir.mkdirs();
                if (!mkdirs) {
                    msg = "External=" + alldir;
                    alldir = new File(context.getCacheDir(), cache);
                }
            }
        } else {
            String status = Environment.getExternalStorageState();
            msg = "status=" + status;
            if (Environment.MEDIA_MOUNTED.equals(status)) {
                alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/" + cache);
            } else {
                alldir = new File(context.getCacheDir(), cache);
            }
        }
        if (!alldir.exists()) {
            boolean mkdirs = alldir.mkdirs();
            StableLogHashMap logHashMap = new StableLogHashMap("geCacheFile");
            logHashMap.put("alldir", "" + alldir);
            logHashMap.put("mkdirs", "" + mkdirs);
            logHashMap.put("msg", "" + msg);
            UmsAgentManager.umsAgentDebug(context, LogConfig.LIVE_CACHE_FILE, logHashMap.getData());
        }
        return alldir;
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
