package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 战队pk  战队成员
 *
 * @author chekun
 * created  at 2019/2/13 10:42
 */
public class TeamMate {

    /** 学生id **/
    private String id;
    /** 学生id **/
    private int idInt;
    /** 昵称 **/
    private String name;

    public TeamMate() {
    }

    public TeamMate(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        idInt = Integer.parseInt(id);
    }

    public int getIdInt() {
        return idInt;
    }

    public void setIdInt(int idInt) {
        this.idInt = idInt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
