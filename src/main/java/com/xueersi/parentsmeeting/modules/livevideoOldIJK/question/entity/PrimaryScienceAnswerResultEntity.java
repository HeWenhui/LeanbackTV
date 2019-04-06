package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangYuansun on 2019/3/7
 *
 * 小学理科 互动题结果页
 */
public class PrimaryScienceAnswerResultEntity {
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

    List<Answer> answerList = new ArrayList<>();

    public static class Answer {
        int amswerNumber;
        String rightAnswer;
        String myAnswer;
        int right;

        public int getAmswerNumber() {
            return amswerNumber;
        }

        public void setAmswerNumber(int amswerNumber) {
            this.amswerNumber = amswerNumber;
        }

        public String getRightAnswer() {
            return rightAnswer;
        }

        public void setRightAnswer(String rightAnswer) {
            this.rightAnswer = rightAnswer;
        }

        public String getMyAnswer() {
            return myAnswer;
        }

        public void setMyAnswer(String myAnswer) {
            this.myAnswer = myAnswer;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
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

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
}
