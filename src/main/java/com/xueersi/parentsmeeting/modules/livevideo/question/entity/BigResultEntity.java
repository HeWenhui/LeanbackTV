package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.ArrayList;

public class BigResultEntity {
    //    public String standAnswer;
//    public String youAnswer;
//    public int rightType = 0;
    private int isRight;
    private int gold;
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

    public ArrayList<BigResultItemEntity> getBigResultItemEntityArrayList() {
        return bigResultItemEntityArrayList;
    }
}
