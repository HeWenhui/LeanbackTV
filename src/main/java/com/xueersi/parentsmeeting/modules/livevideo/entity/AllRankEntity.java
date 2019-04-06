package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2017/9/22.
 * 所有排名
 */
public class AllRankEntity {
    private MyRankEntity myRankEntityClass = new MyRankEntity();
    private MyRankEntity myRankEntityMyTeam = new MyRankEntity();
    private MyRankEntity myRankEntityTeams = new MyRankEntity();

    public MyRankEntity getMyRankEntityClass() {
        return myRankEntityClass;
    }

    public MyRankEntity getMyRankEntityMyTeam() {
        return myRankEntityMyTeam;
    }

    public MyRankEntity getMyRankEntityTeams() {
        return myRankEntityTeams;
    }
}
