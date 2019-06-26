package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by huadl on 2017/6/30.
 * 是否有正在直播课程
 */
public class LiveCourseEntity {
    /**
     * 是否有直播
     */
    private int hasLiveCourse;
    /**
     * 直播id
     */
    private String liveId;
    /**
     * 直播旁听能不能看视频
     */
//    private int allowVisitLive;
    /**
     * 直播旁听能不能看视频
     */
    private int allowVisitNextLive;
    /**
     * 直播提示
     */
    private String liveHint;
    /**
     * 直播结束时间
     */
    private long liveEndTime;

    /**
     * 前一讲直播ID
     */
    private String preLiveId;
    /**
     * 下一讲直播ID
     */
    private String nextLiveId;
    /**
     * 下一讲直播时间
     */
    private long nextLiveTime;
    /**
     * 下一讲直播时间
     */
    private long nextEndLiveTime;
    /**
     * 下一讲直播时间
     */
    private String nextLiveHint;
    private String teacherHeadImg;
    private String stuCouId;

    public String getTeacherHeadImg() {
        return teacherHeadImg;
    }

    public void setTeacherHeadImg(String teacherHeadImg) {
        this.teacherHeadImg = teacherHeadImg;
    }

    /**
     * 延迟请求时间
     */
    private long loopTime;

    public int getHasLiveCourse() {
        return hasLiveCourse;
    }

    public void setHasLiveCourse(int hasLiveCourse) {
        this.hasLiveCourse = hasLiveCourse;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

//    public int getAllowVisitLive() {
//        return allowVisitLive;
//    }
//
//    public void setAllowVisitLive(int allowVisitLive) {
//        this.allowVisitLive = allowVisitLive;
//    }

    public int getAllowVisitNextLive() {
        return allowVisitNextLive;
    }

    public void setAllowVisitNextLive(int allowVisitNextLive) {
        this.allowVisitNextLive = allowVisitNextLive;
    }

    public String getLiveHint() {
        return liveHint;
    }

    public void setLiveHint(String liveHint) {
        this.liveHint = liveHint;
    }

    public long getLoopTime() {
        return loopTime;
    }

    public void setLoopTime(long loopTime) {
        this.loopTime = loopTime;
    }

    public String getPreLiveId() {
        return preLiveId;
    }

    public void setPreLiveId(String preLiveId) {
        this.preLiveId = preLiveId;
    }

    public String getNextLiveId() {
        return nextLiveId;
    }

    public void setNextLiveId(String nextLiveId) {
        this.nextLiveId = nextLiveId;
    }

    public long getNextLiveTime() {
        return nextLiveTime;
    }

    public void setNextLiveTime(long nextLiveTime) {
        this.nextLiveTime = nextLiveTime;
    }

    public String getNextLiveHint() {
        return nextLiveHint;
    }

    public void setNextLiveHint(String nextLiveHint) {
        this.nextLiveHint = nextLiveHint;
    }

    public long getNextEndLiveTime() {
        return nextEndLiveTime;
    }

    public void setNextEndLiveTime(long nextEndLiveTime) {
        this.nextEndLiveTime = nextEndLiveTime;
    }

    public long getLiveEndTime() {
        return liveEndTime;
    }

    public void setLiveEndTime(long liveEndTime) {
        this.liveEndTime = liveEndTime;
    }

    public String getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(String stuCouId) {
        this.stuCouId = stuCouId;
    }
}
