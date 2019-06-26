package com.xueersi.parentsmeeting.modules.livevideo.core;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SysLogEntity;

/**
 * Created by linyuqiang on 2018/8/22.
 * 直播日志
 */
public interface LiveOnLineLogs extends LiveProvide {

    void getOnloadLogs(String TAG, SysLogEntity logEntity, StableLogHashMap stableLogHashMap, String str);

    void getOnloadLogs(String TAG, SysLogEntity logEntity, StableLogHashMap stableLogHashMap, String str, Throwable e);

    void saveOnloadLogs(String TAG, String str);

    void saveOnloadLogs(String TAG, String str, Throwable e);
}
