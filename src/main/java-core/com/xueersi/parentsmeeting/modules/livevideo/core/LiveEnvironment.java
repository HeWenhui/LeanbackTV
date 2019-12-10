package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.app.Activity;
import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/** 直播环境中的常用方法 */
public interface LiveEnvironment {

    Activity getActivity();

    LiveGetInfo getLiveGetInfo();

    boolean isExper();

    boolean isBack();

    boolean isBigLive();

    LiveAndBackDebug getLiveAndBackDebug();

    LogToFile createLogToFile(String TAG);

    <T> T get(Class<T> clazz);
}
