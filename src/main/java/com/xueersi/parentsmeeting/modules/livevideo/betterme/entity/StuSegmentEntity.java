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
    private String star;
    /**
     * 剩余需要完成多少任务可以得到下一个星星
     */
    private String aimNumber;
    /**
     * 学生累计完成目标的次数
     */
    private String sumCount;
    private String segmentType;

    public String getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(String segmentType) {
        this.segmentType = segmentType;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getAimNumber() {
        return aimNumber;
    }

    public void setAimNumber(String aimNumber) {
        this.aimNumber = aimNumber;
    }

    public String getSumCount() {
        return sumCount;
    }

    public void setSumCount(String sumCount) {
        this.sumCount = sumCount;
    }
}
