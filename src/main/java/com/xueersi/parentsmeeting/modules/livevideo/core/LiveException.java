package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.util.AndroidException;

/**
 * @Date on 2019/4/22 23:59
 * @Author linyuqiang
 * 直播异常
 */
public class LiveException extends AndroidException {

    public LiveException(Exception cause) {
        super(cause);
    }

    public LiveException(String name, Throwable cause) {
        super(name, cause);
    }
}
