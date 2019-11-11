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
    /** 兼容课件组2019年夏季课件尺寸问题， 2019.4.4新增  0 旧尺寸，1 新尺寸 */
    private String summerCourseWareSize;

    private int bolockChinese;
    private int evenDriveRightEvenNumUrl;

    private int isOpenStimulation;

    public int getIsOpenStimulation() {
        return isOpenStimulation;
    }

    public void setIsOpenStimulation(int isOpenStimulation) {
        this.isOpenStimulation = isOpenStimulation;
    }

    public int getEvenDriveRightEvenNumUrl() {
        return evenDriveRightEvenNumUrl;
    }

    public void setEvenDriveRightEvenNumUrl(int evenDriveRightEvenNumUrl) {
        this.evenDriveRightEvenNumUrl = evenDriveRightEvenNumUrl;
    }

    public int getBolockChinese() {
        return bolockChinese;
    }

    public void setBolockChinese(int bolockChinese) {
        this.bolockChinese = bolockChinese;
    }

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

    public String getSummerCourseWareSize() {
        return summerCourseWareSize;
    }

    public void setSummerCourseWareSize(String summerCourseWareSize) {
        this.summerCourseWareSize = summerCourseWareSize;
    }
}
