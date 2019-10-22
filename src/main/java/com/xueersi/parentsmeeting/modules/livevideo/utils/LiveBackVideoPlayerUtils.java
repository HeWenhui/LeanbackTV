package com.xueersi.parentsmeeting.modules.livevideo.utils;

import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;

public class LiveBackVideoPlayerUtils {

    public static String handleBackVideoPath(String url) {
        String videoPath;
        if (url.contains("http") || url.contains("https")) {
            videoPath = DoPSVideoHandle.getPSVideoPath(url);
        } else {
            videoPath = url;
        }
        return videoPath;
    }
}
