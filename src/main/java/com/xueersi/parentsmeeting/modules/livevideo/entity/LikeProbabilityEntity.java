package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by Zhang Yuansun on 2018/1/9.
 */

public class LikeProbabilityEntity {
    /** 结果状态标识；0：表示失败；1：表示成功 */
    private int stat;
    /** stuId */
    private int stuId;
    /** 1：表示概率不加倍；2：表示概率加倍 */
    private int probability;

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
