package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2017/9/22.
 * 所有排名
 */
public class AllRankEntity {
    /** 我的班级排名 */
    private MyRankEntity myRankEntityClass = new MyRankEntity();
    /** 组内排名 */
    private MyRankEntity myRankEntityMyTeam = new MyRankEntity();
    /** 小组排名 */
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
