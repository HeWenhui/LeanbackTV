package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity;

import java.util.ArrayList;

public class TeamInfo {
    private String teamId;
    private String teamName;
    private ArrayList<TeamMember> teamMembers = new ArrayList<>();

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }
}
