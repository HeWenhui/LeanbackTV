package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by linyuqiang on 2018/6/23.
 * 直播基于Context单例类
 */
public class ProxUtil {
    private String TAG = "ProxUtil";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
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
        if (context.getClass().getName().contains("CourseDetailActivity")) {
            logger.d( "put:context", new Exception());
        }
        logger.d( "put:context=" + context);
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

    public <T> T remove(Context context, Class<T> clazz) {
        Prox prox = proxHashMap.get(context);
        if (prox == null) {
            return null;
        }
        return prox.remove(clazz);
    }

    public void clear() {
        Set<Context> keys = proxHashMap.keySet();
        for (Context key : keys) {
            proxHashMap.get(key).clear();
        }
        proxHashMap.clear();
    }

    public void clear(Context context) {
        int size = -1;
        Prox prox = proxHashMap.remove(context);
        if (prox != null) {
            size = prox.clear();
        }
        logger.d( "clear:proxHashMap=" + proxHashMap.size() + ",clear=" + size);
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

        private <T> T remove(Class<T> clazz) {
            return (T) objectHashMap.remove(clazz);
        }

        private int clear() {
            int size = objectHashMap.size();
            objectHashMap.clear();
            return size;
        }
    }
}
