package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
*文科直播间 额外参数信息
*@author chekun
*created  at 2018/9/7 16:45
*/
public class ArtsExtLiveInfo {

    /**是否是新文科课件平台*/
    private String newCourseWarePlatform;

    private boolean isPop;
    private long poptime;

    public boolean isPop() {
        return isPop;
    }

    public void setPop(boolean pop) {
        isPop = pop;
    }

    public long getPoptime() {
        return poptime;
    }

    public void setPoptime(long poptime) {
        this.poptime = poptime;
    }

    public void setNewCourseWarePlatform(String newCourseWarePlatform) {
        this.newCourseWarePlatform = newCourseWarePlatform;
    }

    public String getNewCourseWarePlatform() {
        return newCourseWarePlatform;
    }

}
