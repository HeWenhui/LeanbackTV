package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity;

public class TeamMember {
    private int stuId;
    private String stuName;
    private boolean isReport;

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }
}
