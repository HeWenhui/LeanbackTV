package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyqai on 2018/6/23.
 */

public class ProxUtil {
    private static ProxUtil proxUtil;
    private HashMap<Context, Prox> proxHashMap = new HashMap<>();

    public static ProxUtil getProxUtil() {
        if (proxUtil == null) {
            proxUtil = new ProxUtil();
        }
        return proxUtil;
    }

    //**把类和对象按context保存,以实现以后多个直播通知存在的情况*/
    public <T> void put(Context context, Class<T> clazz, T obj) {
        Prox prox = proxHashMap.get(context);
        if (prox == null) {
            prox = new Prox();
            proxHashMap.put(context, prox);
        }
        prox.put(clazz, obj);
    }

    public <T> T get(Context context, Class<T> clazz) {
        Prox prox = proxHashMap.get(context);
        if (prox == null) {
            return null;
        }
        return prox.get(clazz);
    }

    public void clear() {
        Set<Context> keys = proxHashMap.keySet();
        for (Context key : keys) {
            proxHashMap.get(key).clear();
        }
        proxHashMap.clear();
    }

    private class Prox {
        private HashMap<Class, Object> objectHashMap = new HashMap<>();

        private <T> void put(Class<T> clazz, Object obj) {
            objectHashMap.put(clazz, obj);
        }

        //**把类和对象保存*/
        private <T> T get(Class<T> clazz) {
            return (T) objectHashMap.get(clazz);
        }

        private void clear() {
            objectHashMap.clear();
        }
    }
}
