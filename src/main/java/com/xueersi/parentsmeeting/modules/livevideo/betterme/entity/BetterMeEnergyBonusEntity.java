package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;

/**
 * @Date on 2019/7/26 16:37
 * @Author zhangyuansun
 * @Description
 */
public class BetterMeEnergyBonusEntity {

    private ArrayList<TeamMemberEntity> myTeamMemberList = new ArrayList<>();
    private ArrayList<TeamMemberEntity> opTeamBMemberList = new ArrayList<>();

    private int myTeamTotal;
    private int myTeamBetterMeTotal;
    private int myTeamId;

    private int opTeamTotal;
    private int opTeamBetterMeTotal;
    private int opTeamId;

    public ArrayList<TeamMemberEntity> getMyTeamMemberList() {
        return myTeamMemberList;
    }

    public void setMyTeamMemberList(ArrayList<TeamMemberEntity> myTeamMemberList) {
        this.myTeamMemberList = myTeamMemberList;
    }

    public ArrayList<TeamMemberEntity> getOpTeamBMemberList() {
        return opTeamBMemberList;
    }

    public void setOpTeamBMemberList(ArrayList<TeamMemberEntity> opTeamBMemberList) {
        this.opTeamBMemberList = opTeamBMemberList;
    }

    public int getMyTeamTotal() {
        return myTeamTotal;
    }

    public void setMyTeamTotal(int myTeamTotal) {
        this.myTeamTotal = myTeamTotal;
    }

    public int getMyTeamBetterMeTotal() {
        return myTeamBetterMeTotal;
    }

    public void setMyTeamBetterMeTotal(int myTeamBetterMeTotal) {
        this.myTeamBetterMeTotal = myTeamBetterMeTotal;
    }

    public int getMyTeamId() {
        return myTeamId;
    }

    public void setMyTeamId(int myTeamId) {
        this.myTeamId = myTeamId;
    }

    public int getOpTeamTotal() {
        return opTeamTotal;
    }

    public void setOpTeamTotal(int opTeamTotal) {
        this.opTeamTotal = opTeamTotal;
    }

    public int getOpTeamBetterMeTotal() {
        return opTeamBetterMeTotal;
    }

    public void setOpTeamBetterMeTotal(int opTeamBetterMeTotal) {
        this.opTeamBetterMeTotal = opTeamBetterMeTotal;
    }

    public int getOpTeamId() {
        return opTeamId;
    }

    public void setOpTeamId(int opTeamId) {
        this.opTeamId = opTeamId;
    }
}
