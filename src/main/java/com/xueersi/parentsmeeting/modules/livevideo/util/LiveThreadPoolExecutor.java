package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.support.annotation.NonNull;

import com.xueersi.xesalib.utils.log.Loger;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lyqai on 2018/6/26.
 * 直播线程池
 */
public class LiveThreadPoolExecutor {
    static String TAG = "LiveThreadPoolExecutor";
    private static LiveThreadPoolExecutor liveThreadPoolExecutor;

    public static LiveThreadPoolExecutor getInstance() {
        if (liveThreadPoolExecutor == null) {
            liveThreadPoolExecutor = new LiveThreadPoolExecutor();
        }
        return liveThreadPoolExecutor;
    }

    /** 和服务器的ping，线程池 */
    private ThreadPoolExecutor pingPool;

    private LiveThreadPoolExecutor() {
        pingPool = new ThreadPoolExecutor(3, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, "" + r);
                Loger.d(TAG, "newThread:r=" + r);
                return thread;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        });
    }

    public void execute(Runnable command) {
        Loger.d(TAG, "execute:r=" + command + "," + (pingPool == null));
        if (pingPool == null) {
            return;
        }
        pingPool.execute(command);
    }

    public static void destory() {
        LiveThreadPoolExecutor liveThreadPoolExecutor2 = liveThreadPoolExecutor;
        if (liveThreadPoolExecutor2 != null) {
            liveThreadPoolExecutor = null;
            ThreadPoolExecutor pingPool = liveThreadPoolExecutor2.pingPool;
            liveThreadPoolExecutor2.pingPool = null;
            pingPool.shutdown();
        }
    }
}