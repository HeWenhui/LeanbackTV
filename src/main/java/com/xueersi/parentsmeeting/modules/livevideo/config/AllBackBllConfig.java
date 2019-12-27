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
            "com.xueersi.parentsmeeting.modules.livevideo.business.ScienceVotePlayBackBll",
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

    private static String[] lightbackClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.message.business.LightLiveMsgBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll.DiscountCouponBackBll",
    };

    public static ArrayList<BllConfigEntity> getLiveBackBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < backClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(backClassPath[i]));
        }
        return arrayList;
    }

    public static ArrayList<BllConfigEntity> getLightliveBackBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < lightbackClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(lightbackClassPath[i]));
        }
        return arrayList;
    }

}
