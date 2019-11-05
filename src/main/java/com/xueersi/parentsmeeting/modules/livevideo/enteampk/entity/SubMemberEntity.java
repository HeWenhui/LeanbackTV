package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;


/**
 * 分组信息
 */
public class SubMemberEntity {

    /** 性别 */
    private int gender;
    /** 学生ID */
    private int stuId;
    /** 英文名*/
    private String englishName;

    /** 头像地址 */
    private String iconUrl;
    private int sex;

    /** 是否是自己 */
    boolean isMy;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isMy() {
        return isMy;
    }

    public void setMy(boolean my) {
        isMy = my;
    }
}
