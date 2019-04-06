package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;

/**
 * Created by lingyuqiang on 2017/9/22.
 * 各种排名
 */
public class MyRankEntity {
    private String myId;
    private ArrayList<RankEntity> rankEntities = new ArrayList<>();

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public ArrayList<RankEntity> getRankEntities() {
        return rankEntities;
    }
}
