package com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity;

public class ScoreEnergy {
    public int scores;
    public int energy;

    public ScoreEnergy(int scores, int energy) {
        this.scores = scores;
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "scores=" + scores + ",energy=" + energy;
    }
}
