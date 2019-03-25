package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
*直播间 理科答题结果
*@author chekun
*created  at 2019/2/20 10:28
*/
public class ScienceAnswerResult {

    public static int STATE_CODE_RIGHT = 1;
    /**试题id**/
    private String id;
    /**答题所得金币**/
    private int gold;
    /**答题结果**/
    private int isRight;
    /**答题所得能量**/
    private int energy;

    public ScienceAnswerResult(String id, int gold, int isRight, int energy) {
        this.id = id;
        this.gold = gold;
        this.isRight = isRight;
        this.energy = energy;
    }

    public ScienceAnswerResult() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
