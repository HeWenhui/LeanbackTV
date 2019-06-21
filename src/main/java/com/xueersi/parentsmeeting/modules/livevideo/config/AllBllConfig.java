package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Intent;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllBllConfig {
    //"com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
    private static String[] secClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business.TeacherPraiseSecBll"};
    private static String[] engClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll"};
    private static String[] cnClassPath = {};

//    public static BllConfigEntity[] live_business_arts = {new BllConfigEntity(getEnTeamPkIRCBllClassPath()),
//            new BllConfigEntity(getLiveAchievementIRCBllClassPath())};

    public static ArrayList<BllConfigEntity> getLiveBusinessScience(Intent intent) {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < secClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(secClassPath[i]));
        }
        return arrayList;
    }

    public static ArrayList<BllConfigEntity> getLiveBusinessArts() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < engClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(engClassPath[i]));
        }
        return arrayList;
    }

    public static ArrayList<BllConfigEntity> getLiveBusinessCn() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < cnClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(cnClassPath[i]));
        }
        return arrayList;
    }

}
