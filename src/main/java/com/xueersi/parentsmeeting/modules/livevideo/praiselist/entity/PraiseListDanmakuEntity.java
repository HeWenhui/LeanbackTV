package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

public class PraiseListDanmakuEntity {
    private String name;
    private int number;
    //1:组内战友点赞  2.广播战队赞数
    private int barrageType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBarrageType() {
        return barrageType;
    }

    public void setBarrageType(int barrageType) {
        this.barrageType = barrageType;
    }
}
