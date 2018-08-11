package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by David on 2018/8/11.
 */

public class PsState {
    private int resId;
    private boolean state;

    public PsState(int resId, boolean state) {
        this.resId = resId;
        this.state = state;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
