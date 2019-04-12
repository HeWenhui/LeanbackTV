package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by Administrator on 2016/4/14.
 */
public class LearnReportEntity {
    /**
     * stuId : 5002351
     * gold : 5
     * rate : 100%
     * averageRate : 1
     * rank : 1
     * lastRank : 2
     * allRank : 3
     * time : 2540
     * stuName : 风景第三
     */

    private ReportEntity stu;

    public void setStu(ReportEntity stu) {
        this.stu = stu;
    }

    public ReportEntity getStu() {
        return stu;
    }

    public static class ReportEntity {
        private int stuId;
        private int gold;
        private String rate;
        private String averageRate;
        private int rank;
        private String rankStr;
        private int lastRank;
        private String lastRankStr;
        private int time;
        private String stuName;
        private String teacherName;
        private String teacherIMG;

        public void setStuId(int stuId) {
            this.stuId = stuId;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public void setAverageRate(String averageRate) {
            this.averageRate = averageRate;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public void setLastRank(int lastRank) {
            this.lastRank = lastRank;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public int getStuId() {
            return stuId;
        }

        public int getGold() {
            return gold;
        }

        public String getRate() {
            return rate;
        }

        public String getAverageRate() {
            return averageRate;
        }

        public int getRank() {
            return rank;
        }

        public int getLastRank() {
            return lastRank;
        }

        public int getTime() {
            return time;
        }

        public String getStuName() {
            return stuName;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public String getTeacherIMG() {
            return teacherIMG;
        }

        public void setTeacherIMG(String teacherIMG) {
            this.teacherIMG = teacherIMG;
        }

        public String getRankStr() {
            return rankStr;
        }

        public void setRankStr(String rankStr) {
            this.rankStr = rankStr;
        }

        public String getLastRankStr() {
            return lastRankStr;
        }

        public void setLastRankStr(String lastRankStr) {
            this.lastRankStr = lastRankStr;
        }
    }
}
