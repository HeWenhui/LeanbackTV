package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Intent;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

public class AllBllConfig {
    public static String getEnTeamPkIRCBllClassPath() {
        return "com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll";
    }

    public static String getLiveAchievementIRCBllClassPath() {
        return "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll";
    }

    public static String getSpeechCollectiveIRCBllClassPath() {
        return "com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveIRCBll";
    }

    public static String getTeacherPraiseIRCBllClassPath() {
        return "com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business.TeacherPraiseSecBll";
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
