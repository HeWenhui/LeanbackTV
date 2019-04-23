package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;

public class EnTeamPkRankEntity {
    private int myTeam = 0;
    private int myTeamTotal;//: 180, // a队总能量值
    private int myTeamCurrent;//: 40,// a队本轮能量增加值
    private int apkTeamId;//:4,       // a队头像id
    private int opTeamTotal;//:200, // b队总能量值
    private int opTeamCurrent;//":50,// b队本轮能量增加值
    private int bpkTeamId;//:5,       // b队头像id
    private int isWin = 0;
    private int noShow = 0;
    ArrayList<TeamMemberEntity> memberEntities = new ArrayList<>();

    public int getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(int myTeam) {
        this.myTeam = myTeam;
    }

    public int getMyTeamTotal() {
        return myTeamTotal;
    }

    public void setMyTeamTotal(int myTeamTotal) {
        this.myTeamTotal = myTeamTotal;
    }

    public int getMyTeamCurrent() {
        return myTeamCurrent;
    }

    public void setMyTeamCurrent(int myTeamCurrent) {
        this.myTeamCurrent = myTeamCurrent;
    }

    public int getApkTeamId() {
        return apkTeamId;
    }

    public void setApkTeamId(int apkTeamId) {
        this.apkTeamId = apkTeamId;
    }

    public int getOpTeamTotal() {
        return opTeamTotal;
    }

    public void setOpTeamTotal(int opTeamTotal) {
        this.opTeamTotal = opTeamTotal;
    }

    public int getOpTeamCurrent() {
        return opTeamCurrent;
    }

    public void setOpTeamCurrent(int opTeamCurrent) {
        this.opTeamCurrent = opTeamCurrent;
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

    public int getIsWin() {
        return isWin;
    }

    public void setIsWin(int isWin) {
        this.isWin = isWin;
    }

    public int getNoShow() {
        return noShow;
    }

    public void setNoShow(int noShow) {
        this.noShow = noShow;
    }
}
