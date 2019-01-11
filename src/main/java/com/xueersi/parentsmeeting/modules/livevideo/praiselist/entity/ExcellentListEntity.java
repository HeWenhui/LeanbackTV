package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 光荣榜
 */

public class ExcellentListEntity {

    ArrayList<TeamEntity> teamList = new ArrayList<>();
    /** 是否点赞标志位 */
    private int praiseStatus;
    /** 1代表我在榜上，0表示我不在榜上 */
    private int isMy;

    public int getPraiseStatus() {
        return praiseStatus;
    }

    public void setPraiseStatus(int praiseStatus) {
        this.praiseStatus = praiseStatus;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public ArrayList<TeamEntity> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<TeamEntity> teamList) {
        this.teamList = teamList;
    }

    /**
     * 战队信息
     */
    public class TeamEntity {

        List<ExcellentListEntity.StudentEntity> studentList = new ArrayList<>();
        /**
         * 上榜人数
         */
        private int onListNums;
        /**
         * 战队总人数
         */
        private int teamMemberNums;
        /**
         * 战队id
         */
        private String pkTeamId;
        /**
         * 战队的排名 如果有并列则排名相同
         */
        private int teamRanking;
        /**
         * 只有学生所在战队会有此字段(1:在榜上  0:不在榜上  -1：不在此战队)
         */
        private int isMy;

        private String hoverImg;
        private String hoverSmallImg;
        private String normalImg;
        private String normalSmallImg;
        private String pressImg;
        private String pressSmallImg;
        private String teamName;

        public List<ExcellentListEntity.StudentEntity> getStudentList() {
            return studentList;
        }

        public void setStudentList(List<ExcellentListEntity.StudentEntity> studentList) {
            this.studentList = studentList;
        }

        public int getOnListNums() {
            return onListNums;
        }

        public void setOnListNums(int onListNums) {
            this.onListNums = onListNums;
        }

        public int getTeamMemberNums() {
            return teamMemberNums;
        }

        public void setTeamMemberNums(int teamMemberNums) {
            this.teamMemberNums = teamMemberNums;
        }

        public String getPkTeamId() {
            return pkTeamId;
        }

        public void setPkTeamId(String pkTeamId) {
            this.pkTeamId = pkTeamId;
        }

        public int getTeamRanking() {
            return teamRanking;
        }

        public void setTeamRanking(int teamRanking) {
            this.teamRanking = teamRanking;
        }

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }

        public String getHoverImg() {
            return hoverImg;
        }

        public void setHoverImg(String hoverImg) {
            this.hoverImg = hoverImg;
        }

        public String getHoverSmallImg() {
            return hoverSmallImg;
        }

        public void setHoverSmallImg(String hoverSmallImg) {
            this.hoverSmallImg = hoverSmallImg;
        }

        public String getNormalImg() {
            return normalImg;
        }

        public void setNormalImg(String normalImg) {
            this.normalImg = normalImg;
        }

        public String getNormalSmallImg() {
            return normalSmallImg;
        }

        public void setNormalSmallImg(String normalSmallImg) {
            this.normalSmallImg = normalSmallImg;
        }

        public String getPressImg() {
            return pressImg;
        }

        public void setPressImg(String pressImg) {
            this.pressImg = pressImg;
        }

        public String getPressSmallImg() {
            return pressSmallImg;
        }

        public void setPressSmallImg(String pressSmallImg) {
            this.pressSmallImg = pressSmallImg;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }

    public class StudentEntity {
        /** 学生总共上过优秀榜的次数 */
        private int excellentNum;
        /** 用户姓名 */
        private String stuName;
        /** 1表示我在榜上，0表示我不在榜上 */
        private int isMy;

        public int getExcellentNum() {
            return excellentNum;
        }

        public void setExcellentNum(int excellentNum) {
            this.excellentNum = excellentNum;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }
    }
}
