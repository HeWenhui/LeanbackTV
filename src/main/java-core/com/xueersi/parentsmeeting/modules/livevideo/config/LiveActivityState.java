package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * Created by linyuqiang on 2019/6/27.
 * activity生命周期
 */
public class LiveActivityState {
    public static final int INVALID_STATE = -1;   // Invalid state used as a null value.
    public static final int INITIALIZING = 0;     // Not yet created.
    public static final int CREATED = 1;          // Created.
    public static final int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    public static final int STOPPED = 3;          // Fully created, not started.
    public static final int STARTED = 4;          // Created and started, not resumed.
    public static final int RESUMED = 5;          // Created started and resumed.
}
