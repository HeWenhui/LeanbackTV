package com.xueersi.parentsmeeting.modules.livevideo.liveLog;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

/**
 * @ClassName LiveMonitorDebug
 * @Description 直播监控日志的调试类
 * @Author lizheng
 * @Date 2019-11-01 17:09
 * @Version 1.0
 */
public class LiveMonitorDebug {

    public static void dLog(String log) {
        Logger liveMonitorDebug = LoggerFactory.getLogger("LiveMonitorDebug");
        liveMonitorDebug.d(log);
    }
}
