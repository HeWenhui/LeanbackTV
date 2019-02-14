package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
* 战队pk  战队成员
*@author chekun
*created  at 2019/2/13 10:42
*/
public class TeamMate {

    /**学生id**/
    private String id;
    /**昵称**/
    private String name;
    /**是否在线**/
    private boolean onLine;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
