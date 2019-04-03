package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * Created by huadl on 2017/6/30.
 * 旁听课堂实体
 */
public class AuditClassRoomEntity {
    /**
     * 签到时间
     */
    private String checkInTime;
    /**
     * 签到时间
     */
    private String title;
    /**
     * 课前测正确率
     */
    private String preTestCorrectRate;
    /**
     * 我的排名
     */
    private String mineRate;
    /**
     * 我的小组排名
     */
    private String teamRate;
    /**
     * 我的班级排名
     */
    private String classRate;
    /**
     * 互动题正确率
     */
    private String questionRateCorrectRate;
    /**
     * 我的排名
     */
    private List<UserScoreEntity> mineRateList;
    /**
     * 我的小组排名
     */
    private List<UserScoreEntity> teamRateList;
    /**
     * 我的班级排名
     */
    private List<UserScoreEntity> classRateList;
    /**
     * 互动提对错情况
     */
    private List<UserScoreEntity> questionDetailList;
    /**
     * 语音题目得分情况
     */
    private List<UserScoreEntity> voiceQuestionDetailList;

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getPreTestCorrectRate() {
        return preTestCorrectRate;
    }

    public void setPreTestCorrectRate(String preTestCorrectRate) {
        this.preTestCorrectRate = preTestCorrectRate;
    }

    public List<UserScoreEntity> getMineRateList() {
        return mineRateList;
    }

    public void setMineRateList(List<UserScoreEntity> mineRateList) {
        this.mineRateList = mineRateList;
    }

    public String getQuestionRateCorrectRate() {
        return questionRateCorrectRate;
    }

    public void setQuestionRateCorrectRate(String questionRateCorrectRate) {
        this.questionRateCorrectRate = questionRateCorrectRate;
    }

    public List<UserScoreEntity> getQuestionDetailList() {
        return questionDetailList;
    }

    public void setQuestionDetailList(List<UserScoreEntity> questionDetailList) {
        this.questionDetailList = questionDetailList;
    }

    public List<UserScoreEntity> getVoiceQuestionDetailList() {
        return voiceQuestionDetailList;
    }

    public void setVoiceQuestionDetailList(List<UserScoreEntity> voiceQuestionDetailList) {
        this.voiceQuestionDetailList = voiceQuestionDetailList;
    }

    public String getMineRate() {
        return mineRate;
    }

    public void setMineRate(String mineRate) {
        this.mineRate = mineRate;
    }

    public String getTeamRate() {
        return teamRate;
    }

    public void setTeamRate(String teamRate) {
        this.teamRate = teamRate;
    }

    public String getClassRate() {
        return classRate;
    }

    public void setClassRate(String classRate) {
        this.classRate = classRate;
    }

    public List<UserScoreEntity> getTeamRateList() {
        return teamRateList;
    }

    public void setTeamRateList(List<UserScoreEntity> teamRateList) {
        this.teamRateList = teamRateList;
    }

    public List<UserScoreEntity> getClassRateList() {
        return classRateList;
    }

    public void setClassRateList(List<UserScoreEntity> classRateList) {
        this.classRateList = classRateList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
