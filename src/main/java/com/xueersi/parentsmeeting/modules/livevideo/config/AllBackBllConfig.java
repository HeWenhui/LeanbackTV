package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllBackBllConfig {
    private static String[] backClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionPlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackagePlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5PlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5PlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenPalyBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ChsAnswerResultBackBll",
    };

    private static String[] standLiveClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.StandExperienceQuestionPlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceRedPackageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.StandExperienceEnglishH5PlayBackBll",
    };

    private static String[] lecClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.leclearnreport.business.LecLearnReportIRCBll",
    };

    public static ArrayList<BllConfigEntity> getLiveBackBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < backClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(backClassPath[i]));
        }
        return arrayList;
    }

    public static ArrayList<BllConfigEntity> getStandLiveVideoExperienceBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < standLiveClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(standLiveClassPath[i]));
        }
        return arrayList;
    }
}