package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author chekun
 * created  at 2018/9/7 10:06
 */
public class ScoreRange {
    private int low;
    private int high;
    public ScoreRange(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }
}
