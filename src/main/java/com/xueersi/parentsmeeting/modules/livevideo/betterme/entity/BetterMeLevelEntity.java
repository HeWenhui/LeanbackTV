package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

/**
 * 英语小目标 段位信息
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeLevelEntity {
    /**
     * 段位等级
     */
    private int levelColor;
    /**
     * 段位名称
     */
    private String levelName;
    /**
     * 段位切图资源ID
     */
    private int levelDrawableRes;
    /**
     * 升星文案
     */
    private String upStardescription;
    /**
     * 升级文案
     */
    private String upLeveldescription;

    public int getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(int levelColor) {
        this.levelColor = levelColor;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelDrawableRes() {
        return levelDrawableRes;
    }

    public void setLevelDrawableRes(int levelDrawableRes) {
        this.levelDrawableRes = levelDrawableRes;
    }

    public String getUpStardescription() {
        return upStardescription;
    }

    public void setUpStardescription(String upStardescription) {
        this.upStardescription = upStardescription;
    }

    public String getUpLeveldescription() {
        return upLeveldescription;
    }

    public void setUpLeveldescription(String upLeveldescription) {
        this.upLeveldescription = upLeveldescription;
    }
}
