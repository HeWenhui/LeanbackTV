package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import java.util.ArrayList;

public class PkTeamEntity {
    private int myTeam = 0;
    private int aId  ;
    private ArrayList<TeamMemberEntity> aTeamMemberEntity = new ArrayList<>();
    private int bId  ;
    private ArrayList<TeamMemberEntity> bTeamMemberEntity = new ArrayList<>();

    public int getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(int myTeam) {
        this.myTeam = myTeam;
    }

    public int getaId() {
        return aId;
    }

    public void setaId(int aId) {
        this.aId = aId;
    }

    public ArrayList<TeamMemberEntity> getaTeamMemberEntity() {
        return aTeamMemberEntity;
    }

    public void setaTeamMemberEntity(ArrayList<TeamMemberEntity> aTeamMemberEntity) {
        this.aTeamMemberEntity = aTeamMemberEntity;
    }

    public int getbId() {
        return bId;
    }

    public void setbId(int bId) {
        this.bId = bId;
    }

    public ArrayList<TeamMemberEntity> getbTeamMemberEntity() {
        return bTeamMemberEntity;
    }

    public void setbTeamMemberEntity(ArrayList<TeamMemberEntity> bTeamMemberEntity) {
        this.bTeamMemberEntity = bTeamMemberEntity;
    }
}
