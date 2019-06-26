package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
*战队pk 进步榜
*@author chekun
*created  at 2019/1/14 14:28
*/
public class TeamPkStuProgress {
   private String stuId;
   private String name;
   private String avatarPath;
   private String teamName;

    /**
     * 是否是超级黑马
     */
   private boolean isSuper;

   /**进步幅度*/
   private String progressScope;

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

    public void setProgressScope(String progressScope) {
        this.progressScope = progressScope;
    }

    public String getProgressScope() {
        return progressScope;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }

    public boolean isSuper() {
        return isSuper;
    }
}
