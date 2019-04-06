package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2016/8/5.
 */
public class FlowerEntity {
    private int ftype;
    private int id;
    private String tip;
    private int gold;

    public FlowerEntity(int ftype, int id, String tip, int gold) {
        this.ftype = ftype;
        this.id = id;
        this.tip = tip;
        this.gold = gold;
    }

    public int getFtype() {
        return ftype;
    }

    public void setFtype(int ftype) {
        this.ftype = ftype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
