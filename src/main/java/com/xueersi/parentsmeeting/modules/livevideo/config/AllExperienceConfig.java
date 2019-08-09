package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllExperienceConfig {
    private static String[] experienceClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll",
    };
    private static String[] experienceHalfClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5HalfBodyExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.HalfBodyRedPackageExperienceBll",
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

    public static ArrayList<BllConfigEntity> getExperienceBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < experienceClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(experienceClassPath[i]));
        }
        return arrayList;
    }

    public static ArrayList<BllConfigEntity> getHalfExperienceBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < experienceHalfClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(experienceHalfClassPath[i]));
        }
        return arrayList;
    }
}
