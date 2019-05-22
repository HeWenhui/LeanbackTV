package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class PkAddEnergy {
    int energy;
    boolean first;

    public PkAddEnergy(boolean first, int energy) {
        this.first = first;
        this.energy = energy;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isFirst() {
        return first;
    }
}
