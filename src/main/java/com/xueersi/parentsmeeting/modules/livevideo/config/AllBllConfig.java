package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Intent;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllBllConfig {
    //"com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
    private static String[] secClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkCreat",
            "com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.RankBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business.TeacherPraiseSecBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoteBll",
            "com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.presenter.PraiseListIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll",
            "com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportBll",
            "com.xueersi.parentsmeeting.modules.livevideo.practice.PraiseTutorBll",
    };
    private static String[] engClassPath = {
            "com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.RankBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.ArtsPraiseListBll",
            "com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter.EnglishSpeechBulletIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.worddictation.business.WordDictationIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ArtsAnswerResultBll",
            "com.xueersi.parentsmeeting.modules.livevideo.practice.PraiseTutorBll",
            "com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll"};
    private static String[] cnClassPath = {"com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.chpk.business.ChinesePkBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.RankBll",
            "com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll",
            "com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.business.SpeechCollectiveIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.presenter.ChineseSpeechBulletScreenIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.presenter.PraiseListIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll",
            "com.xueersi.parentsmeeting.modules.livevideo.practice.PraiseTutorBll",
            "com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.ChsAnswerResultBll",
            "com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.GoldMicroPhoneBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.SuperSpeakerBll",
    };

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
