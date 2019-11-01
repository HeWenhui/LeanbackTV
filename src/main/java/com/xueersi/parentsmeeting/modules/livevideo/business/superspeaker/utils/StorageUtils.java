package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;

public class StorageUtils {
    //    public static String imageUrl;
    static String videoPath = "";
    static String audioPath = "";

    public static String getVideoPath() {
        return videoPath;
    }

    public static void setVideoPath(String liveId, String coursewareId) {
        videoPath = LiveHttpConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + coursewareId + ".mp4";
    }

    public static String getVideoPath(String liveId, String coursewareId) {
        return LiveHttpConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + coursewareId + ".mp4";
    }

    public static String getAudioUrl() {
        return audioPath;
//        return LiveHttpConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + coursewareId + "audio.mp3";
    }

    public static void setAudioUrl(String liveId, String coursewareId) {
        audioPath = LiveHttpConfig.SUPER_SPEAKER_VIDEO_PATH + liveId + "_" + coursewareId + "audio.mp3";
    }

    public static void setStorageSPKey(String liveId, String courseWareId, int value) {
        ShareDataManager.getInstance().put(
                ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                value,
                ShareDataManager.SHAREDATA_NOT_CLEAR,
                true);
    }

    public static int getStorageSPValue(String liveId, String courseWareId) {
        return ShareDataManager.getInstance().getInt(
                ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + liveId + "_" + courseWareId,
                0,
                ShareDataManager.SHAREDATA_NOT_CLEAR);
    }
}
