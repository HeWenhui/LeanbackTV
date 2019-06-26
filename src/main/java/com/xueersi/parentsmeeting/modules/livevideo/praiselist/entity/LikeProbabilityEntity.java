package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

/**
 * Created by Zhang Yuansun on 2018/1/9.
 */

public class LikeProbabilityEntity {
    /** stuId */
    private String stuId;
    /** 1：表示概率不加倍；2：表示概率加倍 */
    private int probability;

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
