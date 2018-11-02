package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

public class LiveLoggerFactory {
    public static Logger getLogger(String tag) {
        Logger logger = LoggerFactory.getLogger(tag);
        logger.setLogMethod(false);
        return logger;
    }

    public static Logger getLogger(Object o) {
        Logger logger = LoggerFactory.getLogger(o.getClass().getSimpleName());
        logger.setLogMethod(false);
        return logger;
    }

    public static Logger getLogger(Class c) {
        Logger logger = LoggerFactory.getLogger(c.getSimpleName());
        logger.setLogMethod(false);
        return logger;
    }
}
