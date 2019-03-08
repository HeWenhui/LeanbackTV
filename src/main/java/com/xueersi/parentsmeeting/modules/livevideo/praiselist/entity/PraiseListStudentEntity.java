package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

public class PraiseListStudentEntity {
    /** 学生总共上过优秀榜的次数 */
    private int excellentNum;
    /** 用户姓名 */
    private String stuName;
    /** 1表示我在榜上，0表示我不在榜上 */
    private int isMy;
    /** 学生id */
    private String stuId;

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

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }
}
