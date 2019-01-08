package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 点赞榜
 */

public class LikeListEntity {

    ArrayList<StudentEntity> studentList = new ArrayList<>();
    /** 1代表我在榜上，0表示我不在榜上 */
    private int isMy;

    public ArrayList<StudentEntity> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<StudentEntity> studentList) {
        this.studentList = studentList;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public class StudentEntity{

        /** 用户按照获赞值从大到小的姓名排行 */
        private String stuName;
        /** 1代表我在榜上，0表示我不在榜上 */
        private int isMy;
        /** 学生获赞个数 */
        private int stuPraiseNum;

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

        public int getStuPraiseNum() {
            return stuPraiseNum;
        }

        public void setStuPraiseNum(int stuPraiseNum) {
            this.stuPraiseNum = stuPraiseNum;
        }
    }


}
