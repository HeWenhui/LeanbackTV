package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.support.annotation.NonNull;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by linyuqiang on 2018/6/26.
 * 直播线程池
 */
public class LiveThreadPoolExecutor {
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
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
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r, "Live-Pool-" + r) {
                    @Override
                    public synchronized void start() {
                        logger.d("newThread:start:id=" + getId());
                        super.start();
                    }
                };
                logger.d("newThread:r=" + r);
                return thread;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.d("rejectedExecution:r=" + r);
            }
        });
        pingPool.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable command) {
        if (pingPool == null) {
            logger.d("execute:r=" + command);
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
