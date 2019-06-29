package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.List;

/**
 * FileName: ChineseAISubjectResultEntity
 * Author: WangDe
 * Date: 2019/4/8 14:45
 * Description: AI语文题结果实体
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class ChineseAISubjectResultEntity {

    public static final int ABSLUTELY_WRONG = 0;
    public static final int ABSLUTELY_RIGHT = 1;
    public static final int PARTIALLY_RIGHT = 2;

    /**
     * 答题获得的金币数
     */
    int gold;
    /**
     * 整体作答情况类型
     * 0-全部答错，1-全部答对， 2-部分正确
     * 界面上分别对应蓝色、红色、橘色的主题色
     */
    int type;
    int energy;
    double totalScore;
    int isRight;
    int isAnswered;
    /** 1-选择 2-填空 3-主观题*/
    String testType;
    List<String> rightAnswers;
    List<StuAnswer> stuAnswers;

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(int isAnswered) {
        this.isAnswered = isAnswered;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public List<String> getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(List<String> rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public List<StuAnswer> getStuAnswers() {
        return stuAnswers;
    }

    public void setStuAnswers(List<StuAnswer> stuAnswers) {
        this.stuAnswers = stuAnswers;
    }
    public static class StuAnswer {
        String answer;
        String scoreKey;
        String score;
        String id;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getScoreKey() {
            return scoreKey;
        }

        public void setScoreKey(String scoreKey) {
            this.scoreKey = scoreKey;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
