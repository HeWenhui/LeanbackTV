package com.xueersi.parentsmeeting.modules.livevideo.core;

/**
 * Created by linyuqiang on 2018/8/22.
 * 直播日志
 */
public interface LiveOnLineLogs {
    void getOnloadLogs(String TAG, final String str);

    String getPrefix();
}
