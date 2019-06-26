package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 进步榜
 */

public class ProgressListEntity {

    /** 是否点赞标志位 */
    private int praiseStatus;

    /** 是否在榜单中 */
    int isMy;

    ArrayList<ProgressEntity> progressEntities = new ArrayList<>();

    public ArrayList<ProgressEntity> getProgressEntities() {
        return progressEntities;
    }

    public void setProgressEntities(ArrayList<ProgressEntity> progressEntities) {
        this.progressEntities = progressEntities;
    }

    public class ProgressEntity {

        /** 学生ID */
        String stuId;
        /** 学生姓名 */
        String stuName;
        /** 是否在榜单中 */
        int isMy;
        /** 标准进步分数 */
        String progressScore;


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

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }

        public String getProgressScore() {
            return progressScore;
        }

        public void setProgressScore(String progressScore) {
            this.progressScore = progressScore;
        }
    }

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


}
