package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.ArrayList;

public class BigResultEntity {
    //    public String standAnswer;
//    public String youAnswer;
//    public int rightType = 0;
    private int isRight;
    private int gold;
    private int right_num;
    private int wrong_num;
    private int rate;
    private ArrayList<BigResultItemEntity> bigResultItemEntityArrayList = new ArrayList<>();

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

    public int getRight_num() {
        return right_num;
    }

    public void setRight_num(int right_num) {
        this.right_num = right_num;
    }

    public int getWrong_num() {
        return wrong_num;
    }

    public void setWrong_num(int wrong_num) {
        this.wrong_num = wrong_num;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public ArrayList<BigResultItemEntity> getBigResultItemEntityArrayList() {
        return bigResultItemEntityArrayList;
    }
}
