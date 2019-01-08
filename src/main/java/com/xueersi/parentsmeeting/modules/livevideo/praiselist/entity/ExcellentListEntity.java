package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 光荣榜
 */

public class ExcellentListEntity {

    /** 是否点赞标志位 */
    private int praiseStatus;

    /** 1表示我在榜上，0表示我不在榜上 */
    private int isMy;


    ArrayList<StudentEntity> studentList = new ArrayList<>();

    public ArrayList<StudentEntity> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<StudentEntity> studentList) {
        this.studentList = studentList;
    }

    public class StudentEntity {
        /** 学生总共上过优秀榜的次数 */
        private String excellentNum;
        /** 用户姓名 */
        private String stuName;

        /** 1表示我在榜上，0表示我不在榜上 */
        private int isMy;

        public String getExcellentNum() {
            return excellentNum;
        }

        public void setExcellentNum(String excellentNum) {
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
