package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import java.util.ArrayList;

/** 小组互动组内信息 */
public class InteractiveTeam {
    private String live_id;
    private String class_id;
    private String pk_team_id;
    private String team_type;
    private int interactive_team_id;
    private ArrayList<TeamMemberEntity> entities = new ArrayList<>();

    public String getLive_id() {
        return live_id;
    }

    public void setLive_id(String live_id) {
        this.live_id = live_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getPk_team_id() {
        return pk_team_id;
    }

    public void setPk_team_id(String pk_team_id) {
        this.pk_team_id = pk_team_id;
    }

    public String getTeam_type() {
        return team_type;
    }

    public void setTeam_type(String team_type) {
        this.team_type = team_type;
    }

    public int getInteractive_team_id() {
        return interactive_team_id;
    }

    public void setInteractive_team_id(int interactive_team_id) {
        this.interactive_team_id = interactive_team_id;
    }

    public ArrayList<TeamMemberEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<TeamMemberEntity> entities) {
        this.entities = entities;
    }
}
