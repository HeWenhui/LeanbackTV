package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 点赞榜
 */

public class LikeListEntity {

    /** 1代表我在榜上，0表示我不在榜上 */
    private int isMy;

    List<PraiseListTeamEntity> teamList = new ArrayList<>();

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public List<PraiseListTeamEntity> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<PraiseListTeamEntity> teamList) {
        this.teamList = teamList;
    }

}
