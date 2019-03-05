package com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2019/1/7.
 *
 * 计算小超市榜单
 */
public class MinimarketListEntity {

    List<PraiseListTeamEntity> teamList = new ArrayList<>();
    /**
     * 是否可以发布(0:否  1:是)
     */
    private int isRelease;
    /**
     * 榜单名称
     */
    private String title;
    /**
     * //榜单id 0:小超市每周风云榜 1:小超市上讲风云榜
     */
    private int titleId;
    /**
     * 战队数量
     */
    private int teamNum;
    /**
     * 1代表我在榜上，0表示我不在榜上
     */
    private int isMy;

    public int getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(int isRelease) {
        this.isRelease = isRelease;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(int teamNum) {
        this.teamNum = teamNum;
    }

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
