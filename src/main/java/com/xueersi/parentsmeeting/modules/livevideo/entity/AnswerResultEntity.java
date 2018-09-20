package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 *
 * 文科 答题结果
 * @author chenkun
 * @version 1.0, 2018/8/16 上午10:20
 */

public class AnswerResultEntity {

    /**文科新课件平台 老课件 答题结果*/
    public static final int RESULT_TYPE_OLD_COURSE_WARE = 1;
    /**文科课件平台 新课件*/
    public static final int RESULT_TYPE_NEW_COURSE_WARE = 2;

    private String liveId;
    private String stuId;
    /**后端生成的虚拟id*/
    private String virtualId;
    /**试题个数*/
    private int testCount;
    /**答题状态 0 :全错 1: 对一半  2:全对*/
    private int isRight;
    /**获得的金币数*/
    private int gold;
    /**正确率*/
    private double rightRate;
    private long createTime;

    public List<String> getIdArray() {
        return idArray;
    }

    public void setIdArray(List<String> idArray) {
        this.idArray = idArray;
    }

    /**档次答题所有试题id集合*/
    private List<String> idArray;

    private int  resultType;

    private List<Answer> answerList;

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(String virtualId) {
        this.virtualId = virtualId;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public double getRightRate() {
        return rightRate;
    }

    public void setRightRate(double rightRate) {
        this.rightRate = rightRate;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }


    public static class Answer{

        private String liveId;
        private String stuId;
        private String testId;
        private String testSrc;
        /**试题类型  1 填空题   2选择题*/
        private int testType;
        /**用户自己 选择题答案*/
        private List<String> choiceList;
        /**用户自己 填空题答案*/
        private List<String> blankList;
        /**标准答案*/
        private List<String> rightAnswers;

        /**这道小题是否正确 0:全错 1:对一半 2:全对*/
        private int isRight;
        /**这道小题的正确率*/
        private double rightRate;
        private long createTime;

        public String getLiveId() {
            return liveId;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public String getTestSrc() {
            return testSrc;
        }

        public void setTestSrc(String testSrc) {
            this.testSrc = testSrc;
        }

        public int getTestType() {
            return testType;
        }

        public void setTestType(int testType) {
            this.testType = testType;
        }

        public List<String> getChoiceList() {
            return choiceList;
        }

        public void setChoiceList(List<String> choiceList) {
            this.choiceList = choiceList;
        }

        public List<String> getBlankList() {
            return blankList;
        }

        public void setBlankList(List<String> blankList) {
            this.blankList = blankList;
        }

        public List<String> getRightAnswers() {
            return rightAnswers;
        }

        public void setRightAnswers(List<String> rightAnswers) {
            this.rightAnswers = rightAnswers;
        }

        public int getIsRight() {
            return isRight;
        }

        public void setIsRight(int isRight) {
            this.isRight = isRight;
        }

        public double getRightRate() {
            return rightRate;
        }

        public void setRightRate(double rightRate) {
            this.rightRate = rightRate;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

    }


}
