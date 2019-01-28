package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

public class TeamMemberEntity {
    public int id;
    public boolean isMy;
    public boolean isSelect;
    public String name;
    public String headurl = "";
    public int resId;
    public int gold;
    public int energy;
    public int praiseCount;
    public int thisPraiseCount;
    public String nickName = "";

    public void copy(TeamMemberEntity other) {
        id = other.id;
        isMy = other.isMy;
        name = other.name;
        headurl = other.headurl;
        resId = other.resId;
        gold = other.gold;
        energy = other.energy;
        praiseCount = other.praiseCount;
        thisPraiseCount = other.thisPraiseCount;
        nickName = other.nickName;
    }

    @Override
    public String toString() {
        return "id=" + id + ",nickName=" + nickName;
    }
}
