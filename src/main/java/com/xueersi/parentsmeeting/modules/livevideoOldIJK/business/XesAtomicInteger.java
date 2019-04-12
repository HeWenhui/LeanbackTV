package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 包裹AtomicInteger，找到赋值0的地方
 * Created by linyuqiang on 2017/2/8.
 */
public class XesAtomicInteger {
    String TAG = "XesAtomicInteger";
    final AtomicInteger atomicInteger;
    LogToFile logToFile;

    public XesAtomicInteger(int count) {
        atomicInteger = new AtomicInteger(count);
        logToFile = new LogToFile(TAG);
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final int get() {
        return atomicInteger.get();
    }

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(int newValue, Exception e) {
        if (newValue == 0) {
            logToFile.e("set:newValue=0", e);
        }
        atomicInteger.set(newValue);
    }

    @Override
    public String toString() {
        return atomicInteger.toString();
    }
}
