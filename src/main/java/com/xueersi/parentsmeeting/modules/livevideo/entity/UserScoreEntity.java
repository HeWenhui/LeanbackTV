package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.parentsmeeting.modules.livevideo.config.AuditRoomConfig;

/**
 * Created by huadl on 2017/6/30.
 * 学生成绩实体
 */
public class UserScoreEntity {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 排名
     */
    private int index;
    /**
     * 正确率
     */
    private String correctRate;
    /**
     * 得分
     */
    private String score;
    /**
     * 是否是本组得分
     */
    private boolean isMyScore;
    /**
     * 题目状态
     */
    private int questionStatus;
    /**
     * 题目id
     */
    private int questionId;
    /**
     * 数据类型
     */
    private int dataType;
    /**
     * 组名
     */
    private String teamName;
    /**
     * 班名
     */
    private String className;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(String correctRate) {
        this.correctRate = correctRate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(int questionStatus) {
        this.questionStatus = questionStatus;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }


    public String getQuestionStatusText() {
        if (questionStatus == AuditRoomConfig.QUESTION_WRONG) {
            return "错误";
        } else if (questionStatus == AuditRoomConfig.QUESTION_RIGHT) {
            return "正确";
        } else if (questionStatus == AuditRoomConfig.QUESTION_HALF_RIGHT) {
            return "半对";
        }
        return "错误";
    }

    public String getBigQuestionStatusText() {
        if (questionStatus == AuditRoomConfig.QUESTION_BIG_WRONG) {
            return "错误";
        } else if (questionStatus == AuditRoomConfig.QUESTION_BIG_RIGHT) {
            return "正确";
        } else if (questionStatus == AuditRoomConfig.QUESTION_BIG_HALF_RIGHT) {
            return "半对";
        }
        return "错误";
    }

    public boolean isMyScore() {
        return isMyScore;
    }

    public void setMyScore(boolean myScore) {
        isMyScore = myScore;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getShowName() {
        if(AuditRoomConfig.RATE_MY == dataType) {
            return userName;
        } else if(AuditRoomConfig.RATE_TEAM == dataType) {
            return teamName;
        } else if(AuditRoomConfig.RATE_CLASS == dataType) {
            return className;
        }
        return "";
    }
}
