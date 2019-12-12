package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity
 * @ClassName: CourseTeacherEntity
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/28 10:56
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 10:56
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CourseTeacherEntity {
    public static final int IDENTITY_MAIN = 1;//中教主讲
    public static final int IDENTITY_SECONDARY = 4;//辅讲
    public static final int IDENTITY_FOREIGN = 5;//外教
    public static final int IDENTITY_EXE = 7;//专属老师
    /**
     * 专属辅导老师
     */
    public static final int EXCTEACHERCOURSE = 1;
    /**
     * 老师头像地址
     */
    private String teacherImg;

    /**
     * 老师名称
     */
    private String teacherName;


    private String name;

    /**
     * 描述简介（url)
     */
    private String description;
    /**
     * 老师提示
     */
    private String teacherHint;

    /**
     * 老师所教课程ID
     */
    private String courseId;

    /**
     * 老师所教班级ID
     */
    private String classId;

    /**
     * 老师上课时间
     */
    private String classTime;

    /**
     * 剩余人数
     */
    private String surplusPerson;

    private int type;//老师类型

    private String typeName;//

    private String teacherId;

    private String spell;//后台管这个叫老师拼音，能做为老师的唯一标示

    private int jumpType;//老师详情页去原生 还是h5 1 去原生 0或者字段不存在 去h5

    //专属老师
    private int excTeacherCourse;

    public CourseTeacherEntity() {
    }

    public CourseTeacherEntity(String teacherName) {
        this.teacherName = teacherName;
    }

    public CourseTeacherEntity(String teacherImg, String teacherName) {
        this.teacherImg = teacherImg;
        this.teacherName = teacherName;
    }

    public CourseTeacherEntity(String teacherImg, String teacherName, String hint) {
        this.teacherImg = teacherImg;
        this.teacherName = teacherName;
        this.teacherHint = hint;
    }


    /**
     * 老师头像地址
     */
    public String getTeacherImg() {
        return teacherImg;
    }

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

    /**
     * 老师名称
     */
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherHint() {
        return teacherHint;
    }

    public void setTeacherHint(String teacherHint) {
        this.teacherHint = teacherHint;
    }

    public String getSurplusPerson() {
        return surplusPerson;
    }

    public void setSurplusPerson(String surplusPerson) {
        this.surplusPerson = surplusPerson;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getExcTeacherCourse() {
        return excTeacherCourse;
    }

    public void setExcTeacherCourse(int excTeacherCourse) {
        this.excTeacherCourse = excTeacherCourse;
    }

    public boolean isOrderConfirmExcTeacherCourse() {
        return excTeacherCourse == IDENTITY_EXE;
    }

    public boolean isExcTeacherCourse() {
        return excTeacherCourse == EXCTEACHERCOURSE;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }
}
