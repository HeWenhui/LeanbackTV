package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by lyqai on 2017/9/27.
 */

public class StarAndGoldEntity {
    private int starCount;
    private int goldCount;
    private PkEnergy pkEnergy = new PkEnergy();

    public int getStarCount() {
        return starCount;
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
