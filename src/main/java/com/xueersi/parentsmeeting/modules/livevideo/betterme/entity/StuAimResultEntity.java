package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

/**
 * 英语小目标 结果信息
 *
 * @author zhangyuansun
 * created  at 2018/12/14
 */
public class StuAimResultEntity {
    private boolean isUpGrade;
    private String segment;
    private int segmentType;
    private int aimNumber;
    private int star;
    private boolean isDoneAim;
    private String aimType;
    private String realTimeVal;
    private String aimValue;

    public boolean isUpGrade() {
        return isUpGrade;
    }

    public void setUpGrade(boolean upGrade) {
        isUpGrade = upGrade;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public int getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(int segmentType) {
        this.segmentType = segmentType;
    }

    public int getAimNumber() {
        return aimNumber;
    }

    public void setAimNumber(int aimNumber) {
        this.aimNumber = aimNumber;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public boolean isDoneAim() {
        return isDoneAim;
    }

    public void setDoneAim(boolean doneAim) {
        isDoneAim = doneAim;
    }

    public String getAimType() {
        return aimType;
    }

    public void setAimType(String aimType) {
        this.aimType = aimType;
    }

    public String getRealTimeVal() {
        return realTimeVal;
    }

    public void setRealTimeVal(String realTimeVal) {
        this.realTimeVal = realTimeVal;
    }

    public String getAimValue() {
        return aimValue;
    }

    public void setAimValue(String aimValue) {
        this.aimValue = aimValue;
    }
}
