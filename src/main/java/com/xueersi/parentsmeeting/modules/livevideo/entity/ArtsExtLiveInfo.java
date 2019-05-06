package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 文科直播间 额外参数信息
 *
 * @author chekun
 * created  at 2018/9/7 16:45
 */
public class ArtsExtLiveInfo {

    /** 是否是新文科课件平台 */
    private String newCourseWarePlatform;
    private int isGroupGameCourseWare = -1;

    public void setNewCourseWarePlatform(String newCourseWarePlatform) {
        this.newCourseWarePlatform = newCourseWarePlatform;
    }

    public String getNewCourseWarePlatform() {
        return newCourseWarePlatform;
    }

    public int getIsGroupGameCourseWare() {
        return isGroupGameCourseWare;
    }

    public void setIsGroupGameCourseWare(int isGroupGameCourseWare) {
        this.isGroupGameCourseWare = isGroupGameCourseWare;
    }
}
