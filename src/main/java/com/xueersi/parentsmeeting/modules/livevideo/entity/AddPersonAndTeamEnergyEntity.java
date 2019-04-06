package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @@author chenkun
 * 投票 能量
 */
public class AddPersonAndTeamEnergyEntity {

    private long teamEnergy;

    public AddPersonAndTeamEnergyEntity(long teamEnergy) {
        this.teamEnergy = teamEnergy;
    }

    public long getTeamEnergy() {
        return teamEnergy;
    }

    public void setTeamEnergy(long teamEnergy) {
        this.teamEnergy = teamEnergy;
    }
}
