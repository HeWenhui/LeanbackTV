package ren.yale.android.cachewebviewlib;

import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import ren.yale.android.cachewebviewlib.config.CacheConfig;

/**
 * Created by yale on 2017/9/15.
 */

 class CacheWebViewLog {
    private static final String TAG="CacheWebView";


    public static void d(String log){
        if (CacheConfig.getInstance().isDebug()){
            Loger.d(TAG,log);
        }
    }
}
