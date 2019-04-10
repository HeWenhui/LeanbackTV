package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

public class AllBllConfig {
    public static String getEnTeamPkIRCBllClassPath() {
        if (MediaPlayer.isPSIJK) {
            return "com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.business.EnTeamPkIRCBll";
        }
    }

    public static String getLiveAchievementIRCBllClassPath() {
        if (MediaPlayer.isPSIJK) {
            return "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business.LiveAchievementIRCBll";
        }
    }

    public static BllConfigEntity[] live_business_arts = {new BllConfigEntity(getEnTeamPkIRCBllClassPath()),
            new BllConfigEntity(getLiveAchievementIRCBllClassPath())};
    public static BllConfigEntity[] live_business_cn = {};
    public static BllConfigEntity[] live_business_science = {};
}
