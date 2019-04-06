package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 上课点名
 * Created by linyuqiang on 2016/6/5.
 */
public class ClassSignEntity {
    /** 用户名称 */
    private String stuName;
    private String teacherName;
    private String teacherIMG;
    private int status;

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherIMG() {
        return teacherIMG;
    }

    public void setTeacherIMG(String teacherIMG) {
        this.teacherIMG = teacherIMG;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
