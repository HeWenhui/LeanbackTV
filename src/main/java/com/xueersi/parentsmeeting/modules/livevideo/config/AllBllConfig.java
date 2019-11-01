package com.xueersi.parentsmeeting.modules.livevideo.config;

import android.content.Intent;

import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;

import java.util.ArrayList;

public class AllBllConfig {
    //"com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
    private static String[] secClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBll",
            //聊天
            "com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll",
            //战队pk，分文理
            "com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkCreat",
            //签到
            "com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
            //排行榜
            "com.xueersi.parentsmeeting.modules.livevideo.business.RankBll",
            //互动题
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            //课件
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareIRCBll",
            //集体发言
            "com.xueersi.parentsmeeting.modules.livevideo.speechcollective.business.SpeechCollectiveIRCBll",
            //老师点赞
            "com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business.TeacherPraiseSecBll",
            //投票
            "com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoteBll",
            //理科投票
            "com.xueersi.parentsmeeting.modules.livevideo.business.ScienceVoteBll",
            //智能私信
            "com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll",
            //领奖台
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll",
            //学习报告
            "com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LearnReportIRCBll",
            //红包
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            //nb实验
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            //疑问标记点
            "com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkIRCBll",
            //懂了吗
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            //语音弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenIRCBll",
            //表扬榜
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.presenter.PraiseListIRCBll",
            //初高中理科点赞互动
            "com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll",
            //精彩瞬间
            "com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportBll",
            //辅导老师表扬榜
            "com.xueersi.parentsmeeting.modules.livevideo.practice.PraiseTutorBll",
            //金话筒
            "com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.GoldMicroPhoneBll",
            //接麦，初中小学
            "com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business.VideoChatCreat",
            //直播反馈
//            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.EvaluateTeacherBll",
            //金话筒
            "com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone.GoldMicroPhoneBll",
            //教师反馈
            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.FeedbackTeacherBll",
            //语文半身直播超级演讲秀
            "com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.SuperSpeakerBll"
    };
    private static String[] engClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishCommonBll",
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBll",
            "com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll",
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
            "com.xueersi.parentsmeeting.modules.livevideo.achievement.business.LiveAchievementIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatIRCBll",
//            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.EvaluateTeacherBll",
            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.FeedbackTeacherBll",
            "com.xueersi.parentsmeeting.modules.livevideo.betterme.presenter.BetterMeIRCBll",};
    private static String[] cnClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBll",
            "com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallIRCBll",
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
            "com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatIRCBll",
//            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.EvaluateTeacherBll",
            "com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.FeedbackTeacherBll"
    };
    private static String[] lecClassPath = {
            //弹幕
            "com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuBll",
            "com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll",
            "com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.understand.business.UnderstandIRCBll",
            "com.xueersi.parentsmeeting.modules.livevideo.leclearnreport.business.LecLearnReportIRCBll",
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

    public static ArrayList<BllConfigEntity> getLiveBusinessLec() {
        ArrayList<BllConfigEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < lecClassPath.length; i++) {
            arrayList.add(new BllConfigEntity(lecClassPath[i]));
        }
        return arrayList;
    }

}
