package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;
/**
 * 英语小目标 段位信息
 *
 * @author zhangyuansun
 * created  at 2018/12/12
 */
public class StuSegmentEntity {
    /**
     * 学生段位信息
     */
    private String segment;
    /**
     * 段位的星星数
     */
    private int star;
    /**
     * 剩余需要完成多少任务可以得到下一个星星
     */
    private int aimNumber;
    /**
     * 学生累计完成目标的次数
     */
    private int sumCount;

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getAimNumber() {
        return aimNumber;
    }

    public void setAimNumber(int aimNumber) {
        this.aimNumber = aimNumber;
    }

    public int getSumCount() {
        return sumCount;
    }

    public void setSumCount(int sumCount) {
        this.sumCount = sumCount;
    }

    public int getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(int segmentType) {
        this.segmentType = segmentType;
    }

    private int segmentType;


}
