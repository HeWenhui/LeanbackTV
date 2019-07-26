package com.xueersi.parentsmeeting.modules.livevideo.betterme.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;

/**
 * @Date on 2019/7/26 16:37
 * @Author zhangyuansun
 * @Description
 */
public class TeamPKBetterMeRewardsEntity {

    private ArrayList<TeamMemberEntity> teamAMemberList = new ArrayList<>();
    private ArrayList<TeamMemberEntity> teamBMemberList = new ArrayList<>();

    private int teamATotal;
    private int teamABetterMeTotal;

    private int teamBTotal;
    private int teamBBetterMeTotal;
}
