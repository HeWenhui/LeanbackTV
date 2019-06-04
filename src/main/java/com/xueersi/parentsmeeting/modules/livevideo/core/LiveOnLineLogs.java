package com.xueersi.parentsmeeting.modules.livevideo.core;

import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/8/22.
 * 直播日志
 */
public interface LiveOnLineLogs {

    void getOnloadLogs(String TAG, String label, StableLogHashMap stableLogHashMap, String str);

    void getOnloadLogs(String TAG, String label, StableLogHashMap stableLogHashMap, String str, Throwable e);

    void saveOnloadLogs(String TAG, String str);

    void saveOnloadLogs(String TAG, String str, Throwable e);
}
