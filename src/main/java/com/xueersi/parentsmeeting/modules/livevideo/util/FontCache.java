package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/4/29.
 * 直播缓存一些字体
 */
public class FontCache {

    /**
     * 缓存字体，但是目前没有释放的方法
     */
    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(Context context, String name) {
        Typeface tf = fontCache.get(name);
        if (tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

}
