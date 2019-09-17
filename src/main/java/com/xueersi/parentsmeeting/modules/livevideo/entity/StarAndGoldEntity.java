package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2017/9/27.
 */

public class StarAndGoldEntity {
    public final static int ENGLISH_INTELLIGENT_RECOGNITION = 1;
    private int starCount;
    private int goldCount;
    private int catagery;
    private PkEnergy pkEnergy = new PkEnergy();

    public int getStarCount() {
        return starCount;
    }

    public void setCatagery(int catagery) {
        this.catagery = catagery;
    }

    public int getCatagery() {
        return catagery;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getGoldCount() {
        return goldCount;
    }

    public void setGoldCount(int goldCount) {
        this.goldCount = goldCount;
    }

    public PkEnergy getPkEnergy() {
        return pkEnergy;
    }

    public void setPkEnergy(PkEnergy pkEnergy) {
        this.pkEnergy = pkEnergy;
    }

    public static class PkEnergy {
        public int me;
        public int myTeam;
        public int opTeam;
    }
}
