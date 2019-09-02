package com.xueersi.parentsmeeting.modules.livevideo.lib;

/**
 *tcp 暂时的日志，为了在异常的时候被bugly上传
 */
public class LiveTcpLoggerFactory {
    public static Logger getLogger(String tag) {
        return new Logger(tag);
    }
}
