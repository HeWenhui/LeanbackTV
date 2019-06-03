package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Intent;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

public class AllBllConfig {
    public static String getEnTeamPkIRCBllClassPath() {
        if (MediaPlayer.getIsNewIJK()) {
            return "com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.business.EnTeamPkIRCBll";
        }
    }

    public static String getLiveAchievementIRCBllClassPath() {
        if (MediaPlayer.getIsNewIJK()) {
            return "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business.LiveAchievementIRCBll";
        }
    }

    public static String getSpeechCollectiveIRCBllClassPath() {
        if (MediaPlayer.getIsNewIJK()) {
            return "com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveIRCBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechcollective.business.SpeechCollectiveIRCBll";
        }
    }

    public static String getTeacherPraiseIRCBllClassPath() {
        if (MediaPlayer.getIsNewIJK()) {
            return "com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business.TeacherPraiseSecBll";
        } else {
            return "com.xueersi.parentsmeeting.modules.livevideoOldIJK.teacherpraisesec.business.TeacherPraiseSecBll";
        }
    }

//    public static BllConfigEntity[] live_business_arts = {new BllConfigEntity(getEnTeamPkIRCBllClassPath()),
//            new BllConfigEntity(getLiveAchievementIRCBllClassPath())};

    public static BllConfigEntity[] getLiveBusinessArts() {
        return new BllConfigEntity[]{
                new BllConfigEntity(getEnTeamPkIRCBllClassPath()),
                new BllConfigEntity(getLiveAchievementIRCBllClassPath())
        };
    }

    public static BllConfigEntity[] getLiveBusinessScience(Intent intent) {
        return new BllConfigEntity[]{
                new BllConfigEntity(getSpeechCollectiveIRCBllClassPath()),
                new BllConfigEntity(getTeacherPraiseIRCBllClassPath())
        };
    }

    public static BllConfigEntity[] live_business_cn = {};

}
