package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author  chenkun
 *
 * 学生总金币 及team总能量
 */
public class StudentCoinAndTotalEnergyEntity {

    private long competitorEnergy;
    /**自己组的能量*/
    private long myEnergy;
    private long stuLiveGold;
    /**自己的能量*/
    private int stuEnergy;

    public StudentCoinAndTotalEnergyEntity(){

    }
    public StudentCoinAndTotalEnergyEntity(long competitorEnergy, long myEnergy, long stuLiveGold) {
        this.competitorEnergy = competitorEnergy;
        this.myEnergy = myEnergy;
        this.stuLiveGold = stuLiveGold;
    }


    public long getCompetitorEnergy() {
        return competitorEnergy;
    }

    public void setCompetitorEnergy(long competitorEnergy) {
        this.competitorEnergy = competitorEnergy;
    }

    public long getMyEnergy() {
        return myEnergy;
    }

    public void setMyEnergy(long myEnergy) {
        this.myEnergy = myEnergy;
    }

    public long getStuLiveGold() {
        return stuLiveGold;
    }

    public void setStuLiveGold(long stuLiveGold) {
        this.stuLiveGold = stuLiveGold;
    }

    public int getStuEnergy() {
        return stuEnergy;
    }

    public void setStuEnergy(int stuEnergy) {
        this.stuEnergy = stuEnergy;
    }
}
