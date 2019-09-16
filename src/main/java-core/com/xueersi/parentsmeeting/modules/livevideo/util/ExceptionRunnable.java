package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.xueersi.lib.framework.utils.ThreadMap;

public class ExceptionRunnable implements Runnable {
    Exception exception;

    public ExceptionRunnable() {
        this.exception = new Exception();
        ThreadMap.getInstance().addKey("postexception", exception);
    }

    public ExceptionRunnable(Exception exception) {
        this.exception = exception;
        ThreadMap.getInstance().addKey("postexception", exception);
    }

    @Override
    public void run() {

    }
}
