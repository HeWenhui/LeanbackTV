package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import java.io.Serializable;

//体验课课中推荐课程的classID
public class RecommondCourseEntity implements Serializable {
    private String courseName;

    private String coursePrice;

    private String courseId;

    private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String clssId) {
        this.classId = clssId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(String coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
