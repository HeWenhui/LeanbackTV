package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
 * Created by lenovo on 2018/12/6.
 */

public class LiveVideoChConfig {

//    public static String CH_LIVE_HTTP_HOST = "https://live.chs.xueersi.com";

    /**
     * 获取分队信息
     */
    public static String URL_CHPK_PKTEAMINFO(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE :
                LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/getTeamNameAndMembers";
    }

    /**
     * pk对手信息
     */
    public static String URL_CHPK_MATCHTEAM(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/getMatchResult";
    }

    /**
     * 获取本场次 金币，能量信息
     */
    public static String URL_CHPK_LIVESTUGOLDANDTOTALENERGY(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/liveStuGoldAndTotalEnergy";
    }

    /**
     * 添加能能量值接口
     */
    public static String URL_CHPK_ADDPERSONANDTEAMENERGY(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/addPersonAndTeamEnergy";
    }

    /**
     * 学生开宝箱
     */
    public static String URL_CHPK_GETSTUCHESTURL(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/getStuChest";
    }

    /**
     * 班级宝箱结果
     */
    public static String URL_CHPK_GETCLASSCHESTRESULT(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/getClassChestResult";
    }

    /**
     * 战队pk结果
     */
    public static String URL_CHPK_STUPKRESULT(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/stuPKResult";
    }

    /**
     * 贡献之星结果
     */
    public static String URL_CHPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/teamEnergyNumAndContributionStar";
    }

    /**
     * 贡献之星结果多题型
     */
    public static String URL_CHPK_TEAMENERGYNUMANDCONTRIBUTIONSTARMUL(boolean isHalfBody) {
        return (isHalfBody ? LiveHttpConfig.HTTP_HOST_SCIENCE : LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST) + "/LiveCourse/teamEnergyNumAndContributionStarNew";
    }


    /**
     * 测试卷地址
     */
    public static String URL_EXAM_PAGER = LiveHttpConfig.HTTP_LIVE_CHINESE_HOST + "/LiveExam/examPaper";


}
