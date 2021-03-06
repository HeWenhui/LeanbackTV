package com.xueersi.parentsmeeting.modules.livevideo.config;

import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllExperienceConfig {
    private static String[] experienceClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ArtsAnswerResultPlayBackBll",
    };
    private static String[] experienceRecordClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.business.ExperIRCMessBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ArtsAnswerResultPlayBackBll",
    };
    private static String[] experienceHalfClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.business.ExperHalfbodyIRCMessBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.HalfBodyRedPackageExperienceBll",
    };
    private static String[] standLiveClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceRedPackageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.livemessage.StandExperienceMessageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.understand.StandExperienceUnderstandBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.StandExperienceEvaluationBll",
            "com.xueersi.parentsmeeting.modules.livevideo.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll",
//            "com.xueersi.parentsmeeting.modules.livevideo.question.business.StandExperienceEnglishH5PlayBackBll",
//            "com.xueersi.parentsmeeting.modules.livevideo.question.business.StandExperienceQuestionPlayBackBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ArtsAnswerResultPlayBackBll",

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

    public static ArrayList<BllConfigEntity> getExperienceRecordBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < experienceRecordClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(experienceRecordClassPath[i]));
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

    public static ArrayList<BllConfigEntity> getStandLiveVideoExperienceBusiness() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < standLiveClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(standLiveClassPath[i]));
        }
        return arrayList;
    }
}
