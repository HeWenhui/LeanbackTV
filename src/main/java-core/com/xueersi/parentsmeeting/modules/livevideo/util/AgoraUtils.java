package com.xueersi.parentsmeeting.modules.livevideo.util;

import io.agora.rtc.Constants;

public class AgoraUtils {

    public static final int REMOTE_VIDEO_STATE_STARTING = Constants.REMOTE_VIDEO_STATE_STARTING;

    public static boolean isPlay(int state) {
        boolean isPlay = state != Constants.REMOTE_VIDEO_STATE_FROZEN && state != Constants.REMOTE_VIDEO_STATE_FAILED;
        LiveLoggerFactory.getLogger("AgoraUtils").d("isPlay:isPlay=" + isPlay);
        return isPlay;
    }
}
