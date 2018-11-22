package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import java.util.ArrayList;

public class EnTeamPkRankEntity {
    private int myTeam = 0;
    private int aTotalScore;//: 180, // a队总能量值
    private int aCurrentScore;//: 40,// a队本轮能量增加值
    private int apkTeamId;//:4,       // a队头像id
    private int bTotalScore;//:200, // b队总能量值
    private int bCurrentScore;//":50,// b队本轮能量增加值
    private int bpkTeamId;//:5,       // b队头像id
    ArrayList<TeamMemberEntity> memberEntities = new ArrayList<>();

    public int getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(int myTeam) {
        this.myTeam = myTeam;
    }

    public int getaTotalScore() {
        return aTotalScore;
    }

    public void setaTotalScore(int aTotalScore) {
        this.aTotalScore = aTotalScore;
    }

    public int getaCurrentScore() {
        return aCurrentScore;
    }

    public void setaCurrentScore(int aCurrentScore) {
        this.aCurrentScore = aCurrentScore;
    }

    public int getApkTeamId() {
        return apkTeamId;
    }

    public void setApkTeamId(int apkTeamId) {
        this.apkTeamId = apkTeamId;
    }

    public int getbTotalScore() {
        return bTotalScore;
    }

    public void setbTotalScore(int bTotalScore) {
        this.bTotalScore = bTotalScore;
    }

    public int getbCurrentScore() {
        return bCurrentScore;
    }

    public void setbCurrentScore(int bCurrentScore) {
        this.bCurrentScore = bCurrentScore;
    }

    public int getBpkTeamId() {
        return bpkTeamId;
    }

    public void setBpkTeamId(int bpkTeamId) {
        this.bpkTeamId = bpkTeamId;
    }

    public ArrayList<TeamMemberEntity> getMemberEntities() {
        return memberEntities;
    }

    public void setMemberEntities(ArrayList<TeamMemberEntity> memberEntities) {
        this.memberEntities = memberEntities;
    }
}
