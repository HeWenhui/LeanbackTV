package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
*战队pk 明星榜
*@author chekun
*created  at 2019/1/14 14:28
*/
public class TeamPkStar {
   private String stuId;
   private String name;
   private String avatarPath;
   private String teamName;
   private String energy;

    /**
     * 是否是超级明星
     */
   private boolean isSuper;

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }

    public boolean isSuper() {
        return isSuper;
    }
}
