package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;

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
    }

    public void setContext(Context context) {
        logToFile = new LogToFile(context, TAG);
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public final int get() {
        return atomicInteger.get();
    }

    boolean small = false;

    /**
     * Sets to the given value.
     *
     * @param newValue the new value
     */
    public final void set(int newValue, Exception e) {
        if (newValue <= 0) {
            if (logToFile != null) {
                logToFile.e("set:newValue=" + newValue, e);
            }
            newValue = 1;
            if (!small) {
                small = true;
                LiveCrashReport.postCatchedException(TAG, e);
            }
        }
        atomicInteger.set(newValue);
    }

    @Override
    public String toString() {
        return atomicInteger.toString();
    }
}
