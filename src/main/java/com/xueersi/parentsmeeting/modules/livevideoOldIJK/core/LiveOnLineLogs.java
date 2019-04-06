package com.xueersi.parentsmeeting.modules.livevideoOldIJK.core;

/**
 * Created by linyuqiang on 2018/8/22.
 * 直播日志
 */
public interface LiveOnLineLogs {

    void getOnloadLogs(String TAG, String str);

    void getOnloadLogs(String TAG, String str, Throwable e);

    void saveOnloadLogs(String TAG, String str);

    void saveOnloadLogs(String TAG, String str, Throwable e);
}
