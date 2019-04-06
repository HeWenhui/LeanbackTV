package com.xueersi.parentsmeeting.modules.livevideoOldIJK.event;

public class StandExperienceRecommondCourseEvent {
    private String tip;
    private String courseId;
    private String classId;

    public StandExperienceRecommondCourseEvent(String tip, String courseId, String classId) {
        this.tip = tip;
        this.courseId = courseId;
        this.classId = classId;
    }

//    public StandExperienceRecommondCourseEvent(String tip) {
//        this.tip = tip;
//    }


    public String getTip() {
        return tip;
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
}
