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
    /**兼容课件组2019年夏季课件尺寸问题， 2019.4.4新增  0 旧尺寸，1 新尺寸*/
    private String summerCourseWareSize;
    /**
     * 英语小目标
     * 1---迟到
     * 2----没有迟到
     */
    private boolean isArriveLate;
    /**
     * 1---小目标可以使用
     * 2---小目标无法使用
     */
    private boolean isUseBetterMe;
    /**
     * 段位信息
     */
    private String segment;
    /**
     * 段位类型
     */
    private String segmentType;
    /**
     * 段位星星
     */
    private String star;
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
    public boolean isArriveLate() {
        return isArriveLate;
    }

    public void setArriveLate(boolean arriveLate) {
        isArriveLate = arriveLate;
    }

    public boolean isUseBetterMe() {
        return isUseBetterMe;
    }

    public void setUseBetterMe(boolean useBetterMe) {
        isUseBetterMe = useBetterMe;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(String segmentType) {
        this.segmentType = segmentType;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String segmentStar) {
        this.star = segmentStar;
    }
}
