package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2019/1/7.
 *
 * 计算小超市榜单
 */
public class MinimarketListEntity {

    List<TeamEntity> teamList = new ArrayList<>();
    /**
     * 是否可以发布(0:否  1:是)
     */
    private int isRelease;
    /**
     * 榜单标题
     */
    private String title;
    /**
     * 榜单id
     */
    private String titleId;
    /**
     * 战队数量
     */
    private int teamNum;

    public int getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(int isRelease) {
        this.isRelease = isRelease;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public int getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(int teamNum) {
        this.teamNum = teamNum;
    }

    public List<TeamEntity> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamEntity> teamList) {
        this.teamList = teamList;
    }

    /**
     * 战队信息
     */
    public class TeamEntity {

        List<StudentEntity> studentList = new ArrayList<>();
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

        public List<StudentEntity> getStudentList() {
            return studentList;
        }

        public void setStudentList(List<StudentEntity> studentList) {
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

    /**
     * 学生信息
     */
    public class StudentEntity{
        /**
         * 学生id
         */
        private String stuId;
        /**
         * 学生姓名
         */
        private String stuName;
        /**
         * 学生打卡次数
         */
        private int stuPunchNum;
        /**
         * 是否是本人
         */
        private int isMy;

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public int getStuPunchNum() {
            return stuPunchNum;
        }

        public void setStuPunchNum(int stuPunchNum) {
            this.stuPunchNum = stuPunchNum;
        }

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }
    }
}
