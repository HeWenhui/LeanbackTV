package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 优秀榜
 */

public class ExcellentListEntity {

    ArrayList<PraiseListTeamEntity> teamList = new ArrayList<>();
    /** 是否点赞标志位 */
    private int praiseStatus;
    /** 1代表我在榜上，0表示我不在榜上 */
    private int isMy;

    public int getPraiseStatus() {
        return praiseStatus;
    }

    public void setPraiseStatus(int praiseStatus) {
        this.praiseStatus = praiseStatus;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public ArrayList<PraiseListTeamEntity> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<PraiseListTeamEntity> teamList) {
        this.teamList = teamList;
    }

}
