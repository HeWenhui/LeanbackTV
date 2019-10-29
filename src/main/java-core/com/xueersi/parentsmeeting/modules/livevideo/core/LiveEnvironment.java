package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.app.Activity;
import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

public interface LiveEnvironment {

    Activity getActivity();

    LiveGetInfo getLiveGetInfo();

    boolean isExper();

    boolean isBack();
}
