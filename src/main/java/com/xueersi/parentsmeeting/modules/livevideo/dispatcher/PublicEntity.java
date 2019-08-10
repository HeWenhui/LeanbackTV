package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dqq on 2019/7/25.
 */
public class PublicEntity {

    private String courseId;
    private String courseName;
    private String notice;
    private String scheduleTime;
    private String imageUrl;
    private String bigImageUrl;
    private int status;
    private String statusName;
    private String playBackUrl;
    private int liveNum;
    private int totalSize;
    private String teacherId;
    private String teacherHeadUrl;
    private String teacherName;
    private String teacherDescriptionUrl;
    private String fliterGrade;
    private String fliterSubject;
    private String instructions;
    private int sendPlayVideoTime;
    private String liveType;
    private String reservationNum;
    private long gotoClassTime;
    private String onlineNums;
    private List<VideoQuestionEntity> lstVideoQuestion = new ArrayList();
    private String streamTimes;
    private String radioType;
    private String isExistPlayback;
    private String isBelongToSeries;
    private String seriesId;
    private String activeUrl;
    private String studyreportUrl;

    public PublicEntity() {
    }

    public String getCourseId() {
        return this.courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getNotice() {
        return this.notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getScheduleTime() {
        return this.scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBigImageUrl() {
        return this.bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static String getStatusName(int status) {
        String statusName = "";
        if (status == 4) {
            statusName = "直播回放";
        } else if (status == 3) {
            statusName = "直播中，立即观看...";
        } else if (status == 2) {
            statusName = "已预约";
        } else if (status == 1) {
            statusName = "立即预约";
        }

        return statusName;
    }

    public String getPlayBackUrl() {
        return this.playBackUrl;
    }

    public void setPlayBackUrl(String playBackUrl) {
        this.playBackUrl = playBackUrl;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getLiveNum() {
        return this.liveNum;
    }

    public void setLiveNum(int liveNum) {
        this.liveNum = liveNum;
    }

    public String getTeacherId() {
        return this.teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherHeadUrl() {
        return this.teacherHeadUrl;
    }

    public void setTeacherHeadUrl(String teacherHeadUrl) {
        this.teacherHeadUrl = teacherHeadUrl;
    }

    public String getTeacherName() {
        return this.teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getFliterGrade() {
        return this.fliterGrade;
    }

    public void setFliterGrade(String fliterGrade) {
        this.fliterGrade = fliterGrade;
    }

    public String getFliterSubject() {
        return this.fliterSubject;
    }

    public void setFliterSubject(String fliterSubject) {
        this.fliterSubject = fliterSubject;
    }

    public List<VideoQuestionEntity> getLstVideoQuestion() {
        return this.lstVideoQuestion;
    }

    public void setLstVideoQuestion(List<VideoQuestionEntity> lstVideoQuestion) {
        this.lstVideoQuestion = lstVideoQuestion;
    }

    public String getStreamTimes() {
        return this.streamTimes;
    }

    public void setStreamTimes(String streamTimes) {
        this.streamTimes = streamTimes;
    }

    public int getSendPlayVideoTime() {
        return this.sendPlayVideoTime;
    }

    public void setSendPlayVideoTime(int sendPlayVideoTime) {
        this.sendPlayVideoTime = sendPlayVideoTime;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getLiveType() {
        return this.liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public String getReservationNum() {
        return this.reservationNum;
    }

    public void setReservationNum(String reservationNum) {
        this.reservationNum = reservationNum;
    }

    public long getGotoClassTime() {
        return this.gotoClassTime;
    }

    public void setGotoClassTime(long gotoClassTime) {
        this.gotoClassTime = gotoClassTime;
    }

    public String getOnlineNums() {
        return this.onlineNums;
    }

    public void setOnlineNums(String onlineNums) {
        this.onlineNums = onlineNums;
    }

    public String getRadioType() {
        return this.radioType;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public String getIsExistPlayback() {
        return this.isExistPlayback;
    }

    public void setIsExistPlayback(String isExistPlayback) {
        this.isExistPlayback = isExistPlayback;
    }

    public String getIsBelongToSeries() {
        return this.isBelongToSeries;
    }

    public void setIsBelongToSeries(String isBelongToSeries) {
        this.isBelongToSeries = isBelongToSeries;
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getActiveUrl() {
        return this.activeUrl;
    }

    public void setActiveUrl(String activeUrl) {
        this.activeUrl = activeUrl;
    }

    public String getStudyreportUrl() {
        return this.studyreportUrl;
    }

    public void setStudyreportUrl(String studyreportUrl) {
        this.studyreportUrl = studyreportUrl;
    }

    public String getTeacherDescriptionUrl() {
        return this.teacherDescriptionUrl;
    }

    public void setTeacherDescriptionUrl(String teacherDescriptionUrl) {
        this.teacherDescriptionUrl = teacherDescriptionUrl;
    }


}
